---
title: Lab 38 - Backups and Disaster Recovery with Velero
lesson: 38
module: 12 Production
tags: [kubernetes, velero, backup, restore, disaster-recovery, pv]
---

# Lab 38 - Backups and Disaster Recovery with Velero

## Objective

In this lab you will run MinIO inside your kind cluster as an S3-compatible backup target, install the Velero CLI and the Velero server with the AWS provider, create an on-demand backup of a stateful workload with a PVC-backed Persistent Volume, simulate a disaster by deleting the namespace, and restore everything from the backup. You will also create a scheduled backup with a cron expression to learn how Velero automates disaster recovery.

## Prerequisites

- A running kind cluster
- kubectl installed and configured
- Completion of Lessons 1 through 38
- Understanding of Persistent Volumes, PVCs, and StorageClasses from Module 05

## Pre-Lab Checklist

- [ ] kind cluster running
- [ ] `kubectl get nodes` shows Ready status
- [ ] `wget` or `curl` available for downloading the Velero CLI
- [ ] A terminal session long enough to run a full install and restore cycle
- [ ] At least 2 GB of free disk space for MinIO and the backup data

---

## Step 1: Install the Velero CLI

Download the Velero CLI and move it onto your PATH:

```bash
wget https://github.com/vmware-tanzu/velero/releases/download/v1.11.1/velero-v1.11.1-linux-amd64.tar.gz
tar -xzf velero-v1.11.1-linux-amd64.tar.gz
sudo mv velero-v1.11.1-linux-amd64/velero /usr/local/bin/
velero version
```

Expected output:

```text
Client Version: v1.11.1
Server Version: v1.11.1
```

Replace `v1.11.1` in the URLs with the current Velero release if you prefer a newer version.

## Step 2: Run MinIO In-Cluster as the S3 Target

In production you point backups at managed object storage such as AWS S3. In this lab, run an S3-compatible MinIO server inside the cluster so Velero has a real S3 endpoint to write to.

Create the MinIO manifest:

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: minio
  namespace: minio-backup
spec:
  replicas: 1
  selector:
    matchLabels:
      app: minio
  template:
    metadata:
      labels:
        app: minio
    spec:
      containers:
      - name: minio
        image: minio/minio:latest
        args:
        - server
        - /data
        env:
        - name: MINIO_ROOT_USER
          value: minio
        - name: MINIO_ROOT_PASSWORD
          value: minio123
        ports:
        - containerPort: 9000
---
apiVersion: v1
kind: Service
metadata:
  name: minio
  namespace: minio-backup
spec:
  type: ClusterIP
  selector:
    app: minio
  ports:
  - port: 9000
    targetPort: 9000
```

Save the manifest as `minio.yaml` and apply it:

```bash
kubectl apply -f minio.yaml
```

Wait for the MinIO pod to be Ready:

```bash
kubectl wait --for=condition=available deployment/minio -n minio-backup --timeout=5m
kubectl get pods -n minio-backup
```

Expected output:

```text
NAME                    READY   STATUS    RESTARTS   AGE
minio-6f7bf8c6b9-v2x4k   1/1     Running   0          26s
```

Velero will reference this endpoint as the in-cluster hostname `minio.minio-backup.svc:9000`.

## Step 3: Create the Credentials File and Bucket

Create a local credentials file in the AWS format that Velero expects:

```bash
mkdir -p /tmp/velero-lab
cat > /tmp/velero-lab/credentials << 'EOF'
[default]
aws_access_key_id = minio
aws_secret_access_key = minio123
EOF
```

Create the S3 bucket by running the MinIO client as a one-shot pod:

```bash
kubectl run minio-mc --image=minio/mc --restart=Never --attach --rm \
  --command -- sh -c 'mc alias set local http://minio.minio-backup.svc:9000 minio minio123 && mc mb local/velero-backups'
```

Expected output:

```text
Adding local... up-to-date
Bucket created successfully "velero-backups".
```

## Step 4: Install the Velero Server with `velero install`

Install Velero with the AWS provider pointed at MinIO. Path-style S3 URLs are required because MinIO does not use virtual-hosted-style bucket addresses:

```bash
velero install \
    --provider aws \
    --bucket velero-backups \
    --secret-file /tmp/velero-lab/credentials \
    --backup-location-config region=minio,s3ForcePathStyle="true",s3Url=http://minio.minio-backup.svc:9000 \
    --plugins velero/velero-plugin-for-aws:v1.7.1 \
    --use-volume-snapshots=false \
    --use-restic
```

Explanation:

- `--provider aws`: Velero uses the AWS S3 client, which is compatible with any S3 API.
- `--bucket velero-backups`: The S3 bucket created in Step 3.
- `--secret-file`: The credentials file the Velero server mounts as a Secret.
- `--backup-location-config`: Points Velero at the in-cluster MinIO endpoint.
- `s3ForcePathStyle=true`: Required for MinIO and other non-AWS S3-compatible targets.
- `--use-volume-snapshots=false`: Cloud disk snapshots are not available in kind.
- `--use-restic`: Captures Persistent Volume data at the file level through Restic. This is how the PVC data is restored in a later step.
- `--plugins velero/velero-plugin-for-aws:v1.7.1`: The plugin that implements the AWS S3 object storage interface for Velero. It is what lets Velero talk to MinIO.

Plugin and Velero versions must match. Plugin tag versions change between release cycles, and an outdated tag produces `Failed listing backup-locations` errors. Verify what is actually running before assuming the install worked:

```bash
kubectl -n velero get pods -l component=velero
velero plugin list
```

Expected output:

```text
NAME    TYPE
velero.io/aws/backup-storage-location   BackupStorageLocation
velero.io/aws/volume-snapshotter        VolumeSnapshotter
velero.io/aws/restic                    RestoreItemAction
```

Wait for the Velero server to be ready:

```bash
kubectl wait --for=condition=available deployment/velero -n velero --timeout=5m
```

Verify the Backup Storage Location is `Available`:

```bash
velero backup-location get
```

Expected output:

```text
NAME           PROVIDER   BUCKET           PHASE       LAST VALIDATED                 ACCESS MODE
default        aws        velero-backups   Available   2026-08-09 15:05:14 +0000 UTC  [ReadWrite]
```

## Step 5: Deploy a Sample Workload with a PVC

Create a namespace and a workload that writes a file to a Persistent Volume, so the backup must capture both YAML and PV data:

```yaml
apiVersion: v1
kind: Namespace
metadata:
  name: app-data
---
apiVersion: v1
kind: PersistentVolume
metadata:
  name: app-pv
  labels:
    type: local
spec:
  storageClassName: manual
  capacity:
    storage: 1Gi
  accessModes:
  - ReadWriteOnce
  hostPath:
    path: /data/app
---
apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  name: app-pvc
  namespace: app-data
spec:
  storageClassName: manual
  accessModes:
  - ReadWriteOnce
  resources:
    requests:
      storage: 1Gi
---
apiVersion: apps/v1
kind: Deployment
metadata:
  name: app
  namespace: app-data
spec:
  replicas: 1
  selector:
    matchLabels:
      app: app
  template:
    metadata:
      labels:
        app: app
    spec:
      containers:
      - name: app
        image: busybox
        command:
        - /bin/sh
        - -c
        - sleep 3600
        volumeMounts:
        - name: data
          mountPath: /data
      volumes:
      - name: data
        persistentVolumeClaim:
          claimName: app-pvc
```

Save it as `app-workload.yaml` and apply it. A plain kind cluster has no dynamic storage provisioner, so a static hostPath PV + PVC pair is used to bind immediately:

```bash
kubectl apply -f app-workload.yaml
kubectl get pv app-pv
kubectl get pvc -n app-data
```

Expected output:

```text
NAME     CAPACITY   ACCESS MODES   RECLAIM POLICY   STATUS   CLAIM          STORAGECLASS   VOLUME   AGE
app-pv   1Gi        RWO            Retain          Bound    app-data/app-pvc  manual       app-pv   12s

NAME       STATUS   VOLUME   CAPACITY   ACCESS MODES   STORAGECLASS   AGE
app-pvc   Bound    app-pv   1Gi         ReadWriteOnce  manual         12s
```

Write a marker line to the volume so it can prove the file survived the disaster:

```bash
kubectl wait --for=condition=available deployment/app -n app-data --timeout=5m
kubectl exec deployment/app -n app-data -- sh -c 'echo precious-company-log > /data/journal.txt'
kubectl exec deployment/app -n app-data -- cat /data/journal.txt
```

Expected output:

```text
precious-company-log
```

## Step 6: Create a Velero Backup

Create an on-demand backup of the `app-data` namespace:

```bash
velero backup create app-backup --include-namespaces app-data
```

Expected output:

```text
Backup request "app-backup" submitted successfully.
```

Wait for the backup to finish and inspect it:

```bash
velero backup get app-backup
velero backup describe app-backup --details
```

Wait until the phase reads `Completed`:

```text
Name:         app-backup
Namespace:    velero
Labels:       velero.io/storage-location=default
Status:
  Phase:  Completed
```

The detail view also lists the namespaces and the Restic pod-volume backups that were taken for the PVC. A successful file-level backup of the PVC is recorded as `app-data/app-pvc: <backed-up>`.

## Step 7: Simulate a Disaster

Delete the entire namespace, which removes the Deployment and PVC:

```bash
kubectl delete namespace app-data
```

Confirm the namespace is gone:

```bash
kubectl get namespace app-data
```

Expected output:

```text
Error from server (NotFound): namespace "app-data" not found
```

Because the PV uses `ReclaimPolicy: Retain`, the hostPath volume still exists on the node. Wipe the data directory on the node so the volume is genuinely empty, exactly how a lost cloud disk behaves:

```bash
docker exec kind-control-plane rm -rf /data/app-data
docker exec kind-control-plane ls /data/app-data
```

Expected output:

```text
ls: cannot access '/data/app-data': No such file or directory
```

## Step 8: Restore from Backup

Restore the namespace and everything inside it from the backup taken in Step 5:

```bash
velero restore create app-data-restore \
    --from-backup app-data \
    --include-namespaces app-data
```

Expected output:

```text
Restore request "app-data-restore" submitted successfully.
```

Wait for the restore to complete:

```bash
velero restore get app-data-restore
velero restore describe app-data-restore
```

The describe output reports `Phase: Completed` and lists `Completed` items such as the namespace, PVC, and Deployment.

Verify the namespace, Deployment, PVC, and Pod were recreated:

```bash
kubectl get ns | grep app-data
kubectl get all -n app-data
kubectl get pvc -n app-data
```

Wait for the Pod to be Ready:

```bash
kubectl wait --for=condition=available deployment/app -n app-data --timeout=5m
```

Confirm the Persistent Volume data was restored along with the YAML:

```bash
kubectl exec deployment/app -n app-data -- cat /data/journal.txt
```

Expected output:

```text
precious-company-log
```

## Step 9: Create a Scheduled Backup (Optional)

Automatic backups are more reliable than manual ones. Create a `Schedule` that backs up the `app-data` namespace daily at 2 AM and keeps each backup for 30 days:

```bash
velero schedule create app-nightly --schedule="0 2 * * *" --include-namespaces app-data --ttl 720h
```

Expected output:

```text
Schedule "app-nightly" created from cron expression 0 2 * * *
```

List the schedules:

```bash
velero schedule get
```

Expected output:

```text
NAME         STATUS    CREATED                         SCHEDULE    BACKUP TTL   LAST BACKUP   SELECTOR
app-data     Created   2026-08-09 15:10:00 +0000 UTC   0 2 * * *   720h0m0s     n/a           <none>
```

Backups are only created when the cron expression fires. To confirm automation works, create a temporary schedule that fires every 5 minutes and watch a Backup object appear:

```bash
velero schedule create every-5-min --schedule="*/5 * * * *" --include-namespaces app-data
sleep 310
velero backup get --schedule every-5
```

Once verified, remove the temporary schedule:

```bash
velero schedule delete every-5-min && velero schedule delete app-data
```

## Cleanup

Remove the restore/test schedule, sample workload, MinIO, and the entire Velero installation:

```bash
velero uninstall --yes
kubectl delete namespace velero minio-backup app-data
kubectl delete pv app-pv
rm -f minio.yaml app-workload.yaml /tmp/velero-lab/credentials
rm -rf /tmp/velero-lab
```

Confirm the cluster has returned to a clean state:

```bash
kubectl get all -A
kubectl get ns
```

## Lab Questions

1. Why must `--use-volume-snapshots=false` be used when running Velero on a kind cluster?
2. What is the purpose of `s3ForcePathStyle=true` in the backup location config?
3. What would happen if a backup contained only the namespace YAML but not the Persistent Volume data?
4. How does Velero know where to store backups, and how does it authenticate to MinIO?
5. Why is `velero plugin list` useful after installing, and what breaks when the plugin tag does not match the Velero version?

## Expected Results

After completing this lab:

- You (multiple) can install the Velero CLI and the Velero server into a kind cluster
- You can run an in-cluster MinIO server as an S3-compatible backup target
- You can create a backup of a namespace that contains both YAML and PVC-backed data
- You can delete a namespace, simulate a disaster, and restore the application including its Persistent Volume data
- You can create and inspect a cron-based Velero Schedule
- You understand the Velero plugin model and how plugin tags affect the backup location

## Key Commands Reference

| Command | Purpose |
|---------|---------|
| `velero install ...` | Installs the Velero server with a BackupStorageLocation |
| `velero backup-location get` | Shows the configured BSL and its `Available` phase |
| `velero plugin list` | Lists the plugins loaded by the installed Velero server |
| `velero backup create <name> --include-namespaces <ns>` | Creates an on-demand backup |
| `velero backup describe <name> --details` | Shows backup phase and included resources |
| `velero restore create --from-backup <name> --include-namespaces <ns>` | Restores a namespace from a backup |
| `velero restore get <name>` | Shows the restore phase |
| `velero schedule create <name> --schedule="0 2 * * *"` | Creates a daily 2 AM scheduled backup |
| `velero uninstall --yes` | Removes the Velero installation and its CRDs |

## Next

- Return to the [Lesson 38 file](../docs/12-production/lesson-38-backups-and-disaster-recovery-with-velero.md) to review the concepts
- Try the Mini Project: Create a Velero `Schedule` that backs up the default namespace every 5 minutes, delete a ConfigMap by hand, and restore it from the latest scheduled backup
- Proceed to the next lesson in Module 12 to learn about production hardening and security best practices
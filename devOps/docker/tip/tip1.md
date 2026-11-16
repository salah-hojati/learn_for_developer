Sure — here’s the English version.

You have two separate issues:

1. **Compose warning:** `the attribute 'version' is obsolete`
2. **Connection error:** Docker Compose cannot connect to the Podman socket

---

## 1. Remove the obsolete `version` field

Open your `docker-compose.yml` and delete the top-level `version:` line, for example:

```yaml
# Remove this line
version: '3.8'

services:
  redis:
    image: redis:7-alpine
```

Docker Compose v2 no longer needs it. This warning is harmless, but removing it avoids confusion.

---

## 2. Fix: Cannot connect to Podman socket

Your error:

```text
Cannot connect to the Docker daemon at unix:///run/user/1000/podman/podman.sock
```

means Docker Compose is trying to use the **Podman socket**, but that socket is not running or not available.

### Recommended fix: start the rootless Podman socket

Run:

```bash
systemctl --user enable --now podman.socket
```

Then point Docker Compose to that socket:

```bash
export DOCKER_HOST=unix:///run/user/$(id -u)/podman/podman.sock
```

Verify:

```bash
docker info
```

If it works, try again:

```bash
docker compose up -d
```

To make `DOCKER_HOST` persistent, add this line to your `~/.bashrc` or `~/.zshrc`:

```bash
export DOCKER_HOST=unix:///run/user/$(id -u)/podman/podman.sock
```

Then reload your shell:

```bash
source ~/.bashrc
```

---

## If you want a system-wide symlink alternative

Only do this if you are intentionally using Podman as the Docker API provider:

```bash
sudo rm -f /var/run/docker.sock
sudo ln -s /run/podman/podman.sock /var/run/docker.sock
```

But the `systemctl --user` + `DOCKER_HOST` method is usually safer and more standard for rootless Podman.

---

## Alternative: use `podman-compose`

If your goal is to use Podman directly, you can install and use `podman-compose` instead of Docker Compose:

```bash
podman-compose up -d
```

That avoids Docker socket compatibility issues entirely.

---

### Quick checklist

```bash
# 1. Remove version: from docker-compose.yml

# 2. Start Podman socket
systemctl --user enable --now podman.socket

# 3. Set DOCKER_HOST
export DOCKER_HOST=unix:///run/user/$(id -u)/podman/podman.sock

# 4. Verify
docker info

# 5. Start services
docker compose up -d
```
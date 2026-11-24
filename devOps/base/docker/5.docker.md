<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">

# مرحله ۵: امنیت، بهینه‌سازی، BuildKit پیشرفته و CI/CD 🔐⚙️

هدف این مرحله: image هایی بسازی که **کوچیک، امن، قابل بازتولید** باشن، **آسیب‌پذیری‌هاشون رو اسکن کنی**، **multi-arch** بیلد کنی، **registry خصوصی** راه بندازی، و همه رو **خودکار در CI/CD** اجرا کنی.

---

## بخش اول: سخت‌سازی امنیتی Container (Hardening)

Docker به صورت پیش‌فرض برای **راحتی توسعه** تنظیم شده، نه امنیت. container ها با root اجرا می‌شن، قابلیت‌های وسیع Linux دارن، فایل‌سیستم قابل نوشتن دارن، و محدودیت منابع ندارن. هر کدوم از این پیش‌فرض‌ها یه سطح حمله‌ست.

### ۱.۱. کاربر غیر root — پایه‌ای‌ترین قدم

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```dockerfile
# ✅ در Dockerfile
FROM node:20-alpine
WORKDIR /app
COPY --chown=node:node . .
USER node
CMD ["node", "index.js"]
```

</div>


<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```dockerfile
# ✅ نسخه صریح‌تر (UID/GID ثابت، بهتر برای K8s)
RUN addgroup -g 10001 app && \
    adduser -u 10001 -G app -D app
COPY --chown=10001:10001 . /app
USER 10001:10001
```

</div>


**چرا مهمه؟** اگه container رخنه کنه، مهاجم دسترسی root روی **خود container** نداره (و در صورت escape، روی هاست هم محدودتره).

---

### ۱.۲. فایل‌سیستم Read-Only

فایل‌سیستم read-only مانع از نوشتن webshell، cron job، یا تغییر باینری‌ها توسط مهاجم می‌شه:

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```bash
docker run --read-only \
  --tmpfs /tmp:rw,noexec,nosuid,size=64m \
  --tmpfs /var/cache/nginx:rw,size=32m \
  myapp:secure
```

</div>


**نکته کلیدی:** هر مسیری که اپ **واقعاً** نیاز به نوشتن داره رو با `--tmpfs` mount کن (RAM-based، با restart پاک می‌شه). مسیرهای رایج:
- `/tmp` → همیشه
- `/var/cache/nginx` → برای Nginx
- `/var/run` → برای PID files

در Compose:

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```yaml
services:
  api:
    read_only: true
    tmpfs:
      - /tmp:size=64m
      - /var/run:size=16m
```

</div>


---

### ۱.۳. حذف Capabilities

Capabilities قدرت root رو به تیکه‌های ریز تقسیم می‌کنن. container ها به صورت پیش‌فرض مجموعه‌ای از capabilities دارن که اکثر workload ها **بهشون نیازی ندارن**.

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```bash
# همه رو بردار، فقط لازم رو اضافه کن
docker run \
  --cap-drop=ALL \
  --cap-add=NET_BIND_SERVICE \   # فقط اگه پورت <1024 bind می‌کنی
  myapp:secure
```

</div>


**کدوم capability لازمه؟**
- وب‌سرور روی پورت 80/443 → `NET_BIND_SERVICE`
- هیچ‌کدوم از اپ‌های معمولی (Node/Python/Go روی پورت بالا) → **هیچی!**
- ping → `NET_RAW`

در Compose:

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```yaml
services:
  api:
    cap_drop:
      - ALL
    # cap_add:
    #   - NET_BIND_SERVICE
```

</div>


---

### ۱.۴. `no-new-privileges` — جلوگیری از ارتقای دسترسی

از اجرای باینری‌های `setuid`/`setgid` که می‌تونن دسترسی بالاتر بگیرن جلوگیری می‌کنه. **رایگان و بی‌دردسره**:

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```bash
docker run --security-opt=no-new-privileges:true myapp:secure
```

</div>


<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```yaml
# Compose
services:
  api:
    security_opt:
      - no-new-privileges:true
```

</div>


**نکته:** این گزینه `sudo` و `su` رو داخل container غیرفعال می‌کنه — که دقیقاً چیزیه که می‌خوای.

---

### ۱.۵. Seccomp — محدودسازی System Call

Docker یه **پروفایل پیش‌فرض seccomp** داره که ~۴۴ syscall خطرناک رو بلاک می‌کنه (مثل `reboot`, `mount`, `ptrace`). **به صورت پیش‌فرض فعاله**، ولی اگه `--privileged` بزنی غیرفعال می‌شه.

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```bash
# صریحاً پروفایل پیش‌فرض رو فعال کن
docker run --security-opt seccomp=builtin myapp:secure

# یا پروفایل سفارشی
docker run --security-opt seccomp=/path/to/profile.json myapp:secure
```

</div>


**قانون:** **هرگز `--privileged` نزن** مگر اینکه واقعاً بدونی چیکار می‌کنی.

---

### ۱.۶. محدودیت منابع (Resource Limits)

یه container بدون محدودیت می‌تونه حافظه/CPU هاست رو بخوره و بقیه رو از کار بندازه:

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```bash
docker run \
  --memory=512m --memory-swap=512m \
  --cpus=1.0 \
  --pids-limit=200 \
  myapp:secure
```

</div>


<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```yaml
# Compose
services:
  api:
    deploy:
      resources:
        limits:
          cpus: "1.0"
          memory: 512M
          pids: 200
        reservations:
          memory: 256M
```

</div>


---

### ۱.۷. چک‌لیست سخت‌سازی (خلاصه)

| کنترل | فلگ / دستورالعمل |
|-------|-------------------|
| کاربر غیر root | `USER 10001` در Dockerfile |
| فایل‌سیستم read-only | `--read-only` + `--tmpfs` |
| Capabilities حداقلی | `--cap-drop=ALL --cap-add=...` |
| بدون ارتقای دسترسی | `--security-opt=no-new-privileges:true` |
| Seccomp | `--security-opt seccomp=builtin` |
| محدودیت منابع | `--memory`, `--cpus`, `--pids-limit` |
| بدون Docker socket | ❌ هرگز `-v /var/run/docker.sock` |
| Base image پین‌شده | `FROM image@sha256:...` |

---

### ۱.۸. تله‌ی Docker Socket

**هرگز `/var/run/docker.sock` رو به container mount نکن.** این کار به container **دسترسی root روی هاست** می‌ده، چون daemon Docker با root اجرا می‌شه و socket API کاملشه. این شایع‌ترین راه خودتحمیلی container escape هست.

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```bash
# ❌ این کار رو نکن!
docker run -v /var/run/docker.sock:/var/run/docker.sock ci-runner
```

</div>


اگه یه workload نیاز به build image داره، از **BuildKit rootless** یا **Kaniko** استفاده کن.

---

## بخش دوم: اسکن آسیب‌پذیری (Vulnerability Scanning)

### ۲.۱. Docker Scout

Docker Scout ابزار رسمی Docker برای تحلیل CVE هاست:

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```bash
# خلاصه وضعیت
docker scout quickview myapp:secure

# لیست کامل CVE ها
docker scout cves myapp:secure

# فقط CVE های fixable با severity بالا
docker scout cves --only-severity critical,high --only-fixed myapp:secure

# فقط CVE های یه پکیج خاص
docker scout cves --only-package express myapp:secure

# خروجی با exit code (برای CI — اگه CVE باشه fail می‌شه)
docker scout cves -e --only-severity critical,high --only-fixed myapp:secure

# پیشنهاد برای رفع
docker scout recommendations myapp:secure
```

</div>


**فیلترهای پرکاربرد**:
- `--only-fixed` → فقط اونایی که patch دارن (عملی‌ترن)
- `--only-severity critical,high` → فقط مهم‌ها
- `-e, --exit-code` → اگه CVE پیدا شد، exit code 2 بده (برای CI)
- `--only-base` → فقط CVE هایی که از base image میان

---

### ۲.۲. Trivy (جایگزین متن‌باز و محبوب)

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```bash
# نصب (Ubuntu)
sudo apt-get install -y wget apt-transport-https gnupg
wget -qO - https://aquasecurity.github.io/trivy-repo/deb/public.key | sudo apt-key add -
echo "deb https://aquasecurity.github.io/trivy-repo/deb $(lsb_release -sc) main" | sudo tee /etc/apt/sources.list.d/trivy.list
sudo apt-get update && sudo apt-get install -y trivy

# اسکن ساده
trivy image myapp:secure

# فقط HIGH و CRITICAL
trivy image --severity HIGH,CRITICAL myapp:secure

# نادیده گرفتن CVE های بدون patch
trivy image --severity HIGH,CRITICAL --ignore-unfixed myapp:secure

# اسکن کانفیگ (misconfiguration) و secret ها هم
trivy image --scanners vuln,misconfig,secret --severity HIGH,CRITICAL myapp:secure
```

</div>


**نکته مهم:** `--ignore-unfixed` می‌تونه ریسک‌های critical رو پنهان کنه اگه patch موجود نباشه. با احتیاط استفاده کن.

---

### ۲.۳. تله‌ی Cache در اسکن (مهم برای CI)

یه مشکل واقعی: `apt-get update` تو Dockerfile ممکنه از **cache قدیمی** استفاده کنه و image با پکیج‌های آسیب‌پذیر قدیمی ship بشه，در حالی که Trivy CVE نشون می‌ده.

**راه‌حل:** یه `ARG` بساز که تو cache key شرکت کنه و هر build عوض بشه:

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```dockerfile
ARG APT_CACHE_BUST=unknown
RUN apt-get update && apt-get upgrade -y && \
    apt-get install -y --no-install-recommends curl && \
    rm -rf /var/lib/apt/lists/*
```

</div>


<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```bash
# در CI، هر run یه مقدار جدید بده
docker build --build-arg APT_CACHE_BUST=$GITHUB_RUN_ID -t myapp:secure .
```

</div>


اینطوری لایه `apt` هر بار دوباره اجرا می‌شه و آخرین security update ها رو می‌گیره.

---

## بخش سوم: BuildKit پیشرفته

### ۳.۱. Cache Mount — سرعت انفجاری

مشکل کلاسیک: `COPY . .` قبل از `npm ci` باعث می‌شه هر تغییر کد، **کل نصب dependencies** رو از نو انجام بده. حتی با جدا کردن `package.json`، اگه lock file عوض بشه، بازم full install لازمه.

**راه‌حل: `--mount=type=cache`** — cache پکیج‌منیجر رو **خارج از لایه‌های image** نگه می‌داره و بین build ها reuse می‌کنه.

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```dockerfile
# syntax=docker/dockerfile:1.7

FROM node:20-alpine AS build
WORKDIR /app

COPY package.json package-lock.json ./
RUN --mount=type=cache,target=/root/.npm \
    npm ci --prefer-offline --no-audit

COPY . .
RUN npm run build

FROM nginx:1.27-alpine
COPY --from=build /app/dist /usr/share/nginx/html
```

</div>


**نتیجه واقعی:** یه پروژه frontend از **۸ دقیقه و ۱۰ ثانیه** به **۴۰ ثانیه** رسیده.

**معادل برای زبان‌های دیگه**:

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```dockerfile
# Python / pip
RUN --mount=type=cache,target=/root/.cache/pip \
    pip install -r requirements.txt

# Go
RUN --mount=type=cache,target=/go/pkg/mod \
    go mod download

# Rust / cargo
RUN --mount=type=cache,target=/usr/local/cargo/registry \
    cargo build --release

# apt
RUN --mount=type=cache,target=/var/cache/apt \
    apt-get update && apt-get install -y curl
```

</div>


**⚠️ تله مهم:** محتوای cache mount **وارد image نهایی نمی‌شه** و به registry push نمی‌شه. فقط روی builder محلی می‌مونه. برای **تولید نهایی** باید از `RUN` یا `COPY --from` معمولی استفاده کنی.

---

### ۳.۲. Secret Mount — بدون درز کردن secret

**مشکل:** `ENV DB_PASSWORD=secret` یا `COPY .env` secret رو **تو لایه‌های image** ذخیره می‌کنه. هر کسی `docker history` بزنه می‌بینه.

**راه‌حل: `--mount=type=secret`**

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```dockerfile
# syntax=docker/dockerfile:1.7
FROM alpine
RUN --mount=type=secret,id=db_password \
    cat /run/secrets/db_password > /dev/null && \
    echo "password was available during build, not stored"
```

</div>


<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```bash
# build با secret
docker build --secret id=db_password,src=./db_password.txt -t myapp:secure .

# یا از env
echo "$DB_PASSWORD" | docker build --secret id=db_password -t myapp:secure .
```

</div>


**secret در هیچ لایه‌ای ذخیره نمی‌شه.** فقط در زمان build در `/run/secrets/<id>` قابل دسترسیه.

---

### ۳.۳. SSH Mount — برای private repo

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```dockerfile
# syntax=docker/dockerfile:1.7
FROM alpine/git AS repo
RUN --mount=type=ssh \
    git clone git@github.com:myorg/private-repo.git /src
```

</div>


<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```bash
# build با ssh forwarding
docker build --ssh default -t myapp:secure .
```

</div>


**نیاز به `ssh-agent` فعال:** `eval $(ssh-agent) && ssh-add ~/.ssh/id_ed25519`

---

### ۳.۴. نکات تکمیلی BuildKit

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```dockerfile
# فعال‌سازی صریح آخرین سینتکس
# syntax=docker/dockerfile:1.7

# heredoc (نوشتن فایل بدون COPY)
RUN <<EOF
echo "line 1" > /etc/config
echo "line 2" >> /etc/config
EOF

# mount به‌عنوان منبع (bind)
RUN --mount=type=bind,source=./config,target=/config \
    cp /config/app.conf /etc/app/
```

</div>


---

## بخش چهارم: Multi-Architecture Build

### ۴.۱. چرا multi-arch؟

اگه image رو روی Mac M1/M2 (arm64) build کنی و روی سرور Ubuntu (amd64) اجرا کنی، **کار نمی‌کنه**. `buildx` این مشکل رو حل می‌کنه.

### ۴.۲. راه‌اندازی

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```bash
# ساخت builder با پشتیبانی multi-arch
docker buildx create --name mybuilder --use --bootstrap

# چک کن
docker buildx ls
```

</div>


### ۴.۳. Build و Push

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```bash
# build برای چند معماری + push (multi-arch فقط با push کار می‌کنه)
docker buildx build \
  --platform linux/amd64,linux/arm64 \
  --tag myregistry/myapp:1.0 \
  --push \
  .
```

</div>


**نکته کلیدی:** برای multi-arch، باید **push** کنی (یا `--load` فقط برای یه معماری). خروجی یه **manifest list** هست که registry هر معماری رو جدا نگه می‌داره.

### ۴.۴. بررسی

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```bash
docker buildx imagetools inspect myregistry/myapp:1.0
```

</div>


خروجی باید هر دو معماری رو نشون بده.

### ۴.۵. در Dockerfile — استفاده از `TARGETARCH`

اگه باینری architecture-specific داری:

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```dockerfile
# syntax=docker/dockerfile:1.7
FROM alpine AS dl
ARG TARGETARCH
RUN wget -O /usr/local/bin/tool \
    "https://example.com/tool-linux-${TARGETARCH}"
RUN chmod +x /usr/local/bin/tool

FROM alpine
COPY --from=dl /usr/local/bin/tool /usr/local/bin/tool
ENTRYPOINT ["tool"]
```

</div>


`TARGETARCH` خودکار `amd64` یا `arm64` می‌شه.

---

## بخش پنجم: Registry خصوصی (Private Registry)

### ۵.۱. راه‌اندازی ساده (بدون auth و TLS — فقط برای تست)

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```bash
docker run -d -p 5000:5000 --name learn-registry registry:2

docker tag myapp:secure localhost:5000/myapp:secure
docker push localhost:5000/myapp:secure
docker pull localhost:5000/myapp:secure
```

</div>


### ۵.۲. اضافه کردن Basic Auth

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```bash
# ساخت فایل htpasswd
mkdir -p auth
docker run --rm --entrypoint htpasswd httpd:2 \
  -Bbn admin mypassword > auth/htpasswd

# ری‌استارت registry با auth
docker rm -f learn-registry
docker run -d -p 5001:5000 --name learn-registry \
  -v "$PWD/auth:/auth" \
  -e REGISTRY_AUTH=htpasswd \
  -e REGISTRY_AUTH_HTPASSWD_REALM='Registry Realm' \
  -e REGISTRY_AUTH_HTPASSWD_PATH=/auth/htpasswd \
  registry:2

# login و push
docker login localhost:5001
docker tag myapp:secure localhost:5001/myapp:secure
docker push localhost:5001/myapp:secure
```

</div>


**بدون login، pull با 401 رد می‌شه**.

### ۵.۳. اضافه کردن TLS (Self-signed برای تست)

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```bash
mkdir -p certs
openssl req -newkey rsa:4096 -nodes -sha256 -x509 -days 365 \
  -subj '/CN=registry.local' \
  -addext 'subjectAltName=DNS:registry.local,DNS:localhost,IP:127.0.0.1' \
  -keyout certs/domain.key -out certs/domain.crt

docker rm -f learn-registry
docker run -d -p 5002:5000 --name learn-registry \
  -v "$PWD/auth:/auth" \
  -v "$PWD/certs:/certs" \
  -e REGISTRY_AUTH=htpasswd \
  -e REGISTRY_AUTH_HTPASSWD_REALM='Realm' \
  -e REGISTRY_AUTH_HTPASSWD_PATH=/auth/htpasswd \
  -e REGISTRY_HTTP_TLS_CERTIFICATE=/certs/domain.crt \
  -e REGISTRY_HTTP_TLS_KEY=/certs/domain.key \
  registry:2

# trust cert روی هاست (Linux)
sudo mkdir -p /etc/docker/certs.d/registry.local:5002
sudo cp certs/domain.crt /etc/docker/certs.d/registry.local:5002/ca.crt
sudo systemctl restart docker

# استفاده
docker tag myapp:secure registry.local:5002/myapp:secure
docker push registry.local:5002/myapp:secure
```

</div>


**نکات عیب‌یابی**:
- `http: server gave HTTP response to HTTPS client` → daemon expects HTTPS. برای تست insecure، `localhost:5002` رو به `insecure-registries` در `daemon.json` اضافه کن.
- **Self-signed cert rejected** → CA رو روی هاست trust کن و Docker daemon رو ری‌استارت کن.

### ۵.۴. Compose برای Registry

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```yaml
services:
  registry:
    image: registry:2
    ports:
      - "5000:5000"
    volumes:
      - registry-data:/var/lib/registry
    environment:
      REGISTRY_AUTH: htpasswd
      REGISTRY_AUTH_HTPASSWD_REALM: Registry Realm
      REGISTRY_AUTH_HTPASSWD_PATH: /auth/htpasswd
    restart: unless-stopped

volumes:
  registry-data:
```

</div>


---

## بخش ششم: CI/CD با GitHub Actions

### ۶.۱. Workflow کامل با cache

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```yaml
name: docker-build

on:
  push:
    branches: [main]
  pull_request:

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4

      - name: Login to Docker Hub
        if: github.event_name != 'pull_request'
        uses: docker/login-action@v4
        with:
          username: ${{ vars.DOCKERHUB_USERNAME }}
          password: ${{ secrets.DOCKERHUB_TOKEN }}

      - name: Set up Docker Buildx
        uses: docker/setup-buildx-action@v4

      - name: Build and push
        uses: docker/build-push-action@v7
        with:
          context: .
          push: ${{ github.event_name != 'pull_request' }}
          tags: user/app:${{ github.sha }},user/app:latest
          platforms: linux/amd64,linux/arm64
          cache-from: type=gha
          cache-to: type=gha,mode=max
```

</div>


**نکته:** `cache-from: type=gha` و `cache-to: type=gha,mode=max` از GitHub Actions cache استفاده می‌کنن تا build ها بین run ها سریع باشن. `mode=max` لایه‌های میانی رو هم export می‌کنه.

**⚠️ مهم:** از ۱۵ آوریل ۲۰۲۵، فقط **GitHub Cache service API v2** پشتیبانی می‌شه. حداقل نسخه‌های لازم: Buildx ≥ v0.21.0, BuildKit ≥ v0.20.0, Compose ≥ v2.33.1, Docker Engine ≥ v28.0.0.

---

### ۶.۲. Workflow با اسکن امنیتی (CVE Gate)

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```yaml
name: secure-build

on:
  push:
    branches: [main]

jobs:
  build-scan-push:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4

      - uses: docker/setup-buildx-action@v4

      - name: Build image
        uses: docker/build-push-action@v7
        with:
          context: .
          load: true
          tags: myapp:scan
          build-args: |
            APT_CACHE_BUST=${{ github.run_id }}-${{ github.run_attempt }}
          cache-from: type=gha
          cache-to: type=gha,mode=max

      - name: Scan with Trivy
        uses: aquasecurity/trivy-action@master
        with:
          image-ref: myapp:scan
          severity: 'CRITICAL,HIGH'
          ignore-unfixed: true
          exit-code: '1'    # fail pipeline

      - name: Scan with Docker Scout
        if: success()
        uses: docker/scout-action@v1
        with:
          command: cves
          image: myapp:scan
          only-severity: critical,high
          only-fixed: true
          exit-code: true

      - name: Login to registry
        if: success() && github.ref == 'refs/heads/main'
        uses: docker/login-action@v4
        with:
          username: ${{ vars.DOCKERHUB_USERNAME }}
          password: ${{ secrets.DOCKERHUB_TOKEN }}

      - name: Push
        if: success() && github.ref == 'refs/heads/main'
        uses: docker/build-push-action@v7
        with:
          context: .
          push: true
          tags: user/app:${{ github.sha }},user/app:latest
          platforms: linux/amd64,linux/arm64
          cache-from: type=gha
```

</div>


**نکته:** `APT_CACHE_BUST=${{ github.run_id }}-${{ github.run_attempt }}` باعث می‌شه لایه `apt` هر run دوباره اجرا بشه و آخرین security update ها گرفته بشن. **همون image که اسکن شد، push می‌شه** (اسکن و push از یک build استفاده می‌کنن).

---

### ۶.۳. Workflow با Dependabot برای base image

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```yaml
# .github/dependabot.yml
version: 2
updates:
  - package-ecosystem: "docker"
    directory: "/"
    schedule:
      interval: "weekly"
```

</div>


این base image رو خودکار آپدیت می‌کنه (و PR می‌سازه) تا CVE های base image کم بشن.

---

## بخش هفتم: چک‌لیست جامع امنیت و بهینه‌سازی

### Dockerfile
- [ ] `FROM` با tag دقیق + digest (`@sha256:...`)
- [ ] `USER` غیر root
- [ ] multi-stage (فقط runtime تو image نهایی)
- [ ] `.dockerignore` کامل
- [ ] `--mount=type=cache` برای package manager
- [ ] `--mount=type=secret` برای secret (نه `ENV`/`COPY .env`)
- [ ] `HEALTHCHECK`
- [ ] `apt-get update && install && rm -rf /var/lib/apt/lists/*` تو یه `RUN`
- [ ] `--no-install-recommends` برای apt
- [ ] base image کوچیک (alpine/distroless/slim)

### Runtime
- [ ] `--read-only` + `--tmpfs` برای مسیرهای قابل نوشتن
- [ ] `--cap-drop=ALL` + `--cap-add` حداقلی
- [ ] `--security-opt=no-new-privileges:true`
- [ ] `--security-opt seccomp=builtin` (یا سفارشی)
- [ ] `--memory`, `--cpus`, `--pids-limit`
- [ ] ❌ هرگز `--privileged`
- [ ] ❌ هرگز mount کردن `/var/run/docker.sock`

### CI/CD
- [ ] اسکن CVE (Trivy یا Docker Scout) با `exit-code` برای fail
- [ ] `--only-fixed` برای CVE های قابل رفع
- [ ] cache با `type=gha,mode=max`
- [ ] `APT_CACHE_BUST` برای تازه‌سازی security updates
- [ ] multi-arch build (اگه لازمه)
- [ ] Dependabot برای base image

---

## بخش هشتم: تمرین عملی — همه با هم

یه image امن و بهینه بساز، اسکن کن، و اصلاح کن:

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```bash
mkdir -p ~/docker-learn/step5/secure-app && cd ~/docker-learn/step5/secure-app
```

</div>


**فایل‌ها:**

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```bash
cat > package.json <<'EOF'
{
  "name": "secure-app",
  "version": "1.0.0",
  "scripts": { "start": "node index.js" },
  "dependencies": { "express": "^4.19.0" }
}
EOF

cat > index.js <<'EOF'
const express = require('express');
const app = express();
app.get('/', (_, res) => res.send('secure!'));
app.get('/health', (_, res) => res.json({ status: 'ok' }));
app.listen(3000, () => console.log('on 3000'));
EOF

cat > .dockerignore <<'EOF'
node_modules
.git
.env
Dockerfile
EOF
```

</div>


**Dockerfile امن و بهینه:**

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```dockerfile
# syntax=docker/dockerfile:1.7

# ─── مرحله build ───
FROM node:20-alpine AS builder
WORKDIR /app
COPY package*.json ./
RUN --mount=type=cache,target=/root/.npm \
    npm ci --prefer-offline --no-audit
COPY . .
RUN npm ci --only=production

# ─── مرحله runtime ───
FROM node:20-alpine
WORKDIR /app

# کاربر غیر root با UID ثابت
RUN addgroup -g 10001 app && adduser -u 10001 -G app -D app

COPY --from=builder --chown=10001:10001 /app/node_modules ./node_modules
COPY --chown=10001:10001 index.js package.json ./

USER 10001:10001

ENV NODE_ENV=production
EXPOSE 3000

HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD wget -qO- http://localhost:3000/health || exit 1

CMD ["node", "index.js"]
```

</div>


**Build و اسکن:**

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```bash
docker build -t learn-secure:v1 .

# اسکن با Trivy
trivy image --severity HIGH,CRITICAL --ignore-unfixed learn-secure:v1

# یا Docker Scout
docker scout cves --only-severity critical,high --only-fixed learn-secure:v1
```

</div>


**اجرای سخت‌شده:**

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```bash
docker run -d --name learn-secure \
  -p 8890:3000 \
  --read-only \
  --tmpfs /tmp:rw,noexec,nosuid,size=32m \
  --cap-drop=ALL \
  --security-opt=no-new-privileges:true \
  --security-opt seccomp=builtin \
  --memory=256m --cpus=0.5 --pids-limit=100 \
  learn-secure:v1

# تست
curl http://localhost:8890/health
# → {"status":"ok"}

# چک وضعیت
docker ps
# STATUS: Up ... (healthy)

# اگه کاربر غیر root درست باشه:
docker exec learn-secure id
# uid=10001 gid=10001
```

</div>


**مقایسه با نسخه غیر امن:**

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```bash
# نسخه naive
docker build -t learn-secure:naive -f- . <<'EOF'
FROM node:20
WORKDIR /app
COPY . .
RUN npm install
CMD ["node", "index.js"]
EOF

# مقایسه حجم
docker image ls learn-secure

# اسکن نسخه naive (CVE بیشتر + کاربر root)
trivy image --severity HIGH,CRITICAL learn-secure:naive
```

</div>


تفاوت حجم و تعداد CVE رو ببین. این قدرت بهینه‌سازی و امنیت.

**پاک‌سازی:**

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```bash
docker rm -f learn-secure 2>/dev/null
docker image rm learn-secure:v1 learn-secure:naive 2>/dev/null
```

</div>


---

## سوالات خودآزمایی

1. `--read-only` رو با `--tmpfs` چطور ترکیب می‌کنی و چرا؟
2. `--cap-drop=ALL` چه سودی داره؟ کِی `--cap-add` لازمه؟
3. `no-new-privileges` چه چیزی رو بلاک می‌کنه؟
4. چرا mount کردن `/var/run/docker.sock` خطرناکه؟
5. `--mount=type=cache` چه فرقی با layer caching معمولی داره؟
6. `--mount=type=secret` چرا از `ENV` امن‌تره؟
7. برای multi-arch build، چرا `--push` لازمه؟
8. `APT_CACHE_BUST` چه مشکلی رو حل می‌کنه؟
9. تفاوت `docker scout cves` و `trivy image` چیه؟
10. چرا `--privileged` ممنوعه؟

<details>
<summary>جواب‌ها</summary>

1. `--read-only` روت FS رو immutable می‌کنه. `--tmpfs` برای مسیرهایی که باید نوشته بشن (مثل `/tmp`) یه فایل‌سیستم موقت RAM-based می‌ده.
2. capabilities اضافی رو حذف می‌کنه و سطح حمله رو کم می‌کنه. `NET_BIND_SERVICE` فقط برای bind به پورت <1024 لازمه.
3. اجرای باینری‌های `setuid`/`setgid` و ارتقای دسترسی از طریق `su`/`sudo` رو بلاک می‌کنه.
4. Docker daemon با root اجرا می‌شه و socket API کاملشه. mount کردنش = دسترسی root روی هاست.
5. cache mount **خارج از image** ذخیره می‌شه و بین build ها reuse می‌شه، ولی وارد image نهایی نمی‌شه. layer caching معمولی به محتوای لایه وابسته‌ست.
6. secret mount تو لایه‌های image ذخیره نمی‌شه و تو `docker history` دیده نمی‌شه.
7. multi-arch یه manifest list تولید می‌کنه که registry باید نگه‌داره. `--load` فقط یه معماری رو local نگه می‌داره.
8. cache قدیمی لایه `apt` رو می‌شکنه تا `apt-get upgrade` دوباره اجرا بشه و آخرین security patch ها گرفته بشن.
9. Docker Scout ابزار رسمی Docker با تمرکز روی SBOM و recommendations. Trivy متن‌باز و چندمنظوره (vuln, misconfig, secret).
10. `--privileged` همه محدودیت‌ها (seccomp, capabilities, read-only, ...) رو برمی‌داره و container عملاً root روی هاست می‌شه.
</details>

---

## چک‌لیست پایان مرحله ۵

- [ ] یه image با `USER` غیر root و `--read-only` اجرا کردم
- [ ] `--cap-drop=ALL` و `no-new-privileges` رو تست کردم
- [ ] با Trivy یا Docker Scout اسکن کردم و CVE دیدم
- [ ] `--mount=type=cache` رو تو یه Dockerfile استفاده کردم و سرعتش رو دیدم
- [ ] `--mount=type=secret` رو تست کردم
- [ ] `docker buildx` با `--platform` رو دیدم (حتی اگه push نکردم)
- [ ] Registry خصوصی با htpasswd راه انداختم
- [ ] یه workflow ساده GitHub Actions با cache نوشتم (یا فهمیدم)

---

**قدم بعدی:** بعد از این مرحله، پایه‌ت کامل شده. مسیرهای بعدی می‌تونه باشه:
- **Kubernetes** (گام طبیعی بعد از Docker)
- **Docker Swarm** (اگه orchestration سبک‌تر می‌خوای)
- **پروژه واقعی** (همون FastAPI/Node که پیشنهاد دادم — حالا با امنیت و CI/CD کامل)

**آماده‌ای بریم سراغ پروژه واقعی (مرحله ۴ رو با یه مثال عملی کامل کنیم)؟** یا می‌خوای اول Kubernetes رو شروع کنیم؟


</div>

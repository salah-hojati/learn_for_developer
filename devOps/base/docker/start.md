<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">


# نقشه راه یادگیری کامل Docker 🐳

خوبه که اول امنیت setup فعلی رو تضمین کنیم، بعد شروع کنیم به یادگیری. بریم مرحله به مرحله.

---

## مرحله ۰: محافظت از چیزهای فعلی (خیلی مهم!)

قبل از هر کاری، این کارها رو بکن تا compose های در حال اجرا آسیب نبینن:
</div>

```bash
# 1. لیست container های در حال اجرا
docker ps -a

# 2. لیست image ها
docker images

# 3. لیست volume ها (اینا دیتا هستن، حواست باشه!)
docker volume ls

# 4. لیست network ها
docker network ls

# 5. محل compose files فعلی رو یادداشت کن
find / -name "docker-compose.yml" -o -name "compose.yml" 2>/dev/null
```

<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">
**نکته کلیدی:** برای یادگیری یه **prefix جدا** استفاده کن. مثلاً همه container هایی که می‌سازی رو با `learn-` شروع کن:
- `learn-nginx`, `learn-postgres`, `learn-node`
- اینطوری هیچ‌وقت با setup اصلی قاطی نمی‌شن

**قانون طلایی:** هیچ‌وقت `docker system prune -a` یا `docker volume prune` نزن! اینا setup اصلی رو نابود می‌کنن.

---

## مرحله ۱: مبانی (۱-۲ هفته)

### مفاهیم پایه‌ای که باید کامل بفهمی:
| مفهوم | توضیح |
|-------|-------|
| **Image** | تمپلیت read-only، مثل snapshot |
| **Container** | نمونه اجرایی image، مثل process |
| **Dockerfile** | دستور ساخت image |
| **Volume** | ذخیره‌سازی پایدار |
| **Network** | ارتباط بین container ها |
| **Registry** | انبار image (Docker Hub) |

### تمرین‌های عملی:

</div>

```bash
# شروع ساده - hello world
docker run hello-world

# Ubuntu interactive
docker run -it --name learn-ubuntu ubuntu:22.04 bash
# داخلش: apt update && apt install curl -y
# بعد exit کن، دوباره با دستور زیر برگرد:
docker start -ai learn-ubuntu

# پاک کردن تمیز
docker rm learn-ubuntu
```
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">

### دستورات ضروری که باید حفظ شی:
</div>

```bash
docker ps -a              # همه container ها
docker images             # image ها
docker logs <name>        # لاگ
docker exec -it <name> sh # ورود به container در حال اجرا
docker inspect <name>     # جزئیات کامل
docker stats              # مصرف منابع
docker stop/start/restart <name>
docker rm <name>          # حذف container
docker rmi <image>        # حذف image
```
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">
**منبع پیشنهادی:** [play-with-docker.com](https://labs.play-with-docker.com/) — بدون دست زدن به سیستم خودت تمرین کن.

---

## مرحله ۲: Dockerfile نویسی (۱-۲ هفته)

### یه پروژه کوچیک بساز - مثلاً یه اپ Node یا Python:
</div>


```dockerfile
# نمونه Dockerfile برای Python
FROM python:3.11-slim

WORKDIR /app

COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

COPY . .

EXPOSE 8000
CMD ["python", "app.py"]
```
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">

### چیزهایی که باید یاد بگیری:
- `FROM`, `RUN`, `COPY`, `ADD`, `WORKDIR`, `ENV`, `EXPOSE`, `CMD`, `ENTRYPOINT`
- تفاوت `CMD` و `ENTRYPOINT`
- **Multi-stage build** (خیلی مهم برای image کوچیک)
- `.dockerignore`
- Layer caching (ترتیب دستورات چرا مهمه)

### تمرین:
یه Flask/FastAPI ساده بنویس و داکرایز کن. بعد یه نسخه multi-stage ازش بساز و حجم image رو مقایسه کن.

---

## مرحله ۳: Volume و Network (۱ هفته)

### Volume:
</div>

```bash
# named volume
docker volume create learn-data
docker run -v learn-data:/data ubuntu touch /data/test.txt

# bind mount
docker run -v $(pwd)/mydata:/data ubuntu ls /data
```
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">
**تفاوت bind mount و named volume رو کامل بفهم** — این سوال مصاحبه‌ایه.

### Network:
</div>

```bash
docker network create learn-net
docker run -d --name learn-db --network learn-net postgres:15
docker run -it --network learn-net ubuntu bash
# داخلش: ping learn-db  ← اینجوری با اسم resolve می‌شه
```
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">
انواع network: `bridge`, `host`, `none`, `overlay` — هر کدوم کجا استفاده می‌شن؟

---

## مرحله ۴: Docker Compose (۱ هفته)

### یه stack واقعی بساز (جدا از stack فعلی):
</div>

```yaml
# learn-compose/docker-compose.yml
services:
  web:
    build: ./app
    ports:
      - "8080:8000"
    environment:
      - DB_HOST=db
    depends_on:
      - db
    networks:
      - learn-net

  db:
    image: postgres:15
    environment:
      POSTGRES_PASSWORD: secret
    volumes:
      - learn-pgdata:/var/lib/postgresql/data
    networks:
      - learn-net

volumes:
  learn-pgdata:

networks:
  learn-net:
```
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">
**نکته امنیتی برای setup فعلی‌ت:**
- اسم project رو با `-p learn` مشخص کن: `docker compose -p learn up -d`
- اینطوری container ها و network هاش جدا می‌شن از اونایی که داری

### چیزهایی که باید مسلط شی:
- `depends_on` + `healthcheck` (چرا depends_on کافی نیست)
- `profiles` (برای سرویس‌های اختیاری)
- `env_file`, `secrets`
- `override` files (dev vs prod)
- `docker compose watch` (جدید)

---

## مرحله ۵: مباحث پیشرفته (۲-۳ هفته)

| موضوع | چرا مهمه |
|-------|----------|
| **Multi-stage builds** | image کوچیک، امن‌تر |
| **BuildKit & buildx** | build سریع‌تر، multi-arch |
| **Docker Swarm** | orchestration ساده |
| **Registry خصوصی** | push/pull به registry خودت |
| **Health checks** | نظارت بر سلامت |
| **Resource limits** | `--memory`, `--cpus` |
| **Security** | non-root user، read-only fs، capabilities |
| **Logging drivers** | ارسال لاگ به جاهای مختلف |

### امنیت - چک‌لیست حرفه‌ای:
```dockerfile
# کاربر غیر root
RUN useradd -m appuser
USER appuser

# فایل سیستم read-only
docker run --read-only --tmpfs /tmp myimage
```

---

## مرحله ۶: پروژه‌های واقعی برای تمرین

این پروژه‌ها رو به ترتیب انجام بده:

1. **ساده:** یه Nginx که یه صفحه HTML سرو کنه
2. **متوسط:** Node/Python app + Postgres + Redis با compose
3. **پیشرفته:** همون stack + Nginx reverse proxy + SSL (Let's Encrypt)
4. **حرفه‌ای:** Multi-stage build + healthcheck + CI/CD با GitHub Actions
5. **نهایی:** یه mini PaaS راه بنداز با Traefik + چند سرویس

---

## منابع پیشنهادی

- **مستندات رسمی:** [docs.docker.com](https://docs.docker.com/) (بهترین منبع)
- **تمرین آنلاین:** [Play with Docker](https://labs.play-with-docker.com/)
- **کتاب:** "Docker Deep Dive" از Nigel Poulton
- **ویدیو:** کانال NetworkChuck, TechWorld with Nana
- **مسیر یادگیری:** [Docker Curriculum](https://docker-curriculum.com/)

---

## برنامه هفتگی پیشنهادی

| هفته | تمرکز |
|------|-------|
| ۱ | نصب، دستورات پایه، container lifecycle |
| ۲ | Dockerfile، build، multi-stage |
| ۳ | Volume، Network |
| ۴ | Docker Compose |
| ۵ | امنیت، optimization، registry |
| ۶ | Swarm یا Kubernetes مقدماتی |
| ۷+ | پروژه واقعی |

---

## چک‌لیست سریع قبل از هر تمرین

```
□ setup اصلی رو با docker ps -a چک کن و بکاپ بگیر
□ همه اسامی رو با learn- شروع کن  
□ compose رو با -p learn اجرا کن
□ هیچ‌وقت prune نزن
□ بعد تمرین، فقط چیزهای learn- رو پاک کن
```

---

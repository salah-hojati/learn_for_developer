

<!-- SOURCE: ch1.md -->
# File: ch1.md

<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">

باشه، بیا این موضوع رو **کاملاً از صفر و بدون اصطلاحات پیچیده** یاد بگیریم. فعلاً اصلاً به جدول نگاه نکن.

## **۱. Regex قراره چی کار کنه؟**

فرض کن این متن رو داریم:

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```
String text = "<a><b><c>";
```

</div>


ما می‌خوای با Regex قسمت‌هایی رو که بین `<` و `>` هستند پیدا کنیم.

مثلاً:

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```
<a>
<b>
<c>
```

</div>


اما Regex ما اینه:

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```
<.+>
```

</div>


بیا ببینیم چرا این سه حالت نتیجه‌های متفاوتی می‌دن.

## **۲. اول Greedy (حریص) رو یاد بگیر**

این Regex رو ببین:

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```
<.+>
```

</div>


معنی اجزاش:

| علامت | معنی                        |
| ----- | --------------------------- |
| `<`   | دقیقاً یک علامت `<` پیدا کن |
| `.`   | هر کاراکتری                 |
| `+`   | حداقل یک کاراکتر            |
| `>`   | دقیقاً یک علامت `>`         |

حالا Java سعی می‌کنه این الگو رو در متن پیدا کنه.

## **متن ما**

`<a><b><c>`

Greedy می‌خواهد تا جای ممکن از متن را بگیرد.

وقتی به اولین `<` می‌رسه، شروع می‌کنه به گرفتن کاراکترها.

اول ممکنه این رو در نظر بگیریم:

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```
<a>
```

</div>


اما Greedy می‌گه:

> چرا همین‌جا متوقف بشم؟ هنوز `<b><c>` هم وجود داره!

پس تا آخر متن پیش می‌ره و آخرین `>` رو انتخاب می‌کنه.

نتیجه:

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```
<a><b><c>
```

</div>


یعنی **Greedy سعی می‌کنه بزرگ‌ترین Match ممکن رو پیدا کنه.**

## **۳. Lazy (تنبل) دقیقاً برعکس عمل می‌کنه**

حالا فقط یک `?` اضافه می‌کنیم:

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```
<.+?>
```

</div>


تفاوت این دو فقط همین علامت `?` هست.

Lazy می‌گه:

> من نمی‌خوام بیشتر از چیزی که لازم دارم کاراکتر بگیرم. همین که الگو کامل شد، متوقف می‌شم.

از اول متن شروع می‌کنه:

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```
<a><b><c>
```

</div>


به اولین `>` که می‌رسه، الگو کامل شده:

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```
<a>
```

</div>


پس متوقف می‌شه.

بعد جستجو از ادامه متن:

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```
<b><c>
```

</div>


Match دوم:

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```
<b>
```

</div>


و بعد:

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```
<c>
```

</div>


نتیجه نهایی:

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```
<a>
<b>
<c>
```

</div>


## **تفاوت اصلی**

Greedy → تا جای ممکن می‌گیرد.

Lazy → به محض اینکه بتواند Match شود، متوقف می‌شود.

## **۴. حالا Possessive (بدون عقب‌گرد)**

این قسمت رو فعلاً فقط با یک مثال یاد بگیر.

فرض کن Regex ما اینه:

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```
<.++>
```

</div>


این Regex از دو قسمت تشکیل شده:

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```
<.++>
```

</div>


- `<` علامت شروع رو پیدا می‌کنه.
- `.++` یعنی یک یا چند کاراکتر را بگیر، اما **دیگه از کاراکترهایی که گرفتی عقب‌نشینی نکن**.
- `>` باید در انتها Match بشه.

در حالت Greedy، اگر الگو موفق نشه، Regex می‌تونه بعضی از کاراکترهایی رو که گرفته پس بده (به این می‌گیم Backtracking).

اما Possessive می‌گه:

> هرچی گرفتم، دیگه پس نمی‌دم!

به همین دلیل در این مثال کل متن رو می‌گیره و عقب نمی‌ره.

## **۵. با یک مثال ساده‌تر، Greedy و Lazy رو مقایسه کنیم**

این مثال از همه مهم‌تره.

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```
String text = "12345";
```

</div>


Regex:

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```
\d+
```

</div>


خروجی:

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```
12345
```

</div>


چون Greedy هست، تمام اعداد رو می‌گیره.

حالا:

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```
\d+?
```

</div>


اگر با `Matcher.find()` اجراش کنیم، خروجی تقریباً این می‌شه:

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```
1
2
3
4
5
```

</div>


چرا؟ چون Lazy سعی می‌کنه کمترین تعداد ممکن رو بگیره؛ اینجا هر بار فقط یک رقم.

## **۶. مثال واقعی Java (این قسمت مهمه)**

فرض کن یک متن HTML داریم:

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```
String html = "<b>سلام</b> و <b>خداحافظ</b>";
```

</div>


می‌خوای فقط متن داخل تگ `b` رو استخراج کنی.

### **روش اول: Greedy**

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```
Pattern pattern = Pattern.compile("<b>(.*)</b>");
Matcher matcher = pattern.matcher(html);

while (matcher.find()) {
    System.out.println(matcher.group(1));
}
```

</div>


خروجی:

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```
سلام</b> و <b>خداحافظ
```

</div>


چرا؟

`.*` حریص هست. تا آخر متن می‌ره و آخرین `</b>` رو پیدا می‌کنه. در نتیجه دو تگ `b` رو به هم وصل می‌کنه و یک Match بزرگ تحویل می‌ده.

### **روش دوم: Lazy**

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```
Pattern pattern = Pattern.compile("<b>(.*?)</b>");
Matcher matcher = pattern.matcher(html);

while (matcher.find()) {
    System.out.println(matcher.group(1));
}
```

</div>


خروجی:

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```
سلام
خداحافظ
```

</div>


اینجا `.*?` به محض اینکه به اولین `</b>` می‌رسه متوقف می‌شه.

## **۷. فقط این رو فعلاً حفظ کن**

## **خلاصه سه حالت**

**Greedy**

بیشترین مقدار ممکن

**Lazy**

کمترین مقدار لازم

**Possessive**

گرفتن و پس ندادن

| حالت       | علامت | رفتار                                               |
| ---------- | ----- | --------------------------------------------------- |
| Greedy     | `+`   | حداکثر رو می‌گیره، اگر لازم باشه عقب می‌ره          |
| Lazy       | `+?`  | ابتدا حداقل رو می‌گیره و در صورت نیاز بیشتر می‌گیره |
| Possessive | `++`  | حداکثر رو می‌گیره و دیگه عقب نمی‌ره                 |

### **یک جمله برای مصاحبه**

> Greedy تا حد امکان Match می‌کند و در صورت نیاز Backtracking انجام می‌دهد. Lazy کمترین Match ممکن را انتخاب می‌کند و در صورت نیاز گسترش می‌دهد. Possessive مثل Greedy عمل می‌کند، اما Backtracking ندارد.

اگر بخوای، در مرحله بعد می‌تونم با **۵ تمرین خیلی ساده و خروجی هر Regex** کاری کنم که این سه حالت کاملاً برات جا بیفته.
</div>



<!-- SOURCE: 4.md -->
# File: 4.md

<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">




# مرحله ۴: Docker Compose — همه‌چیز با هم 🎼

هدف این مرحله: بتونی **هر stack واقعی** رو با YAML تعریف کنی، بفهمی `depends_on` و `healthcheck` چطور کار می‌کنن، و **بدونی چطور با setup فعلیت تداخل نکنی**.

---

## ۱. Compose دقیقاً چیه و چرا؟

تا مرحله ۳، همه‌چیز رو **دستی** با `docker network create`، `docker volume create`، `docker run` می‌ساختیم. تصور کن ۱۰ سرویس داشته باشی:



<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```bash
# بدون Compose (کابوس)
docker network create myapp-net
docker volume create myapp-pgdata
docker volume create myapp-redisdata
docker run -d --name myapp-db --network myapp-net -e POSTGRES_PASSWORD=... -v myapp-pgdata:/var/lib/postgresql/data postgres:15
docker run -d --name myapp-cache --network myapp-net redis:alpine
docker run -d --name myapp-api --network myapp-net -p 3000:3000 -e DB_HOST=myapp-db ...
# ... ۲۰ خط دیگه
```

</div>


**با Compose:**

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```yaml
services:
  db:
    image: postgres:15
    environment:
      POSTGRES_PASSWORD: secret
    volumes:
      - pgdata:/var/lib/postgresql/data
  cache:
    image: redis:alpine
  api:
    build: .
    ports:
      - "3000:3000"
    environment:
      DB_HOST: db
volumes:
  pgdata:
```

</div>


<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```bash
docker compose up -d
```

</div>


**Compose این کارها رو خودکار می‌کنه:**
- ✅ یه network به اسم `<project>_default` می‌سازه
- ✅ volume ها رو می‌سازه
- ✅ container ها رو به ترتیب dependency راه می‌اندازه
- ✅ DNS بین سرویس‌ها (با اسم سرویس!)
- ✅ یکجا `down` همه رو می‌بره

---

## ۲. 🔴 مهم‌ترین نکته برای تو: **Project Name**

Compose هر stack رو زیر یه **project** می‌بره. اسم project پیش‌فرض = **اسم پوشه** که compose توش هست. این برای setup فعلیت **حیاتیه**:

- **Network** می‌شه: `<project>_default`
- **Volume** می‌شه: `<project>_<volumename>`
- **Container** می‌شه: `<project>-<service>-1`

**خطر:** اگه تو یه پوشه به اسم `backend` compose بسازی و setup اصلیت هم پوشه‌ش `backend` باشه، collide می‌شن!

**راه‌حل قطعی:** همیشه `-p` بده:

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```bash
docker compose -p learn up -d
```

</div>


اینطوری:
- network → `learn_default`
- volume → `learn_pgdata`
- container → `learn-db-1`

**قانون طلایی:** از الان هر compose ای می‌سازی، با `-p learn` اجرا کن. یا `COMPOSE_PROJECT_NAME=learn` تو `.env`.

---

## ۳. اولین Compose — نسخه ساده

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```bash
mkdir -p ~/docker-learn/step4/first && cd ~/docker-learn/step4/first
```

</div>


**`docker-compose.yml`** (یا `compose.yml` — تو نسخه‌های جدید هر دو کار می‌کنن، ولی `compose.yml` توصیه‌ست):

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```yaml
services:
  web:
    image: nginx:alpine
    ports:
      - "8888:80"
```

</div>


<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```bash
# اجرا
docker compose -p learn up -d

# چک
docker compose -p learn ps

# لاگ‌ها
docker compose -p learn logs

# دنبال کردن لاگ‌ها
docker compose -p learn logs -f web

# متوقف کردن و حذف
docker compose -p learn down
```

</div>


**بعد از `up` اینا رو ببین:**

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```bash
docker ps --filter "name=learn-"
docker network ls | grep learn
docker volume ls | grep learn
```

</div>


**نتیجه:** Compose خودش یه network ساخت (`learn_default`)، container رو بهش وصل کرد.

---

## ۴. دستورات Compose — چیت‌شیت

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```bash
# ─── چرخه حیات ───
docker compose -p learn up -d              # ساخت و اجرا در پس‌زمینه
docker compose -p learn up -d --build      # rebuild image ها
docker compose -p learn up -d --force-recreate  # بازسازی اجباری
docker compose -p learn down               # حذف container + network (volume می‌مونه!)
docker compose -p learn down -v            # ⚠️ + حذف volume ها (دیتا می‌ره)
docker compose -p learn down --rmi all     # + حذف image ها
docker compose -p learn stop               # متوقف (نه حذف)
docker compose -p learn start              # شروع دوباره
docker compose -p learn restart            # ری‌استارت

# ─── مشاهده ───
docker compose -p learn ps                 # وضعیت
docker compose -p learn ps -a              # همه (حتی متوقف)
docker compose -p learn logs               # همه لاگ‌ها
docker compose -p learn logs -f api        # لاگ یه سرویس زنده
docker compose -p learn logs --tail=100    # آخرین ۱۰۰ خط
docker compose -p learn top                # process ها
docker compose -p learn images             # image های استفاده شده

# ─── تعامل ───
docker compose -p learn exec api bash      # shell تو سرویس در حال اجرا
docker compose -p learn run --rm api sh    # یه container موقت
docker compose -p learn run --rm api python manage.py migrate

# ─── build ───
docker compose -p learn build              # build همه
docker compose -p learn build api          # فقط یه سرویس
docker compose -p learn pull               # pull image ها

# ─── بررسی/اعتبار ───
docker compose -p learn config             # نمایش compose نهایی (بعد از merge)
docker compose -p learn config --services  # لیست سرویس‌ها

# ─── scale ───
docker compose -p learn up -d --scale api=3  # ۳ تا api!
```

</div>


**نکته مهم:** `docker compose` (با فاصله) نسخه جدید (v2) هست. `docker-compose` (با خط تیره) نسخه قدیم. **از `docker compose` استفاده کن.**

---

## ۵. ساختار کامل یک compose file

بیایم یه compose کامل و واقعی رو خط به خط بفهمیم:

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```yaml
# ─── نسخه: از Compose v2 لازم نیست بنویسی ───
# version: "3.8"   ← قدیمی، حذف کن

# ─── سرویس‌ها: قلب فایل ───
services:

  # ─── ۱. یه سرویس با image آماده ───
  db:
    image: postgres:15
    environment:
      POSTGRES_USER: app
      POSTGRES_PASSWORD: secret
      POSTGRES_DB: mydb
    volumes:
      - pgdata:/var/lib/postgresql/data
    networks:
      - backend
    # ⚠️ پورت publish نشده — فقط داخل network

  # ─── ۲. یه سرویس با build ───
  api:
    build:
      context: ./api
      dockerfile: Dockerfile
      args:
        NODE_VERSION: "20"
      target: prod            # اگه multi-stage داری
    ports:
      - "3000:3000"
    environment:
      DB_HOST: db             # ← اسم سرویس!
      DB_PORT: 5432
      NODE_ENV: production
    env_file:
      - .env.api
    depends_on:
      db:
        condition: service_healthy   # ← نکته مهم! بعداً
    networks:
      - backend
      - frontend
    restart: unless-stopped
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:3000/health"]
      interval: 30s
      timeout: 5s
      retries: 3
      start_period: 10s

  # ─── ۳. سرویس frontend ───
  web:
    image: nginx:alpine
    ports:
      - "8080:80"
    volumes:
      - ./nginx.conf:/etc/nginx/nginx.conf:ro
      - ./static:/usr/share/nginx/html:ro
    depends_on:
      - api
    networks:
      - frontend

  # ─── ۴. cache ───
  cache:
    image: redis:7-alpine
    command: ["redis-server", "--appendonly", "yes"]
    volumes:
      - redisdata:/data
    networks:
      - backend

# ─── Volume ها ───
volumes:
  pgdata:
    # driver: local  ← پیش‌فرض
  redisdata:

# ─── Network ها ───
networks:
  backend:
    driver: bridge
  frontend:
    driver: bridge
```

</div>


**نکات مهم این فایل:**

1. **`DB_HOST: db`** — به اسم سرویس وصل می‌شه، نه IP!
2. **دو network جدا** (`backend`, `frontend`) — `web` به `db` دسترسی نداره! این isolation امنیتیه.
3. **`db` پورت publish نکرده** — فقط از داخل network accessible.
4. **`condition: service_healthy`** — تا db سالم نشه، api شروع نمی‌شه (بعداً توضیح کامل).

---

## ۶. `depends_on` — تله‌ی کلاسیک 🪤

### اشتباه رایج:

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```yaml
services:
  db:
    image: postgres:15
  api:
    build: .
    depends_on:
      - db
```

</div>


**فکر می‌کنی:** "api بعد از db شروع می‌شه، عالیه!"
**واقعیت:** `depends_on` فقط **ترتیب شروع** رو تعیین می‌کنه. Postgres ممکنه **چند ثانیه** طول بکشه تا آماده بشه. `api` بلافاصله بعد از `db` استارت می‌خوره، تلاش می‌کنه وصل شه، **fail** می‌شه.

### راه‌حل: `healthcheck` + `condition: service_healthy`

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```yaml
services:
  db:
    image: postgres:15
    environment:
      POSTGRES_PASSWORD: secret
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U postgres"]
      interval: 5s
      timeout: 5s
      retries: 10
      start_period: 10s

  api:
    build: .
    depends_on:
      db:
        condition: service_healthy   # ← کلید!
```

</div>


**حالا:** Compose صبر می‌کنه تا healthcheck دیتابیس `healthy` بشه، بعد api رو شروع می‌کنه.

### سه حالت `condition`:

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```yaml
depends_on:
  db:
    condition: service_started    # فقط شروع بشه (پیش‌فرض، ناکافی)
  db:
    condition: service_healthy    # healthcheck سبز بشه (توصیه!)
  db:
    condition: service_completed_successfully  # برای job های یک‌باره (migrations)
```

</div>


### مثال `service_completed_successfully` (مهم):

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```yaml
services:
  db:
    image: postgres:15
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U postgres"]
      interval: 5s
      retries: 10

  migrate:
    build: ./api
    command: ["npm", "run", "migrate"]
    depends_on:
      db:
        condition: service_healthy
    restart: "no"          # بعد از اتمام، متوقف بمونه

  api:
    build: ./api
    depends_on:
      migrate:
        condition: service_completed_successfully   # ← تا migration تموم نشه!
```

</div>


این پترن برای migration ها **بینهایت مهمه**.

---

## ۷. Environment و Secrets

### روش ۱: `environment` مستقیم

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```yaml
services:
  api:
    environment:
      DB_HOST: db
      DEBUG: "false"
      # یا لیست:
      # - DB_HOST=db
      # - DEBUG=false
```

</div>


**نکته:** مقدارها باید string باشن. `DEBUG: false` (بدون quote) ممکنه مشکل بشه. `DEBUG: "false"` بذار.

### روش ۲: `env_file`

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```yaml
services:
  api:
    env_file:
      - .env
      - .env.local
```

</div>


**`.env`:**

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```
DB_HOST=db
DB_PASSWORD=secret
API_KEY=xyz
```

</div>


**مزیت:** secrets تو git نمی‌رن (اگه `.env` رو ignore کنی).

### روش ۳: متغیر از هاست (variable substitution)

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```yaml
services:
  api:
    image: myapp:${TAG:-latest}
    environment:
      DB_PASSWORD: ${DB_PASSWORD:?DB_PASSWORD is required}
```

</div>


- `${VAR}` = مقدار از هاست
- `${VAR:-default}` = اگه نبود، default
- `${VAR:?error}` = اگه نبود، خطا بده (بهترین برای secrets!)

**Compose خودکار `.env` تو پوشه فعلی رو می‌خونه** برای این substitution.

### روش ۴: `secrets` (برای production)

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```yaml
services:
  api:
    secrets:
      - db_password

secrets:
  db_password:
    file: ./secrets/db_password.txt
```

</div>


داخل container: `/run/secrets/db_password`

**نکته:** تو standalone compose، secrets فقط **فایل mount** می‌شن. برای رمزنگاری واقعی باید Swarm یا K8s داشته باشی. ولی از ENV بهترن چون تو `docker inspect` دیده نمی‌شن.

---

## ۸. Profiles — سرویس‌های اختیاری

بعضی سرویس‌ها فقط برای dev یا debug لازم‌ان. `profiles` اینا رو مدیریت می‌کنه:

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```yaml
services:
  api:
    build: .
    # بدون profile → همیشه اجرا

  db:
    image: postgres:15

  adminer:
    image: adminer
    profiles: ["tools"]      # فقط با profile tools

  debug:
    image: busybox
    profiles: ["debug", "tools"]
```

</div>


<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```bash
# بدون profile — فقط api و db
docker compose -p learn up -d

# با profile tools — api, db, adminer, debug
docker compose -p learn --profile tools up -d

# فقط چیزهای یه profile خاص
docker compose -p learn --profile debug up -d
```

</div>


**کاربرد:** adminer, mailhog, portainer, pgadmin — همه تو `tools` بذار. production بدونشون بالا میاد.

---

## ۹. Override Files — dev vs prod

**الگو:**

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```
compose.yml             ← پایه (production-ready)
compose.override.yml    ← خودکار merge می‌شه (dev)
compose.prod.yml        ← برای production
```

</div>


**`compose.yml`:**

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```yaml
services:
  api:
    image: myregistry/api:latest
    restart: always
    environment:
      NODE_ENV: production

  db:
    image: postgres:15
    volumes:
      - pgdata:/var/lib/postgresql/data
```

</div>


**`compose.override.yml`** (خودکار merge، **بدون نیاز به دستور اضافه**):

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```yaml
services:
  api:
    build: .
    volumes:
      - ./src:/app/src          # live reload
    environment:
      NODE_ENV: development
    ports:
      - "3000:3000"

  db:
    ports:
      - "127.0.0.1:5432:5432"   # فقط از localhost
```

</div>


<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```bash
# dev (خودکار override رو merge می‌کنه)
docker compose -p learn up -d

# prod (فقط compose.yml)
docker compose -p learn -f compose.yml up -d

# یا هر ترکیبی
docker compose -p learn -f compose.yml -f compose.prod.yml up -d
```

</div>


**چرا override خوبه؟**
- `compose.yml` = منبع حقیقت (production)
- `override` = تغییرات dev که تو gitignore هستن
- **بدون دستور اضافه** فعاله → سرعت توسعه

**⚠️ نکته مهم:** `override.yml` خودکار فقط وقتی load می‌شه که `-f` نداده باشی.

---

## ۱۰. Healthcheck در Compose

همون healthcheck مرحله ۲، ولی حالا تو compose:

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```yaml
services:
  api:
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:3000/health"]
      interval: 30s        # هر ۳۰ ثانیه چک
      timeout: 5s          # هر چک بیشتر از ۵ ثانیه طول کشید = fail
      retries: 3           # ۳ بار fail = unhealthy
      start_period: 10s    # ۱۰ ثانیه اول نادیده بگیر (grace period)

  db:
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U postgres"]
      interval: 5s
      timeout: 3s
      retries: 10

  cache:
    healthcheck:
      test: ["CMD", "redis-cli", "ping"]
      interval: 10s
      retries: 5
```

</div>


**سه حالت سلامتی:**
- `starting` — تو `start_period`
- `healthy` — تست پاس شد
- `unhealthy` — `retries` بار fail شد

**مشاهده:**

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```bash
docker compose -p learn ps
# NAME           STATUS
# learn-api-1    Up 2 minutes (healthy)
# learn-db-1     Up 2 minutes (healthy)
```

</div>


---

## ۱۱. `restart` Policy

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```yaml
services:
  api:
    restart: unless-stopped    # ← توصیه برای production
```

</div>


| مقدار | معنی |
|-------|------|
| `no` | ری‌استارت نکن (پیش‌فرض) |
| `always` | همیشه، حتی بعد از reboot هاست |
| `unless-stopped` | مگر اینکه خودت stop کنی (بهترین برای prod) |
| `on-failure[:N]` | فقط اگه fail شد (کد خطا) |

---

## ۱۲. `command` و `entrypoint`

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```yaml
services:
  # override کردن CMD
  api:
    image: node:20-alpine
    command: ["npm", "run", "dev"]      # CMD رو عوض می‌کنه
    working_dir: /app
    volumes:
      - .:/app

  # override کردن ENTRYPOINT
  special:
    image: alpine
    entrypoint: ["sh", "-c"]
    command: ["echo hello && sleep 3600"]
```

</div>


**تفاوت `command` و `entrypoint` تو compose، همون Dockerfile هستش.**

---

## ۱۳. `build` — همه گزینه‌ها

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```yaml
services:
  api:
    build:
      context: .                    # مسیر build (پیش‌فرض = پوشه compose)
      dockerfile: Dockerfile.prod   # اسم فایل (پیش‌فرض = Dockerfile)
      target: prod                  # برای multi-stage
      args:
        NODE_VERSION: "20"
        BUILD_ENV: production
      cache_from:
        - myregistry/api:cache
      labels:
        - "com.example.version=1.0"
      shm_size: "2gb"               # برای build های سنگین
    image: myregistry/api:1.0       # ← tag نهایی (بعد از build)
```

</div>


**نکته `image` + `build`:** اگه هم `build` بدی هم `image`، Docker build می‌کنه و با اون اسم tag می‌زنه. بدون `image`، اسم تصادفی می‌شه.

---

## ۱۴. مثال کامل و عملی — همه‌چیز با هم

بیایم یه stack واقعی بسازیم که همه مفاهیم رو داشته باشه:

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```bash
mkdir -p ~/docker-learn/step4/full && cd ~/docker-learn/step4/full
```

</div>


**ساختار:**

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```
full/
├── compose.yml
├── .env
├── api/
│   ├── Dockerfile
│   ├── package.json
│   └── index.js
└── nginx.conf
```

</div>


**`api/Dockerfile`:**

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```dockerfile
FROM node:20-alpine
WORKDIR /app
COPY package*.json ./
RUN npm install --production
COPY index.js .
ENV NODE_ENV=production
EXPOSE 3000
CMD ["node", "index.js"]
```

</div>


**`api/package.json`:**

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```json
{
  "name": "api",
  "version": "1.0.0",
  "dependencies": { "express": "^4.19.0", "pg": "^8.11.0" }
}
```

</div>


**`api/index.js`:**

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```javascript
const express = require('express');
const { Pool } = require('pg');
const app = express();

const pool = new Pool({
  host: process.env.DB_HOST,
  port: process.env.DB_PORT,
  user: process.env.DB_USER,
  password: process.env.DB_PASSWORD,
  database: process.env.DB_NAME,
});

app.get('/', async (req, res) => {
  try {
    const r = await pool.query('SELECT NOW() as now');
    res.json({ message: 'hello', db_time: r.rows[0].now });
  } catch (e) {
    res.status(500).json({ error: e.message });
  }
});

app.get('/health', (req, res) => res.json({ status: 'ok' }));

app.listen(3000, () => console.log('api on 3000'));
```

</div>


**`.env`:**

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```bash
DB_USER=app
DB_PASSWORD=secret
DB_NAME=mydb
```

</div>


**`compose.yml`:**

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```yaml
services:
  db:
    image: postgres:15-alpine
    environment:
      POSTGRES_USER: ${DB_USER}
      POSTGRES_PASSWORD: ${DB_PASSWORD}
      POSTGRES_DB: ${DB_NAME}
    volumes:
      - pgdata:/var/lib/postgresql/data
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U ${DB_USER} -d ${DB_NAME}"]
      interval: 5s
      timeout: 3s
      retries: 10
      start_period: 10s
    networks:
      - backend
    restart: unless-stopped

  api:
    build:
      context: ./api
    environment:
      DB_HOST: db
      DB_PORT: 5432
      DB_USER: ${DB_USER}
      DB_PASSWORD: ${DB_PASSWORD}
      DB_NAME: ${DB_NAME}
    depends_on:
      db:
        condition: service_healthy
    healthcheck:
      test: ["CMD", "wget", "-qO-", "http://localhost:3000/health"]
      interval: 10s
      timeout: 3s
      retries: 5
      start_period: 10s
    networks:
      - backend
    restart: unless-stopped

  web:
    image: nginx:alpine
    ports:
      - "8090:80"
    depends_on:
      api:
        condition: service_healthy
    networks:
      - backend
    restart: unless-stopped

volumes:
  pgdata:

networks:
  backend:
    driver: bridge
```

</div>


**اجرا:**

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```bash
# اجرا
docker compose -p learn4 up -d

# تماشای راه‌اندازی
docker compose -p learn4 ps
docker compose -p learn4 logs -f

# تست
curl http://localhost:8090/health
# → {"status":"ok"}

curl http://localhost:8090/
# → باید nginx پیش‌فرض باشه (چون nginx.conf ننوشتیم)، یا اگه nginx.conf بدی، api رو proxy می‌کنه
```

</div>


**نکته:** چون `web` پورت publish شده ولی `api` نه، از بیرون فقط به `web` دسترسی داری. `web` از طریق network با `api` حرف می‌زنه. این isolation امنیتیه.

**آزمایش isolation:**

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```bash
# از web به db دسترسی داری؟
docker compose -p learn4 exec web sh -c "nc -zv db 5432"
# ✅ open — چون هر دو تو backend هستن

# اگه یه network جدا برای web می‌ساختیم، این fail می‌شد
```

</div>


**تست healthcheck و dependency:**

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```bash
# همه رو متوقف کن
docker compose -p learn4 down

# فقط db رو راه بنداز
docker compose -p learn4 up -d db

# وضعیت رو ببین
docker compose -p learn4 ps
# STATUS: Up ... (health: starting)
# چند ثانیه بعد: (healthy)

# حالا api رو بالا بیار — بلافاصله بعد از healthy
docker compose -p learn4 up -d api

# تست
sleep 3
curl http://localhost:8090/health
```

</div>


**پاک‌سازی:**

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```bash
docker compose -p learn4 down
# volume موند
docker volume ls | grep learn4

# اگه دیتا هم می‌خوای پاک کنی:
docker compose -p learn4 down -v
```

</div>


---

## ۱۵. `docker compose watch` — قابلیت جدید

از Docker Compose v2.22+، `watch` جایگزین hot reload دستی می‌شه:

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```yaml
services:
  api:
    build: .
    develop:
      watch:
        - action: sync          # کد رو sync کن
          path: ./src
          target: /app/src
        - action: rebuild       # rebuild کن
          path: package.json
        - action: sync+restart
          path: ./config
          target: /app/config
```

</div>


<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```bash
docker compose -p learn watch
```

</div>


**سه action:**
- `sync` — فایل رو کپی می‌کنه تو container (سریع)
- `rebuild` — image رو دوباره می‌سازه (برای `package.json`)
- `sync+restart` — کپی + ری‌استارت (برای config)

**خیلی بهتر از bind mount برای dev.**

---

## ۱۶. Compose خودکار setup رو merge می‌کنه (نکته امنیتی)

اگه تو یه پوشه دو فایل `compose.yml` و `compose.override.yml` داشته باشی، **خودکار** merge می‌شن. حالا تصور کن یه `compose.override.yml` از یه پروژه قدیمی یادت مونده باشه:

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```bash
# چک کن تو پوشه فعلیت چه فایلایی هستن
ls -la compose*.yml docker-compose*.yml
```

</div>


**راه امن:**

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```bash
# همیشه فایل رو صریح بده
docker compose -p learn -f compose.yml up -d
```

</div>


**یا چک کن چه چیزی merge می‌شه:**

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```bash
docker compose -p learn config
```

</div>


---

## ۱۷. `extends` — ارث‌بری بین فایل‌ها

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```yaml
# base.yml
services:
  base-api:
    image: myapi:latest
    environment:
      LOG_LEVEL: info
    restart: unless-stopped
```

</div>


<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```yaml
# compose.yml
services:
  api:
    extends:
      file: base.yml
      service: base-api
    environment:
      LOG_LEVEL: debug     # override
```

</div>


**کاربرد:** اشتراک تنظیمات بین چند stack. ولی **معمولاً از override files بهتره** برای اکثر کارها.

---

## ۱۸. `scale` — چند نمونه از یه سرویس

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```bash
# ۳ تا api
docker compose -p learn up -d --scale api=3
```

</div>


**⚠️ محدودیت:** اگه سرویس `ports` publish کرده باشه، نمی‌تونی scale کنی (پورت collide می‌شه). راه‌حل: از `expose` (فقط داخل network) یا reverse proxy (nginx, traefik) استفاده کن.

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```yaml
services:
  api:
    build: .
    expose:
      - "3000"           # فقط داخل network، پورت هاست نمی‌گیره
  lb:
    image: nginx
    ports:
      - "8080:80"
    # nginx.conf: upstream api → api:3000 (Docker DNS همه instance ها رو resolve می‌کنه)
```

</div>


---

## ۱۹. نکات کاربردی و ترفندها

### ۱۹.۱. متغیرهای `.env` پیش‌فرض
Compose خودکار `.env` تو **پوشه compose** رو می‌خونه. برای override:

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```bash
docker compose --env-file .env.prod -p learn up -d
```

</div>


### ۱۹.۲. `COMPOSE_PROJECT_NAME` تو `.env`

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```bash
# .env
COMPOSE_PROJECT_NAME=learn
```

</div>

حالا نیازی به `-p learn` نداری.

### ۱۹.۳. `container_name` — مراقب باش!

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```yaml
services:
  api:
    container_name: my-custom-api    # ← اسم ثابت
```

</div>


**⚠️ هشدار:** با `container_name` نمی‌تونی scale کنی (اسم‌ها collide می‌شن) و ممکنه با setup اصلیت collide بشه. **توصیه: نداشته باشش** و از naming خودکار Compose استفاده کن.

### ۱۹.۴. `pull_policy`

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```yaml
services:
  db:
    image: postgres:15
    pull_policy: always          # همیشه pull کن (برای latest)
    # pull_policy: missing       # پیش‌فرض: اگه نبود pull کن
    # pull_policy: never         # هرگز pull نکن (از image محلی)
```

</div>


### ۱۹.۵. `logging` — کنترل حجم لاگ

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```yaml
services:
  api:
    logging:
      driver: json-file
      options:
        max-size: "10m"
        max-file: "3"
```

</div>


**مهم برای production:** جلوگیری از پر شدن دیسک با لاگ.

### ۱۹.۶. `resources` — محدودیت منابع (Compose v2.24+)

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```yaml
services:
  api:
    deploy:
      resources:
        limits:
          cpus: "1.0"
          memory: 512M
        reservations:
          memory: 256M
```

</div>


**نکته:** `deploy` تو compose معمولی هم کار می‌کنه (نه فقط swarm).

### ۱۹.۷. `depends_on` تو حالت scale
اگه `--scale api=3` بزنی، `depends_on` روی **همه ۳ تا** اعمال می‌شه. یعنی هر ۳ تا منتظر `db` سالم می‌مونن.

---

## ۲۰. چک‌لیست امنیتی برای setup فعلیت

**قبل از هر `docker compose up`:**

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```bash
# ۱. فایلای compose تو پوشه رو ببین
ls compose*.yml docker-compose*.yml 2>/dev/null

# ۲. چک کن پورت‌ها با چیزی collide نکنن
ss -tlnp | grep -E ':(8888|8090|5432|6379)'
# یا:
docker ps --format "{{.Ports}}" | grep -E '8888|8090'

# ۳. اسم project رو چک کن
echo "Project: learn"
docker ps --filter "name=learn-" -a

# ۴. قبل از up، config رو بررسی کن
docker compose -p learn config

# ۵. همیشه با -p کار کن
docker compose -p learn up -d
```

</div>


**قانون‌های طلایی برای تو:**
1. ✅ همیشه `-p learn` (یا `COMPOSE_PROJECT_NAME=learn` تو `.env`)
2. ✅ اسم پورت‌ها رو با setup اصلیت collide نکن (مثلاً 8888, 8889, 8090)
3. ✅ `docker compose down` بدون `-v` → volume ها سالم می‌مونن
4. ❌ هرگز `docker compose down -v` روی setup اصلیت
5. ❌ `container_name` ثابت نذار (جلوگیری از collide)
6. ✅ قبل از `down`، `ps` بزن که مطمئن شی setup اصلیت نیست

**اسکریپت امن برای پاک‌سازی:**

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```bash
# همه چیزهای learn رو ببر
docker compose -p learn down
docker compose -p learn4 down
docker rm -f $(docker ps -aq --filter "name=learn") 2>/dev/null
docker network rm $(docker network ls -q --filter "name=learn") 2>/dev/null
docker volume rm $(docker volume ls -q --filter "name=learn") 2>/dev/null
```

</div>


---

## ۲۱. برای فهم بهتر setup فعلیت

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```bash
# ─── تو پوشه setup اصلیت ───
cd /path/to/your/compose/files

# نمایش compose نهایی (بعد از merge)
docker compose config

# لیست سرویس‌ها
docker compose config --services

# لیست network ها و volume ها
docker compose config --volumes
docker compose config --networks

# کدوم project name?
docker compose ls
# NAME     STATUS     CONFIG FILES
# myapp    running    /path/compose.yml

# وضعیت
docker compose ps
```

</div>


**این تحلیل بهت نشون می‌ده setup فعلیت زیر کاپوت چطور کار می‌کنه.** اینطوری وقتی چیزی خراب شد، می‌دونی کجا رو نگاه کنی.

---

## ۲۲. سوالات خودآزمایی

1. `docker compose down` با `down -v` چه فرقی داره؟ کدوم برای setup اصلیت امنه؟
2. چرا `depends_on` کافی نیست برای دیتابیس؟
3. تفاوت `environment` و `env_file` چیه؟
4. `docker compose -p learn up` چه چیزهایی می‌سازه؟ (اسم دقیق)
5. `compose.override.yml` چطور کار می‌کنه؟
6. `profiles` به چه دردی می‌خوره؟
7. اگه یه سرویس `ports` publish کرده باشه، می‌تونی scale کنی؟ چرا؟
8. تفاوت `expose` و `ports` تو compose چیه؟
9. `condition: service_completed_successfully` کِی استفاده می‌شه؟
10. چرا `container_name` برای setup فعلیت خطرناکه؟

<details>
<summary>جواب‌ها</summary>

1. `down` فقط container + network رو می‌بره (volume سالم). `down -v` volume رو هم می‌بره (دیتا نابود). برای setup اصلیت **هرگز** `-v` نزن.
2. `depends_on` فقط ترتیب شروع رو کنترل می‌کنه، نه آمادگی. Postgres چند ثانیه طول می‌کشه تا وصل بشه. `healthcheck` + `condition: service_healthy` لازمه.
3. `environment` مستقیم تو YAML. `env_file` از فایل `.env` می‌خونه. `env_file` بهتره برای secrets (تو git نمی‌ره).
4. Network به اسم `learn_default`، container هایی به اسم `learn-<service>-1`، volume هایی به اسم `learn_<volume>`.
5. خودکار با `up` (بدون `-f`) merge می‌شه. برای dev استفاده می‌شه، تو gitignore هست.
6. سرویس‌های اختیاری (adminer, debug tools) که فقط با `--profile` فعال می‌شن. در غیر این صورت نادیده گرفته می‌شن.
7. نه، چون هر instance پورت هاست رو می‌خواد و collide می‌شه. باید `expose` کنی و از reverse proxy استفاده کنی.
8. `ports` پورت رو روی هاست باز می‌کنه (از بیرون accessible). `expose` فقط داخل network (برای scale و امنیت).
9. برای job های یک‌باره مثل migration — api صبر می‌کنه تا migration با موفقیت تموم بشه.
10. اسم ثابت cause collide می‌شه اگه setup اصلیت هم بخواد از همون اسم استفاده کنه. همچنین مانع scale می‌شه. بذار Compose خودش اسم بده.
</details>

---

## ۲۳. چک‌لیست پایان مرحله ۴

قبل از رفتن به مرحله ۵ (امنیت و بهینه‌سازی)، مطمئن شو:

- [ ] یه stack کامل (app + db + cache) رو با Compose راه انداختم
- [ ] `depends_on` + `healthcheck` رو عملی دیدم (بدونش fail، با اون کار کرد)
- [ ] می‌فهمم چرا `-p learn` حیاتیه برای setup فعلیت
- [ ] تفاوت `down` و `down -v` رو می‌دونم
- [ ] override files رو تست کردم (dev vs prod)
- [ ] profiles رو تست کردم (سرویس اختیاری)
- [ ] `docker compose config` رو روی setup فعلیت زدم و ساختارش رو فهمیدم

---

**قدم بعدی:** مرحله ۵ (امنیت، بهینه‌سازی، multi-stage پیشرفته، registry، CI/CD). ولی اول، اگه خواستی، می‌تونیم مرحله ۴ رو با یه پروژه واقعی (همون FastAPI/Node که پیشنهاد دادم) عملی‌تر کنیم.

**آماده مرحله ۵ باشیم؟** یا می‌خوای الان مرحله ۴ رو روی یه مثال واقعی با هم پیاده کنیم؟


</div>




<!-- SOURCE: ch2.md -->
# File: ch2.md

<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">

عالیه 👌 این ۵ تمرین رو از خیلی ساده به کمی چالشی می‌چینیم. **فعلاً جواب‌ها رو نگاه نکن؛ خودت حدس بزن هر Regex چه خروجی‌ای می‌ده.** بعد جواب هر سؤال رو باز کن و با توضیح مقایسه کن.

# **تمرین ۱ — Greedy**

فرض کن این کد رو داریم:

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```
String text = "12345";

Pattern pattern = Pattern.compile("\\d+");
Matcher matcher = pattern.matcher(text);

while (matcher.find()) {
    System.out.println(matcher.group());
}
```

</div>


❓ سؤال

به نظرت خروجی کد چی می‌شه؟

آیا ۵ عدد جدا چاپ می‌شوند یا کل عدد یک‌جا؟

مشاهده جواب

خروجی:

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```
12345
```

</div>


**چرا؟** `\d+` یعنی یک یا چند رقم. چون `+` به‌صورت Greedy است، همه رقم‌های متوالی را در یک Match می‌گیرد.

# **تمرین ۲ — Lazy**

این بار فقط یک `?` اضافه شده:

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```
String text = "12345";

Pattern pattern = Pattern.compile("\\d+?");
Matcher matcher = pattern.matcher(text);

while (matcher.find()) {
    System.out.println(matcher.group());
}
```

</div>


❓ سؤال

این بار چند Match داریم؟

آیا خروجی مثل تمرین قبلی است؟

مشاهده جواب

خروجی:

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```
1
2
3
4
5
```

</div>


**چرا؟** `\d+?` ابتدا کمترین تعداد رقم را می‌گیرد که یک رقم است. بعد `find()` جستجو را از ادامه متن انجام می‌دهد.

# **تمرین ۳ — تفاوت Greedy و Lazy در تگ**

متن:

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```
String text = "<a><b><c>";
```

</div>


Regex:

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```
Pattern pattern = Pattern.compile("<.+>");
Matcher matcher = pattern.matcher(text);

while (matcher.find()) {
    System.out.println(matcher.group());
}
```

</div>


❓ سؤال

چند بار چاپ می‌شود؟

یک بار کل متن یا سه بار هر تگ جداگانه؟

مشاهده جواب

خروجی:

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```
<a><b><c>
```

</div>


**چرا؟** چون `.+` حریص است. اولین `<` را پیدا می‌کند، تا آخر متن پیش می‌رود و با آخرین `>` تطبیق را کامل می‌کند.

# **تمرین ۴ — Lazy در تگ‌ها**

همان متن قبلی:

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```
String text = "<a><b><c>";
```

</div>


اما این بار Regex:

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```
Pattern pattern = Pattern.compile("<.+?>");
Matcher matcher = pattern.matcher(text);

while (matcher.find()) {
    System.out.println(matcher.group());
}
```

</div>


❓ سؤال

چرا این Regex با تمرین ۳ متفاوت است، در حالی که فقط یک `?` اضافه شده؟

مشاهده جواب

خروجی:

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```
<a>
<b>
<c>
```

</div>


**چرا؟** `.+?` از اولین `<` شروع می‌کند و به محض اینکه اولین `>` را پیدا کرد، Match را تمام می‌کند. سپس `find()` از ادامه متن جستجو می‌کند.

# **تمرین ۵ — Possessive (مهم‌ترین تمرین)**

اینجا یک مثال می‌بینیم که تفاوت واقعی Possessive با Greedy مشخص می‌شود.

متن:

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```
String text = "123abc";
```

</div>


### **Regex اول — Greedy**

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```
Pattern pattern = Pattern.compile("\\d+\\d");
Matcher matcher = pattern.matcher(text);

System.out.println(matcher.find());
```

</div>


**خروجی:**

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```
true
```

</div>


**چرا؟**

`\d+` ابتدا تمام ارقام `123` را می‌گیرد. اما Regex هنوز یک `\d` دیگر لازم دارد.

Greedy می‌تواند Backtracking کند؛ یعنی یک رقم را پس بدهد تا `\d` آخر آن را مصرف کند.

در نتیجه Match پیدا می‌شود: `12` به‌عنوان `\d+` و `3` به‌عنوان `\d` آخر.

### **Regex دوم — Possessive**

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```
Pattern pattern = Pattern.compile("\\d++\\d");
Matcher matcher = pattern.matcher(text);

System.out.println(matcher.find());
```

</div>


**خروجی:**

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```
false
```

</div>


**چرا؟**

`\d++` تمام ارقام `123` را می‌گیرد و دیگر هیچ رقمی را پس نمی‌دهد.

`\d` آخر نیاز به یک رقم دارد؛ ولی تمام رقم‌ها قبلاً گرفته شده‌اند و Possessive اجازه عقب‌گرد نمی‌دهد.

پس Match شکست می‌خورد.

# **خلاصه تصویری در ذهن**

## **فرض کن ۵ تا توپ داری**

### **1**

### **2**

### **3**

### **4**

### **5**

**Greedy 🟠**

همه ۵ توپ را برمی‌دارد؛ اگر لازم شود، یکی‌یکی پس می‌دهد.

**Lazy 🟢**

ابتدا یک توپ برمی‌دارد؛ اگر لازم باشد توپ‌های بیشتری برمی‌دارد.

**Possessive 🔴**

همه ۵ توپ را برمی‌دارد؛ ولی تحت هیچ شرایطی پس نمی‌دهد.

| Regex  | معنی                                    |
| ------ | --------------------------------------- |
| `\d+`  | Greedy؛ یک یا چند رقم                   |
| `\d+?` | Lazy؛ کمترین تعداد لازم، سپس گسترش      |
| `\d++` | Possessive؛ یک یا چند رقم، بدون عقب‌گرد |
| `.*`   | هر کاراکتری، Greedy                     |
| `.*?`  | هر کاراکتری، Lazy                       |
| `.*+`  | هر کاراکتری، Possessive                 |

**یک نکته مهم برای مصاحبه:** Greedy و Lazy هر دو می‌توانند Backtracking انجام دهند؛ تفاوتشان در نحوه شروع Match است. Possessive Backtracking را در آن بخش از الگو ممنوع می‌کند.

اگر بخواهی، مرحله بعد می‌توانیم همین مبحث را با ۵ سؤال تستی شبیه مصاحبه Java جلو ببریم که خودت خروجی Regexها را حدس بزنی و من جواب‌ها را بررسی کنم.
</div>



<!-- SOURCE: week1.md -->
# File: week1.md

<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">
باشه، پس از **هفته‌ی اول** شروع می‌کنیم و روز‌به‌روز با کد نمونه پیش می‌رویم. هدف این هفته این است که با `var`, Text Blocks, Records, Pattern Matching for `instanceof`, و Switch Expressions راحت شوی و همزمان ۷ مسئله‌ی آرایه/Two Pointers را با سبک Java 17 حل کنی.

---

## 📅 هفته ۱ — روز ۱: `var`

### 🎓 بخش Java 17 (۳۰ دقیقه)

`var` یک **Local Variable Type Inference** است، نه داینامیک تایپینگ. کامپایلر نوع را از سمت راست استنتاج می‌کند و بعد از آن نوع ثابت است.

**کجا مجاز است:**
</div>

```java
var name = "Ali";                  // String
var list = new ArrayList<String>(); // ArrayList<String>
var map = new HashMap<String, Integer>();
for (var entry : map.entrySet()) { ... }
try (var reader = new BufferedReader(...)) { ... }
```

**کجا مجاز نیست:**
```java
var x;                    // ❌ بدون مقدار اولیه
var y = null;             // ❌ نوع قابل استنتاج نیست
class Foo { var field; }  // ❌ فیلد کلاس
void m(var p) {}          // ❌ پارامتر متد
var f() { return 1; }     // ❌ نوع بازگشتی
```
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">
**نکته‌ی مصاحبه:** `var` نوع را در **compile-time** استنتاج می‌کند؛ پس `var x = 1; x = "hi";` کامپایل نمی‌شود. این سؤال خیلی رایج است.

### 🧩 بخش DSA (۶۰ دقیقه) — Two Sum

**مسئله:** آرایه‌ی `nums` و عدد `target` داده شده. دو ایندکسی را برگردان که جمعشان `target` شود.

**راه‌حل با Java 17:**

</div>


```java
public int[] twoSum(int[] nums, int target) {
    var seen = new HashMap<Integer, Integer>(); // value -> index
    for (var i = 0; i < nums.length; i++) {
        var complement = target - nums[i];
        if (seen.containsKey(complement)) {
            return new int[]{seen.get(complement), i};
        }
        seen.put(nums[i], i);
    }
    return new int[]{};
}
```
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">
- **پیچیدگی:** زمان O(n)، فضا O(n)
- **الگو:** Hash Map
- **Edge cases:** آرایه‌ی خالی، دو عنصر یکسان، جواب در انتها

### ✍️ تمرین
همین مسئله را یک بار دیگر بنویس ولی این بار از `record` استفاده نکن (چون هنوز یاد نگرفته‌ای) و با `var` تمیز نگهش دار.

---

## 📅 روز ۲: Text Blocks

### 🎓 بخش Java 17 (۳۰ دقیقه)

Text Block یک رشته‌ی چندخطی است که با `"""` شروع و تمام می‌شود.

</div>

```java
var json = """
        {
          "name": "Ali",
          "age": 30
        }
        """;
```
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">
**قوانین:**
- باید بعد از `"""` یک خط جدید باشد.
- فاصله‌ی ابتدایی مشترک حذف می‌شود (incidental whitespace).
- `\` در انتهای خط یعنی بدون newline.
- `\s` یعنی فاصله‌ی اجباری.

**مثال کاربردی:**
</div>

```java
var query = """
        SELECT id, name
        FROM users
        WHERE age > %d
        ORDER BY name
        """.formatted(18);
```
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">
**نکته‌ی مصاحبه:** Text Block یک `String` معمولی است، فقط سینتکس راحت‌تر است. `String` در جاوا immutable است، پس هر Text Block هم immutable است.

### 🧩 بخش DSA (۶۰ دقیقه) — Best Time to Buy and Sell Stock

**مسئله:** آرایه‌ی قیمت‌ها. یک بار بخر و یک بار بعدش بفروش. بیشترین سود را برگردان.

**راه‌حل:**
</div>

```java
public int maxProfit(int[] prices) {
    var minPrice = Integer.MAX_VALUE;
    var maxProfit = 0;
    for (var price : prices) {
        if (price < minPrice) {
            minPrice = price;
        } else {
            maxProfit = Math.max(maxProfit, price - minPrice);
        }
    }
    return maxProfit;
}
```
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">
- **پیچیدگی:** زمان O(n)، فضا O(1)
- **الگو:** Greedy / One Pass
- **Edge cases:** آرایه‌ی نزولی (سود صفر)، یک عنصر

### ✍️ تمرین
یک `record Report(String title, String body)` بساز که `body` را با Text Block پرش کنی و در `toString` چاپ کنی.

---

## 📅 روز ۳: Records (بخش ۱)

### 🎓 بخش Java 17 (۳۰ دقیقه)

`record` یک کلاس **immutable** است که فقط داده نگه می‌دارد. کامپایلر خودش `constructor`, `accessor`, `equals`, `hashCode`, `toString` را می‌سازد.
</div>

```java
public record Point(int x, int y) {}

var p = new Point(3, 5);
System.out.println(p.x());       // 3
System.out.println(p);           // Point[x=3, y=5]
System.out.println(p.equals(new Point(3, 5))); // true
```

**محدودیت‌ها:**
- نمی‌تواند `extends` کند (ولی `implements` می‌تواند).
- فیلدها `final` هستند.
- نمی‌توانی فیلد اضافه اضافه کنی.
- می‌توانی متد اضافه کنی، متد static، و constructor سفارشی.

**نکته‌ی مصاحبه:** Records به‌طور خودکار `final` هستند و برای DTO و value object عالی‌اند. در مصاحبه‌ها وقتی state کوچک و immutable داری، از `record` استفاده کن.

### 🧩 بخش DSA (۶۰ دقیقه) — Contains Duplicate

**مسئله:** آیا آرایه عنصر تکراری دارد؟
</div>

```java
public boolean containsDuplicate(int[] nums) {
    var seen = new HashSet<Integer>();
    for (var n : nums) {
        if (!seen.add(n)) return true;
    }
    return false;
}
```
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">
- **پیچیدگی:** زمان O(n)، فضا O(n)
- **الگو:** HashSet
- **نکته:** `Set.add` اگر عنصر تکراری باشد `false` برمی‌گرداند — یک trick تمیز.

### ✍️ تمرین
یک `record Range(int start, int end)` بساز با یک متد `boolean contains(int n)`.

---

## 📅 روز ۴: Records (بخش ۲) — Constructor فشرده

### 🎓 بخش Java 17 (۳۰ دقیقه)

**Compact Constructor** برای اعتبارسنجی یا نرمال‌سازی:

</div>

```java
public record Range(int start, int end) {
    public Range {
        if (start > end) {
            throw new IllegalArgumentException("start > end");
        }
    }
}
```

**Static Factory:**
```java
public record Point(int x, int y) {
    public static Point origin() { return new Point(0, 0); }
    public Point translate(int dx, int dy) { return new Point(x + dx, y + dy); }
}
```
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">
**نکته‌ی مصاحبه:** در `record` نمی‌توانی فیلد mutable اضافه کنی. اگر state متغیر می‌خواهی، `record` انتخاب درستی نیست.

### 🧩 بخش DSA (۶۰ دقیقه) — Product of Array Except Self

**مسئله:** آرایه‌ی `nums`. آرایه‌ای برگردان که هر عنصرش حاصل‌ضرب همه‌ی عناصر به‌جز خودش باشد. بدون تقسیم و در O(n).
</div>

```java
public int[] productExceptSelf(int[] nums) {
    var n = nums.length;
    var result = new int[n];
    result[0] = 1;
    for (var i = 1; i < n; i++) {
        result[i] = result[i - 1] * nums[i - 1];
    }
    var right = 1;
    for (var i = n - 1; i >= 0; i--) {
        result[i] *= right;
        right *= nums[i];
    }
    return result;
}
```
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">
- **پیچیدگی:** زمان O(n)، فضا O(1) (به‌جز آرایه‌ی خروجی)
- **الگو:** Prefix/Suffix Product

### ✍️ تمرین
یک `record Matrix(int[][] data)` بساز و در constructor فشرده چک کن که همه‌ی سطرها طول یکسان داشته باشند.

---

## 📅 روز ۵: Pattern Matching for `instanceof`

### 🎓 بخش Java 17 (۳۰ دقیقه)

**قبل (Java 8):**
</div>

```java
if (obj instanceof String) {
    String s = (String) obj;
    System.out.println(s.length());
}
```

**حالا (Java 16+):**
```java
if (obj instanceof String s) {
    System.out.println(s.length());
}
```

**با شرط:**
```java
if (obj instanceof String s && s.length() > 5) {
    System.out.println(s.toUpperCase());
}
```
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">
**نکته‌ی مصاحبه:** این pattern flow-sensitive است؛ یعنی `s` فقط داخل بلاکی که شرط true است قابل استفاده است. این را با `&&` و `||` می‌توانی کنترل کنی.

### 🧩 بخش DSA (۶۰ دقیقه) — Maximum Subarray (Kadane)

**مسئله:** بزرگ‌ترین مجموع زیرآرایه‌ی پیوسته را برگردان.
</div>

```java
public int maxSubArray(int[] nums) {
    var maxEndingHere = nums[0];
    var maxSoFar = nums[0];
    for (var i = 1; i < nums.length; i++) {
        maxEndingHere = Math.max(nums[i], maxEndingHere + nums[i]);
        maxSoFar = Math.max(maxSoFar, maxEndingHere);
    }
    return maxSoFar;
}
```
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">
- **پیچیدگی:** زمان O(n)، فضا O(1)
- **الگو:** Kadane / DP ساده

### ✍️ تمرین
یک متد بنویس که `Object` می‌گیرد و اگر `Integer` بود مربعش را، اگر `String` بود طولش را، وگرنه `-1` برگرداند. با `instanceof` pattern بنویس.

---

## 📅 روز ۶: Switch Expressions

### 🎓 بخش Java 17 (۳۰ دقیقه)
</div>

**قبل:**
```java
String dayType;
switch (day) {
    case MONDAY:
    case TUESDAY:
        dayType = "Weekday";
        break;
    default:
        dayType = "Other";
}
```

**حالا:**
```java
var dayType = switch (day) {
    case MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY -> "Weekday";
    case SATURDAY, SUNDAY -> "Weekend";
};
```

**با بلاک و `yield`:**
```java
var result = switch (x) {
    case 1 -> "one";
    case 2 -> {
        var doubled = x * 2;
        yield "two (" + doubled + ")";
    }
    default -> "many";
};
```
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">
**نکته‌ی مصاحبه:** `switch` expression باید **exhaustive** باشد (یا `default` داشته باشد یا همه‌ی حالت‌ها پوشش داده شوند). این با enum و sealed خیلی خوب کار می‌کند.

### 🧩 بخش DSA (۶۰ دقیقه) — Move Zeroes

**مسئله:** همه‌ی صفرها را به انتهای آرایه منتقل کن، ترتیب بقیه حفظ شود، in-place.

</div>

```java
public void moveZeroes(int[] nums) {
    var insertPos = 0;
    for (var n : nums) {
        if (n != 0) {
            nums[insertPos++] = n;
        }
    }
    while (insertPos < nums.length) {
        nums[insertPos++] = 0;
    }
}
```
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">
- **پیچیدگی:** زمان O(n)، فضا O(1)
- **الگو:** Two Pointers

### ✍️ تمرین
یک enum `Season` بساز و با `switch` expression تعداد روزهای هر فصل را برگردان.

---

## 📅 روز ۷: مرور + بازنویسی

### 🎯 کارهای امروز

1. **مرور Java 17**: همه‌ی کدهای هفته را باز کن و یک بار دیگر بخوان. کدام‌ها را راحت یاد گرفتی و کدام‌ها سخت؟
2. **بازنویسی DSA**: سه مسئله‌ی زیر را دوباره حل کن، این بار بدون نگاه کردن به راه‌حل:
    - Maximum Subarray
    - Product of Array Except Self
    - Move Zeroes
3. **Mock Interview کوچک**: با تایمر ۳۰ دقیقه، یک مسئله‌ی جدید (مثلاً Two Sum II) را حل کن و صدایت را ضبط کن که چطور فکر می‌کنی.
4. **پروژه‌ی هفته**: یک `record Shape` بساز که `sealed interface` باشد و با `switch` expression محیط و مساحت را حساب کن.

### 📝 چک‌لیست پایان هفته

- [ ] `var` را در ۵ جای مختلف استفاده کرده‌ام
- [ ] Text Block را در یک برنامه‌ی واقعی استفاده کرده‌ام
- [ ] حداقل ۳ `record` ساخته‌ام (با compact constructor)
- [ ] `instanceof` pattern را در کد استفاده کرده‌ام
- [ ] `switch` expression نوشته‌ام
- [ ] ۷ مسئله‌ی DSA را حل کرده‌ام
- [ ] ۱ Mock Interview انجام داده‌ام

---

## 💡 نکات مهم برای ادامه

- **هر روز کد بزن**، حتی اگر ۱۵ دقیقه. بدون کد، یادگیری Java 17 فراموش می‌شود.
- **قبل از دیدن راه‌حل، ۲۰ دقیقه تلاش کن.** این سخت‌ترین ولی مفیدترین بخش است.
- **یادداشت بردار**: برای هر مسئله، الگو + پیچیدگی + edge caseها را در یک فایل Markdown بنویس.
- **هفته‌ی بعد** روی `Sealed`, `Switch Pattern Matching`, و `HashMap/Sliding Window` تمرکز داریم.

---

اگر خواستی، می‌توانم:
- **هفته‌ی دوم** را هم با همین سطح جزئیات بنویسم، یا
- یک **قالب Markdown** برای یادداشت‌هایت بسازم که هر روز پر کنی، یا
- یک **پروژه‌ی کوچک هفتگی** طراحی کنم که همه‌ی مفاهیم هفته را ترکیب کند.

کدام را اول شروع کنیم؟

</div>



<!-- SOURCE: 2.md -->
# File: 2.md

<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">


حتماً. این مجموعه را به شکل **سؤالات احتمالی مصاحبه Java Developer (Mid-level تا Senior) درباره RabbitMQ، Kafka و Spring Boot همراه جواب** آماده کردم.

جواب‌ها را طوری نوشته‌ام که بتوانی در مصاحبه شفاهی بیان کنی؛ نه فقط تعریف کتابی، بلکه همراه با نکات فنی و سناریوهای واقعی.

# **بخش ۱ — مفاهیم پایه Messaging**

## **سؤال ۱: Message Broker چیست و چرا از آن استفاده می‌کنیم؟**

**جواب:**

Message Broker یک واسط بین سرویس‌هاست که ارتباط Asynchronous را برقرار می‌کند. Producer پیام را ارسال می‌کند و Consumer در زمان مناسب آن را دریافت و پردازش می‌کند.

مزایای اصلی:

- Loose Coupling بین سرویس‌ها
- کاهش وابستگی مستقیم
- پردازش Async
- تحمل بهتر خطا و در دسترس نبودن موقت سرویس‌ها
- مدیریت حجم بالای درخواست‌ها با Buffering
- امکان Scaling مستقل Consumerها

مثلاً Order Service پس از ثبت سفارش، یک Event منتشر می‌کند و Notification Service بدون اینکه Order Service منتظر آن بماند، ایمیل ارسال می‌کند.

## **سؤال ۲: تفاوت Synchronous و Asynchronous Communication چیست؟**

**Synchronous:**

سرویس درخواست را ارسال می‌کند و منتظر پاسخ می‌ماند.

مثال:

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```
Order Service
     |
     | REST
     v
Payment Service
     |
     | Response
     v
Order Service
```

</div>


**Asynchronous:**

سرویس پیام را ارسال می‌کند و لازم نیست منتظر پردازش Consumer باشد.

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```
Order Service
     |
     v
Message Broker
     |
     v
Payment Consumer
```

</div>


در ارتباط Async، سرویس‌ها وابستگی زمانی کمتری به یکدیگر دارند؛ ولی مدیریت Retry، Duplicate Message و Eventual Consistency مهم‌تر می‌شود.

## **سؤال ۳: تفاوت Queue و Topic چیست؟**

**Queue:**

پیام‌ها برای مصرف‌کننده یا مصرف‌کنندگان نگهداری می‌شوند. در مدل Work Queue، معمولاً هر پیام توسط یکی از Consumerهای همکار پردازش می‌شود.

**Topic:**

برای انتشار Eventها در یک دسته مشخص استفاده می‌شود. چند Consumer مستقل می‌توانند همان Eventها را دریافت کنند.

مثلاً:

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```
order.created
order.cancelled
payment.completed
```

</div>


البته Topic در RabbitMQ و Kafka دقیقاً رفتار یکسانی ندارد؛ در RabbitMQ Topic معمولاً نوع Exchange و Routing Pattern است، ولی در Kafka Topic یک Log از Partitionها است.

## **سؤال ۴: Message Delivery Guarantee چیست؟**

مشخص می‌کند پیام با چه تضمینی به مقصد برسد یا پردازش شود.

سه مدل مهم:

| مدل           | مفهوم                                                           |
| ------------- | --------------------------------------------------------------- |
| At-most-once  | پیام حداکثر یک بار پردازش می‌شود؛ احتمال از دست رفتن وجود دارد  |
| At-least-once | پیام ممکن است دوباره تحویل داده شود؛ احتمال Duplicate وجود دارد |
| Exactly-once  | اثر پردازش دقیقاً یک بار اعمال شود، با وجود Retry و Failure     |

در عمل، در بسیاری از سیستم‌ها At-least-once استفاده می‌شود و با Idempotency از اثر تکراری پیام جلوگیری می‌کنیم.

# **بخش ۲ — سؤالات RabbitMQ**

[RabbitMQ Cluster using AWS Peer Discovery | by Sugandh Pasricha | Medium](https://images.openai.com/static-rsc-4/lo1gAWePJK5z2o3HkAEwYeUjaAhXPXOejK1giAuJqepCAKqRz4TB5BYQTzpb4-XH-CqeyMtau1-MUfHv3jPJLGY3a4plM2XloYkiKRjIRgGWN1B791fRIqCLOtVaOzh4JPw8ZK4bMFEOwgj-zOGh-FdAYkPFSfVHURFxXaGdti0?purpose=inline)

[Kafka vs. RabbitMQ: How to Choose the Right Message Queue for Your Microservices](https://images.openai.com/static-rsc-4/qj-H7NaSMpPMDp4M59crVXgLZfB8Zv1FkuIshCE-mQH0O3e3zIh2ToMjitdJqGgA8AQ2s-qqZaL_otn4Z9IFZY5M5TGkyxGCSzPqd7Q8Q_frR9d1jx_mhLfi5sRbB-rBfbDh-Gf9NoCk4yjAGIgCt5CwTFORRLpys2laEfg__wA?purpose=inline)

[How RabbitMQ Works: A Guide to Asynchronous Messaging and Queueing | by Double Pointer | Tech Wrench | Medium](https://images.openai.com/static-rsc-4/8XEcdy09aD70YNeXKm9SR3vJ4OBFooYzuholHfxidlL7qPBGkCl0hbDEC4Y9bF9kh9mbG1W_eMq80pUknQ4CDrxeILVA-yhqe6Hz2pkowD9cprDeBy8erHEOzERAgBDuk39Hcr88qlMSRJqLKS5N6dXe00lc-2xBJEUjHNyEL1A?purpose=inline)

7

## **سؤال ۵: RabbitMQ چگونه کار می‌کند؟**

RabbitMQ یک Message Broker است که پیام را از Producer دریافت می‌کند، به Exchange می‌فرستد و Exchange با استفاده از Binding و Routing Key پیام را به Queue مناسب هدایت می‌کند.

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```
Producer
   |
   v
 Exchange
   |
 Routing Key
   |
 Binding
   |
   v
  Queue
   |
   v
 Consumer
```

</div>


Consumer پیام را دریافت و پس از پردازش موفق، ACK می‌دهد تا Broker آن را حذف کند.

## **سؤال ۶: Exchange چیست و چه انواعی دارد؟**

Exchange پیام را از Producer دریافت می‌کند و براساس Routing Rules آن را به Queueها هدایت می‌کند.

چهار نوع اصلی:

### **1. Direct Exchange**

Routing Key باید با Binding Key برابر باشد.

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```
Routing Key: order.created
Binding Key: order.created
```

</div>


پیام به Queue متصل به این Binding ارسال می‌شود.

### **2. Fanout Exchange**

پیام را به تمام Queueهای متصل ارسال می‌کند و Routing Key را نادیده می‌گیرد.

مناسب Broadcast.

### **3. Topic Exchange**

بر اساس Patternهای Routing Key مسیریابی می‌کند.

مثلاً:

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```
order.eu.created
order.us.created
order.eu.cancelled
```

</div>


الگوها:

- `*` → دقیقاً یک کلمه
- `#` → صفر یا چند کلمه

مثال:

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```
order.*.created
```

</div>


این موارد را Match می‌کند:

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```
order.eu.created
order.us.created
```

</div>


اما این را Match نمی‌کند:

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```
order.eu.payment.created
```

</div>


### **4. Headers Exchange**

Routing براساس Headerهای پیام انجام می‌شود، نه Routing Key.

## **سؤال ۷: تفاوت Direct Exchange و Topic Exchange چیست؟**

**Direct Exchange:**

مقایسه دقیق Routing Key با Binding Key.

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```
payment.success
```

</div>


**Topic Exchange:**

از Pattern Matching استفاده می‌کند.

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```
payment.*
payment.#
```

</div>


Direct زمانی مناسب است که Routing دقیق داشته باشیم؛ Topic زمانی که Routing انعطاف‌پذیر و مبتنی بر الگو می‌خواهیم.

## **سؤال ۸: Binding چیست؟**

Binding ارتباط بین Exchange و Queue است و تعیین می‌کند چه پیام‌هایی از Exchange به Queue منتقل شوند.

مثلاً:

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```
Exchange: order.exchange
Queue: order.created.queue
Binding Key: order.created
```

</div>


اگر پیام با Routing Key برابر `order.created` منتشر شود، به Queue مربوطه هدایت می‌شود.

## **سؤال ۹: ACK، NACK و Reject چیست؟**

### **ACK**

Consumer اعلام می‌کند پیام با موفقیت پردازش شده است.

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```
Message → Processing → ACK → Message Removed
```

</div>


### **NACK**

Consumer اعلام می‌کند پردازش پیام موفق نبوده و امکان Requeue یا Dead-letter کردن آن وجود دارد.

### **Reject**

برای رد یک پیام استفاده می‌شود و می‌توان مشخص کرد که دوباره به Queue برگردد یا خیر.

در Spring:

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```
channel.basicAck(deliveryTag, false);
```

</div>


برای NACK:

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```
channel.basicNack(deliveryTag, false, true);
```

</div>


پارامتر آخر `true` یعنی پیام دوباره به Queue برگردد.

نکته: Requeue دائمی پیام‌های خراب می‌تواند باعث Infinite Retry و مصرف منابع شود.

## **سؤال ۱۰: تفاوت Durable Queue و Persistent Message چیست؟**

این سؤال بسیار مهم است.

**Durable Queue** یعنی تعریف Queue بعد از Restart شدن Broker باقی بماند.

**Persistent Message** یعنی پیام برای ذخیره‌سازی پایدار علامت‌گذاری شده باشد.

برای اینکه پیام در مقابل Restart شدن RabbitMQ دوام بیشتری داشته باشد، معمولاً هر دو لازم‌اند؛ البته تضمین نهایی به نوع Queue، وضعیت Replication، تنظیمات و نحوه تأیید Publish نیز وابسته است.

## **سؤال ۱۱: Dead Letter Queue (DLQ) چیست؟**

DLQ محل نگهداری پیام‌هایی است که نمی‌توانند به شکل عادی پردازش شوند.

پیام ممکن است Dead Letter شود اگر:

- Reject یا NACK شود و Requeue غیرفعال باشد.
- TTL پیام تمام شود.
- Queue به دلیل محدودیت Length پیام را Dead Letter کند.

معمولاً برای پیام‌های خراب یا پیام‌هایی که Retryهایشان تمام شده است استفاده می‌شود.

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```
Main Queue
    |
    v
 Consumer
    |
    +---- Success → ACK
    |
    +---- Failure → Retry
                      |
                      +---- Retry exhausted
                                  |
                                  v
                                DLQ
```

</div>


بهتر است پیام‌های DLQ بررسی و Alert شوند؛ صرفاً انتقال به DLQ به معنی حل مشکل نیست.

## **سؤال ۱۲: Retry در RabbitMQ چگونه پیاده‌سازی می‌شود؟**

سه روش رایج:

1. Retry مستقیم با Requeue
2. Retry Queue به همراه TTL
3. Dead Letter Exchange

معماری پیشنهادی:

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```
Main Queue
    |
 Consumer
    |
 Failure
    |
    v
Retry Queue (TTL)
    |
    v
Dead Letter Exchange
    |
    v
Main Queue
```

</div>


برای جلوگیری از Retry بی‌نهایت، باید تعداد Retry مشخص باشد.

مثلاً:

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```
Retry 1 → 5 seconds
Retry 2 → 30 seconds
Retry 3 → 2 minutes
After 3 retries → DLQ
```

</div>


Retry فوری همیشه مناسب نیست؛ چون اگر سرویس مقصد Down باشد، باعث فشار بیشتر می‌شود. Retry با Backoff معمولاً بهتر است.

## **سؤال ۱۳: Prefetch در RabbitMQ چیست؟**

Prefetch تعیین می‌کند Consumer تا چه تعداد پیام را می‌تواند دریافت کند که هنوز ACK نشده‌اند.

مثلاً:

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```
Prefetch = 10
```

</div>


یعنی Broker حداکثر ۱۰ پیام Unacknowledged را برای آن Consumer ارسال می‌کند.

مزایا:

- کنترل مصرف حافظه
- جلوگیری از دریافت حجم زیاد پیام
- جلوگیری از Overload شدن Consumer
- کنترل Load Balancing بین Consumerها

اگر Prefetch خیلی بالا باشد، یک Consumer ممکن است پیام‌های زیادی را دریافت کند و Consumerهای دیگر بیکار بمانند.

## **سؤال ۱۴: اگر Consumer در RabbitMQ Crash کند چه اتفاقی می‌افتد؟**

اگر پیام هنوز ACK نشده باشد، RabbitMQ معمولاً آن پیام را دوباره به حالت قابل مصرف برمی‌گرداند و Consumer دیگری می‌تواند آن را دریافت کند.

اگر Consumer قبل از ACK پیام را پردازش کرده باشد، اما بعد از پردازش Crash کند، پیام ممکن است دوباره تحویل داده شود.

بنابراین Duplicate Processing ممکن است اتفاق بیفتد.

برای جلوگیری از اثر تکراری باید Consumer را Idempotent طراحی کنیم.

## **سؤال ۱۵: چگونه RabbitMQ را در Spring Boot پیاده‌سازی می‌کنیم؟**

Dependency:

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-amqp</artifactId>
</dependency>
```

</div>


تعریف Queue و Exchange:

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```
@Configuration
public class RabbitConfig {

    @Bean
    public Queue orderQueue() {
        return new Queue("order.queue", true);
    }

    @Bean
    public DirectExchange orderExchange() {
        return new DirectExchange("order.exchange");
    }

    @Bean
    public Binding orderBinding() {
        return BindingBuilder
                .bind(orderQueue())
                .to(orderExchange())
                .with("order.created");
    }
}
```

</div>


Producer:

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```
@Service
@RequiredArgsConstructor
public class OrderProducer {

    private final RabbitTemplate rabbitTemplate;

    public void send(OrderCreatedEvent event) {
        rabbitTemplate.convertAndSend(
                "order.exchange",
                "order.created",
                event
        );
    }
}
```

</div>


Consumer:

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```
@Component
public class OrderConsumer {

    @RabbitListener(queues = "order.queue")
    public void consume(OrderCreatedEvent event) {
        System.out.println("Order received: " + event.getOrderId());
    }
}
```

</div>


## **سؤال ۱۶: RabbitMQ چگونه Message Loss را کاهش می‌دهد؟**

برای کاهش احتمال از دست رفتن پیام باید از چند قابلیت استفاده کنیم:

- Durable Queue
- Persistent Message
- Publisher Confirms
- Manual ACK
- Replicated Queue مانند Quorum Queue
- مدیریت صحیح Retry و DLQ

نکته مهم: صرف استفاده از RabbitMQ به معنی تضمین قطعی عدم از دست رفتن پیام نیست. باید Producer، Broker و Consumer هر کدام درست تنظیم و مدیریت شوند.

# **بخش ۳ — سؤالات Kafka**

[Kafka Topics, Partitions & Replication Factor — A Beginner-Friendly Guide | by Rajat Gupta | Medium](https://images.openai.com/static-rsc-4/VE515cwveGSb2TgD3iUSE75jdKHMF8x3ZrUoBIVsSgqgy4sKmMkUTd5FXd1YaoqK8yR0Y3nXqxaV8aySV0iz4elu5upIswo8dv96rt7vuS_kNKXkvUZIQ_dfsOdfJeMieTblQRuecExv1oJlneUy_cvW-Gw1USTdyuerjMXGypU?purpose=inline)

[All things Kafka - DEV Community](https://images.openai.com/static-rsc-4/DqEEx-xDjrg5xHA14DxSX52W9GJHkrUypD-yPUoFpY9KkD5eA8pCHjWW8I54BlPfiagvxaX8lEElYTxs50EEeem53qqfevYzhlpkp6boL7E9CPD5rkGHcNHDkgfd-AKMLzUnihPTo3CSJ-X1XPWUaQAlBORYa1hIhbB9zzW5SZY?purpose=inline)

[Apache Kafka: Key concepts. A Snapshot of Key Concepts | by Amine Raji | Medium](https://images.openai.com/static-rsc-4/B35DBkcxh9tvtII6q1R8B55KG_6mHuHfH5xSOfMeOqbbaSyVzAlg6gpyA1DfIlqresfdEaxBYLtxVq-cwuBzESEAhet6Ji9oxcjqQKfDZIe-1qun8-mQCn6ViCvZPQQ3G_MnHPW7s_D3heb1PS2WC5Akr89mk0a5cmqMgTQkzts?purpose=inline)

8

## **سؤال ۱۷: Kafka چیست و چه تفاوتی با RabbitMQ دارد؟**

Kafka یک Distributed Event Streaming Platform است که برای ذخیره و پردازش حجم بالای Eventها طراحی شده است.

Kafka پیام‌ها را در Topic و Partition نگهداری می‌کند. برخلاف Queueهای سنتی، پیام بعد از خواندن Consumer به صورت خودکار حذف نمی‌شود؛ بلکه براساس Retention Policy باقی می‌ماند.

Kafka مناسب است برای:

- Event-Driven Microservices
- Event Streaming
- Log Aggregation
- Data Pipeline
- Real-time Analytics
- Event Replay

## **سؤال ۱۸: Topic و Partition چیست؟**

**Topic** دسته‌ای از Eventهاست.

مثلاً:

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```
orders
payments
users
notifications
```

</div>


Topic از Partition تشکیل می‌شود.

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```
orders
  |
  +---- Partition 0
  +---- Partition 1
  +---- Partition 2
```

</div>


Partitionها باعث Parallel Processing و Scaling می‌شوند.

هر Partition یک Log مرتب از پیام‌هاست.

[Kafka Topics, Partitions & Replication Factor — A Beginner-Friendly Guide | by Rajat Gupta | Medium](https://images.openai.com/static-rsc-4/VE515cwveGSb2TgD3iUSE75jdKHMF8x3ZrUoBIVsSgqgy4sKmMkUTd5FXd1YaoqK8yR0Y3nXqxaV8aySV0iz4elu5upIswo8dv96rt7vuS_kNKXkvUZIQ_dfsOdfJeMieTblQRuecExv1oJlneUy_cvW-Gw1USTdyuerjMXGypU?purpose=inline)

[Kafka Topics, Partitions, and Brokers: Core Architecture | Conduktor](https://images.openai.com/static-rsc-4/s1rNvxnUeLI-esV4tBQGbbomCeJBVA_yAYUHWM2E6lwgrHstxPUxjLQl1UknMbF2nSoIbldCDUIPp9j0MIGSajcXuHIY2R21wX6vCuoGsRj8BUA2plqJn2kh66jlFwldV13M5i54FsR1Flqs8OuN4O2CqwlVr4RggwOdzDz8Q5E?purpose=inline)

[Documentation Redirect Legacy | Apache Kafka](https://images.openai.com/static-rsc-4/H3z45Z5f806_1cAQrbYjYphKG89P1yXBJUpxSe8gy-aTxz6u9bKaCO5AXqz97-28WmYtYxeQhtfb5DiQfDAe6S33GEmJYV1qMBCEqDDK4gf8FmcHQ6IM0teGccYY3WcuopM-xb1eFLmui-jKMFUFPeEgzgMut-SwVX-s5DpuaWM?purpose=inline)

7

مثلاً:

| Partition | Offset | Event     |
| --------- | ------ | --------- |
| 0         | 0      | Order-101 |
| 0         | 1      | Order-102 |
| 0         | 2      | Order-103 |
| 1         | 0      | Order-104 |
| 1         | 1      | Order-105 |

Ordering در Kafka در سطح Partition تضمین می‌شود، نه بین همه Partitionهای یک Topic.

## **سؤال ۱۹: Offset چیست؟**

Offset شماره موقعیت یک پیام در Partition است.

Consumer از Offset برای مشخص کردن محل خواندن پیام استفاده می‌کند.

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```
Partition 0

Offset 0 → Event A
Offset 1 → Event B
Offset 2 → Event C
Offset 3 → Event D
```

</div>


اگر Consumer روی Offset 2 باشد، یعنی موقعیت خواندن آن در این Partition مربوط به پیام با Offset 2 است.

Kafka Offset را برای هر Partition جداگانه مدیریت می‌کند.

## **سؤال ۲۰: Consumer Group چیست؟**

Consumer Group مجموعه‌ای از Consumerهاست که برای مصرف یک Topic یا چند Topic با هم کار می‌کنند.

Kafka Partitionها را بین Consumerهای یک Group تقسیم می‌کند.

مثال:

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```
Topic: orders
Partitions: 4

Consumer Group: order-service

Consumer 1 → Partition 0, 1
Consumer 2 → Partition 2
Consumer 3 → Partition 3
```

</div>


هر Partition در یک Consumer Group در یک لحظه به یک Consumer اختصاص داده می‌شود.

اگر Consumerهای یک Group از تعداد Partitionها بیشتر باشند، Consumerهای اضافی بیکار خواهند ماند.

[KIP-848: A New Consumer Rebalance Protocol for Apache Kafka® 4.0](https://images.openai.com/static-rsc-4/3oZjV_e_OiNL9gZDZ8AaVGQVs-KbknmUEoBSB2c_q3pyBwXuvoun5afbyXstzn6JhXgntqEQWRUsLBlPuVe9uGV0swLwcnmQB52opBGyMGs6a4pyJXUVD0nXSVidQWDUwcE-JD25w4hhyUxZcLRVNQ_XtGkCq8lPVvG3WGc6YGY?purpose=inline)

## **سؤال ۲۱: اگر تعداد Consumer از تعداد Partition بیشتر باشد چه اتفاقی می‌افتد؟**

Consumerهای اضافی Partition دریافت نمی‌کنند و Idle می‌مانند.

مثلاً:

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```
Partitions = 3
Consumers = 5
```

</div>


در نتیجه:

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```
Consumer 1 → Partition 0
Consumer 2 → Partition 1
Consumer 3 → Partition 2
Consumer 4 → Idle
Consumer 5 → Idle
```

</div>


بنابراین تعداد Consumerهای فعال در یک Group برای یک Topic معمولاً نمی‌تواند از تعداد Partitionهای آن بیشتر شود.

## **سؤال ۲۲: اگر Consumer Crash کند، Kafka چه کاری انجام می‌دهد؟**

Kafka تشخیص می‌دهد Consumer از Group خارج شده است.

سپس Rebalancing انجام می‌شود و Partitionهای Consumer خراب به Consumerهای باقی‌مانده تخصیص داده می‌شوند.

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```
Before:

Consumer 1 → Partition 0
Consumer 2 → Partition 1
Consumer 3 → Partition 2

Consumer 2 Crash

After Rebalance:

Consumer 1 → Partition 0, 1
Consumer 3 → Partition 2
```

</div>


Consumer جدید از Offset ثبت‌شده یا Offset تعیین‌شده توسط سیاست Reset شروع به مصرف می‌کند.

## **سؤال ۲۳: Rebalancing چیست؟**

Rebalancing فرایند تخصیص مجدد Partitionها بین Consumerهای یک Group است.

دلایل:

- اضافه شدن Consumer
- حذف Consumer
- Crash شدن Consumer
- تغییر تعداد Partitionها
- تغییر Subscription

در زمان Rebalance ممکن است مصرف پیام موقتاً متوقف شود.

در سیستم‌های حساس به Latency، Rebalancing زیاد می‌تواند باعث کاهش Performance شود.

## **سؤال ۲۴: تفاوت&#x20;**`enable.auto.commit`**&#x20;و Manual Commit چیست؟**

### **Auto Commit**

Kafka Offset را به شکل خودکار Commit می‌کند.

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```
enable.auto.commit=true
```

</div>


مزیت: پیاده‌سازی ساده‌تر.

ریسک: ممکن است Offset جلوتر از پردازش واقعی ثبت شود و در صورت Crash پیام از دست برود.

### **Manual Commit**

Consumer پس از پردازش موفق پیام، Offset را Commit می‌کند.

در این حالت کنترل بیشتری داریم.

ریسک: اگر پردازش انجام شود ولی Commit موفق نشود، پیام ممکن است دوباره پردازش شود.

بنابراین Manual Commit نیز Duplicate را به تنهایی حذف نمی‌کند.

## **سؤال ۲۵: تفاوت&#x20;**`earliest`**&#x20;و&#x20;**`latest`**&#x20;چیست؟**

تنظیم:

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```
auto.offset.reset=earliest
```

</div>


اگر Consumer Group Offset معتبر نداشته باشد، مصرف را از اولین Offset موجود در محدوده Retention شروع می‌کند.

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```
auto.offset.reset=latest
```

</div>


اگر Offset معتبر نداشته باشد، از آخرین موقعیت موجود شروع می‌کند و پیام‌های جدید را می‌خواند.

این تنظیم فقط وقتی استفاده می‌شود که Offset معتبر برای Consumer وجود نداشته باشد؛ نه اینکه در هر Restart مصرف را از ابتدا شروع کند.

## **سؤال ۲۶: Kafka چگونه Ordering را تضمین می‌کند؟**

Kafka ترتیب پیام‌ها را در یک Partition تضمین می‌کند.

اگر بخواهیم تمام Eventهای مربوط به یک سفارش به ترتیب پردازش شوند، باید از یک Key مشخص استفاده کنیم.

مثلاً:

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```
kafkaTemplate.send(
    "orders",
    orderId,
    event
);
```

</div>


اگر Key برابر `orderId` باشد، پیام‌های دارای Key یکسان معمولاً به یک Partition هدایت می‌شوند؛ بنابراین ترتیب آن‌ها در آن Partition حفظ می‌شود.

مثال:

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```
order-100
    |
    v
Partition 2

OrderCreated
PaymentCompleted
OrderShipped
```

</div>


این پیام‌ها به ترتیب در Partition ثبت می‌شوند.

## **سؤال ۲۷: Replication در Kafka چیست؟**

Replication یعنی نگهداری نسخه‌های مختلف Partition روی Brokerهای مختلف.

مثلاً:

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```
Topic: orders
Partition: 0

Broker 1 → Leader
Broker 2 → Follower
Broker 3 → Follower
```

</div>


Leader مسئول دریافت و مدیریت اصلی درخواست‌های مربوط به Partition است.

Followerها داده را Replicate می‌کنند تا در صورت خرابی Broker، امکان بازیابی وجود داشته باشد.

مفاهیم مهم:

- Leader
- Follower
- Replication Factor
- ISR (In-Sync Replicas)

مثلاً اگر Replication Factor برابر 3 باشد، از هر Partition سه Replica وجود خواهد داشت.

## **سؤال ۲۸: ISR چیست؟**

ISR مخفف In-Sync Replicas است.

Replicaهایی که به اندازه کافی با Leader هماهنگ هستند و طبق شرایط Kafka در مجموعه Replicaهای همگام قرار دارند.

اگر Leader خراب شود، Kafka می‌تواند از Replicaهای مناسب برای انتخاب Leader جدید استفاده کند.

برای جلوگیری از از دست رفتن داده، تنظیمات Replication و Producer باید متناسب با نیاز Reliability انجام شوند.

## **سؤال ۲۹: Kafka چگونه پیام‌ها را نگهداری می‌کند؟**

Kafka پیام‌ها را براساس Retention Policy نگهداری می‌کند.

Retention می‌تواند براساس زمان یا حجم باشد.

مثال:

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```
retention.ms=604800000
```

</div>


یعنی حدود ۷ روز.

پس حتی اگر Consumer پیام‌ها را بخواند، Kafka تا پایان Retention آن‌ها را نگهداری می‌کند.

همچنین Kafka از Log Compaction نیز پشتیبانی می‌کند که برای Topicهایی با Key مناسب، آخرین مقدار مربوط به هر Key را حفظ می‌کند و به مدیریت حجم کمک می‌کند.

## **سؤال ۳۰: تفاوت Kafka و Traditional Queue در حذف پیام چیست؟**

در Traditional Queue مانند مدل معمول RabbitMQ، وقتی پیام به شکل موفق مصرف و ACK شود، معمولاً از Queue حذف می‌شود.

اما در Kafka:

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```
Producer → Kafka Topic → Consumer
                         |
                         v
                    Offset Commit
```

</div>


Commit شدن Offset به معنی حذف پیام نیست.

پیام همچنان در Kafka باقی می‌ماند تا Retention Policy آن را حذف کند.

به همین دلیل چند Consumer Group می‌توانند یک Event را مستقل از یکدیگر مصرف کنند.

# **بخش ۴ — Kafka در Spring Boot**

## **سؤال ۳۱: Kafka را چگونه در Spring Boot پیاده‌سازی می‌کنیم؟**

Dependency:

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```
<dependency>
    <groupId>org.springframework.kafka</groupId>
    <artifactId>spring-kafka</artifactId>
</dependency>
```

</div>


Configuration:

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```
spring:
  kafka:
    bootstrap-servers: localhost:9092

    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer

    consumer:
      group-id: order-group
      auto-offset-reset: earliest
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.springframework.kafka.support.serializer.JsonDeserializer
      properties:
        spring.json.trusted.packages: com.example.event
```

</div>


## **سؤال ۳۲: Kafka Producer در Spring Boot چگونه نوشته می‌شود؟**

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```
@Service
@RequiredArgsConstructor
public class OrderProducer {

    private final KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;

    public void publish(OrderCreatedEvent event) {

        kafkaTemplate.send(
                "orders",
                event.getOrderId().toString(),
                event
        );
    }
}
```

</div>


`KafkaTemplate` برای ارسال پیام به Kafka استفاده می‌شود.

در این مثال:

- Topic: `orders`
- Key: `orderId`
- Value: `OrderCreatedEvent`

است.

## **سؤال ۳۳: Kafka Consumer در Spring Boot چگونه نوشته می‌شود؟**

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```
@Component
public class OrderConsumer {

    @KafkaListener(
        topics = "orders",
        groupId = "order-group"
    )
    public void consume(OrderCreatedEvent event) {

        System.out.println(
            "Received order: " + event.getOrderId()
        );
    }
}
```

</div>


Spring Kafka پیام را به Listener تحویل می‌دهد.

در Production، Consumer باید Exception Handling، Retry، کنترل Offset و Idempotency مناسب داشته باشد.

## **سؤال ۳۴: تفاوت&#x20;**`KafkaTemplate`**&#x20;و&#x20;**`@KafkaListener`**&#x20;چیست؟**

`KafkaTemplate` برای ارسال پیام به Kafka استفاده می‌شود.

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```
kafkaTemplate.send("orders", event);
```

</div>


`@KafkaListener` برای دریافت پیام از Kafka استفاده می‌شود.

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```
@KafkaListener(topics = "orders")
public void listen(OrderCreatedEvent event) {
    // Process event
}
```

</div>


پس:

- `KafkaTemplate` → Producer
- `@KafkaListener` → Consumer

## **سؤال ۳۵: تفاوت&#x20;**`RabbitTemplate`**&#x20;و&#x20;**`KafkaTemplate`**&#x20;چیست؟**

| RabbitTemplate       | KafkaTemplate           |
| -------------------- | ----------------------- |
| متعلق به Spring AMQP | متعلق به Spring Kafka   |
| ارسال به Exchange    | ارسال به Topic          |
| Routing Key دارد     | Key و Partitioning دارد |
| RabbitMQ             | Kafka                   |
| Message Broker       | Event Streaming         |

در RabbitMQ پیام با Exchange و Routing Key مسیریابی می‌شود.

در Kafka پیام به Topic ارسال می‌شود و Key می‌تواند در تعیین Partition نقش داشته باشد.

# **بخش ۵ — سؤالات مهم Reliability و Production**

این بخش از سؤال‌های تعریف‌محور مهم‌تر است؛ معمولاً در مصاحبه‌های سطح Senior، مصاحبه‌کننده از اینجا وارد سناریوهای واقعی می‌شود.

## **سؤال ۳۶: چگونه از پردازش تکراری پیام جلوگیری می‌کنید؟**

مشکل Duplicate Message در سیستم‌های Messaging طبیعی است.

مثلاً:

1. Consumer پیام را دریافت می‌کند.
2. عملیات ثبت پرداخت را انجام می‌دهد.
3. قبل از ACK یا Commit، برنامه Crash می‌کند.
4. پیام دوباره دریافت می‌شود.
5. پرداخت مجدداً انجام می‌شود.

برای جلوگیری از این مشکل از **Idempotency** استفاده می‌کنیم.

مثلاً هر Event دارای `eventId` باشد:

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```
{
  "eventId": "evt-12345",
  "orderId": 1001,
  "eventType": "PAYMENT_COMPLETED"
}
```

</div>


در دیتابیس جدول پردازش Eventها داشته باشیم:

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```
CREATE TABLE processed_events (
    event_id VARCHAR(100) PRIMARY KEY,
    processed_at TIMESTAMP
);
```

</div>


قبل از پردازش:

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```
@Transactional
public void process(OrderEvent event) {

    if (processedEventRepository.existsById(event.getEventId())) {
        return;
    }

    paymentService.process(event);

    processedEventRepository.save(
        new ProcessedEvent(event.getEventId())
    );
}
```

</div>


اما این مثال به تنهایی کافی نیست؛ باید عملیات اصلی و ثبت `eventId` در یک Transaction انجام شوند و در برابر Race Condition هم طراحی درستی داشته باشیم.

## **سؤال ۳۷: Idempotency چیست؟**

Idempotency یعنی اجرای چندباره یک عملیات نتیجه نهایی یکسانی داشته باشد.

مثلاً:

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```
Set account balance to 100
```

</div>


اگر چند بار اجرا شود، نتیجه همچنان 100 خواهد بود.

اما:

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```
Add 100 to account balance
```

</div>


اگر چند بار اجرا شود، هر بار مقدار حساب تغییر می‌کند.

در Messaging، Idempotent Consumer باعث می‌شود Duplicate Message اثر تکراری روی سیستم نداشته باشد.

روش‌های رایج:

- Event ID
- Unique Constraint در دیتابیس
- Idempotency Key
- جدول Processed Events
- عملیات Database با شرط و کنترل مناسب

## **سؤال ۳۸: Exactly-once آیا واقعاً وجود دارد؟**

جواب مناسب مصاحبه:

> Exactly-once delivery is difficult to guarantee across distributed systems. Kafka supports idempotent producers and transactions for specific Kafka workflows, but exactly-once processing across Kafka and an external database or service requires additional design. In practice, we often use at-least-once delivery combined with idempotent consumers.

یعنی Kafka قابلیت Exactly-once در سناریوهای مشخصی دارد، اما اگر Consumer در دیتابیس Oracle یا PostgreSQL اطلاعات ذخیره کند، Kafka نمی‌تواند به تنهایی تضمین کند که تغییر دیتابیس و Commit Offset با هم Atomic باشند.

## **سؤال ۳۹: اگر Kafka پیام را دریافت کند ولی Consumer قبل از Commit کردن Offset Crash کند چه می‌شود؟**

اگر پیام پردازش شده باشد ولی Offset Commit نشده باشد، Consumer بعد از Restart ممکن است همان پیام را مجدداً دریافت کند.

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```
Receive Message
      |
Process Successfully
      |
      X Crash before Offset Commit
      |
Restart
      |
Receive Same Message Again
```

</div>


نتیجه:

**Duplicate Processing**

راهکار:

- Idempotent Consumer
- کنترل Offset Commit
- ذخیره Event ID
- جلوگیری از Side Effect تکراری

## **سؤال ۴۰: اگر Consumer پیام را دریافت کند ولی پردازش آن شکست بخورد چه می‌کنید؟**

باید براساس نوع خطا تصمیم بگیریم.

### **خطای موقت**

مثلاً:

- Database temporarily unavailable
- Timeout سرویس دیگر
- Network issue

از Retry با Backoff استفاده می‌کنیم.

### **خطای دائمی**

مثلاً:

- Invalid payload
- داده ناقص
- Unsupported event

پیام را پس از Retry محدود به DLQ منتقل می‌کنیم تا بررسی شود.

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```
Consumer
   |
   v
Processing
   |
   +---- Success → ACK / Commit
   |
   +---- Temporary Failure → Retry
   |
   +---- Permanent Failure → DLQ
```

</div>


## **سؤال ۴۱: Backpressure چیست؟**

Backpressure زمانی مطرح می‌شود که Producer سریع‌تر از Consumer پیام تولید کند.

مثلاً:

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```
Producer: 10,000 messages/sec
Consumer: 2,000 messages/sec
```

</div>


در نتیجه، حجم پیام‌ها افزایش پیدا می‌کند.

راهکارها:

- افزایش Consumerها
- افزایش Partitionها در Kafka
- کنترل Prefetch در RabbitMQ
- Rate Limiting
- Batch Processing
- کاهش هزینه پردازش
- کنترل Producer Rate
- مانیتورینگ Lag و Queue Depth

نکته: افزایش Consumer همیشه جواب نمی‌دهد؛ در Kafka تعداد Consumerهای فعال به تعداد Partitionها محدود است و در RabbitMQ نیز محدودیت‌های منابع و ظرفیت Queue مطرح‌اند.

## **سؤال ۴۲: Kafka Consumer Lag چیست؟**

Consumer Lag یعنی Consumer از آخرین پیام موجود در Partition عقب افتاده است.

مثلاً:

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```
Latest Offset = 10000
Consumer Offset = 8500

Lag = 1500
```

</div>


یعنی Consumer هنوز 1500 پیام عقب‌تر از آخرین پیام موجود است.

Lag زیاد ممکن است نشان‌دهنده این موارد باشد:

- Consumer کند است.
- حجم پیام‌ها افزایش یافته است.
- تعداد Consumer کافی نیست.
- Database یا External Service کند است.
- پردازش پیام‌ها هزینه زیادی دارد.

## **سؤال ۴۳: Transactional Outbox Pattern چیست و چرا استفاده می‌شود؟**

این سؤال برای مصاحبه Senior بسیار مهم است.

فرض کنید:

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```
@Transactional
public void createOrder(Order order) {

    orderRepository.save(order);

    kafkaTemplate.send("orders", new OrderCreatedEvent(order));
}
```

</div>


مشکل:

ممکن است Order در Database ثبت شود، اما ارسال پیام به Kafka شکست بخورد.

در نتیجه:

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```
Database → Order Saved
Kafka → Event Not Sent
```

</div>


این وضعیت باعث ناسازگاری سیستم می‌شود.

### **راهکار: Transactional Outbox**

به جای ارسال مستقیم پیام به Kafka، Event را در همان Database و Transaction ثبت می‌کنیم.

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```
Transaction
    |
    +---- Save Order
    |
    +---- Save Outbox Event
```

</div>


هر دو عملیات در یک Transaction دیتابیس انجام می‌شوند.

سپس یک Worker یا CDC Tool مانند Debezium، Outbox را خوانده و Event را به Kafka ارسال می‌کند.

[Outbox Pattern for Reliable Event Publishing | Conduktor](https://images.openai.com/static-rsc-4/W5LVi_TQcPZQZ4LBWH_Pkz8ECnpW8pAsw3nbZjWkvK9bHc0D-XmfooOZovJzq4Z3uUOEpeWQlrT2vVdeBOCkRWTUAkKFu38pBHvCDAnfWfP8aI4NN8XSHYvfLh1dJ61nhL_S-DEEKr9kCNdKknmDbZ2xF1IeBcIMWrB2tMOuqC8?purpose=inline)

[11 Kafka Design Patterns for Every Backend Engineer | by Vineet Sharma | Apr, 2026 | Medium](https://images.openai.com/static-rsc-4/sdii90X5WvymvOvsuOBy3_jpcqV3vEPoqPVAoj_6iuSNEFFLlaPBXTun208ke2ZwnqZzAbHcyC8-JEbx_n5Wd971Uv3vTbBPKErPE26a9x40vyA4sJYdMQiIhx73ND_j9JT2jNc6T0JKUZoovGSYCTtuhOgQZThTl6RCsRpRmqo?purpose=inline)

[Debezium meets Quarkus](https://images.openai.com/static-rsc-4/0SIsPrmB-NW3Zq9cI4kK5-9sCSfYMlUdj9ivrJedmFJKtVf-8L7vY1KnyvAvZukR0kk8hSVqRYWiM4Z-3YH8VX_l6gig-jA_FXhe2MZvLSy1CoB5CGw0Vzv9R8a7-wa-4tJrdI__3IJ2BXRVWrcWryrxTzR3qTkWjIvghTffJJs?purpose=inline)

9

مزایا:

- جلوگیری از مشکل Dual Write
- هماهنگی بین ذخیره اطلاعات و ثبت Event
- کاهش احتمال گم شدن Event

با این حال، ارسال Event ممکن است تکراری شود؛ بنابراین Consumer باید Idempotent باشد.

## **سؤال ۴۴: تفاوت Event و Command چیست؟**

### **Command**

درخواست انجام یک کار است.

مثلاً:

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```
CreateOrder
ProcessPayment
SendEmail
```

</div>


Command معمولاً به یک Handler مشخص هدایت می‌شود.

### **Event**

اعلام می‌کند یک اتفاق در گذشته رخ داده است.

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```
OrderCreated
PaymentCompleted
UserRegistered
```

</div>


Event معمولاً برای چند سرویس قابل انتشار است و هر سرویس می‌تواند مستقل به آن واکنش نشان دهد.

| Command                  | Event                            |
| ------------------------ | -------------------------------- |
| فعل و درخواست            | اتفاق رخ‌داده                    |
| `CreateOrder`            | `OrderCreated`                   |
| هدف انجام عملیات         | اطلاع‌رسانی                      |
| معمولاً یک Handler منطقی | ممکن است چند Consumer داشته باشد |

## **سؤال ۴۵: اگر یک Event توسط چند Microservice مصرف شود، چه معماری‌ای طراحی می‌کنید؟**

مثلاً پس از ثبت سفارش:

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```
                  Order Service
                       |
                       v
                  OrderCreated
                       |
                       v
                     Kafka
          _____________|______________
         |             |             |
         v             v             v
  Payment Service  Inventory     Notification
                   Service         Service
```

</div>


هر Microservice Consumer Group مستقل دارد.

مثلاً:

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```
payment-group
inventory-group
notification-group
```

</div>


در این صورت هر Group می‌تواند Eventهای Topic را مستقل مصرف کند.

نکته مهم: اگر چند Consumer در یک Group باشند، Partitionها بین آن‌ها تقسیم می‌شوند؛ اما اگر Consumerها در Groupهای مختلف باشند، هر Group به صورت مستقل Eventها را دریافت می‌کند.

## **سؤال ۴۶: چگونه Message Ordering را در یک سیستم Microservice حفظ می‌کنید؟**

در Kafka:

- از Key مناسب استفاده می‌کنیم.
- Eventهای مربوط به یک Entity را با Key یکسان منتشر می‌کنیم.
- مطمئن می‌شویم به Partition یکسان هدایت شوند.
- در Consumer از Parallel Processing نامناسب که ترتیب را خراب می‌کند جلوگیری می‌کنیم.

مثلاً برای سفارش:

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```
kafkaTemplate.send(
    "orders",
    orderId.toString(),
    event
);
```

</div>


برای `orderId` یکسان، پیام‌ها معمولاً به Partition یکسان می‌روند.

در RabbitMQ نیز برای Ordering باید Queue، تعداد Consumerها و نحوه Processing را با دقت انتخاب کنیم. داشتن چند Consumer یا پردازش Concurrent می‌تواند ترتیب نهایی انجام عملیات را تغییر دهد.

## **سؤال ۴۷: آیا می‌توان از Kafka و RabbitMQ به صورت همزمان در یک پروژه استفاده کرد؟**

بله.

مثلاً:

| کاربرد                 | ابزار    |
| ---------------------- | -------- |
| پردازش سفارش‌های Async | RabbitMQ |
| توزیع Job              | RabbitMQ |
| Eventهای دامنه         | Kafka    |
| Event Analytics        | Kafka    |
| Audit Events           | Kafka    |
| ارسال ایمیل با Worker  | RabbitMQ |

اما استفاده هم‌زمان از هر دو باعث پیچیدگی عملیاتی می‌شود.

اگر یک ابزار تمام نیازها را پوشش می‌دهد، اضافه کردن ابزار دوم ممکن است ارزش کافی نداشته باشد.

# **بخش ۶ — سؤالات سناریویی که احتمالاً مصاحبه‌کننده می‌پرسد**

این قسمت را حتماً تمرین کن؛ چون در مصاحبه معمولاً مصاحبه‌کننده با یک مشکل شروع می‌کند و می‌خواهد راهکار معماری تو را بشنود.

## **سؤال ۴۸: اگر پیام‌ها در RabbitMQ یا Kafka Duplicate شوند، چه می‌کنید؟**

**جواب:**

Duplicate Message یک اتفاق طبیعی در سیستم‌های توزیع‌شده است. من سیستم را به شکلی طراحی می‌کنم که Consumerها Idempotent باشند. برای هر Event یک شناسه یکتا در نظر می‌گیرم و در دیتابیس از Unique Constraint یا جدول Processed Events استفاده می‌کنم تا یک Event نتواند Side Effect تکراری ایجاد کند.

## **سؤال ۴۹: اگر Kafka Consumer خیلی کند باشد، چطور مشکل را حل می‌کنید؟**

**جواب:**

ابتدا Consumer Lag و زمان پردازش هر پیام را بررسی می‌کنم. مشخص می‌کنم مشکل از خود Consumer است یا Database، شبکه یا سرویس‌های خارجی.

سپس:

- اگر پردازش CPU-bound باشد، Consumer را بهینه می‌کنم.
- اگر Database کند باشد، Query و Indexها را بررسی می‌کنم.
- اگر Parallelism کم باشد، تعداد Partitionها و Consumerها را بررسی می‌کنم.
- اگر پیام‌ها به صورت تکی پردازش شوند، در صورت مناسب بودن از Batch استفاده می‌کنم.
- اگر External API کند باشد، Timeout، Retry و Circuit Breaker را بررسی می‌کنم.

## **سؤال ۵۰: اگر RabbitMQ Queue دائماً در حال بزرگ شدن باشد، چه می‌کنید؟**

**جواب:**

ابتدا Queue Depth و نرخ تولید و مصرف پیام را بررسی می‌کنم.

اگر:

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```
Publish Rate > Consume Rate
```

</div>


Queue بزرگ‌تر خواهد شد.

بعد موارد زیر را بررسی می‌کنم:

1. Consumerها سالم هستند؟
2. تعداد Consumer کافی است؟
3. Processing Time زیاد است؟
4. Database کند است؟
5. پیام‌ها به شکل غیرضروری Retry می‌شوند؟
6. Prefetch مناسب است؟
7. آیا می‌توان Consumerها را Scale کرد؟

اگر Consumerها امکان Scaling داشته باشند، تعداد آن‌ها را افزایش می‌دهم و سپس ظرفیت Queue و منابع Broker را بررسی می‌کنم.

## **سؤال ۵۱: آیا Kafka تضمین می‌کند یک پیام فقط یک بار پردازش شود؟**

**جواب:**

خیر، به صورت پیش‌فرض چنین تضمینی برای کل عملیات Business نداریم.

Kafka از Idempotent Producer و Transactional Processing پشتیبانی می‌کند، اما اگر Consumer پیام را دریافت کند، عملیات Database را انجام دهد و قبل از Commit کردن Offset Crash کند، ممکن است پیام دوباره دریافت شود.

برای جلوگیری از اثر تکراری، Consumer باید Idempotent باشد.

## **سؤال ۵۲: چرا نباید در Consumer عملیات سنگین انجام دهیم؟**

**جواب:**

چون عملیات سنگین باعث افزایش Processing Time، Consumer Lag، کاهش Throughput و احتمال Rebalance می‌شود.

اگر پردازش سنگین باشد:

- پردازش را به بخش‌های کوچک‌تر تقسیم می‌کنم.
- Database را بهینه می‌کنم.
- Parallelism کنترل‌شده ایجاد می‌کنم.
- از Batch استفاده می‌کنم.
- در صورت نیاز Event دیگری منتشر می‌کنم تا یک سرویس مستقل مسئول پردازش سنگین باشد.

اما Parallel Processing باید به نحوی باشد که ترتیب Eventها و Thread Safety به هم نخورند.

# **بخش ۷ — سؤالات کوتاه و پرتکرار مصاحبه**

| سؤال                  | جواب کوتاه                                                    |
| --------------------- | ------------------------------------------------------------- |
| RabbitMQ چیست؟        | Message Broker برای ارتباط Async و Routing پیام‌ها            |
| Kafka چیست؟           | Distributed Event Streaming Platform                          |
| Exchange چیست؟        | نقطه ورود و Routing پیام در RabbitMQ                          |
| Binding چیست؟         | ارتباط بین Exchange و Queue                                   |
| Routing Key چیست؟     | کلیدی که در Routing پیام نقش دارد                             |
| Fanout چیست؟          | ارسال پیام به تمام Queueهای متصل                              |
| Topic Exchange چیست؟  | Exchange با Pattern Matching روی Routing Key                  |
| DLQ چیست؟             | محل نگهداری پیام‌های ناموفق                                   |
| ACK چیست؟             | تأیید پردازش پیام                                             |
| NACK چیست؟            | رد پیام                                                       |
| Prefetch چیست؟        | محدودیت پیام‌های Unacknowledged Consumer                      |
| Kafka Partition چیست؟ | تقسیم Topic برای Parallelism و Scalability                    |
| Offset چیست؟          | موقعیت پیام در Partition                                      |
| Consumer Group چیست؟  | گروه Consumerهای همکار برای تقسیم Partitionها                 |
| Rebalancing چیست؟     | تخصیص مجدد Partition بین Consumerها                           |
| Consumer Lag چیست؟    | فاصله Consumer از آخرین پیام موجود                            |
| ISR چیست؟             | مجموعه Replicaهای همگام با Leader                             |
| Idempotency چیست؟     | اجرای تکراری بدون ایجاد اثر تکراری                            |
| Retention چیست؟       | مدت یا میزان نگهداری پیام در Kafka                            |
| Backpressure چیست؟    | کنترل فشار ناشی از تولید سریع‌تر پیام نسبت به مصرف            |
| Replay چیست؟          | خواندن دوباره Eventهای قبلی                                   |
| Poison Message چیست؟  | پیام مشکل‌داری که مکرراً نمی‌تواند پردازش شود                 |
| Outbox Pattern چیست؟  | ثبت Transactional داده و Event در دیتابیس برای ارسال مطمئن‌تر |

# **بخش ۸ — جدول نهایی مقایسه RabbitMQ و Kafka**

| موضوع             | RabbitMQ                     | Kafka                        |
| ----------------- | ---------------------------- | ---------------------------- |
| معماری            | Broker با Routing            | Distributed Log              |
| Message Structure | Exchange → Queue             | Topic → Partition            |
| مصرف پیام         | معمولاً با ACK حذف می‌شود    | با Commit Offset حذف نمی‌شود |
| Replay            | محدود و نیازمند طراحی        | بسیار مناسب                  |
| Ordering          | وابسته به Queue و Consumer   | در هر Partition              |
| Parallelism       | Consumerها                   | Partitionها و Consumer Group |
| Retry             | Queue، TTL، DLX              | Retry Topic و الگوهای دیگر   |
| Throughput        | مناسب                        | بسیار بالا در Streaming      |
| Work Distribution | بسیار مناسب                  | ممکن، اما متفاوت از Queue    |
| Event Streaming   | ممکن، ولی Kafka مناسب‌تر است | کاربرد اصلی                  |
| Persistence       | Queue و Message              | Log و Retention              |
| Scaling           | Queue و Consumer             | Partition و Broker           |
| کاربرد            | Job، Task، Async Command     | Event، Streaming، Analytics  |

# **بخش ۹ — ۱۰ سؤال طلایی که باید بتوانی بدون کمک جواب بدهی**

اگر برای مصاحبه آماده می‌شوی، این ۱۰ سؤال را در اولویت قرار بده:

1. چرا به جای REST از RabbitMQ یا Kafka استفاده می‌کنیم؟
2. تفاوت RabbitMQ و Kafka چیست و چه زمانی کدام را انتخاب می‌کنیم؟
3. Exchangeهای RabbitMQ چه تفاوتی دارند؟
4. ACK، NACK و Retry چگونه کار می‌کنند؟
5. DLQ چیست و چگونه پیاده‌سازی می‌شود؟
6. Kafka Partition و Consumer Group چگونه کار می‌کنند؟
7. Offset و Rebalancing چیست؟
8. چگونه از Duplicate Processing جلوگیری می‌کنیم؟
9. Transactional Outbox Pattern چیست و چرا استفاده می‌شود؟
10. اگر Consumer کند شود، پیام‌ها Duplicate شوند یا Queue/Lag زیاد شود، چه راهکاری داریم؟

## **پیشنهاد برای نحوه مطالعه**

## **ترتیب پیشنهادی مطالعه**

۱

**RabbitMQ — مباحث Core**

Exchange، Queue، Binding، Routing، ACK، Retry و DLQ

۲

**Kafka — مباحث Core**

Topic، Partition، Offset، Consumer Group، Rebalance و Replication

۳

**Spring Boot Implementation**

RabbitTemplate، KafkaTemplate، Listenerها، Configuration و Serialization

۴

**Production Scenarios**

Idempotency، Retry، Outbox، Failure، Scaling و Monitoring

اگر بخواهی برای مصاحبه آماده شوی، صرف خواندن جواب‌ها کافی نیست. باید بتوانی هر جواب را در حدود ۱ تا ۲ دقیقه توضیح بدهی و اگر مصاحبه‌کننده سؤال بعدی را پرسید، وارد جزئیات فنی شوی.

در ادامه می‌توانیم RabbitMQ و Kafka را به صورت یک **جزوه کامل مصاحبه Java Spring Boot، سؤال و جواب سطح Mid/Senior، همراه مثال واقعی، کد و سناریوهای Production** تکمیل کنیم.
</div>



<!-- SOURCE: 1.md -->
# File: 1.md

<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">


عالیه. اگر قرار است برای **RabbitMQ و Kafka در Java + Spring Boot مصاحبه فنی** آماده شوی، بهتر است فقط تعریف این دو ابزار را حفظ نکنی؛ باید بتوانی معماری، تفاوت‌ها، سناریوهای واقعی، نحوه پیاده‌سازی و مشکلات Production را توضیح بدهی.

با توجه به سؤالات Java، Spring، Hibernate، JMS و معماری بک‌اند که داری تمرین می‌کنی، پیشنهاد می‌کنم این مبحث را به شکل **سؤالات مصاحبه همراه جواب تشریحی و کد Spring Boot** جلو ببریم.

## **نقشه راه مصاحبه RabbitMQ و Kafka**

## **مرحله ۱ — مفاهیم پایه Messaging**

- Message Broker چیست و چرا استفاده می‌کنیم؟
- تفاوت Synchronous و Asynchronous communication
- Producer، Consumer، Message، Broker
- Queue و Topic چیست؟
- Message delivery guarantee
- Loose Coupling و Event-Driven Architecture

## **مرحله ۲ — RabbitMQ**

- معماری RabbitMQ
- Exchange و انواع آن
- Queue و Binding
- Routing Key
- Acknowledgment و NACK
- Retry و Dead Letter Queue
- Prefetch و Consumer concurrency
- Persistence و Durability
- پیاده‌سازی با Spring Boot و Spring AMQP

## **مرحله ۳ — Kafka**

- Kafka Architecture
- Broker و Cluster
- Topic و Partition
- Offset
- Consumer Group
- Replication و Leader/Follower
- Ordering
- Retention
- Rebalancing
- Delivery semantics
- پیاده‌سازی با Spring Kafka

## **مرحله ۴ — مقایسه RabbitMQ و Kafka**

- Queue-based Messaging vs Distributed Event Streaming
- Message حذف می‌شود یا باقی می‌ماند؟
- Retry چگونه انجام می‌شود؟
- Ordering
- Scalability
- Throughput
- چه زمانی RabbitMQ و چه زمانی Kafka؟

## **مرحله ۵ — سؤالات Senior / Production**

- جلوگیری از Duplicate Processing
- Idempotency
- Retry Strategy
- Poison Message
- Transactional Outbox
- Exactly Once و محدودیت‌های آن
- Consumer failure
- Message loss
- Backpressure
- Monitoring و Observability

# **جلسه اول: Messaging چیست و چرا استفاده می‌کنیم؟**

فرض کن یک فروشگاه اینترنتی با Spring Boot داریم. مشتری سفارش ثبت می‌کند و بعد از ثبت سفارش باید:

1. سفارش در دیتابیس ذخیره شود.
2. پرداخت انجام شود.
3. ایمیل برای مشتری ارسال شود.
4. موجودی کالا کاهش پیدا کند.
5. سرویس Notification پیام ارسال کند.

## **روش اول: ارتباط مستقیم (Synchronous)**



<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```mermaid
%%{init: {"theme":"base","themeVariables":{"fontFamily":"-apple-system, BlinkMacSystemFont, 'Segoe UI', Helvetica, Arial, sans-serif","fontSize":"16px","background":"#000000","primaryColor":"#00284d","primaryTextColor":"#99ceff","primaryBorderColor":"rgba(255,255,255,0.1)","lineColor":"#afafaf","secondaryColor":"#000e1a","tertiaryColor":"#000e1a","edgeLabelBackground":"#000e1a","clusterBkg":"#303030","clusterBorder":"rgba(255,255,255,0.15)","titleColor":"#ededed","nodeTextColor":"#99ceff"}}}%%
flowchart TD
    A["Order Service"] --> B["Payment Service"]
    B --> C["Inventory Service"]
    C --> D["Email Service"]
    D --> E["Order Completed"]
```

</div>



در این حالت Order Service باید مستقیماً سرویس‌های دیگر را صدا بزند.

### **مشکلات**

- اگر Email Service خراب باشد، ممکن است ثبت سفارش با مشکل مواجه شود.
- Order Service به سرویس‌های زیادی وابسته است.
- زمان پاسخ‌گویی بالا می‌رود.
- اگر یکی از سرویس‌ها Timeout شود، زنجیره درخواست دچار مشکل می‌شود.
- Scaling مستقل سرویس‌ها سخت‌تر می‌شود.

## **روش دوم: ارتباط Asynchronous با Message Broker**

در این معماری، Order Service پیام را به Message Broker می‌فرستد و سرویس‌های دیگر آن را دریافت می‌کنند.

[A Guide To Event-Driven Architectural Patterns](images/2.png)
<img src="images/2.png" alt="لوگو" width="200">

[Spring Boot + Apache Kafka Course - The Practical Guide - HackMD](images/1.png)
<img src="images/1.png" alt="لوگو" width="200">
[Event-Driven Architecture: Scalable and Resilient Systems that Never Miss a Beat | by Bakti Pratama | Coffee☕ And Code💚 | Medium](images/3.png)
<img src="images/3.png" alt="لوگو" width="200">
10

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```mermaid
flowchart TD
A[Order Service] --> B[Message Broker]
B --> C[Payment Consumer]
B --> D[Inventory Consumer]
B --> E[Notification Consumer]
```

</div>




Order Service دیگر لازم نیست منتظر بماند تا تمام سرویس‌ها کارشان را انجام دهند.

مثلاً بعد از ثبت سفارش:



<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```
orderRepository.save(order);

rabbitTemplate.convertAndSend(
    "order.exchange",
    "order.created",
    new OrderCreatedEvent(order.getId())
);
```

</div>


در اینجا Order Service پیام `OrderCreatedEvent` را منتشر می‌کند.

سرویس‌های دیگر می‌توانند مستقل از Order Service این پیام را دریافت و پردازش کنند.

# **1. Message Broker چیست؟**

Message Broker یک واسط نرم‌افزاری است که پیام‌ها را از Producer دریافت می‌کند و بر اساس قواعد مشخص آن‌ها را به Consumerها تحویل می‌دهد.

دو ابزار مهم:

- RabbitMQ
- Apache Kafka

### **Producer**

سیستمی که پیام تولید می‌کند.

مثال: Order Service

### **Consumer**

سیستمی که پیام را دریافت و پردازش می‌کند.

مثال: Notification Service

### **Message**

اطلاعاتی که بین سرویس‌ها ارسال می‌شود.

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```
{
  "eventId": "e-1001",
  "orderId": 5001,
  "eventType": "ORDER_CREATED",
  "customerId": 120,
  "createdAt": "2026-11-21T10:00:00"
}
```

</div>


# **2. RabbitMQ چیست؟**

RabbitMQ

RabbitMQ یک Message Broker است که معمولاً برای ارسال، ذخیره موقت، Routing و تحویل پیام بین Producer و Consumer استفاده می‌شود.

در RabbitMQ مفاهیم مهم عبارت‌اند از:

| Component   | وظیفه                       |
| ----------- | --------------------------- |
| Producer    | تولید پیام                  |
| Exchange    | دریافت پیام و تعیین مسیر آن |
| Binding     | اتصال Exchange به Queue     |
| Routing Key | مشخص کردن مسیر پیام         |
| Queue       | نگهداری پیام تا مصرف آن     |
| Consumer    | دریافت و پردازش پیام        |

[Why Do We Need a Message Queue? - ByteByteGo Newsletter](https://images.openai.com/static-rsc-4/Hhu7JPU_vwfZVoJABYAM1f7arB2WdkT3CVqTytQjdFJVgPKBQh-Cwf4nX2CRMgRgM6LvmB1Hfb2bpYCyLCtycEHgqYUtvIDGS9eSa-6jQG8SlAewI548QOUtOCCBg4QZzeZosRE3p4PL0h1lkQgE9OiY6iR4XXtnauTtsnFsOmo?purpose=inline)

[RabbitMQ + Spring boot + Docker. Отправляем и получаем сообщения через Producer и Consumer. Пошаговое руководство / Хабр](https://images.openai.com/static-rsc-4/bamdFKWQpMV9mN9bCsbmwM1cdMZwLyflROwVbV5Gwe-_L7vRXc0YpA_ljvB6NTYRkcnpXPu8n8GFhcTa_jN30jQ73uUpD8fnYWtL_kqR7rs_gO6F_NdLadnDUqCuXfoRSdXW_Ca2lu2mHmMHLq75zgjybxq6ebWqui8ckaUS-To?purpose=inline)

[OpenAI + .NET Web API. Artificial Intelligence is no longer… | by Gaurav Ashish | Medium](https://images.openai.com/static-rsc-4/VewqYN-1MKyTUiWE1gz_ZEZ3vstYaKkZfIdguIoaPr5EOoQONfmG3aS9y98vEZqm0raCrZVjBwsBZh9Q8nqPiiS-hwBSgb4RrUMDyPopzX3Yr1rx29L7DxxsWdMr6nRnOrhGnayLSbxgnpktcHjPy6hzE3aQTgYqmdhs_Dp5t4A?purpose=inline)

7

معماری کلی:

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```
Producer
   |
   v
 Exchange
   |
 Binding + Routing Key
   |
   v
  Queue
   |
   v
 Consumer
```

</div>


RabbitMQ در بسیاری از سناریوها برای Task Queue، توزیع کار، پردازش Async، Retry و ارتباط سرویس‌ها بسیار مناسب است.&#x20;

[image](https://www.google.com/s2/favicons?domain=https://www.rabbitmq.com\&sz=32)

RabbitMQ



# **3. Kafka چیست؟**

Apache Kafka

Kafka بیشتر برای Distributed Event Streaming و نگهداری و پردازش حجم زیادی از Eventها طراحی شده است.

معماری کلی:

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```
Producer
   |
   v
 Kafka Cluster
   |
   v
 Topic
   |
   +---- Partition 0
   +---- Partition 1
   +---- Partition 2
   |
   v
 Consumer Group
   |
   +---- Consumer 1
   +---- Consumer 2
```

</div>


مفاهیم مهم Kafka:

| Component      | وظیفه                        |
| -------------- | ---------------------------- |
| Producer       | ارسال Event                  |
| Broker         | نگهداری و مدیریت داده        |
| Topic          | دسته‌بندی Eventها            |
| Partition      | تقسیم Topic برای Parallelism |
| Offset         | موقعیت Consumer در Partition |
| Consumer Group | گروه Consumerهای همکار       |
| Replication    | افزایش تحمل خرابی            |

Kafka برخلاف مدل Queue معمولی، پیام‌ها را تا زمان پایان Retention نگهداری می‌کند؛ بنابراین Consumer می‌تواند مجدداً پیام‌های قبلی را بخواند یا به Offset مشخصی برگردد.

# **4. مهم‌ترین تفاوت RabbitMQ و Kafka**

| ویژگی          | RabbitMQ                                    | Kafka                                      |
| -------------- | ------------------------------------------- | ------------------------------------------ |
| مدل            | Message Broker                              | Event Streaming Platform                   |
| ساختار اصلی    | Exchange و Queue                            | Topic و Partition                          |
| مصرف پیام      | معمولاً پیام بعد از ACK از Queue حذف می‌شود | پیام با Consumer شدن حذف نمی‌شود           |
| Replay         | معمولاً محدود و نیازمند طراحی مجدد          | قابلیت اصلی و طبیعی                        |
| Ordering       | با توجه به Queue و شرایط Consumer           | در سطح Partition                           |
| Load balancing | بین Consumerهای یک Queue                    | بین Consumerهای یک Group                   |
| کاربرد         | Task Queue، Command، Async Processing       | Event Streaming، Analytics، Event Pipeline |
| Throughput     | مناسب، بسته به معماری و تنظیمات             | بسیار مناسب حجم و Throughput بالا          |
| Retry          | معمولاً Queue، TTL، DLQ                     | Retry Topic یا الگوهای retry               |
| Scaling        | Queueها و Consumerها                        | Partitionها و Consumerها                   |

نکته مصاحبه:

این جواب اشتباه است که بگویی Kafka همیشه سریع‌تر است یا RabbitMQ همیشه بهتر است.

انتخاب ابزار به نوع نیاز بستگی دارد:

- اگر بخواهم Jobها را بین Workerها توزیع کنم → RabbitMQ انتخاب مناسبی است.
- اگر بخواهم Eventهای یک سیستم را ذخیره کنم و چند سرویس مستقل آن‌ها را مصرف کنند، Replay انجام دهند و حجم زیادی از داده پردازش کنند → Kafka انتخاب مناسبی است.

# **5. سؤال اول مصاحبه**

## **اگر در یک پروژه Microservice از شما بپرسند چرا به جای REST از RabbitMQ یا Kafka استفاده می‌کنید، چه پاسخی می‌دهید؟**

جواب پیشنهادی در مصاحبه:

> REST communication is synchronous and creates runtime dependency between services. Using a message broker allows asynchronous communication, loose coupling, better fault tolerance, and independent scaling of consumers. It also helps absorb traffic spikes and allows services to process events independently. However, it introduces additional complexity such as eventual consistency, message retries, duplicate processing, and monitoring.

پیشنهاد من این است که این موضوع را **سؤال‌به‌سؤال و با عمق مصاحبه Senior Java Developer** جلو ببریم؛ هر سؤال را اول با توضیح فارسی، بعد پاسخ مناسب انگلیسی برای مصاحبه، و در صورت نیاز با کد Spring Boot بررسی کنیم.

جلسه بعدی می‌تواند از **RabbitMQ: Exchangeها (Direct، Fanout، Topic، Headers)، Binding و Routing Key** شروع شود؛ این یکی از بخش‌هایی است که احتمال سؤال شدنش در مصاحبه بسیار زیاد است.

اگر بخواهی، از همین حالا می‌توانیم حالت مصاحبه واقعی را شروع کنیم: من سؤال می‌پرسم، تو جواب می‌دهی، و جواب تو را مثل مصاحبه‌گر Senior بررسی و اصلاح می‌کنم.
</div>


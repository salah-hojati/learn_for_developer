<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">

ریس استریم (Redis Streams) یک ساختار داده **لاگ‌محور (append-only log)** است که از نسخه ۵.۰ به بعد به Redis اضافه شده و آن را به یک بروکر پیام سبک و مقاوم تبدیل می‌کند. برخلاف Pub/Sub که پیام‌ها را دور می‌ریزد، در Streams هر رویداد **به‌صورت پایدار ذخیره** می‌شود، ترتیب زمانی دارد و می‌توان آن را توسط چندین مصرف‌کننده مستقل پردازش کرد. این ویژگی‌ها باعث می‌شود Streams گزینه‌ای مناسب برای جایگزینی Kafka در حجم‌های کمتر از ترابایت باشد.

---

### 🧩 مفاهیم اصلی

- **شناسه پیام (Message ID)**: هر ورودی یک شناسه یکتا به‌صورت `timestamp-sequence` دارد که به‌طور خودکار (با `*`) یا دستی تولید می‌شود و **ترتیب زمانی** را تضمین می‌کند.
- **ورودی (Entry)**: هر پیام یک جفت‌های کلید-مقدار (مانند یک Map) است.
- **گروه مصرف‌کننده (Consumer Group)**: مکانیزم اصلی Streams که به چند مصرف‌کننده اجازه می‌دهد روی یک Stream کار کنند. پیام‌ها بین اعضای گروه **تقسیم (load balance)** می‌شوند و هر مصرف‌کننده **موقعیت خودش** را در Stream نگه می‌دارد.
- **لیست ورودی‌های در انتظار (PEL - Pending Entry List)**: هر پیامی که توسط یک مصرف‌کننده در گروه خوانده شده اما هنوز **تأیید (ACK)** نشده باشد، در PEL ثبت می‌شود. این مکانیزم تضمین می‌کند که اگر مصرف‌کننده‌ای از کار بیفتد، پیام گم نشود و بتوان آن را به مصرف‌کننده دیگری سپرد.

---

### ⚙️ دستورات کلیدی (با مثال)

| هدف | دستور | مثال |
|---|---|---|
| **نوشتن پیام** | `XADD` | `XADD orders * orderId ORD-001 amount 99.99` |
| **خواندن ساده** | `XREAD` | `XREAD COUNT 10 BLOCK 5000 STREAMS orders $` |
| **ساخت گروه** | `XGROUP CREATE` | `XGROUP CREATE orders processors $ MKSTREAM` |
| **خواندن در گروه** | `XREADGROUP` | `XREADGROUP GROUP processors worker-1 COUNT 5 STREAMS orders >` |
| **تأیید پردازش** | `XACK` | `XACK orders processors 1715985432000-0` |
| **مشاهده PEL** | `XPENDING` | `XPENDING orders processors - + 10` |
| **محدودسازی حجم** | `XTRIM` | `XTRIM orders MAXLEN ~ 100000` |

> نکته: در `XREAD`، علامت `$` یعنی «از آخرین پیام موجود به بعد» و در `XREADGROUP`، علامت `>` یعنی «پیام‌های جدیدی که هنوز به هیچ مصرف‌کننده‌ای در گروه تحویل داده نشده‌اند».

---

### ☕ پیاده‌سازی در جاوا

#### 🧩 روش اول: Jedis (کلاینت خام)

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```java
import redis.clients.jedis.JedisPooled;
import redis.clients.jedis.StreamEntryID;
import redis.clients.jedis.params.XAddParams;

// تولید پیام
JedisPooled jedis = new JedisPooled("localhost", 6379);
Map<String, String> fields = Map.of("orderId", "ORD-001", "amount", "99.99");
StreamEntryID id = jedis.xadd("orders", XAddParams.xAddParams(), fields);
// یا برای ID خودکار:
// StreamEntryID id = jedis.xadd("orders", StreamEntryID.NEW_ENTRY, fields);
```

</div>


<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```java
// مصرف در گروه
import redis.clients.jedis.params.XReadGroupParams;
import redis.clients.jedis.resps.StreamEntry;

Map<String, StreamEntryID> offsets = Map.of("orders", StreamEntryID.XREADGROUP_UNDELIVERED_ENTRY);
List<Map.Entry<String, List<StreamEntry>>> result = jedis.xreadGroup(
    "processors", "worker-1",
    XReadGroupParams.xReadGroupParams().count(5).block(3000),
    offsets
);
for (var stream : result) {
    for (StreamEntry entry : stream.getValue()) {
        // پردازش entry.getFields()
        jedis.xack("orders", "processors", entry.getID()); // تأیید پس از پردازش موفق
    }
}
```

</div>



#### 🍃 روش دوم: Spring Data Redis (توصیه‌شده برای Spring Boot)

Spring Data Redis دو انتزاع اصلی دارد: **`opsForStream()`** برای عملیات مستقیم و **`StreamMessageListenerContainer`** برای گوش دادن پس‌زمینه‌ای به پیام‌ها.

**پیکربندی و مصرف‌کننده:**

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```java
@Configuration
public class StreamsConfig {
    static final String STREAM = "orders";
    static final String GROUP = "order-workers";

    @Bean(initMethod = "start", destroyMethod = "stop")
    StreamMessageListenerContainer<String, MapRecord<String, String, String>> streamContainer(
            RedisConnectionFactory connectionFactory) {
        var options = StreamMessageListenerContainerOptions.builder()
                .pollTimeout(Duration.ofSeconds(1))
                .build();
        return StreamMessageListenerContainer.create(connectionFactory, options);
    }

    @Bean
    Subscription orderSubscription(
            StreamMessageListenerContainer<String, MapRecord<String, String, String>> container,
            StringRedisTemplate template) {
        try {
            template.opsForStream().createGroup(STREAM, ReadOffset.from("0"), GROUP);
        } catch (Exception ignored) { /* گروه از قبل وجود دارد */ }

        return container.receive(
                Consumer.from(GROUP, "worker-1"),
                StreamOffset.create(STREAM, ReadOffset.lastConsumed()),
                message -> {
                    process(message.getValue());
                    template.opsForStream().acknowledge(STREAM, GROUP, message.getId());
                }
        );
    }
}
```

</div>



**تولید پیام با `opsForStream()`:**

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```java
template.opsForStream().add(
    StreamRecords.string(Map.of("orderId", "ORD-002")).withStreamKey("orders")
);
```

</div>



---

### 🛡️ قابلیت اطمینان: PEL و Claim کردن پیام‌های سرگردان

اگر مصرف‌کننده‌ای قبل از `XACK` از کار بیفتد، پیام در PEL گروه باقی می‌ماند. با `XPENDING` می‌توانید آن‌ها را ببینید و با `XCLAIM` (یا `XAUTOCLAIM`) به مصرف‌کننده سالم منتقل کنید.

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```java
// مشاهده پیام‌های در انتظار
PendingMessages pending = template.opsForStream()
        .pending(STREAM, GROUP, Range.unbounded(), 10);

// تصاحب پیام‌هایی که بیش از ۱ دقیقه بلاک مانده‌اند
for (PendingMessage entry : pending) {
    var claimed = template.opsForStream()
            .claim(STREAM, GROUP, "rescuer", Duration.ofMinutes(1), entry.getId());
    // پردازش و سپس acknowledge
}
```

</div>



---

### 📌 نکات عملی و محدودیت‌ها

- **کنترل حافظه**: Streams در حافظه ذخیره می‌شود. برای جلوگیری از رشد بی‌رویه، از `XTRIM MAXLEN ~ N` یا پارامترهای `stream-node-max-entries` در `redis.conf` استفاده کنید.
- **مقیاس‌پذیری**: در Redis Cluster، هر Stream در یک slot قرار می‌گیرد؛ برای توزیع بار می‌توانید از sharding سمت کلاینت استفاده کنید.
- **تحویل حداقل یک‌بار (At-least-once)**: به دلیل مکانیزم PEL و ACK، Streams تحویل **حداقل یک‌بار** را تضمین می‌کند. این یعنی ممکن است در موارد نادر (مثلاً وقتی پیامی دوباره claim می‌شود) یک پیام **بیش از یک‌بار** پردازش شود. بنابراین مصرف‌کننده باید **idempotent** طراحی شود.
- **مقایسه با Pub/Sub**: Pub/Sub آتش‌به‌اختیار است و پیام‌ها را دور می‌ریزد؛ Streams پایدار است، ذخیره می‌شود، قابل بازپخش (replay) است و چندین گروه مصرف‌کننده می‌توانند به‌طور مستقل آن را بخوانند.
</div>

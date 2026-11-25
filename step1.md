

<!-- SOURCE: 1.md -->
# File: 1.md

Working with JSON in Java EE (now Jakarta EE) involves a few key approaches. The ecosystem has evolved from needing third-party libraries to having powerful, standardized APIs built directly into the platform.

Here’s a breakdown of the main options, how they differ, and how to use them.

### 🧭 The Landscape: Your Options for JSON in Java EE

There are two main paths you can take, depending on the version of Java/Jakarta EE you are using and your specific needs.

1.  **Standard APIs (Recommended for modern Java EE/Jakarta EE 8+):**
    *   **JSON-B (JSON Binding):** A high-level API for directly mapping Java objects to JSON and back (serialization/deserialization). It's the standard and is integrated into JAX-RS.
    *   **JSON-P (JSON Processing):** A lower-level API for parsing, generating, and manipulating JSON data as a tree (object model) or a stream. It's useful for dynamic or generic JSON structures.

2.  **Third-Party Libraries:**
    *   **Jackson:** A very popular, feature-rich library that serves as the default JSON provider for many Java EE application servers (like JBoss/WildFly).
    *   **Gson:** A simple and widely-used library from Google, particularly good for straightforward serialization and deserialization.

The Servlet API itself does not have built-in JSON support. To use JSON in a plain servlet, you pair it with one of the libraries mentioned above.

### 🚀 Using the Standard Java EE APIs (JSON-B & JSON-P)

These APIs are part of the Jakarta EE specification (formerly Java EE). In a full application server, the implementations are already provided. On a plain servlet container or Java SE, you would need to add an implementation like Eclipse Yasson (for JSON-B) or Eclipse Parsson (for JSON-P).

#### JSON-B: The Object Mapper

JSON-B is the easiest way to work with JSON when you have a fixed, typed Java object.

**Example: Serialize and Deserialize a `User` object**

```java
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;

public class User {
    public String name;
    public int age;
    // A public no-arg constructor is required for deserialization
    public User() {}
}

public class JsonBExample {
    public static void main(String[] args) {
        User user = new User();
        user.name = "Ada";
        user.age = 36;

        // Create a Jsonb instance (it's thread-safe and should be reused)
        try (Jsonb jsonb = JsonbBuilder.create()) {
            // Serialize: Object -> JSON String
            String json = jsonb.toJson(user);
            System.out.println(json); // {"name":"Ada","age":36}

            // Deserialize: JSON String -> Object
            User parsedUser = jsonb.fromJson("{\"name\":\"Ada\",\"age\":36}", User.class);
            System.out.println(parsedUser.name); // Ada
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```
This example is based on the standard usage pattern for JSON-B. You can customize the mapping with annotations like `@JsonbProperty("full_name")` to rename fields or `@JsonbTransient` to exclude them.

#### JSON-P: The Tree and Stream Processor

JSON-P is more flexible for cases where you don't have a corresponding Java class or need to manipulate the JSON structure directly.

**Example: Build and Parse a JSON Object with the Object Model (Tree)**

```java
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import java.io.StringReader;

public class JsonPExample {
    public static void main(String[] args) {
        // Build a JSON object
        JsonObjectBuilder builder = Json.createObjectBuilder()
            .add("firstName", "Michael")
            .add("lastName", "Scott");
        JsonObject jsonObject = builder.build();
        String jsonString = jsonObject.toString();
        System.out.println(jsonString); // {"firstName":"Michael","lastName":"Scott"}

        // Parse a JSON string
        try (var reader = Json.createReader(new StringReader(jsonString))) {
            JsonObject parsedObj = reader.readObject();
            String firstName = parsedObj.getString("firstName");
            System.out.println(firstName); // Michael
        }
    }
}
```
The object model for JSON-P is similar to the DOM API for XML. For very large JSON documents, JSON-P also offers a streaming API (similar to StAX for XML) to process data without loading it all into memory.

### 🔌 Using Third-Party Libraries: Jackson & Gson

These libraries are often used in plain servlets or in environments where you want more control.

#### Jackson

Jackson is known for its performance and extensive feature set.

**Example: Handling JSON in a Servlet with Jackson**

```java
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.Map;

public class UserServlet extends HttpServlet {
    // ObjectMapper is thread-safe and should be reused
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Map<?, ?> body;
        try {
            // Read JSON from the request body
            body = MAPPER.readValue(req.getInputStream(), Map.class);
        } catch (IOException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid JSON");
            return;
        }

        // Set response headers and write JSON
        resp.setStatus(HttpServletResponse.SC_CREATED);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        MAPPER.writeValue(resp.getOutputStream(), Map.of("received", body.size()));
    }
}
```
This pattern is recommended for servlets: read directly from the request's `InputStream` and write to the response's `OutputStream`, letting Jackson handle the encoding.

#### Gson

Gson is often praised for its simplicity.

**Example: Returning a JSON Response from a Servlet with Gson**

```java
import com.google.gson.Gson;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;

public class EmployeeServlet extends HttpServlet {
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Employee employee = new Employee(1, "Karan", "IT", 5000);
        String employeeJsonString = this.gson.toJson(employee);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        out.print(employeeJsonString);
        out.flush();
    }
}
```
This is a standard, straightforward approach for sending a JSON response from a servlet using Gson.

### ✨ Modern Best Practices: Let JAX-RS Do the Work

If you are building RESTful web services with JAX-RS (the standard REST API for Java EE), you rarely need to manually call a JSON library. The runtime handles serialization and deserialization for you.

By declaring the media types, you can use your Java objects directly as method parameters and return types.

```java
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/users")
public class UserResource {

    @POST
    @Consumes(MediaType.APPLICATION_JSON) // Expect JSON in the request body
    @Produces(MediaType.APPLICATION_JSON) // Return JSON in the response
    public Response create(User user) {
        // The 'user' object is automatically deserialized from the request JSON
        // The returned object will be automatically serialized to JSON
        return Response.status(Response.Status.CREATED).entity(user).build();
    }
}
```
With this approach, the JAX-RS runtime (using a provider like JSON-B or Jackson) transparently handles the conversion. Malformed JSON is typically rejected with a 400 Bad Request before your method is even called.

### 💎 Key Takeaways and Common Pitfalls

*   **Choose the right tool:** Use **JSON-B** (or Jackson/Gson) for mapping to typed objects. Use **JSON-P** for dynamic or streaming processing. In **JAX-RS**, rely on automatic content negotiation.
*   **Reuse instances:** Creating `Jsonb`, `ObjectMapper`, or `Gson` instances is expensive. Create them once (e.g., as a `static final` field or a CDI bean) and reuse them.
*   **Ensure a default constructor:** Classes deserialized by JSON-B or Jackson generally need a public no-argument constructor.
*   **Mind the namespace:** Jakarta EE 9+ uses `jakarta.*` packages (e.g., `jakarta.json`), replacing the older `javax.*` namespaces. Ensure your imports match your server version.
*   **Handle errors gracefully:** When parsing JSON in a servlet, catch `IOException` and return a `400 Bad Request` for invalid input instead of letting a `500 Server Error` occur.

Hopefully, this gives you a solid foundation for working with JSON in Java EE. If you want to dive deeper into a specific API or see a particular use case in more detail, feel free to ask.



<!-- SOURCE: 2.md -->
# File: 2.md

<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">



# 🎓 جلسه ۲ — کوانتیفایرها و کلاس‌های کاراکتری (عمیق)

این جلسه **قلب اعتبارسنجی**‌ه. اگه این رو کامل بفهمی، ۸۰٪ اعتبارسنجی‌ها رو راحت می‌نویسی.

## ۲.۱ کوانتیفایرها — لیست کامل

| کوانتیفایر | معنی | مثال | match می‌شه با |
|-----------|------|------|---------------|
| `?` | ۰ یا ۱ | `colou?r` | `color`, `colour` |
| `*` | ۰ یا بیشتر | `ab*` | `a`, `ab`, `abb`, `abbb` |
| `+` | ۱ یا بیشتر | `ab+` | `ab`, `abb` (نه `a`) |
| `{n}` | دقیقاً n | `\d{3}` | `123` (نه `12`, نه `1234`) |
| `{n,}` | حداقل n | `\d{3,}` | `123`, `1234`, `12345` |
| `{n,m}` | بین n تا m | `\d{3,5}` | `123`, `1234`, `12345` |

## ۲.۲ سه حالت کوانتیفایر — Greedy / Lazy / Possessive

این بخشی‌ه که خیلی‌ها نمی‌دونن و بعداً باگ می‌سازه:

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```java
String text = "<a><b><c>";
```

</div>


| الگو | نتیجه | توضیح |
|------|-------|-------|
| `<.+>` (greedy) | `<a><b><c>` | بیشترین مقدار ممکن |
| `<.+?>` (lazy) | `<a>`, `<b>`, `<c>` | کمترین مقدار ممکن |
| `<.++>` (possessive) | `<a><b><c>` (بدون backtracking) | مثل greedy ولی بدون عقب‌گرد |

**پیش‌فرض `greedy` هست.** برای اعتبارسنجی معمولاً همین خوبه. ولی برای **استخراج** (مثلاً از HTML یا لاگ) معمولاً `lazy` (`?`) می‌خوای.

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```java
// مثال واقعی: استخراج محتوای تگ <b>
String html = "<b>سلام</b> و <b>خداحافظ</b>";
Pattern.compile("<b>(.*?)</b>");   // lazy → دو تا match جدا
Pattern.compile("<b>(.*)</b>");    // greedy → یک match بزرگ
```

</div>


## ۲.۳ کلاس‌های کاراکتری — لیست کامل

| نماد | معنی | معادل |
|------|------|-------|
| `\d` | یک رقم | `[0-9]` |
| `\D` | غیر رقم | `[^0-9]` |
| `\w` | word (حرف/رقم/_ ) | `[a-zA-Z0-9_]` (پیش‌فرض ASCII) |
| `\W` | غیر word | `[^a-zA-Z0-9_]` |
| `\s` | فضای خالی (space, tab, \n...) | `[ \t\n\x0B\f\r]` |
| `\S` | غیر فضای خالی | — |
| `.` | **هر** کاراکتری به‌جز newline | — |

## ۲.۴ ⚠️ تله‌های رایج (این‌ها رو حفظ کن)

**تله ۱: `.` با `matches()` و رشتهٔ خالی**

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```java
"".matches(".*");   // true ✅ (صفر کاراکتر)
"".matches(".+");   // false ✅ (حداقل یک کاراکتر)
```

</div>


**تله ۲: `.` شامل newline نمی‌شه**

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```java
"a\nb".matches("a.b");   // false ❌
"a\nb".matches("a(?s).b"); // true (یا با Pattern.DOTALL)
```

</div>


**تله ۳: `-` داخل `[]`**

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```java
"[a-z]"    // بازه: هر حرف کوچک ✅
"[az-]"    // حرف a، حرف z، یا دش (چون آخر `[]` هست)
"[-az]"    // همون بالا (چون اول `[]` هست)
"[a\\-z]"  // حرف a، دش، حرف z (escape شده)
```

</div>

دش در **وسط** `[]` معنی «بازه» می‌ده. این دقیقاً همون چیزیه که توی تمرین ۴ جلسهٔ قبل خوردی.

**تله ۴: `^` داخل `[]` معنی نگاتیو داره**

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```java
"[abc]"    // a یا b یا c
"[^abc]"   // هر چیزی به‌جز a, b, c (حداقل یک کاراکتر)
```

</div>


**تله ۵: `\w` حروف فارسی رو نمی‌گیره (پیش‌فرض)**

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```java
"سلام".matches("\\w+");   // false ❌
```

</div>

اگه لازم داری Unicode هم پشتیبانی بشه، `Pattern.UNICODE_CHARACTER_CLASS` یا `\p{L}` لازمه (جلسهٔ ۳).

## ۲.۵ نکته‌ای که خیلی کمک می‌کنه: «چی مجاز نیست؟» رو بپرس

برای اعتبارسنجی، دو طرز فکر:

**الف) «چه چیزایی مجازه»** (whitelist — پیشنهادی):

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```java
"^[a-z]+$"   // فقط حروف کوچک
```

</div>


**ب) «چه چیزایی مجاز نیست»** (blacklist — خطرناک):

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```java
"^[^0-9]+$"  // هر چیزی به‌جز رقم ← شامل فاصله، ایموجی، ...
```

</div>


همیشه از **الف** استفاده کن. blacklist نشت داره.

---

## 📝 تمرین‌های جلسه ۲ (۶ تا — سخت‌تر از قبل)

برای هرکدوم **الگو + یه تست کوچیک** بنویس.

### تمرین ۱ — کوانتیفایر `*` و `+` (مفهومی، ولی بنویس)
این‌ها true هستن یا false؟ چرا؟

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```java
"".matches("a*")          // ?
"".matches("a+")          // ?
"aaa".matches("a{2,}")    // ?
"aa".matches("a{2,}")     // ?
"a".matches("a{2,}")      // ?
"abcdef".matches("a.*f")  // ?
"af".matches("a.*f")      // ?
```

</div>


### تمرین ۲ — کد ملی ۱۰ رقمی واقعی
کد ملی ایران: **دقیقاً ۱۰ رقم**. سه رقم اول **نباید همه صفر باشن** (مثل `0001234567` نامعتبره).

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```
"1234567890"  → true
"0001234567"  → false
"123456789"   → false (۹ رقم)
"12345678901" → false (۱۱ رقم)
```

</div>


**راهنما:** از lookahead منفی استفاده کن: `(?!...)` — در جلسهٔ ۳ مفصل می‌گم ولی توی این یه مورد می‌تونی امتحان کنی. یا از یه روش دیگه هم می‌شه (بدون lookahead) — هر دو درسته.

### تمرین ۳ — کد ملی «با چک‌سام» (چالشی)
فقط با Regex **نمی‌شه** چک‌سام کد ملی رو حساب کرد. پس:
1. اول Regex بنویس که فقط ۱۰ رقم و سه رقم اول غیرصفر رو قبول کنه
2. بعد یه متد جاوا بنویس که چک‌سام واقعی رو چک کنه (الگوریتم معروف: مجموع رقم‌ها × وزن، mod 11)

این تمرین بهت یاد می‌ده **مرز Regex کجاست** — بخش مهمی از اعتبارسنجی درست.

### تمرین ۴ — ایمیل (نسخهٔ کاربردی، نه استاندارد کامل)
ایمیل: `username@domain.tld`
- username: حروف انگلیسی، رقم، `.`، `_`، `-` (حداقل ۱ کاراکتر)
- `@`
- domain: حروف، رقم، `-` (حداقل ۱ کاراکتر)
- `.`
- tld: حروف (حداقل ۲ کاراکتر)

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```
"ali@gmail.com"        → true
"ali.2000@test.co.uk"  → true
"ali@"                 → false
"@gmail.com"           → false
"ali@gmail"            → false (tld نداره)
```

</div>


### تمرین ۵ — این الگو چه اشکالی داره؟

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```java
String email = "^.+@.+\\..+$";
```

</div>

چرا این برای اعتبارسنجی **بد** هست؟ حداقل ۲ تا مشکل بگو. (نکته: از بحث blacklist vs whitelist کمک بگیر.)

### تمرین ۶ — URL ساده
URL که با `http://` یا `https://` شروع می‌شه، بعد domain (حروف/رقم/`-`/`.`، حداقل ۱ کاراکتر)، و اختیاراً یه `/path` با هر کاراکتری به‌جز فاصله.

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```
"https://google.com"           → true
"http://test.org/path/to/page" → true
"ftp://google.com"             → false
"google.com"                   → false
```

</div>


---

## 💡 راهنماها

| چی می‌خوای | چطور |
|-----------|------|
| دقیقاً ۱۰ رقم | `\d{10}` |
| سه رقم اول غیر از ۰۰۰ | `(?!000)` یا `[1-9]\d{2}` برای بخش اول + `\d{7}` |
| حروف+رقم+بعضی نمادها | `[A-Za-z0-9._-]` |
| اختیاری | `?` |
| `(?!...)` = «از اینجا به بعد نباید شروع بشه با...» | جلسهٔ ۳ کامل می‌گم |
| tld حداقل ۲ حرف | `[a-zA-Z]{2,}` |

---

هر ۶ تا رو بنویس. **مخصوصاً تمرین ۵ رو جدی بگیر** — این طرز فکر (چرا این الگو بد است؟) از هر تکنیک Regex مهم‌تره.

بعد جلسه ۳ می‌ریم **lookahead/lookbehind** که برای اعتبارسنجی حرفه‌ای حیاتیه. 🚀
</div>



<!-- SOURCE: 2.md -->
# File: 2.md

<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">

حتماً. بر اساس متن فایل شما، یک **آزمون جامع و شبیه آزمون استخدامی و مصاحبه فنی Java Backend** آماده می‌کنم.

مطالب آزمون از موضوعات موجود در متن تهیه می‌شود؛ از جمله:

- JSON در Java EE / Jakarta EE
- JSON-B و JSON-P
- Jackson و Gson
- JAX-RS و REST
- Regex، Quantifierها و Greedy/Lazy/Possessive
- Java Threads و Thread Lifecycle
- Race Condition و Synchronization
- `synchronized`، `volatile` و `AtomicInteger`
- Deadlock
- `ExecutorService`، `Callable`، `Future`
- اصول برنامه‌نویسی Concurrent و Best Practices

آزمون ترکیبی از **تست چهارگزینه‌ای، خروجی کد، تشخیص خطا، سوالات مفهومی و سوالات سناریومحور مصاحبه** خواهد بود.

# **آزمون جامع Java Backend — نسخه تمرینی**

**سطح:** متوسط تا پیشرفته

40 سؤال

**زمان پیشنهادی:** 60 دقیقه\
**نمره:** 100

سؤالات چهارگزینه‌ای، تحلیل کد و سناریوهای مصاحبه

## **بخش اول — JSON در Java EE / Jakarta EE**

سؤالات ۱ تا ۸ | هر سؤال ۲ امتیاز

### **سؤال ۱**

کدام API برای تبدیل مستقیم یک Java Object به JSON و بالعکس مناسب‌تر است؟

- A) JSON-P
- B) JSON-B
- C) Servlet API
- D) JDBC

### **سؤال ۲**

مهم‌ترین تفاوت JSON-B و JSON-P چیست؟

- A) JSON-B فقط برای خواندن JSON است.
- B) JSON-P فقط برای تبدیل Object به String استفاده می‌شود.
- C) JSON-B برای mapping بین Java Object و JSON و JSON-P برای پردازش ساختار JSON استفاده می‌شود.
- D) هیچ تفاوتی ندارند.

### **سؤال ۳**

در JSON-B برای تغییر نام یک فیلد Java هنگام تبدیل به JSON از کدام Annotation استفاده می‌کنیم؟

- A) `@JsonIgnore`
- B) `@JsonbProperty`
- C) `@JsonProperty`
- D) `@JsonbTransient`

### **سؤال ۴**

Annotation `@JsonbTransient` چه کاری انجام می‌دهد؟

- A) فیلد را به JSON تبدیل می‌کند.
- B) فیلد را از Mapping حذف می‌کند.
- C) فیلد را فقط در صورت Null بودن حذف می‌کند.
- D) فیلد را به String تبدیل می‌کند.

### **سؤال ۵**

در JSON-P کدام API برای ساخت یک JSON Object استفاده می‌شود؟

- A) `JsonObjectBuilder`
- B) `JsonObjectMapper`
- C) `JsonConverter`
- D) `JsonSerializer`

### **سؤال ۶**

کدام گزینه درباره JSON-P صحیح است؟

- A) فقط برای JSONهای کوچک مناسب است.
- B) هیچ قابلیت Streaming ندارد.
- C) می‌تواند از Object Model و Streaming برای پردازش JSON استفاده کند.
- D) فقط برای Servletها کاربرد دارد.

### **سؤال ۷**

در Jakarta EE 9 به بعد، Namespace جدید APIها چیست؟

- A) `java.*`
- B) `javax.*`
- C) `jakarta.*`
- D) `org.java.*`

### **سؤال ۸**

در یک Servlet ساده، برای دریافت و ارسال JSON چه چیزی صحیح است؟

- A) Servlet API به‌صورت داخلی JSON را تبدیل می‌کند.
- B) باید از کتابخانه‌ای مانند Jackson یا Gson استفاده کرد.
- C) فقط JSON-B قابل استفاده است.
- D) استفاده از JSON در Servlet امکان‌پذیر نیست.

## **بخش دوم — Jackson، Gson و JAX-RS**

سؤالات ۹ تا ۱۵ | هر سؤال ۲ امتیاز

### **سؤال ۹**

کدام کلاس در Jackson برای Serialization و Deserialization استفاده می‌شود؟

- A) `JsonConverter`
- B) `ObjectMapper`
- C) `JsonBuilder`
- D) `JsonFactoryBean`

### **سؤال ۱۰**

چرا در Jackson بهتر است `ObjectMapper` را مجدداً استفاده کنیم؟

- A) چون فقط یک بار قابل اجراست.
- B) چون ساخت آن هزینه‌بر است و Thread-safe بوده و می‌توان آن را reuse کرد.
- C) چون در هر درخواست باید تغییر کند.
- D) چون Serialization فقط با یک Mapper انجام می‌شود.

### **سؤال ۱۱**

در کد زیر چه اتفاقی می‌افتد؟

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```
Map<?, ?> body =
    MAPPER.readValue(req.getInputStream(), Map.class);
```

</div>


- A) JSON خروجی Response تولید می‌شود.
- B) JSON ورودی به یک Map تبدیل می‌شود.
- C) Map به JSON تبدیل می‌شود.
- D) Request حذف می‌شود.

### **سؤال ۱۲**

اگر JSON ورودی نامعتبر باشد، در مثال Servlet با Jackson چه پاسخ مناسبی باید به Client داده شود؟

- A) `200 OK`
- B) `201 Created`
- C) `400 Bad Request`
- D) `500 Internal Server Error` در همه شرایط

### **سؤال ۱۳**

در Gson، کدام متد برای تبدیل Java Object به JSON String استفاده می‌شود؟

- A) `fromJson()`
- B) `parse()`
- C) `toJson()`
- D) `readValue()`

### **سؤال ۱۴**

در JAX-RS، Annotation `@Consumes(MediaType.APPLICATION_JSON)` چه مفهومی دارد؟

- A) نوع داده‌ای که Endpoint در Response برمی‌گرداند.
- B) نوع محتوایی که Endpoint دریافت می‌کند.
- C) Endpoint فقط JSON را در Database ذخیره می‌کند.
- D) JSON را حذف می‌کند.

### **سؤال ۱۵**

در JAX-RS اگر یک متد Resource یک Java Object برگرداند و `@Produces(MediaType.APPLICATION_JSON)` داشته باشد، چه اتفاقی می‌افتد؟

- A) باید حتماً Object را دستی به String تبدیل کنیم.
- B) Runtime می‌تواند Object را به JSON تبدیل کند.
- C) خروجی همیشه XML خواهد بود.
- D) درخواست رد می‌شود.

## **بخش سوم — Regex و Quantifierها**

سؤالات ۱۶ تا ۲۵ | هر سؤال ۲ امتیاز

### **سؤال ۱۶**

Quantifier `?` چه مفهومی دارد؟

- A) حداقل یک بار
- B) صفر یا بیشتر
- C) صفر یا یک بار
- D) دقیقاً دو بار

### **سؤال ۱۷**

الگوی زیر چه رشته‌هایی را Match می‌کند؟

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```
ab*
```

</div>


- A) فقط `ab`
- B) `a` و `ab` و `abb` و `abbb` و ...
- C) فقط `abb`
- D) `a` یا `b`

### **سؤال ۱۸**

تفاوت `+` و `*` چیست؟

- A) هیچ تفاوتی ندارند.
- B) `+` صفر یا بیشتر و `*` یک یا بیشتر است.
- C) `+` یک یا بیشتر و `*` صفر یا بیشتر است.
- D) `+` دقیقاً یک بار.

### **سؤال ۱۹**

خروجی Match برای Regex زیر کدام است؟

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```
\d{3}
```

</div>


- A) فقط 2 رقم
- B) حداقل 3 رقم
- C) دقیقاً 3 رقم
- D) حداکثر 3 رقم

### **سؤال ۲۰**

Regex زیر چه چیزی را Match می‌کند؟

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```
\d{3,}
```

</div>


- A) دقیقاً 3 رقم
- B) حداقل 3 رقم
- C) بین 3 و 5 رقم
- D) حداکثر 3 رقم

### **سؤال ۲۱**

Regex زیر کدام رشته‌ها را Match می‌کند؟

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```
\d{3,5}
```

</div>


- A) 2 تا 6 رقم
- B) حداقل 5 رقم
- C) بین 3 تا 5 رقم
- D) فقط 3 رقم

### **سؤال ۲۲**

تفاوت Greedy و Lazy چیست؟

- A) Greedy کوتاه‌ترین Match را انتخاب می‌کند.
- B) Lazy بلندترین Match را انتخاب می‌کند.
- C) Greedy سعی می‌کند بیشترین Match ممکن و Lazy کمترین Match ممکن را پیدا کند.
- D) هیچ تفاوتی ندارند.

### **سؤال ۲۳**

در Regex، کدام علامت Quantifier را Lazy می‌کند؟

- A) `+`
- B) `?`
- C) `*`
- D) `^`

### **سؤال ۲۴**

Possessive Quantifier چه ویژگی‌ای دارد؟

- A) Match را به عقب برمی‌گرداند و کوتاه می‌کند.
- B) هیچ‌وقت Match نمی‌کند.
- C) Match گرفته‌شده را پس نمی‌دهد و Backtracking را در آن بخش متوقف می‌کند.
- D) فقط یک کاراکتر را Match می‌کند.

### **سؤال ۲۵**

کدام گزینه یک Quantifier با تعداد دقیق 4 بار است؟

- A) `+`
- B) `*`
- C) `{4}`
- D) `{4,}`

## **بخش چهارم — Thread و Multithreading در Java**

سؤالات ۲۶ تا ۳۵ | هر سؤال ۲ امتیاز

### **سؤال ۲۶**

Thread چیست؟

- A) یک Database
- B) کوچک‌ترین واحد اجرای مستقل در یک Process
- C) یک کلاس برای ذخیره اطلاعات
- D) یک نوع Exception

### **سؤال ۲۷**

در کد زیر چه تفاوتی بین `start()` و `run()` وجود دارد؟

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```
Thread t = new Thread(() -> {
    System.out.println("Hello");
});

t.start();
```

</div>


- A) `start()` فقط متد `run()` را صدا می‌زند.
- B) `start()` یک Thread جدید را اجرا می‌کند، اما اجرای مستقیم `run()` روی Thread جاری است.
- C) هر دو یک Thread جدید ایجاد می‌کنند.
- D) `run()` همیشه سریع‌تر است.

### **سؤال ۲۸**

چرا معمولاً Implement کردن `Runnable` نسبت به Extend کردن `Thread` ترجیح داده می‌شود؟

- A) چون `Runnable` قابلیت اجرای چند بار ندارد.
- B) چون Java از Multiple Inheritance پشتیبانی می‌کند.
- C) چون محدودیت ارث‌بری از یک کلاس را ایجاد نمی‌کند و Task را از Thread جدا می‌کند.
- D) چون `Runnable` همیشه Thread-safe است.

### **سؤال ۲۹**

کدام ترتیب Lifecycle یک Thread صحیح است؟

- A) `NEW → TERMINATED → RUNNABLE`
- B) `NEW → RUNNABLE → BLOCKED/WAITING/TIMED_WAITING → TERMINATED`
- C) `RUNNABLE → NEW → WAITING`
- D) `WAITING → NEW → TERMINATED`

### **سؤال ۳۰**

متد `join()` چه کاری انجام می‌دهد؟

- A) Thread را متوقف و حذف می‌کند.
- B) Thread جاری منتظر اتمام Thread دیگری می‌ماند.
- C) Thread جدید ایجاد می‌کند.
- D) Lock را آزاد می‌کند.

### **سؤال ۳۱**

چرا `count++` یک عملیات Atomic نیست؟

- A) چون متغیر باید `final` باشد.
- B) چون شامل خواندن، افزایش و نوشتن است و ممکن است بین Threadها تداخل ایجاد شود.
- C) چون فقط در Main Thread کار می‌کند.
- D) چون `++` در Java وجود ندارد.

### **سؤال ۳۲**

Race Condition چیست؟

- A) خطای Syntax
- B) زمانی که نتیجه اجرای برنامه به ترتیب یا زمان‌بندی اجرای Threadها وابسته می‌شود.
- C) زمانی که یک Thread اجرا نمی‌شود.
- D) خطای اتصال به Database.

### **سؤال ۳۳**

`synchronized` چه کاری انجام می‌دهد؟

- A) اجرای تمام Threadها را متوقف می‌کند.
- B) دسترسی هم‌زمان به بخش محافظت‌شده را با استفاده از Lock کنترل می‌کند.
- C) یک Thread جدید ایجاد می‌کند.
- D) فقط مقدار متغیر را به‌روز می‌کند.

### **سؤال ۳۴**

کدام گزینه درباره `volatile` صحیح است؟

- A) عملیات `count++` را Atomic می‌کند.
- B) Visibility تغییرات را بین Threadها تضمین می‌کند، اما Atomicity عملیات مرکب را تضمین نمی‌کند.
- C) جایگزین تمام Lockها است.
- D) باعث ایجاد Thread جدید می‌شود.

### **سؤال ۳۵**

برای یک Counter ساده که نیاز به Increment اتمیک دارد، کدام گزینه مناسب‌تر است؟

- A) `String`
- B) `volatile int`
- C) `AtomicInteger`
- D) `ArrayList<Integer>`

## **بخش پنجم — Deadlock، ExecutorService و Concurrent Best Practices**

سؤالات ۳۶ تا ۴۰ | هر سؤال ۳ امتیاز

### **سؤال ۳۶**

Deadlock چه زمانی اتفاق می‌افتد؟

- A) وقتی یک Thread به‌سرعت اجرا می‌شود.
- B) وقتی دو یا چند Thread منتظر Lockهایی هستند که توسط Threadهای دیگر نگه داشته شده‌اند و هیچ‌کدام پیش نمی‌روند.
- C) وقتی Thread به پایان می‌رسد.
- D) وقتی از `Runnable` استفاده می‌کنیم.

### **سؤال ۳۷**

بهترین راه جلوگیری از Deadlock در مثال دو Lock چیست؟

- A) استفاده از Lockهای بیشتر
- B) استفاده از `sleep()` بیشتر
- C) گرفتن Lockها همیشه در یک ترتیب ثابت
- D) حذف `join()`

### **سؤال ۳۸**

چرا استفاده از `ExecutorService` نسبت به ایجاد Thread جدید برای هر Task بهتر است؟

- A) چون Threadها را حذف می‌کند.
- B) چون مدیریت Threadها و Reuse آن‌ها را انجام می‌دهد و هزینه ایجاد Threadهای مکرر را کاهش می‌دهد.
- C) چون تمام Taskها را هم‌زمان اجرا می‌کند.
- D) چون هیچ محدودیتی در تعداد Thread ندارد.

### **سؤال ۳۹**

تفاوت `Runnable` و `Callable` چیست؟

- A) `Runnable` مقدار برمی‌گرداند، اما `Callable` نه.
- B) هر دو فقط `void` برمی‌گردانند.
- C) `Callable` می‌تواند نتیجه برگرداند و Exception پرتاب کند؛ `Runnable` نتیجه‌ای برنمی‌گرداند.
- D) هیچ تفاوتی ندارند.

### **سؤال ۴۰**

در کد زیر، `future.get()` چه رفتاری دارد؟

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```
Future<Integer> future = pool.submit(() -> {
    Thread.sleep(1000);
    return 42;
});

int result = future.get();
```

</div>


- A) نتیجه را بدون انتظار برمی‌گرداند.
- B) Thread را حذف می‌کند.
- C) در صورت آماده نبودن نتیجه، منتظر می‌ماند تا Task کامل شود.
- D) باعث Deadlock در تمام شرایط می‌شود.

## **بخش ششم — سؤالات سناریومحور و تحلیل کد**

سؤالات ۴۱ تا ۴۵ | هر سؤال ۳ امتیاز

### **سؤال ۴۱ — تحلیل Race Condition**

کد زیر را در نظر بگیر:

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```
class Counter {
    int count = 0;

    void increment() {
        count++;
    }
}
```

</div>


دو Thread هرکدام 100,000 بار متد `increment()` را اجرا می‌کنند.

کدام نتیجه محتمل است؟

- A) همیشه 200000
- B) همیشه 100000
- C) ممکن است عددی کمتر از 200000 باشد.
- D) برنامه حتماً Exception می‌دهد.

### **سؤال ۴۲ — انتخاب راهکار Thread-Safe**

برای اصلاح کد سؤال قبل، کدام گزینه صحیح‌تر است؟

- A) استفاده از `volatile int`
- B) استفاده از `synchronized` یا `AtomicInteger`
- C) استفاده از `String`
- D) حذف `join()`

### **سؤال ۴۳ — Thread Pool**

در کد زیر، پس از اجرای `shutdown()` چه اتفاقی می‌افتد؟

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```
ExecutorService pool = Executors.newFixedThreadPool(4);

pool.submit(() -> doWork());
pool.submit(() -> doWork());

pool.shutdown();
```

</div>


- A) Taskهای اجراشده فوراً متوقف می‌شوند.
- B) Thread Pool هیچ‌وقت بسته نمی‌شود.
- C) Taskهای قبلی اجازه تکمیل دارند، اما Task جدید پذیرفته نمی‌شود.
- D) تمام Threadها فوراً Kill می‌شوند.

### **سؤال ۴۴ — InterruptedException**

در کد زیر بهترین رفتار چیست؟

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```
try {
    Thread.sleep(1000);
} catch (InterruptedException e) {
    // What should we do?
}
```

</div>


- A) Exception را نادیده بگیریم.
- B) فقط `printStackTrace()` کنیم.
- C) وضعیت Interrupt را Restore کنیم یا Exception را به لایه بالاتر منتقل کنیم.
- D) Thread جدید بسازیم.

### **سؤال ۴۵ — انتخاب ابزار مناسب**

در یک پروژه Java، نیازهای زیر را داریم:

- پردازش تعداد زیادی Task
- جلوگیری از ایجاد Thread جدید برای هر درخواست
- دریافت نتیجه از Task
- امکان انتظار برای نتیجه

کدام ترکیب مناسب‌تر است؟

- A) `Thread` + `sleep()`
- B) `ExecutorService` + `Callable` + `Future`
- C) `volatile` + `String`
- D) `synchronized` + `System.out.println()`

# **نتیجه و ارزیابی آزمون**

## **مجموع: 100 امتیاز**

40 سؤال اصلی + 5 سؤال سناریویی

| نمره       | سطح شما                                    |
| ---------- | ------------------------------------------ |
| 90 تا 100  | عالی — آمادگی بسیار خوب برای مصاحبه        |
| 80 تا 89   | خوب — نقاط ضعف جزئی دارید                  |
| 70 تا 79   | متوسط رو به خوب — نیازمند مرور             |
| 60 تا 69   | متوسط — بعضی مفاهیم هنوز تثبیت نشده‌اند    |
| 40 تا 59   | نیاز به تمرین جدی                          |
| کمتر از 40 | پیشنهاد می‌شود مطالب را مجدداً مطالعه کنید |

## **نحوه شرکت در آزمون**

پیشنهاد من این است که فعلاً **کلید پاسخ را به شما نشان ندهم** تا آزمون واقعاً شبیه مصاحبه باشد.

جواب‌ها را به این شکل ارسال کن:

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">

```
1:B
2:C
3:B
4:B
5:A
...
45:B
```

</div>


می‌توانی پاسخ‌ها را حتی در چند مرحله ارسال کنی.

بعد از اینکه جواب‌ها را فرستادی، من:

- نمره از 100 محاسبه می‌کنم.
- تمام جواب‌های اشتباه را مشخص می‌کنم.
- پاسخ صحیح هر سؤال را توضیح می‌دهم.
- نکات مهم مصاحبه‌ای هر مبحث را توضیح می‌دهم.
- مشخص می‌کنم در کدام مبحث ضعف داری.
- در پایان، یک **آزمون شفاهی مصاحبه Java Backend** از نقاط ضعفت می‌گیرم.

اگر بخواهی، می‌توانیم همین آزمون را به شکل **مصاحبه تعاملی** هم برگزار کنیم؛ یعنی من یک سؤال بپرسم، تو جواب بدهی و سؤال بعدی بر اساس جواب تو مطرح شود.
</div>



<!-- SOURCE: 1.md -->
# File: 1.md

Java 8 was a landmark release that fundamentally changed how Java developers write code, introducing functional programming concepts to the language. For a core review, it's essential to understand its most impactful features, which primarily center around **Lambda Expressions, the Stream API, and a suite of new APIs** for modern application development.

### 📝 Lambda Expressions & Functional Interfaces
These are the foundation of Java 8's functional style.

*   **Lambda Expressions**: These allow you to treat functionality as a method argument or code as data. They provide a clear and concise way to represent a single method interface using an expression. The basic syntax is `(parameters) -> expression` or `(parameters) -> { statements; }`.
    ```java
    // Before Java 8: Anonymous inner class
    Runnable r = new Runnable() {
        @Override
        public void run() {
            System.out.println("Hello, Java 8!");
        }
    };

    // With Java 8 Lambda
    Runnable r = () -> System.out.println("Hello, Java 8!");
    ```
*   **Functional Interfaces**: A functional interface is any interface that contains **exactly one abstract method** (it can still have multiple default or static methods). This is the type that a lambda expression targets. The `@FunctionalInterface` annotation is optional but helps the compiler enforce the single-abstract-method rule.
    ```java
    @FunctionalInterface
    interface MathOperation {
        int operate(int a, int b);
    }

    MathOperation addition = (a, b) -> a + b; // Lambda targets the functional interface
    ```
*   **Method References**: A shorthand notation for a lambda expression that simply calls an existing method. It uses the `::` operator and makes code even more readable. There are four main types: static method reference (`Integer::parseInt`), instance method of a particular object (`System.out::println`), instance method of an arbitrary object of a type (`String::toUpperCase`), and constructor reference (`ArrayList::new`).

### 🌊 The Stream API
The Stream API provides a powerful, declarative way to process sequences of elements (like collections) with functional-style operations.

*   **Key Concepts**:
    *   **Declarative**: You specify *what* you want to do, not *how* to do it (unlike traditional loops).
    *   **Lazy Evaluation**: Intermediate operations (like `filter`, `map`) are not executed until a terminal operation (like `collect`, `forEach`) is called.
    *   **Parallel Processing**: Streams can easily leverage multiple cores with `.parallelStream()`.
*   **Common Operations**:
    *   **Intermediate Operations** (return a new Stream): `filter()`, `map()`, `flatMap()`, `distinct()`, `sorted()`, `limit()`, `skip()`.
    *   **Terminal Operations** (produce a result or side-effect): `forEach()`, `collect()`, `reduce()`, `count()`, `anyMatch()`, `findFirst()`.
    ```java
    List<String> names = Arrays.asList("Alice", "Bob", "Charlie", "David");

    // Imperative style (pre-Java 8)
    for (String name : names) {
        if (name.startsWith("A")) {
            System.out.println(name.toUpperCase());
        }
    }

    // Functional style with Stream API
    names.stream()
         .filter(name -> name.startsWith("A"))
         .map(String::toUpperCase)
         .forEach(System.out::println);
    ```

### 🛡️ Optional Class
`Optional<T>` is a container object that may or may not contain a non-null value. Its primary purpose is to provide a **clearer API signal** that a method might return no result, and to help avoid `NullPointerException`.

```java
// Instead of returning null, a method can return an Optional
public Optional<String> findUserById(int id) {
    // ... lookup logic
    return Optional.ofNullable(user); // Wraps a possibly null value
}

// Using the Optional
Optional<String> user = findUserById(123);
String name = user.orElse("Guest"); // Provides a default value if empty
user.ifPresent(System.out::println); // Executes only if a value is present
```

### ⏰ New Date and Time API (`java.time`)
This API was introduced to fix the design flaws of the old `java.util.Date` and `Calendar` classes. It is **immutable and thread-safe**, providing a fluent interface for date-time manipulation.

*   **Core Classes**:
    *   `LocalDate`, `LocalTime`, `LocalDateTime`: Represent date and/or time without a timezone.
    *   `ZonedDateTime`: Represents a date-time with a specific timezone.
    *   `Instant`: Represents a point in time (machine-readable timestamp).
    *   `Duration` and `Period`: Used to represent amounts of time.
    *   `DateTimeFormatter`: For parsing and formatting date-time objects.
    ```java
    LocalDate today = LocalDate.now();
    LocalDate nextWeek = today.plusWeeks(1);
    LocalTime now = LocalTime.of(14, 30);

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    String formattedDate = today.format(formatter);
    ```

### ⚡ Concurrency: CompletableFuture
`CompletableFuture<T>` is a major enhancement for asynchronous programming. It implements both `Future` and `CompletionStage`, allowing you to **compose, combine, and chain asynchronous computations** in a non-blocking way.

```java
// Start an asynchronous task
CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
    // Simulate a long-running task
    return "Result";
});

// Chain another operation to run when the first completes
future.thenApply(result -> result + " Processed")
      .thenAccept(finalResult -> System.out.println(finalResult));
```

### 🔧 Other Core Enhancements
*   **Default Methods**: Allow interfaces to have method implementations without breaking existing implementing classes. This enables library evolution (e.g., adding `forEach` to `Iterable`).
*   **Static Methods in Interfaces**: Interfaces can now have static helper methods.
*   **Enhanced `Map` API**: New methods like `getOrDefault()`, `forEach()`, `computeIfAbsent()`, `merge()`, and `replaceAll()` make working with maps easier.
*   **Repeating & Type Annotations**: You can apply the same annotation multiple times to a declaration, and annotations can be placed on any type use.
*   **Nashorn JavaScript Engine**: A new, faster JavaScript engine integrated into the JVM, allowing seamless interaction between Java and JavaScript.

### 💎 Summary
For a core review, focus on mastering **Lambdas, Functional Interfaces, and the Stream API**, as they are the most transformative features. Then, ensure you are comfortable using **`Optional`** for null safety and the **`java.time`** API for all date/time operations. Understanding **`CompletableFuture`** is crucial for writing modern, non-blocking asynchronous code. These features together define the modern Java programming paradigm.

If you'd like to dive deeper into any specific area, such as advanced Stream collectors or `CompletableFuture` composition patterns, feel free to ask.



<!-- SOURCE: 1.md -->
# File: 1.md

# Java Threads — Step by Step

I'll walk you from "what is a thread" to writing safe, modern multithreaded code. Each step builds on the previous one.

---

## Step 1: What is a Thread?

A **thread** is the smallest unit of execution inside a program. A Java program starts with one thread — the **main thread**. Threads let you do multiple things "at the same time" (concurrently).

**Why use threads?**
- Keep the UI responsive while doing background work
- Use multi-core CPUs
- Handle many network requests at once

**Key idea:** Threads share memory (the heap), but each has its own stack. Sharing memory is powerful *and* dangerous (we'll see why in Step 5).

---

## Step 2: Creating a Thread

There are **two classic ways**, plus a modern lambda way.

### Way 1: Extend `Thread`

```java
class MyThread extends Thread {
    @Override
    public void run() {
        System.out.println("Running in: " + Thread.currentThread().getName());
    }
}

public class Main {
    public static void main(String[] args) {
        MyThread t = new MyThread();
        t.start();   // NOT t.run()!
    }
}
```

### Way 2: Implement `Runnable` (preferred)

```java
class MyTask implements Runnable {
    @Override
    public void run() {
        System.out.println("Running in: " + Thread.currentThread().getName());
    }
}

public class Main {
    public static void main(String[] args) {
        Thread t = new Thread(new MyTask());
        t.start();
    }
}
```

### Way 3: Lambda (modern, concise)

```java
public class Main {
    public static void main(String[] args) {
        Thread t = new Thread(() -> System.out.println("Hello from " + Thread.currentThread().getName()));
        t.start();
    }
}
```

> ⚠️ **Critical:** call `start()`, never `run()` directly. `start()` creates a new thread; `run()` just executes the code on the current thread.

**Why prefer `Runnable`?** Java allows only one superclass, so extending `Thread` blocks you from extending anything else. `Runnable` keeps your class free.

---

## Step 3: Thread Lifecycle & Key Methods

A thread goes through states: `NEW → RUNNABLE → (BLOCKED / WAITING / TIMED_WAITING) → TERMINATED`.

The methods you'll actually use:

```java
Thread t = new Thread(() -> {
    try {
        Thread.sleep(2000);               // pause 2s (throws InterruptedException)
    } catch (InterruptedException e) {
        System.out.println("Interrupted!");
        return;                            // good practice: stop cleanly
    }
    System.out.println("Done sleeping");
});

t.start();
t.join();                                  // main thread waits until t finishes
System.out.println("Main continues");
```

| Method | What it does |
|---|---|
| `start()` | Launch the thread |
| `join()` | Wait for a thread to finish |
| `sleep(ms)` | Pause current thread (static) |
| `interrupt()` | Signal a thread to stop; causes `InterruptedException` in blocking calls |
| `Thread.currentThread()` | Reference to the running thread (static) |
| `isAlive()` | Is it still running? |

---

## Step 4: The Problem — Race Conditions

When threads share mutable data, results become unpredictable.

```java
class Counter {
    int count = 0;
    void increment() { count++; }     // NOT atomic!
}

public class Main {
    public static void main(String[] args) throws InterruptedException {
        Counter c = new Counter();
        Runnable task = () -> {
            for (int i = 0; i < 100_000; i++) c.increment();
        };

        Thread t1 = new Thread(task);
        Thread t2 = new Thread(task);
        t1.start(); t2.start();
        t1.join();  t2.join();

        System.out.println(c.count);   // Expect 200000, often get less!
    }
}
```

Why? `count++` is actually **three steps**: read → add → write. Threads interleave between steps and overwrite each other. This is a **race condition**.

---

## Step 5: Fix It with `synchronized`

`synchronized` ensures only one thread at a time executes a block, using a **lock** (monitor).

### Synchronized method

```java
class Counter {
    private int count = 0;

    synchronized void increment() {   // locks on `this`
        count++;
    }

    synchronized int getCount() { return count; }
}
```

### Synchronized block (finer-grained, better performance)

```java
class Counter {
    private int count = 0;
    private final Object lock = new Object();

    void increment() {
        synchronized (lock) {          // only this block is locked
            count++;
        }
    }
}
```

**Rules to remember:**
- `synchronized` instance method → locks on `this`
- `synchronized static` method → locks on the `Class` object
- Threads must lock on the **same object** to be mutually excluded

---

## Step 6: `volatile` and Atomics

### `volatile` — visibility, not atomicity

```java
class Flag {
    volatile boolean running = true;   // other threads see writes immediately
}
```

`volatile` guarantees a thread sees the latest value. It does **not** make `count++` safe.

### `AtomicInteger` — lock-free, thread-safe counters

```java
import java.util.concurrent.atomic.AtomicInteger;

class Counter {
    private final AtomicInteger count = new AtomicInteger(0);

    void increment() { count.incrementAndGet(); }   // atomic
    int getCount()   { return count.get(); }
}
```

Use `Atomic*` classes for simple counters and flags; they're usually faster than `synchronized`.

---

## Step 7: Deadlock (what to avoid)

Deadlock happens when two threads each hold a lock the other needs — forever.

```java
Object lockA = new Object();
Object lockB = new Object();

Thread t1 = new Thread(() -> {
    synchronized (lockA) {
        sleep(100);
        synchronized (lockB) { System.out.println("t1 done"); }
    }
});

Thread t2 = new Thread(() -> {
    synchronized (lockB) {
        sleep(100);
        synchronized (lockA) { System.out.println("t2 done"); }
    }
});
// Both threads hang forever.
```

**Prevention:** always acquire locks in the **same order** across all threads, and keep lock scopes small.

---

## Step 8: Thread Pools & `ExecutorService` (the modern way)

Creating a new thread per task is expensive. Use a **thread pool** instead.

```java
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) {
        ExecutorService pool = Executors.newFixedThreadPool(4);  // 4 worker threads

        for (int i = 0; i < 10; i++) {
            int taskId = i;
            pool.submit(() -> System.out.println("Task " + taskId + " on " + Thread.currentThread().getName()));
        }

        pool.shutdown();   // stop accepting new tasks; existing ones finish
    }
}
```

Common factories: `newFixedThreadPool(n)`, `newCachedThreadPool()`, `newSingleThreadExecutor()`.

> 💡 Always `shutdown()` the pool, or your app may never exit.

---

## Step 9: Getting Results with `Callable` + `Future`

`Runnable` can't return a value. `Callable<T>` can.

```java
import java.util.concurrent.*;

public class Main {
    public static void main(String[] args) throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(2);

        Callable<Integer> task = () -> {
            Thread.sleep(1000);
            return 42;
        };

        Future<Integer> future = pool.submit(task);

        System.out.println("Doing other work...");
        int result = future.get();      // blocks until result is ready
        System.out.println("Result: " + result);

        pool.shutdown();
    }
}
```

`Future` also gives you `isDone()`, `cancel()`, and `get(timeout, unit)`.

---

## Step 10: Best Practices ✅

1. **Prefer executors over raw `Thread`.** Let the pool manage threads.
2. **Minimize shared mutable state.** If you can avoid sharing, do.
3. **Prefer immutability.** `final` fields, immutable objects, no shared mutability = no locks needed.
4. **Use `Atomic*` or `synchronized` — but know which you need.** `volatile` alone won't make compound operations safe.
5. **Keep synchronized blocks small.** Don't do I/O or long work while holding a lock.
6. **Never swallow `InterruptedException`.** Either rethrow or restore the interrupt flag with `Thread.currentThread().interrupt()`.
7. **For complex coordination**, learn `java.util.concurrent`: `CountDownLatch`, `Semaphore`, `BlockingQueue`, `CompletableFuture`.
8. **Test under stress** — run your concurrency code many times; race conditions are intermittent.

---

## Quick Cheat Sheet

| Need | Use |
|---|---|
| Run code in background | `ExecutorService.submit(Runnable)` |
| Return a value | `Callable<T>` + `Future<T>` |
| Safe counter | `AtomicInteger` |
| Visibility across threads | `volatile` |
| Protect a critical section | `synchronized` block |
| Producer/consumer | `BlockingQueue` |
| Wait for N tasks | `CountDownLatch` / `invokeAll` |
| Combine async results | `CompletableFuture` |

---

**Suggested next steps:** try the Step 4 example, break it (see the wrong count), fix it with `synchronized`, then with `AtomicInteger`. Then rewrite Step 8 using `CompletableFuture`. That progression covers 90% of what you'll need in real code.

Want me to go deeper into any step — e.g., `CompletableFuture`, `BlockingQueue`, or how `synchronized` works under the hood (monitors & happens-before)?


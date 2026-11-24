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

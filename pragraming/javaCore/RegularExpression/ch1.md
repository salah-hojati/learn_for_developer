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

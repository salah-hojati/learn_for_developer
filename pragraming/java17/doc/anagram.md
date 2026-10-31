<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">

### معنی Anagram (آناگرام) چیست؟

دو رشته **anagram** هم هستند اگر:

- دقیقاً **همان حروف** را داشته باشند،
- هر حرف **به همان تعداد** تکرار شده باشد،
- فقط **ترتیب حروف** فرق کند.

به عبارت ساده: اگر بتوانی با جابه‌جا کردن حروف یکی، دیگری را بسازی، آن دو آناگرام هستند.

### مثال‌ها

این دو آناگرام هستند:
</div>


```text
s = "listen"
t = "silent"
```
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">


چون هر دو حروف `l, i, s, t, e, n` را دقیقاً یک بار دارند.

این دو هم آناگرام هستند:
</div>


```text
s = "rail safety"
t = "fairy tales"
```
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">


اگر فاصله‌ها را نادیده بگیریم.

اما این دو آناگرام نیستند:
</div>


```text
s = "hello"
t = "world"
```
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">


چون حروفشان یکی نیست.

و این دو هم آناگرام نیستند:
</div>


```text
s = "aab"
t = "abb"
```
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">


چون تعداد تکرار `a` و `b` یکی نیست.

---

### شرط‌های لازم برای آناگرام بودن

1. طول دو رشته باید برابر باشد.
2. تعداد تکرار هر کاراکتر در دو رشته باید برابر باشد.

---

### الگوریتم ساده

در پایتون می‌توانی این‌طور بررسی کنی:
</div>


```python
from collections import Counter

def is_anagram(s, t):
    return Counter(s) == Counter(t)
```
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">


یا حتی ساده‌تر:
</div>


```python
def is_anagram(s, t):
    return sorted(s) == sorted(t)
```
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">


ولی روش `Counter` معمولاً بهتر است، چون مرتب‌سازی هزینه‌ی بیشتری دارد.

---

### مثال کامل
</div>


```python
from collections import Counter

s = "listen"
t = "silent"

print(Counter(s) == Counter(t))  # True
```
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">


اگر بخواهی فاصله‌ها و بزرگی/کوچکی حروف مهم نباشند:
</div>


```python
def normalize(x):
    return x.replace(" ", "").lower()

def is_anagram(s, t):
    return Counter(normalize(s)) == Counter(normalize(t))
```
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">


---

### خلاصه

**Anagram یعنی دو کلمه/رشته که حروفشان دقیقاً یکی است، فقط جایشان عوض شده.**
مثلاً:
</div>


```text
"listen"  ↔  "silent"
"abc"     ↔  "cba"
"123"     ↔  "321"
```
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">


اگر رشته‌های `s` و `t` خودت را بفرستی، می‌توانم دقیقاً بگویم آناگرام هستند یا نه.
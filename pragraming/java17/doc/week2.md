<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">


عالی! هفته‌ی دوم روی **Sealed Classes**, **Pattern Matching for `switch`**, و **HashMap / Sliding Window** تمرکز دارد. این هفته نقطه‌ی اوج Java 17 است چون Sealed + Pattern Matching + Records یک trio قدرتمند می‌سازند که در مصاحبه‌ها خیلی دیده می‌شود.

---

## 📅 هفته ۲ — روز ۱: Sealed Classes (مقدمه)

### 🎓 بخش Java 17 (۳۰ دقیقه)

**Sealed Class** به تو اجازه می‌دهد کنترل کنی چه کلاس‌هایی می‌توانند از آن ارث‌بری کنند. این یک انقلاب در طراحی domain model است.

**سینتکس:**
</div>


```java
public sealed class Shape permits Circle, Rectangle, Triangle {}

public final class Circle extends Shape {}
public final class Rectangle extends Shape {}
public final class Triangle extends Shape {}
```
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">


**سه حالت برای زیرکلاس‌ها:**
- `final` — دیگر کسی نمی‌تواند ارث‌بری کند
- `sealed` — خودش هم محدود است
- `non-sealed` — باز می‌شود (برای کتابخانه‌های third-party)

**نکته‌ی مهم:** زیرکلاس‌ها باید در **همان ماژول/پکیج** باشند (اگر unnamed module باشد، همان پکیج).

**چرا مهم است؟**
- exhaustive checking در `switch`
- مدل‌سازی دقیق‌تر domain
- جلوگیری از ارث‌بری ناخواسته
- مکمل عالی برای Records

**مثال کاربردی:**
</div>


```java
public sealed interface PaymentMethod permits Card, Cash, Crypto {}

public record Card(String number, String cvv) implements PaymentMethod {}
public record Cash(double amount) implements PaymentMethod {}
public record Crypto(String wallet, double amount) implements PaymentMethod {}
```
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">


**نکته‌ی مصاحبه:** Sealed + Record + Pattern Matching = replacement برای Visitor Pattern و `enum` های سنگین.

### 🧩 بخش DSA (۶۰ دقیقه) — Valid Anagram

**مسئله:** دو رشته‌ی `s` و `t` داده شده. آیا anagram یکدیگرند؟

**راه‌حل ۱ — شمارش با آرایه:**
</div>


```java
public boolean isAnagram(String s, String t) {
    if (s.length() != t.length()) return false;
    var count = new int[26];
    for (var i = 0; i < s.length(); i++) {
        count[s.charAt(i) - 'a']++;
        count[t.charAt(i) - 'a']--;
    }
    for (var c : count) if (c != 0) return false;
    return true;
}
```
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">


**راه‌حل ۲ — HashMap (برای یونیکد):**
</div>


```java
public boolean isAnagram(String s, String t) {
    if (s.length() != t.length()) return false;
    var freq = new HashMap<Character, Integer>();
    for (var c : s.toCharArray()) freq.merge(c, 1, Integer::sum);
    for (var c : t.toCharArray()) {
        var v = freq.get(c);
        if (v == null || v == 0) return false;
        freq.put(c, v - 1);
    }
    return true;
}
```
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">




- **پیچیدگی:** زمان O(n)، فضا O(1) یا O(k)
- **الگو:** Frequency Count

### ✍️ تمرین
یک `sealed interface Shape` با `record Circle`, `record Square` بساز. در `main` یک `List<Shape>` بساز و هر کدام را چاپ کن.

---

## 📅 روز ۲: Sealed + Records با هم

### 🎓 بخش Java 17 (۳۰ دقیقه)

**ترکیب Sealed + Record** یک الگوی idiomatic است:
</div>


```java
public sealed interface Expr permits Num, Add, Mul, Neg {}

public record Num(double value) implements Expr {}
public record Add(Expr left, Expr right) implements Expr {}
public record Mul(Expr left, Expr right) implements Expr {}
public record Neg(Expr operand) implements Expr {}
```
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">



**چرا عالی است؟**
- `record` برای داده‌ی immutable
- `sealed` برای محدود کردن حالت‌ها
- کامپایلر می‌داند همه‌ی حالت‌ها را دیده
- `switch` بدون `default` کار می‌کند (اگر exhaustive باشد)

**نکته:** `record` خودش `final` است، پس `permits` نیازی به `final` اضافی ندارد.

**مثال: ارزیابی expression**
</div>


```java
public double eval(Expr e) {
    return switch (e) {
        case Num n -> n.value();
        case Add a -> eval(a.left()) + eval(a.right());
        case Mul m -> eval(m.left()) * eval(m.right());
        case Neg n -> -eval(n.operand());
    };
}
```
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">


هیچ `default` لازم نیست — این قدرت Sealed + Pattern Matching است.

### 🧩 بخش DSA (۶۰ دقیقه) — Group Anagrams

**مسئله:** آرایه‌ای از رشته‌ها. آن‌ها را به گروه‌های anagram تقسیم کن.

**راه‌حل — HashMap با کلید مرتب‌شده:**
</div>


```java
public List<List<String>> groupAnagrams(String[] strs) {
    var map = new HashMap<String, List<String>>();
    for (var s : strs) {
        var chars = s.toCharArray();
        Arrays.sort(chars);
        var key = new String(chars);
        map.computeIfAbsent(key, k -> new ArrayList<>()).add(s);
    }
    return new ArrayList<>(map.values());
}
```

**راه‌حل پیشرفته — کلید شمارشی (سریع‌تر):**
```java
public List<List<String>> groupAnagrams(String[] strs) {
    var map = new HashMap<String, List<String>>();
    for (var s : strs) {
        var count = new int[26];
        for (var c : s.toCharArray()) count[c - 'a']++;
        var key = Arrays.toString(count);
        map.computeIfAbsent(key, k -> new ArrayList<>()).add(s);
    }
    return new ArrayList<>(map.values());
}
```

- **پیچیدگی:** زمان O(n·k) (با شمارش)، فضا O(n·k)
- **الگو:** HashMap + Canonical Key

### ✍️ تمرین
یک `sealed interface Json` با `record JsonString`, `record JsonNumber`, `record JsonBool`, `record JsonNull`, `record JsonArray`, `record JsonObject` بساز.

---

## 📅 روز ۳: Pattern Matching for `switch`

### 🎓 بخش Java 17 (۳۰ دقیقه)

**قبل (Java 8):**
```java
double area(Shape s) {
    if (s instanceof Circle) {
        Circle c = (Circle) s;
        return Math.PI * c.radius() * c.radius();
    } else if (s instanceof Rectangle) {
        Rectangle r = (Rectangle) s;
        return r.width() * r.height();
    }
    throw new IllegalArgumentException();
}
```

**حالا (Java 17 preview، Java 21 stable):**
```java
double area(Shape s) {
    return switch (s) {
        case Circle c -> Math.PI * c.radius() * c.radius();
        case Rectangle r -> r.width() * r.height();
        case Triangle t -> 0.5 * t.base() * t.height();
    };
}
```

**انواع pattern:**
```java
// Type pattern
case Circle c -> ...

// Guarded pattern (با when)
case Circle c when c.radius() > 10 -> "big circle";
case Circle c -> "small circle";

// Null handling (Java 21)
case null -> "null!";

// Constant
case Integer i when i == 0 -> "zero";
```

**نکته‌ی مهم:** در Java 17 این ویژگی **preview** است و باید با `--enable-preview` کامپایل شود. در Java 21 رسماً stable شد. اگر مصاحبه‌ات Java 17 است، بگو که با آن آشنا هستی و در Java 21 کامل شده.

**Exhaustiveness:** اگر selector از نوع sealed باشد و همه‌ی زیرکلاس‌ها پوشش داده شوند، `default` لازم نیست.

### 🧩 بخش DSA (۶۰ دقیقه) — Longest Substring Without Repeating Characters

**مسئله:** طولانی‌ترین زیررشته‌ی بدون کاراکتر تکراری را برگردان.

**راه‌حل — Sliding Window + HashMap:**
```java
public int lengthOfLongestSubstring(String s) {
    var lastSeen = new HashMap<Character, Integer>();
    var maxLen = 0;
    var left = 0;
    for (var right = 0; right < s.length(); right++) {
        var c = s.charAt(right);
        if (lastSeen.containsKey(c) && lastSeen.get(c) >= left) {
            left = lastSeen.get(c) + 1;
        }
        lastSeen.put(c, right);
        maxLen = Math.max(maxLen, right - left + 1);
    }
    return maxLen;
}
```

**راه‌حل بهینه — آرایه برای ASCII:**
```java
public int lengthOfLongestSubstring(String s) {
    var lastSeen = new int[128];
    Arrays.fill(lastSeen, -1);
    var maxLen = 0;
    var left = 0;
    for (var right = 0; right < s.length(); right++) {
        var c = s.charAt(right);
        if (lastSeen[c] >= left) {
            left = lastSeen[c] + 1;
        }
        lastSeen[c] = right;
        maxLen = Math.max(maxLen, right - left + 1);
    }
    return maxLen;
}
```

- **پیچیدگی:** زمان O(n)، فضا O(min(n, alphabet))
- **الگو:** Sliding Window + HashMap/Array

**Edge cases:**
- رشته خالی
- همه کاراکترها یکسان
- همه کاراکترها متفاوت

### ✍️ تمرین
متد `area(Shape)` را با `switch` pattern بنویس. اگر JDK 17 داری، با `--enable-preview` کامپایل کن.

---

## 📅 روز ۴: Sealed + Switch + Record (ترکیب کامل)

### 🎓 بخش Java 17 (۳۰ دقیقه)

**الگوی کامل:** Sealed + Record + Pattern Matching

```java
public sealed interface Result<T> permits Success, Failure {}

public record Success<T>(T value) implements Result<T> {}
public record Failure<T>(String error) implements Result<T> {}

public <T> T unwrap(Result<T> result) {
    return switch (result) {
        case Success<T> s -> s.value();
        case Failure<T> f -> throw new RuntimeException(f.error());
    };
}
```

**مزایا:**
- کامپایلر مطمئن می‌شود همه‌ی حالت‌ها پوشش داده شده
- generic type inference عالی کار می‌کند
- جایگزین `Optional` و `Either` می‌شود

**نکته‌ی مصاحبه:** این الگو در طراحی APIهای مدرن (Result, Either, State Machine) خیلی رایج است. اگر بتوانی در مصاحبه نشان بدهی، امتیاز بزرگی می‌گیری.

### 🧩 بخش DSA (۶۰ دقیقه) — Longest Repeating Character Replacement

**مسئله:** رشته‌ی `s` و عدد `k`. می‌توانی حداکثر `k` کاراکتر را عوض کنی. طولانی‌ترین زیررشته‌ی با کاراکتر یکسان را برگردان.

**راه‌حل — Sliding Window:**
```java
public int characterReplacement(String s, int k) {
    var count = new int[26];
    var left = 0;
    var maxCount = 0;
    var maxLen = 0;
    for (var right = 0; right < s.length(); right++) {
        count[s.charAt(right) - 'A']++;
        maxCount = Math.max(maxCount, count[s.charAt(right) - 'A']);
        // پنجره نامعتبر: (طول - maxCount) > k
        while ((right - left + 1) - maxCount > k) {
            count[s.charAt(left) - 'A']--;
            left++;
        }
        maxLen = Math.max(maxLen, right - left + 1);
    }
    return maxLen;
}
```

- **پیچیدگی:** زمان O(n)، فضا O(1)
- **الگو:** Sliding Window با frequency

**نکته‌ی کلیدی:** در این مسئله، `maxCount` را هرگز کاهش نمی‌دهیم — این یک trick معروف است که در مصاحبه توضیحش را بدهی، امتیاز می‌گیری.

### ✍️ تمرین
یک `sealed interface Tree` با `record Leaf`, `record Node` بساز و یک متد `int sum(Tree)` با `switch` pattern بنویس.

---

## 📅 روز ۵: `List.of`, `Map.of`, `Set.of`

### 🎓 بخش Java 17 (۳۰ دقیقه)

**کارخانه‌های Collection (از Java 9):**
```java
var list = List.of(1, 2, 3);              // immutable
var map = Map.of("a", 1, "b", 2);         // immutable
var set = Set.of(1, 2, 3);                // immutable
var mapOfEntries = Map.ofEntries(
    Map.entry("a", 1),
    Map.entry("b", 2)
);
```

**ویژگی‌ها:**
- **Immutable** — هر تغییر `UnsupportedOperationException` می‌دهد
- **Null-hostile** — `null` را قبول نمی‌کند
- **Compact** — پیاده‌سازی بهینه
- **Random iteration order** برای Set و Map

**تفاوت با `Arrays.asList`:**
```java
var a = Arrays.asList(1, 2, 3); // fixed-size, قابل set
var b = List.of(1, 2, 3);        // immutable کامل
```

**نکته‌ی مصاحبه:** `List.of` برای داده‌های ثابت (constant lists) عالی است. برای داده‌های متغیر، `new ArrayList<>(List.of(...))` بساز.

**متدهای مفید Java 8+:**
```java
map.getOrDefault(key, 0);
map.putIfAbsent(key, value);
map.computeIfAbsent(key, k -> new ArrayList<>());
map.merge(key, 1, Integer::sum);
list.removeIf(x -> x < 0);
```

### 🧩 بخش DSA (۶۰ دقیقه) — Top K Frequent Elements

**مسئله:** آرایه و عدد `k`. `k` عنصر پرتکرار را برگردان.

**راه‌حل ۱ — Heap:**
```java
public int[] topKFrequent(int[] nums, int k) {
    var freq = new HashMap<Integer, Integer>();
    for (var n : nums) freq.merge(n, 1, Integer::sum);
    
    var heap = new PriorityQueue<Map.Entry<Integer, Integer>>(
        Comparator.comparingInt(Map.Entry::getValue)
    );
    for (var e : freq.entrySet()) {
        heap.offer(e);
        if (heap.size() > k) heap.poll();
    }
    
    var result = new int[k];
    for (var i = k - 1; i >= 0; i--) {
        result[i] = heap.poll().getKey();
    }
    return result;
}
```

**راه‌حل ۲ — Bucket Sort (بهینه‌تر):**
```java
public int[] topKFrequent(int[] nums, int k) {
    var freq = new HashMap<Integer, Integer>();
    for (var n : nums) freq.merge(n, 1, Integer::sum);
    
    var buckets = new List[nums.length + 1];
    for (var e : freq.entrySet()) {
        var f = e.getValue();
        if (buckets[f] == null) buckets[f] = new ArrayList<>();
        buckets[f].add(e.getKey());
    }
    
    var result = new int[k];
    var idx = 0;
    for (var i = buckets.length - 1; i >= 0 && idx < k; i--) {
        if (buckets[i] != null) {
            for (var n : buckets[i]) {
                result[idx++] = n;
                if (idx == k) return result;
            }
        }
    }
    return result;
}
```

- **پیچیدگی راه‌حل ۱:** زمان O(n log k)، فضا O(n)
- **پیچیدگی راه‌حل ۲:** زمان O(n)، فضا O(n)
- **الگو:** HashMap + Heap/Bucket

### ✍️ تمرین
یک `Map<String, List<Integer>>` بساز با `Map.of` و `List.of` و روی آن iterate کن.

---

## 📅 روز ۶: Optional

### 🎓 بخش Java 17 (۳۰ دقیقه)

**Optional** یک container است که ممکن است مقدار داشته باشد یا نداشته باشد. برای جلوگیری از `NullPointerException` طراحی شده.

**ساخت:**
```java
var present = Optional.of("hello");
var empty = Optional.empty();
var nullable = Optional.ofNullable(maybeNull);
```

**استفاده:**
```java
optional.ifPresent(System.out::println);
optional.orElse("default");
optional.orElseGet(() -> computeDefault());
optional.orElseThrow(() -> new RuntimeException("missing"));
optional.map(String::length).orElse(0);
optional.filter(s -> s.length() > 3);
```

**زنجیره‌سازی:**
```java
var result = findUser(id)
    .map(User::getAddress)
    .map(Address::getCity)
    .orElse("Unknown");
```

**اشتباهات رایج:**
- ❌ `optional.get()` بدون `isPresent()`
- ❌ `Optional` به‌عنوان فیلد کلاس یا پارامتر متد
- ❌ `Optional<List<T>>` — به‌جایش `List<T>` خالی برگردان
- ✅ فقط به‌عنوان نوع بازگشتی متد

**نکته‌ی مصاحبه:** `Optional` یک value-based class است؛ نباید برای identity یا sync استفاده شود.

### 🧩 بخش DSA (۶۰ دقیقه) — Two Sum II (Sorted Array)

**مسئله:** آرایه‌ی **مرتب** و `target`. دو ایندکس (۱-based) را برگردان.

**راه‌حل — Two Pointers:**
```java
public int[] twoSum(int[] numbers, int target) {
    var left = 0;
    var right = numbers.length - 1;
    while (left < right) {
        var sum = numbers[left] + numbers[right];
        if (sum == target) {
            return new int[]{left + 1, right + 1};
        } else if (sum < target) {
            left++;
        } else {
            right--;
        }
    }
    return new int[]{};
}
```

- **پیچیدگی:** زمان O(n)، فضا O(1)
- **الگو:** Two Pointers

**نکته:** چون آرایه مرتب است، Two Pointers از HashMap سریع‌تر و کم‌حافظه‌تر است.

### ✍️ تمرین
یک متد `Optional<User> findById(int id)` بنویس که اگر پیدا شد `Optional.of(user)` وگرنه `Optional.empty()` برگرداند.

---

## 📅 روز ۷: مرور + پروژه

### 🎯 کارهای امروز

1. **مرور Java 17**: Sealed, Pattern Matching, `List.of`, Optional
2. **بازنویسی DSA**: این ۳ مسئله را دوباره حل کن:
    - Longest Substring Without Repeating Characters
    - Group Anagrams
    - Top K Frequent Elements
3. **Mock Interview**: با تایمر ۴۵ دقیقه، `Longest Repeating Character Replacement` را حل کن.
4. **پروژه‌ی هفته: Expression Evaluator**

### 🎁 پروژه‌ی هفته: Expression Evaluator

یک ارزیاب expression ساده با Sealed + Record + Pattern Matching بساز:

```java
public sealed interface Expr permits Num, Add, Sub, Mul, Div, Neg {}

public record Num(double value) implements Expr {}
public record Add(Expr left, Expr right) implements Expr {}
public record Sub(Expr left, Expr right) implements Expr {}
public record Mul(Expr left, Expr right) implements Expr {}
public record Div(Expr left, Expr right) implements Expr {}
public record Neg(Expr operand) implements Expr {}

public class Evaluator {
    public static double eval(Expr e) {
        return switch (e) {
            case Num n -> n.value();
            case Add a -> eval(a.left()) + eval(a.right());
            case Sub s -> eval(s.left()) - eval(s.right());
            case Mul m -> eval(m.left()) * eval(m.right());
            case Div d -> eval(d.left()) / eval(d.right());
            case Neg n -> -eval(n.operand());
        };
    }
    
    public static void main(String[] args) {
        // (2 + 3) * 4
        Expr e = new Mul(
            new Add(new Num(2), new Num(3)),
            new Num(4)
        );
        System.out.println(eval(e)); // 20.0
    }
}
```

**توسعه‌ی اختیاری:**
- `simplify(Expr)` — ساده‌سازی
- `toString(Expr)` — چاپ به‌صورت infix
- `sealed interface Stmt` برای متغیرها

### 📝 چک‌لیست پایان هفته

- [ ] `sealed interface` ساخته‌ام
- [ ] `sealed` + `record` ترکیب کرده‌ام
- [ ] `switch` pattern نوشته‌ام
- [ ] `List.of`, `Map.of` استفاده کرده‌ام
- [ ] `Optional` را درست به‌کار برده‌ام
- [ ] ۷ مسئله‌ی DSA را حل کرده‌ام
- [ ] پروژه‌ی Expression Evaluator را کامل کرده‌ام
- [ ] Mock Interview انجام داده‌ام

---

## 📊 آمار پایان هفته ۲

اگر طبق برنامه پیش رفته باشی:

| مورد | تعداد |
|---|---|
| ویژگی‌های Java 17 یادگرفته | ۵ |
| مسائل DSA | ۷ |
| پروژه | ۱ |
| Mock Interview | ۱ |
| **مجموع مسائل تا الان** | **۱۴** |
| **مجموع ویژگی‌ها تا الان** | **۱۰** |

---

## 💡 نکات مهم برای هفته ۳

هفته‌ی بعد می‌رویم سراغ:
- **Stream API** (پایه‌ای برای کد مدرن)
- **Linked List / Stack / Queue**
- **بازنویسی کدهای Java 8 با Stream**

اگر خواستی، می‌توانم:
- **هفته‌ی سوم** را با همین سطح جزئیات بنویسم
- یک **پروژه‌ی Maven** آماده کنم که این کدها را در آن نگه داری
- یک **بانک سؤال Mock Interview** برای Java 17 بسازم

کدام را اول شروع کنیم؟
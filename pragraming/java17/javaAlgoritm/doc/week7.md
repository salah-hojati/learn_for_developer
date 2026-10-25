عالی! هفته‌ی هفتم روی **JVM و GC Tuning**، **Profiling**، و **Dynamic Programming** (قلب مصاحبه‌های سخت) تمرکز دارد. این هفته ترکیبی از عمق پلتفرمی و قدرت الگوریتمی است.

---

## 📅 هفته ۷ — روز ۱: JVM Architecture و Memory Model

### 🎓 بخش Java 17 (۳۰ دقیقه)

**ساختار JVM:**
```
┌─────────────────────────────────────┐
│         Class Loader Subsystem       │
│  (Loading → Linking → Initialization)│
├─────────────────────────────────────┤
│         Runtime Data Areas           │
│  ┌──────┐ ┌──────┐ ┌──────────────┐  │
│  │ Heap │ │Stack │ │ Metaspace    │  │
│  └──────┘ └──────┘ └──────────────┘  │
│  ┌──────────┐ ┌──────────────────┐   │
│  │PC Reg.   │ │Native Method St. │   │
│  └──────────┘ └──────────────────┘   │
├─────────────────────────────────────┤
│       Execution Engine               │
│  Interpreter | JIT | GC              │
└─────────────────────────────────────┘
```

**Runtime Data Areas:**

**۱. Heap (مشترک بین threadها):**
- **Young Generation**: Eden + Survivor (S0, S1)
- **Old Generation (Tenured)**
- **نکته:** در Java 8+ PermGen حذف و به Metaspace منتقل شد.

**۲. Stack (per-thread):**
- هر thread یک stack دارد
- Frameها: Local Variables, Operand Stack, Frame Data
- اندازه‌ی پیش‌فرض: 512KB–1MB (`-Xss`)

**۳. Metaspace (Java 8+):**
- به‌جای PermGen
- در native memory، نه heap
- رشد خودکار دارد (`-XX:MaxMetaspaceSize`)

**۴. Program Counter (per-thread):**
- آدرس دستور بعدی

**۵. Native Method Stack:**
- برای متدهای native (JNI)

**مقایسه‌ی Heap و Stack:**

| ویژگی | Heap | Stack |
|---|---|---|
| اشتراک | مشترک | per-thread |
| اندازه | بزرگ | کوچک |
| سرعت | کندتر | سریع‌تر |
| مدیریت | GC | LIFO |
| خطا | OutOfMemoryError | StackOverflowError |
| ذخیره | Object, Array | primitive, reference |

**تنظیمات مهم:**
```bash
-Xms512m          # حداقل heap
-Xmx2g            # حداکثر heap
-Xss512k          # اندازه‌ی stack هر thread
-XX:MetaspaceSize=128m
-XX:MaxMetaspaceSize=256m
-XX:NewRatio=2    # نسبت Old/Young
-XX:SurvivorRatio=8 # نسبت Eden/Survivor
```

**نکته‌ی مصاحبه:** اگر از تو بپرسند تفاوت Heap و Stack چیست، این جدول را بگو. اگر بپرسند PermGen چه شد، بگو در Java 8 حذف و به Metaspace منتقل شد (در native memory).

### 🧩 بخش DSA (۶۰ دقیقه) — Climbing Stairs

**مسئله:** n پله. هر بار ۱ یا ۲ پله. چند راه؟

**راه‌حل ۱ — Recursive (کند):**
```java
public int climbStairs(int n) {
    if (n <= 2) return n;
    return climbStairs(n - 1) + climbStairs(n - 2);
}
// پیچیدگی: O(2^n) — بد!
```

**راه‌حل ۲ — Memoization (Top-Down DP):**
```java
public int climbStairs(int n) {
    var memo = new int[n + 1];
    return climb(n, memo);
}

private int climb(int n, int[] memo) {
    if (n <= 2) return n;
    if (memo[n] != 0) return memo[n];
    return memo[n] = climb(n - 1, memo) + climb(n - 2, memo);
}
// پیچیدگی: O(n) زمان، O(n) فضا
```

**راه‌حل ۳ — Tabulation (Bottom-Up DP):**
```java
public int climbStairs(int n) {
    if (n <= 2) return n;
    var prev2 = 1;
    var prev1 = 2;
    for (var i = 3; i <= n; i++) {
        var curr = prev1 + prev2;
        prev2 = prev1;
        prev1 = curr;
    }
    return prev1;
}
// پیچیدگی: O(n) زمان، O(1) فضا
```

- **الگو:** DP / Fibonacci
- **نکته:** این مسئله دقیقاً Fibonacci است. سه رویکرد (recursive, memo, tabulation) را بلد باش.

### ✍️ تمرین
مسئله‌ی `Min Cost Climbing Stairs` را با هر سه رویکرد حل کن.

---

## 📅 روز ۲: GC — مفاهیم و G1

### 🎓 بخش Java 17 (۳۰ دقیقه)

**انواع GC در Java 17:**
- **Serial GC**: تک‌thread، برای برنامه‌های کوچک
- **Parallel GC**: multi-thread، throughput بالا
- **G1 GC**: پیش‌فرض از Java 9، latency-balance
- **ZGC**: latency خیلی کم (زیر ۱۰ms)، آزمایشی در 17
- **Shenandoah**: مشابه ZGC، نه در Oracle JDK

**فعال‌سازی:**
```bash
-XX:+UseSerialGC
-XX:+UseParallelGC
-XX:+UseG1GC      # پیش‌فرض
-XX:+UseZGC
```

**G1 GC — ویژگی‌ها:**
- Heap را به regionهای مساوی تقسیم می‌کند (معمولاً ۱–۳۲MB)
- Regionها Young یا Old می‌شوند (پویا)
- **Concurrent Marking**: تشخیص garbage در حالی که برنامه اجرا می‌شود
- **Mixed Collections**: هم Young و هم بخشی از Old را جمع می‌کند
- هدف: **Predictable Pause Times** با `-XX:MaxGCPauseMillis`

**G1 Phases:**
```
Young GC → Concurrent Mark → Remark → Cleanup → Mixed GC → ...
```

**تنظیمات G1:**
```bash
-XX:+UseG1GC
-XX:MaxGCPauseMillis=200      # هدف pause
-XX:G1HeapRegionSize=16m
-XX:InitiatingHeapOccupancyPercent=45
-XX:G1NewSizePercent=5
-XX:G1MaxNewSizePercent=60
```

**مثال لاگ GC:**
```bash
java -Xlog:gc*:file=gc.log:time,uptime,level,tags -jar app.jar
```

خروجی نمونه:
```
[2.345s][info][gc] GC(0) Pause Young (Normal) (G1 Evacuation Pause) 
    123M->45M(512M) 12.345ms
```

- `123M->45M`: قبل و بعد از GC
- `(512M)`: کل heap
- `12.345ms`: زمان pause

**نکته‌ی مصاحبه:** G1 هدفش **Predictable Pause** است، نه throughput مطلق. اگر throughput مهم‌تر است، Parallel GC. اگر latency خیلی مهم است، ZGC.

### 🧩 بخش DSA (۶۰ دقیقه) — House Robber

**مسئله:** آرایه‌ی پول خانه‌ها. نمی‌توانی دو خانه‌ی مجاور بدزدی. حداکثر چقدر؟

**راه‌حل ۱ — Memoization:**
```java
public int rob(int[] nums) {
    var memo = new int[nums.length];
    Arrays.fill(memo, -1);
    return rob(nums, 0, memo);
}

private int rob(int[] nums, int i, int[] memo) {
    if (i >= nums.length) return 0;
    if (memo[i] != -1) return memo[i];
    var robCurrent = nums[i] + rob(nums, i + 2, memo);
    var skipCurrent = rob(nums, i + 1, memo);
    return memo[i] = Math.max(robCurrent, skipCurrent);
}
```

**راه‌حل ۲ — Tabulation با O(1) فضا:**
```java
public int rob(int[] nums) {
    var prev2 = 0; // dp[i-2]
    var prev1 = 0; // dp[i-1]
    for (var n : nums) {
        var curr = Math.max(prev1, prev2 + n);
        prev2 = prev1;
        prev1 = curr;
    }
    return prev1;
}
```

- **پیچیدگی:** زمان O(n)، فضا O(1)
- **الگو:** DP — House Robber

**نکته:** `curr = max(prev1, prev2 + n)` — یا خانه‌ی قبلی را رد می‌کنی یا این را می‌دزدی. کلید DP.

### ✍️ تمرین
مسئله‌ی `House Robber II` (خانه‌ها دایره‌ای) را حل کن — hint: دو بار DP روی `[0..n-2]` و `[1..n-1]`.

---

## 📅 روز ۳: ZGC، Heap Tuning، و Profiling مقدماتی

### 🎓 بخش Java 17 (۳۰ دقیقه)

**ZGC (Z Garbage Collector):**
- **هدف:** pause time زیر ۱۰ms، مستقل از heap size
- **Concurrent**: تقریباً همه‌ی کارها concurrent
- **Region-based** (مثل G1، ولی متفاوت)
- **Generational ZGC**: از Java 21 (در 17 non-generational)
- **آزمایشی در Java 17**: `-XX:+UnlockExperimentalVMOptions -XX:+UseZGC`

**فعال‌سازی ZGC:**
```bash
java -XX:+UseZGC -Xmx16g -jar app.jar
```

**مقایسه GC ها:**

| GC | Pause Target | Throughput | Heap | کاربرد |
|---|---|---|---|---|
| Serial | High | Low | Small | CLI, embedded |
| Parallel | High | High | Medium-Large | Batch |
| G1 | Medium | Medium-High | Medium-Large | General |
| ZGC | Very Low | Medium | Large | Low-latency |
| Shenandoah | Very Low | Medium | Large | Low-latency |

**ابزارهای Profiling:**

**۱. `jps` — لیست JVM processes:**
```bash
jps -lvm
```

**۲. `jcmd` — دستورات JVM:**
```bash
jcmd <pid> VM.flags              # flags فعلی
jcmd <pid> GC.heap_info          # اطلاعات heap
jcmd <pid> Thread.print          # thread dump
jcmd <pid> GC.run                # اجرای GC
jcmd <pid> JFR.start             # شروع JFR
```

**۳. `jmap` — اطلاعات heap:**
```bash
jmap -heap <pid>
jmap -histo <pid>                # histogram کلاس‌ها
jmap -dump:format=b,file=heap.hprof <pid>
```

**۴. `jstack` — thread dump:**
```bash
jstack <pid>
jstack -l <pid> > threads.txt
```

**۵. JFR (Java Flight Recorder):**
```bash
java -XX:StartFlightRecording=duration=60s,filename=rec.jfr -jar app.jar
jfr print --events GCPhasePause rec.jfr
jfr summary rec.jfr
```

**نکته‌ی مصاحبه:** اگر از تو بپرسند چطور memory leak پیدا می‌کنی، این مسیر:
1. `jcmd <pid> GC.heap_info` — ببین heap رشد می‌کند
2. `jmap -histo <pid>` — کلاس‌های پرتعداد
3. `jmap -dump:file=heap.hprof` — dump
4. تحلیل با Eclipse MAT یا JProfiler
5. `jstack` برای threadهای stuck

### 🧩 بخش DSA (۶۰ دقیقه) — Coin Change

**مسئله:** سکه‌ها با مقادیر مختلف. حداقل تعداد سکه برای رسیدن به `amount`.

**راه‌حل — Tabulation:**
```java
public int coinChange(int[] coins, int amount) {
    var dp = new int[amount + 1];
    Arrays.fill(dp, amount + 1); // "infinity"
    dp[0] = 0;
    
    for (var i = 1; i <= amount; i++) {
        for (var coin : coins) {
            if (coin <= i) {
                dp[i] = Math.min(dp[i], dp[i - coin] + 1);
            }
        }
    }
    return dp[amount] > amount ? -1 : dp[amount];
}
```

- **پیچیدگی:** زمان O(amount × coins)، فضا O(amount)
- **الگو:** Unbounded Knapsack

**نکته:** `dp[i]` = حداقل سکه برای مقدار `i`. مقدار اولیه `amount + 1` به‌عنوان "بی‌نهایت" (چون بیشترین تعداد سکه `amount` است اگر سکه‌ی ۱ داشته باشیم).

### ✍️ تمرین
مسئله‌ی `Coin Change II` (تعداد راه‌ها، نه حداقل) را حل کن.

---

## 📅 روز ۴: Dynamic Programming — Knapsack

### 🎓 بخش Java 17 (۳۰ دقیقه)

**0/1 Knapsack:** هر آیتم یا برداشته می‌شود یا نه.

**مسئله:** آیتم‌ها با weight و value. حداکثر ارزش با ظرفیت `W`.

**راه‌حل 2D:**
```java
public int knapsack(int[] weights, int[] values, int W) {
    var n = weights.length;
    var dp = new int[n + 1][W + 1];
    
    for (var i = 1; i <= n; i++) {
        for (var w = 1; w <= W; w++) {
            // نه برداشتن
            dp[i][w] = dp[i - 1][w];
            // برداشتن (اگر جا شود)
            if (weights[i - 1] <= w) {
                dp[i][w] = Math.max(dp[i][w], 
                    dp[i - 1][w - weights[i - 1]] + values[i - 1]);
            }
        }
    }
    return dp[n][W];
}
```

**بهینه‌سازی به 1D:**
```java
public int knapsack(int[] weights, int[] values, int W) {
    var dp = new int[W + 1];
    for (var i = 0; i < weights.length; i++) {
        // مهم: از W به پایین iterate کن!
        for (var w = W; w >= weights[i]; w--) {
            dp[w] = Math.max(dp[w], dp[w - weights[i]] + values[i]);
        }
    }
    return dp[W];
}
```

**نکته‌ی حیاتی:** در 0/1 Knapsack، حلقه‌ی `w` از **بالا به پایین** است. چرا؟ چون هر آیتم فقط یک بار استفاده می‌شود. اگر از پایین به بالا بروی، آیتم چند بار استفاده می‌شود (که می‌شود Unbounded Knapsack).

**Unbounded Knapsack:**
```java
for (var w = weights[i]; w <= W; w++) {  // از پایین به بالا
    dp[w] = Math.max(dp[w], dp[w - weights[i]] + values[i]);
}
```

**انواع Knapsack:**
| نوع | ترتیب حلقه | مثال |
|---|---|---|
| 0/1 | w از بالا به پایین | House Robber |
| Unbounded | w از پایین به بالا | Coin Change |
| Bounded | هر دو | محدودیت تعداد |

**نکته‌ی مصاحبه:** Knapsack یکی از پرتکرارترین الگوهای DP است. Subset Sum، Partition Equal Subset، Target Sum همگی Knapsack هستند.

### 🧩 بخش DSA (۶۰ دقیقه) — Partition Equal Subset Sum

**مسئله:** آیا می‌توان آرایه را به دو زیرمجموعه‌ی با مجموع برابر تقسیم کرد؟

```java
public boolean canPartition(int[] nums) {
    var total = Arrays.stream(nums).sum();
    if (total % 2 != 0) return false;
    var target = total / 2;
    
    var dp = new boolean[target + 1];
    dp[0] = true;
    
    for (var num : nums) {
        for (var w = target; w >= num; w--) {
            dp[w] = dp[w] || dp[w - num];
        }
    }
    return dp[target];
}
```

- **پیچیدگی:** زمان O(n × target)، فضا O(target)
- **الگو:** 0/1 Knapsack (Subset Sum)

**نکته‌ی Java 17:** از `Arrays.stream(nums).sum()` استفاده کردم. برای آرایه‌های بزرگ، `IntStream.of(nums).sum()` یا حلقه‌ی ساده سریع‌تر است.

### ✍️ تمرین
مسئله‌ی `Target Sum` را حل کن (با علامت‌های + و -).

---

## 📅 روز ۵: Longest Common Subsequence و LIS

### 🎓 بخش Java 17 (۳۰ دقیقه)

**LCS (Longest Common Subsequence):**

**مسئله:** طولانی‌ترین زیردنباله‌ی مشترک بین دو رشته.

**راه‌حل 2D:**
```java
public int longestCommonSubsequence(String text1, String text2) {
    var m = text1.length();
    var n = text2.length();
    var dp = new int[m + 1][n + 1];
    
    for (var i = 1; i <= m; i++) {
        for (var j = 1; j <= n; j++) {
            if (text1.charAt(i - 1) == text2.charAt(j - 1)) {
                dp[i][j] = dp[i - 1][j - 1] + 1;
            } else {
                dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
            }
        }
    }
    return dp[m][n];
}
```

**بهینه‌سازی به 1D:**
```java
public int longestCommonSubsequence(String text1, String text2) {
    var m = text1.length();
    var n = text2.length();
    var dp = new int[n + 1];
    
    for (var i = 1; i <= m; i++) {
        var prev = 0;
        for (var j = 1; j <= n; j++) {
            var temp = dp[j];
            if (text1.charAt(i - 1) == text2.charAt(j - 1)) {
                dp[j] = prev + 1;
            } else {
                dp[j] = Math.max(dp[j], dp[j - 1]);
            }
            prev = temp;
        }
    }
    return dp[n];
}
```

**الگوی LCS:**
- `dp[i][j]` = LCS دو پیشوند
- اگر کاراکترها برابر: `dp[i-1][j-1] + 1`
- وگرنه: `max(dp[i-1][j], dp[i][j-1])`

**کاربردهای LCS:**
- Diff (مثل Git)
- Edit Distance
- DNA sequence alignment
- Shortest Common Supersequence

**LIS (Longest Increasing Subsequence):**

در هفته ۴ دیدیم. دو رویکرد:
- DP: O(n²)
- Binary Search: O(n log n)

**نکته‌ی مصاحبه:** LCS و LIS دو الگوی کلاسیک DP هستند. اگر مسئله "زیردنباله" یا "زیررشته" داشت، به این دو فکر کن.

### 🧩 بخش DSA (۶۰ دقیقه) — Edit Distance

**مسئله:** حداقل عملیات (insert, delete, replace) برای تبدیل `word1` به `word2`.

```java
public int minDistance(String word1, String word2) {
    var m = word1.length();
    var n = word2.length();
    var dp = new int[m + 1][n + 1];
    
    // مقدار پایه
    for (var i = 0; i <= m; i++) dp[i][0] = i;
    for (var j = 0; j <= n; j++) dp[0][j] = j;
    
    for (var i = 1; i <= m; i++) {
        for (var j = 1; j <= n; j++) {
            if (word1.charAt(i - 1) == word2.charAt(j - 1)) {
                dp[i][j] = dp[i - 1][j - 1];
            } else {
                dp[i][j] = 1 + Math.min(
                    Math.min(dp[i - 1][j],     // delete
                             dp[i][j - 1]),    // insert
                    dp[i - 1][j - 1]           // replace
                );
            }
        }
    }
    return dp[m][n];
}
```

- **پیچیدگی:** زمان O(m × n)، فضا O(m × n)
- **الگو:** DP روی رشته‌ها

**نکته:** این مسئله پایه‌ی diff و spell checker است. سه عملیات + حالت equal.

### ✍️ تمرین
مسئله‌ی `Longest Palindromic Subsequence` را با LCS حل کن (LCS رشته با معکوسش).

---

## 📅 روز ۶: Bit Manipulation

### 🎓 بخش Java 17 (۳۰ دقیقه)

**عملگرهای بیتی در جاوا:**
```java
int a = 5;   // 0101
int b = 3;   // 0011

a & b   // 0001 = 1 (AND)
a | b   // 0111 = 7 (OR)
a ^ b   // 0110 = 6 (XOR)
~a      // ...11111010 = -6 (NOT)
a << 1  // 1010 = 10 (left shift)
a >> 1  // 0010 = 2 (arithmetic right shift)
a >>> 1 // 0010 = 2 (logical right shift)
```

**Trickهای رایج:**

**۱. چک زوج/فرد:**
```java
if ((n & 1) == 0) { /* even */ }
```

**۲. چک توان ۲:**
```java
boolean isPowerOfTwo(int n) {
    return n > 0 && (n & (n - 1)) == 0;
}
```

**۳. پاک کردن کم‌ارزش‌ترین بیت ۱:**
```java
n = n & (n - 1);
```

**۴. پیدا کردن کم‌ارزش‌ترین بیت ۱:**
```java
int lowest = n & (-n);
```

**۵. شمردن بیت‌های ۱ (Brian Kernighan):**
```java
public int countBits(int n) {
    var count = 0;
    while (n != 0) {
        n &= (n - 1);
        count++;
    }
    return count;
}
```

**۶. `Integer.bitCount` (بهینه):**
```java
Integer.bitCount(7);   // 3
Integer.bitCount(-1);  // 32
```

**۷. معکوس کردن بیت‌ها:**
```java
Integer.reverse(5);   // 10100000...
Integer.reverseBytes(0x12345678);
```

**۸. Swap دو عدد بدون temp:**
```java
a ^= b;
b ^= a;
a ^= b;
```

**۹. XOR trick — پیدا کردن عدد یکتا:**
```java
public int singleNumber(int[] nums) {
    var result = 0;
    for (var n : nums) result ^= n;
    return result;
}
```

**۱۰. چک کردن بیت i:**
```java
boolean isSet = (n & (1 << i)) != 0;
```

**۱۱. ست کردن بیت i:**
```java
n |= (1 << i);
```

**۱۲. پاک کردن بیت i:**
```java
n &= ~(1 << i);
```

**۱۳. toggle بیت i:**
```java
n ^= (1 << i);
```

**نکته‌ی مصاحبه:** XOR سه ویژگی مهم دارد:
- `a ^ a = 0`
- `a ^ 0 = a`
- `a ^ b ^ a = b` (commutative)

**نکته‌ی Java 17:** `Integer.toBinaryString(n)` برای نمایش باینری. `Integer.parseInt(s, 2)` برای تبدیل.

### 🧩 بخش DSA (۶۰ دقیقه) — Single Number

**مسئله:** هر عدد دو بار تکرار شده به‌جز یکی. آن یکی را پیدا کن.

```java
public int singleNumber(int[] nums) {
    var result = 0;
    for (var n : nums) result ^= n;
    return result;
}
```

- **پیچیدگی:** زمان O(n)، فضا O(1)
- **الگو:** XOR

**نسخه‌ی پیشرفته — Single Number II (هر عدد ۳ بار):**
```java
public int singleNumber(int[] nums) {
    var ones = 0;
    var twos = 0;
    for (var n : nums) {
        ones = (ones ^ n) & ~twos;
        twos = (twos ^ n) & ~ones;
    }
    return ones;
}
```

**نسخه‌ی Single Number III (دو عدد یکتا):**
```java
public int[] singleNumber(int[] nums) {
    var xor = 0;
    for (var n : nums) xor ^= n;
    var diff = xor & (-xor); // کم‌ارزش‌ترین بیت متفاوت
    var a = 0;
    var b = 0;
    for (var n : nums) {
        if ((n & diff) == 0) a ^= n;
        else b ^= n;
    }
    return new int[]{a, b};
}
```

### ✍️ تمرین
مسائل `Counting Bits` و `Reverse Bits` را حل کن.

---

## 📅 روز ۷: مرور + پروژه

### 🎯 کارهای امروز

1. **مرور JVM**: Heap, Stack, Metaspace, GC
2. **مرور Profiling**: `jcmd`, `jmap`, `jstack`, JFR
3. **مرور DP**: 1D, 2D, Knapsack, LCS, LIS
4. **مرور Bit Manipulation**: XOR, bit tricks
5. **بازنویسی DSA**:
    - Coin Change
    - Partition Equal Subset Sum
    - Edit Distance
6. **Mock Interview**: با تایمر ۴۵ دقیقه، `Word Break` را با DP حل کن.
7. **پروژه‌ی هفته: JVM Health Monitor**

### 🎁 پروژه‌ی هفته: JVM Health Monitor

یک برنامه که با `jcmd` و JFR، سلامت JVM را مانیتور می‌کند.

```java
import java.io.*;
import java.lang.management.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;

public class JvmHealthMonitor {
    
    private final List<Snapshot> history = new ArrayList<>();
    
    public record Snapshot(
        LocalDateTime time,
        long heapUsed,
        long heapMax,
        long nonHeapUsed,
        int threadCount,
        long gcCount,
        long gcTimeMs,
        double cpuLoad
    ) {}
    
    public Snapshot capture() {
        var mem = ManagementFactory.getMemoryMXBean();
        var heap = mem.getHeapMemoryUsage();
        var nonHeap = mem.getNonHeapMemoryUsage();
        var threads = ManagementFactory.getThreadMXBean();
        var gcBeans = ManagementFactory.getGarbageCollectorMXBeans();
        var os = ManagementFactory.getOperatingSystemMXBean();
        
        long gcCount = 0, gcTime = 0;
        for (var gc : gcBeans) {
            gcCount += gc.getCollectionCount();
            gcTime += gc.getCollectionTime();
        }
        
        var cpuLoad = -1.0;
        if (os instanceof com.sun.management.OperatingSystemMXBean sunOs) {
            cpuLoad = sunOs.getProcessCpuLoad() * 100;
        }
        
        var snapshot = new Snapshot(
            LocalDateTime.now(),
            heap.getUsed() / 1024 / 1024,
            heap.getMax() / 1024 / 1024,
            nonHeap.getUsed() / 1024 / 1024,
            threads.getThreadCount(),
            gcCount,
            gcTime,
            cpuLoad
        );
        history.add(snapshot);
        return snapshot;
    }
    
    public void printReport() {
        System.out.println("=== JVM Health Report ===");
        System.out.printf("%-20s %10s %10s %10s %10s %10s%n",
            "Time", "Heap(MB)", "Max(MB)", "Threads", "GC Count", "GC(ms)");
        
        for (var s : history) {
            System.out.printf("%-20s %10d %10d %10d %10d %10d%n",
                s.time().toLocalTime(),
                s.heapUsed(), s.heapMax(),
                s.threadCount(), s.gcCount(), s.gcTimeMs());
        }
    }
    
    public void detectLeaks() {
        if (history.size() < 3) return;
        var first = history.get(0);
        var last = history.get(history.size() - 1);
        
        var heapGrowth = last.heapUsed() - first.heapUsed();
        var minutes = java.time.Duration.between(first.time(), last.time()).toMinutes();
        
        if (minutes > 0 && heapGrowth > 50) {
            System.out.printf("⚠️ Heap رشد %d MB در %d دقیقه — احتمال leak%n",
                heapGrowth, minutes);
        } else {
            System.out.println("✅ Heap پایدار است");
        }
    }
    
    public void run(int intervalSeconds, int durationSeconds) 
            throws InterruptedException {
        var end = System.currentTimeMillis() + durationSeconds * 1000L;
        
        while (System.currentTimeMillis() < end) {
            var s = capture();
            System.out.printf("[%s] Heap: %d/%d MB, Threads: %d, GC: %d%n",
                s.time().toLocalTime(), s.heapUsed(), s.heapMax(),
                s.threadCount(), s.gcCount());
            Thread.sleep(intervalSeconds * 1000L);
        }
        
        printReport();
        detectLeaks();
    }
    
    public static void main(String[] args) throws Exception {
        var monitor = new JvmHealthMonitor();
        
        // شبیه‌سازی بار
        var executor = Executors.newFixedThreadPool(4);
        for (var i = 0; i < 4; i++) {
            executor.submit(() -> {
                var list = new ArrayList<byte[]>();
                while (true) {
                    list.add(new byte[1024 * 100]); // 100KB
                    if (list.size() > 1000) list.clear();
                    try { Thread.sleep(10); } catch (InterruptedException e) { return; }
                }
            });
        }
        
        monitor.run(2, 20); // هر ۲ ثانیه، برای ۲۰ ثانیه
        executor.shutdownNow();
    }
}
```

**توسعه‌ی اختیاری:**
- JFR recording و تحلیل خودکار
- `jcmd <pid> GC.heap_info` را از `ProcessBuilder` صدا بزن
- هشدار در صورت GC pause طولانی
- ذخیره در CSV و رسم نمودار
- Metric export به Prometheus
- مقایسه با baseline

**نکته:** این پروژه به تو نشان می‌دهد چطور در production، JVM را مانیتور کنی. این مهارت در مصاحبه‌های Senior خیلی ارزشمند است.

### 📝 چک‌لیست پایان هفته

- [ ] ساختار JVM را می‌شناسم (Heap, Stack, Metaspace)
- [ ] تفاوت GCها را می‌دانم (Serial, Parallel, G1, ZGC)
- [ ] با `jcmd`, `jmap`, `jstack` کار کرده‌ام
- [ ] JFR را اجرا و تحلیل کرده‌ام
- [ ] 1D DP را پیاده کرده‌ام (Climbing Stairs, House Robber)
- [ ] 2D DP را پیاده کرده‌ام (LCS, Edit Distance)
- [ ] Knapsack (0/1 و Unbounded) را می‌دانم
- [ ] Bit Manipulation tricks را بلدم
- [ ] ۷ مسئله‌ی DSA را حل کرده‌ام
- [ ] پروژه‌ی JVM Health Monitor را کامل کرده‌ام
- [ ] Mock Interview انجام داده‌ام

---

## 📊 آمار پایان هفته ۷

| مورد | تعداد |
|---|---|
| ویژگی‌های Java 17 | ۴ (JVM, GC, Profiling, Migration) |
| الگوهای DP | ۵ (1D, 2D, Knapsack, LCS, LIS) |
| Bit Tricks | ۱۳ |
| مسائل DSA | ۷ |
| پروژه | ۱ |
| Mock Interview | ۱ |
| **مجموع مسائل تا الان** | **۴۹** |
| **مجموع ویژگی‌ها تا الان** | **۲۸** |

---

## 💡 نکات مهم برای هفته ۸ (آخر)

هفته‌ی آخر، **مرور و Mock Interview** است:
- مرور تمام مفاهیم (Records, Sealed, Pattern Matching, Stream, GC, DP)
- ۷ Mock Interview کامل (یکی هر روز)
- پروژه‌ی نهایی: **LRU Cache + Rate Limiter**
- پورتفولیو و آماده‌سازی برای مصاحبه

اگر خواستی، می‌توانم:
- **هفته‌ی هشتم** را با همین سطح جزئیات بنویسم
- یک **پروژه‌ی Maven کامل** با تمام کدهای هفت هفته بسازم
- یک **آزمون خودارزیابی جامع** برای مرور کل مسیر طراحی کنم
- یک **راهنمای آماده‌سازی مصاحبه** (Behavioral + Technical) بنویسم

کدام را اول شروع کنیم؟
عالی! هفته‌ی هشتم آخرین هفته است و روی **مرور جامع**، **Mock Interview**، و **پروژه‌ی نهایی** تمرکز دارد. این هفته، هفته‌ی تثبیت است — نه یادگیری چیز جدید، بلکه تبدیل دانش به مهارت.

---

## 📅 هفته ۸ — ساختار کلی

هر روز شامل سه بخش است:
- **مرور (۳۰ دقیقه):** مرور یک حوزه‌ی Java 17 یا DSA
- **Mock Interview (۶۰ دقیقه):** یک مسئله با تایمر، به‌صورت واقعی
- **بازتاب (۱۵ دقیقه):** نوشتن نقاط قوت و ضعف

**نکته‌ی مهم:** در این هفته، از IDE استفاده‌ی خودکار (autocomplete) را کم کن. در مصاحبه‌های واقعی معمولاً باید کد را تایپ کنی. پس با یک ادیتور ساده یا کاغذ تمرین کن.

---

## 📅 روز ۱: مرور Java 17 — زبان

### 🎓 بخش مرور (۳۰ دقیقه)

**چک‌لیست درونی:** بدون نگاه کردن به منابع، این سؤالات را جواب بده:

**۱. `var`**
- کجا مجاز است؟ کجا نه؟
- آیا داینامیک تایپینگ است؟
- تفاوت با `Object` چیست؟

**۲. Records**
- چه چیزی خودکار ساخته می‌شود؟
- Compact constructor چیست؟
- آیا می‌تواند `extends` کند؟
- آیا می‌تواند mutable باشد؟

**۳. Sealed**
- سه حالت زیرکلاس چیست؟
- چرا با Record ترکیب می‌شود؟
- محدودیت ماژول/پکیج چیست؟

**۴. Pattern Matching**
- `instanceof` pattern چه مشکلی را حل می‌کند؟
- تفاوت `switch` expression و `switch` statement؟
- Guarded pattern چیست؟
- Record deconstruction pattern چیست؟

**۵. Text Blocks**
- قوانین incidental whitespace چیست؟
- `\s` و `\` چه کاری می‌کنند؟

**۶. Stream**
- تفاوت intermediate و terminal operation؟
- `map` vs `flatMap`؟
- `Stream` یک‌بار مصرف است؟
- `parallelStream` کجا مفید است؟

**۷. Optional**
- چه وقت استفاده کنیم؟ چه وقت نه؟
- `orElse` vs `orElseGet`؟
- چرا نباید field باشد؟

**پاسخ‌ها را روی کاغذ بنویس.** بعد با منابع چک کن.

### 🎤 Mock Interview ۱ (۶۰ دقیقه)

**مسئله:** LRU Cache (LeetCode 146)

```
یک LRU (Least Recently Used) Cache طراحی کن که 
get(key) و put(key, value) را در O(1) انجام دهد.
ظرفیت مشخص است.
```

**قوانین Mock:**
- تایمر ۴۵ دقیقه
- اول سؤال را تکرار کن و edge caseها را بپرس
- رویکرد را قبل از کد توضیح بده
- کد را با ادیتور ساده بنویس (نه autocomplete)
- تست دستی روی چند مثال

**راه‌حل:** HashMap + DoublyLinkedList. (در ادامه‌ی پروژه می‌بینی)

### 📝 بازتاب
- کدام ویژگی Java 17 را راحت جواب دادی؟
- کدام یکی سخت بود؟ چرا؟
- در Mock Interview چقدر وقت صرف کردی؟ کجا گیر کردی؟

---

## 📅 روز ۲: مرور DSA — آرایه، رشته، HashMap

### 🎓 بخش مرور (۳۰ دقیقه)

**الگوهای کلیدی این حوزه:**
1. **Two Pointers** — آرایه/رشته مرتب، جستجوی جفت
2. **Sliding Window** — زیرآرایه/زیررشته با شرط
3. **HashMap/HashSet** — شمارش، جستجو، frequency
4. **Prefix Sum** — مجموع زیرآرایه
5. **Kadane** — حداکثر زیرآرایه

**سؤالات مرور:**
- تفاوت Two Pointers و Sliding Window؟
- چه وقت از HashMap به‌جای Sort استفاده کنیم؟
- Prefix Sum چه مشکلی را حل می‌کند؟

**تمرین سریع روی کاغذ:**
- Two Sum را در ۳ دقیقه بنویس
- Longest Substring را در ۵ دقیقه بنویس

### 🎤 Mock Interview ۲ (۶۰ دقیقه)

**مسئله:** Minimum Window Substring (LeetCode 76)

```
دو رشته s و t. کوچک‌ترین زیررشته‌ی s که همه‌ی 
کاراکترهای t را (با فرکانس) داشته باشد را پیدا کن.
```

**راه‌حل:** Sliding Window با frequency map.

**نکات مصاحبه:**
- چرا Sliding Window کار می‌کند؟
- چرا می‌توان پنجره را بزرگ کرد وقتی شرط برقرار نیست؟
- چرا می‌توان کوچک کرد وقتی شرط برقرار است؟

### 📝 بازتاب
- آیا در ۴۵ دقیقه تمام شد؟
- آیا edge caseها را پرسیدی؟
- آیا راه‌حل بهینه را پیدا کردی؟

---

## 📅 روز ۳: مرور DSA — درخت، Heap، Trie

### 🎓 بخش مرور (۳۰ دقیقه)

**الگوهای کلیدی:**
1. **DFS (Recursive)** — پیمایش‌های درخت
2. **BFS (Queue)** — Level-order
3. **BST Properties** — Inorder صعودی، جستجو
4. **Heap** — Kth، Top K، Merge K
5. **Trie** — Prefix search

**سؤالات مرور:**
- Inorder/Preorder/Postorder چه تفاوتی دارند؟
- Morris Traversal چرا O(1) فضا است؟
- Heap vs TreeMap چه وقت؟
- Trie چه وقتی از HashMap بهتر است؟

**تمرین روی کاغذ:**
- `maxDepth` را بنویس (recursive + iterative)
- `levelOrder` را بنویس
- `validateBST` را بنویس

### 🎤 Mock Interview ۳ (۶۰ دقیقه)

**مسئله:** Serialize and Deserialize Binary Tree (LeetCode 297)

```
یک درخت دودویی را به رشته تبدیل کن و برگردان.
```

**راه‌حل:** Preorder با `null` marker. یا BFS.

**نکات:**
- چرا Preorder + null کافی است؟
- چرا Inorder تنها کافی نیست؟
- آیا می‌توان بدون null marker؟ (بله با تعداد)

### 📝 بازتاب
- آیا با `StringBuilder` کد را بهینه کردی؟
- آیا از `Arrays.stream` برای split استفاده کردی؟
- edge case: درخت خالی، یک گره، چپ‌گرا، راست‌گرا

---

## 📅 روز ۴: مرور DSA — گراف، Union-Find، Dijkstra

### 🎓 بخش مرور (۳۰ دقیقه)

**الگوهای کلیدی:**
1. **BFS/DFS روی Grid** — Number of Islands
2. **Topological Sort** — Course Schedule
3. **Union-Find** — Components, Cycle
4. **Dijkstra** — Shortest Path
5. **Bellman-Ford** — Negative weights

**سؤالات مرور:**
- BFS vs DFS — چه وقت؟
- Topological Sort روی گراف با cycle چه می‌شود؟
- Dijkstra چرا روی وزن منفی کار نمی‌کند؟
- Union-Find با Path Compression چقدر سریع است؟

**تمرین روی کاغذ:**
- Union-Find را از حفظ بنویس
- BFS با distance بنویس

### 🎤 Mock Interview ۴ (۶۰ دقیقه)

**مسئله:** Alien Dictionary (LeetCode 269)

```
لیستی از کلمات مرتب‌شده بر اساس الفبای بیگانه.
ترتیب حروف الفبا را پیدا کن.
```

**راه‌حل:** Topological Sort روی گراف حروف.

**نکات:**
- چگونه یال‌ها را استخراج می‌کنی؟
- چرا باید cycle را چک کنی؟
- edge case: ترتیب نامعتبر (مثل `["abc", "ab"]`)

### 📝 بازتاب
- آیا edge case `["abc", "ab"]` را ديدي؟
- آیا cycle detection را درست انجام دادی؟

---

## 📅 روز ۵: مرور DSA — DP و Bit Manipulation

### 🎓 بخش مرور (۳۰ دقیقه)

**الگوهای کلیدی:**
1. **1D DP** — Fibonacci, House Robber
2. **2D DP** — LCS, Edit Distance
3. **Knapsack** — 0/1, Unbounded
4. **LIS** — O(n²) و O(n log n)
5. **Bit Tricks** — XOR, mask, count

**سؤالات مرور:**
- سه رویکرد DP: recursive, memoization, tabulation؟
- تفاوت 0/1 و Unbounded Knapsack؟
- چرا در 0/1 Knapsack از بالا به پایین iterate می‌کنیم؟
- XOR سه ویژگی مهم؟

**تمرین روی کاغذ:**
- Coin Change بنویس
- LCS بنویس
- Single Number بنویس

### 🎤 Mock Interview ۵ (۶۰ دقیقه)

**مسئله:** Word Break (LeetCode 139)

```
رشته s و dictionary. آیا می‌توان s را به کلمات dictionary تقسیم کرد؟
```

**راه‌حل:**
```java
public boolean wordBreak(String s, List<String> wordDict) {
    var dict = new HashSet<>(wordDict);
    var n = s.length();
    var dp = new boolean[n + 1];
    dp[0] = true;
    
    for (var i = 1; i <= n; i++) {
        for (var j = 0; j < i; j++) {
            if (dp[j] && dict.contains(s.substring(j, i))) {
                dp[i] = true;
                break;
            }
        }
    }
    return dp[n];
}
```

**نکات:**
- چرا `dp[0] = true`؟
- چرا `break` می‌زنیم؟
- پیچیدگی: O(n² × m) زمان، O(n) فضا

### 📝 بازتاب
- آیا در ۴۵ دقیقه تمام شد؟
- آیا از `HashSet` برای dictionary استفاده کردی؟
- آیا substring را بهینه کردی؟

---

## 📅 روز ۶: مرور Java 17 — پلتفرم و JVM

### 🎓 بخش مرور (۳۰ دقیقه)

**سؤالات کلیدی:**

**۱. GC:**
- چرا G1 پیش‌فرض شد؟
- تفاوت G1 و ZGC؟
- `MaxGCPauseMillis` چه می‌کند؟

**۲. JVM Memory:**
- تفاوت Heap و Stack؟
- Metaspace کجاست؟
- PermGen چه شد؟

**۳. Profiling:**
- `jcmd`, `jmap`, `jstack` برای چه؟
- JFR چیست؟
- چطور memory leak پیدا می‌کنی؟

**۴. Migration:**
- چالش‌های 8 → 17؟
- `javax` → `jakarta`؟
- `--add-opens`؟

**۵. Modules:**
- `module-info.java` چه چیزی دارد؟
- `requires transitive`؟
- `exports ... to`؟

**پاسخ‌ها را روی کاغذ بنویس.**

### 🎤 Mock Interview ۶ (۶۰ دقیقه)

**مسئله‌ی Design:** Rate Limiter

```
یک Rate Limiter طراحی کن که در یک پنجره‌ی زمانی، 
حداکثر N درخواست را قبول کند. Thread-safe باشد.
```

**راه‌حل ۱ — Fixed Window:**
```java
public class FixedWindowRateLimiter {
    private final int maxRequests;
    private final long windowMs;
    private final Map<String, Window> windows = new ConcurrentHashMap<>();
    
    private static class Window {
        long startTime;
        AtomicInteger count = new AtomicInteger(0);
    }
    
    public FixedWindowRateLimiter(int maxRequests, long windowMs) {
        this.maxRequests = maxRequests;
        this.windowMs = windowMs;
    }
    
    public boolean allow(String key) {
        var now = System.currentTimeMillis();
        var window = windows.computeIfAbsent(key, k -> {
            var w = new Window();
            w.startTime = now;
            return w;
        });
        
        synchronized (window) {
            if (now - window.startTime >= windowMs) {
                window.startTime = now;
                window.count.set(0);
            }
            return window.count.incrementAndGet() <= maxRequests;
        }
    }
}
```

**راه‌حل ۲ — Sliding Window Log:**
```java
public class SlidingWindowRateLimiter {
    private final int maxRequests;
    private final long windowMs;
    private final Map<String, Deque<Long>> logs = new ConcurrentHashMap<>();
    
    public SlidingWindowRateLimiter(int maxRequests, long windowMs) {
        this.maxRequests = maxRequests;
        this.windowMs = windowMs;
    }
    
    public synchronized boolean allow(String key) {
        var now = System.currentTimeMillis();
        var log = logs.computeIfAbsent(key, k -> new ArrayDeque<>());
        
        while (!log.isEmpty() && now - log.peekFirst() >= windowMs) {
            log.pollFirst();
        }
        
        if (log.size() < maxRequests) {
            log.offerLast(now);
            return true;
        }
        return false;
    }
}
```

**نکات:**
- Fixed Window ساده‌تر، ولی burst در مرز پنجره دارد.
- Sliding Window دقیق‌تر، ولی حافظه بیشتر.
- Token Bucket و Leaky Bucket هم هست.

### 📝 بازتاب
- آیا thread-safety را رعایت کردی؟
- آیا `ConcurrentHashMap` را انتخاب کردی؟
- آیا می‌توانی الگوریتم‌های مختلف را مقایسه کنی؟

---

## 📅 روز ۷: Mock Interview نهایی + پروژه‌ی نهایی

### 🎓 صبح: Mock Interview ۷ (۶۰ دقیقه)

**مسئله:** LRU Cache — نسخه‌ی نهایی

```
LRU Cache با ظرفیت مشخص. get و put در O(1).
```

**راه‌حل نهایی:**
```java
public class LRUCache<K, V> {
    
    private static class Node<K, V> {
        K key;
        V value;
        Node<K, V> prev, next;
        Node(K key, V value) { this.key = key; this.value = value; }
    }
    
    private final int capacity;
    private final Map<K, Node<K, V>> map = new HashMap<>();
    private final Node<K, V> head = new Node<>(null, null);
    private final Node<K, V> tail = new Node<>(null, null);
    
    public LRUCache(int capacity) {
        this.capacity = capacity;
        head.next = tail;
        tail.prev = head;
    }
    
    public V get(K key) {
        var node = map.get(key);
        if (node == null) return null;
        moveToFront(node);
        return node.value;
    }
    
    public void put(K key, V value) {
        var node = map.get(key);
        if (node != null) {
            node.value = value;
            moveToFront(node);
            return;
        }
        
        if (map.size() == capacity) {
            var lru = tail.prev;
            remove(lru);
            map.remove(lru.key);
        }
        
        var newNode = new Node<>(key, value);
        addFirst(newNode);
        map.put(key, newNode);
    }
    
    private void moveToFront(Node<K, V> node) {
        remove(node);
        addFirst(node);
    }
    
    private void remove(Node<K, V> node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }
    
    private void addFirst(Node<K, V> node) {
        node.next = head.next;
        node.prev = head;
        head.next.prev = node;
        head.next = node;
    }
}
```

**نکات مهم:**
- چرا `HashMap` + `DoublyLinkedList`؟
- چرا از `LinkedHashMap` استفاده نکردم؟
- چرا `Node` را `static` کردم؟

### 🎁 پروژه‌ی نهایی: Mini Cache Library

یک کتابخانه‌ی کوچک با چند نوع Cache:

```java
public interface Cache<K, V> {
    V get(K key);
    void put(K key, V value);
    int size();
    void clear();
}

// پیاده‌سازی‌ها
public class LruCache<K, V> implements Cache<K, V> { /* ... */ }
public class TtlCache<K, V> implements Cache<K, V> { /* ... */ }
public class LfuCache<K, V> implements Cache<K, V> { /* ... */ }

// Builder با Java 17 idiom
public class CacheBuilder<K, V> {
    private int capacity = 100;
    private Duration ttl;
    private Policy policy = Policy.LRU;
    
    public enum Policy { LRU, LFU, TTL }
    
    public CacheBuilder<K, V> capacity(int c) { this.capacity = c; return this; }
    public CacheBuilder<K, V> ttl(Duration d) { this.ttl = d; return this; }
    public CacheBuilder<K, V> policy(Policy p) { this.policy = p; return this; }
    
    public Cache<K, V> build() {
        return switch (policy) {
            case LRU -> new LruCache<>(capacity);
            case LFU -> new LfuCache<>(capacity);
            case TTL -> new TtlCache<>(capacity, ttl);
        };
    }
}
```

**توسعه‌ی اختیاری:**
- Thread-safe wrapper
- Statistics (hit/miss rate)
- Loading cache (با `Function<K, V>`)
- Eviction listener
- Metrics export

### 📝 بازتاب نهایی — پایان ۸ هفته

**سؤالات:**
1. کدام هفته سخت‌ترین بود؟ چرا؟
2. کدام ویژگی Java 17 بیشترین تأثیر را روی کد روزمره‌ات دارد؟
3. کدام الگوی DSA را هنوز راحت نیستی؟
4. چه چیزی را باید بعد از این ۸ هفته ادامه بدهی؟
5. آیا آماده‌ی مصاحبه هستی؟ چه چیزی کم داری؟

**یک فایل `reflection.md` بنویس با پاسخ به این سؤالات.**

---

## 📊 آمار کل ۸ هفته

| مورد | تعداد |
|---|---|
| ویژگی‌های Java 17 | ۲۸+ |
| الگوهای DSA | ۲۰+ |
| ساختارهای داده | ۱۰+ |
| مسائل DSA | ۵۰+ |
| Mock Interview | ۸ |
| پروژه | ۸ |
| فایل‌های Markdown | ۵۶+ |

---

## 🎯 برنامه‌ی بعد از ۸ هفته

**اگر آماده‌ی مصاحبه هستی:**
- **LeetCode Contest** هفتگی برای سرعت
- **Mock Interview** با دوستان یا Pramp/Interviewing.io
- **System Design** برای سطوح بالاتر
- **Behavioral** مصاحبه (STAR method)
- **CV و پورتفولیو** روی GitHub

**اگر می‌خواهی عمیق‌تر شوی:**
- **Concurrency** عمیق‌تر (JMM, locks, atomics)
- **JVM Internals** (JIT, bytecode, class loading)
- **Spring Framework 6** و Java 17 idioms
- **Reactive** (Project Reactor, RxJava)
- **Kotlin** (اگر می‌خواهی از Java مهاجرت کنی)

**اگر می‌خواهی تدریس کنی:**
- یک **بلاگ** بنویس
- یک **ویدیو** بساز
- یک **دوره** طراحی کن
- **متن‌باز** (open source) مشارکت کن

---

## 🏆 نکته‌ی پایانی

بعد از ۸ هفته، تو:
- **Java 17** را عمیق می‌شناسی
- **۵۰+ مسئله‌ی DSA** را حل کرده‌ای
- **۸ پروژه** ساخته‌ای
- **۸ Mock Interview** انجام داده‌ای
- یک **پورتفولیوی واقعی** داری

حس "چیزی بلد نیستم" که اول گفتی، الان باید جای خود را به **اطمینان** داده باشد. اما یادت باشد:

> **مهارت با تکرار ساخته می‌شود، نه با خواندن.**

پس بعد از این ۸ هفته، **متوقف نشو**. هر روز ۳۰ دقیقه DSA، هر هفته یک Mock، هر ماه یک پروژه‌ی جدید. این چرخه‌ی رشد است.

---

## 📄 می‌خواهی چه چیزی برایت بسازم؟

الان که ۸ هفته تمام شد، می‌توانم:
1. یک **پروژه‌ی Maven کامل** با تمام کدهای ۸ هفته بسازم
2. یک **بانک ۱۰۰ سؤال مصاحبه** درباره Java 17 + DSA طراحی کنم
3. یک **راهنمای Behavioral Interview** با STAR method بنویسم
4. یک **System Design Primer** برای مصاحبه‌های Senior بسازم
5. **CV و LinkedIn** را برای موقعیت‌های Java 17 بهینه کنیم

کدام را اول شروع کنیم؟
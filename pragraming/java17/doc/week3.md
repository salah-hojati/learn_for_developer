<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">


عالی! هفته‌ی سوم روی **Stream API** (قلب کد مدرن جاوا)، **Linked List**, **Stack**, و **Queue** تمرکز دارد. این هفته پلی است بین Java 8 که بلدی و Java 17 که می‌خواهی مسلط شوی.

---

## 📅 هفته ۳ — روز ۱: Stream API (مقدمه)

### 🎓 بخش Java 17 (۳۰ دقیقه)

**Stream** یک pipeline از عملیات روی داده است. Stream خودش داده نگه نمی‌دارد؛ فقط روی منبع (Collection, Array, I/O) کار می‌کند.

**ساخت Stream:**
</div>


```java
var list = List.of(1, 2, 3, 4, 5);

var s1 = list.stream();                          // از Collection
var s2 = Arrays.stream(new int[]{1, 2, 3});     // از Array
var s3 = Stream.of(1, 2, 3);                     // مستقیم
var s4 = IntStream.range(1, 6);                  // 1..5
var s5 = IntStream.rangeClosed(1, 5);            // 1..5 شامل 5
var s6 = Stream.iterate(1, n -> n * 2).limit(5); // 1,2,4,8,16
var s7 = Stream.generate(Math::random).limit(3); // تصادفی
```
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">


**دو نوع عملیات:**
- **Intermediate** (تنبل): `map`, `filter`, `sorted`, `distinct`, `limit`, `skip`, `peek`, `flatMap`
- **Terminal** (اجراشونده): `collect`, `forEach`, `reduce`, `count`, `anyMatch`, `allMatch`, `findFirst`, `min`, `max`, `toArray`

**مثال پایه:**
</div>


```java
var result = list.stream()
    .filter(n -> n % 2 == 0)
    .map(n -> n * n)
    .sorted()
    .toList();  // Java 16+
// [4, 16]
```
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">


**نکات مهم:**
- Stream یک‌بار مصرف است. بعد از terminal operation، دیگر قابل استفاده نیست.
- عملیات‌ها تنبل‌اند: تا terminal نرسد، اجرا نمی‌شوند.
- Stream موازی (`parallelStream`) فقط وقتی مفید است که کار سنگین و داده زیاد باشد.

**نکته‌ی مصاحبه:** `map` یک‌به‌یک است، `flatMap` یک‌به‌چند. `Stream` vs `Collection`: Collection داده دارد، Stream pipeline است.

### 🧩 بخش DSA (۶۰ دقیقه) — Reverse Linked List

**مسئله:** یک singly linked list را معکوس کن.

**تعریف Node:**
</div>


```java
class ListNode {
    int val;
    ListNode next;
    ListNode(int val) { this.val = val; }
    ListNode(int val, ListNode next) { this.val = val; this.next = next; }
}
```
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">


**راه‌حل — Iterative:**
</div>


```java
public ListNode reverseList(ListNode head) {
    ListNode prev = null;
    var curr = head;
    while (curr != null) {
        var next = curr.next;
        curr.next = prev;
        prev = curr;
        curr = next;
    }
    return prev;
}
```

**راه‌حل — Recursive:**
```java
public ListNode reverseList(ListNode head) {
    if (head == null || head.next == null) return head;
    var newHead = reverseList(head.next);
    head.next.next = head;
    head.next = null;
    return newHead;
}
```

- **پیچیدگی:** زمان O(n)، فضا O(1) iterative / O(n) recursive
- **الگو:** Pointer Manipulation

**نکته‌ی Java 17:** می‌توانی از `record` برای Node استفاده کنی اگر immutable باشد، ولی در این مسئله نیاز به تغییر `next` داریم، پس `class` بهتر است.

### ✍️ تمرین
یک `Stream<Integer>` از اعداد ۱ تا ۱۰۰ بساز، اعداد بخش‌پذیر بر ۳ را فیلتر کن، مربعشان را بگیر و جمع کن.

---

## 📅 روز ۲: Stream — `map`, `filter`, `flatMap`

### 🎓 بخش Java 17 (۳۰ دقیقه)

**`map`** — تبدیل هر عنصر:
```java
var names = List.of("ali", "sara", "reza");
var upper = names.stream()
    .map(String::toUpperCase)
    .toList();
// [ALI, SARA, REZA]
```

**`filter`** — انتخاب بر اساس شرط:
```java
var evens = IntStream.rangeClosed(1, 10)
    .filter(n -> n % 2 == 0)
    .boxed()
    .toList();
// [2, 4, 6, 8, 10]
```

**`flatMap`** — یک‌به‌چند:
```java
var words = List.of("hello world", "java 17");
var letters = words.stream()
    .flatMap(w -> Arrays.stream(w.split(" ")))
    .toList();
// [hello, world, java, 17]
```

**مثال ترکیبی — فایل و کلمات:**
```java
var wordCount = Files.lines(Path.of("file.txt"))
    .flatMap(line -> Arrays.stream(line.split("\\s+")))
    .map(String::toLowerCase)
    .filter(w -> !w.isBlank())
    .collect(Collectors.groupingBy(w -> w, Collectors.counting()));
```

**نکته‌ی مصاحبه:** `flatMap` برای "unwrapping" ساختارهای nested است. مثال: `List<List<T>>` → `List<T>`.

### 🧩 بخش DSA (۶۰ دقیقه) — Merge Two Sorted Lists

**مسئله:** دو لیست مرتب را در یک لیست مرتب ادغام کن.

```java
public ListNode mergeTwoLists(ListNode l1, ListNode l2) {
    var dummy = new ListNode(0);
    var tail = dummy;
    while (l1 != null && l2 != null) {
        if (l1.val <= l2.val) {
            tail.next = l1;
            l1 = l1.next;
        } else {
            tail.next = l2;
            l2 = l2.next;
        }
        tail = tail.next;
    }
    tail.next = (l1 != null) ? l1 : l2;
    return dummy.next;
}
```

- **پیچیدگی:** زمان O(n+m)، فضا O(1)
- **الگو:** Two Pointers + Dummy Node

**نکته:** Dummy Node یک trick کلاسیک است که کد را ساده می‌کند و edge caseها را حذف می‌کند.

### ✍️ تمرین
یک `List<List<Integer>>` بساز و با `flatMap` همه را در یک `List<Integer>` بریز، سپس `distinct` و `sorted` کن.

---

## 📅 روز ۳: Stream — `reduce`, `collect`, `groupingBy`

### 🎓 بخش Java 17 (۳۰ دقیقه)

**`reduce`** — تجمیع:
```java
var sum = IntStream.rangeClosed(1, 10).reduce(0, Integer::sum);
// 55

var product = IntStream.rangeClosed(1, 5).reduce(1, (a, b) -> a * b);
// 120

var max = list.stream().reduce(Integer::max);
```

**`collect`** — جمع‌آوری:
```java
var list = stream.collect(Collectors.toList());       // یا .toList()
var set = stream.collect(Collectors.toSet());
var map = stream.collect(Collectors.toMap(
    User::id, User::name
));
var joined = names.stream().collect(Collectors.joining(", "));
```

**`groupingBy`:**
```java
var byDept = employees.stream()
    .collect(Collectors.groupingBy(Employee::department));
// Map<String, List<Employee>>

var countByDept = employees.stream()
    .collect(Collectors.groupingBy(
        Employee::department,
        Collectors.counting()
    ));
// Map<String, Long>

var avgSalaryByDept = employees.stream()
    .collect(Collectors.groupingBy(
        Employee::department,
        Collectors.averagingDouble(Employee::salary)
    ));
```

**`partitioningBy`:**
```java
var partitioned = numbers.stream()
    .collect(Collectors.partitioningBy(n -> n > 0));
// Map<Boolean, List<Integer>>
```

**`teeing` (Java 12+):**
```java
var result = numbers.stream().collect(Collectors.teeing(
    Collectors.minBy(Integer::compareTo),
    Collectors.maxBy(Integer::compareTo),
    (min, max) -> "Min: " + min.orElseThrow() + ", Max: " + max.orElseThrow()
));
```

**نکته‌ی مصاحبه:** `groupingBy` + downstream collector یکی از پرکاربردترین ترکیب‌هاست. در مصاحبه‌های SQL-محور جاوا، این را از تو می‌پرسند.

### 🧩 بخش DSA (۶۰ دقیقه) — Linked List Cycle

**مسئله:** آیا لیست حلقه دارد؟

**راه‌حل — Floyd's Tortoise and Hare:**
```java
public boolean hasCycle(ListNode head) {
    var slow = head;
    var fast = head;
    while (fast != null && fast.next != null) {
        slow = slow.next;
        fast = fast.next.next;
        if (slow == fast) return true;
    }
    return false;
}
```

- **پیچیدگی:** زمان O(n)، فضا O(1)
- **الگو:** Fast & Slow Pointers

**نسخه‌ی پیشرفته — پیدا کردن شروع حلقه:**
```java
public ListNode detectCycle(ListNode head) {
    var slow = head;
    var fast = head;
    while (fast != null && fast.next != null) {
        slow = slow.next;
        fast = fast.next.next;
        if (slow == fast) {
            var entry = head;
            while (entry != slow) {
                entry = entry.next;
                slow = slow.next;
            }
            return entry;
        }
    }
    return null;
}
```

### ✍️ تمرین
یک `List<Employee>` بساز و با `groupingBy` میانگین حقوق هر دپارتمان را حساب کن.

---

## 📅 روز ۴: Stack و Queue

### 🎓 بخش Java 17 (۳۰ دقیقه)

**Stack با `ArrayDeque`:**
```java
var stack = new ArrayDeque<Integer>();
stack.push(1);
stack.push(2);
stack.push(3);
System.out.println(stack.pop());  // 3
System.out.println(stack.peek()); // 2
System.out.println(stack.isEmpty()); // false
```

**چرا `ArrayDeque` و نه `Stack`؟**
- `Stack` قدیمی است و از `Vector` ارث‌بری می‌کند (synchronized، کند).
- `ArrayDeque` سریع‌تر، بدون synchronization اضافی.
- `ArrayDeque` هم stack و هم queue است.

**Queue با `ArrayDeque`:**
```java
var queue = new ArrayDeque<Integer>();
queue.offer(1);
queue.offer(2);
queue.offer(3);
System.out.println(queue.poll());  // 1
System.out.println(queue.peek());  // 2
```

**تفاوت متدها:**
| عملیات | صف (Queue) | پشته (Stack) |
|---|---|---|
| افزودن | `offer` / `add` | `push` / `addFirst` |
| حذف | `poll` / `remove` | `pop` / `removeFirst` |
| دیدن | `peek` | `peek` / `peekFirst` |

**`PriorityQueue`:**
```java
var pq = new PriorityQueue<Integer>(); // min-heap
pq.offer(3); pq.offer(1); pq.offer(2);
System.out.println(pq.poll()); // 1

// max-heap
var maxPq = new PriorityQueue<Integer>(Comparator.reverseOrder());
```

**نکته‌ی مصاحبه:** `ArrayDeque` هرگز `null` را قبول نمی‌کند (چون `null` نشانه‌ی خالی بودن است). `PriorityQueue` بر اساس heap پیاده شده، نه sorted list؛ پس iterate روی آن ترتیب ندارد.

### 🧩 بخش DSA (۶۰ دقیقه) — Valid Parentheses

**مسئله:** رشته‌ای از `()[]{}` داده شده. آیا معتبر است؟

```java
public boolean isValid(String s) {
    var stack = new ArrayDeque<Character>();
    for (var c : s.toCharArray()) {
        switch (c) {
            case '(', '[', '{' -> stack.push(c);
            case ')' -> { if (stack.isEmpty() || stack.pop() != '(') return false; }
            case ']' -> { if (stack.isEmpty() || stack.pop() != '[') return false; }
            case '}' -> { if (stack.isEmpty() || stack.pop() != '{') return false; }
        }
    }
    return stack.isEmpty();
}
```

**نکته‌ی Java 17:** از `switch` expression با `->` استفاده کردم که خوانا‌تر از `case ... break` است.

- **پیچیدگی:** زمان O(n)، فضا O(n)
- **الگو:** Stack

### ✍️ تمرین
یک `MyStack<T>` با `ArrayDeque` بنویس و متدهای `push`, `pop`, `peek`, `isEmpty` را پیاده کن.

---

## 📅 روز ۵: Stack — مسائل کلاسیک

### 🎓 بخش Java 17 (۳۰ دقیقه)

**الگوهای رایج Stack:**
- **Monotonic Stack** — برای پیدا کردن next greater/smaller element
- **Expression Evaluation** — infix/postfix
- **Backtracking iterative**
- **Balanced parentheses**

**Monotonic Stack مثال:**
```java
// Next Greater Element
public int[] nextGreater(int[] nums) {
    var n = nums.length;
    var result = new int[n];
    Arrays.fill(result, -1);
    var stack = new ArrayDeque<Integer>(); // ایندکس‌ها
    for (var i = 0; i < n; i++) {
        while (!stack.isEmpty() && nums[stack.peek()] < nums[i]) {
            result[stack.pop()] = nums[i];
        }
        stack.push(i);
    }
    return result;
}
```

**نکته:** Monotonic Stack یک تکنیک O(n) برای مسائلی است که به‌نظر O(n²) می‌رسند.

### 🧩 بخش DSA (۶۰ دقیقه) — Min Stack

**مسئله:** یک Stack طراحی کن که `push`, `pop`, `top`, `getMin` را در O(1) انجام دهد.

```java
class MinStack {
    private final ArrayDeque<Integer> stack = new ArrayDeque<>();
    private final ArrayDeque<Integer> minStack = new ArrayDeque<>();
    
    public void push(int val) {
        stack.push(val);
        if (minStack.isEmpty() || val <= minStack.peek()) {
            minStack.push(val);
        }
    }
    
    public void pop() {
        var val = stack.pop();
        if (val.equals(minStack.peek())) {
            minStack.pop();
        }
    }
    
    public int top() {
        return stack.peek();
    }
    
    public int getMin() {
        return minStack.peek();
    }
}
```

- **پیچیدگی:** همه‌ی عملیات‌ها O(1)
- **الگو:** Auxiliary Stack

**نکته:** استفاده از `equals` به‌جای `==` برای مقایسه‌ی `Integer` مهم است (چون `==` فقط برای مقادیر -128..127 درست است).

### ✍️ تمرین
با `nextGreater`، مسائل Next Smaller Element و Daily Temperatures را هم حل کن.

---

## 📅 روز ۶: Queue و Deque — مسائل

### 🎓 بخش Java 17 (۳۰ دقیقه)

**الگوهای رایج Queue:**
- **BFS** در گراف/درخت
- **Sliding Window Maximum** با Deque
- **Task Scheduling** با PriorityQueue
- **Producer-Consumer** با `BlockingQueue`

**Sliding Window Maximum — Monotonic Deque:**
```java
public int[] maxSlidingWindow(int[] nums, int k) {
    var deque = new ArrayDeque<Integer>(); // ایندکس‌ها، کاهشی
    var result = new int[nums.length - k + 1];
    var idx = 0;
    for (var i = 0; i < nums.length; i++) {
        // حذف از جلو اگر بیرون پنجره است
        while (!deque.isEmpty() && deque.peekFirst() <= i - k) {
            deque.pollFirst();
        }
        // حذف از عقب اگر کوچک‌تر از فعلی است
        while (!deque.isEmpty() && nums[deque.peekLast()] < nums[i]) {
            deque.pollLast();
        }
        deque.offerLast(i);
        // اضافه به نتیجه
        if (i >= k - 1) {
            result[idx++] = nums[deque.peekFirst()];
        }
    }
    return result;
}
```

**نکته‌ی مصاحبه:** این مسئله یکی از سخت‌ترین مسائل Queue است. اگر بتوانی با Monotonic Deque توضیح بدهی، امتیاز بزرگی می‌گیری.

### 🧩 بخش DSA (۶۰ دقیقه) — Implement Queue using Stacks

**مسئله:** یک Queue با دو Stack پیاده کن.

```java
class MyQueue {
    private final ArrayDeque<Integer> inStack = new ArrayDeque<>();
    private final ArrayDeque<Integer> outStack = new ArrayDeque<>();
    
    public void push(int x) {
        inStack.push(x);
    }
    
    public int pop() {
        if (outStack.isEmpty()) {
            while (!inStack.isEmpty()) {
                outStack.push(inStack.pop());
            }
        }
        return outStack.pop();
    }
    
    public int peek() {
        if (outStack.isEmpty()) {
            while (!inStack.isEmpty()) {
                outStack.push(inStack.pop());
            }
        }
        return outStack.peek();
    }
    
    public boolean empty() {
        return inStack.isEmpty() && outStack.isEmpty();
    }
}
```

- **پیچیدگی:** amortized O(1) برای هر عملیات
- **الگو:** Two Stacks

### ✍️ تمرین
یک `BlockingQueue` ساده با `ArrayDeque` و `synchronized` پیاده کن.

---

## 📅 روز ۷: مرور + پروژه

### 🎯 کارهای امروز

1. **مرور Stream API**: `map`, `filter`, `flatMap`, `reduce`, `collect`, `groupingBy`
2. **مرور Stack/Queue**: `ArrayDeque`, `PriorityQueue`, Monotonic Stack
3. **بازنویسی DSA**:
    - Reverse Linked List (هم iterative هم recursive)
    - Valid Parentheses (با `switch` expression)
    - Min Stack
4. **Mock Interview**: با تایمر ۴۵ دقیقه، `Sliding Window Maximum` را حل کن.
5. **پروژه‌ی هفته: Employee Analytics با Stream**

### 🎁 پروژه‌ی هفته: Employee Analytics

یک برنامه بنویس که یک `List<Employee>` می‌گیرد و با Stream این گزارش‌ها را می‌سازد:

```java
public record Employee(String name, String department, double salary, int age) {}

public class EmployeeAnalytics {
    
    // میانگین حقوق هر دپارتمان
    public static Map<String, Double> avgSalaryByDept(List<Employee> employees) {
        return employees.stream()
            .collect(Collectors.groupingBy(
                Employee::department,
                Collectors.averagingDouble(Employee::salary)
            ));
    }
    
    // گران‌ترین کارمند هر دپارتمان
    public static Map<String, Optional<Employee>> topPaidByDept(List<Employee> employees) {
        return employees.stream()
            .collect(Collectors.groupingBy(
                Employee::department,
                Collectors.maxBy(Comparator.comparingDouble(Employee::salary))
            ));
    }
    
    // اسامی مرتب‌شده‌ی کارمندان با حقوق بالای میانگین
    public static List<String> highEarners(List<Employee> employees) {
        var avg = employees.stream()
            .mapToDouble(Employee::salary)
            .average()
            .orElse(0);
        return employees.stream()
            .filter(e -> e.salary() > avg)
            .map(Employee::name)
            .sorted()
            .toList();
    }
    
    // تعداد کارمندان هر دپارتمان
    public static Map<String, Long> countByDept(List<Employee> employees) {
        return employees.stream()
            .collect(Collectors.groupingBy(
                Employee::department,
                Collectors.counting()
            ));
    }
    
    // بخش‌بندی بر اساس سن
    public static Map<Boolean, List<Employee>> partitionByAge(
            List<Employee> employees, int threshold) {
        return employees.stream()
            .collect(Collectors.partitioningBy(e -> e.age() >= threshold));
    }
}
```

**توسعه‌ی اختیاری:**
- `Map<String, DoubleSummaryStatistics>` با `Collectors.summarizingDouble`
- گزارش با `Collectors.joining` برای چاپ
- `teeing` برای min و max همزمان

### 📝 چک‌لیست پایان هفته

- [ ] `Stream` را در ۵ جای مختلف استفاده کرده‌ام
- [ ] `flatMap` را در یک مسئله‌ی واقعی به‌کار برده‌ام
- [ ] `groupingBy` + downstream collector نوشته‌ام
- [ ] `ArrayDeque` را برای Stack و Queue استفاده کرده‌ام
- [ ] `PriorityQueue` با Comparator سفارشی نوشته‌ام
- [ ] `Monotonic Stack` را پیاده کرده‌ام
- [ ] ۷ مسئله‌ی DSA را حل کرده‌ام
- [ ] پروژه‌ی Employee Analytics را کامل کرده‌ام
- [ ] Mock Interview انجام داده‌ام

---

## 📊 آمار پایان هفته ۳

| مورد | تعداد |
|---|---|
| ویژگی‌های Java 17/Stream | ۶ |
| مسائل DSA | ۷ |
| پروژه | ۱ |
| Mock Interview | ۱ |
| **مجموع مسائل تا الان** | **۲۱** |
| **مجموع ویژگی‌ها تا الان** | **۱۶** |

---

## 💡 نکات مهم برای هفته ۴

هفته‌ی بعد می‌رویم سراغ:
- **Stream پیشرفته**: `IntStream`, `parallelStream`, `Collectors.teeing`
- **Binary Search**: پیاده‌سازی و مسائل
- **Sorting**: Merge, Quick, Heap
- **Comparator پیشرفته**: `thenComparing`, `reversed`, `nullsFirst`

اگر خواستی، می‌توانم:
- **هفته‌ی چهارم** را با همین سطح جزئیات بنویسم
- یک **پروژه‌ی Maven کامل** با تمام کدهای سه هفته بسازم
- یک **بانک ۵۰ سؤال Mock Interview** درباره Java 17 + DSA بسازم

کدام را اول شروع کنیم؟
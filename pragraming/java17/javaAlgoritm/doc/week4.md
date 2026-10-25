عالی! هفته‌ی چهارم روی **Stream پیشرفته**، **Binary Search**، **الگوریتم‌های مرتب‌سازی**، و **Comparator پیشرفته** تمرکز دارد. این هفته پایه‌ی مصاحبه‌های فنی جدی است.

---

## 📅 هفته ۴ — روز ۱: IntStream و Streamهای تخصصی

### 🎓 بخش Java 17 (۳۰ دقیقه)

**چرا IntStream؟** برای جلوگیری از autoboxing. `Stream<Integer>` هر عنصر را box می‌کند؛ `IntStream` مستقیم روی `int` کار می‌کند.

**ساخت:**
```java
var range = IntStream.range(1, 5);        // 1,2,3,4
var closed = IntStream.rangeClosed(1, 5); // 1,2,3,4,5
var fromArray = Arrays.stream(new int[]{1, 2, 3});
var chars = "hello".chars();              // IntStream از کاراکترها
```

**متدهای اختصاصی:**
```java
IntStream.rangeClosed(1, 10)
    .filter(n -> n % 2 == 0)
    .map(n -> n * n)          // int → int (بدون boxing)
    .sum();                    // 220

var stats = IntStream.of(3, 1, 4, 1, 5, 9).summaryStatistics();
stats.getMin();    // 1
stats.getMax();    // 9
stats.getSum();    // 23
stats.getAverage(); // 3.833...
stats.getCount();  // 6
```

**تبدیل بین Stream و IntStream:**
```java
var boxed = IntStream.range(1, 5).boxed();        // IntStream → Stream<Integer>
var unboxed = list.stream().mapToInt(Integer::intValue); // Stream<Integer> → IntStream
```

**متدهای مفید دیگر:**
```java
IntStream.rangeClosed(1, 5)
    .mapToObj(i -> "Item " + i)   // IntStream → Stream<String>
    .forEach(System.out::println);

"hello world".chars()
    .filter(Character::isLetter)
    .mapToObj(c -> (char) c)
    .forEach(System.out::print); // helloworld
```

**نکته‌ی مصاحبه:** `IntStream`, `LongStream`, `DoubleStream` برای کارایی هستند. `mapToInt`, `mapToLong`, `mapToDouble` برای تبدیل از Stream معمولی استفاده می‌شوند.

### 🧩 بخش DSA (۶۰ دقیقه) — Binary Search (پیاده‌سازی)

**مسئله:** جستجوی دودویی در آرایه‌ی مرتب.

**راه‌حل iterative (استاندارد):**
```java
public int binarySearch(int[] nums, int target) {
    var left = 0;
    var right = nums.length - 1;
    while (left <= right) {
        var mid = left + (right - left) / 2;  // جلوگیری از overflow
        if (nums[mid] == target) return mid;
        if (nums[mid] < target) left = mid + 1;
        else right = mid - 1;
    }
    return -1;
}
```

**راه‌حل recursive:**
```java
public int binarySearch(int[] nums, int target) {
    return search(nums, target, 0, nums.length - 1);
}

private int search(int[] nums, int target, int left, int right) {
    if (left > right) return -1;
    var mid = left + (right - left) / 2;
    if (nums[mid] == target) return mid;
    if (nums[mid] < target) return search(nums, target, mid + 1, right);
    return search(nums, target, left, mid - 1);
}
```

**نکات حیاتی:**
- `left + (right - left) / 2` بهتر از `(left + right) / 2` است (overflow).
- `while (left <= right)` — دقت کن که `<=` است نه `<`.
- پیچیدگی: زمان O(log n)، فضا O(1).

**الگوی `lower_bound` و `upper_bound`:**
```java
// اولین ایندکسی که nums[i] >= target
int lowerBound(int[] nums, int target) {
    var left = 0;
    var right = nums.length;
    while (left < right) {
        var mid = left + (right - left) / 2;
        if (nums[mid] < target) left = mid + 1;
        else right = mid;
    }
    return left;
}

// اولین ایندکسی که nums[i] > target
int upperBound(int[] nums, int target) {
    var left = 0;
    var right = nums.length;
    while (left < right) {
        var mid = left + (right - left) / 2;
        if (nums[mid] <= target) left = mid + 1;
        else right = mid;
    }
    return left;
}
```

**نکته‌ی مصاحبه:** در مصاحبه‌ها از تو می‌خواهند Binary Search را برای "پیدا کردن مرز" بنویسی، نه فقط پیدا کردن یک عدد. `lowerBound` و `upperBound` را حفظ کن.

### ✍️ تمرین
یک متد `int countOccurrences(int[] nums, int target)` بنویس که با `lowerBound` و `upperBound` تعداد تکرار را در O(log n) حساب کند.

---

## 📅 روز ۲: Binary Search روی جواب

### 🎓 بخش Java 17 (۳۰ دقیقه)

**الگوی "Binary Search on Answer":** وقتی جواب یک عدد در بازه‌ی مشخصی است و می‌توانی چک کنی که یک جواب خاص معتبر است یا نه.

**ساختار کلی:**
```java
int binarySearchOnAnswer(int low, int high) {
    while (low < high) {
        var mid = low + (high - low) / 2;
        if (isValid(mid)) high = mid;
        else low = mid + 1;
    }
    return low;
}
```

**مثال: Koko Eating Bananas (LeetCode 875)**
```java
public int minEatingSpeed(int[] piles, int h) {
    var low = 1;
    var high = Arrays.stream(piles).max().orElse(1);
    while (low < high) {
        var mid = low + (high - low) / 2;
        if (canFinish(piles, h, mid)) high = mid;
        else low = mid + 1;
    }
    return low;
}

private boolean canFinish(int[] piles, int h, int speed) {
    var hours = 0;
    for (var p : piles) {
        hours += (p + speed - 1) / speed;  // ceil division
    }
    return hours <= h;
}
```

**نکته‌ی Java 17:** از `Arrays.stream(piles).max()` استفاده کردم که idiomatic است. `(p + speed - 1) / speed` یک trick برای ceil division.

### 🧩 بخش DSA (۶۰ دقیقه) — Search in Rotated Sorted Array

**مسئله:** آرایه‌ی مرتبی که در نقطه‌ای چرخیده. `target` را پیدا کن.

```java
public int search(int[] nums, int target) {
    var left = 0;
    var right = nums.length - 1;
    while (left <= right) {
        var mid = left + (right - left) / 2;
        if (nums[mid] == target) return mid;
        
        // کدام نیمه مرتب است؟
        if (nums[left] <= nums[mid]) {
            // نیمه‌ی چپ مرتب
            if (nums[left] <= target && target < nums[mid]) right = mid - 1;
            else left = mid + 1;
        } else {
            // نیمه‌ی راست مرتب
            if (nums[mid] < target && target <= nums[right]) left = mid + 1;
            else right = mid - 1;
        }
    }
    return -1;
}
```

- **پیچیدگی:** زمان O(log n)، فضا O(1)
- **الگو:** Modified Binary Search

**نکته:** این مسئله سخت است چون باید تشخیص بدهی کدام نیمه مرتب است. شرط `nums[left] <= nums[mid]` کلید است.

### ✍️ تمرین
مسئله‌ی Find Minimum in Rotated Sorted Array را با همان منطق حل کن.

---

## 📅 روز ۳: Merge Sort

### 🎓 بخش Java 17 (۳۰ دقیقه)

**Merge Sort** یک الگوریتم divide-and-conquer با پیچیدگی O(n log n) است.

```java
public void mergeSort(int[] nums) {
    if (nums.length < 2) return;
    mergeSort(nums, 0, nums.length - 1);
}

private void mergeSort(int[] nums, int left, int right) {
    if (left >= right) return;
    var mid = left + (right - left) / 2;
    mergeSort(nums, left, mid);
    mergeSort(nums, mid + 1, right);
    merge(nums, left, mid, right);
}

private void merge(int[] nums, int left, int mid, int right) {
    var temp = new int[right - left + 1];
    var i = left;
    var j = mid + 1;
    var k = 0;
    
    while (i <= mid && j <= right) {
        if (nums[i] <= nums[j]) temp[k++] = nums[i++];
        else temp[k++] = nums[j++];
    }
    while (i <= mid) temp[k++] = nums[i++];
    while (j <= right) temp[k++] = nums[j++];
    
    System.arraycopy(temp, 0, nums, left, temp.length);
}
```

**نسخه‌ی Java 17 idiom — با `Arrays.copyOfRange`:**
```java
public int[] mergeSort(int[] nums) {
    if (nums.length < 2) return nums;
    var mid = nums.length / 2;
    var left = mergeSort(Arrays.copyOfRange(nums, 0, mid));
    var right = mergeSort(Arrays.copyOfRange(nums, mid, nums.length));
    return merge(left, right);
}

private int[] merge(int[] a, int[] b) {
    var result = new int[a.length + b.length];
    var i = 0; var j = 0; var k = 0;
    while (i < a.length && j < b.length) {
        result[k++] = (a[i] <= b[j]) ? a[i++] : b[j++];
    }
    while (i < a.length) result[k++] = a[i++];
    while (j < b.length) result[k++] = b[j++];
    return result;
}
```

**ویژگی‌ها:**
- **Stable**: ترتیب عناصر مساوی حفظ می‌شود
- **پیچیدگی:** زمان O(n log n) در همه‌ی حالات، فضا O(n)
- **کاربرد**: مرتب‌سازی linked list، external sorting، inversion count

**نکته‌ی مصاحبه:** Merge Sort بهترین گزینه برای linked list است چون نیازی به random access ندارد.

### 🧩 بخش DSA (۶۰ دقیقه) — Sort Colors (Dutch National Flag)

**مسئله:** آرایه‌ای از ۰، ۱، ۲. in-place مرتب کن.

**راه‌حل — Three Pointers:**
```java
public void sortColors(int[] nums) {
    var low = 0;
    var mid = 0;
    var high = nums.length - 1;
    while (mid <= high) {
        switch (nums[mid]) {
            case 0 -> { swap(nums, low++, mid++); }
            case 1 -> { mid++; }
            case 2 -> { swap(nums, mid, high--); }
        }
    }
}

private void swap(int[] nums, int i, int j) {
    var t = nums[i];
    nums[i] = nums[j];
    nums[j] = t;
}
```

- **پیچیدگی:** زمان O(n)، فضا O(1)
- **الگو:** Dutch National Flag

**نکته‌ی Java 17:** از `switch` expression استفاده کردم که خوانا‌تر است. دقت کن `mid` وقتی با ۰ swap می‌شود باید `mid++` شود، ولی با ۲ نه.

### ✍️ تمرین
الگوریتم Merge Sort را طوری تغییر بده که تعداد inversionها را هم بشمارد (Inversion Count).

---

## 📅 روز ۴: Quick Sort

### 🎓 بخش Java 17 (۳۰ دقیقه)

**Quick Sort** با pivot و partition کار می‌کند.

```java
public void quickSort(int[] nums) {
    quickSort(nums, 0, nums.length - 1);
}

private void quickSort(int[] nums, int low, int high) {
    if (low < high) {
        var pivot = partition(nums, low, high);
        quickSort(nums, low, pivot - 1);
        quickSort(nums, pivot + 1, high);
    }
}

private int partition(int[] nums, int low, int high) {
    var pivot = nums[high];
    var i = low - 1;
    for (var j = low; j < high; j++) {
        if (nums[j] <= pivot) {
            i++;
            swap(nums, i, j);
        }
    }
    swap(nums, i + 1, high);
    return i + 1;
}

private void swap(int[] nums, int i, int j) {
    var t = nums[i];
    nums[i] = nums[j];
    nums[j] = t;
}
```

**بهبود: Random Pivot**
```java
private int partition(int[] nums, int low, int high) {
    var randomIdx = low + new Random().nextInt(high - low + 1);
    swap(nums, randomIdx, high);
    // ... ادامه مثل قبل
}
```

**ویژگی‌ها:**
- **پیچیدگی:** میانگین O(n log n)، بدترین O(n²) (با pivot بد)
- **فضا:** O(log n) برای recursion stack
- **Stable:** ❌ (نسخه‌ی استاندارد ناپایدار است)
- **در عمل سریع‌تر از Merge Sort** برای آرایه‌ها

**مقایسه‌ی Merge vs Quick:**

| ویژگی | Merge Sort | Quick Sort |
|---|---|---|
| زمان (میانگین) | O(n log n) | O(n log n) |
| زمان (بدترین) | O(n log n) | O(n²) |
| فضا | O(n) | O(log n) |
| Stable | ✅ | ❌ |
| کاربرد | Linked List, External | Array, in-place |

**نکته‌ی مصاحبه:** `Arrays.sort()` در جاوا برای primitiveها از Dual-Pivot Quicksort و برای objectها از Timsort (ترکیب Merge + Insertion) استفاده می‌کند. این سؤال خیلی رایج است.

### 🧩 بخش DSA (۶۰ دقیقه) — Kth Largest Element

**مسئله:** k-امین بزرگ‌ترین عنصر را پیدا کن.

**راه‌حل ۱ — Quick Select (میانگین O(n)):**
```java
public int findKthLargest(int[] nums, int k) {
    var target = nums.length - k; // ایندکس در آرایه‌ی مرتب
    return quickSelect(nums, 0, nums.length - 1, target);
}

private int quickSelect(int[] nums, int low, int high, int target) {
    var pivot = partition(nums, low, high);
    if (pivot == target) return nums[pivot];
    if (pivot < target) return quickSelect(nums, pivot + 1, high, target);
    return quickSelect(nums, low, pivot - 1, target);
}
```

**راه‌حل ۲ — Min-Heap:**
```java
public int findKthLargest(int[] nums, int k) {
    var heap = new PriorityQueue<Integer>();
    for (var n : nums) {
        heap.offer(n);
        if (heap.size() > k) heap.poll();
    }
    return heap.peek();
}
```

- **Quick Select:** زمان میانگین O(n)، بدترین O(n²)، فضا O(1)
- **Heap:** زمان O(n log k)، فضا O(k)
- **الگو:** Quick Select / Heap

**نکته:** در مصاحبه، هر دو را بلد باش. Quick Select سریع‌تر است ولی Heap پایدارتر.

### ✍️ تمرین
یک `quickSort` با random pivot بنویس و با `Arrays.sort` مقایسه کن.

---

## 📅 روز ۵: Heap Sort و Comparator پیشرفته

### 🎓 بخش Java 17 (۳۰ دقیقه)

**Heap Sort:**
```java
public void heapSort(int[] nums) {
    var n = nums.length;
    // ساخت max-heap
    for (var i = n / 2 - 1; i >= 0; i--) heapify(nums, n, i);
    // استخراج یک‌به‌یک
    for (var i = n - 1; i > 0; i--) {
        swap(nums, 0, i);
        heapify(nums, i, 0);
    }
}

private void heapify(int[] nums, int n, int i) {
    var largest = i;
    var left = 2 * i + 1;
    var right = 2 * i + 2;
    if (left < n && nums[left] > nums[largest]) largest = left;
    if (right < n && nums[right] > nums[largest]) largest = right;
    if (largest != i) {
        swap(nums, i, largest);
        heapify(nums, n, largest);
    }
}
```

- **پیچیدگی:** زمان O(n log n) در همه حالات، فضا O(1)
- **Stable:** ❌

**Comparator پیشرفته:**
```java
var employees = new ArrayList<Employee>();

// زنجیره‌سازی
employees.sort(
    Comparator.comparing(Employee::department)
        .thenComparing(Employee::name)
        .thenComparingDouble(Employee::salary).reversed()
);

// null handling
employees.sort(Comparator.nullsFirst(
    Comparator.comparing(Employee::name)
));

// معکوس
var bySalaryDesc = Comparator.comparingDouble(Employee::salary).reversed();

// طبیعی
employees.sort(Comparator.naturalOrder());

// سفارشی
employees.sort((a, b) -> Double.compare(b.salary(), a.salary()));
```

**نکته‌ی Java 17 idiom:**
```java
// max با Comparator
var top = employees.stream()
    .max(Comparator.comparingDouble(Employee::salary))
    .orElseThrow();

// sort در Stream
var sorted = employees.stream()
    .sorted(Comparator.comparing(Employee::name))
    .toList();
```

**نکته‌ی مصاحبه:** `Comparator.comparing(...).thenComparing(...)` بهترین روش برای مرتب‌سازی چندسطحی است. `reversed()` را در جای درست بگذار (بعد از `thenComparing` یا قبل از آن).

### 🧩 بخش DSA (۶۰ دقیقه) — Merge Intervals

**مسئله:** لیستی از بازه‌ها. بازه‌های همپوشان را ادغام کن.

```java
public int[][] merge(int[][] intervals) {
    if (intervals.length <= 1) return intervals;
    
    // مرتب‌سازی بر اساس شروع
    Arrays.sort(intervals, Comparator.comparingInt(a -> a[0]));
    
    var result = new ArrayList<int[]>();
    var current = intervals[0];
    result.add(current);
    
    for (var i = 1; i < intervals.length; i++) {
        var next = intervals[i];
        if (next[0] <= current[1]) {
            // همپوشان — ادغام
            current[1] = Math.max(current[1], next[1]);
        } else {
            current = next;
            result.add(current);
        }
    }
    return result.toArray(new int[0][]);
}
```

- **پیچیدگی:** زمان O(n log n) (مرتب‌سازی)، فضا O(n)
- **الگو:** Sort + Merge

**نکته‌ی Java 17:** `Comparator.comparingInt(a -> a[0])` idiomatic است. `result.toArray(new int[0][])` یک trick برای تبدیل List به Array است.

### ✍️ تمرین
مسئله‌ی `Insert Interval` را با همین منطق حل کن.

---

## 📅 روز ۶: Collectors.teeing و Stream پیشرفته

### 🎓 بخش Java 17 (۳۰ دقیقه)

**`Collectors.teeing` (Java 12+):** دو collector را همزمان اجرا می‌کند و نتایج را ترکیب می‌کند.

```java
// min و max همزمان
record MinMax(int min, int max) {}

var result = IntStream.of(3, 1, 4, 1, 5, 9).boxed()
    .collect(Collectors.teeing(
        Collectors.minBy(Integer::compareTo),
        Collectors.maxBy(Integer::compareTo),
        (min, max) -> new MinMax(min.orElseThrow(), max.orElseThrow())
    ));
```

**مثال پیشرفته: گزارش employee**
```java
record DeptStats(long count, double avgSalary, double totalSalary) {}

var stats = employees.stream()
    .collect(Collectors.groupingBy(
        Employee::department,
        Collectors.teeing(
            Collectors.counting(),
            Collectors.averagingDouble(Employee::salary),
            (count, avg) -> new DeptStats(count, avg, count * avg)
        )
    ));
```

**`Collectors.summarizingDouble`:**
```java
var summary = employees.stream()
    .collect(Collectors.groupingBy(
        Employee::department,
        Collectors.summarizingDouble(Employee::salary)
    ));
// Map<String, DoubleSummaryStatistics>
```

**`parallelStream`:**
```java
var sum = IntStream.rangeClosed(1, 1_000_000)
    .parallel()
    .sum();
```

**نکته:** `parallelStream` فقط برای کارهای CPU-intensive و داده‌ی زیاد مفید است. برای I/O یا داده کم، overhead بیشتر از سود است.

**نکته‌ی مصاحبه:** `teeing` یک collector قدرتمند است که در مصاحبه‌های Java 17 خیلی دیده می‌شود. اگر بتوانی از آن استفاده کنی، نشان می‌دهی به‌روز هستی.

### 🧩 بخش DSA (۶۰ دقیقه) — Longest Increasing Subsequence (LIS)

**مسئله:** طولانی‌ترین زیردنباله‌ی صعودی را پیدا کن.

**راه‌حل ۱ — DP (O(n²)):**
```java
public int lengthOfLIS(int[] nums) {
    var n = nums.length;
    var dp = new int[n];
    Arrays.fill(dp, 1);
    var max = 1;
    for (var i = 1; i < n; i++) {
        for (var j = 0; j < i; j++) {
            if (nums[j] < nums[i]) {
                dp[i] = Math.max(dp[i], dp[j] + 1);
            }
        }
        max = Math.max(max, dp[i]);
    }
    return max;
}
```

**راه‌حل ۲ — Binary Search (O(n log n)):**
```java
public int lengthOfLIS(int[] nums) {
    var tails = new ArrayList<Integer>();
    for (var n : nums) {
        var idx = lowerBound(tails, n);
        if (idx == tails.size()) tails.add(n);
        else tails.set(idx, n);
    }
    return tails.size();
}

private int lowerBound(List<Integer> list, int target) {
    var left = 0;
    var right = list.size();
    while (left < right) {
        var mid = left + (right - left) / 2;
        if (list.get(mid) < target) left = mid + 1;
        else right = mid;
    }
    return left;
}
```

- **راه‌حل ۱:** زمان O(n²)، فضا O(n)
- **راه‌حل ۲:** زمان O(n log n)، فضا O(n)
- **الگو:** DP + Binary Search

**نکته:** `tails` یک آرایه‌ی کمکی است که کوچک‌ترین tail ممکن برای هر طول را نگه می‌دارد. این trick مهم است.

### ✍️ تمرین
مسئله‌ی `Russian Doll Envelopes` را با LIS حل کن.

---

## 📅 روز ۷: مرور + پروژه

### 🎯 کارهای امروز

1. **مرور Stream پیشرفته**: `IntStream`, `teeing`, `summarizingDouble`, `parallelStream`
2. **مرور Binary Search**: standard, lowerBound, upperBound, on answer
3. **مرور Sorting**: Merge, Quick, Heap
4. **مرور Comparator**: `thenComparing`, `reversed`, `nullsFirst`
5. **بازنویسی DSA**:
    - Search in Rotated Sorted Array
    - Kth Largest Element (هر دو رویکرد)
    - Merge Intervals
6. **Mock Interview**: با تایمر ۴۵ دقیقه، `LIS` را حل کن.
7. **پروژه‌ی هفته: Sorting Visualizer CLI**

### 🎁 پروژه‌ی هفته: Sorting Visualizer CLI

یک برنامه بنویس که یک آرایه می‌گیرد و مراحل مرتب‌سازی را چاپ می‌کند.

```java
public class SortingVisualizer {
    
    public static void bubbleSort(int[] arr, Consumer<int[]> onStep) {
        var n = arr.length;
        for (var i = 0; i < n - 1; i++) {
            for (var j = 0; j < n - i - 1; j++) {
                if (arr[j] > arr[j + 1]) {
                    var t = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = t;
                    onStep.accept(arr);
                }
            }
        }
    }
    
    public static void printBar(int[] arr) {
        var max = Arrays.stream(arr).max().orElse(1);
        for (var v : arr) {
            var bar = "█".repeat(Math.max(1, v * 20 / max));
            System.out.printf("%3d %s%n", v, bar);
        }
        System.out.println("-".repeat(30));
    }
    
    public static void main(String[] args) {
        var arr = new int[]{5, 2, 8, 1, 9, 3};
        System.out.println("Before:");
        printBar(arr);
        
        bubbleSort(arr, SortingVisualizer::printBar);
        
        System.out.println("After:");
        printBar(arr);
    }
}
```

**توسعه‌ی اختیاری:**
- Merge Sort, Quick Sort, Heap Sort را اضافه کن
- زمان هر الگوریتم را با `System.nanoTime()` اندازه بگیر
- با `Thread.sleep` انیمیشن واقعی بساز
- تعداد comparison و swap را بشمار

### 📝 چک‌لیست پایان هفته

- [ ] `IntStream` را در ۳ جای مختلف استفاده کرده‌ام
- [ ] `Collectors.teeing` نوشته‌ام
- [ ] `parallelStream` را تست کرده‌ام
- [ ] Binary Search را از حفظ نوشته‌ام
- [ ] `lowerBound` / `upperBound` را پیاده کرده‌ام
- [ ] Merge Sort, Quick Sort, Heap Sort را نوشته‌ام
- [ ] `Comparator.thenComparing` را در یک مسئله‌ی واقعی به‌کار برده‌ام
- [ ] ۷ مسئله‌ی DSA را حل کرده‌ام
- [ ] پروژه‌ی Sorting Visualizer را کامل کرده‌ام
- [ ] Mock Interview انجام داده‌ام

---

## 📊 آمار پایان هفته ۴

| مورد | تعداد |
|---|---|
| ویژگی‌های Java 17 | ۴ |
| الگوریتم‌های مرتب‌سازی | ۳ |
| مسائل DSA | ۷ |
| پروژه | ۱ |
| Mock Interview | ۱ |
| **مجموع مسائل تا الان** | **۲۸** |
| **مجموع ویژگی‌ها تا الان** | **۲۰** |

---

## 💡 نکات مهم برای هفته ۵

هفته‌ی بعد می‌رویم سراغ:
- **Pattern Matching for `switch`** (نسخه‌ی کامل، با preview در 17)
- **درخت‌ها**: Binary Tree, BST, پیمایش‌ها
- **Heap و PriorityQueue** در عمق
- **Trie** (مقدمه)

اگر خواستی، می‌توانم:
- **هفته‌ی پنجم** را با همین سطح جزئیات بنویسم
- یک **پروژه‌ی Maven کامل** با تمام کدهای چهار هفته بسازم
- یک **آزمون خودارزیابی** برای مرور چهار هفته‌ی گذشته طراحی کنم

کدام را اول شروع کنیم؟
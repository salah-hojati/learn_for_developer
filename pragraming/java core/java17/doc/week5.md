عالی! هفته‌ی پنجم روی **Pattern Matching for `switch`** (نسخه‌ی کامل)، **درخت‌ها** (Binary Tree, BST)، **Heap / PriorityQueue**، و **Trie** تمرکز دارد. این هفته وارد دنیای ساختارهای داده‌ی درختی می‌شویم که قلب مصاحبه‌های فنی است.

---

## 📅 هفته ۵ — روز ۱: Pattern Matching for switch (کامل)

### 🎓 بخش Java 17 (۳۰ دقیقه)

در Java 17 این ویژگی **preview** است و باید با `--enable-preview` کامپایل شود. در Java 21 رسماً stable شد.

**انواع pattern:**

**۱. Type Pattern:**
```java
static String describe(Object obj) {
    return switch (obj) {
        case Integer i -> "int: " + i;
        case String s -> "string: " + s;
        case null -> "null!";
        default -> "unknown";
    };
}
```

**۲. Guarded Pattern (با `when`):**
```java
static String classify(Object obj) {
    return switch (obj) {
        case Integer i when i < 0 -> "negative";
        case Integer i when i == 0 -> "zero";
        case Integer i -> "positive";
        default -> "other";
    };
}
```

**۳. Record Deconstruction Pattern (Java 21 stable، 17 preview):**
```java
record Point(int x, int y) {}

static String quadrant(Object obj) {
    return switch (obj) {
        case Point(int x, int y) when x > 0 && y > 0 -> "Q1";
        case Point(int x, int y) when x < 0 && y > 0 -> "Q2";
        case Point(int x, int y) when x < 0 && y < 0 -> "Q3";
        case Point(int x, int y) when x > 0 && y < 0 -> "Q4";
        case Point(int x, int y) when x == 0 || y == 0 -> "axis";
        default -> "?";
    };
}
```

**۴. Null Handling:**
```java
switch (obj) {
    case null -> "null!";
    case String s -> "string";
    default -> "other";
}
```

**۵. Nested Patterns:**
```java
record Line(Point start, Point end) {}

switch (shape) {
    case Line(Point(var x1, var y1), Point(var x2, var y2)) -> 
        Math.sqrt(Math.pow(x2-x1, 2) + Math.pow(y2-y1, 2));
    default -> 0;
}
```

**نکته‌ی مصاحبه:** Pattern Matching + Sealed + Record یک trilogy است که جایگزین Visitor Pattern می‌شود. اگر بتوانی در مصاحبه این را نشان بدهی، امتیاز بزرگی می‌گیری.

**نکات مهم:**
- در Java 17 preview، `when` استفاده می‌شود (در Java 21 هم `when`).
- Exhaustiveness: اگر selector از نوع sealed باشد، `default` لازم نیست.
- Dominance: ترتیب patternها مهم است (خاص‌تر اول).

### 🧩 بخش DSA (۶۰ دقیقه) — Invert Binary Tree

**مسئله:** یک درخت دودویی را معکوس کن (آینه‌ای).

**تعریف:**
```java
class TreeNode {
    int val;
    TreeNode left, right;
    TreeNode(int val) { this.val = val; }
    TreeNode(int val, TreeNode left, TreeNode right) {
        this.val = val; this.left = left; this.right = right;
    }
}
```

**راه‌حل recursive:**
```java
public TreeNode invertTree(TreeNode root) {
    if (root == null) return null;
    var left = invertTree(root.left);
    var right = invertTree(root.right);
    root.left = right;
    root.right = left;
    return root;
}
```

**راه‌حل iterative (BFS):**
```java
public TreeNode invertTree(TreeNode root) {
    if (root == null) return null;
    var queue = new ArrayDeque<TreeNode>();
    queue.offer(root);
    while (!queue.isEmpty()) {
        var node = queue.poll();
        var temp = node.left;
        node.left = node.right;
        node.right = temp;
        if (node.left != null) queue.offer(node.left);
        if (node.right != null) queue.offer(node.right);
    }
    return root;
}
```

- **پیچیدگی:** زمان O(n)، فضا O(h) (h ارتفاع)
- **الگو:** Tree DFS/BFS

### ✍️ تمرین
یک `sealed interface Tree` با `record Leaf`, `record Node` بساز و `invert` را با Pattern Matching بنویس.

---

## 📅 روز ۲: Binary Tree — پیمایش‌ها

### 🎓 بخش Java 17 (۳۰ دقیقه)

**چهار پیمایش اصلی:**

**۱. Inorder (چپ → ریشه → راست):**
```java
public List<Integer> inorder(TreeNode root) {
    var result = new ArrayList<Integer>();
    inorderHelper(root, result);
    return result;
}

private void inorderHelper(TreeNode node, List<Integer> result) {
    if (node == null) return;
    inorderHelper(node.left, result);
    result.add(node.val);
    inorderHelper(node.right, result);
}
```

**۲. Preorder (ریشه → چپ → راست):**
```java
public List<Integer> preorder(TreeNode root) {
    var result = new ArrayList<Integer>();
    var stack = new ArrayDeque<TreeNode>();
    if (root != null) stack.push(root);
    while (!stack.isEmpty()) {
        var node = stack.pop();
        result.add(node.val);
        if (node.right != null) stack.push(node.right);
        if (node.left != null) stack.push(node.left);
    }
    return result;
}
```

**۳. Postorder (چپ → راست → ریشه):**
```java
public List<Integer> postorder(TreeNode root) {
    var result = new LinkedList<Integer>();
    var stack = new ArrayDeque<TreeNode>();
    if (root != null) stack.push(root);
    while (!stack.isEmpty()) {
        var node = stack.pop();
        result.addFirst(node.val); // معکوس preorder
        if (node.left != null) stack.push(node.left);
        if (node.right != null) stack.push(node.right);
    }
    return result;
}
```

**۴. Level-order (BFS):**
```java
public List<List<Integer>> levelOrder(TreeNode root) {
    var result = new ArrayList<List<Integer>>();
    if (root == null) return result;
    var queue = new ArrayDeque<TreeNode>();
    queue.offer(root);
    while (!queue.isEmpty()) {
        var levelSize = queue.size();
        var level = new ArrayList<Integer>();
        for (var i = 0; i < levelSize; i++) {
            var node = queue.poll();
            level.add(node.val);
            if (node.left != null) queue.offer(node.left);
            if (node.right != null) queue.offer(node.right);
        }
        result.add(level);
    }
    return result;
}
```

**Morris Traversal (O(1) فضا):**
```java
public List<Integer> inorderMorris(TreeNode root) {
    var result = new ArrayList<Integer>();
    var curr = root;
    while (curr != null) {
        if (curr.left == null) {
            result.add(curr.val);
            curr = curr.right;
        } else {
            var predecessor = curr.left;
            while (predecessor.right != null && predecessor.right != curr) {
                predecessor = predecessor.right;
            }
            if (predecessor.right == null) {
                predecessor.right = curr;
                curr = curr.left;
            } else {
                predecessor.right = null;
                result.add(curr.val);
                curr = curr.right;
            }
        }
    }
    return result;
}
```

**نکته‌ی مصاحبه:** Morris Traversal یک تکنیک O(1) فضا است که در مصاحبه‌های سخت پرسیده می‌شود. اگر بلد باشی، امتیاز می‌گیری.

### 🧩 بخش DSA (۶۰ دقیقه) — Maximum Depth of Binary Tree

**مسئله:** بیشترین عمق درخت.

```java
public int maxDepth(TreeNode root) {
    if (root == null) return 0;
    return 1 + Math.max(maxDepth(root.left), maxDepth(root.right));
}
```

**نسخه‌ی iterative (BFS):**
```java
public int maxDepth(TreeNode root) {
    if (root == null) return 0;
    var queue = new ArrayDeque<TreeNode>();
    queue.offer(root);
    var depth = 0;
    while (!queue.isEmpty()) {
        depth++;
        var size = queue.size();
        for (var i = 0; i < size; i++) {
            var node = queue.poll();
            if (node.left != null) queue.offer(node.left);
            if (node.right != null) queue.offer(node.right);
        }
    }
    return depth;
}
```

- **پیچیدگی:** زمان O(n)، فضا O(h) recursive / O(w) BFS
- **الگو:** Tree DFS/BFS

### ✍️ تمرین
مسائل Same Tree و Symmetric Tree را با recursive و iterative حل کن.

---

## 📅 روز ۳: Binary Search Tree (BST)

### 🎓 بخش Java 17 (۳۰ دقیقه)

**BST Property:** برای هر گره، همه‌ی مقادیر چپ کوچک‌تر و همه‌ی مقادیر راست بزرگ‌تر هستند.

**Insert:**
```java
public TreeNode insert(TreeNode root, int val) {
    if (root == null) return new TreeNode(val);
    if (val < root.val) root.left = insert(root.left, val);
    else if (val > root.val) root.right = insert(root.right, val);
    return root;
}
```

**Search:**
```java
public TreeNode search(TreeNode root, int val) {
    var curr = root;
    while (curr != null) {
        if (curr.val == val) return curr;
        curr = (val < curr.val) ? curr.left : curr.right;
    }
    return null;
}
```

**Delete (سه حالت):**
```java
public TreeNode delete(TreeNode root, int val) {
    if (root == null) return null;
    if (val < root.val) root.left = delete(root.left, val);
    else if (val > root.val) root.right = delete(root.right, val);
    else {
        // حالت ۱ و ۲: صفر یا یک فرزند
        if (root.left == null) return root.right;
        if (root.right == null) return root.left;
        // حالت ۳: دو فرزند — جانشین inorder
        var successor = findMin(root.right);
        root.val = successor.val;
        root.right = delete(root.right, successor.val);
    }
    return root;
}

private TreeNode findMin(TreeNode node) {
    while (node.left != null) node = node.left;
    return node;
}
```

**Validate BST (با بازه):**
```java
public boolean isValidBST(TreeNode root) {
    return validate(root, Long.MIN_VALUE, Long.MAX_VALUE);
}

private boolean validate(TreeNode node, long min, long max) {
    if (node == null) return true;
    if (node.val <= min || node.val >= max) return false;
    return validate(node.left, min, node.val)
        && validate(node.right, node.val, max);
}
```

**نکته:** `validate` با `long` کار می‌کند تا مقادیر `Integer.MIN_VALUE` و `MAX_VALUE` مشکل نسازند.

**نکته‌ی مصاحبه:** BST معتبر = inorder traversal صعودی. یک راه ساده‌تر ولی O(n) فضا همین است.

### 🧩 بخش DSA (۶۰ دقیقه) — Validate BST

**مسئله:** آیا درخت یک BST معتبر است؟

**راه‌حل ۱ — با بازه (بهترین):**
```java
public boolean isValidBST(TreeNode root) {
    return validate(root, null, null);
}

private boolean validate(TreeNode node, Integer min, Integer max) {
    if (node == null) return true;
    if ((min != null && node.val <= min) || (max != null && node.val >= max)) {
        return false;
    }
    return validate(node.left, min, node.val)
        && validate(node.right, node.val, max);
}
```

**راه‌حل ۲ — Inorder:**
```java
public boolean isValidBST(TreeNode root) {
    var prev = new TreeNode[1]; // آرایه برای mutable در lambda
    return inorder(root, prev);
}

private boolean inorder(TreeNode node, TreeNode[] prev) {
    if (node == null) return true;
    if (!inorder(node.left, prev)) return false;
    if (prev[0] != null && prev[0].val >= node.val) return false;
    prev[0] = node;
    return inorder(node.right, prev);
}
```

- **پیچیدگی:** زمان O(n)، فضا O(h)
- **الگو:** Tree DFS + Bounds

**نکته:** صرفاً چک کردن `node.left.val < node.val < node.right.val` **کافی نیست**. باید بازه‌ی کل زیردرخت را چک کنی.

### ✍️ تمرین
مسئله‌ی Kth Smallest Element in a BST را با inorder حل کن.

---

## 📅 روز ۴: Heap و PriorityQueue

### 🎓 بخش Java 17 (۳۰ دقیقه)

**Heap Property:**
- **Min-Heap:** هر والد ≤ فرزندان (ریشه کوچک‌ترین)
- **Max-Heap:** هر والد ≥ فرزندان (ریشه بزرگ‌ترین)

**PriorityQueue در جاوا:**
```java
// Min-heap (پیش‌فرض)
var minHeap = new PriorityQueue<Integer>();
minHeap.offer(3); minHeap.offer(1); minHeap.offer(2);
minHeap.poll(); // 1

// Max-heap
var maxHeap = new PriorityQueue<Integer>(Comparator.reverseOrder());

// با Comparator سفارشی
var bySalary = new PriorityQueue<Employee>(
    Comparator.comparingDouble(Employee::salary)
);

// از Collection
var heap = new PriorityQueue<>(List.of(3, 1, 2));
```

**پیاده‌سازی Min-Heap از صفر:**
```java
public class MinHeap {
    private int[] data = new int[16];
    private int size = 0;
    
    public void offer(int val) {
        if (size == data.length) data = Arrays.copyOf(data, size * 2);
        data[size] = val;
        siftUp(size++);
    }
    
    public int poll() {
        if (size == 0) throw new NoSuchElementException();
        var min = data[0];
        data[0] = data[--size];
        siftDown(0);
        return min;
    }
    
    public int peek() {
        if (size == 0) throw new NoSuchElementException();
        return data[0];
    }
    
    private void siftUp(int i) {
        while (i > 0) {
            var parent = (i - 1) / 2;
            if (data[parent] <= data[i]) break;
            swap(parent, i);
            i = parent;
        }
    }
    
    private void siftDown(int i) {
        while (true) {
            var left = 2 * i + 1;
            var right = 2 * i + 2;
            var smallest = i;
            if (left < size && data[left] < data[smallest]) smallest = left;
            if (right < size && data[right] < data[smallest]) smallest = right;
            if (smallest == i) break;
            swap(i, smallest);
            i = smallest;
        }
    }
    
    private void swap(int i, int j) {
        var t = data[i]; data[i] = data[j]; data[j] = t;
    }
}
```

**Heapify — ساخت heap از آرایه در O(n):**
```java
public void heapify(int[] arr) {
    var n = arr.length;
    for (var i = n / 2 - 1; i >= 0; i--) {
        siftDown(arr, n, i);
    }
}
```

**نکته‌ی مصاحبه:** `PriorityQueue` در جاوا thread-safe نیست. برای نسخه‌ی thread-safe از `PriorityBlockingQueue` استفاده کن.

**مقایسه‌ی Heap با TreeMap:**
- `PriorityQueue`: فقط min/max دسترسی، O(log n) insert/poll
- `TreeMap`: کلیدها مرتب، O(log n) همه عملیات

### 🧩 بخش DSA (۶۰ دقیقه) — Kth Largest Element

**مسئله:** k-امین بزرگ‌ترین عنصر.

**راه‌حل — Min-Heap با اندازه k:**
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

- **پیچیدگی:** زمان O(n log k)، فضا O(k)
- **الگو:** Heap

**بهینه‌تر — Quick Select:** (در هفته ۴ دیدیم)

**نکته:** برای k کوچک، Heap بهتر است. برای k بزرگ، Quick Select.

### ✍️ تمرین
مسئله‌ی Top K Frequent Words را با Heap و Comparator حل کن.

---

## 📅 روز ۵: Tree — مسائل سطح متوسط

### 🎓 بخش Java 17 (۳۰ دقیقه)

**Lowest Common Ancestor (LCA):**
```java
public TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
    if (root == null || root == p || root == q) return root;
    var left = lowestCommonAncestor(root.left, p, q);
    var right = lowestCommonAncestor(root.right, p, q);
    if (left != null && right != null) return root;
    return (left != null) ? left : right;
}
```

**Binary Tree Paths:**
```java
public List<String> binaryTreePaths(TreeNode root) {
    var result = new ArrayList<String>();
    if (root != null) dfs(root, "", result);
    return result;
}

private void dfs(TreeNode node, String path, List<String> result) {
    var newPath = path.isEmpty() ? String.valueOf(node.val) : path + "->" + node.val;
    if (node.left == null && node.right == null) {
        result.add(newPath);
        return;
    }
    if (node.left != null) dfs(node.left, newPath, result);
    if (node.right != null) dfs(node.right, newPath, result);
}
```

**نسخه‌ی Java 17 idiom — با `record` و `switch`:**
```java
public List<String> binaryTreePaths(TreeNode root) {
    return paths(root, "");
}

private List<String> paths(TreeNode node, String prefix) {
    if (node == null) return List.of();
    var current = prefix.isEmpty() 
        ? String.valueOf(node.val) 
        : prefix + "->" + node.val;
    if (node.left == null && node.right == null) return List.of(current);
    return Stream.concat(
        paths(node.left, current).stream(),
        paths(node.right, current).stream()
    ).toList();
}
```

**نکته:** نسخه‌ی Stream-based elegant است ولی برای درخت‌های بزرگ overhead دارد. در مصاحبه، نسخه‌ی iterative یا recursive ساده را ترجیح بده.

### 🧩 بخش DSA (۶۰ دقیقه) — Binary Tree Level Order Traversal

**مسئله:** درخت را سطح‌به‌سطح چاپ کن.

```java
public List<List<Integer>> levelOrder(TreeNode root) {
    var result = new ArrayList<List<Integer>>();
    if (root == null) return result;
    var queue = new ArrayDeque<TreeNode>();
    queue.offer(root);
    while (!queue.isEmpty()) {
        var size = queue.size();
        var level = new ArrayList<Integer>(size);
        for (var i = 0; i < size; i++) {
            var node = queue.poll();
            level.add(node.val);
            if (node.left != null) queue.offer(node.left);
            if (node.right != null) queue.offer(node.right);
        }
        result.add(level);
    }
    return result;
}
```

**نسخه‌ی Zigzag:**
```java
public List<List<Integer>> zigzagLevelOrder(TreeNode root) {
    var result = new ArrayList<List<Integer>>();
    if (root == null) return result;
    var queue = new ArrayDeque<TreeNode>();
    queue.offer(root);
    var leftToRight = true;
    while (!queue.isEmpty()) {
        var size = queue.size();
        var level = new LinkedList<Integer>();
        for (var i = 0; i < size; i++) {
            var node = queue.poll();
            if (leftToRight) level.addLast(node.val);
            else level.addFirst(node.val);
            if (node.left != null) queue.offer(node.left);
            if (node.right != null) queue.offer(node.right);
        }
        result.add(level);
        leftToRight = !leftToRight;
    }
    return result;
}
```

- **پیچیدگی:** زمان O(n)، فضا O(w) (w پهنای درخت)
- **الگو:** BFS

### ✍️ تمرین
مسائل Right Side View و Average of Levels را با BFS حل کن.

---

## 📅 روز ۶: Trie (مقدمه)

### 🎓 بخش Java 17 (۳۰ دقیقه)

**Trie** (درخت پیشوندی) برای ذخیره و جستجوی رشته‌ها. هر گره نماینده‌ی یک کاراکتر.

**پیاده‌سازی:**
```java
public class Trie {
    private static class Node {
        Node[] children = new Node[26];
        boolean isEnd;
    }
    
    private final Node root = new Node();
    
    public void insert(String word) {
        var curr = root;
        for (var c : word.toCharArray()) {
            var idx = c - 'a';
            if (curr.children[idx] == null) {
                curr.children[idx] = new Node();
            }
            curr = curr.children[idx];
        }
        curr.isEnd = true;
    }
    
    public boolean search(String word) {
        var node = find(word);
        return node != null && node.isEnd;
    }
    
    public boolean startsWith(String prefix) {
        return find(prefix) != null;
    }
    
    private Node find(String s) {
        var curr = root;
        for (var c : s.toCharArray()) {
            var idx = c - 'a';
            if (curr.children[idx] == null) return null;
            curr = curr.children[idx];
        }
        return curr;
    }
}
```

**نسخه‌ی Java 17 idiom — با `record` و `HashMap` برای یونیکد:**
```java
public class Trie {
    private static final class Node {
        final Map<Character, Node> children = new HashMap<>();
        boolean isEnd;
    }
    
    private final Node root = new Node();
    
    public void insert(String word) {
        var curr = root;
        for (var c : word.toCharArray()) {
            curr = curr.children.computeIfAbsent(c, k -> new Node());
        }
        curr.isEnd = true;
    }
    // ...
}
```

**پیچیدگی:**
- Insert: O(m) (m طول کلمه)
- Search: O(m)
- Space: O(total characters × alphabet)

**کاربردها:**
- Autocomplete
- Spell checker
- IP routing
- Word search (Boggle)

**نکته‌ی مصاحبه:** Trie برای مسائل prefix-based عالیه. اگر مسئله "پیشوند مشترک" یا "جستجوی کلمه" داشت، به Trie فکر کن.

### 🧩 بخش DSA (۶۰ دقیقه) — Implement Trie (Prefix Tree)

**مسئله:** Trie را پیاده کن (LeetCode 208).

```java
class Trie {
    private final TrieNode root;
    
    public Trie() {
        root = new TrieNode();
    }
    
    public void insert(String word) {
        var node = root;
        for (var c : word.toCharArray()) {
            if (node.children[c - 'a'] == null) {
                node.children[c - 'a'] = new TrieNode();
            }
            node = node.children[c - 'a'];
        }
        node.isEnd = true;
    }
    
    public boolean search(String word) {
        var node = searchNode(word);
        return node != null && node.isEnd;
    }
    
    public boolean startsWith(String prefix) {
        return searchNode(prefix) != null;
    }
    
    private TrieNode searchNode(String s) {
        var node = root;
        for (var c : s.toCharArray()) {
            if (node.children[c - 'a'] == null) return null;
            node = node.children[c - 'a'];
        }
        return node;
    }
    
    private static class TrieNode {
        TrieNode[] children = new TrieNode[26];
        boolean isEnd;
    }
}
```

- **پیچیدگی:** insert/search O(m)، فضا O(n·m)
- **الگو:** Trie

### ✍️ تمرین
مسئله‌ی `Word Dictionary with Wildcards` را با Trie حل کن (جستجو با `.`).

---

## 📅 روز ۷: مرور + پروژه

### 🎯 کارهای امروز

1. **مرور Pattern Matching for switch**: type, guarded, record deconstruction
2. **مرور Tree**: traversals, BST, LCA
3. **مرور Heap**: PriorityQueue, heapify, Kth
4. **مرور Trie**: insert, search, startsWith
5. **بازنویسی DSA**:
    - Validate BST (دو رویکرد)
    - Level Order Traversal
    - Implement Trie
6. **Mock Interview**: با تایمر ۴۵ دقیقه، `Lowest Common Ancestor` را حل کن.
7. **پروژه‌ی هفته: Autocomplete System**

### 🎁 پروژه‌ی هفته: Autocomplete System

یک سیستم autocomplete با Trie که کلمات را ذخیره و بر اساس prefix پیشنهاد می‌دهد.

```java
public class AutocompleteSystem {
    
    private static final class TrieNode {
        TrieNode[] children = new TrieNode[26];
        boolean isEnd;
    }
    
    private final TrieNode root = new TrieNode();
    
    public void insert(String word) {
        var node = root;
        for (var c : word.toCharArray()) {
            var idx = c - 'a';
            if (node.children[idx] == null) {
                node.children[idx] = new TrieNode();
            }
            node = node.children[idx];
        }
        node.isEnd = true;
    }
    
    public List<String> suggest(String prefix) {
        var node = findNode(prefix);
        if (node == null) return List.of();
        var result = new ArrayList<String>();
        collect(prefix, node, result);
        return result;
    }
    
    private TrieNode findNode(String prefix) {
        var node = root;
        for (var c : prefix.toCharArray()) {
            var idx = c - 'a';
            if (node.children[idx] == null) return null;
            node = node.children[idx];
        }
        return node;
    }
    
    private void collect(String prefix, TrieNode node, List<String> result) {
        if (node.isEnd) result.add(prefix);
        for (var i = 0; i < 26; i++) {
            if (node.children[i] != null) {
                collect(prefix + (char) ('a' + i), node.children[i], result);
            }
        }
    }
    
    public static void main(String[] args) {
        var ac = new AutocompleteSystem();
        ac.insert("apple");
        ac.insert("app");
        ac.insert("application");
        ac.insert("banana");
        ac.insert("band");
        
        System.out.println(ac.suggest("app"));
        // [app, apple, application]
        System.out.println(ac.suggest("ban"));
        // [banana, band]
    }
}
```

**توسعه‌ی اختیاری:**
- ذخیره‌ی frequency و مرتب‌سازی بر اساس آن
- Top K suggestions (با Heap)
- ذخیره/بارگذاری از فایل
- Case-insensitive
- پشتیبانی از یونیکد

### 📝 چک‌لیست پایان هفته

- [ ] Pattern Matching for switch با guarded pattern نوشته‌ام
- [ ] Record deconstruction را تست کرده‌ام
- [ ] هر چهار traversal درخت را پیاده کرده‌ام
- [ ] BST را با insert/delete/search پیاده کرده‌ام
- [ ] `MinHeap` را از صفر نوشته‌ام
- [ ] `PriorityQueue` با Comparator سفارشی استفاده کرده‌ام
- [ ] `Trie` را پیاده کرده‌ام
- [ ] ۷ مسئله‌ی DSA را حل کرده‌ام
- [ ] پروژه‌ی Autocomplete System را کامل کرده‌ام
- [ ] Mock Interview انجام داده‌ام

---

## 📊 آمار پایان هفته ۵

| مورد | تعداد |
|---|---|
| ویژگی‌های Java 17 | ۲ |
| ساختارهای داده | ۳ (Tree, Heap, Trie) |
| مسائل DSA | ۷ |
| پروژه | ۱ |
| Mock Interview | ۱ |
| **مجموع مسائل تا الان** | **۳۵** |
| **مجموع ویژگی‌ها تا الان** | **۲۲** |

---

## 💡 نکات مهم برای هفته ۶

هفته‌ی بعد می‌رویم سراغ:
- **Graph**: نمایش، BFS، DFS، Topological Sort
- **Union-Find**: پیاده‌سازی و کاربردها
- **Dijkstra**: کوتاه‌ترین مسیر
- **Module System و مهاجرت Java 8 → 17**

اگر خواستی، می‌توانم:
- **هفته‌ی ششم** را با همین سطح جزئیات بنویسم
- یک **پروژه‌ی Maven کامل** با تمام کدهای پنج هفته بسازم
- یک **آزمون خودارزیابی** برای مرور پنج هفته‌ی گذشته طراحی کنم

کدام را اول شروع کنیم؟
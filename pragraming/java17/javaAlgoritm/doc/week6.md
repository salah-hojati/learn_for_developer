عالی! هفته‌ی ششم روی **گراف** (BFS, DFS, Topological Sort)، **Union-Find**، **Dijkstra**، و **Module System + مهاجرت Java 8 → 17** تمرکز دارد. این هفته سنگین‌ترین هفته از نظر DSA است، ولی چون پایه‌ی خوبی داری، از پسش برمی‌آیی.

---

## 📅 هفته ۶ — روز ۱: نمایش گراف و BFS

### 🎓 بخش Java 17 (۳۰ دقیقه)

**دو روش نمایش گراف:**

**۱. Adjacency List (برای گراف‌های sparse):**
```java
// گراف بدون وزن
Map<Integer, List<Integer>> graph = new HashMap<>();

// اضافه کردن یال
graph.computeIfAbsent(1, k -> new ArrayList<>()).add(2);
graph.computeIfAbsent(2, k -> new ArrayList<>()).add(1);

// یا با آرایه (اگر گره‌ها ۰..n-1 هستند)
List<List<Integer>> adj = new ArrayList<>();
for (var i = 0; i < n; i++) adj.add(new ArrayList<>());
adj.get(0).add(1);
adj.get(1).add(0);
```

**۲. Adjacency Matrix (برای گراف‌های dense):**
```java
int[][] matrix = new int[n][n];
matrix[0][1] = 1; // یال از ۰ به ۱
matrix[1][0] = 1; // دوطرفه
```

**مقایسه:**
| ویژگی | Adjacency List | Adjacency Matrix |
|---|---|---|
| فضا | O(V + E) | O(V²) |
| بررسی یال | O(degree) | O(1) |
| iterate روی همسایه‌ها | O(degree) | O(V) |
| مناسب برای | Sparse | Dense |

**ساخت گراف از edge list با Stream (Java 17 idiom):**
```java
record Edge(int from, int to) {}

var edges = List.of(
    new Edge(0, 1), new Edge(1, 2), new Edge(2, 0)
);

var graph = edges.stream()
    .collect(Collectors.groupingBy(
        Edge::from,
        Collectors.mapping(Edge::to, Collectors.toList())
    ));
```

**نکته‌ی مصاحبه:** در مصاحبه، معمولاً `Map<Integer, List<Integer>>` یا `List<List<Integer>>` کافی است. اگر وزن داشت، `Map<Integer, List<int[]>>` یا `List<List<int[]>>` با `[neighbor, weight]`.

### 🧩 بخش DSA (۶۰ دقیقه) — BFS

**الگوی BFS:**
```java
public void bfs(Map<Integer, List<Integer>> graph, int start) {
    var visited = new HashSet<Integer>();
    var queue = new ArrayDeque<Integer>();
    queue.offer(start);
    visited.add(start);
    
    while (!queue.isEmpty()) {
        var node = queue.poll();
        System.out.println(node);
        for (var neighbor : graph.getOrDefault(node, List.of())) {
            if (!visited.contains(neighbor)) {
                visited.add(neighbor);
                queue.offer(neighbor);
            }
        }
    }
}
```

**BFS با فاصله (level):**
```java
public Map<Integer, Integer> bfsWithDistance(
        Map<Integer, List<Integer>> graph, int start) {
    var dist = new HashMap<Integer, Integer>();
    var queue = new ArrayDeque<Integer>();
    dist.put(start, 0);
    queue.offer(start);
    
    while (!queue.isEmpty()) {
        var node = queue.poll();
        for (var neighbor : graph.getOrDefault(node, List.of())) {
            if (!dist.containsKey(neighbor)) {
                dist.put(neighbor, dist.get(node) + 1);
                queue.offer(neighbor);
            }
        }
    }
    return dist;
}
```

**نکته:** BFS کوتاه‌ترین مسیر را در گراف **بدون وزن** پیدا می‌کند. برای گراف وزن‌دار، Dijkstra لازم است.

### 🧩 مسئله — Number of Islands

**مسئله:** ماتریس ۰/۱. تعداد جزیره‌ها را بشمار.

```java
public int numIslands(char[][] grid) {
    if (grid == null || grid.length == 0) return 0;
    var rows = grid.length;
    var cols = grid[0].length;
    var count = 0;
    
    for (var r = 0; r < rows; r++) {
        for (var c = 0; c < cols; c++) {
            if (grid[r][c] == '1') {
                count++;
                bfs(grid, r, c);
            }
        }
    }
    return count;
}

private void bfs(char[][] grid, int r, int c) {
    var rows = grid.length;
    var cols = grid[0].length;
    var queue = new ArrayDeque<int[]>();
    queue.offer(new int[]{r, c});
    grid[r][c] = '0';
    
    int[][] dirs = {{1,0}, {-1,0}, {0,1}, {0,-1}};
    
    while (!queue.isEmpty()) {
        var cell = queue.poll();
        for (var d : dirs) {
            var nr = cell[0] + d[0];
            var nc = cell[1] + d[1];
            if (nr >= 0 && nr < rows && nc >= 0 && nc < cols 
                    && grid[nr][nc] == '1') {
                grid[nr][nc] = '0';
                queue.offer(new int[]{nr, nc});
            }
        }
    }
}
```

- **پیچیدگی:** زمان O(rows × cols)، فضا O(min(rows, cols))
- **الگو:** BFS/DFS روی Grid

### ✍️ تمرین
مسئله‌ی `Rotting Oranges` را با BFS multi-source حل کن.

---

## 📅 روز ۲: DFS و Topological Sort

### 🎓 بخش Java 17 (۳۰ دقیقه)

**DFS recursive:**
```java
public void dfs(Map<Integer, List<Integer>> graph, int node, Set<Integer> visited) {
    if (visited.contains(node)) return;
    visited.add(node);
    System.out.println(node);
    for (var neighbor : graph.getOrDefault(node, List.of())) {
        dfs(graph, neighbor, visited);
    }
}
```

**DFS iterative:**
```java
public void dfsIterative(Map<Integer, List<Integer>> graph, int start) {
    var visited = new HashSet<Integer>();
    var stack = new ArrayDeque<Integer>();
    stack.push(start);
    
    while (!stack.isEmpty()) {
        var node = stack.pop();
        if (visited.contains(node)) continue;
        visited.add(node);
        System.out.println(node);
        for (var neighbor : graph.getOrDefault(node, List.of())) {
            if (!visited.contains(neighbor)) {
                stack.push(neighbor);
            }
        }
    }
}
```

**Topological Sort (Kahn's Algorithm — BFS):**
```java
public List<Integer> topoSort(int n, int[][] edges) {
    var graph = new HashMap<Integer, List<Integer>>();
    var indegree = new int[n];
    
    for (var e : edges) {
        graph.computeIfAbsent(e[0], k -> new ArrayList<>()).add(e[1]);
        indegree[e[1]]++;
    }
    
    var queue = new ArrayDeque<Integer>();
    for (var i = 0; i < n; i++) {
        if (indegree[i] == 0) queue.offer(i);
    }
    
    var result = new ArrayList<Integer>();
    while (!queue.isEmpty()) {
        var node = queue.poll();
        result.add(node);
        for (var neighbor : graph.getOrDefault(node, List.of())) {
            if (--indegree[neighbor] == 0) {
                queue.offer(neighbor);
            }
        }
    }
    
    return result.size() == n ? result : List.of(); // cycle detection
}
```

**Topological Sort (DFS-based):**
```java
public List<Integer> topoSortDFS(int n, int[][] edges) {
    var graph = new HashMap<Integer, List<Integer>>();
    for (var e : edges) {
        graph.computeIfAbsent(e[0], k -> new ArrayList<>()).add(e[1]);
    }
    
    var visited = new boolean[n];
    var stack = new ArrayDeque<Integer>();
    
    for (var i = 0; i < n; i++) {
        if (!visited[i]) dfsTopo(graph, i, visited, stack);
    }
    
    var result = new ArrayList<Integer>();
    while (!stack.isEmpty()) result.add(stack.pop());
    return result;
}

private void dfsTopo(Map<Integer, List<Integer>> graph, int node,
                     boolean[] visited, ArrayDeque<Integer> stack) {
    visited[node] = true;
    for (var neighbor : graph.getOrDefault(node, List.of())) {
        if (!visited[neighbor]) dfsTopo(graph, neighbor, visited, stack);
    }
    stack.push(node);
}
```

**نکته‌ی مصاحبه:** Topological Sort فقط روی **DAG** (Directed Acyclic Graph) کار می‌کند. اگر گراف cycle داشته باشد، Topological Sort وجود ندارد. Kahn's Algorithm برای cycle detection عالی است (اگر نتیجه < n باشد، cycle دارد).

### 🧩 مسئله — Course Schedule

**مسئله:** `numCourses` و `prerequisites`. آیا می‌توانی همه‌ی دوره‌ها را بگیری؟

```java
public boolean canFinish(int numCourses, int[][] prerequisites) {
    var graph = new HashMap<Integer, List<Integer>>();
    var indegree = new int[numCourses];
    
    for (var p : prerequisites) {
        graph.computeIfAbsent(p[1], k -> new ArrayList<>()).add(p[0]);
        indegree[p[0]]++;
    }
    
    var queue = new ArrayDeque<Integer>();
    for (var i = 0; i < numCourses; i++) {
        if (indegree[i] == 0) queue.offer(i);
    }
    
    var count = 0;
    while (!queue.isEmpty()) {
        var node = queue.poll();
        count++;
        for (var neighbor : graph.getOrDefault(node, List.of())) {
            if (--indegree[neighbor] == 0) queue.offer(neighbor);
        }
    }
    
    return count == numCourses;
}
```

- **پیچیدگی:** زمان O(V + E)، فضا O(V + E)
- **الگو:** Topological Sort / Cycle Detection

### ✍️ تمرین
مسئله‌ی `Course Schedule II` را حل کن (بازگرداندن ترتیب).

---

## 📅 روز ۳: Union-Find (Disjoint Set Union)

### 🎓 بخش Java 17 (۳۰ دقیقه)

**Union-Find** برای مدیریت مجموعه‌های جدا از هم. دو عملیات اصلی:
- `find(x)`: ریشه‌ی مجموعه‌ی x را پیدا کن
- `union(x, y)`: دو مجموعه را ادغام کن

**پیاده‌سازی با Path Compression + Union by Rank:**
```java
public class UnionFind {
    private final int[] parent;
    private final int[] rank;
    private int components;
    
    public UnionFind(int n) {
        parent = new int[n];
        rank = new int[n];
        components = n;
        for (var i = 0; i < n; i++) parent[i] = i;
    }
    
    public int find(int x) {
        if (parent[x] != x) {
            parent[x] = find(parent[x]); // path compression
        }
        return parent[x];
    }
    
    public boolean union(int x, int y) {
        var rootX = find(x);
        var rootY = find(y);
        if (rootX == rootY) return false;
        
        // union by rank
        if (rank[rootX] < rank[rootY]) {
            parent[rootX] = rootY;
        } else if (rank[rootX] > rank[rootY]) {
            parent[rootY] = rootX;
        } else {
            parent[rootY] = rootX;
            rank[rootX]++;
        }
        components--;
        return true;
    }
    
    public boolean connected(int x, int y) {
        return find(x) == find(y);
    }
    
    public int getComponents() {
        return components;
    }
}
```

**پیچیدگی (با هر دو بهینه‌سازی):**
- `find`: amortized O(α(n)) ≈ O(1)
- `union`: amortized O(α(n))
- `α` تابع Ackermann معکوس است که برای هر n عملی تقریباً ≤ ۵ است.

**کاربردها:**
- Kruskal's MST
- Cycle detection در گراف بدون جهت
- Connected components
- Dynamic connectivity
- Image processing (percolation)

**نکته‌ی مصاحبه:** Union-Find با Path Compression + Union by Rank یکی از بهینه‌ترین ساختارهای داده است. اگر مسئله "components" یا "connected" داشت، به Union-Find فکر کن.

### 🧩 مسئله — Number of Connected Components

**مسئله:** `n` گره و لیست یال‌ها. تعداد components را بشمار.

```java
public int countComponents(int n, int[][] edges) {
    var uf = new UnionFind(n);
    for (var e : edges) {
        uf.union(e[0], e[1]);
    }
    return uf.getComponents();
}
```

**نسخه‌ی DFS:**
```java
public int countComponents(int n, int[][] edges) {
    var graph = new HashMap<Integer, List<Integer>>();
    for (var e : edges) {
        graph.computeIfAbsent(e[0], k -> new ArrayList<>()).add(e[1]);
        graph.computeIfAbsent(e[1], k -> new ArrayList<>()).add(e[0]);
    }
    
    var visited = new boolean[n];
    var count = 0;
    for (var i = 0; i < n; i++) {
        if (!visited[i]) {
            count++;
            dfs(graph, i, visited);
        }
    }
    return count;
}
```

- **پیچیدگی:** زمان O(V + E)، فضا O(V)
- **الگو:** Union-Find / DFS

### ✍️ تمرین
مسئله‌ی `Graph Valid Tree` را با Union-Find حل کن.

---

## 📅 روز ۴: Dijkstra و کوتاه‌ترین مسیر

### 🎓 بخش Java 17 (۳۰ دقیقه)

**Dijkstra** برای کوتاه‌ترین مسیر در گراف **وزن‌دار با وزن غیرمنفی**.

**پیاده‌سازی با PriorityQueue:**
```java
public int[] dijkstra(int n, Map<Integer, List<int[]>> graph, int src) {
    var dist = new int[n];
    Arrays.fill(dist, Integer.MAX_VALUE);
    dist[src] = 0;
    
    var pq = new PriorityQueue<int[]>(Comparator.comparingInt(a -> a[1]));
    pq.offer(new int[]{src, 0}); // [node, dist]
    
    while (!pq.isEmpty()) {
        var curr = pq.poll();
        var node = curr[0];
        var d = curr[1];
        
        if (d > dist[node]) continue; // stale entry
        
        for (var edge : graph.getOrDefault(node, List.of())) {
            var neighbor = edge[0];
            var weight = edge[1];
            var newDist = d + weight;
            
            if (newDist < dist[neighbor]) {
                dist[neighbor] = newDist;
                pq.offer(new int[]{neighbor, newDist});
            }
        }
    }
    return dist;
}
```

**نکات مهم:**
- Dijkstra روی گراف با **وزن منفی** کار نمی‌کند (برای آن Bellman-Ford لازم است).
- `if (d > dist[node]) continue;` یک بهینه‌سازی مهم است (stale entries).
- پیچیدگی: O((V + E) log V) با binary heap.

**مقایسه‌ی الگوریتم‌های کوتاه‌ترین مسیر:**
| الگوریتم | گراف | پیچیدگی | کاربرد |
|---|---|---|---|
| BFS | بدون وزن | O(V + E) | کوتاه‌ترین مسیر |
| Dijkstra | وزن غیرمنفی | O((V+E) log V) | کوتاه‌ترین مسیر |
| Bellman-Ford | هر وزنی | O(VE) | تشخیص cycle منفی |
| Floyd-Warshall | همه جفت‌ها | O(V³) | همه جفت‌ها |

**نکته‌ی مصاحبه:** اگر در مصاحبه گراف وزن‌دار دیدی و وزن منفی نبود، Dijkstra. اگر وزن منفی بود یا می‌خواستی cycle منفی تشخیص بدهی، Bellman-Ford.

### 🧩 مسئله — Network Delay Time

**مسئله:** `times[i] = [u, v, w]`. از گره `k` پیام بفرست. چقدر طول می‌کشد تا همه‌ی گره‌ها پیام را بگیرند؟

```java
public int networkDelayTime(int[][] times, int n, int k) {
    var graph = new HashMap<Integer, List<int[]>>();
    for (var t : times) {
        graph.computeIfAbsent(t[0], x -> new ArrayList<>()).add(new int[]{t[1], t[2]});
    }
    
    var dist = new int[n + 1];
    Arrays.fill(dist, Integer.MAX_VALUE);
    dist[k] = 0;
    
    var pq = new PriorityQueue<int[]>(Comparator.comparingInt(a -> a[1]));
    pq.offer(new int[]{k, 0});
    
    while (!pq.isEmpty()) {
        var curr = pq.poll();
        var node = curr[0];
        var d = curr[1];
        if (d > dist[node]) continue;
        
        for (var edge : graph.getOrDefault(node, List.of())) {
            var newDist = d + edge[1];
            if (newDist < dist[edge[0]]) {
                dist[edge[0]] = newDist;
                pq.offer(new int[]{edge[0], newDist});
            }
        }
    }
    
    var max = 0;
    for (var i = 1; i <= n; i++) {
        if (dist[i] == Integer.MAX_VALUE) return -1;
        max = Math.max(max, dist[i]);
    }
    return max;
}
```

- **پیچیدگی:** زمان O((V + E) log V)، فضا O(V + E)
- **الگو:** Dijkstra

### ✍️ تمرین
مسئله‌ی `Path with Minimum Effort` را با Dijkstra حل کن.

---

## 📅 روز ۵: Java Modules (JPMS)

### 🎓 بخش Java 17 (۳۰ دقیقه)

**Java Platform Module System (JPMS)** در Java 9 معرفی شد و هدفش کپسوله‌سازی قوی‌تر و مدیریت وابستگی است.

**فایل `module-info.java`:**
```java
module com.example.myapp {
    requires java.base;        // پیش‌فرض
    requires java.sql;
    requires transitive java.logging; // transitive برای مصرف‌کننده‌ها
    
    exports com.example.myapp.api;
    exports com.example.myapp.model to com.example.other;
    
    opens com.example.myapp.internal to spring.core;
    
    uses com.example.myapp.spi.Plugin;
    provides com.example.myapp.spi.Plugin 
        with com.example.myapp.internal.MyPlugin;
}
```

**دستورات کلیدی:**
- `requires`: وابستگی به ماژول دیگر
- `requires transitive`: وابستگی transitive (مصرف‌کننده‌ها هم می‌بینند)
- `requires static`: وابستگی اختیاری در compile-time
- `exports`: در دسترس قرار دادن package
- `exports ... to`: در دسترس قرار دادن فقط برای ماژول‌های خاص
- `opens`: باز کردن برای reflection
- `uses` / `provides`: ServiceLoader

**مزایا:**
- کپسوله‌سازی قوی (public در ماژول = قابل دسترس از بیرون)
- تشخیص وابستگی چرخه‌ای در compile-time
- کاهش حجم JRE با `jlink`
- Startup سریع‌تر

**چالش‌ها:**
- بسیاری از کتابخانه‌ها هنوز ماژولار نشده‌اند (Automatic Modules)
- Reflection محدود شده
- پیچیدگی برای پروژه‌های کوچک

**نکته‌ی مصاحبه:** JPMS در پروژه‌های enterprise (مثل Spring Boot) معمولاً استفاده نمی‌شود ولی در مصاحبه‌های Java 17 ممکن است پرسیده شود. بدانی که چیست و چرا معرفی شد، کافی است.

### 🧩 مسئله — Number of Provinces

**مسئله:** ماتریس `isConnected`. تعداد provinces (components) را بشمار.

**راه‌حل — DFS:**
```java
public int findCircleNum(int[][] isConnected) {
    var n = isConnected.length;
    var visited = new boolean[n];
    var count = 0;
    
    for (var i = 0; i < n; i++) {
        if (!visited[i]) {
            count++;
            dfs(isConnected, visited, i);
        }
    }
    return count;
}

private void dfs(int[][] isConnected, boolean[] visited, int city) {
    visited[city] = true;
    for (var j = 0; j < isConnected.length; j++) {
        if (isConnected[city][j] == 1 && !visited[j]) {
            dfs(isConnected, visited, j);
        }
    }
}
```

**راه‌حل — Union-Find:**
```java
public int findCircleNum(int[][] isConnected) {
    var n = isConnected.length;
    var uf = new UnionFind(n);
    for (var i = 0; i < n; i++) {
        for (var j = i + 1; j < n; j++) {
            if (isConnected[i][j] == 1) uf.union(i, j);
        }
    }
    return uf.getComponents();
}
```

- **پیچیدگی:** زمان O(n²)، فضا O(n)
- **الگو:** DFS / Union-Find

### ✍️ تمرین
یک `module-info.java` ساده برای یک پروژه‌ی Maven بساز و با `mvn compile` کامپایل کن.

---

## 📅 روز ۶: مهاجرت Java 8 → 17

### 🎓 بخش Java 17 (۳۰ دقیقه)

**گام‌های مهاجرت:**

**۱. تغییر نسخه در `pom.xml`:**
```xml
<properties>
    <maven.compiler.source>17</maven.compiler.source>
    <maven.compiler.target>17</maven.compiler.target>
    <maven.compiler.release>17</maven.compiler.release>
</properties>
```

**۲. کتابخانه‌های حذف‌شده که باید اضافه شوند:**
```xml
<!-- JAXB (حذف شده در Java 11) -->
<dependency>
    <groupId>jakarta.xml.bind</groupId>
    <artifactId>jakarta.xml.bind-api</artifactId>
    <version>4.0.0</version>
</dependency>

<!-- JAX-WS -->
<dependency>
    <groupId>jakarta.xml.ws</groupId>
    <artifactId>jakarta.xml.ws-api</artifactId>
    <version>4.0.0</version>
</dependency>

<!-- Activation -->
<dependency>
    <groupId>jakarta.activation</groupId>
    <artifactId>jakarta.activation-api</artifactId>
    <version>2.1.0</version>
</dependency>
```

**۳. تغییر `javax` به `jakarta`:**
- `javax.persistence.*` → `jakarta.persistence.*`
- `javax.servlet.*` → `jakarta.servlet.*`
- `javax.validation.*` → `jakarta.validation.*`

**۴. حذف APIهای منسوخ:**
- `sun.misc.Unsafe` (محدود شده)
- `javax.xml.bind.*` (حذف شده)
- `java.awt.Applet` (حذف شده)
- `SecurityManager` (منسوخ)

**۵. کپسوله‌سازی قوی JDK:**
```bash
# اگر مجبوری به API داخلی دسترسی داشته باشی
--add-opens java.base/java.lang=ALL-UNNAMED
--add-exports java.base/sun.nio.ch=ALL-UNNAMED
```

**۶. ارتقای وابستگی‌ها:**
- Spring Framework 5 → 6
- Spring Boot 2.x → 3.x
- Hibernate 5 → 6
- JUnit 4 → 5

**۷. تست:**
```bash
mvn clean test
mvn versions:display-dependency-updates
mvn versions:display-plugin-updates
```

**چک‌لیست مهاجرت:**
- [ ] JDK 17 نصب و `JAVA_HOME` تنظیم شده
- [ ] نسخه در pom/gradle به 17 تغییر کرده
- [ ] وابستگی‌های حذف‌شده اضافه شده‌اند
- [ ] `javax` → `jakarta` انجام شده
- [ ] تست‌ها پاس می‌شوند
- [ ] Performance مقایسه شده
- [ ] `jdeps` برای بررسی وابستگی‌ها اجرا شده
- [ ] `jdeprscan` برای APIهای منسوخ اجرا شده

**ابزارهای مفید:**
```bash
# بررسی وابستگی‌ها به JDK internal
jdeps --jdk-internals myapp.jar

# بررسی APIهای منسوخ
jdeprscan --release 17 myapp.jar

# بررسی سازگاری
jdeprscan --release 17 --list myapp.jar
```

**نکته‌ی مصاحبه:** اگر از تو بپرسند "چالش‌های مهاجرت از 8 به 17 چه بود؟"، این‌ها را بگو:
1. حذف JAXB/JAX-WS و نیاز به وابستگی جدید
2. `javax` → `jakarta` (مخصوصاً برای Spring Boot 3)
3. کپسوله‌سازی قوی JDK و `--add-opens`
4. ارتقای کتابخانه‌ها (Spring 5→6، Hibernate 5→6)
5. حذف `SecurityManager` و APIهای منسوخ

### 🧩 مسئله — Redundant Connection

**مسئله:** یال‌هایی که یک cycle ایجاد می‌کنند را پیدا کن.

```java
public int[] findRedundantConnection(int[][] edges) {
    var n = edges.length;
    var uf = new UnionFind(n + 1);
    
    for (var e : edges) {
        if (!uf.union(e[0], e[1])) {
            return e;
        }
    }
    return new int[]{};
}
```

- **پیچیدگی:** زمان O(n α(n)) ≈ O(n)، فضا O(n)
- **الگو:** Union-Find

### ✍️ تمرین
یک پروژه‌ی ساده‌ی Java 8 را بردار و به Java 17 مهاجرت بده. `jdeps` و `jdeprscan` را اجرا کن.

---

## 📅 روز ۷: مرور + پروژه

### 🎯 کارهای امروز

1. **مرور Graph**: BFS, DFS, Topological Sort
2. **مرور Union-Find**: پیاده‌سازی و کاربرد
3. **مرور Dijkstra**: PriorityQueue-based
4. **مرور Modules**: `module-info.java` و مهاجرت
5. **بازنویسی DSA**:
    - Number of Islands
    - Course Schedule
    - Network Delay Time
6. **Mock Interview**: با تایمر ۴۵ دقیقه، `Word Ladder` را با BFS حل کن.
7. **پروژه‌ی هفته: Graph Path Finder CLI**

### 🎁 پروژه‌ی هفته: Graph Path Finder CLI

یک برنامه که گراف را از ورودی می‌گیرد و کوتاه‌ترین مسیر را پیدا می‌کند.

```java
public class GraphPathFinder {
    
    private final Map<Integer, List<int[]>> graph = new HashMap<>();
    
    public void addEdge(int from, int to, int weight) {
        graph.computeIfAbsent(from, k -> new ArrayList<>()).add(new int[]{to, weight});
        graph.computeIfAbsent(to, k -> new ArrayList<>()).add(new int[]{from, weight});
    }
    
    public record PathResult(List<Integer> path, int distance) {}
    
    public PathResult shortestPath(int src, int dst) {
        var dist = new HashMap<Integer, Integer>();
        var prev = new HashMap<Integer, Integer>();
        var pq = new PriorityQueue<int[]>(Comparator.comparingInt(a -> a[1]));
        
        dist.put(src, 0);
        pq.offer(new int[]{src, 0});
        
        while (!pq.isEmpty()) {
            var curr = pq.poll();
            var node = curr[0];
            var d = curr[1];
            
            if (node == dst) break;
            if (d > dist.getOrDefault(node, Integer.MAX_VALUE)) continue;
            
            for (var edge : graph.getOrDefault(node, List.of())) {
                var neighbor = edge[0];
                var newDist = d + edge[1];
                if (newDist < dist.getOrDefault(neighbor, Integer.MAX_VALUE)) {
                    dist.put(neighbor, newDist);
                    prev.put(neighbor, node);
                    pq.offer(new int[]{neighbor, newDist});
                }
            }
        }
        
        if (!dist.containsKey(dst)) return new PathResult(List.of(), -1);
        
        var path = new LinkedList<Integer>();
        for (Integer at = dst; at != null; at = prev.get(at)) {
            path.addFirst(at);
        }
        return new PathResult(path, dist.get(dst));
    }
    
    public static void main(String[] args) {
        var g = new GraphPathFinder();
        g.addEdge(0, 1, 4);
        g.addEdge(0, 2, 1);
        g.addEdge(2, 1, 2);
        g.addEdge(1, 3, 1);
        g.addEdge(2, 3, 5);
        
        var result = g.shortestPath(0, 3);
        System.out.println("Path: " + result.path());
        System.out.println("Distance: " + result.distance());
        // Path: [0, 2, 1, 3]
        // Distance: 4
    }
}
```

**توسعه‌ی اختیاری:**
- پشتیبانی از گراف جهت‌دار
- Bellman-Ford برای وزن منفی
- نمایش گراف با GraphViz
- خواندن گراف از فایل
- Topological Sort روی DAG
- Union-Find برای connected components

### 📝 چک‌لیست پایان هفته

- [ ] گراف را با Adjacency List پیاده کرده‌ام
- [ ] BFS و DFS را نوشته‌ام
- [ ] Topological Sort را با هر دو روش پیاده کرده‌ام
- [ ] Union-Find را با Path Compression + Rank نوشته‌ام
- [ ] Dijkstra را با PriorityQueue پیاده کرده‌ام
- [ ] `module-info.java` را تست کرده‌ام
- [ ] یک پروژه را از Java 8 به 17 مهاجرت داده‌ام
- [ ] ۷ مسئله‌ی DSA را حل کرده‌ام
- [ ] پروژه‌ی Graph Path Finder را کامل کرده‌ام
- [ ] Mock Interview انجام داده‌ام

---

## 📊 آمار پایان هفته ۶

| مورد | تعداد |
|---|---|
| ویژگی‌های Java 17 | ۲ (Modules, Migration) |
| الگوریتم‌های گراف | ۴ (BFS, DFS, TopoSort, Dijkstra) |
| ساختارهای داده | ۱ (Union-Find) |
| مسائل DSA | ۷ |
| پروژه | ۱ |
| Mock Interview | ۱ |
| **مجموع مسائل تا الان** | **۴۲** |
| **مجموع ویژگی‌ها تا الان** | **۲۴** |

---

## 💡 نکات مهم برای هفته ۷

هفته‌ی بعد می‌رویم سراغ:
- **GC و JVM Tuning**: G1, ZGC, Heap, Metaspace
- **Profiling**: JFR, jcmd, jmap, jstack
- **Dynamic Programming**: 1D, 2D, Knapsack, LCS, LIS
- **Bit Manipulation**: مقدمه

اگر خواستی، می‌توانم:
- **هفته‌ی هفتم** را با همین سطح جزئیات بنویسم
- یک **پروژه‌ی Maven کامل** با تمام کدهای شش هفته بسازم
- یک **آزمون خودارزیابی** برای مرور شش هفته‌ی گذشته طراحی کنم

کدام را اول شروع کنیم؟
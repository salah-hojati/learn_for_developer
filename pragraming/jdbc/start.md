# JDBC (Java Database Connectivity) — A Complete Introduction

JDBC is the standard Java API for connecting to relational databases. It lets you execute SQL statements from Java code in a database-agnostic way (the same code works with MySQL, PostgreSQL, Oracle, etc., by swapping the driver).

---

## 1. Core Architecture

```
Java Application
       │
       ▼
  JDBC API (java.sql / javax.sql)
       │
       ▼
  JDBC Driver (vendor-specific JAR)
       │
       ▼
  Database (MySQL, PostgreSQL, Oracle...)
```

The JDBC API defines **interfaces**; the database vendor provides **implementations** (the driver JAR).

### Key interfaces

| Interface | Purpose |
|-----------|---------|
| `DriverManager` | Manages a list of drivers, creates connections |
| `Connection` | A session with the database |
| `Statement` | Executes static SQL |
| `PreparedStatement` | Executes parameterized SQL (prevents SQL injection) |
| `CallableStatement` | Calls stored procedures |
| `ResultSet` | Holds query results (a cursor over rows) |
| `DataSource` | Modern alternative to `DriverManager` (supports pooling) |

---

## 2. The 6-Step JDBC Workflow

Every JDBC program follows these steps:

```java
import java.sql.*;

public class JdbcDemo {
    public static void main(String[] args) throws SQLException {
        // 1. Define connection info
        String url  = "jdbc:mysql://localhost:3306/mydb";
        String user = "root";
        String pass = "secret";

        // 2 & 3. Get a connection (driver is auto-registered in modern JDBC)
        try (Connection conn = DriverManager.getConnection(url, user, pass)) {

            // 4. Create a statement
            String sql = "SELECT id, name FROM users WHERE age > ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, 18);   // bind parameter

                // 5. Execute and get ResultSet
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        int id    = rs.getInt("id");
                        String nm = rs.getString("name");
                        System.out.println(id + " -> " + nm);
                    }
                }
            }
        } // connection auto-closed here (try-with-resources)
    }
}
```

### URL format
```
jdbc:<subprotocol>://<host>:<port>/<database>[?param=value...]
```

Common examples:
- `jdbc:mysql://localhost:3306/mydb`
- `jdbc:postgresql://localhost:5432/mydb`
- `jdbc:oracle:thin:@localhost:1521:ORCL`
- `jdbc:h2:mem:testdb` (in-memory, great for testing)

---

## 3. Statement Types — When to Use Which

### `Statement` — static SQL, no parameters
```java
Statement st = conn.createStatement();
st.executeUpdate("UPDATE users SET active = 1");
ResultSet rs = st.executeQuery("SELECT * FROM users");
```

### `PreparedStatement` — **always prefer this**
Benefits:
- Prevents SQL injection (parameters are sent separately from SQL)
- Precompiled → faster for repeated execution
- Handles type conversion

```java
String sql = "INSERT INTO users (name, age) VALUES (?, ?)";
PreparedStatement ps = conn.prepareStatement(sql);
ps.setString(1, "Alice");
ps.setInt(2, 30);
ps.executeUpdate();
```

### `CallableStatement` — stored procedures
```java
CallableStatement cs = conn.prepareCall("{call get_user(?)}");
cs.setInt(1, 42);
ResultSet rs = cs.executeQuery();
```

---

## 4. Executing SQL — The Three Methods

| Method | Use for | Returns |
|--------|---------|---------|
| `executeQuery()` | `SELECT` | `ResultSet` |
| `executeUpdate()` | `INSERT`, `UPDATE`, `DELETE`, DDL | `int` (rows affected) |
| `execute()` | Unknown / multiple results | `boolean` (true = ResultSet available) |

---

## 5. Reading a `ResultSet`

```java
while (rs.next()) {                    // advance cursor
    int id       = rs.getInt(1);       // by column index (1-based!)
    String name  = rs.getString("name"); // by column label (safer)
    Date created = rs.getDate("created_at");
    double bal   = rs.getDouble("balance");
    boolean v    = rs.getBoolean("active");

    // NULL handling
    int nullable = rs.getInt("maybe_null");
    if (rs.wasNull()) {
        // value was SQL NULL
    }
}
```

**`next()` returns `false` when no rows remain.** Use `rs.getMetaData()` for column info programmatically.

---

## 6. Resource Management — try-with-resources

Every JDBC resource (`Connection`, `Statement`, `ResultSet`) must be closed. Use try-with-resources:

```java
try (Connection c = DriverManager.getConnection(url);
     PreparedStatement p = c.prepareStatement(sql);
     ResultSet r = p.executeQuery()) {
    // work here
}
// all closed in reverse order automatically
```

---

## 7. Transactions

By default, each statement auto-commits. To group operations:

```java
try (Connection conn = ds.getConnection()) {
    conn.setAutoCommit(false);
    try {
        // ... multiple updates ...
        conn.commit();
    } catch (SQLException e) {
        conn.rollback();
        throw e;
    } finally {
        conn.setAutoCommit(true);
    }
}
```

Savepoints for partial rollback:
```java
Savepoint sp = conn.setSavepoint("before_batch");
// ...
conn.rollback(sp);
```

---

## 8. Batch Processing (performance)

Instead of firing N statements to the DB:

```java
PreparedStatement ps = conn.prepareStatement("INSERT INTO t VALUES (?, ?)");
for (Row r : rows) {
    ps.setInt(1, r.a);
    ps.setString(2, r.b);
    ps.addBatch();
}
int[] counts = ps.executeBatch();   // one round-trip
```

---

## 9. Modern Best Practice: `DataSource` + Connection Pool

Don't create a new physical connection per request. Use a **pool** (HikariCP, Apache DBCP, c3p0) via a `DataSource`:

```java
HikariConfig cfg = new HikariConfig();
cfg.setJdbcUrl("jdbc:postgresql://localhost:5432/mydb");
cfg.setUsername("app");
cfg.setPassword("secret");
cfg.setMaximumPoolSize(10);

DataSource ds = new HikariDataSource(cfg);

// Later:
try (Connection c = ds.getConnection()) { ... }
```

`DataSource` is the JDBC 2.0+ standard — DI frameworks (Spring, Jakarta EE) use it everywhere.

---

## 10. Common Pitfalls

| Pitfall | Fix |
|---------|-----|
| String concatenation in SQL | Use `PreparedStatement` with `?` |
| Forgetting to close connections | try-with-resources |
| `rs.getInt("col")` without checking `wasNull()` | Check `wasNull()` for nullable primitives |
| Creating a connection per query | Use a pool / `DataSource` |
| `Class.forName("...")` everywhere | Modern JDBC 4+ auto-loads drivers from the classpath |
| Case sensitivity in column names | Prefer column **index** or match DB's exact label |
| Not using `conn.setAutoCommit(false)` for multi-step logic | Wrap in transaction |

---

## 11. Suggested Learning Path

1. **Set up a sandbox** — use H2 in-memory (`jdbc:h2:mem:test`) so you don't need a real DB.
2. **Write a CRUD app** — insert, select, update, delete a `Person` table.
3. **Refactor to `PreparedStatement` everywhere** — feel why it matters.
4. **Add transactions** — a money transfer (debit one account, credit another).
5. **Introduce a connection pool** (HikariCP).
6. **Explore `ResultSetMetaData` / `DatabaseMetaData`** — dynamic column handling.
7. **Build a tiny DAO layer** — separate SQL from business logic.
8. **Compare with Spring JDBC / JPA** — see how frameworks abstract the boilerplate.

---

## Quick Reference: Minimal POM Dependencies

```xml
<!-- MySQL -->
<dependency>
  <groupId>com.mysql</groupId>
  <artifactId>mysql-connector-j</artifactId>
  <version>8.4.0</version>
</dependency>

<!-- PostgreSQL -->
<dependency>
  <groupId>org.postgresql</groupId>
  <artifactId>postgresql</artifactId>
  <version>42.7.3</version>
</dependency>

<!-- HikariCP -->
<dependency>
  <groupId>com.zaxxer</groupId>
  <artifactId>HikariCP</artifactId>
  <version>5.1.0</version>
</dependency>

<!-- H2 (testing) -->
<dependency>
  <groupId>com.h2database</groupId>
  <artifactId>h2</artifactId>
  <version>2.2.224</version>
</dependency>
```

---

Want me to go deeper on any specific area? For example:
- **Build a complete CRUD example** with a `User` table end-to-end
- **Spring JDBC vs raw JDBC** comparison
- **Connection pooling internals** (how HikariCP works)
- **JDBC + transactions + isolation levels**
- **Batch processing / performance tuning**
- **Using JDBC with a specific database** (MySQL/PostgreSQL/Oracle)
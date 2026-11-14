Absolutely — here’s the same guidance in English.

You **can learn MongoDB without learning a programming language**. Treat MongoDB as a standalone database system and interact with it directly using **MongoDB Compass** (GUI) or **mongosh** (official shell). You’ll learn database concepts, data modeling, queries, indexes, and aggregation — not application programming.

## Learning path for a “non-programming” learner

### 1. Build the core mental model
- **Document**: the basic unit of data, JSON-like. Fields can vary between documents and can be nested.
- **Collection**: a group of documents, similar to a table, but **without a fixed schema**.
- **Database**: a container for collections.
- **BSON**: MongoDB’s binary storage format; a JSON extension supporting more data types.

### 2. Practice CRUD with GUI and shell
This is the most important hands-on part — no programming required.
- **Tool choice**: Start with **MongoDB Compass** (official free GUI). It lets you insert, query, update, and delete visually. Then try **mongosh** for direct database commands.
- **Practice environment**: Create a free **MongoDB Atlas M0 cluster** — no local installation needed.
- **Core operations**: Practice **insert, find, update, delete** in Compass or mongosh. You’ll use **MQL (MongoDB Query Language)**, which is a database command language, not a general-purpose programming language.

Example of a database command — not application programming:
```javascript
db.users.insertOne({ name: "Alice", age: 30 })
db.users.find({ age: { $gt: 25 } })
```
You’re learning operators like `find`, `$gt`, `$lookup` — database operations, not `if/else` or loops.

### 3. Learn data organization and advanced querying
- **Indexes**: how to speed up queries and avoid full collection scans.
- **Aggregation Pipeline**: MongoDB’s framework for multi-stage data transformation and analysis.
- **Data Modeling**: the key database-design skill — when to **embed** vs. **reference** data based on query patterns.

### 4. Understand architecture and operations concepts
- **Replica Set**: multiple MongoDB instances maintaining the same data for redundancy and high availability; automatic failover.
- **Sharding**: distributing data across multiple machines for horizontal scaling.

## Recommended free resources
1. **MongoDB University — M001: MongoDB Basics**: official, free, provides practice clusters, focused on database operations. Best starting point.
2. **MongoDB Compass + MongoDB Atlas free tier**: fastest feedback loop for non-programmers.
3. **MongoDB-Zero-to-Hero** on GitHub: beginner-oriented, uses simple language and demos, emphasizes Compass and Atlas.
4. **learn-databases** on GitHub: self-study database course; its MongoDB module is designed for learners without programming/database experience.
5. **The Little MongoDB Book**: short introductory reading with examples.

## Suggested first week
- Day 1–2: Create Atlas M0 cluster; install/open Compass; create a database and collection.
- Day 3–4: Practice insert/find/update/delete in Compass.
- Day 5: Repeat the same operations in mongosh.
- Day 6–7: Start MongoDB University M001 and learn basic query operators.

Start with **Atlas + Compass + MongoDB University M001**. That combination is the most direct, gives fast feedback, and requires no programming language.
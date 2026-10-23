Having a separate Linux machine for Docker while developing on Windows 11 with IntelliJ IDEA is a great setup. Here’s how to connect your Spring Boot application on Windows to Redis running in Docker on your Linux machine, plus a learning path to follow.

### 🐧 Step 1: Set Up Redis on Your Linux Docker Host

First, get Redis running and accessible from outside the Linux machine.

**1. Start Redis with Port Mapping**

Run this command on your Linux machine to start a Redis container and map its port to the host:

```bash
docker run --name my-redis -p 6379:6379 -d redis
```

The `-p 6379:6379` flag publishes the container's port 6379 to the same port on the Linux host, allowing external connections.

**2. Open the Firewall**

By default, many Linux distributions block external access to port 6379. You need to allow it. If you're using `firewalld` (common on CentOS/RHEL/Fedora), run:

```bash
sudo firewall-cmd --zone=public --add-port=6379/tcp --permanent
sudo firewall-cmd --reload
```

This opens port 6379 for TCP traffic and reloads the firewall rules. If you use `ufw` (common on Ubuntu/Debian), the command would be `sudo ufw allow 6379/tcp`. For a development environment, you could also temporarily stop the firewall with `sudo systemctl stop firewalld`, but opening the specific port is safer.

**3. (Recommended) Set a Password**

For security, especially if the machine is on a shared network, set a Redis password. You can do this by passing a configuration flag when starting the container:

```bash
docker run --name my-redis -p 6379:6379 -d redis redis-server --requirepass "YourStrongPassword"
```

**4. Find Your Linux Machine's IP Address**

On the Linux machine, run `ip addr` or `hostname -I` to get its local IP address (e.g., `192.168.1.100`). You'll need this for your Spring Boot configuration.

### ☕ Step 2: Configure Spring Boot to Connect Remotely

In your Spring Boot project on Windows, update your `application.properties` (or `application.yml`) to point to your Linux machine's IP address instead of `localhost`.

**application.properties:**
```properties
spring.data.redis.host=192.168.1.100
spring.data.redis.port=6379
spring.data.redis.password=YourStrongPassword
```

> **Note:** Recent Spring Boot versions use the `spring.data.redis.*` property prefix. Older versions used `spring.redis.*`. If the `spring.data.redis` properties aren't recognized, try the older prefix.

The critical point is that **`localhost` on your Windows machine refers to the Windows machine itself**, not the Linux Docker host. You must use the Linux machine's actual IP address.

### 🔌 Step 3: Use the IntelliJ IDEA Redis Plugin

IntelliJ IDEA (Ultimate Edition) includes a built-in Redis client via the **Database Tools and SQL** plugin, which is enabled by default.

**To connect:**
1. Go to **File | New | Data Source** and select **Redis** (or find it in the **Database** tool window on the right).
2. In the connection settings, enter:
    - **Host:** Your Linux machine's IP address (e.g., `192.168.1.100`)
    - **Port:** `6379`
    - **Password:** Your Redis password (if set)
3. Click **Test Connection** to verify. Once successful, you can browse keys, run commands, and inspect data directly in IDEA.

> **Note:** The built-in Redis client is only available in IntelliJ IDEA Ultimate. Community Edition users can install a third-party plugin like **Iedis** from the JetBrains Marketplace.

### 🗺️ Your Learning Path (Revisited)

With your environment now set up for remote development, you can follow a structured path:

**Stage 1: Redis Fundamentals**
- **Redis University (Free):** Start with "Get Started with Redis" for core data structures and commands.
- **LabEx:** Use interactive labs for hands-on practice with advanced features like transactions and Pub/Sub.

**Stage 2: Redis with Java**
- **Official Tutorial (Jedis):** Learn the basics of connecting and operating Redis from Java.
- **CodeSignal:** Consider "Learning and Mastering Redis with Java and Jedis" for a more comprehensive course.

**Stage 3: Redis with Spring Boot**
- **Official Course:** "Spring and Redis: Up and Running" from Redis.io is an excellent, practical guide that walks you through building a Spring Boot app with Redis. It even shows you how to manage Redis with Docker Compose.
- **Udemy Masterclass:** For advanced topics like reactive programming and caching strategies, the "Reactive Redis Masterclass For Java Spring Boot Developers" is highly relevant.

### 💡 Practical Tips

- **Verify Connectivity First:** Before running your Spring Boot app, test the connection from your Windows machine using a command-line tool or the IDEA plugin. This isolates network issues from application configuration issues.
- **Use Environment Variables:** For cleaner configuration, use environment variables for the Redis host instead of hardcoding IPs, especially if you move between networks.
- **Consider a `docker-compose.yml`:** Even though Redis is on a separate machine, using Docker Compose on the Linux host to manage Redis (and potentially other services) makes it easier to configure and restart.

If you run into connection issues, the most common culprits are the firewall on the Linux machine or using `localhost` in your Spring Boot configuration. Double-check both.
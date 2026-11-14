# Lesson 1: Your First Spring Boot Application — "Hello World"

Welcome to Lesson 1. By the end of this lesson, you will have a running Spring Boot web application that responds to HTTP requests. We will go slowly and explain every step.

---

## 🎯 What You Will Learn in Lesson 1

1. How to set up your development environment.
2. How to create a Spring Boot project from scratch.
3. Understand the basic project structure.
4. Write your first REST endpoint.
5. Run and test your application.

---

## 🛠️ Part 1: Environment Setup (Do This Once)

### 1.1 Install Java (JDK 17 or Higher)

Spring Boot 3 requires **Java 17** minimum. If you don't have it:

1. Go to [https://adoptium.net](https://adoptium.net) (Eclipse Temurin).
2. Download the **JDK 17** (or 21) installer for your operating system.
3. Install it using the default options.
4. Verify installation by opening a terminal/command prompt and running:

```bash
java -version
```

You should see something like:

```
openjdk version "17.0.10" 2024-01-16
OpenJDK Runtime Environment Temurin-17.0.10+7
```

If you see a version number, you are good.

---

### 1.2 Install an IDE (Integrated Development Environment)

We recommend **IntelliJ IDEA Community Edition** (free) because it has excellent Spring Boot support.

1. Download from [https://www.jetbrains.com/idea/download](https://www.jetbrains.com/idea/download).
2. Choose the **Community** version (free).
3. Install with default settings.

> If you prefer VS Code, install the **Spring Boot Extension Pack** from the marketplace.

---

### 1.3 (Optional) Install a Build Tool

Spring Boot projects use either **Maven** or **Gradle**. IntelliJ bundles Maven, but for command-line use you can install Maven:

- Download from [https://maven.apache.org/download.cgi](https://maven.apache.org/download.cgi)
- Verify with `mvn -v`

> You do **not** need this if you use the IDE's built-in Maven. We'll use Maven in this lesson.

---

## 🚀 Part 2: Create Your Spring Boot Project

### 2.1 Use Spring Initializr (Recommended)

Spring Initializr is the official project generator.

1. Open your browser and go to **[https://start.spring.io](https://start.spring.io)**.
2. Fill in the form exactly as follows:

| Field | Value |
|-------|-------|
| Project | **Maven** |
| Language | **Java** |
| Spring Boot | **3.3.x** (latest stable 3.x) |
| Group | `com.example` |
| Artifact | `hello-spring` |
| Name | `hello-spring` |
| Description | Demo project for Spring Boot |
| Package name | `com.example.hellospring` |
| Packaging | **Jar** |
| Java | **17** (or 21 if installed) |

3. In the **Dependencies** section on the right, click **ADD DEPENDENCIES** and select:
    - **Spring Web**

   This is the only dependency you need for Lesson 1. It brings in everything to build a web server.

4. Click the **GENERATE** button. A `.zip` file will download.

5. Unzip the file to a folder where you keep your projects (e.g., `~/projects/hello-spring`).

---

### 2.2 Open the Project in IntelliJ IDEA

1. Open IntelliJ IDEA.
2. Click **Open** (or **File → Open**).
3. Navigate to the unzipped `hello-spring` folder and select it. Click **OK**.
4. IntelliJ will detect it's a Maven project and automatically download dependencies. Wait for the progress bar at the bottom to finish (it may take a minute the first time).

---

## 📁 Part 3: Understand the Project Structure

After opening, you will see a structure like this:

```
hello-spring
├── pom.xml
└── src
    ├── main
    │   ├── java
    │   │   └── com/example/hellospring
    │   │       └── HelloSpringApplication.java
    │   └── resources
    │       └── application.properties
    └── test
        └── java
            └── com/example/hellospring
                └── HelloSpringApplicationTests.java
```

### What Each File Does

| File/Folder | Purpose |
|-------------|---------|
| `pom.xml` | Maven configuration file. Lists dependencies (like Spring Web). |
| `HelloSpringApplication.java` | The **main class**. Contains the `main()` method that starts the application. Annotated with `@SpringBootApplication`. |
| `application.properties` | Configuration file. Empty for now. |
| `HelloSpringApplicationTests.java` | A placeholder test. We'll ignore it for now. |

### Look at the Main Class

Open `src/main/java/com/example/hellospring/HelloSpringApplication.java`. It should look like this:

```java
package com.example.hellospring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class HelloSpringApplication {

    public static void main(String[] args) {
        SpringApplication.run(HelloSpringApplication.class, args);
    }

}
```

**Explanation:**

- `@SpringBootApplication` is a convenience annotation that combines three annotations:
    - `@Configuration` — marks the class as a source of bean definitions.
    - `@EnableAutoConfiguration` — tells Spring Boot to automatically configure the application based on dependencies.
    - `@ComponentScan` — tells Spring to scan the current package and sub-packages for components (controllers, services, etc.).
- `SpringApplication.run(...)` boots up the entire Spring application, including the embedded web server (Tomcat).

---

## ✍️ Part 4: Write Your First REST Endpoint

Now we'll create a controller that responds to an HTTP GET request.

### 4.1 Create a Controller Class

1. In IntelliJ, right-click on the package `com.example.hellospring`.
2. Select **New → Java Class**.
3. Name it `HelloController` and press Enter.

### 4.2 Write the Code

Replace the contents of `HelloController.java` with:

```java
package com.example.hellospring;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/hello")
    public String sayHello() {
        return "Hello, Spring Boot!";
    }
}
```

**Explanation:**

- `@RestController` — tells Spring that this class handles HTTP requests and returns the response body directly (not a view/template).
- `@GetMapping("/hello")` — maps HTTP GET requests to the `/hello` URL to the `sayHello()` method.
- The method returns a `String`, which Spring will send as the HTTP response body with status `200 OK`.

---

## ▶️ Part 5: Run the Application

### 5.1 Run from IntelliJ

1. Open `HelloSpringApplication.java`.
2. Click the **green play button** next to the `main` method (or right-click the file and choose **Run 'HelloSpringApplication.main()'**).
3. Wait for the console to show:

```
Started HelloSpringApplication in 2.345 seconds (process running for 2.678)
Tomcat started on port(s): 8080 (http)
```

> If you see these messages, your application is running!

### 5.2 Test in the Browser

1. Open your web browser.
2. Go to: [http://localhost:8080/hello](http://localhost:8080/hello)
3. You should see:

```
Hello, Spring Boot!
```

**🎉 Congratulations! You have built and run your first Spring Boot application.**

### 5.3 Test with curl (Optional)

In a terminal, run:

```bash
curl http://localhost:8080/hello
```

You should see:

```
Hello, Spring Boot!
```

---

## 🧪 Part 6: Understand What Just Happened (The Flow)

Here is the sequence of events when you visited `http://localhost:8080/hello`:

1. **The embedded Tomcat server** (included by Spring Web) received the HTTP GET request on port 8080.
2. **Spring DispatcherServlet** (the front controller) intercepted the request.
3. It looked for a handler method mapped to `/hello`. It found `HelloController.sayHello()`.
4. It invoked the method.
5. The returned String `"Hello, Spring Boot!"` was written to the HTTP response body.
6. The browser displayed the text.

This is the core of Spring MVC — you will build on this pattern throughout the course.

---

## ✅ Lesson 1 Summary Checklist

Before moving on, verify you can answer "yes" to all of these:

- [ ] Java 17+ is installed and `java -version` works.
- [ ] IntelliJ IDEA (or another IDE) is installed.
- [ ] You created a Spring Boot project using Spring Initializr with the **Spring Web** dependency.
- [ ] You understand the purpose of `@SpringBootApplication`.
- [ ] You created a `HelloController` with `@RestController` and `@GetMapping`.
- [ ] You ran the application and saw `Hello, Spring Boot!` in the browser at `http://localhost:8080/hello`.

---

## 🧹 How to Stop the Application

- In IntelliJ: click the **red square** (Stop button) in the Run panel.
- Or press `Ctrl + C` (Windows/Linux) / `Cmd + C` (Mac) in the terminal if you ran from the command line.

---

## 🔜 What's Next (Lesson 2 Preview)

In Lesson 2, you will:

- Learn about **Dependency Injection** (the heart of Spring).
- Build a REST API that returns **JSON** (not just a String).
- Create a `Product` class and return a list of products as JSON.

---

## 📚 Optional Homework (Highly Recommended)

1. Change the greeting message to your name: `"Hello, [YourName]!"`.
2. Add a second endpoint: `@GetMapping("/goodbye")` returning `"Goodbye, Spring Boot!"`. Test it in the browser.
3. Experiment: What happens if you visit `http://localhost:8080/` (without `/hello`)? You should get a **Whitelabel Error Page** (404). This is Spring's default error page. Don't worry — we'll customize it later.

---

**Ready for Lesson 2?** Just say **"start lesson 2"** and we will build a JSON REST API with proper layered architecture.
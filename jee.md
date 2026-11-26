

<!-- SOURCE: 0.md -->
# File: 0.md

Learning Enterprise JavaBeans (EJB) is a structured journey, especially valuable if you're working with or targeting large-scale enterprise systems. The technology has evolved significantly; modern EJB (version 3.x and later) uses annotations, making it far simpler than its notoriously complex past versions.

Here is a step-by-step guide to get you started.

### 📚 Phase 1: Build Your Foundation

Before diving into EJB, ensure you're comfortable with these core Java and enterprise concepts. Most courses expect you to have solid Java SE experience.

*   **Core Java**: Strong grasp of **Object-Oriented Programming (OOP)**, **Annotations**, **Interfaces**, and **Generics**.
*   **Web & Database Basics**: Familiarity with **Servlets**, **JDBC** (Java Database Connectivity), and **SQL** is assumed.
*   **Build Tools**: Experience with **Maven** is highly recommended, as it's the standard for managing dependencies in modern Java projects.

### 🧠 Phase 2: Understand the Core Concepts

EJB is a specification for building **distributed, transactional, and secure** server-side Java applications. The application server (like WildFly/JBoss, GlassFish, or WebLogic) manages the lifecycle and provides these services to your beans.

The modern EJB 3.x specification defines three main types of beans:

1.  **Session Beans**: Implement business logic. They are the most common type and come in three flavors:
    *   **Stateless**: Do not maintain state between method calls. Any instance can serve any client, making them efficient and scalable.
    *   **Stateful**: Maintain state specific to a single client across multiple method invocations, useful for managing workflows.
    *   **Singleton**: Guarantees exactly one instance exists per application, often used for shared configuration or caches.
2.  **Message-Driven Beans (MDBs)**: Enable **asynchronous** processing. They listen for messages from a JMS (Java Message Service) Queue or Topic and process them independently, allowing for loosely coupled systems.
3.  **Entity Beans**: Historically used for persistence. **In modern Java EE/Jakarta EE, entity beans are replaced by the Java Persistence API (JPA)**. Note that many modern application servers (like JBoss EAP) no longer support the old EJB 2.1 entity beans.

### ⚙️ Phase 3: Set Up Your Environment

To practice, you'll need two key components:

1.  **A Java IDE**: Use **Eclipse** or **IntelliJ IDEA**.
2.  **A Java EE / Jakarta EE Application Server**: You need a runtime environment to deploy your EJBs. Popular choices include:
    *   **WildFly (formerly JBoss)**: Open-source and widely used.
    *   **GlassFish**: The reference implementation for Jakarta EE.
    *   **Apache TomEE**: A lightweight server built on Tomcat.

**Action**: Download and install one of these servers. Most IDEs have built-in integrations to manage the server easily. Also, set up a new **Maven project** in your IDE to manage dependencies.

### 👨‍💻 Phase 4: Your First EJB — "Hello World" with a Stateless Bean

The best way to learn is by doing. The classic "Hello World" for EJB is a simple stateless session bean.

**Step 1: Create the Business Interface**
This interface defines what your bean can do. For a local bean (client and bean in same JVM), you can optionally use the `@Local` annotation.

```java
import jakarta.ejb.Local;

@Local
public interface Greeting {
    String sayHello(String name);
}
```

**Step 2: Implement the Bean**
Create a class that implements the interface and annotate it with `@Stateless` to tell the container it's a stateless session bean.

```java
import jakarta.ejb.Stateless;

@Stateless
public class GreetingBean implements Greeting {
    @Override
    public String sayHello(String name) {
        return "Hello, " + name + "!";
    }
}
```

**Step 3: Create a Client to Test It**
You can test your EJB by injecting it into another managed component, like a Servlet. The `@EJB` annotation handles the lookup and injection for you.

```java
import jakarta.ejb.EJB;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/greet")
public class GreetingServlet extends HttpServlet {
    @EJB
    private Greeting greetingBean; // Injection!

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String message = greetingBean.sayHello("EJB Learner");
        resp.getWriter().println(message);
    }
}
```

**Step 4: Deploy and Run**
Package your application (e.g., as a WAR or EAR file) and deploy it to your application server. Then, access the servlet's URL (e.g., `http://localhost:8080/your-app/greet`) to see the output: `Hello, EJB Learner!`.

### 🚀 Phase 5: Explore Advanced Topics

Once you have a "Hello World" working, you can systematically explore more powerful features.

*   **Stateful & Singleton Beans**: Practice creating a stateful bean to track a user's session or a singleton bean to hold application-wide counters.
*   **Message-Driven Beans (MDBs)**: Learn the basics of **JMS** (Queues and Topics) and create an MDB that processes messages asynchronously.
*   **Transaction Management**: EJBs shine in transaction handling. Learn about **Container-Managed Transactions (CMT)**, where the container automatically handles transaction boundaries using annotations like `@TransactionAttribute`.
*   **Dependency Injection & Interceptors**: Explore how `@EJB` and `@Resource` work, and use **Interceptors** to add cross-cutting concerns (like logging) to your beans without modifying their core logic.
*   **Timer Service & Asynchronous Calls**: Learn to schedule tasks using the EJB Timer Service and invoke bean methods asynchronously using `@Asynchronous`.

### 💡 Modern Context & Next Steps

It's important to understand EJB's place in today's ecosystem.

*   **EJB vs. Spring**: For **new** projects, most teams today prefer **Spring Boot** or frameworks like **Quarkus/Micronaut** for building microservices, as they offer similar capabilities with less boilerplate. Learning EJB is primarily valuable for **maintaining existing enterprise applications** or working in organizations with a significant investment in the Java EE/Jakarta EE stack.
*   **Jakarta EE**: EJB is a core part of the **Jakarta EE** platform (the successor to Java EE). The annotations have moved from `javax.ejb.*` to `jakarta.ejb.*`, which you'll see in modern tutorials.

**Your Next Step**: After mastering the "Hello World," pick one topic from Phase 5 (like MDBs or Transactions) and find a specific tutorial to build a small project around it. The official Jakarta EE tutorial and the quickstarts for WildFly are excellent, practical resources for this.

If you get stuck on a specific concept or want to explore a particular bean type in more detail, feel free to ask.



<!-- SOURCE: 1.md -->
# File: 1.md

Stepping into the "Next Step" after your first "Hello World" is all about moving from a simple stateless bean to the features that make EJB powerful for enterprise applications: **asynchronous messaging, transaction management, scheduling, and cross-cutting concerns**.

The best way to learn is to pick **one** of these topics and build a small, focused project around it. Here are four concrete paths you can take, each with a recommended project and a link to a working example.

### 🚀 Choose Your Next Project

| Topic | What You'll Build | Core EJB Feature to Learn |
| :--- | :--- | :--- |
| **Messaging** | A "Hello World" for Message-Driven Beans (MDBs). | Asynchronous processing with JMS. |
| **Transactions** | A scenario that updates a database and sends a JMS message in one transaction. | Container-Managed Transactions (CMT) and `@TransactionAttribute`. |
| **Scheduling** | A bean that automatically runs a task on a timer. | The EJB Timer Service and `@Schedule`. |
| **Cross-Cutting Logic** | An "audit" logger that runs before/after your bean's methods without changing the bean's code. | Interceptors and `@AroundInvoke`. |

---

### 📨 Project 1: Messaging with a Message-Driven Bean (MDB)

This project demonstrates how to process messages asynchronously, decoupling the message sender from the processor.

*   **What to build**: Create an MDB that listens to a JMS Queue. Then, create a simple servlet or session bean that sends a text message to that queue. When you send a message, the MDB should pick it up and print the content to the server console.
*   **Key concepts**: `@MessageDriven`, `@ActivationConfigProperty` (to specify the destination queue), and the `onMessage` method.
*   **Reference Example**: The **`helloworld-mdb`** quickstart from WildFly is a perfect, complete example. It creates a JMS Queue and Topic and demonstrates an MDB listening to them. You can find the code in the WildFly quickstart repository.

### 💳 Project 2: Transaction Management (CMT)

This project shows how EJB ensures data consistency by treating multiple operations as a single, atomic transaction.

*   **What to build**: Create a scenario where a method needs to perform two actions: insert a record into a database using JPA, and then send a JMS message. You want both actions to succeed or fail together. Annotate the method with `@TransactionAttribute(TransactionAttributeType.REQUIRED)`.
*   **Key concepts**: The different transaction attributes: `REQUIRED`, `REQUIRES_NEW`, `MANDATORY`, `NOT_SUPPORTED`, `SUPPORTS`, and `NEVER`.
*   **Reference Example**: The **`cmt`** quickstart from WildFly explicitly demonstrates updating a database and sending a JMS message in the same transaction. It's an excellent deep dive into how CMT works in practice.

### ⏰ Project 3: The EJB Timer Service

This project introduces you to creating scheduled, automatic tasks within your application.

*   **What to build**: Create a Singleton session bean with a method annotated with `@Schedule`. Configure it to run every few seconds or minutes. Inside the method, have it print a timestamp or perform a simple calculation to prove it's running automatically.
*   **Key concepts**: `@Schedule` (for calendar-based timers) and `@Timeout` (for programmatic timers). You can also explore `TimerService` for more dynamic scheduling.
*   **Reference Example**: The **`ejb-timer`** quickstart on GitHub demonstrates using `@Schedule` to print a message to the server console every 6 seconds. This is a very focused way to understand the timer mechanism.

### 🕵️ Project 4: Interceptors for Cross-Cutting Concerns

This project teaches you how to add logic (like logging or security checks) that applies across multiple business methods without cluttering the business logic itself.

*   **What to build**: Create an Interceptor class with a method annotated with `@AroundInvoke`. This method should log a message *before* and *after* the target business method is invoked. Then, apply this interceptor to your existing `GreetingBean` from the "Hello World" project.
*   **Key concepts**: The `InvocationContext` object, which allows you to access method parameters and call `ctx.proceed()` to continue the invocation chain.
*   **Reference Example**: Many tutorials provide snippets for this. The Oracle documentation shows a security-focused interceptor that checks the caller's principal before allowing a method to proceed.

---

### 💡 A More Comprehensive Practice Idea

If you're feeling confident and want a slightly larger challenge that combines several concepts, consider building a simple **Temperature Converter** or a basic **Banking Application**. The official Jakarta EE tutorial has a "Getting Started" guide that walks you through building a **`converter`** application (a stateless bean for currency conversion) with a web client. This shows how EJB integrates with the web tier.

Once you pick a project and start building, you'll likely encounter specific questions. Feel free to ask for clarification on any of these topics or code examples.



<!-- SOURCE: 1.md -->
# File: 1.md

Building a multi-module EJB project for WebLogic with an Oracle database is an excellent way to learn, as it mirrors how real enterprise applications are structured. Your instinct to use `pom` packaging for a parent module is exactly the right approach for organizing this.

Here is a step-by-step guide to create a **Core Banking learning project** called `core-banking-ejb`.

### 🏗️ The Multi-Module Architecture

We will create a parent project and four child modules. This structure separates concerns and follows the standard Java EE packaging model.

| Module | Packaging | Purpose |
| :--- | :--- | :--- |
| **`core-banking-ejb` (Parent)** | `pom` | Manages dependencies and builds all child modules. |
| **`banking-domain`** | `jar` | Contains JPA Entities (Customer, Account, Transaction) and business interfaces. |
| **`banking-services`** | `ejb` | Contains the Session Beans (the core business logic). |
| **`banking-web`** | `war` | A simple web client (Servlet or REST) to invoke the EJBs. |
| **`banking-ear`** | `ear` | Assembles the EJB and WAR modules into a single deployable EAR file for WebLogic. |

---

### Step 1: Create the Parent `pom.xml`

Create the root project directory `core-banking-ejb` and a `pom.xml` file with `packaging` set to `pom`. This file defines the modules and centralizes dependency versions.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.learning.ejb</groupId>
    <artifactId>core-banking-ejb</artifactId>
    <version>1.0-SNAPSHOT</version>
    <packaging>pom</packaging>

    <modules>
        <module>banking-domain</module>
        <module>banking-services</module>
        <module>banking-web</module>
        <module>banking-ear</module>
    </modules>

    <properties>
        <maven.compiler.source>11</maven.compiler.source>
        <maven.compiler.target>11</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <!-- Use versions compatible with your WebLogic Server -->
        <javaee.api.version>8.0</javaee.api.version>
        <oracle.jdbc.version>19.18.0.0</oracle.jdbc.version>
        <!-- WebLogic Maven Plugin Version (adjust to your WLS version) -->
        <weblogic.maven.plugin.version>12.2.1-0-0</weblogic.maven.plugin.version>
    </properties>

    <dependencyManagement>
        <dependencies>
            <!-- Java EE API (EJB, JPA, Servlet) -->
            <dependency>
                <groupId>javax</groupId>
                <artifactId>javaee-api</artifactId>
                <version>${javaee.api.version}</version>
                <scope>provided</scope>
            </dependency>
            <!-- Oracle JDBC Driver -->
            <dependency>
                <groupId>com.oracle.database.jdbc</groupId>
                <artifactId>ojdbc8-production</artifactId>
                <version>${oracle.jdbc.version}</version>
                <type>pom</type>
            </dependency>
        </dependencies>
    </dependencyManagement>
</project>
```
> **Note**: We use the `javax` namespace (`javaee-api`) because WebLogic 12c/14c primarily supports Java EE 8. If you are using a newer WebLogic version that supports Jakarta EE 9+, you would switch to `jakarta.jakartaee-api`. The Oracle JDBC driver is available on Maven Central.

### Step 2: Create the `banking-domain` Module (JPA Entities)

This module holds the persistent data model. In a core banking context, the central entities are **Customer**, **Account**, and **Transaction**.

**1. Create the module directory and its `pom.xml`:**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0">
    <parent>
        <artifactId>core-banking-ejb</artifactId>
        <groupId>com.learning.ejb</groupId>
        <version>1.0-SNAPSHOT</version>
    </parent>
    <modelVersion>4.0.0</modelVersion>
    <artifactId>banking-domain</artifactId>
    <packaging>jar</packaging>

    <dependencies>
        <dependency>
            <groupId>javax</groupId>
            <artifactId>javaee-api</artifactId>
        </dependency>
    </dependencies>
</project>
```

**2. Create the JPA Entities.** For example, a simplified `Account` entity:

```java
package com.learning.ejb.domain;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "ACCOUNTS")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "account_seq")
    @SequenceGenerator(name = "account_seq", sequenceName = "ACCOUNT_SEQ")
    private Long id;
    private String accountNumber;
    private BigDecimal balance;
    // Getters and setters...
}
```
You would also create `Customer` and `Transaction` entities here.

### Step 3: Create the `banking-services` Module (Session Beans)

This module contains the EJBs that implement the business logic, such as transferring funds between accounts. It depends on the `banking-domain` module.

**1. Create the module directory and `pom.xml`:**

```xml
<project ...>
    <parent>... (same as before) ...</parent>
    <artifactId>banking-services</artifactId>
    <packaging>ejb</packaging>

    <dependencies>
        <dependency>
            <groupId>com.learning.ejb</groupId>
            <artifactId>banking-domain</artifactId>
            <version>${project.version}</version>
        </dependency>
        <dependency>
            <groupId>javax</groupId>
            <artifactId>javaee-api</artifactId>
        </dependency>
    </dependencies>
    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-ejb-plugin</artifactId>
                <version>3.1.0</version>
                <configuration>
                    <ejbVersion>3.2</ejbVersion>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

**2. Create a Stateless Session Bean.** This bean uses `EntityManager` to perform database operations within a container-managed transaction.

```java
package com.learning.ejb.services;

import com.learning.ejb.domain.Account;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;

@Stateless
public class AccountServiceBean {

    @PersistenceContext(unitName = "bankingPU")
    private EntityManager em;

    public void transferFunds(Long fromAccountId, Long toAccountId, BigDecimal amount) {
        Account from = em.find(Account.class, fromAccountId);
        Account to = em.find(Account.class, toAccountId);
        from.setBalance(from.getBalance().subtract(amount));
        to.setBalance(to.getBalance().add(amount));
        // Changes are automatically flushed to the DB on transaction commit
    }
}
```

### Step 4: Create the `banking-web` Module (Web Client)

This module provides a simple way to trigger your EJBs, for example, via a Servlet. It depends on the `banking-services` module (or just the domain module if it only needs the interfaces).

**1. Create the module directory and `pom.xml`:**

```xml
<project ...>
    <parent>... (same as before) ...</parent>
    <artifactId>banking-web</artifactId>
    <packaging>war</packaging>

    <dependencies>
        <dependency>
            <groupId>com.learning.ejb</groupId>
            <artifactId>banking-services</artifactId>
            <version>${project.version}</version>
            <scope>provided</scope> <!-- Will be provided by the EJB module in the EAR -->
        </dependency>
        <dependency>
            <groupId>javax</groupId>
            <artifactId>javaee-api</artifactId>
            <scope>provided</scope>
        </dependency>
    </dependencies>
    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-war-plugin</artifactId>
                <version>3.3.2</version>
                <configuration>
                    <failOnMissingWebXml>false</failOnMissingWebXml>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

**2. Create a Servlet that injects and invokes the EJB:**

```java
package com.learning.ejb.web;

import com.learning.ejb.services.AccountServiceBean;
import javax.ejb.EJB;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.math.BigDecimal;

@WebServlet("/transfer")
public class TransferServlet extends HttpServlet {
    @EJB
    private AccountServiceBean accountService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // In a real app, get these from request parameters
        accountService.transferFunds(1L, 2L, new BigDecimal("100.00"));
        resp.getWriter().println("Transfer completed successfully!");
    }
}
```

### Step 5: Create the `banking-ear` Module (Assembly)

This module packages the EJB and WAR into an EAR file for deployment.

**1. Create the module directory and `pom.xml`:**

```xml
<project ...>
    <parent>... (same as before) ...</parent>
    <artifactId>banking-ear</artifactId>
    <packaging>ear</packaging>

    <dependencies>
        <dependency>
            <groupId>com.learning.ejb</groupId>
            <artifactId>banking-services</artifactId>
            <version>${project.version}</version>
            <type>ejb</type>
        </dependency>
        <dependency>
            <groupId>com.learning.ejb</groupId>
            <artifactId>banking-web</artifactId>
            <version>${project.version}</version>
            <type>war</type>
        </dependency>
    </dependencies>
    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-ear-plugin</artifactId>
                <version>3.2.0</version>
                <configuration>
                    <version>7</version>
                    <defaultLibBundleDir>lib</defaultLibBundleDir>
                    <modules>
                        <ejbModule>
                            <groupId>com.learning.ejb</groupId>
                            <artifactId>banking-services</artifactId>
                        </ejbModule>
                        <webModule>
                            <groupId>com.learning.ejb</groupId>
                            <artifactId>banking-web</artifactId>
                            <contextRoot>/banking</contextRoot>
                        </webModule>
                    </modules>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

### Step 6: Configure WebLogic and Oracle Database

**1. Database Setup:**
Create an Oracle database user/schema (e.g., `BANKING`) and a sequence `ACCOUNT_SEQ` (as defined in the `Account` entity).

**2. Create a `persistence.xml` file** in `banking-services/src/main/resources/META-INF/`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<persistence version="2.2"
             xmlns="http://xmlns.jcp.org/xml/ns/persistence"
             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/persistence http://xmlns.jcp.org/xml/ns/persistence/persistence_2_2.xsd">
    <persistence-unit name="bankingPU" transaction-type="JTA">
        <jta-data-source>jdbc/BankingDS</jta-data-source>
        <properties>
            <property name="hibernate.dialect" value="org.hibernate.dialect.Oracle12cDialect"/>
            <property name="hibernate.hbm2ddl.auto" value="update"/> <!-- Use 'validate' in production -->
        </properties>
    </persistence-unit>
</persistence>
```
> **Note**: The `jta-data-source` (`jdbc/BankingDS`) must be created in the WebLogic Administration Console as a **JDBC Data Source** pointing to your Oracle database.

### Step 7: Build and Deploy

**1. Build the project** from the root `core-banking-ejb` directory:
```bash
mvn clean install
```
This will compile all modules and produce the EAR file in `banking-ear/target/`.

**2. Deploy to WebLogic.** You have two main options:

*   **Manual Deployment**: Log in to the WebLogic Admin Console, go to **Deployments**, and upload the generated `banking-ear-1.0-SNAPSHOT.ear` file.
*   **Using the WebLogic Maven Plugin (Recommended)**: Add the following plugin configuration to the `banking-ear/pom.xml`. This allows you to deploy directly from Maven using `mvn pre-integration-test`.

```xml
<plugin>
    <groupId>com.oracle.weblogic</groupId>
    <artifactId>weblogic-maven-plugin</artifactId>
    <version>${weblogic.maven.plugin.version}</version>
    <configuration>
        <middlewareHome>/path/to/your/wls</middlewareHome> <!-- Adjust this path -->
    </configuration>
    <executions>
        <execution>
            <id>wls-deploy</id>
            <phase>pre-integration-test</phase>
            <goals>
                <goal>deploy</goal>
            </goals>
            <configuration>
                <adminurl>t3://localhost:7001</adminurl>
                <user>weblogic</user>
                <password>your_password</password>
                <source>${project.build.directory}/${project.build.finalName}.${project.packaging}</source>
                <targets>AdminServer</targets>
                <name>${project.build.finalName}</name>
            </configuration>
        </execution>
    </executions>
</plugin>
```

### 🚀 What to Learn Next

Once the basic flow is working, you can enhance the project to explore more advanced EJB features:

*   **Add Messaging**: Create a `banking-mdb` module and use a **Message-Driven Bean** to process audit logs or notifications asynchronously.
*   **Add Scheduling**: Use the `@Schedule` annotation on a Singleton bean in `banking-services` to run a daily interest calculation or a system health check.
*   **Add Interceptors**: Create an interceptor to log every call to `transferFunds` for auditing purposes, without modifying the `AccountServiceBean` code.

This modular structure gives you a solid foundation to incrementally add and learn these powerful enterprise features.



<!-- SOURCE: 1.md -->
# File: 1.md

Here is a comprehensive, step-by-step guide to JAXB, with a special focus on how to declare and enforce mandatory vs. optional fields on both the client and server sides.

---

## 1. What is JAXB? (A Quick Refresher)

JAXB (Java Architecture for XML Binding) is a framework that maps Java objects to XML and vice-versa. You annotate Plain Old Java Objects (POJOs), and JAXB handles the conversion (marshalling/unmarshalling) without you writing manual XML parsing code.

**Key components:**
- `JAXBContext`: The entry point that knows which classes to manage.
- `Marshaller`: Converts a Java object → XML.
- `Unmarshaller`: Converts XML → Java object.

---

## 2. Setting Up JAXB (Java 11+)

Since Java 11, JAXB is no longer bundled in the JDK. You must add the API and an implementation (e.g., EclipseLink MOXy or the JAXB RI) explicitly. For **Jakarta** (modern namespace):

```xml
<dependencies>
    <dependency>
        <groupId>jakarta.xml.bind</groupId>
        <artifactId>jakarta.xml.bind-api</artifactId>
        <version>4.0.0</version>
    </dependency>
    <dependency>
        <groupId>org.glassfish.jaxb</groupId>
        <artifactId>jaxb-runtime</artifactId>
        <version>4.0.0</version>
    </dependency>
</dependencies>
```

> If you are on an older `javax.xml.bind` codebase, use the corresponding `javax` artifacts. The annotation names remain the same, only the import packages change.

---

## 3. Core Annotations & Their Role in Mandatory/Optional Fields

The `@XmlElement` annotation is the primary tool for controlling whether a field is required or optional.

| Annotation | Purpose | Affects Mandatory/Optional? |
| :--- | :--- | :--- |
| `@XmlElement` | Maps a field to an XML element. Default mapping if no other annotation. | **Yes** (via `required` & `nillable`) |
| `@XmlAttribute` | Maps a field to an XML attribute. | Yes (via `required` implicitly) |
| `@XmlElement(required=true)` | Marks the element as mandatory (minOccurs=1). | **Yes** (schema-level) |
| `@XmlElement(nillable=true)` | Allows the element to be present but empty (`xsi:nil="true"`). | **Yes** (content-level) |
| `@XmlElement(required=true, nillable=true)` | The element **must** be present, but its value can be null. | **Yes** (both) |

---

## 4. Mandatory vs. Optional on the **Schema / WSDL** (Server-Generated Contract)

When you expose a JAX-WS web service, JAXB annotations on your request/response classes dictate the generated XSD (inside the WSDL). This is where `required` and `nillable` first take effect.

### 4.1. `required` → `minOccurs`
- **`@XmlElement(required = true)`** → The generated `<xs:element>` will have `minOccurs="1"` (or omit `minOccurs` because default is 1). The element **must appear** in the XML.
- **`@XmlElement(required = false)`** (default) → The generated element gets `minOccurs="0"`. The element **may be omitted**.

### 4.2. `nillable` → `nillable="true"`
- **`nillable = true`** → The element can be present with the `xsi:nil="true"` attribute (representing a null value).
- **`nillable = false`** (default) → The element cannot be empty/null if it appears.

**Example: Mapping Java to Schema**

```java
public class PurchaseOrder {
    @XmlElement(required = true, nillable = false)
    public String orderId;          // Must appear, cannot be null

    @XmlElement(required = true, nillable = true)
    public BigDecimal price;        // Must appear, but can be nil

    @XmlElement(required = false)   // Optional
    public String comments;        // May be omitted
}
```

Generated schema fragment (simplified):

```xml
<xs:complexType name="purchaseOrder">
    <xs:sequence>
        <xs:element name="orderId" type="xs:string" minOccurs="1" nillable="false"/>
        <xs:element name="price"   type="xs:decimal" minOccurs="1" nillable="true"/>
        <xs:element name="comments" type="xs:string" minOccurs="0"/>
    </xs:sequence>
</xs:complexType>
```

**Key takeaway:** `required` controls **presence** (`minOccurs`); `nillable` controls whether a present element can have a **null value**.

---

## 5. Mandatory vs. Optional on the **Client Side** (Consuming the Service)

When a client (Java, .NET, etc.) generates stubs from the WSDL/XSD, the `minOccurs` and `nillable` attributes are translated into language-specific patterns.

### 5.1. Behavior in Generated Java Clients
If the schema says `minOccurs="1"`:
- The generated Java field is **not** wrapped in a `JAXBElement`; it is a plain property.
- The client **must** set a value before marshalling; otherwise, the marshaller may throw an error or produce invalid XML (depending on validation settings).

If the schema says `minOccurs="0"`:
- The generated Java field is often wrapped in `JAXBElement<T>` (or accompanied by a `boolean isSetXxx()` flag) to distinguish between “not set” and “set to null”.
- The client is free to omit the field.

### 5.2. Behavior in Generated .NET Clients (Illustrative)
The Oracle WSIT tutorial explicitly shows how JAXB `nillable` and `required` map to .NET:

- **`nillable=true, required=true`** → .NET generates a `Nullable<T>` property **without** an accompanying `Specified` boolean. The element must be sent, but its value can be null.
- **`nillable=true, required=false`** → .NET generates a `Nullable<T>` property **plus** a `bool XxxSpecified` flag. The client can decide whether to include the element at all.

> This demonstrates that `required` and `nillable` are not Java-only concepts; they shape the contract for every client platform.

---

## 6. Mandatory vs. Optional on the **Server Side** (Receiving Requests)

Here is the most critical point for server-side robustness:

> **`@XmlElement(required = true)` does *not* automatically enforce validation at runtime in standard JAXB.** It primarily affects schema generation. Without explicit validation, the JAXB unmarshaller will happily create a Java object with `null` in a field marked `required=true`.

To enforce mandatory fields on the server, you must **activate schema validation**.

### 6.1. Enabling Validation in a JAX-WS Endpoint
If you are using **Apache CXF** (a common JAX-WS implementation), annotate your service class with `@SchemaValidation` (or enable it in configuration). This forces the runtime to validate incoming XML against the schema derived from your JAXB annotations.

```java
import org.apache.cxf.annotations.SchemaValidation;

@WebService
@SchemaValidation(enabled = true)
public class OrderService {
    public void submitOrder(@XmlElement(required = true) String orderId) {
        // If orderId is missing in the request, CXF will throw a SOAPFault
        // before this method is invoked.
    }
}
```

### 6.2. Programmatic Validation with `Unmarshaller.setSchema()`
If you are using JAXB directly (outside a WS container), you must set a `Schema` on the `Unmarshaller`. JAXB 2.0+ requires an explicit schema for validation.

```java
Schema schema = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
                             .newSchema(new File("order.xsd"));
Unmarshaller unmarshaller = context.createUnmarshaller();
unmarshaller.setSchema(schema);   // Now required fields are enforced
Object obj = unmarshaller.unmarshal(xmlInput);
```

### 6.3. Callback-Based Validation (`afterUnmarshal`)
A lightweight alternative (when full schema validation is overkill) is to implement `afterUnmarshal` in your JAXB class and manually check mandatory fields.

```java
public class Order {
    @XmlElement(required = true)
    private String orderId;

    private void afterUnmarshal(Unmarshaller u, Object parent) {
        if (orderId == null || orderId.isBlank()) {
            throw new IllegalArgumentException("orderId is mandatory");
        }
    }
}
```

---

## 7. Summary: Mandatory/Optional Matrix

| Intent | JAXB Declaration | Schema Effect (`minOccurs` / `nillable`) | Client Behavior | Server Enforcement |
| :--- | :--- | :--- | :--- | :--- |
| **Must be present, non-null** | `@XmlElement(required=true, nillable=false)` | `minOccurs=1`, `nillable=false` | Must set a value. | Requires schema validation. |
| **Must be present, may be nil** | `@XmlElement(required=true, nillable=true)` | `minOccurs=1`, `nillable=true` | Must include element; value can be null. | Requires schema validation. |
| **Optional, non-null** | `@XmlElement(required=false, nillable=false)` (default) | `minOccurs=0`, `nillable=false` | May omit entirely. | No validation needed for absence. |
| **Optional, may be nil** | `@XmlElement(required=false, nillable=true)` | `minOccurs=0`, `nillable=true` | May omit or send empty. | No validation needed for absence. |

---

## 8. Best Practices

1. **Do not rely on `required` alone for runtime safety.** Always pair it with schema validation on the server or use callbacks.
2. **Prefer `nillable=false` for mandatory primitive wrappers** (e.g., `Integer`, `BigDecimal`) to avoid ambiguous null vs. absent states.
3. **Use `@XmlElement(required=true)` on web method parameters** in JAX-WS to generate a contract that clearly signals mandatory input to all clients.
4. **Validate on both ends if possible.** The client can validate before sending (using the same schema), and the server should validate upon receipt to prevent malformed data from entering your domain.
5. **Be aware of the Jakarta vs. `javax` namespace migration.** The annotation semantics are identical, but imports and dependencies differ.

---

This guide gives you a complete path from basic JAXB setup through annotation-driven schema generation and, most importantly, the concrete mechanisms to enforce mandatory fields on both the client and server sides.



<!-- SOURCE: 2.md -->
# File: 2.md

To add a new field to a server-side DTO without forcing any changes on the client, the core principle is **strict backward compatibility**. The new field must be introduced in a way that an older client—unaware of its existence—can still function correctly when it receives a response containing this new data.

### 🔑 The Golden Rule: Make It Optional

The single most important step is to declare the new field as **optional** in your JAXB mapping. This ensures that the generated XML Schema marks the element with `minOccurs="0"`, meaning it is not required to be present.

You achieve this by setting `required = false` on the `@XmlElement` annotation. This is actually the default behavior, but being explicit is good practice.

```java
public class ServerResponseDTO {
    // Existing mandatory field
    @XmlElement(required = true)
    private String existingField;

    // NEW FIELD - Added on the server, made optional for backward compatibility
    @XmlElement(required = false) // or simply omit the required attribute
    private String newField;

    // Getters and setters...
}
```

By making `newField` optional, an older client that does not have this field defined in its generated stubs will not fail simply because the element is absent from the XML it expects. The server can include it when available, and the client is not obligated to process it.

### 🤔 What Happens on the Client Side?

How an old client reacts to the new, unexpected XML element depends on how its JAXB unmarshaller is configured:

1. **Clients Without Strict Validation (Default):** Most JAXB clients, by default, are lenient. If they encounter an XML element they do not recognize in the response, they will simply **ignore it**. The unmarshalling process will succeed, and the old client will continue to work as before, completely unaware of the new data. This is the most common scenario and the primary reason why adding optional elements is generally safe.

2. **Clients with Strict Schema Validation:** The risk arises if the client explicitly enables schema validation (`unmarshaller.setSchema(...)`) and validates against an **older, cached version** of the XSD. In this case, the validation will fail because the new element is not defined in the old schema, even if it is optional. However, this is a configuration choice on the client side. For a client you control, you can simply ensure its unmarshaller is not set to validate against a strict, outdated schema.

### 🛡️ Advanced Safeguard: Designing for Extensibility (If Needed)

If you need to guarantee forward compatibility even for clients that perform strict validation, you can design your schema to be "extensible" from the start. This is done by including an `<xs:any>` element at the end of your complex type. This acts as a wildcard that allows any additional elements to be present without breaking schema validation.

However, for the simple goal of adding one field without changing the client, the **`@XmlElement(required = false)` approach is the standard, minimal-impact solution**.

### 💎 Summary of the Solution

| Action | JAXB Annotation | Schema Effect | Client Impact |
| :--- | :--- | :--- | :--- |
| **Add a new, optional field** | `@XmlElement(required = false)` | `minOccurs="0"` | **Minimal.** Old clients will either ignore the new element (default) or can be configured to do so. |

In short, **always add new fields as optional (`minOccurs="0"`)**. This is the fundamental rule for maintaining backward compatibility in schema evolution. By following this, you can evolve your server-side DTOs independently, and clients will only need to be updated if and when they wish to start consuming the new data.



<!-- SOURCE: 1.md -->
# File: 1.md

<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">

# آموزش جامع JPA


---

## ۱. JPA چیه و کجای معماریه؟

**JPA (Java Persistence API)** یه **specification** (استاندارد) هست، نه یه فریمورک. پیاده‌سازی‌هاش:
- **Hibernate** (پرکاربردترین)
- EclipseLink
- OpenJPA

**نکته مهم:** خیلی‌ها JPA و Hibernate رو یکی می‌دونن. JPA فقط interface و annotation تعریف می‌کنه، Hibernate اون‌ها رو پیاده می‌کنه.

معماری لایه‌ای معمول:
</div>

```
Controller → Service → Repository (JPA) → DB
```
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">
---

## ۲. راه‌اندازی

### dependency (Maven)
</div>

```xml
<dependency>
    <groupId>org.hibernate.orm</groupId>
    <artifactId>hibernate-core</artifactId>
    <version>6.4.0.Final</version>
</dependency>
<dependency>
    <groupId>jakarta.persistence</groupId>
    <artifactId>jakarta.persistence-api</artifactId>
    <version>3.1.0</version>
</dependency>
```
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">
اگه Spring Boot داری، فقط `spring-boot-starter-data-jpa` کافیه.


### persistence.xml (بدون Spring)
</div>

```xml
<persistence xmlns="https://jakarta.ee/xml/ns/persistence" version="3.0">
    <persistence-unit name="myPU">
        <provider>org.hibernate.jpa.HibernatePersistenceProvider</provider>
        <class>com.example.User</class>
        <properties>
            <property name="jakarta.persistence.jdbc.url" value="jdbc:mysql://localhost:3306/mydb"/>
            <property name="jakarta.persistence.jdbc.user" value="root"/>
            <property name="jakarta.persistence.jdbc.password" value="pass"/>
            <property name="hibernate.show_sql" value="true"/>
            <property name="hibernate.hbm2ddl.auto" value="update"/>
        </properties>
    </persistence-unit>
</persistence>
```
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">
---

## ۳. Entity و Mapping پایه
</div>

```java
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Transient  // ذخیره نمیشه توی DB
    private String tempData;

    // getters/setters
}
```
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">

### نکات مهم mapping
- `@GeneratedValue` استراتژی‌ها:
    - `IDENTITY`: DB خودش می‌سازه (MySQL)
    - `SEQUENCE`: با sequence (Postgres/Oracle)
    - `TABLE`: جدول جدا برای شمارنده (کند)
    - `AUTO`: خود JPA انتخاب می‌کنه
- اگه `equals/hashCode` می‌نویسی، بر اساس `id` بزن (نه فیلدهای business).

---

## ۴. EntityManager و Lifecycle
</div>

```java
EntityManagerFactory emf = Persistence.createEntityManagerFactory("myPU");
EntityManager em = emf.createEntityManager();

// ذخیره
em.getTransaction().begin();
em.persist(user);       // INSERT
em.getTransaction().commit();

// پیدا کردن
User u = em.find(User.class, 1L);   // اگه نباشه null
User ref = em.getReference(User.class, 1L); // proxy بدون query فوری

// آپدیت (اتوماتیک توی transaction چون managed هست)
em.getTransaction().begin();
u.setFullName("Ali");
em.getTransaction().commit();   // UPDATE خودکار

// حذف
em.getTransaction().begin();
em.remove(u);
em.getTransaction().commit();

em.close();
```
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">

### حالت‌های Entity
- **New/Transient**: تازه ساخته شده، توی DB نیست
- **Managed**: تحت نظر EntityManager، تغییراتش ردیابی می‌شه
- **Detached**: قبلاً managed بوده، دیگه نیست
- **Removed**: برای حذف علامت خورده

**نکته طلایی:** `merge()` برای detached کاربردیه، ولی حواست باشه یه SELECT می‌زنه بعد UPDATE.

---

## ۵. Relationships

### OneToMany / ManyToOne (رایج‌ترین)
</div>

```java
@Entity
public class Department {
    @Id @GeneratedValue
    private Long id;

    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Employee> employees = new ArrayList<>();
}

@Entity
public class Employee {
    @Id @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;
}
```
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">
👉 **سمت `@ManyToOne` صاحب رابطه هست** (مالک foreign key). `mappedBy` سمت معکوسه.

### ManyToMany
</div>

```java
@ManyToMany
@JoinTable(
    name = "user_roles",
    joinColumns = @JoinColumn(name = "user_id"),
    inverseJoinColumns = @JoinColumn(name = "role_id")
)
private Set<Role> roles = new HashSet<>();
```
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">

### OneToOne
</div>


```java
@OneToOne(cascade = CascadeType.ALL)
@JoinColumn(name = "profile_id", unique = true)
private Profile profile;
```
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">

### Cascade types
- `ALL`, `PERSIST`, `MERGE`, `REMOVE`, `REFRESH`, `DETACH`
- `orphanRemoval = true`: وقتی از لیست حذف کنی، از DB هم پاک می‌شه

---

## ۶. JPQL — Query زبان JPA
</div>


```java
// ساده
List<User> users = em.createQuery("SELECT u FROM User u WHERE u.status = :s", User.class)
                     .setParameter("s", Status.ACTIVE)
                     .getResultList();

// تک نتیجه
User u = em.createQuery("SELECT u FROM User u WHERE u.id = :id", User.class)
           .setParameter("id", 1L)
           .getSingleResult();

// Joins
List<Object[]> rows = em.createQuery(
    "SELECT e.name, d.name FROM Employee e JOIN e.department d", Object[].class)
    .getResultList();

// Aggregate
Long count = em.createQuery("SELECT COUNT(u) FROM User u", Long.class).getSingleResult();
```
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">

**تفاوت JPQL و SQL:** JPQL روی **موجودیت‌ها** کار می‌کنه نه جداول. `User u` یعنی کلاس، نه جدول `users`.
</div>

### Named Queries
```java
@Entity
@NamedQuery(name = "User.findActive", query = "SELECT u FROM User u WHERE u.status = 'ACTIVE'")
public class User { ... }
```

---
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">

## ۷. Criteria API — 🎯

Criteria برای ساخت query به صورت **type-safe** و **داینامیک** استفاده می‌شه. وقتی شرط‌ها بر اساس ورودی کاربر عوض می‌شن، عالیه.

### ساختار پایه
</div>

```java
CriteriaBuilder cb = em.getCriteriaBuilder();
CriteriaQuery<User> cq = cb.createQuery(User.class);
Root<User> root = cq.from(User.class);

cq.select(root);
cq.where(cb.equal(root.get("status"), Status.ACTIVE));

List<User> result = em.createQuery(cq).getResultList();
```
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">


### انواع شرط‌ها
</div>


```java
// AND / OR
Predicate p1 = cb.equal(root.get("status"), Status.ACTIVE);
Predicate p2 = cb.greaterThan(root.get("age"), 18);
cq.where(cb.and(p1, p2));

// LIKE
cq.where(cb.like(root.get("name"), "%Ali%"));

// IN
cq.where(root.get("id").in(List.of(1L, 2L, 3L)));

// IS NULL
cq.where(cb.isNull(root.get("email")));

// BETWEEN
cq.where(cb.between(root.get("age"), 18, 65));
```
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">


### مثال داینامیک (کاربرد اصلی Criteria)
</div>


```java
public List<User> search(String name, Status status, Integer minAge) {
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<User> cq = cb.createQuery(User.class);
    Root<User> root = cq.from(User.class);

    List<Predicate> predicates = new ArrayList<>();

    if (name != null) predicates.add(cb.like(root.get("fullName"), "%" + name + "%"));
    if (status != null) predicates.add(cb.equal(root.get("status"), status));
    if (minAge != null) predicates.add(cb.greaterThanOrEqualTo(root.get("age"), minAge));

    cq.where(cb.and(predicates.toArray(new Predicate[0])));
    cq.orderBy(cb.desc(root.get("id")));

    return em.createQuery(cq).getResultList();
}
```
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">


### Join در Criteria
</div>


```java
Root<Employee> emp = cq.from(Employee.class);
Join<Employee, Department> dept = emp.join("department");
cq.where(cb.equal(dept.get("name"), "IT"));
```
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">


### Aggregate + Projection
</div>


```java
CriteriaQuery<Long> cq = cb.createQuery(Long.class);
Root<User> root = cq.from(User.class);
cq.select(cb.count(root));
```
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">


### Tuple برای چند فیلد
</div>


```java
CriteriaQuery<Tuple> cq = cb.createTupleQuery();
Root<User> root = cq.from(User.class);
cq.multiselect(root.get("id"), root.get("fullName"));
List<Tuple> rows = em.createQuery(cq).getResultList();
rows.forEach(t -> System.out.println(t.get(0) + " - " + t.get(1)));
```

<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">

### Metamodel (اختیاری ولی تمیز)

با annotation processor، کلاس‌های `User_` ساخته می‌شن:
</div>


```java
cq.where(cb.equal(root.get(User_.status), Status.ACTIVE));  // type-safe تر
```
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">

**کی Criteria و کی JPQL؟**
- Query ثابت و ساده → JPQL
- Query داینامیک با کلی شرط اختیاری → Criteria
- Query خیلی پیچیده یا خاص DB → Native SQL

---

## ۸. Fetch Strategies و مشکل N+1

### LAZY vs EAGER
- `@ManyToOne` و `@OneToOne` پیش‌فرض **EAGER**
- `@OneToMany` و `@ManyToMany` پیش‌فرض **LAZY**

**توصیه:** همه رو LAZY بذار، هرجا لازم شد با join fetch بیار.

### مشکل N+1
</div>


```java
List<Department> depts = em.createQuery("SELECT d FROM Department d", Department.class).getResultList();
for (Department d : depts) {
    d.getEmployees().size();  // 💥 یه query برای هر department!
}
```

### راه‌حل با JOIN FETCH
```java
List<Department> depts = em.createQuery(
    "SELECT DISTINCT d FROM Department d LEFT JOIN FETCH d.employees", Department.class)
    .getResultList();
```
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">


### EntityGraph
</div>


```java
EntityGraph graph = em.createEntityGraph(Department.class);
graph.addAttributeNodes("employees");

Map<String, Object> hints = Map.of("jakarta.persistence.fetchgraph", graph);
em.find(Department.class, 1L, hints);
```
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">

---

## ۹. Locking (قفل‌گذاری)

### Optimistic (پیشنهادی)
</div>


```java
@Version
private Long version;
```
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">

وقتی دو تراکنش همزمان آپدیت کنن، یکیشون `OptimisticLockException` می‌گیره.

### Pessimistic
</div>


```java
em.find(User.class, 1L, LockModeType.PESSIMISTIC_WRITE);
// یا
em.lock(user, LockModeType.PESSIMISTIC_WRITE);
```

---
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">
## ۱۰. Cache

### First-Level Cache (خودکار)
توی یه `EntityManager`، اگه دوبار `find` بزنی، query دوم نمی‌ره DB.

### Second-Level Cache (بین EntityManagerها)
</div>

```xml
<property name="hibernate.cache.use_second_level_cache" value="true"/>
<property name="hibernate.cache.region.factory_class" value="jcache"/>
```
```java
@Entity
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class User { ... }
```
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">
⚠️ فقط برای دیتای کم‌تغییر (مثل lookup tables) استفاده کن.



---

## ۱۱. Callbacks و Listeners
</div>

```java
@Entity
public class User {
    @PrePersist
    public void prePersist() {
        this.createdAt = new Date();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = new Date();
    }

    @PostLoad
    public void postLoad() { ... }
}
```

<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">

---

## ۱۲. Best Practices خلاصه

1. **EntityManager رو کوتاه‌مدت استفاده کن** (per request/transaction).
2. **همه relationships رو LAZY** بذار، با `JOIN FETCH` بارگذاری کن.
3. **`equals/hashCode` رو بر اساس id** بزن.
4. **`open-session-in-view` رو غیرفعال کن** توی production Spring Boot (کار بدی می‌کنه).
5. **N+1 رو جدی بگیر** — با logging فعالش کن و ببین.
6. **Criteria برای query داینامیک**، JPQL برای بقیه.
7. **DTO projection** بهتر از برگردوندن Entity خامه (هم performance هم امنیت).
8. **Batch operations** برای insert/update حجیم:

</div>


   ```xml
   <property name="hibernate.jdbc.batch_size" value="50"/>
   ```
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">

9. **از `@Data` لامباک روی Entity پرهیز کن** (مشکل با equals/hashCode و lazy).
10. **Migration tool** (Flyway/Liquibase) به‌جای `hbm2ddl.auto`.

---

## ۱۳. مسیر تمرین پیشنهادی

اگه می‌خوای جدی یاد بگیری، این ترتیب رو برو:

1. یه پروژه ساده با Spring Boot + H2 بساز.
2. سه تا entity با روابط ManyToOne/OneToMany بساز.
3. JPQL تمرین کن (select, join, aggregate, subquery).
4. **Criteria رو روی همون entityها** پیاده کن — شرط‌های داینامیک.
5. `hibernate.show_sql=true` رو فعال کن و N+1 رو ببین و fix کن.
6. EntityGraph و Projection رو تمرین کن.
7. Optimistic locking رو تست کن (دو thread همزمان).

---

اگه بخوای، می‌تونم:
- یه **مثال کامل پروژه** با همه این مفاهیم برات بنویسم
- **Criteria API رو با مثال‌های بیشتر** عمیق‌تر باز کنم
- تفاوت **JPA vs Spring Data JPA** رو جدا توضیح بدم
- روی یه مفهوم خاص (مثل N+1 یا Fetch) تمرکز کنم

کدوم بخش رو بیشتر می‌خوای؟
</div>



<!-- SOURCE: 2.md -->
# File: 2.md

<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">


# پروژه کامل JPA — سیستم وبلاگ

یه پروژه واقعی می‌سازیم: **سیستم وبلاگ** با User، Post، Comment، Tag، Profile. همه مفاهیم رو داخلش پیاده می‌کنم.

---

## ۱. ساختار پروژه
</div>


```
blog-app/
├── src/main/java/com/example/blog/
│   ├── BlogApplication.java
│   ├── entity/
│   │   ├── BaseEntity.java
│   │   ├── User.java
│   │   ├── Profile.java
│   │   ├── Post.java
│   │   ├── Comment.java
│   │   └── Tag.java
│   ├── dto/
│   │   ├── PostSummary.java
│   │   ├── PostDetail.java
│   │   └── PostSearchCriteria.java
│   ├── repository/
│   │   ├── UserRepository.java
│   │   ├── PostRepository.java
│   │   ├── PostRepositoryCustom.java
│   │   └── PostRepositoryImpl.java
│   ├── service/
│   │   ├── PostService.java
│   │   └── UserService.java
│   └── controller/
│       ├── PostController.java
│       └── UserController.java
├── src/main/resources/
│   ├── application.yml
│   └── data.sql
└── pom.xml
```
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">


---

## ۲. pom.xml
</div>


```xml
<project xmlns="http://maven.apache.org/POM/4.0.0">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.5</version>
    </parent>

    <groupId>com.example</groupId>
    <artifactId>blog-app</artifactId>
    <version>1.0.0</version>

    <properties>
        <java.version>17</java.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>
</project>
```
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">


> ⚠️ Lombok فقط برای DTO و Service. روی Entity از `@Data` استفاده **نمی‌کنیم** — فقط `@Getter/@Setter` جدا.

---

## ۳. application.yml
</div>


```yaml
spring:
  datasource:
    url: jdbc:h2:mem:blogdb;DB_CLOSE_DELAY=-1
    driver-class-name: org.h2.Driver
    username: sa
    password:
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: true
    properties:
      hibernate:
        format_sql: true
        jdbc:
          batch_size: 30
        order_inserts: true
        order_updates: true
        generate_statistics: true   # برای دیدن N+1
  h2:
    console:
      enabled: true
      path: /h2-console
  sql:
    init:
      mode: always

logging:
  level:
    org.hibernate.SQL: debug
    org.hibernate.orm.jdbc.bind: trace
```
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">


---

## ۴. Entityها

### BaseEntity (برای reuse فیلدهای مشترک)
</div>


```java
package com.example.blog.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@MappedSuperclass
@Getter @Setter
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Version
    private Long version;   // optimistic locking

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // equals/hashCode بر اساس id
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseEntity other = (BaseEntity) o;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
```
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">


### User
</div>


```java
package com.example.blog.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_user_username", columnList = "username", unique = true)
})
@Getter @Setter
public class User extends BaseEntity {

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "full_name", length = 100)
    private String fullName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserStatus status = UserStatus.ACTIVE;

    // OneToOne — سمت مالک
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Profile profile;

    // OneToMany — سمت معکوس
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts = new ArrayList<>();

    // ManyToMany — علاقه‌مندی‌ها
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "user_favorite_posts",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "post_id")
    )
    private Set<Post> favorites = new HashSet<>();

    // helper برای دوطرفه نگه‌داشتن
    public void addPost(Post post) {
        posts.add(post);
        post.setAuthor(this);
    }

    public void removePost(Post post) {
        posts.remove(post);
        post.setAuthor(null);
    }

    public enum UserStatus { ACTIVE, BANNED, PENDING }
}
```
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">


### Profile
</div>


```java
package com.example.blog.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "profiles")
@Getter @Setter
public class Profile extends BaseEntity {

    @Column(length = 500)
    private String bio;

    @Column(length = 200)
    private String website;

    @Column(name = "avatar_url")
    private String avatarUrl;

    // سمت مالک OneToOne — FK اینجاست
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;
}
```
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">


### Post
</div>


```java
package com.example.blog.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "posts", indexes = {
    @Index(name = "idx_post_slug", columnList = "slug", unique = true),
    @Index(name = "idx_post_status", columnList = "status")
})
@Getter @Setter
public class Post extends BaseEntity {

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, unique = true, length = 220)
    private String slug;

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PostStatus status = PostStatus.DRAFT;

    @Column(name = "view_count")
    private Long viewCount = 0L;

    // ManyToOne — سمت مالک
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    // OneToMany — سمت معکوس + BatchSize برای جلوگیری از N+1
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 20)
    private List<Comment> comments = new ArrayList<>();

    // ManyToMany — سمت مالک
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "post_tags",
        joinColumns = @JoinColumn(name = "post_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();

    public void addComment(Comment comment) {
        comments.add(comment);
        comment.setPost(this);
    }

    public void addTag(Tag tag) {
        tags.add(tag);
    }

    public enum PostStatus { DRAFT, PUBLISHED, ARCHIVED }
}
```
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">


### Comment
</div>


```java
package com.example.blog.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "comments")
@Getter @Setter
public class Comment extends BaseEntity {

    @Column(nullable = false, length = 1000)
    private String text;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private boolean approved = false;
}
```
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">


### Tag
</div>


```java
package com.example.blog.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tags")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Getter @Setter
public class Tag extends BaseEntity {

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @ManyToMany(mappedBy = "tags", fetch = FetchType.LAZY)
    private Set<Post> posts = new HashSet<>();
}
```
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">


---

## ۵. DTO و Projectionها

### PostSummary — projection برای لیست (بدون content سنگین)
</div>


```java
package com.example.blog.dto;

public record PostSummary(
    Long id,
    String title,
    String slug,
    String authorName,
    long commentCount,
    PostStatusView status
) {
    public enum PostStatusView { DRAFT, PUBLISHED, ARCHIVED }
}
```
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">


### PostDetail
</div>


```java
package com.example.blog.dto;

import java.time.LocalDateTime;
import java.util.List;

public record PostDetail(
    Long id,
    String title,
    String slug,
    String content,
    String authorName,
    String authorEmail,
    List<String> tags,
    List<CommentView> comments,
    LocalDateTime createdAt
) {
    public record CommentView(Long id, String text, String userName) {}
}
```
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">


### PostSearchCriteria (برای query داینامیک با Criteria)
</div>


```java
package com.example.blog.dto;

public record PostSearchCriteria(
    String titleLike,
    String authorUsername,
    PostStatusView status,
    String tagName,
    Long minViews,
    int page,
    int size,
    String sortBy,     // "createdAt", "viewCount"
    boolean desc
) {
    public enum PostStatusView { DRAFT, PUBLISHED, ARCHIVED }
}
```
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">


---

## ۶. Repositoryها

### UserRepository (Spring Data JPA)
</div>


```java
package com.example.blog.repository;

import com.example.blog.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    // با EntityGraph — profile رو fetch کن بدون N+1
    @EntityGraph(attributePaths = {"profile"})
    @Query("SELECT u FROM User u WHERE u.status = :status")
    List<User> findActiveWithProfile(@Param("status") User.UserStatus status);

    // پروجکشن مستقیم به DTO
    @Query("""
        SELECT new com.example.blog.dto.UserSummary(u.id, u.username, u.email)
        FROM User u WHERE u.status = 'ACTIVE'
    """)
    List<UserSummary> findAllActiveSummaries();

    // JPQL با join
    @Query("""
        SELECT u FROM User u
        JOIN u.posts p
        WHERE p.status = 'PUBLISHED'
        GROUP BY u
        HAVING COUNT(p) >= :minPosts
    """)
    List<User> findProlificAuthors(@Param("minPosts") long minPosts);

    boolean existsByEmail(String email);
}
```
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">


### PostRepository (Spring Data JPA)
</div>


```java
package com.example.blog.repository;

import com.example.blog.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom {

    Optional<Post> findBySlug(String slug);

    // JOIN FETCH برای جلوگیری از N+1
    @Query("""
        SELECT DISTINCT p FROM Post p
        JOIN FETCH p.author
        LEFT JOIN FETCH p.tags
        WHERE p.status = 'PUBLISHED'
    """)
    List<Post> findAllPublishedWithDetails(Pageable pageable);

    @Query("SELECT p FROM Post p JOIN FETCH p.author WHERE p.id = :id")
    Optional<Post> findByIdWithAuthor(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Post p SET p.viewCount = p.viewCount + 1 WHERE p.id = :id")
    int incrementViewCount(@Param("id") Long id);

    // Projection با interface
    @Query("SELECT p.id AS id, p.title AS title, p.slug AS slug, u.username AS authorName " +
           "FROM Post p JOIN p.author u WHERE p.status = 'PUBLISHED'")
    List<PostListView> findAllAsView();

    interface PostListView {
        Long getId();
        String getTitle();
        String getSlug();
        String getAuthorName();
    }

    // Projection با constructor expression
    @Query("""
        SELECT new com.example.blog.dto.PostSummary(
            p.id, p.title, p.slug, u.username,
            (SELECT COUNT(c) FROM Comment c WHERE c.post = p AND c.approved = true),
            com.example.blog.dto.PostSummary.PostStatusView.PUBLISHED
        )
        FROM Post p JOIN p.author u
        WHERE p.status = 'PUBLISHED'
    """)
    Page<PostSummary> findSummaries(Pageable pageable);
}
```
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">


### PostRepositoryCustom — قرارداد
</div>


```java
package com.example.blog.repository;

import com.example.blog.dto.PostSearchCriteria;
import com.example.blog.dto.PostSummary;
import org.springframework.data.domain.Page;

public interface PostRepositoryCustom {
    Page<PostSummary> searchDynamic(PostSearchCriteria criteria);
}
```
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">


### PostRepositoryImpl — پیاده‌سازی با Criteria API ⭐
</div>


```java
package com.example.blog.repository;

import com.example.blog.dto.PostSearchCriteria;
import com.example.blog.dto.PostSummary;
import com.example.blog.entity.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class PostRepositoryImpl implements PostRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Page<PostSummary> searchDynamic(PostSearchCriteria c) {
        CriteriaBuilder cb = em.getCriteriaBuilder();

        // ----- Query اصلی -----
        CriteriaQuery<PostSummary> cq = cb.createQuery(PostSummary.class);
        Root<Post> post = cq.from(Post.class);
        Join<Post, User> author = post.join("author", JoinType.INNER);

        // ----- Subquery برای commentCount -----
        Subquery<Long> commentCount = cq.subquery(Long.class);
        Root<Comment> comment = commentCount.from(Comment.class);
        commentCount.select(cb.count(comment))
                    .where(
                        cb.equal(comment.get("post"), post),
                        cb.isTrue(comment.get("approved"))
                    );

        // ----- Projection -----
        cq.select(cb.construct(
            PostSummary.class,
            post.get("id"),
            post.get("title"),
            post.get("slug"),
            author.get("username"),
            commentCount.getSelection(),
            cb.literal(PostSummary.PostStatusView.PUBLISHED)
        ));

        // ----- Predicates داینامیک -----
        List<Predicate> predicates = new ArrayList<>();

        if (c.titleLike() != null && !c.titleLike().isBlank()) {
            predicates.add(cb.like(cb.lower(post.get("title")),
                    "%" + c.titleLike().toLowerCase() + "%"));
        }
        if (c.authorUsername() != null && !c.authorUsername().isBlank()) {
            predicates.add(cb.equal(author.get("username"), c.authorUsername()));
        }
        if (c.status() != null) {
            predicates.add(cb.equal(post.get("status"),
                    Post.PostStatus.valueOf(c.status().name())));
        }
        if (c.minViews() != null) {
            predicates.add(cb.greaterThanOrEqualTo(post.get("viewCount"), c.minViews()));
        }
        if (c.tagName() != null && !c.tagName().isBlank()) {
            // با subquery برای tag چون ManyToMany
            Subquery<Long> tagSub = cq.subquery(Long.class);
            Root<Post> postSub = tagSub.from(Post.class);
            Join<Post, Tag> tagJoin = postSub.join("tags", JoinType.INNER);
            tagSub.select(postSub.get("id"))
                  .where(cb.equal(tagJoin.get("name"), c.tagName()));
            predicates.add(post.get("id").in(tagSub));
        }

        cq.where(cb.and(predicates.toArray(new Predicate[0])));

        // ----- Sort -----
        Path<?> sortPath = "viewCount".equals(c.sortBy())
                ? post.get("viewCount")
                : post.get("createdAt");
        cq.orderBy(c.desc() ? cb.desc(sortPath) : cb.asc(sortPath));

        TypedQuery<PostSummary> query = em.createQuery(cq);
        query.setFirstResult(c.page() * c.size());
        query.setMaxResults(c.size());

        List<PostSummary> content = query.getResultList();

        // ----- Count Query جدا -----
        CriteriaQuery<Long> countCq = cb.createQuery(Long.class);
        Root<Post> countRoot = countCq.from(Post.class);
        Join<Post, User> countAuthor = countRoot.join("author", JoinType.INNER);
        countCq.select(cb.count(countRoot));

        List<Predicate> countPredicates = new ArrayList<>();
        if (c.titleLike() != null && !c.titleLike().isBlank()) {
            countPredicates.add(cb.like(cb.lower(countRoot.get("title")),
                    "%" + c.titleLike().toLowerCase() + "%"));
        }
        if (c.authorUsername() != null && !c.authorUsername().isBlank()) {
            countPredicates.add(cb.equal(countAuthor.get("username"), c.authorUsername()));
        }
        if (c.status() != null) {
            countPredicates.add(cb.equal(countRoot.get("status"),
                    Post.PostStatus.valueOf(c.status().name())));
        }
        if (c.minViews() != null) {
            countPredicates.add(cb.greaterThanOrEqualTo(countRoot.get("viewCount"), c.minViews()));
        }
        if (c.tagName() != null && !c.tagName().isBlank()) {
            Subquery<Long> tagSub = countCq.subquery(Long.class);
            Root<Post> postSub = tagSub.from(Post.class);
            Join<Post, Tag> tagJoin = postSub.join("tags", JoinType.INNER);
            tagSub.select(postSub.get("id"))
                  .where(cb.equal(tagJoin.get("name"), c.tagName()));
            countPredicates.add(countRoot.get("id").in(tagSub));
        }
        countCq.where(cb.and(countPredicates.toArray(new Predicate[0])));

        Long total = em.createQuery(countCq).getSingleResult();

        return new PageImpl<>(content, PageRequest.of(c.page(), c.size()), total);
    }
}
```
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">


---

## ۷. Serviceها

### PostService
</div>


```java
package com.example.blog.service;

import com.example.blog.dto.*;
import com.example.blog.entity.*;
import com.example.blog.repository.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final TagRepository tagRepository;
    private final EntityManager em;

    // ---------------- CRUD ----------------

    @Transactional
    public PostDetail createPost(String username, String title, String content, Set<String> tagNames) {
        User author = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Post post = new Post();
        post.setTitle(title);
        post.setSlug(slugify(title));
        post.setContent(content);
        post.setStatus(Post.PostStatus.DRAFT);

        author.addPost(post);

        for (String name : tagNames) {
            Tag tag = tagRepository.findByName(name)
                    .orElseGet(() -> {
                        Tag t = new Tag();
                        t.setName(name);
                        return tagRepository.save(t);
                    });
            post.addTag(tag);
        }

        Post saved = postRepository.save(post);
        return toDetail(saved);
    }

    // ---------------- Read با JOIN FETCH ----------------

    @Transactional(readOnly = true)
    public PostDetail getPostBySlug(String slug) {
        Post post = postRepository.findBySlug(slug)
                .orElseThrow(() -> new EntityNotFoundException("Post not found"));
        // هنوز lazy هستن؛ به on-demand بارگذاری می‌شن
        return toDetail(post);
    }

    @Transactional(readOnly = true)
    public List<PostDetail> getPublishedPosts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        // JOIN FETCH → یک query، بدون N+1
        return postRepository.findAllPublishedWithDetails(pageable).stream()
                .map(this::toDetail)
                .toList();
    }

    // ---------------- Criteria Search ----------------

    @Transactional(readOnly = true)
    public Page<PostSummary> search(PostSearchCriteria criteria) {
        return postRepository.searchDynamic(criteria);
    }

    // ---------------- آپدیت با Optimistic Locking ----------------

    @Transactional
    public PostDetail updatePost(Long id, String newTitle, String newContent, Long expectedVersion) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException());

        // چک دستی نسخه (یا بذار خود JPA در commit چک کنه)
        if (!Objects.equals(post.getVersion(), expectedVersion)) {
            throw new ObjectOptimisticLockingFailureException(Post.class, id);
        }

        post.setTitle(newTitle);
        post.setContent(newContent);
        // flush می‌کنه و @Version رو چک می‌کنه
        try {
            em.flush();
        } catch (OptimisticLockException ex) {
            throw new ObjectOptimisticLockingFailureException(Post.class, id);
        }

        return toDetail(post);
    }

    // ---------------- Bulk update ----------------

    @Transactional
    public int incrementView(Long postId) {
        return postRepository.incrementViewCount(postId);
    }

    // ---------------- Mapping ----------------

    private PostDetail toDetail(Post post) {
        return new PostDetail(
            post.getId(),
            post.getTitle(),
            post.getSlug(),
            post.getContent(),
            post.getAuthor().getUsername(),    // lazy → query اضافه (مراقب باش)
            post.getAuthor().getEmail(),
            post.getTags().stream().map(Tag::getName).toList(),
            post.getComments().stream()
                .filter(Comment::isApproved)
                .map(c -> new PostDetail.CommentView(
                        c.getId(), c.getText(), c.getUser().getUsername()))
                .toList(),
            post.getCreatedAt()
        );
    }

    private String slugify(String title) {
        return title.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                + "-" + System.currentTimeMillis();
    }
}
```
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">


### UserService — یک مثال با EntityManager خام
</div>


```java
package com.example.blog.service;

import com.example.blog.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {

    @PersistenceContext
    private EntityManager em;

    // مثال JPQL خام
    @Transactional(readOnly = true)
    public List<User> findUsersWithMinPosts(int minPosts) {
        String jpql = """
            SELECT u FROM User u
            WHERE (SELECT COUNT(p) FROM Post p WHERE p.author = u AND p.status = 'PUBLISHED') >= :min
            ORDER BY u.createdAt DESC
        """;
        TypedQuery<User> q = em.createQuery(jpql, User.class);
        q.setParameter("min", (long) minPosts);
        return q.getResultList();
    }

    // مثال find vs getReference
    @Transactional
    public void assignProfile(Long userId, String bio, String website) {
        // getReference → بدون SELECT، فقط proxy می‌سازه (مفید برای FK)
        User userRef = em.getReference(User.class, userId);

        Profile profile = new Profile();
        profile.setBio(bio);
        profile.setWebsite(website);
        profile.setUser(userRef);

        em.persist(profile);
    }
}
```
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">


---

## ۸. Controllerها
</div>


```java
package com.example.blog.controller;

import com.example.blog.dto.*;
import com.example.blog.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<PostDetail> create(
            @RequestParam String username,
            @RequestParam String title,
            @RequestParam String content,
            @RequestParam Set<String> tags) {
        return ResponseEntity.ok(postService.createPost(username, title, content, tags));
    }

    @GetMapping("/{slug}")
    public ResponseEntity<PostDetail> getBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(postService.getPostBySlug(slug));
    }

    @GetMapping("/published")
    public ResponseEntity<List<PostDetail>> getPublished(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(postService.getPublishedPosts(page, size));
    }

    // Criteria search
    @PostMapping("/search")
    public ResponseEntity<Page<PostSummary>> search(@RequestBody PostSearchCriteria criteria) {
        return ResponseEntity.ok(postService.search(criteria));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostDetail> update(
            @PathVariable Long id,
            @RequestParam String title,
            @RequestParam String content,
            @RequestParam Long version) {
        return ResponseEntity.ok(postService.updatePost(id, title, content, version));
    }

    @PostMapping("/{id}/view")
    public ResponseEntity<Integer> incrementView(@PathVariable Long id) {
        return ResponseEntity.ok(postService.incrementView(id));
    }
}
```
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">


---

## ۹. data.sql — داده اولیه
</div>


```sql
-- password و hash لازم نیست چون فقط read داریم
INSERT INTO users (username, email, full_name, status, created_at, version)
VALUES ('ali', 'ali@test.com', 'Ali Ahmadi', 'ACTIVE', CURRENT_TIMESTAMP, 0);

INSERT INTO users (username, email, full_name, status, created_at, version)
VALUES ('sara', 'sara@test.com', 'Sara Rezaei', 'ACTIVE', CURRENT_TIMESTAMP, 0);

INSERT INTO posts (title, slug, content, status, view_count, author_id, created_at, version)
VALUES ('Intro to JPA', 'intro-to-jpa', 'JPA is...', 'PUBLISHED', 100, 1, CURRENT_TIMESTAMP, 0);

INSERT INTO posts (title, slug, content, status, view_count, author_id, created_at, version)
VALUES ('Criteria API', 'criteria-api', 'Criteria is...', 'PUBLISHED', 50, 2, CURRENT_TIMESTAMP, 0);

INSERT INTO tags (name, created_at, version) VALUES ('java', CURRENT_TIMESTAMP, 0);
INSERT INTO tags (name, created_at, version) VALUES ('jpa', CURRENT_TIMESTAMP, 0);

INSERT INTO post_tags (post_id, tag_id) VALUES (1, 1), (1, 2), (2, 1);
```
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">


---

## ۱۰. تست‌ها و دیدن N+1

### تست سرویس با آمار Hibernate
</div>


```java
package com.example.blog;

import com.example.blog.entity.*;
import com.example.blog.repository.*;
import com.example.blog.service.PostService;
import jakarta.persistence.EntityManager;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class N1ProblemTest {

    @Autowired PostService postService;
    @Autowired EntityManager em;

    @Test
    @Transactional
    void detectN1() {
        Statistics stats = em.getEntityManagerFactory()
                .unwrap(SessionFactory.class).getStatistics();
        stats.clear();

        // این ممکنه N+1 بزنه (لازمه همه lazy هستن)
        var posts = postService.getPublishedPosts(0, 10);

        long queryCount = stats.getPrepareStatementCount();
        System.out.println(">>> Queries executed: " + queryCount);

        // اگه درست fetch کرده باشیم، با 10 post باید ~1-2 query بزنه نه 20+
        assertThat(queryCount).isLessThan(10);
    }
}
```
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">


### تست Optimistic Locking
</div>


```java
@Test
void optimisticLocking_shouldThrowOnConflict() {
    // thread 1: post رو می‌خونه
    // thread 2: post رو می‌خونه و آپدیت می‌کنه
    // thread 1: تلاش برای آپدیت با version قدیمی → exception
}
```
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">


---

## ۱۱. خلاصه آنچه پوشش داده شد

| مفهوم | کجا |
|---|---|
| `@MappedSuperclass` | `BaseEntity` |
| `@Version` (Optimistic Lock) | `BaseEntity` |
| Callbacks `@PrePersist/@PreUpdate` | `BaseEntity` |
| `@OneToOne` مالک/معکوس | `Profile` / `User` |
| `@OneToMany` + `orphanRemoval` | `User.posts`, `Post.comments` |
| `@ManyToOne` LAZY | `Post.author`, `Comment.user` |
| `@ManyToMany` با JoinTable | `Post.tags`, `User.favorites` |
| Second-Level Cache | `Tag` با `@Cacheable` |
| `@BatchSize` | `Post.comments` |
| JPQL + JOIN FETCH | `PostRepository` |
| EntityGraph | `UserRepository` |
| Interface Projection | `PostRepository.PostListView` |
| DTO Constructor Projection | `PostRepository.findSummaries` |
| **Criteria API** با subquery و projection داینامیک | `PostRepositoryImpl.searchDynamic` |
| `EntityManager` خام + `getReference` | `UserService` |
| Bulk UPDATE با `@Modifying` | `incrementViewCount` |
| Pagination | `Page`, `PageRequest`, `PageImpl` |
| N+1 و تستش | `N1ProblemTest` |

---

## ۱۲. نحوه اجرا
</div>

```bash
cd 'jpa/blog-app'
```
```bash
cd 'blog-app'
```

```bash
cd 'blog-app'
mvn spring-boot:run
```
```bash
# H2 console:
 google-chrome http://localhost:8080/h2-console
# JDBC URL: jdbc:h2:mem:blogdb
```

```bash
# تست Criteria search:
curl -X POST http://localhost:8080/api/posts/search \
  -H "Content-Type: application/json" \
  -d '{"titleLike":"jpa","status":"PUBLISHED","page":0,"size":10,"sortBy":"viewCount","desc":true}'
```
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">


---

**حالا آماده‌ام برات سؤال‌های عمیق‌تر جواب بدم.** مثلاً می‌تونی بپرسی:
- چرا `getReference` بهتره تو این حالت؟
- Criteria چه وقت به JPQL ترجیح داره و برعکس؟
- چرا `@Data` روی Entity بد است؟
- چطور `searchDynamic` رو تمیزتر کنم (avoid duplication count query)؟
- N+1 دقیقاً کجاها اتفاق می‌افته و همه راه‌حل‌هاش؟
- چرا `@Version` رو Long گذاشتیم نه int؟
- تفاوت `save()` و `persist()` و `merge()`؟
- `getSingleResult` و مدیریت NoResultException.
- cascade vs orphanRemoval دقیقاً یعنی چی؟

بگو کدوم برات مبهم مونده یا کدوم رو عمیق‌تر باز کنم. 👇
</div>




<!-- SOURCE: 3.md -->
# File: 3.md

<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">
# TagRepository — چیزی که جا افتاد 😅

توی `PostService` از `tagRepository.findByName(name)` استفاده کردم ولی تعریفش رو ننوشتم. اینه:
</div>

```java
package com.example.blog.repository;

import com.example.blog.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface TagRepository extends JpaRepository<Tag, Long> {

    // متد مشتق‌شده — Spring Data خودش query می‌سازه
    Optional<Tag> findByName(String name);

    // پیدا کردن چند tag با هم (برای fetch گروهی)
    List<Tag> findByNameIn(Set<String> names);

    // چک وجود
    boolean existsByName(String name);

    // Tagهای پرکاربرد — با JPQL aggregate
    @Query("""
        SELECT t FROM Tag t
        JOIN t.posts p
        WHERE p.status = 'PUBLISHED'
        GROUP BY t
        ORDER BY COUNT(p) DESC
    """)
    List<Tag> findMostUsedTags();   // می‌تونی Pageable هم اضافه کنی

    // Tagهای بدون پست (برای cleanup)
    @Query("SELECT t FROM Tag t WHERE t.posts IS EMPTY")
    List<Tag> findOrphanTags();

    // شمارش پست‌های هر tag در یک پروجکشن
    @Query("""
        SELECT t.name AS name, COUNT(p) AS postCount
        FROM Tag t LEFT JOIN t.posts p
        WHERE p.status = 'PUBLISHED' OR p IS NULL
        GROUP BY t.name
        ORDER BY COUNT(p) DESC
    """)
    List<TagCountView> findTagCounts();

    interface TagCountView {
        String getName();
        Long getPostCount();
    }
}
```
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">

---

## نکات مهم درباره این Repository

### ۱. `findByName` چرا `Optional` برمی‌گردونه؟
چون ممکنه tag وجود نداشته باشه. تو `PostService` نوشتم:
</div>

```java
Tag tag = tagRepository.findByName(name)
        .orElseGet(() -> {
            Tag t = new Tag();
            t.setName(name);
            return tagRepository.save(t);
        });
```
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">
یعنی: اگه بود استفاده کن، نبود بساز. الگوی get-or-create.

### ۲. نکته ظریف: `get-or-create` با race condition
اگه دو تا request همزمان بیان و هر دو `findByName` بزنن و هیچی پیدا نکنن، هر دو `save` می‌کنن → **unique constraint violation** روی `name`.

راه‌حل تمیزتر:
</div>

```java
@Transactional
public Tag getOrCreate(String name) {
    return tagRepository.findByName(name)
            .orElseGet(() -> {
                try {
                    Tag t = new Tag();
                    t.setName(name);
                    return tagRepository.saveAndFlush(t);
                } catch (DataIntegrityViolationException e) {
                    // یکی دیگه همزمان ساخت — دوباره بخون
                    return tagRepository.findByName(name)
                            .orElseThrow();
                }
            });
}
```
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">
یا با native query `INSERT ... ON CONFLICT DO NOTHING` (بسته به DB).

### ۳. `JOIN t.posts` — چون `Tag.posts` سمت معکوسه
یادت باشه تو `Tag` نوشتیم:
</div>

```java
@ManyToMany(mappedBy = "tags", fetch = FetchType.LAZY)
private Set<Post> posts = new HashSet<>();
```
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">
پس `JOIN t.posts` توی JPQL کار می‌کنه ولی SQL معادلش از جدول واسط `post_tags` استفاده می‌کنه.

### ۴. `IS EMPTY` برای collection
توی JPQL می‌تونی `t.posts IS EMPTY` بنویسی (نه `= null`). این برا collectionهاست. برای scalar از `IS NULL` استفاده کن.

### ۵. Second-Level Cache روی Tag
چون `Tag` رو `@Cacheable @Cache(usage = READ_ONLY)` زدیم:
- `findByName` از کش می‌خونه (بدون query)
- `save` توی cache هم می‌نویسه
- خوبه چون tagها کم تغییر می‌کنن

⚠️ ولی حواست باشه: **کش فقط برای `findById/findByName` کار می‌کنه، نه برای JPQL دستی** مگه `@QueryHints` بذاری:
</div>

```java
@QueryHints(@QueryHint(name = "org.hibernate.cacheable", value = "true"))
@Query("SELECT t FROM Tag t WHERE ...")
```
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">
---

## آپدیت PostService با get-or-create تمیز
</div>

```java
private final TagRepository tagRepository;

@Transactional
public PostDetail createPost(String username, String title, String content, Set<String> tagNames) {
    // ... (کد قبلی)

    for (String name : tagNames) {
        post.addTag(getOrCreateTag(name));
    }
    // ...
}

private Tag getOrCreateTag(String name) {
    return tagRepository.findByName(name)
            .orElseGet(() -> {
                try {
                    Tag t = new Tag();
                    t.setName(name);
                    return tagRepository.saveAndFlush(t);
                } catch (DataIntegrityViolationException e) {
                    return tagRepository.findByName(name).orElseThrow();
                }
            });
}
```
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">

**چرا `saveAndFlush` و نه `save`؟**
چون اگه flush نکنیم، exception تا commit عقب می‌افته و تو نمی‌تونی تو همون متد recover کنی. `saveAndFlush` خطا رو همون‌جا throw می‌کنه تا `catch` کار کنه.

---

کدوم قسمت برات مبهمه؟ می‌خوای:
- `save` vs `saveAndFlush` vs `saveAll` رو کامل توضیح بدم؟
- رفتار `@ManyToMany` سمت معکوس (mappedBy) و اینکه چرا tag نمیتونه post اضافه کنه بدون owner؟
- یا بریم سراغ `@Query` projectionهای مختلف و تفاوت interface vs constructor vs dynamic؟

</div>



<!-- SOURCE: 4.md -->
# File: 4.md

<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">


# UserSummary — DTO جا افتاده 😅

دقیقاً مثل `TagRepository`، توی `UserRepository` به `UserSummary` ارجاع دادم ولی کلاسش رو نساختم. اینه:
</div>


```java
package com.example.blog.dto;

public record UserSummary(
    Long id,
    String username,
    String email
) {}
```
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">


---

## چرا record و نه کلاس معمولی؟

**Constructor Projection** در JPQL فقط به یه **constructor با پارامترهای دقیقاً به همون ترتیب** نیاز داره. record خودش اینو به‌صورت خودکار می‌سازه:
</div>


```java
new com.example.blog.dto.UserSummary(u.id, u.username, u.email)
//   ↑ دقیقاً همون constructor که record می‌سازه
```
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">


اگه کلاس معمولی بود:
</div>


```java
public class UserSummary {
    private final Long id;
    private final String username;
    private final String email;

    public UserSummary(Long id, String username, String email) {
        this.id = id;
        this.username = username;
        this.email = email;
    }
    // getters...
}
```
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">


**نکات مهم constructor projection:**
- ترتیب پارامترها باید **دقیقاً** مطابق `SELECT` باشه
- اسم فیلدها مهم نیست، فقط **ترتیب و type** مهمه
- مسیر کامل کلاس (`com.example.blog.dto.UserSummary`) لازمه، نه فقط اسم ساده
- record تمیزتره چون immutable و boilerplate نداره

---

## جاهایی که هنوز به DTO ارجاع دادم و تعریف نکردم

بذار همه‌شون رو یه‌جا بدم که پروژه کامل کامپایل بشه:

### UserSummary
</div>


```java
package com.example.blog.dto;

public record UserSummary(Long id, String username, String email) {}
```
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">


### UserRepository — آپدیت import
</div>


```java
package com.example.blog.repository;

import com.example.blog.dto.UserSummary;   // ← این رو هم اضافه کن
import com.example.blog.entity.User;
import org.springframework.data.jpa.repository.*;
// ...
```
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">


---

## پروجکشن‌ها در JPA — یه مرور سریع

چون قراره بریم سراغ مفاهیم عمیق‌تر، بذار انواع projection رو کنار هم ببینیم تا این `UserSummary` تو ذهنت جا بیفته:

### ۱. Constructor Projection (همونی که استفاده کردیم)
</div>


```java
@Query("""
    SELECT new com.example.blog.dto.UserSummary(u.id, u.username, u.email)
    FROM User u WHERE u.status = 'ACTIVE'
""")
List<UserSummary> findAllActiveSummaries();
```
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">


✅ type-safe، immutable، کنترل کامل روی خروجی
❌ باید مسیر کامل کلاس بنویسی، ترتیب پارامترها رو دستی نگه داری

**اگه یه فیلد جابه‌جا بشه → خطا فقط در runtime.** این بزرگ‌ترین ضعفشه.

### ۲. Interface Projection
</div>


```java
public interface UserView {
    Long getId();
    String getUsername();
    String getEmail();
}

@Query("SELECT u.id AS id, u.username AS username, u.email AS email FROM User u")
List<UserView> findAllAsView();
```
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">


✅ سبک، به‌روزرسانی آسون، Spring خودش proxy می‌سازه
❌ فقط **getter** داره، نمی‌تونی منطق بذاری، nullability مبهمه

**نکته ظریف:** aliasها (`AS id`) لازم نیستن اگه اسم getter با اسم فیلد entity یکی باشه، ولی گذاشتنشون امن‌تره.

### ۳. Dynamic Projection (با `Class<T>`)
</div>


```java
<T> List<T> findByStatus(UserStatus status, Class<T> type);
```
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">


می‌تونی همون متد رو با هر نوع projection صدا بزنی:
</div>


```java
userRepository.findByStatus(ACTIVE, UserSummary.class);
userRepository.findByStatus(ACTIVE, UserView.class);
```
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">


کاربردی ولی کمتر استفاده می‌شه.

### ۴. DTO در Queryهای Derived

Spring Data از Spring Boot 3 به بعد می‌تونه اینو مستقیم بسازه:
</div>


```java
List<UserSummary> findByStatus(UserStatus status);
```
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">


✅ خیلی تمیز
❌ فقط وقتی که DTO یه constructor با پارامترهای هم‌نام فیلدهای entity داشته باشه (ترتیب مهم نیست، **اسم** مهمه)

---

## تفاوت مهم: constructor projection vs derived query DTO

| | Constructor Projection (`@Query` + `new`) | Derived Query DTO |
|---|---|---|
| تعریف | دستی توی JPQL | خودکار از اسم متد |
| ملاک تطابق | **ترتیب پارامترها** | **اسم فیلدها** |
| کنترل روی SELECT | کامل | محدود |
| خطا در refactor | فقط runtime | compile-time اگه اسم عوض بشه |

مثال derived برای `UserSummary`:
</div>


```java
// اگه UserSummary فیلدهای id, username, email داشته باشه، این کار می‌کنه:
List<UserSummary> findByStatus(User.UserStatus status);
```

<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">


اینجا Spring خودش تشخیص می‌ده که `UserSummary(id, username, email)` بسازه چون اسم فیلدها هم‌خوانی دارن.

---

## چک نهایی — همه DTOهای پروژه

بذار مطمئن شیم همه‌چی تعریف شده:
</div>


```
dto/
├── UserSummary        ✅ (الان ساختم)
├── PostSummary        ✅ (قبلاً بود)
├── PostDetail         ✅ (قبلاً بود)
├── PostSearchCriteria ✅ (قبلاً بود)
```

Repositoryها:
```
repository/
├── UserRepository     ✅
├── PostRepository     ✅
├── PostRepositoryCustom ✅
├── PostRepositoryImpl ✅
└── TagRepository      ✅ (قبلاً ساختم)
```
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">


---

الان پروژه کامپایل می‌شه. می‌تونیم بریم سراغ سؤال‌های عمیق‌تر که گفتی.

پیشنهاد می‌کنم از این‌ها شروع کنیم (بگو کدوم):
1. **`save()` vs `persist()` vs `merge()`** — یه‌جا و درست
2. **cascade vs orphanRemoval** — کی چی اتفاق می‌افته
3. **N+1 دقیق** — همه راه‌حل‌ها کنار هم
4. **Criteria تمیزتر** — بدون تکرار count query
5. **Lazy Loading و `LazyInitializationException`** — چرا و چطور حل
6. **`@Version` و Optimistic Locking** — سناریوی واقعی

کدوم؟ 🎯

</div>



<!-- SOURCE: 5.md -->
# File: 5.md

<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">


# PostSummary — نسخه تعریف‌شده + دو باگ که تو JPQL و Criteria قبلی داشتم 🐛

`PostSummary` رو قبلاً تعریف کردم، ولی الان که دقیق‌تر نگاه می‌کنم، دو تا مشکل توی **استفاده‌ش** داشتم که باید درست کنم. اول تعریف، بعد باگ‌ها:

---

## ۱. تعریف درست
</div>


```java
package com.example.blog.dto;

public record PostSummary(
    Long id,
    String title,
    String slug,
    String authorName,
    Long commentCount,      // ← Long نه long (توضیح پایین)
    PostStatusView status
) {
    public enum PostStatusView { DRAFT, PUBLISHED, ARCHIVED }
}
```
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">


---

## ۲. باگ اول: enum literal توی JPQL ❌

توی `PostRepository.findSummaries` نوشتم:
</div>


```java
@Query("""
    SELECT new com.example.blog.dto.PostSummary(
        p.id, p.title, p.slug, u.username,
        (SELECT COUNT(c) FROM Comment c WHERE c.post = p AND c.approved = true),
        com.example.blog.dto.PostSummary.PostStatusView.PUBLISHED   // ← ❌ این
    )
    FROM Post p JOIN p.author u
    WHERE p.status = 'PUBLISHED'
""")
```
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">


**چرا اشتباهه:**
JPQL syntax `com.example.blog.dto.PostSummary.PostStatusView.PUBLISHED` رو به‌عنوان enum literal نمی‌شناسه. JPQL enum literal نداره (برخلاف SQL). Hibernate ممکنه اینو به‌عنوان دسترسی به static field تفسیر کنه ولی رفتارش غیرقابل‌پیش‌بینیه و تو اکثر نسخه‌ها **fail می‌کنه** با خطایی مثل:
</div>


```
Cannot construct enum from string / Unknown path
```
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">


### ✅ فیکس: enum رو به‌عنوان parameter پاس بده
</div>


```java
@Query("""
    SELECT new com.example.blog.dto.PostSummary(
        p.id, p.title, p.slug, u.username,
        (SELECT COUNT(c) FROM Comment c WHERE c.post = p AND c.approved = true),
        :status
    )
    FROM Post p JOIN p.author u
    WHERE p.status = 'PUBLISHED'
""")
Page<PostSummary> findSummaries(
    @Param("status") PostSummary.PostStatusView status,
    Pageable pageable);
```
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">


و موقع صدا زدن:
</div>


```java
postRepository.findSummaries(PostSummary.PostStatusView.PUBLISHED, pageable);
```
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">

**نکته:** parameter می‌تونه هر enum‌ای باشه — Hibernate خودش bindش می‌کنه به همون type. حالا این دقیقاً همون enum‌ای هست که constructor انتظار داره.

### گزینه جایگزین: اصلاً enum رو نذار
چون query فقط `PUBLISHED` برمی‌گردونه، این فیلد توی DTO اضافیه. ساده‌ترین فیکس:
</div>


```java
public record PostSummary(
    Long id,
    String title,
    String slug,
    String authorName,
    Long commentCount
) {}
```
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">


و JPQL بدون enum. این تمیزترین راهه اگه واقعاً همیشه فقط PUBLISHED برمی‌گردونی.

---

## ۳. باگ دوم: `cb.literal()` توی Criteria ❌

توی `PostRepositoryImpl.searchDynamic` نوشتم:
</div>


```java
cq.select(cb.construct(
    PostSummary.class,
    post.get("id"),
    post.get("title"),
    post.get("slug"),
    author.get("username"),
    commentCount.getSelection(),
    cb.literal(PostSummary.PostStatusView.PUBLISHED)   // ← ⚠️
));
```
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">


**چرا مشکوکه:**
`cb.literal()` برای literalهای ساده (عدد، رشته) طراحی شده. برای **enum** به‌درستی bind نمی‌شه — Hibernate بعضی وقتا به‌جای enum value، اسمش رو به‌صورت رشته bind می‌کنه یا کلاً fail می‌کنه.

### ✅ فیکس: ParameterExpression استفاده کن
</div>


```java
// تعریف parameter
ParameterExpression<PostSummary.PostStatusView> statusParam =
    cb.parameter(PostSummary.PostStatusView.class, "status");

// استفاده در constructor
cq.select(cb.construct(
    PostSummary.class,
    post.get("id"),
    post.get("title"),
    post.get("slug"),
    author.get("username"),
    commentCount.getSelection(),
    statusParam                          // ← ← ←
));

// بعد از ساخت query، مقدار بده
TypedQuery<PostSummary> query = em.createQuery(cq);
query.setParameter("status", PostSummary.PostStatusView.PUBLISHED);
query.setFirstResult(c.page() * c.size());
query.setMaxResults(c.size());
```
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">


این مطمئن‌ترین راهه. parameter مثل هر parameter دیگه bind می‌شه، type-safe هم هست.

---

## ۴. نکته ظریف: `Long` vs `long` برای `commentCount`

قبلاً `long` (primitive) داشتم، الان `Long` کردم. چرا؟

| | `long` | `Long` |
|---|---|---|
| اگه query مقدار null برگردونه | 💥 NullPointerException موقع constructor call | ✅ null می‌شه، ادامه می‌ده |
| Hibernate 6 constructor resolution | معمولاً autoboxing می‌کنه | مطمئن‌تر |
| برای `COUNT()` که همیشه مقدار داره | بی‌خطر | بی‌خطر |

توی `COUNT` هر دو کار می‌کنن، ولی برای **سایر aggregateها** (مثل `MAX`, `AVG`, `SUM`) که ممکنه `null` برگردونن، `Long` امن‌تره. پس **قاعده کلی: برای هر فیلد aggregate از wrapper type استفاده کن**.

**یادآوری:** برای فیلدهای اجباری (id, title) هم `Long`/`String` گذاشتم چون record فیلدهایش nullable هستن به‌هرحال.

---

## ۵. نسخه نهایی — بدون enum برای سادگی

اگه فقط یه `PostSummary` برای لیست published می‌خوای، پیشنهاد می‌کنم این ساده‌ترین نسخه رو استفاده کن:
</div>


```java
// dto/PostSummary.java
public record PostSummary(
    Long id,
    String title,
    String slug,
    String authorName,
    Long commentCount
) {}
```

```java
// PostRepository.java
@Query("""
    SELECT new com.example.blog.dto.PostSummary(
        p.id, p.title, p.slug, u.username,
        (SELECT COUNT(c) FROM Comment c WHERE c.post = p AND c.approved = true)
    )
    FROM Post p JOIN p.author u
    WHERE p.status = 'PUBLISHED'
""")
Page<PostSummary> findSummaries(Pageable pageable);
```

```java
// PostRepositoryImpl.searchDynamic — بخش projection
cq.select(cb.construct(
    PostSummary.class,
    post.get("id"),
    post.get("title"),
    post.get("slug"),
    author.get("username"),
    commentCount.getSelection()
));
```
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">


نه enum نه parameter نه headache. 🎯

---

## چک‌لیست بعد از این فیکس

- ✅ `PostSummary` defined
- ✅ JPQL enum literal fix شد
- ✅ Criteria `cb.literal` fix شد
- ✅ `long` → `Long` fix شد
- ✅ `UserSummary` و `TagRepository` قبلاً fix شدن

پروژه کامپایل و اجرا می‌شه.

---

## یه درس مهم که این باگ‌ها بهمون دادن

**Constructor Projection در JPQL/Criteria سه تا دام داره:**
1. **enum literal** → باید parameter باشه، نمی‌تونی مستقیم بنویسی
2. **subquery در select list** → Hibernate 6 پشتیبانی می‌کنه، ولی استاندارد JPA نیست؛ برای پورت کردن به provider دیگه مراقب باش
3. **ترتیب پارامترها** → باید **دقیقاً** هم‌ترتیب با record/constructor باشه، وگرنه فقط تو runtime می‌فهمی

به همین دلیله که خیلی‌ها **interface projection** رو ترجیح می‌دن — این دام‌ها رو نداره (البته محدودیت‌های خودش رو داره).

---

حالا که DTOها و Repositoryها کامل شدن، بریم سراغ سؤال‌های عمیق‌تر؟ بگو کدوم:

1. **`save()` vs `persist()` vs `merge()`** — دقیقاً کی چی
2. **cascade vs orphanRemoval** — سناریوهای واقعی
3. **N+1** — همه راه‌حل‌ها کنار هم (JOIN FETCH, EntityGraph, BatchSize, subselect)
4. **Lazy Loading و `LazyInitializationException`** — علت و ۴ راه حل
5. **`@Version` و Optimistic Locking** — سناریوی دو thread

کدوم؟ 🎯

</div>



<!-- SOURCE: 6.md -->
# File: 6.md

<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">


# BlogApplication — کلاس main پروژه

فقط این یه فایل جا افتاده بود که Spring Boot بدون اون بالا نمیاد:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```java
package com.example.blog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BlogApplication {

    public static void main(String[] args) {
        SpringApplication.run(BlogApplication.class, args);
    }
}
```
</div>


---

## همین؟! آره، همین 😄

ولی بذار یه چیز مهم رو بگم چون ممکنه به مشکل بخوری:

### نکته ۱: `@SpringBootApplication` دقیقاً چی می‌کنه؟

این annotation ترکیبی از سه‌تاست:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```java
@SpringBootConfiguration   // = @Configuration
@EnableAutoConfiguration   // فعال‌سازی auto-config (JPA, Web, H2 console)
@ComponentScan             // اسکن پکیج و زیرپکیج‌های com.example.blog
```

</div>

**مهم‌ترین بخش: `@ComponentScan`**
چون کلاس تو پکیج `com.example.blog` هست، Spring تمام زیرپکیج‌ها رو اسکن می‌کنه:
- `com.example.blog.entity` → `@Entity`ها
- `com.example.blog.repository` → `@Repository`ها (و Spring Data repositoryها)
- `com.example.blog.service` → `@Service`ها
- `com.example.blog.controller` → `@RestController`ها

⚠️ **اگه `BlogApplication` رو تو پکیج ریشه‌ای نذاری** (مثلاً بذاری تو `com.example.blog.config`)، Spring بقیه رو پیدا **نمی‌کنه** و همه‌چی fail می‌شه. پس **همیشه تو روت پکیج پروژه باشه**.

### نکته ۲: چرا `@EnableJpaRepositories` یا `@EntityScan` لازم نیست؟

چون auto-configuration خودش اینا رو می‌سازه وقتی:
- `spring-boot-starter-data-jpa` توی classpath باشه
- یه `DataSource` تعریف شده باشه (تو `application.yml` هست)
- entityها زیر پکیج روت باشن

اگه entityها یا repositoryها جای عجیبی بودن، مجبور می‌شدی صریح بذاری:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```java
@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.example.blog.repository")
@EntityScan(basePackages = "com.example.blog.entity")
public class BlogApplication { ... }
```
</div>
ولی تو پروژه ما لازم نیست.

### نکته ۳: `@EnableCaching` برای Second-Level Cache؟

یادت باشه `Tag` رو `@Cacheable` کردیم. ولی **Spring Cache** با **Hibernate Second-Level Cache** دو تا چیز مختلفن:

- `@Cacheable` (Hibernate) → کش سطح دوم JPA، **به `@EnableCaching` نیاز نداره** (خود Hibernate مدیریتش می‌کنه)
- `@Cacheable` (Spring) → کش متد سرویس‌ها، **به `@EnableCaching` نیاز داره**

پس برای `Tag` نیازی به `@EnableCaching` نیست. فقط کافیه تو `application.yml` این رو داشته باشی:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```yaml
spring:
  jpa:
    properties:
      hibernate:
        cache:
          use_second_level_cache: true
          region:
            factory_class: org.hibernate.cache.jcache.JCacheRegionFactory
```
</div>
⚠️ **ولی** برای اینکه Hibernate 6 بوت بشه به یه provider کش (مثل `hibernate-jcache` + یه پیاده‌سازی JCache مثل Caffeine یا Ehcache) نیاز داری. اگه نمی‌خوای درگیر شی، **فعلاً `@Cacheable` رو از `Tag` بردار** یا dependency اضافه کن:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```xml
<dependency>
    <groupId>org.hibernate.orm</groupId>
    <artifactId>hibernate-jcache</artifactId>
</dependency>
<dependency>
    <groupId>com.github.ben-manes.caffeine</groupId>
    <artifactId>caffeine</artifactId>
</dependency>
<dependency>
    <groupId>com.github.ben-manes.caffeine</groupId>
    <artifactId>jcache</artifactId>
</dependency>
```
</div>
برای شروع پروژه ساده، **پیشنهاد می‌کنم `@Cacheable` و `@Cache` رو از `Tag` حذف کنی** تا بی‌دردسر بالا بیاد. بعداً که پروژه کار کرد، اضافه‌ش کن.

---

## اجرا
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```bash
# با Maven wrapper (اگه داری)
./mvnw spring-boot:run

# یا با Maven نصب‌شده
mvn spring-boot:run
```
</div>
خروجی موفق باید چیزی شبیه این باشه:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v3.2.5)

... H2 console available at '/h2-console'. Database available at 'jdbc:h2:mem:blogdb'
... Tomcat started on port 8080
```
</div>
بعد برو `http://localhost:8080/h2-console` و با `jdbc:h2:mem:blogdb` / `sa` / (پسورد خالی) وصل شو.

---

## تست‌های سریع بعد از بالا اومدن
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```bash
# لیست پست‌های منتشرشده (باید دو تا از data.sql بیاد)
curl "http://localhost:8080/api/posts/published?page=0&size=10"

# Criteria search
curl -X POST "http://localhost:8080/api/posts/search" \
  -H "Content-Type: application/json" \
  -d '{
    "titleLike": "jpa",
    "status": "PUBLISHED",
    "page": 0,
    "size": 10,
    "sortBy": "viewCount",
    "desc": true
  }'

# گرفتن یه پست با slug
curl "http://localhost:8080/api/posts/intro-to-jpa"

# increment view
curl -X POST "http://localhost:8080/api/posts/1/view"
```

---
</div>

## ساختار نهایی پروژه (کامل)
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
src/main/java/com/example/blog/
├── BlogApplication.java              ✅ ← جدید
├── entity/
│   ├── BaseEntity.java
│   ├── User.java
│   ├── Profile.java
│   ├── Post.java
│   ├── Comment.java
│   └── Tag.java
├── dto/
│   ├── UserSummary.java
│   ├── PostSummary.java
│   ├── PostDetail.java
│   └── PostSearchCriteria.java
├── repository/
│   ├── UserRepository.java
│   ├── PostRepository.java
│   ├── PostRepositoryCustom.java
│   ├── PostRepositoryImpl.java
│   └── TagRepository.java
├── service/
│   ├── PostService.java
│   └── UserService.java
└── controller/
    ├── PostController.java
    └── UserController.java

src/main/resources/
├── application.yml
└── data.sql
```
</div>
---

پروژه الان **کامله**. 🎉

آماده‌ایم بریم سراغ مفاهیم عمیق. از سؤال‌های قبلی بگو کدوم رو باز کنم:

1. **`save()` vs `persist()` vs `merge()`** — دقیقاً کی چی (خیلی پیشنهاد می‌کنم، چون منبع باگ‌های عجیبه)
2. **cascade vs orphanRemoval** — سناریوهای واقعی
3. **N+1** — همه راه‌حل‌ها کنار هم
4. **Lazy Loading و `LazyInitializationException`** — علت و راه‌حل
5. **`@Version` و Optimistic Locking** — سناریوی دو thread

یا هر سؤال دیگه‌ای که تو ذهنته. 🎯


</div>



<!-- SOURCE: 7.md -->
# File: 7.md

<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">


# علت و فیکس خطای `data.sql` 🐛

خطایی که گرفتی تقریباً همیشه یه دلیل اصلی داره: **`data.sql` قبل از اینکه Hibernate جدول‌ها رو بسازه اجرا می‌شه**.

پیام کامل زیر اون `Caused by` (که نذاشتی) تقریباً مطمئناً اینه:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
Caused by: org.h2.jdbc.JdbcSQLSyntaxErrorException: Table "USERS" not found
```
</div>
یعنی جدول `users` هنوز وجود نداره → چون Spring `data.sql` رو **قبل از** `ddl-auto: create-drop` اجرا می‌کنه.

---

## ۱. فیکس اصلی: `defer-datasource-initialization`

توی `application.yml` این خط رو اضافه کن (زیر `spring.jpa`):
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```yaml
spring:
  datasource:
    url: jdbc:h2:mem:blogdb;DB_CLOSE_DELAY=-1
    driver-class-name: org.h2.Driver
    username: sa
    password:
  jpa:
    defer-datasource-initialization: true     # 👈 این خط حیاتی
    hibernate:
      ddl-auto: create-drop
    show-sql: true
    properties:
      hibernate:
        format_sql: true
        jdbc:
          batch_size: 30
        order_inserts: true
        order_updates: true
        generate_statistics: true
  h2:
    console:
      enabled: true
      path: /h2-console
  sql:
    init:
      mode: always
```

**معنی `defer-datasource-initialization: true`:**
> «`data.sql` رو تا وقتی Hibernate تموم DDL رو نساخته عقب بنداز.»

بدون این، ترتیب اینه:
```
1. data.sql  ❌ (جدول هنوز نیست)
2. Hibernate DDL
```

با این، ترتیب می‌شه:
```
1. Hibernate DDL  ✅
2. data.sql       ✅
```
</div>
---

## ۲. اگه بازم fail شد، علت واقعی رو ببین

پیام `ScriptStatementFailedException` فقط **wrapper** هست. علت اصلی رو باید تو خطوط بعدی `Caused by:` ببینی. با `mvn spring-boot:run` یا اجرا تو IntelliJ، خروجی رو کامل اسکرول کن تا ببینی چیزی مثل این:

| خطای واقعی | علت |
|---|---|
| `Table "USERS" not found` | همون مشکل timing → `defer-datasource-initialization` |
| `Column "..." not found` | اسم ستون با entity نمی‌خونه (شاید `ddl-auto` جدول رو با اسم دیگه ساخته) |
| `NULL not allowed for column "..."` | یکی از ستون‌های NOT NULL توی INSERT نیست |
| `Unique index or primary key violation` | data.sql دوبار اجرا شده (مثلاً `ddl-auto: update` + `create-drop` باهم) |
| `Syntax error in SQL statement` | مشکل syntax، مثلاً `value` به‌عنوان اسم ستون |

---

## ۳. چطور مطمئن شی fix کار کرد

بعد از بالا اومدن، لاگ رو نگاه کن. باید این ترتیب رو ببینی:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
Hibernate: create table users (...)
Hibernate: create table posts (...)
Hibernate: create table tags (...)
Hibernate: create table post_tags (...)
...
>>> Running data.sql   ← بعد از DDL
```

بعد برو H2 console:
```
http://localhost:8080/h2-console
JDBC URL: jdbc:h2:mem:blogdb
User: sa
Password: (خالی)
```
و بزن:
```sql
SELECT * FROM users;
SELECT * FROM posts;
```
</div>
باید دو تا user و دو تا post ببینی.

---

## ۴. اگه هنوز مشکل داشتی — چک‌لیست

### الف) اسم جدول‌ها رو چک کن
توی `User` نوشتیم:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```java
@Table(name = "users")  // ← این باعث می‌شه جدول `users` ساخته بشه
```
</div>
اگه `@Table` رو نداشتی، Hibernate از اسم کلاس (`User`) استفاده می‌کرد و جدول `user` می‌ساخت — که `users` توی data.sql پیدا نمی‌شد. (تو کد ما `@Table` هست، پس این مشکل نیست.)

### ب) `spring.jpa.hibernate.ddl-auto` چیه؟
- `create-drop` → ساخت + پاک‌کردن بعد از shutdown (برای dev خوبه)
- `update` → ساخت/آپدیت بدون پاک‌کردن (خطرناک برای production)
- `validate` → فقط چک (برای production با Flyway/Liquibase)

### ج) `spring.sql.init.mode`
- `always` → هر بار اجرا
- `embedded` → فقط برای DB in-memory (H2 پیش‌فرض اینه)
- `never` → هیچ‌وقت

اگه `embedded` باشه و H2 in-memory، خودکار اجرا می‌شه. `always` گذاشتیم که مطمئن باشه.

### د) اگه می‌خوای دوبار INSERT نشه
`create-drop` هر بار DB رو از صفر می‌سازه، پس INSERT تکراری نمی‌شه. ولی اگه `update` داشتی، `data.sql` هر بار اجرا می‌شد و به `unique constraint` می‌خوردی. برای اون حالت از:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```sql
INSERT INTO users (...) VALUES (...)
ON CONFLICT DO NOTHING;  -- یا معادل H2: MERGE INTO ... 
```
</div>

یا بهتر: از **`CommandLineRunner`** استفاده کن:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```java
@Bean
CommandLineRunner initData(UserRepository userRepo, PostRepository postRepo) {
    return args -> {
        if (userRepo.count() > 0) return;   // فقط اگه خالیه
        // ... ساخت user و post
    };
}
```

</div>

این روش **بهتره** چون از JPA رد می‌شه (`@PrePersist`, version، cascade همه کار می‌کنن) و از `data.sql` خیلی امن‌تره.

---

## ۵. پیشنهاد من: `data.sql` رو کلاً بذار کنار

برای پروژه‌های واقعی، `data.sql` فقط یه راه سریع برای mock data هست. باگ‌هاش:
- از JPA رد نمی‌شه → `@PrePersist`, version, cascade همه bypass می‌شن
- به ترتیب جدول‌ها و FKها حساسه
- به schema دقیق وابسته‌ست → refactor کردن entity = شکستن data.sql

راه بهتر برای seed data تو dev:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```java
package com.example.blog.config;

import com.example.blog.entity.*;
import com.example.blog.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Configuration
@RequiredArgsConstructor
public class DataSeeder {

    private final UserRepository userRepo;
    private final PostRepository postRepo;
    private final TagRepository tagRepo;

    @Bean
    @Transactional
    CommandLineRunner seedData() {
        return args -> {
            if (userRepo.count() > 0) return;   // idempotent

            User ali = new User();
            ali.setUsername("ali");
            ali.setEmail("ali@test.com");
            ali.setFullName("Ali Ahmadi");
            ali.setStatus(User.UserStatus.ACTIVE);
            userRepo.save(ali);

            User sara = new User();
            sara.setUsername("sara");
            sara.setEmail("sara@test.com");
            sara.setFullName("Sara Rezaei");
            sara.setStatus(User.UserStatus.ACTIVE);
            userRepo.save(sara);

            Tag java = new Tag();  java.setName("java");
            Tag jpa  = new Tag();  jpa.setName("jpa");
            tagRepo.saveAll(Set.of(java, jpa));

            Post p1 = new Post();
            p1.setTitle("Intro to JPA");
            p1.setSlug("intro-to-jpa");
            p1.setContent("JPA is...");
            p1.setStatus(Post.PostStatus.PUBLISHED);
            p1.setViewCount(100L);
            ali.addPost(p1);
            p1.addTag(java);
            p1.addTag(jpa);
            postRepo.save(p1);

            Post p2 = new Post();
            p2.setTitle("Criteria API");
            p2.setSlug("criteria-api");
            p2.setContent("Criteria is...");
            p2.setStatus(Post.PostStatus.PUBLISHED);
            p2.setViewCount(50L);
            sara.addPost(p2);
            p2.addTag(java);
            postRepo.save(p2);
        };
    }
}
```
</div>
**مزایاش:**
- ✅ از JPA رد می‌شه → `@PrePersist` تاریخ می‌ذاره، `@Version` مقدار می‌ده، cascade کار می‌کنه
- ✅ refactor امن
- ✅ idempotent (چک `count() > 0`)
- ✅ type-safe (کامپایلر خطاها رو می‌گیره)
- ✅ `data.sql` رو حذف می‌کنی و از `defer-datasource-initialization` هم آزاد می‌شی

**در این حالت `data.sql` رو پاک کن** و `application.yml` رو ساده کن:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```yaml
spring:
  datasource:
    url: jdbc:h2:mem:blogdb;DB_CLOSE_DELAY=-1
    driver-class-name: org.h2.Driver
    username: sa
    password:
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: true
    properties:
      hibernate:
        format_sql: true
  h2:
    console:
      enabled: true
      path: /h2-console
```
</div>
---

## ۶. پس `defer-datasource-initialization` یا `DataSeeder`؟

| | `data.sql` + defer | `CommandLineRunner` |
|---|---|---|
| سرعت راه‌اندازی | سریع‌تر | کمی کندتر (از JPA رد می‌شه) |
| امنیت refactor | ❌ | ✅ |
| type-safety | ❌ | ✅ |
| برای production (Flyway/Liquibase) | مکمل خوب | مکمل خوب |
| برای dev/demo | ✅ | ✅ (پیشنهاد من) |

**پیشنهاد من:** برای شروع `defer-datasource-initialization` رو بذار تا پروژه بالا بیاد، بعداً که مطمئن شدی همه‌چی کار می‌کنه، بیا `DataSeeder` رو جایگزین کن.

---

## ۷. یه نکته مهم که ممکنه دوباره بهش بخوری

اگه `@Cacheable` روی `Tag` مونده و dependency `hibernate-jcache` رو نداری، بعد از فیکس `data.sql` ممکنه خطای جدیدی بگیری موقع ساخت `EntityManagerFactory`:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
Unable to create requested service [org.hibernate.cache.spi.CacheImplementor]
```
</div>
چون قبلاً هشدار دادم: **`@Cacheable` + `@Cache` روی `Tag` رو فعلاً بردار** مگه dependency اضافه کنی:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```java
@Entity
@Table(name = "tags")
// @Cacheable
// @Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Getter @Setter
public class Tag extends BaseEntity { ... }
```
</div>
بعداً که خواستی کش رو اضافه کنی، dependencyها رو بذار و برگردون.

---

خلاصه کارهای الان:

1. `spring.jpa.defer-datasource-initialization: true` اضافه کن
2. `@Cacheable` و `@Cache` رو از `Tag` موقتاً بردار
3. `mvn spring-boot:run`
4. اگه خطای جدید گرفتی، `Caused by:` کامل رو بفرست

اگه بعد از این fix بازم گیر کردی، **کل stack trace** (همه خطوط `Caused by:`) رو بفرست تا دقیق بگم. 🎯

</div>



<!-- SOURCE: 8.md -->
# File: 8.md

<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">


# علت اصلی: enum literal داخل JPQL ❌

خطا از این بخش میاد:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
findSummaries(org.springframework.data.domain.Pageable)
```
</div>
یعنی **هنوز نسخه قدیمی** `findSummaries` رو داری که enum literal توش هاردکد شده:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```java
@Query("""
    ...
    com.example.blog.dto.PostSummary.PostStatusView.PUBLISHED   // ← 💥
    ...
""")
```
</div>
JPQL/HQL نمی‌تونه `com.example.blog.dto.PostSummary.PostStatusView.PUBLISHED` رو به‌عنوان enum literal parse کنه (مشکل nested enum داخل record + syntax). به همین دلیل Spring موقع ساخت repository، validation query رو fail می‌کنه و کل زنجیره beanها می‌خوره.

پس باید نسخه fix شده رو کامل اعمال کنی. یه بار تمیز می‌دم:

---

## ۱. `PostSummary` رو ساده کن (enum رو حذف کن)

چون query همیشه `p.status = 'PUBLISHED'` رو فیلتر می‌کنه، فیلد `status` توی DTO **همیشه PUBLISHED** هست → **کاملاً اضافیه**.
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```java
package com.example.blog.dto;

public record PostSummary(
    Long id,
    String title,
    String slug,
    String authorName,
    Long commentCount
) {}
```
</div>
---

## ۲. `PostRepository.findSummaries` رو آپدیت کن
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```java
package com.example.blog.repository;

import com.example.blog.dto.PostSummary;
import com.example.blog.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom {

    Optional<Post> findBySlug(String slug);

    @Query("""
        SELECT DISTINCT p FROM Post p
        JOIN FETCH p.author
        LEFT JOIN FETCH p.tags
        WHERE p.status = 'PUBLISHED'
    """)
    List<Post> findAllPublishedWithDetails(Pageable pageable);

    @Query("SELECT p FROM Post p JOIN FETCH p.author WHERE p.id = :id")
    Optional<Post> findByIdWithAuthor(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Post p SET p.viewCount = p.viewCount + 1 WHERE p.id = :id")
    int incrementViewCount(@Param("id") Long id);

    // 👇 این رو کامل جایگزین کن
    @Query(
        value = """
            SELECT new com.example.blog.dto.PostSummary(
                p.id, p.title, p.slug, u.username,
                (SELECT COUNT(c) FROM Comment c WHERE c.post = p AND c.approved = true)
            )
            FROM Post p
            JOIN p.author u
            WHERE p.status = 'PUBLISHED'
            """,
        countQuery = """
            SELECT COUNT(p.id)
            FROM Post p
            WHERE p.status = 'PUBLISHED'
            """
    )
    Page<PostSummary> findSummaries(Pageable pageable);

    interface PostListView {
        Long getId();
        String getTitle();
        String getSlug();
        String getAuthorName();
    }

    @Query("SELECT p.id AS id, p.title AS title, p.slug AS slug, u.username AS authorName " +
           "FROM Post p JOIN p.author u WHERE p.status = 'PUBLISHED'")
    List<PostListView> findAllAsView();
}
```
</div>
**نکات این fix:**
- ✅ enum literal حذف شد
- ✅ `countQuery` صریح دادم — چون Spring Data نمی‌تونه از queryای که **subquery توی SELECT** داره، count query خودکار بسازه. بدون `countQuery`، بعد از bootstrap، موقع صدا زدن متد خطای runtime می‌گیری.
- ✅ subquery `(SELECT COUNT(c) ...)` توی SELECT در Hibernate 6 پشتیبانی می‌شه

---

## ۳. `PostRepositoryImpl.searchDynamic` رو هم آپدیت کن

چون `PostSummary` دیگه enum نداره، اون `cb.literal(...)` که قبلاً هشدار دادم هم باید حذف بشه:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```java
// بخش projection در searchDynamic
cq.select(cb.construct(
    PostSummary.class,
    post.get("id"),
    post.get("title"),
    post.get("slug"),
    author.get("username"),
    commentCount.getSelection()
    // ← دیگه cb.literal(...) نداریم
));
```
</div>
بقیه کد `searchDynamic` دست نخورده می‌مونه.

---

## ۴. همه‌جاهایی که `PostSummary` رو استفاده می‌کنی چک کن

اگه توی `PostService` یا هر جای دیگه‌ای `PostSummary.PostStatusView` رو استفاده کردی، حذفش کن. توی `PostService` که من نوشتم، `findSummaries` مستقیم صدا زده نمی‌شد و `searchDynamic` هم enum نداشت، پس نباید جایی دیگه مشکل باشه. ولی یه سرچ بزن:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```bash
grep -r "PostStatusView" src/
```
</div>
اگه چیزی پیدا شد، حذف کن.

---

## ۵. اگه بازم می‌خوای enum رو داشته باشی (جایگزین)

اگه واقعاً enum تو DTO لازم داری (مثلاً بعداً می‌خوای statusهای مختلف برگردونی)، **نباید literal بنویسی** — باید parameter بدی:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```java
@Query("""
    SELECT new com.example.blog.dto.PostSummary(
        p.id, p.title, p.slug, u.username,
        (SELECT COUNT(c) FROM Comment c WHERE c.post = p AND c.approved = true),
        :status
    )
    FROM Post p JOIN p.author u
    WHERE p.status = :status
""")
Page<PostSummary> findSummaries(
    @Param("status") Post.PostStatus status,
    Pageable pageable
);
```

</div>

و صدا زدن:

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```java
 postRepository.findSummaries(Post.PostStatus.PUBLISHED, pageable);
```
</div>
ولی چون query ما همیشه published برمی‌گردونه، **پیشنهاد من همون حذف enum هست** — ساده‌تر، بدون بدهی فنی.

---

## چرا این اتفاق افتاد؟

خلاصه‌ی درس:

| کار | نتیجه |
|---|---|
| `:param` توی JPQL | ✅ همه‌جا کار می‌کنه (enum, string, number) |
| `'PUBLISHED'` به‌عنوان string literal | ✅ برای فیلدهای `@Enumerated(STRING)` هم کار می‌کنه (Hibernate تبدیل می‌کنه) |
| `com.example.X.Y.Z` به‌عنوان enum literal | ⚠️ فقط برای enumهای **top-level** و با شرایط خاص؛ nested enum داخل record **نمی‌شه** |
| `cb.literal(...)` توی Criteria برای enum | ⚠️ ناسالم؛ از `cb.parameter(...)` استفاده کن |

---

بعد از این fix، `mvn spring-boot:run` بزن. اگه بازم خطا گرفتی، **کل stack trace** رو (همه `Caused by:`ها) بفرست. 🎯

</div>



<!-- SOURCE: 9.md -->
# File: 9.md

<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">


`EntityGraph` یکی از مفاهیم JPA 2.1 است که خیلی از توسعه‌دهنده‌ها ازش غافل می‌مونن، در حالی که راه تمیزی برای حل N+1 و کنترل دقیق بارگذاری روابط فراهم می‌کنه. در ادامه همه‌چیز رو با مثال و نکات ریز بررسی می‌کنیم.

---

## ۱. EntityGraph دقیقاً چیه و چرا به وجود اومد؟

`EntityGraph` (که بهش **fetch plan** هم می‌گن) یه مکانیزم **اعلانی (declarative)** برای مشخص کردن اینه که **کدوم ویژگی‌های یه entity باید در همون `SELECT` اولیه بارگذاری بشن**.

### مشکلی که حل می‌کنه

فرض کن `Post` داری با `author` (ManyToOne) و `comments` (OneToMany). به‌صورت پیش‌فرض:
- `author` → **EAGER** (چون ManyToOne)
- `comments` → **LAZY** (چون OneToMany)

حالا اگه تو یه query لیست پست‌ها رو بگیری و بعد به `comments` هر پست دسترسی پیدا کنی → **N+1** می‌خوری.

**راه‌حل سنتی:** `JOIN FETCH` توی JPQL.
**راه‌حل EntityGraph:** بدون دست زدن به JPQL، فقط با یه annotation یا یه graph object به JPA می‌گی «این ویژگی‌ها رو با هم توی یه query بیار». JPA خودش `JOIN FETCH` رو تولید می‌کنه.

**مزیت اصلی نسبت به `JOIN FETCH`:** همون JPQL می‌تونه برای سناریوهای مختلف **fetch planهای متفاوت** داشته باشه، بدون اینکه query عوض بشه. یعنی یه متد repository می‌تونی داشته باشی که گاهی با graph کامل و گاهی بدون graph صدا زده بشه.

---

## ۲. دو نوع رفتار: Fetch Graph vs Load Graph

وقتی یه `EntityGraph` رو به JPA می‌دی، باید مشخص کنی که **نسبت به ویژگی‌هایی که توی graph نیستن** چه رفتاری داشته باشه. اینجا دو تا hint وجود داره:

| | **Fetch Graph** (`jakarta.persistence.fetchgraph`) | **Load Graph** (`jakarta.persistence.loadgraph`) |
|---|---|---|
| ویژگی‌های داخل graph | `EAGER` | `EAGER` |
| ویژگی‌های خارج از graph | **`LAZY`** (حتی اگه توی mapping `EAGER` باشن) | **همون `FetchType` پیش‌فرض/تعریف‌شده** |
| کاربرد | fetch plan دقیق و حداقلی | fetch plan «پیش‌فرض + اضافه‌ها» |

**Fetch Graph پیش‌فرضه** و دقیق‌تره: هر چیزی که توی graph نگفتی، lazy می‌شه — حتی اگه تو entity با `@ManyToOne(fetch = EAGER)` تعریف کرده باشی.

**Load Graph** امن‌تره برای وقتی که نمی‌خوای mappingهای موجود رو override کنی: فقط ویژگی‌های اضافی رو EAGER می‌کنه و بقیه رو دست نمی‌زنه.

---

## ۳. روش‌های تعریف EntityGraph

### الف) با `@EntityGraph` در Spring Data JPA (رایج‌ترین و ساده‌ترین)

Spring Data یه annotation به اسم `@EntityGraph` داره که با `attributePaths` کار می‌کنه و خودش `LEFT JOIN FETCH` تولید می‌کنه:
</div>


```java
public interface PostRepository extends JpaRepository<Post, Long> {

    // بارگذاری author و comments در یک query
    @EntityGraph(attributePaths = {"author", "comments"})
    @Query("SELECT p FROM Post p WHERE p.status = 'PUBLISHED'")
    List<Post> findPublishedWithDetails();

    // حتی روی متدهای derived هم کار می‌کنه
    @EntityGraph(attributePaths = {"author"})
    Optional<Post> findBySlug(String slug);
}
```
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">


**نکته مهم:** `@EntityGraph(attributePaths = {})` (خالی) یعنی «هیچ چیزی رو eager نکن» — می‌تونی ازش برای override کردن eagerهای ناخواسته استفاده کنی.

### ب) با `@NamedEntityGraph` روی Entity (قابل استفاده مجدد)
</div>


```java
@Entity
@NamedEntityGraph(
    name = "Post.withAuthorAndTags",
    attributeNodes = {
        @NamedAttributeNode("author"),
        @NamedAttributeNode("tags")
    }
)
public class Post extends BaseEntity {
    // ...
}
```
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">


و توی repository:
</div>


```java
@EntityGraph(value = "Post.withAuthorAndTags", type = EntityGraph.EntityGraphType.LOAD)
Optional<Post> findWithDetailsBySlug(String slug);
```
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">


**مزیت:** graph توی خود entity تعریف می‌شه و از هر جای پروژه قابل استفاده‌ست. اگه از `@NamedEntityGraph` بدون `type` استفاده کنی، پیش‌فرض `FETCH` هست.

### ج) به‌صورت برنامه‌نویسی (Ad-hoc) با `EntityManager`
</div>


```java
EntityGraph<Post> graph = em.createEntityGraph(Post.class);
graph.addAttributeNodes("author");
graph.addSubgraph("comments").addAttributeNodes("user");

Map<String, Object> hints = Map.of("jakarta.persistence.fetchgraph", graph);
Post post = em.find(Post.class, 1L, hints);
```
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">


یا روی query:
</div>


```java
TypedQuery<Post> query = em.createQuery("SELECT p FROM Post p", Post.class);
query.setHint("jakarta.persistence.fetchgraph", graph);
List<Post> posts = query.getResultList();
```
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">


این روش وقتی به‌درد می‌خوره که fetch plan رو **داینامیک** بر اساس شرایط می‌خوای بسازی.

---

## ۴. Subgraph — وقتی رابطه‌ها تو در تو می‌شن

فرض کن `Post → comments → user` رو می‌خوای. `comments` خودش یه relationship هست، و `user` داخل `comments` یه relationship دیگه. اینجا **subgraph** وارد می‌شه.

### با `@NamedSubgraph`
</div>


```java
@Entity
@NamedEntityGraph(
    name = "Post.withCommentsAndUsers",
    attributeNodes = @NamedAttributeNode(value = "comments", subgraph = "commentUsers"),
    subgraphs = @NamedSubgraph(
        name = "commentUsers",
        attributeNodes = @NamedAttributeNode("user")
    )
)
public class Post { ... }
```
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">


### با `attributePaths` و dot notation (خیلی ساده‌تر)

Spring Data از **dot notation** پشتیبانی می‌کنه:
</div>


```java
@EntityGraph(attributePaths = {"comments.user"})
List<Post> findWithCommentsAndUsers();
```
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">


این یعنی: `comments` رو fetch کن، و از داخل هر comment، `user` رو هم fetch کن.

### با `addSubgraph` برنامه‌نویسی
</div>


```java
EntityGraph<Post> graph = em.createEntityGraph(Post.class);
Subgraph<Comment> commentGraph = graph.addSubgraph("comments");
commentGraph.addAttributeNodes("user");
```
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">


---

## ۵. EntityGraph vs JOIN FETCH — کدوم رو انتخاب کنم؟

| | **`JOIN FETCH`** | **`@EntityGraph`** |
|---|---|---|
| سطح | JPQL (داخل query) | Repository (اعلانی) |
| نوع JOIN تولیدشده | معمولاً `INNER JOIN` | `LEFT OUTER JOIN` |
| تغییر fetch plan بدون تغییر query | ❌ | ✅ |
| خوانایی query | کمتر (query شلوغ می‌شه) | بیشتر |
| کنترل دقیق روی JPQL | ✅ | محدود |
| مناسب برای | queryهای یک‌بارمصرف | fetch planهای تکراری در متدهای مختلف |

**قاعده عملی:** اگه همون entity رو توی متدهای مختلف با fetch planهای متفاوت لازم داری → `@EntityGraph`. اگه یه query خاص و یگانه داری → `JOIN FETCH` مستقیم تمیزتره.

**نکته مهم:** `@EntityGraph` در Spring Data عملاً همون `LEFT JOIN FETCH` رو تولید می‌کنه. پس از نظر performance، تفاوت بنیادینی با `JOIN FETCH` ندارن؛ تفاوت در **سبک بیان** و **قابلیت استفاده مجدد** هست.

---

## ۶. دام‌ها و pitfalls مهم (اینجا دقت کن!)

### ⚠️ دام ۱: Pagination + collection در EntityGraph → pagination در حافظه

اگه `@EntityGraph` رو روی یه متد `Pageable` بذاری و collection رو fetch کنی (مثلاً `comments`)، Hibernate نمی‌تونه `LIMIT/OFFSET` رو توی SQL اعمال کنه چون JOIN ردیف‌ها رو زیاد می‌کنه. نتیجه:
</div>


```
HHH000104: firstResult/maxResults specified with collection fetch; applying in memory
```
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">


یعنی **همه ردیف‌ها از DB میان و pagination توی حافظه انجام می‌شه** — فاجعه برای جدول‌های بزرگ.

**راه‌حل:** pagination روی root entity بدون fetch collection، بعد یه query دوم برای collection (یا استفاده از `@BatchSize`).

### ⚠️ دام ۲: دو collection در یک EntityGraph → `MultipleBagFetchException`

اگه دو تا `List` (bag collection) رو همزمان بخوای fetch کنی (مثلاً `comments` و `tags`)، Hibernate استثنا می‌ده:
</div>


```
org.hibernate.loader.MultipleBagFetchException
```
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">


چون JOIN دو collection باعث **Cartesian product انفجاری** می‌شه. Hibernate جلوش رو می‌گیره.

**راه‌حل:**
- هر collection رو توی **یه query جدا** fetch کن (همون session، persistence context stitch می‌کنه)
- یا collection رو از `List` به `Set` تغییر بده (bag نباشه)
- یا از `@BatchSize` استفاده کن

### ⚠️ دام ۳: `@Basic` attributes و EntityGraph

JPA spec می‌گه fetch graph باید روی **همه** attributeها (حتی `@Basic`) اثر بذاره، ولی **Hibernate عملاً `@Basic` رو نادیده می‌گیره** مگر اینکه bytecode enhancement فعال باشه.

یعنی اگه یه فیلد `@Lob` یا `String` بزرگ داری و می‌خوای با EntityGraph لودش نکنی، Hibernate ممکنه بازم لودش کنه. **EntityGraph بیشتر برای روابط (associations) ابزار خوبیه، نه برای basic fieldها.**

### ⚠️ دام ۴: `@OneToOne` دوطرفه و lazy

`@OneToOne` سمت معکوس (`mappedBy`) به‌سختی lazy می‌شه چون Hibernate باید بدونه `null` هست یا نه. EntityGraph می‌تونه کمک کنه ولی رفتارش بین providerها و نسخه‌ها متفاوته.

---

## ۷. کی از EntityGraph استفاده نکنم؟

- **وقتی projection کافیه:** اگه فقط چند فیلد scalar می‌خوای، DTO projection بهتره — نه entity کامل لود می‌شه نه lazy state داره.
- **وقتی collection بزرگ داری:** fetch کردن collection بزرگ با EntityGraph می‌تونه memory و performance رو خراب کنه. `@BatchSize` امن‌تره.
- **وقتی pagination روی collection لازم داری:** همون‌طور که گفتیم، از EntityGraph برای این کار استفاده نکن.

---

## ۸. خلاصه و قاعده طلایی

| سناریو | ابزار پیشنهادی |
|---|---|
| یه relationship کوچیک، بدون pagination | `@EntityGraph` یا `JOIN FETCH` |
| همون entity با fetch planهای مختلف | `@EntityGraph` (به‌خاطر قابلیت reuse) |
| collection بزرگ + pagination | `@BatchSize` (نه EntityGraph) |
| دو collection همزمان | دو query جدا (نه دو EntityGraph fetch) |
| فقط چند فیلد scalar | DTO projection |

**قاعده طلایی:** همیشه `FetchType` پیش‌فرض رو `LAZY` بذار و هر جا نیاز شد با `@EntityGraph` یا `JOIN FETCH` صریح fetch کن. EntityGraph ابزار «fetch دقیق بر اساس use case» هست، نه راهی برای EAGER کردن همه‌چیز.

---

اگه بخوای، می‌تونیم این رو روی همون پروژه وبلاگ پیاده کنیم — مثلاً `PostRepository` رو طوری تغییر بدیم که `findBySlug` با EntityGraph هم `author` هم `comments.user` رو در یک query بیاره، و بعد با `generate_statistics` تعداد queryها رو قبل/بعد مقایسه کنیم. 🎯

</div>



<!-- SOURCE: 1.md -->
# File: 1.md

<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">


چرخهٔ حیات Entity در JPA به وضعیت آن نسبت به **Persistence Context** (که با `EntityManager` مدیریت می‌شود) بستگی دارد، نه فقط به وجود یا نبود رکورد در دیتابیس. چهار وضعیت اصلی:


| وضعیت | در Persistence Context؟ | رکورد در DB؟ | تغییرات Track می‌شود؟ | توضیح |
|---|---|---|---|---|
| **New / Transient** | نه | معمولاً نه | نه | با `new` ساخته شده و هنوز به EntityManager معرفی نشده |
| **Managed / Persistent** | بله | معمولاً بله | بله | تحت مدیریت EntityManager؛ Dirty Checking فعال |
| **Detached** | نه | معمولاً بله | نه | قبلاً Managed بوده ولی از Persistence Context خارج شده |
| **Removed** | بله تا flush | هنوز بله تا flush | — | برای حذف علامت‌گذاری شده و در flush حذف می‌شود |

## 1. New / Transient
- با `new Entity()` ساخته می‌شود.
- در Persistence Context نیست.
- JPA آن را Track نمی‌کند؛ تغییراتش خودکار ذخیره نمی‌شود.
- ممکن است ID هنوز تولید نشده باشد.
- انتقال به Managed با `em.persist(entity)` انجام می‌شود.
- INSERT معمولاً در زمان flush یا commit انجام می‌شود؛ زمان دقیق تولید ID به استراتژی تولید ID بستگی دارد.
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```java
User u = new User();   // New/Transient
em.persist(u);         // Managed
```
</div>

## 2. Managed / Persistent
- Entity در Persistence Context قرار دارد.
- JPA تغییرات آن را ردیابی می‌کند (Dirty Checking) و در flush به‌صورت UPDATE/INSERT اعمال می‌کند.
- `find()`، `getReference()`، query و `persist()` می‌توانند Entity را Managed کنند.
- اگر همان Entity دوباره با `find()` خوانده شود، معمولاً همان نمونهٔ داخل Persistence Context برگردانده می‌شود (Identity Map).
- خروج از این وضعیت با `detach()`، `clear()`، `close()` یا پایان Transaction (در Persistence Context از نوع transaction-scoped) رخ می‌دهد.
- `remove()` آن را به Removed می‌برد.
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```java
User u = em.find(User.class, 1L); // Managed
u.setName("Ali");                 // Dirty Checking → UPDATE در flush
```
</div>


## 3. Detached
- قبلاً Managed بوده ولی دیگر در Persistence Context نیست.
- معمولاً ID دارد و رکوردش در DB وجود دارد.
- تغییرات آن به‌صورت خودکار Track و ذخیره نمی‌شود.
- Lazy Loading ممکن است در این وضعیت با خطا مواجه شود، مثل `LazyInitializationException`.
- برای برگرداندن به Managed از `merge()` استفاده می‌شود.

نکتهٔ مهم:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```java
User managed = em.merge(detachedUser);
```
</div>


`merge()` خود شیء detached را Managed نمی‌کند؛ بلکه یک **کپی Managed** برمی‌گرداند. شیء اصلی همچنان Detached می‌ماند.

- `remove()` روی Entity Detached معمولاً `IllegalArgumentException` می‌دهد.
- `persist()` روی Entity Detached روش درستی نیست و ممکن است `EntityExistsException` یا خطای مشابه بدهد.

## 4. Removed
- با `em.remove(managedEntity)` ایجاد می‌شود.
- Entity هنوز تا زمان flush در Persistence Context است، ولی برای حذف علامت‌گذاری شده.
- در flush یا commit، DELETE اجرا می‌شود.
- بعد از commit، Entity دیگر Managed نیست و عملاً Detached/حذف‌شده محسوب می‌شود.
- `persist()` روی Entity Removed می‌تواند آن را دوباره Managed کند، ولی این کار معمولاً توصیه نمی‌شود.
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```java
User u = em.find(User.class, 1L); // Managed
em.remove(u);                      // Removed
// در flush/commit: DELETE FROM ...
```
</div>



## نمودار سادهٔ انتقال‌ها
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```text
New/Transient
    │ persist()
    ▼
Managed ────────────── remove() ─────────────► Removed
    │                                             │
    │ detach()/clear()/close()/پایان tx           │ flush/commit
    ▼                                             ▼
Detached ◄────────────────────────────────── حذف از DB
    │
    │ merge()
    ▼
Managed (نمونهٔ کپی‌شده)
```
</div>


## چند نکتهٔ کلیدی
- وضعیت Entity همیشه نسبت به یک `EntityManager` و Persistence Context مشخص معنا دارد.
- `flush()` فقط Persistence Context را با DB همگام می‌کند؛ Transaction را commit نمی‌کند.
- `clear()` همهٔ Entityها را Detached می‌کند.
- `close()` EntityManager را می‌بندد و Entityهای Managed آن Detached می‌شوند.
- `cascade` می‌تواند این انتقال‌ها را به Entityهای مرتبط هم منتقل کند؛ مثلاً `CascadeType.PERSIST` یا `CascadeType.REMOVE`.
- `orphanRemoval` مکانیزم دیگری برای حذف Entityهای وابسته است و با چرخهٔ Removed مستقیم یکی نیست.
</div>




<!-- SOURCE: 10.md -->
# File: 10.md

<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">


**MDB (Message-Driven Bean)** یکی از انواع Enterprise JavaBeans (EJB) در Java EE / Jakarta EE است که برای پردازش پیام‌ها به‌صورت غیرهم‌زمان (Asynchronous) استفاده می‌شود.

**JMS (Java Message Service)** نیز یک API در جاوا است که امکان ارسال و دریافت پیام بین بخش‌های مختلف یک سیستم را فراهم می‌کند.

این دو تکنولوژی معمولاً در کنار یکدیگر استفاده می‌شوند.

## **1. MDB چیست؟**

MDB یک Bean در سمت سرور است که منتظر دریافت پیام می‌ماند و به‌محض دریافت پیام، آن را پردازش می‌کند.

برای مثال فرض کن یک فروشگاه اینترنتی داری:

1. کاربر سفارش ثبت می‌کند.
2. سیستم یک پیام به Message Queue ارسال می‌کند.
3. MDB پیام را دریافت می‌کند.
4. MDB عملیات ارسال ایمیل، ثبت سفارش یا پردازش پرداخت را انجام می‌دهد.
5. کاربر مجبور نیست منتظر تمام این عملیات بماند.

[A Java EE Application That Uses the JMS API with a Session Bean](https://images.openai.com/static-rsc-4/JbicG_t1ToQr03ejsj8xFSnZCfjVs0lg7JVLS9m_rOmpiFjRtY3MIFkMPZXANuB15NBcETERl-OdLlfEBxH1R1s8ZmXIcf9oCQk8olScSK2vG1waXi-DbsIRO7z8jgebMuo0hpQfhyMhX2d3KeO5TAvODi3xZuS-A-cdpBVgbGQ?purpose=inline)

[minitorn.tlu.ee](https://minitorn.tlu.ee/~jaagup/kool/java/abiinfo/javaeetutorial5/doc/JMSJ2EEex2.html?utm_source=chatgpt.com)

یک نمونه ساده از MDB:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
import jakarta.ejb.MessageDriven;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import jakarta.jms.TextMessage;

@MessageDriven
public class OrderMDB implements MessageListener {

    @Override
    public void onMessage(Message message) {
        try {
            TextMessage textMessage = (TextMessage) message;

            String order = textMessage.getText();

            System.out.println("Received: " + order);

            // Process order

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```
</div>


در این مثال، متد `onMessage()` زمانی اجرا می‌شود که یک پیام جدید دریافت شود.

## **2. JMS چیست؟**

JMS مخفف Java Message Service است.

JMS یک استاندارد برای ارتباط غیرهم‌زمان بین اجزای مختلف نرم‌افزار است.

دو مدل اصلی در JMS وجود دارد:

### **A. Point-to-Point (Queue)**

در این مدل پیام از طریق Queue ارسال می‌شود و معمولاً یک Consumer آن را دریافت و پردازش می‌کند.

[Empowering Distributed Communication with Spring JMS: A Comprehensive Guide | by Tanvir Khan | Medium](https://images.openai.com/static-rsc-4/t_fGZz2E7pXGOvTErCQWhVYH5iRIuyXbazWHpxpvMMtWYYv0bQIKyj1boXb8IMVu-6fOsehX54OxHbPxzsuwbARHtl43gziZ20dTR3llOQCIxv3plzkIh-rcC3PHW3V-g_HfElej4j7I7TBvOstlkYclKjDeJhZovkwyjaO5zn8?purpose=inline)

[aeontanvir.medium.com](https://aeontanvir.medium.com/empowering-distributed-communication-with-spring-jms-a-comprehensive-guide-f7e0f041c8d5?utm_source=chatgpt.com)

مثال:

- سیستم سفارش‌گیری پیام ارسال می‌کند.
- سیستم انبارداری پیام را دریافت می‌کند.
- موجودی کالا را بررسی می‌کند.

### **B. Publish / Subscribe (Topic)**

در این مدل یک Producer پیام را به Topic ارسال می‌کند و چند Subscriber می‌توانند آن پیام را دریافت کنند.

[Understanding WebLogic JMS](https://images.openai.com/static-rsc-4/cKiB2uX7jKYf2Ed2hUZpu5ovCUJNf6owTP1DHHKXKMTg76tvhdmMvGY5ruWY5DkAdvPZMdg7FWBDxwzD9iYj-B8p7cFcm6YcB8fotVhqN4f73jhVkipqfRfALfArYZ86bqdkvLtGtfGI6kFP2nsyPcAvd3elAAlACWL9qpp2SEc?purpose=inline)

[docs.oracle.com](https://docs.oracle.com/en/middleware/fusion-middleware/weblogic-server/14.1.2/jmspg/overview.html?utm_source=chatgpt.com)

مثال:

وقتی یک سفارش ثبت می‌شود:

- سرویس ایمیل پیام را دریافت می‌کند.
- سرویس انبارداری پیام را دریافت می‌کند.
- سرویس گزارش‌گیری پیام را دریافت می‌کند.

همه می‌توانند از یک پیام مطلع شوند.

## **3. تفاوت MDB و JMS چیست؟**

| ویژگی                          | JMS                 | MDB                     |
| ------------------------------ | ------------------- | ----------------------- |
| نوع                            | API و استاندارد     | نوعی EJB                |
| کاربرد                         | ارسال و دریافت پیام | پردازش پیام             |
| ارسال پیام                     | بله                 | خیر، وظیفه اصلی آن نیست |
| دریافت پیام                    | از طریق Consumer    | به‌صورت خودکار          |
| اجرای غیرهم‌زمان               | پشتیبانی می‌کند     | بله                     |
| مدیریت توسط Application Server | بسته به Provider    | بله                     |

نکته مهم: MDB خودش جایگزین JMS نیست؛ بلکه یکی از روش‌های مصرف پیام‌های JMS است.

## **4. چه زمانی از JMS استفاده می‌کنیم؟**

زمانی از JMS استفاده می‌کنیم که:

- نیاز به ارتباط غیرهم‌زمان بین سرویس‌ها داشته باشیم.
- نخواهیم یک سرویس منتظر پاسخ سرویس دیگر بماند.
- بخواهیم بین سیستم‌های مختلف ارتباط برقرار کنیم.
- حجم زیادی از درخواست‌ها را با Queue مدیریت کنیم.
- بخواهیم Producer و Consumer مستقل از یکدیگر باشند.
- بخواهیم پردازش پیام‌ها حتی در صورت قطع موقت Consumer به تعویق بیفتد.

## **5. تفاوت ارتباط Synchronous و Asynchronous**

فرض کن کاربری سفارشی ثبت می‌کند.

**روش Synchronous (REST API):**

User

HTTP Request

Order Service

Wait for response

Payment Service

Response

User receives result

**روش Asynchronous (JMS + MDB):**

User

Order Service

Send message

JMS Queue

MDB

Process Payment

User does not need to wait for payment processing.

## **6. چه زمانی JMS + MDB انتخاب خوبی نیست؟**

اگر:

- ارتباط ساده و مستقیم بین دو سرویس کافی است.
- نیاز به دریافت فوری پاسخ از سرویس مقصد داری.
- سیستم کوچک است و پیچیدگی Message Broker توجیه ندارد.

در این شرایط REST API ممکن است انتخاب بهتری باشد.

## **7. مثال واقعی در محیط Enterprise**

فرض کن با Java EE و WildFly یا JBoss کار می‌کنی.

می‌توانی از این ترکیب استفاده کنی:

- Java EE / Jakarta EE
- JMS
- MDB
- ActiveMQ Artemis
- WildFly Application Server

در این معماری، Producer پیام را به Queue می‌فرستد و MDB آن را دریافت و پردازش می‌کند.

**یک سؤال برای درک عمیق‌تر:**

آیا دوست داری در مرحله بعد یک پروژه عملی با Java و WildFly پیاده‌سازی کنیم که شامل این موارد باشد؟

1. ایجاد JMS Queue
2. نوشتن یک JMS Producer
3. نوشتن یک MDB Consumer
4. ارسال پیام و مشاهده پردازش آن به‌صورت Asynchronous

این پروژه کمک می‌کند تفاوت JMS و MDB را کاملاً در عمل درک کنی.
</div>




<!-- SOURCE: 11.md -->
# File: 11.md

<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">

# **تفاوت EclipseLink و Hibernate**

**Hibernate** و **EclipseLink** هر دو ORM (Object-Relational Mapping) هستند؛ یعنی به ما اجازه می‌دهند Objectهای Java را به جدول‌های دیتابیس Mapping کنیم و بدون نوشتن SQL برای تمام عملیات CRUD، با Entityها کار کنیم.

هر دو از استاندارد **JPA (Java Persistence API)** پشتیبانی می‌کنند. تفاوت اصلی در پیاده‌سازی، امکانات اختصاصی، رفتارهای Performance، اکوسیستم و نحوه استفاده در پروژه‌هاست.

# **1. JPA چیست و ارتباط آن با Hibernate و EclipseLink چیست؟**

ابتدا باید این سه مفهوم را از هم تفکیک کنیم.

**JPA / Jakarta Persistence**

Specification — استاندارد ORM در Java

**Hibernate**

JPA Implementation

**EclipseLink**

JPA Implementation

JPA خودش ORM نیست که مستقلاً Query اجرا کند؛ یک Specification است که API و رفتارهای استاندارد Persistence را تعریف می‌کند.

Hibernate و EclipseLink، این استاندارد را پیاده‌سازی می‌کنند.

مثلاً این کد:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
@Entity
public class Employee {

    @Id
    private Long id;

    private String name;

    private BigDecimal salary;
}
```
</div>


از نظر استاندارد JPA است و در هر دو Provider قابل استفاده است.

# **2. Hibernate چیست؟**

[Hibernate Logo PNG Vector (SVG) Free Download](https://images.openai.com/static-rsc-4/qQ5hXqPfpE0cIHEb6v15ABi9dbSgmRX2NOifib4FufWPT-mXM0dETe5SCVpH0ktj7ufUZB1uycAzjL40dLzrtfTHASjDl06jI01915yWzOWIdZjKQcDuFWGmNQdsDzYD5M5vwlf9XqKoyduuc8g9VEDIR49FogcFWjAx-gg2Ot4?purpose=inline)

Hibernate ORM

Hibernate یکی از محبوب‌ترین ORMهای Java است که امکاناتی مثل:

- Object Relational Mapping
- مدیریت Entityها
- Lazy Loading
- Dirty Checking
- Transaction Integration
- HQL
- Criteria API
- Caching
- Batch Processing

را ارائه می‌دهد.

Hibernate علاوه بر JPA، APIها و امکانات اختصاصی خودش را نیز دارد.

مثلاً:

- Hibernate Session
- HQL
- Hibernate-specific annotations
- Second-level cache integration
- امکانات تخصصی Fetching و Batch

در پروژه‌های Spring Boot، Hibernate معمولاً Provider پیش‌فرض Spring Data JPA است.

# **3. EclipseLink چیست؟**

[PPT - EclipseLink Runtime Architecture PowerPoint Presentation, free download - ID:5436366](https://images.openai.com/static-rsc-4/73bdRnYHDOS-Qtbaj_81wl85VI2jBNAcktuyD7O6vbz-g-Ewgwbuv_2O6F3LEZdDSVq_-lzntUnKlvoWXtEU9JSsZ65um8wDJhX59I5DuXmOa3MBjMaqtppt_q2XO-yZGhHRsreXGhKIbVCoS7mWdFPN8YzRxGK8YJCAADFhasQ?purpose=inline)

EclipseLink

EclipseLink یک ORM Framework است که توسط Eclipse Foundation توسعه داده می‌شود و پیاده‌سازی Jakarta Persistence محسوب می‌شود.

EclipseLink قابلیت‌هایی مانند:

- JPA
- ORM
- JPQL
- Lazy Loading
- Caching
- Database Mapping
- Advanced Querying
- Performance Optimization

را ارائه می‌دهد.

EclipseLink همچنین در اکوسیستم Jakarta EE شناخته‌شده است و در بعضی Application Serverها و پروژه‌های Enterprise مورد استفاده قرار می‌گیرد.

# **4. تفاوت‌های اصلی Hibernate و EclipseLink**

| ویژگی                  | Hibernate                       | EclipseLink                       |
| ---------------------- | ------------------------------- | --------------------------------- |
| نوع                    | ORM Framework                   | ORM Framework                     |
| پیاده‌سازی JPA         | بله                             | بله                               |
| پشتیبانی JPQL          | بله                             | بله                               |
| پشتیبانی Criteria API  | بله                             | بله                               |
| Lazy Loading           | بله                             | بله                               |
| Dirty Checking         | بله                             | بله                               |
| First-level Cache      | بله                             | بله                               |
| Second-level Cache     | بله                             | بله                               |
| امکانات اختصاصی        | زیاد و گسترده                   | امکانات اختصاصی خودش              |
| محبوبیت در Spring Boot | بسیار زیاد                      | کمتر                              |
| استفاده در Jakarta EE  | بله                             | بله                               |
| HQL                    | دارد                            | ندارد (HQL اختصاصی Hibernate است) |
| API اختصاصی            | Hibernate API                   | EclipseLink API                   |
| Performance            | وابسته به Query و Configuration | وابسته به Query و Configuration   |

نکته: هیچ‌کدام همیشه سریع‌تر از دیگری نیست. Performance به Queryها، Mapping، حجم داده، Index، Fetch Strategy، Connection Pool و تنظیمات بستگی دارد.

# **5. تفاوت در API**

یکی از مهم‌ترین تفاوت‌ها این است که اگر از API استاندارد JPA استفاده کنی، کدت تا حد زیادی مستقل از ORM Provider خواهد بود.

## **کد استاندارد JPA**
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
@Repository
public class EmployeeRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public Employee findById(Long id) {
        return entityManager.find(Employee.class, id);
    }

    public void save(Employee employee) {
        entityManager.persist(employee);
    }
}
```
</div>


این کد هم با Hibernate و هم با EclipseLink قابل اجرا است.

چرا؟

چون `EntityManager`، `@PersistenceContext`، `@Entity` و `@Id` از استاندارد Persistence هستند، نه API اختصاصی Hibernate.

## **کد اختصاصی Hibernate**

```
Session session = entityManager.unwrap(Session.class);

Employee employee = session.get(Employee.class, 100L);
```

در اینجا:

```
Session
```

API اختصاصی Hibernate است.

اگر Provider را به EclipseLink تغییر دهی، این قسمت از کد مستقیماً قابل استفاده نخواهد بود.

# **6. HQL در Hibernate و JPQL در JPA**

یکی از سؤال‌های مهم مصاحبه.

### **JPQL**

JPQL زبان Query استاندارد JPA است.
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
TypedQuery<Employee> query =
    entityManager.createQuery(
        "SELECT e FROM Employee e WHERE e.salary > :salary",
        Employee.class
    );

query.setParameter("salary", 5000);

List<Employee> employees = query.getResultList();
```
</div>


این Query مستقل از Provider است و می‌تواند در Hibernate یا EclipseLink اجرا شود.

### **HQL**

HQL یا Hibernate Query Language، زبان Query اختصاصی Hibernate است.

HQL در بسیاری از موارد مشابه JPQL است، اما Hibernate ممکن است Syntax و قابلیت‌های اختصاصی بیشتری داشته باشد که وابستگی به Hibernate ایجاد می‌کنند.

نکته مصاحبه:

JPQL برای Portability بهتر است؛ HQL زمانی مفید است که بخواهیم از امکانات اختصاصی Hibernate استفاده کنیم.

# **7. تفاوت در Cache**

هر دو Provider از Cache پشتیبانی می‌کنند.

## **First-Level Cache**

First-Level Cache در سطح Persistence Context است.

مثلاً:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
Employee employee1 =
    entityManager.find(Employee.class, 100L);

Employee employee2 =
    entityManager.find(Employee.class, 100L);
```
</div>


اگر Persistence Context یکسان باشد، معمولاً در فراخوانی دوم، Entity از همان Context بازگردانده می‌شود و Query جدیدی به دیتابیس ارسال نمی‌شود.

First-Level Cache در Hibernate و EclipseLink وجود دارد و بخشی از رفتار Persistence Context استاندارد است.

## **Second-Level Cache**

Second-Level Cache بین Persistence Contextها به اشتراک گذاشته می‌شود و می‌تواند در Cache کردن Entityها کمک کند.

هر دو Provider از Second-Level Cache پشتیبانی می‌کنند، اما:

- نحوه Configuration متفاوت است.
- Cache Providerها و Integrationها متفاوت هستند.
- Cache Strategyها و جزئیات پیاده‌سازی می‌توانند متفاوت باشند.

در Hibernate معمولاً Integration با Cacheهایی مانند JCache یا Providerهای سازگار بررسی می‌شود.

در EclipseLink نیز امکانات Cache داخلی و تنظیمات Cache خودش وجود دارد.

# **8. تفاوت در Performance**

یکی از اشتباهات رایج در مصاحبه این است که بگوییم:

> Hibernate همیشه سریع‌تر است.

یا:

> EclipseLink همیشه برای Enterprise بهتر است.

این جملات درست نیستند.

Performance هر ORM به موارد زیر بستگی دارد:

- نوع Query
- تعداد Entityها
- حجم داده
- Fetch Strategy
- Lazy یا Eager Loading
- تعداد Joinها
- N+1 Problem
- Indexهای Database
- Batch Size
- Connection Pool
- Cache Configuration
- Execution Plan دیتابیس

مثلاً اگر در Hibernate این Query را اجرا کنیم:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
List<Order> orders =
    entityManager.createQuery(
        "SELECT o FROM Order o",
        Order.class
    ).getResultList();
```
</div>


و برای هر Order، Customer را جداگانه Lazy Load کنیم، ممکن است مشکل N+1 ایجاد شود.

این مشکل مخصوص Hibernate نیست؛ در EclipseLink هم ممکن است اتفاق بیفتد.

راهکارهای استاندارد:

- `JOIN FETCH`
- `EntityGraph`
- Batch Fetching
- DTO Projection
- Query Optimization

# **9. تفاوت در Lazy Loading و Fetching**

هر دو Provider از Lazy Loading پشتیبانی می‌کنند.

مثلاً:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
@Entity
public class Department {

    @Id
    private Long id;

    @OneToMany(mappedBy = "department",
               fetch = FetchType.LAZY)
    private List<Employee> employees;
}
```
</div>


در این حالت، زمانی که Department را می‌خوانیم، لزوماً Employeeها هم از Database خوانده نمی‌شوند.
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
Department department =
    entityManager.find(Department.class, 10L);
```
</div>


تا زمانی که به `employees` دسترسی نداشته باشیم، ممکن است Query مربوط به Employeeها اجرا نشود.
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
department.getEmployees().size();
```
</div>


در هر دو ORM امکان Lazy Loading وجود دارد، ولی نحوه پیاده‌سازی، Proxyها، Bytecode Enhancement و رفتار بعضی سناریوها می‌تواند متفاوت باشد.

# **10. Hibernate یا EclipseLink را چه زمانی انتخاب می‌کنیم؟**

[Guia Completo de Hibernate: Aprenda Hibernate do Básico ao Avançado](https://images.openai.com/static-rsc-4/7FZzFX5F0i4vJm3p557Rdsp4LqyaCOQsIhPwz5hotUfxVBPZ2BgN9fReHpFANW8B3W1eF2x5vwcDcP_O5taJgWFB2aZGb2oQzq2Ao5jH4HyVYCL9bTbIz-LXXYDoanDqmveWEs_Ta3BWfvDy-HWCq_EnIujM4NQkK3g7x2vbuIs?purpose=inline)

**Hibernate**

- Spring Boot
- Spring Data JPA
- پروژه‌های جدید Java
- Community و Documentation گسترده
- امکانات و Integrationهای متنوع

[Java EE 8 Compatible Eclipse GlassFish 5.1 Released](https://images.openai.com/static-rsc-4/0be9fXImAZU_jBmiBUkxC5XSSsM6s1lB9VvVNWe_uv2f91sfJXDBpaL_sQmBQ28_u3kTE86zMpkBUvmUKTvXx0DyPAa-9irPb4nuFOclPl3aVGoPXnfE-dc5MXwS64prjHItPsjjMVo1lbisiD_DBR_wBOSpWZ8dPVKxbyUU2-c?purpose=inline)

**EclipseLink**

- Jakarta EE
- پروژه‌های Enterprise
- سیستم‌های مبتنی بر EclipseLink
- پروژه‌هایی که Provider مستقل نیاز دارند
- بعضی امکانات خاص Mapping و Performance

در یک پروژه جدید Spring Boot، معمولاً Hibernate انتخاب پیش‌فرض و منطقی است؛ مگر اینکه دلیل مشخصی برای استفاده از EclipseLink وجود داشته باشد.

# **11. آیا می‌توان Hibernate را با EclipseLink جایگزین کرد؟**

بله، اگر معماری برنامه تا حد امکان از استاندارد JPA استفاده کرده باشد، تغییر ORM Provider امکان‌پذیر است.

اما این مهاجرت همیشه بدون تغییر نیست.

مواردی که باید بررسی کنیم:

- Hibernate-specific annotations
- Hibernate API مثل `Session`
- HQLهای اختصاصی
- Hibernate-specific configuration
- تفاوت‌های Query
- Lazy Loading Behavior
- Custom Typeها
- Caching
- Batch Processing
- Transaction behavior
- Performance و Execution Plan

به همین دلیل بهتر است در معماری Repository، تا حد امکان از API استاندارد `EntityManager` استفاده کنیم و وابستگی به API اختصاصی Provider را محدود کنیم.

# **12. جمع‌بندی برای مصاحبه Senior Java**

اگر در مصاحبه از تو بپرسند:

**What is the difference between Hibernate and EclipseLink?**

می‌توانی این جواب را بدهی:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


> Hibernate and EclipseLink are both ORM frameworks and implementations of the JPA specification, now known as Jakarta Persistence.
>
> JPA defines the standard API for persistence, while Hibernate and EclipseLink provide the actual implementation.
>
> Hibernate is widely used in Spring Boot applications and provides many additional features, such as its own Session API, HQL, and advanced ORM capabilities.
>
> EclipseLink is another mature JPA provider, commonly used in Jakarta EE environments, and also provides its own caching, mapping, and optimization features.
>
> If I use standard JPA APIs such as EntityManager, JPQL, and standard annotations, my application becomes more portable between providers. However, switching providers is not always seamless because provider-specific features, SQL generation, caching, lazy loading behavior, and performance characteristics may differ.
>
> In a typical Spring Boot project, I would choose Hibernate by default unless the project has a specific requirement for EclipseLink.
</div>


## **خلاصه نهایی**

## **این ۳ نکته را به خاطر بسپار**

**1. JPA یک استاندارد است؛ Hibernate و EclipseLink پیاده‌سازی آن هستند.**

**2. Hibernate در اکوسیستم Spring Boot رایج‌تر است، ولی EclipseLink نیز یک ORM قدرتمند و بالغ است.**

**3. استفاده از JPA Standard باعث Portability بهتر می‌شود، اما تغییر Provider نیازمند تست Compatibility و Performance است.**

سؤال بعدی که معمولاً بعد از این مبحث در مصاحبه مطرح می‌شود:

**تفاوت JPA، Hibernate، Spring Data JPA و JDBC چیست و هرکدام دقیقاً در چه لایه‌ای قرار دارند؟**

این موضوع خیلی مهم است؛ چون بسیاری از برنامه‌نویسان این چهار مفهوم را با هم اشتباه می‌گیرند.

</div>




<!-- SOURCE: 12.md -->
# File: 12.md

<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">
تغییر **JPA Provider** مثلاً از Hibernate به EclipseLink، در ظاهر ممکن است فقط تغییر یک dependency و تنظیمات باشد؛ اما در پروژه واقعی می‌تواند ریسک‌های جدی در Mapping، SQL، رفتار Transaction و Performance ایجاد کند.

نکته مهم این است که **JPA یک استاندارد است، اما Providerها در پیاده‌سازی جزئیات، رفتارهای پیش‌فرض و امکانات اختصاصی متفاوتی دارند.**

## **1. ریسک‌های اصلی تغییر Provider**

[What is ORM? A Beginner’s Guide to Object-Relational Mapping | by Jesse Onoyeyan | Medium](https://images.openai.com/static-rsc-4/p5d6KsFeKnrVOxy9PSnVxuQUxrr8Yf4OUjCbR8C-AYADUzUIQmTCvDO0bVG0WtgO4IbzUgJtnUGA_DRB1sPsLshtTpn2Oq2nV3tfcdRGw_XjMzmTipf1TjbAhRC8dx5tq-1EI-4SDIU7nerGJRT5CD1Yn980pG4MKziMhK7en1I?purpose=inline)

**Mapping و Entity Behavior**

ممکن است Mappingهای پیچیده، رابطه‌ها، Lazy Loading و Cascade در Provider جدید رفتار متفاوتی داشته باشند.

[SQL Server](https://images.openai.com/static-rsc-4/-bRxQyMqd0US7yQ7FbNXn1FHe9CtWGOaLM76lhhkKZIAGIE97uHVBw1a-oVeJIRQ1p7tATjPQ2R27yu7P5BbMyvgN9jXF--HIwNt1VwPJfFQqE9QoQ19OH4E3NyMx1v2dFuzGYg2M6z_1vwZoB88swol4oguhG-DRAEXMDtdd3M?purpose=inline)

**SQL Generation و Performance**

Provider جدید ممکن است SQL متفاوتی تولید کند، Indexها را متفاوت استفاده کند یا Queryهای بیشتری اجرا کند.

[BUILD | Computing](https://images.openai.com/static-rsc-4/sp4Xq-DuZWRaTSxU2QwZsn9r5LrpkJim_wAk_DASMl4WCZUgcoxP-g4n6e9nIxW-aHwlwAPTXjJJsDzHyqtZHTXyLGHpqEeDFFnWEosOu1rWw-I9qV7CYLq3nO_Fjo4IdPehm3XrAcYojpT_rjEeLwzrNnoqEzeItTSRQ8mNbPY?purpose=inline)

**Compatibility**

کلاس‌ها، Annotationها، APIهای اختصاصی و تنظیمات Provider قبلی ممکن است دیگر قابل استفاده نباشند.

### **ریسک‌ها به تفکیک**

| ریسک                    | توضیح                                 | مثال                                            |
| ----------------------- | ------------------------------------- | ----------------------------------------------- |
| Mapping incompatibility | تفاوت در تفسیر Mappingها              | `@JoinColumn` یا Composite Key                  |
| Lazy Loading            | رفتار متفاوت هنگام دسترسی به Relation | `LazyInitializationException` یا Queryهای اضافی |
| SQL Generation          | تولید SQL متفاوت                      | تغییر JOIN، Subquery یا ترتیب Query             |
| Performance regression  | افت کارایی                            | N+1 Query یا Full Table Scan                    |
| Transaction behavior    | تفاوت در Flush و Lock                 | زمان Flush متفاوت                               |
| Caching                 | تفاوت در Cache و Invalidation         | داده قدیمی یا Cache Miss بیشتر                  |
| Native Queries          | ناسازگاری Result Mapping              | DTO یا `SqlResultSetMapping`                    |
| Vendor-specific APIs    | حذف API اختصاصی                       | Hibernate `Session` یا HQL خاص                  |
| Schema generation       | DDL متفاوت                            | تغییر نوع ستون، Constraint یا Sequence          |
| Exception handling      | تفاوت Exceptionها                     | تغییر نوع خطای Persistence                      |

## **2. ریسک Mapping و Relationshipها**

فرض کن در Hibernate این Mapping را داری:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
@Entity
public class Order {

    @Id
    private Long id;

    @OneToMany(mappedBy = "order",
               fetch = FetchType.LAZY,
               cascade = CascadeType.ALL)
    private List<OrderItem> items;
}
```

و:

```
@Entity
public class OrderItem {

    @Id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORDER_ID")
    private Order order;
}
```
</div>


ممکن است در Provider جدید موارد زیر نیاز به بررسی داشته باشند:

- نحوه مدیریت Lazy Relationshipها
- ترتیب Load شدن Collectionها
- رفتار Cascade
- Dirty Checking
- Orphan Removal
- Entity Lifecycle
- مدیریت Composite IDها

در نتیجه ممکن است کدی که قبلاً بدون مشکل کار می‌کرد، در Provider جدید خطا بدهد یا رفتار متفاوتی داشته باشد.

## **3. ریسک SQL Generation و Performance**

یکی از مهم‌ترین ریسک‌ها همین مورد است.

فرض کن Query زیر را داری:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
List<Order> orders = entityManager
    .createQuery(
        "SELECT o FROM Order o WHERE o.customer.id = :id",
        Order.class
    )
    .setParameter("id", customerId)
    .getResultList();
```
</div>


Hibernate ممکن است SQL زیر را تولید کند:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
SELECT *
FROM ORDERS O
WHERE O.CUSTOMER_ID = ?
```
</div>


Provider جدید ممکن است SQL متفاوتی تولید کند؛ مثلاً:

- JOIN اضافی
- Subquery
- ترتیب متفاوت JOINها
- تعداد Query بیشتر
- استفاده متفاوت از Bind Parameters

ممکن است نتیجه همچنان درست باشد، اما Execution Plan دیتابیس تغییر کند و Performance افت کند.

نکته Senior:

صرفاً تست اینکه Query نتیجه صحیح می‌دهد کافی نیست. باید SQL تولیدشده و Execution Plan را نیز بررسی کرد، مخصوصاً در سیستم‌های Oracle با Queryهای سنگین و حجم داده بالا.

## **4. ریسک N+1 Query**

فرض کن:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
List<Order> orders = orderRepository.findAll();

for (Order order : orders) {
    System.out.println(order.getCustomer().getName());
}
```
</div>


ممکن است Provider قبلی با رفتار خاصی این اطلاعات را به شکل نسبتاً بهینه Load کند، ولی Provider جدید تعداد Query بیشتری اجرا کند.

مثلاً:

**Provider قبلی**
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
SELECT * FROM ORDERS;
```
</div>


یک Query برای Orderها

**Provider جدید**
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
SELECT * FROM ORDERS;

SELECT * FROM CUSTOMER WHERE ID = 1;
SELECT * FROM CUSTOMER WHERE ID = 2;
SELECT * FROM CUSTOMER WHERE ID = 3;
```
</div>


احتمال N+1 Query و افزایش فشار روی Database

البته N+1 مختص تغییر Provider نیست؛ در هر دو Provider امکان وقوع آن وجود دارد، اما تغییر Provider می‌تواند الگوی Queryهای قبلی را تغییر دهد.

## **5. ریسک Cache**

Providerها در زمینه Cache نیز ممکن است متفاوت باشند:

- First-Level Cache یا Persistence Context
- Second-Level Cache
- Query Cache
- Cache Invalidation
- Cache Synchronization
- نحوه Cache کردن Collectionها

مثلاً اگر در Hibernate از Second-Level Cache استفاده کرده باشی:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
@Cacheable
@Entity
public class Product {
    @Id
    private Long id;
}
```
</div>


ممکن است تنظیمات Cache در EclipseLink متفاوت باشد و نیاز به بازطراحی یا تنظیم مجدد داشته باشد.

حتی اگر Cache فعال شود، سیاست Invalidation و رفتار آن لزوماً با Provider قبلی یکسان نیست.

## **6. ریسک APIهای اختصاصی Provider**

اگر کد فقط از استاندارد JPA استفاده کند، مهاجرت آسان‌تر است.

اما اگر به API اختصاصی Hibernate وابسته باشی، مثلاً:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
Session session = entityManager.unwrap(Session.class);
```
</div>


یا از موارد زیر استفاده کرده باشی:

- Hibernate `Session`
- HQL-specific functionality
- Hibernate-specific annotations
- Custom Hibernate Types
- Interceptors
- Event Listeners
- Hibernate-specific Cache APIs

در مهاجرت به EclipseLink باید این قسمت‌ها بررسی و احتمالاً بازنویسی شوند.

اصل مهم: هرچه وابستگی به استاندارد JPA بیشتر باشد، قابلیت تعویض Provider بیشتر خواهد بود.

## **7. ریسک Transaction و Flush**

Providerها در مدیریت Persistence Context و زمان Flush تفاوت‌های پیاده‌سازی دارند.

مثلاً:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
@Transactional
public void transfer() {

    Account source = accountRepository.findById(1L).orElseThrow();
    Account destination = accountRepository.findById(2L).orElseThrow();

    source.setBalance(source.getBalance() - 100);
    destination.setBalance(destination.getBalance() + 100);
}
```
</div>


در این کد تغییرات توسط Dirty Checking مدیریت می‌شوند.

هنگام تغییر Provider باید بررسی شود:

- Dirty Checking
- Flush Mode
- Flush Timing
- Optimistic Locking
- Pessimistic Locking
- Version Field (`@Version`)
- Transaction Synchronization

چون تغییر زمان Flush ممکن است باعث شود خطای Constraint در زمان متفاوتی رخ دهد یا ترتیب اجرای SQL تغییر کند.

## **8. ریسک Schema و Database Compatibility**

اگر پروژه از Schema Generation استفاده کند، مثلاً:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
spring.jpa.hibernate.ddl-auto=update
```
</div>


تغییر Provider می‌تواند DDL متفاوتی تولید کند.

مواردی که باید بررسی شوند:

- نوع ستون‌ها
- Sequence و Identity
- Naming Strategy
- Indexها
- Foreign Keyها
- Unique Constraintها
- Schema و Table Naming
- Case Sensitivity

در سیستم Production توصیه می‌شود Schema را به Provider جدید نسپاری و Migrationها را با ابزارهایی مانند Flyway یا Liquibase مدیریت کنی.

## **9. چگونه Provider را با کمترین ریسک تغییر دهیم؟**

یک استراتژی مناسب برای پروژه Enterprise:

1

**Inventory**

تمام APIها، Annotationها و Featureهای اختصاصی Provider قبلی را شناسایی کن.

2

**Compatibility Analysis**

Mappingها، Queryها، Cache، Transactionها و Native SQLها را بررسی کن.

3

**Test Coverage**

تست Integration برای Repositoryها، Mappingها و Transactionها ایجاد کن.

4

**Performance Baseline**

قبل و بعد از مهاجرت تعداد Queryها، Latency، CPU و Execution Plan را مقایسه کن.

5

**Migration in Stages**

ابتدا در محیط تست و سپس در Staging و با Canary یا Rollback Plan مناسب در Production اجرا کن.

## **10. پاسخ مناسب برای مصاحبه Senior Java**

اگر در مصاحبه از تو پرسیده شود:

**What are the risks of changing the JPA provider?**

می‌توانی این‌طور پاسخ بدهی:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


> Changing a JPA provider is not always straightforward, even if the application uses standard JPA APIs. Different providers may have different behaviors in entity mapping, lazy loading, dirty checking, SQL generation, caching, and transaction handling.
>
> The main risk is not only functional incompatibility but also performance regression, such as additional queries or different execution plans.
>
> I would first identify provider-specific APIs and configurations, then validate entity mappings, queries, transaction behavior, and caching through integration tests. I would also compare generated SQL and execution plans and run performance tests before deploying the new provider.
>
> I would perform the migration incrementally and keep a rollback strategy.
</div>


### **خلاصه‌ای که باید به خاطر بسپاری**

**JPA Standard بودن کد، Provider Independence را تضمین نمی‌کند.**

حتی اگر کد از `EntityManager`، JPQL و Annotationهای استاندارد JPA استفاده کند، باز هم رفتار Provider در مواردی مثل SQL Generation، Lazy Loading، Cache، Flush و Performance می‌تواند متفاوت باشد.

در یک پروژه Senior، تغییر Provider را یک **Migration با ریسک رفتاری و Performance** در نظر می‌گیریم، نه صرفاً تعویض یک Dependency.

اگر بخواهی، می‌توانیم در ادامه یک سناریوی واقعی **Hibernate → EclipseLink** را بررسی کنیم و ببینیم در یک پروژه Spring Boot دقیقاً چه فایل‌ها، Dependencyها، تنظیمات و تست‌هایی باید تغییر کنند.

</div>




<!-- SOURCE: 13.md -->
# File: 13.md

<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">

در JPA/Hibernate، **First-Level Cache (L1)** و **Second-Level Cache (L2)** با هدف کاهش مراجعه به Database و افزایش Performance استفاده می‌شوند؛ اما تفاوت اصلی آن‌ها در **Scope، طول عمر و اشتراک‌گذاری Cache بین Sessionها** است.

## **1. First-Level Cache (L1 Cache)**

First-Level Cache همان Cache داخلی `Persistence Context` است.

در Hibernate، هر `Session` و در JPA هر `EntityManager` دارای Persistence Context است که Entityهای Load شده را مدیریت می‌کند.

[🤯 @Cacheable vs First Level Cache vs Second Level Cache — Explained](https://images.openai.com/static-rsc-4/XiYejZR6kmcScf2Ez2BeoSUOEmCfZvAQCz7koGNjiGP_mHgzW2i3iZc0a0caBjiJjxP_ieJkVWZ_eGq805QYP8paoYE324xmi2JqkUKV9y754Xl02GLBW91DN6LerJ-OQEyZpGVwutJrHl5g6qGAM1gJ6NsN6B9DvKmDYPHQTXk?purpose=inline)

[Spring JPA. Before going to JPA, lets recall JDBC | by Sumit sharma | Medium](https://images.openai.com/static-rsc-4/-uKkDtvaRRYgHAzBOmK8M_sMiui3x26cH7nN7TkTww5dlRy5kdZLGnz0jI_eSx2TxS7mNKDzCMv8TvDxd3vkZiP03r8X0T5G1HgtcZ3JO8dKp02YKG2705n2ZR1wYg2fZ3PHWBxIHs0oWVtu3RmMA0wL3AEoLuK51QogYX7iY_8?purpose=inline)

[Hibernate framework basics and architecture | Mohamed Elhamra](https://images.openai.com/static-rsc-4/ZIGITnJI_LBPtR-Ru1P9V698qTD92Lkgye30Wcl1IPRZj0zXp5F7iZETGdiI2d9C1Cyya6wHU63Eb-huIj182xb9kNVLsPz_bGauX0ZDd6ok1jXGOLiX1UHqzLnQ9H6bh0GlckQ-m_lMhN0WAEljwy2bd4UyX1-NVS9DhKl-q-0?purpose=inline)

7

### **ویژگی‌ها**

- به‌صورت پیش‌فرض فعال است.
- نیاز به تنظیم جداگانه ندارد.
- Scope آن مربوط به یک `Persistence Context` است.
- Entityهای Load شده را نگه می‌دارد.
- از Load مجدد یک Entity با همان ID در همان Context جلوگیری می‌کند.
- Dirty Checking و مدیریت Entityهای تغییر یافته را نیز بر عهده دارد.
- با پایان Persistence Context، Cache آن نیز از بین می‌رود.

### **مثال**

فرض کن چنین کدی داریم:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
@Transactional
public void testCache() {

    Product p1 = entityManager.find(Product.class, 10L);

    Product p2 = entityManager.find(Product.class, 10L);
}
```
</div>


در اجرای اول:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
SELECT * FROM PRODUCT WHERE ID = 10;
```
</div>


Entity از Database خوانده شده و در First-Level Cache قرار می‌گیرد.

در اجرای دوم:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
Product p2 = entityManager.find(Product.class, 10L);
```
</div>


دیگر SQL جدیدی اجرا نمی‌شود؛ Entity از Cache همان Persistence Context برگردانده می‌شود.

**نتیجه**

`p1 == p2` در این Persistence Context برابر `true` است؛ یعنی هر دو reference به همان managed entity instance اشاره می‌کنند.

### **First-Level Cache چگونه کار می‌کند؟**

[Hibernate/JPA Cache Mekanizmaları: First-Level ve Second-Level Cache’i Anlamak | by selçuk meral | Medium](https://images.openai.com/static-rsc-4/E1cm8sni2ghiIZIPxV98zVN83EzsgE_cV6hBYbtFE0GMjOImXuoJDkp86L1Jb2Ux5kM2rqKODK_E9R_IIqEpl8Ly0LMS3w4UdeRkC2KGu8HnVA1EiKoGiFIReDSr4EmTadkBFW6dqGKXMFUW8QzTQqttoEWIk_CScP2_2LSlrJw?purpose=inline)

[Hibernate Second-Level cache with EhCache | by Seonggil Jeong | Medium](https://images.openai.com/static-rsc-4/YwiD6nDcLLuB_l-XLVpbj5eYyYLqGP1QtXqhCs2LToxaWbASRbzg-PEsXa0_dt7_kywDWjf9awPO-LmwM278JcmXYsAkfbqXb3EcJ3B06bReSYaMHexLVleXn7p_WERKP6uddiTFs9AgDUKg9rjLm4v-lv8GbBeXH7TiPyOJj50?purpose=inline)

[JPAの永続性コンテキストとEntityManagerについてご説明いたします #初心者 - Qiita](https://images.openai.com/static-rsc-4/8ypJULD2LWHn9xp6Fpmp_vAlIteyPTTYhGB3ADSgxzLSnUoAg9TPAhFv9w8QEKOEerDH31cHKN1fDmMq5JVcVidcEaZwMWw0p2n35QrGcdfRpvqm5skou8QCXh5nwf_aCrGnFN8P62ylrIz-RW9eMX1At6bvl5mfOJmrZ6KK470?purpose=inline)

[Hibernate framework basics and architecture | Mohamed Elhamra](https://images.openai.com/static-rsc-4/ZIGITnJI_LBPtR-Ru1P9V698qTD92Lkgye30Wcl1IPRZj0zXp5F7iZETGdiI2d9C1Cyya6wHU63Eb-huIj182xb9kNVLsPz_bGauX0ZDd6ok1jXGOLiX1UHqzLnQ9H6bh0GlckQ-m_lMhN0WAEljwy2bd4UyX1-NVS9DhKl-q-0?purpose=inline)

[Spring JPA. Before going to JPA, lets recall JDBC | by Sumit sharma | Medium](https://images.openai.com/static-rsc-4/-uKkDtvaRRYgHAzBOmK8M_sMiui3x26cH7nN7TkTww5dlRy5kdZLGnz0jI_eSx2TxS7mNKDzCMv8TvDxd3vkZiP03r8X0T5G1HgtcZ3JO8dKp02YKG2705n2ZR1wYg2fZ3PHWBxIHs0oWVtu3RmMA0wL3AEoLuK51QogYX7iY_8?purpose=inline)

[java - Clarifying terminology - What does "hydrating" a JPA or Hibernate entity mean when fetching the entity from the DB - Stack Overflow](https://images.openai.com/static-rsc-4/c_aKqK5gyDAXu6Aij5QG687E3dGw_-k9L5fKxWE1x70Hy9_ruhoLgTSg7b1OTVQ0YbPTMgW6vo1bXnnqoU3qszrzLhmKPlY75y_kPREhj_qQnKlC_oRaigvH_y_lVord3nXjJLpCQWDVmJowYseTsGohfoA3iVRtSjh8R3GrIJU?purpose=inline)

[\[JPA\] 영속성 컨텍스트(persistence context) 개요 및 특징(feat. Dirty Checking, flush())](https://images.openai.com/static-rsc-4/646eod4Ni0Z6QfUAnbwn6F27OPpfAT-M7Qk-Coj8_cDQ0ZgUXgC8sDedAu0odfRic0eBd24WDAhBPc7u8GurDli_iDPBXDZ0zXeq86GE4IsE8rW_qReLZ-R4-NNdR1m8F6qt9cyh85WlHeLOqx5Jx3epte0ohTmHlrHJI-9EGZw?purpose=inline)

\#chatgpt-mermaid-\_r_439\_{font-family:-apple-system-body,ui-sans-serif,-apple-system,system-ui,"Segoe UI",Helvetica,"Apple Color Emoji",Arial,sans-serif,"Segoe UI Emoji","Segoe UI Symbol";font-size:16px;fill:rgb(237, 237, 237);}@keyframes edge-animation-frame{from{stroke-dashoffset:0;}}@keyframes dash{to{stroke-dashoffset:0;}}#chatgpt-mermaid-\_r_439\_ .edge-animation-slow{stroke-dasharray:9,5!important;stroke-dashoffset:900;animation:dash 50s linear infinite;stroke-linecap:round;}#chatgpt-mermaid-\_r_439\_ .edge-animation-fast{stroke-dasharray:9,5!important;stroke-dashoffset:900;animation:dash 20s linear infinite;stroke-linecap:round;}#chatgpt-mermaid-\_r_439\_ .error-icon{fill:rgb(48, 48, 48);}#chatgpt-mermaid-\_r_439\_ .error-text{fill:rgb(237, 237, 237);stroke:rgb(237, 237, 237);}#chatgpt-mermaid-\_r_439\_ .edge-thickness-normal{stroke-width:1px;}#chatgpt-mermaid-\_r_439\_ .edge-thickness-thick{stroke-width:3.5px;}#chatgpt-mermaid-\_r_439\_ .edge-pattern-solid{stroke-dasharray:0;}#chatgpt-mermaid-\_r_439\_ .edge-thickness-invisible{stroke-width:0;fill:none;}#chatgpt-mermaid-\_r_439\_ .edge-pattern-dashed{stroke-dasharray:3;}#chatgpt-mermaid-\_r_439\_ .edge-pattern-dotted{stroke-dasharray:2;}#chatgpt-mermaid-\_r_439\_ .marker{fill:rgb(175, 175, 175);stroke:rgb(175, 175, 175);}#chatgpt-mermaid-\_r_439\_ .marker.cross{stroke:rgb(175, 175, 175);}#chatgpt-mermaid-\_r_439\_ svg{font-family:-apple-system-body,ui-sans-serif,-apple-system,system-ui,"Segoe UI",Helvetica,"Apple Color Emoji",Arial,sans-serif,"Segoe UI Emoji","Segoe UI Symbol";font-size:16px;}#chatgpt-mermaid-\_r_439\_ p{margin:0;}#chatgpt-mermaid-\_r_439\_ .label{font-family:-apple-system-body,ui-sans-serif,-apple-system,system-ui,"Segoe UI",Helvetica,"Apple Color Emoji",Arial,sans-serif,"Segoe UI Emoji","Segoe UI Symbol";color:rgb(237, 237, 237);}#chatgpt-mermaid-\_r_439\_ .cluster-label text{fill:rgb(237, 237, 237);}#chatgpt-mermaid-\_r_439\_ .cluster-label span{color:rgb(237, 237, 237);}#chatgpt-mermaid-\_r_439\_ .cluster-label span p{background-color:transparent;}#chatgpt-mermaid-\_r_439\_ .label text,#chatgpt-mermaid-\_r_439\_ span{fill:rgb(237, 237, 237);color:rgb(237, 237, 237);}#chatgpt-mermaid-\_r_439\_ .node rect,#chatgpt-mermaid-\_r_439\_ .node circle,#chatgpt-mermaid-\_r_439\_ .node ellipse,#chatgpt-mermaid-\_r_439\_ .node polygon,#chatgpt-mermaid-\_r_439\_ .node path{fill:rgb(9, 23, 44);stroke:rgb(31, 78, 148);stroke-width:1px;}#chatgpt-mermaid-\_r_439\_ .rough-node .label text,#chatgpt-mermaid-\_r_439\_ .node .label text,#chatgpt-mermaid-\_r_439\_ .image-shape .label,#chatgpt-mermaid-\_r_439\_ .icon-shape .label{text-anchor:middle;}#chatgpt-mermaid-\_r_439\_ .node .katex path{fill:#000;stroke:#000;stroke-width:1px;}#chatgpt-mermaid-\_r_439\_ .rough-node .label,#chatgpt-mermaid-\_r_439\_ .node .label,#chatgpt-mermaid-\_r_439\_ .image-shape .label,#chatgpt-mermaid-\_r_439\_ .icon-shape .label{text-align:center;}#chatgpt-mermaid-\_r_439\_ .node.clickable{cursor:pointer;}#chatgpt-mermaid-\_r_439\_ .root .anchor path{fill:rgb(175, 175, 175)!important;stroke-width:0;stroke:rgb(175, 175, 175);}#chatgpt-mermaid-\_r_439\_ .arrowheadPath{fill:rgb(175, 175, 175);}#chatgpt-mermaid-\_r_439\_ .edgePath .path{stroke:rgb(175, 175, 175);stroke-width:1px;}#chatgpt-mermaid-\_r_439\_ .flowchart-link{stroke:rgb(175, 175, 175);fill:none;}#chatgpt-mermaid-\_r_439\_ .edgeLabel{background-color:rgb(0, 0, 0);text-align:center;}#chatgpt-mermaid-\_r_439\_ .edgeLabel p{background-color:rgb(0, 0, 0);}#chatgpt-mermaid-\_r_439\_ .edgeLabel rect{opacity:0.5;background-color:rgb(0, 0, 0);fill:rgb(0, 0, 0);}#chatgpt-mermaid-\_r_439\_ .labelBkg{background-color:rgba(0, 0, 0, 0.5);}#chatgpt-mermaid-\_r_439\_ .cluster rect{fill:rgb(48, 48, 48);stroke:rgba(255, 255, 255, 0.15);stroke-width:1px;}#chatgpt-mermaid-\_r_439\_ .cluster text{fill:rgb(237, 237, 237);}#chatgpt-mermaid-\_r_439\_ .cluster span{color:rgb(237, 237, 237);}#chatgpt-mermaid-\_r_439\_ div.mermaidTooltip{position:absolute;text-align:center;max-width:200px;padding:2px;font-family:-apple-system-body,ui-sans-serif,-apple-system,system-ui,"Segoe UI",Helvetica,"Apple Color Emoji",Arial,sans-serif,"Segoe UI Emoji","Segoe UI Symbol";font-size:12px;background:rgb(48, 48, 48);border:1px solid rgba(255, 255, 255, 0.15);border-radius:2px;pointer-events:none;z-index:100;}#chatgpt-mermaid-\_r_439\_ .flowchartTitleText{text-anchor:middle;font-size:18px;fill:rgb(237, 237, 237);}#chatgpt-mermaid-\_r_439\_ rect.text{fill:none;stroke-width:0;}#chatgpt-mermaid-\_r_439\_ .icon-shape,#chatgpt-mermaid-\_r_439\_ .image-shape{background-color:rgb(0, 0, 0);text-align:center;}#chatgpt-mermaid-\_r_439\_ .icon-shape p,#chatgpt-mermaid-\_r_439\_ .image-shape p{background-color:rgb(0, 0, 0);padding:2px;}#chatgpt-mermaid-\_r_439\_ .icon-shape .label rect,#chatgpt-mermaid-\_r_439\_ .image-shape .label rect{opacity:0.5;background-color:rgb(0, 0, 0);fill:rgb(0, 0, 0);}#chatgpt-mermaid-\_r_439\_ .label-icon{display:inline-block;height:1em;overflow:visible;vertical-align:-0.125em;}#chatgpt-mermaid-\_r_439\_ .node .label-icon path{fill:currentColor;stroke:revert;stroke-width:revert;}#chatgpt-mermaid-\_r_439\_ .node .neo-node{stroke:rgb(31, 78, 148);}#chatgpt-mermaid-\_r_439\_ [data-look="neo"].node rect,#chatgpt-mermaid-\_r_439\_ [data-look="neo"].cluster rect,#chatgpt-mermaid-\_r_439\_ [data-look="neo"].node polygon{stroke:url(#chatgpt-mermaid-\_r_439\_-gradient);filter:drop-shadow( 1px 2px 2px rgba(185,185,185,1));}#chatgpt-mermaid-\_r_439\_ [data-look="neo"].swimlane.cluster rect{filter:none;}#chatgpt-mermaid-\_r_439\_ [data-look="neo"].node path{stroke:url(#chatgpt-mermaid-\_r_439\_-gradient);stroke-width:1px;}#chatgpt-mermaid-\_r_439\_ [data-look="neo"].node .outer-path{filter:drop-shadow( 1px 2px 2px rgba(185,185,185,1));}#chatgpt-mermaid-\_r_439\_ [data-look="neo"].node .neo-line path{stroke:rgb(31, 78, 148);filter:none;}#chatgpt-mermaid-\_r_439\_ [data-look="neo"].node circle{stroke:url(#chatgpt-mermaid-\_r_439\_-gradient);filter:drop-shadow( 1px 2px 2px rgba(185,185,185,1));}#chatgpt-mermaid-\_r_439\_ [data-look="neo"].node circle .state-start{fill:#000000;}#chatgpt-mermaid-\_r_439\_ [data-look="neo"].icon-shape .icon{fill:url(#chatgpt-mermaid-\_r_439\_-gradient);filter:drop-shadow( 1px 2px 2px rgba(185,185,185,1));}#chatgpt-mermaid-\_r_439\_ [data-look="neo"].icon-shape .icon-neo path{stroke:url(#chatgpt-mermaid-\_r_439\_-gradient);filter:drop-shadow( 1px 2px 2px rgba(185,185,185,1));}#chatgpt-mermaid-\_r_439\_ .node text{font-size:14px;font-weight:600;letter-spacing:normal;fill:rgb(153, 206, 255);}#chatgpt-mermaid-\_r_439\_ .edgeLabels text{font-size:13px;font-weight:600;letter-spacing:-0.08px;fill:rgb(153, 206, 255);}#chatgpt-mermaid-\_r_439\_ .node tspan[font-weight="normal"],#chatgpt-mermaid-\_r_439\_ .edgeLabels tspan[font-weight="normal"]{font-weight:600;}#chatgpt-mermaid-\_r_439\_ .edgeLabel .label rect{opacity:1;rx:13px;ry:13px;fill:rgb(0, 14, 26);stroke:rgb(26, 62, 95);stroke-width:1px;}#chatgpt-mermaid-\_r_439\_ .node rect,#chatgpt-mermaid-\_r_439\_ .node circle,#chatgpt-mermaid-\_r_439\_ .node ellipse,#chatgpt-mermaid-\_r_439\_ .node polygon,#chatgpt-mermaid-\_r_439\_ .node path{fill:rgb(0, 40, 77);stroke:rgba(255, 255, 255, 0.1);stroke-width:1px;}#chatgpt-mermaid-\_r_439\_ .node rect{rx:16px;ry:16px;}#chatgpt-mermaid-\_r_439\_ .mermaid-decision .label-container{fill:rgb(0, 14, 26);stroke:rgb(26, 62, 95);stroke-dasharray:2,2;}#chatgpt-mermaid-\_r_439\_ .edgePaths .flowchart-link{stroke:rgb(175, 175, 175);stroke-width:1px;stroke-linecap:round;stroke-linejoin:round;}#chatgpt-mermaid-\_r_439\_ .marker{fill:rgb(175, 175, 175);stroke:rgb(175, 175, 175);}#chatgpt-mermaid-\_r_439\_ :root{--mermaid-font-family:-apple-system-body,ui-sans-serif,-apple-system,system-ui,"Segoe UI",Helvetica,"Apple Color Emoji",Arial,sans-serif,"Segoe UI Emoji","Segoe UI Symbol";}entityManager.find Product 10Entity in L1 Cache?Return cached EntityExecute SQLLoad from DatabaseStore in Persistence ContextReturn EntityYesNo

### **نکته مهم: First-Level Cache با پایان Transaction دقیقاً چه می‌شود؟**

در Spring، معمولاً Persistence Context به Transaction یا محدوده مدیریت‌شده توسط Spring متصل است، اما طول عمر آن الزاماً همیشه دقیقاً برابر Transaction نیست.

برای مثال:

- در حالت معمول Spring با `@Transactional`، EntityManager مربوط به Transaction پس از پایان آن از حالت مدیریت‌شده خارج می‌شود.
- در حالت Open EntityManager in View (OSIV)، Persistence Context ممکن است تا پایان درخواست HTTP باز بماند.
- اگر EntityManager را خودت بسازی، Scope آن به نحوه مدیریت و زمان `close()` وابسته است.

بنابراین بهتر است بگوییم Cache سطح اول متعلق به **Persistence Context** است، نه ذاتاً و همیشه متعلق به Transaction.

## **2. Second-Level Cache (L2 Cache)**

Second-Level Cache یک Cache مشترک در سطح Application است که می‌تواند داده‌های Entity را بین چند `EntityManager` یا چند Persistence Context به اشتراک بگذارد.

برخلاف L1، این Cache به یک EntityManager خاص محدود نیست و تا زمانی که Cache Provider و تنظیمات آن اجازه دهند، می‌تواند Entityها را بین Contextهای مختلف نگه دارد.

[Hibernate Second-Level cache with EhCache | by Seonggil Jeong | Medium](https://images.openai.com/static-rsc-4/YwiD6nDcLLuB_l-XLVpbj5eYyYLqGP1QtXqhCs2LToxaWbASRbzg-PEsXa0_dt7_kywDWjf9awPO-LmwM278JcmXYsAkfbqXb3EcJ3B06bReSYaMHexLVleXn7p_WERKP6uddiTFs9AgDUKg9rjLm4v-lv8GbBeXH7TiPyOJj50?purpose=inline)

[Medium](https://images.openai.com/static-rsc-4/Ede8vLE3CpAlclhO38OzgAONZihJ76PXcA5pCnqiet8qJa06OwU2SQ0HU0RhkoPhq9dXrIlpdQ17ESj4NNHewonJJ7QOIR7grdOmYEkHGS7lvTnfL60aXV0kgE_Tk7wz0-JB4pVq3OUFEyJa_q4BADYmSF4XsABbG49wVlSCMBk?purpose=inline)

[Hibernate](https://images.openai.com/static-rsc-4/EFeEmdKEiVpDqaGKmpMSINQbHvfIUDJTJZ_1XdXzsq8M6Q8T_XnJLCdN9Lu7_anYldtwdPpywNCDyv8aqm510pzn0p5tTTCzS9Ji7I7s3cNTGqdsHski8fhAh8iFvsJ4LbqOAg-lu4Mc8xXX7M9gldLhkDrQroDXqvmy9RHGIk8?purpose=inline)

6

### **ویژگی‌ها**

- به‌صورت پیش‌فرض فعال نیست.
- نیاز به Configuration دارد.
- بین Persistence Contextهای مختلف قابل اشتراک است.
- معمولاً از Cache Provider مانند Ehcache یا Infinispan استفاده می‌کند.
- می‌تواند برای Entityهای مشخص فعال شود.
- می‌تواند TTL و Eviction Policy داشته باشد.
- در سیستم‌های Cluster، در صورت استفاده از Cache توزیع‌شده، امکان اشتراک بین Nodeها نیز وجود دارد.

### **مثال**

فرض کن دو Transaction مستقل داریم:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
@Transactional
public Product getProduct1(Long id) {
    return entityManager.find(Product.class, id);
}
```
</div>


و:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
@Transactional
public Product getProduct2(Long id) {
    return entityManager.find(Product.class, id);
}
```
</div>


در حالت عادی، هر Transaction Persistence Context خودش را دارد.

اگر فقط L1 فعال باشد:

Transaction 1

`EntityManager 1 → L1 Cache 1 → Database`

Transaction 2

`EntityManager 2 → L1 Cache 2 → Database`

هر دو ممکن است برای خواندن همان Entity به Database مراجعه کنند.

اما با L2 Cache:

Transaction 1

`EntityManager 1 → L1 → Shared L2 Cache → Database`

Transaction 2

`EntityManager 2 → L1 → Shared L2 Cache`

اگر داده در L2 موجود باشد، Transaction دوم ممکن است بدون مراجعه به Database آن را دریافت کند.

## **3. مقایسه First-Level و Second-Level**

| ویژگی       | First-Level (L1)                       | Second-Level (L2)                  |
| ----------- | -------------------------------------- | ---------------------------------- |
| Scope       | یک Persistence Context                 | بین Persistence Contextها          |
| فعال بودن   | پیش‌فرض                                | نیاز به تنظیم                      |
| محل         | داخل Persistence Context               | Cache Provider                     |
| اشتراک      | خیر                                    | بله                                |
| طول عمر     | عمر Persistence Context                | وابسته به Cache Configuration      |
| مدیریت      | JPA / Hibernate                        | Provider و Cache Integration       |
| هدف         | جلوگیری از Load تکراری در همان Context | کاهش Database Access بین Contextها |
| Consistency | قوی‌تر در Context جاری                 | نیازمند مدیریت Consistency         |
| مناسب برای  | Entityهای جاری و تغییرات Transaction   | داده‌های پرتکرار و نسبتاً ثابت     |

## **4. ترتیب جستجوی داده در Cache**

هنگامی که از طریق Hibernate یک Entity را با ID می‌خوانیم، معمولاً مسیر به این شکل است:

**1. First-Level Cache**

Persistence Context

**2. Second-Level Cache**

Shared Cache

**3. Database**

SQL Query در صورت Cache Miss

یعنی:

1. ابتدا L1 بررسی می‌شود.
2. اگر Entity در L1 نباشد، در صورت فعال بودن Cache، L2 بررسی می‌شود.
3. اگر در L2 هم نباشد، Query به Database ارسال می‌شود.
4. Entity در Cacheهای مربوطه قرار می‌گیرد و به Application برگردانده می‌شود.

## **5. فعال کردن Second-Level Cache در Hibernate**

برای استفاده از L2 Cache، باید Cache Provider و تنظیمات Hibernate را مشخص کنیم.

### **مرحله اول: فعال‌سازی Cache در Spring Boot**
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
spring.jpa.properties.hibernate.cache.use_second_level_cache=true
```
</div>


برای Hibernate باید یک Cache Provider مناسب نیز به پروژه اضافه و تنظیم شود؛ Hibernate به‌تنهایی پیاده‌سازی Cache را فراهم نمی‌کند.

### **مرحله دوم: مشخص کردن Entityهایی که Cache شوند**

مثلاً Entity مربوط به `Country` که معمولاً تغییرات کمی دارد:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Cacheable;

@Entity
@Cacheable
public class Country {

    @Id
    private Long id;

    private String name;
}
```
</div>


در Hibernate، برای کنترل دقیق‌تر Cache می‌توان از Annotation اختصاصی Hibernate نیز استفاده کرد:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@jakarta.persistence.Cacheable
@Cache(
    usage = CacheConcurrencyStrategy.READ_ONLY,
    region = "countries"
)
public class Country {

    @Id
    private Long id;

    private String name;
}
```
</div>


در این مثال، `READ_ONLY` برای داده‌هایی مناسب است که تغییر نمی‌کنند یا عملاً Immutable هستند. Hibernate برای L2 از Regionهای مجزا و Strategyهای مختلف Cache استفاده می‌کند.&#x20;

[image](https://www.google.com/s2/favicons?domain=https://docs.hibernate.org\&sz=32)

docs.hibernate.org



## **6. چه داده‌هایی برای L2 Cache مناسب هستند؟**

**مناسب Cache**

- Country
- Currency
- Product Category
- تنظیمات سیستم
- داده‌های Reference
- اطلاعاتی با Read زیاد و Write کم

**معمولاً نامناسب**

- حساب بانکی و موجودی
- Inventory پرتغییر
- اطلاعات مالی حساس به لحظه
- داده‌هایی با Write بسیار زیاد
- اطلاعاتی که Consistency لحظه‌ای لازم دارند

علت این است که L2 Cache ممکن است بین Database و Cache ناسازگاری موقت ایجاد کند؛ مخصوصاً اگر تغییرات از طریق JDBC مستقیم یا سیستم دیگری انجام شوند و Cache از آن تغییرات مطلع نشود.&#x20;

[image](https://www.google.com/s2/favicons?domain=https://docs.hibernate.org\&sz=32)

docs.hibernate.org



## **7. یک نکته مهم: Second-Level Cache با Query Cache متفاوت است**

این سه مورد را با هم اشتباه نکن:

| Cache              | چه چیزی ذخیره می‌کند؟                                            |
| ------------------ | ---------------------------------------------------------------- |
| First-Level Cache  | Entityهای Managed در Persistence Context                         |
| Second-Level Cache | اطلاعات Entityها و بعضی Associationها، قابل اشتراک بین Contextها |
| Query Cache        | نتایج Queryهای مشخص، در صورت فعال‌سازی و پشتیبانی                |

مثلاً Query زیر:

<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
SELECT p FROM Product p WHERE p.category = :category
```
</div>


صرف فعال کردن L2 Cache به این معنی نیست که نتیجه این Query به‌صورت کامل Cache شده است.

L2 عمدتاً Entityها را Cache می‌کند، درحالی‌که Query Cache یک قابلیت جداگانه است.

## **8. سؤال مهم مصاحبه: آیا L2 Cache همیشه باعث Performance بهتر می‌شود؟**

**خیر.**

L2 Cache ممکن است Performance را بهتر کند، اما همیشه مفید نیست.

دلایل:

- هزینه نگهداری Cache
- مصرف RAM
- Cache Invalidation
- Stale Data
- Cache Miss
- Cache Eviction
- هزینه هماهنگی در Cluster
- افزایش پیچیدگی Debugging

در سیستم‌های پرتراکنش، ممکن است هزینه مدیریت Cache بیشتر از سود آن باشد.

بنابراین من L2 Cache را بر اساس الگوی دسترسی به داده فعال می‌کنم؛ به‌خصوص برای داده‌هایی که Read زیاد، Write کم و تحمل مناسبی برای Staleness دارند.

## **9. پاسخ آماده برای مصاحبه Senior Java**
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


> The First-Level Cache is associated with the Persistence Context and is enabled by default. It stores managed entities and prevents repeated database access for the same entity within the same persistence context.
>
> The Second-Level Cache is shared across multiple persistence contexts. It is optional and requires configuration, usually with a cache provider. It helps reduce database reads for frequently accessed data.
>
> I typically use the second-level cache for relatively static reference data, such as countries, currencies, or product categories. However, I avoid caching highly volatile data unless I have a well-defined consistency and invalidation strategy.
</div>


### **خلاصه حفظی**

L1 = Local Cache

یک EntityManager، یک Persistence Context

L2 = Shared Cache

چند Persistence Context می‌توانند از یک Cache مشترک استفاده کنند

L1 همیشه فعال است؛ L2 اختیاری و نیازمند تنظیم است.

اگر بخواهی، سؤال بعدی بسیار مرتبط برای مصاحبه Senior این است: **چطور Cache Invalidation را مدیریت می‌کنیم و مشکل Stale Data در Hibernate L2 Cache و Redis Cache را حل می‌کنیم؟**

</div>




<!-- SOURCE: 14.md -->
# File: 14.md

<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">
این دو مفهوم از مهم‌ترین مکانیزم‌های Hibernate هستند و تقریباً در هر پروژه JPA واقعی با آن‌ها سروکار داریم.

- **Dirty Checking**: Hibernate متوجه می‌شود چه Entityهایی تغییر کرده‌اند و تغییرات را بدون نیاز به `update()` صریح در Database ذخیره می‌کند.
- **Proxy**: Hibernate به‌جای Load کردن کامل بعضی Entityها یا Relationshipها، یک Object واسط ایجاد می‌کند تا Load شدن داده را تا زمان نیاز به تعویق بیندازد (Lazy Loading).

# **1. Dirty Checking چیست؟**

Dirty Checking یعنی Hibernate تغییرات Entityهای Managed را تشخیص دهد و هنگام Flush، SQL مناسب را به Database ارسال کند.

در JPA، وقتی یک Entity را Load می‌کنیم، معمولاً آن Entity وارد **Persistence Context** می‌شود. Hibernate وضعیت اولیه آن را نگه می‌دارد تا بتواند بعداً تغییرات را تشخیص دهد.

[Hibernate Dirty Checking and Flushing Mechanism Explained | prgrmmng.com](https://images.openai.com/static-rsc-4/gX4LNmPXp2_bqdWD7gju_dlj9Q03nSb65e4-gGNgrhodlgLP1s1BT6PIv1lEF7hceP2CKymW7Lzyh0Xa0C8tdEcoQ6R7WhiaBtnO31uQ6GN6aoibOAiZgvac3tgXCI_ZlPt3Idm0vNEG1ZanoBfT70hljLNPCjO4yJD209O965M?purpose=inline)

[JPA 영속성 컨텍스트(Persistence Context), 플러쉬(flush) | HAK’s Blog](https://images.openai.com/static-rsc-4/kafesuNk33bjz7JY4QxkyDXhAnZmlHfHUy1b-xW-lYmO7jxvhrb9QhboTU9HThkwPTIoUQzJn3O9TwFksSe4utyQ4inQIxunBjrXXrMvLfT5Z5qhzwFskMgY3jvw7yd12cBjHhXRJqhD5Xx5XDGn4Tt0yKcItcUeoSwZN1nCip8?purpose=inline)

[After Using Hibernate: Why I’ll Never Write SQL the Same Way Again | by XTechie | Dec, 2025 | Medium](https://images.openai.com/static-rsc-4/nc3ujlu8hnNGMAxA_ppkpPn_cEq04DaNiKFsT4A2wglCL72JazxHwt-Kg2-GE9ePNMyu8D-Gn9qJHPilvI2GBHL24SSMb00EXN6FhxmgA73cCihHnPvRL904aCEIMpA5D3rir6ZKQFLCt0xi_vH6bqEp8MZbdwiSr2NrVefzGAg?purpose=inline)

[Mastering Hibernate — Part 6 : Dirty Checking and Flush Modes in JPA/Hibernate | by Jaouadirabeb | Medium](https://images.openai.com/static-rsc-4/Cv27LAB7uJL-xFTBIi4gXiTrS_4ZMiE7fV3ZRoLrlsFZpVIkPnj4Jfnj6yl0RVp216jhdNWAKyoEDWUlOp6LnZhQ0rD_mQeJ2FcOkbgLmdTIX0g2mWYertpqx6SmkE5o24TrQcqVaLt4Hh_r3prLHKE5ckj7II18cjiQ42FtLGc?purpose=inline)

[Why Hibernate Saves Data When You Didn’t Tell It To | by The Outage Specialist | Medium](https://images.openai.com/static-rsc-4/4qFueNyWnnyjDyjz4lFpOU-oOTs4gRrxIxF1bKO8mQJFIojq3TIUD-ZCpDZ3zJ61SUJeDvELQsm2edEWzmM-xkNi8deaTGtt1s05KXucl6IOJvJ-ZIIRrZx2lN1NQOJF_BPG4Ganz738bPziCZdF_oodO9OVs52nC1vJZSCSts4?purpose=inline)

## **مثال ساده**

فرض کن Entity زیر را داریم:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
@Entity
public class Employee {

    @Id
    private Long id;

    private String name;

    private BigDecimal salary;
}
```

حالا:

```
@Transactional
public void updateSalary(Long id) {

    Employee employee = entityManager.find(Employee.class, id);

    employee.setSalary(new BigDecimal("5000"));
}
```
</div>


شاید انتظار داشته باشی برای ذخیره تغییرات باید این کار را انجام دهی:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
entityManager.merge(employee);
```
</div>


اما در این مثال نیازی به `merge()` نیست.

چرا؟

چون `employee` یک Entity از نوع **Managed** است و Hibernate تغییرات آن را دنبال می‌کند.

### **Hibernate چه اتفاقی می‌اندازد؟**

فرض کن Database ابتدا چنین مقداری دارد:

| id | name | salary |
| -- | ---- | ------ |
| 1  | Ali  | 3000   |

هنگام Load شدن Entity، Hibernate وضعیت اولیه را در Persistence Context نگه می‌دارد.

Initial Snapshot

ID: 1

Name: Ali

Salary: 3000

Current Entity State

ID: 1

Name: Ali

Salary: 5000

Hibernate هنگام Dirty Checking، وضعیت فعلی Entity را با وضعیت اولیه مقایسه می‌کند.

اگر تشخیص دهد `salary` تغییر کرده است، Entity را Dirty در نظر می‌گیرد.

## **2. Dirty Checking چه زمانی اجرا می‌شود؟**

در حالت معمول، بررسی تغییرات در زمان Flush انجام می‌شود؛ نه لزوماً بلافاصله پس از فراخوانی Setter.

Flush معمولاً در شرایط زیر اتفاق می‌افتد:

1. قبل از Commit Transaction
2. قبل از اجرای بعضی Queryها، در حالت `FlushMode.AUTO`
3. با فراخوانی دستی `entityManager.flush()`

مثال:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
@Transactional
public void updateEmployee() {

    Employee employee = entityManager.find(Employee.class, 1L);

    employee.setSalary(new BigDecimal("5000"));

    // هنوز ممکن است SQL اجرا نشده باشد

    entityManager.flush();

    // در این نقطه تغییرات به Database ارسال می‌شوند
}
```
</div>


SQL احتمالی:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
UPDATE EMPLOYEE
SET SALARY = 5000
WHERE ID = 1;
```
</div>


نکته: `flush()` به معنی Commit نیست. SQL به Database ارسال می‌شود، اما Transaction هنوز می‌تواند Rollback شود.

## **3. چرخه Dirty Checking**

[Hibernate Dirty Checking and Flushing Mechanism Explained | prgrmmng.com](https://images.openai.com/static-rsc-4/gX4LNmPXp2_bqdWD7gju_dlj9Q03nSb65e4-gGNgrhodlgLP1s1BT6PIv1lEF7hceP2CKymW7Lzyh0Xa0C8tdEcoQ6R7WhiaBtnO31uQ6GN6aoibOAiZgvac3tgXCI_ZlPt3Idm0vNEG1ZanoBfT70hljLNPCjO4yJD209O965M?purpose=inline)

[After Using Hibernate: Why I’ll Never Write SQL the Same Way Again | by XTechie | Dec, 2025 | Medium](https://images.openai.com/static-rsc-4/nc3ujlu8hnNGMAxA_ppkpPn_cEq04DaNiKFsT4A2wglCL72JazxHwt-Kg2-GE9ePNMyu8D-Gn9qJHPilvI2GBHL24SSMb00EXN6FhxmgA73cCihHnPvRL904aCEIMpA5D3rir6ZKQFLCt0xi_vH6bqEp8MZbdwiSr2NrVefzGAg?purpose=inline)

[JPA 영속성 컨텍스트(Persistence Context), 플러쉬(flush) | HAK’s Blog](https://images.openai.com/static-rsc-4/kafesuNk33bjz7JY4QxkyDXhAnZmlHfHUy1b-xW-lYmO7jxvhrb9QhboTU9HThkwPTIoUQzJn3O9TwFksSe4utyQ4inQIxunBjrXXrMvLfT5Z5qhzwFskMgY3jvw7yd12cBjHhXRJqhD5Xx5XDGn4Tt0yKcItcUeoSwZN1nCip8?purpose=inline)

[JPA Dirty Checking and Flushing Mechanism Explained with Examples | prgrmmng.com](https://images.openai.com/static-rsc-4/T6qEG6Dhx8zaFn8u0eZosCVtY_zN4dGWRPmuAoQF2Gx4BBkiVHTfD2c9rAsizTjLHJwDEQhnFHHGdSqzRB58Sh4qJRNl7ir5x5tQMNTAK4Zf3C8Z6YZClbFVC70_unQTU0GE5i3RUrO8QAT7i9c6uLDOaDdCnfcn-H_nlG8Wf4A?purpose=inline)

[Why Hibernate Saves Data When You Didn’t Tell It To | by The Outage Specialist | Medium](https://images.openai.com/static-rsc-4/4qFueNyWnnyjDyjz4lFpOU-oOTs4gRrxIxF1bKO8mQJFIojq3TIUD-ZCpDZ3zJ61SUJeDvELQsm2edEWzmM-xkNi8deaTGtt1s05KXucl6IOJvJ-ZIIRrZx2lN1NQOJF_BPG4Ganz738bPziCZdF_oodO9OVs52nC1vJZSCSts4?purpose=inline)

\#chatgpt-mermaid-\_r_4ma\_{font-family:-apple-system-body,ui-sans-serif,-apple-system,system-ui,"Segoe UI",Helvetica,"Apple Color Emoji",Arial,sans-serif,"Segoe UI Emoji","Segoe UI Symbol";font-size:16px;fill:rgb(237, 237, 237);}@keyframes edge-animation-frame{from{stroke-dashoffset:0;}}@keyframes dash{to{stroke-dashoffset:0;}}#chatgpt-mermaid-\_r_4ma\_ .edge-animation-slow{stroke-dasharray:9,5!important;stroke-dashoffset:900;animation:dash 50s linear infinite;stroke-linecap:round;}#chatgpt-mermaid-\_r_4ma\_ .edge-animation-fast{stroke-dasharray:9,5!important;stroke-dashoffset:900;animation:dash 20s linear infinite;stroke-linecap:round;}#chatgpt-mermaid-\_r_4ma\_ .error-icon{fill:rgb(48, 48, 48);}#chatgpt-mermaid-\_r_4ma\_ .error-text{fill:rgb(237, 237, 237);stroke:rgb(237, 237, 237);}#chatgpt-mermaid-\_r_4ma\_ .edge-thickness-normal{stroke-width:1px;}#chatgpt-mermaid-\_r_4ma\_ .edge-thickness-thick{stroke-width:3.5px;}#chatgpt-mermaid-\_r_4ma\_ .edge-pattern-solid{stroke-dasharray:0;}#chatgpt-mermaid-\_r_4ma\_ .edge-thickness-invisible{stroke-width:0;fill:none;}#chatgpt-mermaid-\_r_4ma\_ .edge-pattern-dashed{stroke-dasharray:3;}#chatgpt-mermaid-\_r_4ma\_ .edge-pattern-dotted{stroke-dasharray:2;}#chatgpt-mermaid-\_r_4ma\_ .marker{fill:rgb(175, 175, 175);stroke:rgb(175, 175, 175);}#chatgpt-mermaid-\_r_4ma\_ .marker.cross{stroke:rgb(175, 175, 175);}#chatgpt-mermaid-\_r_4ma\_ svg{font-family:-apple-system-body,ui-sans-serif,-apple-system,system-ui,"Segoe UI",Helvetica,"Apple Color Emoji",Arial,sans-serif,"Segoe UI Emoji","Segoe UI Symbol";font-size:16px;}#chatgpt-mermaid-\_r_4ma\_ p{margin:0;}#chatgpt-mermaid-\_r_4ma\_ .label{font-family:-apple-system-body,ui-sans-serif,-apple-system,system-ui,"Segoe UI",Helvetica,"Apple Color Emoji",Arial,sans-serif,"Segoe UI Emoji","Segoe UI Symbol";color:rgb(237, 237, 237);}#chatgpt-mermaid-\_r_4ma\_ .cluster-label text{fill:rgb(237, 237, 237);}#chatgpt-mermaid-\_r_4ma\_ .cluster-label span{color:rgb(237, 237, 237);}#chatgpt-mermaid-\_r_4ma\_ .cluster-label span p{background-color:transparent;}#chatgpt-mermaid-\_r_4ma\_ .label text,#chatgpt-mermaid-\_r_4ma\_ span{fill:rgb(237, 237, 237);color:rgb(237, 237, 237);}#chatgpt-mermaid-\_r_4ma\_ .node rect,#chatgpt-mermaid-\_r_4ma\_ .node circle,#chatgpt-mermaid-\_r_4ma\_ .node ellipse,#chatgpt-mermaid-\_r_4ma\_ .node polygon,#chatgpt-mermaid-\_r_4ma\_ .node path{fill:rgb(9, 23, 44);stroke:rgb(31, 78, 148);stroke-width:1px;}#chatgpt-mermaid-\_r_4ma\_ .rough-node .label text,#chatgpt-mermaid-\_r_4ma\_ .node .label text,#chatgpt-mermaid-\_r_4ma\_ .image-shape .label,#chatgpt-mermaid-\_r_4ma\_ .icon-shape .label{text-anchor:middle;}#chatgpt-mermaid-\_r_4ma\_ .node .katex path{fill:#000;stroke:#000;stroke-width:1px;}#chatgpt-mermaid-\_r_4ma\_ .rough-node .label,#chatgpt-mermaid-\_r_4ma\_ .node .label,#chatgpt-mermaid-\_r_4ma\_ .image-shape .label,#chatgpt-mermaid-\_r_4ma\_ .icon-shape .label{text-align:center;}#chatgpt-mermaid-\_r_4ma\_ .node.clickable{cursor:pointer;}#chatgpt-mermaid-\_r_4ma\_ .root .anchor path{fill:rgb(175, 175, 175)!important;stroke-width:0;stroke:rgb(175, 175, 175);}#chatgpt-mermaid-\_r_4ma\_ .arrowheadPath{fill:rgb(175, 175, 175);}#chatgpt-mermaid-\_r_4ma\_ .edgePath .path{stroke:rgb(175, 175, 175);stroke-width:1px;}#chatgpt-mermaid-\_r_4ma\_ .flowchart-link{stroke:rgb(175, 175, 175);fill:none;}#chatgpt-mermaid-\_r_4ma\_ .edgeLabel{background-color:rgb(0, 0, 0);text-align:center;}#chatgpt-mermaid-\_r_4ma\_ .edgeLabel p{background-color:rgb(0, 0, 0);}#chatgpt-mermaid-\_r_4ma\_ .edgeLabel rect{opacity:0.5;background-color:rgb(0, 0, 0);fill:rgb(0, 0, 0);}#chatgpt-mermaid-\_r_4ma\_ .labelBkg{background-color:rgba(0, 0, 0, 0.5);}#chatgpt-mermaid-\_r_4ma\_ .cluster rect{fill:rgb(48, 48, 48);stroke:rgba(255, 255, 255, 0.15);stroke-width:1px;}#chatgpt-mermaid-\_r_4ma\_ .cluster text{fill:rgb(237, 237, 237);}#chatgpt-mermaid-\_r_4ma\_ .cluster span{color:rgb(237, 237, 237);}#chatgpt-mermaid-\_r_4ma\_ div.mermaidTooltip{position:absolute;text-align:center;max-width:200px;padding:2px;font-family:-apple-system-body,ui-sans-serif,-apple-system,system-ui,"Segoe UI",Helvetica,"Apple Color Emoji",Arial,sans-serif,"Segoe UI Emoji","Segoe UI Symbol";font-size:12px;background:rgb(48, 48, 48);border:1px solid rgba(255, 255, 255, 0.15);border-radius:2px;pointer-events:none;z-index:100;}#chatgpt-mermaid-\_r_4ma\_ .flowchartTitleText{text-anchor:middle;font-size:18px;fill:rgb(237, 237, 237);}#chatgpt-mermaid-\_r_4ma\_ rect.text{fill:none;stroke-width:0;}#chatgpt-mermaid-\_r_4ma\_ .icon-shape,#chatgpt-mermaid-\_r_4ma\_ .image-shape{background-color:rgb(0, 0, 0);text-align:center;}#chatgpt-mermaid-\_r_4ma\_ .icon-shape p,#chatgpt-mermaid-\_r_4ma\_ .image-shape p{background-color:rgb(0, 0, 0);padding:2px;}#chatgpt-mermaid-\_r_4ma\_ .icon-shape .label rect,#chatgpt-mermaid-\_r_4ma\_ .image-shape .label rect{opacity:0.5;background-color:rgb(0, 0, 0);fill:rgb(0, 0, 0);}#chatgpt-mermaid-\_r_4ma\_ .label-icon{display:inline-block;height:1em;overflow:visible;vertical-align:-0.125em;}#chatgpt-mermaid-\_r_4ma\_ .node .label-icon path{fill:currentColor;stroke:revert;stroke-width:revert;}#chatgpt-mermaid-\_r_4ma\_ .node .neo-node{stroke:rgb(31, 78, 148);}#chatgpt-mermaid-\_r_4ma\_ [data-look="neo"].node rect,#chatgpt-mermaid-\_r_4ma\_ [data-look="neo"].cluster rect,#chatgpt-mermaid-\_r_4ma\_ [data-look="neo"].node polygon{stroke:url(#chatgpt-mermaid-\_r_4ma\_-gradient);filter:drop-shadow( 1px 2px 2px rgba(185,185,185,1));}#chatgpt-mermaid-\_r_4ma\_ [data-look="neo"].swimlane.cluster rect{filter:none;}#chatgpt-mermaid-\_r_4ma\_ [data-look="neo"].node path{stroke:url(#chatgpt-mermaid-\_r_4ma\_-gradient);stroke-width:1px;}#chatgpt-mermaid-\_r_4ma\_ [data-look="neo"].node .outer-path{filter:drop-shadow( 1px 2px 2px rgba(185,185,185,1));}#chatgpt-mermaid-\_r_4ma\_ [data-look="neo"].node .neo-line path{stroke:rgb(31, 78, 148);filter:none;}#chatgpt-mermaid-\_r_4ma\_ [data-look="neo"].node circle{stroke:url(#chatgpt-mermaid-\_r_4ma\_-gradient);filter:drop-shadow( 1px 2px 2px rgba(185,185,185,1));}#chatgpt-mermaid-\_r_4ma\_ [data-look="neo"].node circle .state-start{fill:#000000;}#chatgpt-mermaid-\_r_4ma\_ [data-look="neo"].icon-shape .icon{fill:url(#chatgpt-mermaid-\_r_4ma\_-gradient);filter:drop-shadow( 1px 2px 2px rgba(185,185,185,1));}#chatgpt-mermaid-\_r_4ma\_ [data-look="neo"].icon-shape .icon-neo path{stroke:url(#chatgpt-mermaid-\_r_4ma\_-gradient);filter:drop-shadow( 1px 2px 2px rgba(185,185,185,1));}#chatgpt-mermaid-\_r_4ma\_ .node text{font-size:14px;font-weight:600;letter-spacing:normal;fill:rgb(153, 206, 255);}#chatgpt-mermaid-\_r_4ma\_ .edgeLabels text{font-size:13px;font-weight:600;letter-spacing:-0.08px;fill:rgb(153, 206, 255);}#chatgpt-mermaid-\_r_4ma\_ .node tspan[font-weight="normal"],#chatgpt-mermaid-\_r_4ma\_ .edgeLabels tspan[font-weight="normal"]{font-weight:600;}#chatgpt-mermaid-\_r_4ma\_ .edgeLabel .label rect{opacity:1;rx:13px;ry:13px;fill:rgb(0, 14, 26);stroke:rgb(26, 62, 95);stroke-width:1px;}#chatgpt-mermaid-\_r_4ma\_ .node rect,#chatgpt-mermaid-\_r_4ma\_ .node circle,#chatgpt-mermaid-\_r_4ma\_ .node ellipse,#chatgpt-mermaid-\_r_4ma\_ .node polygon,#chatgpt-mermaid-\_r_4ma\_ .node path{fill:rgb(0, 40, 77);stroke:rgba(255, 255, 255, 0.1);stroke-width:1px;}#chatgpt-mermaid-\_r_4ma\_ .node rect{rx:16px;ry:16px;}#chatgpt-mermaid-\_r_4ma\_ .mermaid-decision .label-container{fill:rgb(0, 14, 26);stroke:rgb(26, 62, 95);stroke-dasharray:2,2;}#chatgpt-mermaid-\_r_4ma\_ .edgePaths .flowchart-link{stroke:rgb(175, 175, 175);stroke-width:1px;stroke-linecap:round;stroke-linejoin:round;}#chatgpt-mermaid-\_r_4ma\_ .marker{fill:rgb(175, 175, 175);stroke:rgb(175, 175, 175);}#chatgpt-mermaid-\_r_4ma\_ :root{--mermaid-font-family:-apple-system-body,ui-sans-serif,-apple-system,system-ui,"Segoe UI",Helvetica,"Apple Color Emoji",Arial,sans-serif,"Segoe UI Emoji","Segoe UI Symbol";}Load Entity from DBEntity enters PersistenceContextHibernate stores original stateApplication modifies EntityFlushState changed?Generate UPDATE SQLNo UPDATEDatabaseYesNo

### **نکته مهم**

اگر Entity تغییر نکند، Hibernate معمولاً Update غیرضروری اجرا نمی‌کند.

مثلاً:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
Employee employee = entityManager.find(Employee.class, 1L);

employee.setName(employee.getName());
```
</div>


اگر مقدار واقعاً تغییر نکرده باشد، معمولاً SQL `UPDATE` تولید نمی‌شود.

## **4. هزینه Dirty Checking چیست؟**

Dirty Checking رایگان نیست.

Hibernate برای تشخیص تغییرات باید وضعیت Entityها را مدیریت و در زمان Flush آن‌ها را بررسی کند. این موضوع در پروژه‌های بزرگ می‌تواند هزینه CPU و Memory ایجاد کند.

فرض کن یک Transaction داریم که ۲۰ هزار Entity را Load می‌کند، اما فقط ۱۰ عدد از آن‌ها تغییر می‌کنند.

Hibernate ممکن است لازم باشد تعداد زیادی از Entityها را بررسی کند تا تغییرات را پیدا کند.

### **مشکلات احتمالی**

- افزایش مصرف RAM به دلیل نگهداری Snapshotها
- افزایش CPU در زمان Flush
- طولانی شدن Transaction
- کند شدن Batch Processing
- افزایش هزینه مدیریت Persistence Context

### **راهکارهای بهینه‌سازی**

- Transactionها را کوتاه نگه دار.
- تعداد Entityهای Managed را محدود کن.
- در Batch Processing از `flush()` و `clear()` دوره‌ای استفاده کن.
- برای Read-only عملیات، از Read-only Query یا Entityهای Read-only استفاده کن.
- برای Updateهای حجیم، در صورت مناسب بودن، از Bulk Update استفاده کن.

مثال Batch:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
for (int i = 0; i < employees.size(); i++) {

    Employee employee = employees.get(i);

    employee.setStatus("ACTIVE");

    if (i % 500 == 0) {
        entityManager.flush();
        entityManager.clear();
    }
}
```
</div>


توجه: وقتی `clear()` اجرا می‌شود، Entityها از Persistence Context خارج و Detached می‌شوند. بنابراین باید مطمئن باشی تغییرات موردنظر قبل از `clear()` با `flush()` ارسال شده‌اند و Entityهای بعدی را به‌درستی مدیریت می‌کنی.

# **5. Proxy در Hibernate چیست؟**

Proxy یک Object واسط است که Hibernate به‌جای Load کردن کامل Entity، در بعضی شرایط ایجاد می‌کند.

هدف اصلی آن:

> Lazy Loading؛ یعنی دریافت اطلاعات فقط زمانی که واقعاً به آن نیاز داریم.

[Lazy vs Eager Loading in Hibernate with Spring Boot | by Ahmet Emre DEMİRŞEN | @Override | Apr, 2026 | Medium](https://images.openai.com/static-rsc-4/sdBK1BREgENNWHptPKb9I4xpP169pqIx2u7sZF9ei-GkFzKRNnCanchwXY5z5rxW99ECadPKzDIC_kwCskQjMf535OfqLXitH0yhfguoB1R76kmurGA7LebXHfE-NsMdkWLkluPAuBICdHbh0aOMuoNazjjqQu4PkQsATypDcCI?purpose=inline)

[Hibernate, JPA, Spring DATA JPA, Hibernate Proxy and Architectures | by Berat Yesbek | Medium](https://images.openai.com/static-rsc-4/9ZtYhzrRz1zXnizh5waLSEtJxYEFE9K5zi2wM1OI3dJAy1lSh5ksOJaduy0KoxOdi2uYYy7iFRA2EoIJ55sApMh7_J4_CBBTUt2URQNwLvak70blwcRlzD2PPoLm9p6alw08LuK_xVMXm2Vk-E2Fosk596us8eNwe_NE3Zr0dUg?purpose=inline)

[Get vs Load in Hibernate with example | Javainsimpleway](https://images.openai.com/static-rsc-4/p0n7Lfs115PtdzRTkdiqKm83zgg7o3aaPxysF9VqBltozEXN012ynY3FyWjkZvyEo1YlrdG1N9fu3nD54JymR2KKBY24njd46JySD30r9cWSKrlOWxhOpDvvPktbM1jpGUjUW4zS0Xj99hzXUWcKO7DXEBmm4AdG9oMb7s6Rwqw?purpose=inline)

[get() vs load() in Hibernate: Same Result, Very Different Behavior | by Gaddam.Naveen | Medium](https://images.openai.com/static-rsc-4/AEyvOwXuc9gi_OUnbRA-B5dmqs55COKL3_mmtk6LuaHTliKb1cUnBR2s4mV8gJ9cAwG69xSHnkLA-98QIsunHFX_jHP6fndWermi8ov_mo7MdIlrHtGTF7xP8avxVAf4P2fyOyJcqi8H6kULKhPMxD4VLJej6Uyo1JUTwqZBEko?purpose=inline)

[Mastering Hibernate — Part 4 : Lazy vs Eager Loading + Fixing the N+1 Problem | by Jaouadirabeb | Medium](https://images.openai.com/static-rsc-4/JItE3os_aqR15DH51V-jLHx_Cyx27lluSmRm-JYAKDbPlQnU8mV6QbYvOONSxvON8rKZvvGFLoUdG2FxIxqB2XUTYTYkkxLVLaZFZQzGl8ueyr_MJGtA8UQOdc3tRpCrkr_WG49wzRZrvo0LPtD92fR2OcWH8JDkw1TyAxEGDK8?purpose=inline)

[A follow-up to domain model purity and ORMs · Vladimir Khorikov](https://images.openai.com/static-rsc-4/5TY0VUcwzVB6zEw402OVV6ur-Q2UpTS7G3HpwJ-PdI0ThVpisue8LaCJhggO1ghNc-ytHdoLUUUvqHaGWLX_JeKYG0v1m2UoEEaoNnH7fc5ONAin8oU7m3YM_dmSjehzWLIm13fN-KmwqnVr_QqzkWteqwavL_Me_4ZDmKf4ng0?purpose=inline)

فرض کن Entity زیر را داریم:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
@Entity
public class Department {

    @Id
    private Long id;

    private String name;
}
```
</div>


و:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
@Entity
public class Employee {

    @Id
    private Long id;

    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    private Department department;
}
```
</div>


وقتی Employee را Load می‌کنیم:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
Employee employee = entityManager.find(Employee.class, 1L);
```
</div>


ممکن است Hibernate فقط Employee را Load کند و Department را هنوز Load نکند.
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
SELECT *
FROM EMPLOYEE
WHERE ID = 1;
```
</div>


در این حالت `employee.getDepartment()` ممکن است یک Proxy باشد، نه Department کاملاً Load شده.

## **6. Proxy چگونه کار می‌کند؟**

فرض کن Employee متعلق به Department شماره 10 است.

Hibernate می‌داند Foreign Key برابر 10 است، پس می‌تواند یک Proxy برای Department ایجاد کند، بدون اینکه بلافاصله اطلاعات کامل Department را بخواند.

ابتدا Employee Load می‌شود

Employee

ID: 1

Name: Ali

Department ID: 10

↓

Department Proxy

Department ID: 10

اطلاعات کامل Department هنوز Load نشده است.

↓

وقتی واقعاً به name دسترسی پیدا کنیم
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
employee.getDepartment().getName();
```
</div>


Hibernate می‌تواند Query جدید اجرا کرده و Department را Load کند.

SQL احتمالی در لحظه دسترسی:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
SELECT *
FROM DEPARTMENT
WHERE ID = 10;
```
</div>


به این رفتار **Lazy Loading** می‌گوییم.

## **7. مثال واقعی Lazy Loading**
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
@Transactional
public void testLazyLoading(Long employeeId) {

    Employee employee =
        entityManager.find(Employee.class, employeeId);

    // Employee Load شده
    // Department ممکن است هنوز Load نشده باشد

    Department department = employee.getDepartment();

    // ممکن است هنوز SQL اجرا نشده باشد

    String departmentName = department.getName();

    // در اینجا ممکن است SQL مربوط به Department اجرا شود
}
```
</div>


در این مثال، اگر Department واقعاً Lazy و هنوز Uninitialized باشد، دسترسی به `getName()` باعث Initialize شدن Proxy و اجرای SQL می‌شود.

## **8. Proxy چه مزیتی دارد؟**

فرض کن Employee شامل اطلاعات زیر باشد:

- Department
- Address
- Manager
- Projects
- Skills

اگر همه این Relationshipها به‌صورت Eager Load شوند، ممکن است با Load یک Employee تعداد زیادی Query یا JOIN اجرا شود و حجم زیادی از داده وارد Memory شود.

با Lazy Loading:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
Employee employee = employeeRepository.findById(1L).orElseThrow();
```
</div>


تا زمانی که واقعاً به Address یا Projects دسترسی نداشته باشی، ممکن است آن‌ها Load نشوند.

| مزیت                   | توضیح                                      |
| ---------------------- | ------------------------------------------ |
| کاهش Query غیرضروری    | اطلاعاتی که استفاده نمی‌شوند Load نمی‌شوند |
| کاهش حجم داده          | انتقال داده کمتر از Database               |
| کاهش Memory            | Entityهای مرتبط غیرضروری کمتر Load می‌شوند |
| بهبود Read Performance | برای سناریوهای مناسب                       |

اما Lazy Loading همیشه بهتر نیست؛ اگر در یک Loop به Relationshipها دسترسی پیدا کنی، می‌تواند مشکل N+1 ایجاد کند.

## **9. مشکلات Proxy در Hibernate**

Proxy با وجود مزایا، مشکلات مهمی دارد که در پروژه‌های واقعی و مصاحبه Senior باید بشناسی.

### **9.1. LazyInitializationException**

فرض کن Entity را Load می‌کنی، Transaction تمام می‌شود و سپس به Relationship دسترسی پیدا می‌کنی:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
Employee employee;

@Transactional
public Employee findEmployee(Long id) {
    return employeeRepository.findById(id).orElseThrow();
}
```
</div>


سپس بیرون از Transaction:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
employee.getDepartment().getName();
```
</div>


اگر Proxy هنوز Initialize نشده باشد و Session بسته شده باشد، ممکن است با خطای زیر مواجه شوی:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
LazyInitializationException:
could not initialize proxy - no Session
```
</div>


راه‌حل‌های اصولی:

- Relationshipهای موردنیاز را داخل Transaction Load کن.
- از `JOIN FETCH` استفاده کن.
- از `EntityGraph` استفاده کن.
- در لایه Service، Entity را به DTO تبدیل کن.
- از Eager Loading بی‌رویه اجتناب کن.

مثال:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
@Query("""
    SELECT e
    FROM Employee e
    JOIN FETCH e.department
    WHERE e.id = :id
""")
Optional<Employee> findByIdWithDepartment(Long id);
```
</div>


### **9.2. مشکل N+1**
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
List<Employee> employees = employeeRepository.findAll();

for (Employee employee : employees) {
    System.out.println(employee.getDepartment().getName());
}
```
</div>


اگر 100 کارمند داشته باشیم و Departmentها به شکل Lazy باشند، ممکن است:

- یک Query برای Employees
- 100 Query برای Departmentها

در مجموع 101 Query اجرا شود.

راهکار:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
@Query("""
    SELECT e
    FROM Employee e
    JOIN FETCH e.department
""")
List<Employee> findAllWithDepartment();
```
</div>


یا استفاده از `EntityGraph`، Batch Fetching و Projection، بسته به سناریو.

# **10. آیا Proxy همان Entity است؟**

از نظر نوع و رفتار، همیشه دقیقاً همان کلاس Entity نیست.

Hibernate در بسیاری از حالت‌ها Proxy را با استفاده از تکنیک‌هایی مانند Subclassing ایجاد می‌کند؛ بنابراین ممکن است Proxy از کلاس Entity ارث‌بری کند و رفتارهای دسترسی به فیلدها را کنترل کند.

برای مثال:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
Department department = employee.getDepartment();

System.out.println(department.getClass());
```
</div>


ممکن است چیزی شبیه این ببینی:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
Department$HibernateProxy
```
</div>


نه کلاس اصلی:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
Department
```
</div>


این موضوع می‌تواند در موارد زیر مشکل ایجاد کند:

- بررسی کلاس با `getClass()`
- Serialization
- JSON Mapping
- `equals()` و `hashCode()`
- کلاس‌های `final`
- دسترسی به Fieldها به‌جای Getterها

به همین دلیل طراحی `equals()` و `hashCode()` در Entityها باید با توجه به Identity و Proxyهای Hibernate انجام شود.

# **11. تفاوت Dirty Checking و Proxy**

| ویژگی              | Dirty Checking       | Proxy                           |
| ------------------ | -------------------- | ------------------------------- |
| هدف                | تشخیص تغییرات Entity | Lazy Loading                    |
| مرتبط با           | Persistence Context  | دسترسی به Entity یا Association |
| چه زمانی؟          | عمدتاً هنگام Flush   | هنگام دسترسی به داده Lazy       |
| نتیجه              | تولید `UPDATE`       | Load کردن داده                  |
| مثال               | تغییر Salary         | Load کردن Department            |
| ارتباط با Database | Synchronize تغییرات  | دریافت اطلاعات در صورت نیاز     |

[Hibernate Dirty Checking and Flushing Mechanism Explained | prgrmmng.com](https://images.openai.com/static-rsc-4/gX4LNmPXp2_bqdWD7gju_dlj9Q03nSb65e4-gGNgrhodlgLP1s1BT6PIv1lEF7hceP2CKymW7Lzyh0Xa0C8tdEcoQ6R7WhiaBtnO31uQ6GN6aoibOAiZgvac3tgXCI_ZlPt3Idm0vNEG1ZanoBfT70hljLNPCjO4yJD209O965M?purpose=inline)

[Lazy vs Eager Loading in Hibernate with Spring Boot | by Ahmet Emre DEMİRŞEN | @Override | Apr, 2026 | Medium](https://images.openai.com/static-rsc-4/sdBK1BREgENNWHptPKb9I4xpP169pqIx2u7sZF9ei-GkFzKRNnCanchwXY5z5rxW99ECadPKzDIC_kwCskQjMf535OfqLXitH0yhfguoB1R76kmurGA7LebXHfE-NsMdkWLkluPAuBICdHbh0aOMuoNazjjqQu4PkQsATypDcCI?purpose=inline)

[Get vs Load in Hibernate with example | Javainsimpleway](https://images.openai.com/static-rsc-4/p0n7Lfs115PtdzRTkdiqKm83zgg7o3aaPxysF9VqBltozEXN012ynY3FyWjkZvyEo1YlrdG1N9fu3nD54JymR2KKBY24njd46JySD30r9cWSKrlOWxhOpDvvPktbM1jpGUjUW4zS0Xj99hzXUWcKO7DXEBmm4AdG9oMb7s6Rwqw?purpose=inline)

[After Using Hibernate: Why I’ll Never Write SQL the Same Way Again | by XTechie | Dec, 2025 | Medium](https://images.openai.com/static-rsc-4/nc3ujlu8hnNGMAxA_ppkpPn_cEq04DaNiKFsT4A2wglCL72JazxHwt-Kg2-GE9ePNMyu8D-Gn9qJHPilvI2GBHL24SSMb00EXN6FhxmgA73cCihHnPvRL904aCEIMpA5D3rir6ZKQFLCt0xi_vH6bqEp8MZbdwiSr2NrVefzGAg?purpose=inline)

[Hibernate, JPA, Spring DATA JPA, Hibernate Proxy and Architectures | by Berat Yesbek | Medium](https://images.openai.com/static-rsc-4/9ZtYhzrRz1zXnizh5waLSEtJxYEFE9K5zi2wM1OI3dJAy1lSh5ksOJaduy0KoxOdi2uYYy7iFRA2EoIJ55sApMh7_J4_CBBTUt2URQNwLvak70blwcRlzD2PPoLm9p6alw08LuK_xVMXm2Vk-E2Fosk596us8eNwe_NE3Zr0dUg?purpose=inline)

[JPA 영속성 컨텍스트(Persistence Context), 플러쉬(flush) | HAK’s Blog](https://images.openai.com/static-rsc-4/kafesuNk33bjz7JY4QxkyDXhAnZmlHfHUy1b-xW-lYmO7jxvhrb9QhboTU9HThkwPTIoUQzJn3O9TwFksSe4utyQ4inQIxunBjrXXrMvLfT5Z5qhzwFskMgY3jvw7yd12cBjHhXRJqhD5Xx5XDGn4Tt0yKcItcUeoSwZN1nCip8?purpose=inline)

[\[JPA\] 프록시와 로딩 전략](https://images.openai.com/static-rsc-4/uS-IpTyiAEqHWc2gpwL6NmUTIxRnevbejHn_pmJAxsYt9wswsFDO5swGZggg_eUjKCVdNtbYWEXGGy0Y27xVUI9NWkFvkwpH1DWVHxGRUoKcO4oHklaCJUxG32OcojiCosu8Ao-mrmHUyyLqqziwk96zl6j5XJlvjXShJPw1Mys?purpose=inline)

[Java @Retention Annotation. Hello, the @Retention annotation is… | by Hüsna POYRAZ | @Override | Medium](https://images.openai.com/static-rsc-4/TOEhH4N2i9X87Y61szaGPtBebRvJHfZd2Ln_r2Q1VQ1meTLVA4rcCJvLnaLJC_n49iagzqkeVpaa3QwsZsCHFON3eybu3Dl2H-J8QZHCwhGb6Zwg8glFGK0aKgy5ATUJNKA1p5pfsq4D3771J8gKgxs8fFa_a5yjayCqiadAbZM?purpose=inline)

# **12. یک سناریوی واقعی که هر دو با هم کار می‌کنند**
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
@Transactional
public void changeEmployeeDepartment(Long employeeId,
                                    Long departmentId) {

    Employee employee =
        entityManager.find(Employee.class, employeeId);

    Department department =
        entityManager.getReference(Department.class, departmentId);

    employee.setDepartment(department);
}
```
</div>


اینجا دو مکانیزم مهم اتفاق می‌افتند:

### **Proxy**
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
entityManager.getReference(Department.class, departmentId);
```
</div>


Hibernate ممکن است یک Proxy برای Department ایجاد کند، بدون اینکه اطلاعات کامل آن را از Database بخواند؛ چون برای تنظیم Foreign Key، در بسیاری از حالت‌ها فقط ID کافی است.

### **Dirty Checking**
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
employee.setDepartment(department);
```
</div>


Hibernate متوجه تغییر Relationship در Employee می‌شود.

در زمان Flush، SQL احتمالی:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
UPDATE EMPLOYEE
SET DEPARTMENT_ID = ?
WHERE ID = ?;
```
</div>


در نتیجه، ممکن است بدون اجرای `SELECT` برای Department، فقط با ID آن بتوان Relationship را تغییر داد؛ البته این رفتار به Mapping، وجود Entity و شرایط اجرایی بستگی دارد.

## **13. پاسخ آماده مصاحبه Senior Java**

**Dirty Checking** is a Hibernate mechanism that tracks changes to managed entities within the persistence context. Hibernate compares the entity's current state with its original state and generates the necessary SQL UPDATE statements during flush. This eliminates the need for explicit update calls in typical managed-entity scenarios.

**Proxy** is an object that Hibernate uses to defer loading an entity or association until it is actually accessed. It is commonly used for lazy loading to reduce unnecessary database queries.

However, proxies can cause issues such as LazyInitializationException, N+1 queries, and serialization problems if they are not handled properly.

I usually manage these issues through appropriate fetch strategies, JOIN FETCH, EntityGraph, DTO projections, and well-defined transaction boundaries.

### **خلاصه‌ای که باید به خاطر بسپاری**

**Dirty Checking**

تغییر Entity را تشخیص می‌دهد و هنگام Flush، SQL مناسب را تولید می‌کند.

**Proxy**

Load شدن اطلاعات را تا زمان نیاز به تعویق می‌اندازد (Lazy Loading).

**Flush**

تغییرات Persistence Context را به Database Synchronize می‌کند، اما به‌تنهایی Commit نیست.

اگر این سه مفهوم را در کنار هم درک کنی، بخش بزرگی از رفتار Hibernate در پروژه‌های واقعی را می‌توانی تحلیل کنی؛ از جمله N+1، LazyInitializationException، Queryهای اضافی و Performance مشکلات ORM.
</div>




<!-- SOURCE: 15.md -->
# File: 15.md

<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">

**Batch Fetching** در Hibernate برای کاهش مشکل **N+1 Query** استفاده می‌شود.

ایده اصلی این است که به‌جای اجرای یک Query جداگانه برای Load کردن هر Relationship، Hibernate چند Entity یا Collection را با یک Query و به‌صورت گروهی Load کند.

## **1. مشکل N+1 چیست؟**

فرض کن 100 کارمند داریم و هر کارمند به یک Department مرتبط است:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
@Entity
public class Employee {

    @Id
    private Long id;

    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    private Department department;
}
```
</div>


حالا:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
List<Employee> employees = employeeRepository.findAll();

for (Employee employee : employees) {
    System.out.println(employee.getDepartment().getName());
}
```
</div>


Hibernate ابتدا یک Query برای دریافت کارمندان اجرا می‌کند:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
SELECT * FROM EMPLOYEE;
```
</div>


سپس برای هر کارمند، Department مربوطه را جداگانه Load می‌کند:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
SELECT * FROM DEPARTMENT WHERE ID = 1;
SELECT * FROM DEPARTMENT WHERE ID = 2;
SELECT * FROM DEPARTMENT WHERE ID = 3;
...
```
</div>


مشکل N+1

اگر 100 کارمند داشته باشیم و هر کدام Department متفاوتی داشته باشند، ممکن است 101 Query اجرا شود.

این موضوع باعث افزایش موارد زیر می‌شود:

- Network Round Trip
- Database Load
- Query Execution Overhead
- Response Time

## **2. Batch Fetching چگونه مشکل را حل می‌کند؟**

به‌جای اینکه Hibernate برای هر Department یک Query اجرا کند، چند Department را با یک Query دریافت می‌کند.

مثلاً اگر Batch Size برابر 10 باشد:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
SELECT *
FROM DEPARTMENT
WHERE ID IN (1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
```
</div>


سپس Batch بعدی:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
SELECT *
FROM DEPARTMENT
WHERE ID IN (11, 12, 13, 14, 15, 16, 17, 18, 19, 20);
```
</div>


بنابراین برای 100 Department متمایز، به‌جای 100 Query، تقریباً 10 Query اجرا می‌شود.

**مقایسه تعداد Queryها**

مثال: 100 کارمند با 100 Department متمایز

**بدون Batch Fetching**

**101 Query**

**Batch Size = 10**

**11 Query**

یک Query برای Employeeها + ده Query برای Departmentها

## **3. استفاده از&#x20;**`@BatchSize`

Hibernate Annotation زیر را برای فعال کردن Batch Fetching ارائه می‌دهد:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
import org.hibernate.annotations.BatchSize;

@Entity
public class Department {

    @Id
    private Long id;

    private String name;

    @BatchSize(size = 10)
    @OneToMany(mappedBy = "department", fetch = FetchType.LAZY)
    private List<Employee> employees;
}
```
</div>


اما دقت کن: این مثال Batch Fetching را برای Collection مربوط به `employees` تنظیم می‌کند.

اگر هدفمان Batch Fetching برای Departmentهایی باشد که از Employee دریافت می‌کنیم، باید Annotation را روی Entity مقصد قرار دهیم:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
@Entity
@BatchSize(size = 10)
public class Department {

    @Id
    private Long id;

    private String name;
}
```
</div>


در این حالت Hibernate می‌تواند چند Department Proxy موجود در Persistence Context را هنگام نیاز، گروهی Load کند.

## **4. تنظیم Batch Fetching به‌صورت Global**

به‌جای اینکه روی هر Entity از `@BatchSize` استفاده کنی، می‌توانی یک مقدار پیش‌فرض در تنظیمات Hibernate تعریف کنی:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
spring.jpa.properties.hibernate.default_batch_fetch_size=16
```
</div>


حالا Hibernate می‌تواند برای Associationهای Lazy، تا اندازه Batch مشخص‌شده را گروهی Load کند.

توجه کن که این تنظیم به معنی اجرای خودکار یک Query برای همه Relationshipها نیست؛ فقط هنگام نیاز به Load شدن داده‌های Lazy و وجود موارد مناسب در Persistence Context استفاده می‌شود.

## **5. Batch Fetching برای Collectionها**

فرض کن هر Department چندین Employee دارد:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
@Entity
public class Department {

    @Id
    private Long id;

    @OneToMany(mappedBy = "department", fetch = FetchType.LAZY)
    private List<Employee> employees;
}
```
</div>


و:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
@Entity
@BatchSize(size = 5)
public class Department {

    // ...
}
```
</div>


اگر پنج Department در Persistence Context موجود باشند و Collection مربوط به آن‌ها را یکی‌یکی بخوانیم، Hibernate می‌تواند Collectionهای چند Department را با یک Query دریافت کند.

SQL احتمالی:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
SELECT *
FROM EMPLOYEE
WHERE DEPARTMENT_ID IN (1, 2, 3, 4, 5);
```
</div>


به این ترتیب به‌جای پنج Query مجزا، ممکن است یک Query اجرا شود.

## **6. تفاوت Batch Fetching با JOIN FETCH**

این دو روش برای کاهش Queryهای اضافی استفاده می‌شوند، اما مکانیزم متفاوتی دارند.

| ویژگی        | Batch Fetching          | JOIN FETCH                         |
| ------------ | ----------------------- | ---------------------------------- |
| روش          | چند ID در یک `IN`       | JOIN در همان Query                 |
| زمان Load    | هنگام دسترسی Lazy       | از ابتدا                           |
| Lazy Loading | حفظ می‌شود              | Association انتخاب‌شده Load می‌شود |
| مناسب برای   | تعداد زیادی Association | Relationshipهای مشخص و موردنیاز    |
| مشکل احتمالی | تعداد Batchهای متعدد    | تکرار Rowها و Cartesian Explosion  |

### **مثال JOIN FETCH**
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
@Query("""
    SELECT e
    FROM Employee e
    JOIN FETCH e.department
""")
List<Employee> findAllWithDepartment();
```
</div>


SQL احتمالی:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
SELECT E.*, D.*
FROM EMPLOYEE E
JOIN DEPARTMENT D
    ON E.DEPARTMENT_ID = D.ID;
```
</div>


در این روش Employee و Department با یک Query دریافت می‌شوند.

**چه زمانی کدام را انتخاب کنیم؟**

- اگر دقیقاً می‌دانم کدام Relationship را لازم دارم: معمولاً `JOIN FETCH` یا `EntityGraph`.
- اگر تعداد زیادی Proxy یا Collection به‌صورت Lazy در Persistence Context وجود دارند: Batch Fetching انتخاب مناسبی است.
- اگر Collectionهای چندسطحی یا چندگانه داریم: باید مراقب تکرار Rowها و Cartesian Explosion در JOIN باشیم.

## **7. تفاوت Batch Fetching با JDBC Batch**

این نکته در مصاحبه Senior بسیار مهم است.

| ویژگی  | Batch Fetching                    | JDBC Batch                     |
| ------ | --------------------------------- | ------------------------------ |
| هدف    | کاهش Queryهای SELECT              | کاهش Round Trip برای DML       |
| عملیات | عمدتاً SELECT                     | INSERT / UPDATE / DELETE       |
| مثال   | دریافت 10 Department با یک SELECT | ارسال 100 INSERT به‌صورت Batch |
| تنظیم  | `@BatchSize`                      | `hibernate.jdbc.batch_size`    |

مثلاً JDBC Batch:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
spring.jpa.properties.hibernate.jdbc.batch_size=50
```
</div>


این تنظیم برای بهینه‌سازی عملیات نوشتن گروهی است و با `@BatchSize` یکسان نیست.

## **8. پاسخ آماده مصاحبه Senior Java**

**What problem does Batch Fetching solve in Hibernate?**
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


> Batch Fetching is a Hibernate optimization technique used to reduce the N+1 select problem.
>
> Instead of executing a separate SELECT statement for every lazy-loaded association, Hibernate loads multiple entities or collections using a single SQL query with an IN clause.
>
> We can configure it using `@BatchSize` on an entity or collection, or globally using `hibernate.default_batch_fetch_size`.
>
> Unlike JOIN FETCH, Batch Fetching preserves lazy loading and loads associations in groups when they are accessed.
</div>


## **خلاصه مهم**

- `@BatchSize(10)` یعنی Hibernate می‌تواند حداکثر 10 مورد مناسب را در یک Batch بارگذاری کند.
- Batch Fetching تعداد Queryهای Lazy Loading را کاهش می‌دهد.
- `JOIN FETCH` داده‌ها را از ابتدا با JOIN دریافت می‌کند.
- JDBC Batch برای بهینه‌سازی INSERT، UPDATE و DELETE است، نه حل N+1 در SELECT.

**سؤال تمرینی:** اگر 1000 کارمند داشته باشیم، 10 Department مختلف داشته باشیم و `@BatchSize(10)` تعریف کنیم، آیا Hibernate الزاماً 100 Query اجرا می‌کند؟ چرا؟
</div>




<!-- SOURCE: 16.md -->
# File: 16.md

<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">

مهاجرت از **EclipseLink به Hibernate یا برعکس** را یک پروژه Migration در لایه Persistence در نظر می‌گیرم، نه صرفاً تعویض یک Dependency در `pom.xml`.

چون هر دو Provider استاندارد JPA را پیاده‌سازی می‌کنند، اگر کد پروژه کاملاً مبتنی بر JPA باشد، مهاجرت ساده‌تر است؛ اما تفاوت‌های رفتاری، APIهای اختصاصی و SQL Generation می‌توانند ریسک ایجاد کنند.

من این مهاجرت را به‌صورت مرحله‌ای انجام می‌دهم.

# **1. مرحله اول: بررسی وابستگی به Provider قبلی**

اول بررسی می‌کنم که پروژه چقدر به امکانات اختصاصی Provider فعلی وابسته است.

مثلاً اگر از Hibernate به EclipseLink مهاجرت کنیم، دنبال موارد زیر می‌گردم:

| Hibernate-specific          | توضیح                   |
| --------------------------- | ----------------------- |
| `Session`                   | API اختصاصی Hibernate   |
| `SessionFactory`            | مدیریت Sessionها        |
| `Hibernate.initialize()`    | مقداردهی Proxy          |
| `Hibernate.isInitialized()` | بررسی Lazy Loading      |
| `@Fetch`                    | تنظیم Fetch Strategy    |
| `@BatchSize`                | تنظیم Batch Fetching    |
| `@Type`                     | تعریف Custom Type       |
| HQL                         | Query اختصاصی Hibernate |
| Hibernate Interceptors      | مدیریت رویدادهای داخلی  |

مثلاً این کد:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
Session session =
    entityManager.unwrap(Session.class);

session.createQuery(
    "from Employee",
    Employee.class
).list();
```
</div>


به Hibernate وابسته است.

نسخه استاندارد JPA:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
List<Employee> employees =
    entityManager.createQuery(
        "SELECT e FROM Employee e",
        Employee.class
    ).getResultList();
```
</div>


هرچه کد بیشتر از `EntityManager`، JPQL و Annotationهای استاندارد JPA استفاده کند، مهاجرت آسان‌تر خواهد بود.

# **2. مرحله دوم: بررسی Entity Mappingها**

تمام Entityها و Relationshipها را بررسی می‌کنم.

موارد مهم:

- `@OneToOne`
- `@OneToMany`
- `@ManyToOne`
- `@ManyToMany`
- `@EmbeddedId`
- `@Inheritance`
- `@Version`
- `@Convert`
- `@ElementCollection`
- Cascade و Orphan Removal
- Lazy و Eager Loading

مثلاً:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
@Entity
public class Employee {

    @Id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DEPARTMENT_ID")
    private Department department;
}
```
</div>


این Mapping استاندارد JPA است و اصولاً در هر دو Provider قابل استفاده است.

اما باید رفتار واقعی آن را آزمایش کنم:

- آیا Lazy Loading درست کار می‌کند؟
- آیا Proxyها درست Initialize می‌شوند؟
- آیا Cascade رفتار مورد انتظار دارد؟
- آیا Composite Keyها درست مدیریت می‌شوند؟
- آیا `equals()` و `hashCode()` با Proxyها مشکلی ندارند؟

**نکته:** استاندارد بودن Annotation تضمین نمی‌کند تمام جزئیات رفتار دو Provider یکسان باشد.

# **3. مرحله سوم: بررسی Queryها و SQL Generation**

یکی از مهم‌ترین قسمت‌های مهاجرت، بررسی Queryها است.

فرض کن Query زیر را داریم:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
@Query("""
    SELECT e
    FROM Employee e
    JOIN e.department d
    WHERE d.name = :name
""")
List<Employee> findByDepartmentName(String name);
```
</div>


این Query از JPQL استاندارد استفاده می‌کند و در هر دو Provider قابل استفاده است.

اما باید SQL تولیدشده را بررسی کنیم.

مثلاً:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
SELECT E.*
FROM EMPLOYEE E
JOIN DEPARTMENT D
    ON E.DEPARTMENT_ID = D.ID
WHERE D.NAME = ?
```
</div>


Provider جدید ممکن است SQL متفاوتی تولید کند.

مواردی که بررسی می‌کنم:

1. تعداد Queryها
2. نوع JOINها
3. نحوه Pagination
4. نحوه اجرای Count Query
5. Native Queryها
6. Result Mapping
7. Execution Plan در Oracle
8. استفاده از Indexها

ممکن است Query از نظر Functional درست باشد، اما Performance آن افت کند.

# **4. مرحله چهارم: بررسی Transaction و Persistence Context**

در این مرحله رفتار Transactionها را مقایسه می‌کنم.

مثلاً:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
@Transactional
public void updateEmployee(Long id) {

    Employee employee =
        entityManager.find(Employee.class, id);

    employee.setName("Ali");
}
```
</div>


در هر دو Provider، Dirty Checking از قابلیت‌های استاندارد Persistence است.

اما باید رفتار موارد زیر را بررسی کنم:

- Flush Mode
- Flush Timing
- Dirty Checking
- Optimistic Locking
- Pessimistic Locking
- `@Version`
- Transaction Rollback
- Entity Lifecycle Callbacks

همچنین بررسی می‌کنم که خطاهای Constraint در چه زمانی رخ می‌دهند.

# **5. مرحله پنجم: بررسی Cache و Lazy Loading**

این قسمت اهمیت زیادی دارد، چون Hibernate و EclipseLink در معماری Cache و نحوه مدیریت Lazy Loading تفاوت‌هایی دارند.

| موضوع              | موارد قابل بررسی               |
| ------------------ | ------------------------------ |
| First-Level Cache  | رفتار Persistence Context      |
| Second-Level Cache | Configuration و Cache Provider |
| Query Cache        | قابلیت‌ها و تنظیمات اختصاصی    |
| Lazy Loading       | رفتار Proxy و Indirection      |
| Batch Fetching     | تنظیمات و Annotationها         |
| Cache Invalidation | هماهنگی با Database            |
| Entity Graph       | رفتار Fetch Plan               |

مثلاً Hibernate:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
@Cache(
    usage = CacheConcurrencyStrategy.READ_ONLY
)
```
</div>


این Annotation اختصاصی Hibernate است و در EclipseLink قابل استفاده نیست.

در EclipseLink برای Cache از امکانات اختصاصی خودش استفاده می‌شود.

بنابراین فقط فعال کردن Cache در Provider جدید کافی نیست؛ باید رفتار آن را نیز آزمایش کرد.

# **6. مرحله ششم: تنظیمات و Dependencyها**

اگر پروژه Spring Boot باشد، باید Dependency و تنظیمات Provider را تغییر دهیم.

مثلاً برای مهاجرت به Hibernate در پروژه‌ای که EclipseLink دارد، ابتدا Dependencyهای EclipseLink را حذف و وابستگی‌های سازگار Hibernate را اضافه می‌کنیم.

نمونه تنظیمات Spring Boot:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
spring.jpa.properties.hibernate.show_sql=true

spring.jpa.properties.hibernate.format_sql=true

spring.jpa.properties.hibernate.default_batch_fetch_size=16
```
</div>


اگر بخواهیم Provider را صریحاً مشخص کنیم:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
spring.jpa.database-platform=org.hibernate.dialect.OracleDialect
```
</div>


البته در نسخه‌های جدید Spring Boot و Hibernate، معمولاً تشخیص Dialect از طریق JDBC Metadata انجام می‌شود و تعیین دستی آن همیشه ضروری نیست.

در سمت EclipseLink نیز تنظیمات مخصوص خودش را دارد:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
spring.jpa.properties.eclipselink.logging.level=FINE

spring.jpa.properties.eclipselink.weaving=true
```
</div>


نکته مهم این است که نسخه Spring Boot، Jakarta Persistence و Provider باید با یکدیگر سازگار باشند.

مثلاً نباید بدون بررسی Compatibility، Dependencyهای مبتنی بر `javax.persistence` و `jakarta.persistence` را با هم ترکیب کنیم.

# **7. مرحله هفتم: تست Migration**

من فقط به Unit Test اکتفا نمی‌کنم.

تست‌های زیر را اجرا می‌کنم:

**Functional Tests**

بررسی CRUD، Relationshipها، Cascade و Entity Lifecycle.

**Integration Tests**

تست Repositoryها با همان Database واقعی پروژه، مثلاً Oracle.

**Performance Tests**

مقایسه تعداد Queryها، Response Time، Memory و Execution Plan.

**Consistency Tests**

تست Transaction، Rollback، Locking و Cache Invalidation.

به‌خصوص اگر سیستم Production و دیتابیس Oracle باشد، حتماً Queryهای پرتکرار و سنگین را قبل و بعد از مهاجرت مقایسه می‌کنم.

# **8. مرحله هشتم: اجرای تدریجی در Production**

من ترجیح می‌دهم مهاجرت را یک‌باره و بدون امکان Rollback انجام ندهم.

استراتژی پیشنهادی:

\#chatgpt-mermaid-\_r_54k\_{font-family:-apple-system-body,ui-sans-serif,-apple-system,system-ui,"Segoe UI",Helvetica,"Apple Color Emoji",Arial,sans-serif,"Segoe UI Emoji","Segoe UI Symbol";font-size:16px;fill:rgb(237, 237, 237);}@keyframes edge-animation-frame{from{stroke-dashoffset:0;}}@keyframes dash{to{stroke-dashoffset:0;}}#chatgpt-mermaid-\_r_54k\_ .edge-animation-slow{stroke-dasharray:9,5!important;stroke-dashoffset:900;animation:dash 50s linear infinite;stroke-linecap:round;}#chatgpt-mermaid-\_r_54k\_ .edge-animation-fast{stroke-dasharray:9,5!important;stroke-dashoffset:900;animation:dash 20s linear infinite;stroke-linecap:round;}#chatgpt-mermaid-\_r_54k\_ .error-icon{fill:rgb(48, 48, 48);}#chatgpt-mermaid-\_r_54k\_ .error-text{fill:rgb(237, 237, 237);stroke:rgb(237, 237, 237);}#chatgpt-mermaid-\_r_54k\_ .edge-thickness-normal{stroke-width:1px;}#chatgpt-mermaid-\_r_54k\_ .edge-thickness-thick{stroke-width:3.5px;}#chatgpt-mermaid-\_r_54k\_ .edge-pattern-solid{stroke-dasharray:0;}#chatgpt-mermaid-\_r_54k\_ .edge-thickness-invisible{stroke-width:0;fill:none;}#chatgpt-mermaid-\_r_54k\_ .edge-pattern-dashed{stroke-dasharray:3;}#chatgpt-mermaid-\_r_54k\_ .edge-pattern-dotted{stroke-dasharray:2;}#chatgpt-mermaid-\_r_54k\_ .marker{fill:rgb(175, 175, 175);stroke:rgb(175, 175, 175);}#chatgpt-mermaid-\_r_54k\_ .marker.cross{stroke:rgb(175, 175, 175);}#chatgpt-mermaid-\_r_54k\_ svg{font-family:-apple-system-body,ui-sans-serif,-apple-system,system-ui,"Segoe UI",Helvetica,"Apple Color Emoji",Arial,sans-serif,"Segoe UI Emoji","Segoe UI Symbol";font-size:16px;}#chatgpt-mermaid-\_r_54k\_ p{margin:0;}#chatgpt-mermaid-\_r_54k\_ .label{font-family:-apple-system-body,ui-sans-serif,-apple-system,system-ui,"Segoe UI",Helvetica,"Apple Color Emoji",Arial,sans-serif,"Segoe UI Emoji","Segoe UI Symbol";color:rgb(237, 237, 237);}#chatgpt-mermaid-\_r_54k\_ .cluster-label text{fill:rgb(237, 237, 237);}#chatgpt-mermaid-\_r_54k\_ .cluster-label span{color:rgb(237, 237, 237);}#chatgpt-mermaid-\_r_54k\_ .cluster-label span p{background-color:transparent;}#chatgpt-mermaid-\_r_54k\_ .label text,#chatgpt-mermaid-\_r_54k\_ span{fill:rgb(237, 237, 237);color:rgb(237, 237, 237);}#chatgpt-mermaid-\_r_54k\_ .node rect,#chatgpt-mermaid-\_r_54k\_ .node circle,#chatgpt-mermaid-\_r_54k\_ .node ellipse,#chatgpt-mermaid-\_r_54k\_ .node polygon,#chatgpt-mermaid-\_r_54k\_ .node path{fill:rgb(9, 23, 44);stroke:rgb(31, 78, 148);stroke-width:1px;}#chatgpt-mermaid-\_r_54k\_ .rough-node .label text,#chatgpt-mermaid-\_r_54k\_ .node .label text,#chatgpt-mermaid-\_r_54k\_ .image-shape .label,#chatgpt-mermaid-\_r_54k\_ .icon-shape .label{text-anchor:middle;}#chatgpt-mermaid-\_r_54k\_ .node .katex path{fill:#000;stroke:#000;stroke-width:1px;}#chatgpt-mermaid-\_r_54k\_ .rough-node .label,#chatgpt-mermaid-\_r_54k\_ .node .label,#chatgpt-mermaid-\_r_54k\_ .image-shape .label,#chatgpt-mermaid-\_r_54k\_ .icon-shape .label{text-align:center;}#chatgpt-mermaid-\_r_54k\_ .node.clickable{cursor:pointer;}#chatgpt-mermaid-\_r_54k\_ .root .anchor path{fill:rgb(175, 175, 175)!important;stroke-width:0;stroke:rgb(175, 175, 175);}#chatgpt-mermaid-\_r_54k\_ .arrowheadPath{fill:rgb(175, 175, 175);}#chatgpt-mermaid-\_r_54k\_ .edgePath .path{stroke:rgb(175, 175, 175);stroke-width:1px;}#chatgpt-mermaid-\_r_54k\_ .flowchart-link{stroke:rgb(175, 175, 175);fill:none;}#chatgpt-mermaid-\_r_54k\_ .edgeLabel{background-color:rgb(0, 0, 0);text-align:center;}#chatgpt-mermaid-\_r_54k\_ .edgeLabel p{background-color:rgb(0, 0, 0);}#chatgpt-mermaid-\_r_54k\_ .edgeLabel rect{opacity:0.5;background-color:rgb(0, 0, 0);fill:rgb(0, 0, 0);}#chatgpt-mermaid-\_r_54k\_ .labelBkg{background-color:rgba(0, 0, 0, 0.5);}#chatgpt-mermaid-\_r_54k\_ .cluster rect{fill:rgb(48, 48, 48);stroke:rgba(255, 255, 255, 0.15);stroke-width:1px;}#chatgpt-mermaid-\_r_54k\_ .cluster text{fill:rgb(237, 237, 237);}#chatgpt-mermaid-\_r_54k\_ .cluster span{color:rgb(237, 237, 237);}#chatgpt-mermaid-\_r_54k\_ div.mermaidTooltip{position:absolute;text-align:center;max-width:200px;padding:2px;font-family:-apple-system-body,ui-sans-serif,-apple-system,system-ui,"Segoe UI",Helvetica,"Apple Color Emoji",Arial,sans-serif,"Segoe UI Emoji","Segoe UI Symbol";font-size:12px;background:rgb(48, 48, 48);border:1px solid rgba(255, 255, 255, 0.15);border-radius:2px;pointer-events:none;z-index:100;}#chatgpt-mermaid-\_r_54k\_ .flowchartTitleText{text-anchor:middle;font-size:18px;fill:rgb(237, 237, 237);}#chatgpt-mermaid-\_r_54k\_ rect.text{fill:none;stroke-width:0;}#chatgpt-mermaid-\_r_54k\_ .icon-shape,#chatgpt-mermaid-\_r_54k\_ .image-shape{background-color:rgb(0, 0, 0);text-align:center;}#chatgpt-mermaid-\_r_54k\_ .icon-shape p,#chatgpt-mermaid-\_r_54k\_ .image-shape p{background-color:rgb(0, 0, 0);padding:2px;}#chatgpt-mermaid-\_r_54k\_ .icon-shape .label rect,#chatgpt-mermaid-\_r_54k\_ .image-shape .label rect{opacity:0.5;background-color:rgb(0, 0, 0);fill:rgb(0, 0, 0);}#chatgpt-mermaid-\_r_54k\_ .label-icon{display:inline-block;height:1em;overflow:visible;vertical-align:-0.125em;}#chatgpt-mermaid-\_r_54k\_ .node .label-icon path{fill:currentColor;stroke:revert;stroke-width:revert;}#chatgpt-mermaid-\_r_54k\_ .node .neo-node{stroke:rgb(31, 78, 148);}#chatgpt-mermaid-\_r_54k\_ [data-look="neo"].node rect,#chatgpt-mermaid-\_r_54k\_ [data-look="neo"].cluster rect,#chatgpt-mermaid-\_r_54k\_ [data-look="neo"].node polygon{stroke:url(#chatgpt-mermaid-\_r_54k\_-gradient);filter:drop-shadow( 1px 2px 2px rgba(185,185,185,1));}#chatgpt-mermaid-\_r_54k\_ [data-look="neo"].swimlane.cluster rect{filter:none;}#chatgpt-mermaid-\_r_54k\_ [data-look="neo"].node path{stroke:url(#chatgpt-mermaid-\_r_54k\_-gradient);stroke-width:1px;}#chatgpt-mermaid-\_r_54k\_ [data-look="neo"].node .outer-path{filter:drop-shadow( 1px 2px 2px rgba(185,185,185,1));}#chatgpt-mermaid-\_r_54k\_ [data-look="neo"].node .neo-line path{stroke:rgb(31, 78, 148);filter:none;}#chatgpt-mermaid-\_r_54k\_ [data-look="neo"].node circle{stroke:url(#chatgpt-mermaid-\_r_54k\_-gradient);filter:drop-shadow( 1px 2px 2px rgba(185,185,185,1));}#chatgpt-mermaid-\_r_54k\_ [data-look="neo"].node circle .state-start{fill:#000000;}#chatgpt-mermaid-\_r_54k\_ [data-look="neo"].icon-shape .icon{fill:url(#chatgpt-mermaid-\_r_54k\_-gradient);filter:drop-shadow( 1px 2px 2px rgba(185,185,185,1));}#chatgpt-mermaid-\_r_54k\_ [data-look="neo"].icon-shape .icon-neo path{stroke:url(#chatgpt-mermaid-\_r_54k\_-gradient);filter:drop-shadow( 1px 2px 2px rgba(185,185,185,1));}#chatgpt-mermaid-\_r_54k\_ .node text{font-size:14px;font-weight:600;letter-spacing:normal;fill:rgb(153, 206, 255);}#chatgpt-mermaid-\_r_54k\_ .edgeLabels text{font-size:13px;font-weight:600;letter-spacing:-0.08px;fill:rgb(153, 206, 255);}#chatgpt-mermaid-\_r_54k\_ .node tspan[font-weight="normal"],#chatgpt-mermaid-\_r_54k\_ .edgeLabels tspan[font-weight="normal"]{font-weight:600;}#chatgpt-mermaid-\_r_54k\_ .edgeLabel .label rect{opacity:1;rx:13px;ry:13px;fill:rgb(0, 14, 26);stroke:rgb(26, 62, 95);stroke-width:1px;}#chatgpt-mermaid-\_r_54k\_ .node rect,#chatgpt-mermaid-\_r_54k\_ .node circle,#chatgpt-mermaid-\_r_54k\_ .node ellipse,#chatgpt-mermaid-\_r_54k\_ .node polygon,#chatgpt-mermaid-\_r_54k\_ .node path{fill:rgb(0, 40, 77);stroke:rgba(255, 255, 255, 0.1);stroke-width:1px;}#chatgpt-mermaid-\_r_54k\_ .node rect{rx:16px;ry:16px;}#chatgpt-mermaid-\_r_54k\_ .mermaid-decision .label-container{fill:rgb(0, 14, 26);stroke:rgb(26, 62, 95);stroke-dasharray:2,2;}#chatgpt-mermaid-\_r_54k\_ .edgePaths .flowchart-link{stroke:rgb(175, 175, 175);stroke-width:1px;stroke-linecap:round;stroke-linejoin:round;}#chatgpt-mermaid-\_r_54k\_ .marker{fill:rgb(175, 175, 175);stroke:rgb(175, 175, 175);}#chatgpt-mermaid-\_r_54k\_ :root{--mermaid-font-family:-apple-system-body,ui-sans-serif,-apple-system,system-ui,"Segoe UI",Helvetica,"Apple Color Emoji",Arial,sans-serif,"Segoe UI Emoji","Segoe UI Symbol";}Existing Provider: EclipseLinkDependency and MappingAnalysisReplace Provider in TestEnvironmentRun Integration TestsCompare SQL and PerformanceAll Tests Passed?Fix Compatibility IssuesDeploy to StagingProduction Canary / ControlledReleaseProduction Healthy?RollbackComplete MigrationNoYesNoYes

یک نکته مهم: من در یک Application واحد، دو Provider را هم‌زمان روی یک Persistence Unit فعال نمی‌کنم. برای مقایسه می‌توان از دو محیط تست یا دو نسخه جداگانه Application استفاده کرد.

# **9. چک‌لیست واقعی Migration**

| مورد                  | اقدام                 |
| --------------------- | --------------------- |
| Provider-specific API | حذف یا جایگزینی       |
| Entity Mapping        | تست Compatibility     |
| JPQL / HQL            | بررسی Queryها         |
| Native SQL            | تست Result Mapping    |
| Lazy Loading          | تست Proxyها           |
| Dirty Checking        | تست Update            |
| Cascade               | تست Relationshipها    |
| L1 / L2 Cache         | بازبینی Configuration |
| Batch Fetching        | تنظیم مجدد            |
| Transaction           | تست Commit و Rollback |
| Sequence / Identity   | تست تولید ID          |
| Schema Generation     | بررسی DDL             |
| Performance           | مقایسه Execution Plan |
| Production            | Canary و Rollback     |

# **10. پاسخ آماده برای مصاحبه Senior Java**

اگر مصاحبه‌گر بپرسد:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


**How would you migrate from EclipseLink to Hibernate or vice versa?**
</div>


این پاسخ مناسبی است:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


I would treat the migration as a persistence-layer migration rather than simply replacing a dependency.

First, I would identify provider-specific APIs, annotations, configurations, custom types, and query extensions.

Second, I would review entity mappings, relationships, lazy loading, cascading, transaction management, and caching behavior to identify compatibility issues.

Third, I would replace the provider in a test environment and run integration tests against the actual database.

I would also compare generated SQL, query execution plans, and application performance because two JPA providers may generate different SQL for the same JPQL query.

Finally, I would deploy incrementally, monitor the application, and maintain a rollback strategy.

My main goal would be to preserve functional correctness, transaction consistency, and performance throughout the migration.
</div>


## **نکته‌ای که در مصاحبه باید برجسته کنی**
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


**JPA Portability does not guarantee Behavioral or Performance Portability.**
</div>


یعنی حتی اگر کد کاملاً استاندارد JPA باشد، باز هم ممکن است با تغییر Provider:

- SQL متفاوت تولید شود.
- Lazy Loading رفتار متفاوتی داشته باشد.
- Performance افت کند.
- Cache نیاز به بازطراحی داشته باشد.
- تنظیمات Transaction تغییر کنند.

بنابراین موفقیت Migration فقط به Compile شدن پروژه وابسته نیست؛ باید Functional Correctness و Performance نیز حفظ شوند.

**سؤال تمرینی بعدی:** اگر بعد از مهاجرت از EclipseLink به Hibernate، تعداد Queryهای یک صفحه از 5 به 150 افزایش پیدا کند، چطور تشخیص می‌دهی مشکل از N+1، Fetch Strategy یا Cache است؟

</div>




<!-- SOURCE: 2.md -->
# File: 2.md

<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">


این چهار متد همه روی `EntityManager` کار می‌کنند و اثر اصلی‌شان روی **Persistence Context** است، نه مستقیماً روی دیتابیس. SQL معمولاً در زمان **flush/commit** اجرا می‌شود.

## خلاصهٔ مقایسه

| متد | ورودی معمول | اثر روی وضعیت | SQL | مقدار برگشتی | کاربرد اصلی |
|---|---|---|---|---|---|
| `persist` | New | New → Managed | INSERT در flush | `void` | ثبت Entity جدید |
| `merge` | Detached/New/هر حالت | یک **کپی Managed** برمی‌گرداند | SELECT/INSERT/UPDATE در flush | Entity Managed | برگرداندن Detached به Managed |
| `refresh` | Managed | وضعیت از DB دوباره خوانده می‌شود | SELECT | `void` | دور ریختن تغییرات و همگام‌سازی با DB |
| `detach` | Managed | Managed → Detached | ندارد | `void` | توقف Track کردن یک Entity |

---

## 1. `persist`
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```java
em.persist(entity);
```
</div>


- Entity جدید (New/Transient) را **Managed** می‌کند.
- INSERT معمولاً در flush یا commit انجام می‌شود؛ نه لزوماً همان لحظه.
- مقدار برگشتی ندارد (`void`).
- اگر Entity از قبل Managed باشد، معمولاً عملیات نادیده گرفته می‌شود.
- اگر Entity Detached باشد، معمولاً خطا می‌دهد؛ مثل `EntityExistsException` (ممکن است در persist یا flush).
- اگر Entity در وضعیت Removed باشد، طبق spec می‌تواند دوباره Managed شود، ولی معمولاً توصیه نمی‌شود.
- `CascadeType.PERSIST` به روابط اعمال می‌شود.
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```java
User u = new User();  // New
em.persist(u);        // u حالا Managed است
u.setName("Ali");     // تغییرات Track می‌شود
```
</div>


نکتهٔ مهم: `persist` خود شیء را Managed می‌کند و همان شیء را تغییر وضعیت می‌دهد.

---

## 2. `merge`
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```java
User managed = em.merge(detachedUser);
```
</div>


- برای برگرداندن Entity Detached به چرخهٔ Managed استفاده می‌شود.
- **خود شیء ورودی را Managed نمی‌کند**؛ یک **کپی Managed** برمی‌گرداند.
- اگر Entity Detached باشد، JPA ممکن است اول آن را از DB بخواند، سپس وضعیت شیء ورودی را روی کپی Managed اعمال کند و در flush UPDATE بزند.
- اگر Entity New باشد، یک نمونهٔ Managed جدید ساخته می‌شود و persist می‌شود.
- اگر Entity از قبل Managed باشد، معمولاً همان نمونه یا نمونهٔ Managed مربوطه برگردانده می‌شود.
- مقدار برگشتی را باید استفاده کنید:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```java
User detached = ...;
User managed = em.merge(detached);
managed.setName("New Name"); // این تغییر Track می‌شود
// detached همچنان Detached است
```
</div>


- `CascadeType.MERGE` به روابط اعمال می‌شود.
- `merge` برای New هم کار می‌کند، ولی معمولاً برای New از `persist` استفاده می‌شود.

نکتهٔ مهم: اگر نتیجهٔ `merge` را نادیده بگیرید، تغییرات بعدی روی شیء Detached Track نمی‌شوند.

---

## 3. `refresh`
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```java
em.refresh(managedEntity);
```
</div>


- فقط روی Entity **Managed** معنا دارد.
- وضعیت فعلی Entity را از دیتابیس دوباره می‌خواند و روی همان نمونهٔ Managed می‌نویسد.
- تغییرات ذخیره‌نشدهٔ فعلی را دور می‌ریزد.
- برای دیدن تغییرات خارجی در DB یا بازگرداندن وضعیت به آخرین مقدار DB مفید است.
- مقدار برگشتی ندارد (`void`).
- در صورت نبود رکورد در DB معمولاً `EntityNotFoundException` می‌دهد.
- روی Entity Detached معمولاً `IllegalArgumentException` می‌دهد؛ برای Detached از `find` یا `merge` استفاده کنید.
- می‌تواند LockMode بگیرد:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```java
em.refresh(entity, LockModeType.OPTIMISTIC);
```
</div>


- `CascadeType.REFRESH` به روابط اعمال می‌شود.
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```java
User u = em.find(User.class, 1L); // Managed
u.setName("Temp");
em.refresh(u); // Name دوباره از DB خوانده می‌شود؛ Temp دور ریخته می‌شود
```
</div>


تفاوت با `find`: `find` اگر Entity در Persistence Context باشد، همان را برمی‌گرداند و ممکن است به DB نزند؛ ولی `refresh` حتماً وضعیت را از DB بازخوانی می‌کند.

---

## 4. `detach`
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```java
em.detach(entity);
```
</div>


- Entity Managed را از Persistence Context خارج می‌کند و به **Detached** تبدیل می‌کند.
- هیچ SQL یا عملیات دیتابیسی انجام نمی‌دهد.
- مقدار برگشتی ندارد (`void`).
- بعد از detach، تغییرات Entity به‌صورت خودکار Track و ذخیره نمی‌شوند.
- اگر قبل از flush detach کنید، تغییرات ذخیره‌نشده از دست می‌روند.
- اگر تغییرات قبلاً flush شده باشند، در DB باقی می‌مانند.
- `CascadeType.DETACH` به روابط اعمال می‌شود.
- `detach` یک Entity را خارج می‌کند؛ `clear()` همه را Detached می‌کند و `close()` کل EntityManager را می‌بندد.
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```java
User u = em.find(User.class, 1L); // Managed
em.detach(u);                     // Detached
u.setName("Ali");                 // ذخیره نمی‌شود
```
</div>


`detach` با `remove` فرق دارد: `remove` برای حذف علامت‌گذاری می‌کند، اما `detach` فقط Track شدن را متوقف می‌کند.

---

## قاعدهٔ انتخاب سریع

- Entity جدید داری؟ → `persist`
- Entity Detached داری و می‌خواهی تغییراتش ذخیره شود؟ → `merge` و **نتیجه‌اش را استفاده کن**
- می‌خواهی تغییرات فعلی را دور بریزی و از DB بخوانی؟ → `refresh`
- می‌خواهی Entity دیگر Track نشود / read-only شود؟ → `detach`
- می‌خواهی حذف شود؟ → `remove`

نکتهٔ کلی: همهٔ این وضعیت‌ها نسبت به یک Persistence Context فعال معنا دارند. بعد از commit یا close شدن EntityManager، Entityهای Managed معمولاً Detached می‌شوند.
</div>




<!-- SOURCE: 3.md -->
# File: 3.md

<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">

**Persistence Context** یک **محیط مدیریت‌شده و درون‌حافظه‌ای** است که موجودیت‌ها (Entities) را در طول یک واحد کاری (معمولاً یک تراکنش) نگه می‌دارد و وضعیت آن‌ها را ردیابی می‌کند. این مفهوم، هستهٔ اصلی نحوهٔ کار `EntityManager` در JPA است.

---

## Persistence Context چیست؟

هر `EntityManager` به یک Persistence Context متصل است که محدودهٔ ساخت، ذخیره و حذف موجودیت‌ها را تعریف می‌کند. در واقع، Persistence Context مجموعه‌ای از نمونه‌های Managed است که برای هر شناسهٔ یکتا، فقط **یک نمونهٔ یکتا** در آن وجود دارد.

این مجموعه عملاً به عنوان **کش سطح اول (First-Level Cache)** شناخته می‌شود. وقتی موجودیتی را با `find()` می‌خوانید، JPA ابتدا آن را در Persistence Context جستجو می‌کند و تنها در صورت نبودن، به دیتابیس مراجعه می‌کند. این مکانیزم از رفت‌وبرگشت‌های تکراری به دیتابیس جلوگیری می‌کند.

### انواع Persistence Context
- **Transaction-Scoped**: با پایان تراکنش، Persistence Context نیز پایان می‌یابد. این حالت رایج‌ترین نوع در محیط‌های Jakarta EE است.
- **Extended**: فراتر از یک تراکنش زنده می‌ماند و برای سناریوهای خاص (مثل گفتگوهای طولانی) استفاده می‌شود.

---

## چه زمانی Flush می‌شود؟

Flush فرآیند همگام‌سازی وضعیت موجودیت‌های Managed با دیتابیس است. یعنی JPA دستورات SQL لازم (INSERT/UPDATE/DELETE) را برای اعمال تغییراتِ انباشته‌شده در Persistence Context اجرا می‌کند.

زمان‌های اصلی Flush به **حالت FlushMode** بستگی دارد:

### حالت پیش‌فرض: `FlushModeType.AUTO`
در این حالت (که پیش‌فرض JPA است)، Flush در دو موقعیت اصلی رخ می‌دهد:

1. **قبل از Commit تراکنش**: این یک نقطهٔ تضمین‌شده برای Flush است.
2. **قبل از اجرای یک Query** که ممکن است تحت تأثیر تغییرات درون Persistence Context قرار گیرد. JPA بررسی می‌کند که آیا Query به جدولی اشاره دارد که موجودیتِ تغییریافته‌ای در Persistence Context برای آن وجود دارد یا خیر؛ اگر بله، ابتدا Flush می‌کند تا Query داده‌های به‌روز را ببیند.

### حالت `FlushModeType.COMMIT`
در این حالت، Flush فقط در زمان **Commit تراکنش** انجام می‌شود و قبل از اجرای Query، Flush خودکار رخ نمی‌دهد. این حالت می‌تواند برای کاهش تعداد Flushهای غیرضروری مفید باشد، اما باید با احتیاط استفاده شود.

### Flush دستی
علاوه بر این، می‌توانید با فراخوانی صریح متد `EntityManager.flush()`، JPA را مجبور به همگام‌سازی فوری کنید. این کار زمانی مفید است که نیاز دارید تغییرات قبل از Commit در دیتابیس اعمال شوند (مثلاً برای دریافت یک شناسهٔ تولیدشده یا بررسی یک Constraint).

### نقاط دیگر (مخصوص Hibernate)
برخی پیاده‌سازی‌ها مانند Hibernate ممکن است در نقاط دیگری نیز Flush کنند (مثلاً قبل از اجرای Native Query یا در برخی شرایط خاص)، اما این رفتارها بخشی از مشخصات استاندارد JPA نیستند و به پیاده‌سازی وابسته‌اند.

---

## خلاصه در یک نگاه

- **Persistence Context** = کش سطح اول و محیطی برای ردیابی موجودیت‌های Managed.
- **Flush** = نوشتن تغییرات انباشته‌شده در دیتابیس.
- در حالت **AUTO** (پیش‌فرض): Flush قبل از Commit و قبل از Queryهای مرتبط.
- در حالت **COMMIT**: Flush فقط در زمان Commit.
- **Flush دستی** با `em.flush()` برای همگام‌سازی فوری.

نکتهٔ مهم: Flush باعث **Commit** نمی‌شود؛ تراکنش همچنان باز است و در صورت بروز خطا در Flush، می‌توان تراکنش را Rollback کرد.
</div>




<!-- SOURCE: 4.md -->
# File: 4.md

<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">


مشکل N+1 زمانی رخ می‌دهد که برای بارگذاری N والد، یک کوئری اولیه اجرا شود و سپس برای هر والد، هنگام دسترسی به یک رابطهٔ Lazy، یک کوئری اضافی به دیتابیس ارسال شود. این مسئله در محیط توسعه با داده‌های کم معمولاً دیده نمی‌شود، اما در تولید می‌تواند به‌سرعت تعداد کوئری‌ها را به‌صورت خطی افزایش دهد و منابع را اشغال کند.

---

## چگونه N+1 را تشخیص دهیم؟

### 1. فعال‌سازی لاگ SQL و آمار Hibernate
ساده‌ترین راه، مشاهدهٔ کوئری‌های تکراری است. به‌جای `hibernate.show_sql` که خروجی نامرتب و بدون پارامتر می‌دهد، از Loggerها استفاده کنید:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```properties
# Hibernate 6+
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.orm.jdbc.bind=TRACE

# Hibernate 5
# logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
```
</div>


همچنین `hibernate.generate_statistics=true` را فعال کنید تا بتوانید تعداد کل دستورات SQL را اندازه بگیرید.

### 2. شمارش دستورات SQL در تست‌ها (روش قابل اتکا)
در یک تست یکپارچه‌سازی، تعداد `PreparedStatement` را قبل و بعد از اجرای یک Use Case مقایسه کنید. اگر برای ۵۰ رکورد، ۵۱ کوئری اجرا شود، نشانهٔ N+1 است:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```java
long before = sessionFactory.getStatistics().getPrepareStatementCount();
// ... اجرای متد سرویس ...
long after = sessionFactory.getStatistics().getPrepareStatementCount();
assertTrue((after - before) <= 2); // انتظار: ۱ یا ۲ کوئری، نه ۵۱
```
</div>


نکته: `getQueryExecutionCount()` ممکن است کوئری‌های ثانویه برای مقداردهی Lazy را نشمارد؛ برای شمارش دقیق‌تر از `getPrepareStatementCount()` یا یک DataSource Proxy استفاده کنید.

### 3. ابزارهای تخصصی
- **P6Spy** یا **datasource-proxy**: تمام دستورات JDBC را لاگ می‌کنند و امکان شمارش دقیق را می‌دهند.
- **Query Analyzer**: یک ابزار متن‌باز که در لایهٔ JDBC کوئری‌ها را رهگیری می‌کند و N+1 را با مدل اطمینان تشخیص می‌دهد و از مثبت‌های کاذب (مثل Pagination) جلوگیری می‌کند.
- **JPlusOne** یا **spring-hibernate-query-utils**: ابزارهای سبک‌تر برای تشخیص در محیط توسعه.

### 4. الگوهای مشکوک در کد
- حلقه روی مجموعه‌ای از والدها و دسترسی به یک رابطهٔ Lazy درون حلقه.
- `FetchType.EAGER` روی `@OneToMany` یا `@ManyToMany` که N+1 را به‌صورت بی‌قید و شرط ایجاد می‌کند.
- فعال بودن `spring.jpa.open-in-view=true` که باعث می‌شود N+1 در لایهٔ Serialization پنهان بماند.

---

## چگونه N+1 را رفع کنیم؟

### 1. `JOIN FETCH` (دقیق‌ترین راه‌حل برای یک کوئری خاص)
با `JOIN FETCH`، والد و فرزند در یک SQL بارگذاری می‌شوند و رابطه مستقیماً مقداردهی می‌شود، نه از طریق Proxy:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```java
List<Order> orders = em.createQuery(
    "select o from Order o left join fetch o.lines", Order.class)
    .getResultList();
```
</div>


**محدودیت‌های مهم:**
- در یک کوئری، حداکثر **یک Collection** را می‌توان Fetch کرد؛ دو Collection هم‌زمان باعث `MultipleBagFetchException` یا انفجار تعداد ردیف‌ها می‌شود.
- روی کوئری‌های **Pagination** نباید Collection را `JOIN FETCH` کرد، زیرا Pagination در حافظه اعمال می‌شود و می‌تواند به مشکل عملکردی منجر شود.
- در Hibernate 5 برای حذف والدهای تکراری به `DISTINCT` نیاز است؛ Hibernate 6+ خودش والدها را حذف تکرار می‌کند.

### 2. `@EntityGraph` (طرح Fetch قابل استفادهٔ مجدد)
`EntityGraph` به شما اجازه می‌دهد بدون نوشتن `JOIN FETCH` صریح، مشخص کنید کدام روابط باید در یک کوئری بارگذاری شوند:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```java
EntityGraph<Order> graph = em.createEntityGraph(Order.class);
graph.addAttributeNodes("customer");
graph.addSubgraph("lines").addAttributeNodes("product");

List<Order> orders = em.createQuery("select o from Order o", Order.class)
    .setHint("jakarta.persistence.fetchgraph", graph)
    .getResultList();
```
</div>


تفاوت `fetchgraph` و `loadgraph`: در `fetchgraph`، ویژگی‌های ذکرنشده Lazy در نظر گرفته می‌شوند؛ در `loadgraph`، پیش‌فرض‌های Mapping حفظ می‌شوند.

### 3. Batch Fetching (زمانی که الگوی دسترسی از قبل مشخص نیست)
Batch Fetching به‌جای N کوئری، از یک کوئری `IN (...)` استفاده می‌کند و N+1 را به `1 + N/size` کاهش می‌دهد:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```java
@OneToMany(mappedBy = "order")
@BatchSize(size = 25)
private List<OrderLine> lines;
```
</div>


یا به‌صورت سراسری:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```properties
spring.jpa.properties.hibernate.default_batch_fetch_size=50
```
</div>


این روش **مکمل** است، نه جایگزین کامل `JOIN FETCH`. مزیت اصلی آن این است که وقتی چند Collection دارید و `JOIN FETCH` هم‌زمان باعث Cartesian Product می‌شود، Batch Fetching هر Collection را جداگانه و بهینه بارگذاری می‌کند.

### 4. `@Fetch(FetchMode.SUBSELECT)`
این روش مجموعه را برای **همهٔ والدهای کوئری اصلی** در یک کوئری اضافی بارگذاری می‌کند. برای نتیجهٔ کوچک و کوئری مشخص مناسب است، اما اگر کوئری اصلی سنگین باشد، می‌تواند پرهزینه شود.

### 5. Projection / DTO (بهترین گزینه برای APIهای فقط‌خواندنی)
اگر فقط به چند ستون نیاز دارید، به‌جای بارگذاری Entity کامل، همان ستون‌ها را در یک DTO یا Record پروژه کنید. این کار N+1 را کاملاً حذف می‌کند، حجم دادهٔ منتقل‌شده را کاهش می‌دهد و خطر Dirty Checking ناخواسته را از بین می‌برد:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```java
record OrderSummary(Long id, String customerName, int itemCount) {}

List<OrderSummary> summaries = em.createQuery(
    "select new com.example.OrderSummary(o.id, c.name, size(o.items)) " +
    "from Order o join o.customer c", OrderSummary.class)
    .getResultList();
```
</div>


---

## نکات کلیدی برای جلوگیری از بازگشت مشکل

- **`FetchType.EAGER` را به‌عنوان راه‌حل N+1 استفاده نکنید.** این کار N+1 را به‌صورت بی‌قید و شرط در زمان بارگذاری اجرا می‌کند و انعطاف‌پذیری را از بین می‌برد. همیشه Mapping را `LAZY` نگه دارید و در سطح کوئری با `JOIN FETCH` یا `@EntityGraph` تصمیم بگیرید.
- **`spring.jpa.open-in-view=false` را فعال کنید** تا Lazy Loading خارج از تراکنش و در لایهٔ Serialization باعث N+1 پنهان نشود.
- **در تست‌ها `em.flush()` و `em.clear()` را قبل از assert کردن نتیجه اجرا کنید** تا واقعاً از دیتابیس بخوانید، نه از Cache سطح اول.
- **یک تست Regression برای تعداد SQL اضافه کنید** تا N+1 در آینده دوباره وارد کد نشود.

### انتخاب سریع راه‌حل

| سناریو | راه‌حل پیشنهادی |
|---|---|
| یک Collection و نیاز به Entity کامل | `JOIN FETCH` |
| چند Collection و نیاز به Entity کامل | `@BatchSize` یا `default_batch_fetch_size` (به‌جای چند `JOIN FETCH`) |
| API فقط‌خواندنی با ستون‌های محدود | DTO Projection |
| نیاز به طرح Fetch قابل استفادهٔ مجدد | `@EntityGraph` |
| Pagination + Collection | Batch Fetching (نه `JOIN FETCH`) |

</div>




<!-- SOURCE: 5.md -->
# File: 5.md

<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">


تفاوت اصلی `FetchType.LAZY` و `FetchType.EAGER` در **زمان بارگذاری داده‌های مرتبط** است. این انتخاب یک تصمیم استاتیک در سطح Mapping است که بر عملکرد و رفتار برنامه در هر بار واکشی موجودیت تأثیر می‌گذارد.

---

### 📌 تفاوت بنیادین در تعریف

*   **`FetchType.EAGER` (بارگذاری فوری)**: یک **الزام** برای ارائه‌دهنده Persistence است که داده‌های مرتبط **همزمان با موجودیت اصلی** واکشی شوند. JPA معمولاً این کار را با یک `JOIN` انجام می‌دهد.
*   **`FetchType.LAZY` (بارگذاری تنبل)**: یک **راهنما (Hint)** برای ارائه‌دهنده Persistence است که داده‌های مرتبط **فقط در اولین دسترسی** به آن‌ها واکشی شوند. پیاده‌سازی‌هایی مانند Hibernate معمولاً یک **Proxy** برای رابطه ایجاد می‌کنند و کوئری واقعی زمانی اجرا می‌شود که متدی روی این Proxy فراخوانی شود.

> **نکته**: از آنجا که `LAZY` فقط یک "راهنما" است، پیاده‌سازی مجاز است در برخی شرایط آن را نادیده گرفته و داده را به‌صورت EAGER واکشی کند.

---

### ⚙️ مقادیر پیش‌فرض (Default) در JPA

اگر نوع واکشی را صراحتاً مشخص نکنید، JPA بر اساس نوع رابطه تصمیم می‌گیرد:

| نوع رابطه | FetchType پیش‌فرض |
| :--- | :--- |
| `@OneToOne` | **EAGER** |
| `@ManyToOne` | **EAGER** |
| `@OneToMany` | **LAZY** |
| `@ManyToMany` | **LAZY** |

این پیش‌فرض‌ها منبع بسیاری از مشکلات عملکردی هستند، زیرا روابط "to-one" به‌طور پیش‌فرض EAGER هستند و ممکن است داده‌های غیرضروری زیادی را بارگذاری کنند.

---

### ⚖️ پیامدها و معایب هر کدام

#### پیامدهای `FetchType.EAGER`

*   **واکشی داده‌های غیرضروری**: حتی اگر به رابطه نیاز نداشته باشید، داده‌ها همیشه بارگذاری می‌شوند. این کار حافظه و پهنای باند را هدر می‌دهد.
*   **مشکل N+1 (N+1 Query Problem)**: این مهم‌ترین عیب EAGER است. وقتی یک کوئری `List`ی از والدها را برمی‌گرداند و هر والد رابطه‌ای EAGER دارد، JPA برای **هر والد** یک کوئری جداگانه برای واکشی رابطه اجرا می‌کند. برای N والد، N+1 کوئری اجرا می‌شود که فاجعه‌بار است.
*   **کاهش انعطاف‌پذیری**: نمی‌توانید در زمان اجرای یک کوئری خاص، تصمیم بگیرید که رابطه را EAGER واکشی نکنید. EAGER در سطح Mapping قفل شده است.

#### پیامدهای `FetchType.LAZY`

*   **خطای `LazyInitializationException`**: اگر سعی کنید به یک رابطه LAZY **خارج از محدوده تراکنش فعال** (یا وقتی `EntityManager` بسته شده) دسترسی پیدا کنید، JPA نمی‌تواند کوئری را اجرا کند و این خطا را پرتاب می‌کند. این شایع‌ترین مشکل LAZY است.
*   **احتمال N+1 در صورت استفاده نادرست**: اگر در یک حلقه روی مجموعه‌ای از والدها حرکت کنید و به رابطه LAZY هر کدام دسترسی پیدا کنید، به ازای هر والد یک کوئری اجرا می‌شود و دوباره مشکل N+1 رخ می‌دهد. LAZY خودش N+1 را "حل" نمی‌کند، بلکه آن را به زمان دسترسی موکول می‌کند.
*   **نیاز به مدیریت دقیق تراکنش**: برای استفاده صحیح از LAZY، باید اطمینان حاصل کنید که دسترسی به داده‌های تنبل درون یک تراکنش باز یا با استفاده از الگوهایی مانند `Open Session in View` (با احتیاط) مدیریت می‌شود.

---

### 🏆 بهترین شیوه‌ها (Best Practices)

1.  **قاعده طلایی: تقریباً همیشه از `LAZY` استفاده کنید.** به‌ویژه برای تمام مجموعه‌ها (`@OneToMany`, `@ManyToMany`). این کار انعطاف‌پذیری را به حداکثر می‌رساند و از واکشی ناخواسته داده جلوگیری می‌کند.
2.  **از `EAGER` فقط در موارد بسیار نادر و مشخص استفاده کنید**: تنها زمانی که **مطمئن هستید** هر بار که موجودیت اصلی را بارگذاری می‌کنید، به رابطه آن نیز نیاز خواهید داشت و داده‌های مرتبط کوچک و محدود هستند (مثلاً یک `@ManyToOne` به یک جدول کوچک مثل `Category`). حتی در این صورت نیز بهتر است به فکر جایگزین‌هایی مانند `@EntityGraph` باشید.
3.  **واکشی مورد نیاز را در سطح کوئری مشخص کنید**: به‌جای اتکا به EAGER، از `JOIN FETCH` در JPQL یا `@EntityGraph` برای واکشی صریح و بهینه روابط **در همان کوئری‌هایی که به آن نیاز دارید** استفاده کنید. این کار N+1 را حل می‌کند و کنترل کاملی بر واکشی می‌دهد.

### 💎 خلاصه

| ویژگی | `FetchType.LAZY` | `FetchType.EAGER` |
| :--- | :--- | :--- |
| **زمان واکشی** | در اولین دسترسی (on-demand) | بلافاصله همراه با موجودیت اصلی |
| **عملکرد اولیه** | سریع‌تر (داده کمتری واکشی می‌شود) | کندتر (ممکن است داده غیرضروری زیادی واکشی شود) |
| **مشکل اصلی** | `LazyInitializationException` | مشکل N+1 و واکشی داده‌های غیرضروری |
| **انعطاف‌پذیری** | بالا (می‌توان با `JOIN FETCH` به‌صورت موردی EAGER کرد) | پایین (در سطح Mapping قفل شده است) |
| **توصیه** | **گزینه پیش‌فرض و توصیه‌شده** برای اکثر روابط | به‌ندرت و فقط با دلیل قوی |

</div>




<!-- SOURCE: 6.md -->
# File: 6.md

<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">


# قفل خوش‌بینانه (Optimistic) و بدبینانه (Pessimistic) در JPA — چه زمانی از کدام استفاده کنیم؟

انتخاب بین این دو، در واقع انتخاب بین **«زمان تشخیص تعارض»** و **«هزینهٔ جلوگیری از تعارض»** است. JPA به‌طور پیش‌فرض از قفل خوش‌بینانه استفاده می‌کند، چون فرض می‌کند تعارض هم‌زمانی معمولاً زیاد رخ نمی‌دهد. اما مرز مشخصی برای انتخاب وجود دارد.

---

## 🎯 تفاوت اصلی: تعارض کِی تشخیص داده می‌شود؟

- **قفل خوش‌بینانه (Optimistic)**: تعارض **در زمان commit** تشخیص داده می‌شود. دسترسی هم‌زمان را مسدود نمی‌کند؛ بلکه هنگام به‌روزرسانی بررسی می‌کند که آیا داده در این فاصله توسط تراکنش دیگری تغییر کرده است یا نه. اگر تغییر کرده باشد، `OptimisticLockException` پرتاب و تراکنش rollback می‌شود.
- **قفل بدبینانه (Pessimistic)**: تعارض **در زمان خواندن** پیشگیری می‌شود. فرض می‌کند تعارض خیلی محتمل است، بنابراین همان موقع خواندن، در سطح دیتابیس قفل می‌گذارد و تا پایان تراکنش نگه می‌دارد تا تراکنش‌های دیگر نتوانند آن داده را تغییر دهند (یا بخوانند).

---

## ⚖️ قفل خوش‌بینانه (Optimistic Locking): کِی استفاده کنیم؟

**سناریوی اصلی: احتمال تعارض کم است و هزینهٔ retry قابل قبول است.**

وقتی اکثر تراکنش‌ها بدون تعارض موفق می‌شوند، قفل خوش‌بینانه انتخاب بهینه‌تری است. در چنین شرایطی، قفل بدبینانه برای هر خواندن (`SELECT ... FOR UPDATE`) اتلاف عملکرد جدی است.

### پیاده‌سازی و نکات کلیدی
روش استاندارد در JPA استفاده از فیلد `@Version` است (از نوع `int`، `long`، `Timestamp` و غیره):
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```java
@Entity
public class Account {
    @Version
    private Long version;
    // ...
}
```
</div>



Hibernate هنگام UPDATE به‌طور خودکار شرط `WHERE version = ?` را اضافه می‌کند. اگر تراکنش دیگری در این فاصله ردیف را تغییر داده باشد، مقدار `version` عوض شده، تعداد ردیف‌های تأثیرگرفته صفر می‌شود و Hibernate استثنای `OptimisticLockException` (یا زیرکلاس آن `StaleObjectStateException`) را پرتاب می‌کند.

### استراتژی‌های مدیریت تعارض
- **Retry**: در مرز Use Case استثنای `OptimisticLockException` را بگیرید، داده را دوباره بخوانید، منطق کسب‌وکار را دوباره اعمال کنید و دوباره commit کنید. می‌توانید از مکانیزم‌هایی مثل `@Retry` استفاده کنید، اما حتماً حداکثر تعداد تلاش را محدود کنید تا حلقهٔ بی‌پایان رخ ندهد.
- **اطلاع به کاربر**: در سناریوهای فرم وب، می‌توانید شمارهٔ نسخه را به کلاینت بفرستید. اگر هنگام submit نسخه مطابقت نداشت، HTTP 409 Conflict برگردانید و به کاربر بگویید داده توسط شخص دیگری تغییر کرده و باید صفحه را refresh کند.

### نکات مهم و «تله‌ها»
- **دور زدن با Bulk Update**: دستورات JPQL از نوع `UPDATE Entity e SET ...` مستقیماً روی دیتابیس اجرا می‌شوند، Persistence Context را دور می‌زنند و **به‌طور خودکار `@Version` را افزایش نمی‌دهند**. این باعث می‌شود به‌روزرسانی‌های بعدی مبتنی بر version شکست بخورند. بعد از Bulk Update باید `entityManager.clear()` کنید یا موجودیت‌های تأثیرگرفته را دوباره بارگذاری کنید.
- **`OPTIMISTIC_FORCE_INCREMENT`**: یک حالت قفل خاص است که هنگام commit، **حتی اگر خود موجودیت تغییر نکرده باشد**، version را اجباراً افزایش می‌دهد. برای سناریوهای Aggregate Root مناسب است: وقتی موجودیت فرزند تغییر می‌کند، version والد هم باید افزایش یابد تا سیستم‌های بیرونی بفهمند وضعیت Aggregate تغییر کرده است.

---

## 🔒 قفل بدبینانه (Pessimistic Locking): کِی استفاده کنیم؟

**سناریوی اصلی: تعارض مکرر است (رقابت بالا) و هزینهٔ retry خیلی زیاد است.**

وقتی تراکنش‌های زیادی هم‌زمان روی یک مجموعهٔ «دادهٔ داغ» رقابت می‌کنند، قفل خوش‌بینانه باعث می‌شود تعداد زیادی تراکنش در مرحلهٔ commit شکست بخورند و rollback شوند؛ حلقهٔ retry بیهوده منابع را هدر می‌دهد. اینجا قفل بدبینانه با **سریال‌سازی** دسترسی به دادهٔ داغ، هرچند بعضی تراکنش‌ها را منتظر نگه می‌دارد، از چرخهٔ گران‌قیمت «خواندن-محاسبه-شکست در commit-retry» جلوگیری می‌کند.

**مثال معمول**: **فروش لحظه‌ای (Flash Sale) یا کاهش موجودی انبار**. هزاران درخواست هم‌زمان برای یک SKU رقابت می‌کنند؛ استفاده از `SELECT ... FOR UPDATE` برای سریال‌سازی درخواست‌ها انتخاب روشن و قابل اتکایی است.

### دو حالت اصلی قفل بدبینانه

#### `PESSIMISTIC_WRITE` (قفل نوشتن انحصاری)
- **رفتار**: قفل انحصاری می‌گیرد، معمولاً به‌صورت `SELECT ... FOR UPDATE` ترجمه می‌شود. تراکنش‌های دیگر نمی‌توانند آن ردیف را بخوانند (در دیتابیس‌هایی که پشتیبانی می‌کنند)، به‌روزرسانی کنند یا حذف کنند و فقط باید منتظر بمانند.
- **کاربرد**: سناریوهایی که **قطعاً قصد تغییر داده را دارید**. این رایج‌ترین و از نظر معنایی شفاف‌ترین حالت قفل بدبینانه است؛ مثل کاهش موجودی یا تغییر موجودی حساب.

#### `PESSIMISTIC_READ` (قفل خواندن اشتراکی)
- **رفتار**: قفل اشتراکی می‌گیرد، معمولاً به‌صورت `SELECT ... FOR SHARE` یا `LOCK IN SHARE MODE` ترجمه می‌شود. چند تراکنش می‌توانند هم‌زمان قفل اشتراکی داشته باشند و بخوانند، اما هیچ تراکنشی نمی‌تواند قفل نوشتن بگیرد و داده را تغییر دهد تا همهٔ قفل‌های اشتراکی آزاد شوند.
- **کاربرد**: زمانی که **باید مطمئن شوید داده در طول خواندن تغییر نمی‌کند، اما خودتان لزوماً قصد تغییر ندارید**. مثلاً هنگام محاسبهٔ یک مقدار تجمیعی که بر تصمیم‌های بعدی اثر می‌گذارد و باید اطمینان داشته باشید دادهٔ پایه در طول محاسبه ثابت می‌ماند.
- **نکته**: `PESSIMISTIC_WRITE` رایج‌تر است، چون معنای انحصاری آن امن‌تر و مستقیم‌تر است. رفتار قفل اشتراکی `PESSIMISTIC_READ` ممکن است بین دیتابیس‌های مختلف تفاوت داشته باشد و حتی به قفل انحصاری ارتقا پیدا کند.

### محدودیت‌های کلیدی استفاده از قفل بدبینانه
- **حتماً timeout برای قفل تنظیم کنید**: `jakarta.persistence.lock.timeout` باید همیشه تنظیم شود تا یک تراکنش بی‌نهایت منتظر قفل نماند و Connection Pool را خالی نکند.
- **تراکنش باید تا حد امکان کوتاه باشد**: در تراکنشی که قفل را نگه داشته، **هرگز** نباید HTTP Call، ارسال پیام یا محاسبات طولانی انجام دهید. مدت نگه‌داشتن قفل مستقیماً throughput سیستم را تعیین می‌کند.
- **ترتیب قفل‌گیری را یکسان نگه دارید**: اگر تراکنش باید چند ردیف را قفل کند، همیشه باید قفل‌ها را **با ترتیب یکسان** بگیرید؛ این قاعدهٔ اساسی جلوگیری از Deadlock است.
- **کاربرد `SKIP LOCKED`**: برای سناریوی **صف کار (Work Queue)** که چند worker برای کارهای مختلف رقابت می‌کنند، می‌توان از `FOR UPDATE SKIP LOCKED` استفاده کرد. هر worker از ردیف‌هایی که worker دیگر قفل کرده رد می‌شود و مستقیم کار بعدی موجود را برمی‌دارد؛ این کار throughput پردازش صف را به‌شدت بالا می‌برد.

---

## 💎 راهنمای انتخاب

| معیار | قفل خوش‌بینانه (Optimistic) | قفل بدبینانه (Pessimistic) |
| :--- | :--- | :--- |
| **فرض اصلی** | تعارض **کم** رخ می‌دهد | تعارض **خیلی محتمل** است |
| **تشخیص/پیشگیری از تعارض** | **تشخیص** در زمان commit | **پیشگیری** در زمان خواندن (قفل) |
| **سناریوی مناسب** | خواندن زیاد، نوشتن کم، یا نوشتن‌های پراکنده روی داده‌های مختلف | **ردیف‌های داغ (Hot Rows)**، صف، موجودی فروش لحظه‌ای |
| **واکنش به تعارض** | پرتاب استثنا؛ **retry** یا اطلاع به کاربر توسط لایهٔ اپلیکیشن | تراکنش‌های دیگر **منتظر** آزاد شدن قفل می‌مانند |
| **ریسک اصلی** | طوفان retry (در رقابت بالا)، مدیریت نادرست `OptimisticLockException` | Deadlock، timeout انتظار قفل، کاهش throughput |
| **Annotation** | `@Version` | `@Lock(LockModeType.PESSIMISTIC_WRITE)` |
| **انتخاب پیش‌فرض** | **✅ انتخاب پیش‌فرض در اکثر سناریوها** | فقط وقتی **رقابت بالا و هزینهٔ retry غیرقابل قبول** است |

**جمع‌بندی**: از **قفل خوش‌بینانه به‌عنوان گزینهٔ پیش‌فرض** شروع کنید. وقتی با مانیتورینگ یا تست بار متوجه شدید که یک مسیر کد خاص به‌خاطر رقابت بالا `OptimisticLockException` مکرر تولید می‌کند و هزینهٔ retry غیرقابل قبول شده است، همان بخش را به‌صورت هدفمند به **قفل بدبینانه** تغییر دهید و محدودیت‌های تراکنش کوتاه، تنظیم timeout و جلوگیری از Deadlock را دقیق رعایت کنید.
</div>




<!-- SOURCE: 7.md -->
# File: 7.md

<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">

# تفاوت JPQL، Criteria API و Native Query در JPA

هر سه روش برای اجرای کوئری در JPA وجود دارند، اما در **سطح انتزاع**، **نوع زبان**، **امنیت نوع (Type Safety)** و **قابلیت حمل (Portability)** تفاوت اساسی دارند. انتخاب درست بین آن‌ها مستقیماً بر نگهداشت‌پذیری و امنیت کد اثر می‌گذارد.

---

## 📊 مقایسهٔ کلی

| معیار | JPQL | Criteria API | Native Query |
| :--- | :--- | :--- | :--- |
| **نوع زبان** | String (شبه SQL شیءگرا) | Java API (Builder Pattern) | SQL خام دیتابیس |
| **کار با چه چیزی** | موجودیت‌ها و فیلدهای آن‌ها | موجودیت‌ها و متادیتای آن‌ها | جداول و ستون‌های واقعی |
| **Type Safety** | ❌ فقط در Runtime | ✅ کامل (Compile-time) | ❌ فقط در Runtime |
| **Refactoring خودکار** | ❌ (رشته‌ها شکسته می‌شوند) | ✅ (IDE پشتیبانی می‌کند) | ❌ |
| **Portability بین دیتابیس‌ها** | ✅ بالا | ✅ بالا | ❌ وابسته به دیتابیس |
| **خوانایی** | ✅ بالا برای کوئری‌های ساده | ❌ پایین، طولانی و پرحجم | ✅ برای افراد آشنا با SQL |
| **کوئری پویا (Dynamic)** | ❌ سخت و ناامن | ✅ نقطهٔ قوت اصلی | ❌ سخت |
| **دسترسی به ویژگی‌های خاص دیتابیس** | ❌ محدود | ❌ محدود | ✅ کامل |
| **خطر SQL Injection** | ⚠️ با String Concatenation | ✅ ندارد | ⚠️ با String Concatenation |
| **پشتیبانی از DTO Projection** | ✅ (`new ...`) | ✅ | ✅ |

---

## 1. JPQL (Java Persistence Query Language)

زبان کوئری استاندارد JPA که شبیه SQL است، اما به‌جای **جداول** با **موجودیت‌ها** و به‌جای **ستون‌ها** با **فیلدهای موجودیت** کار می‌کند. JPA آن را به SQL مخصوص دیتابیس ترجمه می‌کند.
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```java
List<Order> orders = em.createQuery(
    "select o from Order o where o.customer.name = :name and o.total > :min",
    Order.class)
    .setParameter("name", "Ali")
    .setParameter("min", 1000)
    .getResultList();
```
</div>


**نقاط قوت:**
- خوانا و مختصر برای اکثر کوئری‌های روزمره.
- مستقل از دیتابیس (Portable).
- از `JOIN FETCH`، `@EntityGraph`، توابع تجمیعی و Subquery پشتیبانی می‌کند.
- پارامترگذاری با `:param` از SQL Injection جلوگیری می‌کند.

**نقاط ضعف:**
- رشته است؛ غلط تایپی یا تغییر نام فیلد فقط **در Runtime** کشف می‌شود.
- ساخت کوئری پویا با آن زشت و خطرناک می‌شود (String Concatenation).
- Refactoring خودکار در IDE کار نمی‌کند.

**کِی استفاده کنیم؟** کوئری‌های **ایستا** و نسبتاً ساده که خوانایی برای تیم مهم است. این گزینهٔ پیش‌فرض برای اکثر موارد است.

---

## 2. Criteria API

یک API برنامه‌نویسی برای ساخت کوئری به‌صورت **شیءگرا** و **Type-Safe**. با استفاده از Metamodel (کلاس‌های `X_` که در زمان کامپایل تولید می‌شوند)، فیلدها به‌صورت attribute قابل ارجاع هستند.
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```java
CriteriaBuilder cb = em.getCriteriaBuilder();
CriteriaQuery<Order> cq = cb.createQuery(Order.class);
Root<Order> root = cq.from(Order.class);

List<Predicate> predicates = new ArrayList<>();
predicates.add(cb.equal(root.get(Order_.customer).get(Customer_.name), "Ali"));
predicates.add(cb.greaterThan(root.get(Order_.total), 1000));

cq.where(predicates.toArray(new Predicate[0]));
List<Order> orders = em.createQuery(cq).getResultList();
```
</div>


**نقاط قوت:**
- **Type Safety کامل**: خطا در زمان **کامپایل** کشف می‌شود.
- Refactoring خودکار (تغییر نام فیلد، IDE همه‌جا را به‌روز می‌کند).
- **ساخت کوئری پویا** بسیار امن و تمیز: می‌توانید به‌راحتی شرط‌ها را با `if` اضافه/حذف کنید (مناسب برای Search Filters).
- بدون خطر SQL Injection (به‌طور ذاتی پارامترگذاری می‌کند).
- Portable بین دیتابیس‌ها.

**نقاط ضعف:**
- **پرحجم و کم‌خوان**: حتی یک کوئری ساده چندین خط می‌شود.
- منحنی یادگیری بالا؛ برای کوئری‌های پیچیده، کد به‌سرعت غیرقابل‌خوان می‌شود.
- نیاز به تولید Metamodel (اضافه کردن یک Annotation Processor مثل Hibernate JPA Metamodel Generator) که ابزار را پیچیده‌تر می‌کند.

**کِی استفاده کنیم؟**
- **کوئری‌های پویا** با شرط‌های متغیر (مثل صفحهٔ جستجوی پیشرفته با ده‌ها فیلتر اختیاری).
- **کتابخانه‌ها و APIهای عمومی** که باید در برابر تغییرات مدل مقاوم باشند.
- جاهایی که Type Safety و Refactoring حیاتی است.

> **جایگزین مدرن**: برای کوئری‌های پویا، **Spring Data JPA Specifications** یا **Querydsl** اغلب خوانایی بسیار بهتری از Criteria خام می‌دهند. Querydsl هم Type-Safe است و هم شبیه JPQL خوانده می‌شود. اگر از Spring Data استفاده می‌کنید، ترکیب `JpaSpecificationExecutor` و Specification را جدی بگیرید.

---

## 3. Native Query

SQL خام دیتابیس که مستقیماً اجرا می‌شود. JPA آن را ترجمه نمی‌کند.
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```java
List<Object[]> rows = em.createNativeQuery(
    "SELECT o.id, o.total, c.name FROM orders o " +
    "JOIN customers c ON c.id = o.customer_id " +
    "WHERE o.total > :min ORDER BY o.total DESC", Order.class)
    .setParameter("min", 1000)
    .getResultList();
```
</div>



**نقاط قوت:**
- **دسترسی کامل به ویژگی‌های خاص دیتابیس**: Window Functions، CTE (`WITH`)، `LATERAL JOIN`، `ON CONFLICT`، توابع اختصاصی Postgres/Oracle، Full-Text Search، Index Hints و غیره.
- **کارایی بهینه در موارد خاص**: می‌توانید کوئری را دقیقاً برای Execution Plan دیتابیس تنظیم کنید (مثلاً استفاده از Index خاص یا `FORCE INDEX`).
- برای **گزارش‌گیری پیچیده** و کوئری‌های تحلیلی سنگین اغلب تنها گزینه است.
- برای کار با **جدول‌های legacy** که نگاشت تمیزی به Entity ندارند.

**نقاط ضعف:**
- **Portability از بین می‌رود**: وابسته به دیتابیس است و تغییر دیتابیس نیازمند بازنویسی است.
- **Type Safety ندارد**: خطا فقط در Runtime.
- Refactoring خودکار کار نمی‌کند.
- خطر SQL Injection بالا اگر پارامترگذاری رعایت نشود.
- نتیجهٔ کوئری معمولاً `Object[]` یا نیاز به `@SqlResultSetMapping` است که مدیریت آن دست‌وپاگیر است.
- نگاشت خودکار به موجودیت‌ها همیشه کار نمی‌کند.

**کِی استفاده کنیم؟**
- نیاز به **ویژگی خاص دیتابیس** که JPQL/Criteria پشتیبانی نمی‌کند.
- **بهینه‌سازی عملکرد** کوئری‌های بحرانی که با JPQL کافی نیست.
- گزارش‌های تحلیلی پیچیده.
- مهاجرت از سیستم legacy یا کوئری‌های DBA-محور.

> **نکتهٔ مهم**: Native Query را **آخرین راه‌حل** در نظر بگیرید، نه اولین. اگر با JPQL یا Criteria می‌توانید همان کار را انجام دهید، آن‌ها را ترجیح دهید. اگر مجبور به Native هستید، حتماً پارامترگذاری کنید و آن را در یک Repository یا DAO ایزوله کنید تا پراکندگی SQL خام در کد کم شود.

---

## 🔍 مثال مقایسه‌ای: یک کوئری یکسان در سه روش

**هدف**: یافتن سفارش‌های بالای ۱۰۰۰ تومان برای مشتری «Ali».
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```java
// JPQL
em.createQuery("select o from Order o where o.customer.name = :n and o.total > :t", Order.class)
  .setParameter("n", "Ali").setParameter("t", 1000).getResultList();

// Criteria
CriteriaBuilder cb = em.getCriteriaBuilder();
CriteriaQuery<Order> cq = cb.createQuery(Order.class);
Root<Order> r = cq.from(Order.class);
cq.select(r).where(
    cb.equal(r.get(Order_.customer).get(Customer_.name), "Ali"),
    cb.greaterThan(r.get(Order_.total), 1000));
em.createQuery(cq).getResultList();

// Native
em.createNativeQuery(
    "select o.* from orders o join customers c on c.id = o.customer_id " +
    "where c.name = ? and o.total > ?", Order.class)
  .setParameter(1, "Ali").setParameter(2, 1000).getResultList();
```
</div>


تفاوت در حجم کد، خوانایی و میزان اتکا به دیتابیس به‌وضوح دیده می‌شود.

---

## 💎 راهنمای انتخاب

| سناریو | انتخاب پیشنهادی |
| :--- | :--- |
| کوئری ایستا و معمولی | **JPQL** |
| Search Filter با شرط‌های متغیر | **Criteria API** (یا Querydsl/Specifications) |
| نیاز به Type Safety و Refactoring | **Criteria API** (یا Querydsl) |
| ویژگی خاص دیتابیس یا بهینه‌سازی بحرانی | **Native Query** |
| گزارش تحلیلی سنگین | **Native Query** |
| کتابخانه یا API عمومی | **Criteria API** |

**جمع‌بندی**: **JPQL را پیش‌فرض** قرار دهید. برای کوئری‌های **پویا** به **Criteria API** (یا Querydsl/Specifications) بروید. و **Native Query** را فقط برای ویژگی‌های خاص دیتابیس یا بهینه‌سازی‌های اثبات‌شده نگه دارید — و در همان صورت نیز آن را ایزوله و پارامترگذاری‌شده بنویسید.

</div>




<!-- SOURCE: 8.md -->
# File: 8.md

<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">

سه نوع Session Bean در EJB (Stateless، Stateful و Singleton) از نظر **نحوهٔ مدیریت State**، **چرخهٔ حیات**، **مدیریت هم‌زمانی (Concurrency)** و **مقیاس‌پذیری** تفاوت‌های بنیادین دارند. انتخاب درست بین آن‌ها مستقیماً بر عملکرد و پیچیدگی برنامه اثر می‌گذارد.

---

### 📊 مقایسهٔ کلی

| معیار | Stateless | Stateful | Singleton |
| :--- | :--- | :--- | :--- |
| **نگهداری State** | ❌ بین فراخوانی‌ها State نگه نمی‌دارد | ✅ State مکالمه‌ای (Conversational) را برای **یک کلاینت** نگه می‌دارد | ✅ State را بین فراخوانی‌ها نگه می‌دارد و **بین همهٔ کلاینت‌ها مشترک** است |
| **تعداد نمونه** | استخر (Pool) از نمونه‌ها؛ هر نمونه می‌تواند به **چندین کلاینت** خدمت دهد (یکی در هر لحظه) | یک نمونه **اختصاصی برای هر کلاینت** | **فقط یک نمونه** به ازای هر Application در هر JVM |
| **چرخهٔ حیات** | ساده: Pool → آماده → `@PreDestroy` (Passivation ندارد) | پیچیده‌تر: `@PostConstruct` → آماده → Passivation (`@PrePassivate`) → Activation (`@PostActivate`) → `@Remove` → `@PreDestroy` | ساده: ایجاد (احتمالاً با `@Startup`) → آماده → `@PreDestroy` (Passivation ندارد) |
| **مدیریت هم‌زمانی** | کانتینر هر نمونه را در هر لحظه فقط به یک کلاینت می‌دهد؛ نیازی به قفل‌گذاری دستی نیست | دسترسی هم‌زمان به یک نمونه توسط همان کلاینت **سریال‌سازی** می‌شود | **نیاز به مدیریت صریح**: Container-Managed (پیش‌فرض) با `@Lock(READ/WRITE)` یا Bean-Managed |
| **مقیاس‌پذیری** | ✅ **بالا**؛ برای تعداد زیاد کلاینت به تعداد کمتری نمونه نیاز است | ❌ **پایین‌تر**؛ هر کلاینت نمونهٔ خودش را می‌خواهد و State در حافظه هزینه دارد | برای حالت عادی مناسب است، اما **گلوگاه** می‌تواند باشد چون همهٔ درخواست‌ها به یک نمونه می‌رسند |
| **مناسب برای Web Service** | ✅ بله | ❌ خیر | ✅ بله |

---

### 1️⃣ Stateless Session Bean

**تعریف**: هیچ State مربوط به کلاینت را بین فراخوانی‌های متد نگه نمی‌دارد. هر نمونه از Stateless Bean از نظر عملکردی معادل نمونه‌های دیگر است و کانتینر می‌تواند درخواست هر کلاینتی را به هر نمونهٔ آزاد در استخر ارسال کند.

**نکتهٔ مهم**: Stateless به معنای «بدون State» در حین اجرای متد نیست؛ Bean می‌تواند در طول یک متد State موقت داشته باشد، اما پس از پایان متد، آن State نباید به کلاینت خاصی گره بخورد.

**چرخهٔ حیات**: کانتینر یک **استخر (Pool)** از نمونه‌ها نگه می‌دارد. چرخه فقط دو مرحله دارد: «وجود ندارد» و «آماده برای فراخوانی متد». Passivation روی آن انجام نمی‌شود.

**مثال کاربردی**: ارسال ایمیل تأیید سفارش، محاسبهٔ مالیات، فراخوانی یک سرویس خارجی. هر درخواست مستقل است و به State درخواست قبلی وابسته نیست.

```java
@Stateless
public class OrderConfirmationService {
    public void sendConfirmation(String email, Long orderId) {
        // منطق ارسال ایمیل — بدون وابستگی به State قبلی
    }
}
```

**کِی استفاده کنیم؟** گزینهٔ **پیش‌فرض و توصیه‌شده** برای اکثر منطق‌های کسب‌وکار. اگر منطق شما در یک متد کامل می‌شود و State کلاینت را بین متدها نیاز ندارید، Stateless بهترین انتخاب از نظر مقیاس‌پذیری و سادگی است.

---

### 2️⃣ Stateful Session Bean

**تعریف**: State مربوط به **یک کلاینت خاص** را در طول چندین فراخوانی متد و تراکنش نگه می‌دارد. این State به **Conversational State** معروف است و نشان‌دهندهٔ تعامل بین Bean و آن کلاینت است. هر نمونه منحصراً به یک کلاینت تعلق دارد و بین کلاینت‌ها به اشتراک گذاشته نمی‌شود.

**چرخهٔ حیات**: پیچیده‌ترین چرخه را دارد. کانتینر ممکن است برای آزادسازی حافظه، Bean را **Passivate** کند (از حافظه به حافظهٔ ثانویه منتقل کند) و هنگام درخواست بعدی کلاینت دوباره **Activate** کند. متدهای `@PrePassivate` و `@PostActivate` در این انتقال‌ها فراخوانی می‌شوند.

**مثال کاربردی**: **سبد خرید**. کلاینت در طول یک نشست خرید، آیتم‌ها را یکی‌یکی اضافه می‌کند و Bean باید محتویات سبد را در طول آن نشست حفظ کند.
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```java
@Stateful
public class ShoppingCartBean {
    private List<Item> items = new ArrayList<>();

    public void addItem(Item item) { items.add(item); }
    public List<Item> getItems() { return items; }

    @Remove
    public void checkout() { /* پایان نشست */ }
}
```
</div>


**کِی استفاده کنیم؟** تنها زمانی که **به‌طور قطعی** نیاز دارید State یک کلاینت خاص را در طول چندین متد حفظ کنید (مثل Wizard چندمرحله‌ای، سبد خرید، مدیریت Workflow). استفاده از Stateful باید **حداقل** باشد، چون مقیاس‌پذیری را کاهش می‌دهد و مدیریت حافظه را پیچیده می‌کند.

---

### 3️⃣ Singleton Session Bean

**تعریف**: کانتینر **فقط یک نمونه** از این Bean را به ازای هر Application در هر JVM ایجاد می‌کند و آن نمونه تا پایان عمر Application زنده می‌ماند. این تنها نوع Session Bean است که State آن به‌طور **مشترک بین همهٔ کلاینت‌ها** قابل دسترسی است.

**مدیریت هم‌زمانی (نکتهٔ کلیدی)**: چون همهٔ کلاینت‌ها به یک نمونه دسترسی دارند، هم‌زمانی باید مدیریت شود. دو حالت وجود دارد:

- **Container-Managed Concurrency (پیش‌فرض)**: کانتینر با Annotationهای `@Lock` دسترسی را کنترل می‌کند.
    - `@Lock(READ)`: چند کلاینت می‌توانند **هم‌زمان** متد را فراخوانی کنند (برای متدهای فقط‌خواندنی).
    - `@Lock(WRITE)` (پیش‌فرض): هنگام اجرای این متد، نمونه **قفل** می‌شود و کلاینت‌های دیگر باید صبر کنند (برای متدهای تغییردهندهٔ State).

- **Bean-Managed Concurrency**: خود برنامه‌نویس با ابزارهای هم‌زمانی Java (مثل `synchronized`) مدیریت می‌کند.

**چرخهٔ حیات**: مشابه Stateless، بدون Passivation. با Annotation `@Startup` می‌توان کانتینر را مجبور کرد Bean را **هنگام راه‌اندازی Application** ایجاد کند (مناسب برای کارهای Initialization).

**مثال کاربردی**: **Registry مرکزی**، **Cache سراسری**، **شمارندهٔ سراسری**، یا انجام وظایف راه‌اندازی/پاک‌سازی در شروع/پایان Application.
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```java
@Singleton
@Startup
public class ApplicationConfig {
    private Map<String, String> settings = new ConcurrentHashMap<>();

    @PostConstruct
    public void load() { /* بارگذاری تنظیمات هنگام Startup */ }

    @Lock(READ)
    public String get(String key) { return settings.get(key); }

    @Lock(WRITE)
    public void put(String key, String value) { settings.put(key, value); }
}
```
</div>


**کِی استفاده کنیم؟** وقتی State باید **در سطح Application به اشتراک گذاشته شود**، یا نیاز به یک نقطهٔ دسترسی واحد و هم‌زمان دارید، یا می‌خواهید کارهای Startup/Shutdown را مدیریت کنید.

---

### 💎 جمع‌بندی و راهنمای انتخاب

| اگر نیاز شما این است... | انتخاب کنید |
| :--- | :--- |
| منطق کسب‌وکار مستقل که در یک متد کامل می‌شود | **Stateless** (پیش‌فرض) |
| حفظ State یک کلاینت خاص در چندین متد (سبد خرید، Wizard) | **Stateful** (فقط در صورت نیاز قطعی) |
| State مشترک در سطح Application، Registry، Cache سراسری | **Singleton** (با مدیریت هم‌زمانی مناسب) |

**Stateless** را پایهٔ معماری خود قرار دهید. **Stateful** را فقط برای سناریوهای مکالمه‌ای واقعی به کار ببرید. و **Singleton** را برای State سراسری و وظایف چرخهٔ حیات Application نگه دارید — و همیشه به **مدیریت هم‌زمانی** آن (انتخاب `@Lock` مناسب یا Bean-Managed) توجه کنید.
</div>



<!-- SOURCE: 9.md -->
# File: 9.md

<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">

مدیریت تراکنش در EJB به دو روش اصلی **مدیریتشده توسط کانتینر (CMT)** و **مدیریتشده توسط Bean (BMT)** انجام میشود. annotation `@TransactionAttribute` قلب CMT است و مشخص میکند که کانتینر EJB چگونه تراکنش را برای یک متد خاص مدیریت کند.

---

### 🏛️ دو رویکرد اصلی مدیریت تراکنش در EJB

| رویکرد | مسئولیت | نحوه پیادهسازی |
| :--- | :--- | :--- |
| **CMT (Container-Managed Transaction)** | کانتینر EJB شروع، commit و rollback تراکنش را مدیریت میکند. | **اعلانی (Declarative)** با `@TransactionAttribute` |
| **BMT (Bean-Managed Transaction)** | خود Bean با استفاده از `UserTransaction` تراکنش را کنترل میکند. | **برنامهای (Programmatic)** با فراخوانی صریح `begin()`، `commit()` و `rollback()` |

**CMT** رایجترین و توصیهشدهترین رویکرد است، زیرا منطق مدیریت تراکنش را از کد کسبوکار جدا میکند و توسعه را سادهتر میسازد. **BMT** برای سناریوهای پیچیدهتری که نیاز به کنترل دقیق مرزهای تراکنش دارید (مانند مدیریت چندین تراکنش در یک متد) استفاده میشود.

---

### 🎯 annotation `@TransactionAttribute` در CMT

این annotation را میتوان روی **کلاس Bean** (بهعنوان پیشفرض برای همهٔ متدها) یا روی **یک متد خاص** اعمال کرد. اگر هم روی کلاس و هم روی متد تنظیم شود، مقدار **متد** اولویت دارد.

این annotation یکی از شش مقدار `TransactionAttributeType` را میپذیرد:

#### 1. `REQUIRED` (پیشفرض)
- **رفتار**: اگر کلاینت در حال حاضر در یک تراکنش باشد، کانتینر همان تراکنش را **ادامه میدهد**. اگر کلاینت تراکنشی نداشته باشد، کانتینر یک **تراکنش جدید** شروع میکند، متد را اجرا و سپس آن را **commit** میکند.
- **کاربرد**: **گزینهٔ پیشفرض و امن** برای اکثر متدهای کسبوکار. این مقدار پیشفرض در EJB 3.0 به بعد است.

#### 2. `REQUIRES_NEW`
- **رفتار**: کانتینر **همیشه یک تراکنش جدید** شروع میکند، صرفنظر از اینکه کلاینت در تراکنش باشد یا نه. اگر کلاینت تراکنشی داشته باشد، آن تراکنش **معلق (Suspended)** میشود و پس از پایان متد، دوباره از سر گرفته میشود.
- **کاربرد**: برای عملیاتی که باید **مستقل از نتیجهٔ تراکنش والد** commit یا rollback شوند. مثال کلاسیک: **ثبت لاگ حسابرسی (Audit Log)** که حتی اگر تراکنش اصلی شکست بخورد، باید ثبت شود.

#### 3. `MANDATORY`
- **رفتار**: متد **باید** در یک تراکنش موجود اجرا شود. اگر کلاینت تراکنشی نداشته باشد، کانتینر **فراخوانی را مسدود کرده و استثنا پرتاب میکند**.
- **کاربرد**: برای متدهایی که **هرگز نباید بهتنهایی اجرا شوند** و منطقاً باید بخشی از یک تراکنش بزرگتر باشند. این یک **قرارداد اجباری** با فراخوانکننده است.

#### 4. `SUPPORTS`
- **رفتار**: اگر کلاینت در تراکنش باشد، متد در **همان تراکنش** اجرا میشود. اگر تراکنشی وجود نداشته باشد، متد **بدون تراکنش** اجرا میشود.
- **کاربرد**: برای متدهای **فقطخواندنی** یا عملیاتی که نتیجهشان به تراکنش وابسته نیست. کانتینر **تغییری در وضعیت تراکنش ایجاد نمیکند**.

#### 5. `NOT_SUPPORTED`
- **رفتار**: متد **هرگز** در یک تراکنش اجرا نمیشود. اگر کلاینت تراکنشی داشته باشد، آن تراکنش **معلق** شده و پس از پایان متد دوباره از سر گرفته میشود.
- **کاربرد**: برای عملیاتی که **نیازی به تراکنش ندارند** یا **نباید** بخشی از تراکنش کلاینت باشند. مثال: فراخوانی یک وبسرویس خارجی که خودش تراکنش را مدیریت میکند.

#### 6. `NEVER`
- **رفتار**: متد **نباید** در هیچ تراکنشی اجرا شود. اگر کلاینت در حال حاضر در یک تراکنش باشد، کانتینر **فراخوانی را مسدود کرده و استثنا پرتاب میکند**.
- **کاربرد**: برای عملیاتی که **قطعاً نباید** در یک تراکنش اجرا شوند. این یک **تضمین سختگیرانه** است.

---

### 📊 خلاصهٔ رفتارها در یک نگاه

| `TransactionAttribute` | اگر کلاینت تراکنش داشته باشد | اگر کلاینت تراکنش نداشته باشد | دستهبندی |
| :--- | :--- | :--- | :--- |
| **`REQUIRED`** (پیشفرض) | از تراکنش کلاینت استفاده میکند | تراکنش جدید شروع میکند | اصلاحکننده (Correcting) |
| **`REQUIRES_NEW`** | تراکنش کلاینت را معلق میکند و تراکنش جدید شروع میکند | تراکنش جدید شروع میکند | اصلاحکننده (Correcting) |
| **`MANDATORY`** | از تراکنش کلاینت استفاده میکند | **استثنا پرتاب میکند** | شکستخورنده (Failing) |
| **`SUPPORTS`** | از تراکنش کلاینت استفاده میکند | بدون تراکنش اجرا میکند | بدون تغییر (No Change) |
| **`NOT_SUPPORTED`** | تراکنش کلاینت را معلق میکند | بدون تراکنش اجرا میکند | اصلاحکننده (Correcting) |
| **`NEVER`** | **استثنا پرتاب میکند** | بدون تراکنش اجرا میکند | شکستخورنده (Failing) |

> **نکته**: دستهبندی «شکستخورنده» به این معناست که کانتینر برای حفظ تضمین خود، **فراخوانی را مسدود میکند** و استثنا پرتاب میکند. دستهبندی «اصلاحکننده» به این معناست که کانتینر **وضعیت تراکنش را بهطور خودکار تنظیم میکند** (شروع، تعلیق یا ادامه) تا تضمین مورد نظر محقق شود.

---

### ⚠️ محدودیتهای استفاده از `@TransactionAttribute`

مقادیر مجاز `@TransactionAttribute` بسته به نوع متد یا Bean محدود میشوند:

- **متدهای شنوندهٔ پیام (Message-Driven Bean)**: فقط `REQUIRED` و `NOT_SUPPORTED` مجاز هستند.
- **متدهای Callback مربوط به Timeout**: فقط `REQUIRED`، `REQUIRES_NEW` و `NOT_SUPPORTED`.
- **متدهای کسبوکار Asynchronous**: فقط `REQUIRED`، `REQUIRES_NEW` و `NOT_SUPPORTED`.
- **متدهای Callback چرخهٔ حیات (`@PostConstruct`، `@PreDestroy`) در Singleton**: فقط `REQUIRED`، `REQUIRES_NEW` و `NOT_SUPPORTED`.
- **Beanهایی که `SessionSynchronization` را پیادهسازی میکنند**: فقط `REQUIRED`، `REQUIRES_NEW` و `MANDATORY`.

---

### 💎 جمعبندی و راهنمای انتخاب

- **`REQUIRED`** را بهعنوان **پیشفرض** برای اکثر متدهای کسبوکار خود قرار دهید.
- از **`REQUIRES_NEW`** برای عملیاتی استفاده کنید که باید **مستقل از تراکنش اصلی** commit شوند (مثل لاگ حسابرسی).
- از **`MANDATORY`** برای متدهایی استفاده کنید که **هرگز نباید بهتنهایی اجرا شوند** و باید بخشی از یک تراکنش بزرگتر باشند.
- از **`SUPPORTS`** برای متدهای **فقطخواندنی** یا عملیاتی که نتیجهشان به تراکنش وابسته نیست، استفاده کنید.
- از **`NOT_SUPPORTED`** برای عملیاتی که **نباید** در تراکنش باشند (مثل فراخوانی سرویسهای خارجی) استفاده کنید.
- از **`NEVER`** برای تضمین **عدم اجرا در تراکنش** استفاده کنید.مدیریت تراکنش در EJB به دو روش اصلی **مدیریتشده توسط کانتینر (CMT)** و **مدیریتشده توسط Bean (BMT)** انجام میشود. annotation `@TransactionAttribute` قلب CMT است و مشخص میکند که کانتینر EJB چگونه تراکنش را برای یک متد خاص مدیریت کند.

---

### 🏛️ دو رویکرد اصلی مدیریت تراکنش در EJB

| رویکرد | مسئولیت | نحوه پیادهسازی |
| :--- | :--- | :--- |
| **CMT (Container-Managed Transaction)** | کانتینر EJB شروع، commit و rollback تراکنش را مدیریت میکند. | **اعلانی (Declarative)** با `@TransactionAttribute` |
| **BMT (Bean-Managed Transaction)** | خود Bean با استفاده از `UserTransaction` تراکنش را کنترل میکند. | **برنامهای (Programmatic)** با فراخوانی صریح `begin()`، `commit()` و `rollback()` |

**CMT** رایجترین و توصیهشدهترین رویکرد است، زیرا منطق مدیریت تراکنش را از کد کسبوکار جدا میکند و توسعه را سادهتر میسازد. **BMT** برای سناریوهای پیچیدهتری که نیاز به کنترل دقیق مرزهای تراکنش دارید (مانند مدیریت چندین تراکنش در یک متد) استفاده میشود.

---

### 🎯 annotation `@TransactionAttribute` در CMT

این annotation را میتوان روی **کلاس Bean** (بهعنوان پیشفرض برای همهٔ متدها) یا روی **یک متد خاص** اعمال کرد. اگر هم روی کلاس و هم روی متد تنظیم شود، مقدار **متد** اولویت دارد.

این annotation یکی از شش مقدار `TransactionAttributeType` را میپذیرد:

#### 1. `REQUIRED` (پیشفرض)
- **رفتار**: اگر کلاینت در حال حاضر در یک تراکنش باشد، کانتینر همان تراکنش را **ادامه میدهد**. اگر کلاینت تراکنشی نداشته باشد، کانتینر یک **تراکنش جدید** شروع میکند، متد را اجرا و سپس آن را **commit** میکند.
- **کاربرد**: **گزینهٔ پیشفرض و امن** برای اکثر متدهای کسبوکار. این مقدار پیشفرض در EJB 3.0 به بعد است.

#### 2. `REQUIRES_NEW`
- **رفتار**: کانتینر **همیشه یک تراکنش جدید** شروع میکند، صرفنظر از اینکه کلاینت در تراکنش باشد یا نه. اگر کلاینت تراکنشی داشته باشد، آن تراکنش **معلق (Suspended)** میشود و پس از پایان متد، دوباره از سر گرفته میشود.
- **کاربرد**: برای عملیاتی که باید **مستقل از نتیجهٔ تراکنش والد** commit یا rollback شوند. مثال کلاسیک: **ثبت لاگ حسابرسی (Audit Log)** که حتی اگر تراکنش اصلی شکست بخورد، باید ثبت شود.

#### 3. `MANDATORY`
- **رفتار**: متد **باید** در یک تراکنش موجود اجرا شود. اگر کلاینت تراکنشی نداشته باشد، کانتینر **فراخوانی را مسدود کرده و استثنا پرتاب میکند**.
- **کاربرد**: برای متدهایی که **هرگز نباید بهتنهایی اجرا شوند** و منطقاً باید بخشی از یک تراکنش بزرگتر باشند. این یک **قرارداد اجباری** با فراخوانکننده است.

#### 4. `SUPPORTS`
- **رفتار**: اگر کلاینت در تراکنش باشد، متد در **همان تراکنش** اجرا میشود. اگر تراکنشی وجود نداشته باشد، متد **بدون تراکنش** اجرا میشود.
- **کاربرد**: برای متدهای **فقطخواندنی** یا عملیاتی که نتیجهشان به تراکنش وابسته نیست. کانتینر **تغییری در وضعیت تراکنش ایجاد نمیکند**.

#### 5. `NOT_SUPPORTED`
- **رفتار**: متد **هرگز** در یک تراکنش اجرا نمیشود. اگر کلاینت تراکنشی داشته باشد، آن تراکنش **معلق** شده و پس از پایان متد دوباره از سر گرفته میشود.
- **کاربرد**: برای عملیاتی که **نیازی به تراکنش ندارند** یا **نباید** بخشی از تراکنش کلاینت باشند. مثال: فراخوانی یک وبسرویس خارجی که خودش تراکنش را مدیریت میکند.

#### 6. `NEVER`
- **رفتار**: متد **نباید** در هیچ تراکنشی اجرا شود. اگر کلاینت در حال حاضر در یک تراکنش باشد، کانتینر **فراخوانی را مسدود کرده و استثنا پرتاب میکند**.
- **کاربرد**: برای عملیاتی که **قطعاً نباید** در یک تراکنش اجرا شوند. این یک **تضمین سختگیرانه** است.

---

### 📊 خلاصهٔ رفتارها در یک نگاه

| `TransactionAttribute` | اگر کلاینت تراکنش داشته باشد | اگر کلاینت تراکنش نداشته باشد | دستهبندی |
| :--- | :--- | :--- | :--- |
| **`REQUIRED`** (پیشفرض) | از تراکنش کلاینت استفاده میکند | تراکنش جدید شروع میکند | اصلاحکننده (Correcting) |
| **`REQUIRES_NEW`** | تراکنش کلاینت را معلق میکند و تراکنش جدید شروع میکند | تراکنش جدید شروع میکند | اصلاحکننده (Correcting) |
| **`MANDATORY`** | از تراکنش کلاینت استفاده میکند | **استثنا پرتاب میکند** | شکستخورنده (Failing) |
| **`SUPPORTS`** | از تراکنش کلاینت استفاده میکند | بدون تراکنش اجرا میکند | بدون تغییر (No Change) |
| **`NOT_SUPPORTED`** | تراکنش کلاینت را معلق میکند | بدون تراکنش اجرا میکند | اصلاحکننده (Correcting) |
| **`NEVER`** | **استثنا پرتاب میکند** | بدون تراکنش اجرا میکند | شکستخورنده (Failing) |

> **نکته**: دستهبندی «شکستخورنده» به این معناست که کانتینر برای حفظ تضمین خود، **فراخوانی را مسدود میکند** و استثنا پرتاب میکند. دستهبندی «اصلاحکننده» به این معناست که کانتینر **وضعیت تراکنش را بهطور خودکار تنظیم میکند** (شروع، تعلیق یا ادامه) تا تضمین مورد نظر محقق شود.

---

### ⚠️ محدودیتهای استفاده از `@TransactionAttribute`

مقادیر مجاز `@TransactionAttribute` بسته به نوع متد یا Bean محدود میشوند:

- **متدهای شنوندهٔ پیام (Message-Driven Bean)**: فقط `REQUIRED` و `NOT_SUPPORTED` مجاز هستند.
- **متدهای Callback مربوط به Timeout**: فقط `REQUIRED`، `REQUIRES_NEW` و `NOT_SUPPORTED`.
- **متدهای کسبوکار Asynchronous**: فقط `REQUIRED`، `REQUIRES_NEW` و `NOT_SUPPORTED`.
- **متدهای Callback چرخهٔ حیات (`@PostConstruct`، `@PreDestroy`) در Singleton**: فقط `REQUIRED`، `REQUIRES_NEW` و `NOT_SUPPORTED`.
- **Beanهایی که `SessionSynchronization` را پیادهسازی میکنند**: فقط `REQUIRED`، `REQUIRES_NEW` و `MANDATORY`.

---

### 💎 جمعبندی و راهنمای انتخاب

- **`REQUIRED`** را بهعنوان **پیشفرض** برای اکثر متدهای کسبوکار خود قرار دهید.
- از **`REQUIRES_NEW`** برای عملیاتی استفاده کنید که باید **مستقل از تراکنش اصلی** commit شوند (مثل لاگ حسابرسی).
- از **`MANDATORY`** برای متدهایی استفاده کنید که **هرگز نباید بهتنهایی اجرا شوند** و باید بخشی از یک تراکنش بزرگتر باشند.
- از **`SUPPORTS`** برای متدهای **فقطخواندنی** یا عملیاتی که نتیجهشان به تراکنش وابسته نیست، استفاده کنید.
- از **`NOT_SUPPORTED`** برای عملیاتی که **نباید** در تراکنش باشند (مثل فراخوانی سرویسهای خارجی) استفاده کنید.
- از **`NEVER`** برای تضمین **عدم اجرا در تراکنش** استفاده کنید.
</div>



<!-- SOURCE: 0.md -->
# File: 0.md

Let's break this down into a practical, step-by-step learning plan. We'll move from the absolute basics to building a real application, with clear goals for each step.

### 🛠️ Step 0: Set Up Your Development Environment

Before writing any code, you need a working setup.

1.  **Install Java (JDK 11 or 17)** : JSF and PrimeFaces run on Java.
2.  **Choose an IDE**: **IntelliJ IDEA** (Ultimate or Community) or **NetBeans** have excellent built-in support for JSF and Facelets.
3.  **Create a Maven Project**: Use your IDE to create a new **Jakarta EE** or **Java Web** project with Maven. This will manage your dependencies.
4.  **Add Dependencies**: In your `pom.xml`, add the dependencies for Jakarta Faces (the modern name for JSF) and PrimeFaces. Here is a starting point:

```xml
<!-- Jakarta Faces API & Implementation -->
<dependency>
    <groupId>jakarta.platform</groupId>
    <artifactId>jakarta.jakartaee-api</artifactId>
    <version>10.0.0</version>
    <scope>provided</scope>
</dependency>

<!-- PrimeFaces -->
<dependency>
    <groupId>org.primefaces</groupId>
    <artifactId>primefaces</artifactId>
    <version>13.0.0</version> <!-- Check for the latest version -->
</dependency>
```
PrimeFaces is famously lightweight—just one JAR, zero configuration, and no required dependencies to get started.

---

### 📘 Step 1: Understand the Two Pillars of JSF

Every JSF application is built on two main concepts. Think of them as a pair that always works together.

*   **Facelets (`.xhtml`)**: This is your **View**. It's an XHTML template that describes the screen layout using JSF and PrimeFaces tags. It's like the HTML of your application, but with special tags that create dynamic, stateful components.
*   **Backing Bean (`.java`)**: This is your **Controller**. It's a simple Java class (usually a CDI `@Named` bean) that holds the data and the logic for your page. The Facelet binds to the properties and methods of this bean.

---

### 👣 Step 2: Your First "Hello World" with PrimeFaces

Let's create a minimal page to see how the pieces connect.

**1. The Backing Bean (`HelloBean.java`)**
Create a Java class with a `@Named` annotation. This makes it discoverable by JSF. It holds a simple message.

```java
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;

@Named
@RequestScoped
public class HelloBean {
    private String message = "Hello, PrimeFaces!";

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
}
```
The `@Named` annotation is essential to bind your controller to a view component.

**2. The Facelet View (`hello.xhtml`)**
Create an `.xhtml` file in your `src/main/webapp` folder. This is where you declare the namespaces and use components.

```xml
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:h="http://xmlns.jcp.org/jsf/html"
      xmlns:p="http://primefaces.org/ui">
<h:head>
    <title>Hello PrimeFaces</title>
</h:head>
<h:body>
    <h:form>
        <p:outputLabel value="Message:" />
        <h:outputText value="#{helloBean.message}" />
        <br/>
        <p:inputText value="#{helloBean.message}" />
        <p:commandButton value="Update" />
    </h:form>
</h:body>
</html>
```
*   `xmlns:p="http://primefaces.org/ui"`: This namespace is necessary to use any PrimeFaces component.
*   `#{helloBean.message}`: This is **Expression Language (EL)** . It creates a two-way binding between the UI and your Java bean.

**3. Run It**
Deploy your application to a server (like **WildFly**, **Payara**, or **Tomcat** with additional setup) and navigate to `hello.xhtml`. You should see a label, the message, an input field, and a button.

---

### 🧱 Step 3: Master the Core JSF Concepts

Now that you have a running app, focus on understanding these fundamentals.

*   **The JSF Lifecycle**: Understand the six phases (Restore View, Apply Request Values, Process Validations, Update Model Values, Invoke Application, Render Response). This is crucial for debugging why data isn't saving or why validation isn't firing.
*   **Managed Bean Scopes**: Learn when to use `@RequestScoped`, `@ViewScoped`, and `@SessionScoped`. A common beginner mistake is using the wrong scope, leading to lost data or memory leaks.
*   **Ajax with PrimeFaces**: This is a game-changer. Instead of full-page reloads, you can update only parts of the page. The key is the `update` attribute on a PrimeFaces command component.

```xml
<p:commandButton value="Ajax Update" 
                 action="#{helloBean.someAction()}" 
                 update="someOutputId" />
<h:outputText id="someOutputId" value="#{helloBean.result}" />
```
This tells JSF to re-render only the component with `id="someOutputId"` after the action completes.

---

### 🚀 Step 4: Build a Real Application (The "Learning Project")

Reading is not enough. You must build. A classic **CRUD (Create, Read, Update, Delete)** application is the perfect training ground.

**Project Idea**: A simple **Employee Management System**.

1.  **Model**: Create an `Employee` class (id, name, email, etc.).
2.  **Service**: Create a simple in-memory service (using a `List`) to store employees. No database needed yet.
3.  **Backing Bean**: Create a bean with methods to `getEmployees()`, `save()`, `delete()`, and properties for the selected employee.
4.  **View**: Build the UI using PrimeFaces components.

**Key Components to Use:**
*   **`p:dataTable`**: Your workhorse for displaying data. Learn to add pagination, sorting, and filtering. This is one of the most powerful components in PrimeFaces.
*   **`p:commandButton`**: For actions like "Save", "Edit", and "Delete".
*   **`p:dialog`**: For showing a modal form to create or edit an employee.
*   **`p:growl`**: For showing success or error notifications to the user.

---

### ⚡ Step 5: Move to Advanced Topics

Once your CRUD application works, you can deepen your knowledge.

*   **Search Expression Framework (SEF)** : Go beyond updating a single component by ID. Use **PrimeFaces Selectors (PFS)** to update multiple components at once with powerful jQuery-like selectors.
*   **Customization & Extensions**: Explore the **PrimeFaces Extensions** library for additional components. Learn how to create your own custom JSF components.
*   **Performance**: Master the `process` and `update` attributes to control exactly what data is sent to the server and what is re-rendered. This is key to building fast applications.
*   **Security**: Learn how to integrate a security framework (like **Spring Security** or **Jakarta Security**) with your JSF application.

### 📚 Recommended Learning Resources

*   **Official Docs**: The [PrimeFaces Showcase](https://www.primefaces.org/showcase/) is the definitive reference for every component, with live examples and code.
*   **Guided Tutorials**:
    *   **Baeldung**: Offers a concise introduction to PrimeFaces setup and basic components.
    *   **Microsoft Learn**: Provides a clear overview of a JSF application and a simple PrimeFaces example.
    *   **Open Liberty Guide**: A comprehensive guide to building a dynamic web app with Jakarta Faces, CDI, and EL.
*   **Books**:
    *   ***PrimeFaces Beginner's Guide***: A classic, step-by-step introduction that walks you through building a sample application.
    *   ***PrimeFaces Cookbook***: Excellent for looking up specific recipes and solutions as you build.

Start with **Step 0** and **Step 2** today. Get that "Hello World" running. Once you see it working, the rest of the path will become much clearer. If you get stuck on a specific step, feel free to ask for more details.



<!-- SOURCE: 1.md -->
# File: 1.md

<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">


در JSF، مفهوم **Managed Bean Lifecycle و Scopes** بسیار مهم است؛ چون مشخص می‌کند یک Bean چه زمانی ساخته می‌شود، چه مدت در حافظه باقی می‌ماند، چه زمانی قابل استفاده است و چه زمانی از بین می‌رود.

اگر با Java EE یا Jakarta EE کار می‌کنی، درک این موضوع برای طراحی صحیح صفحات JSF و جلوگیری از مشکلاتی مثل از دست رفتن اطلاعات فرم یا نگهداری غیرضروری Objectها ضروری است.

# **1. Managed Bean چیست؟**

Managed Bean یک کلاس Java است که توسط Container مدیریت می‌شود و می‌توان از آن در صفحات JSF (مانند XHTML) استفاده کرد.

مثال:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
import jakarta.inject.Named;
import jakarta.faces.view.ViewScoped;
import java.io.Serializable;

@Named
@ViewScoped
public class UserBean implements Serializable {

    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void save() {
        System.out.println("User: " + username);
    }
}
```
</div>
در این مثال:

- `@Named` نام Bean را برای دسترسی از JSF مشخص می‌کند.
- `@ViewScoped` طول عمر Bean را مشخص می‌کند.
- `Serializable` برای قابلیت ذخیره و بازیابی Bean در View Scope ضروری است.

استفاده در صفحه XHTML:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
<h:form>
    <h:inputText value="#{userBean.username}" />

    <h:commandButton
        value="Save"
        action="#{userBean.save}" />
</h:form>
```
</div>

# **2. چرخه حیات Managed Bean**

چرخه حیات یک Managed Bean را می‌توان به شکل زیر در نظر گرفت:

**1. Bean Creation**

ساخت Object توسط Container

**2. Dependency Injection**

تزریق وابستگی‌ها مانند @Inject

**3. PostConstruct**

اجرای متد مقداردهی اولیه

**4. Bean Active**

استفاده در صفحات JSF و اجرای متدها

**5. PreDestroy**

اجرای متد قبل از نابودی Bean

مثال:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
@Named
@RequestScoped
public class UserBean {

    @PostConstruct
    public void init() {
        System.out.println("Bean Created");
    }

    public void save() {
        System.out.println("Saving...");
    }

    @PreDestroy
    public void destroy() {
        System.out.println("Bean Destroyed");
    }
}
```
</div>
دو Annotation مهم:

| Annotation       | کاربرد                               |
| ---------------- | ------------------------------------ |
| `@PostConstruct` | بعد از ایجاد Bean و تزریق وابستگی‌ها |
| `@PreDestroy`    | قبل از نابودی Bean                   |

نکته: متد `init()` لزوماً با هر بار کلیک کاربر اجرا نمی‌شود؛ فقط هنگام ایجاد یک نمونه جدید از Bean اجرا خواهد شد.

# **3. Scope چیست؟**

Scope مشخص می‌کند یک Bean چه مدت زنده بماند و در چه محدوده‌ای قابل دسترسی باشد.

در JSF مهم‌ترین Scopeها عبارت‌اند از:

- `@RequestScoped`
- `@ViewScoped`
- `@SessionScoped`
- `@ApplicationScoped`

حالا تفاوت آن‌ها را بررسی کنیم.

## **4. بررسی Scopeهای مهم JSF**

### **A. RequestScoped**

طول عمر: یک HTTP Request

Bean فقط در طول یک درخواست HTTP زنده است.

مثلاً:

1. کاربر صفحه را باز می‌کند.
2. Bean ساخته می‌شود.
3. اطلاعات فرم را دریافت می‌کند.
4. عملیات ذخیره انجام می‌شود.
5. Response ارسال می‌شود.
6. Bean از بین می‌رود.
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
@Named
@RequestScoped
public class LoginBean {

    private String username;

    public void login() {
        System.out.println(username);
    }
}
```
</div>

**مشکل مهم:**

فرض کن کاربر در یک فرم نام خود را وارد کرده و روی دکمه‌ای کلیک می‌کند که باعث ارسال درخواست Ajax می‌شود.

در درخواست بعدی، Bean جدیدی ساخته می‌شود و مقدار قبلی فیلدها لزوماً حفظ نمی‌شود.

بنابراین برای فرم‌هایی که به چندین درخواست نیاز دارند، معمولاً انتخاب مناسبی نیست.

### **B. ViewScoped**

طول عمر: یک View

این Scope در JSF بسیار کاربردی است.

Bean تا زمانی که کاربر در همان صفحه (View) باقی بماند، زنده می‌ماند.

حتی اگر چندین درخواست Ajax یا Postback اتفاق بیفتد، همان Bean استفاده می‌شود.

مثال:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
import jakarta.inject.Named;
import jakarta.faces.view.ViewScoped;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.io.Serializable;

@Named
@ViewScoped
public class ProductBean implements Serializable {

    private String productName;

    @PostConstruct
    public void init() {
        System.out.println("ProductBean Created");
    }

    public void save() {
        System.out.println("Saving " + productName);
    }

    @PreDestroy
    public void destroy() {
        System.out.println("ProductBean Destroyed");
    }
}
```
</div>

فرض کن کاربر در صفحه مدیریت محصولات است:

- نام محصول را وارد می‌کند.
- دکمه جستجو را می‌زند.
- اطلاعات جدول به‌روزرسانی می‌شود.
- چند بار صفحه را با Ajax به‌روزرسانی می‌کند.
- Bean همچنان همان Bean قبلی است.
- وقتی به صفحه دیگری Navigate کند، Bean از بین می‌رود.

این رفتار مطابق تعریف View Scope در Jakarta Faces است.&#x20;

[image](https://www.google.com/s2/favicons?domain=https://jakarta.ee\&sz=32)

jakarta.ee+1



**چه زمانی استفاده کنیم؟**

- صفحات CRUD
- فرم‌های چندمرحله‌ای در یک صفحه
- جداول با Pagination
- جستجوی Ajax
- صفحات مدیریت کاربران و محصولات

### **C. SessionScoped**

طول عمر: یک User Session

Bean در طول Session کاربر باقی می‌ماند.

یعنی اگر کاربر بین صفحات مختلف حرکت کند، اطلاعات Bean حفظ می‌شود.
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
import jakarta.inject.Named;
import jakarta.enterprise.context.SessionScoped;
import java.io.Serializable;

@Named
@SessionScoped
public class UserSessionBean implements Serializable {

    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
```
</div>
مثلاً:

- کاربر Login می‌کند.
- نام کاربری در Bean ذخیره می‌شود.
- به صفحه Dashboard می‌رود.
- به صفحه Profile می‌رود.
- نام کاربری همچنان در دسترس است.

**چه زمانی استفاده کنیم؟**

- اطلاعات کاربر Login شده
- زبان انتخابی کاربر
- تنظیمات اختصاصی کاربر
- سبد خرید (در صورت نیاز به نگهداری در Session)

نکته: `@SessionScoped` بین درخواست‌های یک Session مشترک است، بنابراین اگر کاربر چند Tab باز کند، همان Bean Session را به اشتراک می‌گذارند. همچنین باید به Thread Safety و Serializable بودن توجه کرد.&#x20;

[image](https://www.google.com/s2/favicons?domain=https://jakarta.ee\&sz=32)

The Eclipse Foundation+1



### **D. ApplicationScoped**

طول عمر: کل Application

در این Scope یک نمونه Bean برای کل برنامه وجود دارد و همه کاربران از همان نمونه استفاده می‌کنند.
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
import jakarta.inject.Named;
import jakarta.enterprise.context.ApplicationScoped;

@Named
@ApplicationScoped
public class ApplicationBean {

    private int visitorCount;

    public synchronized void increment() {
        visitorCount++;
    }

    public int getVisitorCount() {
        return visitorCount;
    }
}
```
</div>
مثلاً:

- کاربر A وارد برنامه می‌شود.
- کاربر B وارد برنامه می‌شود.
- کاربر C وارد برنامه می‌شود.
- همه به همان ApplicationBean دسترسی دارند.

**چه زمانی استفاده کنیم؟**

- تنظیمات عمومی برنامه
- اطلاعات ثابت و مشترک
- Cache مشترک
- داده‌هایی که برای همه کاربران یکسان هستند.

هشدار: اطلاعات خصوصی کاربران را در ApplicationScoped ذخیره نکن؛ چون همه کاربران به همان Instance دسترسی دارند. همچنین دسترسی هم‌زمان باید مدیریت شود.

## **5. مقایسه تمام Scopeها**

| Scope             | طول عمر     | مثال کاربرد   |
| ----------------- | ----------- | ------------- |
| RequestScoped     | یک Request  | پردازش یک فرم |
| ViewScoped        | یک صفحه JSF | صفحه CRUD     |
| SessionScoped     | یک Session  | اطلاعات Login |
| ApplicationScoped | کل برنامه   | تنظیمات عمومی |

برای درک بهتر، این سناریو را ببین:

فرض کن یک کاربر وارد سیستم می‌شود و سه صفحه را باز می‌کند.

[Java EE Dependency Injection Tutorial: CDI, EJB, Patterns, and Best Practices](https://images.openai.com/static-rsc-4/CGp0jTsRREJhVfF0wX6XP9abio5C3zgAG87RQxuNj-GaX0383ZA6xQRWWMFHTTSl4rdOZkmQ9ucOP8BlmBoLdTkGuAh_9UFmtbTlii4haGnnParPNIf-DPHULFGSSu4nDc22kzuHk5D0npNOyqyFF-Y_-WIeJBt5EAyW4nqDj4Y?purpose=inline)

[cleverence.com](https://www.cleverence.com/articles/oracle-documentation/dependency-injection-java-ee-tutorial-4837/?utm_source=chatgpt.com)

| اتفاق           | Request | View  | Session | Application |
| --------------- | ------- | ----- | ------- | ----------- |
| باز کردن صفحه A | جدید    | جدید  | مشترک   | مشترک       |
| Ajax در صفحه A  | جدید    | مشترک | مشترک   | مشترک       |
| رفتن به صفحه B  | جدید    | جدید  | مشترک   | مشترک       |
| رفتن به صفحه C  | جدید    | جدید  | مشترک   | مشترک       |
| خروج کاربر      | پایان   | پایان | پایان   | مشترک       |

توجه: هر View Scope به صفحه خودش تعلق دارد؛ بازگشت به صفحه قبلی پس از Navigation معمولاً یک View جدید ایجاد می‌کند.

## **6. ارتباط Scope با چرخه حیات JSF**

یک نکته مهم این است که چرخه حیات JSF با چرخه حیات Managed Bean یکسان نیست.

JSF دارای ۶ مرحله اصلی است:

1. **Restore View**

   ساخت یا بازیابی درخت کامپوننت‌های صفحه
2. **Apply Request Values**

   دریافت مقادیر ارسال‌شده از فرم
3. **Process Validations**

   اعتبارسنجی مقادیر ورودی
4. **Update Model Values**

   انتقال مقادیر فرم به فیلدهای Bean
5. **Invoke Application**

   اجرای Action Method مانند save()
6. **Render Response**

   تولید HTML و ارسال پاسخ به مرورگر

مثلاً وقتی کاربر روی دکمه Save کلیک می‌کند:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
<h:form>
    <h:inputText value="#{productBean.name}" />

    <h:commandButton
        value="Save"
        action="#{productBean.save}" />
</h:form>
```
</div>

به‌صورت ساده:

1. JSF صفحه را Restore می‌کند.
2. مقدار Input دریافت می‌شود.
3. مقدار اعتبارسنجی می‌شود.
4. مقدار داخل `productBean.name` قرار می‌گیرد.
5. متد `save()` اجرا می‌شود.
6. صفحه دوباره Render می‌شود.

در این مثال اگر Bean از نوع `@ViewScoped` باشد، در Postback همان صفحه، Bean قبلی حفظ می‌شود.

## **7. یک مثال عملی برای مقایسه RequestScoped و ViewScoped**

فرض کن می‌خواهیم یک فرم مدیریت محصول بسازیم.

**حالت اول: RequestScoped**
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
@Named
@RequestScoped
public class ProductBean {

    private String name;

    @PostConstruct
    public void init() {
        System.out.println("Created");
    }

    public void save() {
        System.out.println("Saved: " + name);
    }
}
```
</div>

اگر کاربر صفحه را باز کند و سپس دکمه Ajax را بزند، در هر Request یک Bean جدید ساخته می‌شود.

**حالت دوم: ViewScoped**
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
@Named
@ViewScoped
public class ProductBean implements Serializable {

    private String name;

    @PostConstruct
    public void init() {
        System.out.println("Created");
    }

    public void save() {
        System.out.println("Saved: " + name);
    }
}
```
</div>

در این حالت تا زمانی که کاربر در همان View باقی بماند، Bean حفظ می‌شود.

برای مثال:

**سناریوی اجرای ViewScoped**

1. **باز کردن صفحه**

   Bean Created
2. **کلیک اول روی Ajax**

   Same Bean
3. **کلیک دوم روی Ajax**

   Same Bean
4. **کلیک روی Save**

   Same Bean
5. **رفتن به صفحه دیگر**

   Bean Destroyed

## **8. تفاوت javax و jakarta**

اگر با پروژه‌های قدیمی Java EE کار می‌کنی، ممکن است این Importها را ببینی:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.enterprise.context.RequestScoped;
```
</div>

در Jakarta EE جدید:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
import jakarta.inject.Named;
import jakarta.faces.view.ViewScoped;
import jakarta.enterprise.context.RequestScoped;
```
</div>
همچنین توجه کن که `@ManagedBean` قدیمی JSF با `@Named` در CDI یکسان نیستند. در پروژه‌های جدید بهتر است از CDI استفاده کنی.

منابع رسمی:&#x20;

[image](https://www.google.com/s2/favicons?domain=https://jakarta.ee\&sz=32)

jakarta.ee+3



## **9. سؤالات مهم مصاحبه Java EE / JSF**

این چند سؤال را حتماً بلد باش:

**سؤال ۱:** اگر یک Bean را `@RequestScoped` تعریف کنیم، آیا اطلاعات آن بین دو درخواست HTTP حفظ می‌شود؟

پاسخ: خیر، در هر Request نمونه جدیدی ساخته می‌شود.

**سؤال ۲:** تفاوت اصلی `@ViewScoped` و `@SessionScoped` چیست؟

پاسخ: اولی در محدوده یک View باقی می‌ماند، اما دومی در تمام درخواست‌های یک Session حفظ می‌شود.

**سؤال ۳:** اگر دو کاربر هم‌زمان از یک ApplicationScoped Bean استفاده کنند، چه اتفاقی می‌افتد؟

پاسخ: هر دو از یک Instance استفاده می‌کنند؛ بنابراین باید به Thread Safety توجه کرد.

**سؤال ۴:** چرا در ViewScoped و SessionScoped از Serializable استفاده می‌کنیم؟

پاسخ: چون این Scopeها قابلیت Passivation و بازیابی وضعیت Bean را پشتیبانی می‌کنند و Serializable بودن برای آن‌ها لازم است.

**سؤال ۵:** تفاوت `@PostConstruct` و Constructor چیست؟

پاسخ: Constructor هنگام ایجاد Object اجرا می‌شود، اما `@PostConstruct` بعد از ایجاد Bean و انجام Dependency Injection اجرا می‌شود.

**نکته طلایی برای انتخاب Scope:**

- اطلاعات فقط برای یک درخواست لازم است؟ `@RequestScoped`
- اطلاعات باید در همان صفحه و بین Ajaxها حفظ شود؟ `@ViewScoped`
- اطلاعات باید بین صفحات مختلف کاربر باقی بماند؟ `@SessionScoped`
- اطلاعات برای تمام کاربران مشترک است؟ `@ApplicationScoped`

اگر این چهار Scope را به‌خوبی درک کنی، بخش مهمی از مدیریت State در JSF را یاد گرفته‌ای.
</div>



<!-- SOURCE: 2.md -->
# File: 2.md

<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">

در JSF و PrimeFaces، AJAX این امکان را فراهم می‌کند که فقط بخشی از صفحه بدون Reload کامل مرورگر پردازش و به‌روزرسانی شود.

دو مفهوم بسیار مهم در PrimeFaces وجود دارد:

- `process`: مشخص می‌کند کدام کامپوننت‌ها در سمت سرور پردازش شوند.
- `update`: مشخص می‌کند خروجی کدام کامپوننت‌ها دوباره در مرورگر Render شوند.

اگر تفاوت این دو را به‌خوبی درک کنی، بخش بزرگی از رفتار AJAX در JSF را متوجه شده‌ای.

# **1. AJAX در JSF چگونه کار می‌کند؟**

فرض کن یک فرم ثبت‌نام داری و می‌خواهی وقتی کاربر روی دکمه Check کلیک کرد، فقط نتیجه بررسی نمایش داده شود؛ بدون اینکه کل صفحه Reload شود.

[4. JSF Validation and AJAX | Java EE 7 Web Application Development](https://images.openai.com/static-rsc-4/D97Br0zg3sTzAjJrCl4IIgnTjVTCAglwLe_Tq70TH4ZRtDoLZDw53r_Lq7lJuzxC4VP0JkWw_FNnPRtF3ud8iN34Op2oD6H69ejLbwoUQY9ahQ3nVdPhKPpG-SPvyMPWMewWu-AJZnSuM4-pAcwe_hjVpDSH5X6enFEeUHxMPXM?purpose=inline)

[subscription.packtpub.com](https://subscription.packtpub.com/book/web-development/9781782176640/4/ch04lvl1sec39/a-partial-jsf-lifecycle?utm_source=chatgpt.com)

مراحل کار:

1. کاربر روی دکمه کلیک می‌کند.
2. JavaScript یک AJAX Request به سرور ارسال می‌کند.
3. JSF کامپوننت‌های مشخص‌شده در `process` را پردازش می‌کند.
4. Validation انجام می‌شود.
5. مقادیر به Managed Bean منتقل می‌شوند.
6. متد Action اجرا می‌شود.
7. کامپوننت‌های مشخص‌شده در `update` دوباره Render می‌شوند.
8. فقط بخش‌های مشخص‌شده از HTML در مرورگر جایگزین می‌شوند.

# **2. تفاوت process و update**

این مهم‌ترین قسمت موضوع است.

process

**چه اطلاعاتی به سرور پردازش شود؟**

- دریافت مقادیر Input
- اجرای Conversion
- اجرای Validation
- انتقال مقدار به Bean

update

**چه قسمت‌هایی از صفحه دوباره نمایش داده شوند؟**

- تولید HTML جدید
- ارسال HTML به مرورگر
- جایگزینی DOM قبلی با DOM جدید

به زبان ساده:

`process` مربوط به سمت سرور است.

`update` مربوط به نمایش مجدد در سمت مرورگر است.

# **3. یک مثال عملی با PrimeFaces**

فرض کن یک فرم داریم:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
<h:form id="myForm">

    <p:inputText id="username"
                 value="#{userBean.username}" />

    <p:commandButton value="Check"
                     action="#{userBean.check}"
                     process="@this username"
                     update="result" />

    <h:outputText id="result"
                  value="#{userBean.message}" />

</h:form>
```

Managed Bean:

```
@Named
@ViewScoped
public class UserBean implements Serializable {

    private String username;
    private String message;

    public void check() {
        if ("admin".equals(username)) {
            message = "Username exists!";
        } else {
            message = "Username available!";
        }
    }

    // Getters and Setters
}
```
</div>
چه اتفاقی می‌افتد؟

فرض کن کاربر مقدار `admin` را وارد کرده است.

وقتی روی Check کلیک می‌کند:

**مرحله اول: process**

```
process="@this username"
```

یعنی:

- دکمه پردازش شود.
- Input مربوط به username پردازش شود.
- مقدار username به Bean منتقل شود.

**مرحله دوم: اجرای Action**
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
public void check() {
    if ("admin".equals(username)) {
        message = "Username exists!";
    }
}
```
</div>
**مرحله سوم: update**

```
update="result"
```

یعنی فقط کامپوننت result دوباره Render شود.

در نتیجه خروجی جدید در صفحه نمایش داده می‌شود، بدون Reload کامل صفحه.

# **4. مقادیر مهم process و update**

PrimeFaces چند عبارت آماده برای این دو Attribute دارد:

| مقدار         | معنی                                 |
| ------------- | ------------------------------------ |
| `@this`       | فقط کامپوننت فعلی                    |
| `@form`       | تمام کامپوننت‌های فرم                |
| `@all`        | تمام View                            |
| `@none`       | هیچ کامپوننتی                        |
| `@parent`     | کامپوننت والد                        |
| `@(.myClass)` | کامپوننت‌های دارای CSS Selector مشخص |

مثلاً:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
<p:commandButton
    value="Save"
    action="#{bean.save}"
    process="@form"
    update="@form" />
```
</div>
در این حالت کل فرم پردازش و دوباره Render می‌شود.

یا:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
<p:commandButton
    value="Refresh"
    process="@this"
    update="table" />
```
</div>
فقط خود دکمه پردازش می‌شود و جدول دوباره Render خواهد شد.

# **5. تفاوت process و update با یک مثال مهم**

فرض کن دو Input داری:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
<h:form id="form">

    <p:inputText id="name"
                 value="#{bean.name}" />

    <p:inputText id="age"
                 value="#{bean.age}" />

    <p:commandButton value="Submit"
                     action="#{bean.submit}"
                     process="name"
                     update="result" />

    <h:outputText id="result"
                  value="#{bean.message}" />

</h:form>
```
</div>
کاربر این مقادیر را وارد می‌کند:

- name = Ali
- age = 25

اما ما فقط `name` را در process قرار داده‌ایم.

بنابراین:

- مقدار name به Bean منتقل می‌شود.
- مقدار age ممکن است در Bean قبلی باقی بماند.
- متد submit اجرا می‌شود.
- نتیجه در result نمایش داده می‌شود.

این یکی از مهم‌ترین تفاوت‌هایی است که باید درک کنی.

**نکته:** اگر کامپوننتی در process نباشد، مقدار ارسال‌شده آن برای مدل Bean اعمال نمی‌شود. همچنین Validation آن کامپوننت هم اجرا نمی‌شود.

# **6. تفاوت action و actionListener در AJAX**

در PrimeFaces معمولاً این دو را می‌بینی:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
<p:commandButton
    value="Save"
    action="#{bean.save}"
    process="@form"
    update="@form" />
```
</div>
و:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
<p:commandButton
    value="Check"
    actionListener="#{bean.check}"
    process="@this"
    update="result" />
```
</div>
تفاوت:

| action                         | actionListener            |
| ------------------------------ | ------------------------- |
| برای عملیات اصلی برنامه        | برای واکنش به Event       |
| می‌تواند Navigation انجام دهد  | معمولاً برای پردازش Event |
| می‌تواند String نتیجه برگرداند | معمولاً void است          |

# **7. یک نکته مهم: چرا گاهی AJAX کار نمی‌کند؟**

در پروژه‌های واقعی JSF و PrimeFaces این مشکلات زیاد اتفاق می‌افتند:

**مشکل اول: update کامپوننتی که Render نشده است**

مثلاً:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
<p:outputPanel rendered="#{bean.show}">
    <h:outputText id="result"
                  value="#{bean.message}" />
</p:outputPanel>
```
</div>

اگر `show=false` باشد، کامپوننت در DOM وجود ندارد و نمی‌توان مستقیماً آن را با AJAX به‌روزرسانی کرد.

راه‌حل:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
<p:outputPanel id="container">
    <p:outputPanel rendered="#{bean.show}">
        <h:outputText value="#{bean.message}" />
    </p:outputPanel>
</p:outputPanel>
```
</div>
سپس:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
update="container"
```
</div>
**مشکل دوم: Validation مانع اجرای Action می‌شود**

اگر یک Input اجباری باشد و مقدار آن خالی بماند، ممکن است Action اجرا نشود.

برای مثال:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
<p:inputText value="#{bean.name}"
             required="true" />

<p:commandButton value="Cancel"
                 action="#{bean.cancel}"
                 process="@this" />
```
</div>
با `process="@this"` ورودی اجباری پردازش نمی‌شود؛ بنابراین Validation آن مانع اجرای cancel نخواهد شد.

**مشکل سوم: اشتباه در ID**

در JSF کامپوننت‌ها Naming Container دارند. بنابراین گاهی باید از ID کامل استفاده کنی:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
update=":myForm:result"
```
</div>
علامت `:` یعنی جستجو از ریشه View شروع شود.

# **8. یک تمرین برای سنجش یادگیری**

فرض کن فرم زیر را داریم:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
<h:form id="form">

    <p:inputText id="firstName"
                 value="#{bean.firstName}" />

    <p:inputText id="lastName"
                 value="#{bean.lastName}" />

    <p:commandButton
        value="Test"
        action="#{bean.test}"
        process="@this firstName"
        update="result" />

    <h:outputText id="result"
                  value="#{bean.message}" />

</h:form>
```
</div>
کاربر مقادیر زیر را وارد می‌کند:

- firstName = Ali
- lastName = Ahmadi

و سپس روی Test کلیک می‌کند.

**سؤال: هنگام اجرای متد&#x20;**`test()`**&#x20;مقدار lastName در Bean چه وضعیتی دارد؟**

مقدار Ahmadi به Bean منتقل می‌شود.

مقدار lastName به Bean منتقل نمی‌شود و مقدار قبلی باقی می‌ماند.

کل فرم دوباره ساخته می‌شود.

متد test اجرا نمی‌شود.

بررسی پاسخ

**نکته نهایی برای مصاحبه Java EE:**

اگر فقط سه جمله از این مبحث به خاطر بسپاری:

1. `process` تعیین می‌کند چه کامپوننت‌هایی در چرخه پردازش JSF شرکت کنند.
2. `update` تعیین می‌کند چه کامپوننت‌هایی دوباره Render شوند.
3. AJAX در JSF همچنان از چرخه حیات JSF استفاده می‌کند، اما پردازش و Render را به‌صورت جزئی انجام می‌دهد.

برای یادگیری عمیق‌تر، موضوع بعدی بسیار مهم **JSF Lifecycle و فازهای Execute و Render در Partial AJAX Request** است.
</div>



<!-- SOURCE: 3.md -->
# File: 3.md

<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">




این مبحث یکی از مهم‌ترین بخش‌های JSF و PrimeFaces است. وقتی درک کنی که در یک AJAX Request کدام Phaseها اجرا می‌شوند و کدام کامپوننت‌ها پردازش می‌شوند، بسیاری از مشکلات پیچیده JSF برایت قابل حل می‌شوند.

بیایید با یک مثال واقعی و مرحله‌به‌مرحله بررسی کنیم.

# **1. چرخه حیات JSF چیست؟**

JSF برای پردازش هر HTTP Request از یک چرخه مشخص استفاده می‌کند که شامل ۶ مرحله اصلی است.

## **JSF Lifecycle**

1. **Restore View**

   ساخت یا بازیابی Component Tree صفحه
2. **Apply Request Values**

   خواندن مقادیر ارسال‌شده از مرورگر
3. **Process Validations**

   اعتبارسنجی و تبدیل مقادیر ورودی
4. **Update Model Values**

   انتقال مقادیر معتبر به فیلدهای Managed Bean
5. **Invoke Application**

   اجرای Action و ActionListener
6. **Render Response**

   تولید HTML و ارسال پاسخ به مرورگر

نکته مهم: این شش مرحله برای درخواست‌های معمولی JSF هستند؛ در AJAX هم از همین Lifecycle استفاده می‌شود، اما ممکن است فقط بخشی از کامپوننت‌ها پردازش یا Render شوند.

# **2. دو مفهوم Execute و Render در AJAX**

در JSF Partial AJAX Request دو مفهوم اصلی داریم:

Execute

**چه چیزی پردازش شود؟**

کامپوننت‌هایی که در مراحل دریافت مقدار، Validation و Update Model شرکت می‌کنند.

معادل مفهومی: process

Render

**چه چیزی نمایش داده شود؟**

کامپوننت‌هایی که HTML آن‌ها دوباره تولید و به مرورگر ارسال می‌شود.

معادل مفهومی: update

در استاندارد JSF این دو مفهوم با این پارامترها شناخته می‌شوند:

| مفهوم   | پارامتر AJAX | PrimeFaces |
| ------- | ------------ | ---------- |
| Execute | `execute`    | `process`  |
| Render  | `render`     | `update`   |

در PrimeFaces معمولاً از `process` و `update` استفاده می‌کنیم.

# **3. یک مثال واقعی از Partial AJAX Request**

فرض کن صفحه‌ای برای ثبت اطلاعات کاربر داریم:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
<h:form id="userForm">

    <p:inputText id="name"
                 value="#{userBean.name}"
                 required="true" />

    <p:inputText id="email"
                 value="#{userBean.email}" />

    <p:commandButton
        value="Save"
        action="#{userBean.save}"
        process="@this name email"
        update="result" />

    <p:outputPanel id="result">
        <h:outputText value="#{userBean.message}" />
    </p:outputPanel>

</h:form>
```
</div>
Managed Bean:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
@Named
@ViewScoped
public class UserBean implements Serializable {

    private String name;
    private String email;
    private String message;

    public void save() {
        message = "User saved: " + name;
    }

    // Getters and Setters
}
```
</div>
فرض کن کاربر این اطلاعات را وارد کرده:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
Name  = Ali
Email = ali@example.com
```
</div>
و روی Save کلیک می‌کند.

حالا ببینیم در پشت صحنه چه اتفاقی می‌افتد.

# **4. اجرای Lifecycle در AJAX**

## **Phase 1: Restore View**

JSF صفحه قبلی را بازیابی می‌کند.

در این مرحله Component Tree شامل موارد زیر است:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
userForm
 ├── name
 ├── email
 ├── saveButton
 └── result
```
</div>
چون درخواست AJAX است، JSF وضعیت View قبلی را بازیابی می‌کند.

## **Phase 2: Apply Request Values**

در این مرحله JSF مقادیر ارسال‌شده از مرورگر را در کامپوننت‌های مربوطه قرار می‌دهد.

مثلاً:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
name = Ali
email = ali@example.com
```
</div>
این مقادیر فعلاً در خود کامپوننت‌های JSF قرار دارند، نه لزوماً در Managed Bean.

این تفاوت بسیار مهم است.

## **Phase 3: Process Validations**

JSF مقادیر را بررسی می‌کند:

- آیا مقدار Required خالی است؟
- آیا نوع داده معتبر است؟
- آیا Converter موفق بوده است؟
- آیا Validator موفق بوده است؟

مثلاً اگر `name` خالی باشد، Validation شکست می‌خورد.

در این شرایط:

- فازهای Update Model Values و Invoke Application معمولاً اجرا نمی‌شوند.
- متد `save()` اجرا نمی‌شود.
- JSF به Render Response می‌رود تا خطاها را نمایش دهد.

## **Phase 4: Update Model Values**

اگر Validation موفق باشد، مقادیر کامپوننت‌ها به فیلدهای Bean منتقل می‌شوند.
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
userBean.setName("Ali");

userBean.setEmail("ali@example.com");
```
</div>
حالا Managed Bean دارای مقادیر جدید است.

## **Phase 5: Invoke Application**

در این مرحله Action اجرا می‌شود:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
public void save() {

    message = "User saved: " + name;

}
```
</div>
متد `save()` اجرا شده و مقدار message تغییر می‌کند.

## **Phase 6: Render Response**

حالا JSF خروجی HTML کامپوننت‌هایی را که در Render مشخص شده‌اند تولید می‌کند.

در مثال ما:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
update="result"
```
</div>
بنابراین فقط `result` دوباره Render می‌شود.

مرورگر نیز HTML جدید را در همان قسمت جایگزین می‌کند.

[4. JSF Validation and AJAX | Java EE 7 Web Application Development](https://images.openai.com/static-rsc-4/D97Br0zg3sTzAjJrCl4IIgnTjVTCAglwLe_Tq70TH4ZRtDoLZDw53r_Lq7lJuzxC4VP0JkWw_FNnPRtF3ud8iN34Op2oD6H69ejLbwoUQY9ahQ3nVdPhKPpG-SPvyMPWMewWu-AJZnSuM4-pAcwe_hjVpDSH5X6enFEeUHxMPXM?purpose=inline)

[subscription.packtpub.com](https://subscription.packtpub.com/book/web-development/9781782176640/4/ch04lvl1sec39/a-partial-jsf-lifecycle?utm_source=chatgpt.com)

# **5. تفاوت Full Request و Partial AJAX Request**

این دو را با هم مقایسه کنیم:

| ویژگی              | Full Request            | Partial AJAX                  |
| ------------------ | ----------------------- | ----------------------------- |
| HTTP Request       | بله                     | بله                           |
| Lifecycle JSF      | کامل                    | همان Lifecycle با پردازش جزئی |
| پردازش ورودی‌ها    | معمولاً کل فرم          | محدوده Execute                |
| Validation         | کامپوننت‌های پردازش‌شده | محدوده Execute                |
| اجرای Action       | بله                     | بله                           |
| Render             | کل View                 | محدوده Render                 |
| Reload کامل مرورگر | بله                     | خیر                           |

نکته: در AJAX فقط بخش‌های مشخص‌شده در Render به مرورگر ارسال می‌شوند؛ این به معنی حذف سایر مراحل Lifecycle نیست.

# **6. نکته بسیار مهم: Immediate Attribute**

یکی از موضوعات پیچیده JSF، ویژگی `immediate` است.

فرض کن یک فرم داری که فیلدهای اجباری دارد و یک دکمه Cancel نیز در آن قرار داده‌ای.
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
<h:form>

    <p:inputText value="#{bean.name}"
                 required="true" />

    <p:commandButton
        value="Save"
        action="#{bean.save}" />

    <p:commandButton
        value="Cancel"
        action="#{bean.cancel}"
        immediate="true"
        process="@this" />

</h:form>
```
</div>
چرا برای Cancel از `immediate="true"` استفاده می‌کنیم؟

چون ممکن است کاربر فرم را پر نکرده باشد و بخواهد عملیات را لغو کند.

بدون Immediate، Validation فیلدهای فرم ممکن است مانع اجرای Cancel شود.

با `immediate="true"`:

- رویداد دکمه زودتر پردازش می‌شود.
- Action دکمه در فاز Apply Request Values اجرا می‌شود، نه Invoke Application.
- Validation ورودی‌های دیگر مانع اجرای Action نمی‌شود.

البته `process="@this"` نیز در این مثال باعث می‌شود سایر ورودی‌ها اصلاً پردازش نشوند.

**تفاوت مهم:**

- `process="@this"` محدوده کامپوننت‌های پردازش‌شده را محدود می‌کند.
- `immediate="true"` زمان پردازش رویداد را تغییر می‌دهد.

این دو جایگزین یکدیگر نیستند.

# **7. چگونه فازهای JSF را در عمل مشاهده کنیم؟**

می‌توانی از `PhaseListener` استفاده کنی.

این ابزار به تو اجازه می‌دهد اجرای Phaseهای JSF را در Console مشاهده کنی.

مثال:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
import jakarta.faces.event.PhaseListener;
import jakarta.faces.event.PhaseEvent;
import jakarta.faces.event.PhaseId;

public class MyPhaseListener implements PhaseListener {

    @Override
    public PhaseId getPhaseId() {
        return PhaseId.ANY_PHASE;
    }

    @Override
    public void beforePhase(PhaseEvent event) {

        System.out.println(
            "BEFORE: " +
            event.getPhaseId()
        );
    }

    @Override
    public void afterPhase(PhaseEvent event) {

        System.out.println(
            "AFTER: " +
            event.getPhaseId()
        );
    }
}
```
</div>

ثبت آن در `faces-config.xml`:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
<lifecycle>
    <phase-listener>
        com.example.MyPhaseListener
    </phase-listener>
</lifecycle>
```
</div>
حالا هنگام اجرای درخواست می‌توانی ببینی هر Phase چه زمانی اجرا شده است.

برای AJAX هم می‌توانی Partial Request را در مرورگر، از مسیر Developer Tools → Network → Fetch/XHR بررسی کنی.

# **8. یک نکته حرفه‌ای: Partial Request و Partial Response**

در JSF استاندارد، AJAX Request معمولاً پارامترهایی مانند این دارد:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
jakarta.faces.partial.ajax=true

jakarta.faces.source=userForm:saveButton

jakarta.faces.partial.execute=
userForm:saveButton userForm:name userForm:email

jakarta.faces.partial.render=
userForm:result
```
</div>
این پارامترها به JSF می‌گویند:

- کدام کامپوننت درخواست را ایجاد کرده است.
- چه کامپوننت‌هایی Execute شوند.
- چه کامپوننت‌هایی Render شوند.

پاسخ سرور نیز یک Partial Response است که می‌تواند شامل HTML جدید، پیام‌های خطا و اطلاعات دیگر باشد.

# **9. تمرین مصاحبه‌ای**

این مثال را با دقت بررسی کن:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
<h:form id="form">

    <p:inputText id="name"
                 value="#{bean.name}"
                 required="true" />

    <p:inputText id="email"
                 value="#{bean.email}"
                 required="true" />

    <p:commandButton
        value="Save"
        action="#{bean.save}"
        process="@this name"
        update="result" />

    <p:outputPanel id="result">
        <h:outputText value="#{bean.message}" />
    </p:outputPanel>

</h:form>
```
</div>
**آزمون JSF Lifecycle**

0 از 4 پاسخ

**1. آیا Validation مربوط به email اجرا می‌شود؟**

بله، چون در همان فرم است.

خیر، چون در process قرار ندارد.

**2. آیا مقدار جدید name به Managed Bean منتقل می‌شود؟**

بله، در صورت موفقیت Validation.

خیر، چون AJAX است.

**3. کدام قسمت صفحه دوباره Render می‌شود؟**

کل فرم

فقط result

فقط name

**4. اگر Validation مربوط به name شکست بخورد، آیا save اجرا می‌شود؟**

بله

خیر

بررسی پاسخ‌ها

## **جمع‌بندی نهایی**

این جدول را برای مصاحبه به خاطر بسپار:

| مفهوم                | وظیفه                   |
| -------------------- | ----------------------- |
| Restore View         | بازیابی صفحه            |
| Apply Request Values | دریافت مقادیر ورودی     |
| Process Validations  | اعتبارسنجی              |
| Update Model Values  | انتقال مقادیر به Bean   |
| Invoke Application   | اجرای Action            |
| Render Response      | تولید خروجی             |
| Execute / process    | تعیین محدوده پردازش     |
| Render / update      | تعیین محدوده نمایش مجدد |
| Immediate            | تغییر زمان پردازش Event |

**پیشنهاد برای ادامه یادگیری:** موضوع بسیار مهم بعدی در JSF، تفاوت `ValueChangeListener`، `AjaxBehaviorListener`، `ActionListener` و `Action` است؛ این چهار مورد در پروژه‌های واقعی PrimeFaces کاربرد زیادی دارند.
</div>



<!-- SOURCE: 4.md -->
# File: 4.md

<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">

در JSF، دو مفهوم **Converter** و **Validator** برای کنترل و مدیریت داده‌های ورودی فرم استفاده می‌شوند.

این دو بسیار مهم هستند، چون داده‌ای که کاربر در HTML وارد می‌کند معمولاً به شکل String به سرور ارسال می‌شود؛ در حالی که در Java ممکن است به `Integer`، `Date`، `BigDecimal` یا Objectهای دیگر نیاز داشته باشیم.

تفاوت اصلی:

- **Converter:** تبدیل نوع داده از String به Java Object و برعکس.
- **Validator:** بررسی معتبر بودن داده ورودی.

بیایید هر دو را با مثال واقعی پیاده‌سازی کنیم.

# **1. Converter در JSF چیست؟**

فرض کن کاربر تاریخ زیر را وارد می‌کند:

```
2026-11-19
```

اما در Java می‌خواهیم آن را به یک Object از نوع `LocalDate` تبدیل کنیم.

Converter این کار را انجام می‌دهد.

**Browser**

# **"2026-11-19"**

String

getAsObject()

**Java Application**

# **LocalDate**

Java Object

getAsString()

**Browser**

# **"2026-11-19"**

String

## **2. پیاده‌سازی Custom Converter**

در JSF برای ساخت Converter اختصاصی، Interface زیر را پیاده‌سازی می‌کنیم:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
jakarta.faces.convert.Converter<T>
```
</div>
مثال: تبدیل String به LocalDate.
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@FacesConverter("localDateConverter")
public class LocalDateConverter
        implements Converter<LocalDate> {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public LocalDate getAsObject(
            FacesContext context,
            UIComponent component,
            String value) {

        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        return LocalDate.parse(value, FORMATTER);
    }

    @Override
    public String getAsString(
            FacesContext context,
            UIComponent component,
            LocalDate value) {

        if (value == null) {
            return "";
        }

        return value.format(FORMATTER);
    }
}

```
</div>
دو متد اصلی داریم:

| متد             | وظیفه                 |
| --------------- | --------------------- |
| `getAsObject()` | String به Java Object |
| `getAsString()` | Java Object به String |

استفاده در XHTML:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
<h:form>

    <p:inputText
        value="#{userBean.birthDate}"
        converter="localDateConverter" />

    <p:commandButton
        value="Save"
        action="#{userBean.save}" />

</h:form>
```
</div>
در Managed Bean:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
private LocalDate birthDate;
```
</div>
حالا JSF مقدار ورودی را به LocalDate تبدیل می‌کند.

نکته: اگر تاریخ نامعتبر باشد، بهتر است DateTimeParseException را به ConverterException تبدیل کنی تا JSF بتواند خطای Conversion را مدیریت کند.

# **3. Validator در JSF چیست؟**

فرض کن کاربر یک ایمیل وارد می‌کند:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
ali@gmail.com
```
</div>
می‌خواهیم بررسی کنیم که ایمیل معتبر باشد.

این کار وظیفه Validator است، نه Converter.

برای پیاده‌سازی Validator از Interface زیر استفاده می‌کنیم:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
jakarta.faces.validator.Validator<T>
```
</div>
این Interface دارای متد اصلی زیر است:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
validate()
```
</div>
## **4. پیاده‌سازی Custom Validator**

مثال: اعتبارسنجی ایمیل.
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">


```

import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.validator.Validator;
import jakarta.faces.validator.ValidatorException;
import jakarta.faces.validator.FacesValidator;

@FacesValidator("emailValidator")
public class EmailValidator
        implements Validator<String> {

    @Override
    public void validate(
            FacesContext context,
            UIComponent component,
            String value) {

        if (value == null || value.trim().isEmpty()) {
            return;
        }

        String emailRegex =
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

        if (!value.matches(emailRegex)) {

            FacesMessage message = new FacesMessage(
                FacesMessage.SEVERITY_ERROR,
                "Invalid Email",
                "Please enter a valid email address"
            );

            throw new ValidatorException(message);
        }
    }
}

```
</div>
استفاده در XHTML:
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
<h:form>

    <p:inputText
        id="email"
        value="#{userBean.email}"
        validator="emailValidator" />

    <p:message for="email" />

    <p:commandButton
        value="Register"
        action="#{userBean.register}"
        process="@form"
        update="@form" />

</h:form>
```
</div>
اگر ایمیل نامعتبر باشد:

1. Validator اجرا می‌شود.
2. `ValidatorException` ایجاد می‌شود.
3. JSF پیام خطا را ثبت می‌کند.
4. Model Value به‌روزرسانی نمی‌شود.
5. Action Method اجرا نمی‌شود.
6. پیام خطا در صفحه نمایش داده می‌شود.

# **5. تفاوت Converter و Validator**

این جدول برای مصاحبه Java EE بسیار مهم است.

| ویژگی     | Converter             | Validator          |
| --------- | --------------------- | ------------------ |
| هدف       | تبدیل داده            | اعتبارسنجی داده    |
| متد اصلی  | getAsObject           | validate           |
| ورودی     | String                | Object تبدیل‌شده   |
| خروجی     | Java Object یا String | بدون خروجی         |
| خطا       | ConverterException    | ValidatorException |
| زمان اجرا | قبل از Validation     | بعد از Conversion  |

مثال:

فرض کن کاربر مقدار زیر را وارد می‌کند:

```
abc
```

و فیلد Java از نوع Integer است.

**Converter** تلاش می‌کند آن را به Integer تبدیل کند.

چون تبدیل امکان‌پذیر نیست، خطای Conversion رخ می‌دهد.

اما اگر کاربر مقدار زیر را وارد کند:

```
150
```

تبدیل موفق است، ولی اگر قانون ما این باشد که سن باید بین 18 و 100 باشد، **Validator** مقدار 150 را رد می‌کند.

## **ترتیب اجرای Conversion و Validation**

**User Input**

# **"150"**

**Converter**

## **Integer.valueOf("150")**

**Validator**

## **Check: 18 ≤ age ≤ 100**

Validation Failed

Age 150 is not allowed.

# **6. استفاده از Built-in Converter و Validator**

JSF خودش Converter و Validatorهای آماده دارد و همیشه لازم نیست کلاس اختصاصی بنویسی.

**Converterهای آماده:**
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
<p:inputText value="#{bean.age}">
    <f:convertNumber integerOnly="true"/>
</p:inputText>
```
</div>
تبدیل عددی.
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
<p:inputText value="#{bean.price}">
    <f:convertNumber type="currency"
                     currencySymbol="$"/>
</p:inputText>
```
</div>
تبدیل مبلغ پولی.
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
<h:inputText value="#{bean.birthDate}">
    <f:convertDateTime pattern="yyyy-MM-dd"/>
</h:inputText>
```
</div>
تبدیل تاریخ. برای Java EE قدیمی بسیار رایج است؛ در Jakarta Faces جدید برای `LocalDate` می‌توان از `f:convertDateTime` با `type="localDate"` نیز استفاده کرد، مشروط به پشتیبانی نسخه.

**Validatorهای آماده:**
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
<p:inputText value="#{bean.username}">
    <f:validateLength minimum="3"
                      maximum="20"/>
</p:inputText>
```
</div>

بررسی طول رشته.
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
<p:inputText value="#{bean.age}">
    <f:validateLongRange minimum="18"
                         maximum="100"/>
</p:inputText>
```
</div>
بررسی محدوده عددی.
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
<p:inputText value="#{bean.price}">
    <f:validateDoubleRange minimum="0"
                           maximum="10000"/>
</p:inputText>
```
</div>
بررسی محدوده اعشاری.

# **7. تفاوت Bean Validation و JSF Validator**

یک نکته حرفه‌ای دیگر: در Java EE دو روش برای اعتبارسنجی داریم.

**روش اول: JSF Validator**
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
@FacesValidator("ageValidator")
public class AgeValidator
        implements Validator<Integer> {
    // validation logic
}
```
</div>
**روش دوم: Bean Validation با Jakarta Validation**
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
import jakarta.validation.constraints.*;

public class User {

    @NotBlank
    @Size(min = 3, max = 20)
    private String username;

    @Min(18)
    @Max(100)
    private Integer age;

    @Email
    private String email;

}
```
</div>
در Managed Bean:
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
@Named
@RequestScoped
public class UserBean {

    @Valid
    private User user;

}
```
</div>
در صورت استفاده از JSF و Bean Validation یکپارچه، JSF می‌تواند هنگام پردازش مقادیر فرم، Constraintهای Bean Validation را نیز اجرا کند.

تفاوت کاربردی:

| JSF Validator            | Bean Validation                    |
| ------------------------ | ---------------------------------- |
| وابسته به JSF            | مستقل از JSF                       |
| مناسب Validation فرم     | مناسب Validation در لایه‌های مختلف |
| پیاده‌سازی با Validator  | استفاده از Annotation              |
| مناسب قوانین مرتبط با UI | مناسب قوانین مدل و کسب‌وکار        |

**پیشنهاد معماری:** قوانین عمومی داده‌ها را در Bean Validation قرار بده؛ قوانین اختصاصی UI را در JSF Validator و تبدیل نوع داده را در Converter انجام بده.

# **8. چه زمانی از کدام استفاده کنیم؟**

| سناریو                        | انتخاب                        |
| ----------------------------- | ----------------------------- |
| String به LocalDate           | Converter                     |
| String به Integer             | Converter                     |
| بررسی محدوده سن               | Validator                     |
| بررسی فرمت ایمیل              | Validator                     |
| بررسی اجباری بودن فیلد        | `required="true"`             |
| بررسی طول نام                 | `@Size` یا `f:validateLength` |
| اعتبارسنجی Entity در چند لایه | Bean Validation               |

# **9. تمرین مصاحبه‌ای**

فرض کن یک فرم ثبت‌نام داریم:
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
<p:inputText
    id="age"
    value="#{userBean.age}"
    converter="jakarta.faces.Integer" />
```
</div>

کاربر مقدار `abc` را وارد می‌کند.

**چه اتفاقی می‌افتد؟**

Converter Error رخ می‌دهد.

Validator Error رخ می‌دهد.

مقدار abc در Integer ذخیره می‌شود.

Action Method حتماً اجرا می‌شود.

بررسی پاسخ

**یک نکته مهم برای مهارت حرفه‌ای Java EE:**

در پروژه‌های واقعی بهتر است Converter و Validator را با Dependency Injection نیز آشنا باشی؛ مثلاً وقتی Validator نیاز دارد از Database بررسی کند که آیا یک Username قبلاً ثبت شده است یا خیر.

برای چنین سناریویی باید نحوه استفاده از `@FacesValidator(managed = true)` در نسخه‌های جدید Jakarta Faces و تزریق سرویس با `@Inject` را یاد بگیری. این موضوع در پروژه‌های Enterprise بسیار کاربردی است.
</div>



<!-- SOURCE: 5.md -->
# File: 5.md

<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">


مدیریت خطا و Exception Handling در JSF و PrimeFaces یکی از موضوعات مهم در توسعه برنامه‌های Enterprise با Java EE / Jakarta EE است.

در یک پروژه واقعی، خطاها ممکن است در قسمت‌های مختلف اتفاق بیفتند:

- هنگام دریافت اطلاعات فرم (Conversion Error)
- هنگام اعتبارسنجی اطلاعات (Validation Error)
- هنگام اجرای Action Method
- هنگام ارتباط با Database
- هنگام فراخوانی REST API
- هنگام اجرای AJAX Request
- هنگام بروز خطاهای غیرمنتظره مانند `NullPointerException`

برای مدیریت صحیح این خطاها باید تفاوت بین **Validation Error، Application Exception و System Exception** را بدانیم.

# **1. انواع خطا در JSF**

ابتدا خطاها را به سه دسته تقسیم کنیم:

Validation Error

**خطای اعتبارسنجی**

مثال: کاربر ایمیل نامعتبر وارد می‌کند یا فیلد اجباری را خالی می‌گذارد.

Application Exception

**خطای منطق کسب‌وکار**

مثال: موجودی حساب کافی نیست یا کاربر اجازه انجام عملیات را ندارد.

System Exception

**خطای فنی و غیرمنتظره**

مثال: Database Connection قطع شده یا NullPointerException رخ داده است.

هر دسته روش مدیریت متفاوتی دارد.

# **2. مدیریت Validation Error با FacesMessage**

یکی از روش‌های اصلی نمایش خطا در JSF استفاده از `FacesMessage` است.

فرض کن کاربر نام کاربری نامعتبر وارد کرده است.
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">




```
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;

public void register() {

    if (username == null || username.length() < 3) {

        FacesMessage message = new FacesMessage(
            FacesMessage.SEVERITY_ERROR,
            "Validation Error",
            "Username must contain at least 3 characters"
        );

        FacesContext.getCurrentInstance()
                    .addMessage("registerForm:username", message);

        return;
    }

    // Continue registration
}
```
</div>
در XHTML:
<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;"><


```
<h:form id="registerForm">

    <p:inputText id="username"
                 value="#{userBean.username}" />

    <p:message for="username" />

    <p:commandButton
        value="Register"
        action="#{userBean.register}"
        update="@form" />

</h:form>
```
</div>
در این مثال:

- `FacesMessage` پیام خطا را ایجاد می‌کند.
- `addMessage()` پیام را به JSF اضافه می‌کند.
- `p:message` خطای مربوط به یک کامپوننت را نمایش می‌دهد.

اگر بخواهیم پیام‌های عمومی را نمایش دهیم:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">




```
<p:messages id="messages"
            showDetail="true"
            closable="true" />
```
</div>

تفاوت:

| کامپوننت     | کاربرد                                 |
| ------------ | -------------------------------------- |
| `p:message`  | نمایش خطای یک فیلد مشخص                |
| `p:messages` | نمایش مجموعه پیام‌های صفحه             |
| `h:message`  | نسخه استاندارد JSF برای یک فیلد        |
| `h:messages` | نسخه استاندارد JSF برای مجموعه پیام‌ها |

# **3. مدیریت Exception در Action Method**

فرض کن متدی داریم که اطلاعات کاربر را در Database ذخیره می‌کند.

ممکن است Database در دسترس نباشد یا عملیات ذخیره‌سازی شکست بخورد.

روش مناسب این است که Exception را مدیریت کنیم و پیام قابل فهم به کاربر نمایش دهیم.
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">




```
@Named
@ViewScoped
public class UserBean implements Serializable {

    private String username;

    @Inject
    private UserService userService;

    public void save() {

        try {

            userService.save(username);

            addMessage(
                FacesMessage.SEVERITY_INFO,
                "Success",
                "User saved successfully"
            );

        } catch (DuplicateUserException e) {

            addMessage(
                FacesMessage.SEVERITY_WARN,
                "Warning",
                "Username already exists"
            );

        } catch (Exception e) {

            // Log the actual exception
            e.printStackTrace();

            addMessage(
                FacesMessage.SEVERITY_ERROR,
                "Error",
                "An unexpected error occurred"
            );
        }
    }

    private void addMessage(
            FacesMessage.Severity severity,
            String summary,
            String detail) {

        FacesContext.getCurrentInstance()
            .addMessage(null,
                new FacesMessage(severity, summary, detail));
    }
}
```
</div>
در اینجا:

- خطای Duplicate User به‌صورت اختصاصی مدیریت می‌شود.
- سایر خطاها به‌صورت عمومی مدیریت می‌شوند.
- جزئیات فنی خطا به کاربر نمایش داده نمی‌شود.

در پروژه واقعی به‌جای e.printStackTrace() از Logger استفاده کن و Exception اصلی را همراه Stack Trace ثبت کن.

# **4. استفاده از Custom Exception**

در پروژه‌های Enterprise بهتر است برای خطاهای کسب‌وکار Exception اختصاصی تعریف کنیم.

مثلاً:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">




```
public class DuplicateUserException
        extends RuntimeException {

    public DuplicateUserException(String message) {
        super(message);
    }
}
```

در Service:

```
public void save(String username) {

    if (userRepository.existsByUsername(username)) {

        throw new DuplicateUserException(
            "Username already exists"
        );
    }

    userRepository.save(username);
}
```
</div>

در Managed Bean:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
try {

    userService.save(username);

} catch (DuplicateUserException e) {

    FacesContext.getCurrentInstance()
        .addMessage(null,
            new FacesMessage(
                FacesMessage.SEVERITY_ERROR,
                "Registration Failed",
                "This username is already registered"
            )
        );
}
```
</div>
این معماری باعث می‌شود:

- Business Logic در Service باقی بماند.
- نمایش پیام در JSF انجام شود.
- کدها قابل تست و نگهداری باشند.

# **5. مدیریت Exception در AJAX با PrimeFaces**

این قسمت بسیار مهم است.

فرض کن کاربر روی یک دکمه AJAX کلیک می‌کند:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
<p:commandButton
    value="Load Data"
    action="#{bean.loadData}"
    process="@this"
    update="result" />
```
</div>
حالا فرض کن هنگام اجرای `loadData()` یک Exception رخ می‌دهد.
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
public void loadData() {

    throw new RuntimeException("Database Error");

}
```
</div>
اگر Exception مدیریت نشود، ممکن است درخواست AJAX با خطا مواجه شود و خروجی مورد انتظار نمایش داده نشود.

برای مدیریت خطاهای عادی بهتر است Exception را در Action Method یا Service مدیریت کنیم و پیام مناسب به JSF بدهیم.

مثال:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
public void loadData() {

    try {

        data = dataService.load();

        message = "Data loaded successfully";

    } catch (Exception e) {

        logger.error("Error loading data", e);

        message = "Unable to load data";
    }
}
```
</div>
در XHTML:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
<h:form id="form">

    <p:commandButton
        value="Load Data"
        action="#{bean.loadData}"
        process="@this"
        update="result messages" />

    <p:outputPanel id="result">
        <h:outputText value="#{bean.message}" />
    </p:outputPanel>

    <p:messages id="messages" />

</h:form>
```
</div>
## **استفاده از p:ajaxStatus**

PrimeFaces کامپوننتی دارد که وضعیت AJAX را نمایش می‌دهد.
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
<p:ajaxStatus>

    <f:facet name="start">
        <h:outputText value="Loading..." />
    </f:facet>

    <f:facet name="complete">
        <h:outputText value="Completed" />
    </f:facet>

</p:ajaxStatus>
```
</div>
این کامپوننت برای نمایش وضعیت درخواست AJAX کاربرد دارد؛ اما خودش Exception را مدیریت نمی‌کند.

# **6. ExceptionHandler در JSF**

یکی از قابلیت‌های قدرتمند JSF، `ExceptionHandler` است.

فرض کن در چندین Managed Bean خطاهای مختلفی رخ می‌دهد.

آیا باید در تک‌تک متدها try/catch بنویسیم؟

خیر.

می‌توانیم یک ExceptionHandler سفارشی بسازیم تا Exceptionهای مدیریت‌نشده JSF را در یک نقطه مرکزی بررسی کنیم.

ساخت کلاس:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
import jakarta.faces.context.ExceptionHandler;
import jakarta.faces.context.ExceptionHandlerWrapper;
import jakarta.faces.context.ExceptionQueuedEvent;
import jakarta.faces.context.ExceptionQueuedEventContext;

import java.util.Iterator;

public class CustomExceptionHandler
        extends ExceptionHandlerWrapper {

    private final ExceptionHandler wrapped;

    public CustomExceptionHandler(
            ExceptionHandler wrapped) {

        this.wrapped = wrapped;
    }

    @Override
    public ExceptionHandler getWrapped() {
        return wrapped;
    }

    @Override
    public void handle() {

        Iterator<ExceptionQueuedEvent> iterator =
            getUnhandledExceptionQueuedEvents().iterator();

        while (iterator.hasNext()) {

            ExceptionQueuedEvent event = iterator.next();

            ExceptionQueuedEventContext context =
                (ExceptionQueuedEventContext) event.getSource();

            Throwable exception = context.getException();

            // Log exception here

            System.err.println(
                "Unhandled exception: " + exception
            );

            iterator.remove();
        }

        getWrapped().handle();
    }
}
```
</div>
البته این یک اسکلت آموزشی است. در پروژه واقعی باید موارد زیر را نیز در نظر بگیری:

- ثبت کامل Stack Trace
- مدیریت Exceptionهای مربوط به AJAX
- جلوگیری از افشای اطلاعات حساس
- هدایت کاربر به صفحه مناسب
- حفظ رفتار استاندارد ExceptionHandler برای مواردی که قصد مدیریت آن‌ها را نداری.

سپس یک Factory تعریف می‌کنیم:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
import jakarta.faces.context.ExceptionHandler;
import jakarta.faces.context.ExceptionHandlerFactory;

public class CustomExceptionHandlerFactory
        extends ExceptionHandlerFactory {

    private final ExceptionHandlerFactory parent;

    public CustomExceptionHandlerFactory(
            ExceptionHandlerFactory parent) {

        this.parent = parent;
    }

    @Override
    public ExceptionHandler getExceptionHandler() {

        return new CustomExceptionHandler(
            parent.getExceptionHandler()
        );
    }
}
```
</div>
و در `faces-config.xml` ثبت می‌کنیم:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
<factory>
    <exception-handler-factory>
        com.example.CustomExceptionHandlerFactory
    </exception-handler-factory>
</factory>
```
</div>
**نکته:** در پیاده‌سازی Production نباید همه Exceptionها را بدون بررسی حذف کنی. بعضی Exceptionها باید به Handler اصلی JSF واگذار شوند یا به شکل مناسبی مدیریت شوند.

# **7. مدیریت خطاهای HTTP با web.xml**

بعضی خطاها خارج از چرخه معمول JSF رخ می‌دهند.

مثلاً:

- HTTP 404: صفحه پیدا نشد.
- HTTP 500: خطای داخلی سرور.
- HTTP 403: دسترسی غیرمجاز.

می‌توانیم صفحات اختصاصی برای این خطاها تعریف کنیم.

در `web.xml`:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
<error-page>

    <error-code>404</error-code>

    <location>/error/404.xhtml</location>

</error-page>

<error-page>

    <error-code>500</error-code>

    <location>/error/500.xhtml</location>

</error-page>

<error-page>

    <exception-type>
        java.lang.Exception
    </exception-type>

    <location>/error/general.xhtml</location>

</error-page>
```

مثلاً در `500.xhtml`:

```
<h:body>

    <h1>Internal Server Error</h1>

    <p>
        Something went wrong.
        Please try again later.
    </p>

    <h:link outcome="/index"
            value="Back to Home" />

</h:body>
```
</div>
توجه کن که `ExceptionHandler` مربوط به JSF و Error Pageهای Servlet Container دو مکانیزم متفاوت هستند. نحوه تعامل آن‌ها با توجه به محل وقوع Exception و نوع Request متفاوت است.

# **8. تفاوت روش‌های مدیریت خطا**

| روش                | کاربرد                        |
| ------------------ | ----------------------------- |
| FacesMessage       | نمایش پیام به کاربر           |
| try/catch          | مدیریت Exception در کد        |
| Custom Exception   | خطاهای اختصاصی کسب‌وکار       |
| ExceptionHandler   | مدیریت مرکزی Exceptionهای JSF |
| web.xml error-page | مدیریت خطاهای HTTP و Servlet  |
| Logger             | ثبت خطا برای تیم فنی          |

# **9. معماری پیشنهادی برای یک پروژه Enterprise**

بهترین کار این است که مسئولیت‌ها را از هم جدا کنیم.

**JSF / PrimeFaces**

نمایش فرم، پیام و خطا

**Managed Bean**

مدیریت درخواست و تبدیل Exception به پیام مناسب

**Service Layer**

Business Logic و Business Exception

**Repository / DAO**

Database Access و Persistence Exception

**Centralized Logging**

ثبت Exceptionها و Stack Trace برای تیم فنی

در این معماری:

- Bean مسئول نمایش پیام است.
- Service مسئول قوانین کسب‌وکار است.
- Repository مسئول دسترسی به Database است.
- Logger مسئول ثبت خطاهاست.
- ExceptionHandler مسئول خطاهای مدیریت‌نشده JSF است.

# **10. سؤالات مهم مصاحبه Java EE**

**سؤال اول: تفاوت FacesMessage و Exception چیست؟**

پاسخ: Exception نشان‌دهنده وقوع یک خطا در اجرای برنامه است؛ FacesMessage برای نمایش پیام به کاربر استفاده می‌شود.

**سؤال دوم: آیا ValidationException باعث اجرای Action Method می‌شود؟**

پاسخ: معمولاً خیر. اگر Validation در Lifecycle شکست بخورد، JSF از Update Model Values و Invoke Application عبور نمی‌کند.

**سؤال سوم: آیا ExceptionHandler تمام Exceptionهای برنامه را مدیریت می‌کند؟**

پاسخ: خیر. این Handler در چارچوب Lifecycle و Exception Queue مربوط به JSF کار می‌کند. خطاهای Servlet، فیلترها یا سایر لایه‌ها لزوماً از آن عبور نمی‌کنند.

**سؤال چهارم: تفاوت Application Exception و System Exception چیست؟**

پاسخ:

- Application Exception مربوط به شرایط شناخته‌شده کسب‌وکار است.
- System Exception مربوط به مشکلات فنی و غیرمنتظره است.

**سؤال پنجم: چرا نباید Stack Trace را به کاربر نشان دهیم؟**

پاسخ: چون ممکن است اطلاعات حساس درباره ساختار برنامه، Database، مسیر فایل‌ها و جزئیات امنیتی افشا شود.

# **11. تمرین عملی**

فرض کن یک برنامه مدیریت کاربران با PrimeFaces داری.

کاربر روی دکمه Save کلیک می‌کند و هنگام ذخیره اطلاعات در Database خطای زیر رخ می‌دهد:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
javax.persistence.PersistenceException
```
</div>

**بهترین روش مدیریت این خطا چیست؟**

نمایش مستقیم Stack Trace به کاربر

ثبت کامل خطا در Logger و نمایش پیام عمومی به کاربر

نادیده گرفتن Exception

استفاده از System.out.println به‌تنهایی

بررسی پاسخ

**پیشنهاد برای ادامه مسیر:** موضوع بعدی که ارتباط مستقیمی با Exception Handling در Java EE دارد، مدیریت Transaction با `@Transactional`، تفاوت Checked و Unchecked Exception و تأثیر آن‌ها بر Rollback شدن Transaction است. این موضوع برای کار با JSF، EJB، JPA و Database بسیار مهم است.
</div>



<!-- SOURCE: 6.md -->
# File: 6.md

<div dir="rtl" style="text-align: right; font-family: 'B Nazanin', Tahoma, sans-serif;">


نگهداری یک اپلیکیشن قدیمی **JSF (JavaServer Faces)** معمولاً چالش‌برانگیز است؛ چون در بسیاری از پروژه‌های قدیمی، منطق کسب‌وکار، رابط کاربری، مدیریت State و دسترسی به دیتابیس به‌شدت به یکدیگر وابسته شده‌اند.

اگر مسئول مدرن‌سازی چنین پروژه‌ای باشم، معمولاً آن را یک‌باره بازنویسی نمی‌کنم؛ بلکه با یک برنامه مرحله‌ای و کم‌ریسک جلو می‌روم.

# **۱. مهم‌ترین چالش‌های نگهداری JSF قدیمی**

### **۱. وابستگی به نسخه‌های قدیمی Java و Java EE**

بسیاری از پروژه‌های قدیمی با فناوری‌هایی مانند این‌ها ساخته شده‌اند:

- Java 7 یا Java 8
- Java EE 6 یا Java EE 7
- JSF 2.0 یا 2.2
- PrimeFaces قدیمی
- EJB و JPA قدیمی
- Application Serverهایی مانند GlassFish یا JBoss قدیمی

مشکل این است که کتابخانه‌ها و سرورهای قدیمی ممکن است دیگر پشتیبانی امنیتی دریافت نکنند و با نسخه‌های جدید Java سازگار نباشند.

### **۲. وابستگی شدید به View و Managed Bean**

در بعضی پروژه‌ها، Managed Bean مسئول همه‌چیز است:

- دریافت اطلاعات از فرم
- اعتبارسنجی
- اجرای منطق کسب‌وکار
- ارتباط با دیتابیس
- مدیریت پیام‌ها
- کنترل Navigation

مثلاً:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
@Named
@ViewScoped
public class UserBean implements Serializable {

    @Inject
    private UserRepository repository;

    public void save() {
        // Validation
        // Business Logic
        // Database Access
        // Error Handling
        repository.save(user);
    }
}
```
</div>
این طراحی باعث می‌شود تست‌کردن، تغییر و توسعه سیستم دشوار شود.

**راه‌حل:** انتقال منطق کسب‌وکار به Service Layer و نگه‌داشتن Managed Bean به‌عنوان لایه ارتباطی بین View و Service.

### **۳. مشکلات State Management**

JSF یک Framework مبتنی بر Component و State است.

در پروژه‌های قدیمی ممکن است مشکلات زیر را ببینیم:

- View State بزرگ
- مصرف زیاد حافظه Session
- مشکلات Serialization در `@ViewScoped`
- تداخل اطلاعات بین Tabهای مرورگر
- مشکلات مربوط به Session Expiration

برای مثال، استفاده نادرست از `@SessionScoped` برای اطلاعاتی که باید فقط در یک صفحه نگهداری شوند، می‌تواند باعث تداخل داده‌ها در Tabهای مختلف شود.

### **۴. وابستگی شدید به PrimeFaces**

در بعضی پروژه‌ها، منطق رابط کاربری به نسخه خاصی از PrimeFaces وابسته است.

برای مثال:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
<p:dataTable value="#{userBean.users}" var="user">
    <p:column headerText="Name">
        #{user.name}
    </p:column>
</p:dataTable>
```
</div>

با ارتقای نسخه PrimeFaces ممکن است:

- بعضی Attributeها تغییر کنند.
- بعضی Componentها حذف یا جایگزین شوند.
- رفتار AJAX متفاوت شود.
- CSS و JavaScript سفارشی دچار مشکل شوند.

### **۵. تست‌پذیری پایین**

در پروژه‌های قدیمی ممکن است منطق برنامه مستقیماً داخل Beanها نوشته شده باشد.

در این حالت تست‌کردن منطق کسب‌وکار بدون راه‌اندازی JSF یا Application Server دشوار می‌شود.

**راه‌حل:** جداسازی لایه‌ها و نوشتن Unit Test برای Serviceها.

### **۶. مشکلات امنیتی و وابستگی‌های آسیب‌پذیر**

باید موارد زیر بررسی شوند:

- نسخه Java و Application Server
- کتابخانه‌های قدیمی و آسیب‌پذیر
- نحوه مدیریت Authentication و Authorization
- CSRF و XSS
- اعتبارسنجی ورودی‌ها
- نحوه مدیریت Session و Cookie

# **۲. چگونه یک پروژه JSF قدیمی را مدرن‌سازی می‌کنم؟**

من این کار را در چند مرحله انجام می‌دهم.

## **نقشه راه مدرن‌سازی**

1. **بررسی و ارزیابی پروژه**

   شناسایی نسخه Java، JSF، PrimeFaces، Application Server، کتابخانه‌ها و وابستگی‌ها.
2. **بهبود معماری**

   جداسازی View، Controller/Bean، Service و Repository.
3. **ارتقای زیرساخت**

   ارتقای Java، Jakarta EE و کتابخانه‌ها با بررسی سازگاری.
4. **افزایش تست‌پذیری**

   نوشتن Unit Test، Integration Test و تست‌های رابط کاربری.
5. **بهبود امنیت و کارایی**

   بررسی آسیب‌پذیری‌ها، مدیریت Session، Queryها و Performance.
6. **مدرن‌سازی رابط کاربری و API**

   در صورت نیاز، ایجاد REST API و جداکردن Frontend از Backend.

## **مرحله اول: بررسی نسخه‌ها و سازگاری**

ابتدا وضعیت فعلی پروژه را مشخص می‌کنم.

برای مثال:

| فناوری             | نسخه فعلی  | هدف احتمالی                |
| ------------------ | ---------- | -------------------------- |
| Java               | Java 8     | Java 17 یا 21              |
| JSF                | JSF 2.x    | Jakarta Faces 4.x          |
| PrimeFaces         | نسخه قدیمی | نسخه سازگار و پشتیبانی‌شده |
| Java EE            | Java EE 7  | Jakarta EE 10 یا بالاتر    |
| Persistence        | JPA 2.x    | Jakarta Persistence        |
| Application Server | قدیمی      | نسخه جدید و سازگار         |

این جدول فقط نمونه است؛ انتخاب نسخه نهایی باید براساس پشتیبانی سرور، کتابخانه‌ها و نیاز پروژه باشد.

نکته مهم: مهاجرت از Java EE به Jakarta EE فقط تغییر شماره نسخه نیست. در نسخه‌های جدید، بسیاری از Packageها از `javax.*` به `jakarta.*` تغییر کرده‌اند.

مثلاً:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
// Old Java EE
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.persistence.Entity;

// New Jakarta EE
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import jakarta.persistence.Entity;
```
</div>
این تغییر می‌تواند روی کل پروژه و کتابخانه‌های وابسته تأثیر بگذارد.

## **مرحله دوم: جداسازی لایه‌ها**

یکی از مهم‌ترین کارها خارج‌کردن Business Logic از Managed Bean است.

معماری پیشنهادی:

**JSF / PrimeFaces**

XHTML و رابط کاربری

**View Bean**

مدیریت تعاملات صفحه

**Service Layer**

Business Logic و Transaction

**Repository / DAO**

دسترسی به دیتابیس با JPA

**Database**

مثلاً Service را به شکل زیر می‌نویسم:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.inject.Inject;

@ApplicationScoped
public class UserService {

    @Inject
    private UserRepository repository;

    @Transactional
    public void createUser(User user) {

        if (repository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException(
                "Email already exists"
            );
        }

        repository.save(user);
    }
}
```
</div>
حالا Managed Bean فقط وظیفه ارتباط با UI را دارد:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
@Named
@ViewScoped
public class UserBean implements Serializable {

    @Inject
    private UserService userService;

    private User user = new User();

    public void save() {
        try {
            userService.createUser(user);

            FacesContext.getCurrentInstance()
                .addMessage(null,
                    new FacesMessage("User saved successfully"));

        } catch (IllegalArgumentException e) {

            FacesContext.getCurrentInstance()
                .addMessage(null,
                    new FacesMessage(
                        FacesMessage.SEVERITY_ERROR,
                        e.getMessage(),
                        null
                    ));
        }
    }

    public User getUser() {
        return user;
    }
}
```
</div>
با این معماری می‌توانیم `UserService` را بدون اجرای JSF تست کنیم.

## **مرحله سوم: استفاده از قابلیت‌های جدید Java**

اگر پروژه هنوز از Java قدیمی استفاده می‌کند، به‌تدریج قابلیت‌های جدید را وارد می‌کنم.

مثلاً:

- استفاده از `LocalDate` و `LocalDateTime` به‌جای API قدیمی Date
- استفاده از Stream API
- استفاده از `Optional` در موارد مناسب
- استفاده از `Records` برای DTOها در نسخه‌های مناسب Java
- استفاده از قابلیت‌های جدید زبان برای خوانایی بهتر

مثال DTO:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
public record UserDTO(
    Long id,
    String name,
    String email
) {}
```
</div>
البته Record را نباید بدون بررسی نیازهای JPA به‌عنوان Entity استفاده کرد.

## **مرحله چهارم: جداسازی Frontend از Backend**

اگر قرار باشد سیستم در آینده توسعه زیادی داشته باشد، ممکن است JSF دیگر بهترین انتخاب برای رابط کاربری نباشد.

در این حالت می‌توانم معماری را به سمت REST API ببرم:

**React**

Frontend

**Angular**

Frontend

**REST API**

Jakarta REST / JAX-RS

**Service Layer**

**Repository + Database**

برای مثال، به‌جای اینکه React مستقیماً با Managed Bean ارتباط داشته باشد، درخواست HTTP به REST API ارسال می‌کند:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


```
@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {

    @Inject
    private UserService userService;

    @POST
    public Response createUser(UserDTO dto) {

        userService.createUser(dto);

        return Response.status(201).build();
    }
}
```
</div>
مزیت این معماری این است که Backend دیگر به JSF وابسته نیست و می‌توان چند Frontend مختلف داشت.

اما نکته مهم: **من تمام پروژه را صرفاً به دلیل قدیمی‌بودن JSF به React یا Angular تبدیل نمی‌کنم.** اگر JSF نیازهای سیستم را برآورده می‌کند، ارتقای JSF و بهبود معماری می‌تواند اقتصادی‌تر و کم‌ریسک‌تر باشد.

# **۳. آیا باید کل پروژه را بازنویسی کرد؟**

معمولاً خیر.

سه رویکرد داریم:

| رویکرد      | توضیح                                  | ریسک  |
| ----------- | -------------------------------------- | ----- |
| Upgrade     | ارتقای نسخه‌ها بدون تغییر اساسی معماری | متوسط |
| Refactoring | اصلاح تدریجی معماری و کد               | متوسط |
| Rewrite     | بازنویسی کامل با فناوری جدید           | زیاد  |

من معمولاً روش سوم را آخرین گزینه می‌دانم.

یک راهکار مناسب، الگوی **Strangler Pattern** است: بخش‌های جدید را به‌صورت سرویس یا ماژول مستقل می‌سازیم و به‌تدریج قابلیت‌های قدیمی را جایگزین می‌کنیم.

مثلاً:

- ابتدا ماژول گزارش‌گیری را به REST API تبدیل می‌کنیم.
- سپس ماژول مدیریت کاربران را جدا می‌کنیم.
- بعد ماژول سفارش‌ها را مهاجرت می‌دهیم.
- در نهایت، بخش‌های باقی‌مانده JSF را حذف می‌کنیم؛ اگر واقعاً نیازی به آن‌ها نباشد.

# **۴. جمع‌بندی برای مصاحبه شغلی**

اگر در مصاحبه بپرسند:

**How would you modernize a legacy JSF application?**

می‌توانی این‌طور پاسخ بدهی:
<div dir="ltr" style="text-align: left; font-family: 'B Nazanin', Tahoma, sans-serif;">


> First, I would analyze the existing application, including Java, JSF, PrimeFaces, and application server versions.
>
> Then, I would gradually improve the architecture by separating the presentation layer from business logic and data access.
>
> I would upgrade Java and Jakarta EE dependencies carefully, improve test coverage, review security issues, and optimize performance.
>
> If the application requires a modern frontend, I would gradually introduce REST APIs and migrate frontend modules to React or Angular.
>
> I would avoid a complete rewrite unless there is a strong business or technical reason, because incremental modernization reduces risk and business disruption.
</div>

# **۵. سؤال تمرینی مصاحبه**

فرض کن یک پروژه JSF داری که:

- از Java 8 و JSF 2.2 استفاده می‌کند.
- تمام Business Logic داخل Managed Bean نوشته شده است.
- از PrimeFaces قدیمی استفاده می‌کند.
- تست واحد ندارد.
- ۱۰ سال است که در محیط Production کار می‌کند.

**اولین سه اقدامی که برای مدرن‌سازی این پروژه انجام می‌دهی چیست و چرا؟**

نکته کلیدی این است که در مصاحبه فقط نام فناوری‌های جدید را نیاوری؛ باید نشان بدهی می‌توانی بدون ایجاد اختلال در سیستم Production، یک پروژه قدیمی را مرحله‌به‌مرحله مدرن کنی.
</div>



<!-- SOURCE: 1.md -->
# File: 1.md

Learning JSF (JavaServer Faces) with CDI (Contexts and Dependency Injection) is a great path for building modern Java web applications. CDI has become the standard way to manage backing beans, making your code cleaner and more powerful. Here is a step-by-step guide to get you started.

### 🛠️ Step 1: Set Up Your Environment

You'll need a few tools to begin:
*   **JDK** (Java Development Kit), version 11 or higher.
*   **An IDE** like Apache NetBeans or Eclipse, which have excellent built-in support for JSF and CDI.
*   **A Java EE / Jakarta EE application server** such as GlassFish, WildFly, or Open Liberty. These servers provide the runtime environment for your JSF/CDI application.

### 📁 Step 2: Create a Web Project with CDI Support

Your first goal is to create a project that's configured for both JSF and CDI.

*   In your IDE, create a new **Java Web Application** project.
*   When prompted, select a **Java EE version** (like Java EE 7 or 8) that includes CDI support. Ensure the **"Enable Contexts and Dependency Injection"** option is selected.
*   This step is crucial because it automatically generates a **`beans.xml`** file in your `WEB-INF` directory. This file is a marker that tells the application server to activate CDI in your project.

### 🧠 Step 3: Create a CDI Managed Bean

Instead of the old `@ManagedBean` annotation, you now create a simple Java class and use CDI annotations to make it a managed bean.

Create a class (e.g., `UserBean.java`) and annotate it:

```java
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;

@Named // Makes the bean accessible from JSF pages via Expression Language (EL)
@RequestScoped // Defines the bean's lifecycle (lives for a single HTTP request)
public class UserBean {
    private String name;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getGreeting() {
        return "Hello, " + name + "!";
    }
}
```
*   **`@Named`**: Exposes the bean to JSF, allowing you to use it in your pages with an EL expression like `#{userBean}`.
*   **`@RequestScoped`**: This is a CDI scope annotation. It tells the container to keep the bean instance alive only for the duration of a single HTTP request.

### 🎨 Step 4: Create a JSF Page (Facelets)

Now, create an `.xhtml` file (e.g., `index.xhtml`) in your `webapp` folder to build the user interface.

```html
<?xml version='1.0' encoding='UTF-8' ?>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:h="http://xmlns.jcp.org/jsf/html">
<h:head>
    <title>JSF CDI Demo</title>
</h:head>
<h:body>
    <h:form>
        <p>
            <h:outputLabel value="Enter your name: " for="nameInput" />
            <h:inputText id="nameInput" value="#{userBean.name}" />
        </p>
        <h:commandButton value="Greet Me" action="#{userBean.getGreeting}" />
        <p>
            <h:outputText value="#{userBean.greeting}" />
        </p>
    </h:form>
</h:body>
</html>
```
*   The `value="#{userBean.name}"` attribute binds the input field to the `name` property of your `UserBean`.
*   The `action` attribute on the button triggers the `getGreeting()` method. The result is then displayed by the `h:outputText` component.

### 🔗 Step 5: Understand Dependency Injection (`@Inject`)

CDI's real power comes from injecting one bean into another. This decouples your code and makes it easier to manage.

Imagine you have a `GreetingService` bean:
```java
@ApplicationScoped
public class GreetingService {
    public String createGreeting(String name) {
        return "Hello, " + name + "!";
    }
}
```
You can inject this service into your `UserBean` instead of creating it manually:
```java
import jakarta.inject.Inject;
// ... other imports

@Named
@RequestScoped
public class UserBean {
    @Inject
    private GreetingService greetingService; // CDI injects the dependency

    private String name;

    public String getGreeting() {
        return greetingService.createGreeting(name);
    }
    // ... getters and setters
}
```
The `@Inject` annotation tells the CDI container to provide an instance of `GreetingService` when `UserBean` is created.

### 🌐 Step 6: Explore CDI Bean Scopes

Choosing the right scope is important for managing state. Here are the main CDI scopes you'll use:

| Scope Annotation | Description |
| :--- | :--- |
| **`@RequestScoped`** | The bean lives for a single HTTP request. Good for simple form submissions. |
| **`@SessionScoped`** | The bean lives for an entire user session (across multiple requests). Ideal for user login information. |
| **`@ApplicationScoped`** | A single bean instance is shared across the entire application. Good for services or global configuration. |
| **`@ConversationScoped`** | A longer-running scope that spans multiple requests but is shorter than a session. Useful for multi-step wizards or shopping carts. |
| **`@Dependent`** | The default scope. The bean's lifecycle is tied to the bean it's injected into. |

### ▶️ Step 7: Run Your Application

To see your work, deploy the project to your application server. You can usually do this by right-clicking the project in your IDE and selecting **"Run"** or **"Deploy"**. Once deployed, open your browser and navigate to the application's URL (e.g., `http://localhost:8080/your-app-name/index.xhtml`).

### 💡 A Note on Modern JSF (Jakarta Faces)

It's worth knowing that JSF is now part of **Jakarta EE** and is officially called **Jakarta Faces**. The annotations have moved from the `javax.*` package to `jakarta.*` (e.g., `jakarta.inject.Named`). If you start a new project, use the `jakarta` namespace, as it's the standard for modern enterprise Java.

### 💎 Summary

You've now seen the core workflow: create a CDI bean, expose it with `@Named`, give it a scope, build a Facelets page to interact with it, and use `@Inject` to wire beans together. This foundation will let you build progressively more complex and powerful web applications.

If you get stuck on any of these steps, feel free to ask for more details.



<!-- SOURCE: 2.md -->
# File: 2.md

You've got the basics of `@Named` and `@Inject` down. The next level is about making your application **robust and responsive**. Here are the most impactful tips to learn next, covering AJAX, validation, navigation, and the scopes that tie them all together.

### 🚀 Tip 1: Use `<f:ajax>` for Responsive UI Updates

Instead of full-page reloads, use AJAX to update only parts of the page. This is essential for keeping the UI snappy.

*   **How it works**: Nest `<f:ajax>` inside a component. It turns that component's default event (like a button click) into a partial postback.
*   **Key attributes**:
    *   **`execute`**: Specifies which components' values are sent to the server (e.g., `execute="@form"`).
    *   **`render`**: Specifies which components are refreshed in the browser after the server responds.
*   **Best Practice**: AJAX works best with **`@ViewScoped`** beans because the same bean instance survives across multiple partial requests, preserving your page state.
*   **Crucial Pitfall**: When using `render`, target a component that **always exists** on the page (like a wrapper `<div>`), not the conditionally rendered component itself. If the component doesn't exist in the DOM, the AJAX update will fail.

### ✅ Tip 2: Layer Your Validation for Clear Feedback

JSF applies validation in a specific order. Understanding this helps you place rules correctly.

1.  **`required="true"`**: Checked first, before any conversion.
2.  **Faces Validators** (e.g., `<f:validateLength>`): Standard component-level rules.
3.  **Bean Validation** (JSR 303/349): Annotate your bean properties with `@NotNull`, `@Size`, `@Pattern`, etc. JSF integrates this seamlessly, keeping your rules in one place shared with other layers like JPA or REST.

**Debugging Pro-Tip**: Add `<h:messages globalOnly="false" />` to your page during development. It makes conversion and validation failures visible, which are otherwise easy to miss.

### 🧭 Tip 3: Master Navigation and the Post-Redirect-Get Pattern

Avoid the "Confirm Form Resubmission" browser warning by using the **Post-Redirect-Get (PRG)** pattern.

*   **The Trick**: In your action method, return the outcome with `?faces-redirect=true`.
    ```java
    public String save() {
        // ... save logic ...
        return "success?faces-redirect=true";
    }
    ```
*   **For Bookmarkable Pages**: Use `<f:viewParam>` to read URL parameters and `<f:viewAction>` to run initialization logic. This is the JSF equivalent of a GET controller method.

### 🧠 Tip 4: Choose Scopes Deliberately

Improper scoping is a leading source of bugs. Use this cheat sheet to guide your choice:

| Scope | Lives For | Typical Use | Major Caveat |
| :--- | :--- | :--- | :--- |
| **`@RequestScoped`** | One HTTP request | Stateless pages, simple forms without AJAX | Values are lost between postbacks. |
| **`@ViewScoped`** | One view, across AJAX postbacks | **Most pages with forms, tables, and AJAX** | **Must implement `Serializable`**. |
| **`@SessionScoped`** | The entire user session | Logged-in user, preferences | Never store per-page form data here; it grows memory. |
| **`@ApplicationScoped`** | The entire application | Reference data, caches | Must be thread-safe. |

**Critical Rule**: Any `@ViewScoped` or `@SessionScoped` bean **must be `Serializable`**, because the container may passivate or replicate it. Mark non-serializable fields as `transient`.

### ⚠️ Tip 5: Avoid These Common Pitfalls

These are the mistakes that cause hours of debugging:

*   **Business Logic in the Bean**: Keep backing beans "thin." They should handle binding, validation, and navigation. Move heavy logic, transactions, and persistence to a service layer (`@Inject` a service). This makes testing far easier.
*   **Work in Getters**: A getter can be called multiple times per request. Never put database calls or expensive logic inside a getter; it will kill performance.
*   **Command Component Outside a Form**: A `<h:commandButton>` outside an `<h:form>` will do nothing. JSF often warns you during development, but it's a common oversight.
*   **Forgetting `Serializable`**: As mentioned above, a non-serializable `@ViewScoped` bean works on a single node but fails mysteriously under clustering or passivation.

### 💎 A Useful Next Step: Explore OmniFaces

Once you're comfortable with the core, look at **OmniFaces**. It's a utility library that fills common gaps in JSF, offering powerful components and utilities that make development easier and help you avoid reinventing the wheel.

Would you like to dive deeper into any of these areas, such as setting up AJAX with a specific component library like PrimeFaces, or exploring how to integrate a service layer with JPA?



<!-- SOURCE: 3.md -->
# File: 3.md

JSF (now Jakarta Faces) offers a rich set of components, and they're generally grouped into a few key categories. Since you mentioned **composite components**, I'll make sure to cover that in detail, but here is a complete overview of all the major component types you'll encounter.

### 🧱 The Standard HTML Component Library (`h:`)

This is your fundamental toolkit. These tags map directly to standard HTML elements but come with the added power of being server-side JSF components that can be bound to your backing beans.

| Component Tag | Function | Rendered As |
| :--- | :--- | :--- |
| **`h:form`** | Represents an input form; its children submit data together. | `<form>` |
| **`h:inputText`** | A single-line text input field. | `<input type="text">` |
| **`h:inputSecret`** | A password field (characters are masked). | `<input type="password">` |
| **`h:inputTextarea`** | A multi-line text input field. | `<textarea>` |
| **`h:inputHidden`** | A hidden field to pass data not visible to the user. | `<input type="hidden">` |
| **`h:inputFile`** | A file upload field. | `<input type="file">` |
| **`h:commandButton`** | Submits a form to the application. | `<input type="submit">` |
| **`h:commandLink`** | An anchor link that submits a form (creates an action event). | `<a>` |
| **`h:outputText`** | Displays a line of plain text. | Text |
| **`h:outputLabel`** | Displays a label for an input field; helps with accessibility. | `<label>` |
| **`h:outputLink`** | A standard link that does *not* submit a form or trigger server-side actions. | `<a>` |
| **`h:graphicImage`** | Displays an image. | `<img>` |
| **`h:message`** | Displays a single validation/conversion message for a specific component. | `<span>` |
| **`h:messages`** | Displays all messages for the page (great for global feedback). | `<span>` (list) |
| **`h:dataTable`** | Renders a dynamic HTML table by iterating over a collection of data. | `<table>` |
| **`h:column`** | Represents a single column within an `h:dataTable`. | A column in a table |
| **`h:panelGrid`** | Renders a table for layout purposes, arranging components in a grid. | `<table>` |
| **`h:panelGroup`** | Groups a set of components under a single parent (useful for AJAX rendering). | `<div>` or `<span>` |
| **`h:selectBooleanCheckbox`** | A single checkbox for a boolean value. | `<input type="checkbox">` |
| **`h:selectManyCheckbox`** | A group of checkboxes allowing multiple selections. | Multiple `<input type="checkbox">` |
| **`h:selectOneRadio`** | A group of radio buttons allowing a single selection. | Multiple `<input type="radio">` |
| **`h:selectOneMenu`** | A dropdown menu for single selection. | `<select>` |
| **`h:selectManyMenu`** | A menu allowing multiple selections. | `<select multiple>` |
| **`h:selectOneListbox`** | A list box for single selection (items displayed at once). | `<select size="...">` |
| **`h:selectManyListbox`** | A list box allowing multiple selections. | `<select multiple size="...">` |

You can find the full list of specialized classes for these in the Jakarta Faces API documentation.

### ⚙️ The Core Tag Library (`f:`)

These tags don't render visual UI themselves. Instead, they add powerful behaviors to your components, such as AJAX, validation, conversion, and event handling.

*   **AJAX**: **`f:ajax`** associates AJAX behavior with one or more components, enabling partial page updates without a full reload.
*   **Validation**: Tags like **`f:validateLength`**, **`f:validateLongRange`**, and **`f:validateRegex`** attach server-side validation rules to input components.
*   **Conversion**: Tags like **`f:convertDateTime`** and **`f:convertNumber`** automatically convert user input between strings and Java objects (like `Date` or `BigDecimal`).
*   **Lifecycle & Events**: Tags like **`f:phaseListener`** and **`f:event`** allow you to hook into the JSF request processing lifecycle.
*   **Metadata**: **`f:metadata`** is used as a parent for **`f:viewParam`** (to read URL parameters) and **`f:viewAction`** (to run initialization logic), which is key for bookmarkable pages.

### 🎨 Facelets Templating Tags (`ui:`)

These are for creating reusable layouts and avoiding code duplication. They are the foundation of a maintainable JSF view layer.

*   **`ui:composition`**: Declares that a page is using a template. Its children define the content that will fill the template's slots.
*   **`ui:insert`**: Used *inside a template file* to define a placeholder where content will be inserted.
*   **`ui:define`**: Used *inside a client page* (that uses a template) to provide content for a corresponding `ui:insert` slot in the template.
*   **`ui:include`**: Statically includes another Facelets page at translation time. Useful for reusable fragments like headers or footers.
*   **`ui:decorate`**: Similar to `ui:composition`, but it allows you to decorate an existing template with additional content, rather than completely filling its slots.

### 🧩 Composite Components (`cc:` or custom namespace)

This is the direct answer to your question. **Composite components** are the JSF way to create your own reusable, custom UI widgets from existing components. They are more powerful than simple includes because they are true JSF components with a defined contract.

**How to create one:**
1.  **Create a Facelets file** inside a special folder structure: `resources/yourLibraryName/yourComponentName.xhtml`.
2.  **Define the contract** inside this file using **`<cc:interface>`**. This is where you declare what attributes (`<cc:attribute>`) the user of your component can pass in.
3.  **Define the implementation** inside **`<cc:implementation>`**. This is where you place the actual JSF components that make up your custom widget.

**Example: A custom "Greeting Box" component**

```xhtml
<!-- File: resources/mycomp/greetingBox.xhtml -->
<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:h="http://xmlns.jcp.org/jsf/html"
      xmlns:cc="http://xmlns.jcp.org/jsf/composite">
    <cc:interface>
        <cc:attribute name="userName" required="true" />
    </cc:interface>
    <cc:implementation>
        <div class="greeting-box">
            <h:outputText value="Hello, #{cc.attrs.userName}!" />
        </div>
    </cc:implementation>
</html>
```
**How to use it in another page:**
You first declare the namespace for your library (`xmlns:my="http://xmlns.jcp.org/jsf/composite/mycomp"`) and then use it like any other tag:
```xhtml
<my:greetingBox userName="#{userBean.name}" />
```
This approach lets you build a library of your own high-level components, making your page code much cleaner and more semantic.

### 🚀 Third-Party Component Libraries

The standard JSF library is capable but basic. For rich, modern UI widgets, developers almost always use a third-party library. The most popular is **PrimeFaces**, which offers a massive suite of components like **`p:dataTable`** (with sorting, filtering, pagination), **`p:accordionPanel`**, **`p:tabView`**, **`p:dialog`**, **`p:chart`**, and many more.

### 💡 A Modern Touch: HTML5-Friendly Markup

Since JSF 2.2, you can also use plain HTML5 elements (like `<div>`, `<input>`) and make them behave as JSF components using **pass-through elements** and **pass-through attributes**. This is useful for leveraging modern HTML5 features while still maintaining JSF's server-side power.

Would you like to see a more detailed example of building a composite component, or perhaps explore the rich components offered by a library like PrimeFaces?



<!-- SOURCE: 4.md -->
# File: 4.md

# 📝 Expression Language (EL) in JSF — Complete Guide

EL is the glue between your JSF pages (`.xhtml`) and your CDI beans. It's what makes `#{userBean.name}` work. Mastering EL is essential for writing clean, dynamic views. Here's everything you need to know.

---

### 🔤 Step 1: The Two Syntaxes — `#{}` vs `${}`

JSF primarily uses **deferred evaluation** with `#{}`, but you should understand both.

| Syntax | Name | Evaluation Time | Use In |
| :--- | :--- | :--- | :--- |
| **`#{...}`** | Deferred Expression | Evaluated when the page is **rendered** or the **action fires** (depends on context) | **Always use this in JSF** |
| **`${...}`** | Immediate Expression | Evaluated **immediately** when the page is built | Rarely used in JSF; common in JSP |

**Rule of thumb**: In JSF, **always use `#{}`**. It works correctly with components that need to evaluate values at different lifecycle phases (like input fields, command buttons, and validators).

---

### 🎯 Step 2: The Value Expression — Binding to Beans

This is the most common use of EL. It reads (and often writes) a property on a bean.

```xhtml
<h:inputText value="#{userBean.name}" />
<h:outputText value="#{userBean.greeting}" />
```

**How it works:**
*   `#{userBean.name}` → JSF finds the bean named `userBean` (via `@Named`), then calls **`getName()`** (the getter).
*   When the form is submitted, JSF calls **`setName(...)`** (the setter) to write the value back.
*   **The property must have a getter** (for reading) and a setter (for writing). Read-only is fine for output.

**Resolving the bean name:**
*   With CDI `@Named`, the default bean name is the **class name with the first letter lowercased** (`UserBean` → `userBean`).
*   You can override it: `@Named("user")` → `#{user.name}`.

---

### ⚙️ Step 3: Method Expressions — Calling Methods

EL can also invoke methods, not just read properties.

**Action methods** (return a navigation outcome):
```xhtml
<h:commandButton value="Save" action="#{userBean.save}" />
```

**Action listener methods** (take an `ActionEvent`, return `void`):
```xhtml
<h:commandButton value="Click" actionListener="#{userBean.handleClick}" />
```

**Value change listeners**:
```xhtml
<h:inputText value="#{userBean.name}" valueChangeListener="#{userBean.nameChanged}" />
```

**Calling a method with parameters** (EL 2.2+, so available in all modern JSF):
```xhtml
<h:commandButton value="Delete" action="#{userBean.delete(item.id)}" />
```

> 💡 **Subtle but important difference**: `action="#{bean.method}"` **without parentheses** is a *method expression* — JSF decides when to invoke it. `action="#{bean.method()}"` **with parentheses** invokes it immediately as a *value expression* and uses the return value. For `action` and `actionListener`, **omit the parentheses**.

---

### 🔍 Step 4: Accessing Nested Properties and Collections

EL navigates object graphs automatically, thanks to the JavaBeans conventions.

```xhtml
<!-- Nested bean properties -->
#{orderBean.customer.address.city}

<!-- Map access (by key) -->
#{configBean.settings['app.name']}
#{configBean.settings.appName}   <!-- dot notation works too -->

<!-- List / array access (by index) -->
#{cartBean.items[0].name}

<!-- Inside a dataTable, the var is in EL scope -->
<h:dataTable value="#{userBean.users}" var="user">
    <h:column>#{user.name}</h:column>
</h:dataTable>
```

**How navigation works**: For `#{a.b.c}`, EL calls `getA()`, then `getB()` on the result, then `getC()`. If any step returns `null`, the final result is `null` (no `NullPointerException` — EL is null-safe for reads).

---

### 🧠 Step 5: Implicit EL Objects

JSF and CDI give you built-in objects you can reference without declaring anything.

| Implicit Object | What It Gives You |
| :--- | :--- |
| **`#{param.name}`** | Request parameter (like `request.getParameter`) |
| **`#{paramValues.name}`** | `String[]` of request parameter values |
| **`#{header['User-Agent']}`** | Request header |
| **`#{cookie.name}`** | Cookie object |
| **`#{request}`** | The `HttpServletRequest` |
| **`#{session}`** | The `HttpSession` |
| **`#{application}`** | The `ServletContext` |
| **`#{facesContext}`** | The current `FacesContext` |
| **`#{view}`** | The current `UIViewRoot` |
| **`#{flash}`** | The flash scope map (survives one redirect) |
| **`#{cc}`** | The composite component itself (inside `cc:implementation`) |
| **`#{cc.attrs.name}`** | An attribute passed to a composite component |
| **`#{request.contextPath}`** | App context path (useful for building URLs) |

**Example — building a link with context path:**
```xhtml
<h:outputLink value="#{request.contextPath}/resources/images/logo.png">Logo</h:outputLink>
```

---

### 🎨 Step 6: EL Operators — Logic in the View

EL supports a rich set of operators so you can do simple conditional logic directly in the page. **Use sparingly** — heavy logic belongs in the bean.

| Category | Operators |
| :--- | :--- |
| **Arithmetic** | `+`, `-`, `*`, `/` (or `div`), `%` (or `mod`) |
| **Relational** | `==` (or `eq`), `!=` (or `ne`), `<` (or `lt`), `>` (or `gt`), `<=` (or `le`), `>=` (or `ge`) |
| **Logical** | `&&` (or `and`), `\|\|` (or `or`), `!` (or `not`) |
| **Empty check** | `empty` — true if null, empty string, empty collection, or empty map |
| **Ternary** | `condition ? valueIfTrue : valueIfFalse` |
| **Null-safe access** | `?.` and `?:` (EL 3.0+) |

**Examples:**
```xhtml
<!-- Ternary -->
<h:outputText value="#{userBean.loggedIn ? 'Welcome back!' : 'Please log in'}" />

<!-- Empty check -->
<h:outputText value="No items" rendered="#{empty cartBean.items}" />

<!-- Null-safe navigation (EL 3.0) -->
<h:outputText value="#{orderBean.customer?.name}" />
<!-- If customer is null, returns null instead of throwing NPE -->

<!-- Elvis operator (EL 3.0) -->
<h:outputText value="#{userBean.nickname != null ? userBean.nickname : 'Guest'}" />
<!-- Shorthand: -->
<h:outputText value="#{userBean.nickname ?: 'Guest'}" />
```

---

### 🧮 Step 7: EL Functions (`fn:`)

The **Function tag library** provides useful string/collection utilities.

Declare the namespace:
```xhtml
xmlns:fn="http://xmlns.jcp.org/jsf/function"
```

| Function | Purpose |
| :--- | :--- |
| **`fn:length(list)`** | Length of a string, array, or collection |
| **`fn:contains(str, sub)`** | Does string contain substring? |
| **`fn:startsWith(str, prefix)`** | Prefix check |
| **`fn:endsWith(str, suffix)`** | Suffix check |
| **`fn:toUpperCase(str)`** | Uppercase |
| **`fn:toLowerCase(str)`** | Lowercase |
| **`fn:trim(str)`** | Trim whitespace |
| **`fn:escapeXml(str)`** | Escape XML/HTML special characters |
| **`fn:join(array, sep)`** | Join array into a string |
| **`fn:split(str, sep)`** | Split string into an array |
| **`fn:substring(str, start, end)`** | Substring |
| **`fn:replace(str, old, new)`** | String replace |

**Examples:**
```xhtml
<h:outputText value="#{fn:length(userBean.users)} users" />
<h:outputText value="#{fn:toUpperCase(userBean.name)}" />

<!-- Conditional rendering based on a string -->
<h:outputText value="Admin section" rendered="#{fn:contains(userBean.roles, 'ADMIN')}" />

<!-- Formatting a count -->
<h:outputText value="You have #{fn:length(cartBean.items)} item(s) in your cart." />
```

---

### 🎯 Step 8: EL in Different Contexts

EL is used not only in component attributes, but also in other places:

**1. In component attributes (most common):**
```xhtml
<h:inputText value="#{userBean.name}" rendered="#{userBean.editMode}" />
```

**2. In `<h:outputText>` with `escape`:**
```xhtml
<h:outputText value="#{userBean.bio}" escape="false" />  <!-- allows HTML -->
```

**3. In CSS class names (dynamic styling):**
```xhtml
<div class="alert #{userBean.hasErrors ? 'alert-error' : 'alert-success'}">
    ...
</div>
```

**4. In `<f:viewParam>` to read URL parameters:**
```xhtml
<f:metadata>
    <f:viewParam name="id" value="#{userBean.userId}" />
    <f:viewAction action="#{userBean.loadUser}" />
</f:metadata>
```

**5. In `<f:ajax>` execute/render:**
```xhtml
<f:ajax execute="@this" render="resultPanel" />
<!-- Can also use EL to dynamically pick targets: -->
<f:ajax execute="@this" render="#{userBean.ajaxTarget}" />
```

**6. In `<ui:param>` to pass values to templates/includes:**
```xhtml
<ui:include src="/WEB-INF/fragments/header.xhtml">
    <ui:param name="pageTitle" value="#{userBean.name} Profile" />
</ui:include>
```

**7. In composite component attributes (`cc:attribute`):**
```xhtml
<cc:attribute name="label" default="#{cc.id}" />
```

---

### ⚠️ Step 9: Common EL Pitfalls

These trip up almost everyone at some point:

*   **No setter = silent failure**: If you bind `h:inputText` to a property with only a getter, the submitted value is silently discarded. Always provide a setter for input fields.
*   **Using `${}` in JSF**: Use `#{}` exclusively in JSF. `${}` may behave unexpectedly because it evaluates immediately.
*   **Parentheses on action methods**: `action="#{bean.save}"` ✅, `action="#{bean.save()}"` ❌ (unless you want the return value used as a string, which is rarely the case).
*   **EL is case-sensitive**: `#{userBean.Name}` will not find `getName()` — bean properties must follow the `getX`/`setX` convention with proper casing.
*   **Null-safe reads, but not writes**: `#{a.b.c}` on a null `b` returns `null` safely. But `#{a.b.c = value}` on a null `b` will throw — you can't navigate into nulls to write.
*   **EL scope confusion**: `#{var}` inside a `dataTable`'s `var="item"` refers to the current row. Outside the table, it's gone.
*   **Don't do heavy work in EL**: Something like `#{userBean.loadFromDatabase()}` in a rendered attribute runs on *every* render. Keep EL expressions cheap.

---

### 💎 Step 10: A Practical Combined Example

Here's a small page tying together everything above:

```xhtml
<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:h="http://xmlns.jcp.org/jsf/html"
      xmlns:fn="http://xmlns.jcp.org/jsf/function">

<h:body>
    <!-- Guard: only show form if logged in -->
    <h:panelGroup rendered="#{!empty session.getAttribute('user')}">
        <h:form>
            <h:outputLabel value="Name:" for="name" />
            <h:inputText id="name" value="#{userBean.name}"
                         required="true" />

            <h:commandButton value="Save"
                             action="#{userBean.save}"
                             rendered="#{userBean.editable}" />

            <h:messages />
        </h:form>

        <!-- Cart summary using EL functions and operators -->
        <p>
            You have #{fn:length(cartBean.items)} item(s).
            Total: #{cartBean.total > 100 ? 'Free shipping!' : 'Shipping applies.'}
        </p>

        <!-- Null-safe navigation -->
        <p>Logged in as: #{userBean.profile?.displayName ?: 'Anonymous'}</p>
    </h:panelGroup>
</h:body>
</html>
```

---

### 🚀 What to Learn Next

Since you're already deep in JSF/CDI, the natural next steps from EL are:

1.  **`<f:viewParam>` + `<f:viewAction>`** — the GET-request pattern for bookmarkable pages (uses EL heavily).
2.  **`<f:selectItems>` and `var` scope** — how EL resolves `var` inside iteration components.
3.  **PrimeFaces + EL** — how libraries like PrimeFaces expose additional EL-accessible server-side utilities (e.g., `#{p:component('id')}` for client-side IDs).
4.  **EL 3.0 standalone** — using EL outside JSF (in Java SE, for templating/config).

Would you like to go deeper into any of these — maybe `<f:viewParam>`/`<f:viewAction>` for navigation, or how EL resolves variables inside `dataTable` iteration?



<!-- SOURCE: 5.md -->
# File: 5.md

# 🧭 Deep Dive: `<f:viewParam>`/`<f:viewAction>` + EL Variable Resolution

Great — let's cover both, since they're closely related to everything you've learned about EL and CDI.

---

# PART 1: The GET-Request Pattern — `<f:viewParam>` and `<f:viewAction>`

## 🎯 The Problem They Solve

In JSF, most navigation happens via POST (form submissions). But you often need **bookmarkable URLs** like:

```
/user/details.xhtml?id=42
/product.xhtml?sku=ABC-123&tab=reviews
```

Without `<f:viewParam>`, you'd have to write servlet filters or read `FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()` manually — ugly and error-prone.

**The pattern** is: read URL params → populate bean → run initialization → render page. This gives you the JSF equivalent of a Spring MVC `@GetMapping` + `@RequestParam` controller method.

---

## 🧱 Step 1: The `<f:metadata>` Container

All view parameters and view actions must live inside `<f:metadata>`, which goes **directly under `<f:view>`** (or at the top of the page if `<f:view>` is implicit).

```xhtml
<f:metadata>
    <f:viewParam name="id" value="#{userBean.userId}" />
    <f:viewAction action="#{userBean.loadUser}" />
</f:metadata>
```

**Rules:**
- `<f:metadata>` may appear **only once** per view.
- It must be a **direct child** of `<f:view>` (or the root element of the template client).
- Order matters: `<f:viewParam>` tags are processed **before** `<f:viewAction>` tags.

---

## 🔍 Step 2: `<f:viewParam>` — Reading URL Parameters

`<f:viewParam>` maps a query-string (or path) parameter to a bean property. It behaves **exactly like `<h:inputText>`** — it goes through **conversion and validation** on the way in, and it can write back to the URL when you navigate.

### Key Attributes

| Attribute | Purpose |
| :--- | :--- |
| **`name`** | The URL parameter name (required). |
| **`value`** | The bean property to bind to (required). Must have a getter **and** setter. |
| **`required`** | If `true`, the view fails (with a validation error) when the param is missing. |
| **`converter`** | A converter to convert the string to the target type. |
| **`validator`** | A validator to check the converted value. |
| **`requiredMessage`** | Custom message when `required="true"` fails. |

### Basic Example

```xhtml
<f:metadata>
    <f:viewParam name="id" value="#{userBean.userId}" required="true" />
</f:metadata>
```

```java
@Named
@ViewScoped
public class UserBean implements Serializable {
    private Long userId;
    private User user;

    // getter + setter for userId — JSF writes the "id" param here
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public void loadUser() {
        this.user = userService.findById(userId);
    }

    public User getUser() { return user; }
}
```

Visiting `/details.xhtml?id=42` will:
1. Convert `"42"` to a `Long` using JSF's default number converter.
2. Call `userBean.setUserId(42L)`.
3. Then fire `loadUser()` (if you have a `<f:viewAction>`).

### Combining Multiple Params with Conversion and Validation

```xhtml
<f:metadata>
    <f:viewParam name="id" value="#{productBean.productId}"
                 required="true" requiredMessage="Product ID is required" />

    <f:viewParam name="date" value="#{productBean.purchaseDate}">
        <f:convertDateTime pattern="yyyy-MM-dd" />
    </f:viewParam>

    <f:viewParam name="price" value="#{productBean.maxPrice}">
        <f:validateLongRange minimum="0" maximum="10000" />
    </f:viewParam>

    <f:viewAction action="#{productBean.load}" />
</f:metadata>
```

The order of operations for each `<f:viewParam>` is:

```
Read string → Convert → Validate → Set on bean
```

If any step fails, the value is **not** set and a message is added to the FacesContext.

---

## ⚡ Step 3: `<f:viewAction>` — Running Initialization Logic

`<f:viewAction>` fires a method expression **at the right moment** in the lifecycle — after parameters are converted/validated and **before** the page renders.

### Key Attributes

| Attribute | Purpose |
| :--- | :--- |
| **`action`** | Method to invoke (required). Return a string for navigation, or `null`. |
| **`onPostback`** | Whether to fire on postbacks (AJAX re-renders). **Default: `false`** — see the crucial tip below. |
| **`immediate`** | If `true`, fires in the `APPLY_REQUEST_VALUES` phase (skips conversion/validation of other components). Rarely needed. |
| **`if`** | An EL boolean; if it evaluates to `false`, the action is skipped. |

### The **Critical** Difference: `onPostback="false"` (the default)

This is the single most important thing to understand. By default:

- **First GET request** (user navigates to the URL) → **`<f:viewAction>` fires** ✅
- **Postback** (form submitted, AJAX rerender, validation failure) → **`<f:viewAction>` does NOT fire** ❌

**Why this matters:** If you load the user from the database on every request, an AJAX update to an unrelated field would re-query the DB and overwrite any in-progress edits. So the default (fire once, on the initial GET) is correct for *initialization*.

**If you need it on postbacks** (e.g., reload data after a save):

```xhtml
<f:viewAction action="#{userBean.reload}" onPostback="true" />
```

Use `onPostback="true"` sparingly — it fires on **every** postback, including validation failures.

### Conditional Execution

```xhtml
<f:viewAction action="#{userBean.loadUser}"
              if="#{not empty userBean.userId}" />
```

---

## 🏗️ Step 4: The Complete Pattern in Action

**View: `/user-details.xhtml`**

```xhtml
<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:h="http://xmlns.jcp.org/jsf/html"
      xmlns:f="http://xmlns.jcp.org/jsf/core">

<f:metadata>
    <f:viewParam name="id" value="#{userBean.userId}"
                 required="true" requiredMessage="User ID missing from URL." />
    <f:viewAction action="#{userBean.init}" />
</f:metadata>

<h:body>
    <h:form>
        <h:messages />
        <h:panelGroup rendered="#{not empty userBean.user}">
            <h:outputText value="Editing: #{userBean.user.name}" />
            <h:inputText value="#{userBean.user.email}" />
            <h:commandButton value="Save" action="#{userBean.save}" />
        </h:panelGroup>
    </h:panelGroup>
</h:body>
</html>
```

**Bean: `UserBean.java`**

```java
@Named
@ViewScoped
public class UserBean implements Serializable {
    private Long userId;      // set by <f:viewParam>
    private User user;        // loaded by init()

    @Inject
    UserService userService;

    public void init() {
        user = userService.findById(userId);
        if (user == null) {
            // Navigate away if not found (return value → navigation)
            // In a real app you'd throw a 404 or redirect to an error page.
            FacesContext.getCurrentInstance().getExternalContext()
                        .redirect("error.xhtml");
        }
    }

    public String save() {
        userService.update(user);
        return "user-list?faces-redirect=true";   // PRG pattern
    }

    // getters & setters for userId, user
}
```

### Lifecycle Walkthrough

```
GET /user-details.xhtml?id=42
    │
    ├─ RESTORE_VIEW        — create the view
    ├─ APPLY_REQUEST_VALUES — read "id"=42 → convert to Long → validate → setUserId(42L)
    ├─ PROCESS_VALIDATIONS  — (still inside view metadata processing)
    ├─ UPDATE_MODEL_VALUES  — set userId on the bean
    ├─ INVOKE_APPLICATION   — ← <f:viewAction> fires here: init() runs
    └─ RENDER_RESPONSE      — render page with user loaded
```

Subsequent **postback** (e.g., Save clicked): `invoke init()` is **skipped** (default `onPostback="false"`), so `user` retains its loaded state.

---

## ⚠️ Step 5: `<f:viewAction>` vs `@PostConstruct` vs Getter

Three ways to initialize a page — each with different semantics:

| Approach | When It Runs | Correct For |
| :--- | :--- | :--- |
| **`@PostConstruct`** | Once, when the bean instance is created | Simple setup (e.g., initialize a `List`). **Cannot read `@Named` params reliably** because the bean may be created before the request param is set. |
| **`<f:viewAction>`** | After params are converted & set, before render (on GET by default) | **Loading data based on URL params** — the correct choice for the GET pattern. |
| **Getter (`getUser()`)** | Every time the getter is called (potentially many times per request) | ❌ **Never** for DB calls — see the pitfall below. |

### ⚠️ The Classic Mistake

```java
// ❌ WRONG — DB query in a getter
public User getUser() {
    if (user == null) {
        user = userService.findById(userId);  // fires on EVERY getter call
    }
    return user;
}
```

Getters can be called **multiple times per request** (rendering, AJAX, validation). Use `<f:viewAction>` or lazy-load in a method that's called exactly once.

---

## 🔑 Step 6: The `metadata` and `<f:viewParam>` in Templates

If you're using Facelets templates (`ui:composition`), the `<f:metadata>` belongs in the **template client**, not the template. The template provides the `<f:view>`, and the client fills in the metadata.

**Template: `/WEB-INF/templates/layout.xhtml`**

```xhtml
<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:h="http://xmlns.jcp.org/jsf/html"
      xmlns:ui="http://xmlns.jcp.org/jsf/facelets">
<h:head><title>#{pageTitle}</title></h:head>
<h:body>
    <ui:insert name="metadata" />     <!-- ⬅ slot for <f:metadata> -->
    <div class="content">
        <ui:insert name="content" />
    </div>
</h:body>
</html>
```

**Client: `/user-details.xhtml`**

```xhtml
<ui:composition template="/WEB-INF/templates/layout.xhtml"
                xmlns:ui="http://xmlns.jcp.org/jsf/facelets"
                xmlns:f="http://xmlns.jcp.org/jsf/core"
                xmlns:h="http://xmlns.jcp.org/jsf/html">

    <ui:define name="metadata">
        <f:metadata>
            <f:viewParam name="id" value="#{userBean.userId}" />
            <f:viewAction action="#{userBean.init}" />
        </f:metadata>
    </ui:define>

    <ui:define name="content">
        <h:form>...</h:form>
    </ui:define>
</ui:composition>
```

---

## 💡 Step 7: Pro Tips for `viewParam` / `viewAction`

*   **`includeViewParams`** — when navigating, carry the params along:
    ```xhtml
    <h:link value="Details" outcome="user-details"
            includeViewParams="true">
        <f:param name="id" value="#{item.id}" />
    </h:link>
    ```
*   **Two-argument `<f:viewParam>` with `for`** — you can name a param and reference it in validators. Rarely needed.
*   **Default values** — handle missing params in the bean's setter or in `init()`:
    ```java
    public void setPage(Integer page) {
        this.page = (page == null || page < 1) ? 1 : page;
    }
    ```
*   **PRG (Post-Redirect-Get)** — after a successful save, return `"outcome?faces-redirect=true"` so the browser does a fresh GET. This is essential with `viewParam`: without it, refreshing the page after POST would resubmit the form.

---

# PART 2: EL Variable Resolution Inside Iteration Components

## 🎯 What "Iteration Components" Are

These components iterate over a collection and expose a **loop variable** to EL for each iteration:

- **`<h:dataTable>`** / **`<h:columns>`** (with `var`)
- **`<ui:repeat>`** (with `var`)
- **`<c:forEach>`** (JSTL — with `var`)
- **`<p:dataTable>`, `<p:dataList>`, etc.** (PrimeFaces — same mechanics)
- **`<h:selectManyCheckbox>`, `<h:selectOneMenu>`, etc.** (with `var` on `<f:selectItems>`)

---

## 🧠 Step 1: How the `var` Variable Works

When you write:

```xhtml
<h:dataTable value="#{userBean.users}" var="user">
    <h:column>#{user.name}</h:column>
</h:dataTable>
```

JSF does the following during render:

1. Iterates `#{userBean.users}`.
2. For each element, **binds it to a page-scoped EL variable named `user`**.
3. Renders the row's children with that variable in scope.
4. **Unbinds** the variable when the row is done.

**Key facts:**
*   The `var` name is arbitrary. `var="item"`, `var="u"`, `var="row"` — all valid.
*   The variable is **request-scoped** (actually, stored in `UIViewRoot.getViewMap()`, so it's view-scoped during rendering).
*   It **shadows** any CDI bean with the same name while inside the loop body.
*   It only exists **during rendering of that row** — not outside the component, and not in a subsequent AJAX request unless re-evaluated.

### ⚠️ Critical Pitfall: `var` does NOT survive outside the loop

```xhtml
<h:dataTable value="#{userBean.users}" var="user">
    <h:column>#{user.name}</h:column>
</h:dataTable>

<!-- ❌ BROKEN: 'user' is not defined here -->
<h:outputText value="#{user.name}" />
```

The outer `h:outputText` will resolve `user` against CDI. If no `@Named("user")` bean exists, it returns `null` — no error, but no output.

---

## 🔢 Step 2: `varStatus` — Loop Metadata

Most iteration components (especially `<ui:repeat>` and `<h:dataTable>`) support **`varStatus`**, which exposes a `LoopStatus` object with iteration info.

```xhtml
<ui:repeat value="#{userBean.users}" var="user" varStatus="status">
    <div class="#{status.even ? 'row-even' : 'row-odd'}">
        Row #{status.index + 1} of #{status.end - status.begin + 1}:
        #{user.name}
        <h:commandButton value="Delete"
                         action="#{userBean.delete(user)}"
                         rendered="#{not status.first}" />
    </div>
</ui:repeat>
```

### `LoopStatus` Properties

| Property | Meaning |
| :--- | :--- |
| **`index`** | Zero-based index of the current iteration. |
| **`count`** | One-based count (index + 1). |
| **`begin`** | First index (usually 0). |
| **`end`** | Last index. |
| **`step`** | Increment between rows (usually 1). |
| **`first`** | `true` on the first iteration. |
| **`last`** | `true` on the last iteration. |
| **`even`** / **`odd`** | Alternating row flag — great for zebra striping. |
| **`current`** | The current item (same as the `var` value). |

**Zebra striping:**
```xhtml
<ui:repeat value="#{userBean.users}" var="user" varStatus="s">
    <tr class="#{s.even ? 'even' : 'odd'}">
        <td>#{user.name}</td>
    </tr>
</ui:repeat>
```

---

## 🏗️ Step 3: How `<h:dataTable>` Renders Rows (the "state" gotcha)

Unlike `<ui:repeat>`, `<h:dataTable>` in JSF 2+ **processes rows differently on postback** — this confuses many people.

**The behavior:**
*   On **render**, `h:dataTable` iterates the full collection and renders every row.
*   On **postback**, it only processes the row(s) whose form was actually submitted (e.g., the row whose "Edit" button was clicked). This is a performance optimization.

**The EL implication:** EL inside a row (like `#{user.name}`) resolves correctly during render because the `var` is bound during iteration. But if you try to reference a row item **outside** the table on a later request, it's gone.

**To edit a specific row's item across requests**, you need to either:
*   Store the selected item on the bean: `action="#{userBean.setSelected(user)}"` (but `user` is only valid during that row's render — careful with timing!), or
*   Store the **ID**, then re-fetch: `action="#{userBean.edit(user.id)}"` — this is the safe pattern.

```xhtml
<h:dataTable value="#{userBean.users}" var="user">
    <h:column>
        <h:commandButton value="Edit" action="#{userBean.edit(user.id)}" />
    </h:column>
</h:dataTable>
```

```java
@ViewScoped
public class UserBean {
    private Long selectedId;
    public String edit(Long id) {
        this.selectedId = id;
        return "edit-user?faces-redirect=true&id=" + id;
    }
}
```

---

## 🔁 Step 4: Nested Iteration and Variable Shadowing

You can nest iteration components, and each `var` scopes to its own component.

```xhtml
<h:dataTable value="#{deptBean.departments}" var="dept">
    <h:column>#{dept.name}</h:column>
    <h:column>
        <h:dataTable value="#{dept.employees}" var="emp">
            <h:column>#{emp.name}</h:column>
        </h:dataTable>
    </h:column>
</h:dataTable>
```

### Variable Shadowing

If you reuse a name, the inner one shadows the outer:

```xhtml
<ui:repeat value="#{bean.list1}" var="item">
    Outer: #{item.name}
    <ui:repeat value="#{item.children}" var="item">   <!-- SHADOWS outer 'item' -->
        Inner: #{item.name}
    </ui:repeat>
    <!-- 'item' refers to outer again here ✅ -->
</ui:repeat>
```

**Recommendation**: Use distinct names (`item`, `child`, `sub`) to avoid confusion.

---

## 🧩 Step 5: The `cc` Implicit Variable (Composite Components)

Inside a composite component's `<cc:implementation>`, the **`cc`** variable is available. It gives you access to:

| Expression | Meaning |
| :--- | :--- |
| **`#{cc.attrs.foo}`** | An attribute passed in by the consumer. |
| **`#{cc.id}`** | The component's client ID. |
| **`#{cc.clientId}`** | The fully qualified client ID (with naming container prefix). |
| **`#{cc.parent}`** | The parent component. |
| **`#{cc.children}`** | Child components. |

```xhtml
<cc:interface>
    <cc:attribute name="label" required="true" />
    <cc:attribute name="value" />
</cc:interface>
<cc:implementation>
    <h:outputLabel for="#{cc.clientId}" value="#{cc.attrs.label}" />
    <h:inputText id="#{cc.id}" value="#{cc.attrs.value}" />
</cc:implementation>
```

**`cc.attrs` is the standard way** to read composite component parameters — it's a map, so you can also use `#{cc.attrs['label']}`.

---

## 🔗 Step 6: `<ui:param>` — Passing EL Variables to Includes

`<ui:param>` binds a name → value pair in a **template/include's EL scope**:

```xhtml
<ui:include src="/WEB-INF/fragments/row.xhtml">
    <ui:param name="item" value="#{user}" />
</ui:include>
```

Inside `row.xhtml`, `#{item}` resolves to the `user` object — it's available for the entire included file. This is how you pass loop variables into reusable fragments.

**Common pattern:**

```xhtml
<ui:repeat value="#{userBean.users}" var="user">
    <ui:include src="/WEB-INF/fragments/userRow.xhtml">
        <ui:param name="user" value="#{user}" />
        <ui:param name="editAction" value="#{userBean.edit}" />
    </ui:include>
</ui:repeat>
```

---

## 🎭 Step 7: EL Resolution Order (Which `var` Wins?)

When EL evaluates `#{user.name}`, it searches in this order:

1. **Implicit objects** — `#{param}`, `#{request}`, `#{session}`, etc.
2. **`var` from enclosing iteration components** (`h:dataTable`, `ui:repeat`, `c:forEach`) — the **innermost** one wins.
3. **`ui:param`** bound in the current template/include scope.
4. **`cc.attrs`** (for composite components).
5. **CDI beans** with `@Named("user")` — resolved by the EL resolver chain.
6. **Managed beans** (legacy `@ManagedBean` — deprecated).
7. **Page-scoped / view-scoped attributes** stored via `UIViewRoot.getViewMap()`.

**Practical implication:** If you name a CDI bean the same as a loop `var`, the loop wins inside the loop body. Outside, the CDI bean wins.

---

## ⚠️ Step 8: Common Pitfalls With Iteration EL

| Pitfall | Symptom | Fix |
| :--- | :--- | :--- |
| **Referencing `var` outside the loop** | Silent `null` / blank output | Pass the value explicitly or store on a bean. |
| **Same `var` name as a `@Named` bean** | Confusing shadowing bugs | Use distinct names (`item`, not `userBean`). |
| **Getter called many times** | Slow pages | Cache computed values in the bean; don't query per getter. |
| **`ui:repeat` inside `h:dataTable`** | Works, but postbacks only process the outer row you clicked | Fine for display; for editing, prefer `h:dataTable` in `h:dataTable`. |
| **`<c:forEach>` breaks view scope** | Bean loses state | `c:forEach` is a **render-time** loop — don't use it to wrap input components in forms. Use `ui:repeat` instead. |
| **`var` not visible in a nested `ui:include`** | `null` inside included file | Pass it via `<ui:param>`. |
| **AJAX `render` on a nested component** | Component doesn't update | Target the **outer wrapper** (the row group), not the inner component. |

### ⚠️ The `<c:forEach>` Trap (Important)

`<c:forEach>` (JSTL) executes **during view build**, not during render. So if you wrap input components in it, on postback the loop may not reconstruct the exact same component tree — leading to lost values, "component not found," or view-scope breakage.

**Rule:** Use `<c:forEach>` only for **static** iteration (e.g., iterating over a fixed list of enum values to build output text). Use **`<ui:repeat>`** for anything involving input components, AJAX, or dynamic data.

---

## 💎 Step 9: Putting It All Together — A Working Example

**View:**

```xhtml
<h:form id="userForm">
    <h:dataTable value="#{userBean.users}" var="user" varStatus="s"
                 styleClass="#{s.even ? 'row-even' : 'row-odd'}">

        <!-- Row number -->
        <h:column>
            <f:facet name="header">#</f:facet>
            #{s.count}
        </h:column>

        <!-- Name -->
        <h:column>
            <f:facet name="header">Name</f:facet>
            #{user.name}
        </h:column>

        <!-- Status with EL ternary -->
        <h:column>
            <f:facet name="header">Status</f:facet>
            #{user.active ? 'Active' : 'Inactive'}
        </h:column>

        <!-- Actions: only edit if active; delete only if not first -->
        <h:column>
            <f:facet name="header">Actions</f:facet>
            <h:commandButton value="Edit"
                             action="#{userBean.edit(user.id)}"
                             rendered="#{user.active}" />
            <h:commandButton value="Delete"
                             action="#{userBean.delete(user.id)}"
                             rendered="#{not s.first}" />
        </h:column>
    </h:dataTable>
</h:form>
```

**Bean:**

```java
@Named
@ViewScoped
public class UserBean implements Serializable {
    @Inject UserService userService;

    private List<User> users;

    @PostConstruct
    public void init() { this.users = userService.findAll(); }

    public String edit(Long id) { return "edit-user?faces-redirect=true&id=" + id; }

    public void delete(Long id) {
        userService.delete(id);
        users = userService.findAll();     // refresh list
    }

    public List<User> getUsers() { return users; }
}
```

**What EL resolves and when:**

| Expression | When It Runs | What It Resolves To |
| :--- | :--- | :--- |
| `#{userBean.users}` | GET + postback | Bean getter → `List<User>` |
| `#{user.name}` | Per-row render | `var="user"` → current `User` |
| `#{s.count}` | Per-row render | `varStatus` → 1-based count |
| `#{user.active}` | Per-row render | Property of current `User` |
| `#{not s.first}` | Per-row render | Boolean → skips delete on row 1 |
| `#{userBean.edit(user.id)}` | When the button submits | Method expression with argument |

---

## 🚀 What's Next

You now have a solid foundation in:

- Core JSF components (`h:`, `f:`, `ui:`, composite)
- EL — expressions, operators, functions, implicit objects
- GET pattern (`viewParam`/`viewAction`) + EL iteration semantics

**Natural follow-ups**, depending on your goal:

1. **AJAX deep-dive with `<f:ajax>`** — how `execute`/`render` resolve through EL, and `@this`/`@form`/`@parent` keywords.
2. **Facelets templating in depth** — `<ui:composition>`, `<ui:decorate>`, `<ui:insert>` slots, and `ui:param` cascading.
3. **CDI events in JSF** — `@Observes` to react to lifecycle events, plus `@ViewScoped`/`@ConversationScoped` edge cases.
4. **PrimeFaces `p:dataTable`** — filtering, sorting, selection, lazy loading — where iteration EL meets advanced state management.
5. **Security** — CSRF protection, EL injection prevention (`escapeXml`, avoid `eval`-style EL), and CSRF tokens in forms.

Which one would you like to tackle next?



<!-- SOURCE: 6.md -->
# File: 6.md

This deep-dive covers how `<f:ajax>` resolves its `execute` and `render` targets, focusing on the standard keywords and how to drive them dynamically with EL. A crucial point to know upfront is that the `@parent` keyword you mentioned is a **PrimeFaces extension** and is not part of the standard JSF `<f:ajax>` component. The standard JSF keywords are `@this`, `@form`, `@all`, and `@none`. The core mechanism for both attributes is the **Search Expression Framework**, which resolves the provided IDs against the component tree.

### ⚙️ The Core Mechanics: `execute` vs. `render`

Although they often appear together, these two attributes operate in completely different phases and on different sides of the request.

| Attribute | Side & Phase | Purpose |
| :--- | :--- | :--- |
| **`execute`** | **Server-side** (Process Validations to Invoke Application) | Tells JSF which components' submitted values should be **processed**. This includes applying request values, conversion, validation, and updating the model. Components not included are skipped entirely during these phases. |
| **`render`** | **Client-side** (Render Response) | Tells the JavaScript AJAX handler which parts of the HTML DOM should be **updated** with the server's response. JSF will only generate the response payload for these specified components. |

**Critical Defaults to Remember:**
*   **`execute` defaults to `@this`**. This is a very common source of confusion. An AJAX request triggered by a button will **not** submit other form fields unless you explicitly change this to `@form` or list the specific input IDs.
*   **`render` defaults to `@none`**. You must always specify what you want to update.

---

### 🎯 Standard Keyword Semantics (`@this`, `@form`, `@all`, `@none`)

These keywords are the building blocks for targeting components relative to the one triggering the AJAX event.

| Keyword | `execute` Behavior (Server-side) | `render` Behavior (Client-side) |
| :--- | :--- | :--- |
| **`@this`** | Processes **only the requesting component** (e.g., the command button itself). This is the **default** for `execute`. | Re-renders **only the requesting component**. |
| **`@form`** | Processes **all components within the closest ancestor `<h:form>`**. | Re-renders the **entire form** containing the requesting component. |
| **`@all`** | Processes **every component in the view** (the whole tree). | Re-renders the **entire view** (effectively a full page refresh). |
| **`@none`** | Processes **no components**. Useful for pure client-side actions or invoking a listener without validating any inputs. | Re-renders **nothing**. This is the **default** for `render`. |

**On `@parent`:** As mentioned, this is **not a standard JSF keyword** for `<f:ajax>`. It is a PrimeFaces extension used with `<p:ajax>` and `<p:commandXxx>` components. In standard JSF, you would need to reference the parent component's ID explicitly or use `@form` if the parent is the form.

---

### 🧩 How EL Resolution Works: Dynamic Search Expressions

The power of these attributes goes beyond static keywords. Both `execute` and `render` accept a **space-separated list of search expressions**. More importantly, this list can come from an **EL Expression** that resolves dynamically at request time.

The specification states that if the attribute is a `ValueExpression`, it must evaluate to a **`Collection` of `String` objects** (or a single `String`), where each string is a client ID or a keyword.

#### Example: Building a Dynamic Render Target List

Imagine a page with a central content panel that changes based on user interaction, but you also want to update a global notification bar.

**The Bean:**
```java
@Named
@ViewScoped
public class DynamicAjaxBean implements Serializable {
    private String currentPanelId = "mainContent"; // Can be changed by other actions

    // This property provides the dynamic list of IDs to re-render
    public Collection<String> getDynamicRenderTargets() {
        List<String> targets = new ArrayList<>();
        targets.add(":" + currentPanelId); // e.g., ":mainContent"
        targets.add(":globalNotifications"); // Always update the notification bar
        return targets;
    }
    // ... getters and setters
}
```

**The View (`<f:ajax>` usage):**
```xhtml
<h:commandButton value="Refresh Dynamic Area" action="#{dynamicAjaxBean.someAction}">
    <f:ajax execute="@form" render="#{dynamicAjaxBean.dynamicRenderTargets}" />
</h:commandButton>

<h:panelGroup id="mainContent" layout="block">
    <!-- Content that changes -->
</h:panelGroup>

<h:panelGroup id="globalNotifications" layout="block">
    <!-- A global message area -->
</h:panelGroup>
```

**How it works:**
1.  When the button is clicked, the EL expression `#{dynamicAjaxBean.dynamicRenderTargets}` is evaluated.
2.  The getter returns a `Collection<String>` containing `":mainContent"` and `":globalNotifications"`.
3.  JSF's search expression resolver processes this list. It treats each entry as a client ID to locate the corresponding components in the view tree.
4.  Only those resolved components are included in the AJAX response for the browser to update.

#### EL for Complex Expressions (Separate from Attribute Values)

You can also use EL to generate a **single string** that is the entire search expression. This is useful when the *type* of search (e.g., a keyword vs. an ID) needs to be dynamic, though it is more brittle than returning a Collection.

```xhtml
<!-- Render the form if some condition is met, otherwise render only a specific panel -->
<f:ajax execute="@this"
        render="#{bean.renderWholeForm ? '@form' : ':detailsPanel'}" />
```

This approach mixes keywords and IDs within a dynamically constructed string, which is valid but generally harder to maintain than returning a structured `Collection`.

---

### ⚠️ Critical Pitfalls and Best Practices

*   **`execute` Must Include the Action Source**: If your AJAX request relies on an `action` or `actionListener` (like on a `commandButton`), that component **must** be part of the `execute` set. The default `@this` handles this, but if you specify custom IDs (e.g., `execute="foo"`), you must also include the command component itself (e.g., `execute="@this foo"`), or its action will never be invoked.
*   **`render` Cannot Create Non-Existent Components**: If a component's `rendered` attribute is `false` during the initial page load, it does not exist in the HTML DOM. An AJAX `render` targeting that component ID will fail silently because there is nothing to update. You must wrap the conditional component in a parent container (which is always rendered) and target that parent instead.
*   **Use the Search Expression `Collection` for Complex Logic**: Returning a `Collection<String>` from an EL expression is the most robust way to build dynamic `execute` or `render` targets. It avoids string-concatenation bugs and clearly separates the logic of *what* to target from the JSF mechanism that *resolves* those targets.

Would you like to explore how this search expression framework also powers other components like `<h:message>` or `<h:outputLabel>`, or move on to another JSF topic like Facelets templating or CDI events?



<!-- SOURCE: 7.md -->
# File: 7.md

# 🧪 Coding Exercise #2: Bookmarkable Search Page + Dynamic `render` Lists

## 🎯 Scenario

Build a **product search** page at a bookmarkable URL:

```
/search.xhtml?q=phone&category=electronics&page=2
```

**Requirements:**

1. All search criteria (`q`, `category`, `page`) live in the **URL** — shareable and refreshable.
2. Search runs on **initial GET only** (via `viewAction`), not on every postback.
3. The **Search** button uses AJAX: process the search form, then update a **dynamic set of render targets**.
4. The render targets must be returned from a **`Collection<String>` getter** on the bean — not a hardcoded string.
5. **Pagination** links must preserve the search criteria (`includeViewParams`).
6. Guard against the classic pitfalls: no getter-side DB calls, no targeting conditionally-absent components, proper scope, `Serializable`.
7. Handle the **empty-results** case cleanly.

**Write the bean and the view.** Compare with the solution below.

---

## ✅ Solution

### `SearchBean.java`

```java
import jakarta.enterprise.context.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Named
@ViewScoped
public class SearchBean implements Serializable {          // pitfall #6

    private static final int PAGE_SIZE = 10;

    // --- set by <f:viewParam> ---
    private String q;
    private String category;
    private Integer page = 1;                              // default handled in setter

    // --- search output ---
    private List<Product> results = new ArrayList<>();
    private long totalCount;
    private boolean searched;                              // did init/search run yet?

    @Inject
    ProductService productService;                         // thin bean, service layer

    // ---------------------------------------------------------------
    // Initialization: runs once on the initial GET (onPostback=false)
    // ---------------------------------------------------------------
    public void init() {
        doSearch();
    }

    // ---------------------------------------------------------------
    // AJAX Search button: re-run the query, reset to page 1
    // ---------------------------------------------------------------
    public void search() {
        this.page = 1;
        doSearch();
    }

    private void doSearch() {
        this.searched = true;
        this.totalCount = productService.count(q, category);
        this.results = productService.find(q, category, page, PAGE_SIZE);
    }

    // ---------------------------------------------------------------
    // Dynamic render target list — the point of this exercise
    // ---------------------------------------------------------------
    public Collection<String> getDynamicRenderTargets() {
        List<String> targets = new ArrayList<>();
        targets.add(":resultCount");          // "N results found" line
        targets.add(":resultsPanel");         // table OR empty-state message
        if (totalCount > PAGE_SIZE) {
            targets.add(":pagination");       // only update pager when relevant
        }
        return targets;
    }

    // ---------------------------------------------------------------
    // Pagination (GET links, preserve params)
    // ---------------------------------------------------------------
    public boolean isHasNext()  { return page * PAGE_SIZE < totalCount; }
    public boolean isHasPrev()  { return page > 1; }
    public int getTotalPages()  { return (int) Math.ceil((double) totalCount / PAGE_SIZE); }

    // ---------------------------------------------------------------
    // Getters / setters  — note: NO database calls here (pitfall #6)
    // ---------------------------------------------------------------
    public String getQ() { return q; }
    public void setQ(String q) { this.q = q; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Integer getPage() { return page; }
    public void setPage(Integer page) {                    // null/negative safe
        this.page = (page == null || page < 1) ? 1 : page;
    }

    public List<Product> getResults() { return results; }
    public long getTotalCount() { return totalCount; }
    public boolean isSearched() { return searched; }
}
```

### `search.xhtml`

```xhtml
<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:h="http://xmlns.jcp.org/jsf/html"
      xmlns:f="http://xmlns.jcp.org/jsf/core"
      xmlns:fn="http://xmlns.jcp.org/jsf/function">

<!-- ✅ Requirement 1 & 2: bookmarkable URL, init on GET only -->
<f:metadata>
    <f:viewParam name="q"        value="#{searchBean.q}" />
    <f:viewParam name="category" value="#{searchBean.category}" />
    <f:viewParam name="page"     value="#{searchBean.page}" />
    <f:viewAction action="#{searchBean.init}" />
</f:metadata>

<h:body>

    <!-- ============ SEARCH FORM (sibling of results, not nested) ============ -->
    <h:form id="searchForm">

        <h:outputLabel for="q" value="Keyword:" />
        <h:inputText id="q" value="#{searchBean.q}" />

        <h:outputLabel for="cat" value="Category:" />
        <h:selectOneMenu id="cat" value="#{searchBean.category}">
            <f:selectItem itemValue="#{null}" itemLabel="All" />
            <f:selectItem itemValue="electronics" itemLabel="Electronics" />
            <f:selectItem itemValue="books"       itemLabel="Books" />
        </h:selectOneMenu>

        <!-- ✅ Requirement 3 & 4: dynamic render target list from a Collection -->
        <h:commandButton value="Search"
                         action="#{searchBean.search}">
            <f:ajax execute="@form"
                    render="#{searchBean.getDynamicRenderTargets}" />
        </h:commandButton>
    </h:form>

    <!-- ============ RESULT COUNT (always exists; safe target) ============ -->
    <h:panelGroup id="resultCount" layout="block">
        <h:outputText value="#{searchBean.totalCount} result(s) found"
                      rendered="#{searchBean.searched}" />
    </h:panelGroup>

    <!-- ============ RESULTS PANEL (always exists; safe target) ============ -->
    <h:panelGroup id="resultsPanel" layout="block">

        <!-- ✅ Requirement 7: empty state lives INSIDE the always-rendered wrapper -->
        <h:panelGroup rendered="#{searchBean.searched and empty searchBean.results}"
                      layout="block">
            <h:outputText value="No products matched your search." />
        </h:panelGroup>

        <h:dataTable value="#{searchBean.results}" var="p"
                     rendered="#{not empty searchBean.results}">
            <h:column>
                <f:facet name="header">Name</f:facet>
                #{p.name}
            </h:column>
            <h:column>
                <f:facet name="header">Price</f:facet>
                <h:outputText value="#{p.price}">
                    <f:convertNumber type="currency" currencySymbol="$" />
                </h:outputText>
            </h:column>
        </h:dataTable>
    </h:panelGroup>

    <!-- ============ PAGINATION (GET links; ✅ Requirement 5) ============ -->
    <h:panelGroup id="pagination" layout="block">
        <h:link value="« Prev" outcome="/search"
                includeViewParams="true"
                rendered="#{searchBean.hasPrev}">
            <f:param name="page" value="#{searchBean.page - 1}" />
        </h:link>

        <h:outputText value=" Page #{searchBean.page} of #{searchBean.totalPages} " />

        <h:link value="Next »" outcome="/search"
                includeViewParams="true"
                rendered="#{searchBean.hasNext}">
            <f:param name="page" value="#{searchBean.page + 1}" />
        </h:link>
    </h:panelGroup>

</h:body>
</html>
```

---

## 🔍 Why Each Piece Works

| Requirement | How it's satisfied |
| :--- | :--- |
| **Bookmarkable URL** | Three `<f:viewParam>` bind `q`, `category`, `page` to the bean. |
| **Init on GET only** | `<f:viewAction>` defaults to `onPostback="false"`, so `init()` runs once on the first GET and skips on AJAX postbacks. |
| **Dynamic render list** | `getDynamicRenderTargets()` returns `Collection<String>`; JSF resolves each entry via the **Search Expression Framework** to an absolute client ID. |
| **Empty state** | The empty message is a **child of `resultsPanel`** (always rendered) — the target never disappears from the DOM. |
| **Pagination preserves params** | `includeViewParams="true"` carries `q` and `category`; the explicit `<f:param name="page">` overrides only the page. |
| **No getter DB calls** | All queries happen in `init()` and `search()` — never in `getResults()`. |
| **Clustering-safe** | `@ViewScoped implements Serializable`. |
| **Thin bean** | Logic lives in injected `ProductService`. |

### ⚠️ What Would Break Here (and Why)

- **`render=":resultsPanel :pagination"` hardcoded** → pagination is `@none`-rendered on first search when `totalCount == 0`, causing a JS error on the DOM update. The `Collection` approach adds `:pagination` **only when relevant**. ✅
- **Nesting the results table inside `<h:form id="searchForm">`** → the `dataTable`'s rows (which may contain their own command buttons) would nest forms — invalid HTML. Keeping results as a **sibling** of the form avoids this. ✅
- **Rendering `:resultCount` while it contains `rendered="#{...}"` on the text** → the panel always exists, the text conditionally renders inside. Targeting the panel is safe. ✅
- **Putting the empty-state `<h:panelGroup>` at top level and rendering it directly** → when it's `rendered="false"`, it's absent from the DOM, and the next AJAX update targeting it fails silently. ✅

---

## 🧪 Try It Yourself

1. **Break `render` on purpose:** hardcode `render=":resultsPanel :pagination"` and search with zero results. Open the browser console → see the failed DOM update. Restore the Collection getter.
2. **Remove `onPostback` guard:** temporarily add `onPostback="true"` to `<f:viewAction>`. Type a keyword and click Search — the search runs twice (once for `init`, once for `search`). Remove it.
3. **Add a sort param:** add `<f:viewParam name="sort">`, a `<h:selectOneMenu>` bound to it, and include it in `getDynamicRenderTargets()`. Wire it into `ProductService.find(...)`.
4. **Add OmniFaces:** swap `<h:form>` for `<o:form includeRequestParams="true">` so the search form posts the URL params along — this eliminates the "params lost after AJAX" class of bugs.

---

# 📇 Flashcards — Next Topics

Cover the right column and drill.

## Facelets Templating (`ui:`)

| # | Question | Answer |
| :--- | :--- | :--- |
| 1 | What is a Facelets **template**? | A layout file with `<ui:insert>` placeholders, filled by clients via `<ui:define>`. |
| 2 | What is `<ui:composition template="...">`? | Marks a page as a **template client**; it discards everything outside the tag and fills the template's slots. |
| 3 | `<ui:define name="x">` vs `<ui:insert name="x">` | `define` supplies content (client); `insert` declares a slot (template). |
| 4 | `<ui:include>` vs `<ui:decorate>` | `include` statically includes a fragment; `decorate` wraps content into a template **without** discarding the wrapper's surrounding markup. |
| 5 | Where does a client page's `<f:metadata>` go when using templates? | Inside a `<ui:define name="metadata">` that the template inserts — **not** in the template itself. |
| 6 | How do you pass values into a template/include? | `<ui:param name="..." value="..."/>`. |
| 7 | What is the standard templating file convention? | Templates under `/WEB-INF/templates/`, fragments under `/WEB-INF/fragments/`. |
| 8 | Why store templates under `WEB-INF`? | They're not directly accessible by URL — only includable by the app. |
| 9 | What are composite components (vs. templates)? | True reusable **components** with a contract (`cc:interface`) and implementation (`cc:implementation`) — not just layout fragments. |
| 10 | Which EL object reads a composite component's attributes? | `#{cc.attrs.<name>}` (and `#{cc.clientId}`, `#{cc.id}`). |

## `<ui:repeat>` vs `<c:forEach>`

| # | Question | Answer |
| :--- | :--- | :--- |
| 11 | When does `<c:forEach>` execute? | **View-build time** — it changes the component tree *before* rendering. |
| 12 | When does `<ui:repeat>` execute? | **Render time** — it iterates while rendering the view. |
| 13 | Which one is safe around input components and AJAX? | **`<ui:repeat>`** — the tree is rebuilt consistently on postback. |
| 14 | Why can `<c:forEach>` break view scope? | It rebuilds the tree differently on postback → state mismatches, "component not found," lost values. |
| 15 | What's `<c:forEach>` still good for? | **Static** iteration — e.g., iterating enum values to build output text, or conditionally building markup at build time. |
| 16 | Which supports `varStatus` (first/last/even/index/count)? | **Both** — `ui:repeat` and `h:dataTable`. (`c:forEach` has its own `varStatus` too.) |
| 17 | Which one is available **inside** the tag body during rendering but not after? | The `var` from either — it's scoped to the iteration. |
| 18 | `h:dataTable` vs `ui:repeat` for AJAX row editing? | Prefer `h:dataTable` (or `p:dataTable`) for editing — it only processes the submitted row on postback. |

## CDI Events (`@Observes`)

| # | Question | Answer |
| :--- | :--- | :--- |
| 19 | How do you fire a CDI event? | `@Inject Event<MyEvent> event;` then `event.fire(new MyEvent(...));` |
| 20 | How do you observe one? | `public void on(@Observes MyEvent e) { ... }` — returns `void`. |
| 21 | What is the default observer scope's effect? | Observers are **synchronous** by default — the firing thread runs them inline before `fire()` returns. |
| 22 | How do you make an event asynchronous? | Fire on an async event: `@Inject @Inject Event<MyEvent> event;` — or use `event.fireAsync(...)` (CDI 2.0+). |
| 23 | What are **qualifiers** for? | To distinguish multiple events/observers of the same type — e.g., `@Created`, `@Updated`. |
| 24 | What lifecycle events can JSF/CDI beans observe? | `@Initialized(RequestScoped.class)`, `@Destroyed(...)`, etc., plus custom events. |
| 25 | Why use events instead of direct `@Inject` calls? | Decoupling — the producer doesn't know the consumers. Great for cross-cutting reactions (notifications, audit, cache invalidation). |
| 26 | What's a typical JSF use case for events? | Firing a "UserLoggedIn" event and having unrelated beans (cart loader, audit logger, preferences) react independently. |

---

## 🔑 Quick Sanity Check

1. Which Facelets tag **discards** the client page's surrounding markup? → `<ui:composition>`
2. Which loop is safe inside an `<h:form>` with input components? → `<ui:repeat>`
3. Are CDI observers synchronous by default? → **Yes**
4. What EL object reads composite attributes? → `#{cc.attrs.x}`
5. Where does `<f:metadata>` go in a template-based page? → inside `<ui:define name="metadata">`

---

Would you like to go deeper into any of these three next topics — e.g., a **full Facelets template + 3 client pages** exercise, a **`ui:repeat` vs `c:forEach` side-by-side demo** proving the breakage, or a **CDI event–driven audit log** implementation?



<!-- SOURCE: q.md -->
# File: q.md

# 📝 Review Questions & Answers — The 6 Lessons

Below are two Q&A sets per lesson: a **quick recall** question and a **deeper/scenario** question. Answers are provided immediately after each.

---

## Lesson 1: Use `<f:ajax>` for Responsive UI Updates

**Q1.1 — What are the two key attributes of `<f:ajax>`, and what does each control?**

> **A:** `execute` controls which components' values are sent to the server (server-side processing). `render` controls which components are refreshed in the browser from the AJAX response (client-side DOM update).

**Q1.2 — Why is `@ViewScoped` the best bean scope for AJAX-heavy pages, and what's the classic pitfall when using `render`?**

> **A:** `@ViewScoped` keeps the same bean instance alive across multiple AJAX partial postbacks, so page state is preserved. The classic pitfall is targeting a component in `render` that doesn't exist in the DOM (e.g., because it's conditionally rendered). You must target a parent container that always exists — not the conditionally rendered component itself.

---

## Lesson 2: Layer Your Validation for Clear Feedback

**Q2.1 — In what order does JSF apply the three validation layers?**

> **A:** (1) `required="true"` — checked first, before any conversion; (2) Faces validators like `<f:validateLength>` — standard component-level rules; (3) Bean Validation (JSR 303/349) — annotations like `@NotNull`, `@Size`, `@Pattern` on bean properties.

**Q2.2 — You've added validators but the page silently fails on submit. What's the debugging step, and why does this matter?**

> **A:** Add `<h:messages globalOnly="false" />` to the page during development. Conversion and validation failures are otherwise easy to miss because they add messages to the `FacesContext` without throwing visible errors — you only see them if you display messages. This ensures users get clear feedback instead of a "nothing happened" experience.

---

## Lesson 3: Master Navigation and the Post-Redirect-Get (PRG) Pattern

**Q3.1 — How do you implement the PRG pattern in a JSF action method, and what problem does it solve?**

> **A:** Return the outcome with `?faces-redirect=true` from the action method:
> ```java
> public String save() {
>     // ... save logic ...
>     return "success?faces-redirect=true";
> }
> ```
> It prevents the browser's "Confirm Form Resubmission" warning on refresh after a POST, and gives clean, bookmarkable URLs.

**Q3.2 — For a bookmarkable page like `/product.xhtml?id=42`, which tags do you use to read the parameter and run initialization logic?**

> **A:** `<f:viewParam>` to read the URL parameter into a bean property, and `<f:viewAction>` to run initialization logic after parameters are converted/validated but before the page renders. Both live inside `<f:metadata>`.

---

## Lesson 4: Choose Scopes Deliberately

**Q4.1 — Match each scope to its lifetime and a typical use case: `@RequestScoped`, `@ViewScoped`, `@SessionScoped`, `@ApplicationScoped`.**

> **A:**
> 
> | Scope | Lifetime | Typical Use |
> | :--- | :--- | :--- |
> | `@RequestScoped` | One HTTP request | Stateless pages, simple non-AJAX forms |
> | `@ViewScoped` | One view, across AJAX postbacks | Most pages with forms, tables, AJAX |
> | `@SessionScoped` | Entire user session | Logged-in user, preferences |
> | `@ApplicationScoped` | Entire application | Reference data, caches (must be thread-safe) |

**Q4.2 — What's the critical rule about `@ViewScoped` and `@SessionScoped` beans, and why does it matter under clustering?**

> **A:** They **must implement `Serializable`**. The container may passivate or replicate these beans (e.g., under clustering or when memory is tight). Non-serializable fields should be marked `transient`. A non-serializable bean works on a single node but fails mysteriously under clustering/passivation.

---

## Lesson 5: Avoid These Common Pitfalls

**Q5.1 — Name four common JSF pitfalls and the fix for each.**

> **A:**
> 
> | Pitfall | Fix |
> | :--- | :--- |
> | Business logic in the bean | Keep beans thin; move logic to a service layer injected with `@Inject` |
> | Work in getters | Getters are called multiple times per request — never put DB calls or expensive logic in them |
> | `<h:commandButton>` outside `<h:form>` | It does nothing — always place command components inside a form |
> | Forgetting `Serializable` on view/session beans | Always implement it; mark non-serializable fields `transient` |

**Q5.2 — Why is doing work in a getter especially dangerous on an AJAX-heavy page?**

> **A:** Getters can be called multiple times per request (during rendering, conversion, validation, AJAX updates). A DB call in a getter multiplies with each invocation — drastically hurting performance and potentially overwriting in-progress data. Cache the result in the bean and load it once (e.g., in `@PostConstruct` or via `<f:viewAction>`).

---

## Lesson 6: Explore OmniFaces

**Q6.1 — What is OmniFaces, and why is it worth learning after the core JSF fundamentals?**

> **A:** OmniFaces is a utility library that fills common gaps in JSF. It offers powerful components and utilities (e.g., `Faces`, `Ajax`, `Messages` helpers) that make development easier and help you avoid reinventing the wheel. It's a natural next step once you're comfortable with core JSF.

**Q6.2 — In what kind of situation would you reach for OmniFaces rather than writing your own utility?**

> **A:** When you need common but non-trivial features JSF doesn't provide natively — e.g., a robust `<o:form>` that handles CSRF, `<o:commandScript>` for calling server actions from JavaScript, `Faces.getRequestParameter()` utilities, or `<o:cache>` for view caching. Using a well-tested library avoids subtle bugs and saves time compared to hand-rolling these utilities.

---

## 🎯 Bonus: Mini Integration Challenge

**Q7 — Combine Lessons 1, 3, and 4: Design a small flow where a user edits a product and, after saving via AJAX, sees a success message — but refreshing the page does not resubmit the form. Name the scope, the AJAX tags, and the navigation outcome.**

> **A:**
> - **Scope:** `@ViewScoped` (needs to survive AJAX postbacks while editing).
> - **AJAX tags:** `<f:ajax execute="@form" render=":messages" />` on the Save button — processes the form and updates a messages panel.
> - **Navigation:** After success, return `"product-list?faces-redirect=true"` from the save method (PRG pattern) so refresh doesn't resubmit.
> - **Bonus:** Use `<h:messages>` in the target area so the success message is visible, and `<f:viewParam>`/`<f:viewAction>` to load the product by ID from a bookmarkable URL.

---

Would you like me to generate **flashcards** (one-line Q/A format for quick drilling), a **quiz with multiple-choice answers**, or a **coding exercise** based on these lessons?



<!-- SOURCE: q1.md -->
# File: q1.md

# 🎓 The Full Review Pack — Flashcards, Quiz & Coding Exercise

---

# PART 1: ⚡ Flashcards (Quick Drilling)

Cover the right column and test yourself. Each card is one line.

| # | Question | Answer |
| :--- | :--- | :--- |
| 1 | What does `execute` control in `<f:ajax>`? | Which components' values are **processed server-side** (apply values, convert, validate, update model). |
| 2 | What does `render` control in `<f:ajax>`? | Which components are **refreshed in the browser DOM** from the response. |
| 3 | Default value of `execute`? | `@this` — only the requesting component. |
| 4 | Default value of `render`? | `@none` — nothing is updated. |
| 5 | Best bean scope for AJAX-heavy pages? | `@ViewScoped`. |
| 6 | Classic `render` pitfall? | Targeting a conditionally rendered component that doesn't exist in the DOM — target its always-present parent wrapper instead. |
| 7 | Order of JSF validation layers? | `required` → Faces validators → Bean Validation (JSR 303/349). |
| 8 | Tag to debug silent validation failures? | `<h:messages globalOnly="false" />`. |
| 9 | How do you implement PRG in JSF? | Return `"outcome?faces-redirect=true"` from the action method. |
| 10 | What does PRG prevent? | Browser "Confirm Form Resubmission" on refresh after POST. |
| 11 | Tag to read a URL parameter into a bean? | `<f:viewParam>`. |
| 12 | Tag to run init logic after params are set? | `<f:viewAction>`. |
| 13 | Where do `viewParam`/`viewAction` live? | Inside `<f:metadata>` (direct child of `<f:view>`). |
| 14 | `@RequestScoped` lifetime & use? | One HTTP request; stateless pages, simple non-AJAX forms. |
| 15 | `@ViewScoped` lifetime & use? | One view across AJAX postbacks; forms, tables, AJAX pages. |
| 16 | `@SessionScoped` lifetime & use? | Entire user session; logged-in user, preferences. |
| 17 | `@ApplicationScoped` lifetime & use? | Entire application; reference data, caches — **must be thread-safe**. |
| 18 | Critical rule for view/session beans? | **Must implement `Serializable`**; mark non-serializable fields `transient`. |
| 19 | Why is a non-serializable view bean dangerous? | It fails silently under clustering/passivation despite working on a single node. |
| 20 | Why are getters dangerous for heavy work? | They can be called **multiple times per request** — DB calls in getters multiply and kill performance. |
| 21 | Where should business logic live? | In a service layer injected with `@Inject` — keep backing beans thin. |
| 22 | What happens to a `commandButton` outside a form? | Nothing — it does nothing without an enclosing `<h:form>`. |
| 23 | What is OmniFaces? | A utility library filling common JSF gaps (CSRF handling, JS-to-server calls, caching, helpers). |
| 24 | When to reach for OmniFaces? | When you need non-trivial common features JSF lacks natively — avoid reinventing them. |

---

# PART 2: ✅ Multiple-Choice Quiz

Choose the best answer. Answers with explanations are at the end.

**Q1.** A Save button triggers `<f:ajax render=":messages" />`. The user complains the form fields aren't validated. Why?

- A) `render` should be `@form`
- B) `execute` defaults to `@this`, so the form fields are never processed
- C) Validation is broken
- D) `<f:ajax>` doesn't support validation

---

**Q2.** Which order does JSF apply these checks?

- A) Bean Validation → required → Faces validators
- B) Faces validators → required → Bean Validation
- C) required → Faces validators → Bean Validation
- D) required → Bean Validation → Faces validators

---

**Q3.** An AJAX update targets `id="errorPanel"`, which has `rendered="#{bean.hasErrors}"`. Nothing updates. What's the fix?

- A) Use `@form` in `render`
- B) Make `errorPanel` always rendered and put a conditional child inside it
- C) Change the bean scope to `@RequestScoped`
- D) Add `<h:messages />`

---

**Q4.** Which scope should hold a multi-step, AJAX-driven shopping-cart wizard **across views**?

- A) `@RequestScoped`
- B) `@ViewScoped`
- C) `@SessionScoped` or `@ConversationScoped`
- D) `@ApplicationScoped`

---

**Q5.** A `@ViewScoped` bean works locally but fails in a clustered deployment. Most likely cause?

- A) Missing `@Named`
- B) Missing `Serializable`
- C) Wrong import package
- D) Missing `beans.xml`

---

**Q6.** After a successful form save, refresh shows "Confirm Form Resubmission." The fix?

- A) Add `<f:ajax>`
- B) Change scope to `@SessionScoped`
- C) Return `"outcome?faces-redirect=true"` (PRG)
- D) Add `<h:messages />`

---

**Q7.** You need to load a product from a URL like `/product.xhtml?id=42`. Which tags?

- A) `<f:viewParam>` + `<f:viewAction>`
- B) `<f:ajax>` + `<f:event>`
- C) `<h:inputHidden>` + `@PostConstruct`
- D) `<ui:param>` + `<ui:include>`

---

**Q8.** A getter calls `userService.findById(id)`. Why is this bad?

- A) It breaks CDI
- B) Getters are called multiple times per request, multiplying DB calls
- C) It prevents `Serializable`
- D) It throws exceptions

---

**Q9.** You need robust CSRF handling and a way to call server actions from JavaScript. Best move?

- A) Write it yourself
- B) Reach for OmniFaces (`<o:form>`, `<o:commandScript>`)
- C) Switch to `@RequestScoped`
- D) Use `<c:forEach>`

---

**Q10.** Which component scope is correct for a stateless, non-AJAX "thank you" page?

- A) `@ViewScoped`
- B) `@SessionScoped`
- C) `@RequestScoped`
- D) `@ApplicationScoped`

---

### 🔑 Answer Key

| Q | Answer | Why |
| :--- | :--- | :--- |
| 1 | **B** | `execute` defaults to `@this`; fields must be in `execute="@form"` to be validated. |
| 2 | **C** | `required` → Faces validators → Bean Validation. |
| 3 | **B** | The target must exist in the DOM. Wrap it in an always-rendered parent. |
| 4 | **C** | Multi-step state must outlive a single view — session/conversation scope. |
| 5 | **B** | Clustering/passivation requires `Serializable`. |
| 6 | **C** | PRG via `?faces-redirect=true`. |
| 7 | **A** | `<f:metadata>` with `<f:viewParam>` and `<f:viewAction>`. |
| 8 | **B** | Getters run multiple times per request — DB calls multiply. |
| 9 | **B** | OmniFaces is the idiomatic answer. |
| 10 | **C** | `@RequestScoped` — nothing needs to survive the request. |

---

# PART 3: 💻 Coding Exercise

## 🎯 Scenario

Build a **Product Edit** page. Requirements:

1. URL is bookmarkable: `/product-edit.xhtml?id=42`.
2. The product loads from a service on initial GET **only** — not on every AJAX postback.
3. Save happens via **AJAX**: the form is processed, and a messages area updates in place.
4. On success: show a success message, then redirect to the product list (PRG).
5. Apply all three validation layers (required, faces validator, bean validation).
6. The bean must be safe under clustering.
7. Keep the bean thin — business logic lives in an injected service.

**Write the bean and the view.** Then scroll down to compare.

---

## ✅ Solution

### `ProductEditBean.java`

```java
import jakarta.enterprise.context.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;

@Named
@ViewScoped
public class ProductEditBean implements Serializable {     // ✅ 6: Serializable

    private Long productId;          // set by <f:viewParam>
    private Product product;         // loaded by init()

    @Inject
    ProductService productService;   // ✅ 7: thin bean, service layer injected

    // ✅ 2: runs on initial GET only (viewAction default onPostback=false)
    public void init() {
        if (productId != null) {
            product = productService.findById(productId);
        }
    }

    // ✅ 4: AJAX save, then PRG redirect
    public String save() {
        productService.update(product);
        FacesContext.getCurrentInstance().getExternalContext()
            .getFlash().setKeepMessages(true);   // keep message across redirect
        Messages.addGlobalInfo("Product saved successfully.");
        return "product-list?faces-redirect=true";
    }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
}
```

### `product-edit.xhtml`

```xhtml
<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:h="http://xmlns.jcp.org/jsf/html"
      xmlns:f="http://xmlns.jcp.org/jsf/core"
      xmlns:ui="http://xmlns.jcp.org/jsf/facelets">

<!-- ✅ 1: bookmarkable URL via viewParam + viewAction -->
<f:metadata>
    <f:viewParam name="id" value="#{productEditBean.productId}"
                 required="true"
                 requiredMessage="Product ID missing from URL." />
    <f:viewAction action="#{productEditBean.init}" />
</f:metadata>

<h:body>

    <!-- ✅ 4: messages area updated in place by AJAX, and survives redirect -->
    <h:panelGroup id="messages" layout="block">
        <h:messages globalOnly="false" />
    </h:panelGroup>

    <h:form>
        <!-- ✅ 3: form is processed, messages are re-rendered -->
        <h:commandButton value="Save"
                         action="#{productEditBean.save}">
            <f:ajax execute="@form" render=":messages" />
        </h:commandButton>

        <!-- ✅ 5: three validation layers -->
        <h:outputLabel for="name" value="Name:" />
        <h:inputText id="name"
                     value="#{productEditBean.product.name}"
                     required="true"
                     requiredMessage="Name is required.">
            <f:validateLength minimum="3" maximum="100" />
        </h:inputText>

        <h:inputText value="#{productEditBean.product.price}" />
    </h:form>

</h:body>
</html>
```

### `Product.java` (Bean Validation layer)

```java
public class Product {
    @NotNull @Size(min = 3, max = 100)
    private String name;
    private BigDecimal price;
    // getters & setters
}
```

---

## 🔍 Why This Hits Every Lesson

| Requirement | Lesson |
| :--- | :--- |
| `<f:ajax execute="@form" render=":messages">` | **Lesson 1** — partial update, form processed |
| `<h:messages>` + 3 validation layers | **Lesson 2** — visible feedback, layered rules |
| `?faces-redirect=true` + `viewParam`/`viewAction` | **Lesson 3** — PRG + bookmarkable GET |
| `@ViewScoped` + `Serializable` | **Lesson 4** — correct scope and clustering safety |
| Thin bean, no getter DB calls, `init()` via `viewAction` | **Lesson 5** — pitfalls avoided |
| (Extend with `<o:form>` / `Messages.addGlobalInfo`) | **Lesson 6** — OmniFaces helpers |

---

## 🧪 Try It Yourself

1. **Break it on purpose:** change `render=":messages"` to `render="errorPanel"` where `errorPanel` is inside `rendered="#{false}"`. Observe the silent failure → then fix it by wrapping.
2. **Change scope** to `@RequestScoped` and trigger a validation error via AJAX — watch the loaded product vanish. Restore `@ViewScoped`.
3. **Add an OmniFaces touch:** swap `<h:form>` for `<o:form includeRequestParams="true">` and use `Messages.addGlobalInfo(...)` instead of `FacesContext`.

---

Would you like **another coding exercise** focused on a different scenario (e.g., a search page with `viewParam` + dynamic `render` lists), or a **flashcard set for the next topics** (Facelets templating, `ui:repeat` vs `c:forEach`, CDI events)?



<!-- SOURCE: start.md -->
# File: start.md

Learning JSF and PrimeFaces is a great path for building enterprise Java web applications. Because PrimeFaces is a component library built on top of JSF, the best approach is to learn them **sequentially**—first grasping JSF's core foundation, and then layering PrimeFaces' rich components on top.

Here is a structured, phase-by-phase roadmap to take you from beginner to advanced.

### 🗺️ Phase 1: The Foundation (JSF & Core Java)

Before touching PrimeFaces, you need to understand the underlying framework and its prerequisites.

**Prerequisites**
Ensure you are comfortable with **Java** (especially annotations and generics) and have a basic understanding of **HTML/CSS**. Familiarity with **Maven** is also highly recommended for dependency management.

**Core JSF Concepts to Master**
*   **The JSF Lifecycle**: Understand the six phases: Restore View, Apply Request Values, Process Validations, Update Model Values, Invoke Application, and Render Response. This is crucial for debugging.
*   **Managed Beans & CDI**: Learn how to create backing beans using `@Named` (CDI) or `@ManagedBean`, and understand scopes like `@RequestScoped`, `@ViewScoped`, and `@SessionScoped`.
*   **Facelets (XHTML)**: The view technology for JSF. Learn to use standard JSF tags like `<h:form>`, `<h:inputText>`, and `<h:commandButton>`.
*   **Navigation & Validation**: Understand implicit and explicit navigation rules, and how to use standard validators and converters.
*   **AJAX in JSF**: Learn the `<f:ajax>` tag to perform partial page rendering without writing JavaScript.

**Recommended Resources for Phase 1**
*   **Book**: *Core JavaServer Faces* is considered a definitive guide. The 4th edition covers JSF 2.2 and includes a dedicated chapter on PrimeFaces later, making it a great long-term reference.
*   **Tutorial**: Start with the official Oracle tutorials or Microsoft Learn modules for setting up a basic JSF application.

### 🚀 Phase 2: PrimeFaces Essentials (Beginner to Intermediate)

Once you are comfortable with the JSF lifecycle and basic tags, you can introduce PrimeFaces to supercharge your UI.

**Getting Started**
*   **Setup**: Add the PrimeFaces dependency to your `pom.xml` and configure it in your `web.xml`.
*   **Theming**: Learn how to apply pre-built themes and customize them to change the look and feel of your application.

**Core Components to Learn**
*   **Input & Selection**: Master components like `p:inputText`, `p:calendar`, `p:selectOneMenu`, and `p:autoComplete`.
*   **Buttons & Actions**: Use `p:commandButton` and `p:commandLink` for Ajax-enabled actions.
*   **Data Display**: The **`p:dataTable`** is the workhorse. Learn pagination, sorting, filtering, and row selection.
*   **Panels & Layout**: Use `p:panel`, `p:accordionPanel`, and `p:tabView` to organize content.
*   **Utility Components**: Learn to use `p:growl` for notifications, `p:dialog` for modal windows, and `p:tooltip` for hints.

**Recommended Resources for Phase 2**
*   **Book**: ***PrimeFaces Beginner's Guide*** is excellent for this stage. It walks you through building a sample application ("TechBuzz") incrementally, covering installation, common utility components, client-side validation, and more.
*   **Reference**: The ***PrimeFaces Cookbook*** is a problem-solution guide. You can use it to look up specific recipes for components as you build your first applications.

### ⚡ Phase 3: Advanced Mastery

This phase focuses on building robust, high-performance, and maintainable enterprise applications.

**Advanced Component Usage**
*   **Advanced Data Visualization**: Explore components like `p:tree`, `p:treeTable`, `p:schedule`, and `p:carousel` for complex data representation.
*   **File Handling**: Implement file upload (`p:fileUpload`) and download (`p:fileDownload`) functionality.
*   **Drag & Drop**: Utilize PrimeFaces' built-in drag and drop capabilities for interactive UIs.

**Architectural & Performance Topics**
*   **Search Expression Framework (SEF)**: Go beyond basic `update="id"`. Learn PrimeFaces Selectors (PFS) to reference components using jQuery selectors, which is powerful for complex partial updates.
*   **Performance Optimization**: Learn to correctly define `process` and `update` attributes, use `@ViewScoped` appropriately, and optimize JavaScript/CSS delivery.
*   **Customization**: Understand how to create **custom components** and integrate **PrimeFaces Extensions** for even more functionality.
*   **Security**: Learn about integrating security frameworks (like Spring Security) with JSF and protecting against common web vulnerabilities.

**Recommended Resources for Phase 3**
*   **Book**: ***PrimeFaces Solutions*** is designed for architects and advanced developers. It covers component metadata, dynamic rendering, custom component development, and JavaScript API integration.
*   **Book**: ***PrimeFaces Cookbook (2nd Edition)*** contains advanced recipes on AJAX processing, the Dialog Framework, RequestContext, and more.
*   **Official Docs**: The PrimeFaces GitHub documentation and showcase are essential for deep dives into specific features like the Search Expression Framework and performance tips.

### 💡 Key Tips for Your Learning Journey

*   **Build a Project**: Don't just read—code. Build a simple **CRUD application** (e.g., an Employee Management System) as you learn. This is the fastest way to solidify concepts.
*   **Stick to a Modern Version**: When following tutorials, try to use a recent long-term-support version of JSF (like **JSF 2.3** or **Jakarta Faces 4.0**) and a compatible PrimeFaces version to avoid outdated practices.
*   **Use an IDE**: **NetBeans** and **IntelliJ IDEA** have excellent support for JSF and PrimeFaces, including code completion for tags and attributes.

This roadmap should give you a clear path. If you decide to start with a specific phase and want more detailed resources or have a question about a particular component, feel free to ask. Good luck



<!-- SOURCE: 1.md -->
# File: 1.md

Working with JSON in Java EE (now Jakarta EE) involves a few key approaches. The ecosystem has evolved from needing third-party libraries to having powerful, standardized APIs built directly into the platform.

Here’s a breakdown of the main options, how they differ, and how to use them.

### 🧭 The Landscape: Your Options for JSON in Java EE

There are two main paths you can take, depending on the version of Java/Jakarta EE you are using and your specific needs.

1.  **Standard APIs (Recommended for modern Java EE/Jakarta EE 8+):**
    *   **JSON-B (JSON Binding):** A high-level API for directly mapping Java objects to JSON and back (serialization/deserialization). It's the standard and is integrated into JAX-RS.
    *   **JSON-P (JSON Processing):** A lower-level API for parsing, generating, and manipulating JSON data as a tree (object model) or a stream. It's useful for dynamic or generic JSON structures.

2.  **Third-Party Libraries:**
    *   **Jackson:** A very popular, feature-rich library that serves as the default JSON provider for many Java EE application servers (like JBoss/WildFly).
    *   **Gson:** A simple and widely-used library from Google, particularly good for straightforward serialization and deserialization.

The Servlet API itself does not have built-in JSON support. To use JSON in a plain servlet, you pair it with one of the libraries mentioned above.

### 🚀 Using the Standard Java EE APIs (JSON-B & JSON-P)

These APIs are part of the Jakarta EE specification (formerly Java EE). In a full application server, the implementations are already provided. On a plain servlet container or Java SE, you would need to add an implementation like Eclipse Yasson (for JSON-B) or Eclipse Parsson (for JSON-P).

#### JSON-B: The Object Mapper

JSON-B is the easiest way to work with JSON when you have a fixed, typed Java object.

**Example: Serialize and Deserialize a `User` object**

```java
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;

public class User {
    public String name;
    public int age;
    // A public no-arg constructor is required for deserialization
    public User() {}
}

public class JsonBExample {
    public static void main(String[] args) {
        User user = new User();
        user.name = "Ada";
        user.age = 36;

        // Create a Jsonb instance (it's thread-safe and should be reused)
        try (Jsonb jsonb = JsonbBuilder.create()) {
            // Serialize: Object -> JSON String
            String json = jsonb.toJson(user);
            System.out.println(json); // {"name":"Ada","age":36}

            // Deserialize: JSON String -> Object
            User parsedUser = jsonb.fromJson("{\"name\":\"Ada\",\"age\":36}", User.class);
            System.out.println(parsedUser.name); // Ada
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```
This example is based on the standard usage pattern for JSON-B. You can customize the mapping with annotations like `@JsonbProperty("full_name")` to rename fields or `@JsonbTransient` to exclude them.

#### JSON-P: The Tree and Stream Processor

JSON-P is more flexible for cases where you don't have a corresponding Java class or need to manipulate the JSON structure directly.

**Example: Build and Parse a JSON Object with the Object Model (Tree)**

```java
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import java.io.StringReader;

public class JsonPExample {
    public static void main(String[] args) {
        // Build a JSON object
        JsonObjectBuilder builder = Json.createObjectBuilder()
            .add("firstName", "Michael")
            .add("lastName", "Scott");
        JsonObject jsonObject = builder.build();
        String jsonString = jsonObject.toString();
        System.out.println(jsonString); // {"firstName":"Michael","lastName":"Scott"}

        // Parse a JSON string
        try (var reader = Json.createReader(new StringReader(jsonString))) {
            JsonObject parsedObj = reader.readObject();
            String firstName = parsedObj.getString("firstName");
            System.out.println(firstName); // Michael
        }
    }
}
```
The object model for JSON-P is similar to the DOM API for XML. For very large JSON documents, JSON-P also offers a streaming API (similar to StAX for XML) to process data without loading it all into memory.

### 🔌 Using Third-Party Libraries: Jackson & Gson

These libraries are often used in plain servlets or in environments where you want more control.

#### Jackson

Jackson is known for its performance and extensive feature set.

**Example: Handling JSON in a Servlet with Jackson**

```java
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.Map;

public class UserServlet extends HttpServlet {
    // ObjectMapper is thread-safe and should be reused
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Map<?, ?> body;
        try {
            // Read JSON from the request body
            body = MAPPER.readValue(req.getInputStream(), Map.class);
        } catch (IOException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid JSON");
            return;
        }

        // Set response headers and write JSON
        resp.setStatus(HttpServletResponse.SC_CREATED);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        MAPPER.writeValue(resp.getOutputStream(), Map.of("received", body.size()));
    }
}
```
This pattern is recommended for servlets: read directly from the request's `InputStream` and write to the response's `OutputStream`, letting Jackson handle the encoding.

#### Gson

Gson is often praised for its simplicity.

**Example: Returning a JSON Response from a Servlet with Gson**

```java
import com.google.gson.Gson;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;

public class EmployeeServlet extends HttpServlet {
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Employee employee = new Employee(1, "Karan", "IT", 5000);
        String employeeJsonString = this.gson.toJson(employee);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        out.print(employeeJsonString);
        out.flush();
    }
}
```
This is a standard, straightforward approach for sending a JSON response from a servlet using Gson.

### ✨ Modern Best Practices: Let JAX-RS Do the Work

If you are building RESTful web services with JAX-RS (the standard REST API for Java EE), you rarely need to manually call a JSON library. The runtime handles serialization and deserialization for you.

By declaring the media types, you can use your Java objects directly as method parameters and return types.

```java
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/users")
public class UserResource {

    @POST
    @Consumes(MediaType.APPLICATION_JSON) // Expect JSON in the request body
    @Produces(MediaType.APPLICATION_JSON) // Return JSON in the response
    public Response create(User user) {
        // The 'user' object is automatically deserialized from the request JSON
        // The returned object will be automatically serialized to JSON
        return Response.status(Response.Status.CREATED).entity(user).build();
    }
}
```
With this approach, the JAX-RS runtime (using a provider like JSON-B or Jackson) transparently handles the conversion. Malformed JSON is typically rejected with a 400 Bad Request before your method is even called.

### 💎 Key Takeaways and Common Pitfalls

*   **Choose the right tool:** Use **JSON-B** (or Jackson/Gson) for mapping to typed objects. Use **JSON-P** for dynamic or streaming processing. In **JAX-RS**, rely on automatic content negotiation.
*   **Reuse instances:** Creating `Jsonb`, `ObjectMapper`, or `Gson` instances is expensive. Create them once (e.g., as a `static final` field or a CDI bean) and reuse them.
*   **Ensure a default constructor:** Classes deserialized by JSON-B or Jackson generally need a public no-argument constructor.
*   **Mind the namespace:** Jakarta EE 9+ uses `jakarta.*` packages (e.g., `jakarta.json`), replacing the older `javax.*` namespaces. Ensure your imports match your server version.
*   **Handle errors gracefully:** When parsing JSON in a servlet, catch `IOException` and return a `400 Bad Request` for invalid input instead of letting a `500 Server Error` occur.

Hopefully, this gives you a solid foundation for working with JSON in Java EE. If you want to dive deeper into a specific API or see a particular use case in more detail, feel free to ask.



<!-- SOURCE: 2.md -->
# File: 2.md

Jackson's annotations are the key to controlling how your Java objects are converted to and from JSON. They give you fine-grained control over the mapping process, letting you rename fields, ignore sensitive data, format dates, and much more. Here's a guide to the most important ones, grouped by what they do.

### 📝 Field Mapping and Renaming

These annotations control how Java fields correspond to JSON property names.

- **`@JsonProperty`**: This is the most fundamental annotation. It maps a JSON property name to a Java field when they don't match exactly. It's also used to ensure a property is included in serialization/deserialization, especially when you have custom getters/setters.
  ```java
  public class User {
      @JsonProperty("full_name")
      private String name; // Mapped to "full_name" in JSON
  }
  ```
- **`@JsonAlias`**: This annotation makes deserialization more flexible by allowing a single Java field to accept multiple different JSON property names.
  ```java
  public class UserDTO {
      @JsonAlias({"fullName", "user_name"})
      private String name; // Accepts "name", "fullName", or "user_name" from JSON
  }
  ```

### 🚫 Excluding and Including Fields

These are used for security, reducing payload size, or handling unexpected data.

- **`@JsonIgnore`**: Use this to completely exclude a field from both serialization and deserialization. It's perfect for hiding sensitive data like passwords.
  ```java
  public class User {
      private String name;
      @JsonIgnore
      private String password; // This will never appear in JSON
  }
  ```
- **`@JsonIgnoreProperties`**: When placed on a class, this annotation tells Jackson to ignore any JSON properties that don't have a corresponding field in the Java class. This is extremely useful for avoiding errors when the JSON payload contains extra, unmapped fields.
  ```java
  @JsonIgnoreProperties(ignoreUnknown = true)
  public class Customer {
      private String id;
      private String name;
      // Extra fields in JSON will be ignored instead of causing an error
  }
  ```
- **`@JsonInclude`**: This controls which properties are included during serialization. A common use is `JsonInclude.Include.NON_NULL`, which omits fields with `null` values from the output, leading to cleaner JSON.
  ```java
  @JsonInclude(JsonInclude.Include.NON_NULL)
  public class Product {
      private String name;
      private String description; // Omitted from JSON if null
  }
  ```

### 📅 Formatting Values

- **`@JsonFormat`**: This is essential for formatting date, time, and number values. You can specify a `pattern` (like `"yyyy-MM-dd HH:mm:ss"`) and a `shape` to control the output format precisely.
  ```java
  public class Event {
      @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
      private LocalDateTime dateTime;
  }
  ```

### 🏗️ Customizing Object Creation

- **`@JsonCreator` + `@JsonProperty`**: When a class doesn't have a default no-argument constructor (e.g., it uses `final` fields), you can use `@JsonCreator` to mark a specific constructor or static factory method for Jackson to use during deserialization. You then use `@JsonProperty` on the constructor's parameters to map them to the JSON fields.
  ```java
  public class Account {
      private final String id;
      private final String type;

      @JsonCreator
      public Account(@JsonProperty("id") String id,
                     @JsonProperty("type") String type) {
          this.id = id;
          this.type = type;
      }
  }
  ```

### ⚙️ Advanced Control and Custom Logic

These annotations are for more complex scenarios.

- **`@JsonValue`**: This tells Jackson to serialize the entire object as the value returned by a single method. It's very commonly used to customize how an `enum` is represented in JSON.
  ```java
  public enum Status {
      ACTIVE("active"), INACTIVE("inactive");

      private final String value;
      Status(String value) { this.value = value; }

      @JsonValue
      public String getValue() { return value; }
  }
  ```
- **`@JsonAnySetter` / `@JsonAnyGetter`**: These are useful for handling dynamic or unknown JSON properties. `@JsonAnySetter` catches any unmapped fields during deserialization into a `Map`, while `@JsonAnyGetter` serializes the contents of a `Map` as top-level JSON properties.
- **`@JsonUnwrapped`**: This flattens a nested object's properties into the parent object's JSON structure, which is useful when you want to avoid deep nesting.
- **`@JsonDeserialize` / `@JsonSerialize`**: For ultimate control, you can specify a custom serializer or deserializer class for a specific field using these annotations.

### 💎 Key Takeaways and Best Practices

- **Reuse `ObjectMapper`**: The `ObjectMapper` is the main class for Jackson. It's expensive to create, so instantiate it once (e.g., as a `static final` field) and reuse it throughout your application.
- **`@JsonIgnoreProperties(ignoreUnknown = true)`**: Adding this to your DTOs is a robust practice that makes your API more resilient to changes in the JSON payload from clients.
- **Jackson in Java EE/JAX-RS**: In a JAX-RS application, you often don't need to call Jackson directly. If you configure it as your JSON provider, simply annotating your methods with `@Produces(MediaType.APPLICATION_JSON)` and `@Consumes(MediaType.APPLICATION_JSON)` allows the runtime to handle serialization/deserialization automatically.
- **Annotation Placement**: The examples above place annotations on fields. However, in Java, placing them on the getter methods is also very common and often preferred for clarity.

This covers the most frequently used Jackson annotations. If you'd like to see a deeper example of a specific annotation, such as custom serializers with `@JsonSerialize`, feel free to ask.



<!-- SOURCE: 3.md -->
# File: 3.md

# 🛒 Project: "Order Management API"

A small, end‑to‑end REST API that exercises **every Jackson feature** we covered: immutability via `@JsonCreator`, enum handling, polymorphic payments, custom (de)serializers, date formatting, field hiding, unknown‑property tolerance, and a plain‑servlet endpoint for dynamic payloads.

Deploy target: **Payara 6 / GlassFish 7 (Jersey)**. Notes for WildFly/RESTEasy appear at the end.

---

## 📁 Project structure

```
order-api/
├── pom.xml
└── src/main/java/com/example/orders/
    ├── domain/
    │   ├── Order.java
    │   ├── Customer.java
    │   ├── OrderItem.java
    │   ├── Money.java
    │   ├── OrderStatus.java
    │   ├── PaymentMethod.java
    │   ├── CreditCardPayment.java
    │   └── PayPalPayment.java
    ├── dto/
    │   ├── CreateOrderRequest.java
    │   ├── OrderResponse.java
    │   └── ErrorResponse.java
    ├── json/
    │   ├── MoneySerializer.java
    │   └── SkuDeserializer.java
    ├── config/JacksonConfig.java
    ├── exception/ValidationException.java
    ├── service/OrderService.java
    └── web/
        ├── JaxRsApplication.java
        ├── OrderResource.java
        ├── ApiExceptionMapper.java
        ├── WebhookEvent.java
        └── WebhookServlet.java
```

---

## 1. `pom.xml`

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0">
  <modelVersion>4.0.0</modelVersion>
  <groupId>com.example</groupId>
  <artifactId>order-api</artifactId>
  <version>1.0.0</version>
  <packaging>war</packaging>

  <properties>
    <maven.compiler.source>17</maven.compiler.source>
    <maven.compiler.target>17</maven.compiler.target>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
  </properties>

  <dependencies>
    <!-- Jakarta EE 10 (provided by the app server) -->
    <dependency>
      <groupId>jakarta.platform</groupId>
      <artifactId>jakarta.jakartaee-api</artifactId>
      <version>10.0.0</version>
      <scope>provided</scope>
    </dependency>

    <!-- Jersey's Jackson provider (bundled in Payara/GlassFish; explicit for clarity) -->
    <dependency>
      <groupId>org.glassfish.jersey.media</groupId>
      <artifactId>jersey-media-json-jackson</artifactId>
      <version>3.1.3</version>
    </dependency>

    <!-- Required for Instant / LocalDateTime support -->
    <dependency>
      <groupId>com.fasterxml.jackson.datatype</groupId>
      <artifactId>jackson-datatype-jsr310</artifactId>
      <version>2.15.2</version>
    </dependency>
  </dependencies>

  <build>
    <finalName>order-api</finalName>
  </build>
</project>
```

---

## 2. Domain model

### `domain/OrderStatus.java` — enum round‑trip with `@JsonValue` / `@JsonCreator`

```java
package com.example.orders.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

public enum OrderStatus {
    PENDING("pending"),
    PAID("paid"),
    SHIPPED("shipped"),
    CANCELLED("cancelled");

    private final String code;

    OrderStatus(String code) { this.code = code; }

    @JsonValue                        // serialize as "pending", not "PENDING"
    public String getCode() { return code; }

    @JsonCreator                      // "pending" -> PENDING
    public static OrderStatus fromCode(String code) {
        return Arrays.stream(values())
                .filter(s -> s.code.equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown status: " + code));
    }
}
```

### `domain/Money.java` — custom serialization

```java
package com.example.orders.domain;

import com.example.orders.json.MoneySerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.math.BigDecimal;

@JsonSerialize(using = MoneySerializer.class)
public class Money {
    private final BigDecimal amount;
    private final String currency;

    public Money(BigDecimal amount, String currency) {
        this.amount = amount;
        this.currency = currency;
    }

    public BigDecimal getAmount() { return amount; }
    public String getCurrency() { return currency; }
}
```

### `domain/Customer.java` — immutable, `@JsonCreator` constructor

```java
package com.example.orders.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Customer {
    private final String id;
    private final String name;
    private final String email;

    @JsonCreator
    public Customer(@JsonProperty("id")    String id,
                    @JsonProperty("name")  String name,
                    @JsonProperty("email") String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public String getId()    { return id; }
    public String getName()  { return name; }
    public String getEmail() { return email; }
}
```

### `domain/OrderItem.java` — field‑level custom deserializer (validates SKU)

```java
package com.example.orders.domain;

import com.example.orders.json.SkuDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.math.BigDecimal;

public class OrderItem {
    @JsonDeserialize(using = SkuDeserializer.class)
    private String sku;

    private int quantity;
    private BigDecimal unitPrice;

    public OrderItem() {}

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
}
```

### `domain/PaymentMethod.java` + subclasses — polymorphism

```java
package com.example.orders.domain;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = CreditCardPayment.class, name = "credit_card"),
    @JsonSubTypes.Type(value = PayPalPayment.class,     name = "paypal")
})
public abstract class PaymentMethod {
    public abstract String describe();
}
```

```java
package com.example.orders.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CreditCardPayment extends PaymentMethod {
    private String last4;

    // Accepted from the client but NEVER written back to the response.
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String cardNumber;

    public String getLast4() { return last4; }
    public void setLast4(String last4) { this.last4 = last4; }

    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }

    @Override
    public String describe() { return "Credit card ending in " + last4; }
}
```

```java
package com.example.orders.domain;

public class PayPalPayment extends PaymentMethod {
    private String email;

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    @Override
    public String describe() { return "PayPal account " + email; }
}
```

### `domain/Order.java` — plain domain, no Jackson annotations

```java
package com.example.orders.domain;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Order {
    private final String id;
    private final Customer customer;
    private final List<OrderItem> items;
    private final PaymentMethod paymentMethod;
    private final Instant createdAt;
    private final OrderStatus status;

    public Order(String id, Customer customer, List<OrderItem> items,
                 PaymentMethod paymentMethod, Instant createdAt, OrderStatus status) {
        this.id = id;
        this.customer = customer;
        this.items = items == null ? new ArrayList<>() : new ArrayList<>(items);
        this.paymentMethod = paymentMethod;
        this.createdAt = createdAt;
        this.status = status;
    }

    public String getId() { return id; }
    public Customer getCustomer() { return customer; }
    public List<OrderItem> getItems() { return items; }
    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public Instant getCreatedAt() { return createdAt; }
    public OrderStatus getStatus() { return status; }
}
```

> **Design note:** keeping the entity annotation‑free enforces the **DTO boundary** and shows why annotations belong on the wire classes.

---

## 3. Custom (de)serializers

### `json/MoneySerializer.java`

```java
package com.example.orders.json;

import com.example.orders.domain.Money;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class MoneySerializer extends JsonSerializer<Money> {
    @Override
    public void serialize(Money money, JsonGenerator gen, SerializerProvider provider)
            throws IOException {
        gen.writeStartObject();
        gen.writeStringField("amount", money.getAmount().toPlainString());
        gen.writeStringField("currency", money.getCurrency());
        gen.writeEndObject();
    }
}
```

### `json/SkuDeserializer.java`

```java
package com.example.orders.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import java.io.IOException;
import java.util.regex.Pattern;

public class SkuDeserializer extends JsonDeserializer<String> {
    private static final Pattern SKU = Pattern.compile("^SKU-[A-Z0-9]{4}$");

    @Override
    public String deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
        String value = p.getText();
        if (value == null || !SKU.matcher(value).matches()) {
            throw InvalidFormatException.from(p,
                "Invalid SKU format. Expected 'SKU-' followed by 4 uppercase alphanumerics.",
                value, String.class);
        }
        return value;
    }
}
```

---

## 4. DTOs

### `dto/CreateOrderRequest.java` — tolerates unknown fields

```java
package com.example.orders.dto;

import com.example.orders.domain.Customer;
import com.example.orders.domain.OrderItem;
import com.example.orders.domain.PaymentMethod;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateOrderRequest {
    private Customer customer;
    private List<OrderItem> items;
    private PaymentMethod payment;    // polymorphic!
    private Map<String, String> metadata;

    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }

    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }

    public PaymentMethod getPayment() { return payment; }
    public void setPayment(PaymentMethod payment) { this.payment = payment; }

    public Map<String, String> getMetadata() { return metadata; }
    public void setMetadata(Map<String, String> metadata) { this.metadata = metadata; }
}
```

### `dto/OrderResponse.java` — formatted, `NON_NULL`, hides computed fields

```java
package com.example.orders.dto;

import com.example.orders.domain.Money;
import com.example.orders.domain.OrderStatus;
import com.example.orders.domain.PaymentMethod;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderResponse {
    private final String id;
    private final String customerId;
    private final OrderStatus status;
    private final Money total;

    @JsonFormat(shape = JsonFormat.Shape.STRING,
                pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'",
                timezone = "UTC")
    private final Instant createdAt;

    private final PaymentMethod payment;

    public OrderResponse(String id, String customerId, OrderStatus status,
                         Money total, Instant createdAt, PaymentMethod payment) {
        this.id = id;
        this.customerId = customerId;
        this.status = status;
        this.total = total;
        this.createdAt = createdAt;
        this.payment = payment;
    }

    public String getId() { return id; }
    public String getCustomerId() { return customerId; }
    public OrderStatus getStatus() { return status; }
    public Money getTotal() { return total; }
    public Instant getCreatedAt() { return createdAt; }
    public PaymentMethod getPayment() { return payment; }

    @JsonIgnore                       // debug helper, never serialized
    public String getSummary() { return id + " / " + status; }
}
```

### `dto/ErrorResponse.java`

```java
package com.example.orders.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    private final String code;
    private final String message;

    public ErrorResponse(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() { return code; }
    public String getMessage() { return message; }
}
```

---

## 5. The most important file — `config/JacksonConfig.java`

This tells **Jersey's Jackson provider** how to configure the shared `ObjectMapper` (Java 8 date support, etc.). Without it, `Instant` serialization fails.

```java
package com.example.orders.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.ext.ContextResolver;
import jakarta.ws.rs.ext.Provider;

@Provider
@Produces(MediaType.APPLICATION_JSON)
public class JacksonConfig implements ContextResolver<ObjectMapper> {
    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())                   // Instant / LocalDateTime
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @Override
    public ObjectMapper getContext(Class<?> type) {
        return mapper;
    }
}
```

---

## 6. Service layer (in‑memory) — `service/OrderService.java`

```java
package com.example.orders.service;

import com.example.orders.domain.*;
import com.example.orders.dto.CreateOrderRequest;
import com.example.orders.dto.OrderResponse;
import com.example.orders.exception.ValidationException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class OrderService {
    private static final OrderService INSTANCE = new OrderService();
    public static OrderService getInstance() { return INSTANCE; }

    private final Map<String, Order> store = new ConcurrentHashMap<>();

    public OrderResponse create(CreateOrderRequest req) {
        if (req.getCustomer() == null) throw new ValidationException("customer is required");
        if (req.getItems() == null || req.getItems().isEmpty())
            throw new ValidationException("items must not be empty");
        if (req.getPayment() == null) throw new ValidationException("payment is required");

        String id = "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Order order = new Order(id, req.getCustomer(), req.getItems(),
                req.getPayment(), Instant.now(), OrderStatus.PENDING);
        store.put(id, order);
        return toResponse(order);
    }

    public OrderResponse find(String id) {
        Order o = store.get(id);
        return o == null ? null : toResponse(o);
    }

    private OrderResponse toResponse(Order o) {
        BigDecimal sum = o.getItems().stream()
                .map(i -> i.getUnitPrice() == null ? BigDecimal.ZERO
                          : i.getUnitPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new OrderResponse(o.getId(), o.getCustomer().getId(), o.getStatus(),
                new Money(sum, "USD"), o.getCreatedAt(), o.getPaymentMethod());
    }
}
```

---

## 7. Web layer

### `exception/ValidationException.java`

```java
package com.example.orders.exception;

public class ValidationException extends RuntimeException {
    public ValidationException(String message) { super(message); }
}
```

### `web/JaxRsApplication.java`

```java
package com.example.orders.web;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

@ApplicationPath("/api")
public class JaxRsApplication extends Application {}
```

### `web/OrderResource.java` — the JAX‑RS endpoint

```java
package com.example.orders.web;

import com.example.orders.dto.CreateOrderRequest;
import com.example.orders.dto.OrderResponse;
import com.example.orders.service.OrderService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.net.URI;

@Path("/orders")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OrderResource {

    private final OrderService service = OrderService.getInstance();

    @POST
    public Response create(CreateOrderRequest request) {
        OrderResponse created = service.create(request);
        return Response.created(URI.create("/api/orders/" + created.getId()))
                .entity(created)
                .build();
    }

    @GET
    @Path("/{id}")
    public Response get(@PathParam("id") String id) {
        OrderResponse found = service.find(id);
        return found == null
                ? Response.status(Response.Status.NOT_FOUND).build()
                : Response.ok(found).build();
    }
}
```

### `web/ApiExceptionMapper.java` — converts Jackson errors to friendly JSON

```java
package com.example.orders.web;

import com.example.orders.dto.ErrorResponse;
import com.example.orders.exception.ValidationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ApiExceptionMapper implements ExceptionMapper<Throwable> {

    @Override
    public Response toResponse(Throwable ex) {
        Throwable cursor = ex;
        while (cursor != null) {
            if (cursor instanceof ValidationException ve) {
                return error(400, "validation_error", ve.getMessage());
            }
            if (cursor instanceof InvalidFormatException ife) {
                return error(400, "invalid_format", ife.getOriginalMessage());
            }
            if (cursor instanceof JsonMappingException jme) {
                String msg = jme.getOriginalMessage() != null
                        ? jme.getOriginalMessage() : jme.getMessage();
                return error(400, "invalid_json", msg);
            }
            cursor = cursor.getCause();
        }
        return error(500, "internal_error", "An unexpected error occurred");
    }

    private Response error(int status, String code, String message) {
        return Response.status(status)
                .entity(new ErrorResponse(code, message))
                .build();
    }
}
```

---

## 8. Bonus — a plain servlet with `@JsonAnySetter` / `@JsonAnyGetter`

This demonstrates a **webhook echo** endpoint that accepts *any* JSON shape — perfect for third‑party webhooks.

### `web/WebhookEvent.java`

```java
package com.example.orders.web;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;

import java.util.LinkedHashMap;
import java.util.Map;

public class WebhookEvent {
    private final Map<String, Object> fields = new LinkedHashMap<>();

    @JsonAnySetter
    public void put(String key, Object value) { fields.put(key, value); }

    @JsonAnyGetter
    public Map<String, Object> getFields() { return fields; }
}
```

### `web/WebhookServlet.java`

```java
package com.example.orders.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

@WebServlet("/webhook")
public class WebhookServlet extends HttpServlet {

    // A single, reused mapper — never create one per request.
    private static final ObjectMapper MAPPER =
            new ObjectMapper().registerModule(new JavaTimeModule());

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        try {
            WebhookEvent event = MAPPER.readValue(req.getInputStream(), WebhookEvent.class);
            MAPPER.writeValue(resp.getOutputStream(), event);
        } catch (IOException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            MAPPER.writeValue(resp.getOutputStream(), Map.of("error", "invalid JSON"));
        }
    }
}
```

---

## 9. Build, deploy, and exercise

```bash
cd code
mvn clean package
# deploy target/order-api.war to Payara 6 / GlassFish 7
# base URL: http://localhost:8080/order-api
```

### ① Create an order (valid)

```bash
curl -i -X POST http://localhost:8080/order-api/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customer": { "id": "CUST-001", "name": "Ada Lovelace", "email": "ada@example.com" },
    "items": [
      { "sku": "SKU-AB12", "quantity": 2, "unitPrice": 19.99 },
      { "sku": "SKU-ZX99", "quantity": 1, "unitPrice": 5.00 }
    ],
    "payment": {
      "type": "credit_card",
      "last4": "4242",
      "cardNumber": "4242424242424242"
    },
    "metadata": { "source": "mobile-app" }
  }'
```

**201 Created** — notice *what's missing*: `cardNumber` and `summary` are gone, `metadata` was ignored (not mapped to the response).

```json
{
  "id": "ORD-3F9A12BC",
  "customerId": "CUST-001",
  "status": "pending",
  "total": { "amount": "44.98", "currency": "USD" },
  "createdAt": "2026-11-15T12:34:56Z",
  "payment": { "type": "credit_card", "last4": "4242" }
}
```

### ② Same request, but with a bad SKU

```bash
curl -i -X POST http://localhost:8080/order-api/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customer": { "id": "CUST-001", "name": "Ada", "email": "a@b.c" },
    "items": [{ "sku": "BAD", "quantity": 1, "unitPrice": 1.00 }],
    "payment": { "type": "paypal", "email": "a@b.c" }
  }'
```

**400 Bad Request** — the custom `SkuDeserializer` error is surfaced by the exception mapper:

```json
{
  "code": "invalid_format",
  "message": "Invalid SKU format. Expected 'SKU-' followed by 4 uppercase alphanumerics."
}
```

### ③ Unknown fields are tolerated

Add `"nickname": "admin"` to the body — thanks to `@JsonIgnoreProperties(ignoreUnknown = true)` the call still succeeds with **201**.

### ④ Retrieve the order

```bash
curl http://localhost:8080/order-api/api/orders/ORD-3F9A12BC
```

### ⑤ Webhook echo (dynamic payload)

```bash
curl -X POST http://localhost:8080/order-api/webhook \
  -H "Content-Type: application/json" \
  -d '{"event":"order.paid","data":{"id":"ORD-1"},"timestamp":"2026-11-15T10:00:00Z"}'
```

Returns the **same structure** — `@JsonAnySetter` captured every key into the map, and `@JsonAnyGetter` flattened them back out.

---

## 10. Cheat‑sheet: what each annotation is doing in this project

| Annotation | Where | Effect demonstrated |
|---|---|---|
| `@JsonProperty` | `Customer` ctor | maps JSON → final fields |
| `@JsonCreator` | `Customer` ctor, `OrderStatus.fromCode` | immutable deserialization |
| `@JsonValue` | `OrderStatus.getCode` | enum → `"pending"` |
| `@JsonProperty(access = WRITE_ONLY)` | `CreditCardPayment.cardNumber` | accepted in, hidden out |
| `@JsonIgnore` | `OrderResponse.getSummary` | method never serialized |
| `@JsonInclude(NON_NULL)` | `OrderResponse`, `ErrorResponse` | `null` fields omitted |
| `@JsonFormat` | `OrderResponse.createdAt` | `"2026-11-15T12:34:56Z"` |
| `@JsonIgnoreProperties(ignoreUnknown = true)` | `CreateOrderRequest` | tolerant of extra client fields |
| `@JsonDeserialize(using = …)` | `OrderItem.sku` | custom validation |
| `@JsonSerialize(using = …)` | `Money` | custom object shape |
| `@JsonTypeInfo` + `@JsonSubTypes` | `PaymentMethod` | polymorphic `type` discriminator |
| `@JsonAnySetter` / `@JsonAnyGetter` | `WebhookEvent` | dynamic webhook payloads |
| `ContextResolver<ObjectMapper>` | `JacksonConfig` | register `JavaTimeModule` once |

---

## ⚠️ Key takeaways and pitfalls

- **`JavaTimeModule` is not optional.** Without registering it (as in `JacksonConfig`), `Instant`/`LocalDateTime` throw `InvalidDefinitionException`. This is the #1 cause of Jackson failures in Java EE.
- **Reuse the `ObjectMapper`.** Create one per application (config class or `static final`). Never `new ObjectMapper()` inside a request handler.
- **`WRITE_ONLY` vs `@JsonIgnore`.** `WRITE_ONLY` means *input yes, output no* — correct for passwords, card numbers, secrets. `@JsonIgnore` means *neither* — correct for purely internal fields.
- **Creator binding requires `@JsonProperty` on params** unless you compile with `-parameters` and register `ParameterNamesModule`. Annotate explicitly to be safe.
- **Polymorphic deserialization requires the discriminator.** Miss `@JsonSubTypes` and you get `InvalidTypeIdException`. For *user‑supplied* JSON, never enable `activateDefaultTyping` (remote‑code‑execution risk).
- **`FAIL_ON_UNKNOWN_PROPERTIES` defaults to `true`.** Either annotate your DTOs with `@JsonIgnoreProperties(ignoreUnknown = true)` or configure it globally — both are fine, but be consistent.
- **Jersey vs. RESTEasy.** This project targets Jersey (`jersey-media-json-jackson`). On **WildFly**, RESTEasy's Jackson provider is bundled; drop the `jersey-*` dependency, keep `jackson-datatype-jsr310`, and `JacksonConfig` works unchanged.
- **On Tomcat**, add `jersey-container-servlet` + `jersey-cdi2-se` (or use a Java EE server) to get `@Inject`/CDI integration in resources.

---

If you want to extend this project, the natural next steps are **Bean Validation** (`@NotNull`, `@Valid` on the request DTO), **Jackson's `@JsonView`** for role‑based field visibility, or **`@JsonFilter`** for dynamic response shaping. Just say the word and I'll add them.


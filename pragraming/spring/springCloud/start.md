Here is the same Spring Cloud learning roadmap in English.

# 🗺️ Step-by-Step Spring Cloud Learning Roadmap

Learning Spring Cloud is a gradual process. The key is to build a solid foundation first, then master the core components one by one, and finally connect everything through a complete hands-on project.

## Prerequisites: Build Your Foundation

Before starting Spring Cloud, you must first master the following. Otherwise, later learning will be very difficult.

- **Java fundamentals**: Be comfortable with Java.
- **Spring Boot [mandatory]**: Spring Cloud is built on top of Spring Boot. You need to be able to build web applications with Spring Boot and understand core concepts such as **auto-configuration, starters, and Actuator**.
- **Databases and caching [recommended]**: Basic knowledge of MySQL and Redis will be helpful for the final project.

---

## Phase 1: Understand Microservices and Build the Project Skeleton

**Goal**: Understand “why microservices” and create your first runnable project structure.

1. **Understand microservice architecture**: First understand what microservices are, along with their advantages and disadvantages. Think of it as splitting one large kitchen (a monolithic application) into independent small departments—one for fried rice, one for hot pot, etc. Each department operates independently and collaborates through “food delivery robots” (service communication). This enables fault isolation and independent scaling. But remember: **microservices ≠ split everything infinitely**. Splitting too finely increases communication and deployment complexity.

2. **Create a parent project**: Use Maven with `pom` packaging to create a parent project. Manage Spring Cloud and Spring Boot versions centrally in `dependencyManagement`. This parent project acts as the “family tree” for all submodules.

---

## Phase 2: Master Core Components One by One

This is the main part of learning. Follow the order below and practice by coding each component.

### 1. Service Registry and Discovery

- **Core idea**: After all microservices start, they register themselves with a “registry” (like a household registration office). When one service needs to call another, it looks up the other service’s location there.
- **Recommended technologies**: **Nacos** (Spring Cloud Alibaba) or **Eureka** (Spring Cloud Netflix). For developers in China, **Nacos is strongly recommended** because its documentation is complete, its community is active, and the Netflix stack is in maintenance mode.
- **Practice task**: Set up a registry and successfully register one service with it.

### 2. Service Invocation and Load Balancing

- **Core idea**: How services “call” each other elegantly. **Feign** (now OpenFeign) provides a declarative HTTP client, allowing you to call remote services like local methods without manually building URLs. **Spring Cloud LoadBalancer** distributes requests across multiple service instances.
- **Recommended technologies**: **OpenFeign** + **Spring Cloud LoadBalancer**.
- **Practice task**: Make Service A successfully call an endpoint in Service B through a Feign client.

### 3. API Gateway

- **Core idea**: A unified entry point for microservices. All external requests first go through the gateway, which handles routing, authentication, rate limiting, etc., hiding the complexity of internal services.
- **Recommended technology**: **Spring Cloud Gateway**.
- **Practice task**: Configure a Gateway to route requests to `/user/**` to the user service.

### 4. Configuration Center

- **Core idea**: Centrally manage configuration files for all microservices and support dynamic refresh, so configurations can be updated without restarting services.
- **Recommended technologies**: **Nacos Config** (can share the same Nacos instance as the registry) or **Spring Cloud Config**.
- **Practice task**: Move a service’s configuration (such as database connection settings) to the configuration center and experience dynamic updates.

### 5. Circuit Breaking and Rate Limiting

- **Core idea**: When a service fails or responds too slowly, callers should fail fast (circuit breaking) to prevent cascading failures from bringing down the whole system, and provide fallback logic (such as returning a default value).
- **Recommended technologies**: **Sentinel** (powerful, with a console) or **Resilience4j**.
- **Practice task**: Configure circuit-breaking rules for a service endpoint, simulate a service failure, and observe whether the fallback logic works.

---

## Phase 3: Advanced Topics and Hands-On Practice

After learning the core components, you need to connect them through a complete project.

- **Technology selection advice**: **Prioritize the Spring Cloud Alibaba ecosystem** (Nacos, Sentinel, etc.). This is currently the mainstream choice in China and fits real production environments better.
- **Recommended hands-on projects**:
    - **Build a demo from scratch**: Follow a tutorial, start from creating the parent project, and integrate Eureka/Nacos, Gateway, OpenFeign, and Resilience4j step by step to build a minimal usable microservice system.
    - **Complete e-commerce project**: Find a complete e-commerce project based on Spring Cloud Alibaba and experience more advanced topics such as service decomposition, distributed transactions (Seata), and distributed tracing (Sleuth + Zipkin).
- **Deployment and operations**: Learn how to containerize your microservices with **Docker** and deploy them with one command using **Docker Compose**. This is an essential skill for modern microservice development.

---

## 💎 Summary and Learning Advice

1. **Learn Spring Boot first, then Spring Cloud**: This step cannot be skipped.
2. **Prefer Spring Cloud Alibaba**: For learning and job hunting in China, this is the more practical choice.
3. **Learn through projects**: Do not stop at watching videos and coding single-component demos. **You must build a complete project with multiple services**. This is the key to understanding the essence of microservice architecture.
4. **Understand the ideas behind each component**: When learning each component, think more about what specific problem it solves in microservice architecture—for example, how services find each other, how configuration is managed centrally, and how failures are isolated. This is more important than memorizing configuration items.

This roadmap provides a clear framework. You can learn in phases at your own pace. If you get stuck on a specific component, we can discuss it in more depth.
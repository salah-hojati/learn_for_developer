Learning Spring Security is best approached in layers, starting from its core philosophy and gradually moving toward complex, real-world implementations. Since you asked for a multi-section guide, here is a structured roadmap that takes you from the fundamentals to advanced topics like JWT and OAuth2.

### 🧭 Section 1: The Foundation & Core Concepts
Before writing code, it is crucial to understand the vocabulary and the flow. Spring Security is a powerful, highly customizable framework that is the de-facto standard for securing Spring-based applications.

*   **The 5 Core Concepts**: You must distinguish between these five terms, as they form the basis of every configuration:
    1.  **Authentication**: *Who are you?* (Verifying identity).
    2.  **Authorization**: *What are you allowed to do?* (Granting permissions).
    3.  **Principal**: The currently logged-in user.
    4.  **Authority**: A fine-grained permission (e.g., `READ_PRIVILEGES`).
    5.  **Role**: A coarse-grained group of authorities (e.g., `ROLE_ADMIN`).
*   **The Architecture (The Flow)**: Spring Security operates via a **Filter Chain**. When a request comes in, it is intercepted. The `AuthenticationManager` delegates the actual verification to an `AuthenticationProvider`, which then uses a `UserDetailsService` to load user data and verify the password.

### 🛠️ Section 2: Getting Started & Basic Configuration
The fastest way to learn is to secure a simple application.

*   **Add the Dependency**: The simplest way to start is by adding `spring-boot-starter-security` to your project using Spring Initializr or your build tool (Maven/Gradle).
*   **The Default Behavior**: Once added, restart your application. You will immediately notice that all endpoints are now protected, and you are redirected to a default login page. The console will print a randomly generated password for the user `user`.
*   **Modern Configuration (Spring Security 6+)**: In modern versions, you configure security using a `SecurityFilterChain` bean instead of the deprecated `WebSecurityConfigurerAdapter`. You will learn to define rules for `HttpSecurity`, such as permitting all requests to `/public` but requiring authentication for everything else.

### 🔑 Section 3: Authentication (Verifying Identity)
This section moves beyond the default password to handling your own users.

*   **In-Memory Authentication**: For quick prototypes or tests, you can define users directly in your configuration class using an `InMemoryUserDetailsManager`. You will create `UserDetails` objects with usernames, encoded passwords, and roles.
*   **Database Authentication**: For real applications, you need to store users in a database. You will learn to implement a custom `UserDetailsService` that queries your database (using JDBC or JPA) to load the user's credentials and authorities.
*   **Password Encoding**: Never store passwords in plain text. You must use a `PasswordEncoder` (like `BCryptPasswordEncoder`) to hash passwords before saving them and to verify the raw password during login.

### 🛡️ Section 4: Authorization (Granting Access)
Once authenticated, you must control what resources a user can access.

*   **URL-Based Authorization**: You will configure your `SecurityFilterChain` to restrict access based on URL patterns. For example, you can specify that `/admin/**` requires the `ADMIN` role, while `/user/**` only requires authentication.
*   **Method-Level Security**: For finer control, you can enable method security (`@EnableMethodSecurity`) and use annotations like `@PreAuthorize("hasRole('ADMIN')")` directly on your controller or service methods. This is useful for business logic that isn't tied to a specific URL.

### 🚀 Section 5: Advanced Topics (APIs & Modern Auth)
Modern applications often need to secure REST APIs for mobile or external clients, where session cookies are not ideal.

*   **JWT (JSON Web Tokens)**: You will learn to implement stateless authentication. After a successful login, the server generates a signed JWT. The client sends this token in the `Authorization` header for subsequent requests. You will create a custom filter to validate the token and set the `SecurityContext` without hitting the database on every request.
*   **OAuth2 & Resource Server**: If you are building a microservices architecture or allowing social login (like Google), you will configure your application as a **Resource Server**. It will validate JWT tokens issued by an external Authorization Server (like Keycloak or Okta) using a `jwk-set-uri` to fetch the public keys for signature verification.

### 📚 Learning Resources
*   **Official Guides**: Start with the "Getting Started" and "Hello Spring Security" guides on the official [Spring Security documentation](https://docs.spring.io/spring-security/reference/index.html).
*   **Baeldung**: An excellent resource for focused, practical tutorials on specific topics like [JWT with OAuth](https://www.baeldung.com/spring-security-oauth-jwt).
*   **YouTube**: Channels like *Java Brains* (referenced in the tutorials) offer visual explanations of the complex flows.

This roadmap should give you a clear path forward. If you'd like to dive deeper into any specific section—like setting up a database for user authentication or implementing your first JWT filter—just let me know.
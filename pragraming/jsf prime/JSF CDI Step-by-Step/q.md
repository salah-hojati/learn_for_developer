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
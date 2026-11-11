# Markdown + PlantUML Combination

Here's a complete setup showing how `.md` and `.puml` files work together on **Ubuntu + IntelliJ IDEA**.

## 1. Recommended Project Layout

```
my-docs/
├── README.md
├── diagrams/
│   ├── style.puml
│   ├── architecture.puml
│   └── sequence.puml
└── docs/
    └── design.md
```

Keeping diagrams in a `diagrams/` folder means the same `.puml` can be reused by docs, code, and GitLab/GitHub.

## 2. Shared Style — `diagrams/style.puml`

```plantuml
skinparam backgroundColor #FFFFFF
skinparam shadowing false

skinparam component {
  BackgroundColor #E3F2FD
  BorderColor #1E88E5
}

skinparam database {
  BackgroundColor #FFF3E0
  BorderColor #FB8C00
}

skinparam participant {
  BackgroundColor #F1F8E9
  BorderColor #43A047
}
```

## 3. Reusable Diagram — `diagrams/architecture.puml`

```plantuml
@startuml
!include style.puml

title System Architecture

actor User
component "Web UI" as UI
component "API Gateway" as API
database "PostgreSQL" as DB

User --> UI : HTTPS
UI --> API : REST
API --> DB : JDBC

@enduml
```

## 4. Markdown that Embeds PlantUML — `README.md`

Use a fenced code block with the `plantuml` language tag:

````markdown
# My Project

## Architecture

```plantuml
!include diagrams/architecture.puml
```
````

Inline snippets (no external file) also work:

````markdown
## Login Sequence

```plantuml
@startuml
!include diagrams/style.puml

actor User
participant "Auth Service" as Auth
database "Users DB" as DB

User -> Auth : POST /login
Auth -> DB : SELECT user
DB --> Auth : user row
Auth --> User : JWT
@enduml
```
````

## 5. Mixed documentation — `docs/design.md`

````markdown
# Design Notes

## Context

The service exposes a REST API backed by PostgreSQL.

```plantuml
!include ../diagrams/architecture.puml
```

## Order Flow

```plantuml
@startuml
!include ../diagrams/style.puml

User -> Shop : placeOrder()
Shop -> Payment : charge()
Payment --> Shop : ok
Shop --> User : orderId
@enduml
```
````

Note the `../diagrams/...` path — `!include` resolves **relative to the markdown file**, not the repo root.

## 6. Enabling Rendering in IntelliJ IDEA

1. **Install plugins** (Settings → Plugins → Marketplace):
    - **Markdown** (usually bundled, verify it's enabled)
    - **PlantUML integration**

2. **Set the `dot` path**: Settings → Other Settings → PlantUML → **dot executable path** → `/usr/bin/dot`

3. **Open `README.md`** and click the **preview icon** (top-right) or press `Ctrl+Shift+P` style via "Show Markdown Preview".

The ```` ```plantuml ```` block will render inline.

### If preview shows raw code instead of a diagram

- Make sure **PlantUML integration** is enabled (not just installed).
- Restart the IDE once after install — the Markdown renderer only registers the PlantUML extension at startup.
- Confirm `dot` exists: `which dot` → `/usr/bin/dot`.

## 7. How It Renders on Different Platforms

| Platform | Behavior |
|---|---|
| **IntelliJ IDEA** | Native preview renders ```` ```plantuml ```` via the plugin. `!include` works. |
| **GitLab** | Renders ```` ```plantuml ```` natively (server-side, uses PlantUML server). `!include` **does not** fetch remote repo files. |
| **GitHub** | Does **not** render. Either export PNG/SVG from IntelliJ and commit it, or use a GitHub Action. |
| **VS Code** | Use **Markdown Preview Enhanced** or **PlantUML** extension. |
| **MkDocs** | Add `mkdocs-puml` plugin; code blocks render at build time. |
| **Pandoc** | Use a `plantuml` filter to convert blocks to images. |

### Portable pattern (works everywhere)

Commit both the `.puml` **and** an exported image, and reference the image in `README.md` for GitHub, while keeping the live block for IntelliJ/GitLab:

````markdown
## Architecture

```plantuml
!include diagrams/architecture.puml
```

<!-- fallback for renderers without PlantUML support -->
![Architecture](diagrams/architecture.svg)
````

To export the SVG from IntelliJ: open the `.puml` file → **PlantUML preview panel** → right-click → **Save diagram as SVG**.

## 8. Quick Verification Checklist

```bash
# 1. Java installed?
java -version

# 2. Graphviz installed?
dot -V

# 3. Plugin active in IDEA?
#    Settings → Plugins → Installed → "PlantUML integration" ✔
#    Settings → Languages & Frameworks → Markdown → "PlantUML" enabled ✔

# 4. dot path configured?
#    Settings → Other Settings → PlantUML → /usr/bin/dot
```

Once these are green, `README.md` in IntelliJ will show a live diagram from the ```` ```plantuml ```` block, and the same `.puml` files remain reusable from any other tool.
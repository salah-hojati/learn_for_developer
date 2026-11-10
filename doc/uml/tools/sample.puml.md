Here are sample PlantUML files you can create in IntelliJ IDEA on Ubuntu. Put them in the **same folder** so the `!include` works.

### 1. `Style.puml` — shared styles

Create this file **without** `@startuml` / `@enduml`:

```plantuml
skinparam backgroundColor #FFFFFF

skinparam actor {
  BackgroundColor #E3F2FD
  BorderColor #1E88E5
}

skinparam participant {
  BackgroundColor #F1F8E9
  BorderColor #43A047
}

skinparam database {
  BackgroundColor #FFF3E0
  BorderColor #FB8C00
}

skinparam class {
  BackgroundColor #F3E5F5
  BorderColor #8E24AA
}
```

### 2. `sample-sequence.puml` — sequence diagram sample

```plantuml
@startuml
!include Style.puml

title PlantUML + IntelliJ IDEA on Ubuntu

actor User
participant "IntelliJ IDEA" as IDE
participant "PlantUML Plugin" as Plugin
database "Graphviz (dot)" as GV

User -> IDE: Create sample-sequence.puml
IDE -> Plugin: Request live preview
Plugin -> GV: Generate diagram with dot
GV --> Plugin: SVG output
Plugin --> IDE: Render preview
IDE --> User: Show diagram

note right of Plugin
  Requires Java and Graphviz
  installed on Ubuntu.
end note

@enduml
```

### 3. `sample-class.puml` — class diagram sample

```plantuml
@startuml
!include Style.puml

title Simple Shop Model

class User {
  +id: UUID
  +name: String
  +login()
}

class Order {
  +id: UUID
  +total: BigDecimal
  +place()
}

class Product {
  +id: UUID
  +name: String
  +price: BigDecimal
}

User "1" -- "*" Order : places
Order "*" -- "*" Product : contains

@enduml
```

### 4. `sample-usecase.puml` — use case sample

```plantuml
@startuml
!include Style.puml

left to right direction
title Shop Use Cases

actor Customer
actor Admin

rectangle "Online Shop" {
  usecase "Browse products" as UC1
  usecase "Place order" as UC2
  usecase "Manage products" as UC3
}

Customer --> UC1
Customer --> UC2
Admin --> UC3

@enduml
```

### How to use these in IntelliJ IDEA

1. In your project, right-click the target folder.
2. Choose **New → PlantUML File**.
3. Name it, e.g. `sample-sequence.puml`.
4. Paste one of the samples above.
5. Open the file and use the **PlantUML preview panel** on the right side.

If the preview fails with `Graphviz not found`, check:

```bash
which dot
```

It should output something like:

```bash
/usr/bin/dot
```

Then set that path in:

**Settings → Other Settings → PlantUML → dot executable path**

@startuml
!include Style.puml

title Simple Shop Model

class User {
+id: UUID
+name: String
+login()
}

class Order {
+id: UUID
+total: BigDecimal
+place()
}

class Product {
+id: UUID
+name: String
+price: BigDecimal
}

User "1" -- "*" Order : places
Order "*" -- "*" Product : contains

@enduml
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
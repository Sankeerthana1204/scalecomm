# Application Description

This healthcare platform is being implemented as a set of independent Spring Boot microservices with isolated data ownership, independent deployment units, and explicit service collaboration contracts.

Included services:

- `PatientService`: manages patient profile and identity data used by downstream operational services
- `AppointmentService`: manages appointment scheduling and appointment lifecycle changes
- `HealthRecordService`: manages longitudinal health records and clinical encounter summaries
- `BillingService`: manages invoices and payment tracking for billable activities
- `AuthService`: manages user identity, authentication, and authorization metadata
- `PharmacyService`: manages prescriptions and medication dispensation tracking
- `InsuranceService`: manages coverage policies and claim lifecycle tracking
- `NotificationService`: manages user-facing notification dispatch and delivery tracking

Client access is mediated through BFF layers for the following client types:

- web portal
- mobile app
- admin portal
- partner portal

Synchronous collaboration is exposed through REST APIs. Decoupled workflows use asynchronous messaging for service notifications, cross-service propagation, and long-running coordination patterns such as Saga orchestration or choreography where justified.

Current architectural style includes pragmatic CQRS where it adds clear value:

- `PatientService`, `AppointmentService`, `BillingService`, and `NotificationService` separate command and query application services.
- command flows handle writes and event-driven side effects.
- query flows handle read endpoints and lookup/search responses.

Each service folder in this workspace is structured so it can be promoted into its own repository without refactoring the code layout.
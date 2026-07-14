# Architecture Overview

## Structural Decisions

- Every business capability is implemented as an independent Spring Boot service.
- Every service owns a dedicated MySQL schema and does not share tables with any other service.
- REST is used for synchronous queries and commands where immediate responses are required.
- Asynchronous messaging is used for decoupled event propagation between services.
- Four BFFs are introduced to tailor service aggregation to web, mobile, admin, and partner client needs.

## Collaboration Summary

| Service | Main Responsibility | Typical Sync APIs | Typical Async Events |
| --- | --- | --- | --- |
| PatientService | patient profile lifecycle | create patient, get patient, update patient, search patients | `PatientRegistered`, `PatientUpdated` |
| AppointmentService | appointment scheduling lifecycle | book appointment, get appointment, reschedule appointment, cancel appointment | `AppointmentBooked`, `AppointmentRescheduled`, `AppointmentCancelled` |
| HealthRecordService | clinical record management | create record, get record, list records by patient | `HealthRecordCreated`, `HealthRecordUpdated` |
| BillingService | invoice and payment lifecycle | create invoice, get invoice, record payment | `InvoiceIssued`, `PaymentRecorded` |
| AuthService | access and identity metadata | register user, get user, assign role | `UserRegistered`, `RoleAssigned` |
| PharmacyService | prescription fulfillment tracking | create prescription, get prescription, dispense medication | `PrescriptionCreated`, `MedicationDispensed` |
| InsuranceService | policy and claim lifecycle | create policy, get policy, submit claim, get claim | `PolicyCreated`, `ClaimSubmitted`, `ClaimProcessed` |
| NotificationService | outbound notification delivery | create notification, get notification status | `NotificationQueued`, `NotificationDelivered`, `NotificationFailed` |

## CQRS And Saga Notes

- `PatientService`, `AppointmentService`, `BillingService`, and `NotificationService` use a CQRS-oriented split between command and query services.
- `AppointmentService -> BillingService -> NotificationService` is implemented as an event-driven workflow using RabbitMQ topics and queues.
- `BillingService` command side consumes `appointment.booked` and issues invoices asynchronously; query side serves invoice lookups.
- `NotificationService` command side consumes domain events and materializes notification records; query side serves notification lookups.

## OpenAPI

Every service and BFF includes Springdoc and exposes:

- `/v3/api-docs`
- `/swagger-ui.html`

## Mermaid Diagram

```mermaid
flowchart LR
    subgraph Clients
        Web[Web Portal]
        Mobile[Mobile App]
        Admin[Admin Portal]
        Partner[Partner Portal]
    end

    subgraph BFFs
        WebBFF[Web BFF\nREST]
        MobileBFF[Mobile BFF\nREST]
        AdminBFF[Admin BFF\nREST]
        PartnerBFF[Partner BFF\nREST]
    end

    Web --> WebBFF
    Mobile --> MobileBFF
    Admin --> AdminBFF
    Partner --> PartnerBFF

    subgraph IndependentRepositories
        PatientSvc[PatientService Repo\nREST + Events]
        AppointmentSvc[AppointmentService Repo\nREST + Events]
        HealthRecordSvc[HealthRecordService Repo\nREST + Events]
        BillingSvc[BillingService Repo\nREST + Events]
        AuthSvc[AuthService Repo\nREST + Events]
        PharmacySvc[PharmacyService Repo\nREST + Events]
        InsuranceSvc[InsuranceService Repo\nREST + Events]
        NotificationSvc[NotificationService Repo\nREST + Events]
    end

    WebBFF -->|query/command| PatientSvc
    WebBFF -->|query/command| AppointmentSvc
    WebBFF -->|query| HealthRecordSvc

    MobileBFF -->|query/command| PatientSvc
    MobileBFF -->|query/command| AppointmentSvc
    MobileBFF -->|query| PharmacySvc
    MobileBFF -->|query| NotificationSvc

    AdminBFF -->|query/command| BillingSvc
    AdminBFF -->|query/command| InsuranceSvc
    AdminBFF -->|query/command| AuthSvc
    AdminBFF -->|query| NotificationSvc

    PartnerBFF -->|query/command| InsuranceSvc
    PartnerBFF -->|query| AppointmentSvc
    PartnerBFF -->|query| BillingSvc

    PatientSvc -. event .-> AppointmentSvc
    PatientSvc -. event .-> HealthRecordSvc
    AppointmentSvc -. event .-> BillingSvc
    AppointmentSvc -. event .-> NotificationSvc
    HealthRecordSvc -. event .-> PharmacySvc
    BillingSvc -. event .-> InsuranceSvc
    BillingSvc -. event .-> NotificationSvc
    InsuranceSvc -. event .-> BillingSvc
    PharmacySvc -. event .-> NotificationSvc
    AuthSvc -. event .-> NotificationSvc

    PatientDb[(patient_db)]:::db
    AppointmentDb[(appointment_db)]:::db
    HealthRecordDb[(health_record_db)]:::db
    BillingDb[(billing_db)]:::db
    AuthDb[(auth_db)]:::db
    PharmacyDb[(pharmacy_db)]:::db
    InsuranceDb[(insurance_db)]:::db
    NotificationDb[(notification_db)]:::db

    PatientSvc --> PatientDb
    AppointmentSvc --> AppointmentDb
    HealthRecordSvc --> HealthRecordDb
    BillingSvc --> BillingDb
    AuthSvc --> AuthDb
    PharmacySvc --> PharmacyDb
    InsuranceSvc --> InsuranceDb
    NotificationSvc --> NotificationDb

    classDef db fill:#f4f1de,stroke:#3d405b,color:#111;
```
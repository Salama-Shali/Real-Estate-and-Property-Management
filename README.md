# RentTrack: Rent and Tenant Management for Small Landlords


## The problem

1. **What problem exists?** Small landlords struggle to keep accurate, up-to-date records of their tenants, rent payments and lease terms, and often notice a late payment well after it was due.
2. **Who experiences it?** Small-scale landlords and caretakers who own or manage a few units or a small block of flats without property-management software.
3. **Why does it matter?** Rent is often the landlord's main income, so missed or forgotten payments hurt cash flow. Without clear records, disagreements over who paid what and when are hard to settle.
4. **How is it handled now?** Paper notebooks, Excel or Google Sheets, scrolling through M-Pesa SMS messages, WhatsApp or phone-call reminders, and memory.
5. **What are the limits?** Records are scattered, notebooks get lost, M-Pesa messages get buried, there are no automatic reminders, it is hard to see who is overdue and month-to-month tenants often have no formal record.
6. **How does the app help?** RentTrack keeps tenants, rent, due days and lease terms (including month-to-month) in one place on the landlord's phone, records payments per tenant, sends a daily background reminder for rent due soon, validates input, and works offline.

## Screens

- **Dashboard:** active tenants, total monthly rent expected, and rent due in the next 3 days (with loading and empty states).
- **Tenants:** scrollable list with lease status colours (active, expiring soon, expired, month-to-month), delete with confirmation, and a "Check reminders" button.
- **Add / Edit tenant:** form with validation, date pickers, and an optional lease end date for month-to-month tenancies.
- **Tenant details:** tenant information, lease status and payment history, with a dialog to record a payment (amount and note, for example an M-Pesa code).
- A bottom navigation bar switches between the Dashboard and Tenants.



## Features 

- Create, read, update and delete tenants
- Input validation: empty fields, Kenyan phone numbers, optional email, due day (1 to 28), lease dates, and duplicate units
- Smart rent reminder notifications: checks payment history to avoid reminding tenants who already paid
- Direct tenant communication: Quick action buttons for Call, SMS, and WhatsApp with prefilled reminder templates
- Search & Filter: Instant search by tenant name/unit, and filter by Due Soon or Month-to-Month
- M-Pesa SMS Payment Parser: Auto-fills amount and M-Pesa transaction code from copied SMS receipt text
- Payment Reports & CSV Export: Share financial reports as CSV
- Maintenance Requests Tracking: Log and track maintenance issues per tenant
- Property Management: Schema support for multi-property structures
- Safe Room Database Migrations: Migration 1 to 2 preserving landlord data
- Data stored on the device with Room, works without internet and survives app closing
- Light and dark themes

## Architecture

UI (Fragments) → ViewModel → Repository → DAO → Room database

- `data`: entities (`Tenant`, `Payment`, `Property`, `MaintenanceRequest`), DAOs, database with Room Migration 1 -> 2
- `repository`: single source of data for the ViewModels
- `viewmodel`: screen state, survives rotation
- `ui`: fragments and adapters
- `util`: validation, lease status, MPesa parser, report exporter, and reminder rules
- `worker`: WorkManager reminder job

Database tables: `tenants`, `payments`, `properties`, and `maintenance_requests`.

## Testing

49 unit tests and instrumented DB integration tests cover form validation rules, M-Pesa parsing, report generation, and payment-aware rent reminders.

## Technology

Kotlin, Room Database, ViewModel and LiveData, Navigation Component, RecyclerView, WorkManager, Material Components, AndroidX FileProvider.

## Team contributions

| Member        | Contribution |
|---------------|---|
| Salama Shali  | (entities, DAOs, repositories, ViewModels), tenant list, dashboard, validation and tests, lease dates, rent reminders (WorkManager), theme and bottom navigation, error handling |
| Juliet Nyakiamo | Add and edit tenant form, payments |
| Open Source Contributor | Smart rent reminders (payment awareness), Call/SMS/WhatsApp communication shortcuts, Search & Filter chips, M-Pesa SMS payment parser, CSV payment report export, Property & Maintenance Request entities/DAOs/ViewModels, Room DB Migration 1->2, extended unit & instrumented tests |


## Limitations and Future Work

- Single landlord per device (no login or accounts yet)
- Rent due days are limited to 1 to 28 so the date exists every month
- Future work: Cloud sync / backup, Multi-user landlord login

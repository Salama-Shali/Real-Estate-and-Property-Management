# RentTrack: Rent and Tenant Management for Small Landlords


## The problem

1. **What problem exists?** Small landlords struggle to keep accurate, up-to-date records of their tenants, rent payments and lease terms, and often notice a late payment well after it was due.
2. **Who experiences it?** Small-scale landlords and caretakers who own or manage a few units or a small block of flats without property-management software.
3. **Why does it matter?** Rent is often the landlord's main income, so missed or forgotten payments hurt cash flow. Without clear records, disagreements over who paid what and when are hard to settle.
4. **How is it handled now?** Paper notebooks, Excel or Google Sheets, scrolling through M-Pesa SMS messages, WhatsApp or phone-call reminders, and memory.
5. **What are the limits?** Records are scattered, notebooks get lost, M-Pesa messages get buried, there are no automatic reminders, it is hard to see who is overdue and month-to-month tenants often have no formal record.
6. **How does the app help?** RentTrack keeps tenants, rent, due days and lease terms (including month-to-month) in one place on the landlord's phone, records payments per tenant, sends a daily background reminder for rent due soon, validates input, and works offline.

## Features 

- Tenants: add, view, edit and delete, with validation (empty fields, phone, email, due day, duplicate units, lease dates)
- Lease tracking: start date and optional end date (month-to-month)
- Rent payments recorded per tenant
- Daily rent reminder using WorkManager
- Data stored on the device with Room, so it works without internet

## Architecture

UI (Fragments) → ViewModel → Repository → DAO → Room database

- `data`: entities, DAOs, database
- `repository`: single source of data for the ViewModels
- `viewmodel`: screen state, survives rotation
- `ui`: fragments and adapters
- `util`: validation and reminder rules (unit tested)

## Technology

Kotlin, Room, ViewModel and LiveData, Navigation Component, RecyclerView, WorkManager, Material Components.

## Team contributions

| Member        | Contribution |
|---------------|---|
| Salama Shali  | Project setup, data layer, tenant list, validation, lease dates, reminders |
| Juliet Nyakiamo | Add and edit tenant form, payments |
|       |  |
|          |  |

## Future improvements

M-Pesa payment import, property listings, maintenance requests, tenant messaging, proper database migrations.
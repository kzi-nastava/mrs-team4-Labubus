# Reports API – ugovor frontend ↔ backend

> **Kompletne instrukcije za backend** (validacija, biznis logika, scope, greške) nalaze se u: **[docs/BACKEND_REPORTS_SPEC.md](../../../docs/BACKEND_REPORTS_SPEC.md)**. Ovaj fajl sadrži kratak pregled zahteva i odgovora.

Frontend šalje **POST** zahtev na **`/api/reports`** sa JSON body-om i očekuje JSON odgovor.

## Zahtev (Request body)

Koristi DTO iz `dtos/reports-request-dto.ts`:

```json
{
  "dateFrom": "2025-02-01",
  "dateTo": "2025-02-14",
  "scope": "self",
  "userEmail": "user@example.com"
}
```

- **dateFrom**, **dateTo** – obavezno; datum u ISO 8601 formatu samo datum (YYYY-MM-DD).
- **scope** – opciono; šalje samo admin. Vrednosti:
  - `"self"` – sopstvene vožnje (default za običnog korisnika)
  - `"all_drivers"` – agregat za sve vozače
  - `"all_passengers"` – agregat za sve putnike
  - `"single_user"` – podaci za jednog korisnika (obavezno **userEmail**)
- **userEmail** – obavezno kada je `scope === "single_user"`; email korisnika.

Za običnog korisnika frontend šalje samo `dateFrom` i `dateTo` (scope se ne šalje ili se ignoriše).

## Odgovor (Response body)

Koristi DTO iz `dtos/reports-response-dto.ts`:

```json
{
  "dailyData": [
    {
      "date": "2025-02-01",
      "rideCount": 3,
      "distanceKm": 45.2,
      "amountMoney": -12.50
    }
  ],
  "summary": {
    "totalRides": 42,
    "totalDistanceKm": 520.5,
    "totalAmountMoney": -180.00,
    "averageRidesPerDay": 3.0,
    "averageDistancePerDay": 37.2,
    "averageMoneyPerDay": -12.86
  }
}
```

- **dailyData** – niz po danima u opsegu; **date** u formatu YYYY-MM-DD.
- **amountMoney** – za putnike negativno (potrošeno), za vozače pozitivno (zarađeno).
- **summary** – kumulativne vrednosti i proseci za ceo opseg.

## Autentifikacija

Zahtev ide kroz postojeći HTTP interceptor (token u headeru). Backend treba da:

- Za običnog korisnika/vozača vrati samo podatke za tog korisnika (sopstvene vožnje).
- Za admina poštuje `scope` i eventualno `userEmail` kada je `scope === "single_user"`.

## HTTP status

- **200** – uspeh; body kao gore.
- **4xx/5xx** – greška; frontend prikazuje `error.message` ili `error.error.message` ako postoje.

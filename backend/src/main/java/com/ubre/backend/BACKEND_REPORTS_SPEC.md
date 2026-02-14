# Backend – specifikacija Reports API-ja

Ovaj dokument sadrži kompletne instrukcije za implementaciju backend dela **Reports** funkcionalnosti. Frontend šalje POST zahtev sa opsegom datuma i (za admina) scope-om; backend vraća dnevne podatke i kumulativni rezime za izveštaj.

---

## 1. Endpoint i metod

- **URL:** `POST /api/reports`
- **Content-Type:** `application/json`
- **Body:** JSON objekat (struktura ispod).
- **Odgovor:** JSON objekat (struktura ispod).

Zahtev dolazi sa **JWT tokenom** u headeru (isti mehanizam kao ostali zaštićeni endpointi u aplikaciji). Backend mora da iz tokena izvuče ulogu (REGISTERED_USER, DRIVER, ADMIN) i identifikator trenutnog korisnika.

---

## 2. Request body (šta backend dobija)

Backend **uvek** dobija JSON sa sledećim poljima:

| Polje       | Tip     | Obavezno | Opis |
|------------|---------|----------|------|
| `dateFrom` | string  | da       | Početak opsega datuma u formatu **YYYY-MM-DD** (npr. `"2025-02-01"`). |
| `dateTo`   | string  | da       | Kraj opsega datuma u formatu **YYYY-MM-DD** (npr. `"2025-02-14"`). |
| `scope`    | string  | ne       | Samo za admina. Vrednosti: `"self"`, `"all_drivers"`, `"all_passengers"`, `"single_user"`. |
| `userEmail`| string  | uslovno  | Obavezno **samo** kada je `scope === "single_user"`. Email korisnika za koga se traži izveštaj. |

Primeri body-ja:

**Običan korisnik ili vozač (sopstvene vožnje):**
```json
{
  "dateFrom": "2025-02-01",
  "dateTo": "2025-02-14"
}
```

**Admin – sopstvene vožnje:**
```json
{
  "dateFrom": "2025-02-01",
  "dateTo": "2025-02-14",
  "scope": "self"
}
```

**Admin – svi vozači (agregat):**
```json
{
  "dateFrom": "2025-02-01",
  "dateTo": "2025-02-14",
  "scope": "all_drivers"
}
```

**Admin – svi putnici (agregat):**
```json
{
  "dateFrom": "2025-02-01",
  "dateTo": "2025-02-14",
  "scope": "all_passengers"
}
```

**Admin – jedan korisnik po emailu:**
```json
{
  "dateFrom": "2025-02-01",
  "dateTo": "2025-02-14",
  "scope": "single_user",
  "userEmail": "korisnik@example.com"
}
```

---

## 3. Validacija zahteva

Backend **mora** da validira:

1. **dateFrom, dateTo** – obavezni, format YYYY-MM-DD, `dateFrom` ≤ `dateTo`.
2. **scope** – ako je poslat, mora biti jedna od: `self`, `all_drivers`, `all_passengers`, `single_user`.
3. **userEmail** – ako je `scope === "single_user"`, polje `userEmail` je obavezno i mora odgovarati postojećem korisniku.
4. **Autorizacija:**
   - Ako uloga nije ADMIN, **ignoriši** `scope` i `userEmail` i uvek računaj kao **sopstvene vožnje** trenutnog korisnika (kao da je `scope = "self"`).
   - Ako je uloga ADMIN i `scope` nije poslat, tretiraj kao `scope = "self"`.

U slučaju validacione greške vrati **400 Bad Request** sa jasnom porukom (npr. u `message` ili `error` polju), koju frontend prikazuje korisniku.

---

## 4. Biznis logika – koga uključiti u izveštaj

Treba da filtriraš **vožnje** (rides) po kriterijumu koji zavisi od scope-a i uloge:

- **scope = "self"** (ili običan korisnik bez scope-a):  
  Sve vožnje gde je trenutni korisnik **ili vozač ili putnik**. Za svaku vožnju za tog korisnika računaj:
  - da li je **vozač** te vožnje → doprinosi: +1 vožnja, +distance, +price (zarađeno);
  - da li je **putnik** te vožnje → doprinosi: +1 vožnja, +distance, −price (potrošeno).

- **scope = "all_drivers"**:  
  Agregat za **sve vozače**: uzmi sve vožnje u opsegu datuma i za svaku vožnju dodaj njenom **vozaču**: +1 vožnja, +distance, +price. Zatim agreguj po danu (vidi ispod).

- **scope = "all_passengers"**:  
  Agregat za **sve putnike**: sve vožnje u opsegu, za svakog **putnika** na vožnji: +1 vožnja, +distance, −price (potrošeno). Agreguj po danu.

- **scope = "single_user"**:  
  Samo vožnje gde je korisnik sa `userEmail` vozač ili putnik. Za njega računaj kao za "self" (vozač: +price, putnik: −price), i agreguj po danu.

**Datum vožnje** za grupisanie po danu treba da bude jasan (npr. datum početka vožnje – `startTime` – konvertovan na datum u timezone-u koji koristiš, npr. UTC ili lokalni; bitno da konzistentno koristiš isti izbor).

---

## 5. Agregacija po danu (dailyData)

Za svaki **dan** u opsegu `[dateFrom, dateTo]` (uključujući oba kraja) treba jedan element u nizu `dailyData`:

- **date** – string u formatu **YYYY-MM-DD** (npr. `"2025-02-07"`).
- **rideCount** – ukupan broj vožnji za taj dan (za izabrani scope/korisnika).
- **distanceKm** – ukupno pređenih kilometara za taj dan.
- **amountMoney** – ukupan novac za taj dan:
  - **negativan** za potrošnju (putnik),
  - **pozitivan** za zaradu (vozač).  
  Za agregat (all_drivers / all_passengers) može biti zbir: npr. svi vozači zarađuju → pozitivno; svi putnici troše → negativno.

Ako za neki dan nema vožnji, ipak vrati taj dan sa `rideCount: 0`, `distanceKm: 0`, `amountMoney: 0`.

**Redosled** elemenata u `dailyData`: po kalendarskom datumu, od `dateFrom` do `dateTo`.

---

## 6. Summary (kumulativa i prosek)

U objektu `summary` izračunaj za **ceo opseg** `[dateFrom, dateTo]`:

| Polje                    | Tip    | Kako računati |
|--------------------------|--------|-------------------------------|
| `totalRides`             | number | Zbir svih vožnji u opsegu. |
| `totalDistanceKm`        | number | Zbir svih kilometara u opsegu. |
| `totalAmountMoney`       | number | Zbir svih amountMoney (može biti negativan ako prevladava potrošnja). |
| `averageRidesPerDay`     | number | totalRides / broj_dana_u_opsegu. |
| `averageDistancePerDay`  | number | totalDistanceKm / broj_dana_u_opsegu. |
| `averageMoneyPerDay`     | number | totalAmountMoney / broj_dana_u_opsegu. |

Broj dana u opsegu = broj kalendarskih dana od `dateFrom` do `dateTo` uključujući oba (npr. 1. feb – 14. feb = 14 dana). Deljenje sa 0 izbegavaj (npr. ako je opseg 0 dana, vrati 0 za proseke).

---

## 7. Response body (šta backend vraća)

Uspesan odgovor: **HTTP 200** i JSON oblika:

```json
{
  "dailyData": [
    {
      "date": "2025-02-01",
      "rideCount": 2,
      "distanceKm": 28.5,
      "amountMoney": -15.20
    },
    {
      "date": "2025-02-02",
      "rideCount": 0,
      "distanceKm": 0,
      "amountMoney": 0
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

- **dailyData** – niz objekata, po jedan po danu u opsegu; polja: `date` (string YYYY-MM-DD), `rideCount`, `distanceKm`, `amountMoney` (number).
- **summary** – jedan objekat sa poljima: `totalRides`, `totalDistanceKm`, `totalAmountMoney`, `averageRidesPerDay`, `averageDistancePerDay`, `averageMoneyPerDay` (svi number).

Brojeve zaokružiti na razuman broj decimala (npr. distance i novac na 2 decimale za prikaz; frontend opet formatira).

---

## 8. Greške i HTTP status kodovi

- **400 Bad Request** – nevažeći zahtev (loš format datuma, dateFrom > dateTo, scope = single_user bez userEmail, nepostojeći userEmail, itd.). U body-u vrati poruku koju frontend može prikazati (npr. `{ "message": "Invalid date range" }`).
- **401 Unauthorized** – nema ili nevažeći token.
- **403 Forbidden** – npr. običan korisnik poslao scope koji sme samo admin (ako uopšte dozvoliš slanje scope-a; frontend za običnog korisnika ne šalje scope, ali backend treba da ignoriše scope ako nije admin).
- **404 Not Found** – npr. za `single_user` kada korisnik sa datim email-om ne postoji (možeš i 400 sa porukom „User not found”).
- **500 Internal Server Error** – neočekivana greška; po želji i ovde vrati `message` za logovanje/prikaz.

Frontend očekuje da u grešci može da pročita `error.message` ili `error.error.message` (Angular HttpErrorResponse) da prikaže korisniku.

---

## 9. Rezime ponašanja po ulozi

| Uloga            | Šta frontend šalje          | Šta backend radi |
|------------------|----------------------------|-------------------|
| REGISTERED_USER  | dateFrom, dateTo            | Samo sopstvene vožnje (kao vozač/putnik), ignoriše scope. |
| DRIVER           | dateFrom, dateTo            | Isto – samo sopstvene vožnje. |
| ADMIN            | dateFrom, dateTo, scope (opciono), userEmail (ako single_user) | Poštuje scope: self / all_drivers / all_passengers / single_user; za single_user koristi userEmail. |

---

## 10. Kratak checklist za implementaciju

- [ ] POST `/api/reports` prima JSON body i JWT.
- [ ] Validacija dateFrom, dateTo (format, dateFrom ≤ dateTo).
- [ ] Validacija scope i userEmail kada je scope = single_user.
- [ ] Za ne-admin korisnika uvek samo sopstvene vožnje (ignorisati scope).
- [ ] Za admina: implementirati self, all_drivers, all_passengers, single_user.
- [ ] dailyData: jedan red po danu u opsegu, uključujući dane sa 0 vožnji; date u YYYY-MM-DD.
- [ ] amountMoney: negativno za putnike (potrošeno), pozitivno za vozače (zarađeno).
- [ ] summary: totali i proseci (broj dana = dateFrom do dateTo uključivo).
- [ ] 200 + JSON za uspeh; 400/401/403/404/500 sa čitljivom porukom za greške.

Ovim je definisan kompletan ugovor između frontenda i backend-a za Reports. Frontend već šalje ovakve zahteve i očekuje ovakav odgovor; backend treba da se ponaša u skladu sa ovom specifikacijom.

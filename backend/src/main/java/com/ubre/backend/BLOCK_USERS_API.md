# Block users – očekivani backend endpointi

Frontend koristi sledeće API pozive za blokiranje korisnika (samo za ulogu ADMIN).

## 1. Lista korisnika (za admin panel)

- **Metoda:** `GET`
- **URL:** `/api/users`
- **Autorizacija:** samo ADMIN
- **Odgovor:** `200 OK`, body = niz korisnika u formatu `UserDto[]`

Svaki korisnik mora imati polje `blocked: boolean` (pored `id`, `role`, `name`, `surname`, `email`, `avatarUrl`, `phone`, `address`).

Primer odgovora:

```json
[
  {
    "id": 1,
    "role": "REGISTERED_USER",
    "name": "Petar",
    "surname": "Petrović",
    "email": "petar@example.com",
    "avatarUrl": "...",
    "phone": "...",
    "address": "...",
    "blocked": false
  }
]
```

## 2. Blokiranje korisnika

- **Metoda:** `PUT`
- **URL:** `/api/users/{id}/block`
- **Autorizacija:** samo ADMIN
- **Body (opciono):** `{ "note": "Razlog blokade..." }` – napomena koju vidi korisnik/vozač
- **Odgovor:** `200 OK` (bez body-a ili prazan body)

## 3. Odblokiranje korisnika

- **Metoda:** `PUT`
- **URL:** `/api/users/{id}/unblock`
- **Autorizacija:** samo ADMIN
- **Body:** prazan `{}` ili bez body-a
- **Odgovor:** `200 OK`

---

Napomena: u trenutnoj implementaciji frontend ne šalje `note` pri blokiranju (možeš kasnije dodati modal za unos napomene). Backend može prihvatiti opcioni body sa `note` za prikaz korisniku/vozaču.

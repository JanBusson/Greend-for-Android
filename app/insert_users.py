import firebase_admin
from firebase_admin import credentials, db

# 1. Service Account Key laden
cred = credentials.Certificate("serviceAccountKey.json")
firebase_admin.initialize_app(cred, {
    'databaseURL': 'https://greendr-5ab65-default-rtdb.firebaseio.com/'
})

# 2. Liste mit bestehenden User-UIDs
uids = [
    "NrbGA8NPWJbgxY5iujSOeqaDa6h1",
    "QAgH1AMduKODnGg11sKRcz47avz2",
    "WQb90hEgzLRMVJcxTTSzXFQisZ32",
    "Z47pmPJudvMG1wLUJ87CFHrf6of1",
    "Z6KdWaDByqbonGnjFRFXdaav8I42",
    "cSDGH921GwdChJUNFEws4cCiL0I3",
    "hZ9F9XX4T1clrY7TxJG5U0VNlCx2",
    "nRQoLzJnfYMf263fqGDaqv0P5B3",
    "uasxPK44ihQRSVLDn0xXpS8cBTn1",
    "xgWexXlTrLc1urb7j3d8zOAbatF3"
]

# 3. Dummy-Daten
dummy_data = {
    "name": "Test User",
    "age": 25,
    "gender": "Other",
    "hometown": "Sample City",
    "ecoScore": 50.0,
    "socialScore": 50.0,
    "university": "Sample University",
    "jobTitle": "Sample Job",
    "languages": ["English", "German"],
    "sexuality": "Unknown",
    "bio": "This is a dummy bio."
}

# 4. Felder ergänzen/überschreiben
for uid in uids:
    user_ref = db.reference(f"users/{uid}")
    current_data = user_ref.get() or {}

    # Nur fehlende Felder ergänzen
    updated_data = {**dummy_data, **current_data}  # vorhandene Felder behalten Vorrang
    user_ref.set(updated_data)

    print(f"✅ User {uid} aktualisiert: Fehlende Felder ergänzt.")

print("🎉 Alle User wurden erfolgreich befüllt/ergänzt!")

import os
import random
import firebase_admin
from firebase_admin import credentials, db

# Basisverzeichnis automatisch berechnen (wo das Skript liegt)
BASE_DIR = os.path.dirname(os.path.abspath(__file__))
cred_path = os.path.join(BASE_DIR, "serviceAccountKey.json")

# Firebase initialisieren
cred = credentials.Certificate(cred_path)
firebase_admin.initialize_app(cred, {
    'databaseURL': 'https://greendr-5ab65-default-rtdb.europe-west1.firebasedatabase.app/'
})

# 1. Bestehende User-UIDs
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

# Zufallslisten
names = ["Alex", "Sam", "Jamie", "Taylor", "Chris", "Jordan", "Casey", "Riley", "Morgan", "Avery"]
genders = ["Male", "Female", "Other"]
cities = ["Berlin", "Munich", "Hamburg", "Cologne", "Frankfurt"]
sexualities = ["Heterosexual", "Homosexual", "Bisexual", "Asexual", "Unknown"]
universities = ["HWR Berlin", "TU Munich", "Uni Hamburg", "Uni Cologne", "Goethe Uni Frankfurt"]
jobs = ["Software Engineer", "Data Analyst", "Consultant", "Researcher", "Student Assistant"]
languages_pool = ["German", "English", "Spanish", "French", "Japanese"]
hobbies = ["Reading", "Sports", "Gaming", "Music", "Traveling"]

# 2. Zufallsdaten generieren und in Firebase schreiben
for uid in uids:
    user_data = {
        "name": random.choice(names),
        "age": random.randint(18, 30),
        "gender": random.choice(genders),
        "hometown": random.choice(cities),
        "ecoScore": round(random.uniform(0, 100), 1),
        "socialScore": round(random.uniform(0, 100), 1),
        "university": random.choice(universities),
        "jobTitle": random.choice(jobs),
        "languages": random.sample(languages_pool, k=random.randint(1, 3)),
        "sexuality": random.choice(sexualities),
        "bio": "I love " + random.choice(hobbies) + " and meeting new people!",
        "usageOfDrugs": random.choice(["Never", "Occasionally", "Frequently"]),
        "hobbies": random.sample(hobbies, k=random.randint(1, 3))
    }

    user_ref = db.reference(f"Users/{uid}")
    user_ref.set(user_data)  # Überschreibt vorhandene Daten komplett

    print(f"✅ User {uid} überschrieben mit neuen Daten: {user_data['name']}")

print("🎉 Alle User wurden mit neuen, realistischen Daten überschrieben!")

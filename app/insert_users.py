import os
import random
import firebase_admin
from firebase_admin import credentials, db, auth
import itertools

BASE_DIR = os.path.dirname(os.path.abspath(__file__))
cred_path = os.path.join(BASE_DIR, "serviceAccountKey.json")

# Firebase initialisieren
if not firebase_admin._apps:
    cred = credentials.Certificate(cred_path)
    firebase_admin.initialize_app(cred, {
        'databaseURL': 'https://greendr-5ab65-default-rtdb.europe-west1.firebasedatabase.app/'
    })

# Dummy-Daten
names = ["Alex", "Sam", "Jamie", "Taylor", "Chris", "Jordan", "Casey", "Riley", "Morgan", "Avery"]
genders = ["Male", "Female", "Other"]
cities = ["Berlin", "Munich", "Hamburg", "Cologne", "Frankfurt"]
sexualities = ["Heterosexual", "Homosexual", "Bisexual", "Asexual", "Unknown"]
universities = ["HWR Berlin", "TU Munich", "Uni Hamburg", "Uni Cologne", "Goethe Uni Frankfurt"]
jobs = ["Software Engineer", "Data Analyst", "Consultant", "Researcher", "Student Assistant"]
languages_pool = ["German", "English", "Spanish", "French", "Japanese"]
hobbies = ["Reading", "Sports", "Gaming", "Music", "Traveling"]

# Alle User-Uids holen
all_uids = []
page = auth.list_users()
while page:
    for user in page.users:
        all_uids.append(user.uid)
    page = page.get_next_page()

print(f"Gefundene User-UIDs: {all_uids}")

# Profil-Daten setzen
for uid in all_uids:
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
    user_ref.update(user_data)

    eco_val = db.reference(f"Users/{uid}/ecoScore").get()
    social_val = db.reference(f"Users/{uid}/socialScore").get()
    print(f"✅ {uid}: ecoScore={eco_val}, socialScore={social_val}")

# ✅ Auto-Matching: Jeder mit jedem
print("\n🔗 Erstelle Matches zwischen allen Usern...")
matches_ref = db.reference("Matches")

for uid1, uid2 in itertools.combinations(all_uids, 2):  # Alle einzigartigen Paare
    # Like-Einträge gegenseitig setzen
    db.reference(f"Users/{uid1}/likes/{uid2}").set(True)
    db.reference(f"Users/{uid2}/likes/{uid1}").set(True)

    # Matches setzen
    db.reference(f"Users/{uid1}/matches/{uid2}").set(True)
    db.reference(f"Users/{uid2}/matches/{uid1}").set(True)

    # Optional: Like-Zähler hochsetzen
    db.reference(f"Users/{uid1}/likedByCount").set(len(all_uids)-1)
    db.reference(f"Users/{uid2}/likedByCount").set(len(all_uids)-1)

    # Match-Eintrag in /Matches erstellen
    match_id = matches_ref.push().key
    matches_ref.child(match_id).set({
        "user1": uid1,
        "user2": uid2,
        "status": "like",
        "timestamp": firebase_admin.firestore.SERVER_TIMESTAMP if hasattr(firebase_admin, "firestore") else None
    })

print("🎉 Alle User haben jetzt Matches miteinander!")

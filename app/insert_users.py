import os
import random
import firebase_admin
from firebase_admin import credentials, db, auth
import time

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
comment_texts = [
    "Hey, nice profile!",
    "Cool interests, let's chat!",
    "I like your vibe!",
    "Eco score is impressive!",
    "Let's connect :)",
    "Great match, what do you think?",
    "This app is fun!"
]

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
    db.reference(f"Users/{uid}").update(user_data)

print("✅ Profile aktualisiert.")

# ✅ Matches: Jeder User matched mit 3-5 zufälligen anderen Nutzern
print("\n🔗 Erstelle begrenzte Matches...")
matches_ref = db.reference("Matches")

for uid in all_uids:
    # Zufällige andere Nutzer auswählen
    potential_matches = [u for u in all_uids if u != uid]
    match_partners = random.sample(potential_matches, min(len(potential_matches), random.randint(3, 5)))

    for partner in match_partners:
        # Like gegenseitig setzen
        db.reference(f"Users/{uid}/likes/{partner}").set(True)
        db.reference(f"Users/{partner}/likes/{uid}").set(True)

        # Match gegenseitig setzen
        db.reference(f"Users/{uid}/matches/{partner}").set(True)
        db.reference(f"Users/{partner}/matches/{uid}").set(True)

        # Match in globalem /Matches-Knoten speichern
        match_id = matches_ref.push().key
        matches_ref.child(match_id).set({
            "user1": uid,
            "user2": partner,
            "status": "like",
            "timestamp": int(time.time() * 1000)
        })

print("🎉 Jeder User hat jetzt 3-5 Matches!")

# 🗨️ Kommentare hinzufügen
print("\n💬 Füge zufällige Kommentare hinzu...")
for uid in all_uids:
    comments_ref = db.reference(f"Users/{uid}/comments")
    for _ in range(random.randint(2, 4)):  # 2–4 Kommentare pro User
        author_id = random.choice(all_uids)
        if author_id == uid:
            continue  # Kein Kommentar von sich selbst
        comment = {
            "authorId": author_id,
            "authorName": db.reference(f"Users/{author_id}/name").get(),
            "text": random.choice(comment_texts),
            "timestamp": int(time.time() * 1000)
        }
        comments_ref.push(comment)
    print(f"✅ Kommentare für {uid} hinzugefügt")

print("✅ Fertig: Profile, Matches und Kommentare erstellt!")

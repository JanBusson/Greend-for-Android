/*########################################################
Intialisiert die Datenbankverbindung
########################################################*/

package com.example.greendr;

import com.google.firebase.database.FirebaseDatabase;

public class FirebaseInstance {
    private static FirebaseDatabase database;

    public static void init() {
        if (database == null) {
            database = FirebaseDatabase.getInstance("https://greendr-5ab65-default-rtdb.europe-west1.firebasedatabase.app");
        }
    }

    public static FirebaseDatabase getDatabase() {
        return database;
    }
}
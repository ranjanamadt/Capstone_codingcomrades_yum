package madt.capstone_codingcomrades_yum.utils;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class FirebaseCRUD {


    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public Task<DocumentReference> create(String collection, Map<String, Object> document ){
        // Add a new document with a generated ID
        return db.collection(collection).add(document);
    }

    public Task<QuerySnapshot> getAll(String collection){
        // gets all the documents in a collection
        return db.collection(collection).get();
    }
    public CollectionReference getCollection(String collection){
        // gets a collection
        return db.collection(collection);
    }

    public Task<DocumentSnapshot> getDocument(String collection, String documentId){
        // get a document
        return db.collection(collection).document(documentId).get();
    }

    public Task<Void> deleteDocument(String collection, String documentId){
        // deletes a document
        return db.collection(collection).document(documentId).delete();
    }


}
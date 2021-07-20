package madt.capstone_codingcomrades_yum.utils;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
import java.util.Map;

public class FirebaseCRUD {

    public static FirebaseCRUD firebaseCRUD;

    public static FirebaseCRUD getInstance() {
        if (firebaseCRUD == null) {
            firebaseCRUD = new FirebaseCRUD();
        }
        return firebaseCRUD;
    }

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public Task<DocumentReference> create(String collection, Map<String, Object> document) {
        // Add a new document with a generated ID
        return db.collection(collection).add(document);
    }
    public Task<Void> createSubCollection(String collection, String subCollection, String docId,String userName,Map<String, Object> document) {
Log.e("path :",collection+"/"+docId+"/"+subCollection);
        // Add a new document with a generated ID
        return db.collection(collection+"/"+docId+"/"+subCollection).document(userName).set(document);
    }

    public Task<Void> set(String collection, String docId, Map<String, Object> document) {
        // Add a new document with a generated ID
        return db.collection(collection).document(docId).set(document);

    }
    public Task<Void> updateDoc(String collection, String docId, Map<String, Object> updatedData) {
        // Add a new document with a generated ID
        return db.collection(collection).document(docId).update(updatedData);
    }


    public Task<QuerySnapshot> getAll(String collection) {
        // gets all the documents in a collection
        return db.collection(collection).get();
    }

    public CollectionReference getCollection(String collection) {
        // gets a collection
        return db.collection(collection);
    }

    public Task<DocumentSnapshot> getDocument(String collection, String documentId) {
        // get a document
        Log.e("doc id ", documentId + "//");
        return FirebaseFirestore.getInstance().collection(collection).document(documentId).get();
    }

    public Task<Void> deleteDocument(String collection, String documentId) {
        // deletes a document
        return db.collection(collection).document(documentId).delete();
    }
   public Task<QuerySnapshot> findMatches(String collection, List<? extends Object> eatingPrefer){
       return db.collection(FSConstants.Collections.USERS)
               .whereArrayContainsAny(FSConstants.PREFERENCE_TYPE.TASTE,eatingPrefer)

               .get();


   }

}

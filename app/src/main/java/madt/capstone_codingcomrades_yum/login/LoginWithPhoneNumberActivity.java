package madt.capstone_codingcomrades_yum.login;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

import madt.capstone_codingcomrades_yum.R;
import madt.capstone_codingcomrades_yum.core.BaseActivity;
import madt.capstone_codingcomrades_yum.databinding.ActivityLoginWithPhoneNumberBinding;
import madt.capstone_codingcomrades_yum.utils.YumTopBar;

public class LoginWithPhoneNumberActivity extends BaseActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    public static final String AUTHOR = "author";
    ActivityLoginWithPhoneNumberBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Map<String, Object> user = new HashMap<>();
        user.put("first", "Ada");
        user.put("last", "Lovelace");
        user.put("born", 1815);

// Add a new document with a generated ID
        db.collection("users")
                .add(user)



                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("TESTDB", "DocumentSnapshot added with ID: " + documentReference.getId());

                        db.collection("users")
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                Log.d("TAG", document.getId() + " => " + document.getData());
//                                                db.collection("cities").document("DC")
//                                                        .delete()
//                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                                            @Override
//                                                            public void onSuccess(Void aVoid) {
//                                                                Log.d("TAG2", "DocumentSnapshot successfully deleted!");
//                                                            }
//                                                        })
//                                                        .addOnFailureListener(new OnFailureListener() {
//                                                            @Override
//                                                            public void onFailure(@NonNull Exception e) {
//                                                                Log.w("TAG2", "Error deleting document", e);
//                                                            }
//                                                        });
                                            }
                                        } else {
                                            Log.w("TAG", "Error getting documents.", task.getException());
                                        }
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("TESTDB", "Error adding document", e);
                    }
                });

        binding = DataBindingUtil.setContentView(this, R.layout.activity_login_with_phone_number);
        binding.editTextTextPersonName.setText("test");

        YumTopBar.setToolbar(
                binding.topBar,
                R.drawable.ic_baseline_arrow_back_24,
                "",
                true,
                false,
                new YumTopBar.OnToolbarClickListener() {
                    @Override
                    public void onLeftIconClick() {
                        finish();
                    }
                });

    }

}

package madt.capstone_codingcomrades_yum.createprofile;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.chip.Chip;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import madt.capstone_codingcomrades_yum.R;
import madt.capstone_codingcomrades_yum.core.BaseActivity;
import madt.capstone_codingcomrades_yum.databinding.ActivityFoodTopicsBinding;
import madt.capstone_codingcomrades_yum.sharedpreferences.AppSharedPreferences;
import madt.capstone_codingcomrades_yum.sharedpreferences.SharedConstants;
import madt.capstone_codingcomrades_yum.utils.CommonUtils;
import madt.capstone_codingcomrades_yum.utils.FSConstants;
import madt.capstone_codingcomrades_yum.utils.FirebaseCRUD;
import madt.capstone_codingcomrades_yum.utils.YumTopBar;


public class FoodTopicsActivity extends BaseActivity {
    private ActivityFoodTopicsBinding binding;
    /*final static String[] nofood = {"Acorn Squash","Apple","Arugula","Asparagus","Banana","Blackberries","Broccoli","Brussel Sprouts","Butternut Squash","Cabbage","Carrots","Cauliflower","Chicken","Collard Greens","Cucumber"
    ,"Garlic","Grapes","IceCream","Kale","Lemon","Lettuce","Mustard greens","Oatmeal","Onion","Orange","Papaya","Pear","Peas","Peppers","Pork","Strawberries","Vegan","Vegetarian","Zucchini","Yolk"};
    final static String[] notopics = {"Art", "Movies", "Sports", "Gym", "Politics"};*/
    private List<String> notEatList;
    private List<String> notTalkList;

    List<String> resultNotEat = new ArrayList<>();
    List<String> resultNotTalk = new ArrayList<>();
    List<String> userEnjoyEatingList = new ArrayList<>();
    List<String> userInterestList = new ArrayList<>();
    Boolean checkNotEat = false;
    Boolean checkNotTalk = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_food_topics);

        binding.btnConfirmFoodTopics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (resultNotEat.isEmpty()) {
                    ySnackbar(FoodTopicsActivity.this, getString(R.string.err_not_eat_chip_empty));
                } else if (resultNotTalk.isEmpty()) {
                    ySnackbar(FoodTopicsActivity.this, getString(R.string.err_not_talk_chip_empty));
                } else {

                    Map<String, Object> foodPrefer = new HashMap<>();
                    foodPrefer.put(FSConstants.PREFERENCE_TYPE.NOT_EAT, resultNotEat);
                    foodPrefer.put(FSConstants.PREFERENCE_TYPE.NOT_TALK, resultNotTalk);

                    CommonUtils.showProgress(FoodTopicsActivity.this);

                    FirebaseCRUD.getInstance().updateDoc(FSConstants.Collections.USERS, FirebaseAuth.getInstance().getUid(), foodPrefer).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            CommonUtils.hideProgress();
                            AppSharedPreferences.getInstance().setBoolean(SharedConstants.FOOD_TOPIC_DONE, true);
                            Intent i = new Intent(FoodTopicsActivity.this, FinishProfileActivity.class);
                            startActivity(i);
                        }

                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            CommonUtils.hideProgress();
                            ySnackbar(FoodTopicsActivity.this, getString(R.string.error_saving_not_eat));
                        }
                    });
                }
            }
        });
    }

    private void getSavedFoodTopics() {
        CommonUtils.showProgress(this);
        FirebaseCRUD.getInstance().getDocument(FSConstants.Collections.USERS, FirebaseAuth.getInstance().getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                yLog("user id :", documentSnapshot.getId() + " ");

                List<String> resultNE = (List<String>) documentSnapshot.get(FSConstants.PREFERENCE_TYPE.NOT_EAT);
                List<String> resultNT = (List<String>) documentSnapshot.get(FSConstants.PREFERENCE_TYPE.NOT_TALK);


                if (resultNE != null && resultNE.size() > 0) {
                    resultNotEat.addAll(resultNE);
                    addNotEat(resultNE);
                }
                if (resultNT != null && resultNT.size() > 0) {
                    resultNotTalk.addAll(resultNT);
                    addNotTalk(resultNT);
                }

                getAllFoodTopics();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                CommonUtils.hideProgress();
                ySnackbar(FoodTopicsActivity.this, getString(R.string.error_saving_not_eat));
                getAllFoodTopics();
            }
        });
    }

    private void getAllFoodTopics() {
        FirebaseCRUD.getInstance().getDocument(FSConstants.Collections.PREFERENCES, FSConstants.PREFERENCE_TYPE.NOT_EAT).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                yLog("user name :", (List<String>) documentSnapshot.get("data") + "//");
                notEatList = (List<String>) documentSnapshot.get("data");
                setNotEatDropdown();
                getNotTalkListFromDB();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                CommonUtils.hideProgress();
                ySnackbar(FoodTopicsActivity.this, e.getLocalizedMessage());
            }
        });
    }


    private void getNotTalkListFromDB() {
        FirebaseCRUD.getInstance().getDocument(FSConstants.Collections.PREFERENCES, FSConstants.PREFERENCE_TYPE.NOT_TALK).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                yLog("user name :", (List<String>) documentSnapshot.get("data") + "//");
                notTalkList = (List<String>) documentSnapshot.get("data");
                setNotTalkDropdown();
                CommonUtils.hideProgress();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                CommonUtils.hideProgress();
                ySnackbar(FoodTopicsActivity.this, e.getLocalizedMessage());
            }
        });

    }

    private void setNotTalkDropdown() {
        notTalkList.removeAll(userInterestList);
        yLog("notTalk drop down list: ", notTalkList.toString());
        yLog("userInterestList: ", userInterestList.toString());

        binding.spnNoTopic.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,
                notTalkList));

        binding.spnNoTopic.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(checkNotTalk) {
                    if (resultNotTalk != null && !resultNotTalk.isEmpty()) {
                        if (!resultNotTalk.contains(notTalkList.get(position)))
                            addNoTalkChip(notTalkList.get(position));
                    } else {
                        addNoTalkChip(notTalkList.get(position));
                    }
                }else{
                    checkNotTalk=true;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    private void setNotEatDropdown() {
        notEatList.removeAll(userEnjoyEatingList);
        yLog("notEat drop down list: ", notEatList.toString());
        yLog("userEnjoyEatingList: ", userEnjoyEatingList.toString());

        binding.spnNoFood.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, notEatList));
        binding.spnNoFood.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(checkNotEat) {
                    if (resultNotEat != null && !resultNotEat.isEmpty()) {
                        if (!resultNotEat.contains(notEatList.get(position)))
                            addNoEatChip(notEatList.get(position));
                    } else {
                        addNoEatChip(notEatList.get(position));
                    }
                }else {
                    checkNotEat=true;
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        FirebaseCRUD.getInstance().getDocument(FSConstants.Collections.USERS, FirebaseAuth.getInstance().getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                userEnjoyEatingList = (List<String>) documentSnapshot.get(FSConstants.PREFERENCE_TYPE.ENJOY_EATING);
                userInterestList = (List<String>) documentSnapshot.get(FSConstants.PREFERENCE_TYPE.INTEREST);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                CommonUtils.hideProgress();
                ySnackbar(FoodTopicsActivity.this, getString(R.string.err_fetching_data));
            }
        });

        setTopBar();
        binding.chipGroupNoFood.removeAllViews();
        binding.chipNotTalk.removeAllViews();
        checkNotEat = false;
        checkNotTalk = false;
        if (resultNotEat.isEmpty() || resultNotTalk.isEmpty()) {
            resultNotEat.clear();
            resultNotTalk.clear();
            getSavedFoodTopics();
        } else {
            addNotEat(resultNotEat);
            addNotTalk(resultNotTalk);
        }


    }

    private void addNotEat(List<String> notEatList) {

        for (String notEat : notEatList) {
            Chip newChip = (Chip) getLayoutInflater().inflate(R.layout.pink_chip, binding.chipGroupNoFood, false);
            newChip.setText(notEat);
            binding.chipGroupNoFood.addView(newChip);


            newChip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    binding.chipGroupNoFood.removeView(v);
                    resultNotEat.remove(((Chip) v).getText());
                }
            });
        }

        // getNotTalk(FirebaseAuth.getInstance().getUid());
    }

    private void addNotTalk(List<String> notTalkList) {
        for (String notTalk : notTalkList) {
            Chip newChip = (Chip) getLayoutInflater().inflate(R.layout.yellow_chip, binding.chipNotTalk, false);
            newChip.setText(notTalk);
            binding.chipNotTalk.addView(newChip);

            newChip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    binding.chipNotTalk.removeView(v);
                    resultNotTalk.remove(((Chip) v).getText());
                }
            });
        }
    }

    @Override
    protected void setTopBar() {
        YumTopBar.setToolbar(
                binding.topBar,
                R.drawable.ic_back_arrow,
                getString(R.string.title_food_topics),
                true,
                true,
                new YumTopBar.OnToolbarClickListener() {
                    @Override
                    public void onLeftIconClick() {
                        finish();
                    }
                });
    }

    private void addNoEatChip(String topic) {
        resultNotEat.add(topic);
        Chip newChip = (Chip) getLayoutInflater().inflate(R.layout.pink_chip, binding.chipGroupNoFood, false);
        newChip.setText(topic);
        binding.chipGroupNoFood.addView(newChip);

        newChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.chipGroupNoFood.removeView(v);
                resultNotEat.remove(((Chip) v).getText());
            }
        });

    }

    private void addNoTalkChip(String topic) {
        resultNotTalk.add(topic);
        Chip newChip = (Chip) getLayoutInflater().inflate(R.layout.yellow_chip, binding.chipNotTalk, false);
        newChip.setText(topic);
        binding.chipNotTalk.addView(newChip);

        newChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.chipNotTalk.removeView(v);
                resultNotTalk.remove(((Chip) v).getText());
            }
        });

    }

}
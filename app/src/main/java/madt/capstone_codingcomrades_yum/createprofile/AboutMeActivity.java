package madt.capstone_codingcomrades_yum.createprofile;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import madt.capstone_codingcomrades_yum.R;
import madt.capstone_codingcomrades_yum.core.BaseActivity;
import madt.capstone_codingcomrades_yum.databinding.ActivityAboutMeBinding;
import madt.capstone_codingcomrades_yum.login.LoginActivity;
import madt.capstone_codingcomrades_yum.sharedpreferences.AppSharedPreferences;
import madt.capstone_codingcomrades_yum.sharedpreferences.SharedConstants;
import madt.capstone_codingcomrades_yum.utils.CommonUtils;
import madt.capstone_codingcomrades_yum.utils.FSConstants;
import madt.capstone_codingcomrades_yum.utils.FirebaseCRUD;
import madt.capstone_codingcomrades_yum.utils.YumTopBar;


public class AboutMeActivity extends BaseActivity {
    private ActivityAboutMeBinding binding;
    public static String firstName = "", lastName = "", gender = "", sePref = "", dob = "";
    public static String stringDate = "";
    public boolean isUserExist = false;

    final static String[] genders = {"Male", "Female", "Genderqueer/Non-Binary", "Prefer not to say"};
    final static String[] preferences = {"Straight", "Gay", "Lesbian", "Bisexual", "Asexual", "Demisexual", "Pansexual", "Queer", "Questioning"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_about_me);

        binding.sexPrefSp.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, preferences));
        binding.genderSp.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, genders));

CommonUtils.showProgress(this);
        FirebaseCRUD.getInstance().getDocument(FSConstants.Collections.USERS, FirebaseAuth.getInstance().getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                if (documentSnapshot.exists()) {
                    isUserExist = true;
                    binding.firstNameET.setText(documentSnapshot.get(FSConstants.USER.FIRST_NAME).toString());
                    binding.lastNameET.setText(documentSnapshot.get(FSConstants.USER.LAST_NAME).toString());
                    binding.dobTV.setText(documentSnapshot.get(FSConstants.USER.DOB).toString());
                    for (int i = 0; i < genders.length; i++) {
                        if (genders[i].equals(documentSnapshot.get(FSConstants.USER.GENDER))) {
                            binding.genderSp.setSelection(i);
                            break;
                        }
                    }
                    for (int i = 0; i < preferences.length; i++) {
                        if (preferences[i].equals(documentSnapshot.get(FSConstants.USER.SEX_PREFER))) {
                            binding.sexPrefSp.setSelection(i);
                           break;
                        }
                    }
                } else {
                    isUserExist = false;
                    setDataReceivedFromFB();
                }
                CommonUtils.hideProgress();
/*
                List<String> enjoyEating = (List<String>) documentSnapshot.get(FSConstants.PREFERENCE_TYPE.ENJOY_EATING);
                List<String> taste = (List<String>) documentSnapshot.get(FSConstants.PREFERENCE_TYPE.TASTE);
                List<String> notEat = (List<String>) documentSnapshot.get(FSConstants.PREFERENCE_TYPE.NOT_EAT);
                List<String> notTalk = (List<String>) documentSnapshot.get(FSConstants.PREFERENCE_TYPE.NOT_TALK);

                yLog("previous data enjoyEating: ", enjoyEating.toString());
                yLog("previous data taste: ", taste.toString());
                yLog("previous data notEat: ", notEat.toString());
                yLog("previous data notTalk: ", notTalk.toString());
                if(enjoyEating != null && enjoyEating.size() > 0){
                    user.put(FSConstants.PREFERENCE_TYPE.ENJOY_EATING, enjoyEating);
                }
                if(taste != null && taste.size() > 0){
                    user.put(FSConstants.PREFERENCE_TYPE.TASTE, taste);
                }
                if(notEat != null && notEat.size() > 0){
                    user.put(FSConstants.PREFERENCE_TYPE.NOT_EAT, notEat);
                }
                if(notTalk != null && notTalk.size() > 0){
                    user.put(FSConstants.PREFERENCE_TYPE.NOT_TALK, notTalk);
                }*/
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                ySnackbar(AboutMeActivity.this, getString(R.string.error_saving_user));
                CommonUtils.hideProgress();
            }
        });


        // getting today's date and separating day, month and year and putting that value to array
        long millis = System.currentTimeMillis();
        java.sql.Date todaydate = new java.sql.Date(millis);
        String ArrtodayDate[] = todaydate.toString().split("-");

        // date into the string format
        stringDate = getMonthLetter(Integer.parseInt(ArrtodayDate[1]) - 1).toString() + " " + ArrtodayDate[2] + ", " + ArrtodayDate[0];
        binding.dobTV.setText(stringDate);

        Calendar cal = Calendar.getInstance();
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH);
        int year = cal.get(Calendar.YEAR);

        // on click listener event for the textview
        binding.imgCalender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // creating date picker dialog object
                DatePickerDialog dpDialog = new DatePickerDialog(AboutMeActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        // changing the string date as per the date selected by the user
                        String monthLetter = getMonthLetter(month);
                        stringDate = monthLetter + " " + dayOfMonth + ", " + year;
                        binding.dobTV.setText(stringDate);
                        binding.dobTV.setTextSize(20);
                    }
                }, year, month, day
                );

                // disabling the paste date
                dpDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                // showing the date picket dialog
                dpDialog.show();
            }
        });

        binding.btnConfirmAboutMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.firstNameET.getText().toString().isEmpty()) {
                    ySnackbar(AboutMeActivity.this, getString(R.string.err_first_name_empty));
                } else if (binding.lastNameET.getText().toString().isEmpty()) {
                    ySnackbar(AboutMeActivity.this, getString(R.string.err_last_name_empty));
                } else if (binding.dobTV.getText().toString().isEmpty()) {
                    ySnackbar(AboutMeActivity.this, getString(R.string.err_dob_name_empty));
                } else {
                    firstName = binding.firstNameET.getText().toString();
                    lastName = binding.lastNameET.getText().toString();
                    dob = binding.dobTV.getText().toString();
                    gender = binding.genderSp.getSelectedItem().toString();
                    sePref = binding.sexPrefSp.getSelectedItem().toString();

                    Map<String, Object> user = new HashMap<>();
                    user.put(FSConstants.USER.FIRST_NAME, firstName);
                    user.put(FSConstants.USER.LAST_NAME, lastName);
                    user.put(FSConstants.USER.DOB, dob);
                    user.put(FSConstants.USER.GENDER, gender);
                    user.put(FSConstants.USER.SEX_PREFER, sePref);
                    user.put(FSConstants.USER.DEVICE_TOKEN, AppSharedPreferences.getInstance().getString(SharedConstants.DEVICE_TOKEN));
                    Log.e("is exist :",isUserExist+"//");
                    if (isUserExist) {
                        CommonUtils.showProgress(AboutMeActivity.this);
                        FirebaseCRUD.getInstance().updateDoc(FSConstants.Collections.USERS, FirebaseAuth.getInstance().getUid(), user).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                                AppSharedPreferences.getInstance().setBoolean(SharedConstants.ABOUT_DONE, true);
                                CommonUtils.hideProgress();

                                yLog("user id about me:", FirebaseAuth.getInstance().getUid());
                                Intent i = new Intent(AboutMeActivity.this, TasteActivity.class);
                                startActivity(i);
                            }

                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull @org.jetbrains.annotations.NotNull Exception e) {
                                CommonUtils.hideProgress();
                                ySnackbar(AboutMeActivity.this, getString(R.string.error_saving_user));
                            }
                        });
                    } else {
                        CommonUtils.showProgress(AboutMeActivity.this);
                        FirebaseCRUD.getInstance().set(FSConstants.Collections.USERS, FirebaseAuth.getInstance().getUid(), user).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                                AppSharedPreferences.getInstance().setBoolean(SharedConstants.ABOUT_DONE, true);
                                CommonUtils.hideProgress();

                                yLog("user id about me:", FirebaseAuth.getInstance().getUid());
                                Intent i = new Intent(AboutMeActivity.this, TasteActivity.class);
                                startActivity(i);
                            }

                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull @org.jetbrains.annotations.NotNull Exception e) {
                                CommonUtils.hideProgress();
                                ySnackbar(AboutMeActivity.this, getString(R.string.error_saving_user));
                            }
                        });
                    }
                }
            }
        });
    }

    private void setDataReceivedFromFB() {
        binding.firstNameET.setText(LoginActivity.first_name);
        binding.lastNameET.setText(LoginActivity.last_name);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LoginActivity.first_name = "";
        LoginActivity.last_name = "";
    }

    @Override
    protected void onResume() {
        super.onResume();
        setTopBar();
        if (!firstName.isEmpty()) {
            binding.firstNameET.setText(firstName);
        }
        if (!lastName.isEmpty()) {
            binding.lastNameET.setText(lastName);
        }
        if (!dob.isEmpty()) {
            binding.dobTV.setText(dob);
        }
        if (!gender.isEmpty()) {
            binding.genderSp.setSelection(Arrays.asList(genders).indexOf(gender));
        } else {
            binding.genderSp.setSelection(0);
        }
        if (!gender.isEmpty()) {
            binding.sexPrefSp.setSelection(Arrays.asList(preferences).indexOf(sePref));
        } else {
            binding.sexPrefSp.setSelection(0);
        }
    }

    @Override
    protected void setTopBar() {
        YumTopBar.setToolbar(
                binding.topBar,
                R.drawable.ic_back_arrow,
                getString(R.string.title_about_me),
                true,
                true,
                new YumTopBar.OnToolbarClickListener() {
                    @Override
                    public void onLeftIconClick() {
                        finish();
                    }
                });
    }

    // method to reutn month name against the integer number
    public static String getMonthLetter(int month) {
        if (month == 0) {
            return "Jan";
        } else if (month == 1) {
            return "Feb";
        } else if (month == 2) {
            return "Mar";
        } else if (month == 3) {
            return "Apr";
        } else if (month == 4) {
            return "May";
        } else if (month == 5) {
            return "Jun";
        } else if (month == 6) {
            return "Jul";
        } else if (month == 7) {
            return "Aug";
        } else if (month == 8) {
            return "Sept";
        } else if (month == 9) {
            return "Oct";
        } else if (month == 10) {
            return "Nov";
        } else if (month == 11) {
            return "Dec";
        }
        return "";
    }


}
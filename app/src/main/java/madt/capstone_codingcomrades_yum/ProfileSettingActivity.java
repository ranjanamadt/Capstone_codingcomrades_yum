package madt.capstone_codingcomrades_yum;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.chip.Chip;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import madt.capstone_codingcomrades_yum.core.BaseActivity;
import madt.capstone_codingcomrades_yum.databinding.ActivityProfileSettingBinding;
import madt.capstone_codingcomrades_yum.login.LoginUserDetail;
import madt.capstone_codingcomrades_yum.login.LoginWithPhoneNumberActivity;
import madt.capstone_codingcomrades_yum.sharedpreferences.AppSharedPreferences;
import madt.capstone_codingcomrades_yum.sharedpreferences.SharedConstants;
import madt.capstone_codingcomrades_yum.utils.CommonUtils;
import madt.capstone_codingcomrades_yum.utils.FSConstants;
import madt.capstone_codingcomrades_yum.utils.FirebaseCRUD;
import madt.capstone_codingcomrades_yum.utils.YumTopBar;

public class ProfileSettingActivity extends BaseActivity {

    ActivityProfileSettingBinding binding;

    private List<String> enjoyEatingList, tasteList, interestList, notEatList, notTalkList;
    List<String> resultEating = new ArrayList<>();
    List<String> resultTastes = new ArrayList<>();
    List<String> resultInterest = new ArrayList<>();
    List<String> resultNotEat = new ArrayList<>();
    List<String> resultNotTalk = new ArrayList<>();
    List<String> otherLocations = new ArrayList<>();
    List<String> profileImageList = new ArrayList<>();
    int resultMinAge, resultMaxAge, resultMaxDistance;
    String resultLookingFor;
    Boolean checkEating = false, checkTaste = false, checkInterest = false, checkNoEat = false, checkNoTalk = false;
    final static String[] genders = {"Male", "Female", "Genderqueer/Non-Binary", "Any"};
    int minAgeSeekBar = 18;
    int maxAgeSeekBar = 18;
    int maxDistanceSeekBar = 2;
    Boolean activeStatus= false;
    Boolean statusCheck=false;
    Double editedLat, editedLng;
    LoginUserDetail userDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile_setting);

        binding.paymentBtn.setVisibility(View.GONE);

        binding.preferenceLooking.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, genders));

        binding.seekbarDistance.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                maxDistanceSeekBar = progress;
                binding.maxDistance.setText(String.valueOf(progress) + " Miles");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        binding.imgEditPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ProfileSettingActivity.this,
                        LoginWithPhoneNumberActivity.class);
                LoginWithPhoneNumberActivity.isEdit = true;
                LoginWithPhoneNumberActivity.phoneNumber = binding.number.getText().toString();
                startActivity(i);
            }
        });
        binding.seekbarMinAge.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                minAgeSeekBar = progress;
                binding.minimumAge.setText(String.valueOf(progress) + " Years");
                if (maxAgeSeekBar < minAgeSeekBar) {
                    maxAgeSeekBar = progress;
                    binding.maximumAge.setText(String.valueOf(progress) + " Years");
                }
                if(minAgeSeekBar < 18){
                    minAgeSeekBar = 18;
                    binding.minimumAge.setText(String.valueOf(minAgeSeekBar) + " Years");
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        binding.seekbarMaxAge.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                maxAgeSeekBar = progress;
                binding.maximumAge.setText(String.valueOf(maxAgeSeekBar) + " Years");
                if(maxAgeSeekBar > 100){
                    maxAgeSeekBar = 100;
                    binding.maximumAge.setText(String.valueOf(progress) + " Years");
                }
                if(maxAgeSeekBar < minAgeSeekBar){
                    maxAgeSeekBar = minAgeSeekBar;
                    binding.maximumAge.setText(String.valueOf(maxAgeSeekBar) + " Years");

                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        binding.switchAvailable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(statusCheck) {
                        changeActiveStatus(isChecked);
                }else {
                    statusCheck=true;
                }
            }
        });

        binding.imgEditLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileSettingActivity.this);
                LayoutInflater layoutInflater = LayoutInflater.from(ProfileSettingActivity.this);
                View view = layoutInflater.inflate(R.layout.dialog_new_location, null);
                builder.setView(view);

                final AlertDialog alertDialog = builder.create();
                alertDialog.show();

                EditText cityNameET = view.findViewById(R.id.cityNameET);
                Button btnAdd = view.findViewById(R.id.btnAdd);

                btnAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String cityName = cityNameET.getText().toString().trim();
                        if (cityName.isEmpty()) {
                            alertDialog.dismiss();
                            ySnackbar(ProfileSettingActivity.this, getString(R.string.err_city_name_empty));
                            return;
                        }

                        Geocoder geocoder = new Geocoder(getBaseContext(), Locale.getDefault());
                        try {
                            List<Address> addresses = geocoder.getFromLocationName(cityName, 1);

                            if (addresses.size() > 0) {
                                Address address = addresses.get(0);

                                Double latitude = address.getLatitude();
                                Double longitude = address.getLongitude();
                                editedLat = address.getLatitude();
                                editedLng = address.getLongitude();
                                binding.myLocation.setText(cityName.substring(0, 1).toUpperCase() + cityName.substring(1));

                                /*if (otherLocations != null && !otherLocations.isEmpty()) {
                                    if (!otherLocations.contains(cityName)) {
                                        otherLocations.add(cityName);
                                        addLocationChip(cityName);
                                    } else {
                                        alertDialog.dismiss();
                                    }
                                } else {
                                    otherLocations.add(cityName);
                                    addLocationChip(cityName);
                                }*/

                                //yLog("other locations: ", otherLocations.toString());
                                //ySnackbar(ProfileSettingActivity.this, "other locations: " + otherLocations.toString());
                            } else {
                                alertDialog.dismiss();
                                ySnackbar(ProfileSettingActivity.this, getString(R.string.err_city_not_found));
                                return;
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        alertDialog.dismiss();
                    }
                });
            }
        });

        binding.applyChangesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (resultEating.isEmpty()) {
                    ySnackbar(ProfileSettingActivity.this, getString(R.string.err_enjoy_eating_chip_empty));
                    return;
                }
                if (resultTastes.isEmpty()) {
                    ySnackbar(ProfileSettingActivity.this, getString(R.string.err_enjoy_taste_chip_empty));
                    return;
                }
                if (resultInterest.isEmpty()) {
                    ySnackbar(ProfileSettingActivity.this, getString(R.string.err_interest_chip_empty));
                    return;
                }
                if (resultNotEat.isEmpty()) {
                    ySnackbar(ProfileSettingActivity.this, getString(R.string.err_not_eat_chip_empty));
                    return;
                }
                if (resultNotTalk.isEmpty()) {
                    ySnackbar(ProfileSettingActivity.this, getString(R.string.err_not_talk_chip_empty));
                    return;
                }

                Map<String, Object> editProfile = new HashMap<>();
                editProfile.put(FSConstants.PREFERENCE_TYPE.ENJOY_EATING, resultEating);
                editProfile.put(FSConstants.PREFERENCE_TYPE.TASTE, resultTastes);
                editProfile.put(FSConstants.PREFERENCE_TYPE.INTEREST, resultInterest);
                editProfile.put(FSConstants.PREFERENCE_TYPE.NOT_EAT, resultNotEat);
                editProfile.put(FSConstants.PREFERENCE_TYPE.NOT_TALK, resultNotTalk);
                editProfile.put(FSConstants.USER.LATITUDE, editedLat);
                editProfile.put(FSConstants.USER.LONGITUDE, editedLng);
                editProfile.put(FSConstants.USER.MIN_AGE_PREFERENCE, minAgeSeekBar);
                editProfile.put(FSConstants.USER.MAX_AGE_PREFERENCE, maxAgeSeekBar);
                editProfile.put(FSConstants.USER.LOOKING_FOR, binding.preferenceLooking.getSelectedItem().toString());
                editProfile.put(FSConstants.USER.MAX_DISTANCE, maxDistanceSeekBar);

                FirebaseCRUD.getInstance().updateDoc(FSConstants.Collections.USERS, FirebaseAuth.getInstance().getUid(), editProfile).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        userDetail.setEnjoy_eating(resultEating);
                        userDetail.setTaste(resultTastes);
                        userDetail.setInterest(resultInterest);
                        userDetail.setNot_eat(resultNotEat);
                        userDetail.setNot_talk(resultNotTalk);
                        userDetail.setLatitude(editedLat);
                        userDetail.setLongitude(editedLng);
                        userDetail.setMinAge(minAgeSeekBar);
                        userDetail.setMaxAge(maxAgeSeekBar);
                        userDetail.setLookingFor(binding.preferenceLooking.getSelectedItem().toString());
                        userDetail.setMaxDistance(maxDistanceSeekBar);
                        AppSharedPreferences.getInstance().setString(SharedConstants.USER_DETAIL, new Gson().toJson(userDetail).toString());

                        yToast(ProfileSettingActivity.this, getString(R.string.success_updating_user));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @org.jetbrains.annotations.NotNull Exception e) {
                        CommonUtils.hideProgress();
                        ySnackbar(ProfileSettingActivity.this, getString(R.string.error_saving_user));
                    }
                });
            }
        });

        getEatingPreferences();
        getTastesPreferences();
        getNotEatPreferences();
        getNotTalkPreferences();
        getInterestsPreferences();


    }

    private void changeActiveStatus(Boolean activeStatus) {

        Map<String, Object> activeStatusRequest = new HashMap<>();
        activeStatusRequest.put(FSConstants.USER.ACTIVE_STATUS, activeStatus);



        FirebaseCRUD.getInstance().updateDoc(FSConstants.Collections.USERS, FirebaseAuth.getInstance().getUid(), activeStatusRequest).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                yToast(ProfileSettingActivity.this, getString(R.string.status_updated_sucessfully));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @org.jetbrains.annotations.NotNull Exception e) {
                CommonUtils.hideProgress();
                ySnackbar(ProfileSettingActivity.this, getString(R.string.error_saving_user));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getCurrentUserInfo();
        setTopBar();
        binding.btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtils.logoutNow(ProfileSettingActivity.this);
            }
        });
    }

    private void getCurrentUserInfo() {
        resultEating.clear();
        resultTastes.clear();
        resultInterest.clear();
        resultNotEat.clear();
        resultNotTalk.clear();
        otherLocations.clear();
        binding.chipGroupCity.removeAllViews();
        binding.chipGroupEatingPref.removeAllViews();
        binding.chipGroupNoEatPref.removeAllViews();
        binding.chipGroupNoTalkPref.removeAllViews();
        binding.chipGroupTalkPref.removeAllViews();
        binding.chipGroupTastePref.removeAllViews();

        userDetail = new Gson().fromJson(AppSharedPreferences.getInstance().getString(SharedConstants.USER_DETAIL), LoginUserDetail.class);

        FirebaseCRUD.getInstance().getDocument(FSConstants.Collections.USERS, FirebaseAuth.getInstance().getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {


                addEnjoyEating((List<String>) documentSnapshot.get(FSConstants.PREFERENCE_TYPE.ENJOY_EATING));
                addTaste((List<String>) documentSnapshot.get(FSConstants.PREFERENCE_TYPE.TASTE));
                addTalkAbout((List<String>) documentSnapshot.get(FSConstants.PREFERENCE_TYPE.INTEREST));
                addNotEat((List<String>) documentSnapshot.get(FSConstants.PREFERENCE_TYPE.NOT_EAT));
                addNotTalk((List<String>) documentSnapshot.get(FSConstants.PREFERENCE_TYPE.NOT_TALK));
                binding.number.setText((String) documentSnapshot.get(FSConstants.USER.PHONE_NUMBER));
                activeStatus =(Boolean) documentSnapshot.get(FSConstants.USER.ACTIVE_STATUS);
                binding.switchAvailable.setChecked(activeStatus);

                resultMinAge = Integer.parseInt(documentSnapshot.get(FSConstants.USER.MIN_AGE_PREFERENCE).toString());
                resultMaxAge = Integer.parseInt(documentSnapshot.get(FSConstants.USER.MAX_AGE_PREFERENCE).toString());
                resultMaxDistance = Integer.parseInt(documentSnapshot.get(FSConstants.USER.MAX_DISTANCE).toString());
                resultLookingFor = documentSnapshot.get(FSConstants.USER.LOOKING_FOR).toString();

                binding.seekbarMinAge.setProgress(resultMinAge);
                binding.seekbarMaxAge.setProgress(resultMaxAge);
                binding.seekbarDistance.setProgress(resultMaxDistance);
                int index = findIndex(genders, resultLookingFor);
                binding.preferenceLooking.setSelection(index);

                profileImageList = ((List<String>) documentSnapshot.get(FSConstants.USER.PROFILE_IMAGE));
                if(profileImageList.size() == 1){
                    binding.simpleGridView.setNumColumns(1);
                    /*binding.simpleGridView.requestLayout();
                    binding.simpleGridView.getLayoutParams().height = binding.simpleGridView.getLayoutParams().height/2;*/
                } else {
                    binding.simpleGridView.setNumColumns(2);
                }
                ImageGridAdapter imageGridAdapter = new ImageGridAdapter(getApplicationContext(), profileImageList);
                binding.simpleGridView.setAdapter(imageGridAdapter);


                String latitude = documentSnapshot.get(FSConstants.USER.LATITUDE).toString();
                String longitude = documentSnapshot.get(FSConstants.USER.LONGITUDE).toString();
                editedLat = Double.parseDouble(latitude);
                editedLng = Double.parseDouble(longitude);

                getuserlocation(Double.parseDouble(latitude), Double.parseDouble(longitude));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                CommonUtils.hideProgress();
                ySnackbar(ProfileSettingActivity.this, getString(R.string.error_saving_not_eat));
            }
        });
    }

    private void getuserlocation(Double latitude, Double longitude) {
        Geocoder gcd = new Geocoder(getApplicationContext(), Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = gcd.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) {
                binding.myLocation.setText(addresses.get(0).getLocality());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getEatingPreferences() {
        FirebaseCRUD.getInstance().getDocument(FSConstants.Collections.PREFERENCES, FSConstants.PREFERENCE_TYPE.ENJOY_EATING).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                enjoyEatingList = (List<String>) documentSnapshot.get("data");
                //setEnjoyEatingDropdown(enjoyEatingList);

                binding.spnEatingPref.setAdapter(new ArrayAdapter<String>(ProfileSettingActivity.this, android.R.layout.simple_spinner_dropdown_item, enjoyEatingList));

                binding.spnEatingPref.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (checkEating) {
                            if (resultEating != null && !resultEating.isEmpty()) {
                                if (!resultEating.contains(enjoyEatingList.get(position)))
                                    addEatingChip(enjoyEatingList.get(position));
                            } else {
                                addEatingChip(enjoyEatingList.get(position));
                            }
                        } else {
                            checkEating = true;
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }
        });
    }

    private void getTastesPreferences() {
        FirebaseCRUD.getInstance().getDocument(FSConstants.Collections.PREFERENCES, FSConstants.PREFERENCE_TYPE.TASTE).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                tasteList = (List<String>) documentSnapshot.get("data");
                //setEnjoyEatingDropdown(enjoyEatingList);

                binding.spnTastePref.setAdapter(new ArrayAdapter<String>(ProfileSettingActivity.this, android.R.layout.simple_spinner_dropdown_item, tasteList));

                binding.spnTastePref.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (checkTaste) {
                            if (resultTastes != null && !resultTastes.isEmpty()) {
                                if (!resultTastes.contains(tasteList.get(position)))
                                    addTastesChip(tasteList.get(position));
                            } else {
                                addTastesChip(tasteList.get(position));
                            }
                        } else {
                            checkTaste = true;
                        }

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }
        });
    }

    private void getNotEatPreferences() {
        FirebaseCRUD.getInstance().getDocument(FSConstants.Collections.PREFERENCES, FSConstants.PREFERENCE_TYPE.NOT_EAT).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                notEatList = (List<String>) documentSnapshot.get("data");

                binding.spnNoEatPref.setAdapter(new ArrayAdapter<String>(ProfileSettingActivity.this, android.R.layout.simple_spinner_dropdown_item, notEatList));

                binding.spnNoEatPref.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (checkNoEat) {
                            if (resultNotEat != null && !resultNotEat.isEmpty()) {
                                if (!resultNotEat.contains(notEatList.get(position)))
                                    addNoEatChip(notEatList.get(position));
                            } else {
                                addNoEatChip(notEatList.get(position));
                            }
                        } else {
                            checkNoEat = true;
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                CommonUtils.hideProgress();
                ySnackbar(ProfileSettingActivity.this, e.getLocalizedMessage());
            }
        });
    }

    private void getNotTalkPreferences() {
        FirebaseCRUD.getInstance().getDocument(FSConstants.Collections.PREFERENCES, FSConstants.PREFERENCE_TYPE.NOT_TALK).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                notTalkList = (List<String>) documentSnapshot.get("data");

                binding.spnNoTalkPref.setAdapter(new ArrayAdapter<String>(ProfileSettingActivity.this, android.R.layout.simple_spinner_dropdown_item, notTalkList));

                binding.spnNoTalkPref.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (checkNoTalk) {
                            if (resultNotTalk != null && !resultNotTalk.isEmpty()) {
                                if (!resultNotTalk.contains(notTalkList.get(position)))
                                    addNoTalkChip(notTalkList.get(position));
                            } else {
                                addNoTalkChip(notTalkList.get(position));
                            }
                        } else {
                            checkNoTalk = true;
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                CommonUtils.hideProgress();
                ySnackbar(ProfileSettingActivity.this, e.getLocalizedMessage());
            }
        });
    }

    private void getInterestsPreferences() {
        FirebaseCRUD.getInstance().getDocument(FSConstants.Collections.PREFERENCES, FSConstants.PREFERENCE_TYPE.INTEREST).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                interestList = (List<String>) documentSnapshot.get("data");

                binding.spnTalkPref.setAdapter(new ArrayAdapter<String>(ProfileSettingActivity.this, android.R.layout.simple_spinner_dropdown_item, interestList));

                binding.spnTalkPref.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (checkInterest) {
                            if (resultInterest != null && !resultInterest.isEmpty()) {
                                if (!resultInterest.contains(interestList.get(position)))
                                    addInterestChip(interestList.get(position));
                            } else {
                                addInterestChip(interestList.get(position));
                            }
                        } else {
                            checkInterest = true;
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }
        });

    }

    private void addEnjoyEating(List<String> enjoyEatingList) {
        resultEating.addAll(enjoyEatingList);
        for (String enjoyEat : enjoyEatingList) {
            Chip newChip = (Chip) getLayoutInflater().inflate(R.layout.pink_chip, binding.chipGroupEatingPref, false);
            newChip.setText(enjoyEat);
            binding.chipGroupEatingPref.addView(newChip);

            newChip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    binding.chipGroupEatingPref.removeView(v);
                    resultEating.remove(((Chip) v).getText());
                }
            });
        }
    }

    private void addTaste(List<String> tasteList) {
        resultTastes.addAll(tasteList);
        for (String taste : tasteList) {
            Chip newChip = (Chip) getLayoutInflater().inflate(R.layout.yellow_chip, binding.chipGroupTastePref, false);
            newChip.setText(taste);
            binding.chipGroupTastePref.addView(newChip);

            newChip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    binding.chipGroupTastePref.removeView(v);
                    resultTastes.remove(((Chip) v).getText());
                }
            });
        }
    }

    private void addTalkAbout(List<String> TalkList) {
        resultInterest.addAll(TalkList);
        for (String talk : TalkList) {
            Chip newChip = (Chip) getLayoutInflater().inflate(R.layout.yellow_chip, binding.chipGroupTalkPref, false);
            newChip.setText(talk);
            binding.chipGroupTalkPref.addView(newChip);

            newChip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    binding.chipGroupTalkPref.removeView(v);
                    resultInterest.remove(((Chip) v).getText());
                }
            });
        }
    }

    private void addNotEat(List<String> notEatList) {
        resultNotEat.addAll(notEatList);
        for (String notEat : notEatList) {
            Chip newChip = (Chip) getLayoutInflater().inflate(R.layout.pink_chip, binding.chipGroupNoEatPref, false);
            newChip.setText(notEat);
            binding.chipGroupNoEatPref.addView(newChip);

            newChip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    binding.chipGroupNoEatPref.removeView(v);
                    resultNotEat.remove(((Chip) v).getText());
                }
            });
        }

        // getNotTalk(FirebaseAuth.getInstance().getUid());
    }

    private void addNotTalk(List<String> notTalkList) {
        resultNotTalk.addAll(notTalkList);
        for (String notTalk : notTalkList) {
            Chip newChip = (Chip) getLayoutInflater().inflate(R.layout.yellow_chip, binding.chipGroupNoTalkPref, false);
            newChip.setText(notTalk);
            binding.chipGroupNoTalkPref.addView(newChip);

            newChip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    binding.chipGroupNoTalkPref.removeView(v);
                    resultNotTalk.remove(((Chip) v).getText());
                }
            });
        }
    }

    private void addEatingChip(String topic) {
        resultEating.add(topic);

        Chip newChip = (Chip) getLayoutInflater().inflate(R.layout.pink_chip, binding.chipGroupEatingPref, false);
        newChip.setText(topic);
        binding.chipGroupEatingPref.addView(newChip);

        newChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.chipGroupEatingPref.removeView(v);
                resultEating.remove(((Chip) v).getText());
            }
        });

    }

    private void addTastesChip(String topic) {
        resultTastes.add(topic);

        Chip newChip = (Chip) getLayoutInflater().inflate(R.layout.yellow_chip, binding.chipGroupTastePref, false);
        newChip.setText(topic);
        binding.chipGroupTastePref.addView(newChip);

        newChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.chipGroupTastePref.removeView(v);
                resultTastes.remove(((Chip) v).getText());
            }
        });
    }

    private void addInterestChip(String topic) {
        resultInterest.add(topic);

        Chip newChip = (Chip) getLayoutInflater().inflate(R.layout.yellow_chip, binding.chipGroupTalkPref, false);
        newChip.setText(topic);
        binding.chipGroupTalkPref.addView(newChip);

        newChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.chipGroupTalkPref.removeView(v);
                resultInterest.remove(((Chip) v).getText());
            }
        });
    }

    private void addNoEatChip(String topic) {
        resultNotEat.add(topic);

        Chip newChip = (Chip) getLayoutInflater().inflate(R.layout.pink_chip, binding.chipGroupNoEatPref, false);
        newChip.setText(topic);
        binding.chipGroupNoEatPref.addView(newChip);

        newChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.chipGroupNoEatPref.removeView(v);
                resultNotEat.remove(((Chip) v).getText());
            }
        });

    }

    private void addNoTalkChip(String topic) {
        resultNotTalk.add(topic);

        Chip newChip = (Chip) getLayoutInflater().inflate(R.layout.yellow_chip, binding.chipGroupNoTalkPref, false);
        newChip.setText(topic);
        binding.chipGroupNoTalkPref.addView(newChip);

        newChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.chipGroupNoTalkPref.removeView(v);
                resultNotTalk.remove(((Chip) v).getText());
            }
        });

    }

    private void addLocationChip(String location) {
        String city = location.substring(0, 1).toUpperCase() + location.substring(1);
        Chip newChip = (Chip) getLayoutInflater().inflate(R.layout.purple_chip, binding.chipGroupCity, false);
        newChip.setText(city);
        binding.chipGroupCity.addView(newChip);

        newChip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.chipGroupCity.removeView(v);
                resultTastes.remove(((Chip) v).getText());
            }
        });

    }

    @Override
    protected void setTopBar() {
        YumTopBar.setToolbar(
                binding.topBar,
                R.drawable.ic_back_arrow,
                getString(R.string.edit_settings),
                true,
                true,
                new YumTopBar.OnToolbarClickListener() {
                    @Override
                    public void onLeftIconClick() {
                        finish();
                    }
                });
    }

    public static int findIndex(String[] strArray, String pref)
    {
        if (strArray == null) {
            return -1;
        }

        int len = strArray.length;
        int i = 0;
        while (i < len) {
            if (strArray[i].equalsIgnoreCase(pref)) {
                return i;
            }
            else {
                i = i + 1;
            }
        }
        return -1;
    }

}
package madt.capstone_codingcomrades_yum;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import madt.capstone_codingcomrades_yum.core.BaseActivity;
import madt.capstone_codingcomrades_yum.createprofile.FinishProfileActivity;
import madt.capstone_codingcomrades_yum.createprofile.FoodTopicsActivity;
import madt.capstone_codingcomrades_yum.databinding.ActivityProfileSettingBinding;
import madt.capstone_codingcomrades_yum.login.LoginActivity;
import madt.capstone_codingcomrades_yum.utils.CommonUtils;
import madt.capstone_codingcomrades_yum.utils.FSConstants;
import madt.capstone_codingcomrades_yum.utils.FirebaseCRUD;

public class ProfileSettingActivity extends BaseActivity {

    ActivityProfileSettingBinding binding;


    ChipGroup chipGroupEatingPref, chipGroupTastePref, chipGroupTalkPref, chipGroupNoEatPref, chipGroupNoTalkPref;
    Spinner spnEatingPref, spnTastePref, spnTalkPref, spnNoEatPref, spnNoTalkPref, preference_looking;
    SeekBar seekBar_distance,seekbarMinAge,seekbarMaxAge;
    TextView mylocation, maxDistance, minimumAge, maximumAge;
    private List<String> enjoyEatingList, tasteList, interestList, notEatList, notTalkList;
    List<String> resultEating = new ArrayList<>();
    List<String> resultTastes = new ArrayList<>();
    List<String> resultInterest = new ArrayList<>();
    List<String> resultNotEat = new ArrayList<>();
    List<String> resultNotTalk = new ArrayList<>();
    List<String> otherLocations = new ArrayList<>();
    Boolean checkEating = false, checkTaste = false, checkInterest = false, checkNoEat = false, checkNoTalk = false;
    final static String[] genders = {"Male", "Female", "Genderqueer/Non-Binary", "Any"};
    int minAgeSeekBar = 18;
    int maxAgeSeekBar = 18;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile_setting);

        chipGroupEatingPref = findViewById(R.id.chipGroupEatingPref);
        chipGroupTastePref = findViewById(R.id.chipGroupTastePref);
        chipGroupTalkPref = findViewById(R.id.chipGroupTalkPref);
        chipGroupNoEatPref = findViewById(R.id.chipGroupNoEatPref);
        chipGroupNoTalkPref = findViewById(R.id.chipGroupNoTalkPref);
        spnEatingPref = findViewById(R.id.spnEatingPref);
        spnTastePref = findViewById(R.id.spnTastePref);
        spnTalkPref = findViewById(R.id.spnTalkPref);
        spnNoEatPref = findViewById(R.id.spnNoEatPref);
        spnNoTalkPref = findViewById(R.id.spnNoTalkPref);
        preference_looking = findViewById(R.id.preference_looking);
        seekBar_distance = findViewById(R.id.seekbar_distance);
        seekbarMinAge = findViewById(R.id.seekbarMinAge);
        seekbarMaxAge = findViewById(R.id.seekbarMaxAge);
        minimumAge = findViewById(R.id.minimumAge);
        maximumAge = findViewById(R.id.maximumAge);
        maxDistance = findViewById(R.id.maxDistance);
        mylocation = findViewById(R.id.myLocation);

        binding.preferenceLooking.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, genders));

        binding.seekbarDistance.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                binding.maxDistance.setText(String.valueOf(progress+2) + " Miles");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        binding.seekbarMinAge.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                minAgeSeekBar = progress + 18;
                binding.minimumAge.setText(String.valueOf(progress+18) + " Years");
                if(maxAgeSeekBar < minAgeSeekBar){
                    maxAgeSeekBar = progress + 18;
                    binding.maximumAge.setText(String.valueOf(progress+18) + " Years");
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
                maxAgeSeekBar = progress+minAgeSeekBar;
                binding.maximumAge.setText(String.valueOf(progress+minAgeSeekBar) + " Years");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        binding.newLocation.setOnClickListener(new View.OnClickListener() {
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
                            List<Address> addresses = geocoder.getFromLocationName(cityName,1);

                            if (addresses.size() > 0){
                                Address address = addresses.get(0);

                                Double latitude = address.getLatitude();
                                Double longitude = address.getLongitude();

                                if (otherLocations != null && !otherLocations.isEmpty()) {
                                    if (!otherLocations.contains(cityName)) {
                                        otherLocations.add(cityName);
                                        addLocationChip(cityName);
                                    } else {
                                        alertDialog.dismiss();
                                    }
                                } else {
                                    otherLocations.add(cityName);
                                    addLocationChip(cityName);
                                }

                                yLog("other locations: ", otherLocations.toString());
                                ySnackbar(ProfileSettingActivity.this, "other locations: "+ otherLocations.toString());
                            } else{
                                alertDialog.dismiss();
                                ySnackbar(ProfileSettingActivity.this, getString(R.string.err_city_not_found));
                                return;
                            }
                        }
                        catch (IOException e) {
                            e.printStackTrace();
                        }
                        alertDialog.dismiss();
                    }
                });
            }
        });

        getEatingPreferences();
        getTastesPreferences();
        getNotEatPreferences();
        getNotTalkPreferences();
        getInterestsPreferences();

        FirebaseCRUD.getInstance().getDocument(FSConstants.Collections.USERS, FirebaseAuth.getInstance().getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                addEnjoyEating((List<String>) documentSnapshot.get(FSConstants.PREFERENCE_TYPE.ENJOY_EATING));
                addTaste((List<String>) documentSnapshot.get(FSConstants.PREFERENCE_TYPE.TASTE));
                addTalkAbout((List<String>) documentSnapshot.get(FSConstants.PREFERENCE_TYPE.INTEREST));
                addNotEat((List<String>) documentSnapshot.get(FSConstants.PREFERENCE_TYPE.NOT_EAT));
                addNotTalk((List<String>) documentSnapshot.get(FSConstants.PREFERENCE_TYPE.NOT_TALK));
                //getuserlocation((Double)documentSnapshot.get(FSConstants.USER.LATITUDE), (Double)documentSnapshot.get(FSConstants.USER.LONGITUDE));

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
                        if(checkEating){
                            if (resultEating != null && !resultEating.isEmpty()) {
                                if (!resultEating.contains(enjoyEatingList.get(position)))
                                    addEatingChip(enjoyEatingList.get(position));
                            } else {
                                addEatingChip(enjoyEatingList.get(position));
                            }
                        } else{
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
                        if(checkTaste){
                            if (resultTastes != null && !resultTastes.isEmpty()) {
                                if (!resultTastes.contains(tasteList.get(position)))
                                    addTastesChip(tasteList.get(position));
                            } else {
                                addTastesChip(tasteList.get(position));
                            }
                        } else{
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
                        if(checkNoEat){
                            if (resultNotEat != null && !resultNotEat.isEmpty()) {
                                if (!resultNotEat.contains(notEatList.get(position)))
                                    addNoEatChip(notEatList.get(position));
                            } else {
                                addNoEatChip(notEatList.get(position));
                            }
                        } else{
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
                        if(checkNoTalk){
                            if (resultNotTalk != null && !resultNotTalk.isEmpty()) {
                                if (!resultNotTalk.contains(notTalkList.get(position)))
                                    addNoTalkChip(notTalkList.get(position));
                            } else {
                                addNoTalkChip(notTalkList.get(position));
                            }
                        } else{
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
                        if(checkInterest){
                            if (resultInterest != null && !resultInterest.isEmpty()) {
                                if (!resultInterest.contains(interestList.get(position)))
                                    addInterestChip(interestList.get(position));
                            } else {
                                addInterestChip(interestList.get(position));
                            }
                        } else{
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

    }
}
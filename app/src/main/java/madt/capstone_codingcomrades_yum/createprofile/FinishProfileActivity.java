package madt.capstone_codingcomrades_yum.createprofile;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.chip.Chip;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.gson.Gson;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import madt.capstone_codingcomrades_yum.HomeActivity;
import madt.capstone_codingcomrades_yum.R;
import madt.capstone_codingcomrades_yum.SliderAdapter;
import madt.capstone_codingcomrades_yum.User;
import madt.capstone_codingcomrades_yum.core.BaseActivity;
import madt.capstone_codingcomrades_yum.databinding.ActivityFinishProfileBinding;
import madt.capstone_codingcomrades_yum.login.LoginActivity;
import madt.capstone_codingcomrades_yum.login.LoginUserDetail;
import madt.capstone_codingcomrades_yum.matcheslisting.MatchesFragment;
import madt.capstone_codingcomrades_yum.sharedpreferences.AppSharedPreferences;
import madt.capstone_codingcomrades_yum.sharedpreferences.SharedConstants;
import madt.capstone_codingcomrades_yum.utils.CommonUtils;
import madt.capstone_codingcomrades_yum.utils.FSConstants;
import madt.capstone_codingcomrades_yum.utils.FirebaseCRUD;
import madt.capstone_codingcomrades_yum.utils.YumTopBar;


public class FinishProfileActivity extends BaseActivity {
    private ActivityFinishProfileBinding binding;

    FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;
    LocationCallback locationCallback;
    private static final int LOCATION_REQUEST_CODE = 1;
    private static final int PROFILE_RESULT_CODE = 2;
    List<String> preferences=new ArrayList<>();


    String latitude = "";
    String longitude = "";
    Uri uri;
    String profileImgString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_finish_profile);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        if (!hasLocationPermission())
            requestLocationPermission();
        else
            startUpdateLocation();

        CommonUtils.showProgress(this);
        FirebaseCRUD.getInstance().getDocument(FSConstants.Collections.USERS, FirebaseAuth.getInstance().getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                yLog("user id :", documentSnapshot.getId() + " ");

                String username = documentSnapshot.getString(FSConstants.USER.FIRST_NAME) + " " + documentSnapshot.getString(FSConstants.USER.LAST_NAME);

                binding.tvUserName.setText(username);
                binding.tvGender.setText(documentSnapshot.getString(FSConstants.USER.GENDER));

                Date dobObj = null;
                long age = 0;
                try {
                    dobObj = new SimpleDateFormat("MMM d, yyyy").parse(documentSnapshot.getString(FSConstants.USER.DOB));
                    Date today = new Date();
                    long difference_In_Time = today.getTime() - dobObj.getTime();
                    age = (difference_In_Time / (1000l * 60 * 60 * 24 * 365));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                binding.tvAge.setText(String.valueOf(age) + " years");

                addEnjoyEating((List<String>) documentSnapshot.get(FSConstants.PREFERENCE_TYPE.ENJOY_EATING));
                addTaste((List<String>) documentSnapshot.get(FSConstants.PREFERENCE_TYPE.TASTE));
                addNotEat((List<String>) documentSnapshot.get(FSConstants.PREFERENCE_TYPE.NOT_EAT));
                addNotTalk((List<String>) documentSnapshot.get(FSConstants.PREFERENCE_TYPE.NOT_TALK));

                preferences.addAll((List<String>) documentSnapshot.get(FSConstants.PREFERENCE_TYPE.ENJOY_EATING));
                preferences.addAll((List<String>) documentSnapshot.get(FSConstants.PREFERENCE_TYPE.TASTE));
                preferences.addAll((List<String>) documentSnapshot.get(FSConstants.PREFERENCE_TYPE.NOT_EAT));
                preferences.addAll((List<String>) documentSnapshot.get(FSConstants.PREFERENCE_TYPE.NOT_TALK));
                preferences.addAll((List<String>) documentSnapshot.get(FSConstants.PREFERENCE_TYPE.INTEREST));

                if (!LoginActivity.profile_image.isEmpty())
                    Picasso.get().load(LoginActivity.profile_image).into(binding.imageBtn);


                // getEnjoyEating(FirebaseAuth.getInstance().getUid());
                CommonUtils.hideProgress();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                CommonUtils.hideProgress();
                ySnackbar(FinishProfileActivity.this, getString(R.string.error_saving_not_eat));
            }
        });

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.with(FinishProfileActivity.this)
                        .crop(4f, 3f)                    //Crop image(Optional), Check Customization for more option
                        .compress(1024)            //Final image size will be less than 1 MB(Optional)
                        .maxResultSize(1080, 1080)    //Final image resolution will be less than 1080 x 1080(Optional)
                        .start(PROFILE_RESULT_CODE);
            }
        });

        binding.btnConfirmFinishProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String aboutMe = binding.etAboutMe.getText().toString().trim();
                if (aboutMe.isEmpty()) {
                    ySnackbar(FinishProfileActivity.this, getString(R.string.err_about_me_empty));
                    return;
                } else if (uri == null) {
                    ySnackbar(FinishProfileActivity.this, getString(R.string.err_profile_image_empty));
                    return;
                }

                yLog("image uri", uri.toString());
                yLog("profileImageString", profileImgString);

                Map<String, Object> finishProfile = new HashMap<>();
                finishProfile.put(FSConstants.USER.LATITUDE, latitude);
                finishProfile.put(FSConstants.USER.LONGITUDE, longitude);
                finishProfile.put(FSConstants.USER.ABOUT_ME, aboutMe);
                finishProfile.put(FSConstants.USER.PROFILE_IMAGE, profileImgString);
                finishProfile.put(FSConstants.USER.PREFERENCES, preferences);

                CommonUtils.showProgress(FinishProfileActivity.this);

                FirebaseCRUD.getInstance().updateDoc(FSConstants.Collections.USERS, FirebaseAuth.getInstance().getUid(), finishProfile).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //ySnackbar(FinishProfileActivity.this, "data saved successfully");

                        getCurrentUser();
                    }

                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @org.jetbrains.annotations.NotNull Exception e) {
                        CommonUtils.hideProgress();
                        ySnackbar(FinishProfileActivity.this, getString(R.string.error_saving_user));
                    }
                });

            }
        });
    }

    private void getCurrentUser() {

        FirebaseCRUD.getInstance().getDocument(FSConstants.Collections.USERS, FirebaseAuth.getInstance().getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {

            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                if (documentSnapshot.exists()) {


                    User user = new User(documentSnapshot);

                    LoginUserDetail loginUserDetail = new LoginUserDetail(
                            user.getUuid(),
                            user.getFirstName(),
                            user.getLastName(),
                            user.getDob(),
                            user.getGender(),
                            user.getSePref(),
                            user.getAboutMe(),
                            user.getProfileImage(),
                            user.getInterest(),
                            user.getNot_eat(),
                            user.getNot_talk(),
                            user.getTaste(),
                            user.getEnjoy_eating(),
                            user.getReport_list(),
                            user.getPreferences()
                    );

                    yLog("loginUserDetail: ", new Gson().toJson(loginUserDetail));
                    AppSharedPreferences.getInstance().setString(SharedConstants.USER_DETAIL, new Gson().toJson(loginUserDetail).toString());
                    AppSharedPreferences.getInstance().setBoolean(SharedConstants.FINISH_PROFILE_DONE, true);
                    Intent i = new Intent(FinishProfileActivity.this,
                            HomeActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);

                }

                CommonUtils.hideProgress();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                ySnackbar(FinishProfileActivity.this, getString(R.string.error_saving_user));
                CommonUtils.hideProgress();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PROFILE_RESULT_CODE) {
            uri = data.getData();
            binding.imageBtn.setImageURI(uri);

            InputStream imageStream = null;
            try {
                imageStream = getContentResolver().openInputStream(uri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            Bitmap profileImgBitmap = BitmapFactory.decodeStream(imageStream);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            profileImgBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] profileImgByte = baos.toByteArray();
            profileImgString = Base64.encodeToString(profileImgByte, Base64.DEFAULT);
        }
    }

    private boolean hasLocationPermission() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
    }

    private void startUpdateLocation() {
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(3000);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null) {
                    Location location = locationResult.getLastLocation();
                    latitude = String.valueOf(location.getLatitude());
                    longitude = String.valueOf(location.getLongitude());
                }
            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_REQUEST_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    new AlertDialog.Builder(this)
                            .setMessage("The permission is mandatory")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ActivityCompat.requestPermissions(FinishProfileActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
                                }
                            }).create().show();
                } else
                    startUpdateLocation();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setTopBar();
    }

    @Override
    protected void setTopBar() {
        YumTopBar.setToolbar(
                binding.topBar,
                R.drawable.ic_back_arrow,
                getString(R.string.title_finish_profile),
                true,
                true,
                new YumTopBar.OnToolbarClickListener() {
                    @Override
                    public void onLeftIconClick() {
                        finish();
                    }
                });
    }

    private void addEnjoyEating(List<String> enjoyEatingList) {
        for (String enjoyEat : enjoyEatingList) {
            Chip newChip = (Chip) getLayoutInflater().inflate(R.layout.pink_chip_without_close, binding.chipGroupEnjoyEat, false);
            newChip.setText(enjoyEat);
            binding.chipGroupEnjoyEat.addView(newChip);
        }

        // getTaste(FirebaseAuth.getInstance().getUid());
    }

    private void addTaste(List<String> tasteList) {
        for (String taste : tasteList) {
            Chip newChip = (Chip) getLayoutInflater().inflate(R.layout.yellow_chip_without_close, binding.chipGroupDownToEat, false);
            newChip.setText(taste);
            binding.chipGroupDownToEat.addView(newChip);
        }

        //getNotEat(FirebaseAuth.getInstance().getUid());
    }

    /*private void addTalkAbout(List<String> talkAbout) {
        Chip newChip = (Chip) getLayoutInflater().inflate(R.layout.pink_chip, binding.chipg, false);
        newChip.setText(topic);
        binding.chipGroupNoFood.addView(newChip);
    }*/

    private void addNotEat(List<String> notEatList) {
        for (String notEat : notEatList) {
            Chip newChip = (Chip) getLayoutInflater().inflate(R.layout.pink_chip_without_close, binding.chipGroupNotEat, false);
            newChip.setText(notEat);
            binding.chipGroupNotEat.addView(newChip);
        }

        // getNotTalk(FirebaseAuth.getInstance().getUid());
    }

    private void addNotTalk(List<String> notTalkList) {
        for (String notTalk : notTalkList) {
            Chip newChip = (Chip) getLayoutInflater().inflate(R.layout.yellow_chip_without_close, binding.chipGroupNotTalk, false);
            newChip.setText(notTalk);
            binding.chipGroupNotTalk.addView(newChip);

            newChip.setOnCloseIconClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    binding.chipGroupNotTalk.removeView(v);
                }
            });
        }
    }
}
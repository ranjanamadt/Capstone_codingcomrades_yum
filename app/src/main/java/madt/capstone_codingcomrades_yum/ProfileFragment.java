package madt.capstone_codingcomrades_yum;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import madt.capstone_codingcomrades_yum.createprofile.FinishProfileActivity;
import madt.capstone_codingcomrades_yum.databinding.FragmentProfileBinding;
import madt.capstone_codingcomrades_yum.login.LoginActivity;
import madt.capstone_codingcomrades_yum.sharedpreferences.AppSharedPreferences;
import madt.capstone_codingcomrades_yum.sharedpreferences.SharedConstants;
import madt.capstone_codingcomrades_yum.utils.CommonUtils;
import madt.capstone_codingcomrades_yum.utils.FSConstants;
import madt.capstone_codingcomrades_yum.utils.FirebaseCRUD;
import madt.capstone_codingcomrades_yum.utils.ProfileSettings;

public class ProfileFragment extends Fragment {
    FragmentProfileBinding binding;

    SliderView sliderView;
    List<Uri> profileImagesUriList = new ArrayList<>();
    List<String> profileImagesStringList = new ArrayList<>();
    ActivityResultLauncher<Intent> someActivityResultLauncher;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false);

        String userDetailStr = AppSharedPreferences.getInstance().getString(SharedConstants.USER_DETAIL);

        if(userDetailStr != null && userDetailStr.length() > 0){
            JSONObject userDetailJson = new JSONObject();
            try {
                userDetailJson = new JSONObject(userDetailStr);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                String fullName = userDetailJson.getString("firstName") + " " + userDetailJson.getString("lastName");
                binding.proUserName.setText(fullName);

                Date dobObj = null;
                long age = 0;
                try {
                    dobObj = new SimpleDateFormat("MMM d, yyyy").parse(userDetailJson.getString("dob"));
                    Date today = new Date();
                    long difference_In_Time = today.getTime() - dobObj.getTime();
                    age = (difference_In_Time / (1000l * 60 * 60 * 24 * 365));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                binding.proAge.setText(String.valueOf(age) + " years");

                /*if(userDetailJson.getString("profileImage") != null){

                    byte[] previousImg = userDetailJson.getString("profileImage").getBytes();
                    String previousStr = new String(previousImg, "UTF-8");
                    Uri previousUri = Uri.parse(previousStr);
                    profileImagesUriList.add(previousUri);

                    SliderAdapter sliderAdapter = new SliderAdapter(profileImagesUriList);
                    binding.imageSlider.setSliderAdapter(sliderAdapter);
                    binding.imageSlider.setIndicatorAnimation(IndicatorAnimationType.WORM);
                    //sliderView.setCustomSliderTransformAnimation(SliderAnimations.DEPTHTRANSFORMATION);
                    binding.imageSlider.startAutoCycle();
                }*/
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        binding.addPictures.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*ImagePicker.with(ProfileFragment.this)
                        .crop(4f, 3f)                    //Crop image(Optional), Check Customization for more option
                        .compress(1024)            //Final image size will be less than 1 MB(Optional)
                        .maxResultSize(1080, 1080)    //Final image resolution will be less than 1080 x 1080(Optional)
                        .start();*/
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                someActivityResultLauncher.launch(intent);
            }
        });

        someActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {

                        // There are no request codes
                        Intent data = result.getData();
                        profileImagesUriList.add(data.getData());

                        SliderAdapter sliderAdapter = new SliderAdapter(profileImagesUriList);
                        binding.imageSlider.setSliderAdapter(sliderAdapter);
                        binding.imageSlider.setIndicatorAnimation(IndicatorAnimationType.WORM);
                        //sliderView.setCustomSliderTransformAnimation(SliderAnimations.DEPTHTRANSFORMATION);
                        binding.imageSlider.startAutoCycle();

                        Context applicationContext = HomeActivity.getContextOfApplication();
                        InputStream imageStream = null;
                        try {
                            imageStream = applicationContext.getContentResolver().openInputStream(data.getData());
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        Bitmap profileImgBitmap = BitmapFactory.decodeStream(imageStream);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        profileImgBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] profileImgByte = baos.toByteArray();
                        String profileImgString = Base64.encodeToString(profileImgByte, Base64.DEFAULT);
                        profileImagesStringList.add(profileImgString);

                        Map<String, Object> addImages = new HashMap<>();
                        addImages.put(FSConstants.USER.PROFILE_IMAGE, profileImagesStringList);


                        //CommonUtils.showProgress(FinishProfileActivity.this);

                        FirebaseCRUD.getInstance().updateDoc(FSConstants.Collections.USERS, FirebaseAuth.getInstance().getUid(), addImages).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                //ySnackbar(FinishProfileActivity.this, "data saved successfully");

                                //getCurrentUser();
                            }

                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull @org.jetbrains.annotations.NotNull Exception e) {
                                //CommonUtils.hideProgress();
                                //ySnackbar(FinishProfileActivity.this, getString(R.string.error_saving_user));
                            }
                        });
                    }
                });

        binding.profileSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), ProfileSettingActivity.class);
                startActivity(i);
            }
        });

        binding.btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtils.logoutNow(getActivity());

            }
        });

        return binding.getRoot();
    }
}
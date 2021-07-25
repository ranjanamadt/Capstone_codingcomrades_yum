package madt.capstone_codingcomrades_yum;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import madt.capstone_codingcomrades_yum.core.BaseFragment;
import madt.capstone_codingcomrades_yum.databinding.FragmentProfileBinding;
import madt.capstone_codingcomrades_yum.login.LoginUserDetail;
import madt.capstone_codingcomrades_yum.sharedpreferences.AppSharedPreferences;
import madt.capstone_codingcomrades_yum.sharedpreferences.SharedConstants;
import madt.capstone_codingcomrades_yum.utils.FSConstants;
import madt.capstone_codingcomrades_yum.utils.FirebaseCRUD;

public class ProfileFragment extends BaseFragment {
    FragmentProfileBinding binding;

    SliderView sliderView;
    List<Bitmap> profileImagesUriList = new ArrayList<>();
    List<String> profileImagesStringList = new ArrayList<>();
    ActivityResultLauncher<Intent> someActivityResultLauncher;
    LoginUserDetail userDetail;
    //JSONObject userDetailJson;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false);

        userDetail = new Gson().fromJson(AppSharedPreferences.getInstance().getString(SharedConstants.USER_DETAIL), LoginUserDetail.class);

        try {
            String fullName = userDetail.getFullName();
            binding.proUserName.setText(fullName);

            Date dobObj = null;
            long age = 0;
            try {
                dobObj = new SimpleDateFormat("MMM d, yyyy").parse(userDetail.getDob());
                Date today = new Date();
                long difference_In_Time = today.getTime() - dobObj.getTime();
                age = (difference_In_Time / (1000l * 60 * 60 * 24 * 365));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            binding.proAge.setText(String.valueOf(age) + " years");


            yLog("userDetail.getProfileImage().size(): ", String.valueOf(userDetail.getProfileImage().size()));
            if (userDetail.getProfileImage().size() > 0) {
                profileImagesStringList = new ArrayList<>();

                for (int i = 0; i < userDetail.getProfileImage().size(); i++) {
                    byte[] fireStoreImg = userDetail.getProfileImage().get(i).getBytes();
                    String fireStoreStr = new String(fireStoreImg, "UTF-8");
                    byte[] fireStoreEncodeByte = Base64.decode(fireStoreStr, Base64.DEFAULT);
                    Bitmap fireStoreBitmap = BitmapFactory.decodeByteArray(fireStoreEncodeByte, 0, fireStoreEncodeByte.length);
                    profileImagesUriList.add(fireStoreBitmap);
                    profileImagesStringList.add(userDetail.getProfileImage().get(i));
                }

                SliderAdapter sliderAdapter = new SliderAdapter(profileImagesUriList);
                binding.imageSlider.setSliderAdapter(sliderAdapter);
                binding.imageSlider.setIndicatorAnimation(IndicatorAnimationType.WORM);
                //sliderView.setCustomSliderTransformAnimation(SliderAnimations.DEPTHTRANSFORMATION);
                binding.imageSlider.startAutoCycle();

            } /*else if (userDetailJson.getString(FSConstants.USER.PROFILE_IMAGE) != null){
                byte[] previousImg = userDetailJson.getString(FSConstants.USER.PROFILE_IMAGE).getBytes();
                String previousStr = new String(previousImg, "UTF-8");
                byte[] encodeByte = Base64.decode(previousStr, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
                profileImagesUriList.add(bitmap);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] profileImgByte = baos.toByteArray();
                String profileImgString = Base64.encodeToString(profileImgByte, Base64.DEFAULT);
                profileImagesStringList.add(profileImgString);

                SliderAdapter sliderAdapter = new SliderAdapter(profileImagesUriList);
                binding.imageSlider.setSliderAdapter(sliderAdapter);
                binding.imageSlider.setIndicatorAnimation(IndicatorAnimationType.WORM);
                //sliderView.setCustomSliderTransformAnimation(SliderAnimations.DEPTHTRANSFORMATION);
                binding.imageSlider.startAutoCycle();
            }*/
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


        binding.addPictures.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                        Context applicationContext = HomeActivity.getContextOfApplication();

                        // There are no request codes
                        Intent data = result.getData();
                        try {
                            if (data != null) {
                                profileImagesUriList.add(MediaStore.Images.Media.getBitmap(applicationContext.getContentResolver(), data.getData()));


                                SliderAdapter sliderAdapter = new SliderAdapter(profileImagesUriList);
                                binding.imageSlider.setSliderAdapter(sliderAdapter);
                                binding.imageSlider.setIndicatorAnimation(IndicatorAnimationType.WORM);
                                //sliderView.setCustomSliderTransformAnimation(SliderAnimations.DEPTHTRANSFORMATION);
                                binding.imageSlider.startAutoCycle();

                                InputStream imageStream = null;

                                if (data != null)
                                    imageStream = applicationContext.getContentResolver().openInputStream(data.getData());

                                Bitmap profileImgBitmap = BitmapFactory.decodeStream(imageStream);
                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                profileImgBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                byte[] profileImgByte = baos.toByteArray();
                                String profileImgString = Base64.encodeToString(profileImgByte, Base64.DEFAULT);
                                profileImagesStringList.add(profileImgString);

                                Map<String, Object> addImages = new HashMap<>();
                                addImages.put(FSConstants.USER.PROFILE_IMAGE, profileImagesStringList);


                                //CommonUtils.showProgress(FinishProfileActivity.this);
                                yLog("profileImagesStringList length : ", String.valueOf(profileImagesStringList.size()));
                                FirebaseCRUD.getInstance().updateDoc(FSConstants.Collections.USERS, FirebaseAuth.getInstance().getUid(), addImages).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        //userDetailJson.put(FSConstants.USER.PROFILE_IMAGE, profileImagesStringList);
                                        userDetail.setProfileImage(profileImagesStringList);
                                        AppSharedPreferences.getInstance().setString(SharedConstants.USER_DETAIL, new Gson().toJson(userDetail).toString());
                                    }

                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull @org.jetbrains.annotations.NotNull Exception e) {
                                        //CommonUtils.hideProgress();
                                        //ySnackbar(FinishProfileActivity.this, getString(R.string.error_saving_user));
                                    }
                                });
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });

        binding.profileSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), ProfileSettingActivity.class);
                startActivity(i);
            }
        });


        return binding.getRoot();
    }
}
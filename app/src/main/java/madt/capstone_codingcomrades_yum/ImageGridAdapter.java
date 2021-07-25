package madt.capstone_codingcomrades_yum;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import madt.capstone_codingcomrades_yum.login.LoginUserDetail;
import madt.capstone_codingcomrades_yum.sharedpreferences.AppSharedPreferences;
import madt.capstone_codingcomrades_yum.sharedpreferences.SharedConstants;
import madt.capstone_codingcomrades_yum.utils.CommonUtils;
import madt.capstone_codingcomrades_yum.utils.FSConstants;
import madt.capstone_codingcomrades_yum.utils.FirebaseCRUD;

public class ImageGridAdapter extends BaseAdapter {

    List<String> profileImageList = new ArrayList<>();
    LayoutInflater inflter;
    LoginUserDetail userDetail = new Gson().fromJson(AppSharedPreferences.getInstance().getString(SharedConstants.USER_DETAIL), LoginUserDetail.class);

    public ImageGridAdapter(Context applicationContext, List<String> profileImageList) {
        this.profileImageList = profileImageList;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return profileImageList.size();
    }

    @Override
    public Object getItem(int i) {
        return profileImageList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        /*ViewHolder viewHolder;

        if(convertView == null){
            convertView = inflter.inflate(R.layout.image_gridview, null);

            viewHolder = new ViewHolder();
            viewHolder.imageView = convertView.findViewById(R.id.profileImage);
        } else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Log.d("Imageadapet", "profileImageList.get(i): " + profileImageList.get(i));
        byte[] profileImageByteArray = profileImageList.get(i).getBytes();
        String profileImageString = null;
        try {
            profileImageString = new String(profileImageByteArray, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        byte[] profileImageEncodeByte = Base64.decode(profileImageString, Base64.DEFAULT);
        Bitmap profileImageBitmap = BitmapFactory.decodeByteArray(profileImageEncodeByte, 0, profileImageEncodeByte.length);
        viewHolder.imageView.setImageBitmap(profileImageBitmap); // set logo images
        return convertView;

        *//*byte[] fireStoreImg = userDetail.getProfileImage().get(i).getBytes();
        String fireStoreStr = new String(fireStoreImg, "UTF-8");
        byte[] fireStoreEncodeByte = Base64.decode(fireStoreStr, Base64.DEFAULT);
        Bitmap fireStoreBitmap = BitmapFactory.decodeByteArray(fireStoreEncodeByte, 0, fireStoreEncodeByte.length);
        profileImagesUriList.add(fireStoreBitmap);*/

        convertView = inflter.inflate(R.layout.image_gridview, null); // inflate the layout
        ImageView icon = (ImageView) convertView.findViewById(R.id.profileImage); // get the reference of ImageView
        ImageView close = (ImageView) convertView.findViewById(R.id.close);

        byte[] profileImageByteArray = profileImageList.get(i).getBytes();
        String profileImageString = null;
        try {
            profileImageString = new String(profileImageByteArray, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        byte[] profileImageEncodeByte = Base64.decode(profileImageString, Base64.DEFAULT);
        Bitmap profileImageBitmap = BitmapFactory.decodeByteArray(profileImageEncodeByte, 0, profileImageEncodeByte.length);
        icon.setImageBitmap(profileImageBitmap); // set logo images

        /*Log.e("size adapter: ", "" + profileImageList.size());
        if(profileImageList.size() == 1){
            icon.requestLayout();
            icon.getLayoutParams().height = icon.getLayoutParams().height * 2;
        }*/

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("before adapter: ", "profileImageList size: "+ profileImageList.size());
                profileImageList.remove(i);
                Log.e("after adapter: ", "profileImageList size: "+ profileImageList.size());

                icon.setVisibility(View.GONE);
                close.setVisibility(View.GONE);

                Map<String, Object> profileImageObj = new HashMap<>();
                profileImageObj.put(FSConstants.USER.PROFILE_IMAGE, profileImageList);

                FirebaseCRUD.getInstance().updateDoc(FSConstants.Collections.USERS, FirebaseAuth.getInstance().getUid(), profileImageObj).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.e("success: ", "success profileImageList size: "+ profileImageList.size());
                        userDetail.setProfileImage(profileImageList);
                        AppSharedPreferences.getInstance().setString(SharedConstants.USER_DETAIL, new Gson().toJson(userDetail).toString());
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @org.jetbrains.annotations.NotNull Exception e) {
                        CommonUtils.hideProgress();
                    }
                });
            }
        });

        return convertView;
    }

    private class ViewHolder{
        ImageView imageView;
    }
}

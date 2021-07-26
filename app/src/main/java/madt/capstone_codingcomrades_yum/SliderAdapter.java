package madt.capstone_codingcomrades_yum;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.smarteist.autoimageslider.SliderViewAdapter;
import com.squareup.picasso.Picasso;

import java.io.UnsupportedEncodingException;
import java.util.List;

public class SliderAdapter extends SliderViewAdapter<SliderAdapter.Holder> {

    List<String> images;

    public SliderAdapter(List<String> images){
        this.images = images;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.profile_img_slider_item, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(Holder viewHolder, int position) {
        byte[] fireStoreImg = images.get(position).getBytes();
        String fireStoreStr = null;
        try {
            fireStoreStr = new String(fireStoreImg, "UTF-8");
            byte[] fireStoreEncodeByte = Base64.decode(fireStoreStr, Base64.DEFAULT);
            Bitmap fireStoreBitmap = BitmapFactory.decodeByteArray(fireStoreEncodeByte, 0, fireStoreEncodeByte.length);
            viewHolder.imageView.setImageBitmap(fireStoreBitmap);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getCount() {
        return images.size();
    }

    public class Holder extends SliderViewAdapter.ViewHolder{
        ImageView imageView;

        public Holder(View itemView){
            super(itemView);

            imageView = itemView.findViewById(R.id.imageViewSlider);
        }
    }
}

package madt.capstone_codingcomrades_yum.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import madt.capstone_codingcomrades_yum.R;


public class YumTopBar extends ConstraintLayout {

    static Context mContext;
    static OnToolbarClickListener onToolbarClickListener;

    public YumTopBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init(context);
    }

    public YumTopBar(Context context) {
        super(context);
        init(context);
    }

    public YumTopBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public static YumTopBar setToolbar(YumTopBar toolBar, int leftIcon, String screenTitle,
                                       Boolean showLeftIcon, Boolean showTitle,final OnToolbarClickListener onClickListener) {
        ImageView imgLeftIcon = (ImageView) toolBar.findViewById(R.id.imgLeftIcon);
        TextView txtTitle = (TextView) toolBar.findViewById(R.id.txtTitle);

        onToolbarClickListener = onClickListener;
        if (showLeftIcon) {
            imgLeftIcon.setVisibility(View.VISIBLE);
            imgLeftIcon.setImageResource(leftIcon);
        } else {
            imgLeftIcon.setVisibility(View.INVISIBLE);
        }
        if (showTitle) {
            txtTitle.setText(screenTitle);
            txtTitle.setVisibility(View.VISIBLE);
        } else {
            txtTitle.setVisibility(View.GONE);
        }
        imgLeftIcon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                onToolbarClickListener.onLeftIconClick();
            }
        });

        return toolBar;
    }


    private void init(Context context) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.view_top_bar, this);
    }

    public interface OnToolbarClickListener {
        public void onLeftIconClick();

    }

}



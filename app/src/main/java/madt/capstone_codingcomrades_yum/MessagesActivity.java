package madt.capstone_codingcomrades_yum;

import android.os.Bundle;
import android.os.PersistableBundle;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import madt.capstone_codingcomrades_yum.core.BaseActivity;
import madt.capstone_codingcomrades_yum.databinding.ActivityMessagesBinding;

public class MessagesActivity extends BaseActivity {

    private ActivityMessagesBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_messages);

    }

    @Override
    protected void setTopBar() {

    }
}

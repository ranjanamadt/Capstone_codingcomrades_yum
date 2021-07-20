package madt.capstone_codingcomrades_yum.chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import madt.capstone_codingcomrades_yum.R;
import madt.capstone_codingcomrades_yum.databinding.FragmentChatBinding;


public class ChatFragment extends Fragment {
    private FragmentChatBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_chat, container, false);

        return binding.getRoot();
    }
}
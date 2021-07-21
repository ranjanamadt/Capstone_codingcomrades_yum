package madt.capstone_codingcomrades_yum.chat;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import madt.capstone_codingcomrades_yum.R;
import madt.capstone_codingcomrades_yum.databinding.FragmentChatBinding;


public class ChatFragment extends Fragment {
    private FragmentChatBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_chat, container, false);
        return binding.getRoot();
    }

    class ChatElementAdapter extends RecyclerView.Adapter<ChatElementAdapter.ChatViewHolder> {
        private Activity activity;
        private ArrayList<Message> chatList;

        ChatElementAdapter(Activity activity, ArrayList<Message> chatList) {
            this.activity = activity;
            this.chatList = chatList;
        }

        @NonNull
        @NotNull
        @Override
        public ChatFragment.ChatElementAdapter.ChatViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_chatlist, parent, false);

            return new ChatViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull @NotNull ChatFragment.ChatElementAdapter.ChatViewHolder holder, int position) {

        }


        @Override
        public int getItemCount() {
            return chatList.size();
        }
        class ChatViewHolder extends RecyclerView.ViewHolder {

            TextView usernameTV,dateTV, timeTV, lastMessageTV;

            public ChatViewHolder(@NonNull View itemView) {
                super(itemView);

                usernameTV = itemView.findViewById(R.id.tv_name);
                dateTV = itemView.findViewById(R.id.tv_days);
                timeTV = itemView.findViewById(R.id.tv_time);
                lastMessageTV = itemView.findViewById(R.id.tv_description);
            }
        }

    }

}
package madt.capstone_codingcomrades_yum.chat;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

import madt.capstone_codingcomrades_yum.R;
import madt.capstone_codingcomrades_yum.core.BaseActivity;
import madt.capstone_codingcomrades_yum.databinding.ActivityMessagesBinding;
import madt.capstone_codingcomrades_yum.databinding.FragmentChatBinding;
import madt.capstone_codingcomrades_yum.databinding.MatchChatBinding;
import madt.capstone_codingcomrades_yum.databinding.MatchChatBindingImpl;
import madt.capstone_codingcomrades_yum.login.LoginUserDetail;
import madt.capstone_codingcomrades_yum.sharedpreferences.AppSharedPreferences;
import madt.capstone_codingcomrades_yum.sharedpreferences.SharedConstants;
import madt.capstone_codingcomrades_yum.utils.FSConstants;
import madt.capstone_codingcomrades_yum.utils.FirebaseCRUD;


public class MessageChatActivity extends BaseActivity {
    private MatchChatBinding binding;

    protected LoginUserDetail mLoginDetail;
    private ChatElementAdapter chatAdapter;
    private ArrayList<Message> chatList = new ArrayList<>();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.match_chat);
        mLoginDetail = new Gson().fromJson(AppSharedPreferences.getInstance().getString(SharedConstants.USER_DETAIL), LoginUserDetail.class);
//
        binding.messagesChatRV.setLayoutManager(new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL,false));

        String collectionID = FSConstants.Collections.USERS + "/" + FirebaseAuth.getInstance().getUid() + "/" + FSConstants.Collections.CHATROOM;
        if (getIntent().hasExtra(FSConstants.CHAT_List.CHAT_ID)) {
            FirebaseCRUD.getInstance().getDocument(collectionID , getIntent().getStringExtra(FSConstants.CHAT_List.CHAT_ID)).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    for (HashMap<String, Object> messageObj : (ArrayList<HashMap<String, Object>>) documentSnapshot.get(FSConstants.CHAT_List.MESSAGES)) {
                        chatList.add(new Message(messageObj));
                    }
                    binding.messagesChatRV.setAdapter(new ChatElementAdapter(MessageChatActivity.this, chatList));
                }
            });

        }
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
        public MessageChatActivity.ChatElementAdapter.ChatViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_receive, parent, false);

            return new ChatViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull @NotNull MessageChatActivity.ChatElementAdapter.ChatViewHolder holder, int position) {
            Message chatEl = chatList.get(position);
            holder.receiveMsg.setText(chatEl.getMessageText());
        }

        @Override
        public int getItemCount() {
            return chatList.size();
        }

        class ChatViewHolder extends RecyclerView.ViewHolder {

            TextView receiveMsg;

            public ChatViewHolder(@NonNull View itemView) {
                super(itemView);

                receiveMsg = itemView.findViewById(R.id.recievedMessageTv);
            }
        }

    }
    @Override
    protected void setTopBar() {
//        YumTopBar.setToolbar(
//                binding.topBar,
//                R.drawable.ic_back_arrow,
//                matchUser.getFullName(),
//                true,
//                false,
//                new YumTopBar.OnToolbarClickListener() {
//                    @Override
//                    public void onLeftIconClick() {
//                        finish();
//                    }
//                });
    }

}
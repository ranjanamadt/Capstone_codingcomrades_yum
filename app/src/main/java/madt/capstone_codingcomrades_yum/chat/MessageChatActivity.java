package madt.capstone_codingcomrades_yum.chat;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.MetadataChanges;
import com.google.gson.Gson;
import com.xwray.groupie.GroupAdapter;
import com.xwray.groupie.databinding.BindableItem;
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import madt.capstone_codingcomrades_yum.R;
import madt.capstone_codingcomrades_yum.core.BaseActivity;
import madt.capstone_codingcomrades_yum.databinding.ItemMessageReceiveBinding;
import madt.capstone_codingcomrades_yum.databinding.ItemMessageSendBinding;
import madt.capstone_codingcomrades_yum.databinding.MatchChatBinding;
import madt.capstone_codingcomrades_yum.fcm.SendPushHelper;
import madt.capstone_codingcomrades_yum.login.LoginUserDetail;
import madt.capstone_codingcomrades_yum.sharedpreferences.AppSharedPreferences;
import madt.capstone_codingcomrades_yum.sharedpreferences.SharedConstants;
import madt.capstone_codingcomrades_yum.utils.FSConstants;
import madt.capstone_codingcomrades_yum.utils.FirebaseCRUD;
import madt.capstone_codingcomrades_yum.utils.YumTopBar;


public class MessageChatActivity extends BaseActivity {
    private MatchChatBinding binding;
    private String collectionID = FSConstants.Collections.USERS + "/" + FirebaseAuth.getInstance().getUid() + "/" + FSConstants.Collections.CHATROOM;
    protected LoginUserDetail mLoginDetail;

    private String receiverCollectionID;
    private GroupAdapter messageAdapter = new GroupAdapter<GroupieViewHolder>();
    private List<Message> chatList = new ArrayList<>();
    private String username = "";
    public static ChatDetail chatUserDetail;




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.match_chat);
        mLoginDetail = new Gson().fromJson(AppSharedPreferences.getInstance().getString(SharedConstants.USER_DETAIL), LoginUserDetail.class);
        binding.messagesChatRV.setAdapter(messageAdapter);
        populateData();

        receiverCollectionID = FSConstants.Collections.USERS + "/" + chatUserDetail.getReceiverId() + "/" + FSConstants.Collections.CHATROOM;


        binding.button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String messageText = binding.editText.getText().toString();
                if (!messageText.isEmpty()) {
                    Message newMsg = new Message(
                            mLoginDetail.getFullName(),
                            mLoginDetail.getUuid(),
                            System.currentTimeMillis() + "",
                            messageText,
                            "");

                    chatList.add(newMsg);
//Update CHATROOM FOR LIKED USER
                    Map<String, Object> likedChatList = new HashMap<>();

                    likedChatList.put(FSConstants.CHAT_List.MESSAGES, chatList);
                    likedChatList.put(FSConstants.CHAT_List.USER_DETAIL, new ChatDetail(
                            mLoginDetail.getFirstName(),
                            mLoginDetail.getLastName(),
                            System.currentTimeMillis() + "",
                            messageText,
                            mLoginDetail.getProfileImage().get(0),
                            chatUserDetail.receiverId,
                            mLoginDetail.getUuid(),
                            AppSharedPreferences.getInstance().getString(SharedConstants.DEVICE_TOKEN)
                    ));


                    yLog("my collection :", collectionID);
                    yLog("rece collection :", receiverCollectionID);

                    FirebaseCRUD.getInstance().updateDoc(receiverCollectionID, chatUserDetail.receiverId + mLoginDetail.getUuid(), likedChatList).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<Void> task) {

                        }

                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull @NotNull Exception e) {
                            Log.e("path 1:", e.toString());
                            Log.e("path 1:", e.getLocalizedMessage());

                        }
                    });


                    //Update CHATROOM FOR LOGGED In USER
                    Map<String, Object> currentChatList = new HashMap<>();
                    currentChatList.put(FSConstants.CHAT_List.MESSAGES, chatList);
                    currentChatList.put(FSConstants.CHAT_List.USER_DETAIL, new ChatDetail(
                            chatUserDetail.getFirstName(),
                            chatUserDetail.getLastName(),
                            System.currentTimeMillis() + "",
                            messageText,
                            chatUserDetail.getProfileImage(),
                            mLoginDetail.getUuid(),
                            chatUserDetail.receiverId,
                            chatUserDetail.deviceToken

                    ));
                    binding.editText.setText("");


                    FirebaseCRUD.getInstance().updateDoc(collectionID, mLoginDetail.getUuid() + chatUserDetail.receiverId, currentChatList).addOnCompleteListener(new OnCompleteListener<Void>() {

                        @Override
                        public void onComplete(@NonNull @NotNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                //  messageAdapter.add(new SendMessageItem(newMsg));

                                SendPushHelper.sendPush(MessageChatActivity.this, chatUserDetail.getDeviceToken(), mLoginDetail.getFullName()
                                        , messageText);

                            }
                        }

                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull @NotNull Exception e) {
                            Log.e("path 2:", e.toString());
                            Log.e("path 2:", e.getLocalizedMessage());

                        }
                    });


                }
            }
        });
        if (getIntent().hasExtra(FSConstants.CHAT_List.USER_NAME)) {
            username = getIntent().getStringExtra(FSConstants.CHAT_List.USER_NAME);
        }
        setTopBar();
    }

    private void populateData() {

        if (getIntent().hasExtra(FSConstants.CHAT_List.CHAT_ID)) {


            FirebaseFirestore.getInstance().collection(collectionID).document(getIntent().getStringExtra(FSConstants.CHAT_List.CHAT_ID))
                    .addSnapshotListener(MetadataChanges.INCLUDE, (documentSnapshot, e) -> {
                        yLog("in ", "event listener");
                        chatList.clear();
                        // messageAdapter.notifyDataSetChanged();
                        messageAdapter.clear();
                        messageAdapter.notifyDataSetChanged();
                        if (documentSnapshot.exists()) {
                            chatList = (List<Message>) documentSnapshot.get(FSConstants.CHAT_List.MESSAGES);
                            yLog("list size: ", chatList.size() + "//");
                            for (HashMap<String, Object> messageObj : (ArrayList<HashMap<String, Object>>) documentSnapshot.get(FSConstants.CHAT_List.MESSAGES)) {
                                Message msg = new Message(messageObj);
                                yLog("sender id :", msg.getSenderId());
                                if (msg.getSenderId().equalsIgnoreCase(mLoginDetail.getUuid())) {
                                    messageAdapter.add(new SendMessageItem(msg));
                                } else {
                                    messageAdapter.add(new ReceiveMessageItem(msg));

                                }
                            }
                        }
                        binding.messagesChatRV.scrollToPosition(chatList.size()-1);
                    });


        }
    }


    class SendMessageItem extends BindableItem<ItemMessageSendBinding> {
        Message message;

        @Override
        public void bind(@NonNull @NotNull ItemMessageSendBinding viewBinding, int position) {
            viewBinding.myMessageTv.setText(message.getMessageText());
        }

        public SendMessageItem(Message message) {
            this.message = message;
        }

        @Override
        public int getLayout() {
            return R.layout.item_message_send;
        }
    }

    class ReceiveMessageItem extends BindableItem<ItemMessageReceiveBinding> {
        Message message;

        public ReceiveMessageItem(Message message) {
            this.message = message;
        }

        @Override
        public void bind(@NonNull @NotNull ItemMessageReceiveBinding viewBinding, int position) {
            viewBinding.recievedMessageTv.setText(message.getMessageText());
        }

        @Override
        public int getLayout() {
            return R.layout.item_message_receive;
        }
    }


    @Override
    protected void setTopBar() {
        YumTopBar.setToolbar(
                binding.topBar,
                R.drawable.ic_back_arrow,
                username,
                true,
                true,
                new YumTopBar.OnToolbarClickListener() {
                    @Override
                    public void onLeftIconClick() {
                        finish();
                    }
                });
    }

}
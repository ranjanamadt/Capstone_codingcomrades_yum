package madt.capstone_codingcomrades_yum.chat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.mikhaellopez.circularimageview.CircularImageView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import madt.capstone_codingcomrades_yum.R;
import madt.capstone_codingcomrades_yum.core.BaseFragment;
import madt.capstone_codingcomrades_yum.databinding.FragmentChatBinding;
import madt.capstone_codingcomrades_yum.matcheslisting.MatchDetail;
import madt.capstone_codingcomrades_yum.sharedpreferences.AppSharedPreferences;
import madt.capstone_codingcomrades_yum.sharedpreferences.SharedConstants;
import madt.capstone_codingcomrades_yum.utils.CommonUtils;
import madt.capstone_codingcomrades_yum.utils.FSConstants;
import madt.capstone_codingcomrades_yum.utils.FirebaseCRUD;


public class ChatFragment extends BaseFragment {
    private FragmentChatBinding binding;
    private ChatElementAdapter chatAdapter;
    private ArrayList<ChatDetail> chatList = new ArrayList<>();
    public static String searchKey = "";
    String collectionID = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        collectionID = FSConstants.Collections.USERS + "/" + FirebaseAuth.getInstance().getUid() + "/" +
                FSConstants.Collections.CHATROOM;
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_chat, container, false);
        binding.recyclerList.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        binding.searchview.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchKey = query;
                getChatList(searchKey );
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        CommonUtils.showProgress(getActivity());

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        chatList.clear();
        getChatList(searchKey);
    }

    private void getChatList(String textFilter) {

        FirebaseCRUD.getInstance().getAll(collectionID).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    chatList.removeAll(chatList);
                    for (DocumentSnapshot document : task.getResult()) {

                        ChatDetail userDetail = new ChatDetail(document);
                        userDetail.setChatRoomId(document.getId());
                        if(userDetail.getFirstName().contains(textFilter) ||
                           userDetail.getLastName().contains(textFilter) ||
                           userDetail.getLastMessage().contains(textFilter)){
                            chatList.add(userDetail);
                        }
                    }
                    yLog("chats", chatList.size() + "");

                    binding.recyclerList.setAdapter(new ChatElementAdapter(getActivity(), chatList));
                    CommonUtils.hideProgress();
                }
            }
        });
    }

    class ChatElementAdapter extends RecyclerView.Adapter<ChatElementAdapter.ChatViewHolder> {
        private Activity activity;
        private ArrayList<ChatDetail> chatList;

        ChatElementAdapter(Activity activity, ArrayList<ChatDetail> chatList) {
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
            ChatDetail chatEl = chatList.get(position);
            holder.usernameTV.setText(chatEl.getFirstName() + " " + chatEl.getLastName());
            holder.timeTV.setText(CommonUtils.getTimeFromTimeStamp(chatEl.getLastMessageTimeStamp()));
            holder.dateTV.setText(CommonUtils.getDateFromTimeStamp(chatEl.getLastMessageTimeStamp()));
            if (chatEl.getProfileImage() != null && !chatEl.getProfileImage().isEmpty())
                holder.chatPicture.setImageBitmap(CommonUtils.getBitmapImage(chatEl.getProfileImage()));
            holder.lastMessageTV.setText(chatList.get(position).getLastMessage());

            holder.topView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getActivity(), MessageChatActivity.class);
                    i.putExtra(FSConstants.CHAT_List.CHAT_ID, chatEl.getChatRoomId());
                    i.putExtra(FSConstants.CHAT_List.USER_NAME, chatEl.getFirstName() + " " + chatEl.getLastName());
                    MessageChatActivity.chatUserDetail = chatEl;
                    startActivity(i);
                }
            });
            holder.topView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle(getString(R.string.delete_chat_dialog_title));
                    builder.setMessage(getString(R.string.delete_chat_dialog_description));

                    builder.setCancelable(true);
                    builder.setPositiveButton(getString(R.string.delete_chat_dialog_confirm), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                              FirebaseCRUD.getInstance().deleteDocument(collectionID,chatList.get(position).getChatRoomId()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                  @Override
                                  public void onComplete(@NonNull @NotNull Task<Void> task) {
                                      if(task.isSuccessful()){
                                          ySnackbar(getActivity(),getString(R.string.delete_chat_success));
                                          getChatList(searchKey );
                                      }
                                  }
                              });

                        }
                    });
                    builder.setNegativeButton(getString(R.string.cancel_action), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    builder.create().show();

                    return false;
                }
            });

        }

        @Override
        public int getItemCount() {
            return chatList.size();
        }

        class ChatViewHolder extends RecyclerView.ViewHolder {

            TextView usernameTV, dateTV, timeTV, lastMessageTV;
            CircularImageView chatPicture;
            View topView;

            public ChatViewHolder(@NonNull View itemView) {
                super(itemView);

                usernameTV = itemView.findViewById(R.id.tv_name);
                dateTV = itemView.findViewById(R.id.tv_days);
                timeTV = itemView.findViewById(R.id.tv_time);
                lastMessageTV = itemView.findViewById(R.id.tv_description);
                chatPicture = itemView.findViewById(R.id.chatPicture);
                topView = itemView.findViewById(R.id.viewChat);


            }
        }


    }

}
package madt.capstone_codingcomrades_yum.chat;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mikhaellopez.circularimageview.CircularImageView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import madt.capstone_codingcomrades_yum.R;
import madt.capstone_codingcomrades_yum.databinding.FragmentChatBinding;
import madt.capstone_codingcomrades_yum.sharedpreferences.AppSharedPreferences;
import madt.capstone_codingcomrades_yum.sharedpreferences.SharedConstants;
import madt.capstone_codingcomrades_yum.utils.FSConstants;
import madt.capstone_codingcomrades_yum.utils.FirebaseCRUD;


public class ChatFragment extends Fragment {
    private FragmentChatBinding binding;
    private ChatElementAdapter chatAdapter;
    private ArrayList<Message> chatList  = new ArrayList<>();;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_chat, container, false);
        String collectionID = FSConstants.Collections.USERS + "/" + FirebaseAuth.getInstance().getUid() + "/" +
                FSConstants.Collections.CHATROOM;
        FirebaseCRUD.getInstance()
                .getAll(collectionID).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    int i = task.getResult().getDocuments().size();
                    for (DocumentSnapshot document : task.getResult()) {
                        ArrayList<HashMap<String,Object>> messages = (ArrayList) document.get(FSConstants.CHAT_List.MESSAGES);
                        if(messages != null && messages.size() > 0){
                            Message chatMsg = new Message(messages.get(0));


                            chatList.add(chatMsg);

                        }
                    }

                    binding.recyclerList.setAdapter(new ChatElementAdapter(getActivity(), chatList));
                }
            }
        });

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
            Message chatEl = chatList.get(position);
            holder.usernameTV.setText(chatEl.getSendBy());
            holder.timeTV.setText(chatEl.getTimeFromTimeStamp());
            holder.dateTV.setText(chatList.get(position).getDateFromTimeStamp());
            holder.lastMessageTV.setText(chatList.get(position).messageText);
            FirebaseCRUD.getInstance().getDocument(FSConstants.Collections.USERS , chatEl.getSenderId()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    byte[] decodedString = Base64.decode(documentSnapshot.get(FSConstants.USER.PROFILE_IMAGE).toString(), Base64.DEFAULT);
                    Bitmap img = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    holder.chatPicture.setImageBitmap(img);
                }
            });
        }

        @Override
        public int getItemCount() {
            return chatList.size();
        }
        class ChatViewHolder extends RecyclerView.ViewHolder {

            TextView usernameTV,dateTV, timeTV, lastMessageTV;
            CircularImageView chatPicture;
            public ChatViewHolder(@NonNull View itemView) {
                super(itemView);

                usernameTV = itemView.findViewById(R.id.tv_name);
                dateTV = itemView.findViewById(R.id.tv_days);
                timeTV = itemView.findViewById(R.id.tv_time);
                lastMessageTV = itemView.findViewById(R.id.tv_description);
                chatPicture = itemView.findViewById(R.id.chatPicture);
            }
        }

    }

}
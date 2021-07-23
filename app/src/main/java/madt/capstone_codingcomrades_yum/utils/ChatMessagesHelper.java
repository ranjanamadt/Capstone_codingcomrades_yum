package madt.capstone_codingcomrades_yum.utils;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Map;

public class ChatMessagesHelper {
    public static void createLikedUserChatRoom(String uuid, String chatId, Map<String, Object> likedChatList) {
        FirebaseCRUD.getInstance().createChatRoomSubCollection(FSConstants.Collections.USERS,
                FSConstants.Collections.CHATROOM, uuid,
                chatId, likedChatList
        ).addOnCompleteListener(task -> {

        });
    }

    public static void createCurrentUserChatRoom(String chatId, Map<String, Object> currentChatList) {
        FirebaseCRUD.getInstance().createChatRoomSubCollection(FSConstants.Collections.USERS,
                FSConstants.Collections.CHATROOM, FirebaseAuth.getInstance().getUid(),
                chatId, currentChatList
        ).addOnCompleteListener(task -> {

        });
    }

    public static void updateMessage() {

    }
}

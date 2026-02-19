package com.example.ubre.ui.main;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ubre.R;
import com.example.ubre.ui.apis.ApiClient;
import com.example.ubre.ui.apis.UserApi;
import com.example.ubre.ui.dtos.ChatDto;
import com.example.ubre.ui.dtos.ChatMessageDto;
import com.example.ubre.ui.dtos.UserDto;
import com.example.ubre.ui.services.ChatService;
import com.example.ubre.ui.storages.ChatStorage;
import com.example.ubre.ui.storages.UserStorage;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatFragment extends Fragment {

    private RecyclerView chatListView;
    private RecyclerView messagesView;
    private View inputBar;
    private EditText inputField;
    private TextView titleView;
    private boolean isAdmin;
    private ChatListAdapter chatListAdapter;
    private MessageAdapter messageAdapter;

    public ChatFragment() {}

    public static ChatFragment newInstance() {
        return new ChatFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        SharedPreferences sp = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        String role = sp.getString("role", "");
        isAdmin = "ADMIN".equals(role);

        titleView = view.findViewById(R.id.chat_title);
        chatListView = view.findViewById(R.id.chat_list);
        messagesView = view.findViewById(R.id.chat_messages);
        inputBar = view.findViewById(R.id.chat_input_bar);
        inputField = view.findViewById(R.id.chat_input);
        ImageButton sendBtn = view.findViewById(R.id.chat_send);

        view.findViewById(R.id.chat_btn_back).setOnClickListener(v -> onBackPressed());

        sendBtn.setOnClickListener(v -> onSend());

        // Messages RecyclerView - reverse layout so newest at bottom
        LinearLayoutManager msgLayoutManager = new LinearLayoutManager(getContext());
        msgLayoutManager.setReverseLayout(true);
        msgLayoutManager.setStackFromEnd(false);
        messagesView.setLayoutManager(msgLayoutManager);
        messageAdapter = new MessageAdapter();
        messagesView.setAdapter(messageAdapter);

        if (isAdmin) {
            setupAdminMode();
        } else {
            setupUserMode();
        }

        // Observe messages
        ChatStorage.getInstance().getMessagesReadOnly().observe(getViewLifecycleOwner(), messages -> {
            messageAdapter.setMessages(messages);
        });
    }

    private void setupAdminMode() {
        // Start with chat list visible, messages hidden
        chatListView.setVisibility(View.VISIBLE);
        messagesView.setVisibility(View.GONE);
        inputBar.setVisibility(View.GONE);
        titleView.setText("Live support");

        chatListAdapter = new ChatListAdapter(chat -> {
            ChatStorage.getInstance().setCurrentChat(chat);
            ChatService.getInstance().loadMessages(requireContext());
            String name = chat.getUser() != null
                    ? chat.getUser().getName() + " " + chat.getUser().getSurname()
                    : "User";
            titleView.setText(name);
            chatListView.setVisibility(View.GONE);
            messagesView.setVisibility(View.VISIBLE);
            inputBar.setVisibility(View.VISIBLE);
        });
        chatListView.setLayoutManager(new LinearLayoutManager(getContext()));
        chatListView.setAdapter(chatListAdapter);

        ChatStorage.getInstance().getChatsReadOnly().observe(getViewLifecycleOwner(), chats -> {
            chatListAdapter.setChats(chats);
        });

        ChatService.getInstance().loadChats(requireContext());
    }

    private void setupUserMode() {
        chatListView.setVisibility(View.GONE);
        messagesView.setVisibility(View.VISIBLE);
        inputBar.setVisibility(View.VISIBLE);
        titleView.setText("Live support");

        // Load user's chat
        ChatStorage.getInstance().getCurrentChat().observe(getViewLifecycleOwner(), chat -> {
            if (chat != null && chat.getId() != null) {
                ChatService.getInstance().loadMessages(requireContext());
            }
        });

        ChatService.getInstance().loadMyChat(requireContext());
    }

    private void onBackPressed() {
        if (isAdmin && messagesView.getVisibility() == View.VISIBLE) {
            // Go back to chat list
            ChatStorage.getInstance().setCurrentChat(null);
            ChatStorage.getInstance().setMessages(new ArrayList<>());
            chatListView.setVisibility(View.VISIBLE);
            messagesView.setVisibility(View.GONE);
            inputBar.setVisibility(View.GONE);
            titleView.setText("Live support");
            ChatService.getInstance().loadChats(requireContext());
        } else {
            requireActivity().getSupportFragmentManager().popBackStack();
        }
    }

    private void onSend() {
        String text = inputField.getText().toString().trim();
        if (text.isEmpty()) return;

        UserDto currentUser = UserStorage.getInstance().getCurrentUser().getValue();
        if (currentUser == null) return;

        ChatDto chat = ChatStorage.getInstance().getCurrentChatValue();
        Long chatId = chat != null ? chat.getId() : null;

        ChatMessageDto msg = new ChatMessageDto(
                null, text, new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US).format(new Date()),
                false, chatId, currentUser
        );

        ChatService.getInstance().sendMessage(requireContext(), msg);
//        ChatStorage.getInstance().addMessageToTop(msg);
        inputField.setText("");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ChatStorage.getInstance().setCurrentChat(null);
        ChatStorage.getInstance().setMessages(new ArrayList<>());
    }

    // ============================
    // Chat list adapter (admin)
    // ============================
    interface OnChatSelectedListener {
        void onChatSelected(ChatDto chat);
    }

    static class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.VH> {
        private List<ChatDto> chats = new ArrayList<>();
        private final OnChatSelectedListener listener;

        ChatListAdapter(OnChatSelectedListener listener) {
            this.listener = listener;
        }

        void setChats(List<ChatDto> chats) {
            this.chats = chats != null ? chats : new ArrayList<>();
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_list_item, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int position) {
            ChatDto chat = chats.get(position);
            UserDto user = chat.getUser();
            if (user != null) {
                holder.name.setText(user.getName() + " " + user.getSurname());
                Context ctx = holder.itemView.getContext();

                // Load default avatar first
                Glide.with(ctx).load(R.drawable.img_default_avatar).circleCrop().into(holder.avatar);

                // Then load actual avatar dynamically
                holder.avatar.setTag(user.getId());
                android.content.SharedPreferences sp = ctx.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
                String token = sp.getString("jwt", null);
                if (token != null && user.getId() != null) {
                    UserApi userApi = ApiClient.getClient().create(UserApi.class);
                    userApi.getUserAvatar("Bearer " + token, user.getId()).enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            try {
                                if (response.isSuccessful() && response.body() != null) {
                                    byte[] bytes = response.body().bytes();
                                    if (holder.avatar.getTag() != null && holder.avatar.getTag().equals(user.getId())) {
                                        Glide.with(ctx).asBitmap().load(bytes).circleCrop().into(holder.avatar);
                                    }
                                }
                            } catch (Exception ignored) {}
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {}
                    });
                }
            } else {
                holder.name.setText("Unknown user");
                Glide.with(holder.itemView.getContext()).load(R.drawable.img_default_avatar).circleCrop().into(holder.avatar);
            }
            holder.unread.setVisibility(Boolean.TRUE.equals(chat.getHasUnreadMessages()) ? View.VISIBLE : View.GONE);
            holder.itemView.setOnClickListener(v -> listener.onChatSelected(chat));
        }

        @Override
        public int getItemCount() { return chats.size(); }

        static class VH extends RecyclerView.ViewHolder {
            TextView name;
            ImageView avatar;
            View unread;
            VH(View v) {
                super(v);
                name = v.findViewById(R.id.chat_item_name);
                avatar = v.findViewById(R.id.chat_item_avatar);
                unread = v.findViewById(R.id.chat_item_unread);
            }
        }
    }

    // ============================
    // Message adapter
    // ============================
    class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.VH> {
        private List<ChatMessageDto> messages = new ArrayList<>();

        void setMessages(List<ChatMessageDto> msgs) {
            this.messages = msgs != null ? msgs : new ArrayList<>();
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_message_item, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int position) {
            ChatMessageDto msg = messages.get(position);
            UserDto currentUser = UserStorage.getInstance().getCurrentUser().getValue();
            boolean isMine = currentUser != null && msg.getSender() != null
                    && currentUser.getRole() == msg.getSender().getRole();

            // Bubble
            holder.bubble.setText(msg.getText());
            if (isMine) {
                holder.bubble.setBackgroundResource(R.drawable.bg_chat_bubble_mine);
                holder.bubble.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.surface));
                holder.root.setGravity(Gravity.END);
            } else {
                holder.bubble.setBackgroundResource(R.drawable.bg_chat_bubble_other);
                holder.bubble.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.text_primary));
                holder.root.setGravity(Gravity.START);
            }

            // Sender name — show when different sender from next message
            ChatMessageDto prev = (position < messages.size() - 1) ? messages.get(position + 1) : null;
            boolean showSender = !isMine && (prev == null
                    || prev.getSender() == null || msg.getSender() == null
                    || prev.getSender().getRole() != msg.getSender().getRole());
            if (showSender && msg.getSender() != null) {
                holder.sender.setVisibility(View.VISIBLE);
                holder.sender.setText(msg.getSender().getName() + " " + msg.getSender().getSurname());
            } else {
                holder.sender.setVisibility(View.GONE);
            }

            // Timestamp — show when gap > 15 min from previous message
            boolean showTimestamp = false;
            if (prev == null) {
                showTimestamp = true;
            } else if (msg.getSentAt() != null && prev.getSentAt() != null) {
                try {
                    long curr = parseTimestamp(msg.getSentAt());
                    long prevTs = parseTimestamp(prev.getSentAt());
                    showTimestamp = Math.abs(curr - prevTs) > 900000;
                } catch (Exception ignored) {}
            }
            if (showTimestamp && msg.getSentAt() != null) {
                holder.timestamp.setVisibility(View.VISIBLE);
                holder.timestamp.setText(formatTimestamp(msg.getSentAt()));
            } else {
                holder.timestamp.setVisibility(View.GONE);
            }
        }

        @Override
        public int getItemCount() { return messages.size(); }

        private long parseTimestamp(String sentAt) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
                Date d = sdf.parse(sentAt);
                return d != null ? d.getTime() : 0;
            } catch (Exception e) {
                return 0;
            }
        }

        private String formatTimestamp(String sentAt) {
            try {
                SimpleDateFormat sdfIn = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
                SimpleDateFormat sdfOut = new SimpleDateFormat("d MMM yyyy h:mm a", Locale.US);
                Date d = sdfIn.parse(sentAt);
                return d != null ? sdfOut.format(d) : sentAt;
            } catch (Exception e) {
                return sentAt;
            }
        }

        class VH extends RecyclerView.ViewHolder {
            android.widget.LinearLayout root;
            TextView bubble, sender, timestamp;
            VH(View v) {
                super(v);
                root = v.findViewById(R.id.chat_message_root);
                bubble = v.findViewById(R.id.chat_message_bubble);
                sender = v.findViewById(R.id.chat_message_sender);
                timestamp = v.findViewById(R.id.chat_message_timestamp);
            }
        }
    }
}

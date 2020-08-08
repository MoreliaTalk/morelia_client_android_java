package ru.wtw.moreliatalkclient;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter {
    private List<Message> messageList;
    private RecyclerView recyclerView;

    public static final int TYPE_SERVICE = 0;
    public static final int TYPE_MSG_OUT = 1;
    public static final int TYPE_MSG_IN = 2;

    private static final int MAX_MESSAGES = 1000;

    private int messageTextId;
    private int messageTimeId;
    private int userNameId;

    private int serviceLayout;
    private int msgOutLayout;
    private int msgInLayout;


    public static class Message {
        String text;
        String time;
        String user;
        int type;

        public Message(String text, String user, String time, int type) {
            this.text = text;
            this.user = user;
            this.time = time;
            this.type = type;
        }
    }

    public class MessageView extends RecyclerView.ViewHolder {
        TextView messageText;
        TextView messageTime;
        TextView userName;


        MessageView(@NonNull View itemView, int messageTextId, int messageTimeId, int userNameId) {
            super(itemView);
            messageText = itemView.findViewById(messageTextId);
            messageTime = itemView.findViewById(messageTimeId);
            userName = itemView.findViewById(userNameId);
        }

        void bind(Message message) {
            messageText.setText(message.text);
            messageTime.setText(message.time);
            userName.setText(message.user);
        }
    }

    public MessageAdapter setMessageTextId(int messageTextId) {
        this.messageTextId = messageTextId;
        return this;
    }

    public MessageAdapter setMessageTimeId(int messageTimeId) {
        this.messageTimeId = messageTimeId;
        return this;
    }

    public MessageAdapter setUserNameId(int userNameId) {
        this.userNameId = userNameId;
        return this;
    }

    public MessageAdapter setMsgOutLayout(int msgOutLayout) {
        this.msgOutLayout = msgOutLayout;
        return this;
    }

    public MessageAdapter setMsgInLayout(int msgInLayout) {
        this.msgInLayout = msgInLayout;
        return this;
    }

    public MessageAdapter setServiceLayout(int serviceLayout) {
        this.serviceLayout = serviceLayout;
        return this;
    }

    public MessageAdapter() {
        this.messageList = new ArrayList<>();
    }

    public void appendTo(RecyclerView recyclerView, Context parent) {
        this.recyclerView = recyclerView;
        this.recyclerView.setLayoutManager(new LinearLayoutManager(parent));
        this.recyclerView.setAdapter(this);
    }

    public void addMessage(Message m) {
        messageList.add(m);
        if (messageList.size() > MAX_MESSAGES) {
            messageList = messageList.subList(messageList.size() - MAX_MESSAGES, messageList.size());
        }
        this.notifyDataSetChanged();
        this.recyclerView.scrollToPosition(messageList.size() - 1);
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messageList.get(position);
        return message.type;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int view_type) {
        View view;
        if (view_type == TYPE_MSG_IN) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(msgOutLayout, viewGroup, false);
        } else if (view_type == TYPE_MSG_OUT) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(msgInLayout, viewGroup, false);
        } else {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(serviceLayout, viewGroup, false);
        }
        return new MessageView(view, messageTextId, messageTimeId, userNameId);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        Message message = messageList.get(i);
        ((MessageView) viewHolder).bind(message);
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }
}




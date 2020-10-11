package ru.wtw.moreliatalkclient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

public class FlowActivity extends AppCompatActivity {

    private MessageAdapter adapter;
    private Network network;

    private int themeIndex;

    private static final int VERTICAL_ITEM_SPACE = 48;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flow);

        RecyclerView flowWindow = findViewById(R.id.flowWindow);
        flowWindow.addItemDecoration(new VerticalSpaceItemDecoration(VERTICAL_ITEM_SPACE));


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            themeIndex = extras.getInt("theme", 0);
        } else{
            themeIndex = 0;
        }

        adapter = new MessageAdapter();

        adapter
                .setMsgInLayout(R.layout.themes_classic_message_in)
                .setMsgOutLayout(R.layout.themes_classic_message_out)
                .setErrorLayout(R.layout.themes_classic_message_error)
                .setServiceLayout(R.layout.themes_classic_message_service);


        switch (themeIndex) {
            case 1:
                adapter
                        .setMsgInLayout(R.layout.themes_alex_message_blue_python)
                        .setMsgOutLayout(R.layout.themes_alex_message_yellow_python);
                break;
            case 2:
                adapter
                        .setMsgInLayout(R.layout.themes_alex_glass_snake_blue)
                        .setMsgOutLayout(R.layout.themes_alex_glass_snake_blue)
                        .setServiceLayout(R.layout.themes_alex_glass_snake_blue);
                break;
            case 3:
                adapter
                        .setMsgInLayout(R.layout.themes_nekrod_snake_yellow)
                        .setMsgOutLayout(R.layout.themes_nekrod_snake_green);
                break;
        }

        adapter
                .setMessageTextId(R.id.messageText)
                .setMessageTimeId(R.id.messageTime)
                .setUserNameId(R.id.userName)
                .appendTo(flowWindow, this);


        network = new Network(FlowActivity.this);
        if (extras != null) {
            Log.i("SERVER","Extra");
            network.setUsername(extras.getString("username"));
            network.setLogin(extras.getString("login"));
            network.setEmail(extras.getString("email"));
            network.setPassword(extras.getString("password"));
            network.setServername(extras.getString("servername"));
            network.setReconnect(extras.getBoolean("reconnect"));
            network.setShowJSON(extras.getBoolean("outjson"));
            network.setUseNewAPI(extras.getBoolean("newapi"));
            network.setRawJSON(extras.getBoolean("rawjson"));
            network.setRegister(extras.getBoolean("register"));
            network.connect();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        ImageButton sendButton = findViewById(R.id.sendButton);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (network.isConnected()) {
                    EditText editSend = findViewById(R.id.editSend);
                    String text=editSend.getText().toString().trim();
                    if (!text.isEmpty()) {
                        network.sendMessage(text);
                    }
                    if (network.isNotRawJSON()) {
                        editSend.setText("");
                    }
                }
            }
        });
    }


    public void onMessage(String user, String text, String time) {
        int type = MessageAdapter.TYPE_SERVICE;
        if (!user.equals("")) {
            if (network.getLogin().equals(user)) {
                type = MessageAdapter.TYPE_MSG_IN;
            } else {
                type = MessageAdapter.TYPE_MSG_OUT;
            }
        } else {
            if (time.equals("*")) {
                type = MessageAdapter.TYPE_ERROR;
            }
        }
        adapter.addMessage(
                new MessageAdapter.Message( text, user, time, type)
        );
    }

}


package ru.wtw.moreliatalkclient;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

public class FlowActivity extends AppCompatActivity {

    private MessageAdapter adapter;
    private Network network;
    private DBHelper appDB;
    private RecyclerView flowWindow;


    private String themeIndex;

    private int flow_id;
    private String user_login;

    private static final int VERTICAL_ITEM_SPACE = 48;


    private final int interval = 5000;
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable(){
        public void run() {
            if (network.isConnected()) {
                network.sendRequestAllMessages(flow_id,0);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flow);

        appDB = new DBHelper(this);

        ActionBar actionBar =getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        flowWindow = findViewById(R.id.flowWindow);
        flowWindow.addItemDecoration(new VerticalSpaceItemDecoration(VERTICAL_ITEM_SPACE));

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        themeIndex = sp.getString("themes","Classic");

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            flow_id=extras.getInt("flow_id");
        }

        adapter = new MessageAdapter(appDB.getAllMessages(String.valueOf(flow_id)));

        adapter
                .setMsgInLayout(R.layout.themes_classic_message_in)
                .setMsgOutLayout(R.layout.themes_classic_message_out)
                .setErrorLayout(R.layout.themes_classic_message_error)
                .setServiceLayout(R.layout.themes_classic_message_service);


        switch (themeIndex) {
            case "Alex-Python":
                adapter
                        .setMsgInLayout(R.layout.themes_alex_message_blue_python)
                        .setMsgOutLayout(R.layout.themes_alex_message_yellow_python);
                break;
            case "Alex-Glass":
                adapter
                        .setMsgInLayout(R.layout.themes_alex_glass_snake_blue)
                        .setMsgOutLayout(R.layout.themes_alex_glass_snake_blue)
                        .setServiceLayout(R.layout.themes_alex_glass_snake_blue);
                break;
            case "Nekrod-Snakes":
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
            user_login =extras.getString("user_login");
            setTitle(extras.getString("flow_name"));
            network.setLogin(extras.getString("login"));
            network.setPassword(extras.getString("password"));
            network.setServername(extras.getString("servername"));
            network.setReconnect(true);
            network.setRegister(false);
            network.connect();
        }

        handler.postDelayed(runnable, interval);
        flowWindow.scrollToPosition(adapter.getItemCount()-1);

    }

    private void LoadMessages(String flow_id) {


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
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
                        network.sendMessage(text, flow_id);
                        editSend.setText("");
                    }
                    network.sendRequestAllMessages(flow_id,0);
                }
            }
        });
    }

    public void onMessage() {
        adapter.ReplaceArray(appDB.getAllMessages(String.valueOf(flow_id)));
        adapter.notifyDataSetChanged();
        flowWindow.scrollToPosition(adapter.getItemCount()-1);

/*
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
                new MessageAdapter.Message( text, user, 0, type)
        );
*/
    }

}


package ru.wtw.moreliatalkclient;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ScrollView;
import android.widget.TextView;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.channels.NonReadableChannelException;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import com.google.gson.Gson;

public class Network {
    private URI socketURI;
    private WebSocketClient socket;

    private final Activity activity;

    private String username;
    private String password;
    private String servername;
    private boolean reconnect;
    private boolean showJSON;
    private boolean useNewAPI;

    private boolean isConnected;

    public Network(Activity activity){
        this.activity=activity;
        isConnected=false;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setServername(String servername) {
        this.servername = servername;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setReconnect(boolean reconnect) { this.reconnect = reconnect; }

    public void setShowJSON(boolean showJSON) { this.showJSON = showJSON; }

    public void setUseNewAPI(boolean useNewAPI) { this.useNewAPI = useNewAPI; }

    public boolean isConnected() {
        return isConnected;
    }

    public void reconnect(){
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                socket.reconnect();
            }
        }, 1000);
    }

    public void connect() {
        if (socketURI == null) {
            try {
                socketURI = new URI(servername);
            } catch (URISyntaxException e) {
                e.printStackTrace();
                return;
            }

        }
        if (socket == null) socket = new WebSocketClient(socketURI) {

            @Override
            public void onOpen(ServerHandshake handshakedata) {
                isConnected = true;
                if (useNewAPI) {
                    sendReg();
                } else {
                    sendAuth();
                }
                Log.i("SERVER", "Connected");
            }

            @Override
            public void onMessage(String message) {
                if (showJSON) {
                    outChat("Received: "+message);
                } else {
                    Protocol protocol = new Gson().fromJson(message, Protocol.class);
                    String status = protocol.getStatus();
                    String reply;
                    if (protocol.getMode().equals("message")) {
                        reply = protocol.getTime() + " - " + protocol.getUsername() + ": " + protocol.getText();
                        outChat(reply);
                    }
                    if (protocol.getMode().equals("reg")) {
                        reply = activity.getResources().getString(R.string.auth_status_unknown);
                        if (status.equals("true"))
                            reply = activity.getResources().getString(R.string.auth_status_true);
                        if (status.equals("false"))
                            reply = activity.getResources().getString(R.string.auth_status_false);
                        if (status.equals("newreg"))
                            reply = activity.getResources().getString(R.string.auth_status_newreg);
                        outChat(reply);
                    }
                }
                Log.i("SERVER", "Message: " + message);
            }

            @Override
            public void onMessage(ByteBuffer message) {
                Log.i("SERVER", "Message byte buffer");
            }

            @Override
            public void onClose(final int code, String reason, boolean remote) {
                Log.i("SERVER", "Disconnected with exit code " + String.valueOf(code) + " additional info: " + reason);
                outChat(activity.getResources().getString(R.string.socket_close) + String.valueOf(code));
                if (reconnect) {
                    outChat(activity.getResources().getString(R.string.reconnecting));
                    Network.this.reconnect();
                }
            }

            @Override
            public void onError(Exception ex) {
                Log.e("SERVER", "Error", ex);
                outChat(activity.getResources().getString(R.string.socket_error) + ex.toString());
            }
        };

        Log.i("SERVER","Connect");
        socket.connect();

    }

    public void outChat (final String text) {
        activity.runOnUiThread( new Runnable() {
            @Override
            public void run() {
                final TextView chat = ((TextView) activity.findViewById(R.id.chat));
                final ScrollView chatScroller = activity.findViewById(R.id.chatScroller);
                chat.setText(chat.getText().toString() + "\n" + text + "\n");
                chatScroller.post(new Runnable()
                {
                    public void run()
                    {
                        chatScroller.smoothScrollTo(0, chat.getBottom());
                    }
                });
            }
        });
    }

    public void sendReg () {
        Gson gson = new Gson();
        Protocol protocol = new Protocol();
        protocol.setMode("reg");
        protocol.setUsername(username);
        protocol.setPassword(password);
        String json = "{ \"type\": \"register_user\", \"data\": { \"user\": { \"password\": \"" + password+
                "\", \"login\": \""+username+"\", \"email\": \"qwwer@qwer.ru\", \"username\": \"User1\" }, "+
                "\"meta\": \"None\" }, \"jsonapi\": { \"version\": \"1.0\" }, \"meta\": \"None\" }";
        if (socket != null && socket.isOpen()) {
            if (showJSON) outChat("Sending: "+json);
            Log.i("SERVER","Send reg");
            socket.send(json);
        }
    }

    public void sendAuth () {
        Gson gson = new Gson();
        Protocol protocol = new Protocol();
        protocol.setMode("reg");
        protocol.setUsername(username);
        protocol.setPassword(password);
        String json = gson.toJson(protocol);
        if (socket != null && socket.isOpen()) {
            if (showJSON) outChat("Sending: "+json);
            Log.i("SERVER","Send auth");
            socket.send(json);
        }
    }

    public void sendMessage (String text) {
        Gson gson = new Gson();
        Protocol protocol = new Protocol();
        protocol.setMode("message");
        protocol.setUsername(username);
        protocol.setText(text);
        String json = gson.toJson(protocol);
        if (socket != null && socket.isOpen()) {
            if (showJSON) outChat("Sending: "+json);
            Log.i("SERVER","Send text");
            Log.i("SERVER",json);
            socket.send(json);
        }
    }

}

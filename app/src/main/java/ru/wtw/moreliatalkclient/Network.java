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
import java.util.Objects;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import com.google.gson.Gson;

public class Network {
    private URI socketURI;
    private WebSocketClient socket;

    private final Activity activity;

    private String login;
    private String username;
    private String password;
    private String servername;
    private String email;
    private boolean reconnect;
    private boolean showJSON;
    private boolean useNewAPI;
    private boolean rawJSON;
    private boolean register;

    private boolean isConnected;

    public Network(Activity activity){
        this.activity=activity;
        isConnected=false;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) { this.email = email; }

    public void setLogin(String login) { this.login = login; }

    public void setServername(String servername) {
        this.servername = servername;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setReconnect(boolean reconnect) { this.reconnect = reconnect; }

    public void setShowJSON(boolean showJSON) { this.showJSON = showJSON; }

    public void setUseNewAPI(boolean useNewAPI) { this.useNewAPI = useNewAPI; }

    public void setRawJSON(boolean rawJSON) { this.rawJSON = rawJSON; }

    public void setRegister(boolean register) { this.register = register; }

    public String getUsername() {
        return username;
    }

    public String getLogin() {
        return login;
    }

    public boolean isRawJSON() {
        return rawJSON;
    }

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
                    if (register) {
                        sendReg();
                    } else {
                        sendAuth();
                    }
                } else {
                    sendLegacyAuth();
                }
                Log.i("SERVER", "Connected");
            }

            @Override
            public void onMessage(String message) {
                LegacyProtocol legacyProtocol = new Gson().fromJson(message, LegacyProtocol.class);
                Protocol protocol = new Gson().fromJson(message, Protocol.class);
                if (showJSON) {
                    if (legacyProtocol.getMode() != null) {
                        outChat("", "Received old protocol: "+message, "");
                    } else if (protocol.getType() != null) {
                        outChat("", "Received new protocol: "+message, "");
                    } else {
                        outChat("", "Received unknown protocol: "+message, "");
                    }
                } else {
                    String status = legacyProtocol.getStatus();
                    String reply;
                    if (legacyProtocol.getMode().equals("message")) {
                        //reply = legacyProtocol.getTime() + " - " + legacyProtocol.getUsername() + ": " + legacyProtocol.getText();
                        outChat(legacyProtocol.getUsername(), legacyProtocol.getText(), legacyProtocol.getTime());
                    }
                    if (legacyProtocol.getMode().equals("reg")) {
                        reply = activity.getResources().getString(R.string.auth_status_unknown);
                        if (status.equals("true"))
                            reply = activity.getResources().getString(R.string.auth_status_true);
                        if (status.equals("false"))
                            reply = activity.getResources().getString(R.string.auth_status_false);
                        if (status.equals("newreg"))
                            reply = activity.getResources().getString(R.string.auth_status_newreg);
                        outChat("", reply, "");
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
                outChat("", activity.getResources().getString(R.string.socket_close) + String.valueOf(code), "");
                if (reconnect) {
                    outChat("", activity.getResources().getString(R.string.reconnecting), "");
                    Network.this.reconnect();
                }
            }

            @Override
            public void onError(Exception ex) {
                Log.e("SERVER", "Error", ex);
                outChat("", activity.getResources().getString(R.string.socket_error) + ex.toString(), "");
            }
        };

        Log.i("SERVER","Connect");
        socket.connect();

    }

    public void outChat (final String user, final String text, final String time) {
        if (activity.getClass().toString().equals("class ru.wtw.moreliatalkclient.TextChatActivity")) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((TextChatActivity) activity).onMessage(user, text, time);
                }
            });
        }
        if (activity.getClass().toString().equals("class ru.wtw.moreliatalkclient.FlowActivity")) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((FlowActivity) activity).onMessage(user, text, time);
                }
            });
        }
    }

    public void sendReg () {
        Gson gson = new Gson();
        LegacyProtocol legacyProtocol = new LegacyProtocol();
        legacyProtocol.setMode("reg");
        legacyProtocol.setUsername(username);
        legacyProtocol.setPassword(password);
        String json = "{ \"type\": \"register_user\", \"data\": { \"user\": { \"password\": \"" + password+
                "\", \"login\": \""+login+"\", \"email\": \""+email+"\", \"username\": \""+username+"\" }, "+
                "\"meta\": \"None\" }, \"jsonapi\": { \"version\": \"1.0\" }, \"meta\": \"None\" }";
        if (socket != null && socket.isOpen()) {
            if (showJSON) outChat("", "Sending: "+json, "");
            Log.i("SERVER","Send reg");
            socket.send(json);
        }
    }

    public void sendAuth () {
        Gson gson = new Gson();
        LegacyProtocol legacyProtocol = new LegacyProtocol();
        legacyProtocol.setMode("reg");
        legacyProtocol.setUsername(username);
        legacyProtocol.setPassword(password);
        String json = "{ \"type\": \"auth\", \"data\": { \"user\": { \"password\": \"" + password+
                "\", \"login\": \""+login+"\"}, "+
                "\"meta\": \"None\" }, \"jsonapi\": { \"version\": \"1.0\" }, \"meta\": \"None\" }";
        if (socket != null && socket.isOpen()) {
            if (showJSON) outChat("","Sending: "+json, "");
            Log.i("SERVER","Send auth");
            socket.send(json);
        }
    }

    public void sendLegacyAuth() {
        Gson gson = new Gson();
        LegacyProtocol legacyProtocol = new LegacyProtocol();
        legacyProtocol.setMode("reg");
        legacyProtocol.setUsername(username);
        legacyProtocol.setPassword(password);
        String json = gson.toJson(legacyProtocol);
        if (socket != null && socket.isOpen()) {
            if (showJSON) outChat("", "Sending: "+json, "");
            Log.i("SERVER","Send auth");
            socket.send(json);
        }
    }

    public void sendMessage (String text) {
        if (rawJSON) {
            if (socket != null && socket.isOpen()) {
                if (showJSON) outChat("","Sending RAW: "+text, "");
                Log.i("SERVER","Send text");
                Log.i("SERVER",text);
                socket.send(text);
            }
        } else {
            Gson gson = new Gson();
            LegacyProtocol legacyProtocol = new LegacyProtocol();
            legacyProtocol.setMode("message");
            legacyProtocol.setUsername(username);
            legacyProtocol.setText(text);
            String json = gson.toJson(legacyProtocol);
            if (socket != null && socket.isOpen()) {
                if (showJSON) outChat("","Sending: "+json, "");
                Log.i("SERVER","Send text");
                Log.i("SERVER",json);
                socket.send(json);
            }
        }
    }

}

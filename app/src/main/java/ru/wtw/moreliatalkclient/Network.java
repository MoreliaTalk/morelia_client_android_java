package ru.wtw.moreliatalkclient;

import android.app.Activity;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

// Uncomment to enable compression
//import org.java_websocket.drafts.Draft;
//import org.java_websocket.drafts.Draft_6455;
//import org.java_websocket.extensions.permessage_deflate.PerMessageDeflateExtension;

import com.google.gson.Gson;

public class Network {
    private URI socketURI;
    private WebSocketClient socket;

    private DBHelper appDB;

    // Uncomment and specify in new WebSocketClient(socketURI, perMessageDeflateDraft)  to enable compression
    // private static final Draft perMessageDeflateDraft = new Draft_6455(new PerMessageDeflateExtension());

    private final Activity activity;

    private String login;
    private String username;
    private String password;
    private String servername;
    private String email;

    private String uuid;
    private String auth_id;

    private boolean reconnect;
    private boolean showJSON;
    private boolean useNewAPI;
    private boolean rawJSON;
    private boolean register;

    private boolean isConnected;

    public Network(Activity activity){
        appDB = new DBHelper(activity);
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

    public boolean isNotRawJSON() {
        return !rawJSON;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public String getUuid() {
        return uuid;
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

    public void disconnect(){
        socket.close();
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
        // add second param perMessageDeflateDraft to enable compression
        if (socket == null) {
            socket = new WebSocketClient(socketURI) {

                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    isConnected = true;
                    if (register) {
                        sendReg();
                        register=false;
                    } else {
                        sendAuth();
                    }
                    Log.i("SERVER", "Connected");
                }

                @Override
                public void onMessage(String message) {
                    outJson("Received: " + message);
                    String cleanmessage = message.replaceAll("\\\\n", "").replaceAll("\\\\", "");
                    cleanmessage = cleanmessage.startsWith("\"") ? cleanmessage.substring(1) : cleanmessage;
                    cleanmessage = cleanmessage.endsWith("\"") ? cleanmessage.substring(0, cleanmessage.length() - 1) : cleanmessage;
                    Protocol protocol = new Gson().fromJson(cleanmessage, Protocol.class);
                    String reply = "";
                    if (protocol.getType().equals("register_user")) {
                        if (protocol.getErrors().getCode() == 201) {
                            uuid = protocol.getData().getUser()[0].getUuid();
                            auth_id = protocol.getData().getUser()[0].getAuth_id();
                            reply = activity.getResources().getString(R.string.auth_status_newreg);
                        }
                        if (protocol.getErrors().getCode() == 409)
                            reply = activity.getResources().getString(R.string.auth_status_exist);
                        outRegisterResponse(protocol.getErrors().getCode());
                    }
                    if (protocol.getType().equals("auth")) {
                        if (protocol.getErrors().getCode() == 200) {
                            uuid = protocol.getData().getUser()[0].getUuid();
                            auth_id = protocol.getData().getUser()[0].getAuth_id();
                            reply = activity.getResources().getString(R.string.auth_status_ok);
                        }
                        outAuthResponse(protocol.getErrors().getCode());
                    }
                    if (protocol.getType().equals("user_info")) {
                        if (protocol.getErrors().getCode() == 200) {
                            String req_login = protocol.getData().getUser()[0].getLogin();
                            String req_username = protocol.getData().getUser()[0].getUsername();
                            String req_email = protocol.getData().getUser()[0].getEmail();
                            outUserInfoResponse(req_login,req_username,req_email);
                        }
                    }
                    if (protocol.getType().equals("add_flow")) {
                        if (protocol.getErrors().getCode() == 200) {
                            for (int i = 0; i < protocol.getData().getFlow().length; i++) {
                                appDB.insertFlow(protocol.getData().getFlow()[i].getUuid(),
                                        protocol.getData().getFlow()[i].getTitle(),
                                        protocol.getData().getFlow()[i].getType());
                            }
                            outFlowUpdate();
                        }
                    }
                    if (protocol.getType().equals("all_flow")) {
                        if (protocol.getErrors().getCode() == 200) {
                            for (int i = 0; i < protocol.getData().getFlow().length; i++) {
                                appDB.insertFlow(protocol.getData().getFlow()[i].getUuid(),
                                        protocol.getData().getFlow()[i].getTitle(),
                                        protocol.getData().getFlow()[i].getType());
                            }
                            outFlowUpdate();
                        }
                    }
                    if (protocol.getType().equals("all_messages")) {
                        if (protocol.getErrors().getCode() == 200) {
                            for (int i = 0; i < protocol.getData().getMessage().length; i++) {
                                int own=MessageAdapter.TYPE_MSG_IN;
                                if (protocol.getData().getMessage()[i].getFrom_user().toString().equals(String.valueOf(getUuid()))) {
                                    own=MessageAdapter.TYPE_MSG_OUT;
                                }
                                appDB.insertMsg(protocol.getData().getMessage()[i].getId(),
                                        protocol.getData().getMessage()[i].getFrom_flow(),
                                        protocol.getData().getMessage()[i].getFrom_user(),
                                        protocol.getData().getMessage()[i].getText(),
                                        protocol.getData().getMessage()[i].getTime(), own
                                );

/*
                                        protocol.getData().getMessage()[i].getFrom_flow().toString(),
                                        protocol.getData().getMessage()[i].getFrom_user().toString(),
                                        protocol.getData().getMessage()[i].getText());
*/

                            }
                            outMessage();
                        }
                    }
                    if (!showJSON) {
  //                      outMessage("", reply, "");
                    }
                    Log.i("SERVER", "Message: " + message);
                }

                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void onMessage(ByteBuffer bytebuffer) {
                    Log.i("SERVER", "Message byte buffer, converting to string");
                    String message = StandardCharsets.UTF_8.decode(bytebuffer).toString();
                    onMessage(message);
                }

                @Override
                public void onClose(final int code, String reason, boolean remote) {
                    Log.i("SERVER", "Disconnected with exit code " + code + " additional info: " + reason);
//                    outMessage("", activity.getResources().getString(R.string.socket_close) + code, "*");
                    if (reconnect) {
///                        outMessage("", activity.getResources().getString(R.string.reconnecting), "*");
                        Network.this.reconnect();
                    }
                }

                @Override
                public void onError(Exception ex) {
                    Log.e("SERVER", "Error", ex);
//                    outMessage("", activity.getResources().getString(R.string.socket_error) + ex.toString(), "*");
                }
            };
        }

        Log.i("SERVER","Connect");
        socket.connect();

    }

    public void outMessage() {
        if (activity.getClass().toString().equals("class ru.wtw.moreliatalkclient.FlowActivity")) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((FlowActivity) activity).onMessage();
                }
            });
        }

/*
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
*/
/*
        if (activity.getClass().toString().equals("class ru.wtw.moreliatalkclient.MainActivity")) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((MainActivity) activity).onMessage(user, text, time);
                }
            });
        }
*/
    }

    public void outJson(final String json) {
        appDB.insertJSON(json);
        if (activity.getClass().toString().equals("class ru.wtw.moreliatalkclient.MainActivity")) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((MainActivity) activity).onJson(json);
                }
            });
        }
    }

    public void outFlowUpdate() {
        if (activity.getClass().toString().equals("class ru.wtw.moreliatalkclient.MainActivity")) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((MainActivity) activity).onFlowsUpdate();
                }
            });
        }
    }

    public void outRegisterResponse(final int code) {
        if (activity.getClass().toString().equals("class ru.wtw.moreliatalkclient.MainActivity")) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((MainActivity) activity).onRegisterResponse(code);
                }
            });
        }
    }

    public void outAuthResponse(final int code) {
        if (activity.getClass().toString().equals("class ru.wtw.moreliatalkclient.MainActivity")) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((MainActivity) activity).onAuthResponse(code);
                }
            });
        }
    }

    public void outUserInfoResponse(final String login, final String username, final String email) {
        if (activity.getClass().toString().equals("class ru.wtw.moreliatalkclient.MainActivity")) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((MainActivity) activity).onUserInfoResponse(login, username, email);
                }
            });
        }
    }


    public void sendReg () {
        User[] user = new User[1];
        user[0] = new User();
        user[0].setLogin(login);
        user[0].setUsername(username);
        user[0].setPassword(password);
        user[0].setEmail(email);
        Data data = new Data();
        data.setUser(user);
        Protocol protocol = new Protocol();
        protocol.setType("register_user");
        protocol.setData(data);
        Gson gson = new Gson();
        String json = gson.toJson(protocol);
        if (socket != null && socket.isOpen()) {
            outJson( "Sending: " + json );
            Log.i("SERVER","Send reg");
            socket.send(json);
        }
    }

    public void sendAuth () {
        User[] user = new User[1];
        user[0] = new User();
        user[0].setLogin(login);
        user[0].setPassword(password);
        Data data = new Data();
        data.setUser(user);
        Protocol protocol = new Protocol();
        protocol.setType("auth");
        protocol.setData(data);
        Gson gson = new Gson();
        String json = gson.toJson(protocol);
        if (socket != null && socket.isOpen()) {
            outJson( "Sending: " + json );
            Log.i("SERVER","Send auth");
            socket.send(json);
        }
    }


    public void sendMessage (String text, String flow_id) {
        if (rawJSON) {
            if (socket != null && socket.isOpen()) {
//                if (showJSON) outMessage("","Sending RAW: "+text, "");
                Log.i("SERVER","Send text");
                Log.i("SERVER",text);
                socket.send(text);
            }
        } else {
            User[] user = new User[1];
            user[0] = new User();
            user[0].setAuth_id(auth_id);
            user[0].setUuid(uuid);
            Flow[] flow = new Flow[1];
            flow[0] = new Flow();
            flow[0].setUuid(flow_id);
            Message[] message = new Message[1];
            message[0] = new Message();
            message[0].setText(text);
            Data data = new Data();
            data.setUser(user);
            data.setFlow(flow);
            data.setMessage(message);
            Protocol protocol = new Protocol();
            protocol.setType("send_message");
            protocol.setData(data);
            Gson gson = new Gson();
            String json = gson.toJson(protocol);
            if (socket != null && socket.isOpen()) {
                outJson( "Sending: " + json );
                Log.i("SERVER","Send text");
                Log.i("SERVER",json);
                socket.send(json);
            }
        }
    }

    public void createFlow(String name, String type, String info) {
        User[] user = new User[1];
        user[0] = new User();
        user[0].setAuth_id(auth_id);
        user[0].setUuid(uuid);
        Flow[] flow = new Flow[1];
        flow[0] = new Flow();
        flow[0].setTitle(name);
        flow[0].setType(type);
        flow[0].setInfo(info);
        Data data = new Data();
        data.setUser(user);
        data.setFlow(flow);
        Protocol protocol = new Protocol();
        protocol.setType("add_flow");
        protocol.setData(data);
        Gson gson = new Gson();
        String json = gson.toJson(protocol);
        if (socket != null && socket.isOpen()) {
            outJson( "Sending: " + json );
            Log.i("SERVER","Create flow");
            socket.send(json);
        }
    }

    public void sendRequestUserInfo (String uuid) {
        User[] user = new User[2];
        user[0] = new User();
        user[0].setUuid(uuid);
        user[0].setAuth_id(auth_id);
        user[1] = new User();
        user[1].setUuid(uuid);
        Data data = new Data();
        data.setUser(user);
        Protocol protocol = new Protocol();
        protocol.setType("user_info");
        protocol.setData(data);
        Gson gson = new Gson();
        String json = gson.toJson(protocol);
        if (socket != null && socket.isOpen()) {
            outJson( "Sending: " + json );
            Log.i("SERVER","Request User Info");
            socket.send(json);
        }
    }

    public void sendRequestAllFlow() {
        User[] user = new User[1];
        user[0] = new User();
        user[0].setUuid(uuid);
        user[0].setAuth_id(auth_id);
        Data data = new Data();
        data.setUser(user);
        Protocol protocol = new Protocol();
        protocol.setType("all_flow");
        protocol.setData(data);
        Gson gson = new Gson();
        String json = gson.toJson(protocol);
        if (socket != null && socket.isOpen()) {
            outJson( "Sending: " + json );
            Log.i("SERVER","Request All Flows");
            socket.send(json);
        }
    }

    public void sendRequestAllMessages(String flow_id, int time) {
        User[] user = new User[1];
        user[0] = new User();
        user[0].setUuid(uuid);
        user[0].setAuth_id(auth_id);
        Flow[] flow = new Flow[1];
        flow[0] = new Flow();
        flow[0].setUuid(flow_id);
        Data data = new Data();
        data.setUser(user);
        data.setFlow(flow);
        data.setTime(time);
        Protocol protocol = new Protocol();
        protocol.setType("all_messages");
        protocol.setData(data);
        Gson gson = new Gson();
        String json = gson.toJson(protocol);
        if (socket != null && socket.isOpen()) {
            outJson( "Sending: " + json );
            Log.i("SERVER","Request All Messages");
            socket.send(json);
        }
    }


}

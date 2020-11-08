package ru.wtw.moreliatalkclient;

public class UserSession {
    private String login;
    private String password;
    private String proto;
    private String server;
    private String port;
    private String name;
    private String email;
    private boolean authed;

    public UserSession(String login, String password, String proto, String server, String port, String name, String email) {
        this.login = login;
        this.password = password;
        this.proto = proto;
        this.server = server;
        this.port = port;
        this.name = name;
        this.email = email;
    }

    public UserSession(String login, String password, String proto, String server, String port) {
        this.login = login;
        this.password = password;
        this.proto = proto;
        this.server = server;
        this.port = port;
        this.name = name;
        this.email = email;
    }

    public boolean isAuthed() {
        return authed;
    }

    public void setAuthed() {
        this.authed = true;
    }

    public void setUnAuthed() {
        this.authed = false;
    }

    public String getLogin() {
        return login;
    }

    public String getServer() {
        return server;
    }

    public String getName() {
        return name;
    }
}

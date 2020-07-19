package ru.wtw.moreliatalkclient;

@SuppressWarnings("unused")
public class Protocol {

    private String mode;
    private String username;
    private String password;
    private String text;
    private String timestamp;
    private String status;

    public String getMode() {
        return mode;
    }

    public String getText() {
        return text;
    }

    public String getUsername() {
        return username;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) { this.status = status; }

    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public String getTimestamp() {
        return timestamp;
    }
}


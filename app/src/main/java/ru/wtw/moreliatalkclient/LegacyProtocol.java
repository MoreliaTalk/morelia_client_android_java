package ru.wtw.moreliatalkclient;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LegacyProtocol {

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

    public String getPassword() {
        return password;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getTime() {
        long javatime = Math.round(Double.parseDouble(timestamp) * 1000);
        Date time = new Date(javatime);
        DateFormat df = new SimpleDateFormat("HH:mm:ss");
        return df.format(time);
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

}


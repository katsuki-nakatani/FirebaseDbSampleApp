package org.example.firebasedbsampleapp.entity;

/**
 * Created by katsuki-nakatani on 2016/08/04.
 */

public class Message {
    private String content;
    private String uId;

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getContent() {

        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}

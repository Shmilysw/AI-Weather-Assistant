package com.example.myrobot;

public class ChatBean {
    public static final int SEND = 1;//发送信息
    public static final int RECEIVE = 2; //接收到的信息
    private int state;   //消息的状态（是接收还是发送）
    private String message; //消息的内容

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

package core.network;

import java.io.*;
import java.net.Socket;

public class SocketMessage implements Serializable{
    private Type type;
    private Object content;
    private String sender;
    private String receiver;

    public enum Type{
        LOGIN,                  // 登录请求
        LOGIN_SUCCESS,          // 成功
        LOGIN_FAIL,
        LIST,                   // 请求列表
        INVITE,                 // 邀请对战
        AGREE,                  // 对方同意
        REFUSE,                 // duifang拒绝
        STEP,                   // 移动
        RESTART_REQUEST,        // 重来
        RESTART_AGREE,          //
        RESTART_REFUSE,         //
        RESTART_EXCUTE,         //
        REGRET_REQUEST,         // 悔棋
        REGRET_AGREE,           //
        REGRET_REFUSE,
        REGRET_EXCUTE,          //
        BACK_TO_LIST,           //
        DEFEAT,                 // 认输
        TEXT,                   // 聊天
        NULL,                   // 无效消息
        RETRY                   // 请重试
    }

    public SocketMessage(Type type, Object obj) {
        this.type = type;
        this.content = obj;
    }

    public SocketMessage(Socket socket) {
        Object obj = reveive(socket);
        if(obj instanceof SocketMessage) {
            this.type = ((SocketMessage) obj).getType();
            this.content = ((SocketMessage) obj).getContent();
            this.receiver = ((SocketMessage) obj).getReceiver();
            this.sender =((SocketMessage) obj).getSender();
        }
        else{
            this.type = Type.NULL;
            this.content = null;
        }
    }

    public static Socket createSocket(String ip, int port) {
        try {
            Socket socket = new Socket(ip, port);
            return socket;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void send(Socket socket) {
        OutputStream outputStream = null;
        ObjectOutputStream objectOutputStream = null;
        try {
            outputStream = socket.getOutputStream();
            objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Object reveive(Socket socket) {
        InputStream inputStream = null;
        ObjectInputStream objectInputStream = null;
        try {
            inputStream = socket.getInputStream();
            objectInputStream = new ObjectInputStream(inputStream);
            return objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Type getType() {
        return type;
    }

    public Object getContent() {
        return content;
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }
}

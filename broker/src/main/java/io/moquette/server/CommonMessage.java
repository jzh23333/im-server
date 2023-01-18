package io.moquette.server;

import cn.wildfirechat.proto.WFCMessage;

import java.io.*;

public class CommonMessage implements Serializable {

    private static final long serialVersionUID = 7375178621546868315L;
    private String fromUser;
    private String fromClientId;
    private String serverClientId;
    private byte[] payload;

    /**
     * 对象转字节数组
     */
    public byte[] toByteArray() throws Exception {
        try(
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ObjectOutputStream sOut = new ObjectOutputStream(out);
        ){
            sOut.writeObject(this);
            sOut.flush();
            byte[] bytes = out.toByteArray();
            return bytes;
        }
    }

    /**
     * 字节数组转对象
     */
    public static CommonMessage bytesToObject(byte[] bytes) throws IOException, ClassNotFoundException {
        try(
            ByteArrayInputStream in = new ByteArrayInputStream(bytes);
            ObjectInputStream sIn = new ObjectInputStream(in);
        ){
            return (CommonMessage) sIn.readObject();
        }
    }

    @Override
    public String toString() {
        return "CommonMessage{" +
            "fromUser='" + fromUser + '\'' +
            ", fromClientId='" + fromClientId + '\'' +
            ", serverClientId='" + serverClientId + '\'' +
            '}';
    }

    public String getFromUser() {
        return fromUser;
    }

    public void setFromUser(String fromUser) {
        this.fromUser = fromUser;
    }

    public String getFromClientId() {
        return fromClientId;
    }

    public void setFromClientId(String fromClientId) {
        this.fromClientId = fromClientId;
    }

    public String getServerClientId() {
        return serverClientId;
    }

    public void setServerClientId(String serverClientId) {
        this.serverClientId = serverClientId;
    }

    public byte[] getPayload() {
        return payload;
    }

    public void setPayload(byte[] payload) {
        this.payload = payload;
    }
}

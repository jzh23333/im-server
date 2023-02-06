package cn.wildfirechat.pojos;

import java.util.List;

public class OutputMessageList {
    private List<PojoMessage> messageList;
    private int total;

    public List<PojoMessage> getMessageList() {
        return messageList;
    }

    public void setMessageList(List<PojoMessage> messageList) {
        this.messageList = messageList;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}

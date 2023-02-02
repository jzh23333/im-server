package cn.wildfirechat.pojos;

import java.util.List;

public class OutputGroupList {
    private List<PojoGroupInfo> groupInfos;
    private int total;

    public List<PojoGroupInfo> getGroupInfos() {
        return groupInfos;
    }

    public void setGroupInfos(List<PojoGroupInfo> groupInfos) {
        this.groupInfos = groupInfos;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}

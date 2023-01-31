package com.xiaoleilu.loServer.action.admin;


import cn.wildfirechat.common.APIPath;
import cn.wildfirechat.common.ErrorCode;
import cn.wildfirechat.pojos.*;
import cn.wildfirechat.proto.WFCMessage;
import com.xiaoleilu.loServer.RestResult;
import com.xiaoleilu.loServer.annotation.HttpMethod;
import com.xiaoleilu.loServer.annotation.Route;
import com.xiaoleilu.loServer.handler.Request;
import com.xiaoleilu.loServer.handler.Response;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.util.internal.StringUtil;

import java.util.ArrayList;
import java.util.List;

@Route(APIPath.Group_List)
@HttpMethod("POST")
public class GetGroupListAction extends AdminAction {

    @Override
    public boolean isTransactionAction() {
        return true;
    }

    @Override
    public boolean action(Request request, Response response) {
        if (request.getNettyRequest() instanceof FullHttpRequest) {
            InputGetGroup inputGetGroup = getRequestBody(request.getNettyRequest(), InputGetGroup.class);
            if (inputGetGroup != null
                && (!StringUtil.isNullOrEmpty(inputGetGroup.getGroupId()))) {

                List<WFCMessage.GroupInfo> groupInfos = messagesStore.getGroupInfos(inputGetGroup.getGroupId(), inputGetGroup.getGroupName(),
                    inputGetGroup.getPageNo(), inputGetGroup.getPageSize());

                OutputGroupList out = new OutputGroupList();
                out.setGroupInfos(new ArrayList<>());
                for (WFCMessage.GroupInfo groupInfo : groupInfos) {
                    PojoGroupInfo pojoGroupInfo = new PojoGroupInfo();
                    pojoGroupInfo.setExtra(groupInfo.getExtra());
                    pojoGroupInfo.setName(groupInfo.getName());
                    pojoGroupInfo.setOwner(groupInfo.getOwner());
                    pojoGroupInfo.setPortrait(groupInfo.getPortrait());
                    pojoGroupInfo.setTarget_id(groupInfo.getTargetId());
                    pojoGroupInfo.setType(groupInfo.getType());
                    pojoGroupInfo.setMute(groupInfo.getMute());
                    pojoGroupInfo.setJoin_type(groupInfo.getJoinType());
                    pojoGroupInfo.setPrivate_chat(groupInfo.getPrivateChat());
                    pojoGroupInfo.setSearchable(groupInfo.getSearchable());
                    out.getGroupInfos().add(pojoGroupInfo);
                }
                RestResult result = RestResult.ok(out);

                setResponseContent(result, response);
            } else {
                setResponseContent(RestResult.resultOf(ErrorCode.INVALID_PARAMETER), response);
            }

        }
        return true;
    }
}

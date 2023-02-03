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

                int total = messagesStore.getGroupInfosTotal(inputGetGroup.getGroupId(), inputGetGroup.getGroupName());

                OutputGroupList out = new OutputGroupList();
                out.setTotal(total);
                if (total > 0) {
                    List<PojoGroupInfo> groupInfos = messagesStore.getGroupInfos(inputGetGroup.getGroupId(), inputGetGroup.getGroupName(),
                        inputGetGroup.getPageNo(), inputGetGroup.getPageSize());
                    out.setGroupInfos(groupInfos);
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

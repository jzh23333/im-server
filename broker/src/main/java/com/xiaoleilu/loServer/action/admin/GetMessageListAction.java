package com.xiaoleilu.loServer.action.admin;


import cn.wildfirechat.common.APIPath;
import cn.wildfirechat.common.ErrorCode;
import cn.wildfirechat.pojos.InputGetMessage;
import cn.wildfirechat.pojos.OutputMessageList;
import cn.wildfirechat.pojos.PojoMessage;
import com.hazelcast.util.StringUtil;
import com.xiaoleilu.loServer.RestResult;
import com.xiaoleilu.loServer.annotation.HttpMethod;
import com.xiaoleilu.loServer.annotation.Route;
import com.xiaoleilu.loServer.handler.Request;
import com.xiaoleilu.loServer.handler.Response;
import io.netty.handler.codec.http.FullHttpRequest;

import java.util.List;

@Route(APIPath.Msg_GetList)
@HttpMethod("POST")
public class GetMessageListAction extends AdminAction {

    @Override
    public boolean isTransactionAction() {
        return true;
    }

    @Override
    public boolean action(Request request, Response response) {
        if (request.getNettyRequest() instanceof FullHttpRequest) {
            InputGetMessage getMessage = getRequestBody(request.getNettyRequest(), InputGetMessage.class);
            if (getMessage != null && getMessage.getTimestamp() == 0) {
                int total = messagesStore.getMessageListTotal(getMessage.getSearchable(), getMessage.getTimestamp(),
                    getMessage.getConversationType(), getMessage.getMessageType());

                OutputMessageList out = new OutputMessageList();
                out.setTotal(total);
                if (total > 0) {
                    List<PojoMessage> messageList = messagesStore.getMessageList(getMessage.getSearchable(), getMessage.getTimestamp(),
                        getMessage.getConversationType(), getMessage.getMessageType(), getMessage.getPageNo(), getMessage.getPageSize());
                    out.setMessageList(messageList);
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

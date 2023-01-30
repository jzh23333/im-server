package io.moquette.server;

import cn.wildfirechat.proto.ProtoConstants;
import cn.wildfirechat.proto.WFCMessage;
import io.moquette.spi.IMessagesStore;
import io.moquette.spi.ISessionsStore;
import io.moquette.spi.IStore;
import io.moquette.spi.impl.MessagesPublisher;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashSet;
import java.util.Set;

import static cn.wildfirechat.proto.ProtoConstants.RequestSourceType.*;

public class OnMessageCallback implements MqttCallback {
    private static final Logger LOG = LoggerFactory.getLogger(OnMessageCallback.class);
    private String clientId;
    private MessagesPublisher publisher;
    private IMessagesStore messagesStore;
    private ISessionsStore sessionsStore;
    private Server mServer;
    public void init(String clientId, MessagesPublisher publisher, IStore store, Server server) {
        this.clientId = clientId;
        this.publisher = publisher;
        this.messagesStore = store.messagesStore();
        this.sessionsStore = store.sessionsStore();
        this.mServer = server;
    }

    @Override
    public void connectionLost(Throwable throwable) {
        LOG.debug("连接断开，可以做重连");
    }

    @Override
    public void messageArrived(String s, MqttMessage mqttMessage) {
        LOG.info("Received message: topic: {}, qos: {}", s, mqttMessage.getQos());
        try {
            CommonMessage commonMessage = CommonMessage.bytesToObject(mqttMessage.getPayload());
            WFCMessage.Message message = WFCMessage.Message.parseFrom(commonMessage.getPayload());
            ProtoConstants.RequestSourceType requestSourceType = getRequestSourceType(commonMessage.getRequestSourceType());
            publish(commonMessage.getFromClientId(), message, requestSourceType);
        } catch (Exception e) {
            LOG.error("resolve message failure", e);
        }
    }

    private ProtoConstants.RequestSourceType getRequestSourceType(String name) {
        switch (name) {
            case "Request_From_Admin":
                return Request_From_Admin;
            case "Request_From_Robot":
                return Request_From_Robot;
            case "Request_From_Channel":
                return Request_From_Channel;
            default:
                return Request_From_User;
        }
    }

    private long publish(String clientId, WFCMessage.Message message, ProtoConstants.RequestSourceType requestSourceType) {
        Set<String> notifyReceivers = new LinkedHashSet<>();

        WFCMessage.Message.Builder messageBuilder = message.toBuilder();
        int pullType = messagesStore.getNotifyReceivers(message.getFromUser(), messageBuilder, notifyReceivers, requestSourceType);
        mServer.getImBusinessScheduler().execute(() -> this.publisher.publish2Receivers(messageBuilder.build(), notifyReceivers, clientId, pullType));
        return notifyReceivers.size();
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
        LOG.debug("deliveryComplete---------" + iMqttDeliveryToken.isComplete());
    }
}

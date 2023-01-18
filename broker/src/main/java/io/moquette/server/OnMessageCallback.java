package io.moquette.server;

import cn.wildfirechat.proto.WFCMessage;
import io.moquette.spi.IMessagesStore;
import io.moquette.spi.ISessionsStore;
import io.moquette.spi.IStore;
import io.moquette.spi.impl.MessagesPublisher;
import io.moquette.spi.impl.Qos1PublishHandler;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.fusesource.mqtt.client.MQTTException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashSet;
import java.util.Set;

import static cn.wildfirechat.proto.ProtoConstants.RequestSourceType.Request_From_User;

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
        // 连接丢失后，一般在这里面进行重连
        LOG.info("连接断开，可以做重连");
    }

    @Override
    public void messageArrived(String s, MqttMessage mqttMessage) {
        // subscribe后得到的消息会执行到这里面
        LOG.info("接收消息主题:" + s);
        LOG.info("接收消息Qos:" + mqttMessage.getQos());

        try {
            CommonMessage commonMessage = CommonMessage.bytesToObject(mqttMessage.getPayload());
            LOG.info("接收消息内容:" + commonMessage);
            WFCMessage.Message message = WFCMessage.Message.parseFrom(commonMessage.getPayload());
            LOG.info("接收消息:" + message);
        } catch (Exception e) {
            LOG.error("resolve message failure", e);
        }
    }

    private long saveAndPublish(String clientId, WFCMessage.Message message) {
        Set<String> notifyReceivers = new LinkedHashSet<>();
        WFCMessage.Message.Builder messageBuilder = message.toBuilder();
        int pullType = messagesStore.getNotifyReceivers(message.getFromUser(), messageBuilder, notifyReceivers, Request_From_User);
        mServer.getImBusinessScheduler().execute(() -> this.publisher.publish2Receivers(messageBuilder.build(), notifyReceivers, clientId, pullType));
        return notifyReceivers.size();
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
        LOG.info("deliveryComplete---------" + iMqttDeliveryToken.isComplete());
    }
}

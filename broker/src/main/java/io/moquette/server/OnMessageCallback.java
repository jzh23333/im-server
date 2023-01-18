package io.moquette.server;

import cn.wildfirechat.proto.WFCMessage;
import io.moquette.spi.impl.MessagesPublisher;
import io.moquette.spi.impl.Qos1PublishHandler;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.fusesource.mqtt.client.MQTTException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OnMessageCallback implements MqttCallback {
    private static final Logger LOG = LoggerFactory.getLogger(OnMessageCallback.class);
    private String clientId;
    private MessagesPublisher publisher;
    public OnMessageCallback(String clientId, MessagesPublisher publisher) {
        this.clientId = clientId;
        this.publisher = publisher;
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
            WFCMessage.Message message = WFCMessage.Message.parseFrom(mqttMessage.getPayload());
            LOG.info("接收消息内容:" + message);
        } catch (Exception e) {
            LOG.error("resolve message failure", e);
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
        LOG.info("deliveryComplete---------" + iMqttDeliveryToken.isComplete());
    }
}

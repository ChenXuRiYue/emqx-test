package Axel.Chen.consumer;

import com.google.gson.Gson;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.awt.print.Printable;
import java.lang.reflect.Type;
import java.util.Map;


public class Consumer {

    private MqttClient client;
    private final static Gson gson = new Gson();
    private static int cunt = 0;


    /**
     * 完成基础的客户端创建初始化
     */
    public Consumer(String broker, String clientId) {
        try {
            // 创建客户端
            this.client = new MqttClient(broker, clientId);
            // 设置客户端回调处理逻辑
            this.client.setCallback(new OnMessageCallback());
        } catch (MqttException e) {
            // 打印错误信息
            System.out.println("error occurs in constructor");
            printError(e);
        }
    }

    /**
     * 连接并且订阅主题
     */
    public void connectAndSubscribe(String topic) {
        try {
            // 客户端连接服务器
            this.client.connect();
            // 订阅主题
            client.subscribe(topic);

        } catch (MqttException e) {
            // 打印错误信息
            System.out.println("error occurs in connectAndSubscribe");
            printError(e);
        }
    }

    /**
     * 释放资源
     */
    public void disconnect() {
        try {
            this.client.disconnect();
        } catch (MqttException e) {
            System.out.println("error occurs in disconnect");
            printError(e);
        }
    }

    // 集中处理错误输出日志
    void printError(MqttException e) {
        System.out.println("reason " + e.getReasonCode());
        System.out.println("msg " + e.getMessage());
        System.out.println("loc " + e.getLocalizedMessage());
        System.out.println("cause " + e.getCause());
        System.out.println("except " + e);
    }

    // 定义消息回调处理类
    static class OnMessageCallback implements MqttCallback {
        public void connectionLost(Throwable cause) {
            // 连接丢失后，一般在这里面进行重连
            System.out.println("连接断开，可以做重连");
        }

        public void messageArrived(String topic, MqttMessage message) throws Exception {
            // subscribe后得到的消息会执行到这里面
            System.out.println("接收消息主题:" + topic);
            System.out.println("接收消息Qos:" + message.getQos());
            System.out.println("接收消息内容:" + new String(message.getPayload()));
            // 将json消息的格式转换成 map
            message.getPayload();
            // 将 payload 转换成 String , Long 两个变量
            String messageString = new String(message.getPayload());
            Map<String, String> messageMap = gson.fromJson(messageString, Map.class);
            System.out.println(messageMap);
            MessageCalculate.addMessage(messageMap.get("type").toString(), Long.parseLong((String) messageMap.get("timestamp")));
            cunt++;
            System.out.println("cunt:\t" + cunt);
        }

        public void deliveryComplete(IMqttDeliveryToken token) {
            System.out.println("deliveryComplete---------" + token.isComplete());
        }
    }
}
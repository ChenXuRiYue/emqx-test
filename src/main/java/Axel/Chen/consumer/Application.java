package Axel.Chen.consumer;

import Axel.Chen.constant.MqttConfig;

// 总应用框架，整合消费者和响应
public class Application {
    static final String clientId = "consumer";
    static final String broker = MqttConfig.broker;
    static final String topic = MqttConfig.topic;

    public static void main(String[] args) {
        try {
            // 创建MQTT 客户端
            Consumer consumer = new Consumer(broker, clientId);
            consumer.connectAndSubscribe(topic);
            // 创建服务器，响应Restful 请求
            RestfulService restfulService = new RestfulService(8080);
            restfulService.start();

            // 休眠监听
            while (true) {
                Thread.sleep(1000);
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

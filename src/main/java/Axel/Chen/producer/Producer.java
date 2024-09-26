package Axel.Chen.producer;

import Axel.Chen.constant.MqttConfig;
import com.google.gson.Gson;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Producer {

    private static final String clientId = "producer";
    private static final String broker = MqttConfig.broker;
    private static final String TOPIC = MqttConfig.topic;
    // 设置等级为2 通过两端的四步握手过程，确保消息仅被接收一次。
    private static final Integer QOS = MqttConfig.QOS;
    private static final Gson gson = new Gson();

    public static void main(String[] args) {
        try {
            MqttClient client = new MqttClient(broker, clientId, new MemoryPersistence());
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);

            // 连接Broker
            System.out.println("Connecting to broker: " + broker);
            client.connect(connOpts);
            System.out.println("Connected");

            // 发送100 条随机消息
            for (int i = 0; i < 100; i++) {
                String payload = generateRandomMessage();
                MqttMessage message = new MqttMessage(payload.getBytes());
                message.setQos(QOS);

                // 推送消息到 Broker
                client.publish(TOPIC, message);
                System.out.println("Message " + (i + 1) + " sent: " + payload);

                // 等待 0.05s
                Thread.sleep(50);  // 500 ms delay between messages
            }

            // Disconnect
            client.disconnect();
            System.out.println("Disconnected");

        } catch (MqttException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 生成随机消息
     */

    static Integer cunt = 0;
    private static String generateRandomMessage() {
        Map<String, String> messageMap = new HashMap<>();
        // 随机生成类型
        String[] types = {"A", "B", "C", "D"};
//        String randomType = types[new Random().nextInt(types.length)];
        String randomType = "A";
        messageMap.put("type", randomType);

        // 得到当前时间戳，转换成秒数。 并且随机加减 30s
        // long currentTimestamp = System.currentTimeMillis() / 1000;
        // 方便测试
        long currentTimestamp = 0;
//        long randomTimestamp = ThreadLocalRandom.current().nextLong(currentTimestamp - 30, currentTimestamp + 30);
        long randomTimestamp = currentTimestamp + cunt++;
        messageMap.put("timestamp", (String.valueOf(randomTimestamp)));


        return gson.toJson(messageMap);
    }
}


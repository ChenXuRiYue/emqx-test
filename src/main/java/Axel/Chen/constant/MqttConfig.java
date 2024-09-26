package Axel.Chen.constant;

import jdk.dynalink.beans.StaticClass;

// 封装了常用的配置项
public class MqttConfig {
    public static  final String broker = "tcp://localhost:1883";
    public static  final String topic = "test";
    public static  final Integer QOS = 2;
}

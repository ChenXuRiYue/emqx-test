package Axel.Chen.consumer;

import Axel.Chen.datastruct.Node;
import Axel.Chen.datastruct.SkipList;
import com.google.gson.Gson;

import java.util.*;

public class MessageCalculate {
    // 自定义快表
    private static final SkipList<Long, String> skipList = new SkipList<>();

    public static void addMessage(String type, long timestamp) {
        skipList.insert(timestamp, type);
        System.out.println(skipList.getSize());
    }

    /*
     * 获取各种类型消息的总数
     */
    public static Map<String, Long> getMessage(Long startTime, Long endTime) {
        Map<String, Long> result = new HashMap<>();
        for (Node<Long, String> node : skipList.search(startTime, endTime)) {
            result.put(node.getValue(), result.getOrDefault(node.getValue(), 0L) + 1);
        }
        return result;
    }

    /**
     * 将上述getMessage 同时封装成json 格式
     */
    public static String getMessageJson(Long startTime, Long endTime) {
        return new Gson().toJson(getMessage(startTime, endTime));
    }

    public static void main(String[] args) {
        addMessage("A", 1L);
        addMessage("A", 5L);
        addMessage("A", 9L);
        addMessage("B", 10L);
        addMessage("B", 2L);
        addMessage("B", 6L);
        addMessage("C", 3L);
        addMessage("C", 7L);
        addMessage("D", 4L);
        addMessage("D", 8L);
        System.out.println(getMessageJson(1L, 10L));
    }
}

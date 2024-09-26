package Axel.Chen.consumer;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

// 响应
public class RestfulService {
    private static final int PORT = 8080;
    private final HttpServer server;

    /**
     * 创建一个HTTP服务器，并注册请求处理器
     */
    public RestfulService(int port) throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/get/message/calculation", new RequestHandler());
    }

    public RestfulService() throws IOException {
        this(PORT);
    }

    /**
     * 启动HTTP服务器
     */
    public void start() {
        server.start();
        System.out.println("Server started on port " + PORT);
    }

    /**
     * 暂停HTTP服务器
     */
    public void stop() {
        server.stop(0);
        System.out.println("Server stopped.");
    }

    // 定义请求处理器
    private static class RequestHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String requestMethod = exchange.getRequestMethod();
            if ("GET".equalsIgnoreCase(requestMethod)) {
                URI uri = exchange.getRequestURI();
                // 获取 params 中的参数
                String query = uri.getQuery();
                // 从 query 中 获取 starttime, endtime
                String[] params = query.split("&");
                Map<String, Long> paramsMap = new HashMap<>();
                for(String str: params){
                    String[] param = str.split("=");
                    paramsMap.put(param[0], Long.parseLong(param[1]));
                }
                // 分钟转换成秒
//                Long starTime = paramsMap.get("startTime") * 60L;
//                Long endTime = paramsMap.get("endTime") * 60L;
                // 方便测试
                Long starTime = paramsMap.get("startTime");
                Long endTime = paramsMap.get("endTime");

                String jsonResponse = MessageCalculate.getMessageJson(starTime, endTime);
                // 返回响应示例
                exchange.sendResponseHeaders(200, jsonResponse.length());
                exchange.getResponseBody().write(jsonResponse.getBytes());
                exchange.close();
            }
        }
    }
}

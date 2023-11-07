package com.example.demo.spider.config;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class MyHandler extends TextWebSocketHandler {


    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
        // 处理接收到的消息
    }
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // 当有新连接建立时，向客户端发送爬取数据
//        session.sendMessage(new TextMessage(crawlData));
    }
}

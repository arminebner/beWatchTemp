package com.example.demo.config;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.protocol.JacksonJsonSupport;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

@org.springframework.context.annotation.Configuration
public class SocketIoConfiguration {
    @Bean(initMethod = "start", destroyMethod = "stop")
    public SocketIOServer socketIOServer(
            @Value("${socketio.host:0.0.0.0}") String host,
            @Value("${socketio.port:9092}") int port,
            @Value("${socketio.allowed-origin:*}") String allowedOrigin) {
        Configuration configuration = new Configuration();
        configuration.setHostname(host);
        configuration.setPort(port);
        configuration.setOrigin(allowedOrigin);
        configuration.setJsonSupport(new JacksonJsonSupport(new JavaTimeModule()) {
            @Override
            protected void init(ObjectMapper objectMapper) {
                objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
                super.init(objectMapper);
            }
        });
        return new SocketIOServer(configuration);
    }
}

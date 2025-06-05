package com.example.notificationservice.kafka;

import com.example.notificationservice.config.NotificationProperties;
import com.example.userservice.event.UserEvent;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.SimpleMailMessage;

import static org.mockito.Mockito.*;

@SpringBootTest
@Import({UserEventConsumerTest.TestConfig.class, UserEventConsumerTest.TestKafkaConfig.class})
class UserEventConsumerTest {

    @Configuration
    static class TestConfig {
        @Bean
        public JavaMailSender javaMailSender() {
            return mock(JavaMailSender.class);
        }

        @Bean
        public KafkaTemplate<String, UserEvent> kafkaTemplate() {
            return mock(KafkaTemplate.class);
        }

        @Bean
        public UserEventConsumer userEventConsumer() {
            return new UserEventConsumer(
                    kafkaTemplate(),
                    javaMailSender(),
                    Mockito.mock(NotificationProperties.class)
            );
        }
    }

    @Configuration
    static class TestKafkaConfig {
        @Bean
        public ConcurrentKafkaListenerContainerFactory<String, UserEvent>
        kafkaListenerContainerFactory() {
            return mock(ConcurrentKafkaListenerContainerFactory.class);
        }
    }

    @Autowired
    private UserEventConsumer userEventConsumer;

    @MockBean
    private JavaMailSender mailSender;

    @Test
    void shouldProcessUserEvent() {
        UserEvent event = new UserEvent("user@test.com", "CREATE", "Test");

        userEventConsumer.handleUserEvent(event);

        verify(mailSender).send(any(SimpleMailMessage.class));
    }
}
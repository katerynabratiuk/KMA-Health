package kma.health.app.kma_health.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SlackNotificationServiceTest {

    @Mock
    private RestTemplate restTemplate;

    private SlackNotificationService slackNotificationService;

    @BeforeEach
    void setUp() {
        slackNotificationService = new SlackNotificationService();
        ReflectionTestUtils.setField(slackNotificationService, "webhookUrl", "https://hooks.slack.com/test");
        ReflectionTestUtils.setField(slackNotificationService, "restTemplate", restTemplate);
    }

    @Test
    void testSendMessage_Success() {
        when(restTemplate.postForEntity(anyString(), any(Map.class), eq(String.class)))
                .thenReturn(ResponseEntity.ok("ok"));

        slackNotificationService.sendMessage("Test message");

        verify(restTemplate).postForEntity(eq("https://hooks.slack.com/test"), any(Map.class), eq(String.class));
    }

    @Test
    void testSendMessage_HandlesException() {
        when(restTemplate.postForEntity(anyString(), any(Map.class), eq(String.class)))
                .thenThrow(new RuntimeException("Connection failed"));

        // Should not throw - exception is caught internally
        slackNotificationService.sendMessage("Test message");

        verify(restTemplate).postForEntity(anyString(), any(Map.class), eq(String.class));
    }
}


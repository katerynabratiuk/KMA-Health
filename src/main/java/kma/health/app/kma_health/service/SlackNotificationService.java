package kma.health.app.kma_health.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class SlackNotificationService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${slack.webhook.url}")
    private String webhookUrl;

    public void sendMessage(String message) {
        try {
            Map<String, String> payload = Map.of("text", message);
            restTemplate.postForEntity(webhookUrl, payload, String.class);
        } catch (Exception e) {
            System.err.println("Failed to send Slack notification: " + e.getMessage());
        }
    }
}


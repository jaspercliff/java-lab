package com.jasper.controller;

import com.jasper.AlertmanagerWebhookRequest;
import com.jasper.service.AlertWebhookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@Slf4j
@RestController
@RequestMapping("/webhook")
public class AlertWebhookController {

    private final AlertWebhookService alertWebhookService;
    private final String token;

    public AlertWebhookController(
            AlertWebhookService alertWebhookService,
            @Value("${alert.webhook.token}") String token) {
        this.alertWebhookService = alertWebhookService;
        this.token = token;
    }

    @PostMapping("/alert")
    public ResponseEntity<Void> receive(
            @RequestHeader(value = "Authorization", required = false)
            String authorization,
            @RequestBody AlertmanagerWebhookRequest request) {

        String expected = "Bearer " + token;

        if (authorization == null ||
                !MessageDigest.isEqual(
                        expected.getBytes(StandardCharsets.UTF_8),
                        authorization.getBytes(StandardCharsets.UTF_8))) {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        alertWebhookService.handle(request);

        return ResponseEntity.accepted().build();
    }
}
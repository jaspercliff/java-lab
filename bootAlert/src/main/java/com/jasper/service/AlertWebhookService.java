package com.jasper.service;

import com.jasper.AlertmanagerWebhookRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AlertWebhookService {

    public void handle(AlertmanagerWebhookRequest request) {

        if (request.alerts() == null) {
            return;
        }

        for (var alert : request.alerts()) {

            String alertName =
                    alert.labels().getOrDefault("alertname", "Unknown");

            String severity =
                    alert.labels().getOrDefault("severity", "unknown");

            String application =
                    alert.labels().getOrDefault("application", "unknown");

            String summary =
                    alert.annotations().getOrDefault("summary", "");

            String description =
                    alert.annotations().getOrDefault("description", "");

            if ("firing".equals(alert.status())) {
                log.error("""
                        ================= ALERT FIRING =================
                        告警名称: {}
                        严重程度: {}
                        应用名称: {}
                        摘要: {}
                        详情: {}
                        开始时间: {}
                        Fingerprint: {}
                        ===================================================
                        """,
                        alertName,
                        severity,
                        application,
                        summary,
                        description,
                        alert.startsAt(),
                        alert.fingerprint());

            } else if ("resolved".equals(alert.status())) {

                log.info("""
                        ================= ALERT RESOLVED ================
                        告警名称: {}
                        应用名称: {}
                        摘要: {}
                        恢复时间: {}
                        Fingerprint: {}
                        ===================================================
                        """,
                        alertName,
                        application,
                        summary,
                        alert.endsAt(),
                        alert.fingerprint());
            }
        }
    }
}
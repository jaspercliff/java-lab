package com.jasper;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AlertmanagerWebhookRequest(
        String version,
        String groupKey,
        String status,
        String receiver,
        Map<String, String> groupLabels,
        Map<String, String> commonLabels,
        Map<String, String> commonAnnotations,
        String externalURL,
        List<Alert> alerts
) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Alert(
            String status,
            Map<String, String> labels,
            Map<String, String> annotations,
            String startsAt,
            String endsAt,
            String generatorURL,
            String fingerprint
    ) {
    }
}
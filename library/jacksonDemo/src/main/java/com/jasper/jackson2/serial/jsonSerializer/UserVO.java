package com.jasper.jackson2.serial.jsonSerializer;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public record UserVO(
        Long id,
        String username,

        @JsonSerialize(using = PhoneSerializer.class)
        String phone
) {
}
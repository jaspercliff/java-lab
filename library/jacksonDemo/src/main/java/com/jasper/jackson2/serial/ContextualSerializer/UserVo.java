package com.jasper.jackson2.serial.ContextualSerializer;

public record UserVo(
        @Sensitive(SensitiveType.PHONE) String phone
        ,@Sensitive(SensitiveType.EMAIL) String email
) {
}

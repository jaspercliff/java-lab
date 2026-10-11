package com.jasper.constants;

import com.jasper.api.IEnum;


public enum ThreadType implements IEnum<String> {

    VIRTUAL("virtual", "virtual thread"),
    PLATFORM("platform", "platform thread");

    private final String code;
    private final String desc;

    ThreadType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    public static ThreadType getDefault() {
        return PLATFORM;
    }
    public static ThreadType fromStringOrDefault(String value) {
        try {
            if (value.trim().isEmpty()) {
                return getDefault();
            }
            return IEnum.parseOf(ThreadType.class, value.trim());
        } catch (IllegalArgumentException e) {
            return getDefault();
        }

    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getDesc() {
        return desc;
    }
}
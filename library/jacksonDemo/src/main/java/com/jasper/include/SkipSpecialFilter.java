package com.jasper.include;

public class SkipSpecialFilter {

    @Override
    public boolean equals(final Object obj) {
        return obj instanceof String && obj.equals("special");
    }
}

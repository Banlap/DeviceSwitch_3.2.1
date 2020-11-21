package com.aliyun.iot.ilop.demo.page.language;

import java.io.Serializable;
import java.util.Locale;

class LanguageModel implements Serializable {
    private static final long serialVersionUID = 7490956987077532930L;

    public String language;
    public Locale locale;

    LanguageModel(String language, Locale locale) {
        this.language = language;
        this.locale = locale;
    }
}

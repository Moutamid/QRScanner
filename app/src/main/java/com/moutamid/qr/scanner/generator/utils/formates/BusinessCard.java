package com.moutamid.qr.scanner.generator.utils.formates;

import static com.moutamid.qr.scanner.generator.utils.formates.SchemeUtil.LINE_FEED;
import static com.moutamid.qr.scanner.generator.utils.formates.SchemeUtil.getParameters;

import java.io.Serializable;
import java.util.Map;

public class BusinessCard extends Schema implements Serializable {
    public static final String TITLE = "TITLE";
    private static final String BEGIN_EVENT = "BEGIN:CARD";
    private static final String CONTENT = "CONTENT";
    private static final String TIMESTAMP = "TIMESTAMP";
    private String title;
    private String content;
    private long timestamp;

    public BusinessCard(){
        super();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public Schema parseSchema(String code) {
        if (code == null || !code.startsWith(BEGIN_EVENT)) {
            throw new IllegalArgumentException("this is not a valid VEVENT code: " + code);
        }
        Map<String, String> parameters = getParameters(code);
        if (parameters.containsKey(TITLE)) {
            setTitle(parameters.get(TITLE));
        }
        if (parameters.containsKey(CONTENT)) {
            setContent(parameters.get(CONTENT));
        }
        if (parameters.containsKey(TIMESTAMP)) {
            setTimestamp(Long.parseLong(parameters.get(TIMESTAMP)));
        }

        Map<String, String> param = getParameters(code);
        // TODO
        return this;
    }

    @Override
    public String generateString() {
        StringBuilder sb = new StringBuilder();
        sb.append(BEGIN_EVENT).append(LINE_FEED);
        if (title != null) {
            sb.append(TITLE).append(":").append(title).append(LINE_FEED);
        }if (content != null) {
            sb.append(CONTENT).append(":").append(content).append(LINE_FEED);
        }if (timestamp != 0) {
            sb.append(TIMESTAMP).append(";").append(timestamp).append(LINE_FEED);
        }
        sb.append(LINE_FEED).append("END:VEVENT");
        return sb.toString();
    }

    @Override
    public String toString() {
        return generateString();
    }

    public static BusinessCard parse( final String code) {
        BusinessCard event = new BusinessCard();
        event.parseSchema(code);
        return event;
    }
}

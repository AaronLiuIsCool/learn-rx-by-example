package com.samples.rx.$3advance.domain;

public class Errare {

    private String correlationId;

    private String code;
    private String message;

    private String stream;
    private boolean streamTerminated;

    private long timestamp;
    private Throwable throwable;

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStream() {
        return stream;
    }

    public void setStream(String stream) {
        this.stream = stream;
    }

    public boolean isStreamTerminated() {
        return streamTerminated;
    }

    public void setStreamTerminated(boolean streamTerminated) {
        this.streamTerminated = streamTerminated;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }
}

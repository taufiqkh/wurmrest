package com.quiptiq.wurmrest;

/**
 * Testing class for receiving results, allows for values that wouldn't be allowed for Result.
 */
public class RawResult<T> {
    private String error;
    private T value;

    public void setError() {
        this.error = error;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public String getError() {
        return error;
    }

    public T getValue() {
        return value;
    }
}

package com.quiptiq.wurmrest;

import javax.annotation.Nonnull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents the result of an RmiGameService call. A result may either be an error or a success, as
 * long as it is created with {@link #error(String)} with a non-null error message or with
 * {@link #success(Object)}. The behaviour for calling {@link #error(String)} with a null message is
 * undefined.
 *
 * @param <T> Type of the result value.
 */
public class Result<T> {
    private final String errorMessage;
    private final T value;

    /**
     * Create a new error result with the specified message and time.
     * @param errorMessage Message for the error.
     */
    public static <T> Result<T> error(@Nonnull String errorMessage) {
        return new Result<>(errorMessage, null);
    }

    public static <T> Result<T> success(T value) {
        return new Result<>(null, value);
    }

    private Result(String errorMessage, T value) {
        this.errorMessage = errorMessage;
        this.value = value;
    }

    /**
     * Whether or not the result was an error.
     *
     * @return True if the result was an error, otherwise false.
     */
    @JsonIgnore
    public boolean isError() {
        return errorMessage != null;
    }

    @JsonIgnore
    public boolean isSuccess() {
        return errorMessage == null;
    }

    /**
     * @return Error for this result. If this result is not in error, returns null.
     */
    @JsonProperty("error")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String getError() {
        return errorMessage;
    }

    /**
     * @return value for this result. If this result is in error, returns null. Will return also
     * return null for success if the success value was null.
     */
    public T getValue() {
        return value;
    }
}

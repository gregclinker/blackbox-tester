package com.essexboy;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"responseStatusCode", "executionTime", "exception", "responseBody"})
public class HttpTestResult {
    private String responseBody;
    private int responseStatusCode;
    private String exception;
    private long executionTime;

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("HttpTestResult{");
        sb.append("responseBody='").append(getResponseBody()).append('\'');
        sb.append(", responseStatusCode=").append(responseStatusCode);
        if (exception != null) {
            sb.append(", exception='").append(exception).append('\'');
        }
        sb.append(", executionTime=").append(executionTime);
        sb.append('}');
        return sb.toString();
    }
}

package com.essexboy;

import lombok.Getter;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonPropertyOrder;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@Getter
@Setter
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonPropertyOrder({"responseStatusCode", "executionTime", "exception", "responseBody"})
public class HttpTestResult {
    private String responseBody;
    private int responseStatusCode;
    private String exception;
    private long executionTime;

    public String getResponseBody() {
        if (responseBody != null) {
            return responseBody.replaceAll("\\s+", " ").replaceAll("\\s+([\\{\\}\\[\\]\"])", "$1");
        }
        return null;
    }

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

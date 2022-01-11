package com.essexboy;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"description", "url", "method", "headers", "body", "expected", "httpTestResult", "good"})
public class HttpTest {
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
    List<HttpTestHeader> headers = new ArrayList<>();
    private String description;
    private String url;
    private String method;
    private String body;
    private Expected expected = new Expected();
    private HttpTestResult httpTestResult;

    public Boolean isGood() {
        if (httpTestResult == null) {
            return null;
        }
        return isGood(httpTestResult);
    }

    @JsonIgnore
    public boolean isGood(HttpTestResult httpTestResult) {
        boolean ok;
        ok = httpTestResult.getResponseStatusCode() == expected.getHttpStatus();
        for (String contains : expected.getContains()) {
            ok = ok && httpTestResult.getResponseBody().contains(contains);
        }
        return ok;
    }

    @JsonIgnore
    public String getSummary() {
        return description + " (" + method + ":" + url + ")";
    }
}

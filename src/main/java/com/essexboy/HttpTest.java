package com.essexboy;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class HttpTest {
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
    List<HttpTestHeader> headers = new ArrayList<>();
    private String description;
    private String url;
    private String method;
    private String body;
    private Expected expected = new Expected();
    private HttpTestResult httpTestResult;

    @JsonIgnore
    public boolean isGood() {
        if (httpTestResult != null) {
            return isGood(httpTestResult);
        }
        return false;
    }

    @JsonIgnore
    public boolean isGood(HttpTestResult httpTestResult) {
        boolean ok = true;
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

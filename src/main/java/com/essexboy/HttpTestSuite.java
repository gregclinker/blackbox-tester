package com.essexboy;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonPropertyOrder;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonPropertyOrder({"description", "verbose", "threads", "repeat", "delay", "timeout"})
public class HttpTestSuite {
    @JsonIgnore
    private boolean verbose;
    private String description;
    private int timeout = 5;
    private int repeat = 1;
    private int threads = 1;
    private int delay = 0;
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
    private List<HttpTest> httpTests = new ArrayList<>();

    @JsonIgnore
    public String getSummary() {
        return "HttpTestSuite{" + "verbose=" + verbose +
                ", description='" + description + '\'' +
                ", timeout=" + timeout + "s" +
                ", repeat=" + repeat +
                ", threads=" + threads +
                ", delay=" + delay +
                ", tests=" + httpTests.size() +
                '}';
    }

    @JsonIgnore
    public Integer getSlice() {
        int totalTests = repeat * threads * httpTests.size();
        if (totalTests < 100) {
            return 1;
        } else {
            return totalTests / 100;
        }
    }

    @JsonIgnore
    public String getProgressBar() {
        int totalTests = repeat * threads * httpTests.size();
        int count = 100;
        if (totalTests < 100) {
            count = totalTests;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("|");
        for (int i = 1; i < count - 1; i++) {
            stringBuilder.append("-");
        }
        stringBuilder.append("|");
        return stringBuilder.toString();
    }

    @JsonIgnore
    public boolean isLoadTest() {
        return repeat > 1 || threads > 1;
    }
}

package com.essexboy;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"description", "verbose", "log", "threads", "repeat", "delay", "timeout"})
public class HttpTestSuite {
    private boolean verbose;
    private boolean log;
    private String description;
    private int timeout = 5;
    private int repeat = 1;
    private int threads = 1;
    private int delay = 0;
    @JsonInclude(JsonInclude.Include.NON_NULL)
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
        int perThreadTests = repeat * httpTests.size();
        if (totalTests < 100) {
            return 1;
        } else if (threads > 100) {
            return perThreadTests;
        } else {
            return totalTests / 100;
        }
    }

    @JsonIgnore
    public String getProgressBar() {
        int totalTests = repeat * threads * httpTests.size();
        int perThreadTests = repeat * httpTests.size();
        int count = 100;
        if (totalTests < 100) {
            count = totalTests;
        } else if (threads > 100) {
            count = threads;
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

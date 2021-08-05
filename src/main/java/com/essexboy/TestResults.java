package com.essexboy;

import lombok.Getter;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonPropertyOrder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
@JsonPropertyOrder({"count", "failed", "passed", "min", "max", "average", "greaterThan1000ms", "lessThan1000ms", "lessThan500ms", "lessThan100ms", "lessThan50ms", "lessThan10ms", "p99", "p90"})
public class TestResults {
    @JsonIgnore
    private List<LoadTestResult> loadTestResults = Collections.synchronizedList(new ArrayList<>());

    public void add(LoadTestResult loadTestResult) {
        loadTestResults.add(loadTestResult);
    }

    public Integer getCount() {
        return loadTestResults.size();
    }

    public Long getPassed() {
        return loadTestResults.stream().filter(s -> s.isPassed()).count();
    }

    public Long getFailed() {
        return loadTestResults.stream().filter(s -> !s.isPassed()).count();
    }

    public String getMax() {
        Long max = 0l;
        for (LoadTestResult loadTestResult : loadTestResults) {
            if (loadTestResult.getExecutionTime() > max) {
                max = loadTestResult.getExecutionTime();
            }
        }
        return max + "ms";
    }

    public String getMin() {
        Long min = 100000l;
        for (LoadTestResult loadTestResult : loadTestResults) {
            if (loadTestResult.getExecutionTime() < min) {
                min = loadTestResult.getExecutionTime();
            }
        }
        return min + "ms";
    }

    public String getAverage() {
        Long total = 0l;
        for (LoadTestResult loadTestResult : loadTestResults) {
            total += loadTestResult.getExecutionTime();
        }
        return total / loadTestResults.size() + "ms";
    }

    public String getGreaterThan1000ms() {
        return getGreaterThanMs(1000l);
    }

    public String getLessThan1000ms() {
        return getLessThanMs(1000l);
    }

    public String getLessThan500ms() {
        return getLessThanMs(500l);
    }

    public String getLessThan100ms() {
        return getLessThanMs(100l);
    }

    public String getLessThan50ms() {
        return getLessThanMs(50l);
    }

    public String getLessThan10ms() {
        return getLessThanMs(10l);
    }

    public String getP99() {
        return percentile(99) + "ms";
    }

    public String getP90() {
        return percentile(90) + "ms";
    }

    private String getLessThanMs(long time) {
        Integer count = 0;
        for (LoadTestResult loadTestResult : loadTestResults) {
            if (loadTestResult.getExecutionTime() < time) {
                count++;
            }
        }
        return toPercent(count);
    }

    private String getGreaterThanMs(long time) {
        Integer count = 0;
        for (LoadTestResult loadTestResult : loadTestResults) {
            if (loadTestResult.getExecutionTime() > time) {
                count++;
            }
        }
        return toPercent(count);
    }

    private String toPercent(Integer count) {
        if (count > 0) {
            return ((Double) ((count.doubleValue() / loadTestResults.size()) * 100.0)).intValue() + "%";
        }
        return "0%";
    }

    private Long percentile(Integer percentile) {
        Double aDouble = (((double) loadTestResults.size() / 100.0) * (double) percentile) + 0.5;
        Math.floor(aDouble);
        int limit = aDouble.intValue();
        return loadTestResults.stream().map(s -> s.getExecutionTime()).sorted().limit(limit).max(Long::compare).get();
    }
}

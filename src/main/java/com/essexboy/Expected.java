package com.essexboy;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
public class Expected {
    private int httpStatus;
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
    private List<String> contains = new ArrayList<>();
}

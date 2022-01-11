package com.essexboy;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

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

package com.essexboy;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.http.NameValuePair;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class BBNameValuePair implements NameValuePair {
    private String name;
    private String value;
}

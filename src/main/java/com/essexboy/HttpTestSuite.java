package com.essexboy;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
public class HttpTestSuite {
    @JsonIgnore
    private boolean verbose;
    private String description;
    private String gatewayAlrayanbank;
    @JsonIgnore
    private String refreshToken;
    @JsonIgnore
    private String accessToken;
    private String clientId;
    private String clientSecret;
    private String redirectUrl;
    private int timeout = 5;
    private int repeat = 1;
    private int threads = 1;
    private int delay = 0;
    private String tokenLifeTime = "365d";
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<BBNameValuePair> contstants = new ArrayList<>();
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<HttpTest> httpTests = new ArrayList<>();

    public void setTags() {
        for (HttpTest httpTest : httpTests) {
            httpTest.setUrl(httpTest.getUrl().replaceAll("\\$\\{gatewayAlrayanbank}", gatewayAlrayanbank));
        }
        for (HttpTest httpTest : httpTests) {
            for (BBNameValuePair BBNameValuePair : contstants) {
                httpTest.setUrl(httpTest.getUrl().replaceAll("\\$\\{" + BBNameValuePair.getName() + "}", BBNameValuePair.getValue()));
            }
        }
    }

    public synchronized String getAccessToken() {
        return accessToken;
    }

    public synchronized void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    @JsonIgnore
    public String getSummary() {
        return "HttpTestSuite{" + "verbose=" + verbose +
                ", description='" + description + '\'' +
                ", gatewayAlrayanbank='" + gatewayAlrayanbank + '\'' +
                ", refreshToken='" + refreshToken + '\'' +
                ", clientId='" + clientId + '\'' +
                ", clientSecret='" + clientSecret + '\'' +
                ", redirectUrl='" + redirectUrl + '\'' +
                ", timeout=" + timeout + "s" +
                ", repeat=" + repeat +
                ", threads=" + threads +
                ", delay=" + delay +
                ", tokenLifeTime=" + tokenLifeTime +
                ", contstants=" + contstants +
                '}';
    }

    @JsonIgnore
    public int getTokenLifeTimeSeconds() {
        String units = tokenLifeTime.substring(tokenLifeTime.length() - 1, tokenLifeTime.length());
        int scale = Integer.parseInt(tokenLifeTime.substring(0, tokenLifeTime.length() - 1));
        int tokenLifeTimeInSeconds = 60 * 60 * 24 * 7;
        switch (units) {
            case "m":
                tokenLifeTimeInSeconds = 60 * scale;
                break;
            case "h":
                tokenLifeTimeInSeconds = 60 * 60 * scale;
                break;
            case "d":
                tokenLifeTimeInSeconds = 60 * 60 * 24 * scale;
                break;
            case "w":
                tokenLifeTimeInSeconds = 60 * 60 * 24 * 7 * scale;
                break;
        }
        return tokenLifeTimeInSeconds;
    }
}

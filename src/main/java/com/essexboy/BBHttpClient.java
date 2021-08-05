package com.essexboy;

import lombok.Setter;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustAllStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;

import javax.net.ssl.SSLContext;
import java.util.List;

import static java.util.Arrays.stream;

@Setter
public class BBHttpClient {

    protected int id;
    private int timeout = 10;
    private boolean verbose = false;
    private CloseableHttpClient closeableHttpClient = null;

    protected CloseableHttpClient getHttpClient() throws Exception {

        if (closeableHttpClient == null) {

            SSLContext sslContext = SSLContexts.custom()
                    .loadTrustMaterial(new TrustAllStrategy())
                    .build();

            RequestConfig config = RequestConfig.custom()
                    .setConnectTimeout(timeout * 1000)
                    .setConnectionRequestTimeout(timeout * 1000)
                    .setSocketTimeout(timeout * 1000).build();

            closeableHttpClient = HttpClients.custom()
                    .setSSLHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER)
                    .setSSLContext(sslContext)
                    .setDefaultRequestConfig(config)
                    .build();
        }

        return closeableHttpClient;
    }

    public synchronized HttpResponse execute(HttpRequestBase request) throws Exception {
        HttpResponse response = getHttpClient().execute(request);
        if (verbose) System.out.println(request + " returned " + response.getStatusLine().getStatusCode());
        return response;
    }

    public void log(HttpRequestBase request, List<NameValuePair> params) {
        if (verbose) {
            System.out.println("\n" + request);
            System.out.println("headers [");
            stream(request.getAllHeaders()).forEach(e -> {
                System.out.println("\t" + e.getName() + " : " + e.getValue());
            });
            System.out.println("]");
            if (params != null) {
                System.out.println("body params [");
                params.stream().forEach(e -> System.out.println("\t" + e.getName() + " : " + e.getValue()));
                System.out.println("]");
            }
        }
    }

    protected void log(String message) {
        if (verbose) System.out.println(message);
    }
}

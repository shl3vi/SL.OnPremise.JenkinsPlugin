package io.sealights.plugins.sealightsjenkins.services;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import static io.sealights.plugins.sealightsjenkins.utils.StringUtils.isNullOrEmpty;

/**
 * Http client
 */
public class ApacheHttpClient {

    private final String AUTHORIZATION_HEADER = "Authorization";
    private final String BEARER = "Bearer ";

    public HttpResponse getJson(String url, String proxy, String token) throws IOException {
        HttpGet httpGet = new HttpGet(url);
        return get(proxy, token, httpGet);
    }

    private HttpResponse get(String proxy, String token, HttpGet httpGet) throws IOException {
        CloseableHttpClient httpClient = createHttpClient(proxy);
        if (!isNullOrEmpty(token)){
            httpGet.setHeader(AUTHORIZATION_HEADER, BEARER + token);
        }

        trySetTimeout(httpGet);

        CloseableHttpResponse response = httpClient.execute(httpGet);
        return toHttpResponse(response);
    }

    private void trySetTimeout(HttpGet httpGet) {

        Integer connectTimeout = Integer.getInteger("sl.httpClient.timeout");
        if (connectTimeout == null)
            return;
        int CONNECTION_TIMEOUT_MS = connectTimeout * 1000; // Timeout in millis.
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(CONNECTION_TIMEOUT_MS)
                .setConnectTimeout(CONNECTION_TIMEOUT_MS)
                .setSocketTimeout(CONNECTION_TIMEOUT_MS)
                .build();

        httpGet.setConfig(requestConfig);
    }

    private HttpResponse toHttpResponse(CloseableHttpResponse response) throws IOException {
        HttpResponse httpResponse = new HttpResponse();
        httpResponse.setStatusCode(response.getStatusLine().getStatusCode());

        HttpEntity responseHttpEntity = response.getEntity();
        if (responseHttpEntity != null){
            httpResponse.setResponseStream(responseHttpEntity.getContent());
        }
        return httpResponse;
    }

    private CloseableHttpClient createHttpClient(String proxy) throws MalformedURLException {
        CloseableHttpClient httpClient;

        if (isNullOrEmpty(proxy)) {
            httpClient = HttpClients.createDefault();
        } else {
            URL proxyUrl = new URL(proxy);
            proxy = proxyUrl.toString();
            if (proxyUrl.getPort() == -1) {
                proxy += ":80";
            }
            DefaultProxyRoutePlanner proxyRoutePlanner = createProxyRoutePlanner(proxy);
            httpClient = HttpClients.custom().setRoutePlanner(proxyRoutePlanner).build();
        }

        return httpClient;
    }

    private DefaultProxyRoutePlanner createProxyRoutePlanner(String proxy) throws MalformedURLException {
        URL proxyUrl = new URL(proxy);
        HttpHost proxyHost = new HttpHost(proxyUrl.getHost(), proxyUrl.getPort(), proxyUrl.getProtocol());
        DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxyHost);
        return routePlanner;
    }

}

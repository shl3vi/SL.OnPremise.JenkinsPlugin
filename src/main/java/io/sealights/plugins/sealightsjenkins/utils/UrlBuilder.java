package io.sealights.plugins.sealightsjenkins.utils;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class UrlBuilder {

    private String host = "";
    private String path = "";
    private Map<String, String> paramsMap = new HashMap<>();

    public UrlBuilder withHost(String host) throws MalformedURLException {
        this.host = resolveUrl(host);
        if (this.host.endsWith("/")){
            this.host = this.host.substring(0, this.host.length() - 1);
        }
        return this;
    }

    public UrlBuilder withPath(String... pathParams){
        for (int i=0; i<pathParams.length -1; i++){
            this.path += encodeValue(pathParams[i]) + "/";
        }
        this.path += encodeValue(pathParams[pathParams.length -1]);

        return this;
    }

    public UrlBuilder withQueryParam(String key, String value){
        paramsMap.put(key, value);
        return this;
    }

    public URL toUrl() throws MalformedURLException {
        return new URL(this.toString());
    }

    public String toString(){
        String finalUrl = host + "/" + path;
        if (paramsMap.isEmpty()){
            return finalUrl;
        }
        return finalUrl + "/" + buildQueryParams();
    }

    private String resolveUrl(String urlString) throws MalformedURLException {
        urlString = tryGetUrl(urlString).toString();
        URL url = new URL(urlString);
        return url.toString();
    }

    private URL tryGetUrl(String url) throws MalformedURLException {
        if (!(url.startsWith("http://") || url.startsWith("https://"))) {
            url = "http://" + url;
        }
        URL u = new URL(url);
        return u;
    }

    private String buildQueryParams(){
        StringBuilder queryString = new StringBuilder();

        for (String key: paramsMap.keySet()){
            String value = paramsMap.get(key);
            addQueryStringValue(queryString, key, encodeValue(value));
        }

        String qs = queryString.toString();
        if ("".equals(qs))
            return "";

        qs = "?" + qs;
        qs = qs.substring(0, qs.length() - 1); //Remove the last &.
        return qs;
    }

    private static void addQueryStringValue(StringBuilder queryString, String key, String value) {
        if (!(StringUtils.isNullOrEmpty(value) || StringUtils.isNullOrEmpty(key))) {
            queryString.append(key);
            queryString.append("=");
            queryString.append(value);
            queryString.append("&");
        }
    }

    private static String encodeValue(String value) {
        if (!StringUtils.isNullOrEmpty(value)) {
            try {
                return URLEncoder.encode(value, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException("Failed to encode value. Error:" + e.getMessage());
            }
        }
        return null;
    }
}


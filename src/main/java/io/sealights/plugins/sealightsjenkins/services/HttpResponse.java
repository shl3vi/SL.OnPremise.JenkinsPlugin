package io.sealights.plugins.sealightsjenkins.services;


import java.io.InputStream;

public class HttpResponse {

    private int statusCode;
    private InputStream responseStream;

    public int getStatusCode() {
        return statusCode;
    }
    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
    public boolean isStatusCodeOk(){
        return (statusCode >= 200) && (statusCode < 400);
    }
    public InputStream getResponseStream() {
        return responseStream;
    }
    public void setResponseStream(InputStream responseStream) {
        this.responseStream = responseStream;
    }

    @Override
    public String toString() {
        return "HttpResponse {statusCode:" + statusCode + ", responseStream:"
                + responseStream + "}";
    }
}

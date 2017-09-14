package com.phoenixroberts.popcorn.networking;

import com.phoenixroberts.popcorn.threading.DataSync;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by rzmudzinski on 9/9/17.
 */

public class DataServiceFetch implements DataSync.IDataSyncAction
{
    private String m_UrlString = null;
    private String m_PayloadData = null;
    private boolean m_IsPostRequest = false;
    private HashMap<String,String> m_Headers;
    private IFetchResponseHandler m_FetchResponseHandler;
    private OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build();


    public DataServiceFetch(String urlString) {
        super();
        m_UrlString=urlString;
    }
    public DataServiceFetch(String urlString, HashMap<String,String> headers) {
        super();
        m_UrlString=urlString;
        m_Headers=headers;
    }
    public DataServiceFetch(String urlString, HashMap<String,String> headers, String payloadData) {
        super();
        m_UrlString=urlString;
        m_Headers=headers;
        m_PayloadData=payloadData;
    }
    public DataServiceFetch(String urlString, HashMap<String,String> headers, String payloadData, boolean isPostRequest) {
        super();
        m_UrlString=urlString;
        m_Headers=headers;
        m_PayloadData=payloadData;
        m_IsPostRequest=isPostRequest;
    }

    public IFetchResponseHandler getResponseHandler() {
        return m_FetchResponseHandler;
    }
    public void setResponseHandler(IFetchResponseHandler fetchResponseHandler) {
        m_FetchResponseHandler=fetchResponseHandler;
    }


    @Override
    public void execute() {
        RESTResponse response = new RESTResponse();
        try {
            //Execute a login request...
            response.setResponse(m_IsPostRequest==true?executePost(m_UrlString):executeGet(m_UrlString));
        } catch (Exception x) {
            response.setException(x);
        }
        if(m_FetchResponseHandler!=null) {
            m_FetchResponseHandler.onResponse(response);
        }
    }

    @Override
    public void cancel() {

    }

    private Response executeGet(String url) throws IOException {

        Request.Builder requestBuilder = new Request.Builder().get();
        requestBuilder.url(url);
//            if(m_PayloadData != null) {
//                RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), m_PayloadData);
//                requestBuilder.post(body);
//            }
        if(m_Headers!=null) {
            for(String key : m_Headers.keySet()) {
                String keyValue = m_Headers.get(key);
                requestBuilder.addHeader(key,keyValue);
            }
        }
        Request request = requestBuilder.build();
        Response response = client.newCall(request).execute();
        return response;
    }

    private Response executePost(String url) throws IOException {

        Request.Builder requestBuilder = new Request.Builder();
        requestBuilder.url(url);
        if(m_PayloadData != null) {
            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), m_PayloadData);
            requestBuilder.post(body);
        }
        if(m_Headers!=null) {
            for(String key : m_Headers.keySet()) {
                String keyValue = m_Headers.get(key);
                requestBuilder.addHeader(key,keyValue);
            }
        }
        Request request = requestBuilder.build();
        Response response = client.newCall(request).execute();
        return response;
    }
}

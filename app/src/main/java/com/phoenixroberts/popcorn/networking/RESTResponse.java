package com.phoenixroberts.popcorn.networking;

import okhttp3.Response;

/**
 * Created by rzmudzinski on 9/9/17.
 */

public class RESTResponse implements IRESTResponse {
    private Response m_Response;
    private Exception m_Exception;

    public RESTResponse() {

    }
    public RESTResponse(Response response) {
        m_Response=response;
    }
    public RESTResponse(Response response, Exception x) {
        m_Response=response;
        m_Exception=x;
    }
    public Response getResponse() { return m_Response; }
    public void setResponse(Response response) { m_Response=response; }
    public Exception getExeception() { return m_Exception; }
    public void setException(Exception x) { m_Exception=x; }
}

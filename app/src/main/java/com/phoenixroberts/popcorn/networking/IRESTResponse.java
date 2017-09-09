package com.phoenixroberts.popcorn.networking;

import okhttp3.Response;

/**
 * Created by rzmudzinski on 9/9/17.
 */

public interface IRESTResponse {
    Response getResponse();
    void setResponse(Response response);
    Exception getExeception();
    void setException(Exception x);
}

package com.phoenixroberts.popcorn.data;

/**
 * Created by robz on 9/6/17.
 */

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.phoenixroberts.popcorn.AppMain;
import com.phoenixroberts.popcorn.DataServiceBroadcastReceiver;
import com.phoenixroberts.popcorn.DataSync;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by rzmudzinski on 8/27/17.
 */


//To convert your object to JSON with Jackson:
//https://stackoverflow.com/questions/15786129/converting-java-objects-to-json-with-jackson

public class DataService {
    private static DataService m_DataService = new DataService();
    private String m_BasePath = "https://api.themoviedb.org/3/";
    private String m_APIToken = "";
    private List<DTO.MoviesListItem> m_MoviesList = new ArrayList<DTO.MoviesListItem>();
    //private HashMap<Integer,MovieData> m_MovieDataCache = new HashMap<Integer,MovieData>();

    private DataService() {

    }
    public static DataService getInstance() {
        return m_DataService;
    }

    void broadcastDataServiceEvent(DataServiceBroadcastReceiver.DataServicesEventType dataServicesEventType, HashMap<String,String> extras) {
        Intent i = new Intent(DataServiceBroadcastReceiver.IntentFilter);
        i.putExtra(dataServicesEventType.getClass().getName(),dataServicesEventType.toString());
        if(extras!=null) {
            for (String key : extras.keySet()) {
                i.putExtra(key, extras.get(key));
            }
        }
        AppMain.getAppContext().sendBroadcast(i);
    }

    public List<DTO.MoviesListItem> getMoviesData() {
        return m_MoviesList;
    }

    public DTO.MoviesListItem getMovieData(Integer id) {
        //324852
        DTO.MoviesListItem movie = null;
        try {
            if (m_MoviesList != null) {
                movie = m_MoviesList.stream()       //From
                        .filter(m -> m.getId() == id)   //Where
                        .findFirst().orElse(null);  //Select
            }
        }
        catch(Exception x) {
            Log.d(getClass().toString(),x.getMessage());
        }
        if (movie != null) {

        }
        return movie;
    }
    public UUID fetchMovieData(Integer id) {
        //Currently unclear if all movie data can be obtained from the list of movies
        //fetched or if a subesquent RESTful call is needed to acquire all detail
        //This function serves as a placeholder until this determination can be made
        //"ItemFetchSuccess" is always broadcast and the actual code to perfrom an
        //individual movie fetch is below commented.
        HashMap<String, String> extras = new HashMap<String, String>();
        extras.put(DataServiceBroadcastReceiver.DataServicesEventExtra.MovieId.toString(), id.toString());
        broadcastDataServiceEvent(DataServiceBroadcastReceiver.DataServicesEventType.ItemFetchSuccess, extras);
        return UUID.randomUUID();

//        UUID uuid = UUID.randomUUID();
//        try {
//            //https://api.themoviedb.org/3/movie/374720?api_key=437c0161cd02c1361b4f6d2446c3e376
//            //String servicePath = "movie/".concat(id.toString()).concat("?api_key=437c0161cd02c1361b4f6d2446c3e376");
//            String servicePath = "movie/" + id.toString() + "?api_key=437c0161cd02c1361b4f6d2446c3e376";
//            String payloadData = null; //new Gson().toJson(new LoginDto(userId,userPwd));
//            final String taskName = "Fetch Movie";
//            DataServiceFetch dataSyncAction = new DataServiceFetch(m_BasePath+servicePath,
//                    null, payloadData, false);
//            DataSync.DataSyncTask dataSyncTask = new DataSync.DataSyncTask(taskName,dataSyncAction);
//            dataSyncAction.setResponseHandler(new IFetchResponseHandler() {
//                @Override
//                public void onResponse(IRESTResponse response) {
//                    Log.d(getClass().toString(), "Executing Response Handler for " + taskName);
//                    //processMovieFetchResponse(response);
//                }
//            });
//            Log.d(getClass().toString(), "Executing Login");
//            dataSyncTask.execute();
//        }
//        catch (Exception x) {
//            Log.e(this.getClass().toString(), x.getMessage());
//        }
//        return uuid;
    }

    public UUID fetchMoviesData() {
        UUID uuid = UUID.randomUUID();
        try {
            //https://api.themoviedb.org/3/discover/movie?language=en-US&sort_by=popularity.desc&include_adult=false&include_video=false&page=1&api_key=437c0161cd02c1361b4f6d2446c3e376&
            //discover/movie?api_key=437c0161cd02c1361b4f6d2446c3e376&language=en-US&sort_by=popularity.desc&include_adult=false&include_video=false&page=1
            String servicePath = "discover/movie?api_key=437c0161cd02c1361b4f6d2446c3e376&language=en-US&sort_by=popularity.desc&include_adult=false&include_video=false&page=1";
            String payloadData = null; //new Gson().toJson(new LoginDto(userId,userPwd));
            final String taskName = "Fetch Movies";
            DataServiceFetch dataSyncAction = new DataServiceFetch(m_BasePath+servicePath,
                    null, payloadData, false);
            DataSync.DataSyncTask dataSyncTask = new DataSync.DataSyncTask(taskName,dataSyncAction);
            dataSyncAction.setResponseHandler(new IFetchResponseHandler() {
                @Override
                public void onResponse(IRESTResponse response) {
                    Log.d(getClass().toString(), "Executing Response Handler for " + taskName);
                    processMoviesListFetchResponse(response);
                }
            });
            Log.d(getClass().toString(), "Executing Login");
            dataSyncTask.execute();
        }
        catch (Exception x) {
            Log.e(this.getClass().toString(), x.getMessage());
        }
        return uuid;
    }

    void processMoviesListFetchResponse(IRESTResponse restResponse) {
        boolean bProcessingCompleted = false;
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);

            Response response = restResponse.getResponse();
            if(response!=null) {
                String jsonData = response.body().string();
                DTO.MoviesListResultPage fetchResult = mapper.readValue(jsonData, DTO.MoviesListResultPage.class);
                if (fetchResult != null) {
                    int pageCount = fetchResult.getTotalPages();
                    m_MoviesList = fetchResult.getResults();
                    if (m_MoviesList != null) {
                        for (DTO.MoviesListItem result : m_MoviesList) {
                            String movieTitle = result.getTitle();
                            Log.d("Movie Title", movieTitle);
                        }
                        broadcastDataServiceEvent(DataServiceBroadcastReceiver.DataServicesEventType.ListFetchSuccess, null);
                    }
                }
            }
            else {
                broadcastDataServiceEvent(DataServiceBroadcastReceiver.DataServicesEventType.ListFetchFail, null);
            }
            bProcessingCompleted = true;
        }
        catch(IOException x) {
            Log.e(getClass().toString(),x.getMessage());
        }
        if(bProcessingCompleted==false) {
            broadcastDataServiceEvent(DataServiceBroadcastReceiver.DataServicesEventType.ListFetchFail, null);
        }
    }

    interface IRESTResponse {
        Response getResponse();
        void setResponse(Response response);
        Exception getExeception();
        void setException(Exception x);
    }
    class RESTResponse implements IRESTResponse {
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
    interface IFetchResponseHandler {
        void onResponse(IRESTResponse response);
    }
    class DataServiceFetch implements DataSync.IDataSyncAction
    {
        String m_UrlString = null;
        String m_PayloadData = null;
        boolean m_IsPostRequest = false;
        HashMap<String,String> m_Headers;
        IFetchResponseHandler m_FetchResponseHandler;
        OkHttpClient client = new OkHttpClient.Builder()
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

    class AsyncDataTask extends AsyncTask<Void, Integer, Void> {  //Params, Progress, Result
        OkHttpClient client = new OkHttpClient();
        String m_UrlString = null;
        String m_PayloadData = null;
        boolean m_IsPostRequest = false;
        HashMap<String,String> m_Headers;

        public AsyncDataTask(String urlString) {
            super();
            m_UrlString=urlString;
        }
        public AsyncDataTask(String urlString, HashMap<String,String> headers) {
            super();
            m_UrlString=urlString;
            m_Headers=headers;
        }
        public AsyncDataTask(String urlString, HashMap<String,String> headers, String payloadData) {
            super();
            m_UrlString=urlString;
            m_Headers=headers;
            m_PayloadData=payloadData;
        }
        public AsyncDataTask(String urlString, HashMap<String,String> headers, String payloadData, boolean isPostRequest) {
            super();
            m_UrlString=urlString;
            m_Headers=headers;
            m_PayloadData=payloadData;
            m_IsPostRequest=isPostRequest;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                if(m_IsPostRequest) {
                    executePost(m_UrlString);
                }
                else {
                    executeGet(m_UrlString);
                }
            }
            catch(Exception x) {
                Log.d("Exception", x.getMessage());
            }
            return null;
        }
        private void executeGet(String url) throws IOException {
            Request.Builder requestBuilder = new Request.Builder();
            requestBuilder.url(url);
            Request request = requestBuilder.build();
            Response response = client.newCall(request).execute();
            if(response.isSuccessful()) {
                //DataService.this
            }
        }
        private String executePost(String url) throws IOException {

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

            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);

            String jsonData = response.body().string();

            return response.body().string();
        }
    }
}


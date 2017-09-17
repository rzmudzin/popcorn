package com.phoenixroberts.popcorn.data;

/**
 * Created by robz on 9/6/17.
 */

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.phoenixroberts.popcorn.AppMain;
import com.phoenixroberts.popcorn.AppSettings;
import com.phoenixroberts.popcorn.networking.DataServiceFetch;
import com.phoenixroberts.popcorn.networking.IFetchResponseHandler;
import com.phoenixroberts.popcorn.networking.IRESTResponse;
import com.phoenixroberts.popcorn.threading.DataSync;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import okhttp3.Response;

/**
 * Created by rzmudzinski on 8/27/17.
 */


//To convert your object to JSON with Jackson:
//https://stackoverflow.com/questions/15786129/converting-java-objects-to-json-with-jackson

public class DataService {
    private static DataService m_DataService = new DataService();
    private final String m_MediaServiceBasePath = "http://image.tmdb.org/t/p/";
    private final String m_DataServiceBasePath = "https://api.themoviedb.org/3/";
    private final String m_MovieDiscoveryService = "discover/movie?api_key=";
    private final String m_MovieListService = "movie/";
    private final String m_MovieDataService = "movie/";
    private String m_APIKey;
    private List<DTO.MoviesListItem> m_MoviesList = new ArrayList<DTO.MoviesListItem>();
    private String m_DiscoverySortOrder;
    private String m_ListSortOrder;

    public static class PosterSize {  //2:3
        public static final String W92 = "w92";
        public static final String W154 = "w154";
        public static final String W185 = "w185";   //185x277
        public static final String W342 = "w342";   //500x750
        public static final String W500 = "w500";
        public static final String W780 = "w780";
        public static final String Original = "original";
    }

    public static class SortOrder {
        public static class SortOrderType {
            String m_Name;
            String m_Value;
            public SortOrderType(String name, String value) {
                m_Name=name;
                m_Value=value;
            }
            public String getName() { return m_Name; }
            public String getValue() { return m_Value; }
        }
        protected static List<SortOrderType> values(Field[] fields) {
            List<SortOrderType> sortOrderTypes = new ArrayList<SortOrderType>();
            for (Field f : fields) {
                if (Modifier.isStatic(f.getModifiers())) {
                    try {
                        Object fieldValue = f.get(null);
                        sortOrderTypes.add(new SortOrderType(f.getName().replace('_', ' '),fieldValue!=null?fieldValue.toString():""));
                    }
                    catch(Exception x) {
                        Log.e("SortOrder", x.getMessage());
                    }
                }
            }
            return sortOrderTypes;
        }
    }
    public static class MovieListSortOrder extends SortOrder{
        public static final String Popular = "popular";
        public static final String Highest_Rated = "top_rated";
        public static boolean isValid(String sortOrder) {
            return values().stream()
                    .filter(v -> {
                        return v.getValue().equals(sortOrder);
                    })
                    .findFirst().orElse(null)!=null;
        }
        public static List<SortOrderType> values() {
            return values(MovieListSortOrder.class.getDeclaredFields());
        }
    }
    public static class DiscoverySortOrder extends SortOrder{
//      popularity.asc, popularity.desc, release_date.asc, release_date.desc, revenue.asc, revenue.desc,
//      primary_release_date.asc, primary_release_date.desc, original_title.asc, original_title.desc,
//      vote_average.asc, vote_average.desc, vote_count.asc, vote_count.desc
        public static final String Popular = "popularity" + Direction.Descending;
        public static final String Highest_Rated = "vote_average" + Direction.Descending;

//        public static final String Recent_Releases = "release_date" + Direction.Descending;
//        public static final String Least_Popular = "popularity" + Direction.Ascending;
//        public static final String Lowest_Rated= "vote_average" + Direction.Ascending;
//        public static final String Vintage_Movies = "release_date" + Direction.Ascending;

        public static boolean isValid(String sortOrder) {
            return values().stream()
                    .filter(v -> {
                        return v.getValue().equals(sortOrder);
                    })
                    .findFirst().orElse(null)!=null;
        }
        public static List<SortOrderType> values() {
            return values(DiscoverySortOrder.class.getDeclaredFields());
        }

        public static class Direction {
            public static final String Ascending = ".asc";
            public static final String Descending = ".desc";
        }
    }

    public void setDiscoverySortOrder(String sortOrder) {
        AppSettings.set(AppSettings.Settings.Discovery_Sort_Order, sortOrder);
        m_DiscoverySortOrder = sortOrder;
    }

    public void setListSortOrder(String sortOrder) {
        AppSettings.set(AppSettings.Settings.List_Sort_Order, sortOrder);
        m_ListSortOrder = sortOrder;
    }

    private DataService() {
        m_APIKey = AppSettings.get(AppSettings.Settings.APKI_Key);
        String defaultSortOrder = AppSettings.get(AppSettings.Settings.Discovery_Sort_Order);
        m_DiscoverySortOrder = defaultSortOrder.equals("")==false?defaultSortOrder: DiscoverySortOrder.Popular;

        setListSortOrder(MovieListSortOrder.Popular);
        defaultSortOrder = AppSettings.get(AppSettings.Settings.List_Sort_Order);
        m_ListSortOrder = defaultSortOrder.equals("")==false?defaultSortOrder: MovieListSortOrder.Popular;
    }
    public static DataService getInstance() {
        return m_DataService;
    }

    private void broadcastDataServiceEvent(DataServiceBroadcastReceiver.DataServicesEventType dataServicesEventType, HashMap<String,String> extras) {
        Context context = AppMain.getAppContext();
        if(context!=null) {
            Intent i = new Intent(DataServiceBroadcastReceiver.IntentFilter);
            i.putExtra(dataServicesEventType.getClass().getName(), dataServicesEventType.toString());
            if (extras != null) {
                for (String key : extras.keySet()) {
                    i.putExtra(key, extras.get(key));
                }
            }
            context.sendBroadcast(i);
        }
    }
    public String getAPIKey() {
        return m_APIKey;
    }
    public void setAPIKey(String apiKey) {
        m_APIKey=apiKey;
    }
    public String getMovieGridPosterPath(Integer movieId) {
        return getPosterPath(movieId, PosterSize.W185);
    }
    public String getMovieDetailPosterPath(Integer movieId) {
        return getPosterPath(movieId, PosterSize.W500);
    }
    public String getPosterPath(Integer movieId, String posterSize) {
        DTO.MoviesListItem movie = getMovieData(movieId);
        String posterPath = movie.getPosterPath();
        return m_MediaServiceBasePath + posterSize + posterPath;
    }

    public List<DTO.MoviesListItem> getMoviesData() {
        return m_MoviesList;
    }

    public DTO.MoviesListItem getMovieData(Integer id) {
        DTO.MoviesListItem movie = null;
        try {
            if (m_MoviesList != null) {
                movie = m_MoviesList.stream()                              //From
                        .filter(m -> {                                      //Where
                            return m.getId().equals(id);
                        })
                        .findFirst().orElse(null);                          //Select
            }
        }
        catch(Exception x) {
            Log.e(getClass().toString(),x.getMessage());
        }
        return movie;
    }

    public UUID fetchListDiscoveryData() {
        return fetchMoviesListData(null);
    }
    public UUID fetchMoviesListData(String sortOrder) {
        UUID uuid = UUID.randomUUID();
        try {
            String page = "&page=1";
            String apiKey = "?api_key=" + m_APIKey;

            String queryString = apiKey+page;
            String servicePath = m_MovieListService + m_ListSortOrder + queryString;
            final String taskName = "Fetch Movies";
            DataServiceFetch dataSyncAction = new DataServiceFetch(m_DataServiceBasePath+ servicePath,
                    null,       //Headers
                    null,       //Json payload data
                    false);
            DataSync.DataSyncTask dataSyncTask = new DataSync.DataSyncTask(taskName,dataSyncAction);
            dataSyncAction.setResponseHandler(new IFetchResponseHandler() {
                @Override
                public void onResponse(IRESTResponse response) {
                    Log.d(getClass().toString(), "Executing Response Handler for " + taskName);
                    processMoviesListFetchResponse(response);
                }
            });
            Log.d(getClass().toString(), "Executing Movies List Query");
            dataSyncTask.execute();
        }
        catch (Exception x) {
            Log.e(this.getClass().toString(), x.getMessage());
        }
        return uuid;
    }
    //TODO: determine if the discovery endpoint will be used in future iterations of the project - remove if not needed
    public UUID fetchMoviesDiscoveryData() {
        return fetchMoviesDiscoveryData(null);
    }
    public UUID fetchMoviesDiscoveryData(String sortOrder) {
        UUID uuid = UUID.randomUUID();
        try {
            String language = "&language=en-US";
            sortOrder = "&sort_by="+(sortOrder!=null?sortOrder: m_DiscoverySortOrder);
            String filter = "&include_adult=false&include_video=false";
            String page = "&page=1";

            String queryString = language+sortOrder+filter+page;
            String servicePath = m_MovieDiscoveryService + m_APIKey + queryString;
            final String taskName = "Discover Movies";
            DataServiceFetch dataSyncAction = new DataServiceFetch(m_DataServiceBasePath+ servicePath,
                    null,       //Headers
                    null,       //Json payload data
                    false);
            DataSync.DataSyncTask dataSyncTask = new DataSync.DataSyncTask(taskName,dataSyncAction);
            dataSyncAction.setResponseHandler(new IFetchResponseHandler() {
                @Override
                public void onResponse(IRESTResponse response) {
                    Log.d(getClass().toString(), "Executing Response Handler for " + taskName);
                    processMoviesListFetchResponse(response);
                }
            });
            Log.d(getClass().toString(), "Executing Discover Query");
            dataSyncTask.execute();
        }
        catch (Exception x) {
            Log.e(this.getClass().toString(), x.getMessage());
        }
        return uuid;
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
//            DataServiceFetch dataSyncAction = new DataServiceFetch(m_DataServiceBasePath+servicePath,
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
    private void processMoviesListFetchResponse(IRESTResponse restResponse) {
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
}


package com.phoenixroberts.popcorn.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.phoenixroberts.popcorn.AppMain;
import com.phoenixroberts.popcorn.DataServiceBroadcastReceiver;
import com.phoenixroberts.popcorn.IDataServiceListener;
import com.phoenixroberts.popcorn.R;
import com.phoenixroberts.popcorn.data.DTO;
import com.phoenixroberts.popcorn.data.DataService;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.URL;

/**
 * Created by robz on 9/7/17.
 */

public class MovieDetailFragment extends Fragment implements IDataServiceListener, View.OnClickListener {

    private Integer m_MovieId;
    private View m_RootView;

    public MovieDetailFragment() {
        // Required empty public constructor
    }

    public Integer getMovieId() {
        return m_MovieId;
    }

    public void setMovieId(Integer movieId) {
        m_MovieId = movieId;
    }

    private void processClick() {
        DTO.MoviesListItem movieData = DataService.getInstance().getMovieData(m_MovieId);
        Toast.makeText(this.getActivity(), "\n  " + movieData.getPosterPath() + "  \n", Toast.LENGTH_SHORT).show();
        String sUrlPath = "http://image.tmdb.org/t/p/w185" + movieData.getPosterPath();
        ImageView imageView = (ImageView)m_RootView.findViewById(R.id.movieImage);
        loadImage(imageView, sUrlPath);
    }

    private void loadImage(ImageView imageView, String sUrlPath) {
        try {
            URL url = new URL(sUrlPath);
            Uri uri = Uri.parse(sUrlPath);
            Picasso.with(getActivity())
                    .load(uri)
                    //.resize(imageView.getWidth(),imageView.getHeight())
                    .into(imageView);
        }
        catch(IOException x) {
            Log.e(getClass().toString(),x.getMessage());
        }
    }

    public void onDataServiceResult(DataServiceBroadcastReceiver.DataServicesEventType dataServicesEventType, Intent i) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        DataServiceBroadcastReceiver.getInstance().removeListener(this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState!=null) {
            m_MovieId = savedInstanceState.getInt(AppMain.BundleExtraType.MovieId, m_MovieId);
        }
        DataServiceBroadcastReceiver.getInstance().addListener(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(AppMain.BundleExtraType.MovieId, m_MovieId);
    }

    @Override
    public void onClick(View view) {
        processClick();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        m_RootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        DTO.MoviesListItem movieData = DataService.getInstance().getMovieData(m_MovieId);
        String sUrlPath = "http://image.tmdb.org/t/p/w185" + movieData.getPosterPath();
        ImageView imageView = (ImageView)m_RootView.findViewById(R.id.movieImage);
        loadImage(imageView, sUrlPath);

//        Button clickMe = (Button)m_RootView.findViewById(R.id.clickMe);
//        if(clickMe!=null) {
//            Log.d(getClass().toString(),"Adding Click Listener");
//            clickMe.setOnClickListener(this);
//        }

        return m_RootView;
    }


}

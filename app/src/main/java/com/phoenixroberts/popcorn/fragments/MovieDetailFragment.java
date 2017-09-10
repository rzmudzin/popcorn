package com.phoenixroberts.popcorn.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.phoenixroberts.popcorn.AppMain;
import com.phoenixroberts.popcorn.activities.MainActivity;
import com.phoenixroberts.popcorn.data.DataServiceBroadcastReceiver;
import com.phoenixroberts.popcorn.data.DTO;
import com.phoenixroberts.popcorn.threading.IDataServiceListener;
import com.phoenixroberts.popcorn.R;
import com.phoenixroberts.popcorn.data.DataService;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    private void loadImage(ImageView imageView, String sUrlPath) {
        try {
            Uri uri = Uri.parse(sUrlPath);
            Picasso.with(getActivity())
                    .load(uri)
                    .placeholder(R.drawable.popcorn)
                    .into(imageView);
        }
        catch(Exception x) {
            Log.e(getClass().toString(),x.getMessage());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                getActivity().onBackPressed();
            }
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
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

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        DTO.MoviesListItem movie = DataService.getInstance().getMovieData(m_MovieId);
        m_RootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        TextView title = (TextView)m_RootView.findViewById(R.id.title);
        title.setText(movie.getTitle());
        TextView description = (TextView)m_RootView.findViewById(R.id.description);
        description.setText(movie.getOverview());
        TextView year = (TextView)m_RootView.findViewById(R.id.year);
        year.setText(movie.getReleaseDate());
        TextView userRating = (TextView)m_RootView.findViewById(R.id.user_rating);
        userRating.setText(movie.getVoteAverage().toString());
        ImageView imageView = (ImageView)m_RootView.findViewById(R.id.movieImage);
        loadImage(imageView, DataService.getInstance().getMovieDetailPosterPath(m_MovieId));
        return m_RootView;
    }

}

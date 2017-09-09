package com.phoenixroberts.popcorn.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;

import com.phoenixroberts.popcorn.DataServiceBroadcastReceiver;
import com.phoenixroberts.popcorn.threading.IDataServiceListener;
import com.phoenixroberts.popcorn.R;
import com.phoenixroberts.popcorn.adapters.MovieDataListViewAdapter;
import com.phoenixroberts.popcorn.data.DTO;
import com.phoenixroberts.popcorn.data.DataService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by robz on 9/7/17.
 */

public class MovieGridFragment extends Fragment implements IDataServiceListener {

    private MovieDataListViewAdapter m_ListViewAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_movie_grid, container, false);

        List<DTO.MoviesListItem> moviesData = DataService.getInstance().getMoviesData();
        m_ListViewAdapter = new MovieDataListViewAdapter(this.getActivity(),
                moviesData!=null?moviesData:new ArrayList<DTO.MoviesListItem>(), R.layout.movie_grid_item);

        // Get a reference to the ListView, and attach this adapter to it.
        GridView gridView = (GridView) v.findViewById(R.id.movies_grid);
        gridView.setAdapter(m_ListViewAdapter);
        return v;
    }

    void displayDetailFragment(Integer movieId) {
        MovieDetailFragment movieDetailFragment = new MovieDetailFragment();
        movieDetailFragment.setMovieId(movieId);
        this.getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, movieDetailFragment,"movie_detail")
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        DataServiceBroadcastReceiver.getInstance().removeListener(this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DataServiceBroadcastReceiver.getInstance().addListener(this);
    }

    public void onDataServiceResult(DataServiceBroadcastReceiver.DataServicesEventType dataServicesEventType, Intent i) {
        switch (dataServicesEventType) {
            case ItemFetchSuccess: {
                if(i!=null && i.hasExtra(DataServiceBroadcastReceiver.DataServicesEventExtra.MovieId.toString())) {
                    Integer movieId = Integer.parseInt(i.getStringExtra(DataServiceBroadcastReceiver.DataServicesEventExtra.MovieId.toString()));
//                    MovieData movieData = DataService.getInstance().getMovieData(movieId);
                    displayDetailFragment(movieId);
                }
                break;
            }
            case ItemFetchFail:
                Toast.makeText(getActivity(),"Unable to retreive requested movie data", Toast.LENGTH_SHORT).show();
                break;
            case ListFetchSuccess:
                List<DTO.MoviesListItem> moviesData = DataService.getInstance().getMoviesData();
                m_ListViewAdapter.getMoviesData().clear();
                m_ListViewAdapter.getMoviesData().addAll(moviesData);
                m_ListViewAdapter.notifyDataSetChanged();
                break;
            default:
                break;
        }
    }
}


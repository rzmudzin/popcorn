package com.phoenixroberts.popcorn.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.phoenixroberts.popcorn.activities.MainActivity;
import com.phoenixroberts.popcorn.data.DataServiceBroadcastReceiver;
import com.phoenixroberts.popcorn.dialogs.DialogService;
import com.phoenixroberts.popcorn.dialogs.Dialogs;
import com.phoenixroberts.popcorn.threading.IDataServiceListener;
import com.phoenixroberts.popcorn.R;
import com.phoenixroberts.popcorn.adapters.MovieDataListViewAdapter;
import com.phoenixroberts.popcorn.data.DTO;
import com.phoenixroberts.popcorn.data.DataService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by robz on 9/7/17.
 */

public class MovieGridFragment extends Fragment implements IDataServiceListener {

    private List<Integer> m_ToolbarFilter = new ArrayList<Integer>();
    private MovieDataListViewAdapter m_Adapter;
    private GridView m_GridView;

    public MovieGridFragment() {
        m_ToolbarFilter = new ArrayList<Integer>(Arrays.asList(new Integer [] {
                R.id.settingsMenuOption, R.id.sortOrderMenuOption, R.id.refreshMenuOption
        }));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View v = inflater.inflate(R.layout.fragment_movie_grid, container, false);

        List<DTO.MoviesListItem> moviesData = DataService.getInstance().getMoviesData();
        m_Adapter = new MovieDataListViewAdapter(this.getActivity(),
                moviesData!=null?moviesData:new ArrayList<DTO.MoviesListItem>(),
                R.layout.movie_grid_item);  //R.layout.fragment_movie_data_list

        // Get a reference to the ListView, and attach this adapter to it.
        m_GridView = (GridView) v.findViewById(R.id.movies_grid);
        m_GridView.setAdapter(m_Adapter);
        return v;
    }

    private void displayDetailFragment(Integer movieId) {
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
//        ((MainActivity)getActivity()).setToolbarFilter(m_ToolbarFilter);
        DataServiceBroadcastReceiver.getInstance().addListener(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main, menu);
        for(int i=0; i<menu.size(); ++i) {
            MenuItem menuItem = menu.getItem(i);
            if(m_ToolbarFilter.contains(menuItem.getItemId())) {
                menuItem.setVisible(true);
            }
            else {
                menuItem.setVisible(false);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                getActivity().onBackPressed();
            }
            case R.id.refreshMenuOption:
                if(TextUtils.isEmpty(DataService.getInstance().getAPIKey())) {
                    ((MainActivity)getActivity()).promptUserForAPIKey();
                }
                else {
                    ((MainActivity)getActivity()).LoadData();
                }
                break;
            case R.id.settingsMenuOption: {
                ((MainActivity) getActivity()).promptUserForAPIKey();
                break;
            }
            case R.id.sortOrderMenuOption: {
                if(TextUtils.isEmpty(DataService.getInstance().getAPIKey())) {
                    ((MainActivity)getActivity()).promptUserForAPIKey();
                }
                else {
                    List<Dialogs.ISelectionDialogItemData> options = new ArrayList<Dialogs.ISelectionDialogItemData>();
                    for (DataService.SortOrder.SortOrderType sortOrderType : DataService.SortOrder.values()) {
                        options.add(new Dialogs.SelectionDialogItemData(sortOrderType.getName(), (eventArgs) -> {
                            DataService.getInstance().setSortOrder(sortOrderType.getValue());
                            ((MainActivity) getActivity()).LoadData();
                        }));
                    }
                    DialogService.getInstance().DisplayChoiceSelectionDialog(new Dialogs.SelectionDialogData(getActivity(),
                            "Select Category", options));
                }
                break;
            }
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onDataServiceResult(DataServiceBroadcastReceiver.DataServicesEventType dataServicesEventType, Intent i) {
        try {
            switch (dataServicesEventType) {
                case ItemFetchSuccess: {
                    if (i != null && i.hasExtra(DataServiceBroadcastReceiver.DataServicesEventExtra.MovieId.toString())) {
                        Integer movieId = Integer.parseInt(i.getStringExtra(DataServiceBroadcastReceiver.DataServicesEventExtra.MovieId.toString()));
                        displayDetailFragment(movieId);
                    }
                    break;
                }
                case ItemFetchFail:
                    DialogService.getInstance().DisplayNotificationDialog(new Dialogs.DialogData(getActivity(),
                            "Movie data download failed",
                            "Currently unable to download movie info.\nAre you connected to the internet?",
                            "Ok", null));
                    break;
                case ListFetchFail:
                    DialogService.getInstance().DisplayNotificationDialog(new Dialogs.DialogData(getActivity(),
                            "Movie list download failed",
                            "Currently unable to download movies list.\nAre you connected to the internet?",
                            "Ok", null));
                    break;
                case ListFetchSuccess:
                    List<DTO.MoviesListItem> moviesData = DataService.getInstance().getMoviesData();
                    m_Adapter.getMoviesData().clear();
                    m_Adapter.getMoviesData().addAll(moviesData);
                    m_Adapter.notifyDataSetChanged();
                    m_GridView.smoothScrollToPosition(0);
                    break;
                default:
                    break;
            }
        }
        catch(Exception x){
            Log.e(getClass().toString(),x.getMessage());
        }
    }
}


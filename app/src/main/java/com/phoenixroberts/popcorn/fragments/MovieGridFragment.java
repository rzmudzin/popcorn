package com.phoenixroberts.popcorn.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;

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
    private MovieDataListViewAdapter m_ListViewAdapter;

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
                ((MainActivity)getActivity()).LoadData();
                break;
            case R.id.settingsMenuOption: {
//                DialogService.getInstance().DisplayNotificationDialog(new Dialogs.DialogData(getActivity(),
//                        "Simple Test Dialog", "A simple message to display", "Ok", null));

                DialogService.getInstance().DisplayTextInputDialog(new Dialogs.TextInputDialogData(getActivity(),"Test Text Input Dialog",
                        "Ok", "Cancel", "Dialog for testing text input",
                        (eventArgs) -> {
                            //On ok event handler
                            Dialogs.IDialogTextChangedEventData textInputEventArgs = (Dialogs.IDialogTextChangedEventData)eventArgs;
                            Toast.makeText(getActivity(),textInputEventArgs.getText(), Toast.LENGTH_SHORT).show();
                        },
                        null,       //On cancel
                        null));     //On text changed
                break;
            }
            case R.id.sortOrderMenuOption: {
                DialogService.getInstance().DisplayChoiceSelectionDialog(new Dialogs.SelectionDialogData(getActivity(),
                        "Select Sort Order",
                        new ArrayList<Dialogs.ISelectionDialogItemData>(Arrays.asList(new Dialogs.ISelectionDialogItemData [] {
                                new Dialogs.SelectionDialogItemData("Popular",(eventArgs)->{
                                    Toast.makeText(getActivity(), "\n  Popular  \n", Toast.LENGTH_SHORT).show();
                                    DataService.getInstance().setSortOrder(DataService.SortOrder.Popularity_Descending);
                                    DataService.getInstance().fetchMoviesData();
                                }),
                                new Dialogs.SelectionDialogItemData("Top Rated",(eventArgs)->{
                                    Toast.makeText(getActivity(), "\n  Top Rated  \n", Toast.LENGTH_SHORT).show();
                                    DataService.getInstance().setSortOrder(DataService.SortOrder.Rating_Descending);
                                    DataService.getInstance().fetchMoviesData();
                                })
                        }))));
                break;
            }
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
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


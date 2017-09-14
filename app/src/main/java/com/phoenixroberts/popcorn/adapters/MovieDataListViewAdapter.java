package com.phoenixroberts.popcorn.adapters;

/**
 * Created by robz on 9/6/17.
 */

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.phoenixroberts.popcorn.R;
import com.phoenixroberts.popcorn.data.DTO;
import com.phoenixroberts.popcorn.data.DataService;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by robz on 9/6/17.
 */

public class MovieDataListViewAdapter extends ArrayAdapter<DTO.MoviesListItem> implements View.OnClickListener {
    Context m_Context;
    List<DTO.MoviesListItem> m_MoviesData;
    int m_ItemLayoutId = R.layout.fragment_movie_data_list;

    public MovieDataListViewAdapter(Context c, List<DTO.MoviesListItem> moviesData) {
        this(c, moviesData, R.layout.fragment_movie_data_list);
    }
    public MovieDataListViewAdapter(Context c, List<DTO.MoviesListItem> moviesData, int itemLayoutId) {
        super(c,itemLayoutId,moviesData!=null?moviesData:new ArrayList<DTO.MoviesListItem>(Arrays.asList(new DTO.MoviesListItem [5])));
        m_ItemLayoutId=itemLayoutId;
        m_Context = c;
        m_MoviesData = moviesData;
    }

    public List<DTO.MoviesListItem> getMoviesData() {
        return m_MoviesData;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(m_ItemLayoutId==R.layout.fragment_movie_data_list) {
            return populateMovieListView(position, convertView, parent);
        }
        return populateMovieGrid(position, convertView, parent);
    }

    View populateMovieListView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = convertView;
        if(v==null) {
            v = LayoutInflater.from(m_Context).inflate(R.layout.fragment_movie_data_item, parent, false);
            v.setTag(new MovieDataItemViewHolder(
                    (TextView)v.findViewById(R.id.movieDataItemTitle),
                    (TextView)v.findViewById(R.id.movieDataItemId),
                    null
            ));
        }
        MovieDataItemViewHolder movieDataViewHolder = (MovieDataItemViewHolder)v.getTag();
        movieDataViewHolder.getMovieTitle().setText(m_MoviesData.get(position).getTitle());
        movieDataViewHolder.getMovieId().setText(Integer.toString(m_MoviesData.get(position).getId()));
        return v;
    }

    View populateMovieGrid(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = convertView;
        if(v==null) {
            v = LayoutInflater.from(m_Context).inflate(R.layout.movie_grid_item, parent, false);
            ImageView movieImage = (ImageView)v.findViewById(R.id.movie_image);
            movieImage.setOnClickListener(this);
            v.setTag(new MovieDataItemViewHolder(
                    (TextView)v.findViewById(R.id.movie_title),
                    (TextView)v.findViewById(R.id.movieDataItemId),
                    movieImage
            ));
        }
        MovieDataItemViewHolder movieDataViewHolder = (MovieDataItemViewHolder)v.getTag();
        movieDataViewHolder.getMovieTitle().setText(m_MoviesData.get(position).getTitle());
        movieDataViewHolder.getMovieImage().setTag(m_MoviesData.get(position).getId().toString());
        String sUrlPath = DataService.getInstance().getMovieGridPosterPath(m_MoviesData.get(position).getId());
        loadImage(movieDataViewHolder.getMovieImage(), sUrlPath);
        //Below need a better way of handling this... Picasso seems to get the images out of order if we skip invalid urls though
        //Same result as well when implementing a listener and overriding the image load error method
        if(sUrlPath.endsWith("null")) {
            Log.d(getClass().toString(),"Null Image Poster Path");
            movieDataViewHolder.getMovieTitle().setVisibility(View.VISIBLE);
        }
        else {
            movieDataViewHolder.getMovieTitle().setVisibility(View.GONE);
        }
        return v;
    }

    private void loadImage(ImageView imageView, String sUrlPath) {
        try {
            Uri uri = Uri.parse(sUrlPath);
            Picasso.with(m_Context)
                    .load(uri).placeholder(R.drawable.popcorn).into(imageView);
        }
        catch(IOException x) {
            Log.e(getClass().toString(),x.getMessage());
        }
    }

    @Override
    public void onClick(View view) {
        Object viewTag = view.getTag();
        if(viewTag!=null) {
            DataService.getInstance().fetchMovieData(Integer.parseInt(viewTag.toString()));
        }
    }


    class MovieDataItemViewHolder {
        private TextView m_MovieTitle;
        private TextView m_MovieId;
        private ImageView m_MovieImage;

        public MovieDataItemViewHolder(TextView movieTitle, TextView movieId, ImageView movieImage) {
            m_MovieTitle = movieTitle;
            m_MovieId = movieId;
            m_MovieImage=movieImage;
        }
        public TextView getMovieId() { return m_MovieId; }
        public void setMovieId(TextView movieId) { m_MovieId = movieId; }
        public TextView getMovieTitle() { return m_MovieTitle; }
        public void setMovieTitle(TextView movieTitle) { m_MovieTitle = movieTitle; }
        public ImageView getMovieImage() { return m_MovieImage; }
        public void setMovieImage(ImageView imageView) { m_MovieImage = imageView; }
    }
}


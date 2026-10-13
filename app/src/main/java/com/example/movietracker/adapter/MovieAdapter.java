package com.example.movietracker.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.movietracker.R;
import com.example.movietracker.model.Movie;

import java.util.List;
import java.util.Locale;

/** Adapter untuk ListView film. Dipakai oleh MovieListActivity dan FavoriteActivity. */
public class MovieAdapter extends BaseAdapter {

    private final Context context;
    private final LayoutInflater inflater;
    private List<Movie> movies;

    public MovieAdapter(Context context, List<Movie> movies) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.movies = movies;
    }

    /** Mengganti isi daftar lalu menyegarkan tampilan. */
    public void updateData(List<Movie> newMovies) {
        this.movies = newMovies;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return movies.size();
    }

    @Override
    public Movie getItem(int position) {
        return movies.get(position);
    }

    @Override
    public long getItemId(int position) {
        return movies.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_movie, parent, false);
            holder = new ViewHolder();
            holder.ivPoster = convertView.findViewById(R.id.ivPoster);
            holder.tvTitle = convertView.findViewById(R.id.tvTitle);
            holder.tvGenre = convertView.findViewById(R.id.tvGenre);
            holder.tvYear = convertView.findViewById(R.id.tvYear);
            holder.tvRating = convertView.findViewById(R.id.tvRating);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Movie movie = movies.get(position);

        holder.tvTitle.setText(movie.getTitle());
        holder.tvGenre.setText(movie.getGenre());
        holder.tvYear.setText(String.valueOf(movie.getYear()));

        if (movie.getRatingCount() > 0) {
            holder.tvRating.setText(String.format(Locale.getDefault(),
                    "★ %.1f (%d rating)", movie.getAverageRating(), movie.getRatingCount()));
        } else {
            holder.tvRating.setText("Belum ada rating");
        }

        // Poster diambil dari nama drawable yang tersimpan di kolom "poster"
        int resId = context.getResources()
                .getIdentifier(movie.getPoster(), "drawable", context.getPackageName());
        if (resId == 0) {
            resId = R.drawable.poster_action; // cadangan jika nama tidak ditemukan
        }
        holder.ivPoster.setImageResource(resId);

        return convertView;
    }

    private static class ViewHolder {
        ImageView ivPoster;
        TextView tvTitle;
        TextView tvGenre;
        TextView tvYear;
        TextView tvRating;
    }
}

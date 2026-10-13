package com.example.movietracker.model;

public class Movie {
    private final int id;
    private final String title;
    private final String genre;
    private final int year;
    private final String synopsis;
    private final String poster;
    private final double averageRating; // 0 jika belum ada rating
    private final int ratingCount;

    public Movie(int id, String title, String genre, int year, String synopsis,
                 String poster, double averageRating, int ratingCount) {
        this.id = id;
        this.title = title;
        this.genre = genre;
        this.year = year;
        this.synopsis = synopsis;
        this.poster = poster;
        this.averageRating = averageRating;
        this.ratingCount = ratingCount;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getGenre() { return genre; }
    public int getYear() { return year; }
    public String getSynopsis() { return synopsis; }
    public String getPoster() { return poster; }
    public double getAverageRating() { return averageRating; }
    public int getRatingCount() { return ratingCount; }
}

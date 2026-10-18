package kz.aitu.sdp.facade;

public class DvdPlayer {

    private boolean poweredOn;
    private String currentMovie;

    public void on() {
        poweredOn = true;
        System.out.println("DVD player: on");
    }

    public void play(String movie) {
        if (!poweredOn) {
            throw new IllegalStateException("Turn on the DVD player before playing a movie.");
        }
        if (movie == null || movie.isBlank()) {
            throw new IllegalArgumentException("Movie title must not be blank.");
        }

        currentMovie = movie;
        System.out.println("DVD player: playing " + currentMovie);
    }

    public void stop() {
        currentMovie = null;
        System.out.println("DVD player: stopped");
    }

    public void off() {
        if (currentMovie != null) {
            throw new IllegalStateException("Stop the movie before turning off the DVD player.");
        }

        poweredOn = false;
        System.out.println("DVD player: off");
    }

    public boolean isOn() {
        return poweredOn;
    }

    public boolean isPlaying() {
        return currentMovie != null;
    }
}

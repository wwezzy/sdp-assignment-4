package kz.aitu.sdp.facade;

import java.util.Objects;

public class HomeTheaterFacade {

    private static final int MOVIE_VOLUME = 5;
    private static final int MOVIE_BRIGHTNESS = 10;

    private final DvdPlayer dvdPlayer;
    private final Amplifier amplifier;
    private final Projector projector;
    private final Screen screen;
    private final TheaterLights lights;

    private boolean watching;

    public HomeTheaterFacade() {
        this(new DvdPlayer(), new Amplifier(), new Projector(), new Screen(), new TheaterLights());
    }

    HomeTheaterFacade(DvdPlayer dvdPlayer, Amplifier amplifier,
                      Projector projector, Screen screen, TheaterLights lights) {
        this.dvdPlayer = Objects.requireNonNull(dvdPlayer, "DVD player is required.");
        this.amplifier = Objects.requireNonNull(amplifier, "Amplifier is required.");
        this.projector = Objects.requireNonNull(projector, "Projector is required.");
        this.screen = Objects.requireNonNull(screen, "Screen is required.");
        this.lights = Objects.requireNonNull(lights, "Lights are required.");
    }

    public void watchMovie(String movie) {
        if (movie == null || movie.isBlank()) {
            throw new IllegalArgumentException("Movie title must not be blank.");
        }
        if (watching) {
            throw new IllegalStateException("End the current movie before starting another one.");
        }

        System.out.println("Get ready to watch: " + movie);
        try {
            screen.down();
            lights.dim(MOVIE_BRIGHTNESS);
            dvdPlayer.on();

            amplifier.on();
            amplifier.setSource(dvdPlayer);
            amplifier.setVolume(MOVIE_VOLUME);

            projector.on();
            projector.setInput(dvdPlayer);
            dvdPlayer.play(movie);
            projector.showPicture(screen);
            watching = true;
        } catch (RuntimeException failure) {
            try {
                shutdown();
            } catch (RuntimeException cleanupFailure) {
                failure.addSuppressed(cleanupFailure);
            }
            throw new IllegalStateException(
                    "The movie could not be started. Theater shutdown was attempted.", failure);
        }
    }

    public void endMovie() {
        System.out.println("Shutting down the home theater");
        shutdown();
    }

    public boolean isWatching() {
        return watching;
    }

    private void shutdown() {
        Runnable[] actions = {
                dvdPlayer::stop,
                projector::off,
                amplifier::off,
                dvdPlayer::off,
                screen::up,
                lights::on
        };

        RuntimeException firstFailure = null;
        for (Runnable action : actions) {
            try {
                action.run();
            } catch (RuntimeException failure) {
                if (firstFailure == null) {
                    firstFailure = failure;
                } else {
                    firstFailure.addSuppressed(failure);
                }
            }
        }

        watching = false;
        if (firstFailure != null) {
            throw new IllegalStateException(
                    "Some theater components could not be shut down.", firstFailure);
        }
    }
}


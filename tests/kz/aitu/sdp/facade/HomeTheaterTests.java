package kz.aitu.sdp.facade;

import java.io.OutputStream;
import java.io.PrintStream;

public class HomeTheaterTests {

    private static int passed;

    public static void main(String[] args) {
        PrintStream console = System.out;
        try (PrintStream quiet = new PrintStream(OutputStream.nullOutputStream())) {
            System.setOut(quiet);

            run(() -> {
                DvdPlayer player = new DvdPlayer();
                check(!player.isOn() && !player.isPlaying(), "A new player must be off.");
                expect(IllegalStateException.class, () -> player.play("Interstellar"));
            });
            run(() -> {
                DvdPlayer player = new DvdPlayer();
                player.on();
                expect(IllegalArgumentException.class, () -> player.play(null));
                expect(IllegalArgumentException.class, () -> player.play("  "));
                check(!player.isPlaying(), "Invalid titles must not start playback.");
            });
            run(() -> {
                DvdPlayer player = new DvdPlayer();
                player.on();
                player.play("Interstellar");
                expect(IllegalStateException.class, player::off);
                check(player.isOn() && player.isPlaying(), "Rejected shutdown must preserve playback.");
                player.stop();
                check(player.isOn() && !player.isPlaying(), "Stop must preserve power.");
                player.off();
                check(!player.isOn(), "Stopped player must switch off.");
            });
            run(() -> {
                Amplifier amplifier = new Amplifier();
                DvdPlayer player = new DvdPlayer();
                expect(IllegalStateException.class, () -> amplifier.setSource(player));
                expect(IllegalStateException.class, () -> amplifier.setVolume(5));
            });
            run(() -> {
                Amplifier amplifier = new Amplifier();
                amplifier.on();
                expect(IllegalArgumentException.class, () -> amplifier.setSource(null));
                expect(IllegalStateException.class, () -> amplifier.setSource(new DvdPlayer()));
                check(amplifier.getSource() == null, "Rejected source must not be stored.");
            });
            run(() -> {
                Amplifier amplifier = new Amplifier();
                DvdPlayer player = new DvdPlayer();
                player.on();
                amplifier.on();
                amplifier.setSource(player);
                check(amplifier.getSource() == player, "The same player reference must be stored.");
                amplifier.setVolume(0);
                check(amplifier.getVolume() == 0, "Zero volume must be allowed.");
                amplifier.setVolume(10);
                check(amplifier.getVolume() == 10, "Maximum volume must be allowed.");
                expect(IllegalArgumentException.class, () -> amplifier.setVolume(-1));
                expect(IllegalArgumentException.class, () -> amplifier.setVolume(11));
                check(amplifier.getVolume() == 10, "Invalid volume must preserve the previous value.");
                amplifier.off();
                check(!amplifier.isOn() && amplifier.getVolume() == 0
                        && amplifier.getSource() == null, "Shutdown must clear amplifier state.");
                check(player.isOn(), "Disconnecting a source must not switch off the player.");
            });
            run(() -> {
                Projector projector = new Projector();
                expect(IllegalStateException.class, () -> projector.setInput(new DvdPlayer()));
                expect(IllegalStateException.class, () -> projector.showPicture(new Screen()));
                projector.on();
                expect(IllegalArgumentException.class, () -> projector.setInput(null));
                expect(IllegalStateException.class, () -> projector.setInput(new DvdPlayer()));
            });
            run(() -> {
                Projector projector = new Projector();
                projector.on();
                Screen screen = new Screen();
                screen.down();
                expect(IllegalStateException.class, () -> projector.showPicture(screen));
            });
            run(() -> {
                DvdPlayer player = new DvdPlayer();
                Projector projector = new Projector();
                Screen screen = new Screen();
                player.on();
                projector.on();
                projector.setInput(player);
                screen.down();
                expect(IllegalStateException.class, () -> projector.showPicture(screen));
                player.play("Interstellar");
                projector.showPicture(screen);
                player.stop();
                expect(IllegalStateException.class, () -> projector.showPicture(screen));
            });
            run(() -> {
                DvdPlayer player = new DvdPlayer();
                Projector projector = new Projector();
                Screen screen = new Screen();
                player.on();
                player.play("Interstellar");
                projector.on();
                projector.setInput(player);
                expect(IllegalStateException.class, () -> projector.showPicture(screen));
                expect(IllegalArgumentException.class, () -> projector.showPicture(null));
                screen.down();
                projector.showPicture(screen);
                check(projector.getInput() == player, "Projector must use the selected player.");
                projector.off();
                check(!projector.isOn() && projector.getInput() == null,
                        "Projector shutdown must clear its input.");
            });
            run(() -> {
                TheaterLights lights = new TheaterLights();
                check(lights.getBrightness() == 100, "Lights must start at full brightness.");
                lights.dim(0);
                lights.dim(100);
                lights.dim(10);
                expect(IllegalArgumentException.class, () -> lights.dim(-1));
                expect(IllegalArgumentException.class, () -> lights.dim(101));
                check(lights.getBrightness() == 10, "Invalid brightness must preserve state.");
                lights.on();
                check(lights.getBrightness() == 100, "Restoring lights must use full brightness.");
            });
            run(() -> {
                Screen screen = new Screen();
                check(!screen.isDown(), "Screen must start raised.");
                screen.down();
                check(screen.isDown(), "Screen must be lowered.");
                screen.up();
                check(!screen.isDown(), "Screen must be raised.");
            });
            run(() -> {
                Fixture theater = new Fixture(new Projector());
                theater.facade.watchMovie("Interstellar");
                theater.checkPlaying();
                theater.facade.endMovie();
                theater.checkStopped();
            });
            run(() -> {
                Fixture theater = new Fixture(new Projector());
                expect(IllegalArgumentException.class, () -> theater.facade.watchMovie(null));
                expect(IllegalArgumentException.class, () -> theater.facade.watchMovie(""));
                expect(IllegalArgumentException.class, () -> theater.facade.watchMovie("  "));
                theater.checkStopped();
            });
            run(() -> {
                Fixture theater = new Fixture(new Projector());
                theater.facade.watchMovie("Interstellar");
                expect(IllegalStateException.class, () -> theater.facade.watchMovie("Arrival"));
                theater.checkPlaying();
                theater.facade.endMovie();
                theater.checkStopped();
            });
            run(() -> {
                Fixture theater = new Fixture(new Projector());
                theater.facade.watchMovie("Interstellar");
                theater.facade.endMovie();
                theater.facade.watchMovie("Arrival");
                theater.checkPlaying();
                theater.facade.endMovie();
                theater.checkStopped();
            });
            run(() -> {
                Fixture theater = new Fixture(new Projector());
                theater.facade.endMovie();
                theater.facade.endMovie();
                theater.checkStopped();
            });
            run(() -> {
                RuntimeException deviceFailure = new IllegalStateException("Simulated picture failure.");
                Projector broken = new Projector() {
                    @Override
                    public void showPicture(Screen screen) {
                        throw deviceFailure;
                    }
                };
                Fixture theater = new Fixture(broken);
                IllegalStateException failure = expect(IllegalStateException.class,
                        () -> theater.facade.watchMovie("Interstellar"));
                check(failure.getCause() == deviceFailure, "Startup must preserve the original failure.");
                theater.checkStopped();
            });
            run(() -> {
                Projector broken = new Projector() {
                    @Override
                    public void off() {
                        throw new IllegalStateException("Simulated projector shutdown failure.");
                    }
                };
                Fixture theater = new Fixture(broken);
                theater.facade.watchMovie("Interstellar");
                expect(IllegalStateException.class, theater.facade::endMovie);
                check(!theater.player.isOn() && !theater.player.isPlaying(),
                        "DVD cleanup must run after another device fails.");
                check(!theater.amplifier.isOn(), "Amplifier cleanup must still run.");
                check(!theater.screen.isDown() && theater.lights.getBrightness() == 100,
                        "Screen and lights must still be restored.");
                check(!theater.facade.isWatching(), "The session must no longer be active.");
                check(theater.projector.isOn(),
                        "A failed device is not falsely reported as successfully switched off.");
            });
            run(() -> {
                RuntimeException startupFailure = new IllegalStateException("Simulated startup failure.");
                Projector broken = new Projector() {
                    @Override
                    public void showPicture(Screen screen) {
                        throw startupFailure;
                    }

                    @Override
                    public void off() {
                        throw new IllegalStateException("Simulated cleanup failure.");
                    }
                };
                Fixture theater = new Fixture(broken);
                IllegalStateException failure = expect(IllegalStateException.class,
                        () -> theater.facade.watchMovie("Interstellar"));
                check(failure.getCause() == startupFailure, "Cleanup must not replace the startup cause.");
                check(startupFailure.getSuppressed().length == 1,
                        "Cleanup failure must remain available as a suppressed exception.");
                check(!theater.player.isOn() && !theater.amplifier.isOn(),
                        "Remaining devices must still receive cleanup calls.");
            });
            run(() -> expect(NullPointerException.class, () ->
                    new HomeTheaterFacade(null, new Amplifier(), new Projector(),
                            new Screen(), new TheaterLights())));
            run(() -> {
                HomeTheaterFacade facade = new HomeTheaterFacade();
                facade.watchMovie("Interstellar");
                check(facade.isWatching(), "Default facade must assemble working components.");
                facade.endMovie();
                check(!facade.isWatching(), "Default facade must end the session.");
            });

        } finally {
            System.setOut(console);
        }
        console.println("PASS: " + passed + " test scenarios");
    }

    private static void run(Runnable scenario) {
        scenario.run();
        passed++;
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private static <T extends RuntimeException> T expect(Class<T> type, Runnable operation) {
        try {
            operation.run();
        } catch (RuntimeException failure) {
            if (type.isInstance(failure)) {
                return type.cast(failure);
            }
            throw new AssertionError("Expected " + type.getSimpleName()
                    + " but received " + failure.getClass().getSimpleName(), failure);
        }
        throw new AssertionError("Expected " + type.getSimpleName());
    }

    private static class Fixture {
        final DvdPlayer player = new DvdPlayer();
        final Amplifier amplifier = new Amplifier();
        final Projector projector;
        final Screen screen = new Screen();
        final TheaterLights lights = new TheaterLights();
        final HomeTheaterFacade facade;

        Fixture(Projector projector) {
            this.projector = projector;
            facade = new HomeTheaterFacade(player, amplifier, projector, screen, lights);
        }

        void checkPlaying() {
            check(facade.isWatching() && player.isOn() && player.isPlaying(),
                    "The movie session must be running.");
            check(amplifier.isOn() && amplifier.getSource() == player && amplifier.getVolume() == 5,
                    "Audio must use the selected player and movie volume.");
            check(projector.isOn() && projector.getInput() == player && screen.isDown(),
                    "Video must use the selected player and lowered screen.");
            check(lights.getBrightness() == 10, "Movie lighting must be applied.");
        }

        void checkStopped() {
            check(!facade.isWatching() && !player.isOn() && !player.isPlaying(),
                    "The movie session must be stopped.");
            check(!amplifier.isOn() && amplifier.getSource() == null && amplifier.getVolume() == 0,
                    "Amplifier must be reset.");
            check(!projector.isOn() && projector.getInput() == null && !screen.isDown(),
                    "Projector and screen must be reset.");
            check(lights.getBrightness() == 100, "Lights must be restored.");
        }
    }
}


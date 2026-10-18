package kz.aitu.sdp.facade;

public class TheaterLights {

    private static final int FULL_BRIGHTNESS = 100;

    private int brightness = FULL_BRIGHTNESS;

    public void dim(int brightness) {
        if (brightness < 0 || brightness > FULL_BRIGHTNESS) {
            throw new IllegalArgumentException("Brightness must be between 0 and 100.");
        }

        this.brightness = brightness;
        System.out.println("Lights: brightness " + brightness + "%");
    }

    public void on() {
        brightness = FULL_BRIGHTNESS;
        System.out.println("Lights: brightness " + brightness + "%");
    }

    public int getBrightness() {
        return brightness;
    }
}


package kz.aitu.sdp.facade;

public class Projector {

    private boolean poweredOn;
    private DvdPlayer input;

    public void on() {
        poweredOn = true;
        System.out.println("Projector: on");
    }

    public void setInput(DvdPlayer input) {
        if (!poweredOn) {
            throw new IllegalStateException("Turn on the projector before selecting an input.");
        }
        if (input == null) {
            throw new IllegalArgumentException("Video input must not be null.");
        }
        if (!input.isOn()) {
            throw new IllegalStateException("Turn on the DVD player before connecting the projector.");
        }

        this.input = input;
        System.out.println("Projector: DVD player selected");
    }

    public void showPicture(Screen screen) {
        if (!poweredOn) {
            throw new IllegalStateException("Turn on the projector before showing a picture.");
        }
        if (screen == null) {
            throw new IllegalArgumentException("Screen must not be null.");
        }
        if (input == null || !input.isPlaying()) {
            throw new IllegalStateException("Start the selected DVD player before showing a picture.");
        }
        if (!screen.isDown()) {
            throw new IllegalStateException("Lower the screen before showing a picture.");
        }

        System.out.println("Projector: showing movie on the screen");
    }

    public void off() {
        poweredOn = false;
        input = null;
        System.out.println("Projector: off");
    }

    public boolean isOn() {
        return poweredOn;
    }

    public DvdPlayer getInput() {
        return input;
    }
}


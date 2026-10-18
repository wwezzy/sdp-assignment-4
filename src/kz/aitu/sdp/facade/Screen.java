package kz.aitu.sdp.facade;

public class Screen {

    private boolean lowered;

    public void down() {
        lowered = true;
        System.out.println("Screen: down");
    }

    public void up() {
        lowered = false;
        System.out.println("Screen: up");
    }

    public boolean isDown() {
        return lowered;
    }
}


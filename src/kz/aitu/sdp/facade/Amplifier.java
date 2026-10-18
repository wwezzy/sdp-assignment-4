package kz.aitu.sdp.facade;

public class Amplifier {

    private boolean poweredOn;
    private int volume;
    private DvdPlayer source;

    public void on() {
        poweredOn = true;
        System.out.println("Amplifier: on");
    }

    public void setSource(DvdPlayer source) {

        if (!poweredOn) {
                throw new IllegalStateException("Turn on the amplifier before selecting a source.");
            }

        if (source == null) {
                throw new IllegalArgumentException("Audio source must not be null.");
            }

        this.source = source;
        System.out.println("Amplifier: DVD player selected");
    }

    public void setVolume(int volume) {

        if (!poweredOn) {
            throw new IllegalStateException("Turn on the amplifier before setting the volume.");
        }

        if (volume < 0 || volume > 10) {
            throw new IllegalArgumentException("Volume must be between 0 and 10.");
        }

        this.volume = volume;
        System.out.println("Amplifier: volume " + volume);
    }

    public void off() {
        
    }
}

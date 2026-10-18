package kz.aitu.sdp.facade;

public class FacadeDemo {

    public static void main(String[] args) {
        HomeTheaterFacade theater = new HomeTheaterFacade();

        theater.watchMovie("Interstellar");
        theater.endMovie();

        System.out.println("\nInvalid request:");
        try {
            theater.watchMovie(" ");
        } catch (IllegalArgumentException failure) {
            System.out.println("Rejected: " + failure.getMessage());
        }
    }
}


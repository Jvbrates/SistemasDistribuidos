import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.Executors;

public class CapitalizeServer {
    public static void main(String[] args) throws Exception {
        try (var listener = new ServerSocket(59999)) {
            System.out.println("The capitalization server is running...");
            var pool = Executors.newFixedThreadPool(20);
            while (true) {
                pool.execute(new Capitalizer(listener.accept()));
            }
        }
    }

    private static class Capitalizer implements Runnable {
        private final Socket socket;

        public Capitalizer(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            System.out.println("Connected: " + socket);
            try {
                var in = new Scanner(socket.getInputStream());
                var out = new PrintWriter(socket.getOutputStream(), true);
                while (in.hasNextLine()) { //Bloqueante
                    out.println(in.nextLine().toUpperCase());
                }
            } catch (Exception e) {
                System.out.println("Error:" + socket);
            } finally {
                /*not use a try-with-resources block here because the socket was
                created on the main thread.*/
                try {
                    socket.close();
                } catch (IOException e) {
                    System.err.println("Error catched:" + e.getMessage());
                }
                System.out.println("Closed: " + socket);
            }
        }
    }
}

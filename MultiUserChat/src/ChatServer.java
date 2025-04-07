import java.awt.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.Executors;

public class ChatServer {

    private static final Set<String> usernames = new HashSet<>();

    private static Set<PrintWriter> writers = new HashSet<>();

    private static ArrayList<Color> COLORS = new ArrayList<>(Arrays.asList(
            Color.RED, // own by system
            Color.DARK_GRAY,
            Color.GREEN,
            Color.BLUE,
            Color.YELLOW,
            Color.CYAN,
            Color.MAGENTA,
            Color.ORANGE,
            Color.BLACK
    ));

    static int counterColor = 0;
    private static int getColor(){
        return (counterColor++)%(COLORS.size()-1) + 1;
    }

    public static void main(String[] args) throws Exception {
        System.out.println("Server is running...");
        var pool = Executors.newFixedThreadPool(20);
        try (var listener = new ServerSocket(59999)) {
            while (true) {
                pool.execute(new Handler(listener.accept()));
            }
        }
    }

    private static class Handler implements Runnable {
        private Socket socket;
        private String name;
        private int color = 1;
        private Scanner in;
        private PrintWriter out;

        public Handler(Socket socket) {
            this.socket = socket;
        }


        public void broadcast(String name, int color, String msg) {
            for (PrintWriter writer : writers) {
                String date = new Date().toString();
                writer.println("MESSAGE "+ color +" " + name + ": " + msg + " ["+date+"]");
            }
        }

        public void run() {
            try {
                this.in = new Scanner(socket.getInputStream());
                this.out = new PrintWriter(socket.getOutputStream(), true);
                while (true) {
                    out.println("SUBMITNAME");
                    name = in.nextLine();
                    if (name == null) {
                        return;
                    }
                    synchronized (usernames) {
                        if (!name.isBlank() && !usernames.contains(name)) {
                            usernames.add(name);
                            color = getColor();
                            break;
                        }
                    }
                }
                out.println("NAMEACCEPTED " + name);
                broadcast("SYSTEM", 0, name+" has joined" );
                writers.add(out);

                // Accept messages from this client and broadcast them.
                while (true) {
                    String input = in.nextLine();
                    if (input.toLowerCase().startsWith("/quit")) {
                        return;
                    }
                    broadcast(name, color, input);
                }

            } catch (Exception e) {
                System.out.println(e);
            } finally {
                if (out != null) {
                    writers.remove(out);
                }
                if (name != null) {
                    System.out.println(name + " is leaving");
                    usernames.remove(name);
                    broadcast("SYSTEM", 0, name + " has left ");
                }
                try {
                    socket.close();
                } catch (IOException _) {
                }
            }
        }
    }
}


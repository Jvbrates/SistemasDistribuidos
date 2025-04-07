import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.util.Date;

public class DateServer {
    public static void main(String[] args) throws IOException {
        try (var listener = new ServerSocket(59090)) {
            System.out.println("Server Start, Listening on port 59090");

            while (true) {
                var socket = listener.accept();
                System.out.println("Client " + socket.getInetAddress().getHostAddress() + ":" + socket.getPort());

                // True is to AutoFlush (Enviar sem esperar o buffer encher ou comando de envio)
                var out = new PrintWriter(socket.getOutputStream(), true);

                out.println(new Date().toString());
            }
        }
    }
}

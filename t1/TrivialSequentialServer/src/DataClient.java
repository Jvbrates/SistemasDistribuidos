import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class DataClient {
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Incorrect Args. Use:<host>");
            return;
        }
        var socket = new Socket(args[0], 59090);
        var in = new Scanner(socket.getInputStream());
        System.out.println("Server Response: " + in.nextLine());
    }
}

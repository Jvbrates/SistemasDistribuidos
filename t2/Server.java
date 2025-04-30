import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Server {

    public static void main(String[] args) {


        try {
            Registry registry = LocateRegistry.createRegistry(2020);
            ServerChat serverChat = new ServerChat();
            Naming.bind("rmi://localhost:2020/Servidor", serverChat);

        } catch (RemoteException | MalformedURLException e) {
            e.printStackTrace();
        } catch (AlreadyBoundException e) {
            throw new RuntimeException(e);
        }

    }
}

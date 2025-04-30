import java.io.Serializable;
import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ServerChat extends UnicastRemoteObject implements IServerChat, Serializable {
    private static Registry registry;
    private final Map<String, IRoomChat> roomList = new HashMap<String, IRoomChat>();

    protected ServerChat() throws RemoteException {
    }

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

    @Override
    public synchronized ArrayList<String> getRooms() {
        return new ArrayList<String>(roomList.keySet());
    }

    @Override
    public synchronized void createRoom(String roomName) throws RemoteException {
        if (!roomList.containsKey(roomName)) {
            RoomChat room = new RoomChat();
            try {
                registry.bind(roomName, room);
                roomList.put(roomName, room);
                System.out.println("Room " + roomName + " created");

            } catch (AlreadyBoundException e) {
                System.out.println("[UserChat] " + roomName + " already exists");
            }

        }
    }

}

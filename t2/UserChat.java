import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class UserChat extends UnicastRemoteObject implements IUserChat {
    IServerChat serverChat;
    ArrayList<IRoomChat> roomChats;

    @Override
    public void deliverMsg(String senderName, String msg) {
        System.out.println("[UserChat] " + senderName + ": " + msg);
    }

    public UserChat(String url) throws RemoteException, MalformedURLException, NotBoundException {
        this.serverChat = (IServerChat)Naming.lookup(url);
    }

    ArrayList<String> getRooms() throws RemoteException {
        return serverChat.getRooms();
    }

    void createRoom(String roomName) throws RemoteException {
        serverChat.createRoom(roomName);
    }

    public static IRoomChat joinRoom(String roomName)  {
        try {
            return (IRoomChat) Naming.lookup("rmi://localhost:2020/"+roomName);
        } catch (Exception e) {
            return null;
        }
    }

    public static void main(String[] args) {
        try {
            UserChat userChat = new UserChat("rmi://localhost:2020/Servidor");
            ArrayList<String> rooms = userChat.getRooms();
            rooms.forEach(System.out::println);
            userChat.createRoom("SalaTeste");
            rooms = userChat.getRooms();
            rooms.forEach(System.out::println);
            IRoomChat roomChat = joinRoom("testRoom");
            roomChat.joinRoom("User", userChat);
        } catch (RemoteException e){
            e.printStackTrace();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (NotBoundException e) {
            throw new RuntimeException(e);
        }
    }

}

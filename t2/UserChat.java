import java.io.Serializable;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
public class UserChat extends UnicastRemoteObject implements IUserChat, Serializable {
    private final String name;
    boolean roomOpened = false;
    private final IServerChat serverChat;
    private IRoomChat roomChat;
    private CallbackDeliverMsg callbackDeliverMsg;


    public void sendMesage(String msg) {
        try {
            roomChat.sendMsg(this.name, msg);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public UserChat(String url, String name, CallbackDeliverMsg callbackDeliverMsg) throws RemoteException, MalformedURLException, NotBoundException {
        this.serverChat = (IServerChat) Naming.lookup(url);
        this.name = name;
        this.callbackDeliverMsg = callbackDeliverMsg;
    }

    public boolean getRoomOpened() {
        return this.roomOpened;
    }

    public static void main(String[] args) {
        try {
            String username = args[0];

            UserChat userChat = new UserChat("rmi://localhost:2020/Servidor", args[0], new ClientGUI());
            ArrayList<String> rooms = userChat.getRooms();
            rooms.forEach(System.out::println);
            userChat.createRoom("SalaTeste");
            rooms = userChat.getRooms();
            rooms.forEach(System.out::println);
            IRoomChat roomChat = userChat.joinRoom("SalaTeste");
            assert roomChat != null;


            while (userChat.roomOpened()) {
                String msg = System.console().readLine();
                if (msg.equals("/leave")) {
                    userChat.leave();
                    continue;
                }
                userChat.send(msg);
            }

            System.exit(0);


        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (MalformedURLException | NotBoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void send(String msg) {
        try {
            roomChat.sendMsg(name, msg);
        } catch (Exception e) {e.printStackTrace();}
    }

    public boolean roomOpened() {
        return roomOpened;
    }

    @Override
    public void deliverMsg(String senderName, String msg) throws RemoteException {
        if (msg.equals("Sala fechada pelo servidor")) {
            this.roomChat = null;
            this.roomOpened = false;

        }
        callbackDeliverMsg.receive(senderName, msg);
        System.out.println("[UserChat] " + senderName + ": " + msg);
    }

    public String getName() {
        return this.name;
    }

    ArrayList<String> getRooms() throws RemoteException {
        return serverChat.getRooms();
    }

    void createRoom(String roomName) throws RemoteException {
        serverChat.createRoom(roomName);
    }

    public IRoomChat joinRoom(String roomName) throws RemoteException, MalformedURLException, NotBoundException {

            roomChat = (IRoomChat) Naming.lookup("rmi://localhost:2020/" + roomName);
            roomChat.joinRoom(this.getName(), this);
            roomOpened = true;
            return roomChat;

    }

    public void leave() {
        try {
            if (roomOpened && roomChat != null) {
                roomChat.leaveRoom(name);
                roomChat = null;
                roomOpened = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

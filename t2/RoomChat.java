import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

public class RoomChat extends UnicastRemoteObject implements IRoomChat, Serializable {
    private final Map<String, IUserChat> userList = new HashMap<String, IUserChat>();
    private final Registry registry;
    private final String roomName;

    protected RoomChat(Registry registry, String roomName) throws RemoteException {
        this.registry = registry;
        this.roomName = roomName;
    }

    @Override
    public synchronized void sendMsg(String userName, String msg) throws RemoteException{
        userList.forEach( (name,user) -> {
            try {
                user.deliverMsg(userName ,msg);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });
        System.out.println("["+roomName+"]User " + userName + " sent msg ");
    }

    @Override
    public synchronized void joinRoom(String userName, IUserChat user) throws RemoteException{
        userList.put(userName, user);
        System.out.println("["+roomName+"] User " + userName + " joined");
        sendMsg("System", "User " + userName + " joined ");
    }

    @Override
    public synchronized void leaveRoom(String usrName)  throws RemoteException{
        userList.remove(usrName);
        System.out.println("["+roomName+"] User " + usrName + " left");
        sendMsg("System", "User " + usrName + " left ");

    }

    @Override
    public synchronized String getRoomName()  throws RemoteException {
        return this.roomName;
    }

    @Override
    public synchronized void closeRoom()  throws RemoteException{
        try {
            if (!userList.isEmpty()){
                sendMsg("System", "Closing room");
            }
            registry.unbind(roomName);
            sendMsg("System", "Sala fechada pelo servidor");
        } catch (java.rmi.NotBoundException e) {
            System.out.println("Room not found");
        }


    }
}


import java.rmi.RemoteException;
import java.util.ArrayList;

public interface IServerChat extends java.rmi.Remote {
    public /*synchronized*/ ArrayList<String> getRooms() throws RemoteException;

    public /*synchronized*/ void createRoom(String roomName) throws RemoteException;

}
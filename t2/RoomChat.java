import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;

public class RoomChat extends UnicastRemoteObject implements IRoomChat, Serializable {
    private Map<String, IUserChat> userList;

    protected RoomChat() throws RemoteException {
    }

    @Override
    public void sendMsg(String usrName, String msg) {

    }

    @Override
    public void joinRoom(String userName, IUserChat user) {

    }

    @Override
    public void leaveRoom(String usrName) {

    }

    @Override
    public String getRoomName() {
        return "";
    }

    @Override
    public void closeRoom() {

    }
}


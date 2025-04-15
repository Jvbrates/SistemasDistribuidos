import java.util.ArrayList;

public interface IServerChat extends java.rmi.Remote {
    public /*synchronized*/ ArrayList<String> getRooms();
    public /*synchronized*/ void createRoom(String roomName);
}
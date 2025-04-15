import java.rmi.Remote;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ServerChat implements IServerChat {
    private final Map<String, IRoomChat> roomList = new HashMap<String, IRoomChat>();

    @Override
    public synchronized ArrayList<String> getRooms() {
        return new ArrayList<String>(roomList.keySet());
    }

    @Override
    public synchronized void createRoom(String roomName) {
        if(!roomList.containsKey(roomName)){
            roomList.put(roomName, new RoomChat());
        }
    }
}

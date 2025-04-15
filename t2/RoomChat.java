import java.util.Map;

public class RoomChat implements IRoomChat {
    private Map<String, IUserChat> userList;

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


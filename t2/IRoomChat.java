public interface IRoomChat extends java.rmi.Remote {
    public /*synchronized*/ void sendMsg(String usrName, String msg);
    public /*synchronized*/ void joinRoom(String userName, IUserChat user);
    public /*synchronized*/ void leaveRoom(String usrName);
    public /*synchronized*/ String getRoomName();
    public void closeRoom();
}
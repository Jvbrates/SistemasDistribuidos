import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Set;

public class ServerGUI extends JFrame implements IcallbackCreateRoom {
    public static DefaultTableModel model = new DefaultTableModel(new Object[]{"Salas"}, 0);
    private JComboBox<String> roomComboBox;
    private JButton closeRoomButton;

    static ServerChat serverChat;
    public void closeRoom() {
        try {
            String room = roomComboBox.getSelectedItem().toString();
            serverChat.closeRoom(room);
            update_table();
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public ServerGUI() throws RemoteException, MalformedURLException, AlreadyBoundException {
        serverChat = new ServerChat("rmi://0.0.0.0:2020/Servidor", this);
        roomComboBox = new JComboBox<>();
        closeRoomButton = new JButton("Fechar sala");
        JPanel roomPanel = new JPanel();
        roomPanel.add(roomComboBox);
        roomPanel.add(closeRoomButton);
        this.add(roomPanel, BorderLayout.NORTH);
        closeRoomButton.addActionListener(e -> closeRoom());

        this.setTitle("Salas");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(250, 150);
        update_table();

        JTable tabela = new JTable(ServerGUI.model);


        // Adiciona tabela ao frame
        this.add(new JScrollPane(tabela));
        this.setVisible(true);
    }

    public static void main(String[] args) throws RemoteException, MalformedURLException, AlreadyBoundException {
        ServerGUI serverGUI = new ServerGUI();
        serverGUI.setVisible(true);
    }

    private void update_table() {
        ArrayList<String> arrayRooms = serverChat.getRooms();

        model.setDataVector(new Object[][]{arrayRooms.toArray()}, new String[]{"Salas"});
        atualizarComboBox(this.roomComboBox, serverChat.getRooms());
    }

    public static void atualizarComboBox(JComboBox<String> comboBox, ArrayList<String> itens) {
        comboBox.removeAllItems();
        for (String item : itens) {
            comboBox.addItem(item);
        }
    }


    @Override
    public void roomListUpdate(Set<String> roomList) {
        Object[][] arrayRooms = new Object[roomList.size()][1];
        int i = 0;
        for (String room : roomList) {
            arrayRooms[i][0] = room;
            i++;
        }

        model.setDataVector(arrayRooms, new String[]{"Salas"});
        atualizarComboBox(this.roomComboBox, new ArrayList<String>(roomList));
    }
}

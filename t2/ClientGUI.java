import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.*;
import java.rmi.RemoteException;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.util.List;

public class ClientGUI extends JFrame implements CallbackDeliverMsg {
    private JTextField messageField;
    private JTextArea chatArea;
    private JButton sendButton;
    private JComboBox<String> roomComboBox;
    private JButton joinRoomButton;
    private JButton leaveRoomButton;
    private JButton createRoomButton;

    private UserChat userChat;
    private String userName;

    public ClientGUI() {
        setTitle("Client Chat");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Solicitar nome do usuário
        userName = JOptionPane.showInputDialog(this, "Digite seu nome:");
        if (userName == null || userName.trim().isEmpty()) {
            userName = "Anônimo";
        }

        try {
            // Conectar ao servidor RMI
            userChat = new UserChat("rmi://localhost:2020/Servidor", userName, this);
        } catch (RemoteException | MalformedURLException | NotBoundException e) {
            JOptionPane.showMessageDialog(this, "Erro ao conectar ao servidor: " + e.getMessage(), 
                                       "Erro de Conexão", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        // Layout setup
        setLayout(new BorderLayout());

        // Chat area
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(chatArea);
        add(scrollPane, BorderLayout.CENTER);

        // Input panel
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BorderLayout());

        messageField = new JTextField();
        inputPanel.add(messageField, BorderLayout.CENTER);

        sendButton = new JButton("Enviar");
        inputPanel.add(sendButton, BorderLayout.EAST);

        add(inputPanel, BorderLayout.SOUTH);

        // Room control panel
        JPanel roomPanel = new JPanel();
        roomComboBox = new JComboBox<>();
        joinRoomButton = new JButton("Juntar-se à Sala");
        leaveRoomButton = new JButton("Sair da sala");
        createRoomButton = new JButton("Criar Sala");
        
        roomPanel.add(new JLabel("Salas:"));
        roomPanel.add(roomComboBox);
        roomPanel.add(joinRoomButton);
        roomPanel.add(leaveRoomButton);
        leaveRoomButton.setEnabled(false);
        roomPanel.add(createRoomButton);
        
        add(roomPanel, BorderLayout.NORTH);

        // Configurar listeners
        sendButton.addActionListener(e -> sendMessage());
        messageField.addActionListener(e -> sendMessage());
        joinRoomButton.addActionListener(e -> joinRoom());
        createRoomButton.addActionListener(e -> createRoom());
        leaveRoomButton.addActionListener(e -> leaveRoom());
        roomComboBox.addPopupMenuListener(new PopupMenuListener() {
                                              @Override
                                              public void popupMenuCanceled(PopupMenuEvent popupMenuEvent) {

                                              }

                                              @Override
                                              public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                                                  updateRoomsList();
                                                  updateLeaveButtonCombobox();
                                              }

                                              @Override
                                              public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                                                  // Quando fecha
                                              }
                                          });
        // Atualizar lista de salas
        updateRoomsList();
        updateLeaveButtonCombobox();
    }

    public void receive(String senderName, String msg){
        chatArea.append(senderName + ": " + msg + "\n");
        updateLeaveButtonCombobox();
        updateRoomsList();
    }

    private void updateRoomsList() {
        try {
            List<String> rooms = userChat.getRooms();
            roomComboBox.removeAllItems();
            for (String room : rooms) {
                roomComboBox.addItem(room);
            }
        } catch (RemoteException e) {
            JOptionPane.showMessageDialog(this, "Erro ao obter a lista de salas: " + e.getMessage(), 
                                       "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void joinRoom() {
        String selectedRoom = (String) roomComboBox.getSelectedItem();
        if (selectedRoom != null) {
            try {
                userChat.joinRoom(selectedRoom);
                updateLeaveButtonCombobox();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erro ao juntar-se à sala: " + e.getMessage(),
                                           "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void updateLeaveButtonCombobox() {
        if (userChat.getRoomOpened()){
            roomComboBox.setEnabled(false);
            joinRoomButton.setEnabled(false);
            leaveRoomButton.setEnabled(true);
        } else {
            roomComboBox.setEnabled(true);
            joinRoomButton.setEnabled(roomComboBox.getItemCount() > 0);
            leaveRoomButton.setEnabled(false);
        }
        System.out.println(roomComboBox.getItemCount());
    }

    private void leaveRoom() {
        userChat.leave();
        updateLeaveButtonCombobox();
    }


    private void createRoom() {
        String roomName = JOptionPane.showInputDialog(this, "Nome da Sala:");
        if (roomName != null && !roomName.trim().isEmpty()) {
            try {
                userChat.createRoom(roomName.trim());
                updateRoomsList();
                updateLeaveButtonCombobox();
                chatArea.append("Sala criada: " + roomName + "\n");
            } catch (RemoteException e) {
                JOptionPane.showMessageDialog(this, "Erro criando a sala: " + e.getMessage(), 
                                           "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void sendMessage() {
        String message = messageField.getText().trim();
        if (!message.isEmpty()) {
            try {
                if (userChat.getRoomOpened()) {
                    userChat.sendMesage(message);
                } else {
                    chatArea.append("Sistema: Você não está em nenhuma sala. Junte-se a uma sala.\n");
                }
                messageField.setText("");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erro mandando mensagem: " + e.getMessage(), 
                                           "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
        chatArea.setCaretPosition(chatArea.getDocument().getLength());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ClientGUI clientGUI = new ClientGUI();
            clientGUI.setVisible(true);
        });
    }
}
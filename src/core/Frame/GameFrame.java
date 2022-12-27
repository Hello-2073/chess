package core.Frame;

import javax.swing.*;
import java.net.Socket;

public class GameFrame extends JFrame{
    private String myAccount;
    private String yourAccount;

    private Socket socket;

    private GamePanel gamePanel;
    private ListFrame listFrame;

    public GameFrame(ListFrame listFrame, boolean isBlack, String opposite_account) {
        if(listFrame != null) {
            this.socket = listFrame.getSocket();
            this.listFrame = listFrame;
        }
        if(listFrame != null) this.myAccount = listFrame.getAccount();
        this.yourAccount = opposite_account;

        this.setTitle("游戏界面-与"+yourAccount+"的对战-当前用户-"+myAccount);
        this.setSize(900, 675);
        this.setResizable(false);
        this.setLocationRelativeTo(listFrame);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setLayout(null);

        this.gamePanel = new GamePanel(this, isBlack, this.yourAccount);
        gamePanel.setBounds(0, 0, 900, 640);
        this.add(gamePanel);

        this.setVisible(true);
    }


    public String getMyAccount() {
        return myAccount;
    }

    public Socket getSocket() {
        return socket;
    }

    public GamePanel getGamePanel() {
        return gamePanel;
    }

    public ListFrame getListFrame() {return listFrame;}
}

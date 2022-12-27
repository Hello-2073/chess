package core.Frame;

import core.model.Step;
import core.network.ClientThread;
import core.network.SocketMessage;

import javax.swing.*;
import java.awt.event.*;
import java.net.Socket;
import java.util.Vector;

/*
    这是游戏大厅 因为外观是列表所以叫作 ListFrame
    它运行着客户端的接收线程
    在waiting函数中，修改重写的response()方法，可以添加更多接收功能
*/

public class ListFrame extends JFrame implements ActionListener {
    public static void main(String[] args) {
        new LoginFrame();
    }

    private final Socket socket;
    private final String account;
    private Vector<String> players;

    private GameFrame gameFrame = null;
    private GamePanel gamePanel = null;

    private boolean isInviting = false;

    private final JTextField searchField;
    private final JButton refreshButton;
    private final JButton searchButton;
    private  DefaultListModel playerModel;
    private JList playerList;

    public ListFrame(LoginFrame loginFrame) {
        System.out.println("listfram launched");
        this.socket = loginFrame.getSocket();
        this.account = loginFrame.getAccount();

        this.setTitle("BUAA国际象棋Online-玩家列表-欢迎["+this.account+"]");
        this.setSize(900, 675);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLayout(null);

        this.searchField = new JTextField("输入ID...");
        searchField.setBounds(280, 20, 560, 35);

        this.add(searchField);

        this.searchButton = new JButton("通过ID挑战");
        searchButton.setBounds(130, 20, 120, 30);
        searchButton.setActionCommand("search");
        searchButton.addActionListener(this);
        this.add(searchButton);

        this.refreshButton = new JButton("刷新");
        refreshButton.setBounds(30, 20, 90, 30);
        refreshButton.setActionCommand("refresh");
        refreshButton.addActionListener(this);
        this.add(refreshButton);

        this.playerModel= new DefaultListModel();
        this.playerList = new JList(playerModel);
        this.playerList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getClickCount() == 2) {
                    // 向这个玩家发起挑战
                    String receiver = players.elementAt(playerList.getSelectedIndex());
                    if(receiver.equals(account)){
                        HintFrame.HintAction ha = new HintFrame.HintAction() {
                            @Override
                            public void yes() {}
                            @Override
                            public void no() {}
                        };
                        new HintFrame("尊敬的 "+account+" : 不能挑战自己", ha);
                    }
                    else{
                        System.out.println("向"+receiver+"发起挑战");
                        invite(receiver);
                        isInviting = true;
                    }
                }
            }
        });
        JScrollPane listScrollPanel = new JScrollPane(playerList);
        listScrollPanel.setBounds(30, 80, 820, 500);
        this.add(listScrollPanel);

        this.setResizable(false);
        this.setVisible(true);

        refresh();
        waiting();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String actionCommand = e.getActionCommand();
        switch(actionCommand) {
            case "refresh":
                refresh();
                break;
            case "search":
                search();
                break;
            default:
                break;
        }
    }

    private void refresh() {
        SocketMessage msg = new SocketMessage(SocketMessage.Type.LIST, null);
        msg.setSender(account);
        msg.send(socket);

    }

    private void search(){
        String searchAcount = searchField.getText();
        if(playerModel.contains(searchAcount)){
            invite(searchAcount);
        }
        else{
            searchField.setText("不存在该用户");
        }
    }

    private void invite(String receiver) {
        SocketMessage msg = new SocketMessage(SocketMessage.Type.INVITE, null);
        msg.setSender(this.account);
        msg.setReceiver(receiver);
        msg.send(socket);
        System.out.println("已发送向 "+msg.getReceiver()+" 的对战请求");
    }

    // 网络监听
    private void waiting() {
        System.out.println("Begin to wait");
        ClientThread.ClientListenser listenser = new ClientThread.ClientListenser() {
            @Override
            public void response(SocketMessage msg) {
                System.out.println("New message "+msg.getContent());
                switch (msg.getType()) {
                    case LIST -> {
                        players = (Vector<String>)msg.getContent();
                        playerModel.clear();
                        playerModel.addAll(players);
                        playerList.validate();
                    }
                    case INVITE -> {
                        HintFrame.HintAction ha = new HintFrame.HintAction() {
                            @Override
                            public void yes() {
                                SocketMessage agree = new SocketMessage(SocketMessage.Type.AGREE, null);
                                agree.setSender(msg.getReceiver());
                                agree.setReceiver(msg.getSender());
                                agree.send(socket);
                            }
                            @Override
                            public void no() {
                                SocketMessage refuse = new SocketMessage(SocketMessage.Type.REFUSE, null);
                                refuse.setSender(msg.getReceiver());
                                refuse.setSender(msg.getSender());
                                refuse.send(socket);
                            }
                        };
                        new HintFrame("尊敬的 "+account+" : "+msg.getSender()+" 请求对战", ha);
                    }
                    case AGREE -> {
                        isInviting = false;
                        System.out.println("开始与 " + msg.getSender() + " 的游戏");
                        launchGame(msg.getSender(), (boolean)msg.getContent());
                    }
                    case REFUSE -> {
                        isInviting = false;
                        System.out.println("对方已拒绝");
                    }
                    case STEP -> {
                        gamePanel.beginMyTurn((Step)msg.getContent());
                    }
                    case RESTART_REQUEST -> {
                        HintFrame.HintAction ha = new HintFrame.HintAction() {
                            @Override
                            public void yes() {
                                SocketMessage agree = new SocketMessage(SocketMessage.Type.RESTART_AGREE, null);
                                agree.setSender(msg.getReceiver());
                                agree.setReceiver(msg.getSender());
                                agree.send(socket);
                            }
                            @Override
                            public void no() {
                                SocketMessage refuse = new SocketMessage(SocketMessage.Type.RESTART_REFUSE, null);
                                refuse.setSender(msg.getReceiver());
                                refuse.setSender(msg.getSender());
                                refuse.send(socket);
                            }
                        };
                        new HintFrame("尊敬的 "+account+" : "+msg.getSender()+"请求重来", ha);
                    }
                    case RESTART_EXCUTE -> {
                        System.out.println("对方同意重来");
                        gamePanel.restarted();
                    }
                    case RESTART_REFUSE -> {
                        HintFrame.HintAction ha = new HintFrame.HintAction() {
                            @Override
                            public void yes() {}
                            @Override
                            public void no() {}
                        };
                        new HintFrame("尊敬的 "+account+" : "+msg.getSender()+" 拒绝重来", ha);
                    }
                    case REGRET_REQUEST -> {
                        HintFrame.HintAction ha = new HintFrame.HintAction() {
                            @Override
                            public void yes() {
                                SocketMessage agree = new SocketMessage(SocketMessage.Type.REGRET_AGREE, null);
                                agree.setSender(msg.getReceiver());
                                agree.setReceiver(msg.getSender());
                                agree.send(socket);
                            }
                            @Override
                            public void no() {
                                SocketMessage refuse = new SocketMessage(SocketMessage.Type.REGRET_REFUSE, null);
                                refuse.setSender(msg.getReceiver());
                                refuse.setSender(msg.getSender());
                                refuse.send(socket);
                            }
                        };
                        new HintFrame("尊敬的 "+account+" : "+msg.getSender()+" 请求悔棋", ha);
                    }
                    case REGRET_EXCUTE -> {
                        System.out.println("对方同意悔棋");
                        gamePanel.regreted();
                    }
                    case REGRET_REFUSE -> {
                        HintFrame.HintAction ha = new HintFrame.HintAction() {
                            @Override
                            public void yes() {}
                            @Override
                            public void no() {}
                        };
                        new HintFrame("尊敬的 "+account+" : "+msg.getSender()+" 拒绝悔棋", ha);
                    }
                    case TEXT -> {
                        gamePanel.chated(msg);
                    }
                    case DEFEAT -> {
                        HintFrame.HintAction ha = new HintFrame.HintAction() {
                            @Override
                            public void yes() {}
                            @Override
                            public void no() {}
                        };
                        gamePanel.setStateLabel("对方认输");
                        new HintFrame("尊敬的 "+account+" : "+msg.getSender()+" 向您认输", ha);
                    }
                    case BACK_TO_LIST -> {
                        HintFrame.HintAction ha = new HintFrame.HintAction() {
                            @Override
                            public void yes() {}
                            @Override
                            public void no() {}
                        };
                        new HintFrame("尊敬的 "+account+" : "+msg.getSender()+" 退出游戏", ha);
                        gamePanel.backToListed();
                    }
                    default -> {
                        System.out.println("Unknown requst");
                    }
                }
            }
        };
        System.out.println("listener created");
        ClientThread clientThread = new ClientThread(socket, listenser);
        System.out.println("Thread created");
    }

    // 启动游戏
    private void launchGame(String account, boolean isBlack) {
        this.gameFrame = new GameFrame(this, isBlack, account);
        this.gamePanel = gameFrame.getGamePanel();
        this.dispose();
    }

    public Socket getSocket() {
        return socket;
    }

    public String getAccount() {
        return account;
    }
}

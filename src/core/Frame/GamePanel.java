package core.Frame;

import core.model.ChessBoard;
import core.model.Step;
import core.model.chessman.Chess;
import core.network.SocketMessage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.Socket;

public class GamePanel extends JPanel implements ActionListener {
    private static final int LENGTH = 640;
    private static final int WIDTH = 640;
    private final int MARGIN = 45;
    private final int SIZE = 55;
    private final int SPACE = 70;

    private ChessBoard chessBoard;

    private boolean isBlack;
    private boolean inBlackTurn;
    private Chess selectedChess = null;
    private Point selectedPoint = null;
    private int stepCnt = 0;

    private String myAccount;
    private String yourAccount;
    private Socket socket;

    private JFrame gameFrame;
    private JFrame listFrame;
    private JLabel stateLabel;
    private JButton beginButton;
    private JButton regretButton;
    private JButton defeatButton;
    private JButton peaceButton;

    private JScrollPane chatJsp;
    private JTextArea chatJta;
    private JTextField chatJtf;
    private JButton chatSend;

    private Image pic_board;


    public GamePanel(GameFrame gameFrame, boolean isBlack, String yourAccount) {
        this.myAccount = gameFrame.getMyAccount();
        this.yourAccount = yourAccount;
        this.socket = gameFrame.getSocket();

        this.gameFrame = gameFrame;
        this.listFrame = gameFrame.getListFrame();
        this.isBlack = isBlack;
        this.chessBoard = new ChessBoard();
        String path_board = "pic" + File.separator + (isBlack ? "chessboard_black.png" : "chessboard_white.png");
        pic_board = Toolkit.getDefaultToolkit().getImage(path_board);

        this.setLayout(null);

        this.stateLabel = new JLabel("正在游戏");
        this.stateLabel.setBounds(735, 70, 120, 30);
        this.add(stateLabel);

        this.beginButton = new JButton("重新开始");
        this.beginButton.setBounds(700, 130, 120, 30);
        this.beginButton.setActionCommand("Restart");
        this.beginButton.addActionListener(this);
        this.add(beginButton);

        this.regretButton = new JButton("悔棋");
        this.regretButton.setBounds(700, 170, 120, 30);
        this.regretButton.setActionCommand("Regret");
        this.regretButton.addActionListener(this);
        this.add(regretButton);

        this.defeatButton = new JButton("认输");
        this.defeatButton.setBounds(700, 210, 120, 30);
        this.defeatButton.setActionCommand("Defeat");
        this.defeatButton.addActionListener(this);
        this.add(defeatButton);

        this.peaceButton = new JButton("回到列表");
        this.peaceButton.setBounds(700, 250, 120, 30);
        this.peaceButton.setActionCommand("Exit");
        this.peaceButton.addActionListener(this);
        this.add(peaceButton);

        this.chatJsp = new JScrollPane();
        this.chatJsp.setBounds(660, 340, 210, 200);
        this.chatJta = new JTextArea(200, 200);
        chatJta.setBounds(0,0,210, 200);
        chatJta.setEditable(false);
        this.chatJsp.add(chatJta);
        this.add(chatJsp);

        this.chatJtf = new JTextField(200);
        this.chatJtf.setBounds(660, 550, 150, 30);
        this.add(chatJtf);

        this.chatSend = new JButton("发送");
        this.chatSend.setBounds(810, 550, 60, 30);
        this.chatSend.setActionCommand("Send");
        this.chatSend.addActionListener(this);
        this.add(chatSend);

        this.chessBoard.stdReset();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (isBlack != inBlackTurn) return;
                Point clickedPoint = getClickedPoint(e.getX(), e.getY());
                if(clickedPoint != null) {
                    System.out.println("Clicked at"+"("+e.getX()+","+e.getY()+")");
                    System.out.println("Clicked at"+"("+clickedPoint.x+","+clickedPoint.y+")");
                    Chess clickedChess = chessBoard.ChessAt(clickedPoint.x, clickedPoint.y);
                    if(selectedChess == null) {
                        if(clickedChess != null && isBlack == clickedChess.isBlack()) {
                            selectedChess = clickedChess;
                            selectedPoint = clickedPoint;
                        }
                    }
                    else{
                        System.out.println("select:"+selectedChess.getName());
                        Step step;
                        step = new Step(stepCnt, isBlack, selectedChess, selectedPoint, clickedPoint, chessBoard);
                        if(step.isLegal()) {
                            System.out.println("move");
                            chessBoard.StepAndRecord(step);
                            selectedChess = null;
                            selectedPoint = null;
                            endMyTurn(step);
                        }
                        else {
                            selectedChess = null;
                            selectedPoint = null;
                        }
                    }
                }
                repaint();
            }
        });

    }

    public void setStateLabel(String info) {
        stateLabel.setText(info);
    }

    public void beginMyTurn(Step step) {
        this.inBlackTurn = this.isBlack;
        this.chessBoard.StepAndRecord(step);
        repaint();
        if(chessBoard.getWinner() == null) {
            this.stateLabel.setText("我方走");
        }
        else {
            this.stateLabel.setText(chessBoard.getWinner()+"胜利");
        }
    }

    private void endMyTurn(Step step) {
        this.inBlackTurn = !this.isBlack;
        SocketMessage msg = new SocketMessage(SocketMessage.Type.STEP, step);
        msg.setSender(this.myAccount);
        msg.setReceiver(this.yourAccount);
        msg.send(socket);
        repaint();
        if(chessBoard.getWinner() == null) {
            this.stateLabel.setText("对方走");
        }
        else {
            this.stateLabel.setText(chessBoard.getWinner()+"胜利");
        }
    }

    private Point getClickedPoint(int x, int y) {
        Point p =new Point();
        p.x = (x - MARGIN ) / SPACE;
        p.y = (y - MARGIN ) / SPACE;
        if(p.x < 0 || p.x > 7 || p.y < 0 || p.y > 7) {
            return null;
        }
        if(this.isBlack) {
            p.x = 7 - p.x;
        }
        else{
            p.y = 7 - p.y;
        }
        return p;
    }

    // 绘制方法
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        String path_board = "pic" + File.separator +  "chessboard.png";
        pic_board = Toolkit.getDefaultToolkit().getImage(path_board);
        System.out.println(pic_board);
        g.drawImage(pic_board, 0, 0, WIDTH, LENGTH,  this);

        drawChesses(g);
        if(selectedPoint != null) {
            paintRect(g, selectedPoint);
        }
    }

    private void drawChesses(Graphics g) {
        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                if(chessBoard.ChessAt(i, j) != null) {
                    paintSingleChess(g, i, j, chessBoard.ChessAt(i, j));
                }
            }
        }
    }

    private void paintSingleChess(Graphics g, int x, int y, Chess chess) {
        int px = !isBlack ? x : (7 - x);
        int py = !isBlack ? (7 - y) : y;
        px = MARGIN + (SPACE * px);
        py = MARGIN + (SPACE * py);
        Image img = Toolkit.getDefaultToolkit().getImage(chess.getImg_path());
        g.drawImage(img, px, py, SIZE, SIZE, this);
    }

    private void paintRect(Graphics g, Point selectedPoint) {
        int x = selectedPoint.x;
        int y = selectedPoint.y;
        int px = !isBlack ? x : (7 - x);
        int py = !isBlack ? (7 - y) : y;
        px = MARGIN + (SPACE * px);
        py = MARGIN + (SPACE * py);
        g.drawRect(px, py, SIZE, SIZE);
    }

    // 按钮控制方法 重来、悔棋、认输、求和、发消息
    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        switch(command) {
            case "Restart":
                restart();
                break;
            case "Regret":
                regert();
                break;
            case "Defeat":
                defeat();
                break;
            case "Exit":
                backToList();
                break;
            case "Send":
                chat();
                break;
        }
    }

    // 发起重新开始请求
    private void restart() {
        SocketMessage msg = new SocketMessage(SocketMessage.Type.RESTART_REQUEST, null);
        msg.setSender(this.myAccount);
        msg.setReceiver(this.yourAccount);
        msg.send(socket);
    }
    // 执行重新开始
    public void restarted() {
        //this.isBlack = !this.isBlack;
        this.inBlackTurn = false;
        this.chessBoard.stdReset();
        repaint();
    }
    // 发起悔棋请求
    private void regert() {
        if(this.isBlack == this.inBlackTurn) {
            this.stateLabel.setText("您的回合不能悔棋");
        }
        else{
            SocketMessage msg = new SocketMessage(SocketMessage.Type.REGRET_REQUEST, null);
            msg.setSender(this.myAccount);
            msg.setReceiver(this.yourAccount);
            msg.send(socket);
        }
    }
    // 执行悔棋操作
    public void regreted() {
        this.inBlackTurn = !this.inBlackTurn;
        chessBoard.regret();
        repaint();
    }

    private void defeat() {
        SocketMessage msg = new SocketMessage(SocketMessage.Type.DEFEAT, null);
        stateLabel.setText("我方认输");
        msg.setSender(this.myAccount);
        msg.setReceiver(this.yourAccount);
        msg.send(socket);
    }

    private void backToList() {
        SocketMessage msg = new SocketMessage(SocketMessage.Type.BACK_TO_LIST, null);
        msg.setSender(this.myAccount);
        msg.setReceiver(this.yourAccount);
        msg.send(socket);
        this.listFrame.setVisible(true);
        this.gameFrame.dispose();
    }

    public void  backToListed() {
        this.listFrame.setVisible(true);
        this.gameFrame.dispose();
    }

    private void chat() {
        String text = this.chatJtf.getText();
        this.chatJta.append(this.myAccount+":"+text+"\n");
        SocketMessage msg = new SocketMessage(SocketMessage.Type.TEXT, text);
        msg.setSender(this.myAccount);
        msg.setReceiver(this.yourAccount);
        msg.send(socket);
    }

    public void chated(SocketMessage msg) {
        String text = (String) msg.getContent();
        String sender = msg.getSender();
        this.chatJta.append(sender+":"+text+"\n");
    }
}


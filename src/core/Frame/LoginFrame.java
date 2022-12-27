package core.Frame;

import core.network.SocketMessage;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.Socket;

public class LoginFrame extends JFrame implements ActionListener {
    public static void main(String[] args) {
        new LoginFrame();
    }

    private Socket socket =null;
    private String account;

    private final JTextField accountFiled;
    private final JTextField ipField;
    private final JTextField portField;

    private JLabel welcome;

    public LoginFrame()  {
        this.setTitle("BUAA国际象棋Online-登录界面");
        this.setSize(900, 675);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLayout(null);
        // 欢迎
        welcome = new JLabel("BUAA CHESS ONLINE");
        welcome.setBounds(385, 100, 140, 30);
        this.add(welcome);
        // 账号栏
        JLabel accountLable = new JLabel("ID：");
        accountLable.setBounds(310, 150, 40, 30);
        this.add(accountLable);
        accountFiled = new JTextField();
        accountFiled.setBounds(350,150,200,30);
        this.add(accountFiled);
        // ip 地址栏
        JLabel ipLable = new JLabel("IP：");
        ipLable.setBounds(310, 200, 40, 30);
        this.add(ipLable);
        ipField = new JTextField("127.0.0.1");
        ipField.setBounds(350, 200, 200, 30);
        this.add(ipField);
        // 端口栏
        JLabel portLabel = new JLabel("端口：");
        portLabel.setBounds(310, 250, 40, 30);
        this.add(portLabel);
        portField = new JTextField("8080");
        portField.setBounds(350, 250, 200, 30);
        this.add(portField);
        // 登录按钮
        JButton login = new JButton("登录");
        login.setBounds(350, 300, 200, 30);
        login.addActionListener(this);
        login.setActionCommand("login");
        this.add(login);
        // 注册按钮
        JButton signin = new JButton("注册");
        signin.setBounds(350, 350, 90, 30);
        signin.addActionListener(this);
        signin.setActionCommand("signin");
        this.add(signin);
        // 忘记密码按钮
        JButton forget = new JButton("忘记密码");
        forget.setBounds(460,350, 90, 30);
        forget.addActionListener(this);
        forget.setActionCommand("forget");
        this.add(forget);

        this.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String actionCommand = e.getActionCommand();
        switch(actionCommand) {
            case "login":
                login();
                break;
            case "signin":
                signin();
                break;
            case"forget":
                forget();
                break;
            default:
                break;
        }
    }

    private void login()  {
        account = accountFiled.getText();
        String ip = ipField.getText();
        String portStr = portField.getText();
        int port = Integer.parseInt(portStr);
        try {
            if(socket == null) {
                socket = new Socket(ip, port);
            }
            SocketMessage msg = new SocketMessage(SocketMessage.Type.LOGIN, account);
            System.out.println(msg.getType());
            msg.send(socket);
            System.out.println("Acount:" + account + "try to login");
            SocketMessage receive = new SocketMessage(socket);
            if(account.length()==0) {
                this.welcome.setText("ID不可为空");
            }
            else if(receive.getType() == SocketMessage.Type.NULL) {
                this.welcome.setText("找不到服务器");
            }
            else if(receive.getType() == SocketMessage.Type.LOGIN_SUCCESS){
                LoginFrame.this.dispose();
                new ListFrame(this);
            }
            else if(receive.getType() == SocketMessage.Type.LOGIN_FAIL) {
                this.welcome.setText("ID被占用");
            }
            else{
                System.out.println("未知原因登录失败");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void signin() {
        JFrame info = new JFrame();
        JLabel label = new JLabel("~直接使用一个ID加入游戏吧~");
        label.setBounds(55, 50, 180, 30);
        info.setTitle("提示");
        info.setLayout(null);
        info.add(label);
        info.setSize(300, 200);
        info.setLocationRelativeTo(null);
        info.setVisible(true);
    }

    private void forget() {
        JFrame info = new JFrame();
        JLabel label = new JLabel("~现在还没有密码功能呢~");
        label.setBounds(75, 50, 150, 30);
        JLabel addr = new JLabel("请期待下一版");
        addr.setBounds(105, 80, 150, 30);
        info.setTitle("提示");
        info.setLayout(null);
        info.add(label);
        info.add(addr);
        info.setSize(300, 200);
        info.setLocationRelativeTo(null);
        info.setVisible(true);
    }

    public String getAccount() {
        return account;
    }

    public Socket getSocket() {
        return socket;
    }
}

package core.network;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Server extends JFrame implements ActionListener {
    public static void main(String[] args) {
        new Server();
    }
    private int port;
    private boolean stalled = false;
    private Map<String, ServerThread> clients = new HashMap<>(); // 账户 + 线程

    private JTextField jtf;
    private JButton jb;
    private JLabel jl;
    private boolean working = false;

    public Server() {
        this.setTitle("BUAA国际象棋Online-服务端");

        this.setSize(400,300);
        this.setLayout(null);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        jl = new JLabel("正在启动服务器，请输入服务端口");
        jl.setBounds(100, 30, 200, 30);
        this.add(jl);

        jtf = new JTextField("8080",20);
        jtf.setBounds(100, 70, 200, 30);
        this.add(jtf);

        jb = new JButton("运行");
        jb.setBounds(150, 120, 100, 30);
        jb.setActionCommand("begin");
        jb.addActionListener(this);
        this.add(jb);

        this.setVisible(true);
    }

    public void start() {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("server begin");
            while(working) {

                Socket socket = serverSocket.accept();
                System.out.println("new connect");
                ServerThread serverThread = new ServerThread(socket);
                System.out.println("new server thread created");
                serverThread.start();
                System.out.println("new thread started");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean isOccupied(int port) {
        boolean occupied;
        try {
            Socket socket = new Socket("127.0.0.1", port);
            occupied = true;        // 可以连接，正在使用
        } catch (IOException e) {
            occupied = false;       // 不能连接，可以使用
        }
        return occupied;
    }

    public void end() {
        Vector<String> list = new Vector<>(clients.keySet());
        for(int i = 0; i < list.capacity(); i++) {
            ServerThread st = clients.get(list.get(i));
            st.end();
        }
        working = false;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd =e.getActionCommand();
        switch (cmd) {
            case "begin":
                if(working) break;
                int port = Integer.parseInt(jtf.getText());
                if (isOccupied(port)) {
                    jl.setText("端口被占用，请重新输入");
                } else {
                    jl.setText("服务器启动成功");
                    jtf.setEditable(false);
                    this.port = port;
                    working = true;
                    start();
                }
                break;
            case "end":
                System.out.println("hhh");
                if(working) {
                    working = false;
                    jtf.setEditable(true);
                    end();
                }
                break;
            default:
                break;
        }

    }


    //////////////////////////////////////////////////////
    public class ServerThread extends Thread {
        private Socket socket;
        private String account;
        private boolean runnning = true;

        public ServerThread(Socket socket) {
            this.socket = socket;
            this.account = null;
        }

        public void end() {
            runnning = false;
        }

        @Override
        public void run() {
            int cnt = 0;
            try {
                while(runnning) {
                    Thread.sleep(500);
                    if(cnt > 10) {
                        if(this.account != null) {
                            clients.remove(this.account);
                        }
                        end();
                        break;
                    }
                    System.out.println("Thread is running");
                    SocketMessage receive = new SocketMessage(socket);
                    System.out.println("New message");
                    switch (receive.getType()) {
                        case LOGIN:
                            cnt = 0;
                            if(stalled) break;
                            System.out.println("登录请求");
                            handle_login(receive);
                            break;
                        case LIST:
                            cnt = 0;
                            if(stalled) break;
                            System.out.println("请求列表");
                            handle_list(receive);
                            break;
                        case INVITE:
                            cnt = 0;
                            if(stalled) break;
                            System.out.println("请求匹配");
                            handle_invite(receive);
                            break;
                        case AGREE:
                            cnt = 0;
                            if(stalled) break;
                            System.out.println("开始对战");
                            handle_agree(receive);
                            break;
                        case STEP:
                            cnt = 0;
                            if(stalled) break;
                            System.out.println(receive.getSender()+"走棋");
                            handle_step(receive);
                            break;
                        case TEXT:
                            cnt = 0;
                            if(stalled) break;
                            System.out.println(receive.getSender()+" 对"+receive.getReceiver()+"说"+receive.getContent());
                            handle_text(receive);
                            break;
                        case RESTART_REQUEST:
                            cnt = 0;
                            if(stalled) break;
                            System.out.println(receive.getSender()+"向"+receive.getReceiver()+"发起重新开始请求");
                            handle_restart_request(receive);
                            break;
                        case RESTART_AGREE:
                            cnt = 0;
                            if(stalled) break;
                            System.out.println(receive.getSender()+" 同意 "+receive.getReceiver()+" 的重来请求");
                            handle_restart_agree(receive);
                            break;
                        case RESTART_REFUSE:
                            cnt = 0;
                            if(stalled) break;
                            System.out.println(receive.getSender()+" 拒绝 "+receive.getReceiver()+" 的悔棋请求");
                            handle_restart_refuse(receive);
                            break;
                        case REGRET_REQUEST:
                            cnt = 0;
                            if(stalled) break;
                            System.out.println(receive.getSender()+" 向 "+receive.getReceiver()+" 请求悔棋");
                            handle_regret_request(receive);
                            break;
                        case REGRET_AGREE:
                            cnt = 0;
                            if(stalled) break;
                            System.out.println(receive.getSender()+" 同意 "+receive.getReceiver()+" 的悔棋请求");
                            handle_regret_agree(receive);
                            break;
                        case REGRET_REFUSE:
                            cnt = 0;
                            if(stalled) break;
                            System.out.println(receive.getSender()+" 拒绝 "+receive.getReceiver()+" 的悔棋请求");
                            handle_regret_refuse(receive);
                            break;
                        case DEFEAT:
                            cnt = 0;
                            if(stalled) break;
                            System.out.println(receive.getSender()+" 向 "+receive.getReceiver()+" 认输");
                            handle_defeat(receive);
                            break;
                        case BACK_TO_LIST:
                            cnt = 0;
                            if(stalled) break;
                            System.out.println(receive.getSender()+" 退出与"+receive.getReceiver()+" 的对战");
                            handle_back_to_list(receive);
                            break;
                        case NULL:
                            cnt++;
                            System.out.println("无效消息:"+receive.getContent()+" form "+receive.getSender()+" to "+receive.getReceiver());
                            handle_null();
                            break;
                        default:
                            cnt++;
                            System.out.println("未知类型消息");
                            break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void handle_login(SocketMessage request) {
            Object content = request.getContent();
            this.account = (String)content;
            if(clients.get(account) == null) {
                System.out.println(account+" login");
                clients.put(account, this);
                SocketMessage response = new SocketMessage(SocketMessage.Type.LOGIN_SUCCESS, null);
                response.send(socket);
            }
            else {
                SocketMessage response = new SocketMessage(SocketMessage.Type.LOGIN_FAIL, null);
                response.send(socket);
            }
        }

        private void handle_list(SocketMessage receive) {
            Vector<String> list = new Vector<>(clients.keySet());
            SocketMessage response = new SocketMessage(SocketMessage.Type.LIST, list);
            response.send(socket);
        }

        private void handle_invite(SocketMessage request) {
            String sender = request.getSender();
            String receiver = request.getReceiver();
            System.out.println("查找"+receiver+"服务线程");
            Socket receiverSocket = clients.get(receiver).getSocket();
            System.out.println("查找成功");
            SocketMessage invite = new SocketMessage(SocketMessage.Type.INVITE,sender);
            invite.setReceiver(receiver);
            invite.setSender(sender);
            invite.send(receiverSocket);
            System.out.println(sender+" 向 "+receiver+" 发起请求 ");
        }

        private void handle_agree(SocketMessage answer) {
            String inviting;
            String invited;
            invited = answer.getSender();
            inviting = answer.getReceiver();
            Socket invitingSocket = clients.get(inviting).getSocket();

            System.out.println(invited+" response to "+ inviting);
            SocketMessage toSender = new SocketMessage(SocketMessage.Type.AGREE, true);
            toSender.setReceiver(invited);
            toSender.setSender(inviting);
            toSender.send(socket);

            SocketMessage toReceiver = new SocketMessage(SocketMessage.Type.AGREE, false);
            toReceiver.setReceiver(inviting);
            toReceiver.setSender(invited);
            toReceiver.send(invitingSocket);
        }

        private void handle_step(SocketMessage step) {
            String receiver = step.getReceiver();
            if(clients.get(receiver)!= null) {
                Socket receiverSocket = clients.get(receiver).getSocket();
                step.send(receiverSocket);
            }
        }

        private void handle_text(SocketMessage msg) {
            String receiver = msg.getReceiver();
            Socket receiverSocket = clients.get(receiver).getSocket();
            msg.send(receiverSocket);
        }

        private void handle_restart_request(SocketMessage msg) {
            String receiver = msg.getReceiver();
            Socket receiverSocket = clients.get(receiver).getSocket();
            msg.send(receiverSocket);
        }

        private void handle_restart_agree(SocketMessage msg) {
            String receiver = msg.getReceiver();
            String sender = msg.getSender();
            Socket receiverSocket = clients.get(receiver).getSocket();
            SocketMessage exe = new SocketMessage(SocketMessage.Type.RESTART_EXCUTE, null);
            exe.send(socket);
            exe.send(receiverSocket);
        }

        private void handle_restart_refuse(SocketMessage msg) {
            String receiver = msg.getReceiver();
            Socket receiverSocket = clients.get(receiver).getSocket();
            msg.send(receiverSocket);
        }

        private void handle_regret_request(SocketMessage msg) {
            String receiver = msg.getReceiver();
            Socket receiverSocket = clients.get(receiver).getSocket();
            msg.send(receiverSocket);
        }

        private void handle_regret_agree(SocketMessage msg) {
            String receiver = msg.getReceiver();
            Socket receiverSocket = clients.get(receiver).getSocket();
            SocketMessage exe = new SocketMessage(SocketMessage.Type.REGRET_EXCUTE, null);
            exe.send(socket);
            exe.send(receiverSocket);
        }

        private void handle_regret_refuse(SocketMessage msg) {
            String receiver = msg.getReceiver();
            Socket receiverSocket = clients.get(receiver).getSocket();
            msg.send(receiverSocket);
        }

        private void handle_defeat(SocketMessage msg) {
            String receiver = msg.getReceiver();
            Socket receiverSocket = clients.get(receiver).getSocket();
            msg.send(receiverSocket);
        }

        private void handle_back_to_list(SocketMessage msg) {
            String receiver = msg.getReceiver();
            Socket receiverSocket = clients.get(receiver).getSocket();
            msg.send(receiverSocket);
        }

        private void handle_null() {
            SocketMessage response = new SocketMessage(SocketMessage.Type.RETRY, null);
            response.send(socket);
        }

        public Socket getSocket() {
            return socket;
        }
    }
}

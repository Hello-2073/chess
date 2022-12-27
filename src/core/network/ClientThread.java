package core.network;

import java.net.Socket;

public class ClientThread extends Thread{
    private Socket socket;
    private boolean isSutdown = false;
    private ClientListenser clientListenser;

    public ClientThread(Socket socket, ClientListenser clientListenser) {
        this.socket = socket;
        this.clientListenser = clientListenser;
        this.start();

    }

    public interface ClientListenser {
        void response(SocketMessage msg);
    }

    @Override
    public void run() {
        while(!isSutdown) {
            SocketMessage msg;
            try {
                msg = new SocketMessage(socket);
                System.out.println(currentThread().getName()+" has new message");
                if(clientListenser != null) {
                    clientListenser.response(msg);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

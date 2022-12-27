package core.Frame;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.Socket;

/*
    这是向用户询问 ”确定“ 还是 ”取消“ 的提示界面
    使用需要重写 yes() 和 no() 函数
    分别表示选择 ”同意“ 与 ”取消时执行的操作“
*/

public class HintFrame extends JFrame implements ActionListener {
    private JLabel hint;
    private JButton yes;
    private JButton no;

    private Socket socket;
    private String sender;
    private String receiver;

    private HintAction hintAction;

    public HintFrame(String info, HintAction hintAction) {

        this.setSize(400, 300);
        this.setLocationRelativeTo(null);
        this.setLayout(null);
        this.setTitle("新请求/消息");

        this.hint = new JLabel(info);
        this.hint.setBounds(120, 80, 200, 30);
        this.add(hint);

        this.yes = new JButton("确定");
        yes.setActionCommand("yes");
        yes.setBounds(80, 160, 90, 30);
        yes.addActionListener(this);
        this.add(yes);

        this.no = new JButton("取消");
        no.setActionCommand("no");
        no.setBounds(210, 160, 90, 30);
        no.addActionListener(this);
        this.add(no);

        this.hintAction = hintAction;

        this.setVisible(true);
    }

    public interface HintAction{
        void yes();
        void no();
    }

    @Override
    public void actionPerformed(ActionEvent e){
        String actionCommand = e.getActionCommand();
        switch(actionCommand) {
            case "yes":
                hintAction.yes();
                this.dispose();
                break;
            case "no":
                hintAction.no();
                this.dispose();
                break;
            default:
                break;
        }
    }
}

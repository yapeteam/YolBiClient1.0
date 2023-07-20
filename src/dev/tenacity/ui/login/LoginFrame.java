package dev.tenacity.ui.login;

import dev.tenacity.ui.login.test.login.LoginCallBack;
import net.minecraft.client.Minecraft;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class LoginFrame extends JFrame {
    private JPanel panel;
    private JButton loginButton;
    private JTextField UsernameField;
    private JPasswordField PasswordField;

    public LoginFrame(LoginCallBack callBack) {
        super("LoginYourAccount");
        add(panel);
        float width = 300, height = 200;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = (int) screenSize.getWidth();
        int screenHeight = (int) screenSize.getHeight();
        int[] size = {(int) (width / 1920 * screenWidth), (int) (height / 1080 * screenHeight)};
        setSize(size[0], size[1]);
        setResizable(false);
        getRootPane().setDefaultButton(loginButton);
        setAlwaysOnTop(true);
        setLocation((int) ((screenWidth - width) / 2), (int) ((screenHeight - height) / 2));
        loginButton.addActionListener(e -> {
            callBack.run(UsernameField.getText(), toString(PasswordField.getPassword()));
        });
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                setVisible(false);
                if (Minecraft.getMinecraft() != null)
                    Minecraft.getMinecraft().shutdown();
                System.exit(0);
            }
        });
    }

    public JButton getLoginButton() {
        return loginButton;
    }

    private String toString(char[] chars) {
        StringBuilder str = new StringBuilder();
        for (char c : chars) {
            str.append(c);
        }
        return str.toString();
    }

    @Override
    public void setVisible(boolean b) {
        this.UsernameField.setText("");
        this.PasswordField.setText("");
        super.setVisible(b);
    }
}

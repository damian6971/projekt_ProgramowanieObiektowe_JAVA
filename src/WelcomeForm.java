import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class WelcomeForm extends JFrame {
    private JButton przejdźDalejButton;
    private JPanel JPanel1;
    private JButton zamknijButton;
    public WelcomeForm(){
        super("Wypożyczalnia sprzętu budowalnego");
        setContentPane(JPanel1);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(680,500);
        setLocationRelativeTo(null);

        setResizable(false);
        przejdźDalejButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MenuForm menuForm = new MenuForm();
                menuForm.setVisible(true);
                dispose();
            }
        });
        zamknijButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();

            }
        });
    }
}

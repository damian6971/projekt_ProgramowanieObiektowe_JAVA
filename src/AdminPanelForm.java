import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AdminPanelForm  extends JFrame{
    private JPanel JPanel1;
    private JButton listasprzętuButton;
    private JButton zarządzajRezerwacjamiButton;
    private JButton użytkownicyButton;
    private JButton wylogujHtmlButton;
    private JButton zamknijHtmlButton;
    private JButton zarządzajwypożyczeniamiButton1;
    private JButton wylogujButton;
    private JButton zamknijButton;

    public AdminPanelForm() {
        super("Panel Administratora");
        setContentPane(JPanel1);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500,500);
        setLocationRelativeTo(null);
        setResizable(false);

        zamknijHtmlButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });


        wylogujHtmlButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                MenuForm menuForm = new MenuForm();
                menuForm.setVisible(true);
            }
        });
        listasprzętuButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                ListaSprzetuForm listaSprzetuForm = new ListaSprzetuForm();
                listaSprzetuForm.setVisible(true);

            }
        });


        zarządzajRezerwacjamiButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                ZarządzanieRezerwacjami zarządzanieRezerwacjami = new ZarządzanieRezerwacjami();
                zarządzanieRezerwacjami.setVisible(true);
            }
        });
        zarządzajwypożyczeniamiButton1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                ZarzadzanieWypozyczeniamiAdmin zarzadzanieWypozyczeniamiAdmin = new ZarzadzanieWypozyczeniamiAdmin();
                zarzadzanieWypozyczeniamiAdmin.setVisible(true);
            }
        });
        użytkownicyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                UzytkownicyAdminForm uzytkownicyAdminForm = new UzytkownicyAdminForm();
                uzytkownicyAdminForm.setVisible(true);
            }
        });
    }


}

import com.sun.source.tree.NewArrayTree;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
public class KlientPanelForm extends JFrame{
    private JPanel JPanel1;
    private JButton rezerwujsprzętButton;
    private JButton mojewypożyczeniaButton;
    private JButton profilMojeDaneButton;
    private JButton wylogujButton;
    private JButton zamknijButton;
    private String nazwa_uzytkownika;

    public KlientPanelForm(String nazwa_uzytkownika){
        super("Klient");
        this.nazwa_uzytkownika = nazwa_uzytkownika;
        setContentPane(JPanel1);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500,500);
        setLocationRelativeTo(null);
        setResizable(false);

        zamknijButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        wylogujButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                MenuForm menuForm = new MenuForm();
                menuForm.setVisible(true);
            }
        });
        profilMojeDaneButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                ProfilUzytkownikaForm profilUzytkownikaForm = new ProfilUzytkownikaForm(nazwa_uzytkownika);
                profilUzytkownikaForm.setVisible(true);

            }
        });

        rezerwujsprzętButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                int idUzytkownika = getIdUzytkownika(nazwa_uzytkownika);
                if (idUzytkownika != -1) {
                    RezerwacjaSprzetuForm rezerwacjaSprzetuForm = new RezerwacjaSprzetuForm(idUzytkownika,nazwa_uzytkownika);
                    rezerwacjaSprzetuForm.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(null, "Nie znaleziono użytkownika w bazie.");
                }

            }
        });
        mojewypożyczeniaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                MojeWypozyczeniaPanelKlientaForm mojeWypozyczeniaPanelKlientaForm = new MojeWypozyczeniaPanelKlientaForm(nazwa_uzytkownika);
                mojeWypozyczeniaPanelKlientaForm.setVisible(true);
            }
        });
    }

    private int getIdUzytkownika(String nazwa_uzytkownika) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/wypozyczalnia", "root", "");
             PreparedStatement stmt = conn.prepareStatement("SELECT id_uzytkownika FROM uzytkownicy WHERE nazwa_uzytkownika = ?")) {

            stmt.setString(1, nazwa_uzytkownika);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id_uzytkownika");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Błąd bazy danych: " + e.getMessage());
        }
        return -1;
    }
}

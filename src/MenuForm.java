import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class MenuForm extends JFrame{
    private JPanel JPanel1;
    private JTextField loginTextField1;
    private JPasswordField passwordField1;
    private JButton zalogujButton1;
    private JButton wsteczButton;
    private JButton zarejestrujButton;
    public MenuForm(){
        super("Panel logowania");
        setContentPane(JPanel1);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500,500);
        setLocationRelativeTo(null);
        setResizable(false);

        wsteczButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                WelcomeForm welcomeForm = new WelcomeForm();
                welcomeForm.setVisible(true);
            }
        });
        zarejestrujButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                registrationForm registrationForm = new registrationForm();
                registrationForm.setVisible(true);
            }
        });
        zalogujButton1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                logowanie();
            }
        });
    }
    private void logowanie() {
        String login = loginTextField1.getText().trim();
        String haslo = new String(passwordField1.getPassword());

        if (login.equals("admin") && haslo.equals("admin")) {
            dispose();
            AdminPanelForm adminPanel = new AdminPanelForm();
            adminPanel.setVisible(true);
        }else{
            try (Connection conn = DatabaseConnection.getConnection()) {
                String sql = "SELECT * FROM uzytkownicy WHERE nazwa_uzytkownika = ? AND haslo = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, login);
                stmt.setString(2, haslo); // w prawdziwej aplikacji hasło powinno być haszowane

                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    dispose();
                    KlientPanelForm klientPanelForm = new KlientPanelForm(login);
                    klientPanelForm.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(this, "Błędny login lub hasło.", "Błąd logowania", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Błąd połączenia z bazą danych: " + ex.getMessage(), "Błąd", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

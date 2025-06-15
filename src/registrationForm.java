import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;


public class registrationForm extends JFrame{
    private JTextField NazwaUzytkownikatextField1;
    private JPasswordField hasloPasswordField1;
    private JPasswordField powtorzHasloPasswordField1;
    private JTextField eMailTextField1;
    private JButton anulujButton;
    private JPanel JPanel1;
    private JButton zarejestrujSięButton;
    private JTextField NumerTeltextField1;
    private JTextField nazwiskoTextField1;
    private JTextField imieTextField1;

    public registrationForm() {
        super("Rejestracja");
        setContentPane(JPanel1);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500,500);
        setLocationRelativeTo(null);
        setResizable(false);

        anulujButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                MenuForm menuForm = new MenuForm();
                menuForm.setVisible(true);
            }
        });
        zarejestrujSięButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                zarejestrujUzytkownika();
            }
        });
    }

    private void zarejestrujUzytkownika() {
        String username = NazwaUzytkownikatextField1.getText().trim();
        String email = eMailTextField1.getText().trim();
        String haslo = String.valueOf(hasloPasswordField1.getPassword()).trim();
        String powtorzHaslo = String.valueOf(powtorzHasloPasswordField1.getPassword()).trim();
        String numerTel = NumerTeltextField1.getText().trim();
        String imie = imieTextField1.getText().trim();
        String nazwisko = nazwiskoTextField1.getText().trim();

        if (username.isEmpty() || email.isEmpty() || haslo.isEmpty() || powtorzHaslo.isEmpty()
                || numerTel.isEmpty() || imie.isEmpty() || nazwisko.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Wszystkie pola muszą być wypełnione!", "Błąd", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!haslo.equals(powtorzHaslo)) {
            JOptionPane.showMessageDialog(this, "Hasła nie są takie same!", "Błąd", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (haslo.length() < 6) {
            JOptionPane.showMessageDialog(this, "Hasło musi mieć co najmniej 6 znaków!", "Błąd", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!email.matches("^[\\w.-]+@[\\w.-]+\\.[a-z]{2,}$")) {
            JOptionPane.showMessageDialog(this, "Niepoprawny adres e-mail!", "Błąd", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!numerTel.matches("^(\\+48)?\\d{9}$")) {
            JOptionPane.showMessageDialog(this, "Niepoprawny numer telefonu! Musi mieć 9 cyfr (lub +48...)", "Błąd", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            String checkSql = "SELECT COUNT(*) FROM uzytkownicy WHERE nazwa_uzytkownika = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setString(1, username);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                JOptionPane.showMessageDialog(this, "Użytkownik o podanej nazwie już istnieje!", "Błąd", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String sql = "INSERT INTO uzytkownicy (nazwa_uzytkownika, haslo, email, telefon, imie, nazwisko) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, haslo);
            stmt.setString(3, email);
            stmt.setString(4, numerTel);
            stmt.setString(5, imie);
            stmt.setString(6, nazwisko);
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Rejestracja zakończona sukcesem!");
            dispose();
            MenuForm menuForm = new MenuForm();
            menuForm.setVisible(true);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Błąd podczas rejestracji: " + ex.getMessage(), "Błąd", JOptionPane.ERROR_MESSAGE);
        }
    }
}

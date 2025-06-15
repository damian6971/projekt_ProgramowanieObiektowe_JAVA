import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class ProfilUzytkownikaForm extends JFrame{
    private JPanel JPanel1;
    private JTextField NazwaUzytkownikatextField1;
    private JPasswordField hasloPasswordField1;
    private JPasswordField powtorzHasloPasswordField1;
    private JTextField eMailTextField1;
    private JTextField NumerTeltextField1;
    private JTextField nazwiskoTextField1;
    private JTextField imieTextField1;
    private JButton zapiszZmianyButton;
    private JButton anulujButton;
    private JButton usuńKontoButton;
    private String staraNazwaUzytkownika;
    public ProfilUzytkownikaForm(String nazwa_uzytkownika) {
        super("Moje dane");
        this.staraNazwaUzytkownika = nazwa_uzytkownika;
        setContentPane(JPanel1);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500,500);
        setLocationRelativeTo(null);
        setResizable(false);
        wczytajDaneUzytkownika(nazwa_uzytkownika);


        anulujButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                KlientPanelForm klientPanelForm = new KlientPanelForm(staraNazwaUzytkownika);
                klientPanelForm.setVisible(true);
            }
        });
        usuńKontoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                usunKonto();
            }
        });
        zapiszZmianyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                zapiszZmiany();
                KlientPanelForm klientPanelForm = new KlientPanelForm(staraNazwaUzytkownika);
            }
        });
    }
    private void wczytajDaneUzytkownika(String nazwa_uzytkownika) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT imie, nazwisko, email, telefon, nazwa_uzytkownika, haslo FROM uzytkownicy WHERE nazwa_uzytkownika = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, nazwa_uzytkownika);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Uzytkownik u = new Uzytkownik();
                u.setImie(rs.getString("imie"));
                u.setNazwisko(rs.getString("nazwisko"));
                u.setEmail(rs.getString("email"));
                u.setTelefon(rs.getString("telefon"));
                u.setNazwaUzytkownika(rs.getString("nazwa_uzytkownika"));
                u.setHaslo(rs.getString("haslo"));

                imieTextField1.setText(u.getImie());
                nazwiskoTextField1.setText(u.getNazwisko());
                eMailTextField1.setText(u.getEmail());
                NumerTeltextField1.setText(u.getTelefon());
                NazwaUzytkownikatextField1.setText(u.getNazwaUzytkownika());
                hasloPasswordField1.setText(u.getHaslo());
                powtorzHasloPasswordField1.setText(u.getHaslo());
            } else {
                JOptionPane.showMessageDialog(this, "Nie znaleziono użytkownika!", "Błąd", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Błąd wczytywania danych: " + ex.getMessage(), "Błąd", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void usunKonto() {
        String username = NazwaUzytkownikatextField1.getText().trim();

        int potwierdzenie = JOptionPane.showConfirmDialog(this,
                "Czy na pewno chcesz usunąć swoje konto?",
                "Potwierdzenie usunięcia",
                JOptionPane.YES_NO_OPTION);
        if (potwierdzenie == JOptionPane.YES_OPTION) {
            try (Connection conn = DatabaseConnection.getConnection()) {
                String sql = "DELETE FROM uzytkownicy WHERE nazwa_uzytkownika = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, username);
                int deleted = stmt.executeUpdate();
                if (deleted > 0) {
                    JOptionPane.showMessageDialog(this, "Konto zostało usunięte.");
                    dispose(); // zamknij profil
                    new MenuForm().setVisible(true); // wróć do logowania
                } else {
                    JOptionPane.showMessageDialog(this, "Nie udało się usunąć konta.", "Błąd", JOptionPane.ERROR_MESSAGE);
                }

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Błąd podczas usuwania konta: " + ex.getMessage(), "Błąd", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    private void zapiszZmiany() {
        Uzytkownik u = new Uzytkownik();
        u.setNazwaUzytkownika(NazwaUzytkownikatextField1.getText().trim());
        u.setEmail(eMailTextField1.getText().trim());
        u.setHaslo(String.valueOf(hasloPasswordField1.getPassword()).trim());
        String powtorzHaslo = String.valueOf(powtorzHasloPasswordField1.getPassword()).trim();
        u.setTelefon(NumerTeltextField1.getText().trim());
        u.setImie(imieTextField1.getText().trim());
        u.setNazwisko(nazwiskoTextField1.getText().trim());

        if (u.getNazwaUzytkownika().isEmpty() || u.getEmail().isEmpty() || u.getHaslo().isEmpty()
                || powtorzHaslo.isEmpty() || u.getTelefon().isEmpty() || u.getImie().isEmpty() || u.getNazwisko().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Wszystkie pola muszą być wypełnione!", "Błąd", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!u.getHaslo().equals(powtorzHaslo)) {
            JOptionPane.showMessageDialog(this, "Hasła się nie zgadzają!", "Błąd", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (u.getHaslo().length() < 6) {
            JOptionPane.showMessageDialog(this, "Hasło musi mieć co najmniej 6 znaków!", "Błąd", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!u.getEmail().matches("^[\\w.-]+@[\\w.-]+\\.[a-z]{2,}$")) {
            JOptionPane.showMessageDialog(this, "Niepoprawny adres e-mail!", "Błąd", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!u.getTelefon().matches("^(\\+48)?\\d{9}$")) {
            JOptionPane.showMessageDialog(this, "Niepoprawny numer telefonu!", "Błąd", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            String checkSql = "SELECT COUNT(*) FROM uzytkownicy WHERE nazwa_uzytkownika = ? AND nazwa_uzytkownika != ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setString(1, u.getNazwaUzytkownika());
            checkStmt.setString(2, staraNazwaUzytkownika); // ważne!
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {
                JOptionPane.showMessageDialog(this, "Nazwa użytkownika jest już zajęta!", "Błąd", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String sql = "UPDATE uzytkownicy SET nazwa_uzytkownika = ?, haslo = ?, email = ?, telefon = ?, imie = ?, nazwisko = ? WHERE nazwa_uzytkownika = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, u.getNazwaUzytkownika());
            stmt.setString(2, u.getHaslo());
            stmt.setString(3, u.getEmail());
            stmt.setString(4, u.getTelefon());
            stmt.setString(5, u.getImie());
            stmt.setString(6, u.getNazwisko());
            stmt.setString(7, staraNazwaUzytkownika);

            int updated = stmt.executeUpdate();
            if (updated > 0) {
                staraNazwaUzytkownika = u.getNazwaUzytkownika();
                JOptionPane.showMessageDialog(this, "Dane zostały zapisane.");
                dispose();
                new KlientPanelForm(staraNazwaUzytkownika).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Nie udało się zapisać zmian.", "Błąd", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Błąd podczas zapisu: " + ex.getMessage(), "Błąd", JOptionPane.ERROR_MESSAGE);
        }
    }
}



import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class DodawanieEdycjaSprzetu extends JFrame {


    private JPanel JPanel1;
    private JTextField ProducentUzytkownikatextField1;
    private JTextField CenaGodzinaTextField1;
    private JTextField nazwaTextField1;
    private JTextField IDTextField1;
    private JButton zapiszEdycjeButton;
    private JButton dodajSprzetButton;
    private JTextField CenaDzientextField2;
    private JComboBox TypcomboBox1;
    private JComboBox DostepnosccomboBox1;
    private JButton wrócButton;

    public DodawanieEdycjaSprzetu()  {
        super("Dodawanie i Edycja sprzętu");
        setContentPane(JPanel1);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500,500);
        setLocationRelativeTo(null);
        setResizable(false);


        dodajSprzetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dodajSprzet();
            }
        });
        zapiszEdycjeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                zapiszEdycjeSprzetu();
            }
        });
        wrócButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                ListaSprzetuForm listaSprzetuForm = new ListaSprzetuForm();
                listaSprzetuForm.setVisible(true);
            }
        });
    }



    private void dodajSprzet() {
        String id = IDTextField1.getText().trim();
        String nazwa = nazwaTextField1.getText().trim();
        String producent = ProducentUzytkownikatextField1.getText().trim();
        String cenaDzienText = CenaDzientextField2.getText().trim();
        String cenaGodzinaText = CenaGodzinaTextField1.getText().trim();
        String typ = TypcomboBox1.getSelectedItem().toString().trim();
        String dostepnoscText = DostepnosccomboBox1.getSelectedItem().toString().trim();

        if (nazwa.isEmpty() || producent.isEmpty() || cenaDzienText.isEmpty() || cenaGodzinaText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Wszystkie pola muszą być wypełnione!", "Błąd", JOptionPane.ERROR_MESSAGE);
            return;
        }

        double cenaDzien, cenaGodzina;
        try {
            cenaDzien = Double.parseDouble(cenaDzienText);
            cenaGodzina = Double.parseDouble(cenaGodzinaText);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Cena musi być poprawną liczbą!", "Błąd", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean dostepnosc = dostepnoscText.equalsIgnoreCase("Dostępny");

        Sprzet sprzet;
        switch (typ.toLowerCase()) {
            case "ciężki":
                sprzet = new SprzetCiezki(0, nazwa, producent, dostepnosc, cenaDzien, cenaGodzina);
                break;
            case "średni":
                sprzet = new SprzetSredni(0, nazwa, producent, dostepnosc, cenaDzien, cenaGodzina);
                break;
            case "lekki":
                sprzet = new SprzetLekki(0, nazwa, producent, dostepnosc, cenaDzien, cenaGodzina);
                break;
            default:
                sprzet = new Sprzet(0, nazwa, producent, dostepnosc, cenaDzien, cenaGodzina);
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "INSERT INTO sprzet (nazwa, typ, producent, cena_za_dzien, cena_za_godzine, dostepnosc) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, sprzet.nazwa);
            stmt.setString(2, typ);
            stmt.setString(3, sprzet.producent);
            stmt.setDouble(4, sprzet.cenaZaDzien);
            stmt.setDouble(5, sprzet.cenaZaGodzine);
            stmt.setBoolean(6, sprzet.dostepnosc);

            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Sprzęt został dodany pomyślnie!");
            dispose();
            new ListaSprzetuForm().setVisible(true);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Błąd podczas dodawania sprzętu: " + ex.getMessage(), "Błąd", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void zapiszEdycjeSprzetu() {
        String idText = IDTextField1.getText().trim();
        String nazwa = nazwaTextField1.getText().trim();
        String producent = ProducentUzytkownikatextField1.getText().trim();
        String cenaDzienText = CenaDzientextField2.getText().trim();
        String cenaGodzinaText = CenaGodzinaTextField1.getText().trim();
        String typ = TypcomboBox1.getSelectedItem().toString().trim();
        String dostepnoscText = DostepnosccomboBox1.getSelectedItem().toString().trim();

        if (idText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "ID sprzętu musi być podane do edycji!", "Błąd", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int id;
        try {
            id = Integer.parseInt(idText);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "ID musi być liczbą całkowitą!", "Błąd", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (nazwa.isEmpty() || producent.isEmpty() || cenaDzienText.isEmpty() || cenaGodzinaText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Wszystkie pola muszą być wypełnione!", "Błąd", JOptionPane.ERROR_MESSAGE);
            return;
        }

        double cenaDzien, cenaGodzina;
        try {
            cenaDzien = Double.parseDouble(cenaDzienText);
            cenaGodzina = Double.parseDouble(cenaGodzinaText);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Ceny muszą być poprawnymi liczbami!", "Błąd", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean dostepnosc;
        if (dostepnoscText.equalsIgnoreCase("Dostępny")) {
            dostepnosc = true;
        } else if (dostepnoscText.equalsIgnoreCase("Niedostępny")) {
            dostepnosc = false;
        } else {
            JOptionPane.showMessageDialog(this, "Dostępność musi być 'Dostępny' lub 'Niedostępny'", "Błąd", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Sprzet sprzet;
        switch (typ.toLowerCase()) {
            case "ciężki":
                sprzet = new SprzetCiezki(id, nazwa, producent, dostepnosc, cenaDzien, cenaGodzina);
                break;
            case "średni":
                sprzet = new SprzetSredni(id, nazwa, producent, dostepnosc, cenaDzien, cenaGodzina);
                break;
            case "lekki":
                sprzet = new SprzetLekki(id, nazwa, producent, dostepnosc, cenaDzien, cenaGodzina);
                break;
            default:
                sprzet = new Sprzet(id, nazwa, producent, dostepnosc, cenaDzien, cenaGodzina);
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "UPDATE sprzet SET nazwa = ?, typ = ?, producent = ?, cena_za_dzien = ?, cena_za_godzine = ?, dostepnosc = ? WHERE id_sprzetu = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, sprzet.nazwa);
            stmt.setString(2, typ); // typ jako tekst
            stmt.setString(3, sprzet.producent);
            stmt.setDouble(4, sprzet.cenaZaDzien);
            stmt.setDouble(5, sprzet.cenaZaGodzine);
            stmt.setBoolean(6, sprzet.dostepnosc);
            stmt.setInt(7, sprzet.id);

            int updated = stmt.executeUpdate();

            if (updated > 0) {
                JOptionPane.showMessageDialog(this, "Sprzęt został pomyślnie zaktualizowany!");
                dispose();
                new ListaSprzetuForm().setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Nie znaleziono sprzętu o podanym ID!", "Błąd", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Błąd podczas edycji sprzętu: " + ex.getMessage(), "Błąd", JOptionPane.ERROR_MESSAGE);
        }
    }
}



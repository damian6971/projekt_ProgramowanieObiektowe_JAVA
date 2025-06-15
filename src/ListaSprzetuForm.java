import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class ListaSprzetuForm extends JFrame {
    private JPanel JPanel1;
    private JComboBox DostepnosccomboBox;
    private JComboBox TypcomboBox;
    private JComboBox CenacomboBox;
    private JButton wróćButton;
    private JButton dodajEdytujSprzętButton;
    private JButton usuńWybranyButton;
    private JTextField IDdoUsunieciatextField1;
    private JTable table1;
    public ListaSprzetuForm(){
        super("Sprzęt");
        setContentPane(JPanel1);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700,500);
        setLocationRelativeTo(null);
        setResizable(false);
        zaladujSprzet();

        DostepnosccomboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                zaladujSprzet();
            }
        });
        TypcomboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                zaladujSprzet();
            }
        });
        CenacomboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                zaladujSprzet();
            }
        });
        usuńWybranyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                usunSprzetPoId();
            }
        });
        dodajEdytujSprzętButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                DodawanieEdycjaSprzetu dodawanieEdycjaSprzetu = new DodawanieEdycjaSprzetu();
                dodawanieEdycjaSprzetu.setVisible(true);
            }
        });
        wróćButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                AdminPanelForm adminPanelForm = new AdminPanelForm();
                adminPanelForm.setVisible(true);
            }
        });
        table1.setRowHeight(30);
        table1.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table1.setShowGrid(true);
        table1.setGridColor(new Color(220, 220, 220));
        table1.setIntercellSpacing(new Dimension(1, 1));
        table1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table1.setSelectionBackground(new Color(184, 207, 229));
        table1.setSelectionForeground(Color.BLACK);
        table1.setFillsViewportHeight(true);
        table1.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        JTableHeader header = table1.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(new Color(240, 240, 240));
        header.setForeground(Color.BLACK);
        header.setReorderingAllowed(false);
    }
    private void zaladujSprzet() {
        DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[]{"ID", "Nazwa", "Producent", "Typ", "Dostępność", "Cena/Dzień"});

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = buildQuery();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id_sprzetu");
                String nazwa = rs.getString("nazwa");
                String producent = rs.getString("producent");
                String typ = rs.getString("typ"); // zakładamy: ciężki/średni/lekki
                boolean dostepnosc = rs.getBoolean("dostepnosc");
                double cenaDzien = rs.getDouble("cena_za_dzien");
                double cenaGodz = rs.getDouble("cena_za_godzine");

                Sprzet sprzet;
                switch (typ.toLowerCase()) {
                    case "ciężki":
                        sprzet = new SprzetCiezki(id, nazwa, producent, dostepnosc, cenaDzien, cenaGodz);
                        break;
                    case "średni":
                        sprzet = new SprzetSredni(id, nazwa, producent, dostepnosc, cenaDzien, cenaGodz);
                        break;
                    case "lekki":
                        sprzet = new SprzetLekki(id, nazwa, producent, dostepnosc, cenaDzien, cenaGodz);
                        break;
                    default:
                        sprzet = new Sprzet(id, nazwa, producent, dostepnosc, cenaDzien, cenaGodz); // awaryjnie
                }
                model.addRow(new Object[]{
                        id,
                        nazwa,
                        producent,
                        sprzet.getTypOpisowy(), // nadpisana metoda
                        dostepnosc ? "Tak" : "Nie",
                        cenaDzien
                });
            }
            table1.setModel(model);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Błąd podczas ładowania sprzętu: " + e.getMessage());
        }
    }
    private String buildQuery() {
        String baseQuery = "SELECT * FROM sprzet WHERE 1=1";

        String dostepnosc = DostepnosccomboBox.getSelectedItem().toString();
        if (dostepnosc.equals("Dostępny")) {
            baseQuery += " AND dostepnosc = 1";
        } else if (dostepnosc.equals("Niedostępny")) {
            baseQuery += " AND dostepnosc = 0";
        }

        String typ = TypcomboBox.getSelectedItem().toString();
        if (!typ.equals("Wszystkie")) { // <- poprawiona wartość
            baseQuery += " AND typ = '" + typ + "'";
        }

        String cenaSort = CenacomboBox.getSelectedItem().toString();
        if (cenaSort.equals("Cena rosnąco")) {
            baseQuery += " ORDER BY cena_za_dzien ASC";
        } else if (cenaSort.equals("Cena malejąco")) {
            baseQuery += " ORDER BY cena_za_dzien DESC";
        }
        return baseQuery;
    }
    private void usunSprzetPoId() {
        String idText = IDdoUsunieciatextField1.getText().trim();

        if (idText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Podaj ID sprzętu do usunięcia.");
            return;
        }
        try {
            int id = Integer.parseInt(idText);

            int potwierdzenie = JOptionPane.showConfirmDialog(this,
                    "Czy na pewno chcesz usunąć sprzęt o ID: " + id + "?",
                    "Potwierdzenie usunięcia",
                    JOptionPane.YES_NO_OPTION);

            if (potwierdzenie == JOptionPane.YES_OPTION) {
                try (Connection conn = DatabaseConnection.getConnection()) {
                    String sql = "DELETE FROM sprzet WHERE id_sprzetu = ?";
                    PreparedStatement stmt = conn.prepareStatement(sql);
                    stmt.setInt(1, id);

                    int deleted = stmt.executeUpdate();

                    if (deleted > 0) {
                        JOptionPane.showMessageDialog(this, "Sprzęt został usunięty.");
                        IDdoUsunieciatextField1.setText("");
                        zaladujSprzet();
                    } else {
                        JOptionPane.showMessageDialog(this, "Nie znaleziono sprzętu o podanym ID.", "Błąd", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (SQLException ex) {
                    if (ex.getMessage().contains("a foreign key constraint fails")) {
                        JOptionPane.showMessageDialog(this, "Nie można usunąć sprzętu – jest powiązany z rezerwacją.", "Błąd", JOptionPane.ERROR_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this, "Błąd podczas usuwania sprzętu: " + ex.getMessage(), "Błąd", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "ID musi być liczbą całkowitą.");
        }
    }
}

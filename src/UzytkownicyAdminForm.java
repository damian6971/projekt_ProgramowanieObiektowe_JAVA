import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
public class UzytkownicyAdminForm extends JFrame{
    private JPanel JPanel1;
    private JTable table1;
    private JButton wróćButton;
    private JButton usuńKontoButton;
    public UzytkownicyAdminForm() {
        super("Użytkownicy");
        this.setContentPane(this.JPanel1);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setSize(800, 600);
        this.setLocationRelativeTo(null);
        loadUzytkownicy();
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
        usuńKontoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                usunWybranegoUzytkownika();
            }
        });
    }
    private void loadUzytkownicy() {
        DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[]{
                "ID", "Nazwa użytkownika", "Hasło", "Email", "Telefon", "Imię", "Nazwisko"
        });
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM uzytkownicy";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id_uzytkownika");

                Uzytkownik uzytkownik = new Uzytkownik(
                        rs.getString("imie"),
                        rs.getString("nazwisko"),
                        rs.getString("telefon"),
                        rs.getString("nazwa_uzytkownika"),
                        rs.getString("haslo"),
                        rs.getString("email")
                );
                model.addRow(new Object[]{
                        id,
                        uzytkownik.getNazwaUzytkownika(),
                        uzytkownik.getHaslo(),
                        uzytkownik.getEmail(),
                        uzytkownik.getTelefon(),
                        uzytkownik.getImie(),
                        uzytkownik.getNazwisko()
                });
            }
            table1.setModel(model);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Błąd podczas ładowania użytkowników: " + e.getMessage());
        }
    }
    private void usunWybranegoUzytkownika() {
        int selectedRow = table1.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Wybierz użytkownika do usunięcia.");
            return;
        }
        String nazwaUzytkownika = table1.getValueAt(selectedRow, 1).toString();

        int potwierdzenie = JOptionPane.showConfirmDialog(this,
                "Czy na pewno chcesz usunąć użytkownika: " + nazwaUzytkownika + "?",
                "Potwierdzenie usunięcia",
                JOptionPane.YES_NO_OPTION);
        if (potwierdzenie == JOptionPane.YES_OPTION) {
            try (Connection conn = DatabaseConnection.getConnection()) {
                String sql = "DELETE FROM uzytkownicy WHERE nazwa_uzytkownika = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, nazwaUzytkownika);

                int deleted = stmt.executeUpdate();
                if (deleted > 0) {
                    JOptionPane.showMessageDialog(this, "Użytkownik został usunięty.");
                    loadUzytkownicy();
                } else {
                    JOptionPane.showMessageDialog(this, "Nie udało się usunąć użytkownika.", "Błąd", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Błąd podczas usuwania użytkownika: " + ex.getMessage(), "Błąd", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}


import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;
import java.sql.Date;
public class ZarządzanieRezerwacjami extends JFrame{
    private JPanel JPanel1;
    private JButton zaakceptujButton;
    private JButton odrzućButton;
    private JButton wróćButton;
    private JTable table1;
    public ZarządzanieRezerwacjami(){
        super("Zarządzanie rezerwacjami-Administrator");
        setContentPane(JPanel1);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(650, 500);
        setLocationRelativeTo(null);
        setResizable(false);
        loadOczekujaceRezerwacje();
        setVisible(true);

        zaakceptujButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                zaakceptujRezerwacje();
            }
        });
        odrzućButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                odrzucRezerwacje();
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
    private void loadOczekujaceRezerwacje() {
        DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(new Object[]{"ID rezerwacji", "ID sprzętu", "Użytkownik", "Data od", "Data do", "Kwota"});
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/wypozyczalnia", "root", "");
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT r.id_rezerwacji, r.id_sprzetu, u.nazwa_uzytkownika, r.data_od, r.data_do, r.kwota " +
                             "FROM rezerwacje r JOIN uzytkownicy u ON r.id_uzytkownika = u.id_uzytkownika " +
                             "WHERE r.status = 'oczekuje'")) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id_rezerwacji"),
                        rs.getInt("id_sprzetu"),
                        rs.getString("nazwa_uzytkownika"),
                        rs.getDate("data_od"),
                        rs.getDate("data_do"),
                        rs.getDouble("kwota")
                });
            }
            table1.setModel(model);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Błąd ładowania rezerwacji: " + ex.getMessage());
        }
    }
    private void zaakceptujRezerwacje() {
        int selectedRow = table1.getSelectedRow();
        if (selectedRow == -1) return;

        int idRezerwacji = (int) table1.getValueAt(selectedRow, 0);

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/wypozyczalnia", "root", "")) {
            conn.setAutoCommit(false);

            PreparedStatement getInfo = conn.prepareStatement("SELECT * FROM rezerwacje WHERE id_rezerwacji = ?");
            getInfo.setInt(1, idRezerwacji);
            ResultSet rs = getInfo.executeQuery();
            if (rs.next()) {
                int idSprzetu = rs.getInt("id_sprzetu");
                int idUzytkownika = rs.getInt("id_uzytkownika");
                Date dataOd = rs.getDate("data_od");
                Date dataDo = rs.getDate("data_do");
                double kwota = rs.getDouble("kwota");

                PreparedStatement updateStatus = conn.prepareStatement("UPDATE rezerwacje SET status = 'zaakceptowana' WHERE id_rezerwacji = ?");
                updateStatus.setInt(1, idRezerwacji);
                updateStatus.executeUpdate();

                PreparedStatement insertWypozyczenie = conn.prepareStatement("INSERT INTO wypozyczenia (id_rezerwacji, id_uzytkownika, id_sprzetu, data_od, data_do, kwota) VALUES (?, ?, ?, ?, ?, ?)");
                insertWypozyczenie.setInt(1, idRezerwacji);
                insertWypozyczenie.setInt(2, idUzytkownika);
                insertWypozyczenie.setInt(3, idSprzetu);
                insertWypozyczenie.setDate(4, dataOd);
                insertWypozyczenie.setDate(5, dataDo);
                insertWypozyczenie.setDouble(6, kwota);
                insertWypozyczenie.executeUpdate();

                PreparedStatement updateSprzet = conn.prepareStatement("UPDATE sprzet SET dostepnosc = 0 WHERE id_sprzetu = ?");
                updateSprzet.setInt(1, idSprzetu);
                updateSprzet.executeUpdate();
                conn.commit();
                JOptionPane.showMessageDialog(this, "Rezerwacja zaakceptowana.");
            }
            loadOczekujaceRezerwacje();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Błąd akceptacji rezerwacji: " + ex.getMessage());
        }
    }
    private void odrzucRezerwacje() {
        int selectedRow = table1.getSelectedRow();
        if (selectedRow == -1) return;
        int idRezerwacji = (int) table1.getValueAt(selectedRow, 0);
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/wypozyczalnia", "root", "");
             PreparedStatement stmt = conn.prepareStatement("UPDATE rezerwacje SET status = 'odrzucona' WHERE id_rezerwacji = ?")) {
            stmt.setInt(1, idRezerwacji);
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Rezerwacja odrzucona.");
            loadOczekujaceRezerwacje();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Błąd odrzucenia rezerwacji: " + ex.getMessage());
        }
    }
}

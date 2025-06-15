import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import com.itextpdf.text.Document;
import java.awt.*;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.io.FileOutputStream;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
public class ZarzadzanieWypozyczeniamiAdmin extends JFrame {
    private JPanel JPanel1;
    private JTable table1;
    private JButton wygenerujParagonButton;
    private JButton usuńZaznaczonąButton;
    private JButton wróćButton;
    public ZarzadzanieWypozyczeniamiAdmin(){
        super("Zarządzanie Wypożyczeniami");
        setContentPane(JPanel1);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700,500);
        setLocationRelativeTo(null);
        setResizable(false);
        loadWypozyczenia();
        wróćButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                AdminPanelForm adminPanelForm = new AdminPanelForm();
                adminPanelForm.setVisible(true);
            }
        });
        wygenerujParagonButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generujParagonDlaZaznaczonego();
            }
        });
        usuńZaznaczonąButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                usunZaznaczoneWypozyczenie();
            }
        });
        table1.setRowHeight(30);
        table1.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 14));
        table1.setShowGrid(true);
        table1.setGridColor(new Color(220, 220, 220));
        table1.setIntercellSpacing(new Dimension(1, 1));
        table1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table1.setSelectionBackground(new Color(184, 207, 229));
        table1.setSelectionForeground(Color.BLACK);
        table1.setFillsViewportHeight(true);
        table1.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        JTableHeader header = table1.getTableHeader();
        header.setFont(new java.awt.Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(new Color(240, 240, 240));
        header.setForeground(Color.BLACK);
        header.setReorderingAllowed(false);
    }
    private void loadWypozyczenia() {
        String[] columns = {"ID", "Użytkownik", "Sprzęt", "Kwota", "Data od", "Data do"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        String sql = "SELECT w.id_wypozyczenia, u.nazwa_uzytkownika, u.imie, u.nazwisko, u.telefon, " + "s.nazwa AS sprzet, s.producent, w.kwota, w.data_od, w.data_do " + "FROM wypozyczenia w " + "JOIN uzytkownicy u ON w.id_uzytkownika = u.id_uzytkownika " + "JOIN sprzet s ON w.id_sprzetu = s.id_sprzetu";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Uzytkownik u = new Uzytkownik(
                        rs.getString("imie"),
                        rs.getString("nazwisko"),
                        rs.getString("telefon"),
                        rs.getString("nazwa_uzytkownika"),
                        "",
                        ""
                );
                Sprzet s = new Sprzet(
                        0,
                        rs.getString("sprzet"),
                        rs.getString("producent"),
                        true,
                        0.0,
                        0.0
                );
                model.addRow(new Object[]{
                        rs.getInt("id_wypozyczenia"),
                        u.getNazwaUzytkownika(),
                        s.getNazwa(),
                        String.format("%.2f zł", rs.getDouble("kwota")),
                        rs.getDate("data_od"),
                        rs.getDate("data_do")
                });
            }
            table1.setModel(model);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Błąd ładowania danych: " + ex.getMessage());
        }
    }
    private void generujParagonDlaZaznaczonego() {
        int selectedRow = table1.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Wybierz wypożyczenie.");
            return;
        }
        String nazwa_uzytkownika = (String) table1.getValueAt(selectedRow, 1);

        try (Connection conn = DatabaseConnection.getConnection()) {
            String userSql = "SELECT imie, nazwisko, telefon FROM uzytkownicy WHERE nazwa_uzytkownika = ?";
            PreparedStatement userStmt = conn.prepareStatement(userSql);
            userStmt.setString(1, nazwa_uzytkownika);
            ResultSet userRs = userStmt.executeQuery();

            String imie = "", nazwisko = "", telefon = "";
            if (userRs.next()) {
                imie = userRs.getString("imie");
                nazwisko = userRs.getString("nazwisko");
                telefon = userRs.getString("telefon");
            }
            String wypSql = "SELECT s.nazwa AS sprzet, w.data_od, w.data_do, w.kwota AS cena " +
                    "FROM wypozyczenia w " +
                    "JOIN sprzet s ON w.id_sprzetu = s.id_sprzetu " +
                    "JOIN uzytkownicy u ON w.id_uzytkownika = u.id_uzytkownika " +
                    "WHERE u.nazwa_uzytkownika = ?";
            PreparedStatement wypStmt = conn.prepareStatement(wypSql);
            wypStmt.setString(1, nazwa_uzytkownika);
            ResultSet wypRs = wypStmt.executeQuery();

            Document document = new Document();
            String fileName = "paragon_" + nazwa_uzytkownika + ".pdf";
            PdfWriter.getInstance(document, new FileOutputStream(fileName));
            document.open();

            document.add(new Paragraph("Paragon"));
            document.add(new Paragraph("Klient: " + imie + " " + nazwisko));
            document.add(new Paragraph("Telefon: " + telefon));
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(4);
            table.addCell("Sprzęt");
            table.addCell("Data od");
            table.addCell("Data do");
            table.addCell("Cena (zł)");

            double suma = 0;
            while (wypRs.next()) {
                table.addCell(wypRs.getString("sprzet"));
                table.addCell(wypRs.getDate("data_od").toString());
                table.addCell(wypRs.getDate("data_do").toString());
                double cena = wypRs.getDouble("cena");
                table.addCell(String.format("%.2f zł", cena));
                suma += cena;
            }
            document.add(table);
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Suma do zapłaty: " + String.format("%.2f zł", suma)));
            document.close();
            JOptionPane.showMessageDialog(this, "Paragon zapisano jako " + fileName);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Błąd PDF: " + ex.getMessage());
        }
    }
    private void usunZaznaczoneWypozyczenie() {
        int selectedRow = table1.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Wybierz wypożyczenie do usunięcia.");
            return;
        }
        int idWypozyczenia = (int) table1.getValueAt(selectedRow, 0);

        int confirm = JOptionPane.showConfirmDialog(this, "Czy na pewno chcesz usunąć wypożyczenie ID: " + idWypozyczenia + "?", "Potwierdzenie usunięcia", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM wypozyczenia WHERE id_wypozyczenia = ?")) {
            stmt.setInt(1, idWypozyczenia);
            int deleted = stmt.executeUpdate();
            if (deleted > 0) {
                JOptionPane.showMessageDialog(this, "Usunięto wypożyczenie.");
                loadWypozyczenia();
            } else {
                JOptionPane.showMessageDialog(this, "Nie znaleziono wypożyczenia do usunięcia.");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Błąd podczas usuwania: " + ex.getMessage());
        }
    }
}

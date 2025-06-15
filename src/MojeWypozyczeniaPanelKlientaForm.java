import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileOutputStream;
import java.sql.*;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
public class MojeWypozyczeniaPanelKlientaForm extends JFrame{
    private JPanel JPanel1;
    private JTable table1;
    private JButton generujParagonButton;
    private JButton wróćButton;
    private JScrollPane wypozyczeniaScrollPane;
    private String nazwa_uzytkownika;
    public MojeWypozyczeniaPanelKlientaForm(String nazwa_uzytkownika) {
        super("Moje wypożyczenia");
        this.nazwa_uzytkownika = nazwa_uzytkownika;
        setContentPane(JPanel1);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(650,500);
        setLocationRelativeTo(null);
        setResizable(false);
        loadWypozyczenia();

        wróćButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                KlientPanelForm klientPanelForm = new KlientPanelForm(nazwa_uzytkownika);
                klientPanelForm.setVisible(true);
            }
        });
        generujParagonButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generujParagonPDF( nazwa_uzytkownika );
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
    private void loadWypozyczenia() {
        String[] columnNames = {"Sprzęt", "Kwota", "Data od", "Data do"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);

        String sql = "SELECT s.nazwa AS sprzet, " +"w.kwota AS kwota," + "w.data_od AS data_od," + "w.data_do AS data_do " + "FROM   wypozyczenia w " + "JOIN   sprzet       s ON w.id_sprzetu     = s.id_sprzetu " +  "JOIN   uzytkownicy  u ON w.id_uzytkownika = u.id_uzytkownika " +   "WHERE  u.nazwa_uzytkownika = ?";
        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/wypozyczalnia", "root", "");
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nazwa_uzytkownika);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Object[] row = {
                            rs.getString("sprzet"),
                            String.format("%.2f zł", rs.getDouble("kwota")),
                            rs.getDate("data_od"),
                            rs.getDate("data_do")
                    };
                    tableModel.addRow(row);
                }
            }
            table1.setModel(tableModel);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Błąd podczas ładowania wypożyczeń: " + e.getMessage());
        }
    }
    private void generujParagonPDF(String nazwa_uzytkownika) {
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
}

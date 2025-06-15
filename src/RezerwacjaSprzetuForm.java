import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.sql.*;
public class RezerwacjaSprzetuForm extends JFrame {
    private JPanel mainPanel;
    private JPanel baner;
    private JPanel dane;
    private JPanel ODPanel;
    private JPanel DOPanel;
    private JButton ObliczKosztButton;
    private JButton wróćButton;
    private JTable listaSprzetow;
    private JTextField KwotatextField1;
    private JButton zarezerwujButton;
    private DatePicker ODdatePicker1;
    private DatePicker DOdatePicker2;
    private String nazwa_uzytkownika;
    private int idUzytkownika;
    public RezerwacjaSprzetuForm(int idUzytkownika,String nazwa_uzytkownika) {
        super("Rezerwacja sprzętu");
        this.nazwa_uzytkownika = nazwa_uzytkownika;
        this.idUzytkownika = idUzytkownika;
        this.setContentPane(this.mainPanel);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setSize(900, 600);
        this.setLocationRelativeTo(null);

        ODdatePicker1 = createDatePicker();
        DOdatePicker2 = createDatePicker();

        setDateLimits();

        ODPanel.setLayout(new BorderLayout());
        DOPanel.setLayout(new BorderLayout());

        ODPanel.add(ODdatePicker1, BorderLayout.CENTER);
        DOPanel.add(DOdatePicker2, BorderLayout.CENTER);

        setDefaultDates();
        this.setVisible(true);
        addListeners();
        loadSprzetData();
        ObliczKosztButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                obliczKwote();
            }
        });
        zarezerwujButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                zapiszRezerwacje();
            }
        });
        wróćButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                KlientPanelForm klientPanelForm = new KlientPanelForm(nazwa_uzytkownika);
                klientPanelForm.setVisible(true);
            }
        });
        listaSprzetow.setRowHeight(30);
        listaSprzetow.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        listaSprzetow.setShowGrid(true);
        listaSprzetow.setGridColor(new Color(220, 220, 220));
        listaSprzetow.setIntercellSpacing(new Dimension(1, 1));
        listaSprzetow.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaSprzetow.setSelectionBackground(new Color(184, 207, 229));
        listaSprzetow.setSelectionForeground(Color.BLACK);
        listaSprzetow.setFillsViewportHeight(true);
        listaSprzetow.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        JTableHeader header = listaSprzetow.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(new Color(240, 240, 240));
        header.setForeground(Color.BLACK);
        header.setReorderingAllowed(false);
    }
    private DatePicker createDatePicker() {
        DatePickerSettings settings = new DatePickerSettings();
        settings.setFormatForDatesCommonEra("yyyy-MM-dd");
        settings.setAllowEmptyDates(false);
        DatePicker datePicker = new DatePicker(settings);
        datePicker.setPreferredSize(new Dimension(150, 30));
        datePicker.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return datePicker;
    }
    private void setDateLimits() {
        ODdatePicker1.getSettings().setDateRangeLimits(LocalDate.now(), null);
        DOdatePicker2.getSettings().setDateRangeLimits(LocalDate.now(), null);
    }
    private void setDefaultDates() {
        ODdatePicker1.setDate(LocalDate.now());
        DOdatePicker2.setDate(LocalDate.now().plusDays(7));
    }
    private void addListeners() {
        ODdatePicker1.addDateChangeListener(e -> {
            LocalDate selectedOD = ODdatePicker1.getDate();
            if (selectedOD != null) {
                LocalDate minDO = selectedOD.plusDays(1);
                DOdatePicker2.getSettings().setDateRangeLimits(minDO, null);

                if (DOdatePicker2.getDate() != null && DOdatePicker2.getDate().isBefore(minDO)) {
                    DOdatePicker2.setDate(minDO);
                }
            }
        });
    }
    private void loadSprzetData() {
        String[] columnNames = {"ID", "Nazwa", "Typ (opis)", "Producent", "Cena za dzień", "Dostępność"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);

        String sql = "SELECT id_sprzetu, nazwa, typ, producent, cena_za_dzien, cena_za_godzine, dostepnosc FROM sprzet";

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/wypozyczalnia", "root", "");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id_sprzetu");
                String nazwa = rs.getString("nazwa");
                String typ = rs.getString("typ");
                String producent = rs.getString("producent");
                double cenaDzien = rs.getDouble("cena_za_dzien");
                double cenaGodz = rs.getDouble("cena_za_godzine");
                boolean dostepnosc = rs.getBoolean("dostepnosc");

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
                        sprzet = new Sprzet(id, nazwa, producent, dostepnosc, cenaDzien, cenaGodz);
                }
                Object[] row = {
                        id,
                        nazwa,
                        sprzet.getTypOpisowy(),
                        producent,
                        String.format("%.2f zł", cenaDzien),
                        dostepnosc ? "Dostępny" : "Niedostępny"
                };
                tableModel.addRow(row);
            }
            listaSprzetow.setModel(tableModel);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Błąd podczas ładowania danych: " + e.getMessage());
        }
    }
    private void obliczKwote() {
        LocalDate dataOD = ODdatePicker1.getDate();
        LocalDate dataDO = DOdatePicker2.getDate();
        if (dataOD == null || dataDO == null || dataDO.isBefore(dataOD)) {
            JOptionPane.showMessageDialog(this, "Wybierz poprawne daty.");
            return;
        }
        int selectedRow = listaSprzetow.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Wybierz sprzęt z tabeli.");
            return;
        }
        int idSprzetu = (int) listaSprzetow.getValueAt(selectedRow, 0);
        int liczbaDni = (int) (dataDO.toEpochDay() - dataOD.toEpochDay());

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/wypozyczalnia", "root", "");
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM sprzet WHERE id_sprzetu = ?")) {

            stmt.setInt(1, idSprzetu);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String typ = rs.getString("typ");
                String nazwa = rs.getString("nazwa");
                String producent = rs.getString("producent");
                boolean dostepnosc = rs.getBoolean("dostepnosc");
                double cenaZaDzien = rs.getDouble("cena_za_dzien");
                double cenaZaGodzine = rs.getDouble("cena_za_godzine");

                Sprzet sprzet;
                switch (typ.toLowerCase()) {
                    case "ciężki":
                        sprzet = new SprzetCiezki(idSprzetu, nazwa, producent, dostepnosc, cenaZaDzien, cenaZaGodzine);
                        break;
                    case "średni":
                        sprzet = new SprzetSredni(idSprzetu, nazwa, producent, dostepnosc, cenaZaDzien, cenaZaGodzine);
                        break;
                    case "lekki":
                        sprzet = new SprzetLekki(idSprzetu, nazwa, producent, dostepnosc, cenaZaDzien, cenaZaGodzine);
                        break;
                    default:
                        sprzet = new Sprzet(idSprzetu, nazwa, producent, dostepnosc, cenaZaDzien, cenaZaGodzine);
                }
                double kwota = sprzet.obliczKoszt(liczbaDni);
                KwotatextField1.setText(String.format("%.2f zł", kwota));
            } else {
                JOptionPane.showMessageDialog(this, "Nie znaleziono sprzętu w bazie.");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Błąd SQL: " + e.getMessage());
        }
    }
    private void zapiszRezerwacje() {
        LocalDate dataOD = ODdatePicker1.getDate();
        LocalDate dataDO = DOdatePicker2.getDate();
        if (dataOD == null || dataDO == null || dataDO.isBefore(dataOD)) {
            JOptionPane.showMessageDialog(this, "Niepoprawne daty.");
            return;
        }
        int selectedRow = listaSprzetow.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Wybierz sprzęt.");
            return;
        }
        int idSprzetu = (int) listaSprzetow.getValueAt(selectedRow, 0);
        String kwotaText = KwotatextField1.getText().replace(" zł", "").replace(",", ".");
        if (kwotaText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Najpierw oblicz kwotę.");
            return;
        }
        double kwota;
        try {
            kwota = Double.parseDouble(kwotaText);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Nieprawidłowa kwota.");
            return;
        }
        String sql = "INSERT INTO rezerwacje (id_uzytkownika, id_sprzetu, data_od, data_do, kwota, status) VALUES (?, ?, ?, ?, ?, 'oczekuje')";

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/wypozyczalnia", "root", "");
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idUzytkownika);
            stmt.setInt(2, idSprzetu);
            stmt.setDate(3, Date.valueOf(dataOD));
            stmt.setDate(4, Date.valueOf(dataDO));
            stmt.setDouble(5, kwota);

            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Rezerwacja zapisana. Oczekuje na akceptację.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Błąd zapisu: " + ex.getMessage());
        }
    }
}
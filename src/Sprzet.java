public class Sprzet {
    protected int id;
    protected String nazwa;
    protected String producent;
    protected boolean dostepnosc;
    protected double cenaZaDzien;
    protected double cenaZaGodzine;
    public Sprzet(int id, String nazwa, String producent, boolean dostepnosc,
                  double cenaZaDzien, double cenaZaGodzine) {
        this.id = id;
        this.nazwa = nazwa;
        this.producent = producent;
        this.dostepnosc = dostepnosc;
        this.cenaZaDzien = cenaZaDzien;
        this.cenaZaGodzine = cenaZaGodzine;
    }
    public double obliczKoszt(int dni) {
        return dni * cenaZaDzien;
    }
    public String getTypOpisowy() {
        return "Ogólny sprzęt";
    }
    public String getNazwa() {
        return nazwa;
    }
}
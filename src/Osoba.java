public class Osoba {
    protected String imie;
    protected String nazwisko;
    protected String telefon;

    public Osoba(String imie, String nazwisko, String telefon) {
        this.imie = imie;
        this.nazwisko = nazwisko;
        this.telefon = telefon;
    }
    public String getImie() { return imie; }
    public String getNazwisko() { return nazwisko; }
    public String getTelefon() { return telefon; }
    public void setImie(String imie) { this.imie = imie; }
    public void setNazwisko(String nazwisko) { this.nazwisko = nazwisko; }
    public void setTelefon(String telefon) { this.telefon = telefon; }
}
public class Uzytkownik extends Osoba {
    private String nazwaUzytkownika;
    private String haslo;
    private String email;
    public Uzytkownik(String imie, String nazwisko, String telefon,
                      String nazwaUzytkownika, String haslo, String email) {
        super(imie, nazwisko, telefon);
        this.nazwaUzytkownika = nazwaUzytkownika;
        this.haslo = haslo;
        this.email = email;
    }
    public Uzytkownik() {
        super("", "", "");
    }
    public String getNazwaUzytkownika() { return nazwaUzytkownika; }
    public String getHaslo() { return haslo; }
    public String getEmail() { return email; }
    public void setNazwaUzytkownika(String nazwaUzytkownika) { this.nazwaUzytkownika = nazwaUzytkownika; }
    public void setHaslo(String haslo) { this.haslo = haslo; }
    public void setEmail(String email) { this.email = email; }
}
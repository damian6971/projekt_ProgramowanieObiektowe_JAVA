public class SprzetLekki extends Sprzet {
    public SprzetLekki(int id, String nazwa, String producent, boolean dostepnosc,
                       double cenaZaDzien, double cenaZaGodzine) {
        super(id, nazwa, producent, dostepnosc, cenaZaDzien, cenaZaGodzine);
    }
    @Override
    public String getTypOpisowy() {
        return "Sprzęt lekki – łatwy w transporcie i obsłudze.";
    }
}
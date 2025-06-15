public class SprzetCiezki extends Sprzet {
    public SprzetCiezki(int id, String nazwa, String producent, boolean dostepnosc,
                        double cenaZaDzien, double cenaZaGodzine) {
        super(id, nazwa, producent, dostepnosc, cenaZaDzien, cenaZaGodzine);
    }
    @Override
    public String getTypOpisowy() {
        return "Sprzęt ciężki – wymaga specjalnych uprawnień.";
    }
}
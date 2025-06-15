public class SprzetSredni extends Sprzet {
    public SprzetSredni(int id, String nazwa, String producent, boolean dostepnosc,
                        double cenaZaDzien, double cenaZaGodzine) {
        super(id, nazwa, producent, dostepnosc, cenaZaDzien, cenaZaGodzine);
    }
    @Override
    public String getTypOpisowy() {
        return "Sprzęt średni – standardowy dla prac budowlanych.";
    }
}
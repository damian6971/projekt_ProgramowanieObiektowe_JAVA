-- phpMyAdmin SQL Dump
-- version 5.1.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Czas generowania: 09 Cze 2025, 19:34
-- Wersja serwera: 10.4.22-MariaDB
-- Wersja PHP: 8.1.2

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Baza danych: `wypozyczalnia`
--

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `sprzet`
--

CREATE TABLE `sprzet` (
  `id_sprzetu` int(11) NOT NULL,
  `nazwa` varchar(100) NOT NULL,
  `typ` varchar(100) DEFAULT NULL,
  `producent` varchar(100) DEFAULT NULL,
  `dostepnosc` tinyint(1) DEFAULT 1,
  `cena_za_dzien` decimal(10,2) NOT NULL,
  `cena_za_godzine` decimal(10,2) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Zrzut danych tabeli `sprzet`
--

INSERT INTO `sprzet` (`id_sprzetu`, `nazwa`, `typ`, `producent`, `dostepnosc`, `cena_za_dzien`, `cena_za_godzine`) VALUES
(6, 'Betoniarka 160L', 'Średni', 'ZREMB', 1, '80.00', '15.00'),
(7, 'Wiertarka udarowa', 'Lekki', 'Bosch', 1, '45.00', '8.00'),
(8, 'Młot pneumatyczny', 'Lekki', 'Hilti', 1, '100.00', '20.00'),
(12, 'taess', 'Ciężki', 'asdasd', 1, '123.00', '1.00'),
(13, 'asd', 'Ciężki', 'asd', 1, '123.00', '12.00');

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `uzytkownicy`
--

CREATE TABLE `uzytkownicy` (
  `id_uzytkownika` int(11) NOT NULL,
  `nazwa_uzytkownika` varchar(50) NOT NULL,
  `haslo` varchar(255) NOT NULL,
  `email` varchar(100) NOT NULL,
  `telefon` varchar(20) DEFAULT NULL,
  `imie` varchar(50) DEFAULT NULL,
  `nazwisko` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Zrzut danych tabeli `uzytkownicy`
--

INSERT INTO `uzytkownicy` (`id_uzytkownika`, `nazwa_uzytkownika`, `haslo`, `email`, `telefon`, `imie`, `nazwisko`) VALUES
(2, 'ela', 'ela123', 'elakania652@gmail.com', '987654321', 'Halina', 'Orzeł'),
(3, 'darek', 'darek123', 'darakana@poczta.onet.pl', '987654321', 'Dariusz', 'Wiśniewski'),
(4, 'damian777', 'damian777', 'damianaknai123@o2.pl', '535340742', 'Damian', 'Kania'),
(5, 'Damian69', 'damian123', 'damiankania762@o2.pl', '598745655', 'Damian', 'Kania');

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `wypozyczenia`
--

CREATE TABLE `wypozyczenia` (
  `id_wypozyczenia` int(11) NOT NULL,
  `id_klienta` int(11) NOT NULL,
  `id_sprzetu` int(11) NOT NULL,
  `data_wypozyczenia` date NOT NULL,
  `data_zwrotu` date DEFAULT NULL,
  `koszt` decimal(10,2) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Indeksy dla zrzutów tabel
--

--
-- Indeksy dla tabeli `klienci`
--
ALTER TABLE `klienci`
  ADD PRIMARY KEY (`id_klienta`);

--
-- Indeksy dla tabeli `sprzet`
--
ALTER TABLE `sprzet`
  ADD PRIMARY KEY (`id_sprzetu`);

--
-- Indeksy dla tabeli `uzytkownicy`
--
ALTER TABLE `uzytkownicy`
  ADD PRIMARY KEY (`id_uzytkownika`),
  ADD UNIQUE KEY `nazwa_uzytkownika` (`nazwa_uzytkownika`),
  ADD UNIQUE KEY `email` (`email`);

--
-- Indeksy dla tabeli `wypozyczenia`
--
ALTER TABLE `wypozyczenia`
  ADD PRIMARY KEY (`id_wypozyczenia`),
  ADD KEY `id_klienta` (`id_klienta`),
  ADD KEY `id_sprzetu` (`id_sprzetu`);

--
-- AUTO_INCREMENT dla zrzuconych tabel
--

--
-- AUTO_INCREMENT dla tabeli `klienci`
--
ALTER TABLE `klienci`
  MODIFY `id_klienta` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT dla tabeli `sprzet`
--
ALTER TABLE `sprzet`
  MODIFY `id_sprzetu` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=14;

--
-- AUTO_INCREMENT dla tabeli `uzytkownicy`
--
ALTER TABLE `uzytkownicy`
  MODIFY `id_uzytkownika` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT dla tabeli `wypozyczenia`
--
ALTER TABLE `wypozyczenia`
  MODIFY `id_wypozyczenia` int(11) NOT NULL AUTO_INCREMENT;

--
-- Ograniczenia dla zrzutów tabel
--

--
-- Ograniczenia dla tabeli `wypozyczenia`
--
ALTER TABLE `wypozyczenia`
  ADD CONSTRAINT `wypozyczenia_ibfk_1` FOREIGN KEY (`id_klienta`) REFERENCES `klienci` (`id_klienta`),
  ADD CONSTRAINT `wypozyczenia_ibfk_2` FOREIGN KEY (`id_sprzetu`) REFERENCES `sprzet` (`id_sprzetu`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;

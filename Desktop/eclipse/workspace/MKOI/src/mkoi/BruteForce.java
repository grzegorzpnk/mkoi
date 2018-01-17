package mkoi;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * Klasa odpowiedzialna za realizacjê testu pe³nego przegl¹du, czyli ataku
 * brute-force na token.
 * 
 * @author Rados³aw Skar¿ycki
 *
 */
public class BruteForce {

	/**
	 * Numer karty
	 */
	private String cardNumber = "";
	/**
	 * Data wa¿noœci karty
	 */
	private String cardValidity = "";
	/**
	 * Wygenerowane ziarno
	 */
	private byte[] seed = null;
	/**
	 * Obliczony token
	 */
	private byte[] hashValue = null;

	/**
	 * Kontruktor ataku.
	 * 
	 * @param cardNumber
	 *            numer karty
	 * @param cardValidity
	 *            data wa¿noœci karty
	 * @param seed
	 *            wygenerowane ziarno
	 * @param hashValue
	 *            obliczony token
	 */
	public BruteForce(String cardNumber, String cardValidity, byte[] seed, byte[] hashValue) {
		this.cardNumber = cardNumber;
		this.cardValidity = cardValidity;
		this.seed = seed;
		this.hashValue = hashValue;
	}

	/**
	 * Metoda uruchamiaj¹ca przeprowadzenie ataku.
	 * 
	 * @param version
	 *            wersja testu do uruchomienia - ostatecznie ograniczona do jednego
	 *            testu po wyliczeniach matematycznych czasu liczenia.
	 * @throws NoSuchAlgorithmException
	 *             wyj¹tek dotycz¹cy braku funkcji skrótu w bibliotece
	 */
	public void attackBruteForce(int version) throws NoSuchAlgorithmException {
		System.out.println("Rozpoczêcie ataku brute-force na kartê.\n");
		System.out.println("Wybrano test \"Znane ziarno, nieznane dane karty.\"");
		attack1();
		System.out.println("Zakoñczono atak brute-force.");
	}

	/**
	 * Metoda przeprowadza atak na numer karty i datê wa¿noœci przy za³o¿eniach, ¿e
	 * znamy pierwsze 6 i ostatnie 4 cyfry numeru karty oraz maksymnalny termin
	 * wa¿noœci karty to 12/22.
	 * 
	 * @throws NoSuchAlgorithmException
	 *             wyj¹tek dotycz¹cy braku funkcji skrótu w bibliotece
	 */
	private void attack1() throws NoSuchAlgorithmException {
		System.out.println("=============");
		System.out.println("Atak 1 - nieznane dane karty.");

		byte[] inputBytes = new byte[16 + 2 + 2 + 32];
		prepareCardNumber(inputBytes);
		prepareSeed(inputBytes);

		int card[] = new int[6];
		for (int i = 0; i < 6; i++) {
			card[i] = 0;
		}

		int data1 = 1;
		int data2 = 18;

		byte[] hash = hashValue;
		byte[] calculateHash = null;
		MessageDigest digest = MessageDigest.getInstance("SHA-256");

		long startTime = 0;
		long stopTime = 0;

		boolean running = true;

		String foundNumber = "";
		String foundData = "";

		startTime = System.currentTimeMillis();
		while (running) {
			// unknown numbers
			for (int i = 6; i < 12; i++) {
				inputBytes[i] = Integer.toString(card[i - 6]).getBytes()[0];
			}
			// unknown data
			for (data2 = 18; data2 < 23; data2++) {
				if (!running)
					break;
				for (data1 = 1; data1 < 13; data1++) {
					if (!running)
						break;
					if (data1 < 10) {
						inputBytes[16] = (new String("0")).getBytes()[0];
						inputBytes[17] = Integer.toString(data1).getBytes()[0];
					} else {
						inputBytes[16] = Integer.toString(data1).getBytes()[0];
						inputBytes[17] = Integer.toString(data1).getBytes()[1];
					}
					inputBytes[18] = Integer.toString(data2).getBytes()[0];
					inputBytes[19] = Integer.toString(data2).getBytes()[1];

					// Solution.wyswietlWByte(inputBytes);
					calculateHash = digest.digest(inputBytes);

					if (Arrays.equals(hash, calculateHash)) {
						stopTime = System.currentTimeMillis();
						System.out.println("Znaleziono hash.");
						Solution.wyswietlWHex(calculateHash);
						for (int i = 0; i < 16; i++) {
							foundNumber += new String(new byte[] { inputBytes[i] });// Byte.toString(inputBytes[i]);
						}
						foundData += new String(new byte[] { inputBytes[16] });
						foundData += new String(new byte[] { inputBytes[17] });
						foundData += "/";
						foundData += new String(new byte[] { inputBytes[18] });
						foundData += new String(new byte[] { inputBytes[19] });
						running = false;
					}
				}
			}

			// increment card number
			card[5]++;
			if (card[5] > 9) {
				card[5] = 0;
				card[4]++;
			}
			if (card[4] > 9) {
				card[4] = 0;
				card[3]++;
			}
			if (card[3] > 9) {
				card[3] = 0;
				card[2]++;
			}
			if (card[2] > 9) {
				card[2] = 0;
				card[1]++;
			}
			if (card[1] > 9) {
				card[1] = 0;
				card[0]++;
			}
			if (card[0] > 9) {
				running = false;
			}
		}
		if (stopTime == 0)
			stopTime = System.currentTimeMillis();

		if (foundNumber.equals("")) {
			System.out.println("Nie znaleziono numeru karty ani daty wa¿noœci. Czas trwania wyszukiwania: "
					+ (stopTime - startTime));
		} else {
			System.out.println("Numer karty:   " + foundNumber);
			System.out.println("Data wa¿noœci: " + foundData);
			System.out.println("Czas liczenia: " + (stopTime - startTime));
		}
	}

	/**
	 * Metoda przygotowuj¹ca znany fragment numeru karty.
	 * 
	 * @param inputBytes
	 *            ci¹g bajtów podawany do funkcji hashuj¹cej
	 */
	private void prepareCardNumber(byte[] inputBytes) {
		byte[] number = cardNumber.getBytes();
		// first 6
		inputBytes[0] = number[0];
		inputBytes[1] = number[1];
		inputBytes[2] = number[2];
		inputBytes[3] = number[3];
		inputBytes[4] = number[4];
		inputBytes[5] = number[5];
		// last 4
		inputBytes[12] = number[12];
		inputBytes[13] = number[13];
		inputBytes[14] = number[14];
		inputBytes[15] = number[15];
	}

	/**
	 * Metoda kopiuj¹ca seed.
	 * 
	 * @param inputBytes
	 *            ci¹g bajtów podawany do funkcji hashuj¹cej
	 */
	private void prepareSeed(byte[] inputBytes) {
		for (int i = 20; i < 52; i++) {
			inputBytes[i] = seed[i - 20];
		}
	}
}

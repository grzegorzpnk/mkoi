package mkoi;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.io.UnsupportedEncodingException;
import java.math.*;

/**
 * Klasa odpowiedzialna za wyliczanie funkcji skrótu SHA-256.
 * 
 * @author Grzegorz Panek
 *
 */
public class SHA256 {

	/**
	 * Predefiniowane wartoœci potrzebne do policzenia funkcji skrótu.
	 */
	final static long[] K = { 0x428a2f98L, 0x71374491L, 0xb5c0fbcfL, 0xe9b5dba5L, 0x3956c25bL, 0x59f111f1L, 0x923f82a4L,
			0xab1c5ed5L, 0xd807aa98L, 0x12835b01L, 0x243185beL, 0x550c7dc3L, 0x72be5d74L, 0x80deb1feL, 0x9bdc06a7L,
			0xc19bf174L, 0xe49b69c1L, 0xefbe4786L, 0x0fc19dc6L, 0x240ca1ccL, 0x2de92c6fL, 0x4a7484aaL, 0x5cb0a9dcL,
			0x76f988daL, 0x983e5152L, 0xa831c66dL, 0xb00327c8L, 0xbf597fc7L, 0xc6e00bf3L, 0xd5a79147L, 0x06ca6351L,
			0x14292967L, 0x27b70a85L, 0x2e1b2138L, 0x4d2c6dfcL, 0x53380d13L, 0x650a7354L, 0x766a0abbL, 0x81c2c92eL,
			0x92722c85L, 0xa2bfe8a1L, 0xa81a664bL, 0xc24b8b70L, 0xc76c51a3L, 0xd192e819L, 0xd6990624L, 0xf40e3585L,
			0x106aa070L, 0x19a4c116L, 0x1e376c08L, 0x2748774cL, 0x34b0bcb5L, 0x391c0cb3L, 0x4ed8aa4aL, 0x5b9cca4fL,
			0x682e6ff3L, 0x748f82eeL, 0x78a5636fL, 0x84c87814L, 0x8cc70208L, 0x90befffaL, 0xa4506cebL, 0xbef9a3f7L,
			0xc67178f2L };

	/**
	 * Predefiniowane wartoœci potrzebne do policzenia funkcji skrótu.
	 */
	static long[] H = { 0x6a09e667L, 0xbb67ae85L, 0x3c6ef372L, 0xa54ff53aL, 0x510e527fL, 0x9b05688cL, 0x1f83d9abL,
			0x5be0cd19L };

	/**
	 * Predefiniowane wartoœci tymczasowe potrzebne do policzenia funkcji skrótu.
	 */
	static long[] X = { 0x6a09e667L, 0xbb67ae85L, 0x3c6ef372L, 0xa54ff53aL, 0x510e527fL, 0x9b05688cL, 0x1f83d9abL,
			0x5be0cd19L };
	
	
	/**
	 * Metoda licz¹ca skrót SHA-256 dla przygotowanego ci¹gu.
	 * @param buf przygotowany ci¹g
	 */
	static void compress(byte[] buf) {

		long[] A = new long[8]; // A,B,C,D,E,F,G,H
		long[] W = new long[64];
		long modulo = (long) Math.pow(2, 32);

		BigInteger bi = new BigInteger(buf);
		byte[] tmp = new byte[4];
		long[] tmphex = new long[4];

		// sha256 message schedule podziel kawa³ek na szesnaœcie 32-bitowych s³ów
		// big-endian w [0..15]
		for (int i = 0; i < 16; i++) {
			W[i] = 0;

			for (int j = 0; j < 4; j++) {
				tmp[j] = buf[i * 4 + j];
				tmphex[j] = tmp[j];
				W[i] = W[i] | ((tmphex[j] & 0x000000FF) << (24 - j * 8));
				// 0691f9c9710d900996ecf8ad7c394af923459f1856cec9f3487bbf2038129011
			}

			StringBuilder sb = new StringBuilder();
			for (long b : W) {
				sb.append(String.format("%02X ", b));
			}
			// System.out.println("w: " + i + " " + sb);
		}
		// Rozszerz szesnaœcie 32-bitowych s³ów na szeœædziesi¹t cztery 32-bitowe s³owa:
		for (int t = 16; t < 64; t++) {
			W[t] = (Sigma1(W[t - 2]) + W[t - 7] + Sigma0(W[t - 15]) + W[t - 16]);
			W[t] = (W[t] % modulo);
		}

		// Inicjalizuj wartoœæ skrótu dla tego kawa³ka:
		for (int i = 0; i < 8; i++)
			A[i] = H[i];

		// Pêtla g³ówna:
		for (int i = 0; i < 64; i++) {

			long T1 = A[7] + Sum1(A[4]) + Ch(A[4], A[5], A[6]) + K[i] + W[i];
			T1 = T1 % modulo;

			long T2 = Sum0(A[0]) + Maj(A[0], A[1], A[2]);
			T2 = T2 % modulo;
			// Dodaj ten hash kawa³ka do bie¿¹cego rezultatu:
			A[7] = A[6];
			A[6] = A[5];
			A[5] = A[4];
			A[4] = (A[3] + T1) % modulo;
			A[3] = A[2];
			A[2] = A[1];
			A[1] = A[0];
			A[0] = (T1 + T2) % modulo;

			StringBuilder sb = new StringBuilder();

		}

		// Wyprodukuj ostateczn¹ wartoœæ skrótu (big-endian): // hashe 1-8
		for (int i = 0; i < 8; i++) {
			H[i] += A[i];
			H[i] = H[i] % modulo;
		}

		StringBuilder sb = new StringBuilder();
		for (long b : H) {
			sb.append(String.format("%02X ", b));
		}
		// System.out.println("H na koniec : " + sb);

	}

	/**
	 * Metoda przygotowuj¹ca ci¹g do liczenia skrótu SHA-256.
	 * 
	 * @param messageToHash
	 *            ci¹g do obliczenia funkcji skrótu SHA-256
	 * @return wartoœæ funkcji skrótu SHA-256 dla podanego ciagu
	 * @throws UnsupportedEncodingException
	 *             wyj¹tek dotycz¹cy nieobs³ugiwanego kodowania
	 */
	public String sha256(byte[] messageToHash) throws UnsupportedEncodingException {
		String hash = "";
		String str = "";
		byte[] message = messageToHash;
		byte[] buffer = new byte[64];// 64x8
		int c = 0;
		int l = message.length;// dlugosc
		// System.out.println(" Lenght: " + l);

		for (int j = 0; j < l; j++)
			buffer[j] = message[j];

		buffer[l] = (byte) (10000000 & 0xff); // dodanie bitu '1'
		int lb = Integer.valueOf(String.valueOf(l * 8));
		buffer[60] = (byte) (lb >> 24);
		buffer[61] = (byte) (lb >> 16);
		buffer[62] = (byte) (lb >> 8);
		buffer[63] = (byte) (lb);

		compress(buffer);

		StringBuilder sb = new StringBuilder();
		for (byte b : buffer) {
			sb.append(String.format("%02X ", b));
		}
		// System.out.println("Buffer in byte: " + sb);

		for (int i = 0; i < 8; i++) {
			str = Long.toHexString(H[i]);
			int strlen = str.length();
			for (int j = strlen; j < 8; j++)
				str = "0" + str;
			hash += str;
		}

		H=null;
		H=X.clone();
		// System.out.println(hash);
		return hash;
	}

	/**
	 * Metoda dokonuj¹ca rotacji w prawo.
	 * 
	 * @param x
	 *            parametr rotowany
	 * @param n
	 *            parametr, o ile rotowaæ
	 * @return parametr rotowany po rotacji
	 */
	static long RotateRight(long x, int n) {

		return (((x) >> (n)) | ((x) << (32 - (n))));
	}

	/**
	 * Metoda dokonuj¹ca przesuniêcia w prawo.
	 * 
	 * @param x
	 *            parametr przesuwany
	 * @param n
	 *            parametr, o ile przesuwaæ
	 * @return parametr przesuwany po przesuniêciu
	 */
	static long ShiftRight(long x, int n) {

		return ((x) >> (n));
	}

	/**
	 * Metoda oblicza wartoœæ zmiennej pomocniczej Sum0.
	 * 
	 * @param x
	 *            parametr poddawany przekszta³ceniom
	 * @return wartoœæ zmiennej pomocniczej Sum0
	 */
	static long Sum0(long x) {
		return (RotateRight(x, 2) ^ RotateRight(x, 13) ^ RotateRight(x, 22));

	}

	/**
	 * Metoda oblicza wartoœæ zmiennej pomocniczej Sum1.
	 * 
	 * @param x
	 *            parametr poddawany przekszta³ceniom
	 * @return wartoœæ zmiennej pomocniczej Sum1
	 */
	static long Sum1(long x) {
		return (RotateRight(x, 6) ^ RotateRight(x, 11) ^ RotateRight(x, 25));

	}

	/**
	 * Metoda oblicza wartoœæ zmiennej pomocniczej Ch.
	 * 
	 * @param x
	 *            parametr wyliczania zmiennej
	 * @param y
	 *            parametr wyliczania zmiennej
	 * @param z
	 *            parametr wyliczania zmiennej
	 * @return wartoœæ zmiennej pomocniczej Ch
	 */
	private static long Ch(long x, long y, long z) {
		return ((x & y) ^ (~x & z));
	}

	/**
	 * Metoda oblicza wartoœæ zmiennej pomocniczej Maj.
	 * 
	 * @param x
	 *            parametr wyliczania zmiennej
	 * @param y
	 *            parametr wyliczania zmiennej
	 * @param z
	 *            parametr wyliczania zmiennej
	 * @return wartoœæ zmiennej pomocniczej Maj
	 */
	private static long Maj(long x, long y, long z) {
		return ((x & y) ^ (x & z) ^ (y & z));
	}

	/**
	 * Metoda oblicza wartoœæ zmiennej pomocniczej Sigma0.
	 * 
	 * @param x
	 *            parametr poddawany przekszta³ceniom
	 * @return wartoœæ zmiennej pomoniczej Sigma0
	 */
	private static long Sigma0(long x) {
		return (RotateRight(x, 7) ^ RotateRight(x, 18) ^ ShiftRight(x, 3));
	}

	/**
	 * Metoda oblicza wartoœæ zmiennej pomocniczej Sigma1.
	 * 
	 * @param x
	 *            parametr poddawany przekszta³ceniom
	 * @return wartoœæ zmiennej pomocniczej Sigma1
	 */
	private static long Sigma1(long x) {
		return (RotateRight(x, 17) ^ RotateRight(x, 19) ^ ShiftRight(x, 10));
	}

}

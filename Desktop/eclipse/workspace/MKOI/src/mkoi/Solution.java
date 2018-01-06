package mkoi;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class Solution{
	
	public static void main(String[] args) throws IOException, NoSuchAlgorithmException{
	
	String cardNumber = null;	
	byte[] cardNumberBytes = null;
	String cardValidity = null;
	String cardValidityMonth =null;
	byte[] cardValidityMonthBytes = null; 
	String cardValidityYear = null;
	byte[] cardValidityYearBytes = null;
	String tmpRead[] = new String[2];
	boolean flaga = true;
	byte[] InputBytes = null;;
	byte[] hashedOutputByte=null;
	byte[] seed = new byte[32];	;
	Scanner sc = new Scanner(System.in);
	new Random().nextBytes(seed);	//generujemy ziarno:

	System.out.println("Wprowadz 16 cyfrowy numer karty p³atniczej:");
	
	while(flaga == true)
	{
	cardNumber = sc.nextLine();
	if(cardNumber.length()!=16)
		System.out.println("Wprowadzono numer karty o innej d³ugoœci. Wprowadz poprawny numer karty jeszcze raz:");
	else 
		flaga = false;
	}

	cardNumberBytes = cardNumber.getBytes(StandardCharsets.UTF_8);

	System.out.println("Wprowadz termin wa¿noœci karty p³atniczej (MM/RR): ");
	flaga = true;
	while(flaga == true)
	{
	cardValidity = sc.nextLine();
	tmpRead = cardValidity.split("/");
	cardValidityMonth = tmpRead[0];
	cardValidityYear = tmpRead[1];
	cardValidityMonthBytes = cardValidityMonth.getBytes(StandardCharsets.UTF_8);
	cardValidityYearBytes = cardValidityYear.getBytes(StandardCharsets.UTF_8);
	
	System.out.println("Program wytworzy token dla karty o numerze: \n"+ cardNumber
			+ "\nData wa¿noœci karty to:\n"+cardValidityMonth+"/"+cardValidityYear);

	if(cardValidity.length()!=5 | !cardValidity.contains("/"))
		System.out.println("Wprowadzono niepoprawne dane. Wprowadz termin wa¿noœci karty jeszcze raz (MM/RR):");
	else 
		flaga = false;
	}

		System.out.println("Wygenerowane ziarno:");
		wyswietlWByte(seed);
		System.out.println("Numer karty w bajtach: (w ASCII)");
		wyswietlWByte(cardNumberBytes);
		System.out.println("Miesiac waznosci w bajtach: (w ASCII)");
		wyswietlWByte(cardValidityMonthBytes);
		System.out.println("Rok waznosci w bajtach: (w ASCII)" );
		wyswietlWByte(cardValidityYearBytes);		
				
				
		//konkatenacja w bajt i wrzucenie w algorytm
				
	InputBytes =  new byte[16+2+2+32];			
	System.arraycopy(cardNumberBytes, 0, InputBytes, 0, cardNumberBytes.length);
	System.arraycopy(cardValidityMonthBytes, 0, InputBytes, cardNumberBytes.length, cardValidityMonthBytes.length);
	System.arraycopy(cardValidityYearBytes, 0, InputBytes, cardNumberBytes.length+cardValidityMonthBytes.length, cardValidityYearBytes.length);
	System.arraycopy(seed, 0, InputBytes, cardNumberBytes.length+cardValidityMonthBytes.length+cardValidityYearBytes.length, seed.length);
	
	
		System.out.println("Dane do hashu (numer karty + miesiac oraz rok platnosci + seed): ");
		wyswietlWByte(InputBytes);

		//TODO: CLS
		
		boolean flaga2 = true;
		int wybor;
		String hashedOutputByte2 = null;
		long start,stop;
		
		while(flaga2)
		{
			
			System.out.println("\n1. Policz hash za pomoc¹ fcji bibliotecznej");
			System.out.println("2. Policz hash za pomoc¹ fcji zaimplementowanej");
			System.out.println("Wybierz operacjê:");
			wybor = Integer.parseInt(sc.nextLine());
			
			switch(wybor)
			{
				case 1:
					MessageDigest digest = MessageDigest.getInstance("SHA-256");
					System.out.println("Stosujemy funkcjê SHA 256 z biblioteki Message Digest:");

					start = System.nanoTime();
					hashedOutputByte = digest.digest(InputBytes);
					stop = System.nanoTime();

					wyswietlWHex(hashedOutputByte);
					System.out.println("Czas wykonania funkcji:"+(stop-start)/1000+" us");
				
					break;
				case 2:
					System.out.println("Stosujemy funkcjê SHA 256 zaimplementowan¹:");
					
					start = System.nanoTime();
					hashedOutputByte2 = SHA256.sha256(InputBytes);
					stop = System.nanoTime();
					
					System.out.println(hashedOutputByte2);
					System.out.println("Czas wykonania funkcji:"+(stop-start)/1000+" us");

					break;
				case 3:
					System.out.println("Konczymy program!");
					flaga2=false;
					break;
								
			}
		}    
	}
	
	public static void wyswietlWHex(byte[]array){
		String hexStr2 = "";
	    for (int i = 0; i < array.length; i++) {
	        hexStr2 +=  Integer.toString( ( array[i] & 0xff ) + 0x100, 16).substring( 1 );
	    }
		System.out.println(hexStr2);	
	}
	
	public static void wyswietlWByte(byte[]array){
		String hexStr2 = "";
	    for (int i = 0; i < array.length; i++) {
	        hexStr2 +=  Integer.toString(array[i] & 0xff )+ " ";
	    }
		System.out.println(hexStr2);	
	}
	
	public static byte[] intToByteArray (final int integer) {
	    byte[] result = new byte[4];
	  
	    result[0] = (byte)((integer & 0xFF000000) >> 24);
	    result[1] = (byte)((integer & 0x00FF0000) >> 16);
	    result[2] = (byte)((integer & 0x0000FF00) >> 8);
	    result[3] = (byte)(integer & 0x000000FF);
	  
	    return result;
	}
}

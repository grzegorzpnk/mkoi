package projekt_MKOI;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class hashowanie{
	
	public static void main(String[] args){
	String hashInput = "grzegorz";
	byte[] hashInputByte;
	byte[] hashOutputByte=null;
	hashInputByte = hashInput.getBytes(StandardCharsets.UTF_8);
	
	//hash za pomoca metody bibliotekowej
	try{
		
		long start=System.currentTimeMillis();
	MessageDigest digest = MessageDigest.getInstance("SHA-256");
	//d³ugie obliczenia
	hashOutputByte = digest.digest(hashInputByte);
	long stop=System.currentTimeMillis();
	System.out.println("Czas wykonania fcji bibliotekowej:"+(stop-start));
	}
	catch(Exception e){
		System.out.println("Wybrano nieprawid³owy algorytm");
	}
	
	//konwersja tablicy byte to Stringa w celu wyswietlenia
	String hexStr = "";
    for (int i = 0; i < hashOutputByte.length; i++) {
        hexStr +=  Integer.toString( ( hashOutputByte[i] & 0xff ) + 0x100, 16).substring( 1 );
    }
	System.out.println(hexStr);
	
	
	
	byte[] hashOutputByte2=null;
	
	//hash za pomoca zaimplementowanej przez nas fcji skrótu
	
	long start2=System.currentTimeMillis();
	SHA256Digest sha = new SHA256Digest();
	//d³ugie obliczenia
	hashOutputByte2 = sha.digest(hashInputByte);
	long stop2=System.currentTimeMillis();
	System.out.println("Czas wykonania fcji naszej:"+(stop2-start2));
	
	String hexStr2 = "";
    for (int i = 0; i < hashOutputByte2.length; i++) {
        hexStr2 +=  Integer.toString( ( hashOutputByte2[i] & 0xff ) + 0x100, 16).substring( 1 );
    }
	System.out.println(hexStr2);
	
	if(hexStr.equals(hexStr2))
		System.out.println("bingoo!");
	
  
    
	}
}

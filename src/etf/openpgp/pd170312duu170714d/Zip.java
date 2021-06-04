package etf.openpgp.pd170312duu170714d;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.zip.*; 

public class Zip { 
	public static int sChunk = 8192; 

	public static byte[] compress(byte[] input_, String filename_) {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream);
		ZipEntry zipEntry = new ZipEntry(filename_);
		
		try {
			zipOutputStream.putNextEntry(zipEntry);
		    zipOutputStream.write(input_);
		    zipOutputStream.closeEntry();
		    zipOutputStream.close();
	  	} catch (IOException e) {
	  		System.out.println("Couldn't compress file.");
		}
      
		return byteArrayOutputStream.toByteArray();
	}

	public static byte[] decompress(byte[] input_) {
		ZipInputStream zipin = null; 
    	ByteArrayInputStream in = new ByteArrayInputStream(input_); 
    	zipin = new ZipInputStream(in); 

	    try { 
    		zipin.getNextEntry( ); 
    	} 
    	catch (IOException e) {}
    
	    byte[] buffer = new byte[sChunk]; 
	    ByteArrayOutputStream out = new ByteArrayOutputStream(); 
	    int length; 
	    
	    try {
			while ((length = zipin.read(buffer, 0, sChunk)) != -1) 
				out.write(buffer, 0, length);
		} catch (IOException e1) {
			System.out.println("Couldn't decompress file.");
		} 

	    try { zipin.close( ); } 
	    catch (IOException e) {} 
	    
	    return out.toByteArray();
	}
  
	public static void main(String[] args){
		System.out.println("Compressing...");
		byte[] compressedData = compress("Test, test 1 2 3... Ura!".getBytes(), "test.zip");
		System.out.println("Decompressing...");
		byte[] oreginalData = decompress(compressedData);
		System.out.println("Done!");
		System.out.println("Message: " + new String(oreginalData, StandardCharsets.UTF_8));
	}
}
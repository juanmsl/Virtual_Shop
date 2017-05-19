package Utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/** Class FileManager.java to represent
 * @author juanm */
public class FileManager {
	public static BufferedReader readFile(File archivo) {
		try {
			FileReader file = new FileReader(archivo);
			BufferedReader buffer = new BufferedReader(file);
			return buffer;
		}
		catch (FileNotFoundException e) {
			return null;
		}
	}
	
	public static String readLine(BufferedReader buffer) {
		String linea = "";
		try {
			linea = buffer.readLine();
			return linea;
		}
		catch (IOException e) {
			System.out.println(e.getMessage());
			return null;
		}
	}
	
	public static void closeBuffer(BufferedReader buffer) {
		try {
			buffer.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}

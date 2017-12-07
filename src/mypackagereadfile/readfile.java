package mypackagereadfile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class readfile {

	public static void main(String[] args) throws IOException {
		FileReader fileName=new FileReader("C:\\Users\\Ahmed Kamel\\workspace\\testcam\\log.txt");
		BufferedReader reader=new BufferedReader(fileName);
		String text="";
		String line=reader.readLine();
		while(line != null){
			text += line;
			line=reader.readLine();
		}
		System.out.println(text.charAt(text.length()-1));
		reader.close();
	}

}

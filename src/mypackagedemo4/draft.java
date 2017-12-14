package mypackagedemo4;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
public class draft {
	public static void main(String[] args) throws IOException {
		ArrayList<Integer> mylist = new ArrayList<Integer>(5);
		mylist.add(1);
		mylist.add(34);
		mylist.add(123);
		mylist.add(444);
		mylist.add(555);
		mylist.add(666);
		mylist.add(777);
		
		if(mylist.size()>=3){
			int x1=mylist.get(1);
			int x2=mylist.get(2);
			int x3=mylist.get(3);
			mylist.remove(0);
			mylist.set(0, x1);
			mylist.set(1, x2);
			mylist.set(2, x3);
			
			for (Integer x:mylist)
				System.out.println(x);
		}	
	}
}

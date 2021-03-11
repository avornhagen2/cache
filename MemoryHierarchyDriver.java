import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;



/***************************************************
* Program Title: CSCI 8150 Advanced Computer Architecture Project *
* Author: Austin Vornhagen and Lucas Asato *
* Class: CSCI 8150, Spring 2021 *
* Objective: Implement a memory 3-level memory hierarchy comprised of interacting L1 and L2 cache controllers and a memory. *
*****************************************************/
public class MemoryHierarchyDriver {

	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		CacheManager scheduler = new CacheManager();
		loadFileInput(args[0],scheduler.alq);
		scheduler.runCycles();
		
	}//end of main
	
	
	//cache manager method
	public static void cacheManager() {
		
	}
	
	public static void loadFileInput(String file, ArrayListQueue alq) {
		
		//read from file
		Scanner input = null;
		QueueObject messageAndWait = null;
		input = new Scanner(file);

		while(input.hasNextLine())
		{
			messageAndWait.setMessage(input.nextLine());
			alq.enqueue(0, messageAndWait);//enqueue from CPU to L1C
		}
		
		input.close();

		//first part is instruction, second part is location(memory address), third part is size(number of bytes)

	}//end of loadFileInput

}//end of class MemoryHierarchyDriver

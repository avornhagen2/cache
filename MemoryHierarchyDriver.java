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

	final private static int CPUtoL1C = 0;
	static ArrayList<String> Instructions = new ArrayList<String>();
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		CacheManager scheduler = new CacheManager();
		loadFileInput(args[0],scheduler.alq);
		scheduler.runCycles(args[1],Instructions);
		
	}//end of main
	
	public static void loadFileInput(String file, ArrayListQueue alq) {
		
		//read from file
		try {
			File myFile = new File(file);
			Scanner scannerReadFile = new Scanner(myFile);
			//Scanner input = null;
			
			//input = new Scanner(file);
			
			
			
			while(scannerReadFile.hasNextLine())
			{
				QueueObject messageAndWait = new QueueObject();
				messageAndWait.setMessage(scannerReadFile.nextLine());
				messageAndWait.setWait(true);
	            alq.enqueue(CPUtoL1C, messageAndWait);//enqueue from CPU to L1C
			}
			
			scannerReadFile.close();
		}catch (FileNotFoundException e){
			System.out.println("Memory Hierarchy: The file could not be found.");
			e.printStackTrace();
		}
		

		

		//first part is instruction, second part is location(memory address), third part is size(number of bytes)

	}//end of loadFileInput

}//end of class MemoryHierarchyDriver

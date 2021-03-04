import java.util.ArrayList;

public class CPUStub {

	public static void cpuStubMessage() {
		
		System.out.println("This is our CPU that issues a sequence of loads and stores.");
		
	}//end of cpuStubMessage
	
	
	//Get data from I/O
	public static ArrayList<String> loadFileInput() {
		
		//CPU Read Instructions
			//support 2 - 8 blocked processor instructions
		//CPU Write Instructions
			//support 2 - 8 blocked processor instructions
		//Address
		ArrayList<String> input = new ArrayList<String>();
		input.add("CPURead 1023 2");//first part is instruction, second part is location(memory address), third part is size(number of bytes)
		input.add("CPURead 1023 2");
		
		return input;
		
	}//end of loadFileInput
	
	//Send data to I/O
	public static void sendOutputResults() {
		
		//stream indicating the messages that were executed by the different units
		//value returned
		//write to external file (ask prof about this)
		//same format at input but what we receive from memory hierarchy
		//write to text file
		
	}//sendOutputResults
	
	//Send data to L1 Controller
	public static void storeFileInputInL1CacheController() {
		
		//enqueue to L1
		
	}//end of storeFileInputInL1CacheController
	
	//Get data from L1 Controller
	public static void readInputL1CacheController() {
		
		//dequeue from L1
		
	}//end of readInputL1CacheController
	
	

}//end of class CPUStub

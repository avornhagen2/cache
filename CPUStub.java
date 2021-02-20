
public class CPUStub {

	public static void cpuStubMessage() {
		
		System.out.println("This is our CPU that issues a sequence of loads and stores.");
		
	}//end of cpuStubMessage
	
	
	//Get data from I/O
	public static void loadFileInput() {
		
		//CPU Read Instructions
			//support 2 - 8 blocked processor instructions
		//CPU Write Instructions
			//support 2 - 8 blocked processor instructions
		//Address
		
	}//end of loadFileInput
	
	//Send data to I/O
	public static void sendOutputResults() {
		
		//stream indicating the messages that were executed by the different units
		//value returned
		//write to external file (ask prof about this)
		
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

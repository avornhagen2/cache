
public class L1CacheController {

	
	public ControllerObject Address;
	//public ControllerObject[4][4]; 
	
	//OFFSET = log2(width of block/smallest number of bytes accessed) = log2(32b/1b) = 5
			
	//INDEX = log2(cache size/(block size * number of ways)) = log2(8kb/(32*4)) = log2(2^13/2^7) = 6
	
	//TAG = 13 - (5 + 6) = 2
	
	
	public static void readFromCPU( ) {
		
		//dequeue from stub
		
	}// end of readFromCPU
	
	
	public static void writeToCPU() {
		
		//enqueue to stub
		
	}//end of Write to CPU
	
	
	public static void writeToL1Data() {
		
	}//end of writeToL1Data
	
	public static void readFromL1Data() {
		
	}//readFromL1Data
	
	
	
	
}// end of class L1CacheController

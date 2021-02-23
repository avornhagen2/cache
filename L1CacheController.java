import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public class L1CacheController {

	//we are going to be using FIFO for replacing data
	public ControllerObject Address;
	final public static int setSize = 4;
	final public int INDEX = 6;
	final public static int sets = 64;
	final public int tag = 6;
	static ControllerObject L1C[][] = new ControllerObject[sets][setSize];
	static L1Data L1D[][] = new L1Data[sets][setSize];
	static int[] fifoCounter = new int[sets];
	
	
	//OFFSET = log2(width of block/smallest number of bytes accessed) = log2(32b/1b) = 5
			
	//INDEX = log2(cache size/(block size * number of ways)) = log2(8kb/(32*4)) = log2(2^13/2^7) = 6
	
	//TAG = main memory size - (index + offset) = 17 - (5 + 6) = 6
	
	//lines = cache size/block size = 8KB/32B = 256B
	
	//Sets = lines/ways = 64B
	
	
	public static void readFromCPU() {
		
		//dequeue from stub
		Queue<ControllerObject> q = new LinkedList<>();
		
		//contents of L1C array
		//state, valid, 
		
		
	}// end of readFromCPU
	
	
	public static void writeToCPU() {
		
		//enqueue to stub
		
	}//end of Write to CPU
	
	
	public static void writeToL1Data() {
		
		//request a line from L1D
		//get a state of that line
		
	}//end of writeToL1Data
	
	public static void readFromL1Data( int tag, int index, int offset, int bytes) {
		
		//request a line from L1D
		ArrayList<ControllerObject> temp = new ArrayList<ControllerObject>();
		//ControllerObject temp;
		States states;
		
		//Arrays.fill(fifoCounter, 0);
		
//		L1Data[][] L1D = new L1Data();
		//LineObject line = new LineObject();
		
		for(int i=0; i < L1C[index].length;i++) {
			if(L1C[index][i].getValid() == true)
			{
				//store objects in L1D
				temp.add(L1C[index][i]);
				
				
				if(tag == L1C[index][i].getTag())
				{
					//check if it is a hit
					states = States.HIT;
					
				}//else 
				//{
//					//miss
//					if(L1C[index][i].getClean() == true)
//					{
//						states = States.MISSC;
//					}else {
//						states = States.MISSD;
//					}
//					//revisit when we understand MISSI
					
				//}

					
				//L1D array at [index][i].  block int array go to array index 0 + offset return value at index

			}//else {
//				states = States.MISSI;
//			}
		}
		
		if(temp.size() != 4) {
			states = States.MISSI;
		}else {
			
			if(L1C[index][fifoCounter[index]].getClean()) {
				states = States.MISSC;
			}else {
				states = States.MISSD;
			}
		
		}
		
//		is it valid? --> what is stage of control?
//				Hit = valid is true && requested address found in cache
//				Missc = valid is true && requested address not found in cache && line is clean state
//				Missd = valid is true && requested address not found in cache && line is dirty state
//				Missi = valid is false && requested address not found in cache
		
		//get a state of that line
		//switch statement for what to do based on the state
		
		
	}//readFromL1Data
	
	
	public static String readHit(int row, int column, int offset, int numberOfBytes) {
		
		String readResult = "";

		
		for(int i = 0; i < numberOfBytes; i++) {
			readResult += L1D[row][column].getByte(offset,row,column);
			//readFinal += readFinal.valueOf(readResult);
			offset++;//need to check if this still works the way we want
		}
		
		return readResult;
	}//end of readHit
	
	public static String readMISSC() {
		//go to L2C
		return null;
	}
	
	public static String readMISSD() {
		//Victimize
		return null;
	}
	
	public static String readMISSI() {
		//go to L2C
		return null;
	}
	
	public static String writeMISSC() {
		return null;
		
	}
	
	public static String writeMISSD() {
		return null;
	}
	
	public static String writeMISSI() {
		return null;
	}
	
}// end of class L1CacheController




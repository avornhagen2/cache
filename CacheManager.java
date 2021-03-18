import java.io.FileWriter;
import java.io.IOException;

public class CacheManager {

	public ArrayListQueue alq = new ArrayListQueue();
	private int time;
	CPUStub cpu = new CPUStub(alq);
	L1Data L1D = new L1Data(alq);
	L2Cache L2 = new L2Cache(alq);
	L1CacheController L1C = new L1CacheController(alq,L1D);
	MemoryStub memory = new MemoryStub(alq);
	private boolean flag = true;
	private int emptyCount = 0;
	final private static int CPUtoL1C = 0;
	final private static int L1CtoL1D = 1;
	final private static int L1CtoL2 = 2;
	final private static int L2toDRAM = 3;
	final private static int DRAMtoL2 = 4;
	final private static int L2toL1C = 5;
	final private static int L1DtoL1C = 6;
	final private static int L1CtoCPU = 7;
	
	public CacheManager()
	{
		time = 0;
		
		//create queues
		for(int i = 0; i < 8; i++)
		{
			alq.addQueue();
		}
	}
	
	
	public void runCycles(String fileName)
	{
		//write headers
		try {
			FileWriter myWriter = new FileWriter(fileName);
			myWriter.write(String.format("%20s %20s %20s %20s %20s %20s %20s %20s", "CPU to L1C", "L1C to L1D", "L1C to L2", "L2 to DRAM","DRAM to L2","L2 to L1C","L1D to L1C","L1C to CPU"));
			myWriter.close();
		}catch(IOException e) {
			System.out.println("An error occurred");
			e.printStackTrace();
		}
		
		while(flag)
		{
			
			//add line to table
			try {
				FileWriter myWriter = new FileWriter(fileName);
				myWriter.write(String.format("%20s %20s %20s %20s %20s %20s %20s %20s", alq.getHeadOfQueue(CPUtoL1C).getMessage(), alq.getHeadOfQueue(L1CtoL1D).getMessage(), alq.getHeadOfQueue(L1CtoL2).getMessage(), alq.getHeadOfQueue(L2toDRAM).getMessage(),alq.getHeadOfQueue(DRAMtoL2).getMessage(),alq.getHeadOfQueue(L2toL1C).getMessage(),alq.getHeadOfQueue(L1DtoL1C).getMessage(),alq.getHeadOfQueue(L1CtoCPU).getMessage()));
				myWriter.close();
			}catch(IOException e) {
				System.out.println("An error occurred");
				e.printStackTrace();
			}
			
			
			emptyCount = 0;
			//cpu.storeFileInputInL1CacheController();
			
			L1C.run();
			L1D.run();
			L2.run();
			memory.run();
			cpu.run();
			//update set all waits to false
			
			
			
			for(int i = 0; i < 8; i++) {
				if(alq.isSingleQueueEmpty(i))
				{
					emptyCount++;
				}
			}
			
			if(emptyCount == 8)
			{
				flag = false;
			}
	
			time++;
		}
		
	}
	

	
}//end of CacheManager

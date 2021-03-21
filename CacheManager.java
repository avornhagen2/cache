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
		//populate DRAM
		L1C.populateL1C();
		L1D.populateL1D();
		L2.populateL2();
		memory.populateDRAM();
		//write headers
		try {
			FileWriter myWriter = new FileWriter(fileName);
			myWriter.write(String.format("%5s | %20s | %20s | %20s | %20s | %20s | %20s | %20s | %20s %n"
					, "Cycle"
					, "CPU to L1C"
					, "L1C to L1D"
					, "L1C to L2", "L2 to DRAM","DRAM to L2","L2 to L1C","L1D to L1C","L1C to CPU"));
			
			while(flag)
			{
				myWriter.write(String.format("%5s | %20s | %20s | %20s | %20s | %20s | %20s | %20s | %20s %n"
						, time
						, handleMessage(alq.getHeadOfQueue(CPUtoL1C))
						, handleMessage(alq.getHeadOfQueue(L1CtoL1D))
						, handleMessage(alq.getHeadOfQueue(L1CtoL2))
						, handleMessage(alq.getHeadOfQueue(L2toDRAM))
						, handleMessage(alq.getHeadOfQueue(DRAMtoL2))
						, handleMessage(alq.getHeadOfQueue(L2toL1C))
						, handleMessage(alq.getHeadOfQueue(L1DtoL1C))
						, handleMessage(alq.getHeadOfQueue(L1CtoCPU))));
				
				emptyCount = 0;
				//cpu.storeFileInputInL1CacheController();
				
				L1C.run();
				L1D.run();
				L2.run();
				memory.run();
				cpu.run();
				
				//update set all waits to false
				for(int i = 0; i < alq.listOfQueues.size();i++)
				{
					if(alq.getHeadOfQueue(i) != null)
					{
						alq.getHeadOfQueue(i).setWait(false);
					}
					
				}
				
				
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
				
				System.out.printf("%5s | %20s | %20s | %20s | %20s | %20s | %20s | %20s | %20s %n"
					, "Cycle"
					, "CPU to L1C"
					, "L1C to L1D"
					, "L1C to L2", "L2 to DRAM","DRAM to L2","L2 to L1C","L1D to L1C","L1C to CPU");
				
				time++;
				System.out.printf("%5s | %20s | %20s | %20s | %20s | %20s | %20s | %20s | %20s %n"
						, time
						, handleMessage(alq.getHeadOfQueue(CPUtoL1C))
						, handleMessage(alq.getHeadOfQueue(L1CtoL1D))
						, handleMessage(alq.getHeadOfQueue(L1CtoL2))
						, handleMessage(alq.getHeadOfQueue(L2toDRAM))
						, handleMessage(alq.getHeadOfQueue(DRAMtoL2))
						, handleMessage(alq.getHeadOfQueue(L2toL1C))
						, handleMessage(alq.getHeadOfQueue(L1DtoL1C))
						, handleMessage(alq.getHeadOfQueue(L1CtoCPU)));
			}
			myWriter.close();
		}catch(IOException e) {
			System.out.println("An error occurred");
			e.printStackTrace();
		}
		
		
		
	}//end of run cycles
	
	public String handleMessage(QueueObject q)
	{
		if(q == null)
		{
			return "";
		}else
		{
			return q.getMessage();
		}
	}
	
}//end of CacheManager

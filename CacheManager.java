import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class CacheManager {

	public ArrayListQueue alq = new ArrayListQueue();
	private int time;
	CPUStub cpu = new CPUStub(alq);
	boolean mutualInclusion = false;
	L2Cache L2 = new L2Cache(alq,mutualInclusion);
	ArrayList<Integer> busyAddresses = new ArrayList<Integer>();
	boolean[][] busyCheckL1 = new boolean[64][4];
	L1Data L1D = new L1Data(alq,busyAddresses,busyCheckL1);
	L1CacheController L1C = new L1CacheController(alq,L1D,busyAddresses, mutualInclusion,busyCheckL1);
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
	
	
	public void runCycles(String fileName, ArrayList<String> Instructions)
	{
		//populate DRAM
		L1C.populateL1C();
		L1D.populateL1D();
		L2.populateL2();
		memory.populateDRAM();
		
		//write headers
		try {
			FileWriter myWriter = new FileWriter(fileName);
			
			myWriter.write(String.format("%5s | %25s | %25s | %25s | %25s | %25s | %25s | %25s | %25s | %25s | %25s %n"
					, "Cycle"
					, "CPU to L1C"
					, "InstructionsInL1"
					, "L1C to L1D"
					, "L1C to L2"
					, "InstructionsInL2"
					, "L2 to DRAM"
					,"DRAM to L2"
					,"L2 to L1C"
					,"L1D to L1C"
					,"L1C to CPU"));
			
			
			
			while(flag)
			{
				
				
				emptyCount = 0;
				//cpu.storeFileInputInL1CacheController();
				L1C.run();
				L1D.run();
				L2.run();
				memory.run();
				String CPUOutput = cpu.run();
				
				if(!CPUOutput.equals(""))
				{
					myWriter.write(String.format("----------------------------------------------------------------------------%n"+
							"CPU Output: %50s%n"+"----------------------------------------------------------------------------%n", CPUOutput));
					
				}
				//update set all waits to false
				for(int i = 0; i < alq.listOfQueues.size();i++)
				{
					if(alq.getHeadOfQueue(i) != null)
					{
						alq.getHeadOfQueue(i).setWait(false);
					}
					
				}
				
				
				
				if(time == 100)
				{
					int i = 0;
					
				}
				if(time == 44)
				{
					int i = 0;
					
				}
				if(time == 500)
				{
					int i = 0;
					
				}
				for(int i = 0; i < 8; i++) {
					if(alq.isSingleQueueEmpty(i) && L1C.InstructionsL1.isEmpty() && L2.InstructionsL2.isEmpty())
					{
						emptyCount++;
					}
				}
				
				if(emptyCount == 8)
				{
					flag = false;
					System.out.println("Number of Busy addresses: " + busyAddresses.size());
				}
				
				System.out.printf("%5s | %20s | %20s | %20s | %20s | %20s | %20s | %20s | %20s | %20s | %20s %n"
					, "Cycle"
					, "CPU to L1C"
					, "InstructionsInL1"
					, "L1C to L1D"
					, "L1C to L2"
					, "InstructionsInL2"
					, "L2 to DRAM"
					,"DRAM to L2"
					,"L2 to L1C"
					,"L1D to L1C"
					,"L1C to CPU");
				
				myWriter.write(String.format("%5s | %25s | %25s | %25s | %25s | %25s | %25s | %25s | %25s | %25s | %25s %n"
						, time
						, handleMessage(alq.getHeadOfQueue(CPUtoL1C),CPUtoL1C)
						, InstructionOutputL1()
						, handleMessage(alq.getHeadOfQueue(L1CtoL1D),L1CtoL1D)
						, handleMessage(alq.getHeadOfQueue(L1CtoL2),L1CtoL2)//add in column for InstructionsL1
						, InstructionOutputL2()
						, handleMessage(alq.getHeadOfQueue(L2toDRAM),L2toDRAM)
						, handleMessage(alq.getHeadOfQueue(DRAMtoL2),DRAMtoL2)
						, handleMessage(alq.getHeadOfQueue(L2toL1C),L2toL1C)
						, handleMessage(alq.getHeadOfQueue(L1DtoL1C),L1DtoL1C)
						, handleMessage(alq.getHeadOfQueue(L1CtoCPU),L1CtoCPU)));
				
				time++;
				System.out.printf("%5s | %20s | %20s | %20s | %20s | %20s | %20s | %20s | %20s | %20s | %20s %n"
						, time
						, handleMessage(alq.getHeadOfQueue(CPUtoL1C),CPUtoL1C)
						, InstructionOutputL1()
						, handleMessage(alq.getHeadOfQueue(L1CtoL1D),L1CtoL1D)
						, handleMessage(alq.getHeadOfQueue(L1CtoL2),L1CtoL2)
						, InstructionOutputL2()
						, handleMessage(alq.getHeadOfQueue(L2toDRAM),L2toDRAM)
						, handleMessage(alq.getHeadOfQueue(DRAMtoL2),DRAMtoL2)
						, handleMessage(alq.getHeadOfQueue(L2toL1C),L2toL1C)
						, handleMessage(alq.getHeadOfQueue(L1DtoL1C),L1DtoL1C)
						, handleMessage(alq.getHeadOfQueue(L1CtoCPU),L1CtoCPU));
				
				
			}
			myWriter.close();
		}catch(IOException e) {
			System.out.println("An error occurred");
			e.printStackTrace();
		}
		
		
		
	}//end of run cycles
	
	public String handleMessage(QueueObject q, int queueNumber)
	{
		if(q == null)
		{
			return "";
		}else
		{
			if(queueNumber == L2toDRAM || queueNumber == DRAMtoL2)
			{
				return q.getMessage() + "-" + alq.getHeadOfQueueBus(queueNumber).getBusNumber();
			}else {
				return q.getMessage();
			}
			
		}
	}
	
	public String InstructionOutputL1()
	{
		if(L1C.InstructionsL1.isEmpty())
		{
			return "";
		}else
		{
			return L1C.InstructionsL1.get(0);
		}
	}

	public String InstructionOutputL2()
	{
		if(L2.InstructionsL2.isEmpty()) {
			return "";
		}else {
			return L2.InstructionsL2.get(0).getMessage();
		}
	}
}//end of CacheManager

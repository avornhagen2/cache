
public class CacheManager {

	public ArrayListQueue alq = new ArrayListQueue();
	private int time;
	CPUStub cpu = new CPUStub();
	L1CacheController L1C;
	L1Data L1D;
	L2Cache L2 = new L2Cache();
	MemoryStub memory = new MemoryStub();
	private boolean flag = true;
	private int emptyCount = 0;
	
	public CacheManager()
	{
		time = 0;
		
		//create queues
		for(int i = 0; i < 8; i++)
		{
			alq.addQueue();
		}
		
		
	}
	
	public void runCycles()
	{
		
		while(flag)
		{
			emptyCount = 0;
			//cpu.storeFileInputInL1CacheController();
			
			//
			
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

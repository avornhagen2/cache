import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Queue;
import java.util.Scanner;



public class CPUStub {

	final private static int L1CtoCPU = 7;
	ArrayListQueue alq;
	
	public CPUStub(ArrayListQueue alq)
	{
		this.alq = alq;
	}
	
	public static void cpuStubMessage() {
		
		System.out.println("This is our CPU that issues a sequence of loads and stores.");
		
	}//end of cpuStubMessage
	
	
	public void run()
	{
		if(!alq.isSingleQueueEmpty(L1CtoCPU))
		{
			QueueObject messageAndWait = alq.dequeue(L1CtoCPU);
			String input = messageAndWait.getMessage();
			
			if(input == null || input == "")
			{
				System.out.println("bad message null or empty string");
			}else {
				System.out.println(input);
			}
			
		}
	}
	
	

	
	

}//end of class CPUStub

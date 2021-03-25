import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Queue;
import java.util.Scanner;



public class CPUStub {

	final private static int CPUtoL1C = 0;
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
		
		
		
		if(!alq.isSingleQueueEmpty(L1CtoCPU) && alq.getHeadOfQueueWait(L1CtoCPU) == false)
		{
			QueueObject messageAndWait = alq.dequeue(L1CtoCPU);
			String input = getOutputMessage(messageAndWait);
			String message = messageAndWait.getMessage();
			String[] splitInput = message.trim().split(" ");
			//int AddressInput = Integer.parseInt(splitInput[1].substring(0, 4));
			if(splitInput[0].equals("CPURead") || splitInput[0].equals("SendToCPU"))
			{
				if(input == null || input == "")
				{
					System.out.println("bad message null or empty string");
				}else {
					System.out.println(input);
				}
				//busyAddresses.remove(busyAddresses.indexOf(AddressInput));
			}
			
			//busyAddresses.remove(busyAddresses.indexOf(AddressInput));
		}
	}
	
	public String getOutputMessage(QueueObject qo)
	{
		String output = "";
		int number = ((QueueObjectChild)qo).block.getBlock().length;
		for(int i = 0; i < number; i++)//ERROR HERE
		{
			output = output + ((QueueObjectChild)qo).block.getBlockValue(i);
		}
		return output;
	}


	
	

}//end of class CPUStub

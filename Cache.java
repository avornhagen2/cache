import java.util.ArrayList;

public class Cache {

	public static int NUMBER_SETS;
	public static int SET_SIZE;
	public int INDEX;
	public int TAG;
	

	
	
	
	
	public static int getNUMBER_SETS() {
		return NUMBER_SETS;
	}




	public static void setNUMBER_SETS(int nUMBER_SETS) {
		NUMBER_SETS = nUMBER_SETS;
	}




	public static int getSET_SIZE() {
		return SET_SIZE;
	}




	public static void setSET_SIZE(int sET_SIZE) {
		SET_SIZE = sET_SIZE;
	}

	public void sendRequestToDestination(QueueObject newHead, int destination, ArrayListQueue alq) 
	{
		ArrayList<QueueObject> oldQueueObjectsHolder = new ArrayList<QueueObject>();
		//mutualInclusion = true;
		while(!alq.isSingleQueueEmpty(destination))
		{
			oldQueueObjectsHolder.add(alq.dequeue(destination));
		}
		
		alq.enqueue(destination, newHead);
		
		for(int i = 0; i < oldQueueObjectsHolder.size(); i++)
		{
			alq.enqueue(destination, oldQueueObjectsHolder.get(i));
		}
	}

	
}//end of cache

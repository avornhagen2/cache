import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class CacheScheduler {

	int time = 0;
	
	List<Queue<Object>> listOfQueues = new ArrayList<Queue<Object>>();
	
	public void addQueue() 
	{
		listOfQueues.add(new LinkedList<Object>());
	}
	
	public void addToSpecificQueue(int indexOfQueue, Object x)
	{
		listOfQueues.get(indexOfQueue).add(x);
	}
	
//	public void removeFromSpecificQueue(int indexOfQueue, Object x)
//	{
//		listOfQueues.get(indexOfQueue).remove(x);
//	}
	
	public Object getFrontOfSingleQueue(int indexOfQueue)
	{
		return listOfQueues.get(indexOfQueue).poll();
	}
	
	public void removeHeadOfQueue(int indexOfQueue)
	{
		listOfQueues.get(indexOfQueue).remove();
	}
	
	public void makeSingleQueueEmpty(int indexOfQueue) 
	{
		listOfQueues.get(indexOfQueue).clear();
	}
	
	public boolean isSingleQueueEmpty(int indexOfQueue)
	{
		return listOfQueues.get(indexOfQueue).isEmpty();
	}
	
}//end of cache scheduler







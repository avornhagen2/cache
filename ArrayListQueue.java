import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class ArrayListQueue {

	int time = 0;
	
	List<Queue<QueueObject>> listOfQueues = new ArrayList<Queue<QueueObject>>();
	
	
	
	public void addQueue() 
	{
		listOfQueues.add(new LinkedList<QueueObject>());
	}
	
	public void enqueue(int indexOfQueue, QueueObject x)
	{
		listOfQueues.get(indexOfQueue).add(x);
	}
	
	
	public QueueObject dequeue(int indexOfQueue)
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
	
	public void setHeadOfQueueWait(int indexOfQueue, boolean input)
	{
		listOfQueues.get(indexOfQueue).peek().setWait(input);
	}
	
	public boolean getHeadOfQueueWait(int indexOfQueue)
	{
		return listOfQueues.get(indexOfQueue).peek().getWait();
	}
	
	public QueueObject getHeadOfQueue(int indexOfQueue)
	{
		return listOfQueues.get(indexOfQueue).peek();
	}
	
}//end of ArrayListQueue

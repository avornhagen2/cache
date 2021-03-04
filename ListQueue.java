import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

public class ListQueue<String> {
	
	private ListNode front;
	private ListNode back;
	
	public ListQueue() {
		front = back = null;
	}
	
	public boolean isEmpty() {
		return front == null;
	}
	 
	public void enqueue(String x) {
		if(isEmpty())
		{
			back = front = new ListNode(x);
		}else {
			back = back.next = new ListNode<String>(x);
		}
	}
	
	public String dequeue() {
		if(isEmpty())
		{
			throw new Exception("ListQueue dequeue");
		}
		String returnValue = front.element;
		front = front.next;
		return returnValue;
	}
	
	public String getFront() {
		if(isEmpty())
		{
			throw new UnderflowException("List getFront");
		}
		return front.element;
	}
	
	public void makeEmpty() {
		front = null;
		back = null;
	}
	
	
}//end of ListQueue


public class QueueObjectChild extends QueueObject{

	
	LineObject block;
	
	public QueueObjectChild(int size)
	{	
		block = new LineObject(size);
		this.block.populateLineObject();
	}
	
}//end of QueueObjectchild

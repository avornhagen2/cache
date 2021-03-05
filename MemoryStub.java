
public class MemoryStub {

	final int TAG = 0;
	final int setSize = 4000;
	final public int TAG = 64; //since index = 2^6, so the ideia is 64*tag + index// need to check
	private LineObject[] DRAM = new LineObject[setSize];
	//private LineObject current;
	private LineObject updateValue;
	
	public LineObject getBlock(int index, int tag)
	{
		int row = tag * TAG + index;
		return DRAM[row];
	}
	
	//The idea of set and get valuesBus is that the Scheduler is going to ask for the values in parts
	//giving the initial value position and the total size of the bus. The input and output are Strings
	//that need to be splited and trim before used.
	// if whanted we can change the communication to be instead of string use arrays. 
	public void setValuesBus(int index, int tag, int startValue, int busSize, String input)
	{
		int row = tag * TAG + index;
		String[] split = input.trim().split(" ");
		for (int i = 0; i < busSize; i++)
		{
			DRAM[row].setBlockValue(startValue + i, split[i]); //set value for the array string once at a time 
		}
		
	}

	public String getValuesBus (int index, int tag, int startValue, int busSize) //can change the output to an array if needed
	{
		LineObject current = getBlock(index, tag);
		String output = "";
		for (int i = 0; i < busSize; i++)
		{
			output += current[startValue + i] + " "; //each value is going to be separated by an space, need to remember to separate the string latter
		}
		return output; 
	}
	

}//end of memory stub

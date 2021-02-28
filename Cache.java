
public class Cache {

	public static int NUMBER_SETS;
	public static int SET_SIZE;
	public int INDEX;
	public int TAG;
	public 
	static int[] fifoCounter = new int[NUMBER_SETS];
	
	
	
	
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




	public static int[] getFifoCounter() {
		return fifoCounter;
	}




	public static void setFifoCounter(int[] fifoCounter) {
		Cache.fifoCounter = fifoCounter;
	}




	//Whenever called add one to the counter in the respective row
	// if the value of the counter is bigger than setSIZE ste counter to zero
	public static void fifoStepper(int row)
	{
		fifoCounter[row] += 1;
		if (fifoCounter[row] >= SET_SIZE)
		{
			fifoCounter[row] = 0;
		}
	}
			
	public static States check_State(int tag, int index, ControllerObject[][] cache2D)
	{
		int numberValid = 0;
		States states = null;

		for(int i=0; i < cache2D[index].length;i++)
		{
			if(cache2D[index][i].getValid() == true)
			{
				//store objects in L1D
				//temp.add(L1C[index][i]);
				numberValid++;

				if(tag == cache2D[index][i].getTag())
				{
					//check if it is a hit
					states = States.HIT;
					break;
				}
			}
		}
		if(states != States.HIT)
		{
			if (numberValid != SET_SIZE) {
				states = States.MISSI;
			}
			else
				{

				if (cache2D[index][fifoCounter[index]].getClean())
				{
					states = States.MISSC;
				}
				else
				{
					states = States.MISSD;
				}
			}
		}
		return states;
	}
}//end of cache

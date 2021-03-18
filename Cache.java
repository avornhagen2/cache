
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

//have to change check state for L2
//	public static States check_State(int index, int tag, ControllerObject[][] cache2D)
//	{
//		int numberValid = 0;
//		States states = null;
//
//		for(int i=0; i < cache2D[index].length;i++)
//		{
//			if(cache2D[index][i].getValid() == true)
//			{
//				//store objects in L1D
//				//temp.add(L1C[index][i]);
//				numberValid++;
//
//				if(tag == cache2D[index][i].getTag())
//				{
//					//check if it is a hit
//					states = States.HIT;
//					COLUMN = i;
//					break;
//				}
//			}
//		}
//		if(states != States.HIT)
//		{
//			if (numberValid != SET_SIZE) {
//				states = States.MISSI;
//			}
//			else
//			{
//
//				if (cache2D[index][fifoCounter[index]].getClean())//change fifocounter[index]
//				{
//					states = States.MISSC;
//				}
//				else
//				{
//					states = States.MISSD;
//				}
//			COLUMN = fifoCounter[index];
//			}
//			
//		}
//		return states;
//	}//end of check state
	

	
}//end of cache

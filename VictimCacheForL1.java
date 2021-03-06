

public class VictimCacheForL1 extends Cache {

	final int row = 2;
	final int column = 2;
	final int setSize = 2;
	int[][] victimInstruction = new int[row][column];
	LineObject[] victimData = new LineObject[setSize];
	LRU lru = new LRU(row);
	int[] valid = new int[] {-1,-1};
	
	
	
//	public VictimCacheForL1(int[][] victimInstruction, LineObject[] victimData) {
//		super();
//		this.victimInstruction = victimInstruction;
//		this.victimData = victimData;
//	}
//	
	
//	public int[][] getVictimInstruction() {
//		return victimInstruction;
//	}
//	
//	public void setVictimInstruction(int[][] victimInstruction) {
//		this.victimInstruction = victimInstruction;
//	}
//	
	public void setVictimCacheValueDirectly(int Tag, int Index, LineObject data)
	{
		if(isFull())
		{
			int record = lru.LRUMissCD();//make sure this works with static
			
			victimInstruction[record][0] = Tag; 
			victimInstruction[record][1] = Index;
			victimData[record].setBlock(data.getBlock());
			victimData[record].setAddress(data.getAddress());

		}else {
			
			int record = lru.LRUMissI();
			
			victimInstruction[record][0] = Tag; 
			victimInstruction[record][1] = Index;
			valid[record] = 1;
			victimData[record].setBlock(data.getBlock());
			victimData[record].setAddress(data.getAddress());
		}
		System.out.println("Set Victim Cache Success");
	}
	
	public boolean checkValue(int Tag, int Index)
	{
		boolean exists = false;
		for(int i = 0; i < row; i++)
		{
			if(victimInstruction[i][0] == Tag && victimInstruction[i][1] == Index)
			{
				exists = true;
			}
		}
		return exists;
	}
	
	public boolean isFull()
	{
		boolean full = true;
		for(int i = 0; i < setSize; i++)
		{
			if(valid[i] == -1)
			{
				full = false;
				break;
			}
		}
		return full;
	}
	
	public LineObject getVictimCacheValue(int Tag, int Index)
	{
		LineObject output = null;
		for(int i = 0; i < row; i++)
		{
			if(victimInstruction[i][0] == Tag && victimInstruction[i][1] == Index)
			{
				output = victimData[i];
				victimData[i] = new LineObject(32);
				victimInstruction[i][0] = -1;
				victimInstruction[i][1] = -1;
				valid[i] = -1;
				lru.LRU[i] = -1;
			}
		}
		System.out.println("Read from Victim Cache Success");
		return output;
	}
	
//	public void victimLRU()
//	{
//		
//	}
//	
//	public void setVictimValue(int[] data, String[] data) {
//		victimInstruction[] = data;
//		victimData[index].setBlock(data);
//	}
//	
//	public LineObject[] getVictimData() {
//		return victimData;
//	}
//	
////	public void setVictimData(LineObject[] victimData) {
////		this.victimData = victimData;
////	}
//	
//	public void setVictimDataValue(int index, String[] data) {
//		victimData[index].setBlock(data);
//	}
	
	
	
}// end of victimcacheforL1

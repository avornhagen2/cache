

public class VictimCacheForL1 extends Cache {

	final int row = 2;
	final int column = 2;
	final int setSize = 2;
	int[][] victimInstruction = new int[row][column];
	LineObject[] victimData = new LineObject[setSize];
	LRU[] lru = new LRU[row];
	
	
	
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
	public void check(int tag, int index)
	{
		for(int i = 0; i < victimInstruction.length; i++)
		{
			if(tag == victimInstruction[i][0] && index == victimInstruction[i][1])
			{
				//send to L1C and L1D
				
			}
		}
	}
	
	public void victimLRU()
	{
		
	}
	
	public void setVictimValue(int[] data, String[] data) {
		victimInstruction[] = data;
		victimData[index].setBlock(data);
	}
	
	public LineObject[] getVictimData() {
		return victimData;
	}
	
//	public void setVictimData(LineObject[] victimData) {
//		this.victimData = victimData;
//	}
	
	public void setVictimDataValue(int index, String[] data) {
		victimData[index].setBlock(data);
	}
	
	
	
}// end of victimcacheforL1

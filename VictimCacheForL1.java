
public class VictimCacheForL1 extends Cache {

	final int row = 2;
	final int column = 2;
	final int setSize = 2;
	int[][] victimInstruction = new int[row][column];
	LineObject[] victimData = new LineObject[setSize];
	
	
	public VictimCacheForL1(int[][] victimInstruction, LineObject[] victimData) {
		super();
		this.victimInstruction = victimInstruction;
		this.victimData = victimData;
	}
	
	
	public int[][] getVictimInstruction() {
		return victimInstruction;
	}
	
	public void setVictimInstruction(int[][] victimInstruction) {
		this.victimInstruction = victimInstruction;
	}
	
	public void setVictimInstructionValue(int row, int column, int[] data) {
		victimInstruction[row] = data;
	}
	
	public LineObject[] getVictimData() {
		return victimData;
	}
	
	public void setVictimData(LineObject[] victimData) {
		this.victimData = victimData;
	}
	
	public void setVictimDataValue(int index, LineObject data) {
		victimData[index] = data;
	}
	
}// end of victimcacheforL1

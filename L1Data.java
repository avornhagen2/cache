
public class L1Data {

	//receive offset
	//L1CacheController L1C = new L1CacheController();
	private LineObject[][] L1D;
	//String[][][] L1D = new String[L1C.NUMBER_SETS][L1C.SET_SIZE][blockSize];
	
	
	
	
	public L1Data(int numberOfSets, int setSize) {
		super();
		L1D = new LineObject[numberOfSets][setSize];
	}
	



	public void setL1DValue(String input, int row, int column,int offset) {
		
		L1D[row][column].setBlockValue(input,offset);
	}


	public LineObject getL1D() {
		return null;
	}




	public String getL1DValue (int row, int column, int byteIndex){
		return L1D[row][column].getBlock()[byteIndex];
	}


	


	
	
}//end of L1Data

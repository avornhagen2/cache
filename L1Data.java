
public class L1Data {

	//receive offset
	final int blockSize = 32;
	L1CacheController L1C = new L1CacheController();
	String[][][] L1D = new String[L1C.NUMBER_SETS][L1C.SET_SIZE][blockSize];
	
	
	
	
	public L1Data(L1CacheController l1c, String[][][] l1d) {
		super();
		L1C = l1c;
		L1D = l1d;
	}
	






	public L1CacheController getL1C() {
		return L1C;
	}





	public void setL1C(L1CacheController l1c) {
		L1C = l1c;
	}





	public String[][][] getL1D() {
		return L1D;
	}

	public String getL1DValue (int row, int column, int byteIndex){
		return L1D[row][column][byteIndex];
	}
	


	public void setL1D(String[][][] l1d) {
		L1D = l1d;
	}






	
	
}//end of L1Data

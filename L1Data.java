
public class L1Data {

	//receive offset
	L1CacheController L1C = new L1CacheController();
	
	ControllerObject L1D[][] = new ControllerObject[L1C.sets][L1C.setSize];
	
	
	
	
	public L1Data(L1CacheController l1c, ControllerObject[][] l1d) {
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





	public ControllerObject[][] getL1D() {
		return L1D;
	}


	


	public void setL1D(ControllerObject[][] l1d) {
		L1D = l1d;
	}



	public int getByte(int offset, int row, int column) {
		LineObject temp = new LineObject();
		temp = L1D[row][column];
		int value = temp.getBlock().getvalue(offset);
		return value;
	}


	
	
}//end of L1Data

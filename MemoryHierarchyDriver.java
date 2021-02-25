import java.util.ArrayList;

/***************************************************
* Program Title: CSCI 8150 Advanced Computer Architecture Project *
* Author: Austin Vornhagen and Lucas Asato *
* Class: CSCI 8150, Spring 2021 *
* Objective: Implement a memory 3-level memory hierarchy comprised of interacting L1 and L2 cache controllers and a memory. *
*****************************************************/
public class MemoryHierarchyDriver {

	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		String[] DRAM = null;
		
		CPUStub cpu = new CPUStub();
		L1CacheController L1C = new L1CacheController();
		//L1D
		//L2C
		//L2D
		//memory stub
		
		ArrayList<String> inputs = new ArrayList<String>();
		
		inputs = cpu.loadFileInput();
		
		L1C.readFromCPU();
		
	}//end of main
	
	
	//cache manager method
	public static void cacheManager() {
		
	}

}//end of class MemoryHierarchyDriver

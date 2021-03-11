
public class QueueObject {

	private boolean wait = false;
	private String message = "";
	private int transactionL1 = -1;
	private int transactionL2 = -1;
	
	public boolean getWait() {
		return wait;
	}
	public void setWait(boolean wait) {
		this.wait = wait;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	public int getTransactionL1() {
		return transactionL1;
	}

	public void setTransactionL1(int transactionL1) {
		this.transactionL1 = transactionL1;
	}

	public int getTransactionL2() {
		return transactionL2;
	}

	public void setTransactionL2(int transactionL2) {
		this.transactionL2 = transactionL2;
	}
	
}//end of Queue Object

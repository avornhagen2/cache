import java.util.Arrays;

public class LRU {

	private int maxSize;
	public int[] LRU;
	
	public LRU(int maxSize)
	{
		this.maxSize = maxSize;
		LRU = new int[maxSize];
		Arrays.fill(LRU,  -1);
	}
	
	public int LRUMissCD()
	{
		int header = LRU[0];
		
		for(int i = 0; i < maxSize - 1;i++)
		{
			LRU[i] = LRU[i+1];
		}
		LRU[maxSize-1] = header;
		return header;
	}
	
	public int LRUMissI()
	{
		int output = -1;
		for(int i = 0; i < maxSize; i++)
		{
			if(LRU[i] == -1)
			{
				LRU[i] = i;
				output = i;
				break;
			}
		}
		return output;
	}
	
	public int LRUHit(int hitColumn)
	{
		int temp = 0;
		int count = 0;
		for(int i = 0; i < maxSize; i++)
		{
			if(LRU[i] == hitColumn)
			{
				temp = LRU[i];
				count = i;
				break;
			}
			
		}
		
		for(int i = count; i < maxSize-1; i++)
		{
			LRU[i] = LRU[i+1];
		}
		LRU[maxSize-1] = temp;
		return temp;
	}
	
	public int tail()
	{
		return LRU[maxSize - 1];
	}
	
	public int head()
	{
		return LRU[0];
	}
	
}//end of LRU

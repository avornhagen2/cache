import java.util.Arrays;

public class LRU {

	private static int maxSize;
	public static int[] LRU;
	
	public LRU(int maxSize)
	{
		this.maxSize = maxSize;
		LRU = new int[maxSize];
		Arrays.fill(LRU,  -1);
	}
	
	public static int LRUMissCD()
	{
		int header = LRU[0];
		
		for(int i = 0; i < maxSize - 1;i++)
		{
			LRU[i] = LRU[i+1];
		}
		LRU[maxSize-1] = header;
		return header;
	}
	
	public static int LRUMissI(int input)
	{
		
		for(int i = 0; i < maxSize; i++)
		{
			if(LRU[i] == -1)
			{
				LRU[i] = input;
				break;
			}
		}
		return input;
	}
	
	public static int LRUHit(int hitColumn)
	{
		int temp = 0;
		
		for(int i = 0; i < maxSize; i++)
		{
			if(LRU[i] == hitColumn)
			{
				temp = i;
			}else
			{
				temp = -1;//for debugging
			}
		}
		
		for(int i = temp; i < maxSize-1; i++)
		{
			LRU[i] = LRU[i+1];
		}
		return temp;
	}
	
	public static int tail()
	{
		return LRU[maxSize - 1];
	}
	
	public static void victimLRU(int input)
	{
		LRUMissI(input);
		
	}
	
}//end of LRU

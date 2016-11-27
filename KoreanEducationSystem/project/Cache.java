package project;

import java.util.ArrayList;

public class Cache 
{
	private ArrayList<String[]> cache;
	
	public Cache()
	{
		cache = new ArrayList<String[]>();
	}
	
	public int find(String eojeol)
	{
		int size = cache.size();
		for(int i = 0; i < size; ++i)
		{
			if(cache.get(i)[0].equals(eojeol))
			{
				return i;
			}
		}
		
		return -1;
	}
	
	public ArrayList<String> get(int index)
	{
		ArrayList<String> result = new ArrayList<String>();
		String[] d = cache.get(index);
		for(int i = 1; i < 4; ++i)
		{
			result.add(d[i]);
		}
		return result;
	}
	
	public void add(String d0, String d1, String d2, String d3)
	{
		String[] data = new String[4];
		data[0] = d0;
		data[1] = d1;
		data[2] = d2;
		data[3] = d3;
		cache.add(data);
	}
}

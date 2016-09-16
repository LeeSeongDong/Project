package morph;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

class Comp implements Comparable<Comp>
{
	double dis;
	String data;

	public Comp(double dis, String data)
	{
		this.dis = dis;
		this.data = data;
	}

	public int compareTo(Comp d)
	{
		if(this.dis > d.dis)
		{
			return 1;
		}
		else if(this.dis < d.dis)
		{
			return -1;
		}
		else
		{
			return 0;
		}
	}
}

class Comp2 implements Comparator<Integer>
{
	private HashMap<Integer, Integer> hm;

	public Comp2(HashMap<Integer, Integer> hm)
	{
		this.hm = hm;
	}

	public int compare(Integer o1, Integer o2)
	{
		if(hm.get(o1) < hm.get(o2))
			return 1;
		else if(hm.get(o1) == hm.get(o2))
			return 0;
		else
			return -1;
	}
}

class Comp3 implements Comparator<String>
{
	private String initStr;
	private WeightedEDA weda;
	
	public Comp3(String initStr)
	{
		this.initStr = initStr;
		weda = new WeightedEDA();
	}

	public int compare(String o1, String o2)
	{
		if(weda.getEditDistance(o1, initStr) > weda.getEditDistance(o2, initStr))
			return 1;
		else if(weda.getEditDistance(o1, initStr) < weda.getEditDistance(o2, initStr))
			return -1;
		else
			return 0;
	}
}
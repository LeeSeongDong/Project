package project;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

import com.google.common.collect.Sets;


public class SimilarityAnalyzer
{
	private Mongo m;
	
	private static final char[] CHO = 
		{0x3131, 0x3132, 0x3134, 0x3137, 0x3138, 0x3139, 0x3141, 0x3142, 0x3143, 0x3145,
				0x3146, 0x3147, 0x3148, 0x3149, 0x314a, 0x314b, 0x314c, 0x314d, 0x314e};

	public SimilarityAnalyzer(Mongo m)
	{
		this.m = m;
		m.connect();
	}

	// No intermediate HashSet
	public static int getCount(HashSet<Integer> set1, HashSet<Integer> set2)
	{
		HashSet<Integer> a;
		HashSet<Integer> b;
		if (set1.size() <= set2.size()) 
		{
			a = set1;
			b = set2;
		} 
		else 
		{
			a = set2;
			b = set1;
		}

		int count = 0;
		for (Integer e : a) 
		{
			if (b.contains(e)) 
			{
				count++;
			}
		}
		return count;
	}
	
	public  boolean isValid(String str)
	{
		int size = str.length();
		for(int i = 0; i < size; ++i)
		{
			if((str.codePointAt(i) >= 44032 && str.codePointAt(i) <= 55203))		//한글
			{
				continue;
			}
			else if(str.codePointAt(i) >= 48 && str.codePointAt(i) <= 57)		//숫자
			{
				continue;
			}
			else if(str.codePointAt(i) >= 65 && str.codePointAt(i) <= 90)		//영대문자
			{
				continue;
			}
			else if(str.codePointAt(i) >= 97 && str.codePointAt(i) <= 122)		//영소문자
			{
				continue;
			}
			else if(str.codePointAt(i) == 32 || str.codePointAt(i) == 33 || str.codePointAt(i) == 63 || str.codePointAt(i) == 44)
			{
				continue;
			}

			return false;
		}

		return true;
	}
	
	public static HashSet<Integer> getIntersection(HashSet<Integer> set1, HashSet<Integer> set2)
	{
		if(set1 == null || set2 == null)
		{
			return null;
		}
		else if(set1.size() < set2.size())
		{
			HashSet<Integer> intersection = new HashSet<Integer>(Sets.intersection(set1, set2));
			return intersection;
		}
		else
		{
			HashSet<Integer> intersection = new HashSet<Integer>(Sets.intersection(set2, set1));
			return intersection;
		}
	}

	public HashSet<String> setLoad(String fileName)
	{
		HashSet<String> result = new HashSet<String>();

		try 
		{
			FileReader fr = new FileReader(fileName);
			BufferedReader br = new BufferedReader(fr);

			String buffer;

			while((buffer = br.readLine()) != null)
			{
				result.add(buffer);
			}

			br.close();
			fr.close();
		} 
		catch (Exception e) 
		{
			//e.printStackTrace();
			return null;
		}

		return result;
	}

	public HashMap<String, HashSet<Integer>> mapLoad(String fileName)
	{
		HashMap<String, HashSet<Integer>> result = new HashMap<String, HashSet<Integer>>();

		try 
		{
			FileReader fr = new FileReader(fileName);
			BufferedReader br = new BufferedReader(fr);

			String buffer;

			while((buffer = br.readLine()) != null)
			{
				String[] temp = buffer.split("\t");

				HashSet<Integer> hs = new HashSet<Integer>();
				int size = temp.length;
				for(int i = 1; i < size; ++i)
				{
					hs.add(Integer.parseInt(temp[i]));
				}

				result.put(temp[0], hs);
			}

			br.close();
			fr.close();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			return null;
		}

		return result;
	}

	public boolean isContains(String str)
	{
		m.getEojeolCollection(str.charAt(0) + "");
		
		if(m.isExist_Eojeol(str))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public boolean isContainsCache(String str)
	{
		m.getCacheCollection(str.charAt(0) + "");
		
		if(m.isExist_Cache(str))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public ArrayList<String> getCache(String eojeol)
	{
		return m.getCache(eojeol);
	}

	public String getCHO(char c)
	{
		char test = c;

		if(test >= 0xAC00)
		{
			char uniVal = (char) (test - 0xAC00);
			char cho = (char) (((uniVal - (uniVal % 28))/28)/21);

			c = CHO[cho];
		}

		return Character.toString(c);
	}

	//교집합
	public HashMap<Integer, Integer> findRecommendation(String str)
	{		
		m.getIndexCollection("" + str.charAt(0));

		str = str.replaceAll("[^[0-9]]","ⓝ");

		while(str.contains("ⓝⓝ"))
		{
			str = str.replaceAll("ⓝⓝ", "ⓝ");
		}
		str = str.replaceAll(" ", "").trim();


		HashMap<String, HashSet<Integer>> hm = m.getIndexs(str);
		HashMap<Integer, Integer> result = new HashMap<Integer, Integer>();

		int size = str.length();
		System.out.println("size : " + size);
		for(int i = 1; i < size-1; i += 2)
		{
			HashSet<Integer> hs = getIntersection(hm.get("" + str.charAt(i)), hm.get("" + str.charAt(i+1)));
			HashMap<Integer, Integer> temp = new HashMap<Integer, Integer>();
			if(hs == null)
			{
				continue;
			}
			for(Integer x : hs)
			{
				temp.put(x, 2);
			}

			for(int j = 0; j < size; ++j)
			{
				if(j == i || j == i+1)
				{
					continue;
				}

				HashSet<Integer> hs2 = hm.get("" + str.charAt(j));
				if(hs2 == null)
				{
					continue;
				}
				for(Integer x : hs2)
				{
					if(temp.containsKey(x))
					{
						temp.put(x, temp.get(x) +1);
					}
				}
			}

			ArrayList<Integer> arr = new ArrayList<Integer>(temp.keySet());
			int l = arr.size();
			for(int j = 0; j < l; ++j)
			{
				Integer key = arr.get(j);
				if(result.containsKey(key))
				{
					result.put(key, result.get(key) > temp.get(key) ? result.get(key) : temp.get(key)); 
				}
				else
				{
					result.put(key, temp.get(key));
				}
			}
		}

		return result;
	}
	
	//카운팅
	public HashMap<Integer, Integer> findRecommendation2(String str)
	{
		m.getIndexCollection(getCHO(str.charAt(0)));

		str = str.replaceAll("[^[0-9]]","ⓝ");

		while(str.contains("ⓝⓝ"))
		{
			str = str.replaceAll("ⓝⓝ", "ⓝ");
		}
		str = str.replaceAll(" ", "").trim();

		HashMap<String, HashSet<Integer>> hm = m.getIndexs(str);
		HashMap<Integer, Integer> result = new HashMap<Integer, Integer>();

		int size = str.length();
		for(int i = 1; i < size; ++i)
		{
			String t = "" + str.charAt(i);
			HashSet<Integer> v = hm.get(t);
			if(v == null)
			{
				continue;
			}
			for(Integer x : v)
			{
				if(result.containsKey(x))
				{
					result.put(x, result.get(x) +1);
				}
				else
				{
					result.put(x, 1);
				}
			}
		}

		return result;
	}

	public ArrayList<String> getRecommend(String initStr)
	{		
		String str = initStr;

		m.getEojeolCollection(initStr.charAt(0) + "");

		ArrayList<String> temp = new ArrayList<String>();
		ArrayList<String> result = new ArrayList<String>();

		HashMap<Integer, Integer> hm = findRecommendation(str);
		if(hm.size() == 0)
		{
			result.add(" ");
			result.add(" ");
			result.add(" ");
			return result;
		}
		
		ArrayList<Integer> arr = new ArrayList<Integer>(hm.keySet());
		Collections.sort(arr, new Comp2(hm));
		
		int max = hm.get(arr.get(0));

		m.getEojeolCollection(initStr.charAt(0) + "");

		int size = arr.size();
		for(int i = 0; i < size; ++i)
		{
			if(hm.get(arr.get(i)) < max)
			{
				continue;
			}
			
			String corpus = m.getEojeol(arr.get(i));
			if(corpus == null)
			{
				continue;
			}
			
			result.add(corpus);
		}

		Collections.sort(result, new Comp3(initStr));
		WeightedEDA w = new WeightedEDA();
		
		
		for(int i = 0; i < (result.size() < 3 ? result.size() : 3 ); ++i)
		{
			if(w.getEditDistance(initStr, result.get(i)) < 10)
			{
				temp.add(result.get(i));
			}
		}
		
		while(temp.size() < 3)
		{
			temp.add(" ");
		}
		
		System.out.println(temp);
		
		return temp;
	}
}

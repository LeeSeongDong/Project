package morph;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.Sets;
import com.mongodb.Mongo;

import mongo.MongoTest;

public class SimilarityAnalyzer_
{
	private MongoTest mt;
	private static final char[] CHO = 
		{0x3131, 0x3132, 0x3134, 0x3137, 0x3138, 0x3139, 0x3141, 0x3142, 0x3143, 0x3145,
				0x3146, 0x3147, 0x3148, 0x3149, 0x314a, 0x314b, 0x314c, 0x314d, 0x314e};

	public SimilarityAnalyzer_(MongoTest mt)
	{
		this.mt = mt;
		mt.con();
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
		mt.getCollectionEojeol(getCHO(str.charAt(0)));
		
		if(mt.isExist_Eojeol(str))
		{
			return true;
		}
		else
		{
			return false;
		}
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
		mt.getCollectionIndex(getCHO(str.charAt(0)));

		str = str.replaceAll("[^[0-9]]","ⓝ");

		while(str.contains("ⓝⓝ"))
		{
			str = str.replaceAll("ⓝⓝ", "ⓝ");
		}
		str = str.replaceAll(" ", "").trim();


		HashMap<String, HashSet<Integer>> hm = mt.getIndexs(str);
		HashMap<Integer, Integer> result = new HashMap<Integer, Integer>();


		int size = str.length();
		for(int i = 0; i < size-1; i += 2)
		{
			if(str.charAt(0) == 'ⓝ' && i == 0)
			{
				continue;
			}
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
				if(j == i || j == i+1 || (str.charAt(0) == 'ⓝ' && j == 0))
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
		mt.getCollectionIndex(getCHO(str.charAt(0)));

		str = str.replaceAll("[^[0-9]]","ⓝ");

		while(str.contains("ⓝⓝ"))
		{
			str = str.replaceAll("ⓝⓝ", "ⓝ");
		}
		str = str.replaceAll(" ", "").trim();

		HashMap<String, HashSet<Integer>> hm = mt.getIndexs(str);
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
		long startTime = System.currentTimeMillis();

		mt.getCollectionEojeol(getCHO(initStr.charAt(0)) + "");

		ArrayList<String> result = new ArrayList<String>();

		HashMap<Integer, Integer> hm = findRecommendation(str);
		ArrayList<Integer> arr = new ArrayList<Integer>(hm.keySet());
		Collections.sort(arr, new Comp2(hm));
		int max = hm.get(arr.get(0));
		System.out.println("max : " + max);
		mt.getCollectionEojeol(getCHO(initStr.charAt(0)) + "");

		int size = arr.size();
		for(int i = 0; i < size; ++i)
		{
			if(hm.get(arr.get(i)) < max)
			{
				continue;
			}
			
			String corpus = mt.getEojeol(arr.get(i));
			if(corpus == null)
			{
				continue;
			}
			
			result.add(corpus);
		}

		Collections.sort(result, new Comp3(initStr));
		
		System.out.println(result);
		
		long endTime = System.currentTimeMillis();
		System.out.println("경과시간 : " + (endTime - startTime));
		
		return result;
	}


	public static void main(String[] args)
	{
		MongoTest mt = new MongoTest();
		SimilarityAnalyzer_ smi = new SimilarityAnalyzer_(mt);

		ArrayList<String> arr = smi.getRecommend("밥을 찢는다");

		for(int i = 0; i < arr.size(); ++i)
		{
			//System.out.println(arr.get(i));
		}

	}
}

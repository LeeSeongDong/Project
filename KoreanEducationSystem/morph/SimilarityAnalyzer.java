package morph;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.Sets;

import mongo.MongoTest;

public class SimilarityAnalyzer 
{
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
		String fileName = "C://Users//LSD//L//기타자료//SIG자료//16_여름//데이터들//Corpus_6__//" + str.charAt(0) + ".txt";
		HashSet<String> hs = setLoad(fileName);

		if(hs.contains(str))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	//교집합
	//카운팅?
	public HashMap<Integer, Integer> findRecommendation(String str)
	{
		String fileName = "C://Users//LSD//L//기타자료//SIG자료//16_여름//데이터들//Corpus_7_//" + str.charAt(0) + "_In.txt";
		HashMap<String, HashSet<Integer>> hm = mapLoad(fileName);
		HashMap<Integer, Integer> result = new HashMap<Integer, Integer>();

		str = str.replaceAll(" ", "").trim();
		System.out.println("start");
		System.out.println(str);

		int size = str.length();
		for(int i = 1; i < size; i += 2)
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

			for(int j = 1; j < size; ++j)
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
					result.put(key, result.get(key) > result.get(key) ? result.get(key) : result.get(key)); 
				}
				else
				{
					result.put(key, temp.get(key));
				}
			}
		}

		return result;
	}

	public HashMap<Integer, Integer> findRecommendation2(String str)
	{
		String fileName = "C://Users//LSD//L//기타자료//SIG자료//16_여름//데이터들//Corpus_7_//" + str.charAt(0) + "_In.txt";

		HashMap<String, HashSet<Integer>> hm = mapLoad(fileName);
		HashMap<Integer, Integer> result = new HashMap<Integer, Integer>();

		str = str.replaceAll(" ", "").trim();
		System.out.println("start");

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



	public static void main(String[] args)
	{
		SimilarityAnalyzer smi = new SimilarityAnalyzer();
		MorphAnalyzer_ ma = new MorphAnalyzer_();
		CorpusAnalyzer_ ca = new CorpusAnalyzer_();

		String str = "유병현은 달리기를";
		String initStr = str;
		long startTime = System.currentTimeMillis();

		ArrayList<String> unkArr = ma.getMorph(str, "unk");
		for(int i = 0; i < unkArr.size(); ++i)
		{
			str = str.replaceAll(unkArr.get(i), "ⓤ");
		}

		ArrayList<String> ncchArr = ma.getMorph(str, "nnc");
		for(int i = 0; i < ncchArr.size(); ++i)
		{
			str = str.replaceAll(ncchArr.get(i), "ⓝ");
		}
		System.out.println(str);
		
		if(smi.isContains(initStr))
		{
			System.out.println("Correct");
		}
		else
		{
			WeightedEDA weda = new WeightedEDA();
			HashMap<Integer, Integer> hm = smi.findRecommendation2(str);
			ArrayList<Integer> arr = new ArrayList<Integer>(hm.keySet());

			Collections.sort(arr, new Comp2(hm));
			
			ArrayList<String> ar = ca.fileLoad("C://Users//LSD//L//기타자료//SIG자료//16_여름//데이터들//Corpus_6__//" + str.charAt(0) + ".txt");
			
			ArrayList<String> ar2 = new ArrayList<String>();
			
			for(int i = 0; i < 50; ++i)
			{
				String corpus = ar.get(arr.get(i));
				if(weda.getEditDistance(initStr, corpus) < initStr.length()/2 + 2)
				{
					ar2.add(corpus);
				}
			}
			
			for(int i = 0; i < ar2.size(); ++i)
			{
				System.out.println(ar2.get(i));
			}
		}
		
		long endTime = System.currentTimeMillis();
		System.out.println("경과시간 : " + (endTime - startTime));

		ma.close();
	}
}

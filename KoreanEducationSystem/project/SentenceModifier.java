package project;

import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.regex.Pattern;


public class SentenceModifier {

	public ArrayList<String> getSentence(ArrayList<String> arr)
	{
		if(arr == null)
			return null;
		
		StringTokenizer st = null;
		ArrayList<String> senList = new ArrayList<String>();
		
		for(int i=0; i < arr.size(); i++)
		{
			st = new StringTokenizer(arr.get(i),".");
			
			while(st.hasMoreTokens())
				senList.add(st.nextToken());
		}
		
		return senList;
	}
	
	public ArrayList<String> getEojeol(String str)
	{
		str = str.replaceAll("[^[0-9]]","ⓝ");
		
		String replaceN = "ⓝⓝ";
		
		while(str.contains(replaceN))
			str = str.replaceAll(replaceN, "ⓝ");
		
		ArrayList<String> eojeolList = new ArrayList<String>();
		
		StringTokenizer st_special = new StringTokenizer(str, "()[]\",/'“”");
		
		ArrayList<String> temp = new ArrayList<String>();
		
		while(st_special.hasMoreTokens())
			temp.add(st_special.nextToken().trim());
		
		for(int i=0 ; i < temp.size() ; i++)
		{
			String[] candidates = temp.get(i).split(" ");
			
			if(candidates.length < 2)
				continue;
			
			for(int j=0; j < candidates.length-1 ; j++)
			{
				if(candidates[j].equals(" ") || candidates[j].equals(" ") || candidates[j+1].equals(" ") || candidates[j+1].equals(""))
					continue;
				
				String eojeol = candidates[j] + " " + candidates[j+1];
				if(Pattern.matches("^[가-힣ⓝ!? ]*$", eojeol))
					eojeolList.add(eojeol);
			}
		}
		
		return eojeolList;
		
	}
}

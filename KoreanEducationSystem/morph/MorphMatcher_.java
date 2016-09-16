package morph;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;

public class MorphMatcher_
{
	public static ArrayList<String> fileLoad(String fileName)
	{
		ArrayList<String> result = new ArrayList<String>();

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
			e.printStackTrace();
		}

		return result;
	}

	public String match(String inputText)
	{
		ArrayList<String> msgList = new ArrayList<String>();
		WeightedEDA weda = new WeightedEDA();

		MorphAnalyzer ma = new MorphAnalyzer();
		ArrayList<String> arr = fileLoad("C://Users//LSD//L//기타자료//SIG자료//16_여름//데이터들//Corpus_6//" + inputText.charAt(0) + ".txt");
		ArrayList<Comp> recommendation = new ArrayList<Comp>();

		int size = arr.size();
		boolean flag = false;
		

		if(!flag)
		{
			msgList.add("은/는 올바르지 않은 어절구성입니다.");
			Collections.sort(recommendation);

			for(int i = 0; i < 3; ++i)
			{
				if(!recommendation.isEmpty())
				{
					msgList.add("@@##" + recommendation.get(i).data);
				}
				else
				{
					msgList.add("@@## ");
				}
			}
		}

		ma.close();

		String msg = "";
		size = msgList.size();
		for(int i = 0; i < size; ++i)
		{
			msg += msgList.get(i);
		}

		return msg;
	}


	public static void main(String[] args)
	{
		MorphAnalyzer_ ma = new MorphAnalyzer_();
		WeightedEDA weda = new WeightedEDA();

		String inputText = "찢었다";
		String text = ma.makeTrainingset(inputText).get(0);
		String str = text.replaceAll(" ", "");
		System.out.println(text);

		ArrayList<String> arr = fileLoad("C://Users//LSD//L//기타자료//SIG자료//16_여름//데이터들//Trainingset//" + text.charAt(0) + ".txt");
		ArrayList<Comp> recommendation = new ArrayList<Comp>();

		int size = arr.size();
		boolean flag = false;
		for(int i = 0; i < size; ++i)
		{
			String temp = arr.get(i);
			double wed = weda.getEditDistance(text, temp);
			if(wed < str.length())
			{
				Comp d = new Comp(wed, temp);
				recommendation.add(d);
			}

			if(text.equals(temp))
			{
				flag = true;
				System.out.println("올바른 어절구성!");
				break;
			}
		}

		if(!flag)
		{
			System.out.println("올바르지 않은 어절구성! \n 추천단어 목록");

			Collections.sort(recommendation);
			size = recommendation.size();
			System.out.println(size);

			for(int i = 0; i < size; ++i)
			{
				System.out.println(recommendation.get(i).data);
			}
		}

		ma.close();
	}
}
package morph;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;

public class CorpusIndexing 
{
	public static ArrayList<String> tokenize(String input)
	{
		ArrayList<String> arr = new ArrayList<String>();
		
		String[] temp = input.substring(1, input.length()-1).split(", ");
		
		for(int i = 0; i < temp.length; ++i)
		{
			arr.add(temp[i]);
		}
		
		return arr;
	}

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
	
	public static void fileSave(String fileName, String str)
	{
		try 
		{
			FileWriter fw = new FileWriter(fileName, true);
			PrintWriter pw = new PrintWriter(fw);
			
			pw.println(str);
			
			pw.close();
			fw.close();
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static void fileIndexing()
	{
		for(int i = 761; i <= 780; ++i)
		{
			System.out.println(i + "/1442 인덱싱 시작");
			
			ArrayList<String> corpusList = fileLoad("C://Users//LSD//L//기타자료//SIG자료//16_여름//데이터들//의미분석TS_3차//" + i + ".txt");
			
			int size = corpusList.size();
			for(int j = 0; j < size; ++j)
			{
				try
				{
					ArrayList<String> tokenList = tokenize(corpusList.get(j));

					int tokenSize = tokenList.size();
					for(int k = 0; k < tokenSize; ++k)
					{
						String fileName = "C://Users//LSD//L//기타자료//SIG자료//16_여름//데이터들//TS_2차//" + tokenList.get(k).split(" ")[0].split("/")[0] + ".txt";

						System.out.println(i + " : " + j + "=" + tokenList.get(k).split(" ")[0].split("/")[0]);
						fileSave(fileName, tokenList.get(k));
					}
				}
				catch (Exception e)
				{
					continue;
				}
			}
			
			System.out.println(i + "/1442 인덱싱 끝");
		}
	}
	
	public static void main(String[] args)
	{
		/*
		for(int i = 0; i < 19; ++i)
		{
			for(int j = 0; j < 21; ++j)
			{
				for(int k = 0; k < 28; ++k)
				{
					String a = "";
					
					a += String.format("%c", 4352+i) + String.format("%c", 4449+j) + String.format("%c", 4519+k);
					String fileName = Normalizer.normalize(a, Normalizer.Form.NFC);

				}
			}
		}
		*/
		//test(1, 35);
		fileIndexing();
	}
}

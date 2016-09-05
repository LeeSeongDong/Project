package morph;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;

public class SementicAnalyzer
{	
	public static String getSementic(String input)
	{
		return input.split("/")[0];
	}
	
	public static String getTag(String input)
	{
		return input.split("/")[1];
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
	
	public static void fileSave(String fileName, ArrayList<ArrayList<String>> arr)
	{
		try 
		{
			FileWriter fw = new FileWriter(fileName, false);
			PrintWriter pw = new PrintWriter(fw);
			
			int size = arr.size();
			for(int i = 0; i < size; ++i)
			{
				pw.println(arr.get(i));
			}
			
			pw.close();
			fw.close();
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args)
	{
		MorphAnalyzer_ ma = new MorphAnalyzer_();
		
		for(int i = 1286; i <= 1442; ++i)
		{
			System.out.println(i + "/1442 시작");
			
			ArrayList<String> corpusList = fileLoad("C://Users//LSD//L//기타자료//SIG자료//16_여름//데이터들//StringParser//" + i + ".txt");
			ArrayList<ArrayList<String>> arr = new ArrayList<ArrayList<String>>();
			
			int size = corpusList.size();
			for(int j = 0; j < size; ++j)
			{
				try
				{
					if(corpusList.get(j).equals(" ")
							|| corpusList.get(j).equals("")
							|| corpusList.get(j).equals("  ")
							|| corpusList.get(j).equals("   ")
							|| corpusList.get(j).equals("    "))
					{
						continue;
					}
					System.out.print(i + " : " + j + " = ");
					
					String temp = corpusList.get(j).replaceAll(".\"", "");
					temp = temp.replaceAll("\"", "");
					temp = temp.replaceAll(".\'", "");
					temp = temp.replaceAll("\'", "");
					temp = temp.replaceAll("“", "");
					temp = temp.replaceAll("”", "");
					temp = temp.replaceAll("「", "");
					temp = temp.replaceAll("」", "");
					temp = temp.replaceAll("《", "");
					temp = temp.replaceAll("》", "");
					temp = temp.replaceAll("『", "");
					temp = temp.replaceAll("』", "");
					temp = temp.replaceAll("=", "");
					temp = temp.replaceAll("■", "");
					temp = temp.replaceAll("□", "");
					temp = temp.replaceAll("…", "");
					
					if(temp.equals("") || temp.equals(" "))
					{
						continue;
					}
					
					arr.add(ma.makeTrainingset(temp));
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
			
			fileSave("C://Users//LSD//L//기타자료//SIG자료//16_여름//데이터들//의미분석TS_3차//" + i + ".txt", arr);
			System.out.println(i + "/1442 종료");
		}
		
		ma.close();
	}
	
	/*
	public static void main(String[] args)
	{	
		
	}
	*/
}








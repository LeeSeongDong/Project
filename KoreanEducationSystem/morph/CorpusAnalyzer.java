package morph;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;

public class CorpusAnalyzer 
{
	
	public CorpusAnalyzer() {}
	
	public static void fileSave(ArrayList<ArrayList<String>> arr, String fileName)
	{
		try
		{
			FileWriter fw = new FileWriter(fileName);
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
	
	
	public static ArrayList<ArrayList<String>> fileLoad(String fileName)
	{
		ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
		
		try 
		{
			FileReader fr = new FileReader(fileName);
			MorphAnalyzer_ ma = new MorphAnalyzer_();
			
			String buffer = "";
			String tbuff = "";
			boolean flag = false;
			
			int c;
			while((c = fr.read()) != -1)
			{
				System.out.println(fileName + " : " + buffer + "(" + tbuff + ")" + c + "/" + (char)c);
				if(c == 10)
				{
					continue;
				}
				
				if((char)c == '(')
				{
					if(flag)
					{
						flag = false;
						while((c = fr.read()) != -1)
						{
							if((char)c == '.' || (char)c == '?' || (char)c == '!' || (c == 13 && !(buffer.equals(""))))
							{
								tbuff = "";
								buffer = "";
								break;
							}
						}
					}
					else
					{
						flag = true;
					}
					
					continue;
				}
				else if((char)c == ')')
				{
					if(!tbuff.equals(""))
					{
						result.add(ma.getMorph(tbuff));
					}
					flag = false;
					tbuff = "";
					continue;
				}
				
				if(flag)
				{
					if(!(tbuff.equals("") && (char)c == ' '))
					{
						tbuff += (char)c;
					}
					continue;
				}

				if(c != 13 && !(buffer.equals("") && (char)c ==' '))
				{
					buffer += (char)c;
				}

				if((char)c == '.' || (char)c == '?' || (char)c == '!' || (c == 13 && !(buffer.equals(""))))
				{
					result.add(ma.getMorph(buffer));
					buffer = "";
				}
			}
			
			ma.close();
			fr.close();
			
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		return result;
	}
	
	public static void main(String[] args)
	{
		// C://Users//LSD//L//기타자료//SIG자료//16_여름//데이터들//StringParser//1506.txt
			
		for(int i = 1416; i <= 1442; ++i)
		{
			try
			{
				System.out.println(i + " / 1442  시작...");

				String fileName = "C://Users//LSD//L//기타자료//SIG자료//16_여름//데이터들//StringParser//" + i + ".txt";
				String target = "C://Users//LSD//L//기타자료//SIG자료//16_여름//데이터들//형태소분석_2차//" + i + ".txt";
				fileSave(fileLoad(fileName), target);

				System.out.println(i + " / 1442  완료...");
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
}

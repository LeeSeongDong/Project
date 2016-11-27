package db;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class FileHandler {

	public ArrayList<String> fileLoad(String filename)
	{
		ArrayList<String> stringArr = new ArrayList<String>();

		try {
			FileReader fr = new FileReader(filename);
			BufferedReader br = new BufferedReader(fr);

			String str;

			while((str = br.readLine()) != null) 
			{
				stringArr.add(str);
			}

			fr.close();
			br.close();

			System.out.println(filename + " Load!");
			return stringArr ;
		}
		catch (IOException e) {
			return null;
		}
	}

	public void fileSave(String fileName, ArrayList<String> arr)
	{
		try 
		{
			BufferedWriter bw = new BufferedWriter(new FileWriter(fileName));
			PrintWriter pw = new PrintWriter(bw);

			for(int i=0; i < arr.size(); i++)
			{
				pw.println(arr.get(i));
			}

			pw.flush();
			pw.close();

		} 

		catch (IOException e) 
		{ e.printStackTrace(); }	
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

}

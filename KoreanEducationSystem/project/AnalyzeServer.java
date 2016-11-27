package project;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class AnalyzeServer
{
	public static void main(String[] args) 
	{
		// TODO Auto-generated method stub
		try 
		{
			ServerSocket server = new ServerSocket(80);
			Cache cache = new Cache();
			System.out.println("Server is Run!");

			while (true)
			{
				Socket client = server.accept();
				Secho_ secho = new Secho_(client, cache);
				secho.start();
			}

		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
}

class Secho_ extends Thread 
{
	private Socket client;
	private Cache cache;
	Secho_(Socket client, Cache cache) 
	{
		this.client = client;
		this.cache = cache;
	}

	public void run() 
	{
		try 
		{
			System.out.println("Client has accepted...");
			Mongo m = new Mongo();
			SimilarityAnalyzer smi = new SimilarityAnalyzer(m);
			BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream(), "UTF-8"));
			PrintWriter pw = new PrintWriter(client.getOutputStream());
			String readData = "";

			while (true)
			{
				readData = br.readLine();
				if(readData == null)
				{
					continue;
				}

				System.out.println("readData : " + readData);

				long startTime = System.currentTimeMillis();
				if(readData.equals("exit"))
				{
					pw.println("exit");
					pw.flush();
					break;
				}
				else
				{
					String[] d = readData.split(" ");
					String sendData = "";
					if(d.length == 1)
					{
						sendData += readData + "@no_error@ @ @ @";
					}
					for(int i = 0; i < d.length - 1; ++i)
					{
						String part = d[i] + " " + d[i+1];
						
						if(part.substring(0, part.length()-1).contains("[.]")
								|| part.substring(0, part.length()-1).contains(",")
								|| part.substring(0, part.length()-1).contains("!")
								|| part.substring(0, part.length()-1).contains("[?]"))
						{
							continue;
						}
						
						sendData += part;

						part = part.replaceAll("[^[0-9]]","ⓝ");
						part = part.replaceAll("[.]","");
						part = part.replaceAll(",","");
						part = part.replaceAll("!","");
						part = part.replaceAll("[?]","");
						
						while(part.contains("ⓝⓝ"))
						{
							part = part.replaceAll("ⓝⓝ", "ⓝ");
						}

						System.out.println(part);

						if(!smi.isValid(part) || smi.isContains(part))
						{
							sendData += "@no_error@ @ @ @";
						}
						else
						{
							sendData += "@doubt@";

							ArrayList<String> arr = null;
							int index = cache.find(part);
							if(index != -1)
							{
								arr = cache.get(index);
								System.out.println("cache");
							}
							else
							{
								arr = smi.getRecommend(part);
								cache.add(part, arr.get(0), arr.get(1), arr.get(2));
							}
							
							for(int j = 0; j < 3; ++j)
							{
								sendData += arr.get(j) + "@";
							}
						}
					}

					System.out.println("sendData : " + sendData);
					pw.println(sendData);
					pw.flush();
				}
				
				long endTime = System.currentTimeMillis();
				System.out.println("경과시간 : " + (endTime - startTime));
			}
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}

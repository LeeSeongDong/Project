package morph;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import kr.ac.kaist.swrc.jhannanum.comm.Sentence;
import mongo.MongoTest;
import mongo.SentenceModifier;

public class AnalyzeServer_
{
	public static void main(String[] args) 
	{
		// TODO Auto-generated method stub
		try 
		{
			ServerSocket server = new ServerSocket(80);
			System.out.println("Server is Run!");

			while (true) 
			{
				Socket client = server.accept();
				Secho_ secho = new Secho_(client);
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
	Secho_(Socket client) 
	{
		this.client = client;
	}

	public void run() 
	{
		try 
		{
			System.out.println("Client has accepted...");
			MongoTest mt = new MongoTest();
			CorpusAnalyzer_ ca = new CorpusAnalyzer_();
			SimilarityAnalyzer_ smi = new SimilarityAnalyzer_(mt);
			BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));
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
					for(int i = 0; i < d.length - 1; ++i)
					{
						String part = d[i] + " " + d[i+1];
						sendData += part;
						
						part = part.replaceAll("[^[0-9]]","ⓝ");
						while(part.contains("ⓝⓝ"))
						{
							part = part.replaceAll("ⓝⓝ", "ⓝ");
						}
						
						System.out.println(part);						
						
						if(!ca.isValid(part) || smi.isContains(part))
						{
							sendData += "@no_error@ @ @ @";
						}
						else
						{
							sendData += "@doubt@";
							
							ArrayList<String> arr = smi.getRecommend(sendData.split("@")[i*5]);
							
							for(int j = 0; j < 3; ++j)
							{
								if(arr.size() > j)
								{
									sendData += arr.get(j) + "@";
								}
								else
								{
									sendData += " @";
								}
							}
						}		
					}
					
					System.out.println("sendData : " + sendData);
					pw.println(sendData);
					pw.flush();
				}
			}
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}

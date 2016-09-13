package morph;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class AnalyzeServer
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
				Secho secho = new Secho(client);
				secho.start();
			}

		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
}

class Secho extends Thread 
{
	private Socket client;
	Secho(Socket client) 
	{
		this.client = client;
	}

	public void run() 
	{
		try 
		{
			System.out.println("Client has accepted...");
			MorphAnalyzer ma = new MorphAnalyzer();
			MorphMatcher mm = new MorphMatcher();
			Dictionary d = new Dictionary();
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
				
				System.out.println("from Client > " + readData);

				if(readData.equals("exit"))
				{
					pw.println("exit");
					pw.flush();
					break;
				}
				else
				{
					String data = ma.makeTrainingset(readData).get(0);
					System.out.println("data : " + data);
					String[] dataArr = data.split(" ");
					String dBuffer = "";
					
					for(int i = 0; i < dataArr.length; ++i)
					{
						dBuffer += d.getMeaning(dataArr[i]);
					}

					String b = "\'" + readData + "\'" + mm.match(data) + dBuffer;
					System.out.println(b);
					pw.println(b);
					pw.flush();
				}
			}
			
			ma.close();
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}

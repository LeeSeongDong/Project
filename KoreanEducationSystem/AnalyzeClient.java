package morph;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class AnalyzeClient 
{
	private Socket soc;
	private BufferedReader br;
	private PrintWriter pw;
	
	public boolean connect(String ip, int port)
	{
		try
		{
			this.soc = new Socket(ip, port);
			// 연결된 서버로부터 데이터를 받아올 준비를 한다
			br = new BufferedReader(new InputStreamReader(soc.getInputStream()));
			// 연결된 서버로 데이터를 보낼 준비를 한다
			pw = new PrintWriter(soc.getOutputStream());
		} 
		catch (UnknownHostException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
		System.out.println("Accept to Server Success!!");
		return true;
	}
	
	public boolean send(String msg)
	{
		try
		{
			pw.println(msg);
			pw.flush();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public String recv()
	{
		String msg;
		try 
		{
			msg = br.readLine();
		}
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "-1";
		}
		return msg;
	}
	
	public boolean close()
	{
		try 
		{
			soc.close();
		} 
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	public static void main(String[] args) 
	{
		AnalyzeClient ac = new AnalyzeClient();
		ac.connect("192.168.10.123", 4000);
		
		try
		{
			Scanner scan = new Scanner(System.in);
			String inputData = "";

			while(!inputData.equals("exit"))
			{
				System.out.printf("to Server > ");

				inputData = scan.nextLine();
				ac.send(inputData);
				String[] msg = ac.recv().split("@@##");
				
				for(int i = 0; i < msg.length; ++i)
				{
					System.out.println(msg[i]);
				}

			}
			
			ac.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();            
		}
	}
}



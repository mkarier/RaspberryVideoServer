package client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import com.sun.jna.NativeLibrary;

import shared_class.SharedData;

public class ClientMain 
{
	public static String host = "mkarier-desktop";
	public static void main(String[] args) 
	{
		StreamClient player = null;
		try{
			if(System.getProperty("os.name").contains("Windows"))
				NativeLibrary.addSearchPath("libvlc", SharedData.vlcPath);
			else
				System.out.println("OS = linux");
			Socket socket = new Socket(host, SharedData.comPort);
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			String videoPort = in.readLine();
			System.out.println("video Port = " + videoPort);
			String toPlay = "udp://@:" + videoPort;
			String fromServer = "continue";
			player = new StreamClient(out);
			while(!fromServer.contains("quit"))
			{
				player.init(toPlay);
				player.playSomething();
				fromServer = in.readLine();
				player.close();
			}//end of while
			socket.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			player.close();
		}
	}//end of main

}//end of class

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
			FloatMenu menu = new FloatMenu(player);
			while(!fromServer.contains("quit"))
			{
				player.init(toPlay);
				System.out.println("initialized the player");
				player.playSomething();
				System.out.println("Starting to play");
				fromServer = in.readLine();//TODO: make this better. Right now it is waiting for the server to write 'stop' before moving on
				player.close();
				System.out.println("Player was closed");
			}//end of while
			socket.close();
			menu.close();
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

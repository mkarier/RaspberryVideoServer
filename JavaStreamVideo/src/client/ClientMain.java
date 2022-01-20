package client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

import com.sun.jna.NativeLibrary;

import shared_class.SharedData;

public class ClientMain 
{
	public static String host = "localhost";
	public static void main(String[] args) 
	{
		StreamClient player = null;
		try{
			if(System.getProperty("os.name").contains("Windows"))
				NativeLibrary.addSearchPath("libvlc", SharedData.vlcPath);
			else
				System.out.println("OS = linux");
			Socket socket = null;
			if(args.length >= 1)
				socket = new Socket(args[0], SharedData.comPort);
			else
				socket = new Socket(host, SharedData.comPort);
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			String videoPort = in.readLine();
			System.out.println("video Port = " + videoPort);
			String toPlay = SharedData.access + "://@:" + videoPort;
			String fromServer = "continue";
			player = new StreamClient(out);
			player.init(toPlay);
			System.out.println("initialized the player");
			player.start();
			System.out.println("Starting to play");
			FloatMenu menu = new FloatMenu(player);
			while(!fromServer.contains("quit"))
			{
				try
				{
				
				TimeUnit.SECONDS.sleep(2);
				
				
				fromServer = in.readLine();//TODO: make this better. Right now it is waiting for the server to write 'stop' before moving on
				//player.close();
				player.setTitle(fromServer);
				System.out.println("Player was closed");
				}finally {TimeUnit.SECONDS.sleep(3 *1000);}
			}//end of while
			player.close();
			socket.close();
			menu.close();
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch(Throwable t) {t.printStackTrace();}
		finally
		{			
			System.out.println("Reached Finally");
			player.close();
		}
	}//end of main

}//end of class

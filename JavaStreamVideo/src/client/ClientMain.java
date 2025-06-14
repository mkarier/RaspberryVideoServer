package client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import com.sun.jna.NativeLibrary;

import shared_class.SharedData;
import uk.co.caprica.vlcj.factory.discovery.NativeDiscovery;

public class ClientMain 
{
	public static String host = "localhost";
	public static void main(String[] args) 
	{
		//StreamClient player = null;
		String serverName = args.length >= 1? args[0]:"localhost";
		String networkOptions = ":network-caching=" + (args.length >= 2?args[1]:"1000");
		try
		(
				var socket = new Socket(serverName, SharedData.comPort);
				BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
				var player = new StreamClient(out);
				FloatMenu menu = new FloatMenu(player);
		)
		{
			if(System.getProperty("os.name").contains("Windows"))
				NativeLibrary.addSearchPath("libvlc", SharedData.vlcPath);
			else
				System.out.println("OS = linux");
			new NativeDiscovery().discover();
			
			
			String videoPort = in.readLine();
			System.out.println("video Port = " + videoPort);
			String toPlay = SharedData.access + "://@:" + videoPort;
			String fromServer = "continue";
			
			player.init(toPlay, networkOptions);
			System.out.println("initialized the player");
			player.start();
			System.out.println("Starting to play");
			
			while(!fromServer.contains("quit"))
			{
				fromServer = in.readLine();//TODO: make this better. Right now it is waiting for the server to write 'stop' before moving on
				System.out.println(fromServer);
				player.setTitle(fromServer);
				System.out.println("Player was closed");
			}//end of while
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch(Throwable t) {t.printStackTrace();}
	}//end of main

}//end of class

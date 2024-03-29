package client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
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
		StreamClient player = null;
		String networkOptions = ":network-caching=";
		try{
			if(System.getProperty("os.name").contains("Windows"))
				NativeLibrary.addSearchPath("libvlc", SharedData.vlcPath);
			else
				System.out.println("OS = linux");
			new NativeDiscovery().discover();
			Socket socket = null;
			if(args.length >= 1) {
					socket = new Socket(args[0], SharedData.comPort);
					if(args.length >= 2)
					{
						networkOptions += args[1];
					}
					else
						networkOptions += 1000;
			}
			else
			{
				socket = new Socket(host, SharedData.comPort);
				networkOptions += 1000;
			}
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			String videoPort = in.readLine();
			System.out.println("video Port = " + videoPort);
			String toPlay = SharedData.access + "://@:" + videoPort;
			String fromServer = "continue";
			player = new StreamClient(out);
			player.init(toPlay, networkOptions);
			System.out.println("initialized the player");
			player.start();
			System.out.println("Starting to play");
			FloatMenu menu = new FloatMenu(player);
			while(!fromServer.contains("quit"))
			{
				fromServer = in.readLine();//TODO: make this better. Right now it is waiting for the server to write 'stop' before moving on
				System.out.println(fromServer);
				player.setTitle(fromServer);
				System.out.println("Player was closed");
			}//end of while
			player.close();
			socket.close();
			menu.close();
		} catch (IOException e) {
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

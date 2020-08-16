package server;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import com.sun.jna.NativeLibrary;

import shared_class.SharedData;
import uk.co.caprica.vlcj.discovery.NativeDiscovery;

public class ServerMain {
	
	public static void main(String[] args) 
	{
		try
		{
			NativeLibrary.addSearchPath("libvlc", SharedData.vlcPath);
			ServerSocket server = new ServerSocket(SharedData.comPort);
			Socket client = server.accept();
			InetAddress address = client.getInetAddress();
			
			BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
			out.write(SharedData.videoPort + "\n");
			out.flush();
			StreamVideo video = new StreamVideo(address, args[0], false);
			video.stream();
		}catch(Exception e)
		{
			e.printStackTrace();
		}//end of catch
	}//end of main

}

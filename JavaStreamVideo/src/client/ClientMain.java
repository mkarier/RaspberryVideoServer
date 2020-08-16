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
		// TODO Auto-generated method stub
		try {
			NativeLibrary.addSearchPath("libvlc", SharedData.vlcPath);
			Socket socket = new Socket(host, SharedData.comPort);
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			String videoPort = in.readLine();
			System.out.println("video Port = " + videoPort);
			StreamClient client = new StreamClient("udp://@:" + videoPort);
			client.playSomething();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}//end of main

}//end of class

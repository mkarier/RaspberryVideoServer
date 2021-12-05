package client;

import javax.swing.JFrame;
import javax.swing.JLabel;

public class FloatMenu 
{
	
	JFrame box;// = new JFrame();
	JLabel commands;
	
	public FloatMenu(StreamClient client)
	{
		this.box = new JFrame("Floating Menu");
		String cmd = "<html><p>Spacebar to pause the movie<br>"
				+ "Enter should change windowed mode<br>"
				+ "j to skip next video<br>"
				+ "l to watch previous video<br>"
				+ "a for Audio Cycle<br>"
				+ "n for skip chapter</p></html>";
		this.commands = new JLabel(cmd);
		this.commands.addKeyListener(client.getAdapter());
		this.box.addKeyListener(client.getAdapter());
		this.box.add(this.commands);
		this.box.pack();
		this.box.setVisible(true);
	}//end o fconstructor
	
	public void close()
	{
		this.box.setVisible(false);
		this.box.dispose();
	}//end of close
}//end of FloatMenu

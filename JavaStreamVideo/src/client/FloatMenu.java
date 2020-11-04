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
		String cmd = "'p' or Spacebar to pause the movie\n"
				+ "Enter should change windowed mode\n"
				+ "j for +50 Audio Delay\n"
				+ "l for -50 Audio Delay\n"
				+ "a for Audio Cycle\n";
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

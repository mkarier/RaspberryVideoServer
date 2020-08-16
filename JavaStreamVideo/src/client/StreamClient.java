package client;

import javax.swing.JFrame;

import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;

public class StreamClient 
{
	public JFrame box = new JFrame("Client Player");
	public EmbeddedMediaPlayerComponent player = new EmbeddedMediaPlayerComponent();
	//public EmbeddedMediaPlayerComponent player;
	String toPlay = "";

	public StreamClient(String toPlay)
	{
		this.box.setBounds(100,100, 800, 400);
		this.box.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.toPlay = toPlay;
		this.box.setContentPane(this.player);
	}//end of constructor
	
	
	public void playSomething()
	{
		this.box.setVisible(true);
		this.player.getMediaPlayer().playMedia(toPlay);
		
		while(this.player.getMediaPlayer().isPlaying());
		
	}//end of play something 
	
}//end of class

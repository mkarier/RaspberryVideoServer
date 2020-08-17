package client;

import javax.swing.JFrame;

import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

public class StreamClient 
{
	public JFrame box = new JFrame("Client Player");
	public EmbeddedMediaPlayerComponent componentPlayer = new EmbeddedMediaPlayerComponent();
	public EmbeddedMediaPlayer mediaPlayer;
	//public EmbeddedMediaPlayerComponent player;
	String toPlay = "";

	public StreamClient(String toPlay)
	{
		//this.box.setBounds(100,100, 800, 400);
		this.box.setExtendedState(JFrame.MAXIMIZED_BOTH);
		this.box.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.toPlay = toPlay;
		this.box.setContentPane(this.componentPlayer);
	}//end of constructor
	
	
	public void close()
	{
		this.mediaPlayer.stop();
		this.mediaPlayer.release();
		this.componentPlayer.release();
		this.box.setVisible(false);
		this.box.dispose();
	}//end of close
	
	
	public void playSomething()
	{
		this.box.setVisible(true);
		this.mediaPlayer = this.componentPlayer.getMediaPlayer();
		this.mediaPlayer.playMedia(toPlay);
		
	}//end of play something 
	
}//end of class

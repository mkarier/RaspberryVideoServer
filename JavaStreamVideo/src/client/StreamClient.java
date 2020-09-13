package client;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedWriter;
import java.io.IOException;

import javax.swing.JFrame;

import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

public class StreamClient 
{
	public JFrame box = new JFrame("Client Player");
	public GraphicsDevice device;
	public EmbeddedMediaPlayerComponent componentPlayer = new EmbeddedMediaPlayerComponent();
	public EmbeddedMediaPlayer mediaPlayer;
	public BufferedWriter out;
	private boolean inFullScreen = false;
	//public EmbeddedMediaPlayerComponent player;
	String toPlay = "";
	
	public StreamClient() {}

	public StreamClient(BufferedWriter out)
	{
		this.device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		this.out = out;
	}//end of constructor
	 
	private KeyAdapter adapter = new KeyAdapter()
	{
		@Override
		public void keyReleased(KeyEvent e) {
			// TODO Auto-generated method stub
			if(e.getKeyChar() == KeyEvent.VK_ENTER)
			{
				System.out.println("Enter Key was pressed and should change to a windowed mode");
				if(inFullScreen)
				{
					System.out.println("Enter key was pressed and changing to windowed mode");
					device.setFullScreenWindow(null);
					inFullScreen = false;
				}
				else
				{
					System.out.println("Changing to Full Screen");
					device.setFullScreenWindow(box);
					inFullScreen = true;
				}
			}//end of enter key pressed
		}//end of keyReleased
	};//end of keyAdapter

	public void init(String toPlay)
	{
		this.box = new JFrame("Playing from: " + toPlay);
		this.box.setBounds(100,100, 800, 400);
		this.box.setExtendedState(JFrame.MAXIMIZED_BOTH);
		this.box.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.toPlay = toPlay;
		this.componentPlayer = new EmbeddedMediaPlayerComponent();
		this.componentPlayer.addKeyListener(adapter);
		this.box.setContentPane(this.componentPlayer);
		this.box.addKeyListener(adapter);
	}//end of init
	
	
	public void close()
	{
		this.mediaPlayer.stop();
		this.mediaPlayer.release();
		this.componentPlayer.release();
		this.box.setVisible(false);
		this.box.dispose();
		device.setFullScreenWindow(null);
		inFullScreen = false;
	}//end of close
	
	
	public void playSomething() throws IOException
	{
		this.out.write("start\n");
		this.out.flush();
		this.box.setVisible(true);
		this.mediaPlayer = this.componentPlayer.getMediaPlayer();
		this.mediaPlayer.playMedia(toPlay);
		this.device.setFullScreenWindow(this.box);
		this.inFullScreen = true;
	}//end of play something 
	
}//end of class

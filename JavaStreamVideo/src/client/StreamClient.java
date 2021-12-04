package client;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.io.BufferedWriter;
import java.io.IOException;

import javax.swing.JFrame;

import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;


public class StreamClient 
{
	public JFrame box = new JFrame("Client Player");
	public GraphicsDevice device;
	public EmbeddedMediaPlayerComponent componentPlayer = new EmbeddedMediaPlayerComponent();
	public EmbeddedMediaPlayer mediaPlayer;
	public BufferedWriter out;
	public long audioDelay = 50;
	private boolean inFullScreen = false;
	//public EmbeddedMediaPlayerComponent player;
	String toPlay = "";
	boolean pause = false;
	
	public StreamClient() {}

	public StreamClient(BufferedWriter out)
	{
		this.device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		this.out = out;
	}//end of constructor
	
	public KeyAdapter getAdapter()
	{
		return this.adapter;
	}//end of getAdapter()
	
	private KeyAdapter adapter = new KeyAdapter()
	{
		@Override
		public void keyReleased(KeyEvent e) {
			// TODO Auto-generated method stub
			
			
			switch(e.getKeyChar())
			{
			case KeyEvent.VK_SPACE:
				System.out.println("Going to pause or continue from pause");
				if(!pause)
				{
					sendCommand("PAUSE");
					System.out.println("Going to try and Pause");
				}
				else
				{
					sendCommand("PLAY");
					System.out.println("Going to try and continue");
				}
				pause = !pause;
				break;
			case KeyEvent.VK_ENTER:
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
				break;
			case 'j':
			case 'J':
				sendCommand("SYNCTRACKFORWARD");
				break;
			case 'l':
			case 'L':
				sendCommand("SYNCTRACKBACKWARD");
				break;
			case 'a':
			case 'A':
				sendCommand("CycleAudio");
			case'n':
			case'N':
				sendCommand("SkipChapter");
				break;
			case 'p':
			case 'P':
				sendCommand("PreviousChapter");
				break;
			}//end of switch statment
			//box.requestFocusInWindow();
		}//end of keyReleased
	};//end of keyAdapter

	
	public void sendCommand(String cmd)
	{
		try {
			out.append(cmd);
			out.newLine();
			out.flush();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	public void init(String toPlay)
	{
		this.box = new JFrame("Playing from: " + toPlay);
		this.box.setBounds(100,100, 800, 400);
		this.box.setExtendedState(JFrame.MAXIMIZED_BOTH);
		this.box.addFocusListener(new FocusListener()
				{

					@Override
					public void focusGained(FocusEvent e) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void focusLost(FocusEvent e) {
						// TODO Auto-generated method stub
						//box.requestFocus();
					}
				});
		this.box.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.toPlay = toPlay;
		this.componentPlayer = new EmbeddedMediaPlayerComponent();
		this.componentPlayer.addKeyListener(adapter);
		this.box.setContentPane(this.componentPlayer);
		this.box.addKeyListener(adapter);
	}//end of init
	
	
	public void close()
	{
		//this.mediaPlayer.controls().stop();
		//this.mediaPlayer.release();
		//this.componentPlayer.release();
		this.box.setVisible(false);
		this.box.dispose();
		//device.setFullScreenWindow(null);
		//inFullScreen = false;
	}//end of close
	
	
	public void playSomething() throws IOException
	{
		this.out.write("start\n");
		this.out.flush();
		this.box.setVisible(true);
		this.mediaPlayer = (EmbeddedMediaPlayer) this.componentPlayer.mediaPlayer();
		this.mediaPlayer.media().play(toPlay);
		//this.mediaPlayer.setAudioDelay(audioDelay);
		if(this.inFullScreen)
		{
			this.device.setFullScreenWindow(this.box);
			this.inFullScreen = true;
		}
		try {
			Thread.sleep(1*1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}//end of play something 
	
}//end of class

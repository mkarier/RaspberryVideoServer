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

import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import shared_class.SharedData;
import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.javafx.fullscreen.JavaFXFullScreenStrategy;
import uk.co.caprica.vlcj.javafx.videosurface.ImageViewVideoSurface;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.player.embedded.fullscreen.adaptive.AdaptiveFullScreenStrategy;


public class StreamClient extends Thread implements AutoCloseable
{
	public JFrame box = new JFrame("Client Player");
	protected ImageView view = new ImageView();
	public Scene scene = getScene();
	public Stage stage = null;
	//public GraphicsDevice device;
	public EmbeddedMediaPlayerComponent componentPlayer = new EmbeddedMediaPlayerComponent();
	public EmbeddedMediaPlayer mediaPlayer = this.componentPlayer.mediaPlayer();
	public BufferedWriter out;
	
	//public long audioDelay = 50;
	//private boolean inFullScreen = false;
	//public EmbeddedMediaPlayerComponent player;
	String networkOptions = ":network-caching=1000";
	String toPlay = "";
	boolean pause = false;
	
	public StreamClient() {}

	public StreamClient(BufferedWriter out)
	{
		//this.device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();	
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
				mediaPlayer.fullScreen().toggle();
				/*if(inFullScreen)
				{
					componentPlayer.mediaPlayer().fullScreen();
					System.out.println("Enter key was pressed and changing to windowed mode");
					//device.setFullScreenWindow(null);
					inFullScreen = false;
				}
				else
				{
					componentPlayer.mediaPlayer().
					System.out.println("Changing to Full Screen");
					//device.setFullScreenWindow(box);
					inFullScreen = true;
				}*/
				break;
			case 'j':
			case 'J':
				sendCommand("SKIP");
				//sendCommand("SYNCTRACKFORWARD");
				break;
			case 'l':
			case 'L':
				sendCommand("PREVIOUS");
				//sendCommand("SYNCTRACKBACKWARD");
				break;
			case 'a':
			case 'A':
				sendCommand("CycleAudio");
				break;
			case'n':
			case'N':
				//sendCommand("SkipChapter");
				sendCommand("SKIPFORWARD");
				break;
			case 'p':
			case 'P':
				sendCommand("PreviousChapter");
				break;
			case 't':
			case 'T':
				sendCommand("TITLE");
				break;	
			case KeyEvent.VK_0:
				mediaPlayer.video().setBrightness(0.5f);
				break;
			case KeyEvent.VK_1:
				mediaPlayer.video().setBrightness(1f);
				break;
			case KeyEvent.VK_2:
				mediaPlayer.video().setBrightness(2f);
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
	
	public void init(String toPlay, String networkOptions)
	{
		this.networkOptions = networkOptions;
		this.box = new JFrame("Playing from: " + toPlay);
		this.box.setBounds(100,100, 800, 400);
		this.box.setExtendedState(JFrame.MAXIMIZED_BOTH);
		this.box.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.toPlay = toPlay;
		this.componentPlayer = new EmbeddedMediaPlayerComponent(
				null,
				null,
				new AdaptiveFullScreenStrategy(this.box),
				null,
				null);		
		this.mediaPlayer = this.componentPlayer.mediaPlayer();			
		this.componentPlayer.addKeyListener(adapter);
		this.box.setContentPane(this.componentPlayer);
		this.box.addKeyListener(adapter);
	}//end of init
	
	public Stage init(Stage primaryStage, String toPlay, String networkOptions)
	{	
		this.stage = primaryStage;
		this.toPlay = toPlay;
		this.networkOptions = networkOptions;
		this.componentPlayer = new EmbeddedMediaPlayerComponent(
				null,
				null,
				new JavaFXFullScreenStrategy(primaryStage),
				null,
				null);//*/			
		//this.componentPlayer.addKeyListener(adapter);
		//this.componentPlayer.mediaPlayer().fullScreen().strategy(new JavaFXFullScreenStrategy(primaryStage));
		this.componentPlayer.mediaPlayer().videoSurface().set(new ImageViewVideoSurface(this.view));
		this.mediaPlayer = this.componentPlayer.mediaPlayer();
		
		//MediaPlayerFactory mediaPlayerFactory = new MediaPlayerFactory();
		//this.mediaPlayer = mediaPlayerFactory.mediaPlayers().newEmbeddedMediaPlayer();
		
		this.mediaPlayer.videoSurface().set(new ImageViewVideoSurface(this.view));
		//this.mediaPlayer.fullScreen().strategy(new JavaFXFullScreenStrategy(this.stage));
		
		this.stage.setScene(scene);
		this.stage.setTitle("PlayingFrom: " + toPlay);
		return this.stage;
	}//end of init for javafx Client
	
	@Override
	public void close()
	{
		//this.mediaPlayer.controls().stop();
		this.mediaPlayer.release();
		//this.componentPlayer.release();
		if(this.scene != null)
		this.box.setVisible(false);
		this.box.dispose();
		//device.setFullScreenWindow(null);
		//inFullScreen = false;
	}//end of close
	
	
	public void run()
	{
		try {
			this.out.write("start\n");
			this.out.flush();
			if(this.scene == null)
			{
				this.box.setVisible(true);
				this.mediaPlayer.submit(()-> {
					this.mediaPlayer.media().play(toPlay, this.networkOptions, ":network-synchronisation");
					this.mediaPlayer.video().setAdjustVideo(true);
				});		
			}
			else
			{
				//this.stage.show();
				//this.mediaPlayer.media().play(toPlay, this.networkOptions, ":network-synchronisation");
				//this.mediaPlayer.video().setAdjustVideo(true);
				this.mediaPlayer.submit(()-> {
					this.mediaPlayer.media().play(toPlay, this.networkOptions, ":network-synchronisation");
					this.mediaPlayer.video().setAdjustVideo(true);
				});	
			}//end of else
			
			
				
			System.out.println("StreamClient.run " + networkOptions);
			//this.mediaPlayer.setAudioDelay(audioDelay);
			/*if(this.inFullScreen)
			{
				this.device.setFullScreenWindow(this.box);
				this.inFullScreen = true;
			}*/
			//while(this.box.isVisible());
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}//end of play something 

	public void setTitle(String fromServer) {
		this.box.setTitle(SharedData.access + "://@" + fromServer);
		
	}//end setTitle
	
	protected Scene getScene() {
		BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: black;");
        view.setPreserveRatio(true);
        view.fitWidthProperty().bind(root.widthProperty());
        view.fitHeightProperty().bind(root.heightProperty());
        root.setCenter(view);
		Scene scene= new Scene(root, 600, 600);
		scene.setOnKeyReleased((e) -> {
			System.out.println("Client clicked = " + e.getCode());
			switch(e.getCode())
			{
				case ENTER:
					this.mediaPlayer.fullScreen().set(!this.mediaPlayer.fullScreen().isFullScreen());
					System.out.println("FullScreen? = " + this.mediaPlayer.fullScreen().isFullScreen());
					stage.setFullScreen(this.mediaPlayer.fullScreen().isFullScreen());
					break;
				case PAUSE:
				case SPACE:
					sendCommand(this.pause?"PLAY":"PAUSE");
					this.pause = !this.pause;
					break;
				case TRACK_NEXT:
				case N:
					sendCommand("SKIP");
				case A:
					sendCommand("CycleAudio");
					break;
				case UNDEFINED:
					System.out.println("UNDEFINED keyCode = " + e.getText());
					break;
				default:
					break;
			}
		});//end of setOnKeyReleased
		return scene;
	}//end of addKeyEvents
	
}//end of class

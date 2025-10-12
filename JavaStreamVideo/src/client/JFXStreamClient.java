package client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.List;

import com.sun.jna.NativeLibrary;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import shared_class.SharedData;
import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.factory.discovery.NativeDiscovery;
import uk.co.caprica.vlcj.javafx.fullscreen.JavaFXFullScreenStrategy;
import uk.co.caprica.vlcj.javafx.videosurface.ImageViewVideoSurface;
import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.player.embedded.videosurface.CallbackVideoSurface;


public class JFXStreamClient extends Application
{
	Socket socket = null;
	public BufferedWriter out;
	public BufferedReader in;
	//public long audioDelay = 50;
	//private boolean inFullScreen = false;
	//public EmbeddedMediaPlayerComponent player;
	ImageView view = new ImageView();
	String networkOptions = ":network-caching=1000";
	String toPlay = "";
	boolean pause = false;
	private MediaPlayerFactory mediaPlayerFactory = new MediaPlayerFactory();
	private EmbeddedMediaPlayer mediaPlayer= this.mediaPlayerFactory.mediaPlayers().newEmbeddedMediaPlayer();
	
	private Thread serverListener;
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		List<String> args = getParameters().getRaw();
		for(String input: args)
			System.out.println("args: " + input);
		this.networkOptions = ":network-caching=";
		String host = (args.size() >=1)? args.get(0):"localhost";
		networkOptions += (args.size()>=2)? args.get(1):"1000";
		this.socket = new Socket(host, SharedData.comPort);
		this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		String videoPort = in.readLine();
		System.out.println("JFXStreamClient: video Port = " + videoPort);
		this.toPlay = SharedData.access + "://@:" + videoPort;

		/*var player = createStreamClient(out, primaryStage, toPlay, networkOptions);
		primaryStage.show();
		player.start();
		//Platform.runLater(player);
		
		primaryStage.requestFocus();
		//player.run();*/
		
		
		//initStreamClient(primaryStage, host);
		initEmbeddedStreamClient(primaryStage, host);
		primaryStage.show();
		while(!primaryStage.isShowing());
		try {
			out.write("start\n");
			out.flush();
			this.mediaPlayer.submit(()->
		  	{
		  		this.mediaPlayer.media().play(toPlay, this.networkOptions,":network-synchronisation");
		  		//this.mediaPlayer.video().setAdjustVideo(true);
		    }); 
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}//*/
		
		(new Thread(()->{talkWithServer(primaryStage);})).start();
		System.out.println("Finished Setting shit up");
		//String fromServer = "continue";
		/*while(!fromServer.contains("quit"))
		{
			fromServer = in.readLine();//TODO: make this better. Right now it is waiting for the server to write 'stop' before moving on
			System.out.println("JFXStreamCLient: " + fromServer);
			//primaryStage.setTitle(fromServer);
			//player.setTitle(fromServer);
			//System.out.println("Player was closed");
		}//end of while*/
		
		
	}//end of start
	
	
	protected StreamClient createStreamClient(BufferedWriter out, Stage primaryStage, String toPlay, String networkOptions)
	{
		var client = new StreamClient(out);
		client.init(primaryStage, toPlay, networkOptions);
		return client;
	}//end ofcreateStreamClient
	
	protected Stage initStreamClient(Stage primaryStage, String host)
	{
		//this.mediaPlayer.videoSurface().set(new ImageViewVideoSurface(this.view));
		this.mediaPlayer.fullScreen().strategy(new JavaFXFullScreenStrategy(primaryStage));
		Scene scene = getScene(primaryStage);
	    primaryStage.setScene(scene);
	    primaryStage.setTitle("JFXStreaming from " + host);
		return primaryStage;
	}
	
	protected Stage initEmbeddedStreamClient(Stage primaryStage, String host)
	{
		
		var componentPlayer = new EmbeddedMediaPlayerComponent(
				null,
				null,
				new JavaFXFullScreenStrategy(primaryStage),
				null,
				null);
		
		//componentPlayer.mediaPlayer().videoSurface().set(new ImageViewVideoSurface(this.view));
		this.mediaPlayer = componentPlayer.mediaPlayer();
		Scene scene = getScene(primaryStage);
	    primaryStage.setScene(scene);
	    primaryStage.setTitle("JFXStreaming from " + host);
		return primaryStage;
	}//end of initEmbeddedStreamClient
	
	protected Stage initJavafxClient(Stage primaryStage)
	{
		return primaryStage;
	}
	
	public String talkWithServer(Stage primaryStage){
		String fromServer = "continues";
		try
		{
			while(!fromServer.contains("quit"))
			{
				if(in.ready())
				{
					fromServer = in.readLine();//TODO: make this better. Right now it is waiting for the server to write 'stop' before moving on
					updateTitle(primaryStage, fromServer);
				}
			}//end of while
		}catch(IOException e) {e.printStackTrace();}
		return fromServer;
	}//end of talkWithServer

	
	protected Scene getScene(Stage stage) {
		BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: black;");
        view.setPreserveRatio(true);
        view.fitWidthProperty().bind(root.widthProperty());
        view.fitHeightProperty().bind(root.heightProperty());
        root.setCenter(view);
		Scene scene= new Scene(root, 600, 600, Color.ALICEBLUE);
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
					break;
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
		this.mediaPlayer.videoSurface().set(new ImageViewVideoSurface(this.view));
		return scene;
	}//end of addKeyEvents
	
	public void sendCommand(String cmd)
	{
		try {
			out.append(cmd);
			out.newLine();
			out.flush();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}//end of catch
	}//end of send command
	
	private void updateTitle(Stage primaryStage, String title){
		System.out.println(title);
		Platform.runLater(()->{
			primaryStage.setTitle(title);
		});
		
	}//end of update Title
	
	
	public static void main(String[] args) 
	{
		/*if(System.getProperty("os.name").contains("Windows"))
			NativeLibrary.addSearchPath("libvlc", SharedData.vlcPath);
		else
			System.out.println("OS = linux");
		new NativeDiscovery().discover();*/
		//ClientMain.args = args;
		launch(args);
	}//end of main
}//end of class

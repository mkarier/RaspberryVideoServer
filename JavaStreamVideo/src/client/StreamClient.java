package client;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.List;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import shared_class.SharedData;
import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.javafx.fullscreen.JavaFXFullScreenStrategy;
import uk.co.caprica.vlcj.javafx.videosurface.ImageViewVideoSurface;
import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

public class StreamClient extends Application
{
	public BufferedWriter out;
	public BufferedReader in;
	//public long audioDelay = 50;
	//private boolean inFullScreen = false;
	//public EmbeddedMediaPlayerComponent player;
	ImageView view = new ImageView();;
	String networkOptions = ":network-caching=1000";
	String toPlay = "";
	boolean pause = false;
	private MediaPlayerFactory mediaPlayerFactory = new MediaPlayerFactory();
	private EmbeddedMediaPlayer mediaPlayer= this.mediaPlayerFactory.mediaPlayers().newEmbeddedMediaPlayer();
	Socket socket = null;
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
		this.serverListener = new Thread(()->{
			try {
				talkWithServer(primaryStage);
			} catch (IOException | InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});

		String videoPort = in.readLine();
		System.out.println("video Port = " + videoPort);
		this.toPlay = SharedData.access + "://@:" + videoPort;
        Scene scene = this.getScene(primaryStage);
        primaryStage.setTitle("Streaming from " + host);
        primaryStage.setScene(scene);
        this.out.write("start\n");
		this.out.flush();
		mediaPlayer.videoSurface().set(new ImageViewVideoSurface(this.view));
		mediaPlayer.fullScreen().strategy(new JavaFXFullScreenStrategy(primaryStage));
		System.out.println("StreamClient:run = NetworkOptions" + networkOptions);
		this.mediaPlayer.submit(()->{
			this.mediaPlayer.media().play(toPlay, this.networkOptions, ":network-synchronisation");
			this.mediaPlayer.video().setAdjustVideo(true);
		});
		this.serverListener.start();
		primaryStage.show();
		/*Platform.runLater(()->{
			try {
				talkWithServer(primaryStage);
			} catch (IOException | InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});*/
		
		System.out.println("Finished Setting shit up");
		
	}//end of start
	
	
	public String talkWithServer(Stage primaryStage) throws IOException, InterruptedException {
		String fromServer = "continues";
		while(!fromServer.contains("quit"))
		{
			if(in.ready())
			{
				fromServer = in.readLine();//TODO: make this better. Right now it is waiting for the server to write 'stop' before moving on
				updateTitle(primaryStage, fromServer);
			}
		}//end of while
		return fromServer;
	}//end of talkWithServer

	
	private Scene getScene(Stage primaryStage) {
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
					this.mediaPlayer.submit(()->{
						this.mediaPlayer.fullScreen().set(!this.mediaPlayer.fullScreen().isFullScreen());
					});
					
					break;
				case PAUSE:
				case SPACE:
					sendCommand(this.pause?"PLAY":"PAUSE");
					this.pause = !this.pause;
					break;
				case TRACK_NEXT:
				case N:
					sendCommand("SKIP");
				default:
					break;
			}
		});//end of setOnKeyReleased
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
		primaryStage.setTitle(title);
	}//end of update Title
	
	
	public static void main(String[] args) 
	{
		//ClientMain.args = args;
		launch(args);
	}//end of main
}//end of class

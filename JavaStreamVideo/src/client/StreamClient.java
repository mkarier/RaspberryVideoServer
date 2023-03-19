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
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
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
	private Stage primaryStage;
	//public long audioDelay = 50;
	//private boolean inFullScreen = false;
	//public EmbeddedMediaPlayerComponent player;
	ImageView view = new ImageView();;
	String networkOptions = ":network-caching=1000";
	String toPlay = "";
	boolean pause = false;
	private MediaPlayerFactory mediaPlayerFactory = new MediaPlayerFactory();
	private EmbeddedMediaPlayer mediaPlayer;
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		List<String> args = getParameters().getRaw();
		for(String input: args)
			System.out.println("args: " + input);
		primaryStage.setTitle("ImageView");
		this.networkOptions = ":network-caching=";
		String host = (args.size() >=1)? args.get(0):"localhost";
		networkOptions += (args.size()>=2)? args.get(1):"1000";
		try(Socket socket = new Socket(host, SharedData.comPort);){
			this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			String videoPort = in.readLine();
			System.out.println("video Port = " + videoPort);
			this.toPlay = SharedData.access + "://@:" + videoPort;
			this.mediaPlayer = this.mediaPlayerFactory.mediaPlayers().newEmbeddedMediaPlayer();
			BorderPane root = new BorderPane();
	        root.setStyle("-fx-background-color: black;");
	        view.fitWidthProperty().bind(root.widthProperty());
	        view.fitHeightProperty().bind(root.heightProperty());

	        root.widthProperty().addListener((observableValue, oldValue, newValue) -> {
	            // If you need to know about resizes
	        });

	        root.heightProperty().addListener((observableValue, oldValue, newValue) -> {
	            // If you need to know about resizes
	        });
	        root.setCenter(view);

	        Scene scene = new Scene(root, 1200, 675);
	        primaryStage.setTitle("vlcj JavaFX");
	        primaryStage.setScene(scene);
	        this.out.write("start\n");
			this.out.flush();
			mediaPlayer.videoSurface().set(new ImageViewVideoSurface(this.view));
			this.mediaPlayer.submit(()-> {
				this.mediaPlayer.media().play(toPlay, this.networkOptions, ":network-synchronisation");
				this.mediaPlayer.video().setAdjustVideo(true);
			});			
			System.out.println("StreamClient:run = NetworkOptions" + networkOptions);
			primaryStage.show();
			String fromServer = "continues";
			while(!fromServer.contains("quit"))
			{
				fromServer = in.readLine();//TODO: make this better. Right now it is waiting for the server to write 'stop' before moving on
				System.out.println(fromServer);
				this.setTitle(fromServer);
				//System.out.println("Player was closed");
			}//end of while
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch(Throwable t) {t.printStackTrace();}
		finally
		{			
			System.out.println("Reached Finally");
		}
		
	}//end of start

	

	
	
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

	
	

	public void setTitle(String fromServer) {
		//this.box.setTitle(SharedData.access + "://@" + fromServer);
		this.primaryStage.setTitle(fromServer);
	}//end of set title
	
	
	public static void main(String[] args) 
	{
		//ClientMain.args = args;
		launch(args);
	}//end of main
}//end of class

package server;

import java.net.InetAddress;

import javax.swing.JFrame;

import shared_class.SharedData;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.player.media.Media;

public class StreamVideo 
{
	String pathToVideo = "";
	String target = "";
	String options = "";
	public EmbeddedMediaPlayerComponent player;
	JFrame box = new JFrame("Server");
	boolean hasSubs = false;
	StreamVideo(InetAddress target, String pathToVideo, boolean hasSubs)
	{
		System.out.println(target.toString().substring(1));
		this.target = target.toString().substring(1);
		this.pathToVideo = pathToVideo;
		System.out.println("Path to video " + pathToVideo);
		this.player = new EmbeddedMediaPlayerComponent();
		this.box.setBounds(100,100, 800, 400);
		this.box.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.box.setContentPane(this.player);
		this.box.setVisible(true);
		this.hasSubs = hasSubs;
	}
	
	public void getOptions(boolean hasSub)
	{
		String start = "sout=#";
		String standard = "standard{access=udp,mux=ts,dst="+target+":"+SharedData.videoPort;
		String transcodeForSub = "transcode{vcodec=h264,scale=Auto,acodec=mpga,ab=128,channels=2,samplerate=44100,soverlay}:";
		String transcodeForNoSub = "transcode{vcodec=h264,vb=800,acodec=mpga,ab=128,channels=2,samplerate=44100,scodec=none}:";
		if(hasSub)
			this.options = start + transcodeForSub + standard;
		else
			this.options = start + transcodeForNoSub + standard;
	}//end of get Options
	
	public EmbeddedMediaPlayer prepareVideo(EmbeddedMediaPlayer mediaPlayer)
	{
		String start = "sout=#";
		String standard = "standard{access=udp,mux=ts,dst="+target+":"+SharedData.videoPort;
		String transcodeForSub = "transcode{vcodec=h264,scale=Auto,acodec=mpga,ab=128,channels=2,samplerate=44100,soverlay}:";
		String transcodeForNoSub = "transcode{vcodec=h264,vb=800,acodec=mpga,ab=128,channels=2,samplerate=44100,scodec=none}:";
		if(this.hasSubs)
			mediaPlayer.prepareMedia(this.pathToVideo, start, transcodeForSub, standard);
		else
			mediaPlayer.prepareMedia(this.pathToVideo, start, transcodeForNoSub, standard);
		return mediaPlayer;
	}//end of get Options
	
	
	
	public void stream()
	{
		//EmbeddedMediaPlayer mediaPlayer = prepareVideo(this.player.getMediaPlayer());
		getOptions(this.hasSubs);
		System.out.println(this.options);
		EmbeddedMediaPlayer mediaPlayer = this.player.getMediaPlayer();
		mediaPlayer.playMedia(this.pathToVideo, this.options);
		//mediaPlayer.addMediaOptions(this.options);
		//player.play();
		//while(!player.start());
		while(mediaPlayer.getPosition() < 0.99)
		{
				//System.out.println("Position " + mediaPlayer.getPosition());
		}
		
	}//end of stream
}//End of StreamVideo

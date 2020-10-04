package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.InetAddress;

import javax.swing.JFrame;

import shared_class.SharedData;
import shared_class.VideoData;
import uk.co.caprica.vlcj.binding.internal.libvlc_state_t;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.player.media.Media;

public class StreamServer extends Thread
{
	String target = "";
	String options = "";
	public EmbeddedMediaPlayerComponent componentPlayer;
	private EmbeddedMediaPlayer mediaPlayer;
	JFrame box = new JFrame("Server");
	VideoData video;
	boolean paused = false;
	BufferedReader in;
	BufferedWriter out;
	
	StreamServer(InetAddress target, VideoData video, BufferedReader in , BufferedWriter out)
	{
		System.out.println(target.toString().substring(1));
		this.in = in;
		this.out = out;
		this.target = target.toString().substring(1);
		this.video = video;
		getOptions();
		System.out.println("Path to video " + video.videoPath);
		this.componentPlayer = new EmbeddedMediaPlayerComponent();
		this.box.setBounds(100,100, 800, 400);
		this.box.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.box.setContentPane(this.componentPlayer);
		this.box.setVisible(true);
		this.box.setState(JFrame.ICONIFIED);
	}
	
	public void getOptions()
	{
		String start = "sout=#";
		String standard = String.format("standard{access=udp,mux=ts,dst=%s:%d}", target, SharedData.videoPort);
		String transcodeForSub = "transcode{vcodec=h264,scale=Auto,acodec=mpga,ab=128,channels=2,samplerate=44100,soverlay}:";
		String transcodeForNoSub = "transcode{vcodec=h264,vb=800,acodec=mpga,ab=128,channels=2,samplerate=44100,scodec=none}:";
		if(this.video.hasSubtitles)
			this.options = start + transcodeForSub + standard;
		else
			this.options = start + transcodeForNoSub + standard;
	}//end of get Options
	
	public EmbeddedMediaPlayer prepareVideo(EmbeddedMediaPlayer mediaPlayer)
	{
		String start = "sout=#";
		String standard = String.format("standard{access=udp,mux=ts,dst=%s:%d}", target, SharedData.videoPort);
		String transcodeForSub = "transcode{vcodec=h264,scale=Auto,acodec=mpga,ab=128,channels=2,samplerate=44100,soverlay}:";
		String transcodeForNoSub = "transcode{vcodec=h264,vb=800,acodec=mpga,ab=128,channels=2,samplerate=44100,scodec=none}:";
		if(video.hasSubtitles)
		{
			mediaPlayer.prepareMedia(this.video.videoPath, start, transcodeForSub, standard);
			if(this.video.subtitlePath != null)
				mediaPlayer.setSubTitleFile(this.video.subtitlePath);
		}
		else
			mediaPlayer.prepareMedia(this.video.videoPath, start, transcodeForNoSub, standard);
		return mediaPlayer;
	}//end of get Options
	
	
	public void close()
	{
		this.mediaPlayer.stop();
		this.mediaPlayer.release();
		this.componentPlayer.release();
		this.box.setVisible(false);
		this.box.dispose();
	}//end of close
	
	public float getPosition() {
		return this.mediaPlayer.getPosition();
	}//end of getPosition
	
	public long getCurrentTime()
	{
		return this.mediaPlayer.getTime();
	}
	
	public long getDuration() {
		return this.mediaPlayer.getLength();
	}
	
	public boolean isPaused()
	{
		return this.paused;
	}//end of isPaused
	
	public boolean isPlaying()
	{
		return libvlc_state_t.libvlc_Playing == this.mediaPlayer.getMediaPlayerState();
	}//end of is playing
	
	public void stream()
	{
		//EmbeddedMediaPlayer mediaPlayer = prepareVideo(this.player.getMediaPlayer());
		System.out.println(this.options);
		this.mediaPlayer = this.componentPlayer.getMediaPlayer();
		mediaPlayer.playMedia(this.video.videoPath, this.options);
		if(this.video.subtitlePath != null)
			mediaPlayer.setSubTitleFile(this.video.subtitlePath);
		
	}//end of stream
	
	
	@Override
	public void run()
	{
		while(true)
		{
			String cmd = "";
			try {
				switch((cmd = this.in.readLine().toUpperCase()))
				{
				case "PAUSE":
					this.mediaPlayer.pause();
					this.paused = true;
					break;
				case "PLAY":
					this.mediaPlayer.play();
					this.paused = false;
				}//end of switch
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}//end of catch
			System.out.println(cmd);
		}//end of while loop
	}
}//End of StreamVideo

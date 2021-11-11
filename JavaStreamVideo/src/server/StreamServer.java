package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.util.List;

import javax.swing.JFrame;

import shared_class.SharedData;
import shared_class.VideoData;
import uk.co.caprica.vlcj.factory.discovery.NativeDiscovery;
import uk.co.caprica.vlcj.factory.discovery.strategy.WindowsNativeDiscoveryStrategy;
import uk.co.caprica.vlcj.player.base.TrackDescription;
import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

public class StreamServer extends Thread
{
	String transcodeForSub = "transcode{vcodec=h264,scale=Auto,acodec=mpga,ab=128,channels=2,samplerate=44100,soverlay}:";
	String transcodeForNoSub = "transcode{vcodec=h264,vb=800,acodec=mpga,ab=128,channels=2,samplerate=44100,scodec=none}:";
	//String transcodeForNoSub = "transcode{vcodec=hevc,acodec=mpga,ab=128,channels=2,samplerate=44100,scodec=none}:";
	//String transcodeForSub = "transcode{vcodec=hevc,acodec=mpga,ab=128,channels=2,samplerate=44100,soverlay}:";
	//String transcode = "vcodec=hevc,acodec=mpga,ab=128,channels=2,samplerate=44100,scodec=none";
	String target = "";
	String options = "";
	public EmbeddedMediaPlayerComponent componentPlayer;
	private EmbeddedMediaPlayer mediaPlayer;
	List<TrackDescription> audioDescriptions;
	JFrame box = new JFrame("Server");
	VideoData video;
	boolean paused = false;
	int audioTrack = 0;
	int audioDelay = 0;
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
		try
		{			
			this.componentPlayer = new EmbeddedMediaPlayerComponent();
		}catch(Exception e) {e.printStackTrace();}
		this.box.setBounds(100,100, 800, 400);
		this.box.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.box.setContentPane(this.componentPlayer);
		this.box.setVisible(true);
		this.box.setState(JFrame.ICONIFIED);
	}
	
	public void getOptions()
	{
		String start = "sout=#";
		String standard = String.format("%s{mux=ts,dst=%s,port=%d}", SharedData.access, target, SharedData.videoPort);
		if(this.video.hasSubtitles)
			this.options = start + transcodeForSub + standard;
		else
			this.options = start + transcodeForNoSub + standard;
	}//end of get Options
	
	public EmbeddedMediaPlayer prepareVideo(EmbeddedMediaPlayer mediaPlayer)
	{
		String start = "sout=#";
		String standard = String.format("%s{mux=ts,dst=%s,port=%d}", SharedData.access, target, SharedData.videoPort);
		if(video.hasSubtitles)
		{		
			mediaPlayer.media().play(this.video.videoPath, start, transcodeForSub, standard);
			if(this.video.subtitlePath != null)
				mediaPlayer.subpictures().setSubTitleFile(this.video.subtitlePath);
		}
		else
			mediaPlayer.media().play(this.video.videoPath, start, transcodeForNoSub, standard);
		return mediaPlayer;
	}//end of get Options
	
	
	public void close()
	{		
		//this.mediaPlayer.controls().stop();
		this.mediaPlayer.release();
		//this.componentPlayer.release();
		this.box.setVisible(false);
		this.box.dispose();
	}//end of close
	
	public float getPosition() {
		return this.mediaPlayer.status().position();
	}//end of getPosition
	
	public long getCurrentTime()
	{
		return this.mediaPlayer.status().time();
	}
	
	public long getDuration() {
		return this.mediaPlayer.status().length();
	}
	
	public void skipChapter()
	{
		this.mediaPlayer.controls().skipTime(30 * 1000);
	}
	
	public boolean isPaused()
	{
		return this.paused;
	}//end of isPaused
	
	public void pause()
	{
		this.mediaPlayer.controls().pause();
		this.paused = true;
	}
	
	public void play() throws InterruptedException
	{
		this.mediaPlayer.controls().pause();
		this.paused = false;
	}
	
	public void cycleAudio()
	{
		System.out.println("Track Count = " + this.mediaPlayer.audio().trackCount());
		this.audioDescriptions = this.mediaPlayer.audio().trackDescriptions();
		for(TrackDescription temp : this.audioDescriptions)
		{
			System.out.println(temp.description() + " ID: " + temp.id());
		}
		
		audioTrack++;
		if(audioTrack >= this.mediaPlayer.audio().trackCount())
			audioTrack = 0;
		this.mediaPlayer.audio().setTrack(this.audioDescriptions.get(audioTrack).id());
		//this.mediaPlayer.mute();
		//this.mediaPlayer.mute(false);
		System.out.println("Audio index = " + audioTrack);
		System.out.println("Audio Description: " + this.audioDescriptions.get(audioTrack).description());
		System.out.println("Audio ID: " + this.audioDescriptions.get(audioTrack).id());
	}//end of cycleAudio
	
	public boolean isPlaying()
	{
		return this.mediaPlayer.status().isPlaying();
	}//end of is playing
	
	public void stream()
	{
		//EmbeddedMediaPlayer mediaPlayer = prepareVideo(this.player.getMediaPlayer());
		System.out.println(this.options);
		this.mediaPlayer = this.componentPlayer.mediaPlayer();
		this.mediaPlayer.media().play(this.video.videoPath, this.options);
		this.audioTrack = this.mediaPlayer.audio().track();
		if(this.video.subtitlePath != null)
			mediaPlayer.subpictures().setSubTitleFile(this.video.subtitlePath);
		
	}//end of stream
	
	
	@Override
	public void run()
	{
		try {
			while(true)
			{
				String cmd = "";
				
					switch((cmd = this.in.readLine().toUpperCase()))
					{
					case "PAUSE":
						this.mediaPlayer.controls().pause();
						this.paused = true;
						break;
					case "PLAY":
						this.mediaPlayer.controls().start();
						this.paused = false;
						break;
					case "CYCLEAUDIO":
						System.out.println("Track Count = " + this.mediaPlayer.audio().trackCount());
						this.audioDescriptions = this.mediaPlayer.audio().trackDescriptions();
						for(TrackDescription temp : this.audioDescriptions)
						{
							System.out.println(temp.description() + " ID: " + temp.id());
						}
						
						audioTrack++;
						if(audioTrack >= this.mediaPlayer.audio().trackCount())
							audioTrack = 0;
						this.mediaPlayer.audio().setTrack(this.audioDescriptions.get(audioTrack).id());
						//this.mediaPlayer.mute();
						//this.mediaPlayer.mute(false);
						System.out.println("Audio index = " + audioTrack);
						System.out.println("Audio Description: " + this.audioDescriptions.get(audioTrack).description());
						System.out.println("Audio ID: " + this.audioDescriptions.get(audioTrack).id());
						break;
					case "SYNCTRACKFORWARD":
						this.audioDelay += 50;
						this.mediaPlayer.audio().setDelay(audioDelay);
						System.out.println("Audio Delay = " + this.audioDelay);
						break;
					case "SYNCTRACKBACKWARD":
						this.audioDelay -= 50;
						this.mediaPlayer.audio().setDelay(audioDelay);
						System.out.println("Audio Delay = " + this.audioDelay);
						break;
					case "SKIPCHAPTER":
						this.mediaPlayer.chapters().next();
						break;
					case "PREVIOUSCHAPTER":
						this.mediaPlayer.chapters().previous();
					}//end of switch
				
				System.out.println(cmd);
			}//end of while loop
		} catch (IOException e) {
			// TODO Auto-generated catch block
			this.mediaPlayer.controls().stop();
			e.printStackTrace();
		}//end of catch
	}
}//End of StreamVideo

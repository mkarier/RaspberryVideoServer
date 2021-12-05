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
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.base.TrackDescription;
import uk.co.caprica.vlcj.player.component.EmbeddedMediaListPlayerComponent;
import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.player.list.MediaListPlayer;

public class StreamServer extends Thread
{
	String transcodeForSub = "transcode{vcodec=h264,scale=Auto,acodec=mpga,ab=128,channels=2,samplerate=44100,soverlay}:";
	String transcodeForNoSub = "transcode{vcodec=h264,vb=800,acodec=mpga,ab=128,channels=2,samplerate=44100,scodec=none}:";
	//String transcodeForNoSub = "transcode{vcodec=hevc,acodec=mpga,ab=128,channels=2,samplerate=44100,scodec=none}:";
	//String transcodeForSub = "transcode{vcodec=hevc,acodec=mpga,ab=128,channels=2,samplerate=44100,soverlay}:";
	//String transcode = "vcodec=hevc,acodec=mpga,ab=128,channels=2,samplerate=44100,scodec=none";
	String target = "";
	public EmbeddedMediaListPlayerComponent componentPlayer;
	//private MediaPlayer mediaPlayer;
	private MediaListPlayer listPlayer;
	//List<TrackDescription> audioDescriptions;
	JFrame box = new JFrame("Server");
	List<VideoData> videos;
	boolean paused = false;
	int audioTrack = 0;
	int audioDelay = 0;
	BufferedReader in;
	BufferedWriter out;
	
	StreamServer(InetAddress target, List<VideoData> videos, BufferedReader in , BufferedWriter out)
	{
		System.out.println(target.toString().substring(1));
		this.in = in;
		this.out = out;
		this.target = target.toString().substring(1);
		this.videos = videos;
		
		//getOptions(data);
		try
		{			
			this.componentPlayer = new EmbeddedMediaListPlayerComponent();
			this.listPlayer = this.componentPlayer.mediaListPlayer();
		}catch(Exception e) {e.printStackTrace();}
		addVideos(videos);
		this.box.setBounds(100,100, 800, 400);
		this.box.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.box.setContentPane(this.componentPlayer);
		this.box.setVisible(true);
		this.box.setState(JFrame.ICONIFIED);
	}
	
	public void addVideos(List<VideoData> videos)
	{		
		for(VideoData data: videos)
		{
			String options = this.getOptions(data);
			this.listPlayer.list().media().add(data.videoPath, options);
		}//end of for loop 
	}//end of addVideos
	
	public String getOptions(VideoData video)
	{
		String options = "";
		String start = "sout=#";
		String standard = String.format("%s{mux=ts,dst=%s,port=%d}", SharedData.access, target, SharedData.videoPort);
		if(video.hasSubtitles)
			options = start + transcodeForSub + standard;
		else
			options = start + transcodeForNoSub + standard;
		return options;
	}//end of get Options
	
	/*public EmbeddedMediaPlayer prepareVideo(EmbeddedMediaPlayer mediaPlayer, VideoData video)
	{
		String start = "sout=#";
		String standard = String.format("%s{mux=ts,dst=%s,port=%d}", SharedData.access, target, SharedData.videoPort);
		if(video.hasSubtitles)
		{		
			mediaPlayer.media().play(video.videoPath, start, transcodeForSub, standard);
			if(video.subtitlePath != null)
				mediaPlayer.subpictures().setSubTitleFile(video.subtitlePath);
		}
		else
			mediaPlayer.media().play(video.videoPath, start, transcodeForNoSub, standard);
		return mediaPlayer;
	}//end of get Options*/
	
	
	public void close()
	{		
		//this.mediaPlayer.controls().stop();
		this.listPlayer.release();
		//this.componentPlayer.release();
		this.box.setVisible(false);
		this.box.dispose();
	}//end of close
	
	public float getPosition() {
		return this.componentPlayer.mediaPlayer().status().position();
	}//end of getPosition
	
	public long getCurrentTime()
	{
		try
		{
			return this.componentPlayer.mediaPlayer().status().time();
		}catch(NullPointerException e){
			e.printStackTrace();
			return -20l;
		}
	}//end of getCurrentTime
	
	public long getDuration() {
		try
		{
			return this.componentPlayer.mediaPlayer().status().length();			
		}catch(NullPointerException e){e.printStackTrace();
		return -20l;}
	}//end of getDuration
	
	public void skipChapter()
	{
		this.componentPlayer.mediaPlayer().controls().skipTime(30 * 1000);
	}
	
	public boolean isPaused()
	{
		return this.paused;
	}//end of isPaused
	
	public void pause()
	{
		this.listPlayer.controls().pause();
		this.paused = true;
	}
	
	public void play()
	{
		this.listPlayer.controls().play();
		this.paused = false;
	}
	
	public void cycleAudio()
	{
		System.out.println("Track Count = " + this.componentPlayer.mediaPlayer().audio().trackCount());
		List<TrackDescription> audioDescriptions = this.componentPlayer.mediaPlayer().audio().trackDescriptions();
		for(TrackDescription temp : audioDescriptions)
		{
			System.out.println(temp.description() + " ID: " + temp.id());
		}
		
		audioTrack++;
		if(audioTrack >= this.componentPlayer.mediaPlayer().audio().trackCount())
			audioTrack = 0;
		long currentTime = getCurrentTime();
		this.componentPlayer.mediaPlayer().audio().setTrack(audioDescriptions.get(audioTrack).id());
		
		//this.mediaPlayer.mute();
		//this.mediaPlayer.mute(false);
		System.out.println("Audio index = " + audioTrack);
		System.out.println("Audio Description: " + audioDescriptions.get(audioTrack).description());
		System.out.println("Audio ID: " + audioDescriptions.get(audioTrack).id());//*/
		this.componentPlayer.mediaPlayer().controls().setTime(currentTime);
	}//end of cycleAudio
	
	public boolean isPlaying()
	{
		return this.listPlayer.status().isPlaying();
	}//end of is playing
	
	public void stream()
	{
		//EmbeddedMediaPlayer mediaPlayer = prepareVideo(this.player.getMediaPlayer());	
		this.listPlayer.controls().play();
		/*this.mediaPlayer = this.listPlayer.mediaPlayer().mediaPlayer();
		this.mediaPlayer.media().play(this.video.videoPath, this.options);
		this.audioTrack = this.mediaPlayer.audio().track();
		if(this.video.subtitlePath != null)
			mediaPlayer.subpictures().setSubTitleFile(this.video.subtitlePath);*/
		
	}//end of stream
	
	public void processCommand(String cmd)
	{
			
		switch(cmd.toUpperCase())
		{
		case "PAUSE":
			pause();
			this.paused = true;
			break;
		case "PLAY":
			play();
			this.paused = false;
			break;
		case "CYCLEAUDIO":
			cycleAudio();
			break;
		case "SYNCTRACKFORWARD":
			//this.audioDelay += 50;
			//this.componentPlayer.mediaPlayer().audio().setDelay(audioDelay);
			//System.out.println("Audio Delay = " + this.audioDelay);
			break;
		case "SYNCTRACKBACKWARD":
			//this.audioDelay -= 50;
			//this.componentPlayer.mediaPlayer().audio().setDelay(audioDelay);
			//System.out.println("Audio Delay = " + this.audioDelay);
			break;
		case "SKIPCHAPTER":
			this.componentPlayer.mediaPlayer().chapters().next();
			break;
		case "PREVIOUSCHAPTER":
			this.componentPlayer.mediaPlayer().chapters().previous();
			break;
		case "SKIP":
			this.listPlayer.controls().playNext();
			break;
		case "PREVIOUS":
			this.listPlayer.controls().playPrevious();
			break;
		case "SKIPFORWARD":
			this.componentPlayer.mediaPlayer().controls().setTime(getCurrentTime() + (30 *1000));
			break;
		case "TITLE":
			if(this.componentPlayer.mediaPlayer().status().isPlaying())
			{
				int titleIndex = this.componentPlayer.mediaPlayer().titles().title();
				System.out.println("titleIndex: " + titleIndex);				
				try {
					if(titleIndex < this.videos.size())
					{
						String path = this.videos.get(titleIndex).videoPath.substring(titleIndex);
						String delemit = "\\";
						if(!path.contains(delemit))
							delemit = "/";
						String currentTitle = path.substring(path.lastIndexOf(delemit));
						System.out.println("Title: " + currentTitle);
						this.out.append(currentTitle + "\n");
						this.out.flush();
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}//end of catch
				
			}//if it is played
		}//end of switch
		System.out.println(cmd);
	}//end of process controll
	
	
	@Override
	public void run()
	{
		this.listPlayer.controls().play();
		String cmd  = "";
		try {
			while((cmd = this.in.readLine()) != null)
			{
				processCommand(cmd);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}//End of StreamVideo

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
import uk.co.caprica.vlcj.media.Media;
import uk.co.caprica.vlcj.media.MediaEventListener;
import uk.co.caprica.vlcj.media.MediaParsedStatus;
import uk.co.caprica.vlcj.media.MediaRef;
import uk.co.caprica.vlcj.media.Meta;
import uk.co.caprica.vlcj.media.Picture;
import uk.co.caprica.vlcj.media.TrackType;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.base.MediaPlayerEventListener;
import uk.co.caprica.vlcj.player.base.TrackDescription;
import uk.co.caprica.vlcj.player.component.EmbeddedMediaListPlayerComponent;
import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.player.list.MediaListPlayer;
import uk.co.caprica.vlcj.player.list.MediaListPlayerEventListener;

public class StreamServer extends Thread
{
	String transcodeForSub = "transcode{vcodec=h264,scale=Auto,acodec=mpga,ab=128,channels=2,samplerate=44100,soverlay}:";
	String transcodeForNoSub = "transcode{vcodec=h264,vb=800,acodec=mpga,ab=128,channels=2,samplerate=44100,scodec=none}:";
	//String transcodeForNoSub = "transcode{vcodec=hevc,acodec=mpga,ab=128,channels=2,samplerate=44100,scodec=none}:";
	//String transcodeForSub = "transcode{vcodec=hevc,acodec=mpga,ab=128,channels=2,samplerate=44100,soverlay}:";
	//String transcode = "vcodec=hevc,acodec=mpga,ab=128,channels=2,samplerate=44100,scodec=none";
	String target = "";
	public EmbeddedMediaListPlayerComponent componentPlayer;
	private MediaPlayer mediaPlayer;
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
			this.componentPlayer.mediaPlayer().events().addMediaEventListener(new MediaEventListener() {

				@Override
				public void mediaDurationChanged(Media arg0, long arg1) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void mediaFreed(Media arg0, MediaRef arg1) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void mediaMetaChanged(Media arg0, Meta arg1) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void mediaParsedChanged(Media arg0, MediaParsedStatus arg1) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void mediaStateChanged(Media arg0, uk.co.caprica.vlcj.player.base.State arg1) {
					// TODO Auto-generated method stub
					switch(arg1)
					{
						case BUFFERING:
								System.out.println("Buffering");
							break;
						case ENDED:
								System.out.println("Ended");
							break;
						case ERROR:
								System.out.println("Error");
							break;
						case NOTHING_SPECIAL:
								System.out.println("Nothing_Special");
							break;
						case OPENING:
								System.out.println("Opening");
							break;
						case PAUSED:
								System.out.println("Paused");								
							break;
						case PLAYING:
								System.out.println("Palying");
							break;
						case STOPPED:
								System.out.println("Stopped");
							break;
						default:
							break;						
					}///end of switch					
				}//end of mediaStatChanged

				@Override
				public void mediaSubItemAdded(Media arg0, MediaRef arg1) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void mediaSubItemTreeAdded(Media arg0, MediaRef arg1) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void mediaThumbnailGenerated(Media arg0, Picture arg1) {
					// TODO Auto-generated method stub
					
				}
				
			});
			this.mediaPlayer = this.componentPlayer.mediaPlayer();
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
			String options = data.getOptions(target);
			this.mediaPlayer.events().addMediaPlayerEventListener(new MediaPlayerEventListener() {

				@Override
				public void audioDeviceChanged(MediaPlayer arg0, String arg1) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void backward(MediaPlayer arg0) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void buffering(MediaPlayer arg0, float arg1) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void chapterChanged(MediaPlayer arg0, int arg1) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void corked(MediaPlayer arg0, boolean arg1) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void elementaryStreamAdded(MediaPlayer arg0, TrackType arg1, int arg2) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void elementaryStreamDeleted(MediaPlayer arg0, TrackType arg1, int arg2) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void elementaryStreamSelected(MediaPlayer arg0, TrackType arg1, int arg2) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void error(MediaPlayer arg0) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void finished(MediaPlayer arg0) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void forward(MediaPlayer arg0) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void lengthChanged(MediaPlayer arg0, long arg1) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void mediaChanged(MediaPlayer arg0, MediaRef arg1) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void mediaPlayerReady(MediaPlayer arg0) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void muted(MediaPlayer arg0, boolean arg1) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void opening(MediaPlayer arg0) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void pausableChanged(MediaPlayer arg0, int arg1) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void paused(MediaPlayer arg0) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void playing(MediaPlayer arg0) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void positionChanged(MediaPlayer arg0, float arg1) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void scrambledChanged(MediaPlayer arg0, int arg1) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void seekableChanged(MediaPlayer arg0, int arg1) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void snapshotTaken(MediaPlayer arg0, String arg1) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void stopped(MediaPlayer arg0) {
					// TODO Auto-generated method stu
					
				}

				@Override
				public void timeChanged(MediaPlayer arg0, long arg1) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void titleChanged(MediaPlayer arg0, int arg1) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void videoOutput(MediaPlayer arg0, int arg1) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void volumeChanged(MediaPlayer arg0, float arg1) {
					// TODO Auto-generated method stub
					
				}
				
			});
			this.mediaPlayer.events().addMediaEventListener(new MediaEventListener() {

				@Override
				public void mediaDurationChanged(Media arg0, long arg1) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void mediaFreed(Media arg0, MediaRef arg1) {
					// TODO Auto-generated method stub
					
					
				}

				@Override
				public void mediaMetaChanged(Media arg0, Meta arg1) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void mediaParsedChanged(Media arg0, MediaParsedStatus arg1) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void mediaStateChanged(Media arg0, uk.co.caprica.vlcj.player.base.State arg1) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void mediaSubItemAdded(Media arg0, MediaRef arg1) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void mediaSubItemTreeAdded(Media arg0, MediaRef arg1) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void mediaThumbnailGenerated(Media arg0, Picture arg1) {
					// TODO Auto-generated method stub
					
				}
				
			});
			//this.mediaPlayer.media().play(null, null);
			this.listPlayer.list().media().add(data.videoPath, options);			
		}//end of for loop 
	}//end of addVideos
	
	
	
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
	
	public String getTitle()
	{
		if(this.componentPlayer.mediaPlayer().status().isPlaying())
		{
			int titleIndex = this.componentPlayer.mediaPlayer().titles().title();
			//System.out.println("titleIndex: " + titleIndex);				
			if(titleIndex < this.videos.size())
			{
				String path = this.videos.get(titleIndex).videoPath.substring(titleIndex);
				String delemit = "\\";
				if(!path.contains(delemit))
					delemit = "/";
				String currentTitle = path.substring(path.lastIndexOf(delemit));
				return currentTitle;
			}//end to make sure it wont throw array out of bounds
		}//end of if 
		return "";
	}//end of get Title
	
	
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

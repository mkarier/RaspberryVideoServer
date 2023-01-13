package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Iterator;
import java.util.List;

import javax.swing.JFrame;

import shared_class.SharedData;
import shared_class.VideoData;
import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.media.Media;
import uk.co.caprica.vlcj.media.MediaEventListener;
import uk.co.caprica.vlcj.media.MediaParsedStatus;
import uk.co.caprica.vlcj.media.MediaRef;
import uk.co.caprica.vlcj.media.Meta;
import uk.co.caprica.vlcj.media.Picture;
import uk.co.caprica.vlcj.media.TrackType;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.base.MediaPlayerEventListener;
import uk.co.caprica.vlcj.player.base.State;
import uk.co.caprica.vlcj.player.base.TitleDescription;
import uk.co.caprica.vlcj.player.component.EmbeddedMediaListPlayerComponent;
import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.list.MediaListPlayer;

public class RemoteMediaPlayer
{
	
	String target;
	JFrame box = new JFrame("Server");
	List<VideoData> videIterator;
	private MediaPlayer mediaPlayer;
	private int cursor = 0;
	private int currentChapter = 0;
	private MediaPlayerEventListener listener;
	private MediaPlayerFactory factory = new MediaPlayerFactory();
	private BufferedWriter out;
	private boolean skippingTime = false;
	
	public RemoteMediaPlayer(List<VideoData> videoList, BufferedWriter out, InetAddress clientIP)
	{
		
		this.out = out;
		this.videIterator = videoList;
		this.target = clientIP.toString().substring(1);
		System.out.println(clientIP.getHostName());
		System.out.println(target);
		this.mediaPlayer = factory.mediaPlayers().newMediaPlayer();
		setUpMediaPlayer();
		//setUpWindow();
	}//end of constructor
	
	
	private void setUpMediaPlayer()
	{		
		this.listener = new MediaPlayerEventListener() {
			@Override
			public void timeChanged(MediaPlayer player, long time) {
				// TODO Auto-generated method stub
				//TODO: For testing
				/*if(time > (20 * 1000))
				{
					//player.controls().stop();
					System.out.println("Debug going to next");
					playNext();
				}//*/
				long remainingTime = player.status().length() - SharedData.endTime;
				if(time < SharedData.startTime && !skippingTime)
				{
					player.submit(new Runnable() {
						@Override
						public void run() {
							player.controls().setTime(SharedData.startTime);
						}
					});
					skippingTime = true;
				}//end of skipping to startTime*/
				else if(time > SharedData.startTime && skippingTime)
					skippingTime = false;
				else if(time >= remainingTime)
				{
					player.submit(new Runnable() {
						@Override
						public void run() {
							player.controls().stop();
						}
					});
				}//end if time is greater then the remaining time.
				
				//System.out.println("Remaining time " + remainingTime);
				
			}
			
			@Override
			public void chapterChanged(MediaPlayer player, int chapter)
			{
				currentChapter = chapter;
				System.out.println("Current Chapter: " + currentChapter);				
				if(SharedData.endChapter != 0)
				{
					if(chapter >= SharedData.endChapter)
					{
						playNext();
					}//end if current chapter is greater then or equal to the end chatper
				}//end of else SharedData
			}//end of chapterChanged() Event

			@Override
			public void audioDeviceChanged(MediaPlayer arg0, String arg1) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void backward(MediaPlayer arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void buffering(MediaPlayer player, float arg1) {
				//System.out.println("Bufferring: " + arg1);
				
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
				System.out.println("There was an error");
				
			}

			@Override
			public void finished(MediaPlayer player) {
				// TODO Auto-generated method stub
				System.out.println("Finished Playing media");
				player.submit(new Runnable() {
					@Override
					public void run() {
						player.controls().stop();
					}
				});//end of sumbit				
			}//end of finished

			@Override
			public void forward(MediaPlayer arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void lengthChanged(MediaPlayer arg0, long arg1) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mediaChanged(MediaPlayer player, MediaRef arg1) {
				// TODO Auto-generated method stub
				System.out.println("Media Changed");
				if(SharedData.startChapter != 0)
				{
					player.submit(new Runnable() {
						public void run() {
							player.chapters().setChapter(SharedData.startChapter);
						}
					});
				}
			}//end of media changed

			@Override
			public void mediaPlayerReady(MediaPlayer arg0) {
				// TODO Auto-generated method stub
				//System.out.println("Media Player is ready");
			}

			@Override
			public void muted(MediaPlayer arg0, boolean arg1) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void opening(MediaPlayer arg0) {
				// TODO Auto-generated method stub
				//System.out.println("Opening event");
				
			}

			@Override
			public void pausableChanged(MediaPlayer arg0, int arg1) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void paused(MediaPlayer arg0) {
				// TODO Auto-generated method stub
				//System.out.println("Paused");
				
			}

			@Override
			public void playing(MediaPlayer arg0) {
				// TODO Auto-generated method stub
				//System.out.println("Playing");
				
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
				System.out.println("Media Player Stopped");
				playNext();
				
			}


			@Override
			public void titleChanged(MediaPlayer player, int arg1) {
				// TODO Auto-generated method stub
				for(TitleDescription des: player.titles().titleDescriptions())
				{
					System.out.println(des.name());					
				}//end of for loop
				
			}//end of title changed

			@Override
			public void videoOutput(MediaPlayer arg0, int arg1) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void volumeChanged(MediaPlayer arg0, float arg1) {
				// TODO Auto-generated method stub
				
			}
			
		};
		this.mediaPlayer.events().addMediaPlayerEventListener(listener);
	}//end of setUpMediaPlayer
	
	public void playNext()
	{
		if(this.cursor < this.videIterator.size())
		{
			this.mediaPlayer.submit(new Runnable(){
				@Override
				public void run() {
					mediaPlayer.media().parsing().parse();
				}
			});
			this.mediaPlayer.submit(new Runnable() {
				@Override
				public void run() {
					System.out.println("grabbing next Video....");
					VideoData videoData = videIterator.get(cursor++);
					String options = videoData.getOptions(target);
					System.out.println("Setup: " + videoData.videoPath);
					boolean videoPlayed = mediaPlayer.media().play(videoData.videoPath, options, ":no-sout-all", ":netsync-master", ":network-synchronisation");
					skippingTime = false;
					//System.out.println("Media was prepared");
					//this.mediaPlayer.controls().play();			
					if(!videoPlayed)
					{
						System.out.println("Couldn't play " + videoData.videoPath);
						playNext();
					}//end of if
					else				
					{
						//setUpMediaPlayer();
						System.out.println("playing: " + videoData.title);
						try {
							out.write(videoData.title + "\n");
							out.flush();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}//end of catch
						
					}//end of else
				}//end of run
			});//end of sumbit
		}//end of if there is a next video
		else
		{
			try {
				this.out.write("quit\n");
				this.out.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			System.out.println("There is no next video");
		}//end of else
	}//end of playNexit
	
	
	
	public void processUserCommand(String input)
	{
		System.out.println("User input: " + input);
		switch(input.toUpperCase())
		{
		case "PAUSE":
			this.mediaPlayer.submit(new Runnable() {
				@Override
				public void run()
				{
					mediaPlayer.controls().pause();
				}//end of run function
			});//end of submit
			break;
		case "PLAY":
			this.mediaPlayer.submit(new Runnable() {
				@Override
				public void run()
				{
					mediaPlayer.controls().play();
				}//end of run function
			});//end of submit
			break;
		case "SKIPCHAPTER":
			this.mediaPlayer.submit(new Runnable() {
				@Override
				public void run()
				{
					mediaPlayer.chapters().next();
				}//end of run function
			});//end of submit
			break;
		case "PREVIOUSCHAPTER":
			this.mediaPlayer.submit(new Runnable() {
				@Override
				public void run()
				{
					mediaPlayer.chapters().previous();
				}//end of run function
			});//end of submit
			break;
		case "SKIP":
			playNext();
			break;
		case "PREVIOUS":
			playPrevious();
			break;
		case "SKIPFORWARD":
			this.mediaPlayer.submit(new Runnable() {
				@Override
				public void run()
				{
					mediaPlayer.controls().skipTime(30l * 1000l);
				}//end of run function
			});//end of submit
			break;
		}//end of switch
	}//end of processUserCommand


	private void playPrevious() {
		// TODO Auto-generated method stub
		
	}
	
}//end of RemoteMediaPlayer

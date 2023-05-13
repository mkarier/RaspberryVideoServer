package server;

import shared_class.SharedData;
import uk.co.caprica.vlcj.media.MediaEventListener;
import uk.co.caprica.vlcj.media.MediaRef;
import uk.co.caprica.vlcj.media.TrackType;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.base.MediaPlayerEventListener;
import uk.co.caprica.vlcj.player.base.TitleDescription;

public class StreamServerHelper {

	public static MediaPlayerEventListener LISTENER = new MediaPlayerEventListener() {
			@Override
			public void timeChanged(MediaPlayer player, long time) {
				long remainingTime = player.status().length() - SharedData.endTime;
				if(time < SharedData.startTime && !SharedData.skippingTime)
				{
					player.submit(new Runnable() {
						@Override
						public void run() {
							player.controls().setTime(SharedData.startTime);
						}
					});
					SharedData.skippingTime = true;
				}//end of skipping to startTime*/
				else if(time > SharedData.startTime && SharedData.skippingTime)
					SharedData.skippingTime = false;
				else if(time >= remainingTime)
				{
					player.submit(new Runnable() {
						@Override
						public void run() {
							player.controls().stop();
						}
					});
				}//end if time is greater then the remaining time
				//System.out.println("Remaining time " + remainingTime);
			}//end of timeChanged
			
			@Override
			public void chapterChanged(MediaPlayer player, int chapter)
			{
				//urrentChapter = chapter;
				//System.out.println("Current Chapter: " + currentChapter);				
				if(SharedData.endChapter != 0)
				{
					if(chapter >= SharedData.endChapter)
					{
						player.submit(()->{
							player.controls().stop();
						});
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
				System.out.println("Bufferring: " + arg1);
				
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
				System.out.println("Opening event");
				
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
				System.out.println("Playing");
				
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
				//playNext();
				
			}


			@Override
			public void titleChanged(MediaPlayer player, int arg1) {
				// TODO Auto-generated method stub
				for(TitleDescription des: player.titles().titleDescriptions())
				{
					System.out.println("TitleChanged:TitleDescription = " + des.name());					
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
}//end of StreamserverHelper

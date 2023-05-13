package server;

import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.factory.discovery.NativeDiscovery;
import uk.co.caprica.vlcj.media.Media;
import uk.co.caprica.vlcj.media.callback.seekable.RandomAccessFileMedia;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

public class TranscodeStreamExample {

    public static void main(String[] args) throws InterruptedException {
    	//if(System.getProperty("os.name").toLowerCase().contains("linux"))
			//System.setProperty("jna.library.path", "/snap/vlc/current/usr/lib/");
    	new NativeDiscovery().discover();
        String inputFile = "/media/movies/Dune.mp4";
        System.out.println(inputFile);
        String outputHost = "192.168.50.62";
        int outputPort = 9998;
       // String outputFormat = "mp4v";
        String outputOptions = "#transcode{vcodec=mp2v,acodec=mp4a,ab=128,channels=2,samplerate=44100,scodec=none}:rtp{dst=" + outputHost + ",port=" + outputPort + ",mux=ts}";
        System.out.println("options = " + outputOptions);
        //System.setProperty("vlcj.runtime.x86only", "false");
        // Create a media player factory
        MediaPlayerFactory factory = new MediaPlayerFactory();
        
        // Create a random access file media object for the input file
        //var inputMedia = new RandomAccessFileMedia(factory, inputFile);
        
        // Create media options for transcoding
       // MediaOptions transcodingOptions = new MediaOptions(":sout=" + outputOptions);
        
        // Set the media options on the input media
        //inputMedia.setOptions(transcodingOptions);
        
        // Create an embedded media player
        var player = factory.mediaPlayers().newMediaPlayer();

        // Set the media to play on the player
        player.submit(()->{
        	player.media().play(inputFile, ":sout="+outputOptions, "--no-xlib");
        });
        
        
        // Start playing the media
        //player.controls().play();
        while(true) {
        	System.out.println(player.status().state());
        	switch(player.status().state())
        	{
			case BUFFERING:
				break;
			case ENDED:
				break;
			case ERROR:
				break;
			case NOTHING_SPECIAL:
				break;
			case OPENING:
				break;
			case PAUSED:
				break;
			case PLAYING:
				System.out.println("Time: " + player.status().time());
				break;
			case STOPPED:
				break;
			default:
				break;
        	
        	}//end of switch
        	Thread.sleep(1000 * 5);
        }//end of while
    }
}

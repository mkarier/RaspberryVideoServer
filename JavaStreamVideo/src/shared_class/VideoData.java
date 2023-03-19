package shared_class;

import uk.co.caprica.vlcj.media.callback.CallbackMedia;
import uk.co.caprica.vlcj.media.callback.seekable.RandomAccessFileMedia;

public class VideoData 
{
	public boolean hasSubtitles = false;
	public String subtitlePath;
	public String videoPath; 
	public String title;
	
	
	String transcodeForSub = "transcode{vcodec=mp4v,scale=Auto,acodec=mp4a,ab=128,channels=2,samplerate=44100,soverlay}:";
	///String transcodeForSub = "transcode{vcodec=hevc,acodec=mpga,ab=128,channels=2,samplerate=44100,soverlay}:";
	
	//String transcodeForNoSub = "transcode{vcodec=h264,vb=600,acodec=mp3,ab=128,channels=2,samplerate=44100,scodec=none}:";
	//String transcodeForNoSub = "transcode{vcodec=hvec,acodec=mpga,ab=128,channels=2,samplerate=44100,scodec=none}:";
	//String transcodeForNoSub = "";
	//String transcodeForNoSub = "transcode{vcodec=h264,acodec=mpga,ab=128,channels=2,samplerate=44100,scodec=none}:";
	//String transcodeForNoSub = "transcode{vcodec=h264,vb=300,acodec=mpga,ab=128,channels=2,samplerate=44100,scodec=none}:";
	//String transcodeForNoSub = "transcode{vcodec=mp2v,acodec=mp4a,ab=128,channels=2,samplerate=44100,scodec=none}:";
	String transcodeForNoSub = "transcode{vcodec=,acodec=mp4a,ab=128,channels=2,samplerate=44100,scodec=none}:";
	//VHS FIlter
	//String transcodeForNoSub = "transcode{vcodec=h264,vb=256,vfilter=vhs,acodec=mpga,ab=128,channels=2,samplerate=44100,scodec=none}:";
	//String transcodeForNoSub = "transcode{vcodec=h264,scale=Auto,acodec=mpga,ab=128,channels=2,samplerate=44100,scodec=none}:";
	//Original Transcode
	//String transcodeForNoSub = "transcode{vcodec=h264,vb=300,acodec=mpga,ab=128,channels=2,samplerate=44100,scodec=none}:";
	
	public VideoData()
	{
		
	}//end of default constructor
	public VideoData(String videoPath)
	{
		this.videoPath = videoPath;
		if(videoPath.contains("\\"))
			this.title = videoPath.substring(videoPath.lastIndexOf('\\')+1);
		else
			this.title = videoPath.substring(videoPath.lastIndexOf('/')+1);
	}//end of VideoData constructor
	
	public CallbackMedia getAsMedia() {
		return new RandomAccessFileMedia(new java.io.File(this.videoPath));
	}//end of getCallbackMedia
	public String getOptions(String target)
	{
		String options = "";
		String start = "sout=#";
		String standard = String.format("%s{mux=ts,dst=%s,port=%d}", SharedData.access, target, SharedData.videoPort);
		if(this.hasSubtitles)
			options = start + transcodeForSub + standard;
		else
			options = start + transcodeForNoSub + standard;
		System.out.println(options);
		return options;
	}//end of get Options
}//end of class VideoData

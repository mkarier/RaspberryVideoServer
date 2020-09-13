package shared_class;

public class VideoData 
{
	public boolean hasSubtitles = false;
	public String subtitlePath;
	public String videoPath; 
	
	public VideoData()
	{
		
	}//end of default constructor
	public VideoData(String videoPath)
	{
		this.videoPath = videoPath;
	}//end of VideoData constructor
}//end of class VideoData

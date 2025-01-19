using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace CSharpServer
{
    internal class VideoData : IComparable<VideoData>
    {
        public bool hasSubtitles = false;
        public string subtitlePath = "";
        public string videoPath = "";
        public string title = "";

        public VideoData()
        {
        }

        public VideoData(string videoPath)
        {
            this.videoPath = videoPath;
            if (videoPath.Contains("\\"))                
                this.title = videoPath.Substring(videoPath.LastIndexOf('\\') + 1);
            else
                this.title = videoPath.Substring(videoPath.LastIndexOf('/') + 1);
        }

        public int CompareTo(VideoData? other)
        {
            return this.title.ToLower().CompareTo(other.title.ToLower());
        }
    }//end of internal class
}//end of name space

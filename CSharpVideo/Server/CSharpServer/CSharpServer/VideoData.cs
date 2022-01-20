using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace CSharpServer
{
    internal class VideoData
    {
        public bool hasSubtitles = false;
        public string subtitlePath = "";
        public string videoPath = "";

        public VideoData()
        {
        }

        public VideoData(string videoPath)
        {
            this.videoPath = videoPath;           
        }
    }//end of internal class
}//end of name space

using LibVLCSharp.Shared;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace CSharpServer
{
    internal class RemoteMediaPlayer
    {
        public LibVLC _libVLC;
        public MediaPlayer mp;       
        private List<VideoData> mediaList;
        private Media media;
        private string clientIP;
        private StreamReader reader;
        private StreamWriter writer;
        private int cursor = ServerDriver.start;
        private long duration = 0;

        string transcodeForSub = "transcode{vcodec=h264,scale=Auto,acodec=mpga,ab=128,channels=2,samplerate=44100,soverlay}:";
        string transcodeForNoSub = "transcode{vcodec=h264,vb=800,acodec=mpga,ab=128,channels=2,samplerate=44100,scodec=none}:";

        public RemoteMediaPlayer(List<VideoData> videoList, StreamReader reader, StreamWriter writer, string clientIP)
        {
            this.reader = reader;
            this.writer = writer;
            this.clientIP = clientIP;
            this.mediaList = videoList;
            Core.Initialize();
            this._libVLC = new LibVLC();
            this.mp = new MediaPlayer(this._libVLC);            
            this.cursor = ServerDriver.start;
            this.media = getMedia(this.mediaList[cursor]);
            this.mp.Media = media;
            this.duration = this.media.Duration;
            this.mp.Time = ServerDriver.startTime;
            this.mp.LengthChanged += (sender, args) => ThreadPool.QueueUserWorkItem(_ => this.duration = args.Length);
            this.mp.TimeChanged += (sender, args) => ThreadPool.QueueUserWorkItem(_=> calculateTime(args.Time));
            this.mp.EndReached += (sender, args) => ThreadPool.QueueUserWorkItem(_ => nextMedia());
        }

        public void sendTitle()
        {
            if (this.mp.Media != null)
            {
                this.writer.WriteLine(this.mp.Media.Mrl);
                this.writer.Flush();
            }
        }

        public void calculateTime(long timeArg)
        {
            if (ServerDriver.startTime <= 0) 
                return;
            long endTime = this.duration - ServerDriver.endBeforeTime;            
            //Console.WriteLine($"{timeArg} out of {endTime}");
            if(timeArg >= endTime)
            {
                nextMedia();
            }//end of if*/
        }//end of calculateTime

        public bool isPlaying()
        {
            return this.mp.IsPlaying;
        }//end

        public bool play() {
            Console.WriteLine("Starting to play");
            bool startedPlaying = this.mp.Play();
            //this.duration = this.media.Duration;
            this.mp.Time = ServerDriver.startTime;
            return startedPlaying;
        }

        public void nextMedia()
        {
            this.media = getMedia(this.mediaList[increaseCursor()]);
            this.mp.Media = this.media;
            play();
        }


        public int increaseCursor()
        {
            this.cursor++;
            if (cursor > this.mediaList.Count)
                return -1;
            else return cursor;
        }

        public long getDuration() { return this.duration; }
        public long getCurrentTime() { return this.mp.Time; }

        public Media getMedia(VideoData video)
        {
            string options = "";
            string start = "sout=#";
            string standard = $"{ServerDriver.access}{{mux=ts,dst={this.clientIP},port={ServerDriver.videoPort}}}";
            if (video.hasSubtitles)
                options = start + transcodeForSub + standard;
            else
                options = start + transcodeForNoSub + standard;
            //Console.WriteLine(options);
            return new Media(this._libVLC, video.videoPath, FromType.FromPath, options);
        }//end of getMedia

    }
}

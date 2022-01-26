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

        string transcodeForSub = "transcode{vcodec=h264,scale=Auto,acodec=mp3,ab=128,channels=2,samplerate=44100,soverlay}:";
        //string transcodeForNoSub = "transcode{vcodec=h264,vb=3500,acodec=mp3,ab=192,channels=2,samplerate=44100,scodec=none}:";
        //string transcodeForNoSub = "transcode{vcodec=h264,vb=3500,width=800,height=400,acodec=mp3,ab=192,channels=2,samplerate=44100,scodec=none}:";
        string transcodeForNoSub = "";
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
            ThreadPool.QueueUserWorkItem(_ => listenToClient());
        }

        public void sendTitle()
        {
            if (cursor >= 0 && cursor < this.mediaList.Count)
            {
                int lastIndex = this.mediaList[cursor].videoPath.LastIndexOf("/");
                if(lastIndex == -1)
                    lastIndex = this.mediaList[cursor].videoPath.LastIndexOf("\\");
                this.writer.WriteLine(this.mediaList[cursor].videoPath.Substring(lastIndex));
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
            sendTitle();
            this.mp.Time = ServerDriver.startTime;
            return startedPlaying;
        }

        public void nextMedia()
        {
            this.media = getMedia(this.mediaList[increaseCursor()]);
            this.mp.Media = this.media;
            play();
        }//end of nextMedia

        private void previousMedia()
        {
            this.media = getMedia(this.mediaList[decrementCursor()]);
            this.mp.Media = this.media;
            play();
        }//end of previousMedia


        public int increaseCursor()
        {
            this.cursor++;
            if (cursor > this.mediaList.Count)
                return -1;
            else return cursor;
        }

        public int decrementCursor()
        {
            this.cursor--;
            if (cursor < 0)
                return 0;
            else return cursor;
        }

        public long getDuration() { return this.duration; }
        public long getCurrentTime() { return this.mp.Time; }

        public Media getMedia(VideoData video)
        {
            string options = "";
            string start = ":sout=#";
            string standard = $"{ServerDriver.access}{{mux=ts,dst={this.clientIP},port={ServerDriver.videoPort}}}";
            if (video.hasSubtitles)
                options = start + transcodeForSub + standard;
            else
                options = start + transcodeForNoSub + standard;
            Console.WriteLine(options);
            return new Media(this._libVLC, video.videoPath, FromType.FromPath, options);
        }//end of getMedia


        private void cycleAudio()
        {
            //TODO: cycle audio
        }

       

        public void listenToClient()
        {        
            try
            {
                string? input;
                while ((input = this.reader.ReadLine()) != null)
                {
                    switch(input.ToUpper())
                    {
                        case "PAUSE":
                            this.mp.Pause();                            
                            break;
                        case "PLAY":
                            this.mp.Play();                          
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
                            this.mp.NextChapter();
                            break;
                        case "PREVIOUSCHAPTER":
                            this.mp.PreviousChapter();
                            break;
                        case "SKIP":
                            nextMedia();
                            break;
                        case "PREVIOUS":
                            previousMedia();
                            break;
                        case "SKIPFORWARD":
                            this.mp.Pause();
                            this.mp.Time = (this.mp.Time + (30 * 1000));
                            this.mp.Play();
                            break;
                    }//end of switch
                    Console.WriteLine(input);
                }//end of while loop
            }//end of try
            catch (IOException e) { Console.Error.WriteLine(e.StackTrace); }
        }//end of listenToClient

    }
}

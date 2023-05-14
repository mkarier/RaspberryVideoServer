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
        private int currentChapter = 0;

        string transcodeForSub = "transcode{vcodec=mp2v,acodec=mp4a,ab=128,channels=2,samplerate=44100,soverlay}:";
        //string transcodeForNoSub = "transcode{vcodec=h264,vb=3500,acodec=mp3,ab=192,channels=2,samplerate=44100,scodec=none}:";
        //string transcodeForNoSub = "transcode{vcodec=h264,acodec=mpga,ab=128,channels=2,samplerate=44100,scodec=none}:";
        //string transcodeForNoSub = "transcode{vcodec=h264,vb=3500,width=800,height=400,acodec=mp3,ab=192,channels=2,samplerate=44100,scodec=none}:";
        string transcodeForNoSub = "transcode{vcodec=mp2v,acodec=mp4a,ab=128,channels=2,samplerate=44100,scodec=nones}:";
        //string transcodeForNoSub = "transcode{vcodec=h264,vb=256,vfilter=vhs,acodec=mpga,ab=128,channels=2,samplerate=44100,scodec=none}:";
        //string transcodeForNoSub = "";
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
            this.mp.ChapterChanged += (sender, args) => ThreadPool.QueueUserWorkItem(_ => endingChapter(args.Chapter));
            ThreadPool.QueueUserWorkItem(_ => listenToClient());
        }//end of constructor

        public void sendTitle()
        {
            if (cursor >= 0 && cursor < this.mediaList.Count)
            {
                int lastIndex = this.mediaList[cursor].videoPath.LastIndexOf("/");
                if(lastIndex == -1)
                    lastIndex = this.mediaList[cursor].videoPath.LastIndexOf("\\");
                string title = this.mediaList[cursor].videoPath.Substring(lastIndex) + "\n";
                this.writer.WriteLine(title);
                this.writer.FlushAsync();               
                Console.WriteLine(title);
            }//end of if coursor is greater then zero and less then the mediaList.count
        }//end of sendTitle

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
        }//end of play

        public void nextMedia()
        {
            try
            {
                this.media = getMedia(this.mediaList[increaseCursor()]);
                Media? current = this.mp.Media;
                this.mp.Media = this.media;                
                if (current != null)
                    current.Dispose();
                play();
                writer.WriteLine(this.media.Mrl);
            }catch(IndexOutOfRangeException e)
            {
                quit();
            }
        }//end of nextMedia

        private void quit()
        {
            this.writer.WriteLine("quit");
            this.mp.Dispose();
        }

        private void endingChapter(int chapter)
        {
            if (currentChapter == chapter)
                return;
            else
                currentChapter = chapter;
            Console.WriteLine("Chapter " + chapter);
            if (ServerDriver.startChapter > chapter)
            {
                this.mp.NextChapter();
            }//end of chapter is less then available chapter
            if (ServerDriver.stopChapter > 0)
            {                               
                if (chapter >= ServerDriver.stopChapter)
                {
                    nextMedia();
                }//end of if you reach the chapter the episode shouldEnd;
            }//end of ending chapter
        }//end of endingChapter

        private void previousMedia()
        {
            this.media = getMedia(this.mediaList[decrementCursor()]);
            Media? current = this.mp.Media;
            this.mp.Media = this.media;
            if(current != null)
                current.Dispose();
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
            return new Media(this._libVLC, video.videoPath, FromType.FromPath, options, "--no-xlib");
        }//end of getMedia


        private void cycleAudio()
        {
            //TODO: cycle audio
            this.mp.Pause();
            int currentAudio = this.mp.AudioTrack;
            Console.WriteLine("From Audio track " + currentAudio + this.mp.AudioTrackDescription[currentAudio].Name);
            if(!this.mp.SetAudioTrack((currentAudio + 1)))
               this.mp.SetAudioTrack((currentAudio + 1) % this.mp.AudioTrack);

            this.mp.Play();
            Console.WriteLine("To Audio Track " + this.mp.AudioTrack + this.mp.AudioTrackDescription[this.mp.AudioTrack].Name);        
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

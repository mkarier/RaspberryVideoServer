// See https://aka.ms/new-console-template for more information

using CSharpServer;
using LibVLCSharp.Shared;
using System.Net;
using System.Net.Sockets;

namespace CSharpServer
{
	public class ServerDriver
	{
		internal static int comPort = 9001;
		public static int videoPort = 9998;
		internal static string access = "rtp";
		internal static int start = 0;
		internal static long startTime = 0;
		internal static long endBeforeTime = 0;
		internal static int startChapter = 0;
		internal static int stopChapter = 0;


		private static string[] TypesOfVideos = {".264", ".3g2", ".3gp", ".3gp2", ".3gpp", ".3gpp2", ".3mm", ".3p2", ".60d", ".787", ".89", ".aaf", ".aec", ".aep", ".aepx",
			".aet", ".aetx", ".ajp", ".ale", ".am", ".amc", ".amv", ".amx", ".anim", ".aqt", ".arcut", ".arf", ".asf", ".asx", ".avb",
			".avc", ".avd", ".avi", ".avp", ".avs", ".avs", ".avv", ".axm", ".bdm", ".bdmv", ".bdt2", ".bdt3", ".bik", ".bin", ".bix",
			".bmk", ".bnp", ".box", ".bs4", ".bsf", ".bvr", ".byu", ".camproj", ".camrec", ".camv", ".ced", ".cel", ".cine", ".cip",
			".clpi", ".cmmp", ".cmmtpl", ".cmproj", ".cmrec", ".cpi", ".cst", ".cvc", ".cx3", ".d2v", ".d3v", ".dat", ".dav", ".dce",
			".dck", ".dcr", ".dcr", ".ddat", ".dif", ".dir", ".divx", ".dlx", ".dmb", ".dmsd", ".dmsd3d", ".dmsm", ".dmsm3d", ".dmss",
			".dmx", ".dnc", ".dpa", ".dpg", ".dream", ".dsy", ".dv", ".dv-avi", ".dv4", ".dvdmedia", ".dvr", ".dvr-ms", ".dvx", ".dxr",
			".dzm", ".dzp", ".dzt", ".edl", ".evo", ".eye", ".ezt", ".f4p", ".f4v", ".fbr", ".fbr", ".fbz", ".fcp", ".fcproject",
			".ffd", ".flc", ".flh", ".fli", ".flv", ".flx", ".gfp", ".gl", ".gom", ".grasp", ".gts", ".gvi", ".gvp", ".h264", ".hdmov",
			".hkm", ".ifo", ".imovieproj", ".imovieproject", ".ircp", ".irf", ".ism", ".ismc", ".ismv", ".iva", ".ivf", ".ivr", ".ivs",
			".izz", ".izzy", ".jss", ".jts", ".jtv", ".k3g", ".kmv", ".ktn", ".lrec", ".lsf", ".lsx", ".m15", ".m1pg", ".m1v", ".m21",
			".m21", ".m2a", ".m2p", ".m2t", ".m2ts", ".m2v", ".m4e", ".m4u", ".m4v", ".m75", ".mani", ".meta", ".mgv", ".mj2", ".mjp",
			".mjpg", ".mk3d", ".mkv", ".mmv", ".mnv", ".mob", ".mod", ".modd", ".moff", ".moi", ".moov", ".mov", ".movie", ".mp21", "mp3",
			".mp21", ".mp2v", ".mp4", ".mp4v", ".mpe", ".mpeg", ".mpeg1", ".mpeg4", ".mpf", ".mpg", ".mpg2", ".mpgindex", ".mpl",
			".mpl", ".mpls", ".mpsub", ".mpv", ".mpv2", ".mqv", ".msdvd", ".mse", ".msh", ".mswmm", ".mts", ".mtv", ".mvb", ".mvc",
			".mvd", ".mve", ".mvex", ".mvp", ".mvp", ".mvy", ".mxf", ".mxv", ".mys", ".ncor", ".nsv", ".nut", ".nuv", ".nvc", ".ogm",
			".ogv", ".ogx", ".osp", ".otrkey", ".pac", ".par", ".pds", ".pgi", ".photoshow", ".piv", ".pjs", ".playlist", ".plproj",
			".pmf", ".pmv", ".pns", ".ppj", ".prel", ".pro", ".prproj", ".prtl", ".psb", ".psh", ".pssd", ".pva", ".pvr", ".pxv",
			".qt", ".qtch", ".qtindex", ".qtl", ".qtm", ".qtz", ".r3d", ".rcd", ".rcproject", ".rdb", ".rec", ".rm", ".rmd", ".rmd",
			".rmp", ".rms", ".rmv", ".rmvb", ".roq", ".rp", ".rsx", ".rts", ".rts", ".rum", ".rv", ".rvid", ".rvl", ".sbk", ".sbt",
			".scc", ".scm", ".scm", ".scn", ".screenflow", ".sec", ".sedprj", ".seq", ".sfd", ".sfvidcap", ".siv", ".smi", ".smi",
			".smil", ".smk", ".sml", ".smv", ".spl", ".sqz", ".srt", ".ssf", ".ssm", ".stl", ".str", ".stx", ".svi", ".swf", ".swi",
			".swt", ".tda3mt", ".tdx", ".thp", ".tivo", ".tix", ".tod", ".tp", ".tp0", ".tpd", ".tpr", ".trp", ".ts", ".tsp", ".ttxt",
			".tvs", ".usf", ".usm", ".vc1", ".vcpf", ".vcr", ".vcv", ".vdo", ".vdr", ".vdx", ".veg",".vem", ".vep", ".vf", ".vft",
			".vfw", ".vfz", ".vgz", ".vid", ".video", ".viewlet", ".viv", ".vivo", ".vlab", ".vob", ".vp3", ".vp6", ".vp7", ".vpj",
			".vro", ".vs4", ".vse", ".vsp", ".w32", ".wcp", ".webm", ".wlmp", ".wm", ".wmd", ".wmmp", ".wmv", ".wmx", ".wot", ".wp3",
			".wpl", ".wtv", ".wve", ".wvx", ".xej", ".xel", ".xesc", ".xfl", ".xlmv", ".xmv", ".xvid", ".y4m", ".yog", ".yuv", ".zeg",
			".zm1", ".zm2", ".zm3", ".zmv"};

		static void Main(string[] args)
		{
			List<VideoData> videos = processInput(args);
			foreach(VideoData input in videos)
				Console.WriteLine(input.videoPath);
			try
			{
				TcpListener server = new TcpListener(IPAddress.Any, comPort);
				server.Start();
				TcpClient? client = server.AcceptTcpClient();
				IPEndPoint? remoteEndPoint = (client.Client.RemoteEndPoint as IPEndPoint);
				if (remoteEndPoint != null)
				{
					string clientIP = remoteEndPoint.Address.ToString();
					StreamReader reader = new StreamReader(client.GetStream());
					StreamWriter writer = new StreamWriter(client.GetStream());
					Console.WriteLine(clientIP);
					writer.WriteLine(ServerDriver.videoPort);
					writer.Flush();
					playVideos(videos, reader, writer, clientIP);
				}//end if remoteEndPoint is not null
			}
			catch (VLCException ex) { Console.WriteLine(ex.StackTrace); }
		}//end of main
		
		static void playVideos(List<VideoData> videos, StreamReader reader, StreamWriter writer, string clientIp)
        {
			
			RemoteMediaPlayer rmp = new RemoteMediaPlayer(videos, reader, writer, clientIp);
			Console.WriteLine($"Strarting with\n:{videos[start].videoPath}");
			rmp.play();
			rmp.sendTitle();
			while (true) ;

        }//end of playVideos

		static List<VideoData> processInput(string[] args)
		{
			List<VideoData> list = new List<VideoData>();
			VideoData cursor = new VideoData();
			for(int i = 0; i < args.Length; i++)
			{
				string input = args[i];
				switch (input.ToLower())
				{
					case "--starttime":
						startTime = Int32.Parse(args[++i]) * 1000;
						break;
					case "--endtime":
						endBeforeTime = Int32.Parse(args[++i]) * 1000;
						break;
					case "--startchapter":
						startChapter = Int32.Parse(args[++i]);
						break;
					case "--stopchapter":
						stopChapter = Int32.Parse(args[++i]);
						break;
					case "-s":
					case "-S":
						cursor.hasSubtitles = true;
						break;
					default:
						try
						{
							ServerDriver.start = Int32.Parse(input);
						} catch (FormatException)
						{
							if (Directory.Exists(input))
                            {
								foreach (VideoData data in getVideosFromDir(input))
									list.Add(data);
                            }//end of if
							else if(checkIfVideo(input))
                            {
								cursor = new VideoData(input);
								list.Add(cursor);
                            }

						}//end of catch not a number
						break;

				}//end of switch
			}//end of foreach
			return list;
		}//end of processInput

		static List<VideoData> getVideosFromDir(string input)
        {
			List<VideoData> list = new List<VideoData>();
			foreach(string file in Directory.GetFiles(input))
            {
				if(checkIfVideo(file))
                {
					list.Add(new VideoData(file));
                }//end of if
            }//end of foreach
			return list;
        }//end of getVideoFromDir

		static bool checkIfVideo(string file)
        {
			if (file.LastIndexOf(".") == -1)
				return false;
			string extension = file.Substring(file.LastIndexOf("."));
			return TypesOfVideos.Contains(extension);
        }//end of checkIfVideo
    }//end of class ErverDirver
}//en dof namespace
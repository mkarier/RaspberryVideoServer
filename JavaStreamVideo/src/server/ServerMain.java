package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import shared_class.SharedData;
import shared_class.VideoData;
import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.factory.discovery.NativeDiscovery;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.base.State;

public class ServerMain {
	
	private static String[] TypesOfVideos = {".264", ".3g2", ".3gp", ".3gp2", ".3gpp", ".3gpp2", ".3mm", ".3p2", ".60d", ".787", ".89", ".aaf", ".aec", ".aep", ".aepx",
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
	
	//private static String[] args = null;
//	//BufferedReader in;
	//BufferedWriter out;
	//InetAddress address;
	//List<VideoData> listOfVideos;
	
	public static void main(String[] args) throws IOException {
		new NativeDiscovery().discover();
		var player = new MediaPlayerFactory().mediaPlayers().newMediaPlayer();
		player.events().addMediaPlayerEventListener(StreamServerHelper.LISTENER);
		ArrayList<String> videoTypes = new ArrayList<String>();
		videoTypes.addAll(Arrays.asList(TypesOfVideos));
		SharedData options = new SharedData();
		var listOfVideos = ServerMain.processArgs(args, videoTypes, options);
		for(VideoData video: listOfVideos)
			System.out.println(video.videoPath);
		
		
		try(var server = new ServerSocket(SharedData.comPort);
			Socket client = server.accept();)
		{
			var address = client.getInetAddress().getHostAddress();
			var in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			var out = new PrintWriter(new OutputStreamWriter(client.getOutputStream()));
			out.write(SharedData.videoPort + "\n");
			out.flush();
			Thread userInputListener = new Thread(()->{
				String command = "";
				try {
					while((command = in.readLine()) != null)
						processUserCommand(command, player);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			});//end of userInputListener
			userInputListener.start();
			for(var videoData : listOfVideos)
			{
				player.events().removeMediaPlayerEventListener(StreamServerHelper.LISTENER);
				out.println(videoData.title);
				System.out.println("Setup: " + videoData.videoPath);				
				player.submit(()->{
					player.media().play(videoData.videoPath, videoData.getOptions(address), "--no-xlib");
				});
				Thread.sleep(1000 * 5);
				player.events().addMediaPlayerEventListener(StreamServerHelper.LISTENER);
				while(player.status().state() != State.STOPPED)
				{
					System.out.println("Player State = " + player.status().state());
					Thread.sleep(1000 * 5);
				}
			}//end of for loop
			out.write("quit\n");
			out.flush();
		}catch(IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}//end of main
	
	public ServerMain() throws IOException {
		
	}//end of constructions
	
	
	private static void processUserCommand(String input, MediaPlayer mediaPlayer) {
		System.out.println("User input: " + input);
		switch(input.toUpperCase())
		{
		case "PAUSE":
			mediaPlayer.submit(new Runnable() {
				@Override
				public void run()
				{
					mediaPlayer.controls().pause();
				}//end of run function
			});//end of submit
			break;
		case "PLAY":
			mediaPlayer.submit(new Runnable() {
				@Override
				public void run()
				{
					mediaPlayer.controls().play();
				}//end of run function
			});//end of submit
			break;
		case "SKIPCHAPTER":
			mediaPlayer.submit(new Runnable() {
				@Override
				public void run()
				{
					mediaPlayer.chapters().next();
				}//end of run function
			});//end of submit
			break;
		case "PREVIOUSCHAPTER":
			mediaPlayer.submit(new Runnable() {
				@Override
				public void run()
				{
					mediaPlayer.chapters().previous();
				}//end of run function
			});//end of submit
			break;
		case "SKIP":
			mediaPlayer.submit(()->{
				mediaPlayer.controls().stop();
			});
			break;
		case "PREVIOUS":
			//playPrevious();
			break;
		case "SKIPFORWARD":
			mediaPlayer.submit(new Runnable() {
				@Override
				public void run()
				{
					mediaPlayer.controls().skipTime(30l * 1000l);
				}//end of run function
			});//end of submit
			break;
		}//end of switch
	}
	

	/*@Override
	public void start(Stage primaryStage) throws Exception {
		System.out.println("Staring Server Main");
		//RemoteMediaPlayer mediaPlayer = new RemoteMediaPlayer(listOfVideos, out, in, address);
		//TranscodeStreamExample.main(null);
		//mediaPlayer.start();
		//while(true);
		//Platform.runLater(mediaPlayer);
		

	}//end of start*/
	
	private void runServer() throws IOException {
		// TODO Auto-generated method stub
			}//end of run server

	/**
	 * This method is at the end of the program to clean up stuff. 
	 * @param out This is the writer to the client
	 * @param in this read in from the client
	 * @param server this is Socket that connects to socket
	 * @param client 
	 * @throws IOException
	 */
	private void close(BufferedWriter out, BufferedReader in, ServerSocket server, Socket client) throws IOException
	{
		out.write("quit\n");
		out.flush();
		in.close();
		out.close();
		client.close();
		server.close();
	}//end of cleanup
	
	
	/*private static boolean playVideos(List<VideoData> videos, BufferedReader in, BufferedWriter out, InetAddress address) throws IOException
	{
		
		//for(VideoData video: videos)
		//{
			streamServer = new StreamServer(address, videos, in, out);
			//streamServer.addVideos(videos);
			streamServer.start();
			out.write(SharedData.videoPort + "\n");
			out.flush();
			while(!streamServer.isPlaying());
			streamServer.processCommand("TITLE");
			String title = streamServer.getTitle();
			System.out.println(title);
			//Thread commandListener = new Thread(() -> {checkClientInput(in, streamServer);}); 
			//commandListener.start();
			while(streamServer.isAlive())
			{
				if(!title.contentEquals(streamServer.getTitle()))
				{
					title = streamServer.getTitle();
					System.out.println(title);
					streamServer.processCommand("TITLE");
					
				}
				try { Thread.sleep(30 *1000);}catch(InterruptedException e) {}
			}
			return true;
			//out.write("stop\n");
			//out.flush();
		//}//end of for loop
		//return true;
	}//end of playVideos*/
	
	
	/*public static void checkClientInput(BufferedReader in, StreamServer server)
	{		
		while(true)
		{
			try {
				server.processCommand(in.readLine().toUpperCase());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}//end of catc
		}//end of while
	}//end of checkClientInput*/
	

	public static List<VideoData> processArgs(String[] args, ArrayList<String> videoTypes, SharedData options)
	{
		ArrayList<VideoData> listOfVideos = new ArrayList<VideoData>();
		VideoData cursor = new VideoData();
		boolean useEmbeded = false;
		boolean setStartTime = false;
		boolean setStopTime = false;
		for(int i = 0; i < args.length; i++)
		{
			String arg = args[i];
			System.out.println("args: " + arg);
			switch(arg.toLowerCase())
			{
				case "--starttime":
					setStartTime = true;
					break;
				case "--endtime":
					setStopTime = true;
					break;
				case "--startchapter":
					SharedData.startChapter = Integer.parseInt(args[++i]);
					break;
				case "--endchapter":
					SharedData.endChapter = Integer.parseInt(args[++i]);
					break;
				case "-s":				
					cursor.hasSubtitles= true;
					break;
				case "-es":
					useEmbeded = true;
					break;
				default:
					try {
							if(setStartTime)
							{
								SharedData.startTime = Long.parseLong(arg) * 1000;
								setStartTime = false;
							}
							else if(setStopTime)
							{
								SharedData.endTime = Long.parseLong(arg) * 1000;
								setStopTime = false;
							}
							else
								options.start = Integer.parseInt(arg);
						}
					catch(NumberFormatException e)
					{
						if(checkIfDir(arg))
						{
							listOfVideos.addAll(getVideosFromDir(arg, videoTypes));
							//cursor = listOfVideos.get(listOfVideos.size());
						}//end of if
						else if(checkIfSubtitleFile(arg))
						{
							cursor.subtitlePath = arg;
						}//end of else 
						else if(checkIfVideo(arg, videoTypes))
						{
							cursor = new VideoData(arg);
							listOfVideos.add(cursor);
						}
					}//end of catch
			}//end of swithc
			
		}//end of for loop
		if(options.start < listOfVideos.size())
			return listOfVideos.subList(options.start, listOfVideos.size());
		else
			return listOfVideos;
	}//end of processArgs
	
	
	private static boolean checkIfSubtitleFile(String arg) {
		// TODO Auto-generated method stub
		if(arg.lastIndexOf(".") == -1)
			return false;
		String extension = arg.substring(arg.lastIndexOf("."));
		return extension.contains("srt") || extension.contains("SRT");
	}

	public static boolean checkIfVideo(String input, ArrayList<String> videoTypes)
	{
		if(input.lastIndexOf(".") == -1)
			return false;
		String extension = input.substring(input.lastIndexOf("."));
		return videoTypes.contains(extension);
	}//end of check if Video
	
	public static boolean checkIfDir(String input)
	{
		File test = new File(input);
		return test.isDirectory();
	}//end of checkIfIndex
	
	public static ArrayList<VideoData> getVideosFromDir(String path, ArrayList<String> videoTypes)
	{
		ArrayList<VideoData> videos = new ArrayList<VideoData>();
		File folder = new File(path);
		System.out.println("Directory Path " + folder.getPath());
		for(String file: folder.list())
		{
			try
			{
				if(checkIfVideo(file, videoTypes))
				{
					if(file.startsWith(File.separator) || path.endsWith(File.separator))
						videos.add(new VideoData(path + file));
					else
						videos.add(new VideoData(path + File.separator + file));
				}
		
			}catch(StringIndexOutOfBoundsException e) {e.printStackTrace();}
		}
		
		return videos;
	}//end of getVideosFromDir
	
	
}//end of ServerMain

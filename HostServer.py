import sys
import socket
import os
import time
import vlc

host ="192.168.50.62"
port = "9998"
startIndex = 0

videoTypes = ['.264', '.3g2', '.3gp', '.3gp2', '.3gpp', '.3gpp2', '.3mm', '.3p2', '.60d', '.787', '.89', '.aaf', '.aec', '.aep', '.aepx',
'.aet', '.aetx', '.ajp', '.ale', '.am', '.amc', '.amv', '.amx', '.anim', '.aqt', '.arcut', '.arf', '.asf', '.asx', '.avb',
'.avc', '.avd', '.avi', '.avp', '.avs', '.avs', '.avv', '.axm', '.bdm', '.bdmv', '.bdt2', '.bdt3', '.bik', '.bin', '.bix',
'.bmk', '.bnp', '.box', '.bs4', '.bsf', '.bvr', '.byu', '.camproj', '.camrec', '.camv', '.ced', '.cel', '.cine', '.cip',
'.clpi', '.cmmp', '.cmmtpl', '.cmproj', '.cmrec', '.cpi', '.cst', '.cvc', '.cx3', '.d2v', '.d3v', '.dat', '.dav', '.dce',
'.dck', '.dcr', '.dcr', '.ddat', '.dif', '.dir', '.divx', '.dlx', '.dmb', '.dmsd', '.dmsd3d', '.dmsm', '.dmsm3d', '.dmss',
'.dmx', '.dnc', '.dpa', '.dpg', '.dream', '.dsy', '.dv', '.dv-avi', '.dv4', '.dvdmedia', '.dvr', '.dvr-ms', '.dvx', '.dxr',
'.dzm', '.dzp', '.dzt', '.edl', '.evo', '.eye', '.ezt', '.f4p', '.f4v', '.fbr', '.fbr', '.fbz', '.fcp', '.fcproject',
'.ffd', '.flc', '.flh', '.fli', '.flv', '.flx', '.gfp', '.gl', '.gom', '.grasp', '.gts', '.gvi', '.gvp', '.h264', '.hdmov',
'.hkm', '.ifo', '.imovieproj', '.imovieproject', '.ircp', '.irf', '.ism', '.ismc', '.ismv', '.iva', '.ivf', '.ivr', '.ivs',
'.izz', '.izzy', '.jss', '.jts', '.jtv', '.k3g', '.kmv', '.ktn', '.lrec', '.lsf', '.lsx', '.m15', '.m1pg', '.m1v', '.m21',
'.m21', '.m2a', '.m2p', '.m2t', '.m2ts', '.m2v', '.m4e', '.m4u', '.m4v', '.m75', '.mani', '.meta', '.mgv', '.mj2', '.mjp',
'.mjpg', '.mk3d', '.mkv', '.mmv', '.mnv', '.mob', '.mod', '.modd', '.moff', '.moi', '.moov', '.mov', '.movie', '.mp21',
'.mp21', '.mp2v', '.mp4', '.mp4v', '.mpe', '.mpeg', '.mpeg1', '.mpeg4', '.mpf', '.mpg', '.mpg2', '.mpgindex', '.mpl',
'.mpl', '.mpls', '.mpsub', '.mpv', '.mpv2', '.mqv', '.msdvd', '.mse', '.msh', '.mswmm', '.mts', '.mtv', '.mvb', '.mvc',
'.mvd', '.mve', '.mvex', '.mvp', '.mvp', '.mvy', '.mxf', '.mxv', '.mys', '.ncor', '.nsv', '.nut', '.nuv', '.nvc', '.ogm',
'.ogv', '.ogx', '.osp', '.otrkey', '.pac', '.par', '.pds', '.pgi', '.photoshow', '.piv', '.pjs', '.playlist', '.plproj',
'.pmf', '.pmv', '.pns', '.ppj', '.prel', '.pro', '.prproj', '.prtl', '.psb', '.psh', '.pssd', '.pva', '.pvr', '.pxv',
'.qt', '.qtch', '.qtindex', '.qtl', '.qtm', '.qtz', '.r3d', '.rcd', '.rcproject', '.rdb', '.rec', '.rm', '.rmd', '.rmd',
'.rmp', '.rms', '.rmv', '.rmvb', '.roq', '.rp', '.rsx', '.rts', '.rts', '.rum', '.rv', '.rvid', '.rvl', '.sbk', '.sbt',
'.scc', '.scm', '.scm', '.scn', '.screenflow', '.sec', '.sedprj', '.seq', '.sfd', '.sfvidcap', '.siv', '.smi', '.smi',
'.smil', '.smk', '.sml', '.smv', '.spl', '.sqz', '.srt', '.ssf', '.ssm', '.stl', '.str', '.stx', '.svi', '.swf', '.swi',
'.swt', '.tda3mt', '.tdx', '.thp', '.tivo', '.tix', '.tod', '.tp', '.tp0', '.tpd', '.tpr', '.trp', '.ts', '.tsp', '.ttxt',
'.tvs', '.usf', '.usm', '.vc1', '.vcpf', '.vcr', '.vcv', '.vdo', '.vdr', '.vdx', '.veg','.vem', '.vep', '.vf', '.vft',
'.vfw', '.vfz', '.vgz', '.vid', '.video', '.viewlet', '.viv', '.vivo', '.vlab', '.vob', '.vp3', '.vp6', '.vp7', '.vpj',
'.vro', '.vs4', '.vse', '.vsp', '.w32', '.wcp', '.webm', '.wlmp', '.wm', '.wmd', '.wmmp', '.wmv', '.wmx', '.wot', '.wp3',
'.wpl', '.wtv', '.wve', '.wvx', '.xej', '.xel', '.xesc', '.xfl', '.xlmv', '.xmv', '.xvid', '.y4m', '.yog', '.yuv', '.zeg',
'.zm1', '.zm2', '.zm3', '.zmv']

def createVLCInstance(hasSub, filepath, raspberry):
    start = "sout=#"
    standard = "standard{access=udp,mux=ts,dst=" +raspberry +":"+port+"}"
    transcodeForSub = "transcode{vcodec=h264,scale=Auto,acodec=mpga,ab=128,channels=2,samplerate=44100,soverlay}:"
    transcodeForNoSub = "transcode{vcodec=h264,vb=800,acodec=mpga,ab=128,channels=2,samplerate=44100,scodec=none}:"
    option = ''
    if(hasSub):
        option = start + transcodeForSub + standard 
    else:
        option = start + transcodeForNoSub + standard
    vlcInstance = vlc.Instance()
    vlcPlayer = vlcInstance.media_player_new()
    vlcMedia = vlcInstance.media_new(filepath, option)
    vlcPlayer.set_media(vlcMedia)
    return vlcPlayer

def streamingVideo(vlcPlayer, clientSocket):
    vlcPlayer.play()
    print("Now starting reading from socket")
    clientSocket.setblocking(1)
    command = str(clientSocket.recv(1024))
    while ('quit' not in command) and (vlcPlayer.get_position() < 1.0):
        if 'pause' in command:
            vlcPlayer.pause()
        elif 'play' in command:
            vlcPlayer.play()
        elif 'stop' in command:
            vlcPlayer.stop()
        try:
            command = str(clientSocket.recv(1024))
        except:
            command = 'continue'
    print("About to return to main thread")
    return
        
    
def PlayVideo(filepath,raspberry, clientSocket):
    try:
        vlcPlayer = createVLCInstance(False, filepath, raspberry)
        streamingVideo(vlcPlayer, clientSocket)
    except:
        clientSocket.close()
        vlcPlayer.stop()
        print("server crashed")
        raise
    
def PlayVideoWithSubtitles(filepath,raspberry, clientSocket, sub):
    try:
        vlcPlayer = createVLCInstance(True, filepath, raspberry)
        vlcPlayer.video_set_subtitle_file(sub)
        streamingVideo(vlcPlayer, clientSocket)
    except:
        clientSocket.close()
        vlcPlayer.stop()
        print("server crashed")
        raise        

def CheckIfIndex(root):
    try:
        int(root)
        return True
    except ValueError:
        return False



def main():
    numberOfVideos = len(sys.argv)
    listOfVideos = []    
    for argNum in range(1, numberOfVideos):
        root = sys.argv[argNum]
        if(os.path.isdir(root)):
            for file in os.listdir(root):
                for type in videoTypes:
                    if(file.endswith(type)):
                        listOfVideos.append(os.path.join(root, file))
                        print("added: " + os.path.join(root, file) + "\n")
        elif(CheckIfIndex(root)):
            startIndex = int(root)
            print("Starting at " + root + "\n")
        else:
            listOfVideos.append(root)
            print("added: " + root + "\n")
    listenPort = 9000
    hasSubtitles = False
    serverSocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    serverSocket.bind((host, listenPort))
    print("host ip: " + host)
    serverSocket.listen(1)
    clientSocket, raspberryTuple = serverSocket.accept()
    raspberry = raspberryTuple[0]
    clientSocket.send(port.encode())
    if( '--sub' in listOfVideos[0]):
        print("Video Has Subtitles")
        hasSubtitles = True
        numberOfVideos = 2
    clientSocket.send(str(len(listOfVideos)).encode())
    try:
        for videoIndex in range(startIndex, len(listOfVideos)):
            if(hasSubtitles):
                PlayVideoWithSubtitles(listOfVideos[1], raspberry, clientSocket, listOfVideos[2])
            else:
                PlayVideo(listOfVideos[videoIndex], raspberry,clientSocket)
        serverSocket.close()
        clientSocket.close()

    except KeyboardInterrupt:
        serverSocket.close()
        clientSocket.close()
        sys.exit()
    
    
    
                            
    


    
if __name__ == "__main__":
    main()

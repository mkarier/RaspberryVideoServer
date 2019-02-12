import sys
import socket
import os
import time
import vlc

host ="192.168.50.43"
port = "9998"

def PlayVideo(filepath,raspberry, clientSocket):
	try:
		option = "sout=#standard{access=udp,mux=ts,dst=" +raspberry +":"+port+"}"
		vlcInstance = vlc.Instance()
		vlcPlayer = vlcInstance.media_player_new()
		vlcMedia = vlcInstance.media_new(filepath, option)
		vlcPlayer.set_media(vlcMedia)
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
	except:
		clientSocket.close()
		vlcPlayer.stop()
		print("server crashed")
		raise

def PlayVideoWithSubtitles(filepath,raspberry, clientSocket, sub):
	try:
		option = "sout=#transcode{vcodec=h264,scale=Auto,acodec=mpga,ab=128,channels=2,samplerate=44100,soverlay}:standard{access=udp,mux=ts,dst=" +raspberry +":"+port+"}"
		vlcInstance = vlc.Instance()
		vlcPlayer = vlcInstance.media_player_new()
		vlcMedia = vlcInstance.media_new(filepath, option)
		#vlcPlayer.slaves_add('subtitle', 1, sub)
		vlcPlayer.set_media(vlcMedia)
		vlcPlayer.video_set_subtitle_file(sub)
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
	except:
		clientSocket.close()
		vlcPlayer.stop()
		print("server crashed")
		raise		
		
def main():
	numberOfVideos = len(sys.argv)
	listenPort = 9000
	hasSubtitles = False
	serverSocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
	serverSocket.bind((host, listenPort))
	print("host ip: " + host)
	serverSocket.listen(1)
	clientSocket, raspberryTuple = serverSocket.accept()
	raspberry = raspberryTuple[0]
	clientSocket.send(port.encode())
	if( '--sub' in sys.argv[1]):
		print("Video Has Subtitles")
		hasSubtitles = True
		numberOfVideos = 2
	clientSocket.send(str(numberOfVideos -1).encode())
	try:
		for videoIndex in range(1, numberOfVideos):
			if(hasSubtitles):
				PlayVideoWithSubtitles(sys.argv[2], raspberry, clientSocket, sys.argv[3])
			else:
				PlayVideo(sys.argv[videoIndex], raspberry,clientSocket)
		serverSocket.close()
		clientSocket.close()

	except KeyboardInterrupt:
		serverSocket.close()
		clientSocket.close()
		sys.exit()
	
	
	
							
	


	
if __name__ == "__main__":
	main()

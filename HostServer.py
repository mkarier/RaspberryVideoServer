import sys
import socket
import os
import threading
import vlc

host ='192.168.50.43'
port = '9998'

def StartServer(filepath,raspberry):
	try:
		option = "sout=#standard{access=udp,mux=ts,dst=" +raspberry +":"+port+"}"
		vlcInstance = vlc.Instance()
		vlcPlayer = vlcInstance.media_player_new()
		vlcMedia = vlcInstance.media_new(filepath, option)
		vlcPlayer.set_media(vlcMedia)
		vlcPlayer.play()
		while vlcPlayer.is_playing():
			#print "In Server while loop"
			continue
	except:
		print("server crashed")
		raise

def main():
	filepath = sys.argv[1]
	raspberry = '192.168.50.42'
	listenPort = 9000
	serverSocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
	serverSocket.bind((host, listenPort))
	print "host ip: " + host
	serverSocket.listen(8)
	clientSocket, raspberrypi = serverSocket.accept()
	while True:
		try:
			serverThread.start()
			while 1:
				continue
		except KeyboardInterrupt:
			sys.exit()
	
	
	
							
	


	
if __name__ == "__main__":
	main()

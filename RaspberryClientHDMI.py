import sys
import socket
import os
import threading


serverAddr = 'mkarier-desktop.local'
#options  = ' --win 0,0,800,480 --display 4 '
options = ' -o hdmi --win 0,0,1920,1080 --display 2 '

def main():
	socketClient = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
	chatPort = 9000
	print ("Trying to connect to server")
	socketClient.connect((serverAddr, chatPort))
	print ("formed a connection with server")
	#socketClient.send(raspberry)
	videoPort = socketClient.recv(1024)
	print ("Video port: " + videoPort)
	systemCommand = 'omxplayer' + options +'udp://' + serverAddr + ':' + videoPort
	numberOfVideos = socketClient.recv(1024).decode()
	try:
		for videoIndex in range(int(numberOfVideos)):
			socketClient.send('start'.encode)
			os.system(systemCommand)
			socketClient.send('quit'.encode)
		socketClient.close()

	except KeyboardInterrupt:
		socketClient.send('quit')
		socketClient.close()
		sys.exit()

if __name__ == "__main__":
	main()

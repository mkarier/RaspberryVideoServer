import sys
import socket
import os
import threading


serverAddr = '192.168.50.43'
options  = ' --win 0,0,800,480 --display 4 '
#options = ' --win 0,0,1920,1080 --display 5 '

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
	numberOfVideos = socketClient.recv(1024)
	try:
		for videoIndex in range(int(numberOfVideos)):
			socketClient.send('start')
			os.system(systemCommand)
			socketClient.send('quit')
		socketClient.close()

	except KeyboardInterrupt:
		socketClient.send('quit')
		socketClient.close()
		sys.exit()

if __name__ == "__main__":
	main()

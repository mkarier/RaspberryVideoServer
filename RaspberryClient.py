import sys
import socket
import os
import threading


serverAddr = '192.168.50.43'
raspberry = '192.168.50.42'
options  = ' --win 0,0,800,480 --display 4 '


def main():
	socketClient = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
	chatPort = 9000
	print ("Trying to connect to server")
	socketClient.connect((serverAddr, chatPort))
	print ("formed a connection with server")
	#socketClient.send(raspberry)
	videoPort = socketClient.recv(1024)
	print ("Video port: " + videoPort)
	try:
		socketClient.send('start')
		os.system('omxplayer' + options + 'udp://' + serverAddr + ':' + videoPort)
		socketClient.send('quit')
		socketClient.close()

	except KeyboardInterrupt:
		socketClient.send('quit')
		socketClient.close()
		sys.exit()

if __name__ == "__main__":
	main()

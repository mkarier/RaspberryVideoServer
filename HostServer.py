import sys
import socket
import select
import os
import threading
import time
import vlc

host ='192.168.50.43'
port = '9998'
threadIsAlive = True

def StartServer(filepath,raspberry, clientSocket):
	try:
		option = "sout=#standard{access=udp,mux=ts,dst=" +raspberry +":"+port+"}"
		vlcInstance = vlc.Instance()
		vlcPlayer = vlcInstance.media_player_new()
		vlcMedia = vlcInstance.media_new(filepath, option)
		vlcPlayer.set_media(vlcMedia)
		vlcPlayer.play()
		print("Is about to enter the while loop")
		clientSocket.setblocking(1)
		command = clientSocket.recv(1024)
		while command not in 'quit' and vlcPlayer.is_playing():
			if 'pause' in command:
				vlcPlayer.pause()
			elif 'play' in command:
				vlcPlayer.play()
			elif 'stop' in command:
				vlcPlayer.stop()
			try:
				command = clientSocket.recv(1024)
			except:
				command = 'continue'
		clientSocket.close()
		threadIsAlive = False
		return
	except:
		ThreadIsAlive = False
		clientSocket.close()
		vlcPlayer.stop()
		print("server crashed")
		raise

def main():
	filepath = sys.argv[1]
	listenPort = 9000
	serverSocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
	serverSocket.bind((host, listenPort))
	print "host ip: " + host
	serverSocket.listen(1)
	clientSocket, raspberryTuple = serverSocket.accept()
	raspberry = raspberryTuple[0]
	clientSocket.send(port)
	serverThread = threading.Thread(target=StartServer,args=(filepath, raspberry, clientSocket))
	try:
		serverThread.start()
		while threadIsAlive:
			time.sleep(3)
			continue
		print("The movie Ended")
		serversocket.close()
		clientSocket.close()
	except KeyboardInterrupt:
		serverThread.join()
		serverSocket.close()
		clientSocket.close()
		serverThread.join()
		sys.exit()
	
	
	
							
	


	
if __name__ == "__main__":
	main()

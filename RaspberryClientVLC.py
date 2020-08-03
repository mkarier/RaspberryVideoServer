import sys
import socket
import vlc
import os
import time
#import vlcPlayer

serverAddr = "mkarier-desktop.local"
streamStyle = "udp://@"
testPort = "9998"





def startPlayer(media_player, vlc_instance, videoPort, socketClient):
    #media = vlc_instance.media_new(streamStyle+":"+videoPort)
   # media.get_mrl()
    media_player.set_mrl(streamStyle+":"+videoPort)
    #media_player.set_media(media)
    media_player.play()
    media_player.set_fullscreen(True)
    waitForEnding(socketClient)

def waitForEnding(socketClient):
    command = str(socketClient.recv(1024).decode())
    while(command != "quit"):
        command = str(socketClient.recv(1024).decode())

def main():
    socketClient = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    chatPort = 9000
    
    print ("Trying to connect to server")
    socketClient.connect((serverAddr, chatPort))
    print ("formed a connection with server")
    #socketClient.send(raspberry)
    videoPort = socketClient.recv(1024).decode()
    print ("Video port: " + videoPort)
    vlc_instance = vlc.Instance()
    media_player = vlc_instance.media_player_new()
    numberOfVideos = socketClient.recv(1024).decode()
    try:
        for videoIndex in range(int(numberOfVideos)):
            socketClient.send('start'.encode())
            startPlayer(media_player, vlc_instance, videoPort, socketClient)
            #otherPlayer(videoPort)
            #socketClient.send('quit'.encode())
            time.sleep(10)
        socketClient.close()

    except KeyboardInterrupt:
        socketClient.send('quit')
        socketClient.close()
        sys.exit()


if __name__ == "__main__":
    main()
import sys
import socket
import vlc
import os
import time
#import vlcPlayer

serverAddr = "mkarier-desktop.local"
streamStyle = "udp://@"
testPort = "9998"





def startPlayer(videoPort):
    time.sleep(2)
    vlc_instance = vlc.Instance()
    media_player = vlc_instance.media_player_new()
    media = vlc_instance.media_new(streamStyle+":"+testPort)
    media.get_mrl()
   # media_player.set_mrl(streamStyle+serverAddr+":"+videoPort)
    media_player.set_media(media)
    media_player.play()
    media_player.set_fullscreen(True)
    time.sleep(10)
    while(media_player.is_playing()):
        time.sleep(1)

'''def otherPlayer(videoPort):
    window = vlcPlayer.ApplicationWindow()
    window.setup_objects_and_events()
    window.setMRL(streamStyle+serverAddr+":"+videoPort)
    window.show()
    Gtk.main()'''
    
def simplePlayer():
    os.system("vlc " + streamStyle+serverAddr+":"+videoPort)


def main():
    socketClient = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    chatPort = 9000
    print ("Trying to connect to server")
    socketClient.connect((serverAddr, chatPort))
    print ("formed a connection with server")
    #socketClient.send(raspberry)
    videoPort = socketClient.recv(1024).decode()
    print ("Video port: " + videoPort)
    numberOfVideos = socketClient.recv(1024).decode()
    try:
        for videoIndex in range(int(numberOfVideos)):
            socketClient.send('start'.encode())
            startPlayer(videoPort)
            #otherPlayer(videoPort)
            socketClient.send('quit'.encode())
        socketClient.close()

    except KeyboardInterrupt:
        socketClient.send('quit')
        socketClient.close()
        sys.exit()


if __name__ == "__main__":
    main()
# RaspberryVideoServer
I want to create a server to run on a Desktop that will stream videos passed to it once a client connects. The Raspberry pi Client will connect and start streaming with omxplayer.

install python-vlc through pip. I found if I install 'vlc' through pip it will mess up some objects so make sure that is only
python-vlc' installed. 

setup:
pip install python-vlc

Also the IP addresses for the host is hard coded in both RaspberryClient and HostServer so make sure that the IP address
for the desktop that you are using is placed in there. 

example: 
	python3 Host.py /path/to/VideoFile /path/to/another/video/file
	python3 Host.py --sub /path/to/VideoFile /path/to/Subtitle/File
	python3 Host.py -d /path/to/folder/with/videos/ /path/to/another/folder/with/videos/
NOTE:
	For people who have a Rasberrypi tablet, the difference between RaspberryClient.py and RaspberryClientHDMI.py
	is the options for omxplayer output.

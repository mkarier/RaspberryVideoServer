wget -q -O - https://download.bell-sw.com/pki/GPG-KEY-bellsoft | sudo apt-key add -
if [[ "aarch64" == $(uname -m) ]];
	then echo "deb [arch=arm64] https://apt.bell-sw.com/ stable main" | sudo tee /etc/apt/sources.list.d/bellsoft.list
elif [[ "armv7l" == $(uname -m) ]];
	then echo "deb [arch=armhf] https://apt.bell-sw.com/ stable main" | sudo tee /etc/apt/sources.list.d/bellsoft.list
elif [[ "x86_64" == $(uname -m) ]];
	then echo "deb [arch=amd64] https://apt.bell-sw.com/ stable main" | sudo tee /etc/apt/sources.list.d/bellsoft.list
else
	echo "Can't find arch Type"
fi
sudo apt -y update && sudo apt -y full-upgrade
sudo apt -y install bellsoft-java11
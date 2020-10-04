JarPath = './vlcj-3.12.1/vlcj-3.12.1.jar:./vlcj-3.12.1/jna-5.2.0.jar:./vlcj-3.12.1/jna-platform-5.2.0.jar:./vlcj-3.12.1/slf4j-api-1.7.25.jar:./vlcj-3.12.1/slf4j-nop-1.7.30.jar'
JarClient = 'StreamClient'
JarServer = 'VlcServerTest'
PiJavaVLC:
	javac -cp $(JarPath) src/client/*.java src/server/*.java src/shared_class/*.java -d ./bin/

BuildClient.o: PiJavaVLC
	jar cmvf ./ClientManifest.txt $(JarClient).jar ./org/ 
	#jar um $(JarClient).jar ./ClientManifest.txt
	cd ./bin/ && jar uf ../$(JarClient).jar client/*.class server/*.class shared_class/*.class
	pwd
	cd ./vlcj-3.12.1/ && jar uf ../$(JarClient).jar vlcj-3.12.1.jar jna-5.2.0.jar jna-platform-5.2.0.jar slf4j-api-1.7.25.jar slf4j-nop-1.7.30.jar  
	echo 'Finished creating the Client jar'
	#jar umf $(JarClient).jar 
	
	
BuildServer.o: PiJavaVLC
	jar cmvf ./ServerManifest.txt $(JarServer).jar ./org/ 
	#jar um $(JarClient).jar ./ClientManifest.txt
	cd ./bin/ && jar uf ../$(JarServer).jar client/*.class server/*.class shared_class/*.class
	pwd
	cd ./vlcj-3.12.1/ && jar uf ../$(JarServer).jar vlcj-3.12.1.jar jna-5.2.0.jar jna-platform-5.2.0.jar slf4j-api-1.7.25.jar slf4j-nop-1.7.30.jar  
	echo 'Finished creating the Server jar'
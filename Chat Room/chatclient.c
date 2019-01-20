/*
 * chatclient.c
 *
 *  Created on: Oct 20, 2016
 *      Author: marco
 */


#include"chatclient.h"

pthread_mutex_t clilock;
int stillConnected = 1;

int main(int argc, char ** argv)
{
	int sockfd,len,ret;
	struct sockaddr_in saddr;
	stillConnected = 1;
	pthread_t clReader, clWriter;
	if(argc != 3)
	{
		perror("argv ");
		exit(0);
	}//end of if


	// creating tcp socket
	sockfd = socket(AF_INET,SOCK_STREAM,0);
	saddr.sin_family = AF_INET;
	saddr.sin_port = htons(atoi(argv[2]));

	//saddr.sin_addr = *(struct in_addr *) *(hostinfo->h_addr_list);
	saddr.sin_addr.s_addr = inet_addr(argv[1]);
	len = sizeof(saddr);


	ret = connect(sockfd,(struct sockaddr *)&saddr,len);
	if(ret == -1)
	{
		perror("connect ");
		exit(1);
	}//end of if statement for connection
	printf("Socket:%d\n", sockfd);
	pthread_create(&clReader, 0, (void *)clientReader, &sockfd);
	pthread_create(&clWriter, 0, (void *)clientWriter, &sockfd);
	pthread_join(clReader, NULL);
	pthread_join(clWriter, NULL);
	close(sockfd);

	return 0;
}//end of main

void clientReader(int * socketfd)
{
	//TODO:
	char  buffer[1024];
	int reader = 1;
	while(1)
	{
		reader = read(* socketfd, buffer, 1024);
		//pthread_mutex_lock(&clilock);
		if(reader <= 0 || stillConnected == 0)
		{
			pthread_mutex_lock(&clilock);
			stillConnected = 0;
			pthread_mutex_unlock(&clilock);
			return;
		}//end of if
		//pthread_mutex_unlock(&clilock);
		printf("-->%s", buffer);
		bzero(buffer, 1024);
		//buffer[0] = '\0';
	}//end of outer while
}//end of client Reader

void clientWriter(int *socketfd)
{
	//TODO:
	
	int writer = 0;
	while(1)
	{
		char buffer[1024];
		fgets(buffer, 1024, stdin);
		//pthread_mutex_lock(&clilock);
		if(strcmp(buffer, "quit\n") == 0 || writer == -1 || stillConnected == 0)
		{
			pthread_mutex_lock(&clilock);
			write(*socketfd, "Bye!", strlen("Bye!") * sizeof(char));
			stillConnected = 0;
			pthread_mutex_unlock(&clilock);
			return;
		}//end of if
		//pthread_mutex_unlock(&clilock);
		writer = write(*socketfd, buffer, 1024);
		bzero(buffer, 1024);
		//buffer[0] = '\0';

	}//end of outer while
}//end of clientwriter

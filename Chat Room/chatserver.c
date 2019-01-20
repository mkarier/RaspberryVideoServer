/*
 * chatserver.c
 *
 *  Created on: Oct 18, 2016
 *      Author: marco
 */

#include"chatserver.h"

pthread_mutex_t serverlock;
int main(int argc, char ** argv)
{
	int listenfd;
	pthread_t pRead = pthread_self(), pWrite = pthread_self();
	//int stillConnected = 1;
	int port = atoi(argv[1]);
	struct sockaddr_in servaddr;

	listenfd = socket(AF_INET, SOCK_STREAM, 0);
	bzero(&servaddr, sizeof(servaddr));
	servaddr.sin_family = AF_INET;
	servaddr.sin_addr.s_addr = htonl(INADDR_ANY);
	servaddr.sin_port = htons(port);
	bind(listenfd, (struct sockaddr *) &servaddr, sizeof(servaddr));
	listen(listenfd, 8);
	int i = 0;
	for(i = 0; i < 8; i++)
	{
		connfd[i] = 0;
	}//end of for loop

	while(1)
	{
		i = i%8;
		connfd[i] = accept(listenfd, (struct sockaddr *) NULL, NULL);
		//This part is do the above while loop but with threads.
		printf("Accepted connection: %d\n", connfd[i]);
		//write(connfd[i], "Welcome\n", strlen("Welcome\n") * sizeof(char));
		pthread_create(&pRead, 0, (void *)serverReader, &connfd[i]);
		//pthread_create(&pWrite, 0, (void *)serverWriter, &connfd[i]);
		i++;
		pthread_detach(pRead);
		//pthread_detach(pWrite);
	}//end of outer for loop
}//end of int main

void serverReader(int * sockfd)
{
	char buffer[BUF_SIZE];
	int reader = 0;
	int i = 0;
	int writer = 0;
	while(1)
	{
		reader = read(*sockfd, buffer, 40);
		if(reader == 0 || reader == -1 || strcmp(buffer, "Bye!") == 0)
		{
			close(*sockfd);
			return;
		}//end of if statment when someone quits
		printf("Read from Socket:%d-->%s\n", *sockfd,buffer);
		pthread_mutex_lock(&serverlock);
		for(i = 0; i < 8; i++)
		{
			if(connfd[i] != 0 && connfd[i] != *sockfd)
			{
				writer = write(connfd[i], buffer, 40);
				if(writer == 0 || writer == -1)
				{
					close(connfd[i]);
					connfd[i] = 0;
				}//end of if statement to make sure that the connection is still open
			}//end of else
		}//end of for loop
		pthread_mutex_unlock(&serverlock);
		bzero(buffer, 40);

	}//end of while loop

}//end of reader

void serverWriter(int * sockfd)
{
	char buffer[BUF_SIZE];
	int i = 0, writer = 0;
	while(1)
	{
		fgets(buffer, 40,stdin);
		for(i = 0; i < 8; i++)
		{
			writer = write(connfd[i], buffer, 40);
			if(writer == 0 || writer == -1)
			{
				close(connfd[i]);
				connfd[i] = 0;
			}//end of if statement to make sure that the connection is still open
		}//end for loop to be printing
		bzero(buffer, 40);
	}//end of infinite while loop


}//end of writer;

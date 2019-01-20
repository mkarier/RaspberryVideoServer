/*
 * chatclient.h
 *
 *  Created on: Oct 20, 2016
 *      Author: marco
 */

#ifndef CHATCLIENT_H_
#define CHATCLIENT_H_

#include<stdio.h>
#include<stdlib.h>
#include<unistd.h>
#include<sys/socket.h>
#include<netinet/in.h>
#include<arpa/inet.h>
#include<netdb.h>
#include<string.h>
#include<sys/types.h>
#include<sys/stat.h>
#include<fcntl.h>
#include<pthread.h>

#define BUF_SIZE 500

void clientReader(int * sockfd);
void clientWriter(int * sockfd);

#endif /* CHATCLIENT_H_ */

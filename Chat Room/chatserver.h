/*
 * chatserver.h
 *
 *  Created on: Oct 18, 2016
 *      Author: marco
 */

#ifndef CHATSERVER_H_
#define CHATSERVER_H_

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
int connfd[8];
void serverReader(int * connfd);
void serverWriter(int * connfd);

#endif /* CHATSERVER_H_ */

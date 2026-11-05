#include <assert.h>
#include <stdio.h>
#include "libavformat/avformat.h"
int main(int argc,char **argv){
    assert(argc==2);AVFormatContext *context=NULL;
    int result=avformat_open_input(&context,argv[1],NULL,NULL);
    avformat_close_input(&context);
    if(result>=0)return 2;
    printf("RTSP rejected with %d\n",result);return 0;
}

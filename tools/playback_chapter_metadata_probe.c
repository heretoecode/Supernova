/* Exercises the actual patched AVOS metadata parcel on Android; no player/UI acceptance claim. */
#include <sys/types.h>
#include <stdint.h>
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <libavformat/avformat.h>
#include <libavutil/mathematics.h>
#include "avos_common.h"
#include "avos_mp.h"
#include "avos_mp_priv.h"
#include "avos_mp_metadata.h"
#include "stream.h"
#include "file_type.h"

static int integer(metadata_buffer_t *buffer,int key){avos_msg_t *msg=avos_metadata_get_handle()->get(buffer,key);int value=-1;if(msg&&msg->type==AVOS_MSG_TYPE_INT)memcpy(&value,msg->data,sizeof(value));return value;}
static int64_t integer64(metadata_buffer_t *buffer,int key){avos_msg_t *msg=avos_metadata_get_handle()->get(buffer,key);int64_t value=-1;if(msg&&msg->type==AVOS_MSG_TYPE_INT64)memcpy(&value,msg->data,sizeof(value));return value;}
int main(int argc,char **argv){
    const avos_mp_handle_t *player=avos_mp_get_handle();const avos_metadata_handle_t *metadata=avos_metadata_get_handle();avos_mp_t *mp=player->create(NULL);if(!mp)return 1;
    AV_PROPERTIES av={0};STREAM_CHAPTER first={.start=250,.end=1250},second={.start=1250,.end=5250};strcpy(first.title,"Opening");strcpy(second.title,"Second chapter");STREAM_CHAPTER *chapters[]={&first,&second};metadata_buffer_t *buffer=NULL;
    avos_mp_fillmetadata(mp,TYPE_VID,0,NULL,&av,NULL,5000,1,1,0,2,chapters,250);
    if(player->getmetadata(mp,&buffer)!=0||integer(buffer,AVOS_MP_METADATA_NB_CHAPTER)!=2||integer64(buffer,AVOS_MP_METADATA_CHAPTER)!=0||integer64(buffer,AVOS_MP_METADATA_CHAPTER+2)!=1000)return 2;
    avos_msg_t *title=metadata->get(buffer,AVOS_MP_METADATA_CHAPTER+3);if(!title||title->type!=AVOS_MSG_TYPE_STR||strcmp((const char*)title->data,"Second chapter"))return 3;metadata->destroy(&buffer);
    avos_mp_fillmetadata(mp,TYPE_VID,0,NULL,&av,NULL,5000,1,1,0,0,chapters,0);player->getmetadata(mp,&buffer);if(integer(buffer,AVOS_MP_METADATA_NB_CHAPTER)!=0)return 4;metadata->destroy(&buffer);
    avos_mp_fillmetadata(mp,TYPE_VID,0,NULL,&av,NULL,5000,1,1,0,257,chapters,0);player->getmetadata(mp,&buffer);if(integer(buffer,AVOS_MP_METADATA_NB_CHAPTER)!=0)return 5;metadata->destroy(&buffer);
    if(argc>1){
        AVFormatContext *format=NULL;
        if(avformat_open_input(&format,argv[1],NULL,NULL)<0||avformat_find_stream_info(format,NULL)<0||format->nb_chapters!=2)return 6;
        STREAM_CHAPTER actual[2]={{0}};STREAM_CHAPTER *pointers[]={&actual[0],&actual[1]};
        for(int i=0;i<2;i++){AVChapter *chapter=format->chapters[i];actual[i].start=av_rescale_q(chapter->start,chapter->time_base,(AVRational){1,1000});actual[i].end=av_rescale_q(chapter->end,chapter->time_base,(AVRational){1,1000});AVDictionaryEntry *entry=av_dict_get(chapter->metadata,"title",NULL,0);if(entry)snprintf(actual[i].title,sizeof(actual[i].title),"%s",entry->value);}
        avos_mp_fillmetadata(mp,TYPE_VID,0,NULL,&av,NULL,12000,1,1,0,2,pointers,0);player->getmetadata(mp,&buffer);
        if(integer(buffer,AVOS_MP_METADATA_NB_CHAPTER)!=2||integer64(buffer,AVOS_MP_METADATA_CHAPTER)!=0||integer64(buffer,AVOS_MP_METADATA_CHAPTER+2)!=6000)return 7;
        avos_msg_t *actual_title=metadata->get(buffer,AVOS_MP_METADATA_CHAPTER+3);if(!actual_title||strcmp((const char*)actual_title->data,"Second chapter"))return 8;
        metadata->destroy(&buffer);avformat_close_input(&format);
        puts("PASS: actual FFmpeg demux of generated MKV chapters into the AVOS metadata parcel (0/6000ms)");
    }
    player->destroy(mp);puts("PASS: actual AVOS chapter parcel, origin conversion, titles, absent and over-limit tables");return 0;
}

#include <assert.h>
#include <dlfcn.h>
#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "libavformat/avformat.h"
#include "libavcodec/avcodec.h"
#include "sub_engine.h"
extern SUB_FORMAT_BACKEND *sub_format_ssa_create(void);
extern SUB_FORMAT_BACKEND *sub_format_srt_create(void);
extern SUB_FORMAT_BACKEND *sub_format_gfx_create(void);
static void *handle;
#define F(name) ((__typeof__(&name))dlsym(handle,#name))
static void release(SUB_FORMAT_BACKEND *be,SUB_FRAME *f){if(f)be->free_frame(be,f);}
int main(int argc,char **argv){
    assert(argc==2);const char *libraries[]={"libnvpnativehelper.so","libavos_android.so","libavos.so"};
    for(unsigned i=0;i<3;i++){void *h=dlopen(libraries[i],RTLD_NOW|RTLD_GLOBAL);if(!h){fprintf(stderr,"%s\n",dlerror());return 2;}if(i==2)handle=h;}
    AVFormatContext *format=NULL;assert(avformat_open_input(&format,argv[1],NULL,NULL)>=0);assert(avformat_find_stream_info(format,NULL)>=0);int index=av_find_best_stream(format,AVMEDIA_TYPE_SUBTITLE,-1,-1,NULL,0);assert(index>=0);
    AVCodecParameters *parameters=format->streams[index]->codecpar;const AVCodec *codec=avcodec_find_decoder(parameters->codec_id);assert(codec);AVCodecContext *context=avcodec_alloc_context3(codec);assert(context);assert(avcodec_parameters_to_context(context,parameters)>=0);assert(avcodec_open2(context,codec,NULL)>=0);
    int bitmap=parameters->codec_id==AV_CODEC_ID_HDMV_PGS_SUBTITLE;int styled=parameters->codec_id==AV_CODEC_ID_ASS||parameters->codec_id==AV_CODEC_ID_SSA;
    SUB_USER_STYLE *style=F(sub_style_create)();assert(style);SUB_FORMAT_BACKEND *backend=bitmap?F(sub_format_gfx_create)():styled?F(sub_format_ssa_create)():F(sub_format_srt_create)();assert(backend);
    SUB_FORMAT_OPEN_PARAMS options={.video_w=640,.video_h=360,.real_video_w=640,.real_video_h=360,.user_style=style,.fonts_dir=NULL,.default_font_name=NULL,.is_plain_text_format=!styled&&!bitmap};
    if(styled){options.codec_private=context->subtitle_header;options.codec_private_size=context->subtitle_header_size;}
    assert(!backend->open(backend,&options));AVPacket *packet=av_packet_alloc();assert(packet);unsigned rendered=0,cleared=0;
    while(av_read_frame(format,packet)>=0){
        if(packet->stream_index==index){AVSubtitle subtitle={0};int got=0;assert(avcodec_decode_subtitle2(context,&subtitle,&got,packet)>=0);
            if(got){
                if(bitmap&&subtitle.num_rects==0){assert(!backend->feed_bitmap(backend,NULL,1,1,4,0,0,0,3000,0));SUB_FRAME *frame=backend->render_at(backend,3500);assert(!frame||!frame->events);release(backend,frame);cleared++;}
                for(unsigned i=0;i<subtitle.num_rects;i++){
                    AVSubtitleRect *rectangle=subtitle.rects[i];
                    if(bitmap){assert(rectangle->w==20&&rectangle->h==10&&rectangle->x==100&&rectangle->y==200);uint8_t pixels[20*10*4];uint32_t *palette=(uint32_t*)rectangle->data[1];
                        for(int y=0;y<10;y++)for(int x=0;x<20;x++){uint32_t color=palette[rectangle->data[0][y*rectangle->linesize[0]+x]];unsigned at=(y*20+x)*4;pixels[at]=color>>16;pixels[at+1]=color>>8;pixels[at+2]=color;pixels[at+3]=color>>24;}
                        assert(!backend->feed_bitmap(backend,pixels,20,10,80,0,100,200,1000,-1));
                    }else{const char *text=rectangle->ass;assert(text);if(!styled){for(int n=0;n<8;n++){text=strchr(text,',');assert(text);text++;}}assert(!backend->feed(backend,(const uint8_t*)text,strlen(text),1000,2000));}
                    SUB_FRAME *frame=backend->render_at(backend,1500);assert(frame&&frame->events);unsigned nonzero=0;
                    for(SUB_EVENT *event=frame->events;event;event=event->next){assert(event->w>0&&event->h>0);const uint8_t *rgba=event->data.bitmap.rgba;for(int y=0;y<event->h;y++)for(int x=0;x<event->w;x++)nonzero+=rgba[y*event->data.bitmap.stride+x*4+3]!=0;}
                    assert(nonzero>10);release(backend,frame);rendered++;
                }
                avsubtitle_free(&subtitle);
            }
        }av_packet_unref(packet);
    }
    assert(rendered>0);if(bitmap)assert(cleared>0);else{SUB_FRAME *frame=backend->render_at(backend,3500);assert(!frame||!frame->events);release(backend,frame);cleared++;}
    assert(!backend->flush(backend));SUB_FRAME *empty=backend->render_at(backend,1500);assert(!empty||!empty->events);release(backend,empty);backend->close(backend);free(backend);F(sub_style_destroy)(style);av_packet_free(&packet);avcodec_free_context(&context);avformat_close_input(&format);
    printf("PASS codec=%s rendered=%u clear=%u seek-flush=empty\n",codec->name,rendered,cleared);return 0;
}

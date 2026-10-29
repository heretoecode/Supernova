#include <assert.h>
#include <stdio.h>
#include <stdint.h>
#include <stdlib.h>
#include <string.h>
#include <sys/resource.h>
#include "libavutil/avstring.h"
#include "libavutil/mem.h"
#include "libavutil/hdr_dynamic_metadata.h"
#include "libavutil/encryption_info.h"
int main(int argc,char **argv){
 assert(argc==2);
 if(!strcmp(argv[1],"empty-needle")){
  char *s=av_strireplace("media-$Number$.m4s","","segment");assert(s&&!strcmp(s,"media-$Number$.m4s"));av_free(s);
  s=av_strireplace("AbCaBc","abc","x");assert(s&&!strcmp(s,"xx"));av_free(s);
 }else if(!strcmp(argv[1],"hdr-roundtrip")){
  AVDynamicHDRPlus in={0},out={0};in.num_windows=1;in.targeted_system_display_maximum_luminance=(AVRational){1000,1};
  for(int k=0;k<3;k++)in.params[0].maxscl[k]=(AVRational){0,1};
  in.params[0].average_maxrgb=(AVRational){0,1};in.params[0].fraction_bright_pixels=(AVRational){0,1};
  in.params[0].tone_mapping_flag=0;in.params[0].color_saturation_mapping_flag=1;in.params[0].color_saturation_weight=(AVRational){3,8};
  size_t n=0;assert(av_dynamic_hdr_plus_to_t35(&in,NULL,&n)==0);uint8_t *p=av_malloc(n);assert(p);memset(p,0xAA,n);size_t actual=n;assert(av_dynamic_hdr_plus_to_t35(&in,&p,&actual)==0&&actual==n);
  assert(av_dynamic_hdr_plus_from_t35(&out,p,n)==0);assert(out.params[0].tone_mapping_flag==0&&out.params[0].color_saturation_mapping_flag==1);assert(av_cmp_q(out.params[0].color_saturation_weight,in.params[0].color_saturation_weight)==0);av_free(p);
 }else if(!strcmp(argv[1],"deep-free")){
  struct rlimit limit={512*1024,512*1024};assert(setrlimit(RLIMIT_STACK,&limit)==0);
  AVEncryptionInitInfo *first=NULL,*last=NULL;
  for(unsigned k=0;k<100000;k++){AVEncryptionInitInfo *p=av_encryption_init_info_alloc(0,0,0,0);assert(p);if(last)last->next=p;else first=p;last=p;}
  av_encryption_init_info_free(first);
 }else return 2;
 puts("PASS");return 0;
}

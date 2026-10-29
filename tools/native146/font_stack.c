#include <assert.h>
#include <dlfcn.h>
#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <fontconfig/fontconfig.h>
#include <ass/ass.h>
#include <ft2build.h>
#include FT_FREETYPE_H
#include FT_MULTIPLE_MASTERS_H
static void *handle;
#define F(name) ((__typeof__(&name))dlsym(handle,#name))
int main(void){
    const char *libraries[]={"libavutil.so","libopus.so","libdav1d.so","libmysofa.so","libswresample.so","libavcodec.so","libavformat.so","libswscale.so","libavfilter.so","libnvpnativehelper.so","libavos_android.so","libavos.so"};
    for(unsigned i=0;i<sizeof(libraries)/sizeof(libraries[0]);i++){void *loaded=dlopen(libraries[i],RTLD_NOW|RTLD_GLOBAL);if(!loaded){fprintf(stderr,"dlopen failed: %s\n",dlerror());return 3;}if(!strcmp(libraries[i],"libavos.so"))handle=loaded;}

    fprintf(stderr,"PHASE fontconfig init\n");
    FcConfig *config=F(FcInitLoadConfigAndFonts)();assert(config);FcPattern *pattern=F(FcPatternCreate)();assert(pattern);
    F(FcPatternAddString)(pattern,FC_FAMILY,(const FcChar8*)"Roboto Flex");F(FcConfigSubstitute)(config,pattern,FcMatchPattern);F(FcDefaultSubstitute)(pattern);FcResult result;
    fprintf(stderr,"PHASE fontconfig match\n");FcPattern *match=F(FcFontMatch)(config,pattern,&result);assert(match);FcChar8 *file=NULL;assert(F(FcPatternGetString)(match,FC_FILE,0,&file)==FcResultMatch);assert(file&&strstr((const char*)file,"RobotoFlex.ttf"));
    fprintf(stderr,"PHASE freetype\n");FT_Library ft;FT_Face face;FT_MM_Var *axes;assert(!F(FT_Init_FreeType)(&ft));assert(!F(FT_New_Face)(ft,(const char*)file,0,&face));assert(!F(FT_Get_MM_Var)(face,&axes));unsigned count=axes->num_axis+128;FT_Fixed *coords=malloc(count*sizeof(*coords));assert(coords);memset(coords,0x55,count*sizeof(*coords));assert(!F(FT_Get_Var_Design_Coordinates)(face,count,coords));for(unsigned i=axes->num_axis;i<count;i++)assert(coords[i]==0);free(coords);F(FT_Done_MM_Var)(ft,axes);F(FT_Done_Face)(face);F(FT_Done_FreeType)(ft);
    fprintf(stderr,"PHASE freetype cleanup\n");F(FcPatternDestroy)(match);F(FcPatternDestroy)(pattern);F(FcConfigDestroy)(config);
    fprintf(stderr,"PHASE libass init\n");ASS_Library *library=F(ass_library_init)();assert(library);ASS_Renderer *renderer=F(ass_renderer_init)(library);assert(renderer);F(ass_set_frame_size)(renderer,640,360);F(ass_set_fonts)(renderer,NULL,"Roboto Flex",ASS_FONTPROVIDER_FONTCONFIG,"/test/fonts.conf",1);
    char script[]="[Script Info]\nScriptType: v4.00+\nPlayResX: 640\nPlayResY: 360\n[V4+ Styles]\nFormat: Name, Fontname, Fontsize, PrimaryColour, SecondaryColour, OutlineColour, BackColour, Bold, Italic, Underline, StrikeOut, ScaleX, ScaleY, Spacing, Angle, BorderStyle, Outline, Shadow, Alignment, MarginL, MarginR, MarginV, Encoding\nStyle: Default,Roboto Flex,32,&H00FFFFFF,&H00FFFFFF,&H00000000,&H80000000,0,0,0,0,100,100,0,0,1,2,0,2,10,10,15,1\n[Events]\nFormat: Layer, Start, End, Style, Name, MarginL, MarginR, MarginV, Effect, Text\nDialogue: 0,0:00:00.00,0:00:02.00,Default,,0,0,0,,Supernova subtitle ABC\n";
    fprintf(stderr,"PHASE libass track\n");ASS_Track *track=F(ass_read_memory)(library,script,strlen(script),NULL);assert(track);int changed=0;ASS_Image *image=F(ass_render_frame)(renderer,track,1000,&changed);assert(image);unsigned images=0,covered=0;
    for(;image;image=image->next){assert(image->dst_x>=0&&image->dst_y>=0&&image->dst_x+image->w<=640&&image->dst_y+image->h<=360);images++;for(int y=0;y<image->h;y++)for(int x=0;x<image->w;x++)covered+=image->bitmap[y*image->stride+x]!=0;}
    assert(images&&covered>100);printf("PASS fontconfig SAX discovery; FreeType variation coordinates; libass glyph images=%u covered=%u\n",images,covered);
    F(ass_free_track)(track);F(ass_renderer_done)(renderer);F(ass_library_done)(library);F(FcFini)();return 0;
}

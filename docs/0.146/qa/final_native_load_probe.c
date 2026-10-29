#include <dlfcn.h>
#include <stdio.h>
int main(void){
 const char *libraries[]={"libdav1d.so","libavutil.so","libavcodec.so","libavformat.so","libswscale.so","libswresample.so","libavfilter.so","libdeinterlace.so","libaudiocompress.so","libsfdec.so","libavos_android.so","libavos.so","libavosjni.so","libfilecoreutils.so",0};
 for(int i=0;libraries[i];i++){void *handle=dlopen(libraries[i],RTLD_NOW|RTLD_GLOBAL);if(!handle){printf("FAIL %s %s\n",libraries[i],dlerror());return 1;}printf("PASS %s\n",libraries[i]);}return 0;
}

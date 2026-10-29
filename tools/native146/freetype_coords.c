#include <assert.h>
#include <stdlib.h>
#include <stdio.h>
#include <ft2build.h>
#include FT_FREETYPE_H
#include FT_MULTIPLE_MASTERS_H
int main(int argc,char **argv){
 assert(argc==2);FT_Library library;FT_Face face;FT_MM_Var *axes;
 assert(!FT_Init_FreeType(&library));assert(!FT_New_Face(library,argv[1],0,&face));assert(!FT_Get_MM_Var(face,&axes));assert(axes->num_axis>0);
 unsigned count=axes->num_axis+128;FT_Fixed *coords=malloc(count*sizeof(*coords));assert(coords);for(unsigned k=0;k<count;k++)coords[k]=-12345;
 assert(!FT_Get_Var_Design_Coordinates(face,count,coords));for(unsigned k=axes->num_axis;k<count;k++)assert(coords[k]==0);
 free(coords);FT_Done_MM_Var(library,axes);FT_Done_Face(face);FT_Done_FreeType(library);puts("PASS");
}

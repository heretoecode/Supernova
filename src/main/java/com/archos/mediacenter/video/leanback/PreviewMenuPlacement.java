package com.archos.mediacenter.video.leanback;

/** Pure geometry: a menu occupies safe space beside its anchor, with a deliberate gap. */
public final class PreviewMenuPlacement {
    private PreviewMenuPlacement(){}
    public static int[] place(int width,int height,int left,int top,int right,int bottom,int screenWidth,int screenHeight,int margin,int gap){
        int[][] spaces={{right+gap,margin,screenWidth-margin,screenHeight-margin},
                {margin,margin,left-gap,screenHeight-margin},
                {margin,bottom+gap,screenWidth-margin,screenHeight-margin},
                {margin,margin,screenWidth-margin,top-gap}};
        int[] best=null;long bestArea=-1;
        for(int[] space:spaces){int w=Math.min(width,space[2]-space[0]),h=Math.min(height,space[3]-space[1]);if(w<=0||h<=0)continue;
            long area=(long)w*h;if(area>bestArea){int x=Math.max(space[0],Math.min(left,space[2]-w));int y=Math.max(space[1],Math.min(top,space[3]-h));best=new int[]{x,y,w,h};bestArea=area;}if(w==width&&h==height)break;
        }
        // Full-screen anchors (rare, e.g. an accessibility root) have no adjacent area.
        return best==null?new int[]{margin,margin,Math.max(1,Math.min(width,screenWidth-2*margin)),Math.max(1,Math.min(height,screenHeight-2*margin))}:best;
    }
}

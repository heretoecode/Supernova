import com.archos.mediacenter.video.leanback.PreviewMenuPlacement;
import com.archos.mediacenter.video.diagnostics.DiagnosticFlightRecorder;
import java.util.Random;

/** Dependency-free regression checks; run with the production helper classes. */
public final class PreviewNextChecks {
    private static int checks;
    private static void check(boolean condition, String message) {
        checks++;
        if (!condition) throw new AssertionError(message);
    }
    public static void main(String[] args) {
        Random random = new Random(24092026L);
        for (int n = 0; n < 10000; n++) {
            int sw=960, sh=540, margin=24, gap=14;
            int left=24+random.nextInt(740), top=24+random.nextInt(400);
            int right=left+60+random.nextInt(130), bottom=Math.min(516,top+36+random.nextInt(60));
            int width=240+random.nextInt(200), height=90+random.nextInt(360);
            int[] menu=PreviewMenuPlacement.place(width,height,left,top,right,bottom,sw,sh,margin,gap);
            check(menu[2]>0 && menu[3]>0,"Positive dimensions");
            check(menu[0]>=margin && menu[1]>=margin && menu[0]+menu[2]<=sw-margin && menu[1]+menu[3]<=sh-margin,"Safe bounds");
            check(menu[0]>=right+gap || menu[0]+menu[2]<=left-gap || menu[1]>=bottom+gap || menu[1]+menu[3]<=top-gap,"Anchor gap");
        }
        int[] fallback=PreviewMenuPlacement.place(400,200,0,0,960,540,960,540,24,14);
        check(fallback[0]==24 && fallback[1]==24,"Full-screen anchor safe fallback");
        DiagnosticFlightRecorder ring=new DiagnosticFlightRecorder(8,100);
        ring.add(0,"abc\n");ring.add(50,"def\n");
        check(ring.bytes()==8,"Exact byte capacity");
        ring.add(60,"xyz\n");
        check(ring.snapshot(60).equals("def\nxyz\n"),"Oldest evicted on capacity");
        check(ring.evicted()==1,"Capacity eviction counted");
        check(ring.snapshot(151).equals("xyz\n"),"Monotonic time horizon");
        ring.clear();ring.add(200,"éé\n");
        check(ring.bytes()==5,"UTF-8 bytes, not character count");
        ring.add(210,"too large to retain");
        check(ring.bytes()==5 && ring.evicted()==1,"Oversized record bounded and counted");
        check(ring.snapshot(301).isEmpty() && ring.bytes()==0,"Expired records removed");
        ring.clear();check(ring.evicted()==0,"Clear resets self-health");
        boolean rejected=false;try{new DiagnosticFlightRecorder(0,1);}catch(IllegalArgumentException expected){rejected=true;}
        check(rejected,"Invalid budget rejected");
        System.out.println("PASS: "+checks+" dependency-free assertions (menu geometry and flight recorder)");
    }
}

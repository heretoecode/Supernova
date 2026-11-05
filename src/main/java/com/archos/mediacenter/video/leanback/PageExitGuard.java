package com.archos.mediacenter.video.leanback;
/** Hosted editors participate in the same navigation exit contract as staged browsers. */
public interface PageExitGuard {void requestExit(Runnable leave);}

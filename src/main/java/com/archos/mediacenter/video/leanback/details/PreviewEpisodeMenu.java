package com.archos.mediacenter.video.leanback.details;

import android.app.Activity;
import android.app.Dialog;
import android.net.Uri;
import android.view.View;

import com.archos.mediacenter.video.browser.Delete;
import com.archos.mediacenter.video.browser.adapters.object.Episode;
import com.archos.mediacenter.video.leanback.LibraryActions;
import com.archos.mediacenter.video.leanback.PreviewDialog;
import com.archos.mediacenter.video.leanback.UndoNotice;
import com.archos.mediacenter.video.leanback.scrapping.ManualVideoScrappingActivity;
import com.archos.mediacenter.video.player.PlayerActivity;
import com.archos.mediacenter.video.utils.PlayUtils;
import com.archos.mediaprovider.video.VideoStore;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * An episode card on Details (owner, 18 Nov 2026; docs/0.158/OWNER_NOTES.md §2): OK plays it at once; long-press or
 * the Menu button opens the same menu as More, beside the card, as library posters do. Deleting an episode when
 * earlier episodes of the season are in the library asks first which to delete (RULES §23.5: Keep file first).
 */
final class PreviewEpisodeMenu {
    private PreviewEpisodeMenu() {
    }

    /** OK on an episode card: plays it, from where you stopped. */
    static void play(Activity activity, Episode episode) {
        PlayUtils.startVideo(activity, episode, PlayerActivity.RESUME_FROM_LAST_POS, false, -1, null, -1);
    }

    /** The episode's menu beside its card: Playback · Library · Files & Media, as More. */
    static Dialog show(Activity activity, View card, Episode episode, List<Episode> season) {
        List<String> labels = new ArrayList<>();
        List<String> icons = new ArrayList<>();
        List<Runnable> commands = new ArrayList<>();
        boolean watched = com.archos.mediacenter.video.leanback.PreviewSeriesJourney.completed(episode);
        boolean resume = episode.getResumeMs() > 0 && !watched;
        add(labels, icons, commands, "— Playback", null, null);
        add(labels, icons, commands, resume ? "Resume" : "Play", resume ? "resume" : "play",
            () -> play(activity, episode));
        if (resume)
            add(labels, icons, commands, "Restart episode", "history",
                () -> PlayUtils.startVideo(activity, episode, PlayerActivity.RESUME_NO, false, -1, null, -1));
        add(labels, icons, commands, "— Library", null, null);
        add(labels, icons, commands, watched ? "Mark as unwatched" : "Mark as watched", "check", () -> {
            LibraryActions.markWatched(activity, episode, !watched);
            UndoNotice.show(activity, watched ? "Marked as unwatched" : "Marked as watched", () -> {
                LibraryActions.markWatched(activity, episode, watched);
                refresh(activity);
            });
            refresh(activity);
        });
        add(labels, icons, commands, "Fix match", "search",
            () -> activity.startActivity(new android.content.Intent(activity, ManualVideoScrappingActivity.class)
                                             .putExtra(ManualVideoScrappingActivity.EXTRA_VIDEO, episode)));
        add(labels, icons, commands, "— Files & Media", null, null);
        add(labels, icons, commands, "Delete episode", "delete", () -> delete(activity, episode, season));
        return PreviewDialog.menu(activity, card, name(episode), labels.toArray(new String[0]),
                                  icons.toArray(new String[0]), -1, n -> {
                                      if (commands.get(n) != null)
                                          commands.get(n).run();
                                  });
    }

    private static void add(List<String> labels, List<String> icons, List<Runnable> commands, String label,
                            String icon, Runnable command) {
        labels.add(label);
        icons.add(icon);
        commands.add(command);
    }

    static String name(Episode episode) {
        String title = episode.getEpisodeName();
        return "Episode " + episode.getEpisodeNumber() + (title == null || title.isEmpty() ? "" : " · " + title);
    }

    /** The episodes of the same season, in the library, that come before this one. */
    static List<Episode> earlier(Episode episode, List<Episode> season) {
        List<Episode> out = new ArrayList<>();
        for (Episode other : season)
            if (other != null && other.getId() != episode.getId()
                && other.getSeasonNumber() == episode.getSeasonNumber()
                && other.getEpisodeNumber() < episode.getEpisodeNumber() && other.getFileUri() != null)
                out.add(other);
        out.sort((a, b) -> Integer.compare(a.getEpisodeNumber(), b.getEpisodeNumber()));
        return out;
    }

    /** "1 and 2", "1, 2 and 3". */
    static String numbers(List<Episode> episodes) {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < episodes.size(); i++) {
            if (i > 0)
                out.append(i == episodes.size() - 1 ? " and " : ", ");
            out.append(episodes.get(i).getEpisodeNumber());
        }
        return out.toString();
    }

    private static final String[] COUNTS = {"", "one", "both", "all three", "all four", "all five", "all six",
                                            "all seven", "all eight", "all nine", "all ten"};

    /** "Delete both", "Delete all three" … "Delete all 14". */
    static String deleteAll(int count) {
        return "Delete " + (count < COUNTS.length ? COUNTS[count] : "all " + count);
    }

    /**
     * Delete episode: one warning. With earlier episodes of the season in the library it says so and offers
     * Keep file · Delete episode N only · Delete all · Choose…; nothing earlier goes without that step.
     */
    static void delete(Activity activity, Episode episode, List<Episode> season) {
        List<Episode> earlier = earlier(episode, season);
        if (earlier.isEmpty()) {
            PreviewDialog.warning(activity, "Delete " + name(episode) + "?",
                                  "The file is deleted from its drive or server. This can't be undone.", "Keep file",
                                  "Delete file", () -> deleteFiles(activity, single(episode)));
            return;
        }
        List<Episode> all = new ArrayList<>(earlier);
        all.add(episode);
        String message = "You also have episode" + (earlier.size() > 1 ? "s " : " ") + numbers(earlier)
            + " of this season. The files are deleted from their drive or server. This can't be undone.";
        String[] choices = {"Keep file", "Delete episode " + episode.getEpisodeNumber() + " only",
                            deleteAll(all.size()), "Choose…"};
        Dialog dialog = PreviewDialog.message(activity, "Delete " + name(episode) + "?", message, false, choices, 0,
                                              index -> {
                                                  if (index == 1)
                                                      deleteFiles(activity, single(episode));
                                                  else if (index == 2)
                                                      deleteFiles(activity, all);
                                                  else if (index == 3)
                                                      choose(activity, episode, all);
                                              });
        for (int i : new int[] {1, 2}) {
            android.widget.TextView remove =
                dialog.findViewById(android.R.id.content).findViewWithTag("semantic:dialog.action." + i);
            if (remove != null)
                remove.setTextColor(com.archos.mediacenter.video.leanback.SupernovaStyle.destructiveContent());
        }
    }

    private static List<Episode> single(Episode episode) {
        List<Episode> one = new ArrayList<>();
        one.add(episode);
        return one;
    }

    /** Choose…: the shared choice list with ticks, Select all at its top, then the same warning for what's ticked. */
    static Dialog choose(Activity activity, Episode episode, List<Episode> all) {
        String[] labels = new String[all.size() + 2];
        labels[0] = "Select all";
        for (int i = 0; i < all.size(); i++)
            labels[i + 1] = name(all.get(i));
        labels[labels.length - 1] = "Done";
        Set<Integer> ticked = new TreeSet<>();
        ticked.add(all.size());
        Dialog[] shown = new Dialog[1];
        shown[0] = PreviewDialog.choose(activity, "Choose episodes", labels, 1, ticked, false, index -> {
            if (index == labels.length - 1) {
                shown[0].dismiss();
                List<Episode> chosen = new ArrayList<>();
                for (int i : ticked)
                    chosen.add(all.get(i - 1));
                if (!chosen.isEmpty())
                    confirmChosen(activity, chosen);
                return;
            }
            if (index == 0) {
                boolean every = ticked.size() == all.size();
                ticked.clear();
                if (!every)
                    for (int i = 1; i <= all.size(); i++)
                        ticked.add(i);
            } else if (!ticked.remove(index))
                ticked.add(index);
            for (int i = 0; i < labels.length - 1; i++) {
                View check = shown[0].findViewById(android.R.id.content).findViewWithTag("preview-check:" + i);
                if (check != null)
                    check.setVisibility(i == 0 ? (ticked.size() == all.size() ? View.VISIBLE : View.INVISIBLE)
                                               : ticked.contains(i) ? View.VISIBLE : View.INVISIBLE);
            }
        });
        return shown[0];
    }

    private static void confirmChosen(Activity activity, List<Episode> chosen) {
        String title = chosen.size() == 1 ? "Delete " + name(chosen.get(0)) + "?"
                                          : "Delete episodes " + numbers(chosen) + "?";
        PreviewDialog.warning(activity, title, "The files are deleted from their drive or server. This can't be undone.",
                              chosen.size() == 1 ? "Keep file" : "Keep files",
                              chosen.size() == 1 ? "Delete file" : deleteAll(chosen.size()),
                              () -> deleteFiles(activity, chosen));
    }

    private static void deleteFiles(Activity activity, List<Episode> episodes) {
        List<Uri> files = new ArrayList<>();
        for (Episode e : episodes)
            if (e.getFileUri() != null)
                files.add(e.getFileUri());
        if (files.isEmpty())
            return;
        Delete delete = new Delete(new Delete.DeleteListener() {
            public void onVideoFileRemoved(Uri videoFile, boolean askForFolderRemoval, Uri folderToDelete) {
                refresh(activity);
            }

            public void onDeleteVideoFailed(Uri videoFile) {
                com.archos.mediacenter.video.leanback.PreviewNotice.show(activity, "Couldn't delete the file", true);
            }

            public void onFolderRemoved(Uri folder) {
            }

            public void onDeleteSuccess() {
                refresh(activity);
            }
        }, activity);
        if (files.size() == 1)
            delete.startDeleteProcess(files.get(0));
        else
            delete.startMultipleDeleteProcess(files);
    }

    private static void refresh(Activity activity) {
        activity.getContentResolver().notifyChange(VideoStore.Video.Media.EXTERNAL_CONTENT_URI, null);
    }
}

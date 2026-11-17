package com.archos.mediacenter.video.leanback;
import android.os.Bundle;
import android.content.Intent;
import android.database.Cursor;
import android.view.*;
import android.widget.*;
import androidx.activity.OnBackPressedCallback;
import com.archos.mediacenter.video.diagnostics.Diagnostics;
import com.archos.mediacenter.video.leanback.PreviewLibraryLoader.*;
import com.archos.mediacenter.video.leanback.search.VideoSearchActivity;
import com.archos.mediacenter.video.leanback.settings.VideoSettingsActivity;
import java.util.concurrent.*;

/**
 * Your own library page (page sheet LIBRARY.md), backed by the ordinary library snapshot: the same page and code as
 * Movies and TV Shows. Before it is set up it says "Make this page your own"; Customise page opens the floating side
 * panel on the right, and every change saves and shows at once (no Save button; Back closes the panel). The old Edit
 * Page panel never opens from here.
 */
public final class CustomLibraryActivity extends LeanbackActivity {
    static final String NEW_PAGE_NAME = "New Page";
    private Workspace workspace;
    private TopNavigation navigation;
    private final ExecutorService worker = Executors.newSingleThreadExecutor();
    private volatile boolean closed;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        workspace = new Workspace();
        navigation = new TopNavigation(this, workspace, this::navigate, () -> workspace.atTop());
        navigation.selectTab(6);
        setContentView(navigation);
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            public void handleOnBackPressed() {
                finish();
            }
        });
        Snapshot cache = PreviewLibraryLoader.memoryCache();
        if (cache != null)
            workspace.snapshot = cache;
        workspace.freshEntry = state == null;
        if (state != null)
            workspace.restoreFocus = state.getString("focus", "");
        workspace.render();
        PreviewLibraryLoader loader = new PreviewLibraryLoader(this);
        worker.execute(() -> {
            try (Cursor cursor = loader.loadInBackground()) {
                Snapshot library = loader.snapshot;
                runOnUiThread(() -> {
                    if (!closed)
                        applySnapshot(library);
                });
            } catch (RuntimeException failure) {
                Diagnostics.error("custom_library_load", failure);
            }
        });
    }

    /** A snapshot update is a data refresh, not a new library view lifecycle. */
    void applySnapshot(Snapshot library) {
        workspace.snapshot = library;
        if (workspace.sharedLibrary != null)
            workspace.sharedLibrary.setSnapshot(library);
    }

    private void navigate(int index) {
        if (index == 6)
            return;
        if (index == 4)
            startActivity(new Intent(this, VideoSettingsActivity.class));
        else if (index == 5)
            startActivity(new Intent(this, VideoSearchActivity.class));
        else {
            startActivity(new Intent(this, MainActivityLeanback.class)
                              .putExtra("preview_tab", index)
                              .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            finish();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle state) {
        workspace.rememberFocus();
        state.putString("focus", workspace.restoreFocus);
        super.onSaveInstanceState(state);
    }

    @Override
    protected void onDestroy() {
        closed = true;
        worker.shutdownNow();
        workspace.closePanel();
        super.onDestroy();
    }

    /** The open Customise page panel, or null (tests). */
    android.app.Dialog customisePanel() {
        return workspace.panel;
    }

    private final class Workspace extends FrameLayout implements TopNavigation.ContentFocus {
        CustomLibraryPage draft;
        Snapshot snapshot = new Snapshot();
        LinearLayout libraryBody;
        String restoreFocus = "";
        PreviewPages sharedLibrary;
        android.app.Dialog panel;
        /** True only for the first library mount of a newly opened page (not a restore or panel return). */
        boolean freshEntry;

        Workspace() {
            super(CustomLibraryActivity.this);
            CustomLibraryPage saved = CustomLibraryPage.load(getContext());
            draft = saved != null ? saved : fresh();
            getViewTreeObserver().addOnGlobalFocusChangeListener((oldFocus, newFocus) -> {
                if (navigation == null || newFocus == null)
                    return;
                View target = newFocus;
                while (target != this && !(target.getTag() instanceof String) && target.getParent() instanceof View)
                    target = (View)target.getParent();
                if (!(target.getTag() instanceof String))
                    return;
                for (Entry entry : draft.entries(snapshot))
                    if (entry.key().equals(target.getTag())) {
                        navigation.setArtwork(entry.backdrop);
                        break;
                    }
            });
        }

        private CustomLibraryPage fresh() {
            CustomLibraryPage page = new CustomLibraryPage();
            page.name = NEW_PAGE_NAME;
            return page;
        }

        boolean unset() {
            return CustomLibraryPage.load(getContext()) == null;
        }

        @Override
        public boolean focusFromNavigation() {
            return sharedLibrary != null && sharedLibrary.focusFromNavigation();
        }

        boolean atTop() {
            return sharedLibrary == null || sharedLibrary.atTop();
        }

        void rememberFocus() {
            View focused = findFocus();
            if (focused != null && focused.getTag() instanceof String)
                restoreFocus = (String)focused.getTag();
        }

        void render() {
            rememberFocus();
            removeAllViews();
            library();
            navigation.updateCustomPage();
        }

        /** Customise page: the floating panel; each change saves the page and shows it at once. */
        void customise() {
            closePanel();
            panel = LibraryCustomisePanel.show(getContext(), sharedLibrary.libraryHost(this::saveDraft, this::delete));
            panel.setOnDismissListener(d -> {
                panel = null;
                if (sharedLibrary != null)
                    sharedLibrary.focusCustomControl(unset() ? "empty" : "edit");
            });
        }

        void closePanel() {
            if (panel != null) {
                android.app.Dialog open = panel;
                panel = null;
                open.dismiss();
            }
        }

        private void saveDraft() {
            if (draft.validation() != null)
                return;
            boolean wasUnset = unset();
            if (!draft.save(getContext()))
                return;
            if (wasUnset)
                sharedLibrary.setCustomUnset(false);
            sharedLibrary.configureCustomLibrary(draft, this::customise);
            navigation.updateCustomPage();
        }

        /** Delete page: the delete warning ("Keep page" first); the page goes back to "Make this page your own". */
        void delete() {
            PreviewDialog.warning(
                getContext(), "Delete this page?",
                "Removes this page only. Your videos, watched state and progress are never deleted.", "Keep page",
                "Delete page", () -> {
                    if (CustomLibraryPage.delete(getContext())) {
                        closePanel();
                        draft = fresh();
                        sharedLibrary.configureCustomLibrary(draft, this::customise);
                        sharedLibrary.setCustomUnset(true);
                        navigation.updateCustomPage();
                    } else
                        PreviewDialog.read(getContext(), "Page not deleted", "Try again from the page.");
                });
        }

        void library() {
            setBackground(null);
            setPadding(0, 0, 0, 0);
            libraryBody = new LinearLayout(getContext());
            libraryBody.setOrientation(LinearLayout.VERTICAL);
            addView(libraryBody, new FrameLayout.LayoutParams(-1, -1));
            if (sharedLibrary == null) {
                sharedLibrary = new PreviewPages(getContext(),
                                                 (holder, item)
                                                     -> new VideoViewClickedListener(CustomLibraryActivity.this)
                                                            .onItemClicked(holder, item, null, null));
                sharedLibrary.configureCustomLibrary(draft, this::customise);
                sharedLibrary.setCustomUnset(unset());
                sharedLibrary.setArtworkListener(uri -> {
                    if (navigation != null)
                        navigation.setArtwork(uri);
                });
                // Same charcoal fade under the top bar as Home, Movies and TV Shows, driven by the real scroll.
                sharedLibrary.setScrollOffsetListener(offset -> {
                    if (navigation != null)
                        navigation.setScrollOffset(offset);
                });
            }
            if (sharedLibrary.getParent() instanceof ViewGroup)
                ((ViewGroup)sharedLibrary.getParent()).removeView(sharedLibrary);
            sharedLibrary.setSnapshot(snapshot);
            libraryBody.addView(sharedLibrary, new LinearLayout.LayoutParams(-1, -1));
            if (freshEntry) {
                // Opening the page from the top bar behaves like Movies / TV Shows: focus stays on this page's own
                // name in the bar; Down enters the page through TopNavigation's content-focus route.
                freshEntry = false;
                navigation.post(navigation::focusSelectedTab);
            } else {
                sharedLibrary.focusFromNavigation();
            }
            if (restoreFocus != null && restoreFocus.startsWith("custom.control.")) {
                String control = restoreFocus.substring("custom.control.".length());
                sharedLibrary.post(() -> sharedLibrary.focusCustomControl(control));
            }
        }
    }
}

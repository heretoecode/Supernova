# Complete browser/picker callsite inventory (preimplementation)

Generated from all application Java sources and reviewed with the manifest. Includes imports, definitions, selectors and launch sites so no entry route is silently omitted. System document pickers are retained for capability grants/export/import. Indexed-media queries and portrait/artwork selectors are semantically distinct from storage browsing.

### src/main/java/com/archos/mediacenter/video/browser/BrowserByVideoObjects.java

```text
51: import com.archos.mediacenter.video.browser.filebrowsing.network.BrowserByNetwork;
309: markAsRead(info.position, true, mPreferences.getBoolean(BrowserByNetwork.KEY_NETWORK_BOOKMARKS, true));
311: markAsNotRead(info.position, true, mPreferences.getBoolean(BrowserByNetwork.KEY_NETWORK_BOOKMARKS, true));
```

### src/main/java/com/archos/mediacenter/video/browser/BrowserCategoryVideo.java

```text
43: import com.archos.mediacenter.video.browser.filebrowsing.BrowserByExtStorage;
44: import com.archos.mediacenter.video.browser.filebrowsing.BrowserByVideoFolder;
234: Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
307: fragmentClass = BrowserByVideoFolder.class;
355: fragmentClass = BrowserByExtStorage.class;
```

### src/main/java/com/archos/mediacenter/video/browser/MainActivity.java

```text
97: import com.archos.mediacenter.video.browser.filebrowsing.BrowserByVideoFolder;
1077: public void reloadBrowserByVideoFolder() {
1079: Fragment f = new BrowserByVideoFolder();
```

### src/main/java/com/archos/mediacenter/video/browser/VideoPicker.java

```text
49: public class VideoPicker extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener {
51: private static final String TAG = "VideoPicker";
99: if (Intent.ACTION_GET_CONTENT.equals(getIntent().getAction())) {
224: private final WeakReference<VideoPicker> mActivityRef;
226: public QueryHandler(Context context, VideoPicker activity) {
233: VideoPicker activity = mActivityRef.get();
380: + " from: " + VideoPicker.this.mCursor);
382: VideoPicker.this.mCursor = cursor;
```

### src/main/java/com/archos/mediacenter/video/browser/filebrowsing/BrowserByExtStorage.java

```text
44: public class BrowserByExtStorage extends BrowserByLocalFolder {
121: Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
```

### src/main/java/com/archos/mediacenter/video/browser/filebrowsing/BrowserByFolder.java

```text
91: abstract public class BrowserByFolder extends BrowserByVideoObjects implements
94: private static final Logger log = LoggerFactory.getLogger(BrowserByFolder.class);
124: static final String SORT_PARAM_KEY = BrowserByFolder.class.getSimpleName() + "_SORT";
```

### src/main/java/com/archos/mediacenter/video/browser/filebrowsing/BrowserByLocalFolder.java

```text
31: public abstract class BrowserByLocalFolder extends BrowserByFolder {
33: private static final String TAG = "BrowserByLocalFolder";
```

### src/main/java/com/archos/mediacenter/video/browser/filebrowsing/BrowserByUsb.java

```text
23: public class BrowserByUsb extends BrowserByFolder {
```

### src/main/java/com/archos/mediacenter/video/browser/filebrowsing/BrowserByVideoFolder.java

```text
38: import com.archos.mediacenter.video.utils.FolderPicker;
43: public class BrowserByVideoFolder extends BrowserByLocalFolder {
45: private static final String TAG = "BrowserByVideoFolder";
52: String newPath = result.getData().getStringExtra(FolderPicker.EXTRA_SELECTED_FOLDER);
53: if (DBG) Log.d(TAG, "FolderPicker returns " + newPath);
61: bav.reloadBrowserByVideoFolder();
92: Intent i = new Intent(mContext, FolderPicker.class);
93: i.putExtra(FolderPicker.EXTRA_CURRENT_SELECTION, getDefaultDirectory().getPath());
94: i.putExtra(FolderPicker.EXTRA_DIALOG_TITLE, getResources().getString(R.string.menu_change_folder_details));
```

### src/main/java/com/archos/mediacenter/video/browser/filebrowsing/network/BrowserByNetwork.java

```text
50: import com.archos.mediacenter.video.browser.filebrowsing.BrowserByFolder;
63: public class BrowserByNetwork extends BrowserByFolder {
65: private static final Logger log = LoggerFactory.getLogger(BrowserByNetwork.class);
68: private static final String TAG = BrowserByNetwork.class.getCanonicalName();
385: private final WeakReference<BrowserByNetwork> mBrowserRef;
387: HelpOverlayHandler(BrowserByNetwork browser) {
394: BrowserByNetwork browser = mBrowserRef.get();
```

### src/main/java/com/archos/mediacenter/video/browser/filebrowsing/network/FtpBrowser/BrowserBySFTP.java

```text
45: import com.archos.mediacenter.video.browser.filebrowsing.network.BrowserByNetwork;
50: public class BrowserBySFTP extends BrowserByNetwork implements ListingEngine.Listener,LoaderManager.LoaderCallbacks<Cursor> {
```

### src/main/java/com/archos/mediacenter/video/browser/filebrowsing/network/FtpBrowser/FtpRootFragment.java

```text
126: args.putParcelable(BrowserBySFTP.CURRENT_DIRECTORY, uri);
127: Fragment f = new BrowserBySFTP();
```

### src/main/java/com/archos/mediacenter/video/browser/filebrowsing/network/NewRootFragment.java

```text
45: import com.archos.mediacenter.video.browser.filebrowsing.network.FtpBrowser.BrowserBySFTP;
46: import com.archos.mediacenter.video.browser.filebrowsing.network.SmbBrowser.BrowserBySmb;
47: import com.archos.mediacenter.video.browser.filebrowsing.network.UpnpBrowser.BrowserByUpnp;
77: args.putParcelable(BrowserByNetwork.CURRENT_DIRECTORY, uri);
78: args.putString(BrowserByNetwork.TITLE
80: args.putString(BrowserByNetwork.SHARE_NAME, lastPathSegment);
84: f = new BrowserBySmb();
87: f = new BrowserByUpnp();
90: f = new BrowserBySFTP();
```

### src/main/java/com/archos/mediacenter/video/browser/filebrowsing/network/ShortcutRootFragment.java

```text
34: import com.archos.mediacenter.video.browser.filebrowsing.network.FtpBrowser.BrowserBySFTP;
36: import com.archos.mediacenter.video.browser.filebrowsing.network.SmbBrowser.BrowserBySmb;
37: import com.archos.mediacenter.video.browser.filebrowsing.network.UpnpBrowser.BrowserByUpnp;
170: f = new BrowserBySmb();
172: args.putParcelable(BrowserByNetwork.CURRENT_DIRECTORY, uri);
173: args.putString(BrowserByNetwork.TITLE
175: args.putString(BrowserByNetwork.SHARE_NAME, lastPathSegment);
177: f = new BrowserByUpnp();
179: f = new BrowserBySFTP();
180: args.putParcelable(BrowserBySFTP.CURRENT_DIRECTORY, uri);
```

### src/main/java/com/archos/mediacenter/video/browser/filebrowsing/network/SmbBrowser/BrowserBySmb.java

```text
34: import com.archos.mediacenter.video.browser.filebrowsing.network.BrowserByNetwork;
41: public class BrowserBySmb extends BrowserByNetwork {
136: Log.e("BrowserBySmb", "displayConnectionDescription: user string not found in description");
```

### src/main/java/com/archos/mediacenter/video/browser/filebrowsing/network/UpnpBrowser/BrowserByUpnp.java

```text
23: import com.archos.mediacenter.video.browser.filebrowsing.network.BrowserByNetwork;
32: public class BrowserByUpnp extends BrowserByNetwork {
34: private static final Logger log = LoggerFactory.getLogger(BrowserByUpnp.class);
```

### src/main/java/com/archos/mediacenter/video/browser/filebrowsing/network/UpnpSmbCommonRootFragment.java

```text
25: import com.archos.mediacenter.video.browser.filebrowsing.network.SmbBrowser.BrowserBySmb;
26: import com.archos.mediacenter.video.browser.filebrowsing.network.UpnpBrowser.BrowserByUpnp;
45: args.putParcelable(BrowserByNetwork.CURRENT_DIRECTORY, uri);
46: args.putString(BrowserByNetwork.TITLE, share.getName());
47: args.putString(BrowserByNetwork.SHARE_NAME, FileUtils.getName(uri));
50: f = new BrowserBySmb();
53: f = new BrowserByUpnp();
```

### src/main/java/com/archos/mediacenter/video/browser/tools/MultipleSelectionManager.java

```text
43: import com.archos.mediacenter.video.browser.filebrowsing.network.BrowserByNetwork;
219: mBrowser.markAsRead(i-firstPosition, true, mPreferences.getBoolean(BrowserByNetwork.KEY_NETWORK_BOOKMARKS, true));
233: mBrowser.markAsNotRead(i-firstPosition, true, mPreferences.getBoolean(BrowserByNetwork.KEY_NETWORK_BOOKMARKS, true));
```

### src/main/java/com/archos/mediacenter/video/diagnostics/DiagnosticExportActivity.java

```text
13: Intent create=new Intent(Intent.ACTION_CREATE_DOCUMENT).addCategory(Intent.CATEGORY_OPENABLE)
```

### src/main/java/com/archos/mediacenter/video/info/VideoInfoActivityFragment.java

```text
93: import com.archos.mediacenter.video.browser.filebrowsing.BrowserByFolder;
2158: getActivity().setResult(BrowserByFolder.RESULT_FILE_DELETED, intent);
```

### src/main/java/com/archos/mediacenter/video/leanback/MainFragment.java

```text
91: import com.archos.mediacenter.video.leanback.filebrowsing.ExtStorageListingActivity;
92: import com.archos.mediacenter.video.leanback.filebrowsing.LocalListingActivity;
2053: vActivity.startActivity(new Intent(vActivity, LocalListingActivity.class));
2055: Intent i = new Intent(vActivity, ExtStorageListingActivity.class);
2056: i.putExtra(ExtStorageListingActivity.MOUNT_POINT, box.getPath());
2057: i.putExtra(ExtStorageListingActivity.STORAGE_NAME, box.getName());
```

### src/main/java/com/archos/mediacenter/video/leanback/PreviewNetworkWorkspace.java

```text
9: import com.archos.mediacenter.video.leanback.filebrowsing.ListingActivity;
83: action("browse", "Browse", () -> getContext().startActivity(new Intent(getContext(), ListingActivity.getActivityForUri(source.getUri())).putExtra(ListingActivity.EXTRA_ROOT_URI, source.getUri()).putExtra(ListingActivity.EXTRA_ROOT_NAME, source.getName())));
```

### src/main/java/com/archos/mediacenter/video/leanback/collections/CollectionFragment.java

```text
90: import com.archos.mediacenter.video.leanback.filebrowsing.ListingActivity;
358: if (activity != null) activity.setResult(ListingActivity.RESULT_FILE_DELETED, intent);
```

### src/main/java/com/archos/mediacenter/video/leanback/details/VideoDetailsFragment.java

```text
114: import com.archos.mediacenter.video.leanback.filebrowsing.ListingActivity;
2422: if (getActivity() != null) getActivity().setResult(ListingActivity.RESULT_FILE_DELETED, intent);
```

### src/main/java/com/archos/mediacenter/video/leanback/filebrowsing/ExtStorageListingActivity.java

```text
19: public class ExtStorageListingActivity extends ListingActivity {
25: protected ListingFragment getStartingFragment() {
26: return new LocalListingFragment();
```

### src/main/java/com/archos/mediacenter/video/leanback/filebrowsing/ListingActivity.java

```text
35: import com.archos.mediacenter.video.leanback.network.ftp.FtpListingActivity;
36: import com.archos.mediacenter.video.leanback.network.smb.SmbListingActivity;
37: import com.archos.mediacenter.video.leanback.network.smbj.SmbjListingActivity;
38: import com.archos.mediacenter.video.leanback.network.sshj.SshjListingActivity;
39: import com.archos.mediacenter.video.leanback.network.upnp.UpnpListingActivity;
40: import com.archos.mediacenter.video.leanback.network.webdav.WebdavListingActivity;
42: public abstract  class ListingActivity extends SingleFragmentActivity {
74: abstract protected ListingFragment getStartingFragment();
95: return LocalListingActivity.class;
98: if (isSMBjEnabled()) return SmbjListingActivity.class;
99: else return SmbListingActivity.class;
102: return UpnpListingActivity.class;
105: return WebdavListingActivity.class;
108: return WebdavListingActivity.class;
111: return SmbjListingActivity.class;
114: if (isSSHjEnabled()) return SshjListingActivity.class;
115: else return FtpListingActivity.class;
118: return SshjListingActivity.class;
121: return FtpListingActivity.class;
178: ListingFragment frag = getStartingFragment();
180: args.putParcelable(ListingFragment.ARG_URI, (Parcelable)getStartingUri());
181: args.putString(ListingFragment.ARG_TITLE, getStartingName());
182: args.putBoolean(ListingFragment.ARG_IS_ROOT, true); // this is the first fragment in the activity
184: args.putBoolean(ListingFragment.ARG_CREDENTIALS_JUST_PROVIDED,
212: MultiBackHintManager.getInstance(ListingActivity.this).onBackLongPressed();
217: MultiBackHintManager.getInstance(ListingActivity.this).onBackPressed();
237: if (frag instanceof ListingFragment) {
238: ((ListingFragment) frag).onFileDelete(file);
```

### src/main/java/com/archos/mediacenter/video/leanback/filebrowsing/ListingFragment.java

```text
94: public abstract class ListingFragment extends MyVerticalGridFragment implements ListingEngine.Listener, LoaderManager.LoaderCallbacks<Cursor> {
96: private static final Logger log = LoggerFactory.getLogger(ListingFragment.class);
106: if (result.getResultCode() == ListingActivity.RESULT_FILE_DELETED && result.getData() != null) {
108: ((ListingActivity) requireActivity()).notifyFileDeleted(deletedFile);
126: public static final String SORT_PARAM_KEY = ListingFragment.class.getSimpleName() + "_SORT";
150: private PreviewBrowserSurface previewSurface;
178: protected abstract ListingFragment instantiateNewFragment();
269: previewSurface=new PreviewBrowserSurface(requireActivity(),v,mUri,getTitleView(),this::previewOptions);
334: //LoaderManager.getInstance(ListingFragment.this).restartLoader(0, args, ListingFragment.this);
588: ListingFragment newFragment = instantiateNewFragment();
```

### src/main/java/com/archos/mediacenter/video/leanback/filebrowsing/LocalListingActivity.java

```text
22: public class LocalListingActivity extends ListingActivity {
29: protected ListingFragment getStartingFragment() {
30: return new LocalListingFragment();
```

### src/main/java/com/archos/mediacenter/video/leanback/filebrowsing/LocalListingFragment.java

```text
37: public class LocalListingFragment extends ListingFragment {
39: private static final String TAG = "LocalListingFragment";
45: protected  ListingFragment instantiateNewFragment() {
46: return new LocalListingFragment();
140: ArchosUtils.addBreadcrumb(SentryLevel.INFO, "LocalListingFragment.createBlacklisted", "scan request VideoStoreImportService intent action ACTION_VIDEO_SCANNER_METADATA_UPDATE");
161: ArchosUtils.addBreadcrumb(SentryLevel.INFO, "LocalListingFragment.deleteBlacklisted", "remove video from this directoty VideoStoreImportService intent action ACTION_VIDEO_SCANNER_METADATA_UPDATE");
```

### src/main/java/com/archos/mediacenter/video/leanback/filebrowsing/PreviewBrowserSurface.java

```text
16: public final class PreviewBrowserSurface extends BrowseFrameLayout {
22: PreviewBrowserSurface(Activity activity,View legacy,Uri uri,View titleCommands,Runnable options){
26: public PreviewBrowserSurface(Activity activity,View content,Uri uri,Runnable options){
29: private PreviewBrowserSurface(Activity activity,View legacy,Uri uri,View titleCommands,Runnable options,boolean suppliedDock){
38: if(uri.getScheme()!=null&&!uri.getScheme().equals("file"))rail.addView(control("Local Storage",()->activity.startActivity(new Intent(activity,LocalListingActivity.class))),new LinearLayout.LayoutParams(-1,dp(42)));
```

### src/main/java/com/archos/mediacenter/video/leanback/network/NetworkListingFragment.java

```text
29: import com.archos.mediacenter.video.leanback.filebrowsing.ListingFragment;
38: public class NetworkListingFragment extends ListingFragment {
40: private static final Logger log = LoggerFactory.getLogger(NetworkListingFragment.class);
53: protected  ListingFragment instantiateNewFragment() {
54: return new NetworkListingFragment();
```

### src/main/java/com/archos/mediacenter/video/leanback/network/NetworkRootFragment.java

```text
66: import com.archos.mediacenter.video.leanback.filebrowsing.ListingActivity;
361: Intent intent = new Intent(getActivity(), ListingActivity.getActivityForUri(uri));
362: if (log.isDebugEnabled()) log.debug("onItemClicked: NetworkBrowse ListingActivity root uri={}, root name={}", uri, uri.getHost());
363: intent.putExtra(ListingActivity.EXTRA_ROOT_URI, uri);
365: intent.putExtra(ListingActivity.EXTRA_ROOT_NAME, (shareName==null || shareName.isEmpty())?uri.getHost():shareName);
366: intent.putExtra(ListingActivity.EXTRA_CREDENTIALS_JUST_PROVIDED, true);
398: if (log.isDebugEnabled()) log.debug("onItemClicked: SmbShare ListingActivity root uri={}, root name={}", uri, uri.getHost());
399: Intent intent = new Intent(getActivity(), ListingActivity.getActivityForUri(uri));
400: intent.putExtra(ListingActivity.EXTRA_ROOT_URI, uri);
401: intent.putExtra(ListingActivity.EXTRA_ROOT_NAME, share.getName());
411: if (log.isDebugEnabled()) log.debug("onItemClicked: UpnpServer ListingActivity root uri={}, root name={}", uri, uri.getHost());
412: Intent intent = new Intent(getActivity(), ListingActivity.getActivityForUri(uri));
413: intent.putExtra(ListingActivity.EXTRA_ROOT_URI, uri);
414: intent.putExtra(ListingActivity.EXTRA_ROOT_NAME, server.getName());
```

### src/main/java/com/archos/mediacenter/video/leanback/network/NetworkShortcutDetailsFragment.java

```text
48: import com.archos.mediacenter.video.leanback.filebrowsing.ListingActivity;
77: // If the shortcut has been modified (removed) from the NetworkListingActivity launched by ACTION_OPEN,
291: Intent intent = new Intent(getActivity(), ListingActivity.getActivityForUri(mShortcut.getUri()));
292: intent.putExtra(ListingActivity.EXTRA_ROOT_URI, mShortcut.getUri());
293: intent.putExtra(ListingActivity.EXTRA_ROOT_NAME, mShortcut.getName());
```

### src/main/java/com/archos/mediacenter/video/leanback/network/PreviewSourceManagement.java

```text
11: import com.archos.mediacenter.video.leanback.filebrowsing.ListingActivity;
29: TextView open=button(a,"Open / Browse",()->a.startActivity(new Intent(a,ListingActivity.getActivityForUri(source.getUri())).putExtra(ListingActivity.EXTRA_ROOT_URI,source.getUri()).putExtra(ListingActivity.EXTRA_ROOT_NAME,source.getName())));actions.addView(open);
```

### src/main/java/com/archos/mediacenter/video/leanback/network/ftp/FtpListingActivity.java

```text
17: import com.archos.mediacenter.video.leanback.filebrowsing.ListingActivity;
18: import com.archos.mediacenter.video.leanback.filebrowsing.ListingFragment;
20: public class FtpListingActivity extends ListingActivity {
23: protected ListingFragment getStartingFragment() {
24: return new FtpListingFragment();
```

### src/main/java/com/archos/mediacenter/video/leanback/network/ftp/FtpListingFragment.java

```text
30: import com.archos.mediacenter.video.browser.filebrowsing.network.FtpBrowser.BrowserBySFTP;
31: import com.archos.mediacenter.video.leanback.filebrowsing.ListingFragment;
32: import com.archos.mediacenter.video.leanback.network.NetworkListingFragment;
40: public class FtpListingFragment extends NetworkListingFragment {
42: private static final String TAG = "FtpListingFragment";
70: protected  ListingFragment instantiateNewFragment() {
71: return new FtpListingFragment();
79: mHandler.sendEmptyMessageDelayed(LONG_CONNECTION, BrowserBySFTP.LONG_CONNECTION_DELAY);
```

### src/main/java/com/archos/mediacenter/video/leanback/network/smb/SmbListingActivity.java

```text
17: import com.archos.mediacenter.video.leanback.filebrowsing.ListingActivity;
18: import com.archos.mediacenter.video.leanback.filebrowsing.ListingFragment;
21: public class SmbListingActivity extends ListingActivity {
24: protected ListingFragment getStartingFragment() {
25: return new SmbListingFragment();
```

### src/main/java/com/archos/mediacenter/video/leanback/network/smb/SmbListingFragment.java

```text
30: import com.archos.mediacenter.video.leanback.filebrowsing.ListingFragment;
31: import com.archos.mediacenter.video.leanback.network.NetworkListingFragment;
42: public class SmbListingFragment extends NetworkListingFragment {
44: private static final Logger log = LoggerFactory.getLogger(SmbListingFragment.class);
49: protected  ListingFragment instantiateNewFragment() {
50: return new SmbListingFragment();
```

### src/main/java/com/archos/mediacenter/video/leanback/network/smbj/SmbjListingActivity.java

```text
17: import com.archos.mediacenter.video.leanback.filebrowsing.ListingActivity;
18: import com.archos.mediacenter.video.leanback.filebrowsing.ListingFragment;
20: import com.archos.mediacenter.video.leanback.network.smb.SmbListingFragment;
22: public class SmbjListingActivity extends ListingActivity {
25: protected ListingFragment getStartingFragment() {
26: return new SmbListingFragment();
```

### src/main/java/com/archos/mediacenter/video/leanback/network/sshj/SshjListingActivity.java

```text
17: import com.archos.mediacenter.video.leanback.filebrowsing.ListingActivity;
18: import com.archos.mediacenter.video.leanback.filebrowsing.ListingFragment;
20: import com.archos.mediacenter.video.leanback.network.NetworkListingFragment;
22: public class SshjListingActivity extends ListingActivity {
25: protected ListingFragment getStartingFragment() {
26: return new NetworkListingFragment();
```

### src/main/java/com/archos/mediacenter/video/leanback/network/upnp/UpnpListingActivity.java

```text
17: import com.archos.mediacenter.video.leanback.filebrowsing.ListingActivity;
18: import com.archos.mediacenter.video.leanback.filebrowsing.ListingFragment;
20: public class UpnpListingActivity extends ListingActivity {
23: protected ListingFragment getStartingFragment() {
24: return new UpnpListingFragment();
```

### src/main/java/com/archos/mediacenter/video/leanback/network/upnp/UpnpListingFragment.java

```text
20: import com.archos.mediacenter.video.leanback.filebrowsing.ListingFragment;
21: import com.archos.mediacenter.video.leanback.network.NetworkListingFragment;
29: public class UpnpListingFragment extends NetworkListingFragment {
31: private static final Logger log = LoggerFactory.getLogger(UpnpListingFragment.class);
34: protected  ListingFragment instantiateNewFragment() {
35: return new UpnpListingFragment();
```

### src/main/java/com/archos/mediacenter/video/leanback/network/webdav/WebdavListingActivity.java

```text
17: import com.archos.mediacenter.video.leanback.filebrowsing.ListingActivity;
18: import com.archos.mediacenter.video.leanback.filebrowsing.ListingFragment;
20: import com.archos.mediacenter.video.leanback.network.NetworkListingFragment;
22: public class WebdavListingActivity extends ListingActivity {
25: protected ListingFragment getStartingFragment() {
26: return new NetworkListingFragment();
```

### src/main/java/com/archos/mediacenter/video/leanback/tvshow/SeasonFragment.java

```text
59: import com.archos.mediacenter.video.leanback.filebrowsing.ListingActivity;
468: if (activity != null) activity.setResult(ListingActivity.RESULT_FILE_DELETED, intent);
```

### src/main/java/com/archos/mediacenter/video/streaming/putio/PutioAccountController.java

```text
56: else if(index==3)scopes(true);else if(index==4)activity.startActivity(new android.content.Intent(activity,PutioBrowserActivity.class).putExtra("account",account.id));else if(index==5)searchInput("");else if(index==6)replace(PreviewDialog.read(activity,"Account / Connection",account.username+"\n"+account.status+"\n\nAPI discovery and WebDAV playback use independent connections."));else disconnect();}));
```

### src/main/java/com/archos/mediacenter/video/streaming/putio/PutioBrowserActivity.java

```text
9: import com.archos.mediacenter.video.leanback.filebrowsing.PreviewBrowserSurface;
15: public final class PutioBrowserActivity extends ComponentActivity {
16: private PreviewBrowserSurface surface;private LinearLayout rows;private Future<?> task;private int generation;
24: surface=new PreviewBrowserSurface(this,listing,android.net.Uri.parse("putio://put.io/"),this::options);setContentView(surface);
```

### src/main/java/com/archos/mediacenter/video/utils/FolderPicker.java

```text
64: public class FolderPicker extends FragmentActivity {
66: private static final String TAG = "FolderPicker";
102: FolderPickerDialogFragment df = new FolderPickerDialogFragment();
116: df.show(getSupportFragmentManager(), FolderPickerDialogFragment.FRAGMENT_TAG);
157: static public class FolderPickerDialogFragment extends DialogFragment implements OnItemClickListener, ListingEngine.Listener {
159: public static final String FRAGMENT_TAG = "FolderPickerDialogFragment";
```

### src/main/java/com/archos/mediacenter/video/utils/TorrentPathDialogPreference.java

```text
41: private ActivityResultLauncher<Intent> mFolderPickerLauncher;
43: public void setFolderPickerLauncher(ActivityResultLauncher<Intent> launcher) {
44: mFolderPickerLauncher = launcher;
70: Intent i = new Intent(getContext(), FolderPicker.class);
71: i.putExtra(FolderPicker.EXTRA_CURRENT_SELECTION, getDefaultDirectory(getSharedPreferences()).getPath());
72: i.putExtra(FolderPicker.EXTRA_DIALOG_TITLE, getContext().getString(R.string.torrent_path));
73: if (mFolderPickerLauncher != null) {
74: mFolderPickerLauncher.launch(i);
```

### src/main/java/com/archos/mediacenter/video/utils/VideoPreferencesCommon.java

```text
330: private final ActivityResultLauncher<Intent> mFolderPickerLauncher;
357: mFolderPickerLauncher = preferencesFragment.registerForActivityResult(
359: this::onFolderPickerResult);
365: private void onFolderPickerResult(ActivityResult result) {
367: String newPath = result.getData().getStringExtra(FolderPicker.EXTRA_SELECTED_FOLDER);
694: if (torrentPref != null) torrentPref.setFolderPickerLauncher(mFolderPickerLauncher);
```

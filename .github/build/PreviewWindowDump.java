package supernova.validation;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.UiAutomation;
import android.graphics.Rect;
import android.os.HandlerThread;
import android.util.Xml;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityWindowInfo;
import java.io.FileOutputStream;
import java.lang.reflect.Constructor;
import org.xmlpull.v1.XmlSerializer;

/** Shell-only read-only capture of the focused app window when active-window lookup is null. */
public final class PreviewWindowDump {
    public static void main(String[] args) throws Exception {
        HandlerThread thread = new HandlerThread("preview-window-capture");
        thread.start();
        UiAutomation automation = null;
        try {
            Class<?> connectionType = Class.forName("android.app.IUiAutomationConnection");
            Object connection = Class.forName("android.app.UiAutomationConnection").getDeclaredConstructor().newInstance();
            Constructor<UiAutomation> constructor = UiAutomation.class.getDeclaredConstructor(android.os.Looper.class, connectionType);
            constructor.setAccessible(true);
            automation = constructor.newInstance(thread.getLooper(), connection);
            UiAutomation.class.getDeclaredMethod("connect").invoke(automation);
            AccessibilityServiceInfo info = automation.getServiceInfo();
            info.flags |= AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS | AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS | AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS;
            automation.setServiceInfo(info);
            AccessibilityNodeInfo root = null;
            for (int attempt = 0; attempt < 3 && root == null; attempt++) {
                for (AccessibilityWindowInfo window : automation.getWindows()) {
                    if (!window.isFocused()) continue;
                    AccessibilityNodeInfo candidate = window.getRoot();
                    if (candidate != null && args[0].contentEquals(candidate.getPackageName())) {
                        root = candidate;
                        break;
                    }
                }
                if (root == null) Thread.sleep(500);
            }
            if (root == null) throw new IllegalStateException("Focused application accessibility root unavailable");
            try (FileOutputStream out = new FileOutputStream(args[1])) {
                XmlSerializer xml = Xml.newSerializer();
                xml.setOutput(out, "UTF-8");
                xml.startDocument("UTF-8", true);
                xml.startTag(null, "hierarchy");
                write(xml, root);
                xml.endTag(null, "hierarchy");
                xml.endDocument();
            }
        } finally {
            if (automation != null) UiAutomation.class.getDeclaredMethod("disconnect").invoke(automation);
            thread.quitSafely();
        }
    }
    private static void write(XmlSerializer xml, AccessibilityNodeInfo node) throws Exception {
        xml.startTag(null, "node");
        xml.attribute(null, "text", value(node.getText()));
        xml.attribute(null, "content-desc", value(node.getContentDescription()));
        xml.attribute(null, "resource-id", value(node.getViewIdResourceName()));
        xml.attribute(null, "class", value(node.getClassName()));
        xml.attribute(null, "package", value(node.getPackageName()));
        xml.attribute(null, "focused", String.valueOf(node.isFocused()));
        Rect bounds = new Rect(); node.getBoundsInScreen(bounds);
        xml.attribute(null, "bounds", "["+bounds.left+","+bounds.top+"]["+bounds.right+","+bounds.bottom+"]");
        for (int i = 0; i < node.getChildCount(); i++) {
            AccessibilityNodeInfo child = node.getChild(i);
            if (child != null && child.isVisibleToUser()) write(xml, child);
        }
        xml.endTag(null, "node");
    }
    private static String value(CharSequence value) { return value == null ? "" : value.toString(); }
}

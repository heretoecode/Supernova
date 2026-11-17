package com.archos.mediacenter.video.leanback.details;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.archos.mediacenter.video.leanback.PreviewDialog;
import com.archos.mediacenter.video.leanback.SupernovaStyle;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.util.Collections;

/**
 * More › TMDB page (approved 17 Nov 2026, mock-up files-review-0157-1/07): a pop-up sized to its content with one
 * line, "Scan to open the TMDB page", and the QR code (170 px at 1080p); the page blurred behind; Back closes.
 */
public final class PreviewTmdbCode
{
    static final String LINE = "Scan to open the TMDB page";

    private PreviewTmdbCode()
    {
    }

    public static Dialog show(Context context, String address)
    {
        Bitmap code = code(address, 256);
        Dialog dialog = PreviewDialog.create(context, "tmdb");
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LinearLayout panel = new LinearLayout(context);
        panel.setOrientation(LinearLayout.VERTICAL);
        panel.setGravity(Gravity.CENTER_HORIZONTAL);
        int pad = SupernovaStyle.dp(context, 18);
        panel.setPadding(pad, pad, pad, pad);
        panel.setBackground(SupernovaStyle.glass(context));
        panel.setContentDescription(LINE);
        TextView line = new TextView(context);
        line.setText(LINE);
        line.setTextSize(16);
        line.setTextColor(Color.WHITE);
        panel.addView(line);
        ImageView image = new ImageView(context);
        image.setImageBitmap(code);
        int size = SupernovaStyle.dp(context, 85);
        LinearLayout.LayoutParams imageSize = new LinearLayout.LayoutParams(size, size);
        imageSize.topMargin = SupernovaStyle.dp(context, 12);
        panel.addView(image, imageSize);
        dialog.setContentView(panel);
        dialog.show();
        Window window = dialog.getWindow();
        if (window != null)
        {
            window.setBackgroundDrawableResource(android.R.color.transparent);
            window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
            window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            window.setDimAmount(0.45f);
            if (Build.VERSION.SDK_INT >= 31)
            {
                window.addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
                window.getAttributes().setBlurBehindRadius(SupernovaStyle.dp(context, 20));
                window.setAttributes(window.getAttributes());
            }
        }
        return dialog;
    }

    /** The QR code for an https address (black on white), or null when it can't be made. */
    static Bitmap code(String address, int pixels)
    {
        try
        {
            BitMatrix matrix = new QRCodeWriter().encode(
                address, BarcodeFormat.QR_CODE, pixels, pixels, Collections.singletonMap(EncodeHintType.MARGIN, 2));
            Bitmap bitmap = Bitmap.createBitmap(pixels, pixels, Bitmap.Config.ARGB_8888);
            int[] dots = new int[pixels * pixels];
            for (int y = 0; y < pixels; y++)
            {
                for (int x = 0; x < pixels; x++)
                {
                    dots[y * pixels + x] = matrix.get(x, y) ? Color.BLACK : Color.WHITE;
                }
            }
            bitmap.setPixels(dots, 0, pixels, 0, 0, pixels, pixels);
            return bitmap;
        }
        catch (Exception invalid)
        {
            return null;
        }
    }
}

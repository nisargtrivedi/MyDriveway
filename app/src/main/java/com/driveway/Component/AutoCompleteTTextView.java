package com.driveway.Component;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.appcompat.widget.AppCompatTextView;

import java.util.HashMap;

/**
 * Created by admin on 7/5/2017.
 */

public class AutoCompleteTTextView extends AppCompatAutoCompleteTextView {

    public static final String ANDROID_SCHEMA = "http://schemas.android.com/apk/res/android";

    public AutoCompleteTTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        applyCustomFont(context, attrs);
    }

    public AutoCompleteTTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        applyCustomFont(context, attrs);
    }

    private void applyCustomFont(Context context, AttributeSet attrs) {
        int textStyle = attrs.getAttributeIntValue(ANDROID_SCHEMA, "textStyle", Typeface.NORMAL);

        Typeface customFont = selectTypeface(context, textStyle);
        setTypeface(customFont);
    }

    private Typeface selectTypeface(Context context, int textStyle) {
        /*
        * information about the TextView textStyle:
        * http://developer.android.com/reference/android/R.styleable.html#TextView_textStyle
        */
        switch (textStyle) {
            case Typeface.BOLD: // bold

            case Typeface.BOLD_ITALIC: // bold italic
                return FontCache.getTypeface("ProductSansBold.ttf", context);

            case Typeface.ITALIC: // italic
                return FontCache.getTypeface("ProductSansItalic.ttf", context);

            case Typeface.NORMAL: // regular
                return FontCache.getTypeface("ProductSansRegular.ttf", context);
            default:
                return FontCache.getTypeface("ProductSansRegular.ttf", context);
        }
    }

    protected CharSequence convertSelectionToString(Object selectedItem) {
        /** Each item in the autocompetetextview suggestion list is a hashmap object */
        HashMap<String, String> hm = (HashMap<String, String>) selectedItem;
        return hm.get("description");
    }

}

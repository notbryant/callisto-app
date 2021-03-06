package com.qweex.utils;

import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

//http://www.codeproject.com/Articles/547636/Android-Ready-to-use-simple-directory-chooser-dial
public class PathPreference extends Preference implements Preference.OnPreferenceClickListener,
                                                          DirectoryChooserDialog.ChosenDirectoryListener
{
    String mDefaultPath, mPath, mPrefix;
    View mView;

    private static final String androidns = "http://schemas.android.com/apk/res/android";

    public PathPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    public PathPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public PathPreference(Context context) {
        super(context);
        init(context, null);
    }

    private void init(Context context, AttributeSet attrs)
    {
        setOnPreferenceClickListener(this);
        if(attrs != null)
            mDefaultPath = attrs.getAttributeValue(androidns, "defaultValue");
        else
            mDefaultPath = "/mnt/sdcard";
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue)
    {
        mDefaultPath = restoreValue ? getPath() : (String) defaultValue;
        setSummary(mDefaultPath);
    }

    public String getPath()
    {
        if(mPath==null)
            try {
                if(isPersistent()) {
                    mPath = getPersistedString(mDefaultPath);
                }
            } catch (ClassCastException e) {
                mPath = mDefaultPath;
            }
        return mPath;
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        mView = view;
        setSummary(getPath());
    }

    @Override
    public boolean onPreferenceClick(Preference preference)
    {
        DirectoryChooserDialog d = new DirectoryChooserDialog(getContext(),this);
        d.setNewFolderEnabled(true);
        d.chooseDirectory(getPath());
        return false;
    }

    @Override
    public void onChosenDir(String chosenDir)
    {
        persistString(chosenDir);
        mPath = chosenDir;
        setSummary(chosenDir);
        System.out.println(chosenDir + "!!!");
        try {
            getOnPreferenceChangeListener().onPreferenceChange(this, mPath);
        } catch (NullPointerException e) {}
    }
}

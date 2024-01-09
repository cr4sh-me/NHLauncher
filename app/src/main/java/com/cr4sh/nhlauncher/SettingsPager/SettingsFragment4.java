package com.cr4sh.nhlauncher.SettingsPager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.cr4sh.nhlauncher.MainActivity;
import com.cr4sh.nhlauncher.MyPreferences;
import com.cr4sh.nhlauncher.NHLManager;
import com.cr4sh.nhlauncher.R;
import com.cr4sh.nhlauncher.utils.VibrationUtil;

public class SettingsFragment4 extends Fragment {
    private final MainActivity mainActivity = NHLManager.getInstance().getMainActivity();
    private MyPreferences myPreferences;
    public SettingsFragment4() {
        // Required empty public constructor
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.settings_layout4, container, false);

        myPreferences = new MyPreferences(requireActivity());

        // Get the TextViews from the layout
        TextView title = view.findViewById(R.id.bt_info2);
        TextView textView1 = view.findViewById(R.id.textview1);
        TextView textView2 = view.findViewById(R.id.textview2);
        TextView textView3 = view.findViewById(R.id.textview3);
        Button github = view.findViewById(R.id.github_button);

        setButtonColors(github);

        // Set the text for each TextView
        textView1.setText("'OkHttp' library licensed under Apache 2.0");
        textView2.setText("'PowerSpinner' library licensed under Apache 2.0");
        textView3.setText("'ColorPicker' library licensed under Apache 2.0");

        // Create ClickableSpan for each TextView
        setClickableSpan(textView1, "https://github.com/square/okhttp");
        setClickableSpan(textView2, "https://github.com/skydoves/PowerSpinner");
        setClickableSpan(textView3, "https://github.com/skydoves/ColorPickerView");

        title.setTextColor(Color.parseColor(myPreferences.color80()));
        textView1.setLinkTextColor(Color.parseColor(myPreferences.color80()));
        textView2.setLinkTextColor(Color.parseColor(myPreferences.color80()));
        textView3.setLinkTextColor(Color.parseColor(myPreferences.color80()));

        github.setOnClickListener(v -> {
            VibrationUtil.vibrate(mainActivity, 10);
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/cr4sh-me/NHLauncher"));
            startActivity(intent);
        });


        return view;
    }

    private void setClickableSpan(TextView textView, final String url) {
        SpannableString spannableString = new SpannableString(textView.getText());
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                VibrationUtil.vibrate(mainActivity, 10);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            }
        };
        spannableString.setSpan(clickableSpan, 0, spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

        // Set the SpannableString to the TextView
        textView.setText(spannableString);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void setButtonColors(Button button) {
        button.setBackgroundColor(Color.parseColor(myPreferences.color50()));
        button.setTextColor(Color.parseColor(myPreferences.color80()));
    }
}

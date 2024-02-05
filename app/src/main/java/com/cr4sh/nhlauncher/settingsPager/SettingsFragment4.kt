package com.cr4sh.nhlauncher.settingsPager

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.cr4sh.nhlauncher.R
import com.cr4sh.nhlauncher.utils.NHLManager
import com.cr4sh.nhlauncher.utils.NHLPreferences
import com.cr4sh.nhlauncher.utils.VibrationUtils.vibrate

class SettingsFragment4 : Fragment() {
    private val mainActivity = NHLManager.getInstance().mainActivity
    private var nhlPreferences: NHLPreferences? = null
    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.settings_layout4, container, false)
        nhlPreferences = NHLPreferences(requireActivity())

        // Get the TextViews from the layout
        val title = view.findViewById<TextView>(R.id.bt_info2)
        val textView1 = view.findViewById<TextView>(R.id.textview1)
        val textView2 = view.findViewById<TextView>(R.id.textview2)
        val textView3 = view.findViewById<TextView>(R.id.textview3)
        val github = view.findViewById<Button>(R.id.github_button)
        setButtonColors(github)

        // Set the text for each TextView
        textView1.text = "'OkHttp' library licensed under Apache 2.0"
        textView2.text = "'PowerSpinner' library licensed under Apache 2.0"
        textView3.text = "'colorpicker' library licensed under Apache 2.0"

        // Create ClickableSpan for each TextView
        setClickableSpan(textView1, "https://github.com/square/okhttp")
        setClickableSpan(textView2, "https://github.com/skydoves/PowerSpinner")
        setClickableSpan(textView3, "https://github.com/QuadFlask/colorpicker")
        title.setTextColor(Color.parseColor(nhlPreferences!!.color80()))
        textView1.setLinkTextColor(Color.parseColor(nhlPreferences!!.color80()))
        textView2.setLinkTextColor(Color.parseColor(nhlPreferences!!.color80()))
        textView3.setLinkTextColor(Color.parseColor(nhlPreferences!!.color80()))
        github.setOnClickListener {
            vibrate(mainActivity, 10)
            val intent =
                Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/cr4sh-me/NHLauncher"))
            startActivity(intent)
        }
        return view
    }

    private fun setClickableSpan(textView: TextView, url: String) {
        val spannableString = SpannableString(textView.text)
        val clickableSpan: ClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                vibrate(mainActivity, 10)
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(intent)
            }
        }
        spannableString.setSpan(
            clickableSpan,
            0,
            spannableString.length,
            Spanned.SPAN_INCLUSIVE_EXCLUSIVE
        )

        // Set the SpannableString to the TextView
        textView.text = spannableString
        textView.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun setButtonColors(button: Button) {
        button.setBackgroundColor(Color.parseColor(nhlPreferences!!.color50()))
        button.setTextColor(Color.parseColor(nhlPreferences!!.color80()))
    }
}

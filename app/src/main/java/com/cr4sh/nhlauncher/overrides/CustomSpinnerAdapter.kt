package com.cr4sh.nhlauncher.overrides

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.cr4sh.nhlauncher.R

// This class creates our spinner with custom themes and styles
class CustomSpinnerAdapter(
    context: Context?,
    values: List<String?>?,
    private val imageList: List<Int>,
    private val backgroundColor: String,
    private val textColor: String
) : ArrayAdapter<String?>(
    context!!, R.layout.dropdown_items, values ?: emptyList()
) {
    override fun getDropDownView(position: Int, convertView2: View?, parent: ViewGroup): View? {
        var convertView = convertView2
        if (convertView == null) {
            convertView =
                LayoutInflater.from(context).inflate(R.layout.dropdown_items, parent, false)
        }
        val imageView = convertView?.findViewById<ImageView>(R.id.image_view)
        val textView = convertView?.findViewById<TextView>(R.id.text_view)
        imageView?.setColorFilter(Color.parseColor(textColor), PorterDuff.Mode.MULTIPLY)
        if (position in 0..<count) {
            imageView?.setColorFilter(Color.parseColor(textColor), PorterDuff.Mode.MULTIPLY)
            if (imageList.isNotEmpty() && position < imageList.size) {
                imageView?.setImageResource(imageList[position])
            }
            if (textView != null) {
                textView.text = getItem(position)
            }
        }

        // set background color and text color for dropdown items and selected item in spinner
        convertView?.setBackgroundColor(Color.parseColor(backgroundColor))
        textView?.setTextColor(Color.parseColor(textColor))
        return convertView
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getCustomView(position, parent)
    }

    private fun getCustomView(position: Int, parent: ViewGroup): View {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.spinner_layout, parent, false)
        val imageView = view.findViewById<ImageView>(R.id.image_view)
        val textView = view.findViewById<TextView>(R.id.text_view)
        imageView.setColorFilter(Color.parseColor(textColor), PorterDuff.Mode.MULTIPLY)
        if (imageList.isNotEmpty()) {
            imageView.setImageResource(imageList[position])
        }
        textView.text = getItem(position)

        // Set text color of spinner
        textView.setTextColor(Color.parseColor(textColor))
        return view
    }
}
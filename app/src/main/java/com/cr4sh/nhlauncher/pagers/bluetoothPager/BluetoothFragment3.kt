package com.cr4sh.nhlauncher.pagers.bluetoothPager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.cr4sh.nhlauncher.R
import com.cr4sh.nhlauncher.utils.NHLPreferences

class BluetoothFragment3 : Fragment() {
    var nhlPreferences: NHLPreferences? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.bt_layout3, container, false)
        nhlPreferences = NHLPreferences(requireActivity())
        return view
    }
}

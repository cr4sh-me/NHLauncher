package com.cr4sh.nhlauncher.utils

import com.cr4sh.nhlauncher.activities.MainActivity

class NHLManager private constructor() {
    lateinit var mainActivity: MainActivity

    companion object {
        @JvmStatic
        val instance: NHLManager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { Holder.INSTANCE }
    }

    private object Holder {
        val INSTANCE = NHLManager()
    }
}

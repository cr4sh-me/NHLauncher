package com.cr4sh.nhlauncher.utils

import com.cr4sh.nhlauncher.activities.MainActivity

class NHLManager private constructor() {
    private var mainActivity: MainActivity? = null

    private object Holder {
        val INSTANCE = NHLManager()
    }

    companion object {
        @JvmStatic
        fun getInstance(): NHLManager {
            return Holder.INSTANCE
        }
    }

    fun getMainActivity(): MainActivity? {
        return mainActivity
    }

    fun setMainActivity(activity: MainActivity) {
        this.mainActivity = activity
    }
}

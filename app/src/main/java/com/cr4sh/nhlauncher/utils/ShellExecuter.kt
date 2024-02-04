package com.cr4sh.nhlauncher.utils

import android.util.Log
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.IOException
import java.io.InputStreamReader

// NetHunter commands execution class
class ShellExecuter {
    fun RunAsRoot(command: Array<String>) {
        try {
            val process = Runtime.getRuntime().exec("su -mm")
            val os = DataOutputStream(process.outputStream)
            for (tmpmd in command) {
                os.writeBytes(tmpmd + '\n')
            }
            os.writeBytes("exit\n")
            os.flush()
            try {
                process.waitFor()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun RunAsRootOutput(command: String): String {
        var output = StringBuilder()
        var line: String?
        try {
            val process = Runtime.getRuntime().exec("su -mm")
            val stdin = process.outputStream
            val stderr = process.errorStream
            val stdout = process.inputStream
            stdin.write((command + '\n').toByteArray())
            stdin.write("exit\n".toByteArray())
            stdin.flush()
            stdin.close()
            var br = BufferedReader(InputStreamReader(stdout))
            while (br.readLine().also { line = it } != null) {
                output.append(line).append('\n')
            }
            /* remove the last \n */if (output.isNotEmpty()) output =
                StringBuilder(output.substring(0, output.length - 1))
            br.close()
            br = BufferedReader(InputStreamReader(stderr))
            while (br.readLine().also { line = it } != null) {
                Log.e("Shell Error:", line!!)
            }
            br.close()
            process.waitFor()
            process.destroy()
        } catch (e: IOException) {
            Log.d(TAG, "An IOException was caught: " + e.message)
        } catch (ex: InterruptedException) {
            Log.d(TAG, "An InterruptedException was caught: " + ex.message)
        }
        return output.toString()
    }

    fun Executer(command: String?): String {
        val output = StringBuilder()
        val p: Process
        try {
            p = Runtime.getRuntime().exec(command)
            p.waitFor()
            val reader = BufferedReader(InputStreamReader(p.inputStream))
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                output.append(line).append('\n')
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return output.toString()
    }

    companion object {
        private const val TAG = "ShellExecuter"
    }
}

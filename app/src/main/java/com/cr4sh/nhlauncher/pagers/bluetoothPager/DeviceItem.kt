package com.cr4sh.nhlauncher.pagers.bluetoothPager

import com.cr4sh.nhlauncher.R

class DeviceItem(
    var ip: String = "Network error...",
    var mac: String = "Scanning...",
    var vendor: String = "Unknown",
    var imageRes: Int = 0,
    var os: String = "Unknown",
//    var subname: String? = null,
    var ports: ArrayList<String> = ArrayList(),
    var services: ArrayList<String> = ArrayList(),
    var versions: ArrayList<String> = ArrayList(),
//    var imgResource: Int = R.drawable.nhl_settings
) {
    // Function to update values
//    fun updateValues(newIp: String, newMac: String, newVendor: String, newOs: String, newSubname: String?) {
//        ip = newIp
//        mac = newMac
//        vendor = newVendor
//        os = newOs
//        subname = newSubname
////        imgResource = newImgResource
//        // You can add more updates as needed
//    }

    // Function to add a port
//    fun addPort(port: String) {
//        ports.add(port)
//    }
//
//    // Function to add a service
//    fun addService(service: String) {
//        services.add(service)
//    }
//
//    // Function to add a version
//    fun addVersion(version: String) {
//        versions.add(version)
//    }

    fun guessos() {
        if (ports.contains("21") || ports.contains("22") || ports.contains("23")) {
            this.os = "Linux"
            imageRes = R.drawable.stryker_linux
//            setOs("Linux")
//            setImage(R.drawable.linux)

        }
        if (ports.contains("554") || ports.contains("37777")) {
            this.os = "Secure Camera"
            imageRes = R.drawable.stryker_camera
//            setOs("Secure Camera")
//            setImage(R.drawable.camera)
        }
        if (ports.contains("9100")) {
            this.os = "Printer"
            imageRes = R.drawable.stryker_printer
//            setOs("Printer")
//            setImage(R.drawable.printer)
        }
        if (ports.contains("5555")) {
            this.os = "Android"
            imageRes = R.drawable.stryker_smartphone
//            setOs("Android")
//            setImage(R.drawable.iphone)
        }
        if (ports.contains("2336") || ports.contains("3004") || ports.contains("3031")) {
            this.os = "iOS/MacOS"
            imageRes = R.drawable.stryker_apple
//            setOs("IOS/MACOS")
//            setImage(R.drawable.aplle)
        }
        if (ports.contains("3389") || ports.contains("135") || ports.contains("136") || ports.contains(
                "137"
            ) || ports.contains("138") || ports.contains("139") || ports.contains("5357") || ports.contains(
                "445"
            ) || ports.contains("903")
        ) {
            this.os = "windows"
            imageRes = R.drawable.styker_windows
//            setOs("Windows")
//            setImage(R.drawable.windows)
        }
        if (ports.contains("1900")) {
            this.os = "linux router"
            imageRes = R.drawable.stryker_router
//            setOs("Linux router")
//            setImage(R.drawable.router)
        }
    }


    fun getImage(): Int {
        if (imageRes == 0) {
            imageRes = R.drawable.stryker_local
        }
        if (os.contains("Windows")) {
            imageRes = R.drawable.styker_windows
        }
        if (os.contains("Linux")) {
            imageRes = R.drawable.stryker_linux
        }
        if (os.contains("Android")) {
            imageRes = R.drawable.stryker_smartphone
        }
        if (os.contains("IOS") || os.contains("MacOS") || os.contains("Apple")) {
            imageRes = R.drawable.stryker_apple
        }
        return imageRes
    }
}

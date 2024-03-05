package com.cr4sh.nhlauncher.pagers.bluetoothPager

class DeviceItem(
    var ip: String = "Network error...",
    var mac: String = "Scanning...",
    var vendor: String = "Unknown",
    var image: Int = 0,
    var os: String = "Unknown",
    var subname: String? = null,
    var ports: ArrayList<String> = ArrayList(),
    var services: ArrayList<String> = ArrayList(),
    var versions: ArrayList<String> = ArrayList()
) {
    // Function to update values
    fun updateValues(newIp: String, newMac: String, newVendor: String, newOs: String, newSubname: String?) {
        ip = newIp
        mac = newMac
        vendor = newVendor
        os = newOs
        subname = newSubname
        // You can add more updates as needed
    }

    // Function to add a port
    fun addPort(port: String) {
        ports.add(port)
    }

    // Function to add a service
    fun addService(service: String) {
        services.add(service)
    }

    // Function to add a version
    fun addVersion(version: String) {
        versions.add(version)
    }
}

package com.cr4sh.nhlauncher.pagers.bluetoothPager

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.cr4sh.nhlauncher.R
import com.cr4sh.nhlauncher.activities.MainActivity
import com.cr4sh.nhlauncher.dialogs.NmapDeviceDialog
import com.cr4sh.nhlauncher.pagers.netscannerPager.NetScannerFragment1
import com.cr4sh.nhlauncher.utils.DialogUtils
import com.cr4sh.nhlauncher.utils.NHLManager
import kotlinx.coroutines.launch


class DeviceAdapter(
    private val devices: MutableList<DeviceItem>,
    private val fragment1: NetScannerFragment1
) : RecyclerView.Adapter<DeviceAdapter.ViewHolder>() {


    private val myActivity: MainActivity? = NHLManager.getInstance().getMainActivity()
    val dialogUtils = myActivity?.let { DialogUtils(it.supportFragmentManager) }
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val textViewIP: TextView = itemView.findViewById(R.id.textViewIP)
        val textViewMAC: TextView = itemView.findViewById(R.id.textViewMAC)
        val textViewVendor: TextView = itemView.findViewById(R.id.textViewVendor)
        val textViewOs: TextView = itemView.findViewById(R.id.textViewOs)
        val textViewPorts: TextView = itemView.findViewById(R.id.textViewPorts)
        val textViewServices: TextView = itemView.findViewById(R.id.textViewServices)


        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            val position = bindingAdapterPosition
            val clickedDevice = devices[position]
//
//            fragment1.deviceMac = clickedDevice.mac
//            fragment1.deviceVendor = clickedDevice.vendor

            fragment1.viewLifecycleOwner.lifecycleScope.launch {
                val ndDialog = NmapDeviceDialog(clickedDevice)
                ndDialog.show(fragment1.childFragmentManager, "NmapDeviceDialog")
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun clear() {
        devices.clear()
        notifyDataSetChanged()
    }

    fun updateDevice(ip: String, updatedMac: String, updatedVendor: String, updatedOs: String, ports: ArrayList<String>, services: ArrayList<String>) {
        // Find the index of the device with the specified IP
        val index = devices.indexOfFirst { it.ip == ip }

        // Check if the device is found
        if (index != -1) {
            // Update the properties of the device at the found index
            devices[index].mac = updatedMac
            devices[index].vendor = updatedVendor
            devices[index].os = updatedOs
            devices[index].ports = ports
            devices[index].services = services

            // Notify the RecyclerView adapter if you're using one
            notifyItemChanged(index)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_device, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val device = devices[position]

        holder.textViewIP.text = "IP Address: ${device.ip}"
        holder.textViewMAC.text = "MAC Address: ${device.mac}"
        holder.textViewVendor.text = "Vendor: ${device.vendor}"
        holder.textViewOs.text = "OS: ${device.os}"
        holder.textViewPorts.text = "Ports: ${device.ports}"
        holder.textViewServices.text = "Services: ${device.services}"

    }

    override fun getItemCount(): Int {
        return devices.size
    }
}

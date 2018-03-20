package loodos.sinan.com.movies.receivers

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.provider.Settings
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import loodos.sinan.com.movies.R
import loodos.sinan.com.movies.ui.SplashActivity


/**
 * Created by sinan on 18.03.2018.
 */

class InternetConnectionReceiver(private val context: AppCompatActivity) : BroadcastReceiver(), View.OnClickListener {

    var isNetworkAvailable = true
    private val connectionDialog by lazy { AlertDialog.Builder(context).create() }
    private val btnWifi by lazy { connectionDialog.findViewById<Button>(R.id.dialog_btnWifi) }
    private val btnCellular by lazy { connectionDialog.findViewById<Button>(R.id.dialog_btnCellular) }
    private val btnExit by lazy { connectionDialog.findViewById<Button>(R.id.dialog_btnExit) }

    override fun onReceive(context: Context, arg1: Intent) {

        isNetworkAvailable = networkControl()
        if (context is SplashActivity) {

            if (isNetworkAvailable) {

                context.fetch()
            }
        }
    }

    private fun networkControl(): Boolean {

        val connectivity = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val activeNetwork = connectivity.activeNetworkInfo
        if (activeNetwork != null) {
            if (activeNetwork.type == ConnectivityManager.TYPE_WIFI) {
                connectionDialog.hide()
                return true

            } else if (activeNetwork.type == ConnectivityManager.TYPE_MOBILE) {
                connectionDialog.hide()
                return true
            }
        }
        createDialog()

        return false
    }

    @SuppressLint("InflateParams")
    private fun createDialog() {
        val layoutInflater = LayoutInflater.from(context)
        val alertView = layoutInflater.inflate(R.layout.dialog_no_internet_connection, null, false)
        connectionDialog.setView(alertView)
        connectionDialog.show()
        connectionDialog.setCancelable(false)

        btnWifi?.setOnClickListener(this)
        btnCellular?.setOnClickListener(this)
        btnExit?.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.dialog_btnWifi -> {
                val i = Intent(Settings.ACTION_WIFI_SETTINGS)
                startActivity(context, i, null)
            }
            R.id.dialog_btnCellular -> {
                val i = Intent(Settings.ACTION_DATA_ROAMING_SETTINGS)
                startActivity(context, i, null)
            }
            R.id.dialog_btnExit -> {
                context.finish()
            }
        }
    }
}
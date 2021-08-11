package com.tmobile.mytmobile.echolocate.playground.activities

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.text.method.ScrollingMovementMethod
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.tmobile.mytmobile.echolocate.BuildConfig
import com.tmobile.mytmobile.echolocate.R
import com.tmobile.mytmobile.echolocate.authentication.provider.AuthenticationProvider
import com.tmobile.mytmobile.echolocate.authentication.provider.ITokenReceivedListener
import com.tmobile.mytmobile.echolocate.authentication.utils.TokenSharedPreference
import com.tmobile.mytmobile.echolocate.configmanager.ConfigProvider
import com.tmobile.mytmobile.echolocate.configuration.ConfigNetworkTask
import com.tmobile.mytmobile.echolocate.networkmanager.NetworkProvider
import com.tmobile.mytmobile.echolocate.utils.EchoLocateLog
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_configuration.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.nio.channels.FileChannel
import java.nio.charset.Charset

class ConfigurationActivity : AppCompatActivity() {
    private var count = 0
    private var nwReqDisposable: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configuration)
        config_text_from.movementMethod = ScrollingMovementMethod()
        initListeners()
    }

    private fun initListeners() {
        upload_config_from_cache.setOnClickListener {
            onCacheConfigClicked()
        }
        get_server_config.setOnClickListener {
            onGetServerConfigClicked()
        }
        clear_config.setOnClickListener {
            ConfigProvider.getInstance(this).clearConfigFromCache()
            val currConfig = ConfigProvider.getInstance(this).getAvailableConfiguration()
            config_text_from.text = "Current active config : \n\n$currConfig"
            config_text_from.scrollTo(0,0)
        }
        delete_cache_config.setOnClickListener {
            config_text_from.text = ""
            val file = File(applicationContext.externalCacheDir, "configuration.json")
            if (file.exists()) {
                file.delete()
                config_text_from.text = "Configuration file deleted from cache folder"
            }
            config_text_from.scrollTo(0,0)
        }
    }

    private fun readFile(file: File): String? {
        var jsonStr: String? = null
        try {
            FileInputStream(file).use { stream ->
                val fileChannel = stream.channel
                val byteBuffer =
                    fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, fileChannel.size())
                jsonStr = Charset.defaultCharset()
                    .decode(byteBuffer)
                    .toString()
            }
        } catch (e: IOException) {
            EchoLocateLog.eLogE("error: ${e.localizedMessage}")
        }
        return jsonStr
    }

    private fun onCacheConfigClicked() {
        config_text_from.text = ""
        val file = File(applicationContext.externalCacheDir, "configuration.json")
        if (!file.exists()) {
            config_text_from.text = "Configuration file doesn't exist in cache"
            Toast.makeText(this, "Configuration file doesn't exist in cache", Toast.LENGTH_SHORT)
                .show()
        } else {
            val rawConfig = readFile(file)
            if (!TextUtils.isEmpty(rawConfig)) {
                if (rawConfig != null) {
                    config_text_from.text = rawConfig
                }
                rawConfig?.let { ConfigProvider.getInstance(this).uploadConfigFromCache(it) }
            }
        }
        config_text_from.scrollTo(0,0)
    }

    private fun onGetServerConfigClicked() {
        count = 0
        config_text_from.text = "Requesting config to server...\n\nPlease wait...\n\n"
        config_text_from.append("Url = ${ConfigNetworkTask.getConfigDiaRequest().url}\n\n")

        if (AuthenticationProvider.getInstance(this).isLocallyStoredTokenExpired()) {
            GlobalScope.launch(Dispatchers.Main) {
                AuthenticationProvider.getInstance(applicationContext).refreshToken(
                    object : ITokenReceivedListener {
                        override fun onReceivedToken(token: String) {
                            count++
                            config_text_from.append("$count Config : Send network request\n\n")
                            performConfigNetworkRequest(applicationContext)
                        }
                    }
                )
            }
        } else {
            performConfigNetworkRequest(this)
        }
    }

    private fun performConfigNetworkRequest(context: Context) {
        var configResponse: String? = null
        val networkProvider = NetworkProvider.getInstance(context) as NetworkProvider
        if (nwReqDisposable == null || nwReqDisposable!!.isDisposed()) {
            nwReqDisposable = networkProvider.performNetworkOperations(
                ConfigNetworkTask.getConfigDiaRequest(),
                ConfigNetworkTask.getConfigRequestHeader(),
                ConfigNetworkTask.getConfigRetryPrefs(),
                TokenSharedPreference.tokenObject!!,
                context
            )
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe({
                    if (it.errorMsg.isEmpty()) {
                        configResponse = ConfigProvider.getInstance(this).parseResponse(it, false, 0)
                        EchoLocateLog.eLogD("Diagnostic : Successfully get config from server $configResponse")
                        if (!TextUtils.isEmpty(configResponse)) {
                            config_text_from.append("$count Config from server :\n\n $configResponse")
                        }
                    } else {
                        EchoLocateLog.eLogD("Diagnostic : Error getting config from server ${it.errorMsg}")
                        config_text_from.append("$count Error while getting config from server ${it.errorMsg}")
                    }
                    nwReqDisposable?.dispose()
                }, {
                    EchoLocateLog.eLogE("Diagnostic : Error while download - getting config from assets")
                    config_text_from.append("$count Error while getting config from server")
                    nwReqDisposable?.dispose()
                }, {
                    nwReqDisposable?.dispose()
                })
            config_text_from.scrollTo(0, 0)
        }
    }
}
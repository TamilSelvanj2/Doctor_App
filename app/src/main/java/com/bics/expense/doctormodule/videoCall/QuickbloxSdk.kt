package com.bics.expense.doctormodule.videoCall

import android.app.Application
import android.util.Log
import com.quickblox.auth.session.QBSettings
import com.quickblox.chat.QBChatService
import com.quickblox.core.LogLevel
import com.quickblox.videochat.webrtc.QBRTCConfig


class QuickbloxSdk  :Application() {
    private val applicationID = "103585"
    private val authKey = "ak_WvTDPfNMQvZhutL"
    private val authSecret = "as_qaf2fMgQ55aw2ys"
    private val accountKey = "ack_rzPDDpV1MehAimtFhFZb"

    override fun onCreate() {
        super.onCreate()
        initCredentials()
        initChat()
    }

    private fun initCredentials() {
        Log.d("QuickbloxSdk", "Initializing QuickBlox credentials.")
        QBSettings.getInstance().init(applicationContext, applicationID, authKey, authSecret)
        QBSettings.getInstance().accountKey = accountKey
        QBRTCConfig.setDebugEnabled(true)


    }
    private fun initChat() {
        QBSettings.getInstance().logLevel = LogLevel.DEBUG
        QBChatService.setConfigurationBuilder(QBChatService.ConfigurationBuilder().apply { socketTimeout = 0 })
        QBChatService.getInstance().isReconnectionAllowed = true
    }
}
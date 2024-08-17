package com.bics.expense.doctormodule.videoCall

import android.app.Application
import com.quickblox.auth.session.QBSettings

// user default credentials
const val DEFAULT_USER_PASSWORD = "quickblox@123"

// app credentials
private val APPLICATION_ID = "103926"
private val AUTH_KEY = "ak_zvnJ3Q9FNyf2Dze"
private val AUTH_SECRET = "as_LTUj95zcPRv4TDz"
private val ACCOUNT_KEY = "ack_gzSfrnKe8zVJgszy4cfe"

class QuickbloxSdk  :Application() {



    companion object {
        private lateinit var instance: QuickbloxSdk

        @Synchronized
        fun getInstance(): QuickbloxSdk = instance
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        checkCredentials()
        initCredentials()
    }

    private fun checkCredentials() {
        if (APPLICATION_ID.isEmpty() || AUTH_KEY.isEmpty() || AUTH_SECRET.isEmpty() || ACCOUNT_KEY.isEmpty()) {
        }
    }

    private fun initCredentials() {
        QBSettings.getInstance().init(applicationContext, APPLICATION_ID, AUTH_KEY, AUTH_SECRET)
        QBSettings.getInstance().accountKey = ACCOUNT_KEY

    }
//    @Synchronized
//    fun getDbHelper(): DbHelper {
//        return dbHelper
//    }
}
package com.bics.expense.doctormodule.videoCall

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.bics.expense.doctormodule.databinding.ActivityVideoChatBinding
import com.quickblox.chat.QBChatService
import com.quickblox.chat.QBRestChatService
import com.quickblox.chat.model.QBChatDialog
import com.quickblox.chat.model.QBDialogType
import com.quickblox.core.QBEntityCallback
import com.quickblox.core.exception.QBResponseException
import com.quickblox.videochat.webrtc.QBRTCClient
import com.quickblox.videochat.webrtc.QBRTCSession
import com.quickblox.videochat.webrtc.QBRTCTypes
import com.quickblox.videochat.webrtc.callbacks.QBRTCClientSessionCallbacks
import com.quickblox.videochat.webrtc.callbacks.QBRTCClientVideoTracksCallbacks
import com.quickblox.videochat.webrtc.view.QBRTCSurfaceView
import com.quickblox.videochat.webrtc.view.QBRTCVideoTrack
import org.webrtc.EglBase

class VideoChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVideoChatBinding
    private lateinit var appointmentID: String
    private lateinit var quickBloxPatientID: String
    private lateinit var quickBloxDoctorID: String

    private lateinit var localVideoView: QBRTCSurfaceView
    private lateinit var remoteVideoView: QBRTCSurfaceView

    private  var mRtcClient: QBRTCClient?=null
    private var qbSession: QBRTCSession? = null

    private lateinit var eglBase: EglBase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        appointmentID = intent.getStringExtra("APPOINTMENT_ID") ?: ""
        quickBloxDoctorID = intent.getStringExtra("QUICKBLOX_DOCTOR_ID") ?: ""
        quickBloxPatientID = intent.getStringExtra("QUICKBLOX_PATIENT_ID") ?: ""

        eglBase = EglBase.create()
        localVideoView = binding.localVideoView
        remoteVideoView = binding.remoteVideoView

//        initQBRTCClient()
        createChatDialogAndInitiateCall()
    }
    override fun onDestroy() {
        super.onDestroy()
        eglBase.release()
    }

    private fun initQBRTCClient() {

//       val rtcClient = QBRTCClient.getInstance(applicationContext)
//
//        rtcClient.addSessionCallbacksListener(rtcSessionCallbacks)
//        rtcClient.prepareToProcessCalls()
//        mRtcClient=rtcClient

////
//        val signalingManager = QBVideoChatWebRTCSignalingManager.getInstance(applicationContext)
//        if (signalingManager == null) {
//            Log.e("VideoChatActivity", "Signaling Manager is not initialized.")
//            // Handle the error or notify the user
//            return
//        }
    }

    private fun createChatDialogAndInitiateCall() {
        val occupantIdsList = arrayListOf(
            quickBloxDoctorID.toIntOrNull() ?: 0,
            quickBloxPatientID.toIntOrNull() ?: 0,
            139923749
        )

        val dialog = QBChatDialog().apply {
            name = appointmentID
            type = QBDialogType.GROUP
            setOccupantsIds(occupantIdsList)
        }

        QBRestChatService.createChatDialog(dialog).performAsync(object : QBEntityCallback<QBChatDialog> {
            override fun onSuccess(result: QBChatDialog?, bundle: Bundle?) {
                Log.d("VideoChatActivity", "Chat dialog created successfully: ${result?.dialogId}")
                initiateCall()
            }

            override fun onError(exception: QBResponseException?) {
                Log.e("VideoChatActivity", "Error creating chat dialog: ${exception?.message}")
            }
        })
    }

    private fun initiateCall() {


        val userInfo = mutableMapOf<String, String>()
        val opponents = arrayListOf(
            quickBloxDoctorID.toIntOrNull() ?: 0,
            quickBloxPatientID.toIntOrNull() ?: 0,
            139923749
        )

        val rtcClient = QBRTCClient.getInstance(this)
        rtcClient.prepareToProcessCalls()
        val chatService = QBChatService.getInstance()



//        qbSession = mRtcClient?.createNewSessionWithOpponents(opponents, QBRTCTypes.QBConferenceType.QB_CONFERENCE_TYPE_VIDEO)

        // add signalling manager

        chatService.videoChatWebRTCSignalingManager.addSignalingManagerListener { signaling, createdLocally ->
            if (!createdLocally) {
                rtcClient.addSignaling(signaling)
            }
        }
        qbSession = rtcClient.createNewSessionWithOpponents(opponents, QBRTCTypes.QBConferenceType.QB_CONFERENCE_TYPE_VIDEO)


// configure
//        QBRTCConfig.setDebugEnabled(true)
//        QBRTCConfig.setAnswerTimeInterval(answerTimeInterval)
//        QBRTCConfig.setDisconnectTime(disconnectTimeInterval)
//        QBRTCConfig.setDialingTimeInterval(dialingTimeInterval)


        qbSession?.let { session ->
            session.startCall(userInfo)
            setUpVideoViews(session)
        }
    }

    private fun setUpVideoViews(session: QBRTCSession) {
        localVideoView.init(eglBase.eglBaseContext, null)
        remoteVideoView.init(eglBase.eglBaseContext, null)

        session.addVideoTrackCallbacksListener(object : QBRTCClientVideoTracksCallbacks<QBRTCSession> {
            override fun onLocalVideoTrackReceive(session: QBRTCSession, videoTrack: QBRTCVideoTrack) {
                videoTrack.addRenderer(localVideoView)
                localVideoView.visibility = QBRTCSurfaceView.VISIBLE
            }

            override fun onRemoteVideoTrackReceive(session: QBRTCSession, videoTrack: QBRTCVideoTrack, userID: Int) {
                videoTrack.addRenderer(remoteVideoView)
                remoteVideoView.visibility = QBRTCSurfaceView.VISIBLE
            }
        })
    }

    private val rtcSessionCallbacks = object : QBRTCClientSessionCallbacks {
        override fun onReceiveNewSession(session: QBRTCSession?) {
            qbSession = session
        }

        override fun onUserNoActions(session: QBRTCSession?, userID: Int?) {}
        override fun onUserNotAnswer(session: QBRTCSession?, userID: Int?) {}
        override fun onCallRejectByUser(session: QBRTCSession?, userID: Int?, userInfo: MutableMap<String, String>?) {}
        override fun onCallAcceptByUser(session: QBRTCSession?, userID: Int?, userInfo: MutableMap<String, String>?) {}
        override fun onReceiveHangUpFromUser(session: QBRTCSession?, userID: Int?, userInfo: MutableMap<String, String>?) {}
        override fun onChangeReconnectionState(session: QBRTCSession?, userID: Int?, reconnectionState: QBRTCTypes.QBRTCReconnectionState?) {}
        override fun onSessionClosed(session: QBRTCSession?) {}
        override fun onSessionStartClose(session: QBRTCSession?) {}
    }
}

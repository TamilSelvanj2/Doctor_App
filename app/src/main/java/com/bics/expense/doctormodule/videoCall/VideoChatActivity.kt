package com.bics.expense.doctormodule.videoCall

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.bics.expense.doctormodule.databinding.ActivityVideoChatBinding
import com.quickblox.auth.session.QBSessionManager
import com.quickblox.chat.QBChatService
import com.quickblox.chat.QBRestChatService
import com.quickblox.chat.model.QBChatDialog
import com.quickblox.chat.model.QBDialogType
import com.quickblox.core.QBEntityCallback
import com.quickblox.core.exception.QBResponseException
import com.quickblox.users.model.QBUser
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



    private lateinit var mRtcClient: QBRTCClient
    private var qbSession: QBRTCSession? = null
    private lateinit var localVideoTrack: QBRTCVideoTrack

    private lateinit var eglBase: EglBase



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        appointmentID = intent.getStringExtra("APPOINTMENT_ID") ?: ""
        quickBloxDoctorID = intent.getStringExtra("QUICKBLOX_DOCTOR_ID") ?: ""
        quickBloxPatientID = intent.getStringExtra("QUICKBLOX_PATIENT_ID") ?: ""


        eglBase = EglBase.create()
        binding.localVideoView
        binding.remoteVideoView

        chatLogin()

    }

    override fun onDestroy() {
        super.onDestroy()
        eglBase.release()

    }


     fun chatLogin() {
         val user = QBUser()
         user.id = quickBloxDoctorID.toIntOrNull()
         user.password = "Bics@123"

         QBChatService.getInstance().login(user, object : QBEntityCallback<Void> {
             override fun onSuccess(aVoid: Void?, bundle: Bundle?) {
                 createChatDialogAndInitiateCall()

                 Log.d("VideoChatActivity", "Chatlogin  created successfully: ")

             }
             override fun onError(exception: QBResponseException?) {
                 Log.d("VideoChatActivity", "Chatlogin  created Failed: ")


             }
         })
     }
    fun sessioncraete(){
        val token = QBSessionManager.getInstance().token
        val id = quickBloxDoctorID

        val user = QBUser()
        user.id = quickBloxDoctorID.toIntOrNull()
        user.password = token

        QBChatService.getInstance().login(user, object : QBEntityCallback<Void> {
            override fun onSuccess(aVoid: Void, bundle: Bundle) {
                Log.d("VideoChatActivity", "sessioncraete  created successfully: ")
            }

            override fun onError(exception: QBResponseException) {
                Log.d("VideoChatActivity", "sessioncraete  created failed: ")

            }
        })
    }

    private fun initQBRTCClient() {
        mRtcClient = QBRTCClient.getInstance(applicationContext).apply {
            addSessionCallbacksListener(rtcSessionCallbacks)
            prepareToProcessCalls()
        }

        val chatService = QBChatService.getInstance()
        chatService.videoChatWebRTCSignalingManager?.addSignalingManagerListener { signaling, createdLocally ->
            if (!createdLocally) {
                mRtcClient.addSignaling(signaling)
            }
        } ?: run {
            Log.e("VideoChatActivity", "Signaling Manager is not initialized.")
            // Handle the error or notify the user
        }
    }

    private fun createChatDialogAndInitiateCall() {
        val occupantIdsList = arrayListOf(
            quickBloxDoctorID.toIntOrNull() ?: 0,
            quickBloxPatientID.toIntOrNull() ?: 0,
            140131296
        )

        val dialog = QBChatDialog().apply {
            name = appointmentID
            type = QBDialogType.GROUP
            setOccupantsIds(occupantIdsList)
        }

        QBRestChatService.createChatDialog(dialog).performAsync(object : QBEntityCallback<QBChatDialog> {
            override fun onSuccess(result: QBChatDialog?, bundle: Bundle?) {
                Log.d("VideoChatActivity", "Chat dialog created successfully: ${result?.dialogId}")
                initQBRTCClient()
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
            140131296
        )

        qbSession = mRtcClient.createNewSessionWithOpponents(opponents, QBRTCTypes.QBConferenceType.QB_CONFERENCE_TYPE_VIDEO)

        qbSession?.let { session ->
            session.startCall(userInfo)
            setUpVideoViews(session)
        }
    }

    private fun setUpVideoViews(session: QBRTCSession) {
        // Initialize local video view only if not already initialized
        if (!binding.localVideoView.isInitialized()) {
            binding.localVideoView.init(eglBase.eglBaseContext, null)
        }

        // Initialize remote video view only if not already initialized
        if (!binding.remoteVideoView.isInitialized()) {
            binding.remoteVideoView.init(eglBase.eglBaseContext, null)
        }

        session.addVideoTrackCallbacksListener(object : QBRTCClientVideoTracksCallbacks<QBRTCSession> {
            override fun onLocalVideoTrackReceive(session: QBRTCSession, videoTrack: QBRTCVideoTrack) {
                videoTrack.addRenderer(binding.localVideoView)
                binding.localVideoView.visibility = QBRTCSurfaceView.VISIBLE
            }

            override fun onRemoteVideoTrackReceive(session: QBRTCSession, videoTrack: QBRTCVideoTrack, userID: Int) {
                videoTrack.addRenderer(binding.remoteVideoView)
                binding.remoteVideoView.visibility = QBRTCSurfaceView.VISIBLE
            }
        })
    }

    // Extension function to check if SurfaceViewRenderer is initialized
    private fun QBRTCSurfaceView.isInitialized(): Boolean {
        return this.handler != null
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

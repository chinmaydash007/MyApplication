package com.example.demo

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.CompoundButton
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import com.getvisitapp.google_fit.IntiateSdk
import com.getvisitapp.google_fit.data.VisitStepSyncHelper
import com.getvisitapp.google_fit.event.ClosePWAEvent
import com.getvisitapp.google_fit.event.MessageEvent
import com.getvisitapp.google_fit.event.VisitEventType
import com.getvisitapp.google_fit.util.GoogleFitAccessChecker
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

//9978900304
class DemoActivity : AppCompatActivity(), CompoundButton.OnCheckedChangeListener {
    var TAG = this.javaClass.simpleName


    lateinit var checker: GoogleFitAccessChecker
    lateinit var switch: Switch

    val tataAIG_base_url = "https://uathealthvas.tataaig.com"
    val tataAIG_auth_token = "Basic Z2V0X3Zpc2l0OkZoNjh2JHdqaHU4WWd3NiQ="


    val default_client_id =
        "1050989325945-11eh51j1d0ocl945gmk583ai35ospog3.apps.googleusercontent.com"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_demo)


        findViewById<Button>(R.id.button).setOnClickListener {
            init()

        }

        switch = findViewById<Switch>(R.id.switch1)

        checker = GoogleFitAccessChecker(this)

        val syncStepHelper = VisitStepSyncHelper(context = this, default_client_id)
        syncStepHelper.syncSteps(tataAIG_base_url, tataAIG_auth_token)

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: MessageEvent?) {
        event?.let { eventType ->
            Log.d(TAG, "event:${event.eventType}")


            when (eventType.eventType) {
                VisitEventType.AskForFitnessPermission -> {

                }
                VisitEventType.AskForLocationPermission -> {


                }
                VisitEventType.FitnessPermissionGranted -> {


                }
                is VisitEventType.RequestHealthDataForDetailedGraph -> {

                    val graphEvent =
                        event.eventType as VisitEventType.RequestHealthDataForDetailedGraph

                }
                is VisitEventType.StartVideoCall -> {
                    val callEvent =
                        event.eventType as VisitEventType.StartVideoCall


                }
                is VisitEventType.HRA_Completed -> {


                }
                is VisitEventType.GoogleFitConnectedAndSavedInPWA -> {
                    Handler(Looper.getMainLooper()).postDelayed({
                        //passing event to Visit PWA to close itself
                        EventBus.getDefault().post(ClosePWAEvent())


                    }, 200)
                }
                is VisitEventType.HRAQuestionAnswered -> {
                    // can be used for analytics events
                    val hraQuestionEvent = event.eventType as VisitEventType.HRAQuestionAnswered
                    Log.d(
                        "mytag",
                        "current:${hraQuestionEvent.current} total:${hraQuestionEvent.total}"
                    )
                }
            }

        }

    }

    fun init() {
//        val magicLink = "https://tata-aig.getvisitapp.xyz"
        val magicLink =
            "https://axis-tata.getvisitapp.xyz/sso?userParams=DJsu6SFXLdsSNBuRr2Q83Z9Am1Y3EOEmKY2o4S4hvoYmjZxusiABb7D0nxr3bDtprg0o603IWvmMPOMT-IMuXa5faX64LueVOPmX15BC-WLpWDd8gXPrDUDBD9-w2u-u&clientId=tata-aig-axis-f7b4d3"
        IntiateSdk.s(
            this,
            false,
            magicLink,
            tataAIG_base_url,
            tataAIG_auth_token,
            default_client_id
        )
    }

    override fun onStart() {
        super.onStart()

        //unregister any previously registered event listener first before registering.
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
        EventBus.getDefault().register(this)
    }


    override fun onResume() {
        super.onResume()
        switch.setOnCheckedChangeListener(null)
        switch.isChecked = checker.checkGoogleFitAccess()
        switch.setOnCheckedChangeListener(this)
    }


    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        if (isChecked) {
            init()
        } else {
            checker.revokeGoogleFitPermission(default_client_id)
        }
    }


    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }


}
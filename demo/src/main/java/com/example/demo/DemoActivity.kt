package com.example.demo

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.getvisitapp.google_fit.IntiateSdk
import com.getvisitapp.google_fit.event.ClosePWAEvent
import com.getvisitapp.google_fit.event.MessageEvent
import com.getvisitapp.google_fit.event.VisitEventType
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

//9978900304
class DemoActivity : AppCompatActivity() {
    var TAG = this.javaClass.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_demo)


        findViewById<Button>(R.id.button).setOnClickListener {
            val magicLink = "https://tata-aig.getvisitapp.xyz"
            val baseUrlOfMagicLink = "https://tata-aig.getvisitapp.xyz/"
//
            val default_client_id =
                "1050989325945-11eh51j1d0ocl945gmk583ai35ospog3.apps.googleusercontent.com"




            IntiateSdk.s(this, false, magicLink, baseUrlOfMagicLink, default_client_id)

        }

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: MessageEvent?) {
        event?.let { eventType ->
            Log.d(TAG, "event:${event.eventType}")


            when (eventType.eventType) {
                VisitEventType.AskForFitnessPermission -> {}
                VisitEventType.AskForLocationPermission -> {}
                VisitEventType.FitnessPermissionGranted -> {}
                is VisitEventType.RequestHealthDataForDetailedGraph -> {
                    val graphEvent =
                        event.eventType as VisitEventType.RequestHealthDataForDetailedGraph
                }
                is VisitEventType.StartVideoCall -> {
                    val callEvent =
                        event.eventType as VisitEventType.StartVideoCall
                }
                is VisitEventType.HRA_Completed -> {
                    Handler(Looper.getMainLooper()).postDelayed({

                        //passing event to Visit PWA to close itself
                        EventBus.getDefault().post(ClosePWAEvent())


                    }, 200)
                }
            }

        }

    }

    override fun onStart() {
        super.onStart()

        //unregister any previously registered event listener first before registering.
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
        EventBus.getDefault().register(this)
    }


    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }


}
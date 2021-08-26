package com.deva.foldableapp

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.window.layout.DisplayFeature
import androidx.window.layout.FoldingFeature
import androidx.window.layout.WindowInfoRepository.Companion.windowInfoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        logMessage("On create")

        val windowInfoRepository = windowInfoRepository()

        findViewById<View>(R.id.btn_check).setOnClickListener {
            val windowState = windowInfoRepository()
            lifecycleScope.launch {
                val windowLayout = windowState.windowLayoutInfo.first()
                if (windowLayout.displayFeatures.isEmpty()) {
                    Toast.makeText(
                        this@MainActivity,
                        "State: Collapsed",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    (windowLayout.displayFeatures.getOrNull(0) as? FoldingFeature)?.let {
                        Toast.makeText(
                            this@MainActivity,
                            "State: "+it.state,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }


        lifecycleScope.launch(Dispatchers.Main) {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                windowInfoRepository.windowLayoutInfo.collect { newLayoutInfo ->
//                    logMessage("On folding feature changed: "+newLayoutInfo.displayFeatures)
//                    (newLayoutInfo.displayFeatures.getOrNull(0) as? FoldingFeature)?.let {
//                        logMessage("Is separating: "+it.isSeparating)
//                        logMessage("Orientation: "+it.orientation)
//                        logMessage("State: "+it.state)
//                        logMessage("Occlusion type: "+it.occlusionType)
//                        logMessage("Bounds: "+it.bounds)
//                        logMessage("--------------------------------------")
//                    }

                    if (isBookMode(newLayoutInfo.displayFeatures)) {
                        (newLayoutInfo.displayFeatures.get(0) as? FoldingFeature).state
                        findViewById<View>(R.id.view_fold).visibility = View.VISIBLE
                        findViewById<View>(R.id.tv_text_main).visibility = View.GONE
                    } else {
                        findViewById<View>(R.id.view_fold).visibility = View.GONE
                        findViewById<View>(R.id.tv_text_main).visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun isBookMode(displayFeatures: List<DisplayFeature>) = displayFeatures.isNotEmpty()

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        logMessage("On restore instance state")
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        logMessage("On configuration changed")
    }

    private fun logMessage(message: String) {
        Log.d("FoldableTest", message)
    }
}
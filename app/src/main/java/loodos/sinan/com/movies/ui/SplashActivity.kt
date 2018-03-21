package loodos.sinan.com.movies.ui

import android.animation.Animator
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.support.animation.DynamicAnimation
import android.support.animation.SpringAnimation
import android.support.animation.SpringForce
import android.support.v7.app.AppCompatActivity
import android.util.DisplayMetrics
import android.view.animation.DecelerateInterpolator
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import kotlinx.android.synthetic.main.activity_splash.*
import loodos.sinan.com.movies.BuildConfig
import loodos.sinan.com.movies.R
import loodos.sinan.com.movies.enums.Commons
import loodos.sinan.com.movies.receivers.InternetConnectionReceiver


class SplashActivity : AppCompatActivity(), OnCompleteListener<Void> {

    private val txtLogo by lazy { findViewById<TextView>(R.id.activity_splash_txtLogo) }
    private val mFirebaseRemoteConfig by lazy { FirebaseRemoteConfig.getInstance() }
    private val internetConnectionReceiver by lazy { InternetConnectionReceiver(this) }
    private lateinit var springForce: SpringForce

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val configSettings = FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build()

        mFirebaseRemoteConfig.setConfigSettings(configSettings)

        mFirebaseRemoteConfig.setDefaults(R.xml.remote_config)
    }

    // fetch from firebase remote config
    fun fetch() {
        var cacheExpiration: Long = 3600
        if (mFirebaseRemoteConfig.info.configSettings.isDeveloperModeEnabled) {
            cacheExpiration = 0
        }

        // addOnCompleteListener sends onComplete inteface
        mFirebaseRemoteConfig.fetch(cacheExpiration).addOnCompleteListener(this)
    }

    // Splash Activity Animation
    private fun showAnim() {
        Handler().postDelayed({
            springForce = SpringForce(0f)
            // linear_layout -> kotlin synthetic import
            linear_layout.pivotX = 0f
            linear_layout.pivotY = 0f
            val springAnim = SpringAnimation(linear_layout, DynamicAnimation.ROTATION).apply {
                springForce.dampingRatio = SpringForce.DAMPING_RATIO_HIGH_BOUNCY
                springForce.stiffness = SpringForce.STIFFNESS_VERY_LOW
            }
            springAnim.spring = springForce
            springAnim.setStartValue(80f)
            springAnim.addEndListener(object : DynamicAnimation.OnAnimationEndListener {
                override fun onAnimationEnd(animation: DynamicAnimation<out DynamicAnimation<*>>?, canceled: Boolean, value: Float, velocity: Float) {
                    // if internet is available then start animation
                    if (internetConnectionReceiver.isNetworkAvailable) {
                        val displayMetrics = DisplayMetrics()
                        windowManager.defaultDisplay.getMetrics(displayMetrics)
                        val height = displayMetrics.heightPixels.toFloat()
                        val width = displayMetrics.widthPixels
                        linear_layout.animate()
                                .setStartDelay(1)
                                .translationXBy(width.toFloat() / 2)
                                .translationYBy(height)
                                .setListener(object : Animator.AnimatorListener {
                                    override fun onAnimationRepeat(p0: Animator?) {

                                    }

                                    override fun onAnimationEnd(p0: Animator?) {
                                        val intent = Intent(applicationContext, MoviesActivity::class.java)
                                        finish()
                                        startActivity(intent)
                                        overridePendingTransition(0, 0)
                                    }

                                    override fun onAnimationCancel(p0: Animator?) {

                                    }

                                    override fun onAnimationStart(p0: Animator?) {

                                    }

                                })
                                .setInterpolator(DecelerateInterpolator(1f))
                                .start()
                    }
                }
            })
            springAnim.start()
        }, 3000)
    }

    override fun onResume() {

        super.onResume()

        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(internetConnectionReceiver, filter)
    }

    override fun onPause() {
        super.onPause()

        unregisterReceiver(internetConnectionReceiver)
    }

    // firebase remote config interface
    override fun onComplete(task: Task<Void>) {
        if (task.isSuccessful) {

            mFirebaseRemoteConfig.activateFetched()
            //linear_layout.setBackgroundColor(Color.parseColor(mFirebaseRemoteConfig.getString(Commons.SplashColor.toString())))
            txtLogo.text = mFirebaseRemoteConfig.getString(Commons.TextLogo.toString())
            showAnim()
        } else {
            Toast.makeText(this, Commons.LogoNotFound.toString(), Toast.LENGTH_SHORT).show()
        }
    }
}

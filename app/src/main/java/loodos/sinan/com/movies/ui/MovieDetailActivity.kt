package loodos.sinan.com.movies.ui

import android.content.IntentFilter
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import com.android.volley.*
import com.android.volley.toolbox.StringRequest
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.wang.avi.AVLoadingIndicatorView
import io.androidedu.weatherwidget.core.network.VolleyService
import loodos.sinan.com.movies.R
import loodos.sinan.com.movies.enums.ApiRequest
import loodos.sinan.com.movies.model.Movie
import loodos.sinan.com.movies.receivers.InternetConnectionReceiver
import java.io.IOException


class MovieDetailActivity : AppCompatActivity(), Response.Listener<String>, Response.ErrorListener {

    private val poster by lazy { findViewById<ImageView>(R.id.img_poster) }
    private val title by lazy { findViewById<TextView>(R.id.movie_title) }
    private val ratingBar by lazy { findViewById<RatingBar>(R.id.rating_bar) }
    private val rated by lazy { findViewById<TextView>(R.id.rated) }
    private val runtime by lazy { findViewById<TextView>(R.id.runtime) }
    private val released by lazy { findViewById<TextView>(R.id.released) }
    private val plot by lazy { findViewById<TextView>(R.id.plot) }
    private val country by lazy { findViewById<TextView>(R.id.country) }
    private val language by lazy { findViewById<TextView>(R.id.language) }
    private val genre by lazy { findViewById<TextView>(R.id.genre) }
    private val actors by lazy { findViewById<TextView>(R.id.actors) }
    private val director by lazy { findViewById<TextView>(R.id.director) }
    private val awards by lazy { findViewById<TextView>(R.id.awards) }
    private val progressBar by lazy { findViewById<AVLoadingIndicatorView>(R.id.progress_bar) }
    private val mFirebaseAnalytics by lazy { FirebaseAnalytics.getInstance(this) }
    private val internetConnectionReceiver by lazy { InternetConnectionReceiver(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_detail)

        supportActionBar!!.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(this, R.color.app_bar)))

        val imdbId = intent.getStringExtra(ApiRequest.ImdbId.toString())

        sendRequest(ApiRequest.MovieDetailUrl.toString() + imdbId + ApiRequest.ApiKey.toString())
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

    private fun sendRequest(url: String) {
        val request = StringRequest(Request.Method.GET, url, this, this)
        val retryPolicy: RetryPolicy = DefaultRetryPolicy(3000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        request.retryPolicy = retryPolicy
        VolleyService.build(this).requestQueue.add(request)
        VolleyService.build(this).requestQueue.start()
    }

    private fun castByGSon(response: String) {
        val movieDetailResponse = object : TypeToken<Movie>() {}.type
        val movieDetail = Gson().fromJson<Movie>(response, movieDetailResponse)
        loadData(movieDetail)
        firebaseLog(movieDetail)
    }

    private fun loadData(movie: Movie) {
        setTitle(movie.title)
        Glide.with(this).load(movie.poster).listener(object : RequestListener<Drawable> {
            override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                poster.setBackgroundResource(R.drawable.ic_broken_image)
                progressBar.visibility = View.GONE
                return false
            }

            override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                progressBar.visibility = View.GONE
                return false
            }
        }).into(poster)

        title.text = movie.title
        if (movie.imdbRating != "N/A")
            ratingBar.rating = movie.imdbRating.toFloat() / 2
        rated.text = movie.rated
        runtime.text = movie.runtime
        released.text = movie.released
        plot.text = movie.plot
        country.text = movie.country
        language.text = movie.language
        genre.text = movie.genre
        actors.text = movie.actors
        director.text = movie.director
        awards.text = movie.awards
    }

    private fun firebaseLog(movie: Movie) {
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, movie.imdbID)
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, movie.title)
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SEARCH, bundle)
    }

    override fun onResponse(response: String) {
        try {
            castByGSon(response)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun onErrorResponse(error: VolleyError?) {
        VolleyLog.e("onErrorResponse" + error?.message)
        Toast.makeText(this, error?.message, Toast.LENGTH_LONG).show()
    }
}

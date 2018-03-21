package loodos.sinan.com.movies.ui

import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.VolleyLog
import com.android.volley.toolbox.StringRequest
import com.google.gson.Gson
import com.mancj.materialsearchbar.MaterialSearchBar
import com.wang.avi.AVLoadingIndicatorView
import io.androidedu.weatherwidget.core.network.VolleyService
import loodos.sinan.com.movies.R
import loodos.sinan.com.movies.adapter.MoviesAdapter
import loodos.sinan.com.movies.enums.ApiRequest
import loodos.sinan.com.movies.enums.Commons
import loodos.sinan.com.movies.interfaces.MoviesClickListener
import loodos.sinan.com.movies.interfaces.OnLoadMoreListener
import loodos.sinan.com.movies.model.Search
import loodos.sinan.com.movies.receivers.InternetConnectionReceiver
import loodos.sinan.com.movies.response.SearchResponse

class MoviesActivity : AppCompatActivity(), MoviesClickListener, Response.Listener<String>, Response.ErrorListener, MaterialSearchBar.OnSearchActionListener, OnLoadMoreListener {

    private val searchBar by lazy { findViewById<MaterialSearchBar>(R.id.activity_movies_searchBar) }
    private val recyclerView by lazy { findViewById<RecyclerView>(R.id.activity_movies_recycler_view) }
    private val imgSearch by lazy { findViewById<ImageView>(R.id.activity_movies_imgSearch) }
    private val txtEmptyView by lazy { findViewById<TextView>(R.id.activity_movies_txtEmptyView) }
    private val progress by lazy { findViewById<AVLoadingIndicatorView>(R.id.activity_movies_progress_bar) }

    private val internetConnectionReceiver by lazy { InternetConnectionReceiver(this) }

    private val moviesAdapter by lazy { MoviesAdapter(this, searchList, this) }
    private var searchList: ArrayList<Search?>? = null
    private var isLastItem = false
    private var isLoading = false
    private var query = ""
    private var pageCount = 1
    private val onLoadMoreListener: OnLoadMoreListener = this
    private var isFirstTime = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movies)

        recyclerView.layoutManager = LinearLayoutManager(this)

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (dy < 0) {

                    isLastItem = false
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                if (newState == 1 && (recyclerView?.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
                        == recyclerView.adapter.itemCount - 1) {

                    isLastItem = true
                }

                if (!isLoading && newState == 0 && isLastItem) {

                    isLastItem = false

                    onLoadMoreListener.onLoadMore()

                    isLoading = true
                }
            }
        })

        searchBar.setOnSearchActionListener(this)
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
        VolleyService.build(this).requestQueue.add(request)
        VolleyService.build(this).requestQueue.start()
    }

    private fun emptyView() {
        recyclerView.visibility = View.GONE
        imgSearch.visibility = View.VISIBLE
        txtEmptyView.visibility = View.VISIBLE
    }

    private fun itemView() {
        recyclerView.visibility = View.VISIBLE
        imgSearch.visibility = View.GONE
        txtEmptyView.visibility = View.GONE
    }

    override fun onResponse(response: String) {

        isLoading = false
        val searchResponse: SearchResponse = Gson().fromJson(response, SearchResponse::class.java)

        try {
            if (searchResponse.response.equals(Commons.False.toString())) {

                if (searchResponse.error!!.contains(Commons.MovieNotFound.toString())) {
                    searchList?.removeAt(searchList?.size!!.minus(1))
                    moviesAdapter.notifyItemRemoved(searchList?.size!!)
                    progress.hide()
                    emptyView()
                    Toast.makeText(this, searchResponse.error, Toast.LENGTH_LONG).show()
                    return
                }
                progress.hide()
                emptyView()
                Toast.makeText(this, searchResponse.error, Toast.LENGTH_LONG).show()
                return
            }

            if (pageCount == 1) {
                searchList = searchResponse.search
                Toast.makeText(this, "${searchResponse.totalResults} Sonuç Bulundu", Toast.LENGTH_LONG).show()

                if (isFirstTime) {
                    recyclerView.adapter = moviesAdapter
                } else {
                    moviesAdapter.setSearchList(searchList!!)
                }

                isFirstTime = false
                itemView()
            } else {

                searchList?.removeAt(searchList?.size!!.minus(1))
                moviesAdapter.notifyItemRemoved(searchList?.size!!)
                searchList?.addAll(searchResponse.search!!)
                moviesAdapter.notifyItemInserted(searchResponse.search!!.size)
            }

        } catch (ex: KotlinNullPointerException) {
            //Log.e(this.toString(), ex.message)
            emptyView()
            Toast.makeText(this, searchResponse.error, Toast.LENGTH_LONG).show()
        }
        progress.hide()
    }

    override fun onErrorResponse(error: VolleyError?) {
        VolleyLog.e("onErrorResponse" + error?.message)
        Toast.makeText(this, error?.message, Toast.LENGTH_LONG).show()
    }

    override fun onMoviesClickListener(imdbId: String) {
        val intent = Intent(this, MovieDetailActivity::class.java)
        intent.putExtra(ApiRequest.ImdbId.toString(), imdbId)
        startActivity(intent)
    }

    override fun onButtonClicked(buttonCode: Int) {

    }

    override fun onSearchStateChanged(enabled: Boolean) {

    }

    override fun onSearchConfirmed(text: CharSequence?) {
        /*val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromInputMethod(searchBar.windowToken, 0)*/
        searchBar.disableSearch()
        recyclerView.visibility = View.GONE
        progress.show()
        val search = text.toString().trim()
        query = search.replace(" ", "%20")
        pageCount = 1
        sendRequest(ApiRequest.MoviesUrl.toString() + query + ApiRequest.ApiKey)
    }

    override fun onLoadMore() {

        searchList?.add(null)

        moviesAdapter.notifyItemInserted(searchList?.size!!.minus(1))
        recyclerView.smoothScrollToPosition(searchList!!.size)

        pageCount++

        sendRequest(ApiRequest.MoviesUrl.toString() + query + "&page=$pageCount" + ApiRequest.ApiKey)
    }
}

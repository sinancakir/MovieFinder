package loodos.sinan.com.movies.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import loodos.sinan.com.movies.R
import loodos.sinan.com.movies.holder.LoadingViewHolder
import loodos.sinan.com.movies.holder.MoviesViewHolder
import loodos.sinan.com.movies.interfaces.MoviesClickListener
import loodos.sinan.com.movies.model.Search

/**
 * Created by sinan on 17.03.2018.
 */

class MoviesAdapter(private val context: Context,
                    private var searchList: ArrayList<Search?>?,
                    private val moviesClickListener: MoviesClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_ITEM = 0
    private val VIEW_TYPE_LOADING = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return if (viewType == VIEW_TYPE_ITEM) {

            val view = LayoutInflater.from(parent.context).inflate(R.layout.movie_list_item, parent, false)
            MoviesViewHolder(view)

        } else {

            val view = LayoutInflater.from(parent.context).inflate(R.layout.load_more_list_item, parent, false)
            LoadingViewHolder(view)
        }
    }

    override fun getItemCount(): Int{

        return if(searchList == null || searchList?.size == null){
            0
        }else{
            searchList?.size!!
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (holder is LoadingViewHolder) {

            holder.progressBar.isIndeterminate = true

        } else if (holder is MoviesViewHolder) {

            val searchList = searchList!![position]
            holder.title.text = searchList?.title
            holder.released.text = searchList?.year
            holder.type.text = searchList?.type
            holder.imageProgress.visibility = View.VISIBLE
            holder.itemView.setOnClickListener { moviesClickListener.onMoviesClickListener(searchList!!.imdbID) }

            Glide.with(context).load(searchList?.poster).listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                    holder.imageProgress.visibility = View.GONE
                    holder.poster.setBackgroundResource(R.drawable.ic_broken_image)
                    return false
                }

                override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                    holder.imageProgress.visibility = View.GONE
                    return false
                }

            }).into(holder.poster)
        }
    }

    fun setSearchList(searchList: ArrayList<Search?>) {

        this.searchList = searchList

        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {

        return if (searchList!![position] == null) {

            VIEW_TYPE_LOADING
        } else {

            VIEW_TYPE_ITEM
        }
    }
}
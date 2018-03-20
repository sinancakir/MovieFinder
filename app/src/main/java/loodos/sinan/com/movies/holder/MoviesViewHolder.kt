package loodos.sinan.com.movies.holder

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.wang.avi.AVLoadingIndicatorView
import loodos.sinan.com.movies.R

/**
 * Created by sinan on 17.03.2018.
 */

class MoviesViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val poster by lazy { view.findViewById<ImageView>(R.id.movie_list_item_poster) }
    val title by lazy { view.findViewById<TextView>(R.id.movie_list_item_title) }
    val released by lazy { view.findViewById<TextView>(R.id.movie_list_item_released) }
    val type by lazy { view.findViewById<TextView>(R.id.movie_list_item_type) }
    val imageProgress by lazy { view.findViewById<AVLoadingIndicatorView>(R.id.movie_list_progress_bar) }
}
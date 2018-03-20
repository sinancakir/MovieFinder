package loodos.sinan.com.movies.holder

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ProgressBar
import loodos.sinan.com.movies.R

/**
 * Created by sinan on 17.03.2018.
 */

class LoadingViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    val progressBar by lazy { view.findViewById<ProgressBar>(R.id.load_more_list_item_progressBar) }
}
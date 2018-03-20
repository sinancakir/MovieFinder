package loodos.sinan.com.movies.response

import com.google.gson.annotations.SerializedName
import loodos.sinan.com.movies.model.Search

/**
 * Created by sinan on 17.03.2018.
 */
class SearchResponse {
    @SerializedName("Search")
    val search: ArrayList<Search?>? = null

    @SerializedName("totalResults")
    val totalResults: String? = null

    @SerializedName("Response")
    val response: String? = null

    @SerializedName("Error")
    val error: String? = null
}


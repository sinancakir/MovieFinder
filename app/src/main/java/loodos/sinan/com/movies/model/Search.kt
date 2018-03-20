package loodos.sinan.com.movies.model

import com.google.gson.annotations.SerializedName

/**
 * Created by sinan on 17.03.2018.
 */

data class Search(@SerializedName("Title") val title: String,
                  @SerializedName("Year") val year: String,
                  @SerializedName("imdbID") val imdbID: String,
                  @SerializedName("Type") val type: String,
                  @SerializedName("Poster") val poster: String
)
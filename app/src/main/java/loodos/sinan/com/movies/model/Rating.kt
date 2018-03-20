package loodos.sinan.com.movies.model


import com.google.gson.annotations.SerializedName

/**
 * Created by sinan on 17.03.2018.
 */

data class Rating(@SerializedName("Source") val source: String,
                  @SerializedName("Value") val value: String)
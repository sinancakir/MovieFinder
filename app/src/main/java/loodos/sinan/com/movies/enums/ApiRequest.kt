package loodos.sinan.com.movies.enums

/**
 * Created by sinan on 17.03.2018.
 */
enum class ApiRequest {

    ApiKey {
        override fun toString(): String {
            return "&apikey=f61b9108"
        }
    },

    MoviesUrl {
        override fun toString(): String {
            return "http://www.omdbapi.com/?s="
        }
    },
    MovieDetailUrl {
        override fun toString(): String {
            return "http://www.omdbapi.com/?i="
        }
    }
}
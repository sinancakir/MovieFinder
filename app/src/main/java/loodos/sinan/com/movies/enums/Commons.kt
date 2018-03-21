package loodos.sinan.com.movies.enums

/**
 * Created by sinan on 21.03.2018.
 */

enum class Commons {
    TextLogo {
        override fun toString(): String {
            return "textLogo"
        }
    },
    LogoNotFound {
        override fun toString(): String {
            return "Logo Yazdırılamadı!"
        }
    },
    SplashColor {
        override fun toString(): String {
            return "backgroundColor"
        }
    },
    False {
        override fun toString(): String {
            return "False"
        }
    },
    MovieNotFound {
        override fun toString(): String {
            return "Movie not found"
        }
    }
}
package simple.peng.btv

import android.util.Log
import org.jsoup.Jsoup
import java.net.URL

object ParseUtils {

    fun decode(url: String): Pair<String, String> {
        val connect = Jsoup.connect(url)
        val document = connect.get()
        val title = document.title()
        val location = document.location()
        val longURL = URL(location)
        val shortUrl = longURL.host + longURL.path

        Log.d("ParseUtils", "title: $title")
//        Log.d("ParseUtils", "location: $location")
        Log.d("ParseUtils", "shortUrl: $shortUrl")

        return Pair(title, shortUrl)
    }
}
package com.example

import androidx.appcompat.app.AppCompatActivity
import com.lagradost.cloudstream3.TvType
import com.lagradost.cloudstream3.MainAPI
import android.util.Log
import com.lagradost.cloudstream3.MovieSearchResponse
import com.lagradost.cloudstream3.SearchResponse
import com.lagradost.cloudstream3.app
import com.lagradost.cloudstream3.fixUrlNull
import com.lagradost.cloudstream3.utils.AppUtils.toJson
import com.lagradost.cloudstream3.utils.AppUtils.tryParseJson
import com.lagradost.cloudstream3.utils.ExtractorLink
import com.lagradost.cloudstream3.utils.loadExtractor
import com.lagradost.cloudstream3.getQualityFromString

class ExampleProvider(val plugin: TestPlugin) : MainAPI() { // all providers must be an intstance of MainAPI
    override var mainUrl = "https://toonitalia.green"
    override var name = "Lucio Provider"
    override val supportedTypes = setOf(TvType.Cartoon)

    override var lang = "it"

    // enable this when your provider has a main page
//    override val hasMainPage = true

    // this function gets called when you search for something
    override suspend fun search(query: String): List<SearchResponse> {
        val url = "$mainUrl/?s=${query}"
        val document = app.get(url).document.selectFirst("main.site-main")

        return document?.mapNotNull {
            val inner = it.select("article") ?: return@mapNotNull null
            val link = fixUrlNull(inner.select("h2 > a")?.attr("href")) ?: return@mapNotNull null

            val title = inner.select("h2")?.text() ?: ""
            val image = inner.select("p.cont-img > a > img")?.attr("src")

            MovieSearchResponse(
                    name = title,
                    url = link,
                    apiName = this.name,
                    TvType.Movie,
                    posterUrl = image,
            )
        }?.distinctBy { c -> c.url } ?: listOf()
    }
}

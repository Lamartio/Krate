/*
 * Copyright (c) 2018 Danny Lamarti.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.lamart.krate.example

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.LinearLayout.VERTICAL
import android.widget.TextView
import io.lamart.krate.Krate
import io.reactivex.MaybeObserver
import io.reactivex.disposables.Disposable
import okhttp3.OkHttpClient
import kotlin.properties.Delegates


class ExampleActivity : AppCompatActivity() {

    var quoteText: TextView? = null
    var quoteButton: Button? = null
    var clickButton: Button? = null

    private val httpClient by lazy { OkHttpClient() }
    private val krate: Krate by lazy { application.let { it as ExampleApplication }.krate }
    private var clicks: Int? by Delegates.observable<Int?>(null) { _, _, next ->
        next?.let { clicks ->
            clickButton?.run {
                text = "${clicks} clicks"
                isEnabled = true
            }
        }
    }
    private var quote: Quote? by Delegates.observable<Quote?>(null) { _, _, next ->
        next?.let { quote ->
            quoteText?.run {
                text = "${quote.quote}\n\t- ${quote.author}"
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView()
        krate.get<Int>(CLICKS)
                .toSingle(0)
                .subscribe { value -> clicks = value }

        getAndFetchQuote()
    }

    private fun getAndFetchQuote() = getAndFetchQuote(
            krate,
            httpClient,
            { quoteButton?.isEnabled = false },
            { quote = it },
            { quoteButton?.isEnabled = true },
            { quoteButton?.isEnabled = true }
    )

    private fun setContentView() {
        +LinearLayout(this).children { layout ->

            layout.orientation = VERTICAL
            layout.gravity = Gravity.CENTER

            quoteText = +TextView(this)
            quoteButton = +Button(this).apply {
                text = "Fetch quote"
                setOnClickListener { getAndFetchQuote() }
            }

            +TextView(this).apply {
                text = "Click to increment (and persist) the click count."
            }

            clickButton = +Button(this).apply {
                isEnabled = clicks != null
                setOnClickListener { incrementLocalClicks() }
            }

        }
    }

    private fun incrementLocalClicks() {
        clicks
                ?.let { krate.put("clicks", it + 1).andThen(krate.get<Int>("clicks")) }
                ?.subscribe(object : MaybeObserver<Int> {

                    override fun onSubscribe(d: Disposable) {
                        clickButton?.isEnabled = false
                    }

                    override fun onSuccess(clicks: Int) {
                        this@ExampleActivity.clicks = clicks
                        clickButton?.isEnabled = true
                    }

                    override fun onComplete() {
                        clickButton?.isEnabled = true
                    }

                    override fun onError(e: Throwable) {
                        Log.e(this@ExampleActivity::class.java.name,"clicks", e)
                        clickButton?.isEnabled = true
                    }

                })
    }

    operator fun <V : View> V.unaryPlus() = also(::setContentView)

    companion object {

        const val CLICKS = "clicks"
        const val LATEST_QUOTE = "lates_quote"

    }

}


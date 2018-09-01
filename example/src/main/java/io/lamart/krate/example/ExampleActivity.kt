package io.lamart.krate.example

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.LinearLayout.VERTICAL
import android.widget.TextView
import io.lamart.krate.Krate
import io.reactivex.MaybeObserver
import io.reactivex.disposables.Disposable
import kotlin.properties.Delegates

class ExampleActivity : AppCompatActivity() {

    var count: TextView? = null
    var button: Button? = null

    private val krate: Krate by lazy { application.let { it as ExampleApplication }.krate }
    private var clicks: Int? by Delegates.observable<Int?>(null) { _, _, next ->
        next?.let { clicks ->
            count?.run {
                text = "You clicked: ${clicks} times!"
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        krate.get<Int>("clicks")
                .toSingle(0)
                .subscribe { result -> clicks = result }

        +LinearLayout(this).children { layout ->

            layout.orientation = VERTICAL
            layout.gravity = Gravity.CENTER

            count = +TextView(this)
            button = +Button(this).apply {
                text = "click"
                isEnabled = clicks != null
                setOnClickListener { incrementLocalClicks() }
            }

        }
    }

    private fun incrementLocalClicks() {
        clicks
                ?.let { krate.put("clicks", it).andThen(krate.get<Int>("clicks")) }
                ?.subscribe(object : MaybeObserver<Int> {

                    override fun onSubscribe(d: Disposable) {
                        button?.run { isEnabled = false }
                    }

                    override fun onSuccess(clicks: Int) {
                        this@ExampleActivity.clicks = clicks
                        button?.run { isEnabled = true }
                    }
                    override fun onComplete() {
                        button?.run { isEnabled = true }
                    }

                    override fun onError(e: Throwable) {
                        button?.run { isEnabled = true }
                    }

                })
    }

    operator fun <V : View> V.unaryPlus() = also(::setContentView)

}


package io.lamart.krate.example

import com.google.gson.Gson
import com.google.gson.stream.JsonReader
import io.lamart.krate.Krate
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.Request

fun OkHttpClient.fetchQuote(): Single<Quote> =
        Single
                .fromCallable {
                    Request.Builder()
                            .url("https://talaikis.com/api/quotes/random/")
                            .build()
                            .let(::newCall)
                            .execute()
                            .body()!!
                            .charStream()
                            .let(::JsonReader)
                            .use { Gson().fromJson<Quote>(it, Quote::class.java) }
                }
                .subscribeOn(Schedulers.io())

fun getAndFetchQuote(
        krate: Krate,
        client: OkHttpClient,
        onSubscribe: (Disposable) -> Unit,
        onNext: (Quote) -> Unit,
        onError: (Throwable) -> Unit,
        onComplete: () -> Unit
) {
    krate.getAndFetch(ExampleActivity.LATEST_QUOTE, client::fetchQuote)
            .toObservable()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(onNext, onError, onComplete, onSubscribe)
}
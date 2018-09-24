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

import com.google.gson.Gson
import com.google.gson.stream.JsonReader
import io.lamart.krate.Krate
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import okhttp3.OkHttpClient
import okhttp3.Request

fun OkHttpClient.fetchQuote(): Single<Quote> =
        Single.fromCallable {
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

fun getAndFetchQuote(
        krate: Krate,
        client: OkHttpClient,
        onSubscribe: (Disposable) -> Unit,
        onNext: (Quote) -> Unit,
        onError: (Throwable) -> Unit,
        onComplete: () -> Unit
): Disposable =
        krate.getAndFetch(ExampleActivity.LATEST_QUOTE, client::fetchQuote)
                .toObservable()
                .subscribe(
                        onNext,
                        onError,
                        onComplete,
                        onSubscribe
                )
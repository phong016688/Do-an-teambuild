package com.phong.teamcnpm.teambuilding.data.source.remote.intercep

import android.util.Log
import com.phong.teamcnpm.teambuilding.BuildConfig
import com.phong.teamcnpm.teambuilding.data.source.local.preference.SharedPrefsApi
import com.phong.teamcnpm.teambuilding.data.source.local.preference.SharedPrefsKey
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import java.net.HttpURLConnection
import java.util.*

class InterceptorImpl(private val prefsApi: SharedPrefsApi) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = initializeRequestWithHeaders(chain.request())
        val response = chain.proceed(request) //tien hanh request tra ve response
        val responseCode = response.code()
        val responseBody = response.body()
        val responseBodyString = response.body()?.string()
        if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
            prefsApi.clearKey(SharedPrefsKey.KEY_TOKEN)
            prefsApi.clearKey(SharedPrefsKey.ROLE_CURRENT_USER)
            prefsApi.clearKey(SharedPrefsKey.CURRENT_USER_ID)
        }
        if (BuildConfig.DEBUG) {
            Log.d(
                this::class.java.simpleName,
                "response: {responseCode: $responseCode responseBody: $responseBodyString}"
            )
        }
        return createNewResponse(response, responseBody, responseBodyString)
    }

    // handle request
    //tao request tu request cu
    private fun initializeRequestWithHeaders(request: Request): Request {
        val accessToken = prefsApi.get(SharedPrefsKey.KEY_TOKEN, String::class.java)
        return request.newBuilder()
            .header("Content-Type", "application/x-www-form-urlencoded")
            .addHeader("Authorization", "Bearer $accessToken")
            .addHeader("Accept-Language", Locale.getDefault().language)
            .method(request.method(), request.body())
            .build()
    }

    //handle response
    //tao response tu response cu
    private fun createNewResponse(
        response: Response,
        responseBody: ResponseBody?,
        responseBodyString: String?
    ): Response {
        val contentType = responseBody?.contentType()
        return response.newBuilder()
            .body(ResponseBody.create(contentType, responseBodyString ?: ""))
            .build()
    }
}

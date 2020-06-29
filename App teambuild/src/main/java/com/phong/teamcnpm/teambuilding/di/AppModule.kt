package com.phong.teamcnpm.teambuilding.di

import android.content.Context
import androidx.room.Room
import com.google.gson.Gson
import com.phong.teamcnpm.teambuilding.BuildConfig
import com.phong.teamcnpm.teambuilding.data.repository.AuthenticationRepositoryImpl
import com.phong.teamcnpm.teambuilding.data.repository.EventRepositoryImpl
import com.phong.teamcnpm.teambuilding.data.repository.GroupRepositoryImpl
import com.phong.teamcnpm.teambuilding.data.source.local.preference.SharedPrefsApi
import com.phong.teamcnpm.teambuilding.data.source.local.preference.SharedPrefsApiImpl
import com.phong.teamcnpm.teambuilding.data.source.local.room.MyAppDao
import com.phong.teamcnpm.teambuilding.data.source.remote.intercep.InterceptorImpl
import com.phong.teamcnpm.teambuilding.data.source.remote.service.RestfulApi
import com.phong.teamcnpm.teambuilding.domain.repository.AuthenticationRepository
import com.phong.teamcnpm.teambuilding.domain.repository.EventRepository
import com.phong.teamcnpm.teambuilding.domain.repository.GroupRepository
import com.phong.teamcnpm.teambuilding.ui.group.GroupDetailPresenter
import com.phong.teamcnpm.teambuilding.ui.group.GroupDetailPresenterImpl
import com.phong.teamcnpm.teambuilding.ui.group.GroupDetailView
import com.phong.teamcnpm.teambuilding.ui.group.comment_event.CommentPresenter
import com.phong.teamcnpm.teambuilding.ui.group.comment_event.CommentPresenterImpl
import com.phong.teamcnpm.teambuilding.ui.group.comment_event.CommentView
import com.phong.teamcnpm.teambuilding.ui.group.newevent.NewEventPresenter
import com.phong.teamcnpm.teambuilding.ui.group.newevent.NewEventPresenterImpl
import com.phong.teamcnpm.teambuilding.ui.group.newevent.NewEventView
import com.phong.teamcnpm.teambuilding.ui.group.show_vote.ShowVotePresenter
import com.phong.teamcnpm.teambuilding.ui.group.show_vote.ShowVotePresenterImpl
import com.phong.teamcnpm.teambuilding.ui.group.show_vote.ShowVoteView
import com.phong.teamcnpm.teambuilding.ui.group.update_event.UpdateEventPresenter
import com.phong.teamcnpm.teambuilding.ui.group.update_event.UpdateEventPresenterImpl
import com.phong.teamcnpm.teambuilding.ui.group.update_event.UpdateEventView
import com.phong.teamcnpm.teambuilding.ui.login.LoginPresenter
import com.phong.teamcnpm.teambuilding.ui.login.LoginPresenterImpl
import com.phong.teamcnpm.teambuilding.ui.login.LoginView
import com.phong.teamcnpm.teambuilding.ui.main.MainPresenter
import com.phong.teamcnpm.teambuilding.ui.main.MainPresenterImpl
import com.phong.teamcnpm.teambuilding.ui.main.MainView
import com.phong.teamcnpm.teambuilding.ui.main.event.EventPresenter
import com.phong.teamcnpm.teambuilding.ui.main.event.EventPresenterImpl
import com.phong.teamcnpm.teambuilding.ui.main.event.EventView
import com.phong.teamcnpm.teambuilding.ui.main.group.GroupPresenter
import com.phong.teamcnpm.teambuilding.ui.main.group.GroupPresenterImpl
import com.phong.teamcnpm.teambuilding.ui.main.group.GroupView
import com.phong.teamcnpm.teambuilding.ui.main.setting.SettingPresenter
import com.phong.teamcnpm.teambuilding.ui.main.setting.SettingPresenterImpl
import com.phong.teamcnpm.teambuilding.ui.main.setting.SettingView
import com.phong.teamcnpm.teambuilding.utils.validator.Validator
import com.phong.teamcnpm.teambuilding.widgets.DialogManager
import com.phong.teamcnpm.teambuilding.widgets.DialogManagerImpl
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object AppModule {
  val appModule = module {
    single<SharedPrefsApi> { SharedPrefsApiImpl(androidContext()) }
    single<Interceptor> { InterceptorImpl(get()) }
    single { buildGSON() }
    single { createRetrofit(get(), buildOKHttpClient(androidContext(), get())) }
    single { Validator(androidContext()) }
    single { providerDao(androidContext()) }
    factory<DialogManager> { (context: Context) -> DialogManagerImpl(context) }

    //Presenter
    factory<LoginPresenter> { (view: LoginView) -> LoginPresenterImpl(view, get()) }
    factory<GroupPresenter> { (view: GroupView) -> GroupPresenterImpl(view, get(), get()) }
    factory<EventPresenter> { (view: EventView) -> EventPresenterImpl(view, get(), get()) }
    factory<SettingPresenter> { (view: SettingView) -> SettingPresenterImpl(view, get()) }
    factory<MainPresenter> { (view: MainView) -> MainPresenterImpl(view, get(), get(), get()) }
    factory<NewEventPresenter> { (view: NewEventView) -> NewEventPresenterImpl(view, get()) }
    factory<CommentPresenter> { (view: CommentView) -> CommentPresenterImpl(view, get()) }
    factory<ShowVotePresenter> { (view: ShowVoteView) -> ShowVotePresenterImpl(view, get()) }
    factory<UpdateEventPresenter> { (view: UpdateEventView) -> UpdateEventPresenterImpl(view, get()) }
    factory<GroupDetailPresenter> { (view: GroupDetailView) ->
      GroupDetailPresenterImpl(
        view,
        get(),
        get(),
        get()
      )
    }

    //Repository
    single<AuthenticationRepository>(createdAtStart = true) {
      AuthenticationRepositoryImpl(
        get(),
        get(),
        get()
      )
    }
    single<GroupRepository> { GroupRepositoryImpl(get(), get()) }
    single<EventRepository> { EventRepositoryImpl(get(), get()) }
  }

  private fun providerDao(context: Context): MyAppDao {
    return Room
      .databaseBuilder(context, MyAppDao::class.java, "teambuildingdb")
      .build()
  }

  private fun createRetrofit(gson: Gson, okHttpClient: OkHttpClient): RestfulApi {
    return Retrofit.Builder()
      .baseUrl(BuildConfig.BASE_URL)
      .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
      .addConverterFactory(GsonConverterFactory.create(gson))
      .client(okHttpClient)
      .build()
      .create(RestfulApi::class.java)
  }

  private fun buildOKHttpClient(context: Context, interceptor: Interceptor): OkHttpClient {
    return OkHttpClient.Builder().apply {
      cache(Cache(context.applicationContext.cacheDir, 10 * 1024 * 1024))
      addInterceptor(interceptor)
      readTimeout(30L, TimeUnit.SECONDS)
      connectTimeout(30L, TimeUnit.SECONDS)
      if (BuildConfig.DEBUG) {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        addInterceptor(logging)
      }
    }.build()
  }

  private fun buildGSON(): Gson = Gson()
}
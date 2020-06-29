package com.phong.teamcnpm.teambuilding.ui.main.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.phong.teamcnpm.teambuilding.R
import com.phong.teamcnpm.teambuilding.data.source.local.preference.SharedPrefsApi
import com.phong.teamcnpm.teambuilding.domain.entities.Notification
import io.reactivex.rxjava3.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_home.view.*
import org.koin.android.ext.android.inject

class FragmentHome : Fragment(), HomeView {
  private lateinit var mAdapter: HomeAdapter
  private val mCompositeDisposable = CompositeDisposable()
  private val mPrefsApi: SharedPrefsApi by inject()

  override val disposables: CompositeDisposable
    get() = mCompositeDisposable

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.fragment_home, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    val listFake = (1..10)
      .map {
        val content = if (it == 1) "You have been added to group Group1" else "\n" +
            "There is a new announcement in group Group1"
        val image = "https://file.vforum.vn/hinh/2016/04/telasm-hinh-nen-hot-girl-viet-nam-1.jpg"
        Notification("$it", "Group1", content, image)
      }
    mAdapter = HomeAdapter(mCompositeDisposable, mPrefsApi)
    view.homeRecyclerView.adapter = mAdapter
    mAdapter.submitList(listFake)
  }

  override fun onDestroy() {
    mCompositeDisposable.clear()
    super.onDestroy()
  }


  override fun onGetError(throwable: Throwable) = Unit

  companion object {
    private val TAG = this::class.java.simpleName

    @JvmStatic
    fun newInstance() =
      FragmentHome().apply {
        arguments = Bundle().apply {}
      }
  }


}

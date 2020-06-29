package com.phong.teamcnpm.teambuilding.ui.main.group

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.jakewharton.rxbinding4.view.clicks
import com.phong.teamcnpm.teambuilding.R
import com.phong.teamcnpm.teambuilding.domain.entities.Group
import com.phong.teamcnpm.teambuilding.ui.group.GroupDetailActivity
import com.phong.teamcnpm.teambuilding.utils.extensions.addTo
import com.phong.teamcnpm.teambuilding.utils.extensions.loadFromUrl
import io.reactivex.rxjava3.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_group.*
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class FragmentGroup : Fragment(), GroupView {
  private val mPresenter: GroupPresenter by inject { parametersOf(this) }
  private val mCompositeDisposable = CompositeDisposable()
  private var mGroupId: String = ""

  override val disposables: CompositeDisposable
    get() = mCompositeDisposable

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.fragment_group, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    mPresenter.observeGroupFromLocale()
    Log.d("####", "create ${this::class.java.simpleName}")
    handleEvents()
  }

  override fun getGroupSuccess(group: Group) {
    imageGroupView.loadFromUrl(group.avatar)
    titleGroupTextView.text = group.name
    descriptionGroupTextView.text = group.description
    mGroupId = group.id
  }

  override fun onDestroy() {
    mCompositeDisposable.clear()
    super.onDestroy()
  }

  override fun onGetError(throwable: Throwable) {
    Log.d(this::class.java.simpleName, throwable.message.toString())
  }

  private fun handleEvents() {
    groupContainer
      .clicks()
      .subscribe {
        activity?.also {
          if (mGroupId.isNotEmpty())
            startActivity(GroupDetailActivity.getInstance(it, mGroupId))
        }
      }.addTo(mCompositeDisposable)
  }

  companion object {
    @JvmStatic
    fun newInstance() =
      FragmentGroup().apply {
        arguments = Bundle().apply {}
      }
  }
}

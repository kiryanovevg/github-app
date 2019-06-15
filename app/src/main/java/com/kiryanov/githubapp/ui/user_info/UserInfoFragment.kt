package com.kiryanov.githubapp.ui.user_info

import android.os.Bundle
import android.transition.ChangeBounds
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionInflater
import com.arellomobile.mvp.MvpAppCompatFragment
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.kiryanov.githubapp.LOGIN
import com.kiryanov.githubapp.R
import com.kiryanov.githubapp.TRANSITION_ITEM
import com.kiryanov.githubapp.adapter.UserRepoAdapter
import com.kiryanov.githubapp.model.UserMore
import kotlinx.android.synthetic.main.fragment_user_info.*
import kotlinx.android.synthetic.main.shimmer_user_info.*

class UserInfoFragment : MvpAppCompatFragment(), UserInfoView {

    private lateinit var adapter: UserRepoAdapter

    @InjectPresenter
    lateinit var presenter: UserInfoPresenter

    @ProvidePresenter
    fun providePresenter() = UserInfoPresenter(login)

    private val login: String get() = arguments
        ?.getString(LOGIN)
        ?: throw IllegalArgumentException("Login not specified")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
        sharedElementReturnTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_user_info, container, false).apply {
            transitionName = TRANSITION_ITEM
//            sharedElementEnterTransition = ChangeBounds().apply { duration = 750 }
//            sharedElementReturnTransition = ChangeBounds().apply { duration = 750 }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        postponeEnterTransition()

        setHasOptionsMenu(true)
        (activity as? AppCompatActivity)?.supportActionBar?.title = login
        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onDestroy() {
        setHasOptionsMenu(false)
        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        (activity as? AppCompatActivity)?.supportActionBar?.title = getString(R.string.app_name)
        super.onDestroy()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> findNavController().navigateUp()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun setUserInfo(user: UserMore) {
        context?.also {
            Glide.with(iv_avatar)
                .load(user.avatarUrl)
                .apply(RequestOptions.circleCropTransform())
                .into(iv_avatar)
        }

        tv_name.text = user.name
        tv_login.text = user.login

        location.visibility = if (user.location != null) View.VISIBLE else View.GONE
        company.visibility = if (user.company != null) View.VISIBLE else View.GONE

        tv_location.text = user.location
        tv_company.text = user.company

        tv_repo.text = getString(R.string.repositories_count, user.repositories)
        tv_followers.text = getString(R.string.followers_count, user.followers)
        tv_following.text = getString(R.string.following_count, user.following)

        adapter = UserRepoAdapter()
        adapter.addAll(user.repos)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        recyclerView.layoutAnimation = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_fall_down)
        recyclerView.addItemDecoration(DividerItemDecoration(
            recyclerView.context,
            (recyclerView.layoutManager as LinearLayoutManager).orientation
        ))

    }

    override fun showLoadingError() {
        shimmer_view_container.stopShimmer()
        Toast.makeText(context, R.string.loading_error, Toast.LENGTH_LONG).show()
    }

    override fun setLoadingProgressVisibility(visibility: Boolean) {
        if (visibility) {
            view_container.visibility = View.GONE
            shimmer_view_container.visibility = View.VISIBLE
        } else {
            view_container.visibility = View.VISIBLE
            shimmer_view_container.visibility = View.GONE
        }
    }
}
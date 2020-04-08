package com.kiryanov.githubapp.ui.user_list

import android.os.Bundle
import android.view.*
import android.view.animation.AnimationUtils
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.lifecycle.Observer
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arellomobile.mvp.MvpAppCompatFragment
import com.arellomobile.mvp.presenter.InjectPresenter
import com.kiryanov.githubapp.LOGIN
import com.kiryanov.githubapp.R
import com.kiryanov.githubapp.TRANSITION_ITEM
import com.kiryanov.githubapp.adapter.NetworkState
import com.kiryanov.githubapp.adapter.UserAdapter
import com.kiryanov.githubapp.model.User
import com.kiryanov.githubapp.visibility
import kotlinx.android.synthetic.main.fragment_user_list.*
import kotlinx.android.synthetic.main.item_user.view.*

class UserListFragment : MvpAppCompatFragment(), UserListView {

    @InjectPresenter
    lateinit var presenter: UserListPresenter

    private lateinit var adapter: UserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_user_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        refresh_layout.setOnRefreshListener { presenter.loadUsers() }
        btn_retry.setOnClickListener { presenter.loadRetry() }

        adapter = UserAdapter()
        adapter.retryCallback = { presenter.loadRetry() }
        adapter.onItemClickListener = { user, itemView -> showUserInfo(user, itemView) }

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        recyclerView.layoutAnimation = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_fall_down)
    }

    override fun onDestroy() {
        setHasOptionsMenu(false)
        super.onDestroy()
    }

    override fun setUserList(pagedList: PagedList<User>) {
        adapter.submitList(pagedList)
    }

    override fun setRefreshing(loading: Boolean) {
        refresh_layout.isRefreshing = loading
    }

    override fun setRefreshingErrorVisibility(visibility: Boolean, message: String?) {
        error_layout.visibility(visibility)
        refresh_layout.isEnabled = !visibility
        tv_message.text = message
    }

    override fun setLoadingState(networkState: NetworkState?) {
        adapter.networkState = networkState
    }

    private fun showUserInfo(user: User, itemView: View) {
        ViewCompat.setTransitionName(itemView.item_root, TRANSITION_ITEM)
        val extras = FragmentNavigatorExtras(itemView to itemView.transitionName)

        findNavController().navigate(
            R.id.action_userListFragment_to_userInfoFragment,
            Bundle().apply { putString(LOGIN, user.login) },
            null,
            extras
        )
    }

    override fun showSettingsDialog() {
        SettingsDialogFragment.create(
            object : SettingsDialogFragment.ClickListener {
                override fun onPositiveButtonClick() {
                    presenter.loadUsers()
                }
            }
        ).show(childFragmentManager, "SettingsDialog")
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_user_list, menu)

        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView
        if (searchView is SearchView) {
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    presenter.setSearchQuery(query)
                    return true
                }

                override fun onQueryTextChange(query: String?): Boolean {
//                    vm.setSearchQuery(query)
                    return true
                }
            })

            searchItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
                override fun onMenuItemActionExpand(p0: MenuItem?): Boolean {
                    return true
                }

                override fun onMenuItemActionCollapse(p0: MenuItem?): Boolean {
                    presenter.setSearchQuery(null)
                    return true
                }
            })
        }

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.settings -> presenter.viewState.showSettingsDialog()
        }
        return super.onOptionsItemSelected(item)
    }
}
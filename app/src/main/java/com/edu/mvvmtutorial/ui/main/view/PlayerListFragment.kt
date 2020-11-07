package com.edu.mvvmtutorial.ui.main.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.edu.mvvmtutorial.R
import com.edu.mvvmtutorial.data.model.User
import com.edu.mvvmtutorial.ui.base.BaseFragment
import com.edu.mvvmtutorial.ui.callbacks.ListItemClickListener
import com.edu.mvvmtutorial.ui.main.adapter.PlayerAdapter
import com.edu.mvvmtutorial.ui.main.viewmodel.PlayerViewModel
import com.edu.mvvmtutorial.utils.Status
import com.edu.mvvmtutorial.utils.showMsg
import kotlinx.android.synthetic.main.custom_toolbar.*
import kotlinx.android.synthetic.main.pq_fragment_invite_player.*


class PlayerListFragment : BaseFragment(), ListItemClickListener<Int, User> {
    private var quizId: String? = null
    private lateinit var adapter: PlayerAdapter

    private lateinit var viewModel: PlayerViewModel
    //private var swipeRefresh: SwipeRefreshLayout? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        quizId = arguments?.getString("quizId")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        if (null != view) {
            return view
        }

        return inflater.inflate(R.layout.pq_fragment_invite_player, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpUI()
        setupViewModel()
        setupPlayerObserver()
    }

    private fun setUpUI() {
        tvTitle.setText(R.string.pq_title_invite)
        setRecyclerView()
    }


    private fun setRecyclerView() {
        //val rvRecords: RecyclerView? = layoutView?.findViewById<RecyclerView>(R.id.recyclerView)
        adapter = PlayerAdapter(requireContext(), this)
        recyclerView.adapter = adapter
        //swipeRefresh = layoutView?.findViewById<View>(R.id.swipeRefresh) as SwipeRefreshLayout
        //swipeRefresh?.setOnRefreshListener(this)
    }

    private fun setupPlayerObserver() {
        connectionLiveData.observe(this) {
            viewModel.isNetworkAvailable = it
        }

        viewModel.getUserList().observe(this, {
            when (it.status) {
                Status.SUCCESS -> {
                    progressBar.visibility = View.GONE
                    it.data?.let { quizList -> renderList(quizList) }
                    recyclerView.visibility = View.VISIBLE
                }
                Status.LOADING -> {
                    progressBar.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                }
                Status.ERROR -> {
                    //Handle Error
                    progressBar.visibility = View.GONE
                    showMsg(requireContext(), it.message!!)
                }
                else -> {
                    //idle
                }
            }
        })


    }

    companion object {
        internal val TAG = PlayerListFragment::class.java.simpleName

        fun newInstance(quizId: String): PlayerListFragment {
            val args = Bundle()
            args.putString("quizId", quizId)
            val fragment = PlayerListFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private fun renderList(quizList: List<User>) {
        adapter.apply {
            clearList()
            addList(quizList)
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this).get(PlayerViewModel::class.java)
    }

    override fun onItemClick(type: Int, item: User) {
        super.sendGameInvite(item)
    }

}

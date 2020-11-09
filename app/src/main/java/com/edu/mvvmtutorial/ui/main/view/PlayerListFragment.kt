package com.edu.mvvmtutorial.ui.main.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.edu.mvvmtutorial.R
import com.edu.mvvmtutorial.data.model.User
import com.edu.mvvmtutorial.ui.base.BaseFragment
import com.edu.mvvmtutorial.ui.callbacks.ListItemClickListener
import com.edu.mvvmtutorial.ui.main.adapter.InviteAdapter
import com.edu.mvvmtutorial.ui.main.viewmodel.PlayerViewModel
import com.edu.mvvmtutorial.utils.Const
import com.edu.mvvmtutorial.utils.CustomLog
import com.edu.mvvmtutorial.utils.Status
import com.edu.mvvmtutorial.utils.showMsg
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.custom_toolbar.view.*
import kotlinx.android.synthetic.main.pq_fragment_invite_player.*
import kotlinx.android.synthetic.main.pq_fragment_invite_player.view.*


class PlayerListFragment : BaseFragment(), ListItemClickListener<Int, User> {
    private lateinit var quizId: String

    private var lView: View? = null

    //private lateinit var adapter: PlayerAdapter
    private lateinit var adapter: InviteAdapter

    private lateinit var viewModel: PlayerViewModel
    //private var swipeRefresh: SwipeRefreshLayout? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        quizId = arguments?.getString("quizId")!!
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        if (null != lView) {
            return lView
        }

        lView = inflater.inflate(R.layout.pq_fragment_invite_player, container, false)
        setUpUI(lView!!)
        setupViewModel()
        return lView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //setupPlayerObserver()
    }

    private fun setUpUI(view: View) {
        view.tvTitle.setText(R.string.pq_title_invite)
        setRecyclerView(view)
    }


    private fun setRecyclerView(view: View) {
        //val rvRecords: RecyclerView? = layoutView?.findViewById<RecyclerView>(R.id.recyclerView)
        //adapter = PlayerAdapter(requireContext(), this)
        adapter = InviteAdapter(this, {
            Firebase.firestore.collection(Const.TABLE_USERS)
                .whereNotEqualTo("uid", Firebase.auth.currentUser?.uid)
                .limit(10)

            //.orderBy("name", Query.Direction.ASCENDING)
        })
        view.recyclerView.adapter = adapter
        adapter.setupOnScrollListener(
            view.recyclerView,
            view.recyclerView.layoutManager as LinearLayoutManager
        )

        adapter.onLoadingMore = {
            CustomLog.d(TAG, "onLoadingMore")
        }
        adapter.onLoadingMoreComplete = {
            CustomLog.d(TAG, "onLoadingMoreComplete")
        }
        adapter.onHasLoadedAll = {
            CustomLog.d(TAG, "onHasLoadedAll")
        }
        //swipeRefresh = layoutView?.findViewById<View>(R.id.swipeRefresh) as SwipeRefreshLayout
        //swipeRefresh?.setOnRefreshListener(this)
    }

    override fun onStart() {
        super.onStart()
        adapter.clear()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }

    private fun setupPlayerObserver() {
        /*connectionLiveData.observe(this) {
            viewModel.isNetworkAvailable = it
        }*/

        viewModel.getUserList().observe(viewLifecycleOwner, {
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
        /*adapter.apply {
            clearList()
            addList(quizList)
        }*/
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this).get(PlayerViewModel::class.java)
    }

    override fun onItemClick(type: Int, item: User) {
        super.sendGameInvite(item, quizId)
    }

}

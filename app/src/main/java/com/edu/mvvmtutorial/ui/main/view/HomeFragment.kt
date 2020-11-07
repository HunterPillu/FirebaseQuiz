package com.edu.mvvmtutorial.ui.main.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.lifecycle.ViewModelProvider
import com.edu.mvvmtutorial.R
import com.edu.mvvmtutorial.data.model.Course
import com.edu.mvvmtutorial.data.model.Quiz
import com.edu.mvvmtutorial.ui.base.BaseFragment
import com.edu.mvvmtutorial.ui.callbacks.ListItemClickListener
import com.edu.mvvmtutorial.ui.main.adapter.CourseSpinnerAdapter
import com.edu.mvvmtutorial.ui.main.adapter.QuizAdapter
import com.edu.mvvmtutorial.ui.main.viewmodel.HomeViewModel
import com.edu.mvvmtutorial.utils.CustomLog
import com.edu.mvvmtutorial.utils.Status
import com.edu.mvvmtutorial.utils.showMsg
import kotlinx.android.synthetic.main.pq_fragment_home.*
import kotlinx.android.synthetic.main.pq_fragment_home.view.*
import kotlinx.android.synthetic.main.toolbar_home.*
import kotlinx.android.synthetic.main.toolbar_home.view.*


class HomeFragment : BaseFragment(), AdapterView.OnItemSelectedListener,
    ListItemClickListener<Int, Quiz> {

    companion object {
        const val TAG: String = "home_fragment"

        fun newInstance() = HomeFragment()
    }

    private lateinit var courseAdapter: CourseSpinnerAdapter
    private lateinit var adapter: QuizAdapter
    private lateinit var viewModel: HomeViewModel
    private var lView: View? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (null != lView) {
            CustomLog.e(TAG, "not null")
            return lView
        } else {
            CustomLog.e(TAG, "view is null null")
        }

        lView = inflater.inflate(R.layout.pq_fragment_home, container, false)
        CustomLog.e(TAG, "onCreateView")
        setupUI(lView!!)
        setupViewModel()
        setupQuizObserver()
        setupCourseObserver()
        return lView
    }

    /*override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setupViewModel()
        setupQuizObserver()
        setupCourseObserver()
    }*/

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
    }

    private fun setupUI(view: View) {
        adapter = QuizAdapter(this, arrayListOf())
        /* recyclerView.addItemDecoration(
             DividerItemDecoration(
                 recyclerView.context,
                 (recyclerView.layoutManager as LinearLayoutManager).orientation
             )
         )*/
        view.recyclerView.adapter = adapter

        //init spinner
        courseAdapter = CourseSpinnerAdapter(requireContext(), arrayListOf(Course().apply {
            name = getString(R.string.pq_all)
            courseId = -1
        }))
        view.spCourse.adapter = courseAdapter
        view.spCourse.onItemSelectedListener = this
    }

    private fun setupCourseObserver() {
        viewModel.getCourseList().observe(this, {

            CustomLog.e(TAG, "course observer ${it.status.name}")
            when (it.status) {
                Status.SUCCESS -> {

                    it.data?.let { quizList ->
                        courseAdapter.addList(quizList)
                    }
                    spCourse.visibility = View.VISIBLE
                }
                else -> {
                    //idle
                }
            }
        })


    }

    private fun setupQuizObserver() {
        viewModel.getQuizList().observe(this, {
            CustomLog.e(TAG, "quiz observer ${it.status.name}")
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

    private fun renderList(quizList: List<Quiz>) {
        adapter.apply {
            clearList()
            addData(quizList)
            notifyDataSetChanged()
        }
    }


    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        viewModel.fetchQuiz(courseAdapter.getItem(position).courseId)
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }

    override fun onItemClick(type: Int, item: Quiz) {
        openFragment(PlayerListFragment.newInstance(item.uid))
    }

}
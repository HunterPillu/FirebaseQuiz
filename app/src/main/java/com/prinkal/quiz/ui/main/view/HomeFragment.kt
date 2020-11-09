package com.prinkal.quiz.ui.main.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.lifecycle.ViewModelProvider
import com.prinkal.quiz.R
import com.prinkal.quiz.data.model.Course
import com.prinkal.quiz.data.model.Quiz
import com.prinkal.quiz.ui.base.BaseFragment
import com.prinkal.quiz.ui.callbacks.ListItemClickListener
import com.prinkal.quiz.ui.main.adapter.CourseSpinnerAdapter
import com.prinkal.quiz.ui.main.adapter.QuizAdapter
import com.prinkal.quiz.ui.main.viewmodel.HomeViewModel
import com.prinkal.quiz.utils.CustomLog
import com.prinkal.quiz.utils.Status
import com.prinkal.quiz.utils.showMsg
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
            return lView
        }
        CustomLog.e(TAG, "onCreateView")
        lView = inflater.inflate(R.layout.pq_fragment_home, container, false)
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
        viewModel.getCourseList().observe(viewLifecycleOwner, {

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
        viewModel.getQuizList().observe(viewLifecycleOwner, {
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
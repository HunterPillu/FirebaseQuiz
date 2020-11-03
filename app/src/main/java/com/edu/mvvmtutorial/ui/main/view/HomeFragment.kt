package com.edu.mvvmtutorial.ui.main.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.covidbeads.app.assesment.util.showMsg
import com.edu.mvvmtutorial.R
import com.edu.mvvmtutorial.data.model.Course
import com.edu.mvvmtutorial.data.model.Quiz
import com.edu.mvvmtutorial.ui.base.ViewModelFactory
import com.edu.mvvmtutorial.ui.main.adapter.CourseSpinnerAdapter
import com.edu.mvvmtutorial.ui.main.adapter.QuizAdapter
import com.edu.mvvmtutorial.ui.main.viewmodel.HomeViewModel
import com.edu.mvvmtutorial.utils.Status
import kotlinx.android.synthetic.main.pq_fragment_home.*
import kotlinx.android.synthetic.main.toolbar_home.*


class HomeFragment : Fragment(), AdapterView.OnItemSelectedListener {

    companion object {
        const val TAG: String = "home_fragment"

        fun newInstance() = HomeFragment()
    }

    private lateinit var courseAdapter: CourseSpinnerAdapter
    private lateinit var adapter: QuizAdapter
    private lateinit var viewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.pq_fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setupViewModel()
        setupQuizObserver()
        setupCourseObserver()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        ViewModelProviders.of(
            this,
            ViewModelFactory()
        ).get(HomeViewModel::class.java)
    }

    private fun setupUI() {
        adapter = QuizAdapter(arrayListOf())
        /* recyclerView.addItemDecoration(
             DividerItemDecoration(
                 recyclerView.context,
                 (recyclerView.layoutManager as LinearLayoutManager).orientation
             )
         )*/
        recyclerView.adapter = adapter

        //init spinner
        courseAdapter = CourseSpinnerAdapter(requireContext(), arrayListOf(Course().apply {
            name = getString(R.string.pq_all)
            courseId = -1
        }))
        spCourse.adapter = courseAdapter
        spCourse.onItemSelectedListener = this
    }

    private fun setupCourseObserver() {
        viewModel.getCourseList().observe(this, {
            when (it.status) {
                Status.SUCCESS -> {
                    it.data?.let { quizList ->
                        courseAdapter.addList(quizList)
                    }
                    spCourse.visibility = View.VISIBLE
                }
                Status.LOADING -> {
                    spCourse.visibility = View.GONE
                }
                else -> {
                    //idle
                }
            }
        })


    }

    private fun setupQuizObserver() {
        viewModel.getQuizList().observe(this, {
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


}
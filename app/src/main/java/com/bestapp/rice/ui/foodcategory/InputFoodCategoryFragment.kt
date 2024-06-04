package com.bestapp.rice.ui.foodcategory

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.bestapp.rice.databinding.FragmentInputFoodCategoryBinding
import com.bestapp.rice.model.MeetingUiState
import com.bestapp.rice.ui.BaseFragment
import kotlinx.coroutines.launch

//BaseFragment 지양 클린아키텍처가 나오기전의 방식
class InputFoodCategoryFragment :
    BaseFragment<FragmentInputFoodCategoryBinding>(FragmentInputFoodCategoryBinding::inflate) {


    private val foodCategoryViewModel: FoodCategoryViewModel by viewModels({ requireParentFragment() })

    private val viewModel: InputFoodCategoryViewModel by viewModels()


    private val foodCategoryAdapter: FoodCategoryAdapter by lazy {
        FoodCategoryAdapter(
            onFoodCategoryClick = ::goMeeting
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setFoodCategoryAdapter()
        setupObserve()
    }


    private fun setupObserve() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                foodCategoryViewModel.meetingList.collect {
                    Log.e("cyc", "리사이클러뷰에 들어갈 데이터-->${it}")
                    foodCategoryAdapter.submitList(it)
                }
            }
        }

    }

    private fun setFoodCategoryAdapter() {
        val foodCategoryManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        binding.rv.apply {
            layoutManager = foodCategoryManager
            adapter = foodCategoryAdapter
        }
    }

    private fun goMeeting(meetingUiState: MeetingUiState) {
        foodCategoryViewModel.goMeeting(meetingUiState)
    }
}
package com.bestapp.rice.ui.mettinglist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import coil.load
import com.bestapp.rice.R
import com.bestapp.rice.databinding.FragmentMeetingListBinding
import com.bestapp.rice.databinding.ItemMyMeetingBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MeetingListFragment : Fragment() {
    private val viewModel: MeetingListViewModel by viewModels()

    private var _binding: FragmentMeetingListBinding? = null
    private val binding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentMeetingListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userDocumentID = viewModel.getUserDocumentID()
        viewModel.getMeetingByUserDocumentID(userDocumentID)

        setObserve()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun setObserve() = viewLifecycleOwner.lifecycleScope.launch {
        viewModel.meetingListUiState.collect {
            if (it.meetingUis.size == 0) {
                return@collect
            }

            val (comingMeetings, endMeetings) = it.meetingUis.partition { meetingUi ->
                meetingUi.activation
            }

            setView(comingMeetings, endMeetings)
        }
    }

    private fun setView(
        comingMeetingUiState: List<MeetingListUi>,
        endMeetingUiState: List<MeetingListUi>,
    ) {
        val comingMeetingBindings = listOf(
            binding.itemEnableMeeting1, binding.itemEnableMeeting2, binding.itemEnableMeeting3
        )
        setMeetingByActivation(
            meetingActivationStatus = MeetingActivationStatus.COMING,
            meetingItemBindings = comingMeetingBindings,
            meetingListUis = comingMeetingUiState
        )

        val endMeetingBindings = listOf(
            binding.itemDisableMeeting1, binding.itemDisableMeeting2, binding.itemDisableMeeting3
        )
        setMeetingByActivation(
            meetingActivationStatus = MeetingActivationStatus.END,
            meetingItemBindings = endMeetingBindings,
            meetingListUis = endMeetingUiState
        )
    }

    /**
     *  참여중인 모임과 지난 모임을 세팅하는 함수
     */
    private fun setMeetingByActivation(
        meetingActivationStatus: MeetingActivationStatus,
        meetingItemBindings: List<ItemMyMeetingBinding>,
        meetingListUis: List<MeetingListUi>,
    ) {
        val showItemCount = minOf(meetingListUis.size, meetingItemBindings.size)

        for (i in 0 until showItemCount) {
            meetingItemBindings[i].onBind(meetingListUis[i])
        }

        when (meetingActivationStatus) {
            MeetingActivationStatus.COMING -> {
                changeActionIcon(meetingItemBindings)

                meetingItemBindings.forEachIndexed { index, meetingBinding ->
                    listOf(meetingBinding.tvReview, meetingBinding.ivAction).forEach {
                        val userDocumentID = viewModel.getUserDocumentID().ifBlank {
                            "yUKL3rt0geiVdQJMOeoF"
                        }
                        val isHost = (meetingListUis[index].host == userDocumentID)

                        val meetingDocumentId = meetingListUis[index].meetingDocumentID
                        it.setOnClickListener {
                            goMeetingInfo(meetingDocumentId, isHost)
                        }
                    }
                }
            }

            MeetingActivationStatus.END -> {
                changeReviewUI(meetingItemBindings, meetingListUis)

                meetingItemBindings.forEachIndexed { index, meetingItemBinding ->
                    val clickAreas = listOf(meetingItemBinding.tvReview, meetingItemBinding.ivAction)

                    clickAreas.forEachIndexed { index, view ->
                        view.setOnClickListener {
                            goReview(meetingListUis[index])
                        }
                    }
                }
            }
        }
    }

    private fun goMeetingInfo(meetingDocumentID: String, isHost: Boolean) {
        if (isHost) {
            val action = MeetingListFragmentDirections.actionMeetingListFragmentToMeetingManagementFragment(meetingDocumentID)
            findNavController().navigate(action)
        } else {
            val action = MeetingListFragmentDirections.actionMeetingListFragmentToMeetingInfoFragment(meetingDocumentID)
            findNavController().navigate(action)
        }
    }

    private fun goReview(meetingListUi: MeetingListUi) {
        val action = MeetingListFragmentDirections.actionMeetingListFragmentToReviewFragment(meetingListUi.toMeetingUi())
        findNavController().navigate(action)
    }

    private fun ItemMyMeetingBinding.onBind(endMeeting: MeetingListUi) {
        iv.load(endMeeting.titleImage)
        iv.clipToOutline = true

        tvTitle.text = endMeeting.title
        tvLocation.text = endMeeting.placeLocationUi.locationAddress

        itemMyMeeting.visibility = View.VISIBLE
    }

    private fun changeActionIcon(comingMeetingBindings: List<ItemMyMeetingBinding>) {
        comingMeetingBindings.forEach { itemMyMeetingBinding ->
            itemMyMeetingBinding.ivAction.setImageResource(R.drawable.baseline_keyboard_arrow_right_24)
        }
    }

    private fun changeReviewUI(
        endMeetingBindings: List<ItemMyMeetingBinding>,
        endMeetings: List<MeetingListUi>,
    ) {
        val showItemCount = minOf(endMeetings.size, endMeetingBindings.size)

        for (i in 0 until showItemCount) {
            // endMeetings[i].isDoneReview
            val isDoneReview = if (i % 2 == 0) {
                true
            } else {
                false
            }
            endMeetingBindings[i].switchReviewVisibility(isDoneReview)
        }
    }

    private fun ItemMyMeetingBinding.switchReviewVisibility(
        isDoneReview: Boolean,
    ) {
        if (isDoneReview) {
            tvReview.visibility = View.GONE
            ivAction.isEnabled = false
        } else {
            tvReview.visibility = View.VISIBLE
            ivAction.isEnabled = true
        }
    }
}
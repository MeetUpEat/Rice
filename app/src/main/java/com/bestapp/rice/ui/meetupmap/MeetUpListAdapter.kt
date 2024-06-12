package com.bestapp.rice.ui.meetupmap

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.bestapp.rice.databinding.ItemMeetUpListBinding
import com.bestapp.rice.model.args.MeetingArg

class MeetUpListAdapter : ListAdapter<MeetingArg, MeetUpListAdapter.MeetUpListViewHolder>(diff) {

    class MeetUpListViewHolder(private val binding: ItemMeetUpListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(meetingArg: MeetingArg) {
            binding.ivTitleImage.load(meetingArg.titleImage)
            binding.tvTitle.text = meetingArg.title
            binding.tvDateTime.text = meetingArg.time
            binding.tvPeopleCount.text = "${meetingArg.members.size}/${meetingArg.recruits}명"
            binding.tvPrice.text = meetingArg.costValueByPerson.toString()
            binding.tvDescription.text = meetingArg.description
        }
    }

    override fun onBindViewHolder(holder: MeetUpListViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MeetUpListViewHolder {
        return MeetUpListViewHolder(
            ItemMeetUpListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    companion object {
        val diff = object : DiffUtil.ItemCallback<MeetingArg>() {
            override fun areItemsTheSame(oldItem: MeetingArg, newItem: MeetingArg) =
                oldItem.meetingDocumentID == newItem.meetingDocumentID

            override fun areContentsTheSame(oldItem: MeetingArg, newItem: MeetingArg) =
                oldItem == newItem
        }
    }
}
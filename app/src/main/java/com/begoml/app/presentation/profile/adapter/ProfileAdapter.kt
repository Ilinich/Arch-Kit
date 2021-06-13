package com.begoml.app.presentation.profile.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.begoml.app.databinding.ItemProfileMovieBinding
import com.begoml.app.presentation.profile.model.MoviesUi

class ProfileAdapter : ListAdapter<MoviesUi, ProfileAdapter.ProfileViewHolder>(DIFF_CALLBACK) {

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<MoviesUi>() {
            override fun areItemsTheSame(oldItem: MoviesUi, newItem: MoviesUi) =
                oldItem.name == newItem.name

            override fun getChangePayload(oldItem: MoviesUi, newItem: MoviesUi) = Any()
            override fun areContentsTheSame(oldItem: MoviesUi, newItem: MoviesUi) =
                oldItem.name == newItem.name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileViewHolder {
        val itemBinding = ItemProfileMovieBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProfileViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ProfileViewHolder, position: Int) {
        val moviesUi = getItem(position)
        holder.bindModel(moviesUi)
    }

    inner class ProfileViewHolder(private val itemBinding: ItemProfileMovieBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bindModel(model: MoviesUi) {
            with(itemBinding) {
                textName.text = model.name
                textYear.text = model.year
            }
        }
    }
}

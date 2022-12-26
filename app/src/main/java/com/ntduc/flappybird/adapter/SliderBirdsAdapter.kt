package com.ntduc.flappybird.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ntduc.flappybird.databinding.SliderItemBirdBinding
import com.ntduc.flappybird.model.Bird

class SliderBirdsAdapter(
    private var context: Context,
    list: ArrayList<Bird> = arrayListOf()
) : RecyclerView.Adapter<SliderBirdsAdapter.BirdViewHolder>() {
    private var listOriginal = arrayListOf<Bird>()

    init {
        if (list.isNotEmpty()){
            listOriginal.add(list.last())
            listOriginal.addAll(list)
            listOriginal.add(list.first())
        }
    }

    inner class BirdViewHolder(binding: SliderItemBirdBinding) :
        RecyclerView.ViewHolder(binding.root) {
        internal val binding: SliderItemBirdBinding

        init {
            this.binding = binding
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BirdViewHolder {
        val binding = SliderItemBirdBinding.inflate(LayoutInflater.from(context), parent, false)
        return BirdViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BirdViewHolder, position: Int) {
        val item = listOriginal[position]

        holder.binding.item.setImageResource(item.bird1)
    }

    override fun getItemCount(): Int = listOriginal.size
}
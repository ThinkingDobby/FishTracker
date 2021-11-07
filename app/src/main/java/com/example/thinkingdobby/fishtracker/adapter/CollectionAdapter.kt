package com.example.thinkingdobby.fishtracker.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.thinkingdobby.fishtracker.CollectionDetailActivity
import com.example.thinkingdobby.fishtracker.R
import com.example.thinkingdobby.fishtracker.data.Fish
import com.example.thinkingdobby.fishtracker.viewHolder.CollectionViewHolder

class CollectionAdapter(val context: Context, val dataList: List<Fish>, val bitmapList: List<Bitmap>)
    : RecyclerView.Adapter<CollectionViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): CollectionViewHolder {
        val view = LayoutInflater.from(parent?.context)
                .inflate(R.layout.fish_card, parent, false)
        return CollectionViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: CollectionViewHolder?, position: Int) {
        holder?.bind(dataList[position], bitmapList[position])
        holder?.itemView?.setOnClickListener {
            val intent = Intent(context, CollectionDetailActivity::class.java)
            val bundle = Bundle()
            bundle.putParcelable("selectedFish", dataList[position])
            intent.putExtras(bundle)
            context.startActivity(intent)
        }
    }
}
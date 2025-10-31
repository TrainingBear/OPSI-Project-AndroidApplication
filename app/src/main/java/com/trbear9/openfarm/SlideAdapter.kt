package com.trbear9.openfarm

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.trbear9.openfarm.databinding.ItemSlideBinding
import kotlin.jvm.java

class SlideAdapter(private val context: AppCompatActivity, private val data: List<SlideItem>) :
    RecyclerView.Adapter<SlideAdapter.SlideViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SlideViewHolder {
        val binding = ItemSlideBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SlideViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SlideViewHolder, position: Int) {
        val item = data[position]

        // set isi konten slide
        holder.binding.imgSlide.setImageResource(item.imageRes)
        holder.binding.tvSlideTitle.text = item.title
        holder.binding.tvSlideDesc.text = item.description

        // tampilkan tombol selesai hanya di slide terakhir
        if (position == data.size - 1) {
            holder.binding.btnDone.visibility = View.VISIBLE
            holder.binding.btnDone.setOnClickListener {
//                val context = holder.itemView.context
//                val intent = Intent(context, MainActivity::class.java)
//                context.startActivity(intent)
//                (context as? AppCompatActivity)?.finish()
                context.finish()
            }
        } else {
            holder.binding.btnDone.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = data.size

    class SlideViewHolder(val binding: ItemSlideBinding) :
        RecyclerView.ViewHolder(binding.root)
}

package com.example.photosharefirebasekt.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.photosharefirebasekt.R
import com.example.photosharefirebasekt.model.Post
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.post_item.view.*

class PostAdapter(val postList:ArrayList<Post>) : RecyclerView.Adapter<PostAdapter.ViewHolder>(){

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater=LayoutInflater.from(parent.context)
        val view=inflater.inflate(R.layout.post_item,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.user_email.text=postList[position].email
        holder.itemView.user_explain.text=postList[position].explain
        Picasso.get()
            .load(postList[position].imageUrl)
            .into(holder.itemView.user_post_iv)
    }

    override fun getItemCount(): Int {
        return postList.size
    }
}
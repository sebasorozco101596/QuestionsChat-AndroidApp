package com.sebasorozcob.www.questionschat.view.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.sebasorozcob.www.questionschat.R
import com.sebasorozcob.www.questionschat.databinding.ItemProfileBinding
import com.sebasorozcob.www.questionschat.model.User


class UserAdapter(
    var context: Context,
    private var userList: ArrayList<User>) : RecyclerView.Adapter<UserAdapter.UserViewHolder>()
{

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding: ItemProfileBinding = ItemProfileBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.item_profile, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {

        val user = userList[position]
        holder.binding.userNameTextView.text = user.name
        Log.d("ImageView",user.profileImage.toString())

        Glide.with(context)
            .load(user.profileImage)
            .placeholder(R.drawable.avatar)
            .into(holder.binding.profileImageView)
    }

    override fun getItemCount(): Int = userList.size
}
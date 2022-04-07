package com.sebasorozcob.www.questionschat.view.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.sebasorozcob.www.questionschat.databinding.ActivityMainBinding
import com.sebasorozcob.www.questionschat.model.User
import com.sebasorozcob.www.questionschat.util.DialogProgress
import com.sebasorozcob.www.questionschat.view.adapters.UserAdapter

class MainActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding? = null
    private var database: FirebaseDatabase? = null
    private var users: ArrayList<User>? = null
    private var usersAdapter: UserAdapter? = null
    private var dialog = DialogProgress()
    var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        val dialog = dialog.setProgressDialog(this@MainActivity, "Uploading Image...")
        //dialog.show()

        database = FirebaseDatabase.getInstance()
        users = ArrayList()
        usersAdapter = UserAdapter(this@MainActivity, users!!)
        val layoutManager = GridLayoutManager(this@MainActivity,2)
        binding!!.recyclerList.layoutManager = layoutManager
        database!!.reference
            .child("users")
            .child(FirebaseAuth.getInstance().uid!!)
            .addValueEventListener(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    user = snapshot.getValue(User::class.java)
                }

                override fun onCancelled(error: DatabaseError) { }

            })
        binding!!.recyclerList.adapter = usersAdapter
        database!!.reference
            .child("users")
            .addValueEventListener(object : ValueEventListener{

                override fun onDataChange(snapshot: DataSnapshot) {
                    users!!.clear()
                    for ((index,snapshot1) in snapshot.children.withIndex()) {
                        val user: User? = snapshot1.getValue(User::class.java)
                        if (!user!!.uid.equals(FirebaseAuth.getInstance().uid)) {
                            users!!.add(user)
                            usersAdapter!!.notifyItemInserted(index)
                        }

                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }

    override fun onResume() {
        super.onResume()
        val currentId = FirebaseAuth.getInstance().uid
        database!!.reference
            .child("presence")
            .child(currentId!!).setValue("Online")
    }

    override fun onPause() {
        super.onPause()
        val currentId = FirebaseAuth.getInstance().uid
        database!!.reference
            .child("presence")
            .child(currentId!!).setValue("offline")
    }
}
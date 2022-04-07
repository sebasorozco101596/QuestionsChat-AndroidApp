package com.sebasorozcob.www.questionschat.viewmodel

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.sebasorozcob.www.questionschat.model.User
import com.sebasorozcob.www.questionschat.view.activities.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SetupProfileViewModel: ViewModel() {

    private val setupFinished: MutableLiveData<Boolean> = MutableLiveData()
    private val dialogProgress: MutableLiveData<Boolean> = MutableLiveData()

    private val tag: String = "FirebaseStorageManager"
    private val mStorageRef = FirebaseStorage.getInstance().reference
    private val database = FirebaseDatabase.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun uploadImage(imageURI: Uri, name: String) {

        dialogProgress.value = true

        viewModelScope.launch(Dispatchers.IO) {
            val uploadTask = mStorageRef.child("users/${auth.uid}.jpg").putFile(imageURI)
            uploadTask.addOnSuccessListener {

                val downloadURLTask = mStorageRef.child("users/${FirebaseAuth.getInstance().uid}.jpg").downloadUrl
                downloadURLTask.addOnSuccessListener {
                    Log.e(tag,"IMAGE PATH: $it")
                    val filePath = it.toString()
                    val obj = HashMap<String, String>()
                    obj["image"] = filePath
                    database.reference
                        .child("users")
                        .child(FirebaseAuth.getInstance().uid!!)
                        .updateChildren(obj as Map<String, Any>).addOnSuccessListener { }

                    val imageUrl = it.toString()
                    val uid = auth.uid
                    val phone = auth.currentUser!!.phoneNumber
                    val nameUser: String = name
                    val user = User(uid, nameUser, phone, imageUrl)

                    database.reference
                        .child("users")
                        .child(uid!!)
                        .setValue(user)
                        .addOnCompleteListener {
                            setupFinished.value = true
                            dialogProgress.value = false
                        }
                }.addOnFailureListener {
                    dialogProgress.value = false
                }
            }.addOnFailureListener {
                Log.e(tag, "Image upload failed ${it.printStackTrace()}")
            }
        }

    }

    fun uploadUser(uid: String, name: String, phone: String) {
        val user = User(uid, name, phone, "No Image")
        database.reference
            .child("users")
            .child(uid)
            .setValue(user)
            .addOnCompleteListener {
                setupFinished.value = true
            }
    }


    fun getIsFinished(): MutableLiveData<Boolean> {
        return setupFinished
    }

    fun getDialogProgress(): MutableLiveData<Boolean> {
        return dialogProgress
    }

}
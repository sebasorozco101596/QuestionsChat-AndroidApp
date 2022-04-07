package com.sebasorozcob.www.questionschat.view.activities

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.sebasorozcob.www.questionschat.databinding.ActivitySetupProfileBinding
import com.sebasorozcob.www.questionschat.util.DialogProgress
import com.sebasorozcob.www.questionschat.viewmodel.SetupProfileViewModel

class SetupProfileActivity : AppCompatActivity() {

    private var binding: ActivitySetupProfileBinding? = null
    private var auth: FirebaseAuth? = null
    private var database: FirebaseDatabase? = null
    private var storage: FirebaseStorage? = null
    private var selectedImage: Uri? = null
    private val dialogProgress = DialogProgress()
    private lateinit var dialog: AlertDialog

    // View Model initializing
    private val setupViewModel: SetupProfileViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetupProfileBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        // Initializing the variables
        setup()

        // Opening the gallery of the user for to select the wish image to use
        val getImage = registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) { data ->
            binding!!.profileImageView.setImageURI(data)
            selectedImage = data
        }

        // Getting the image selected by the Gallery from the user's phone
        binding!!.profileImageView.setOnClickListener {
            getImage.launch("*/*")
        }

        // Action of the Set Up Button
        binding!!.setupButton.setOnClickListener {

            showProgressBar()

            // Activity that will be open when the process of uploading finished
            val intent = Intent(this@SetupProfileActivity, MainActivity::class.java)

            val name: String = binding!!.nameEditText.text.toString()
            if (name.isEmpty()) {
                binding!!.nameEditText.error = "Please type a name"
            }

            if (selectedImage != null) {
                val userName = binding!!.nameEditText.text.toString()

                // Sending the information to the view model for to upload the information to the
                // Firebase Storage and the firebase Realtime database
                setupViewModel.uploadImage(
                    selectedImage!!,
                    userName
                )

                // Observing while the process to send all the information to Firebase Finish
                setupViewModel.getIsFinished().observe(this) {
                    if(it) {
                        startActivity(intent)
                        finish()
                    }
                }
                // In case that the user did not selected any image the user will be created without
            // image instead will be have a string of No Image
            } else {
                val uid = auth!!.uid
                val phone = auth!!.currentUser!!.phoneNumber
                val nameUser: String = binding!!.nameEditText.text.toString()

                setupViewModel.uploadUser(uid!!, nameUser, phone!!)

                // Observing while the process to send all the information to Firebase Finish
                setupViewModel.getIsFinished().observe(this) { isFinished ->
                    if(isFinished) {
                        startActivity(intent)
                        finish()
                    }
                }
            }
        }
    }

    // Function that initialize all the necessary variables
    private fun setup() {
        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()
        supportActionBar?.hide()
        dialog = dialogProgress.setProgressDialog(this@SetupProfileActivity, "Uploading Information...")
    }

    // Function that show the progress bar while the information is being uploading to the
    // Firebase server.
    private fun showProgressBar() {

        setupViewModel.getDialogProgress().observe(this
        ) { isFinished ->
            if (!isFinished) {
                dialog.dismiss()
            } else {
                dialog.show()
            }
        }
    }
}
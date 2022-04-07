package com.sebasorozcob.www.questionschat.view.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.view.WindowCompat
import com.google.firebase.auth.FirebaseAuth
import com.sebasorozcob.www.questionschat.databinding.ActivityVerificationBinding

class VerificationActivity : AppCompatActivity() {

    private var binding : ActivityVerificationBinding? = null

    private var auth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerificationBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        auth = FirebaseAuth.getInstance()
        if (auth!!.currentUser != null) {
            val intent = Intent(this@VerificationActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding!!.numberEditText.requestFocus()
        binding!!.continueButton.setOnClickListener {
            val intent = Intent(this@VerificationActivity, OTPActivity::class.java)
            if (binding!!.numberEditText.text.toString().isNotEmpty()) {
                intent.putExtra("phoneNumber", binding!!.numberEditText.text.toString())
                startActivity(intent)
            } else {
                Toast.makeText(this,"Don't Forget to Write your number",Toast.LENGTH_LONG).show()
            }
        }
    }
}
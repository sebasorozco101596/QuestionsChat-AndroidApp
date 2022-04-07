package com.sebasorozcob.www.questionschat.view.activities
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.sebasorozcob.www.questionschat.databinding.ActivityOtpBinding
import com.sebasorozcob.www.questionschat.util.DialogProgress
import java.util.concurrent.TimeUnit

@Suppress("SameParameterValue")
class OTPActivity : AppCompatActivity() {

    private var binding: ActivityOtpBinding? = null
    var verificationId: String? = null
    private var auth: FirebaseAuth? = null
    private val dialogProgress = DialogProgress()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtpBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        val dialog = dialogProgress.setProgressDialog(this@OTPActivity, "Sending OTP...")
        dialog.show()
        supportActionBar?.hide()

        auth = FirebaseAuth.getInstance()

        val phoneNumber = intent.getStringExtra("phoneNumber")
        if (phoneNumber != null) {
            Log.d("Number",phoneNumber)
        }
        val verifyText = "Verify $phoneNumber"
        binding!!.numberVerifyTextView.text = verifyText

        val options = phoneNumber?.let {
            PhoneAuthOptions.newBuilder(auth!!)
                .setPhoneNumber(it)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this@OTPActivity)
                .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks(){

                    override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                        //("Not yet implemented")
                    }

                    override fun onVerificationFailed(p0: FirebaseException) {
                        //("Not yet implemented")
                    }

                    override fun onCodeSent(verifyId: String,
                                            forceResendingToken: PhoneAuthProvider.ForceResendingToken) {
                        super.onCodeSent(verifyId, forceResendingToken)
                        dialog.dismiss()
                        verificationId = verifyId
//                        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
//                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
                        binding!!.otpView.requestFocus()
                    }

                }).build()
        }

        if (options != null) {
            PhoneAuthProvider.verifyPhoneNumber(options)
        }
        binding!!.otpView.setOtpCompletionListener { otp ->
            val credential = PhoneAuthProvider.getCredential(verificationId!!, otp)
            auth!!.signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val intent = Intent(this@OTPActivity, SetupProfileActivity::class.java)
                        startActivity(intent)
                        finishAffinity()
                    } else {
                        Toast.makeText(this@OTPActivity,"Failed", Toast.LENGTH_LONG).show()
                    }
                }
        }
    }
}
package com.sebasorozcob.www.questionschat
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.sebasorozcob.www.questionschat.databinding.ActivityOtpBinding
import java.util.concurrent.TimeUnit

class OTPActivity : AppCompatActivity() {

    private var binding: ActivityOtpBinding? = null
    var verificationId: String? = null
    private var auth: FirebaseAuth? = null
    var dialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtpBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        val dialog = setProgressDialog(this@OTPActivity, "Sending OTP...")
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

    private fun setProgressDialog(context: Context, message:String):AlertDialog {
        val llPadding = 30
        val ll = LinearLayout(context)
        ll.orientation = LinearLayout.HORIZONTAL
        ll.setPadding(llPadding, llPadding, llPadding, llPadding)
        ll.gravity = Gravity.CENTER
        var llParam = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT)
        llParam.gravity = Gravity.CENTER
        ll.layoutParams = llParam

        val progressBar = ProgressBar(context)
        progressBar.isIndeterminate = true
        progressBar.setPadding(0, 0, llPadding, 0)
        progressBar.layoutParams = llParam

        llParam = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT)
        llParam.gravity = Gravity.CENTER
        val tvText = TextView(context)
        tvText.text = message
        tvText.setTextColor(Color.parseColor("#000000"))
        tvText.textSize = 20.toFloat()
        tvText.layoutParams = llParam

        ll.addView(progressBar)
        ll.addView(tvText)

        val builder = AlertDialog.Builder(context)
        builder.setCancelable(true)
        builder.setView(ll)

        val dialog = builder.create()
        val window = dialog.window
        if (window != null) {
            val layoutParams = WindowManager.LayoutParams()
            layoutParams.copyFrom(dialog.window?.attributes)
            layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT
            dialog.window?.attributes = layoutParams
        }
        return dialog
    }
}
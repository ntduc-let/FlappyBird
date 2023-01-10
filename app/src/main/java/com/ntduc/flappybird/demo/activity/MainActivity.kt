package com.ntduc.flappybird.demo.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.ntduc.contextutils.inflater
import com.ntduc.flappybird.demo.App
import com.ntduc.flappybird.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth

    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { res ->
        this.onSignInResult(res)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(inflater)
        setContentView(binding.root)

        init()
    }

    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    private fun init() {
        initData()
        initEvent()
    }

    private fun initData() {
        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
    }

    private fun initEvent() {
        binding.play.setOnClickListener {
            App.getInstance().startEffect()
            startActivity(Intent(this, ChooseLevelActivity::class.java))
        }

        binding.rank.setOnClickListener {
            App.getInstance().startEffect()

            // Choose authentication providers
            val providers = arrayListOf(AuthUI.IdpConfig.GoogleBuilder().build())

            val signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build()
            signInLauncher.launch(signInIntent)
        }

        binding.setting.setOnClickListener {
            App.getInstance().startEffect()
//            startActivity(Intent(this, SettingActivity::class.java))

//            AuthUI.getInstance()
//                .signOut(this)
//                .addOnCompleteListener {
//                    updateUI(null)
//                }

            startActivity(Intent(this, MultiPlayerActivity::class.java))
        }
    }

    override fun onBackPressed() {
        App.getInstance().startEffect()
        super.onBackPressed()
    }

    private fun updateUI(currentUser: FirebaseUser?) {
        Log.d("ntduc_debug", "updateUI: $currentUser")
        if (currentUser == null) {
//            binding.imgAva.setImageResource(R.drawable.img_ava)
//            binding.txtAccount.text = "Trạng thái: Chưa đăng nhập"
//            binding.btnLogin.visibility = View.VISIBLE
//            binding.btnLogout.visibility = View.GONE
//            binding.btnStorage.visibility = View.GONE
        } else {
//            val url = currentUser.photoUrl.toString().replace("s96-c", "s400-c", true)
//            Glide.with(this).load(url)
//                .placeholder(R.drawable.img_ava)
//                .error(R.drawable.img_ava)
//                .into(binding.imgAva)
//            binding.txtAccount.text = "Email: ${currentUser.email}"
//            binding.btnLogin.visibility = View.GONE
//            binding.btnLogout.visibility = View.VISIBLE
//            binding.btnStorage.visibility = View.VISIBLE
        }
    }


    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        if (result.resultCode == RESULT_OK) {
            val user = auth.currentUser
            updateUI(user)
        }
    }
}
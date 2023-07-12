package com.uni.uniteaching.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.uni.uniteaching.R
import com.uni.uniteaching.data.Resource
import com.uni.uniteaching.databinding.ActivityLogInBinding
import com.uni.uniteaching.databinding.ActivitySignUpBinding
import com.uni.uniteaching.viewModel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class LogIn : AppCompatActivity() {
    private lateinit var binding: ActivityLogInBinding
    private val viewModel : AuthViewModel by viewModels()
    private lateinit var progress: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLogInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        progress=binding.logInProgress

        binding.logInBt.setOnClickListener {
            val email=binding.logInEmail.text.toString()
            val password = binding.logInPass.text.toString()



            if (email.isNotEmpty()
                &&password.isNotEmpty()
            ){
                viewModel.logIn(email,password)
                observe()
            }else{
                Toast.makeText(this,"make sure to enter all data",
                    Toast.LENGTH_SHORT).show()

            }
        }


    }
    private fun observe(){
        lifecycleScope.launchWhenCreated {
            viewModel.logIn.collectLatest { state ->
                when (state) {
                    is Resource.Loading -> {
                        progress.visibility = View.VISIBLE

                    }
                    is Resource.Success -> {
                        progress.visibility = View.INVISIBLE
                        Toast.makeText(this@LogIn, state.result, Toast.LENGTH_LONG).show()
                        startActivity(Intent(this@LogIn, HomeScreen::class.java))
                    }
                    is Resource.Failure -> {
                        progress.visibility = View.INVISIBLE
                        Toast.makeText(
                            this@LogIn, state.exception.toString(),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    else -> {}
                }
            }
        }
    }
}

package com.example.photosharefirebasekt

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.photosharefirebasekt.view.FeedActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var auth:FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth= FirebaseAuth.getInstance()
        val currentUser=auth.currentUser
        if(currentUser!=null) {
            val intent=Intent(applicationContext, FeedActivity::class.java)
            startActivity(intent)

        }

    }

    fun loginButton(view: android.view.View) {

        val email=email_editText.text.toString()
        val password=password_editText.text.toString()

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener{task->
            if(task.isSuccessful){
                val currentUser=auth.currentUser?.email.toString()
                Toast.makeText(this,"Hoşgeldin $currentUser",Toast.LENGTH_SHORT).show()
                //diger aktiviteye gidicez
                val intent=Intent(applicationContext, FeedActivity::class.java)
                startActivity(intent)
                finish()
            }
        }.addOnFailureListener{exception->
            Toast.makeText(applicationContext,exception.localizedMessage,Toast.LENGTH_SHORT).show()
        }


    }
    fun registerButton(view: android.view.View) {
        val email=email_editText.text.toString()
        val password=password_editText.text.toString()

        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener{task->
            //asenkron
            if(task.isSuccessful){
                //diger aktiviteye gidicez
                val intent=Intent(applicationContext, FeedActivity::class.java)
                startActivity(intent)
                finish()
            }

        }.addOnFailureListener{ exception ->
            Toast.makeText(applicationContext,exception.localizedMessage,Toast.LENGTH_SHORT).show()
        }


    }
}
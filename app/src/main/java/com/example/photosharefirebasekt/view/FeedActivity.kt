package com.example.photosharefirebasekt.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.photosharefirebasekt.MainActivity
import com.example.photosharefirebasekt.R
import com.example.photosharefirebasekt.adapter.PostAdapter
import com.example.photosharefirebasekt.model.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_feed.*

class FeedActivity : AppCompatActivity() {
    private lateinit var auth:FirebaseAuth
    private lateinit var database:FirebaseFirestore
    private lateinit var postAdapter:PostAdapter

    var postList=ArrayList<Post>()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed)

        auth= FirebaseAuth.getInstance()
        database= FirebaseFirestore.getInstance()
        takeData()
        var layoutManager=LinearLayoutManager(this)
        recyclerView.layoutManager=layoutManager
        postAdapter= PostAdapter(postList)
        recyclerView.adapter=postAdapter

    }
    fun takeData(){
        database.collection("Post")
                //filtreleme
            .orderBy("Date",Query.Direction.DESCENDING)
            .addSnapshotListener{
            snapshot,exception ->
            if(exception!=null){
                Toast.makeText(this,exception.localizedMessage,Toast.LENGTH_SHORT).show()
            }else{
                if(snapshot!=null){
                    if(!snapshot.isEmpty){
                        val documents=snapshot.documents
                        postList.clear()
                        for(postDocument in documents){
                            val userEmail=postDocument["Email"] as String
                            val explain=postDocument["Explain"] as String
                            val url=postDocument["ImageUrl"] as String


                            var downloadPost= Post(userEmail,explain,url)
                            postList.add(downloadPost)


                        }
                        postAdapter.notifyDataSetChanged()

                    }
                }
            }

        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater=menuInflater
        menuInflater.inflate(R.menu.option_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId== R.id.share_photo){
            //foto paylaşma akvtivitesine geçicem
            val intent=Intent(this, PhotoShareActivity::class.java)
            startActivity(intent)
        }
        else if(item.itemId== R.id.exit){
            auth.signOut()
            //çıkış yapacam
            val intent=Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }


        return super.onOptionsItemSelected(item)
    }

}
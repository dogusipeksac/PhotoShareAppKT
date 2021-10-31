package com.example.photosharefirebasekt.view

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.photosharefirebasekt.R
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_photo_share.*
import java.util.*

class PhotoShareActivity : AppCompatActivity() {

    var chosenImage:Uri ?=null
    var chosenBitmap:Bitmap?=null
    private lateinit var storage: FirebaseStorage
    private lateinit var auth: FirebaseAuth
    private lateinit var database:FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_share)
        storage= FirebaseStorage.getInstance()
        auth= FirebaseAuth.getInstance()
        database= FirebaseFirestore.getInstance()
    }

    fun choseImage(view: android.view.View) {

        if(ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)
            !=PackageManager.PERMISSION_GRANTED){
            // izni almamışız
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),1)
        }
        else{
            //izin zaten varsa yapılıcakalr
            val galeryIntent= Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galeryIntent,2)
        }



    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode==1){
            if(grantResults.size>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
            //izin verilirse yapılıcaklar
                val galeryIntent= Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galeryIntent,2)

            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode==2 && resultCode==Activity.RESULT_OK && data != null){
            //bütün versiyonlar için yapıldı
            chosenImage=data.data
            if(chosenImage!=null){
                if(Build.VERSION.SDK_INT>=28){
                    val  source=ImageDecoder.createSource(this.contentResolver,chosenImage!!)
                    chosenBitmap=ImageDecoder.decodeBitmap(source)
                    upload_image.setImageBitmap(chosenBitmap)
                }
                else{
                    //eski versiyonlar için
                    chosenBitmap=MediaStore.Images.Media.getBitmap(this.contentResolver,chosenImage)
                    upload_image.setImageBitmap(chosenBitmap)
                }
            }
        }


        super.onActivityResult(requestCode, resultCode, data)
    }


    fun share(view: android.view.View) {
        //depo işlemleri
        //uuid
        val uuid= UUID.randomUUID()
        val imageName="${uuid}.jpg"
        val reference=storage.reference
        val imageRef=reference.child("images").child(imageName)
        if(chosenImage!=null){
            imageRef.putFile(chosenImage!!).addOnSuccessListener { taskSnapshot->
                val uploadImageRef=FirebaseStorage.getInstance()
                    .reference.child("images")
                    .child(imageName)
                    .downloadUrl
                    .addOnSuccessListener { uri->
                        val downloadUrl=uri.toString()
                        val currentUserEMail=auth.currentUser!!.email.toString()
                        val userExplain=explain_editText.text.toString()
                        val date=Timestamp.now()
                        //veritabanı işlemleri
                        val postHashMap= hashMapOf<String,Any>()
                        postHashMap["ImageUrl"] = downloadUrl
                        postHashMap["Email"]=currentUserEMail
                        postHashMap["Explain"]=userExplain
                        postHashMap["Date"]=date

                        database.collection("Post").add(postHashMap).addOnCompleteListener{
                            if(it.isSuccessful){
                                Toast.makeText(applicationContext,"Post başarı ile paylaşıldı.",Toast.LENGTH_SHORT).show()
                                finish()
                            }
                        }.addOnFailureListener{
                            Toast.makeText(applicationContext,it.localizedMessage,Toast.LENGTH_SHORT).show()
                        }
                    }

            }.addOnFailureListener{
                Toast.makeText(applicationContext,it.localizedMessage,Toast.LENGTH_SHORT).show()

            }
        }






    }
}
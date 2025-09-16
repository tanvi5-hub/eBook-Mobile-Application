package com.example.ebook

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class CreateStoryActivity : AppCompatActivity() {
    private lateinit var storyTitle: EditText
    private lateinit var storyDescription: EditText
    private lateinit var storyCategoryName: String
    private lateinit var coverImageView: ImageView
    private lateinit var selectCoverButton: Button
    private lateinit var createStoryButton: Button

    private var coverImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_story)

        storyTitle = findViewById(R.id.storyTitle)
        storyDescription = findViewById(R.id.storyDescription)
        coverImageView = findViewById(R.id.coverImageView)
        selectCoverButton = findViewById(R.id.selectCoverButton)
        createStoryButton = findViewById(R.id.createStoryButton)

        storyCategoryName = ""
        val storyCategorySpinner = findViewById<Spinner>(R.id.storyCategoryDropdown)
        val storyCategories = arrayOf("Fiction", "Action", "Romantic", "Horror")
        val arrayAdapter = ArrayAdapter(this@CreateStoryActivity, android.R.layout.simple_spinner_dropdown_item, storyCategories)
        storyCategorySpinner.adapter = arrayAdapter

        storyCategorySpinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                storyCategoryName = storyCategories[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        selectCoverButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 1)
        }

        createStoryButton.setOnClickListener {
            val name = storyTitle.text.toString().trim()
            val description = storyDescription.text.toString().trim()
            val category = storyCategoryName.trim()
            if (coverImageUri != null) {
                uploadCoverImageAndCreateStory(name, description, category, coverImageUri!!)
            } else {
                Toast.makeText(this, "Please select a cover image", Toast.LENGTH_SHORT).show()
            }
        }

        // Add a ComposeView to your XML layout and set its content
        findViewById<ComposeView>(R.id.composeView).setContent {
            MaterialTheme {
                Surface(color = Color.Transparent) {
                    BackButtonComposable()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            coverImageUri = data.data
            coverImageView.setImageURI(coverImageUri)
        }
    }

    private fun uploadCoverImageAndCreateStory(name: String, description: String, category: String, imageUri: Uri) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val storyId = FirebaseDatabase.getInstance().getReference("stories").push().key

        if (storyId != null && userId != null) {
            val storageRef = FirebaseStorage.getInstance().getReference("story_covers/$storyId.jpg")
            storageRef.putFile(imageUri).addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    val coverUrl = uri.toString()
                    val story = NewStory(storyId, userId, name, description, category, coverUrl)
                    FirebaseDatabase.getInstance().getReference("stories/$storyId").setValue(story)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(this, "Story created successfully", Toast.LENGTH_SHORT).show()
                                // Navigate to Create First Page
                                val intent = Intent(this, CreatePageActivity::class.java)
                                intent.putExtra("STORY_ID", storyId)
                                intent.putExtra("IS_FIRST_PAGE", true)
                                startActivity(intent)
                                finish()
                            } else {
                                Toast.makeText(this, "Failed to create story", Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            }.addOnFailureListener {
                Toast.makeText(this, "Failed to upload cover image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @Composable
    fun BackButtonComposable() {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Button(
                onClick = {
                    // Handle the back button click to navigate to the author home page
                    val intent = Intent(this@CreateStoryActivity, AuthorHomeActivity::class.java)
                    startActivity(intent)
                    finish()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Transparent)
            ) {
                Text("Back to Home")
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun BackButtonPreview() {
        MaterialTheme {
            BackButtonComposable()
        }
    }
}

data class NewStory(
    val id: String,
    val authorId: String,
    val name: String,
    val description: String,
    val category: String,
    val coverUrl: String
)
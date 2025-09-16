package com.example.ebook
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.ViewGroup.LayoutParams
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
class AuthorHomeActivity : AppCompatActivity() {
    private lateinit var database: FirebaseDatabase
    private lateinit var storiesRef: DatabaseReference
    private lateinit var currentUser: FirebaseAuth
    private lateinit var storiesLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_author_home)
        database = FirebaseDatabase.getInstance()
        storiesRef = database.getReference("stories")
        currentUser = FirebaseAuth.getInstance()
        storiesLayout = findViewById(R.id.stories_layout)
        // Get the current user's authorId
        val authorId = currentUser.currentUser?.uid
        // Query stories where authorId matches the logged-in user's authorId
        val query = storiesRef.orderByChild("authorId").equalTo(authorId)
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                // Clear the existing stories
                storiesLayout.removeAllViews()

                // Iterate through each story and display it
                for (storySnapshot in dataSnapshot.children) {
                    val storyId = storySnapshot.key
                    val name = storySnapshot.child("name").getValue(String::class.java) ?: ""
                    val category = storySnapshot.child("category").getValue(String::class.java) ?: ""
                    val description = storySnapshot.child("description").getValue(String::class.java) ?: ""
                    val coverUrl = storySnapshot.child("coverUrl").getValue(String::class.java) ?: ""

                    // Create a layout for each story
                    val storyLayout = LinearLayout(this@AuthorHomeActivity)
                    storyLayout.orientation = LinearLayout.HORIZONTAL
                    storyLayout.layoutParams = LinearLayout.LayoutParams(
                        LayoutParams.MATCH_PARENT,
                        LayoutParams.WRAP_CONTENT
                    ).apply {
                        setMargins(0, 0, 0, 32)
                    }
                    // Create an ImageView to display the cover photo
                    val coverImageView = ImageView(this@AuthorHomeActivity)
                    val coverLayoutParams = LinearLayout.LayoutParams(
                        200,
                        300
                    ).apply {
                        setMargins(0, 0, 16, 0)
                    }
                    coverImageView.layoutParams = coverLayoutParams
                    Glide.with(this@AuthorHomeActivity).load(coverUrl).into(coverImageView)
                    storyLayout.addView(coverImageView)

                    // Create a TextView to display the story details
                    val storyTextView = TextView(this@AuthorHomeActivity)
                    storyTextView.typeface = Typeface.DEFAULT_BOLD
                    storyTextView.text = "Name: $name\nCategory: $category\nDescription: $description\n"
                    storyLayout.addView(storyTextView)

                    storyLayout.setOnClickListener {
                        val intent = Intent(this@AuthorHomeActivity, AddPagesActivity::class.java)
                        intent.putExtra("STORY_ID", storyId)
                        startActivity(intent)
                    }

                    storiesLayout.addView(storyLayout)
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // Handle database error
                // TODO: Handle database error
            }
        })
        val createStoryButton = findViewById<FloatingActionButton>(R.id.createStoryBtn)
        createStoryButton.setOnClickListener {
            val intent = Intent(this, CreateStoryActivity::class.java)
            startActivity(intent)
        }
        // Navigation button listeners
        val navHome = findViewById<Button>(R.id.nav_home)
        val navStats = findViewById<Button>(R.id.nav_stats)
        val navProfile = findViewById<Button>(R.id.nav_profile)
        navHome.setOnClickListener {
            // Already in HomeActivity
        }
        navStats.setOnClickListener {
            val intent = Intent(this, StatsActivity::class.java)
            startActivity(intent)
        }
        navProfile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
    }
}
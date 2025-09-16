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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class StatsActivity : AppCompatActivity() {

    private lateinit var database: FirebaseDatabase
    private lateinit var storiesRef: DatabaseReference
    private lateinit var currentUser: FirebaseAuth
    private lateinit var statsLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stats)

        database = FirebaseDatabase.getInstance()
        storiesRef = database.getReference("stories")
        currentUser = FirebaseAuth.getInstance()
        statsLayout = findViewById(R.id.stats_layout)
        val authorId = currentUser.currentUser?.uid
        val query = storiesRef.orderByChild("authorId").equalTo(authorId)
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                statsLayout.removeAllViews()
                for (storySnapshot in dataSnapshot.children) {
                    val storyId = storySnapshot.key
                    val name = storySnapshot.child("name").getValue(String::class.java) ?: ""
                    val category = storySnapshot.child("category").getValue(String::class.java) ?: ""
                    val description = storySnapshot.child("description").getValue(String::class.java) ?: ""
                    val coverUrl = storySnapshot.child("coverUrl").getValue(String::class.java) ?: ""
                    val storyLayout = LinearLayout(this@StatsActivity)
                    storyLayout.orientation = LinearLayout.HORIZONTAL
                    storyLayout.layoutParams = LinearLayout.LayoutParams(
                        LayoutParams.MATCH_PARENT,
                        LayoutParams.WRAP_CONTENT
                    ).apply {
                        setMargins(0, 0, 0, 32)
                    }
                    val coverImageView = ImageView(this@StatsActivity)
                    val coverLayoutParams = LinearLayout.LayoutParams(
                        200,
                        300
                    ).apply {
                        setMargins(0, 0, 16, 0)
                    }
                    coverImageView.layoutParams = coverLayoutParams
                    Glide.with(this@StatsActivity).load(coverUrl).into(coverImageView)
                    storyLayout.addView(coverImageView)
                    val storyTextView = TextView(this@StatsActivity)
                    storyTextView.typeface = Typeface.DEFAULT_BOLD
                    storyTextView.text = "Name: $name\nCategory: $category\nDescription: $description\n"
                    storyLayout.addView(storyTextView)
                    storyLayout.setOnClickListener {
                        val intent = Intent(this@StatsActivity, StoryStatsActivity::class.java)
                        intent.putExtra("STORY_ID", storyId)
                        startActivity(intent)
                    }
                    statsLayout.addView(storyLayout)
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // Handle database error
            }
        })
        val navHome = findViewById<Button>(R.id.nav_home)
        val navStats = findViewById<Button>(R.id.nav_stats)
        val navProfile = findViewById<Button>(R.id.nav_profile)
        navHome.setOnClickListener {
            val intent = Intent(this, AuthorHomeActivity::class.java)
            startActivity(intent)
        }
        navStats.setOnClickListener {
            //
        }
        navProfile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
    }
}

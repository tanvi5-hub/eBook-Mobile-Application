package com.example.ebook

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup.LayoutParams
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class StoryStatsActivity : AppCompatActivity() {

    private lateinit var database: FirebaseDatabase
    private lateinit var userRef: DatabaseReference
    private lateinit var statsLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_story_stats)

        database = FirebaseDatabase.getInstance()
        userRef = database.getReference("user")
        statsLayout = findViewById(R.id.activity_stats_layout)

        val storyId = intent.getStringExtra("STORY_ID")
        val authorId = intent.getStringExtra("AUTHOR_ID")
        if (storyId != null) {
            userRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val pathCounts = mutableMapOf<String, Int>()
                    var totalReaders = 0
                    var storyAddedCount = 0
                    val uniqueAuthorFollowers = mutableSetOf<String>()

                    for (userSnapshot in dataSnapshot.children) {
                        val userId = userSnapshot.key ?: continue

                        // Check if user follows the author
                        val bestAuthorId = userSnapshot.child("bestauthor").child("id of author").getValue(String::class.java)
                        if (bestAuthorId == authorId) {
                            uniqueAuthorFollowers.add(userId)
                        }

                        val booklistSnapshot = userSnapshot.child("booklist").child(storyId)
                        if (booklistSnapshot.exists()) {
                            storyAddedCount++
                            val pathSnapshot = booklistSnapshot.child("path")
                            if (pathSnapshot.exists()) {
                                totalReaders++
                                val path = mutableListOf<String>()
                                for (pathEntry in pathSnapshot.children) {
                                    path.add(pathEntry.value.toString())
                                }
                                val pathString = path.joinToString(" -> ")

                                pathCounts[pathString] = pathCounts.getOrDefault(pathString, 0) + 1
                            }
                        }
                    }

                    val authorFollowedCount = uniqueAuthorFollowers.size

                    statsLayout.removeAllViews()
                    for ((path, count) in pathCounts) {
                        val percentage = (count.toDouble() / totalReaders) * 100
                        val pathTextView = TextView(this@StoryStatsActivity)
                        pathTextView.layoutParams = LinearLayout.LayoutParams(
                            LayoutParams.MATCH_PARENT,
                            LayoutParams.WRAP_CONTENT
                        ).apply {
                            setMargins(0, 0, 0, 32)
                        }
                        pathTextView.text = "Path: $path\nChosen by $count readers (${String.format("%.2f", percentage)}%)"
                        pathTextView.setTypeface(pathTextView.typeface, android.graphics.Typeface.BOLD)
                        statsLayout.addView(pathTextView)
                    }

                    // Add the total count of readers who added the story to their library
                    val addedCountTextView = TextView(this@StoryStatsActivity)
                    addedCountTextView.layoutParams = LinearLayout.LayoutParams(
                        LayoutParams.MATCH_PARENT,
                        LayoutParams.WRAP_CONTENT
                    ).apply {
                        setMargins(0, 0, 0, 32)
                    }
                    addedCountTextView.text = "Total readers who added this story to their library: $storyAddedCount"
                    addedCountTextView.setTypeface(addedCountTextView.typeface, android.graphics.Typeface.BOLD)
                    statsLayout.addView(addedCountTextView)

                    // Add the total count of readers who follow the author
                    val followedCountTextView = TextView(this@StoryStatsActivity)
                    followedCountTextView.layoutParams = LinearLayout.LayoutParams(
                        LayoutParams.MATCH_PARENT,
                        LayoutParams.WRAP_CONTENT
                    ).apply {
                        setMargins(0, 0, 0, 32)
                    }
                    followedCountTextView.text = "Total unique readers who follow you: $authorFollowedCount"
                    followedCountTextView.setTypeface(followedCountTextView.typeface, android.graphics.Typeface.BOLD)
                    statsLayout.addView(followedCountTextView)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle database error
                    databaseError.toException().printStackTrace()
                }
            })
        }
        // Navigation button listeners
        val navHome = findViewById<Button>(R.id.nav_home)
        val navStats = findViewById<Button>(R.id.nav_stats)
        val navProfile = findViewById<Button>(R.id.nav_profile)
        navHome.setOnClickListener {
            val intent = Intent(this, AuthorHomeActivity::class.java)
            startActivity(intent)
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

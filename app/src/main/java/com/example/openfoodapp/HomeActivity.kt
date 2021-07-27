package com.example.openfoodapp

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.openfoodapp.fragment.*
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val achievementFragment = AchievementFragment()
        val leaderboardDetailFragment = LeaderboardDetailFragment()
        val scanFragment = ScanFragment()
        val profileFragment = ProfileFragment()

        setCurrentFragment(scanFragment)

        home_bottom_bar.setOnNavigationItemSelectedListener {
            when (it.itemId){
                R.id.main_menu_scan -> setCurrentFragment(scanFragment)
                R.id.main_menu_achievement -> setCurrentFragment(achievementFragment)
                R.id.main_menu_leaderboard -> setCurrentFragment(leaderboardDetailFragment)
                R.id.main_menu_profile -> setCurrentFragment(profileFragment)
            }
            true
        }
    }

    private fun setCurrentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.home_frame_layout, fragment)
            commit()
        }

    override fun onBackPressed() {
        val alertDialog = AlertDialog.Builder(this)

        alertDialog.setTitle("Warning")
        alertDialog.setMessage("Would you quit application ?")

        alertDialog.setPositiveButton("Yes") { dialog: DialogInterface?, which: Int ->
            val intent = Intent(Intent.ACTION_MAIN)
            intent.addCategory(Intent.CATEGORY_HOME)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }

        alertDialog.setNegativeButton(
            "No"
        ) { dialog: DialogInterface, which: Int -> dialog.dismiss() }
        val alert = alertDialog.create()
        alert.show()
    }
}


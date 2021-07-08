package com.example.openfoodapp

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
        val leaderboardFragment = LeaderboardFragment()
        val scanFragment = ScanFragment()
        val socialFragment = SocialFragment()
        val profileFragment = ProfileFragment()

        setCurrentFragment(scanFragment)

        home_bottom_bar.setOnNavigationItemSelectedListener {
            when (it.itemId){
                R.id.main_menu_scan -> setCurrentFragment(scanFragment)
                R.id.main_menu_achievement -> setCurrentFragment(achievementFragment)
                R.id.main_menu_leaderboard -> setCurrentFragment(leaderboardFragment)
                R.id.main_menu_social -> setCurrentFragment(socialFragment)
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
}


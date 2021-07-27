package com.example.openfoodapp.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.openfoodapp.models.Rank
import com.example.openfoodapp.utils.FireBase
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.ProgressBar
import com.example.openfoodapp.R
import com.example.openfoodapp.utils.LeaderboardAdapter
import kotlinx.coroutines.Dispatchers

class LeaderboardDetailFragment : Fragment() {

    private lateinit var unsortedRanking: ArrayList<Rank>
    private lateinit var userRank : Rank
    private var fabExpanded = false
    private lateinit var subLeaderboard: FloatingActionButton
    private lateinit var layoutSubScan : LinearLayout
    private lateinit var layoutSubAchievement : LinearLayout
    private lateinit var layoutSubModification : LinearLayout
    private lateinit var listViewLeaderboard : ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_leaderboard_detail, container, false)

        val progressBar = view.findViewById<ProgressBar>(R.id.progress_bar_leaderboard)
        subLeaderboard = view.findViewById(R.id.action_button_leaderboard)
        val subScan = view.findViewById<FloatingActionButton>(R.id.sub_scan)
        val subAchievement = view.findViewById<FloatingActionButton>(R.id.sub_achievement)
        val subModification = view.findViewById<FloatingActionButton>(R.id.sub_modification)
        layoutSubScan = view.findViewById(R.id.layout_sub_scan)
        layoutSubAchievement = view.findViewById(R.id.layout_sub_achievement)
        layoutSubModification = view.findViewById(R.id.layout_sub_modification)
        val rankCurrentUser = view.findViewById<TextView>(R.id.rank_current_user)
        val nameCurrentUser = view.findViewById<TextView>(R.id.rank_current_user_username)
        val scoreCurrentUser = view.findViewById<TextView>(R.id.rank_current_user_score)

        progressBar.visibility = View.VISIBLE
        GlobalScope.launch(Dispatchers.Main) {
            unsortedRanking = FireBase().getLeaderboard("")
            val sortedRanking : ArrayList<Rank> =
                unsortedRanking.sortedWith(compareBy({ it.score })).reversed() as ArrayList<Rank>
            var i = 0
            var userRanking = 0
            for (rank in sortedRanking){
                i += 1
                if (rank.name == Firebase.auth.currentUser!!.displayName){
                    userRank = sortedRanking[i-1]
                    userRanking = i
                }
            }
            rankCurrentUser.text = userRanking.toString()
            nameCurrentUser.text = userRank.name
            scoreCurrentUser.text = userRank.score.toString()

            val adapter = LeaderboardAdapter(activity!!,sortedRanking)
            listViewLeaderboard = view.findViewById(R.id.listview_leaderboard)
            listViewLeaderboard.adapter = adapter
            progressBar.visibility = View.INVISIBLE
        }


        subLeaderboard.setOnClickListener {
            if (fabExpanded) {
                closeSubMenus()
            } else {
                openSubMenus()
            }
        }
        subScan.setOnClickListener {
            GlobalScope.launch(Dispatchers.Main) {
                unsortedRanking = FireBase().getLeaderboard("wjIijgdTFHe20ilwmAjL")
                val sortedRanking : ArrayList<Rank> =
                    unsortedRanking.sortedWith(compareBy({ it.score })).reversed() as ArrayList<Rank>
                var i = 0
                var userRanking = 0
                for (rank in sortedRanking){
                    i += 1
                    if (rank.name == Firebase.auth.currentUser!!.displayName){
                        userRank = sortedRanking[i-1]
                        userRanking = i
                    }
                }
                rankCurrentUser.text = userRanking.toString()
                nameCurrentUser.text = userRank.name
                scoreCurrentUser.text = userRank.score.toString()
                val adapter = LeaderboardAdapter(activity!!,sortedRanking)
                listViewLeaderboard = view.findViewById(R.id.listview_leaderboard)
                listViewLeaderboard.adapter = adapter
            }
            closeSubMenus()
        }
        subAchievement.setOnClickListener {
            GlobalScope.launch(Dispatchers.Main) {
                unsortedRanking = FireBase().getLeaderboard("")
                val sortedRanking : ArrayList<Rank> =
                    unsortedRanking.sortedWith(compareBy({ it.score })).reversed() as ArrayList<Rank>
                var i = 0
                var userRanking = 0
                for (rank in sortedRanking){
                    i += 1
                    if (rank.name == Firebase.auth.currentUser!!.displayName){
                        userRank = sortedRanking[i-1]
                        userRanking = i
                    }
                }
                rankCurrentUser.text = userRanking.toString()
                nameCurrentUser.text = userRank.name
                scoreCurrentUser.text = userRank.score.toString()
                val adapter = LeaderboardAdapter(activity!!,sortedRanking)
                listViewLeaderboard = view.findViewById(R.id.listview_leaderboard)
                listViewLeaderboard.adapter = adapter
            }
            closeSubMenus()
        }
        subModification.setOnClickListener {
            GlobalScope.launch(Dispatchers.Main) {
                unsortedRanking = FireBase().getLeaderboard("EULMCPXQksK7j7WLj39Z")
                val sortedRanking : ArrayList<Rank> =
                    unsortedRanking.sortedWith(compareBy({ it.score })).reversed() as ArrayList<Rank>
                var i = 0
                var userRanking = 0
                for (rank in sortedRanking){
                    i += 1
                    if (rank.name == Firebase.auth.currentUser!!.displayName){
                        userRank = sortedRanking[i-1]
                        userRanking = i
                    }
                }
                rankCurrentUser.text = userRanking.toString()
                nameCurrentUser.text = userRank.name
                scoreCurrentUser.text = userRank.score.toString()
                val adapter = LeaderboardAdapter(activity!!,sortedRanking)
                listViewLeaderboard = view.findViewById(R.id.listview_leaderboard)
                listViewLeaderboard.adapter = adapter
            }
            closeSubMenus()
        }

        closeSubMenus()

        return view
    }


    private fun closeSubMenus() {
        layoutSubScan.visibility = View.INVISIBLE
        layoutSubAchievement.visibility = View.INVISIBLE
        layoutSubModification.visibility = View.INVISIBLE
        subLeaderboard.setImageResource(R.drawable.podium_1_)
        fabExpanded = false
    }

    private fun openSubMenus() {
        layoutSubScan.visibility = View.VISIBLE
        layoutSubAchievement.visibility = View.VISIBLE
        layoutSubModification.visibility = View.VISIBLE
        subLeaderboard.setImageResource(R.drawable.ic_baseline_close_24)
        fabExpanded = true
    }
}
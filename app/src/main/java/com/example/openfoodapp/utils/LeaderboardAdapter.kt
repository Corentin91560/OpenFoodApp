package com.example.openfoodapp.utils

import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.openfoodapp.R
import com.example.openfoodapp.models.Achievement
import com.example.openfoodapp.models.Rank
import org.w3c.dom.Text

class LeaderboardAdapter(var context: Context, var achievements: ArrayList<Rank>) :
    BaseAdapter() {
    override fun getCount(): Int {
        return achievements.size
    }

    override fun getItem(position: Int): Any {
        return achievements.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view :View = View.inflate(context, R.layout.leaderboard_list_item,null)

        val username = view.findViewById<TextView>(R.id.leaderboard_username)
        val score = view.findViewById<TextView>(R.id.leaderboard_score)
        val ranking = view.findViewById<TextView>(R.id.rank)
        val rankImage = view.findViewById<ImageView>(R.id.trophee_leaderboard)

        username.text = achievements[position].name
        score.text = achievements[position].score.toString()
        ranking.text = (position + 1).toString()
        if (position+1 == 1){
            rankImage.setImageResource(R.drawable.trophee_gold)
        }
        if (position+1 == 2){
            rankImage.setImageResource(R.drawable.trophee_silver)
        }
        if (position+1 == 3){
            rankImage.setImageResource(R.drawable.trophee_bronze)
        }

        return view
    }
}
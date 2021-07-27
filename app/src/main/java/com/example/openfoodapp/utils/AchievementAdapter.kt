package com.example.openfoodapp.utils

import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.openfoodapp.R
import com.example.openfoodapp.models.Achievement

class AchievementAdapter(var context: Context, var achievements: ArrayList<Achievement>) :
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
        var view :View = View.inflate(context, R.layout.achievement_list_item,null)

        var imageAchievement: ImageView = view.findViewById(R.id.achievement_image)
        var nameAchievement: TextView = view.findViewById(R.id.tv_achivement_name)

        if (achievements[position].step1!! <= achievements[position].usercount){
            if (achievements[position].step2 == null){
                imageAchievement.setImageResource(R.drawable.platinium)
            }else if(achievements[position].step2!! <= achievements[position].usercount && achievements[position].usercount<achievements[position].step3!!){
                imageAchievement.setImageResource(R.drawable.silver)
            }else if(achievements[position].step3!! <= achievements[position].usercount && achievements[position].usercount<achievements[position].step4!!){
                imageAchievement.setImageResource(R.drawable.gold)
            }else if(achievements[position].step4!! <= achievements[position].usercount){
                imageAchievement.setImageResource(R.drawable.platinium)
            }else{
                imageAchievement.setImageResource(R.drawable.bronze)
            }
        }else{
            imageAchievement.setImageResource(R.drawable.closed)
        }

        nameAchievement.text = achievements[position].name

        return view
    }
}
package com.example.openfoodapp.fragment

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.openfoodapp.R
import com.example.openfoodapp.models.Achievement
import com.example.openfoodapp.utils.AchievementAdapter
import com.example.openfoodapp.utils.FireBase
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*


/**
 * A simple [Fragment] subclass.
 * Use the [AchievementFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AchievementFragment : Fragment()  {
    private var listAchievement = ArrayList<Achievement>()
    private lateinit var gridView:GridView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


    @DelicateCoroutinesApi
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
        ): View? {
        val view = inflater.inflate(R.layout.fragment_achievement, container, false)
        val progressBar = view.findViewById<ProgressBar>(R.id.progress_bar_achievement)
        GlobalScope.launch(Dispatchers.Main) {
            progressBar.visibility = View.VISIBLE
            listAchievement = FireBase().getAllAchievements()
            for (achievement in listAchievement){
                achievement.usercount = Firebase.auth.currentUser?.let {
                    FireBase().getAchievementProgression(
                        it.uid,achievement.id)
                }!!
            }

            val adapter = AchievementAdapter(activity!!,listAchievement)
            gridView = view.findViewById(R.id.grid_view_achievement)
            gridView.adapter = adapter
            progressBar.visibility = View.INVISIBLE
            gridView.onItemClickListener =
                AdapterView.OnItemClickListener { parent, view, position, id ->
                    val achievement : Achievement = parent.getItemAtPosition(position) as Achievement
                    showdescription(achievement.description,achievement.usercount.toString(),maxStep(achievement).toString())
                }
        }
        return view
    }


    private fun showdescription(description : String, count : String , step : String) {

            val builder: AlertDialog.Builder = AlertDialog.Builder(activity!!)
            builder.setTitle("Description Achievement")
            builder.setMessage("$description \nprogression : $count/$step")
            builder.setNegativeButton("quit") { dialog, which ->
                dialog.cancel()
            }
            builder.show()

    }
    private fun maxStep(achievement : Achievement) : Int{
        var maxstep = 0;

        when {
            achievement.step2 == null -> {
                maxstep = 1
            }
            achievement.usercount< achievement.step1!! -> {
                maxstep = achievement.step1.toInt()
            }
            achievement.usercount<achievement.step2 -> {
                maxstep = achievement.step2.toInt()
            }
            achievement.usercount< achievement.step3!! -> {
                maxstep = achievement.step3.toInt()
            }
            achievement.usercount< achievement.step4!! -> {
                maxstep = achievement.step4.toInt()
            }
            achievement.usercount> achievement.step4!! -> {
                maxstep = achievement.step4.toInt()
            }
        }
        return maxstep
    }
}
package com.satverse.suraksha.onboarding

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.satverse.suraksha.R
import com.satverse.suraksha.onboarding.screens.FirstScreen
import com.satverse.suraksha.onboarding.screens.SecondScreen
import com.satverse.suraksha.onboarding.screens.ThirdScreen
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator

class ViewPagerFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_view_pager, container, false)

        val fragmentList = arrayListOf(
            FirstScreen(),
            SecondScreen(),
            ThirdScreen()
        )

        val adapter = ViewPagerAdapter(
            fragmentList,
            requireActivity().supportFragmentManager,
            lifecycle
        )

        val dotsIndicator = view.findViewById<DotsIndicator>(R.id.dots_indicator)
        val viewPager = view.findViewById<ViewPager2>(R.id.viewPager)

        viewPager.adapter = adapter

        dotsIndicator.attachTo(viewPager)

        view.findViewById<Button>(R.id.login).setOnClickListener {
            findNavController().navigate(R.id.action_onboarding_to_login)
            onBoardingFinished()
        }

        return view
    }

    private fun onBoardingFinished(){
        val sharedPref = requireActivity().getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putBoolean("Finished", true)
        editor.apply()
    }
}
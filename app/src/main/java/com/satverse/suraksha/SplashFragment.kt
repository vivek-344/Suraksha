package com.satverse.suraksha

import android.content.Context
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController

class SplashFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Handler().postDelayed({
            if (onBoardingFinished()) {
                if (isUserLoggedIn()) {
                    findNavController().navigate(R.id.action_splash_to_landingPage)
                }
                else {
                    findNavController().navigate(R.id.action_splash_to_login)
                }
            }
            else {
                findNavController().navigate(R.id.action_splash_to_onboarding)
            }
        }, 3000)


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    private fun onBoardingFinished(): Boolean {
        val sharedPref = requireActivity().getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
        return sharedPref.getBoolean("Finished", false)
    }

    private fun isUserLoggedIn(): Boolean {
        val sharedPref = requireActivity().getSharedPreferences("logIn", Context.MODE_PRIVATE)
        return sharedPref.getBoolean("LoggedIn", false)
    }

}
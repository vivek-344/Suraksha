package com.satverse.suraksha

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class SplashFragment : Fragment() {

    private var onBoardingFinished = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onBoardingFinished = onBoardingFinished()

        @Suppress("DEPRECATION")
        Handler().postDelayed({
            if (isAdded) {
                val navController = findNavController()
                if (onBoardingFinished) {
                    if (isUserLoggedIn()) {
                        navController.navigate(R.id.action_splash_to_landingPage)
                    } else {
                        navController.navigate(R.id.action_splash_to_login)
                    }
                } else {
                    navController.navigate(R.id.action_splash_to_onboarding)
                }
            }
        }, 3000)
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

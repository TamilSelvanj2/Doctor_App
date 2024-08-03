package com.bics.expense.doctormodule.dashboard

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.ViewPager
import com.bics.expense.doctormodule.R
import com.bics.expense.doctormodule.fragment.newRequest.NewRequestFragment
import com.bics.expense.doctormodule.fragment.pastHistory.PastHistoryFragment
import com.bics.expense.doctormodule.fragment.rejected.RejectedFragment
import com.bics.expense.doctormodule.fragment.upcoming.UpcomingFragment
import com.google.android.material.tabs.TabLayout

class AppointmentsFragment : Fragment() {

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager
    private lateinit var adapter: AppointmentAdapter
    private lateinit var newRequestFragment: NewRequestFragment
    private lateinit var upcomingFragment: UpcomingFragment
    private lateinit var pastHistory: PastHistoryFragment
    private lateinit var rejectedFragment: RejectedFragment


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_appointments, container, false)

        tabLayout = view.findViewById(R.id.tabLayout)
        viewPager = view.findViewById(R.id.viewPager)

        adapter = AppointmentAdapter(childFragmentManager)


        newRequestFragment = NewRequestFragment()
        upcomingFragment = UpcomingFragment()
        pastHistory = PastHistoryFragment()
        rejectedFragment = RejectedFragment()


        adapter.addFragment(newRequestFragment,"NewRequest")
        adapter.addFragment(upcomingFragment,"Upcoming")
        adapter.addFragment(pastHistory,"PastHistory")
        adapter.addFragment(rejectedFragment,"Rejected")

        viewPager.adapter = adapter

        // Connect viewPager with tabLayout
        tabLayout.setupWithViewPager(viewPager)

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    val position = it.position
                    viewPager.currentItem = position
                    // Trigger API call here
                    triggerApiCallForSelectedTab(position)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabReselected(tab: TabLayout.Tab?) {
                tab?.let {
                    val position = it.position
                    // Trigger API call here
                    triggerApiCallForSelectedTab(position)


                }
            }
        })

        return view
    }

    private fun triggerApiCallForSelectedTab(position: Int) {
        Log.d("AppointmentFragment", "Tab selected: $position")

        when (position) {
            0 -> {
                // Trigger API call for NewRequest tab
                Log.d("AppointmentFragment", "Refreshing NewRequest data")

                newRequestFragment.refreshData()
            }

            1 -> {
                // Trigger API call for Upcoming tab
                Log.d("AppointmentFragment", "Refreshing Upcoming data")

                upcomingFragment.refreshData()
            }

            2 -> {
                // Trigger API call for PastHistory tab
                Log.d("AppointmentFragment", "Refreshing PastHistory data")

                pastHistory.refreshData()

            }

            3 -> {
                // Trigger API call for Rejected tab
                Log.d("AppointmentFragment", "Refreshing Rejected data")

                rejectedFragment.refreshData()

            }
        }
    }
}
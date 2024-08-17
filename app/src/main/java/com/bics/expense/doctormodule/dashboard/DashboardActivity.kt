package com.bics.expense.doctormodule.dashboard



import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import com.bics.expense.doctormodule.R
import com.bics.expense.doctormodule.databinding.ActivityDashboardBinding
import com.bics.expense.doctormodule.fragment.patientDetail.PatientsFragment
import com.bics.expense.doctormodule.fragment.profile.ProfileFragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.google.android.material.navigation.NavigationView

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var notification: ImageView
    private lateinit var notificationSymbol: ImageView

    private var BASE_URL = "https://myclinicsapi.bicsglobal.com"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_dashboard)


        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView?.let { navView ->
            val headerView = navView.getHeaderView(0)
            val profileNameTextView = headerView.findViewById<TextView>(R.id.profileName)
            val profileEmailTextView = headerView.findViewById<TextView>(R.id.profileEmail)
            val profileImageView = headerView.findViewById<ImageView>(R.id.doctor_icon)


            notification = findViewById(R.id.notificationImageView)
            notificationSymbol = findViewById(R.id.notificationSymbolImageView)

            val accountId = intent.getStringExtra("accountID")
            val firstName = intent.getStringExtra("firstName")
            val lastName = intent.getStringExtra("lastName")
            val profileName = "$firstName $lastName"
            val profileEmail = intent.getStringExtra("role")
            val profileImage = intent.getStringExtra("profileImage")

            profileNameTextView.text = profileName
            profileEmailTextView.text = profileEmail
// Load profile image using Glide with base URL and image URL concatenation
            profileImage.let { imageUrl ->
                val fullImageUrl = BASE_URL + imageUrl // Concatenate base URL with image URL
                Glide.with(this)
                    .load(fullImageUrl)
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.error_image)
                    .transform(CircleCrop()) // Apply CircleCrop transformation
                    .into(profileImageView)
            }
            notification.setOnClickListener {

            }
            notificationSymbol.setOnClickListener {


            }
        }
        // Setup drawer and initial fragment
        setupDrawer()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, DashboardFragment())
            .commit()
    }


    private fun updateToolbar(title: String, color: Int) {
        binding.toolbar.title = title
        binding.toolbar.setBackgroundColor(color)
    }

    private fun setupDrawer() {


        val toggle = androidx.appcompat.app.ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.toolbar,
            R.string.open_nav,
            R.string.close_nav
        )
        binding.drawerLayout?.addDrawerListener(toggle)
        toggle.syncState()


        binding.navView?.findViewById<TextView>(R.id.appointment)?.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, AppointmentsFragment())
                .commit()
            updateToolbar("Appointments", ContextCompat.getColor(this, R.color.white))
            binding.drawerLayout?.closeDrawer(GravityCompat.START)

        }

        binding.navView?.findViewById<TextView>(R.id.booking)?.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, DashboardFragment())
                .commit()
            updateToolbar("Dashboard", ContextCompat.getColor(this, R.color.white))
            binding.drawerLayout?.closeDrawer(GravityCompat.START)

        }

        binding.navView?.findViewById<TextView>(R.id.patient)?.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, PatientsFragment())
                .commit()
            updateToolbar(
                "Patient Details",
                ContextCompat.getColor(this, R.color.nav_item_selected_color)
            )
            binding.drawerLayout?.closeDrawer(GravityCompat.START)

        }

        binding.navView?.findViewById<TextView>(R.id.nav_profile)?.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ProfileFragment())
                .commit()
            updateToolbar("Profile", ContextCompat.getColor(this, R.color.white))
            binding.drawerLayout?.closeDrawer(GravityCompat.START)

        }

        binding.navView?.findViewById<TextView>(R.id.nav_logout)?.setOnClickListener {
            // Handle logout action
            finish() // Finish the current activity to return to the login screen
            Toast.makeText(this, "Logout!", Toast.LENGTH_SHORT).show()
            binding.drawerLayout?.closeDrawer(GravityCompat.START)

        }

        // Initialize drawerLayout using binding
        drawerLayout = binding.drawerLayout!!
    }
}
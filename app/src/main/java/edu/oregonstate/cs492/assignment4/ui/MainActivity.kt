    package edu.oregonstate.cs492.assignment4.ui


    import android.os.Bundle
    import android.view.Menu
    import android.view.MenuInflater
    import android.view.MenuItem
    import androidx.activity.enableEdgeToEdge
    import androidx.appcompat.app.ActionBarDrawerToggle
    import androidx.appcompat.app.AppCompatActivity
    import androidx.core.view.GravityCompat
    import androidx.core.view.MenuProvider
    import androidx.drawerlayout.widget.DrawerLayout
    import androidx.lifecycle.Lifecycle
    import androidx.lifecycle.ViewModelProvider
    import androidx.navigation.findNavController
    import androidx.navigation.fragment.NavHostFragment
    import androidx.navigation.ui.AppBarConfiguration
    import androidx.navigation.ui.navigateUp
    import androidx.navigation.ui.setupActionBarWithNavController
    import com.google.android.material.appbar.MaterialToolbar
    import com.google.android.material.navigation.NavigationView
    import edu.oregonstate.cs492.assignment4.R
    import edu.oregonstate.cs492.assignment4.data.ForecastLocation

    import androidx.navigation.findNavController

    import androidx.navigation.ui.setupActionBarWithNavController
    import androidx.navigation.ui.setupWithNavController
    import androidx.preference.PreferenceManager

    /*
     * Often, we'll have sensitive values associated with our code, like API keys, that we'll want to
     * keep out of our git repo, so random GitHub users with permission to view our repo can't see them.
     * The OpenWeather API key is like this.  We can keep our API key out of source control using the
     * technique described below.  Note that values configured in this way can still be seen in the
     * app bundle installed on the user's device, so this isn't a safe way to store values that need
     * to be kept secret at all costs.  This will only keep them off of GitHub.
     *
     * The Gradle scripts for this app are set up to read your API key from a special Gradle file
     * that lives *outside* your project directory.  This file called `gradle.properties`, and it
     * should live in your GRADLE_USER_HOME directory (this will usually be `$HOME/.gradle/` in
     * MacOS/Linux and `$USER_HOME/.gradle/` in Windows).  To store your API key in `gradle.properties`,
     * make sure that file exists in the correct location, and then add the following line:
     *
     *   OPENWEATHER_API_KEY="<put_your_own_OpenWeather_API_key_here>"
     *
     * If your API key is stored in that way, the Gradle build for this app will grab it and write it
     * into the string resources for the app with the resource name "openweather_api_key".  You'll be
     * able to access your key in the app's Kotlin code the same way you'd access any other string
     * resource, e.g. `getString(R.string.openweather_api_key)`.  This is what's done in the code below
     * when the OpenWeather API key is needed.
     *
     * If you don't mind putting your OpenWeather API key on GitHub, then feel free to just hard-code
     * it in the app. 🤷‍
     */

    class MainActivity : AppCompatActivity() {
        private lateinit var appBarConfiguration: AppBarConfiguration
        private lateinit var drawerLayout: DrawerLayout
        private lateinit var sharedViewModel: SharedViewModel

        override fun onCreate(savedInstanceState: Bundle?) {
            enableEdgeToEdge()
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_main)

            sharedViewModel = ViewModelProvider(this).get(SharedViewModel::class.java)

            setupNavigationDrawer()


            setupMenuProvider()


            observeSavedLocations()
        }

        private fun setupNavigationDrawer() {
            drawerLayout = findViewById(R.id.drawer_layout)
            val navigationView: NavigationView = findViewById(R.id.nav_view)
            val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
            val navController = navHostFragment.navController
            appBarConfiguration = AppBarConfiguration(setOf(R.id.five_day_forecast, R.id.current_weather,R.id.settings), drawerLayout) // Adjust the setOf with your actual fragment ids

            val appBar: MaterialToolbar = findViewById(R.id.top_app_bar)
            setSupportActionBar(appBar)
            setupActionBarWithNavController(navController, appBarConfiguration)
            navigationView.setupWithNavController(navController)
            navigationView.setNavigationItemSelectedListener { menuItem ->

                when (menuItem.itemId) {
                    R.id.nav_current_weather -> {

                        navController.navigate(R.id.current_weather)
                    }
                    R.id.nav_five_day_forecast -> {

                        navController.navigate(R.id.five_day_forecast)
                    }
                    R.id.nav_settings -> {

                        navController.navigate(R.id.settings)
                    }
                }
                drawerLayout.closeDrawer(GravityCompat.START)
                true
            }
        }

        private fun setupMenuProvider() {
            addMenuProvider(object : MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.main_menu, menu)
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                    return when (menuItem.itemId) {
                        R.id.action_settings -> {
                            findNavController(R.id.nav_host_fragment).navigate(R.id.settings)
                            true
                        }
                        else -> false
                    }
                }
            }, this, Lifecycle.State.STARTED)
        }



        private fun observeSavedLocations() {
            sharedViewModel.savedLocations.observe(this) { locations ->
                updateNavigationDrawer(locations)
            }
        }
        private fun updateNavigationDrawer(locations: List<ForecastLocation>) {
            val navigationView: NavigationView = findViewById(R.id.nav_view)
            val menu = navigationView.menu

            val citiesMenu = menu.findItem(R.id.saved_cities)?.subMenu ?: menu.addSubMenu("Cities")
            citiesMenu.clear()

            locations.forEach { location ->
                citiesMenu.add(Menu.NONE, Menu.NONE, Menu.NONE, location.cityName).setOnMenuItemClickListener { menuItem ->

                    drawerLayout.closeDrawers()


                    val editor = PreferenceManager.getDefaultSharedPreferences(applicationContext).edit()
                    editor.putString(getString(R.string.pref_city_key), menuItem.title.toString())
                    editor.apply()


                    sharedViewModel.updateLocationTimestamp(location.cityName)


                    val navController = findNavController(R.id.nav_host_fragment)
                    navController.navigate(R.id.current_weather)

                    true
                }
            }
        }

        override fun onSupportNavigateUp(): Boolean {
            return findNavController(R.id.nav_host_fragment).navigateUp(appBarConfiguration)
                    || super.onSupportNavigateUp()
        }



    }






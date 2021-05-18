package unipi.p17168.imagesician

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.google.android.material.bottomnavigation.BottomNavigationView

class ImagesicianActivity : AppCompatActivity(),
    BottomNavigationView.OnNavigationItemSelectedListener {

    enum class MainScreen(@IdRes val menuItemId: Int,
                          @DrawableRes val menuItemIconId: Int,
                          @StringRes val titleStringId: Int,
                          val fragment: Fragment) {

        /**
         * Screens available for display in the main screen, with their respective titles,
         * icons, and menu item IDs and fragments.
         */
            LOGS(R.id.bottom_navigation_item_image, R.drawable.ic_add_photo_foreground, R.string.image_item, ImageFragment()),
            PROGRESS(R.id.bottom_navigation_item_barcode, R.drawable.ic_qr_code_foreground, R.string.barcode_item, QrcodeFragment()),
            PROFILE(R.id.bottom_navigation_item_settings, R.drawable.ic_horizontal_dots_foreground, R.string.settings_item, SettingsFragment())
    }

    private lateinit var viewPager: ViewPager
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var mainPagerAdapter: MainPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_imagesician)


                // Initialize components/views.
                viewPager = findViewById(R.id.view_pager)
                bottomNavigationView = findViewById(R.id.bottom_navigation_view)
                mainPagerAdapter = MainPagerAdapter(supportFragmentManager)

                // Set items to be displayed.
                mainPagerAdapter.setItems(arrayListOf(MainScreen.LOGS, MainScreen.PROGRESS, MainScreen.PROFILE))

                // Show the default screen.
                val defaultScreen = MainScreen.LOGS
                scrollToScreen(defaultScreen)
                selectBottomNavigationViewMenuItem(defaultScreen.menuItemId)
                supportActionBar?.setTitle(defaultScreen.titleStringId)

                // Set the listener for item selection in the bottom navigation view.
                bottomNavigationView.setOnNavigationItemSelectedListener(this)

                // Attach an adapter to the view pager and make it select the bottom navigation
                // menu item and change the title to proper values when selected.
                viewPager.adapter = mainPagerAdapter
                viewPager.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
                    override fun onPageSelected(position: Int) {
                        val selectedScreen = mainPagerAdapter.getItems()[position]
                        selectBottomNavigationViewMenuItem(selectedScreen.menuItemId)
                        supportActionBar?.setTitle(selectedScreen.titleStringId)
                    }
                })
            }

            /**
             * Scrolls `ViewPager` to show the provided screen.
             */
            private fun scrollToScreen(mainScreen: MainScreen) {
                val screenPosition = mainPagerAdapter.getItems().indexOf(mainScreen)
                if (screenPosition != viewPager.currentItem) {
                    viewPager.currentItem = screenPosition
                }
            }

            /**
             * Selects the specified item in the bottom navigation view.
             */
            private fun selectBottomNavigationViewMenuItem(@IdRes menuItemId: Int) {
                bottomNavigationView.setOnNavigationItemSelectedListener(null)
                bottomNavigationView.selectedItemId = menuItemId
                bottomNavigationView.setOnNavigationItemSelectedListener(this)
            }

            /**
             * Listener implementation for registering bottom navigation clicks.
             */
            override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
                getMainScreenForMenuItem(menuItem.itemId)?.let {
                    scrollToScreen(it)
                    supportActionBar?.setTitle(it.titleStringId)
                    return true
                }
                return false
            }
    }

    fun getMainScreenForMenuItem(menuItemId: Int): ImagesicianActivity.MainScreen? {
        for (mainScreen in ImagesicianActivity.MainScreen.values()) {
            if (mainScreen.menuItemId == menuItemId) {
                return mainScreen
            }
        }
        return null
    }

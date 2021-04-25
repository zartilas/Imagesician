package unipi.p17168.imagesician

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class MainPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

    private val screens = arrayListOf<ImagesicianActivity.MainScreen>()

        fun setItems(screens: List<ImagesicianActivity.MainScreen>) {
            this.screens.apply {
                clear()
                addAll(screens)
                notifyDataSetChanged()
            }
        }

        fun getItems(): List<ImagesicianActivity.MainScreen> {
            return screens
        }

        override fun getItem(position: Int): Fragment {
            return screens[position].fragment
        }

        override fun getCount(): Int {
            return screens.size
        }
}

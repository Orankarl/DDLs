package com.example.orankarl.ddls

import android.os.Bundle
import android.service.autofill.FillEventHistory
import android.support.design.widget.Snackbar
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_main_bar.*
import android.support.v4.view.ViewPager
import android.view.View
import android.widget.Adapter
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.app.FragmentTransaction
import android.widget.DatePicker
import android.widget.TextView
import com.afollestad.materialdialogs.DialogAction
import com.example.orankarl.ddls.R.id.action_add
import com.afollestad.materialdialogs.MaterialDialog
import com.example.orankarl.ddls.R.id.drawer_layout
import kotlinx.android.synthetic.main.add_deadline_dialog.view.*
import kotlinx.android.synthetic.main.deadline_item.*
import kotlinx.android.synthetic.main.deadline_item.view.*
import java.util.*


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var dialog:AddDeadlineDialog
    private lateinit var adapter:MainActivity.Adapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        setupViewPager(viewpager)

        tabs.setupWithViewPager(viewpager)
    }

    private fun setupViewPager(viewPager: ViewPager) {
        adapter = Adapter(supportFragmentManager)
        adapter.addFragment(DeadlineFragment(), "DDL")
        adapter.addFragment(NoticeFragment(), "Notice")
        adapter.addFragment(ChatListFragment(), "Group")
        viewPager.adapter = adapter
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> return true
            R.id.action_add -> {
                menuActionAdd()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun menuActionAdd() {
//        dialog = AddDeadlineDialog()
//        dialog.show(supportFragmentManager, dialog.tag)
//        var dialog = MyDialog(this)
//        dialog.show()
        val materialDialog = MaterialDialog.Builder(this)
                .title(R.string.add_dialog_title_code)
                .customView(R.layout.add_deadline_dialog, true)
                .positiveText("add")
                .negativeText("Cancel")
                .onPositive(MaterialDialog.SingleButtonCallback { dialog, which ->
                    val datePicker:DatePicker = dialog.findViewById(R.id.datePicker) as DatePicker
                    var calendar:Calendar = Calendar.getInstance()
                    calendar.set(Calendar.YEAR, datePicker.year)
                    calendar.set(Calendar.MONTH, datePicker.month)
                    calendar.set(Calendar.DAY_OF_MONTH, datePicker.dayOfMonth)

                    val title:TextView = dialog.findViewById(R.id.add_dialog_title) as TextView
                    val info:TextView = dialog.findViewById(R.id.add_dialog_info) as TextView
                    val fragmentManager = supportFragmentManager
                    for (fragment in fragmentManager.fragments) {
                        if (fragment != null && fragment.isVisible && fragment is DeadlineFragment) {
                            (fragment as DeadlineFragment).deadlineList.add(calendar, title.text.toString(), info.text.toString())
                            (fragment as DeadlineFragment).deadlineList.updateDeadlineList()
                            (fragment as DeadlineFragment).onRefresh()
                        }
                    }
                })
                .show()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_camera -> {
                // Handle the camera action
            }
            R.id.nav_gallery -> {

            }
            R.id.nav_slideshow -> {

            }
            R.id.nav_manage -> {

            }
            R.id.nav_share -> {

            }
            R.id.nav_send -> {

            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    internal class Adapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
        private val mFragments = ArrayList<Fragment>()
        private val mFragmentTitles = ArrayList<String>()

        fun addFragment(fragment: Fragment, title: String) {
            mFragments.add(fragment)
            mFragmentTitles.add(title)
        }

        override fun getItem(position: Int): Fragment {
            return mFragments[position]
        }

        override fun getCount(): Int {
            return mFragments.size
        }

        override fun getPageTitle(position: Int): CharSequence {
            return mFragmentTitles[position]
        }
    }
}

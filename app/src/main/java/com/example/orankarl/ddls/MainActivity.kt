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
import android.util.Log
import android.widget.DatePicker
import android.widget.TextView
import android.widget.Toast
import com.afollestad.materialdialogs.DialogAction
import com.afollestad.materialdialogs.MaterialDialog
import com.example.orankarl.ddls.R.id.*
import kotlinx.android.synthetic.main.add_deadline_dialog.view.*
import kotlinx.android.synthetic.main.deadline_item.*
import kotlinx.android.synthetic.main.deadline_item.view.*
import org.litepal.LitePal
import org.litepal.crud.DataSupport
import java.util.*


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, DeadlineFragment.CurrentUserNameListener {

//    private lateinit var dialog:AddDeadlineDialog
    private lateinit var adapter:MainActivity.Adapter
    private lateinit var currentUser:User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        setupViewPager(viewpager)
        tabs.setupWithViewPager(viewpager)

        initializeDatabase()
    }

    private fun setupViewPager(viewPager: ViewPager) {
        adapter = Adapter(supportFragmentManager)
        adapter.addFragment(DeadlineFragment(), "DDL")
        adapter.addFragment(NoticeFragment(), "Notice")
        adapter.addFragment(ChatListFragment(), "Group")
        viewPager.adapter = adapter
    }

    private fun initializeDatabase() {
        LitePal.getDatabase()
        var userList:List<User> = DataSupport.where("username == ?", "local").find(User::class.java)
        if (userList.isEmpty()) {
            var user = User()
            user.username = "local"
            user.save()
            currentUser = user
        } else {
            currentUser = userList[0]
        }

        DataSupport.deleteAll(Deadline::class.java)
        Deadline(getNewCalendar(2018, 2, 11).timeInMillis,
                "组合数学作业",
                "第十三次",
                currentUser.username).save()

        val deadline1 = Deadline(getNewCalendar(2017, 11, 10).timeInMillis,
                "图形学大作业",
                "Unity Project. Working with A, B, C, D, E and F. Be responsible for OBing.",
                currentUser.username)
        deadline1.save()

        val deadline2 = Deadline(getNewCalendar(2018, 1, 1).timeInMillis,
                "数据库大作业",
                "",
                currentUser.username)
        deadline2.save()

        val deadline3 = Deadline(getNewCalendar(2018, 1, 1).timeInMillis,
                "人工智能大作业",
                "Building neural network by C++ (Without using any existing package).",
                currentUser.username)
        deadline3.save()
    }

    public override fun getCurrentUser():User {
        return currentUser
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
        MaterialDialog.Builder(this)
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
                            val code = (fragment as DeadlineFragment).deadlineList.add(calendar, title.text.toString(), info.text.toString(), currentUser)
                            if (code == 0) {
                                (fragment as DeadlineFragment).deadlineList.updateDeadlineList()
                                (fragment as DeadlineFragment).onRefresh()
                                Toast.makeText(this, "New deadline added successfully", Toast.LENGTH_SHORT).show()
                            } else if (code == 1) {
                                Toast.makeText(this, "Title cannot be empty!", Toast.LENGTH_SHORT).show()
                            } else if (code == 2) {
                                Toast.makeText(this, "Cannot add a past deadline", Toast.LENGTH_SHORT).show()
                            }

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

    private fun getNewCalendar(year:Int, month:Int, day:Int):Calendar {
        return Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month-1)
            set(Calendar.DAY_OF_MONTH, day)
        }
    }
}

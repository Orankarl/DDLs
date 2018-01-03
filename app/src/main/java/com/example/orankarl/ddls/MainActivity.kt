package com.example.orankarl.ddls

import android.Manifest
import android.app.ProgressDialog.show
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.preference.PreferenceManager
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
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.app.FragmentTransaction
import android.support.v4.content.ContextCompat.startActivity
import android.util.Log
import android.widget.*
import com.afollestad.materialdialogs.DialogAction
import com.afollestad.materialdialogs.MaterialDialog
import com.example.orankarl.ddls.R.id.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.add_deadline_dialog.view.*
import kotlinx.android.synthetic.main.deadline_item.*
import kotlinx.android.synthetic.main.deadline_item.view.*
import kotlinx.android.synthetic.main.finished_deadline_dialog.view.*
import kotlinx.android.synthetic.main.nav_header_main.*
import org.litepal.LitePal
import org.litepal.crud.DataSupport
import java.awt.font.NumericShaper
import java.io.File
import java.io.FileOutputStream
import java.util.*


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
        DeadlineFragment.DeadlineCurrentUserGetter, NoticeFragment.NoticeCurrentUserGetter, ChatListFragment.ChatCurrentUserListener {

//    private lateinit var dialog:AddDeadlineDialog
    private lateinit var adapter:MainActivity.Adapter
    private lateinit var currentUser:User
    private lateinit var manager:DatabaseManager
    private lateinit var pref:SharedPreferences
    private lateinit var editor:SharedPreferences.Editor
    private lateinit var lastUsername:String
    private var isLogin:Boolean = false
    private lateinit var headerView:View


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

        initDrawer()

        initDatabase()
    }

    private fun setupViewPager(viewPager: ViewPager) {
        adapter = Adapter(supportFragmentManager)
        adapter.addFragment(DeadlineFragment(), "DDL")
        adapter.addFragment(NoticeFragment(), "Notice")
        adapter.addFragment(ChatListFragment(), "Group")
        viewPager.adapter = adapter
    }

    private fun initDrawer() {
        headerView = nav_view.getHeaderView(0)
        val drawerName = headerView.findViewById<TextView>(R.id.drawer_name)
        drawerName.setText(R.string.not_login_name)
        val drawerImage = headerView.findViewById<ImageView>(R.id.drawer_image)
        drawerImage.setOnClickListener(View.OnClickListener {
            if (!isLogin) {
                drawer_layout.closeDrawer(GravityCompat.START)
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
        })
    }

    private fun setLocalUser() {

    }

    private fun initDatabase() {
        pref = PreferenceManager.getDefaultSharedPreferences(this)
        lastUsername = pref.getString("username", "local")
        manager = DatabaseManager.getInstance(this.applicationContext)
        var users = manager.queryAll(User::class.java)
        if (users.isEmpty()) {
            currentUser = User()
            currentUser.username = lastUsername
            manager.insert(currentUser)
        } else if(users.size == 1) {
            currentUser = manager.queryByWhere(User::class.java, "username", Array<String>(1){"local"})[0]
        } else {
            currentUser = manager.queryByWhere(User::class.java, "username", Array(1){lastUsername})[0]
        }

        manager.deleteAll(Deadline::class.java)
        manager.insert(Deadline(getNewCalendar(2018, 2, 11).timeInMillis,
                "组合数学作业",
                "第十三次",
                currentUser.username))
        manager.insert(Deadline(getNewCalendar(2017, 11, 10).timeInMillis,
                "图形学大作业",
                "Unity Project. Working with A, B, C, D, E and F. Be responsible for OBing.",
                currentUser.username))
        manager.insert(Deadline(getNewCalendar(2018, 1, 3).timeInMillis,
                "数据库大作业",
                "",
                currentUser.username))
        manager.insert(Deadline(getNewCalendar(2018, 1, 3).timeInMillis,
                "人工智能大作业",
                "Building neural network by C++ (Without using any existing package).",
                currentUser.username))
//        manager.insert(Deadline(getNewCalendar(2018, 1, 2).timeInMillis,
//                "3",
//                "Building neural network by C++ (Without using any existing package).",
//                currentUser.username))

        manager.deleteAll(Notice::class.java)
        manager.insert(Notice(getNewCalendar(2017, 10, 11).timeInMillis, "期中考通知", "组合数学", "时间：xxx\n地点：公教楼xxx课室", currentUser.username))
        manager.insert(Notice(getNewCalendar(2017, 12, 26).timeInMillis, "期末展示通知", "数据库系统原理", "1月3号下午在教室进行，请所有小组务必准备好展示用材料", currentUser.username))
        manager.insert(Notice(getNewCalendar(2017, 11, 14).timeInMillis, "大作业通知", "移动互联网编程实践", "五人一组\n作业要求见课程主页\n截止日期12.24", currentUser.username))
        manager.insert(Notice(getNewCalendar(2017, 12, 25).timeInMillis, "作业通知", "数值计算", "P535:1(b),2", currentUser.username))
        manager.insert(Notice(getNewCalendar(2017, 12, 21).timeInMillis, "作业通知", "组合数学与数论", "第十四次作业，12.28上课时交", currentUser.username))

        manager.deleteAll(Course::class.java)
        manager.insert(Course(1, "人工智能", currentUser.username, Calendar.getInstance().timeInMillis, "A", "你好", "大三第一学期"))
        manager.insert(Course(2, "数据库系统原理", currentUser.username, Calendar.getInstance().timeInMillis, "B", "新年快乐", "大三第一学期"))
        manager.insert(Course(3, "高性能计算", currentUser.username, Calendar.getInstance().timeInMillis, "C", "期末考什么时候", "大三第一学期"))
        manager.insert(Course(4, "计算机图形学", currentUser.username, Calendar.getInstance().timeInMillis, "D", "还行", "大三第一学期"))

        manager.deleteAll(Msg::class.java)
        manager.insert(Msg(Calendar.getInstance().timeInMillis, 1, currentUser.username, "C", "你好", Msg.LEFT));
        manager.insert(Msg(Calendar.getInstance().timeInMillis, 1, currentUser.username, "B", "你好", Msg.LEFT));
        manager.insert(Msg(Calendar.getInstance().timeInMillis, 1, currentUser.username, "A", "你好", Msg.LEFT));
        manager.insert(Msg(Calendar.getInstance().timeInMillis, 2, currentUser.username, "B", "你好", Msg.LEFT));
        manager.insert(Msg(Calendar.getInstance().timeInMillis, 3, currentUser.username, "C", "期末考什么时候", Msg.LEFT));
        manager.insert(Msg(Calendar.getInstance().timeInMillis, 4, currentUser.username, "D", "还行", Msg.LEFT));

//        manager.deleteAll(Course::class.java)
//        manager.insert(Course(1, "人工智能", "大三第一学期", currentUser.username))
//        manager.insert(Course(2, "数据库系统原理", "大三第一学期", currentUser.username))
//        manager.insert(Course(3, "高性能计算", "大三第一学期", currentUser.username))
//        manager.insert(Course(4, "计算机图形学", "大三第一学期", currentUser.username))
    }

    private fun initializeDatabase() {
//        LitePal.getDatabase()
//        var userList:List<User> = DataSupport.where("username == ?", "local").find(User::class.java)
//        if (userList.isEmpty()) {
//            var user = User()
//            user.username = "local"
//            currentUser = user
//        } else {
//            currentUser = userList[0]
//        }
//
//        DataSupport.deleteAll(Deadline::class.java)
//        DataSupport.deleteAll(FinishedDeadline::class.java)
//        Deadline(getNewCalendar(2018, 2, 11).timeInMillis,
//                "组合数学作业",
//                "第十三次",
//                currentUser.username).save()
//        Deadline(getNewCalendar(2017, 11, 10).timeInMillis,
//                "图形学大作业",
//                "Unity Project. Working with A, B, C, D, E and F. Be responsible for OBing.",
//                currentUser.username).save()
//        Deadline(getNewCalendar(2018, 1, 1).timeInMillis,
//                "数据库大作业",
//                "",
//                currentUser.username).save()
//        Deadline(getNewCalendar(2018, 1, 1).timeInMillis,
//                "人工智能大作业",
//                "Building neural network by C++ (Without using any existing package).",
//                currentUser.username).save()

//        DataSupport.deleteAll(Notice::class.java)
//        Notice(getNewCalendar(2017, 10, 11).timeInMillis, "期中考通知", "组合数学", "时间：xxx\n地点：公教楼xxx课室", currentUser).save()
//        Notice(getNewCalendar(2017, 12, 26).timeInMillis, "期末展示通知", "数据库系统原理", "1月3号下午在教室进行，请所有小组务必准备好展示用材料", currentUser).save()
//        Notice(getNewCalendar(2017, 11, 14).timeInMillis, "大作业通知", "移动互联网编程实践", "五人一组\n作业要求见课程主页\n截止日期12.24", currentUser).save()
//        Notice(getNewCalendar(2017, 12, 25).timeInMillis, "作业通知", "数值计算", "P535:1(b),2", currentUser).save()
//        Notice(getNewCalendar(2017, 12, 21).timeInMillis, "作业通知", "组合数学与数论", "第十四次作业，12.28上课时交", currentUser).save()
    }

    override fun getCurrentUserDeadline():User {
        return currentUser
    }

    override fun getCurrentUserNotice(): User {
        return currentUser
    }

    override fun getCurrentUserChat(): User {
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
            R.id.ic_action_share -> {
                shareScreenshot()
            }
            else -> return super.onOptionsItemSelected(item)
        }
        return super.onOptionsItemSelected(item)
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
                            val calendar1 = Calendar.getInstance()
                            val deadline = Deadline(calendar.timeInMillis, title.text.toString(), info.text.toString(), currentUser.username)
                            if (title.text.isEmpty()) {
                                Toast.makeText(this, "Title cannot be empty!", Toast.LENGTH_SHORT).show()
                            }
                            else if (CalendarComparator.INSTANCE.compare(calendar1, calendar) == 1)
                                Toast.makeText(this, "Cannot add a past deadline", Toast.LENGTH_SHORT).show()
                            else {
                                manager.insert(deadline)
                                fragment.onRefresh()
                                Toast.makeText(this, "New deadline added successfully", Toast.LENGTH_SHORT).show()
                            }

                        }
                    }
                })
                .show()
    }

    private fun shareScreenshot() {
        if (Build.VERSION.SDK_INT >= 23) {
            val REQUEST_CODE_CONTACT = 101
            val permissions = Array<String>(1) {Manifest.permission.WRITE_EXTERNAL_STORAGE}
            //验证是否许可权限
            for (str in permissions) {
                if (this.checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                    //申请权限
                    this.requestPermissions(permissions, REQUEST_CODE_CONTACT)
                    return
                }
            }
        }
        val fragmentManager = supportFragmentManager
        for (fragment in fragmentManager.fragments) {
            if (fragment != null && fragment is DeadlineFragment) {
                val bitmap = fragment.screenshot
                val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                val calendar = Calendar.getInstance()
                val fileName = calendar.get(Calendar.YEAR).toString() + '_'+
                        (calendar.get(Calendar.MONTH) + 1).toString() + '_'+
                        calendar.get(Calendar.DAY_OF_MONTH).toString() + '_'+
                        calendar.get(Calendar.HOUR_OF_DAY).toString() +
                                calendar.get(Calendar.MINUTE).toString() +
                                calendar.get(Calendar.SECOND).toString() + ".jpeg"
                val imageFile = File(path, fileName)
                val fileOutPutStream = FileOutputStream(imageFile)
                bitmap.compress(Bitmap.CompressFormat.PNG, 80, fileOutPutStream)
                fileOutPutStream.flush()
                fileOutPutStream.close()
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "image/jpeg"
                intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + imageFile.absolutePath))
                intent.putExtra(Intent.EXTRA_TEXT, "Share your deadlines")
                startActivity(Intent.createChooser(intent, "share"))
            }
        }
    }



    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_finished -> {
                drawer_layout.closeDrawer(GravityCompat.START)
                intent = Intent(this, FinishedDeadlineActivity::class.java)
                intent.putExtra("CurrentUserName", currentUser.username);
                startActivity(intent)
                // Handle the camera action
            }
            R.id.nav_gallery -> {
                drawer_layout.closeDrawers()
                intent = Intent(this, CourseActivity::class.java)
                intent.putExtra("CurrentUserName", currentUser.username)
                startActivity(intent)
            }
        }

        return true
    }

    override fun onRestart() {
        super.onRestart()
        val fragmentManager = supportFragmentManager
        for (fragment in fragmentManager.fragments) {
            if (fragment != null && fragment.isVisible && fragment is DeadlineFragment) {
                fragment.onRefresh()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val fragmentManager = supportFragmentManager
        for (fragment in fragmentManager.fragments) {
            if (fragment != null && fragment.isVisible && fragment is DeadlineFragment) {
                fragment.onRefresh()
            }
        }
        if (Net.isLogin) {
            headerView = nav_view.getHeaderView(0)
            val drawerName = headerView.findViewById<TextView>(R.id.drawer_name)
            drawerName.setText(Net.username)
        }
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

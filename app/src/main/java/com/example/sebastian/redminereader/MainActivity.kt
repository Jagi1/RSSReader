package com.example.sebastian.redminereader

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.support.v4.app.NotificationCompat
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.File
import java.io.InputStream
import java.net.URL
import java.time.LocalDateTime
import java.util.*


class MainActivity : AppCompatActivity() {
//    val oneHour = 3600000L
    val oneHour = 10000L
    lateinit var lv: ListView
//    lateinit var mNotificationHelper: NotificationHelper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        mNotificationHelper = NotificationHelper(this)
        lv = this.findViewById(R.id.lv)
        lv.setOnItemClickListener { parent, view, position, id ->
            val uri = Uri.parse(DataHolder.links.get(position))
            this@MainActivity.startActivity(Intent(Intent.ACTION_VIEW,uri))
        }
        lv.adapter = RssSearch().execute().get()
//        sendOnChannel("You have new task on redmine!",titles[0])
    }

//    fun sendOnChannel(title: String, message: String) {
//        val nb: NotificationCompat.Builder = mNotificationHelper.getChannelNotification(title,message)
//        mNotificationHelper.getManager().notify(1, nb.build())
//    }

    fun checkUpdates() {
        val alarmManager: AlarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this,AlertReceiver::class.java)
        intent.putExtra("lastTask",DataHolder.titles[0])
        val pendingIntent = PendingIntent.getBroadcast(this,1,intent,0)
        var l = Date().time
        if (l < Date().time) {
            l += oneHour // start at next 24 hour
        }
        alarmManager.setExact(AlarmManager.RTC_WAKEUP,5000,pendingIntent)
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,l,oneHour,pendingIntent)
    }

    inner class RssSearch: AsyncTask<Int, Int, ArrayAdapter<String>>() {
        override fun doInBackground(vararg params: Int?): ArrayAdapter<String> {
            val url = URL("http://www.benchmark.pl/rss/aktualnosci-pliki.xml")
//            val url = URL("http://212.182.24.105:3001/issues.atom?c%5B%5D=project&c%5B%5D=tracker&c%5B%5D=status&c%5B%5D=subject&f%5B%5D=status_id&f%5B%5D=assigned_to_id&key=9ff138778bdf549cd9a61e11171f1a06e2517f41&op%5Bassigned_to_id%5D=%3D&op%5Bstatus_id%5D=o&set_filter=1&sort=priority%3Adesc%2Cupdated_on%3Adesc&v%5Bassigned_to_id%5D%5B%5D=me&v%5Bstatus_id%5D%5B%5D=")
            val factory: XmlPullParserFactory = XmlPullParserFactory.newInstance()
            factory.isNamespaceAware = false
            val xpp: XmlPullParser = factory.newPullParser()
            xpp.setInput(getInputStream(url), "UTF_8")
            var insideItem = false
            var eventType = xpp.eventType
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    if (xpp.name.equals(other = "item", ignoreCase = true)) insideItem = true
                    else if (xpp.name.equals(other = "title", ignoreCase = true)) {
                        if (insideItem) DataHolder.titles.add(xpp.nextText())
                    }
                    else if (xpp.name.equals(other = "link", ignoreCase = true)) {
                        if (insideItem) DataHolder.links.add(xpp.nextText())
                    }
                }
                else if (eventType == XmlPullParser.END_TAG && xpp.name.equals(other = "item", ignoreCase = true)) insideItem = false
                eventType = xpp.next()
            }
//            checkUpdates()
            return ArrayAdapter(this@MainActivity, android.R.layout.simple_list_item_1,DataHolder.titles)
        }
    }
    fun getInputStream(url: URL): InputStream {
        return url.openConnection().getInputStream()
    }
}

package com.example.sebastian.redminereader

import android.R
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import android.preference.PreferenceManager
import android.support.v4.app.NotificationCompat
import android.widget.ArrayAdapter
import android.widget.Toast
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.URL

class AlertReceiver: BroadcastReceiver() {
    lateinit var mNotificationHelper: NotificationHelper
    lateinit var c: Context
    var lastTask: String? = null
    override fun onReceive(context: Context?, intent: Intent?) {
//        c = context!!
//        lastTask = intent?.getStringExtra("lastTask")
//        mNotificationHelper = NotificationHelper(context)
//        sendOnChannel("You have new task on redmine!","test123")
        RssSearch().execute()
    }
    fun sendOnChannel(title: String, message: String) {
        val nb: NotificationCompat.Builder = mNotificationHelper.getChannelNotification(title,message)
        mNotificationHelper.getManager().notify(1, nb.build())
    }
    inner class RssSearch: AsyncTask<Int, Int, Unit>() {
        var titles: ArrayList<String> = ArrayList()
        var links: ArrayList<String> = ArrayList()
        override fun doInBackground(vararg params: Int?) {
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
                        if (insideItem) titles.add(xpp.nextText())
                    }
                    else if (xpp.name.equals(other = "link", ignoreCase = true)) {
                        if (insideItem) links.add(xpp.nextText())
                    }
                }
                else if (eventType == XmlPullParser.END_TAG && xpp.name.equals(other = "item", ignoreCase = true)) insideItem = false
                eventType = xpp.next()
            }
//            if (titles[0] != (lastTask))
//                if (titles[0].indexOf("(Nowy)",0,false) != -1)
//                    sendOnChannel(titles[0],lastTask!!)
        }
    }
    fun getInputStream(url: URL): InputStream {
        return url.openConnection().getInputStream()
    }
}
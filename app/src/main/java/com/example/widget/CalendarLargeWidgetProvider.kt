package com.example.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.RemoteViews
import com.example.MainActivity
import com.example.R
import com.example.ui.components.getVietnamCalendar
import com.example.utils.EventUtils
import com.example.utils.LunarUtils
import com.example.utils.PhongThuyUtils
import java.util.Calendar

class CalendarLargeWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            updateWidget(context, appWidgetManager, appWidgetId)
        }
    }

    companion object {
        fun updateWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
            val cal = getVietnamCalendar()
            val sDay = cal.get(Calendar.DAY_OF_MONTH)
            val sMonth = cal.get(Calendar.MONTH) + 1
            val sYear = cal.get(Calendar.YEAR)
            val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)

            val dayOfWeekStr = when (dayOfWeek) {
                Calendar.SUNDAY -> "Chủ nhật"
                Calendar.MONDAY -> "Thứ hai"
                Calendar.TUESDAY -> "Thứ ba"
                Calendar.WEDNESDAY -> "Thứ tư"
                Calendar.THURSDAY -> "Thứ năm"
                Calendar.FRIDAY -> "Thứ sáu"
                Calendar.SATURDAY -> "Thứ bảy"
                else -> ""
            }

            val lunar = LunarUtils.convertSolar2Lunar(sDay, sMonth, sYear)
            val lDay = lunar[0]
            val lMonth = lunar[1]
            val lYear = lunar[2]
            val isLeap = lunar[3] == 1

            val canChiDay = LunarUtils.getCanChiDay(sDay, sMonth, sYear)
            val canChiMonth = LunarUtils.getCanChiMonth(lMonth, lYear)
            val isHoangDao = PhongThuyUtils.isHoangDao(canChiDay)
            val tietKhi = PhongThuyUtils.getTietKhi(sDay, sMonth)

            val upcomingEvents = EventUtils.getUpcomingEvents(60)

            val views = RemoteViews(context.packageName, R.layout.widget_calendar_large)
            views.setTextViewText(R.id.widget_large_day_of_week, "$dayOfWeekStr, $sDay thg $sMonth")
            views.setTextViewText(R.id.widget_large_solar_day, sDay.toString())
            views.setTextViewText(R.id.widget_large_solar_year, "Năm $sYear (Dương lịch)")

            val lunarMonthStr = if (isLeap) "$lMonth (Nhuận)" else "$lMonth"
            views.setTextViewText(R.id.widget_large_lunar_full, "Ngày $lDay thg $lunarMonthStr")
            views.setTextViewText(R.id.widget_large_can_chi, "$canChiDay - $canChiMonth")
            views.setTextViewText(
                R.id.widget_large_hoang_dao,
                if (isHoangDao) "★ Ngày Hoàng Đạo (Tốt)" else "• Ngày Hắc Đạo"
            )
            views.setTextViewText(R.id.widget_large_tiet_khi, "Tiết: $tietKhi")

            // Sự kiện 1
            if (upcomingEvents.isNotEmpty()) {
                val ev1 = upcomingEvents[0]
                val p1 = if (ev1.daysAway == 0) "Hôm nay: " else if (ev1.daysAway == 1) "Ngày mai: " else "[Còn ${ev1.daysAway} ngày] "
                views.setTextViewText(R.id.widget_large_event1, "★ $p1${ev1.title}")
                views.setViewVisibility(R.id.widget_large_event1, View.VISIBLE)
            } else {
                views.setTextViewText(R.id.widget_large_event1, "• Không có sự kiện cận kề")
            }

            // Sự kiện 2
            if (upcomingEvents.size > 1) {
                val ev2 = upcomingEvents[1]
                val p2 = if (ev2.daysAway == 1) "Ngày mai: " else "[Còn ${ev2.daysAway} ngày] "
                views.setTextViewText(R.id.widget_large_event2, "• $p2${ev2.title}")
                views.setViewVisibility(R.id.widget_large_event2, View.VISIBLE)
            } else {
                views.setViewVisibility(R.id.widget_large_event2, View.GONE)
            }

            // Sự kiện 3
            if (upcomingEvents.size > 2) {
                val ev3 = upcomingEvents[2]
                val p3 = "[Còn ${ev3.daysAway} ngày] "
                views.setTextViewText(R.id.widget_large_event3, "• $p3${ev3.title}")
                views.setViewVisibility(R.id.widget_large_event3, View.VISIBLE)
            } else {
                views.setViewVisibility(R.id.widget_large_event3, View.GONE)
            }

            // Click vào widget mở ứng dụng
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val pendingIntent = PendingIntent.getActivity(
                context,
                2,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_large_root, pendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}

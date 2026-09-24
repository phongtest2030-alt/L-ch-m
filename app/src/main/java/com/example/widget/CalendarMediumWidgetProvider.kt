package com.example.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.MainActivity
import com.example.R
import com.example.ui.components.getVietnamCalendar
import com.example.utils.EventUtils
import com.example.utils.LunarUtils
import com.example.utils.PhongThuyUtils
import java.util.Calendar

class CalendarMediumWidgetProvider : AppWidgetProvider() {

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

            val upcomingEvents = EventUtils.getUpcomingEvents(30)
            val nextEvent = upcomingEvents.firstOrNull()

            val views = RemoteViews(context.packageName, R.layout.widget_calendar_medium)
            views.setTextViewText(R.id.widget_med_day_of_week, dayOfWeekStr)
            views.setTextViewText(R.id.widget_med_solar_day, sDay.toString())
            views.setTextViewText(R.id.widget_med_solar_month_year, "Tháng $sMonth, $sYear")

            val lunarMonthStr = if (isLeap) "$lMonth (Nhuận)" else "$lMonth"
            views.setTextViewText(R.id.widget_med_lunar_day, "Âm lịch: Ngày $lDay tháng $lunarMonthStr")
            views.setTextViewText(R.id.widget_med_can_chi, "Ngày $canChiDay - $canChiMonth")
            views.setTextViewText(
                R.id.widget_med_hoang_dao,
                if (isHoangDao) "★ Ngày Hoàng Đạo (Tốt)" else "• Ngày Hắc Đạo"
            )

            if (nextEvent != null) {
                val prefix = when (nextEvent.daysAway) {
                    0 -> "Hôm nay: "
                    1 -> "Ngày mai: "
                    else -> "[Còn ${nextEvent.daysAway} ngày] "
                }
                views.setTextViewText(R.id.widget_med_event_title, "★ $prefix${nextEvent.title}")
            } else {
                views.setTextViewText(R.id.widget_med_event_title, "★ Không có sự kiện cận kề")
            }

            // Click vào widget mở ứng dụng
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val pendingIntent = PendingIntent.getActivity(
                context,
                1,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_medium_root, pendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}

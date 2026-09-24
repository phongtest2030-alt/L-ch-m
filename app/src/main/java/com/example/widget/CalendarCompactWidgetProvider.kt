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
import com.example.utils.LunarUtils
import java.util.Calendar

class CalendarCompactWidgetProvider : AppWidgetProvider() {

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
            val canChiDay = LunarUtils.getCanChiDay(sDay, sMonth, sYear)

            val views = RemoteViews(context.packageName, R.layout.widget_calendar_compact)
            views.setTextViewText(R.id.widget_day_of_week, dayOfWeekStr)
            views.setTextViewText(R.id.widget_solar_day, sDay.toString())
            views.setTextViewText(R.id.widget_solar_month_year, "Tháng $sMonth, $sYear")
            views.setTextViewText(R.id.widget_lunar_day_month, "Âm: $lDay/$lMonth")
            views.setTextViewText(R.id.widget_can_chi_day, canChiDay)

            // Bấm vào widget mở ứng dụng chính
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_compact_root, pendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}

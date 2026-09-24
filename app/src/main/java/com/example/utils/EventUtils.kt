package com.example.utils

import com.example.ui.components.getVietnamCalendar
import java.util.Calendar

data class UpcomingEvent(
    val title: String,
    val dateDisplay: String, // e.g., "Hôm nay", "Ngày mai", "28/09"
    val daysAway: Int, // 0 = Hôm nay, 1 = Ngày mai, ...
    val isLunar: Boolean
)

object EventUtils {

    /**
     * Lấy danh sách các ngày lễ / sự kiện / ngày rằm sắp tới trong vòng [daysAhead] ngày
     */
    fun getUpcomingEvents(daysAhead: Int = 30): List<UpcomingEvent> {
        val events = mutableListOf<UpcomingEvent>()
        val cal = getVietnamCalendar()

        for (i in 0..daysAhead) {
            val checkCal = (cal.clone() as Calendar).apply { add(Calendar.DAY_OF_MONTH, i) }
            val sDay = checkCal.get(Calendar.DAY_OF_MONTH)
            val sMonth = checkCal.get(Calendar.MONTH) + 1
            val sYear = checkCal.get(Calendar.YEAR)

            val lunar = LunarUtils.convertSolar2Lunar(sDay, sMonth, sYear)
            val lDay = lunar[0]
            val lMonth = lunar[1]

            val dateLabel = when (i) {
                0 -> "Hôm nay"
                1 -> "Ngày mai"
                else -> String.format("%02d/%02d", sDay, sMonth)
            }

            // Kiểm tra ngày mùng 1 hoặc ngày rằm âm lịch
            if (lDay == 1) {
                events.add(UpcomingEvent("Mùng 1 đầu tháng (${lDay}/${lMonth} ÂL)", dateLabel, i, true))
            } else if (lDay == 15) {
                events.add(UpcomingEvent("Ngày Rằm tháng ${lMonth} ÂL", dateLabel, i, true))
            }

            // Kiểm tra danh sách ngày lễ
            val holidays = HolidayUtils.getHolidays(sDay, sMonth, lDay, lMonth)
            holidays.forEach { h ->
                events.add(UpcomingEvent(h, dateLabel, i, h.contains("Âm lịch")))
            }
        }

        return events.sortedBy { it.daysAway }
    }
}

package com.example.utils

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.sin

object LunarUtils {
    // Math constants
    private const val PI2 = 2 * PI

    private fun INT(d: Double): Int = floor(d).toInt()

    fun jdFromDate(dd: Int, mm: Int, yy: Int): Int {
        var a = (14 - mm) / 12
        var y = yy + 4800 - a
        var m = mm + 12 * a - 3
        var jd = dd + (153 * m + 2) / 5 + 365 * y + y / 4 - y / 100 + y / 400 - 32045
        if (jd < 2299161) {
            jd = dd + (153 * m + 2) / 5 + 365 * y + y / 4 - 32083
        }
        return jd
    }

    fun jdToDate(jd: Int): IntArray {
        var a: Int
        var b: Int
        var c: Int
        var d: Int
        var e: Int
        var m: Int
        if (jd > 2299160) {
            a = jd + 32044
            b = (4 * a + 3) / 146097
            c = a - (146097 * b) / 4
            d = (4 * c + 3) / 1461
            e = c - (1461 * d) / 4
            m = (5 * e + 2) / 153
        } else {
            b = 0
            c = jd + 32082
            d = (4 * c + 3) / 1461
            e = c - (1461 * d) / 4
            m = (5 * e + 2) / 153
        }
        val day = e - (153 * m + 2) / 5 + 1
        val month = m + 3 - 12 * (m / 10)
        val year = b * 100 + d - 4800 + m / 10
        return intArrayOf(day, month, year)
    }

    private fun getSunLongitude(dayNumber: Double, timeZone: Double): Double {
        val t = (dayNumber - 2451545.0) / 36525.0
        val t2 = t * t
        val t3 = t2 * t
        val t4 = t3 * t
        var L0 = 280.46646 + 36000.76983 * t + 0.0003032 * t2
        var M = 357.52911 + 35999.05029 * t - 0.0001537 * t2 - t3 / 24490000.0
        var e = 0.016708634 - 0.000042037 * t - 0.0000001267 * t2
        var C = (1.914602 - 0.004817 * t - 0.000014 * t2) * sin(M * PI / 180)
        C += (0.019993 - 0.000101 * t) * sin(2 * M * PI / 180)
        C += 0.000289 * sin(3 * M * PI / 180)
        var sunLong = L0 + C
        return sunLong * PI / 180.0
    }

    private fun getNewMoonDay(k: Double, timeZone: Double): Int {
        val T = k / 1236.85
        val T2 = T * T
        val T3 = T2 * T
        val dr = PI / 180.0
        var Jd = 2415020.75933 + 29.53058868 * k + 0.0001178 * T2 - 0.000000155 * T3
        Jd += 0.00033 * sin((166.56 + 132.87 * T - 0.009173 * T2) * dr)
        var M = 359.2242 + 29.10535608 * k - 0.0000333 * T2 - 0.00000347 * T3
        var Mprime = 306.0253 + 385.81691806 * k + 0.0107306 * T2 + 0.00001236 * T3
        var F = 21.2964 + 390.67050646 * k - 0.0016528 * T2 - 0.00000239 * T3
        var C1 = (0.1734 - 0.000393 * T) * sin(M * dr) + 0.0021 * sin(2 * M * dr)
        C1 -= 0.0004 * sin(2 * F * dr) + 0.0005 * sin(Mprime * dr)
        var C2 = 0.0004 * sin((Mprime + M) * dr) + 0.0004 * sin((Mprime - M) * dr)
        var C3 = 0.0003 * sin((2 * F + M) * dr) - 0.0004 * sin((2 * F - M) * dr)
        var dJd = Jd + C1 + C2 + C3
        return INT(dJd + 0.5 + timeZone / 24.0)
    }

    private fun getSunLongitudeDay(dayNumber: Double, timeZone: Double): Int {
        var sunLong = getSunLongitude(dayNumber - timeZone / 24.0, timeZone)
        return INT(sunLong / (PI / 6.0))
    }
    
    private fun getLunarMonth11(yy: Int, timeZone: Double): Int {
        var off = jdFromDate(31, 12, yy) - 2415021.0
        var k = INT(off / 29.530588853)
        var nm = getNewMoonDay(k.toDouble(), timeZone)
        if (nm > jdFromDate(31, 12, yy)) {
            k--
            nm = getNewMoonDay(k.toDouble(), timeZone)
        }
        return nm
    }

    private fun getLeapMonthOffset(a11: Int, timeZone: Double): Int {
        var k: Double
        var last = 0
        var arc: Int
        var i = 1
        var leapMonth = 0
        var nm = a11
        var arc0 = getSunLongitudeDay(nm.toDouble(), timeZone)
        var isLeap = false
        var off = a11 - 2415021.0
        k = floor(off / 29.530588853)
        
        while (i < 13) {
            nm = getNewMoonDay(k + i, timeZone)
            arc = getSunLongitudeDay(nm.toDouble(), timeZone)
            if (arc == arc0 && !isLeap) {
                leapMonth = i
                isLeap = true
            }
            arc0 = arc
            i++
        }
        return leapMonth
    }

    fun convertSolar2Lunar(dd: Int, mm: Int, yy: Int, timeZone: Double = 7.0): IntArray {
        var dayNumber = jdFromDate(dd, mm, yy)
        var k = INT((dayNumber - 2415021.0) / 29.530588853)
        var monthStart = getNewMoonDay(k.toDouble(), timeZone)
        if (monthStart > dayNumber) {
            k--
            monthStart = getNewMoonDay(k.toDouble(), timeZone)
        }
        var a11 = getLunarMonth11(yy, timeZone)
        var b11 = a11
        if (a11 >= monthStart) {
            a11 = getLunarMonth11(yy - 1, timeZone)
        } else {
            b11 = getLunarMonth11(yy + 1, timeZone)
        }
        var day = dayNumber - monthStart + 1
        var diff = INT((monthStart - a11) / 29.0)
        var leapMonthDiff = getLeapMonthOffset(a11, timeZone)
        var month = diff + 11
        if (b11 - a11 > 365) {
            leapMonthDiff = getLeapMonthOffset(a11, timeZone)
            if (diff >= leapMonthDiff) {
                month = diff + 10
            }
        }
        var year = yy
        if (month > 12) {
            month -= 12
        }
        if (month >= 11 && diff < 4) {
            year -= 1
        }
        var leap = 0
        if (b11 - a11 > 365 && diff == leapMonthDiff) {
            leap = 1
        }
        return intArrayOf(day, month, year, leap)
    }

    fun convertLunar2Solar(lunarDay: Int, lunarMonth: Int, lunarYear: Int, lunarLeap: Int, timeZone: Double = 7.0): IntArray {
        var a11 = getLunarMonth11(lunarYear, timeZone)
        var b11 = getLunarMonth11(lunarYear - 1, timeZone)
        var c11 = getLunarMonth11(lunarYear + 1, timeZone)
        var off = a11
        if (lunarMonth < 11) {
            off = a11
        } else {
            off = b11
        }
        
        var leapOff = getLeapMonthOffset(off, timeZone)
        var diff = lunarMonth - 11
        if (diff < 0) diff += 12
        if (c11 - a11 > 365 && lunarMonth >= 11) leapOff = getLeapMonthOffset(a11, timeZone)
        if (c11 - a11 > 365 && lunarMonth >= 11 && diff >= leapOff) diff += 1
        if (a11 - b11 > 365 && lunarMonth < 11) leapOff = getLeapMonthOffset(b11, timeZone)
        if (a11 - b11 > 365 && lunarMonth < 11 && diff >= leapOff) diff += 1
        if (lunarLeap == 1) diff += 1
        
        var k = INT((off - 2415021.0) / 29.530588853)
        var monthStart = getNewMoonDay((k + diff).toDouble(), timeZone)
        return jdToDate(monthStart + lunarDay - 1)
    }

    // Can Chi definitions
    val CAN = arrayOf("Giáp", "Ất", "Bính", "Đinh", "Mậu", "Kỷ", "Canh", "Tân", "Nhâm", "Quý")
    val CHI = arrayOf("Tý", "Sửu", "Dần", "Mão", "Thìn", "Tỵ", "Ngọ", "Mùi", "Thân", "Dậu", "Tuất", "Hợi")
    
    fun getCanChiYear(lunarYear: Int): String {
        return "${CAN[(lunarYear + 6) % 10]} ${CHI[(lunarYear + 8) % 12]}"
    }
    
    fun getCanChiMonth(lunarMonth: Int, lunarYear: Int): String {
        val canNam = (lunarYear + 6) % 10
        val canThang = (canNam * 2 + lunarMonth) % 10
        val chiThang = (lunarMonth + 1) % 12
        return "${CAN[canThang]} ${CHI[chiThang]}"
    }

    fun getCanChiDay(dd: Int, mm: Int, yy: Int): String {
        val jd = jdFromDate(dd, mm, yy)
        return "${CAN[(jd + 9) % 10]} ${CHI[(jd + 1) % 12]}"
    }

    fun getCanChiHour(jd: Int, hour: Int): String {
        val chiGio = ((hour + 1) % 24) / 2
        val canGio = ((jd * 2) % 10 + chiGio) % 10
        return "${CAN[canGio]} ${CHI[chiGio]}"
    }
}

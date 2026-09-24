package com.example.utils

object HolidayUtils {
    
    // Key is DD/MM
    val SOLAR_HOLIDAYS = mapOf(
        "01/01" to "Tết Dương lịch",
        "14/02" to "Lễ tình nhân (Valentine)",
        "27/02" to "Ngày Thầy thuốc Việt Nam",
        "08/03" to "Quốc tế Phụ nữ",
        "26/03" to "Thành lập Đoàn TNCS HCM",
        "30/04" to "Giải phóng miền Nam",
        "01/05" to "Quốc tế Lao động",
        "07/05" to "Chiến thắng Điện Biên Phủ",
        "19/05" to "Sinh nhật Bác Hồ",
        "01/06" to "Quốc tế Thiếu nhi",
        "28/06" to "Ngày Gia đình Việt Nam",
        "27/07" to "Thương binh Liệt sĩ",
        "19/08" to "Cách mạng tháng Tám",
        "02/09" to "Quốc khánh",
        "20/10" to "Phụ nữ Việt Nam",
        "20/11" to "Nhà giáo Việt Nam",
        "22/12" to "Thành lập QĐND Việt Nam",
        "24/12" to "Lễ Giáng sinh",
        "25/12" to "Lễ Giáng sinh"
    )

    // Key is DD/MM (Lunar)
    val LUNAR_HOLIDAYS = mapOf(
        "01/01" to "Tết Nguyên Đán",
        "02/01" to "Tết Nguyên Đán (Mùng 2)",
        "03/01" to "Tết Nguyên Đán (Mùng 3)",
        "15/01" to "Tết Nguyên Tiêu (Rằm tháng Giêng)",
        "03/03" to "Tết Hàn Thực",
        "10/03" to "Giỗ Tổ Hùng Vương",
        "15/04" to "Lễ Phật Đản",
        "05/05" to "Tết Đoan Ngọ",
        "15/07" to "Lễ Vu Lan",
        "15/08" to "Tết Trung Thu",
        "09/09" to "Tết Trùng Cửu",
        "10/10" to "Tết Thường Tân",
        "15/10" to "Tết Hạ Nguyên",
        "23/12" to "Ông Táo chầu trời",
        "29/12" to "Tất Niên",
        "30/12" to "Tất Niên" // Assuming it handles 30 vs 29 days
    )

    fun getHolidays(solarDay: Int, solarMonth: Int, lunarDay: Int, lunarMonth: Int): List<String> {
        val holidays = mutableListOf<String>()
        
        val solarKey = String.format("%02d/%02d", solarDay, solarMonth)
        SOLAR_HOLIDAYS[solarKey]?.let { holidays.add(it) }

        val lunarKey = String.format("%02d/%02d", lunarDay, lunarMonth)
        LUNAR_HOLIDAYS[lunarKey]?.let { holidays.add(it + " (Âm lịch)") }

        return holidays
    }
}

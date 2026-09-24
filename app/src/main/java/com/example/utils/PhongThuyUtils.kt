package com.example.utils

object PhongThuyUtils {
    // Ngũ hành của các Can
    private val NGU_HANH_CAN = mapOf(
        "Giáp" to 1, "Ất" to 1,
        "Bính" to 2, "Đinh" to 2,
        "Mậu" to 3, "Kỷ" to 3,
        "Canh" to 4, "Tân" to 4,
        "Nhâm" to 5, "Quý" to 5
    )

    // Ngũ hành của các Chi
    private val NGU_HANH_CHI = mapOf(
        "Tý" to 0, "Sửu" to 0, "Ngọ" to 0, "Mùi" to 0,
        "Dần" to 1, "Mão" to 1, "Thân" to 1, "Dậu" to 1,
        "Thìn" to 2, "Tỵ" to 2, "Tuất" to 2, "Hợi" to 2
    )

    private val NGU_HANH = arrayOf("Kim", "Thủy", "Hỏa", "Thổ", "Mộc")

    private fun extractCan(input: String): String {
        val parts = input.trim().split("\\s+".toRegex())
        return parts.firstOrNull() ?: ""
    }

    private fun extractChi(input: String): String {
        val parts = input.trim().split("\\s+".toRegex())
        return if (parts.size > 1) parts[1] else parts.firstOrNull() ?: ""
    }

    fun getNguHanh(canOrCanChi: String, chiOptional: String = ""): String {
        val can = extractCan(canOrCanChi)
        val chi = if (chiOptional.isNotEmpty()) extractChi(chiOptional) else extractChi(canOrCanChi)
        val valCan = NGU_HANH_CAN[can] ?: 1
        val valChi = NGU_HANH_CHI[chi] ?: 0
        var res = valCan + valChi
        if (res > 5) res -= 5
        val index = (res - 1).coerceIn(0, NGU_HANH.size - 1)
        return NGU_HANH[index]
    }

    private val TRUC = arrayOf("Kiến", "Trừ", "Mãn", "Bình", "Định", "Chấp", "Phá", "Nguy", "Thành", "Thu", "Khai", "Bế")

    fun getTruc(lunarMonth: Int, canChiDay: String): String {
        val chi = extractChi(canChiDay)
        val chiIndex = LunarUtils.CHI.indexOf(chi)
        if (chiIndex == -1) return "Kiến"
        val offset = (chiIndex - lunarMonth + 14) % 12
        return TRUC[offset]
    }

    fun isHoangDao(canChiDay: String): Boolean {
        val chi = extractChi(canChiDay)
        val goodChi = listOf("Tý", "Sửu", "Thìn", "Tỵ", "Mùi", "Tuất")
        return goodChi.contains(chi)
    }

    fun getGioHoangDao(canChiDay: String): String {
        val chi = extractChi(canChiDay)
        return when (chi) {
            "Tý", "Ngọ" -> "Tý (23-1), Sửu (1-3), Mão (5-7), Ngọ (11-13), Thân (15-17), Dậu (17-19)"
            "Sửu", "Mùi" -> "Dần (3-5), Mão (5-7), Tỵ (9-11), Thân (15-17), Tuất (19-21), Hợi (21-23)"
            "Dần", "Thân" -> "Tý (23-1), Sửu (1-3), Thìn (7-9), Tỵ (9-11), Mùi (13-15), Tuất (19-21)"
            "Mão", "Dậu" -> "Tý (23-1), Dần (3-5), Mão (5-7), Ngọ (11-13), Mùi (13-15), Dậu (17-19)"
            "Thìn", "Tuất" -> "Dần (3-5), Thìn (7-9), Tỵ (9-11), Thân (15-17), Dậu (17-19), Hợi (21-23)"
            "Tỵ", "Hợi" -> "Sửu (1-3), Thìn (7-9), Ngọ (11-13), Mùi (13-15), Tuất (19-21), Hợi (21-23)"
            else -> "Tý (23-1), Sửu (1-3), Mão (5-7), Ngọ (11-13)"
        }
    }

    fun getTietKhi(day: Int, month: Int): String {
        return when (month) {
            1 -> if (day < 20) "Tiểu Hàn" else "Đại Hàn"
            2 -> if (day < 19) "Lập Xuân" else "Vũ Thủy"
            3 -> if (day < 21) "Kinh Trập" else "Xuân Phân"
            4 -> if (day < 20) "Thanh Minh" else "Cốc Vũ"
            5 -> if (day < 21) "Lập Hạ" else "Tiểu Mãn"
            6 -> if (day < 21) "Mang Chủng" else "Hạ Chí"
            7 -> if (day < 23) "Tiểu Thử" else "Đại Thử"
            8 -> if (day < 23) "Lập Thu" else "Xử Thử"
            9 -> if (day < 23) "Bạch Lộ" else "Thu Phân"
            10 -> if (day < 23) "Hàn Lộ" else "Sương Giáng"
            11 -> if (day < 22) "Lập Đông" else "Tiểu Tuyết"
            12 -> if (day < 22) "Đại Tuyết" else "Đông Chí"
            else -> "Không rõ"
        }
    }

    fun getHuongXuatHanh(canDay: String): String {
        val can = extractCan(canDay)
        return when (can) {
            "Giáp", "Ất" -> "Hỷ thần: Tây Bắc - Tài thần: Đông Nam"
            "Bính", "Đinh" -> "Hỷ thần: Tây Nam - Tài thần: Chính Đông"
            "Mậu", "Kỷ" -> "Hỷ thần: Đông Nam - Tài thần: Chính Bắc"
            "Canh", "Tân" -> "Hỷ thần: Đông Bắc - Tài thần: Tây Nam"
            "Nhâm", "Quý" -> "Hỷ thần: Chính Nam - Tài thần: Chính Tây"
            else -> "Hỷ thần: Chính Nam - Tài thần: Chính Tây"
        }
    }
}

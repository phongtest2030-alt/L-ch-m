package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.ui.components.getVietnamCalendar
import com.example.utils.EventUtils
import com.example.utils.LunarUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Lịch Âm Dương", appName)
  }

  @Test
  fun `vietnam calendar timezone should be GMT+7`() {
    val cal = getVietnamCalendar()
    assertNotNull(cal)
    assertEquals("Asia/Ho_Chi_Minh", cal.timeZone.id)
  }

  @Test
  fun `lunar conversion calculates correctly`() {
    val lunar = LunarUtils.convertSolar2Lunar(23, 9, 2026)
    assertNotNull(lunar)
    assertEquals(4, lunar.size)
  }

  @Test
  fun `event utils returns upcoming events`() {
    val events = EventUtils.getUpcomingEvents(60)
    assertNotNull(events)
    assertTrue(events.isNotEmpty())
    val first = events[0]
    assertNotNull(first.title)
  }
}

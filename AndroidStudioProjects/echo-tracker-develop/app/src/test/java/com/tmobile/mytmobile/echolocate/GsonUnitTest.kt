package com.tmobile.mytmobile.echolocate

import com.google.gson.Gson
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class GsonUnitTest {

    private lateinit var gson: Gson
    @Before
    fun createGson() {
        gson = Gson()
    }

    @Test
    fun testObjectToString() {
        val jsonString = gson.toJson(TestModel(1, "Test"))
        Assert.assertEquals(jsonString, """{"id":1,"description":"Test"}""")
    }

    @Test
    fun testStringToObject() {
        val jsonString = """{"id":1,"description":"Test"}"""
        val testModel = gson.fromJson(jsonString, TestModel::class.java)
        Assert.assertEquals(testModel.id, 1)
        Assert.assertEquals(testModel.description, "Test")
    }
}

data class TestModel(
        val id: Int,
        val description: String
)
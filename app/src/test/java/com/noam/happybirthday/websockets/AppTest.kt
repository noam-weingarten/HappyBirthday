package com.noam.happybirthday.websockets

import io.ktor.client.request.get
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import junit.framework.TestCase.assertEquals
import org.junit.Test

class AppTest {
    @Test
    fun testRoot() = testApplication {
        application {
            module()
        }
        val response = client.get("/nanit") {
            setBody("HappyBirthday")
        }
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("Hello, world!", response.bodyAsText())
    }

    @Test
    fun testUnKnown() = testApplication {
        application {
            module()
        }
        val response = client.get("/nanit") {
            setBody("Happy Birthday")
        }
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("Do I know you?", response.bodyAsText())
    }
}
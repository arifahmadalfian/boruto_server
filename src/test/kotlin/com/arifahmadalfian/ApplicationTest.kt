package com.arifahmadalfian

import com.arifahmadalfian.data.models.ApiResponse
import com.arifahmadalfian.data.repository.IHeroRepository
import com.arifahmadalfian.data.utils.Constants.NEXT_PAGE_KEY
import com.arifahmadalfian.data.utils.Constants.PREV_PAGE_KEY
import io.ktor.http.*
import kotlin.test.*
import io.ktor.server.testing.*
import io.ktor.application.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.koin.java.KoinJavaComponent.inject

class ApplicationTest {

    private val repository: IHeroRepository by inject(IHeroRepository::class.java)

    @Test
    fun `access root endpoint, assert correct information`() {
        withTestApplication(moduleFunction = Application::module) {
            handleRequest(HttpMethod.Get, "/").apply {
                assertEquals(
                    expected = HttpStatusCode.OK,
                    actual = response.status()
                )
                assertEquals(
                    expected = "Welcome to Boruto api",
                    actual = response.content
                )
            }
        }
    }

    @Test
    fun `access all heroes endpoint, assert correct information`() {
        withTestApplication(moduleFunction = Application::module) {
            handleRequest(HttpMethod.Get, "boruto/heroes").apply {
                assertEquals(
                    expected = HttpStatusCode.OK,
                    actual = response.status()
                )
                val expected = ApiResponse(
                    success = true,
                    message = "Ok",
                    prevPage = null,
                    nextPage = 2,
                    heroes = repository.page1
                )
                val actual = Json.decodeFromString<ApiResponse>(response.content.toString())
                println("EXPECTED : $expected")
                println("ACTUAL : $actual")
                assertEquals(
                    expected = expected,
                    actual = actual
                )
            }
        }
    }

    @Test
    fun `access all heroes endpoint, query second, assert correct information`() {
        withTestApplication(moduleFunction = Application::module) {
            handleRequest(HttpMethod.Get, "boruto/heroes?page=2").apply {
                assertEquals(
                    expected = HttpStatusCode.OK,
                    actual = response.status()
                )
                val expected = ApiResponse(
                    success = true,
                    message = "Ok",
                    prevPage = 1,
                    nextPage = 3,
                    heroes = repository.page2
                )
                val actual = Json.decodeFromString<ApiResponse>(response.content.toString())
                println("EXPECTED : $expected")
                println("ACTUAL : $actual")
                assertEquals(
                    expected = expected,
                    actual = actual
                )
            }
        }
    }

    @Test
    fun `access all heroes endpoint, query all pages, assert correct information`() {
        withTestApplication(moduleFunction = Application::module) {
            val pages = 1..5
            val heroes = listOf(
                repository.page1,
                repository.page2,
                repository.page3,
                repository.page4,
                repository.page5
            )
            pages.forEach { page ->
                handleRequest(HttpMethod.Get, "/boruto/heroes?page=$page").apply {
                    assertEquals(
                        expected = HttpStatusCode.OK,
                        actual = response.status()
                    )
                    val actual = Json.decodeFromString<ApiResponse>(response.content.toString())
                    val expected = ApiResponse(
                        success = true,
                        message = "Ok",
                        prevPage = calculatePage(page = page)[PREV_PAGE_KEY],
                        nextPage = calculatePage(page = page)[NEXT_PAGE_KEY],
                        heroes = heroes[page - 1],
                        lastUpdated = actual.lastUpdated
                    )
                    assertEquals(
                        expected = expected,
                        actual = actual
                    )
                }
            }
        }
    }

    @Test
    fun `access all heroes endpoint, query non existing page number, assert error`() {
        withTestApplication(moduleFunction = Application::module) {
            handleRequest(HttpMethod.Get, "/boruto/heroes?page=6").apply {
                assertEquals(
                    expected = HttpStatusCode.NotFound,
                    actual = response.status()
                )
                val expected = ApiResponse(
                    success = false,
                    message = "Not found"
                )
                val actual = Json.decodeFromString<ApiResponse>(response.content.toString())
                assertEquals(
                    expected = expected,
                    actual = actual
                )
            }
        }
    }

    @Test
    fun `access all heroes endpoint, query invalid page number, assert error`() {
        withTestApplication(moduleFunction = Application::module) {
            handleRequest(HttpMethod.Get, "/boruto/heroes?page=invalid").apply {
                assertEquals(
                    expected = HttpStatusCode.BadRequest,
                    actual = response.status()
                )
                val expected = ApiResponse(
                    success = false,
                    message = "Number only"
                )
                val actual = Json.decodeFromString<ApiResponse>(response.content.toString())
                assertEquals(
                    expected = expected,
                    actual = actual
                )
            }
        }
    }

    @Test
    fun `access search heroes endpoint, query hero name, assert single hero result`() {
        withTestApplication(moduleFunction = Application::module) {
            handleRequest(HttpMethod.Get, "/boruto/heroes/search?name=sas").apply {
                assertEquals(expected = HttpStatusCode.OK, actual = response.status())
                val actual = Json.decodeFromString<ApiResponse>(response.content.toString())
                    .heroes.size
                assertEquals(expected = 1, actual = actual)
            }
        }
    }

    @Test
    fun `access search heroes endpoint, query hero name, assert multiple heroes result`() {
        withTestApplication(moduleFunction = Application::module) {
            handleRequest(HttpMethod.Get, "/boruto/heroes/search?name=sa").apply {
                assertEquals(expected = HttpStatusCode.OK, actual = response.status())
                val actual = Json.decodeFromString<ApiResponse>(response.content.toString())
                    .heroes.size
                assertEquals(expected = 3, actual = actual)
            }
        }
    }

    @Test
    fun `access search heroes endpoint, query an empty text, assert empty list as a result`() {
        withTestApplication(moduleFunction = Application::module) {
            handleRequest(HttpMethod.Get, "/boruto/heroes/search?name=").apply {
                assertEquals(expected = HttpStatusCode.OK, actual = response.status())
                val actual = Json.decodeFromString<ApiResponse>(response.content.toString())
                    .heroes
                assertEquals(expected = emptyList(), actual = actual)
            }
        }
    }

    @Test
    fun `access search heroes endpoint, query non existing hero, assert empty list as a result`() {
        withTestApplication(moduleFunction = Application::module) {
            handleRequest(HttpMethod.Get, "/boruto/heroes/search?name=unknown").apply {
                assertEquals(expected = HttpStatusCode.OK, actual = response.status())
                val actual = Json.decodeFromString<ApiResponse>(response.content.toString())
                    .heroes
                assertEquals(expected = emptyList(), actual = actual)
            }
        }
    }

    @Test
    fun `access non existing endpoint,assert not found`() {
        withTestApplication(moduleFunction = Application::module) {
            handleRequest(HttpMethod.Get, "/unknown").apply {
                assertEquals(expected = HttpStatusCode.NotFound, actual = response.status())
                assertEquals(expected = "Page not Found.", actual = response.content)
            }
        }
    }

    private fun calculatePage(page: Int): Map<String, Int?> {
        var prevPage: Int? = page
        var nextPage: Int? = page
        if (page in 1..4) {
            nextPage = nextPage?.plus(1)
        }
        if (page in 2..5) {
            prevPage = prevPage?.minus(1)
        }
        if (page == 1) {
            prevPage = null
        }
        if (page == 5) {
            nextPage = null
        }
        return mapOf(PREV_PAGE_KEY to prevPage, NEXT_PAGE_KEY to nextPage)
    }
}
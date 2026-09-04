package com.measton.dogapp

import com.measton.dogapp.network.DogApiClient
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.Url
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class DogApiClientTest {

    /**
     * Wraps [engine] in a client configured the same way NetworkModule configures the real
     * one, so these tests exercise the serialization behaviour the app actually ships.
     */
    private fun dogApiWith(engine: HttpClientEngine) = DogApiClient(
        HttpClient(engine) {
            expectSuccess = true
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
            defaultRequest { url("https://api.thedogapi.com/v1/") }
        }
    )

    @Test
    fun `getBreed parses a full breed response into BreedResponse`() = runTest {
        val mockEngine = MockEngine {
            respond(
                content = DOG_EXAMPLE_JSON,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }
        val apiClient = dogApiWith(mockEngine)

        val breed = apiClient.getBreed("1")

        assertEquals("1", breed.id)
        assertEquals("Affenpinscher", breed.name)
        assertEquals("Toy", breed.breedGroup)
        assertEquals("12-15", breed.lifeSpan)
        assertEquals("0LJiOVlxp", breed.imageId)
        assertEquals("3.2-4.5", breed.weight?.metric)
        assertEquals("23-29", breed.height?.metric)
        assertNull(breed.bredFor)
    }

    @Test
    fun `getBreed, when the json contains fields we do not model, ignores them`() = runTest {
        val mockEngine = MockEngine {
            respond(
                content = DOG_EXAMPLE_JSON,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }
        val apiClient = dogApiWith(mockEngine)

        val breed = apiClient.getBreed("1")

        assertEquals("Affenpinscher", breed.name)
    }

    @Test
    fun `fetchImages sends limit, breed_id and has_breeds as query parameters`() = runTest {
        var capturedUrl: Url? = null
        val mockEngine = MockEngine { request ->
            capturedUrl = request.url
            respond(
                content = "[]",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }
        val apiClient = dogApiWith(mockEngine)

        apiClient.fetchImages(limit = 5, breedId = "hound")

        val url = assertNotNull(capturedUrl)
        assertEquals("/v1/images/search", url.encodedPath)
        assertEquals("5", url.parameters["limit"])
        assertEquals("hound", url.parameters["breed_id"])
        // hasBreeds defaults to true in DogApiClient - prove the default reaches the wire.
        assertEquals("true", url.parameters["has_breeds"])
    }
}

private val DOG_EXAMPLE_JSON = """
    {
      "id": "1",
      "name": "Affenpinscher",
      "species_id": "2",
      "life_span": "12-15",
      "temperament": "Confident, alert, playful, loyal, courageous",
      "origin": "Germany",
      "country_codes": "DE",
      "country_code": "DE",
      "description": "Small, sturdy toy breed with a monkey-like expression.",
      "bred_for": null,
      "perfect_for": null,
      "breed_group": "Toy",
      "history": "Originating in 17th-century Germany, bred down from larger terriers.",
      "reference_image_id": "0LJiOVlxp",
      "weight": { "imperial": "7-10", "metric": "3.2-4.5" },
      "height": { "imperial": "9-11.5", "metric": "23-29" },
      "image": {
        "id": "0LJiOVlxp",
        "url": "https://cdn2.thedogapi.com/images/0LJiOVlxp.jpg",
        "width": 2048,
        "height": 1880
      }
    }
""".trimIndent()

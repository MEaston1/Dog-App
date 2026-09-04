package com.measton.dogapp

import com.measton.dogapp.domain.Breed
import com.measton.dogapp.domain.DogImage
import com.measton.dogapp.network.DogApiClient
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandler
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

class HomeRepositoryTest {

    private fun repositoryWith(handler: MockRequestHandler) = HomeRepository(
        DogApiClient(
            HttpClient(MockEngine(handler)) {
                expectSuccess = true
                install(ContentNegotiation) {
                    json(Json { ignoreUnknownKeys = true })
                }
                defaultRequest { url("https://api.thedogapi.com/v1/") }
            }
        )
    )

    private fun jsonHandler(body: String): MockRequestHandler = {
        respond(
            content = body,
            status = HttpStatusCode.OK,
            headers = headersOf(HttpHeaders.ContentType, "application/json"),
        )
    }

    @Test
    fun `getRandomDogImage maps a successful response into the domain model`() = runTest {
        val repository = repositoryWith(jsonHandler(IMAGE_SEARCH_JSON))

        val result = repository.getRandomDogImage()

        val dog = assertIs<ApiResult.Success<DogImage>>(result).data
        assertEquals("0XYvRd7oD", dog.id)
        assertEquals("https://cdn2.thedogapi.com/images/0XYvRd7oD.jpg", dog.url)
        // snake_case JSON reaches the camelCase domain model.
        assertEquals("Affenpinscher", dog.breeds.single().name)
        assertEquals("12-15", dog.breeds.single().lifeSpan)
    }

    @Test
    fun `getBreedDetails falls back rather than nulling out absent optional fields`() = runTest {
        val repository = repositoryWith(jsonHandler(BREED_MISSING_FIELDS_JSON))

        val result = repository.getBreedDetails("1")

        val breed = assertIs<ApiResult.Success<Breed>>(result).data
        assertEquals("Affenpinscher", breed.name)
        assertEquals("Unknown", breed.lifeSpan)
        assertEquals("", breed.temperament)
        assertEquals("", breed.bredFor)
    }

    @Test
    fun `malformed json becomes an Error rather than throwing`() = runTest {
        val repository = repositoryWith(jsonHandler("{ this is not json"))

        val result = repository.getRandomDogImage()

        assertIs<ApiResult.Error>(result)
    }

    @Test
    fun `an empty image search becomes an Error rather than crashing`() = runTest {
        val repository = repositoryWith(jsonHandler("[]"))

        val result = repository.getRandomDogImage()

        val error = assertIs<ApiResult.Error>(result)
        assertTrue(error.exception is NoSuchElementException)
    }

    @Test
    fun `searchImages maps every returned image`() = runTest {
        val repository = repositoryWith(jsonHandler(IMAGE_SEARCH_JSON))

        val result = repository.searchImages(limit = 1, breedId = "1")

        assertEquals(1, assertIs<ApiResult.Success<List<DogImage>>>(result).data.size)
    }

    @Test
    fun `cancellation propagates instead of being captured as an Error`() = runTest {
        val requestReached = CompletableDeferred<Unit>()
        val repository = repositoryWith {
            requestReached.complete(Unit)
            awaitCancellation()
        }
        var result: ApiResult<DogImage>? = null

        val job = launch { result = repository.getRandomDogImage() }
        requestReached.await()
        job.cancelAndJoin()

        // Non-null here would mean apiCall caught the cancellation and returned Error.
        assertNull(result)
    }
}

private val IMAGE_SEARCH_JSON = """
    [
      {
        "id": "0XYvRd7oD",
        "url": "https://cdn2.thedogapi.com/images/0XYvRd7oD.jpg",
        "width": 1200,
        "height": 800,
        "breeds": [
          {
            "id": "1",
            "name": "Affenpinscher",
            "life_span": "12-15",
            "breed_group": "Toy",
            "bred_for": "Small rodent hunting, lapdog",
            "temperament": "Stubborn, Curious, Playful",
            "reference_image_id": "0LJiOVlxp",
            "weight": { "imperial": "7-10", "metric": "3.2-4.5" },
            "height": { "imperial": "9-11.5", "metric": "23-29" }
          }
        ]
      }
    ]
""".trimIndent()

private val BREED_MISSING_FIELDS_JSON = """
    {
      "id": "1",
      "name": "Affenpinscher",
      "breed_group": "Toy"
    }
""".trimIndent()

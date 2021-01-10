package pt.isel.pdm.drag.words.repo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.volley.NetworkResponse
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonRequest
import com.fasterxml.jackson.databind.ObjectMapper


/**
 * The application's repository holding globally relevant information.
 *
 * Implementation note: Currently the information is always fetched from the remote Web API, but it
 * could be cached (with an expiration date) for future use.
 */
class WordsRepository(
        private val queue: RequestQueue,
        private val mapper: ObjectMapper) {

    /**
     * Asynchronous operation for fetching words
     *
     * @return the result promise
     *
     * Design note: using promise style (LiveData is a life cycle aware observable promise)
     */
    fun fetchWords(numOfWords: Int): LiveData<Result<Array<Word>>?> {
        val result = MutableLiveData<Result<Array<Word>>?>()
        var words = Array(30) { Word("DEFAULT VALUE")}
        for (i in 0..numOfWords) {
            val request = GetWordRequest(
                "$BASE_URL$RANDOM_WORD_PATH",
                mapper,
                { words[i] = Result.success(modelFromDTO(it)).getOrThrow() },
                { result.value = Result.failure(it) }
            )
            queue.add(request)
        }
        return result
    }

}



/**
 * Implementation of the request for obtaining the word
 */
class GetWordRequest(
    url: String,
    private val mapper: ObjectMapper,
    success: Response.Listener<WordDTO>,
    error: Response.ErrorListener
) : JsonRequest<WordDTO>(Method.GET, url, "", success, error) {

    override fun parseNetworkResponse(response: NetworkResponse): Response<WordDTO> {
        val currenciesDto = mapper.readValue(String(response.data), WordDTO::class.java)
        return Response.success(currenciesDto, null)
    }
}
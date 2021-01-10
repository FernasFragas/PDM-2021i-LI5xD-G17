package pt.isel.pdm.drag.words

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import pt.isel.pdm.drag.words.repo.Word

//TODO: provavelmente por este viewmodel no view model da activity que for buscar as palavras

const val NUMBER_OF_WORDS_TO_FETCH = 30

/**
 * View model for the [MainActivityV2]
 */
class ViewModel(application: Application) : AndroidViewModel(application) {

    enum class State {
        IDLE,
        IN_PROGRESS
    }

    val word: MutableLiveData<String> = MutableLiveData()

    val wordInfo: LiveData<Result<Array<Word>>?> = Transformations.switchMap(word) {

        if (state.value == State.IDLE) {
            (state as MutableLiveData<State>).value = State.IN_PROGRESS
            getApplication<WordApplication>().repository.fetchWords(NUMBER_OF_WORDS_TO_FETCH)
        }
        else throw IllegalStateException()
    }

    /**
     * The current state of the view model
     */
    val state: LiveData<State> = MutableLiveData(State.IDLE)

    /**
     * Sets the view model state to [State.IDLE].
     */
    fun setToIdle() {
        (state as MutableLiveData<State>).value = State.IDLE
    }
}
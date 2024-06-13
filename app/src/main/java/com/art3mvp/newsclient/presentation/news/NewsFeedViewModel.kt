package com.art3mvp.newsclient.presentation.news

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.art3mvp.newsclient.data.mapper.NewsFeedMapper

import com.art3mvp.newsclient.data.network.ApiFactory
import com.art3mvp.newsclient.domain.FeedPost
import com.art3mvp.newsclient.domain.StatisticItem
import com.vk.api.sdk.VKPreferencesKeyValueStorage
import com.vk.api.sdk.auth.VKAccessToken
import kotlinx.coroutines.launch

class NewsFeedViewModel(application: Application) : AndroidViewModel(application) {


    init {
        loadRecommendations()
    }

    private val initialState = NewsFeedScreenState.Initial

    private val _screenState = MutableLiveData<NewsFeedScreenState>(initialState)

    val screenState: LiveData<NewsFeedScreenState> = _screenState

    private val mapper = NewsFeedMapper()

    private fun loadRecommendations() {
        viewModelScope.launch {
            val storage = VKPreferencesKeyValueStorage(getApplication())
            val token = VKAccessToken.restore(storage) ?: return@launch
            val response =ApiFactory.apiService.loadRecommendation(token.accessToken)
            val feedPosts = mapper.mapResponseToPosts(response)
            _screenState.value = NewsFeedScreenState.Posts(feedPosts)
        }
    }


    fun updateCount(feedPost: FeedPost, newItem: StatisticItem) {

        val currentState = screenState.value
        if (currentState !is NewsFeedScreenState.Posts) return

        val oldPosts = currentState.posts.toMutableList()

        val newStatistics = feedPost.statistics.map { oldItem ->
            if (oldItem.type == newItem.type) {
                oldItem.copy(count = oldItem.count + 1)
            } else {
                oldItem
            }
        }

        val newPosts = oldPosts.apply {
            replaceAll {
                if (feedPost.id == it.id) {
                    feedPost.copy(statistics = newStatistics)
                } else {
                    it
                }
            }
        }

        _screenState.value = NewsFeedScreenState.Posts(newPosts)
    }

    fun removeFeedPost(feedPost: FeedPost) {

        val currentState = screenState.value
        if (currentState !is NewsFeedScreenState.Posts) return

        val modifierList = currentState.posts.toMutableList()
        modifierList.remove(feedPost)
        _screenState.value = NewsFeedScreenState.Posts(modifierList)
    }

}
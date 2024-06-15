package com.art3mvp.newsclient.data.mapper

import com.art3mvp.newsclient.data.model.CommentsResponseDto
import com.art3mvp.newsclient.data.model.NewsFeedResponseDto
import com.art3mvp.newsclient.domain.FeedPost
import com.art3mvp.newsclient.domain.PostComment
import com.art3mvp.newsclient.domain.StatisticItem
import com.art3mvp.newsclient.domain.StatisticType
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.absoluteValue


class MainMapper {

    fun mapResponseToPosts(responseDto: NewsFeedResponseDto): List<FeedPost> {
        val result = mutableListOf<FeedPost>()

        val posts = responseDto.newsFeedContent.posts
        val groups = responseDto.newsFeedContent.groups

        for (post in posts) {

            groups.find { it.id == post.sourceId.absoluteValue }?.let {group ->
                val feedPost = FeedPost(
                    id = post.id,
                    communityId = post.sourceId,
                    communityName = group.name,
                    publicationDate = mapTimeStampToDate(post.date),
                    contentDescription = post.text,
                    communityImageUrl = group.groupPhoto,
                    contentImageUrl = post.attachments?.firstOrNull()?.photo?.urls?.lastOrNull()?.url,
                    statistics = listOf(
                        StatisticItem(StatisticType.LIKES, post.likes.count),
                        StatisticItem(StatisticType.COMMENTS, post.comments.count),
                        StatisticItem(StatisticType.SHARES, post.reposts.count),
                        StatisticItem(StatisticType.VIEWS, post.views.count),
                        ),
                    isLiked = post.likes.isLikedByUser > 0
                    )
                result.add(feedPost)
            }
        }
        return result
    }

    fun mapResponseToPostComments(response: CommentsResponseDto): List<PostComment> {
        val commentsList = mutableListOf<PostComment>()


        response.commentsContainer.commentsList.forEach { commentDto ->

            val user = response.commentsContainer.profilesList.find { it.id == commentDto.fromId}
            if (user != null && commentDto.text.isNotBlank()) {
                val comment = PostComment(
                    id = commentDto.id,
                    authorId = commentDto.fromId,
                    avatarUrl = user.avatarUrl,
                    authorName = user.userName,
                    commentText = commentDto.text,
                    publicationDate = mapTimeStampToDate(commentDto.date)
                )
                commentsList.add(comment)
            }
        }
        return commentsList
    }

    private fun mapTimeStampToDate(timeStamp: Long): String {
        val date = Date(timeStamp * 1000)
        return SimpleDateFormat("dd MMMM yyyy, hh:mm", Locale.getDefault()).format(date)
    }
}
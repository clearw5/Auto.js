package org.autojs.autojs.network

import org.autojs.autojs.network.api.TopicApi
import org.autojs.autojs.network.entity.topic.Post
import org.autojs.autojs.network.entity.topic.Topic

object TopicService {

    private const val CID_SCRIPTS = 9L
    private val mRetrofit = NodeBB.getInstance().retrofit
    private val mTopicApi = mRetrofit.create(TopicApi::class.java)

    suspend fun getTopics(cid: Long): List<Topic> {
        val category = mTopicApi.getCategory(cid).await()
        return category.topics.filter {
            it.appInfo != null
        }
    }

    suspend fun getMainPost(topic: Topic): Post {
        val fullTopic = mTopicApi.getTopic(topic.tid.toLong()).await()
        topic.mainPost = fullTopic.posts.first { post -> post.pid == topic.mainPid }
        return topic.mainPost
    }

    suspend fun getScriptsTopics(): List<Topic> {
        return getTopics(CID_SCRIPTS)
    }

}
package org.autojs.autojs.network.api

import kotlinx.coroutines.Deferred
import org.autojs.autojs.network.entity.topic.Category
import org.autojs.autojs.network.entity.topic.Post
import org.autojs.autojs.network.entity.topic.Topic
import retrofit2.http.GET
import retrofit2.http.Path

interface TopicApi {

    @GET("/api/category/{cid}")
    fun getCategory(@Path("cid") cid: Long): Deferred<Category>

    @GET("/api/topic/{tid}")
    fun getTopic(@Path("tid") pid: Long): Deferred<Topic>


}
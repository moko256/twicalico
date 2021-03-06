/*
 * Copyright 2015-2019 The twitlatte authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.moko256.twitlatte.entity

import com.github.moko256.latte.client.base.ApiClient
import com.github.moko256.latte.client.base.MediaUrlConverter
import com.github.moko256.latte.client.base.entity.AccessToken
import com.github.moko256.latte.client.base.entity.Friendship
import com.github.moko256.latte.client.twitter.CLIENT_TYPE_TWITTER
import com.github.moko256.twitlatte.cacheMap.PostCache
import com.github.moko256.twitlatte.cacheMap.StatusCacheMap
import com.github.moko256.twitlatte.cacheMap.UserCacheMap
import com.github.moko256.twitlatte.collections.LruCache

/**
 * Created by moko256 on 2018/11/28.
 *
 * @author moko256
 */
data class Client(
        val accessToken: AccessToken,
        val apiClient: ApiClient,
        val mediaUrlConverter: MediaUrlConverter,
        val statusCache: StatusCacheMap,
        val userCache: UserCacheMap,
        val friendshipCache: LruCache<Long, Friendship>
) {
    val postCache: PostCache = PostCache(statusCache, userCache)

    val statusLimit: Int = if (accessToken.clientType == CLIENT_TYPE_TWITTER) 200 else 40

}
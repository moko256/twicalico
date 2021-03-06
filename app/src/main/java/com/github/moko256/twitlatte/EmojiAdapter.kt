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

package com.github.moko256.twitlatte

import android.content.Context
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.github.moko256.latte.client.base.entity.Emoji
import com.github.moko256.twitlatte.view.dpToPx

/**
 * Created by moko256 on 2018/12/11.
 *
 * @author moko256
 */
class EmojiAdapter(
        private val list: List<Emoji>,
        private val context: Context,
        private val glideRequests: RequestManager,
        private val onEmojiClick: (Emoji) -> Unit,
        private val onLoadClick: () -> Unit
) : RecyclerView.Adapter<EmojiViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmojiViewHolder {
        val imageView = ImageView(context)
        val dp32 = context.dpToPx(32)
        imageView.layoutParams = ViewGroup.LayoutParams(dp32, dp32)
        return EmojiViewHolder(
                imageView = imageView,
                glideRequests = glideRequests
        )
    }

    override fun getItemCount(): Int {
        return list.size + 1
    }

    override fun onBindViewHolder(holder: EmojiViewHolder, position: Int) {
        if (position != list.size) {
            val emoji = list[position]
            holder.setImage(emoji.url)
            holder.itemView.setOnClickListener {
                onEmojiClick(emoji)
            }
        } else {
            holder.setImage(R.drawable.list_add_icon)
            holder.itemView.setOnClickListener {
                onLoadClick()
            }
        }
    }

    override fun onViewRecycled(holder: EmojiViewHolder) {
        holder.clearImage()
    }
}

class EmojiViewHolder(private val imageView: ImageView, private val glideRequests: RequestManager) : RecyclerView.ViewHolder(imageView) {
    fun setImage(url: String) {
        glideRequests.load(url).into(imageView)
    }

    fun setImage(@DrawableRes resId: Int) {
        glideRequests.load(resId).into(imageView)
    }

    fun clearImage() {
        glideRequests.clear(imageView)
    }
}
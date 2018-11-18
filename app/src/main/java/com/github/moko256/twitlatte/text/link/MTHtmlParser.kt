/*
 * Copyright 2015-2018 The twitlatte authors
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

package com.github.moko256.twitlatte.text.link

import com.github.moko256.twitlatte.text.link.entity.Link
import org.ccil.cowan.tagsoup.Parser
import org.xml.sax.Attributes
import org.xml.sax.InputSource
import org.xml.sax.helpers.DefaultHandler

/**
 * Created by moko256 on 2018/02/09.
 *
 * @author moko256
 */

private val handler = MastodonHtmlHandler()
private val parser = Parser().apply {
    contentHandler = handler
}

fun String.convertHtmlToContentAndLinks(): Pair<String, Array<Link>> = try {
    parser.parse(InputSource(reader()))

    handler.stringBuilder.toString() to handler.linkList.toTypedArray()
} catch (e: Throwable) {
    e.printStackTrace()
    this to emptyArray()
}

private const val TYPE_URL = 0
private const val TYPE_OTHER = 1

private class MastodonHtmlHandler: DefaultHandler() {
    lateinit var stringBuilder: StringBuilder
    val linkList = ArrayList<Link>(6)

    private var noBr = false

    private var type = 0

    private var isDisplayable = true
    private var isNextDots = false

    private lateinit var contentUrl : String

    private var linkStart : Int = -1

    override fun startDocument() {
        stringBuilder = StringBuilder(500)
        noBr = false
        isDisplayable = true
        linkList.clear()
    }

    override fun startElement(uri: String, localName: String, qName: String, attributes: Attributes) {
        when (localName){
            "a" -> {
                val classValue: String? = attributes.getValue("class")
                val linkHref = attributes.getValue("href")?:""
                when {
                    classValue?.contains("hashtag")?:false -> {
                        type = TYPE_OTHER
                        val tag = linkHref.substringAfterLast("/")
                        contentUrl = "twitlatte://tag/$tag"
                    }
                    classValue?.contains("mention")?:false -> {
                        type = TYPE_OTHER
                        val list = linkHref.split("/")
                        val name = list[list.size - 1].replaceFirst("@", "")
                        val domain = list[list.size - 2]
                        contentUrl = "twitlatte://user/$name@$domain"
                    }
                    else -> {
                        type = TYPE_URL
                        contentUrl = linkHref
                    }
                }
                linkStart = stringBuilder.length
            }
            "span" -> {
                isDisplayable = attributes.getValue("class") != "invisible"
            }
            "p" -> {
                if (noBr){
                    stringBuilder.append("\n\n")
                } else {
                    noBr = true
                }
            }
            "br" -> {
                stringBuilder.append("\n")
            }
        }
    }

    override fun characters(ch: CharArray, start: Int, length: Int) {
        if (type == TYPE_URL) {
            if (isDisplayable) {
                isNextDots = if (isNextDots) {
                    stringBuilder.append("…")
                    false
                } else {
                    stringBuilder.append(ch, start, length)
                    true
                }
            }
        } else {
            stringBuilder.append(ch, start, length)
        }
    }

    override fun endElement(uri: String, localName: String, qName: String) {
        if (localName == "a") {
            isNextDots = false

            this.linkList.add(Link(contentUrl, linkStart, stringBuilder.length))
        }
    }
}
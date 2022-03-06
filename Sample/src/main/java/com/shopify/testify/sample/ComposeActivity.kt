/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2021 Shopify Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.shopify.testify.sample

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.DrawableRes
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.shopify.testify.sample.clients.MockClientData
import com.shopify.testify.sample.ui.theme.TestifyTheme
import com.skydoves.landscapist.glide.GlideImage

class ComposeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        composableContent {
            Column {
                TopAppBar()
                LazyColumn {
                    MockClientData.CLIENTS.forEach {
                        item {
                            ClientListItem(
                                name = it.name,
                                avatar = LocalContext.current.resources.getIdentifier(
                                    it.avatar,
                                    "drawable",
                                    LocalContext.current.packageName
                                ),
                                since = it.date
                            )
                        }
                    }
                }
            }
        }
    }

    private fun composableContent(content: @Composable () -> Unit) {
        setContent {
            TestifyTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    content()
                }
            }
        }
    }
}

@Composable
fun TopAppBar() {
    TopAppBar(
        title = { Text(text = "Compose Sample") },
        contentColor = Color.White,
        navigationIcon = {
            IconButton(onClick = { }) {
                Icon(
                    painterResource(id = R.drawable.ic_back_black_24dp),
                    contentDescription = null
                )
            }
        },
    )
}

@Composable
fun ClientListItem(name: String, @DrawableRes avatar: Int, since: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { },
        elevation = 10.dp
    ) {
        Row(
            modifier = Modifier.padding(15.dp),
        ) {
            Avatar(avatar)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 15.dp)
            ) {
                Text(text = name)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Client since $since", color = Color.Gray)
            }
        }
    }
}

@Composable
fun Avatar(@DrawableRes avatar: Int) {
    GlideImage(
        imageModel = avatar,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(64.dp)
            .clip(CircleShape)
            .border(2.dp, Color.Gray, CircleShape),
        requestListener =
        object : RequestListener<Drawable> {
            init {
                Log.d("JETTE", "init ${this.hashCode()}")
                registerIdlingRegistry()
            }

            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable>?,
                isFirstResource: Boolean
            ): Boolean {
                Log.d("JETTE", "onLoadFailed ${this.hashCode()}")
                releaseIdlingRegistry()
                return false
            }

            override fun onResourceReady(
                resource: Drawable?,
                model: Any?,
                target: Target<Drawable>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                Log.d("JETTE", "onResourceReady ${this.hashCode()}")
                releaseIdlingRegistry()
                return false
            }
        }
    )
}

@Composable
fun ImageDemo(onImageLoaded: () -> Unit) {
    GlideImage(
        imageModel = R.drawable.avatar1,
        requestListener = object : RequestListener<Drawable> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable>?,
                isFirstResource: Boolean
            ): Boolean {
                return false
            }

            override fun onResourceReady(
                resource: Drawable?,
                model: Any?,
                target: Target<Drawable>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                onImageLoaded()
                return false
            }
        },
        modifier = Modifier.size(42.dp)
    )
}

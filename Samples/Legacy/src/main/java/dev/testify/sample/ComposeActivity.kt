/*
 * The MIT License (MIT)
 *
 * Modified work copyright (c) 2022 ndtp
 * Original work copyright (c) 2021 Shopify Inc.
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
package dev.testify.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import dev.testify.sample.clients.MockClientData
import dev.testify.sample.ui.theme.TestifyTheme

class ComposeActivity : ComponentActivity() {

    companion object {
        const val EXTRA_DROPDOWN = "extra_dropdown"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val expand = if (intent?.hasExtra(EXTRA_DROPDOWN) == true) {
            intent.getBooleanExtra(EXTRA_DROPDOWN, false)
        } else {
            false
        }
        composableContent {
            Column {
                TopAppBar()
                DropdownDemo(expand)
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
                    painterResource(id = android.R.drawable.arrow_down_float),
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
            Image(
                painter = painterResource(id = avatar),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color.Gray, CircleShape)
            )
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

/**
 * Modified from https://foso.github.io/Jetpack-Compose-Playground/material/dropdownmenu/
 */

@Composable
fun DropdownDemo(expand: Boolean) {
    var expanded by remember { mutableStateOf(expand) }
    val items = listOf("Selection 1", "Selection 2", "Selection 3")
    var selectedIndex: Int? by remember { mutableStateOf(null) }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentSize(Alignment.TopStart)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .clickable(onClick = { expanded = true })
                .border(width = 1.dp, color = Color.Black)
                .padding(horizontal = 4.dp, vertical = 2.dp)
        ) {
            Text(
                selectedIndex?.let { items[it] } ?: "Placeholder Text",
                modifier = Modifier.weight(1f)
            )
            Icon(
                modifier = Modifier.align(CenterVertically),
                painter = painterResource(R.drawable.ic_drop_down),
                contentDescription = "Drop down"
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            items.forEachIndexed { index, s ->
                DropdownMenuItem(onClick = {
                    selectedIndex = index
                    expanded = false
                }) {
                    Text(text = s)
                }
            }
        }
    }
}

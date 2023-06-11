package dev.testify.sample.module

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp


@Composable
fun TopAppBar() {
    androidx.compose.material.TopAppBar(
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
                modifier = Modifier.align(Alignment.CenterVertically),
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

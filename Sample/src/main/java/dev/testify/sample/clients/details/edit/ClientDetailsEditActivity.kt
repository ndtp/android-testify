/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2023 ndtp
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
package dev.testify.sample.clients.details.edit

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import dev.testify.sample.R
import dev.testify.sample.clients.MockClientData

class ClientDetailsEditActivity : AppCompatActivity() {

    private lateinit var view: ClientDetailsEditView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_edit_client_details)
        view = findViewById(R.id.view_root)

        val client = intent?.getSerializableExtra(EXTRA_CLIENT_ITEM) as? MockClientData.Client
        if (client != null) {
            supportActionBar?.title = getString(R.string.edit_name, client.name)
            view.render(client.toViewState())
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_edit_client, menu)
        return true
    }

    private fun MockClientData.Client.toViewState(): ClientDetailsEditViewState {
        return ClientDetailsEditViewState(
            name = this.name,
            avatar = resources.getIdentifier(this.avatar, "drawable", packageName),
            heading = getString(R.string.client_since, this.date),
            address = getString(R.string.mock_address),
            phoneNumber = getString(R.string.mock_phone)
        )
    }

    companion object {
        private const val EXTRA_CLIENT_ITEM = "client_item"

        fun Activity.startClientDetailsEditActivity(item: MockClientData.Client) {
            startActivity(createClientDetailsEditActivityIntent(this, item))
        }

        fun createClientDetailsEditActivityIntent(context: Context, item: MockClientData.Client): Intent {
            return Intent(context, ClientDetailsEditActivity::class.java).apply {
                putExtra(EXTRA_CLIENT_ITEM, item)
            }
        }
    }
}

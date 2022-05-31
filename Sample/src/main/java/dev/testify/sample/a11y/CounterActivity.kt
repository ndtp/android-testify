package dev.testify.sample.a11y

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import dev.testify.sample.R

class CounterActivity : AppCompatActivity() {

    private var count: Int = 0

    private fun updateCount(newValue: Int) {
        count = newValue
        findViewById<TextView>(R.id.countTV).text = count.toString()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        @LayoutRes val layoutId = intent.getIntExtra(EXTRA_LAYOUT, R.layout.activity_counter)
        setContentView(layoutId)

        updateCount(0)

        findViewById<View>(R.id.add_button).setOnClickListener {
            updateCount(count + 1)
        }

        findViewById<View>(R.id.subtract_button).setOnClickListener {
            updateCount(count - 1)
        }
    }

    companion object {
        const val EXTRA_LAYOUT = "extra_layout"
    }
}

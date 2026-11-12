package com.hetibor.bttextcast

import android.app.Activity
import android.media.MediaMetadata
import android.media.session.MediaSession
import android.media.session.PlaybackState
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView

class MainActivity : Activity() {
    private lateinit var mediaSession: MediaSession
    private lateinit var titleInput: EditText
    private lateinit var titlePreview: TextView
    private lateinit var castingSwitch: Switch
    private lateinit var statusText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mediaSession = MediaSession(this, "BTTextcastSession")
        mediaSession.setCallback(object : MediaSession.Callback() {})

        val padding = (24 * resources.displayMetrics.density).toInt()
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(padding, padding, padding, padding)
        }

        val heading = TextView(this).apply {
            text = "BT Textcast"
            textSize = 26f
        }
        layout.addView(heading, matchWidthWrapHeight())

        val prompt = TextView(this).apply {
            text = "Text to show on your Bluetooth device"
            textSize = 16f
            setPadding(0, padding, 0, 8)
        }
        layout.addView(prompt, matchWidthWrapHeight())

        titleInput = EditText(this).apply {
            hint = "Type a message"
            singleLine = true
            imeOptions = EditorInfo.IME_ACTION_DONE
            setText(savedInstanceState?.getString(KEY_TITLE).orEmpty())
        }
        layout.addView(titleInput, matchWidthWrapHeight())

        val previewLabel = TextView(this).apply {
            text = "Current text"
            textSize = 14f
            setPadding(0, padding, 0, 4)
        }
        layout.addView(previewLabel, matchWidthWrapHeight())

        titlePreview = TextView(this).apply {
            textSize = 20f
            text = titleInput.text.toString().ifBlank { "(empty)" }
        }
        layout.addView(titlePreview, matchWidthWrapHeight())

        castingSwitch = Switch(this).apply {
            text = "Casting"
            setPadding(0, padding, 0, 0)
        }
        layout.addView(castingSwitch, matchWidthWrapHeight())

        statusText = TextView(this).apply {
            textSize = 14f
            setPadding(0, 8, 0, 0)
        }
        layout.addView(statusText, matchWidthWrapHeight())
        setContentView(layout)

        titleInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val title = s?.toString().orEmpty()
                titlePreview.text = title.ifBlank { "(empty)" }
                if (castingSwitch.isChecked) publishTitle(title)
            }

            override fun afterTextChanged(s: Editable?) = Unit
        })

        castingSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                publishTitle(titleInput.text.toString())
                statusText.text = "Casting is on. The title is being advertised as now-playing metadata."
            } else {
                mediaSession.setPlaybackState(
                    PlaybackState.Builder()
                        .setState(PlaybackState.STATE_STOPPED, 0L, 0f)
                        .build()
                )
                mediaSession.isActive = false
                mediaSession.setMetadata(null)
                statusText.text = "Casting is off."
            }
        }
        statusText.text = "Casting is off."
    }

    private fun publishTitle(title: String) {
        mediaSession.setMetadata(
            MediaMetadata.Builder()
                .putString(MediaMetadata.METADATA_KEY_TITLE, title)
                .build()
        )
        mediaSession.setPlaybackState(
            PlaybackState.Builder()
                .setActions(PlaybackState.ACTION_PLAY or PlaybackState.ACTION_PAUSE)
                .setState(PlaybackState.STATE_PLAYING, 0L, 0f)
                .build()
        )
        mediaSession.isActive = true
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(KEY_TITLE, titleInput.text.toString())
        super.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        mediaSession.release()
        super.onDestroy()
    }

    private fun matchWidthWrapHeight() = LinearLayout.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    )

    private companion object {
        const val KEY_TITLE = "title"
    }
}

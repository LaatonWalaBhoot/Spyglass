/*
* Copyright 2015 LinkedIn Corp. All rights reserved.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*/
package com.linkedin.android.spyglass.mentions

import android.os.Parcel
import android.os.Parcelable
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View
import com.linkedin.android.spyglass.mentions.Mentionable.MentionDisplayMode
import com.linkedin.android.spyglass.ui.MentionsEditText

/**
 * Class representing a spannable [Mentionable] in an [EditText]. This class is
 * specifically used by the [MentionsEditText].
 */
class MentionSpan : ClickableSpan, Parcelable {
    @JvmField
    val mention: Mentionable
    private var config: MentionSpanConfig

    @JvmField
    var isSelected: Boolean = false
    var displayMode: MentionDisplayMode = MentionDisplayMode.FULL

    constructor(mention: Mentionable) : super() {
        this.mention = mention
        this.config = MentionSpanConfig.Builder().build()
    }

    constructor(mention: Mentionable, config: MentionSpanConfig) : super() {
        this.mention = mention
        this.config = config
    }

    override fun onClick(widget: View) {
        if (widget !is MentionsEditText) {
            return
        }

        // Get reference to the MentionsEditText
        val editText = widget
        val text = editText.text ?: return

        // Set cursor behind span in EditText
        val newCursorPos = text.getSpanEnd(this)
        editText.setSelection(newCursorPos)

        // If we are going to select this span, deselect all others
        val isSelected = isSelected
        if (!isSelected) {
            editText.deselectAllSpans()
        }

        // Toggle whether the view is selected
        this.isSelected = !this.isSelected

        // Update the span (forces it to redraw)
        editText.updateSpan(this)
    }

    override fun updateDrawState(tp: TextPaint) {
        if (isSelected) {
            tp.color = config.SELECTED_TEXT_COLOR
            tp.bgColor = config.SELECTED_TEXT_BACKGROUND_COLOR
        } else {
            tp.color = config.NORMAL_TEXT_COLOR
            tp.bgColor = config.NORMAL_TEXT_BACKGROUND_COLOR
        }
        tp.isUnderlineText = false
    }

    val displayString: String
        get() = mention.getTextForDisplayMode(displayMode)

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(config.NORMAL_TEXT_COLOR)
        dest.writeInt(config.NORMAL_TEXT_BACKGROUND_COLOR)
        dest.writeInt(config.SELECTED_TEXT_COLOR)
        dest.writeInt(config.SELECTED_TEXT_BACKGROUND_COLOR)

        dest.writeInt(displayMode.ordinal)
        dest.writeInt(if (isSelected) 1 else 0)
        dest.writeParcelable(mention, flags)
    }

    constructor(`in`: Parcel) {
        val normalTextColor = `in`.readInt()
        val normalTextBackgroundColor = `in`.readInt()
        val selectedTextColor = `in`.readInt()
        val selectedTextBackgroundColor = `in`.readInt()
        config = MentionSpanConfig(
            normalTextColor, normalTextBackgroundColor,
            selectedTextColor, selectedTextBackgroundColor
        )

        displayMode = MentionDisplayMode.entries[`in`.readInt()]
        isSelected = `in`.readInt() == 1
        mention = `in`.readParcelable(Mentionable::class.java.classLoader)!!
    }

    companion object {
        val CREATOR
                : Parcelable.Creator<MentionSpan> = object : Parcelable.Creator<MentionSpan> {
            override fun createFromParcel(`in`: Parcel): MentionSpan {
                return MentionSpan(`in`)
            }

            override fun newArray(size: Int): Array<MentionSpan> {
                return arrayOfNulls(size)
            }
        }
    }
}

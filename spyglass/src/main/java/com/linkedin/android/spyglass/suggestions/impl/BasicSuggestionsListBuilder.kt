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
package com.linkedin.android.spyglass.suggestions.impl

import android.R
import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.linkedin.android.spyglass.suggestions.SuggestionsResult
import com.linkedin.android.spyglass.suggestions.interfaces.Suggestible
import com.linkedin.android.spyglass.suggestions.interfaces.SuggestionsListBuilder

/**
 * Basic implementation of the [SuggestionsListBuilder] interface.
 */
open class BasicSuggestionsListBuilder : SuggestionsListBuilder {
    /**
     * {@inheritDoc}
     */
    override fun buildSuggestions(
        latestResults: Map<String, SuggestionsResult>,
        currentTokenString: String
    ): List<Suggestible> {
        val results: MutableList<Suggestible> = ArrayList()
        for ((_, result) in latestResults) {
            if (currentTokenString.equals(result.queryToken.tokenString, ignoreCase = true)) {
                results.addAll(result.suggestions)
            }
        }
        return results
    }

    /**
     * {@inheritDoc}
     */
    override fun getView(
        suggestion: Suggestible,
        convertView: View?,
        parent: ViewGroup?,
        context: Context,
        inflater: LayoutInflater,
        resources: Resources
    ): View {
        val view =
            convertView ?: inflater.inflate(R.layout.simple_list_item_1, parent, false)

        if (view is TextView) {
            val text = view
            text.text = suggestion.suggestiblePrimaryText
            text.setTextColor(Color.BLACK)
            text.setBackgroundColor(Color.WHITE)
        }

        return view
    }
}

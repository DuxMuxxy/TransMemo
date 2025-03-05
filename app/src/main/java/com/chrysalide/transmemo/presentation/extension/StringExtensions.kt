package com.chrysalide.transmemo.presentation.extension

import android.text.Spanned
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.core.text.isDigitsOnly
import java.math.RoundingMode

fun String.isValidIntegerValue() = isNotBlank() && isDigitsOnly()

fun String.isValidDecimalValue() = isNotBlank() && matches("^[0-9]+(\\.[0-9]+)?$".toRegex())

fun Float.stripTrailingZeros(): String = toBigDecimal().setScale(2, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString()

@Composable
fun Spanned.convertHtmlSpannedToAnnotatedString(): AnnotatedString =
    buildAnnotatedString {
        val text = this@convertHtmlSpannedToAnnotatedString
        val spans = text.getSpans(0, text.length, Any::class.java)
        append(text.toString())
        spans.forEach { span ->
            val start = text.getSpanStart(span)
            val end = text.getSpanEnd(span)
            when (span) {
                is android.text.style.StyleSpan -> {
                    when (span.style) {
                        android.graphics.Typeface.BOLD -> {
                            addStyle(SpanStyle(fontWeight = FontWeight.Bold), start, end)
                        }

                        android.graphics.Typeface.ITALIC -> {
                            addStyle(SpanStyle(fontStyle = FontStyle.Italic), start, end)
                        }

                        android.graphics.Typeface.BOLD_ITALIC -> {
                            addStyle(SpanStyle(fontWeight = FontWeight.Bold, fontStyle = FontStyle.Italic), start, end)
                        }
                    }
                }

                is android.text.style.UnderlineSpan -> {
                    addStyle(SpanStyle(textDecoration = TextDecoration.Underline), start, end)
                }

                is android.text.style.ParagraphStyle -> {
                    // Handle paragraph styles if needed
                }
            }
        }
    }

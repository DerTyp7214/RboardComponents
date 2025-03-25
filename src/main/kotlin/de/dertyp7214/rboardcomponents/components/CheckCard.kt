package de.dertyp7214.rboardcomponents.components

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.google.android.material.card.MaterialCardView
import de.dertyp7214.rboardcomponents.R
import de.dertyp7214.rboardcomponents.core.dpToPxRounded
import de.dertyp7214.rboardcomponents.core.getAttr

class CheckCard(context: Context, attrs: AttributeSet? = null) : LinearLayout(context, attrs) {

    constructor(context: Context, attrs: AttributeSet? = null, block: CheckCard.() -> Unit) : this(
        context,
        attrs
    ) {
        block(this)
    }

    private val strokeWidth = 5.dpToPxRounded(context)
    private val iconTintSelected =
        ColorStateList.valueOf(context.getAttr(androidx.appcompat.R.attr.colorPrimary))
    private val iconTint =
        ColorStateList.valueOf(context.getAttr(com.google.android.material.R.attr.colorOnSurfaceVariant))

    private val card by lazy { findViewById<MaterialCardView>(R.id.card) }
    private val imageView by lazy { findViewById<ImageView>(R.id.icon) }
    private val textView by lazy { findViewById<TextView>(R.id.text) }

    var isChecked = false
        set(value) {
            card.strokeWidth = if (value) strokeWidth else 0
            imageView.imageTintList = if (value) iconTintSelected else iconTint

            field = value
        }

    @DrawableRes
    var icon: Int? = null
        set(value) {
            value?.let(imageView::setImageResource) ?: imageView.setImageDrawable(null)

            field = value
        }

    @StringRes
    var text: Int? = null
        set(value) {
            textView.text = value?.let(context::getString) ?: ""

            field = value
        }

    var name: String? = null

    init {
        inflate(context, R.layout.check_card, this)

        card.setOnClickListener { isChecked = !isChecked }

        if (name != null) addCard(name!!, this)
    }

    fun setOnClickListener(listener: (Boolean) -> Unit) {
        card.setOnClickListener {
            isChecked = !isChecked
            listener(isChecked)
        }
    }

    override fun callOnClick(): Boolean {
        return card.callOnClick()
    }

    companion object {
        val id = R.id.checkCard
        fun inflate(context: Context, parent: ViewGroup, attachToRoot: Boolean = false): View =
            LayoutInflater.from(context).inflate(R.layout.check_card_wrapper, parent, attachToRoot)

        var allCards = mapOf<String, CheckCard>()
            private set

        private fun addCard(name: String, card: CheckCard) {
            allCards = HashMap(allCards).apply {
                this[name] = card
            }
        }
    }
}
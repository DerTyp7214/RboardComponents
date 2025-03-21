@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package de.dertyp7214.rboardcomponents.components

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.InsetDrawable
import android.os.Build
import android.util.AttributeSet
import android.util.TypedValue
import android.view.WindowInsets
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.MenuRes
import androidx.annotation.RequiresApi
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.widget.PopupMenu
import androidx.core.widget.doOnTextChanged
import com.google.android.material.card.MaterialCardView
import de.dertyp7214.rboardcomponents.R
import de.dertyp7214.rboardcomponents.core.*
import androidx.core.content.withStyledAttributes

@SuppressLint("ResourceType")
class SearchBar(context: Context, attrs: AttributeSet? = null) : LinearLayout(context, attrs) {

    var focus = false
        private set
    private var searchListener: (text: String) -> Unit = {}
    private var closeListener: () -> Unit = {}
    private var focusListener: () -> Unit = {}
    private var menuListener: (ImageButton) -> Unit = {}

    private var popupMenu: PopupMenu? = null
    private var menuItemClickListener: PopupMenu.OnMenuItemClickListener? = null

    internal val searchBar: MaterialCardView
    private val searchButton: ImageButton
    private val backButton: ImageButton
    private val moreButton: ImageButton
    private val searchText: TextView
    private val searchEdit: EditText

    var instantSearch: Boolean = false

    var imeReturn = true

    var text: String = ""
        set(value) {
            field = value
            if (value.isEmpty()) {
                focus = false
                searchButton.visibility = if (iconVisible) VISIBLE else INVISIBLE
                backButton.visibility = GONE

                searchText.visibility = VISIBLE
                searchEdit.visibility = GONE
            } else {
                focus = true
                searchButton.visibility = GONE
                backButton.visibility = VISIBLE

                searchText.visibility = GONE
                searchEdit.visibility = VISIBLE
            }

            searchEdit.setText(value)
            clearFocus(searchEdit)
        }

    var menuVisible: Boolean = true
        set(value) {
            field = value
            if (value) searchButton.setImageResource(R.drawable.ic_hamburger)
            else searchButton.setImageResource(R.drawable.ic_baseline_search_24)
        }

    var iconVisible: Boolean = true
        set(value) {
            field = value
            if (value) {
                searchButton.visibility = VISIBLE
                searchButton.setWidth(38.dpToPxRounded(context))
            } else {
                searchButton.visibility = INVISIBLE
                searchButton.setWidth(12.dpToPxRounded(context))
            }
        }

    var imeOptions
        set(value) {
            searchEdit.imeOptions = value
        }
        get() = searchEdit.imeOptions

    var inputType
        set(value) {
            searchEdit.setRawInputType(value)
        }
        get() = searchEdit.inputType

    var hint: CharSequence
        set(value) {
            searchEdit.hint = value
            searchText.text = value
        }
        get() = searchEdit.hint

    var incognito
        @RequiresApi(Build.VERSION_CODES.O)
        set(value) {
            searchEdit.imeOptions = EditorInfo.IME_NULL
            if (value) searchEdit.imeOptions = EditorInfo.IME_FLAG_NO_PERSONALIZED_LEARNING
        }
        @RequiresApi(Build.VERSION_CODES.O)
        get() = searchEdit.imeOptions and EditorInfo.IME_FLAG_NO_PERSONALIZED_LEARNING > 0

    var password
        set(value) {
            if (value) searchEdit.setRawInputType(EditorInfo.TYPE_TEXT_VARIATION_PASSWORD)
            else searchEdit.setRawInputType(EditorInfo.TYPE_CLASS_TEXT)
        }
        get() = searchEdit.inputType and EditorInfo.TYPE_TEXT_VARIATION_PASSWORD > 0

    var passwordNumber
        set(value) {
            if (value) searchEdit.setRawInputType(EditorInfo.TYPE_NUMBER_VARIATION_PASSWORD)
            else searchEdit.setRawInputType(EditorInfo.TYPE_CLASS_TEXT)
        }
        get() = searchEdit.inputType and EditorInfo.TYPE_NUMBER_VARIATION_PASSWORD > 0

    var email
        set(value) {
            if (value) searchEdit.setRawInputType(EditorInfo.TYPE_TEXT_VARIATION_EMAIL_ADDRESS)
            else searchEdit.setRawInputType(EditorInfo.TYPE_CLASS_TEXT)
        }
        get() = searchEdit.inputType and EditorInfo.TYPE_TEXT_VARIATION_EMAIL_ADDRESS > 0

    init {
        inflate(context, R.layout.search_bar, this)

        searchBar = findViewById(R.id.sb_search_bar)

        searchButton = findViewById(R.id.search_button)
        backButton = findViewById(R.id.back_button)
        moreButton = findViewById(R.id.more_button)

        searchText = findViewById(R.id.search_text)
        searchEdit = findViewById(R.id.search)

        context.withStyledAttributes(
            attrs,
            intArrayOf(R.attr.showIcon)
        ) {
            iconVisible = getBoolean(0, iconVisible)
        }

        moreButton.visibility = INVISIBLE

        searchBar.setOnClickListener {
            if (!focus) {
                focus = true
                searchButton.visibility = GONE
                backButton.visibility = VISIBLE

                searchText.visibility = GONE
                searchEdit.visibility = VISIBLE

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
                    searchEdit.windowInsetsController?.show(WindowInsets.Type.ime())
                searchEdit.requestFocus()
                focusListener()
            }
        }

        backButton.setOnClickListener {
            if (focus) {
                focus = false
                searchButton.visibility = if (iconVisible) VISIBLE else INVISIBLE
                backButton.visibility = GONE

                searchText.visibility = VISIBLE
                searchEdit.visibility = GONE

                searchEdit.setText("")
                clearFocus(searchEdit)
                closeListener()
            }
        }

        moreButton.setOnClickListener {
            popupMenu?.show()
        }

        searchEdit.doOnTextChanged { text, _, _, _ ->
            if (instantSearch) searchListener(text?.toString() ?: "")
        }

        searchEdit.setOnEditorActionListener { _, actionId, _ ->
            if (imeReturn && actionId == imeOptions) {
                clearFocus(searchEdit)
                searchListener(searchEdit.text.toString())
                true
            } else false
        }

        searchButton.setOnClickListener {
            if (menuVisible) menuListener(searchButton)
            else searchBar.callOnClick()
        }
    }

    fun setMenu(
        @MenuRes menu: Int? = null,
        itemClickListener: PopupMenu.OnMenuItemClickListener? = null
    ) {
        popupMenu = if (menu != null) {
            moreButton.visibility = VISIBLE
            PopupMenu(context, moreButton).also { popup ->
                popup.menuInflater.inflate(menu, popup.menu)
                popup.setOnMenuItemClickListener(itemClickListener)
                if (popup.menu is MenuBuilder) {
                    val menuBuilder = popup.menu as MenuBuilder
                    menuBuilder.setOptionalIconsVisible(true)
                    for (item in menuBuilder.visibleItems) {
                        val ICON_MARGIN = 4
                        val iconMarginPx =
                            TypedValue.applyDimension(
                                TypedValue.COMPLEX_UNIT_DIP, ICON_MARGIN.toFloat(), resources.displayMetrics)
                                .toInt()
                        if (item.icon != null) {
                            item.icon = InsetDrawable(item.icon, iconMarginPx, 0, iconMarginPx,0)
                        }
                    }
                }
            }
        } else {
            moreButton.visibility = INVISIBLE
            null
        }
    }

    fun focus() {
        searchBar.performClick()
    }

    fun search() {
        clearFocus(searchEdit)
        searchListener(searchEdit.text.toString())
    }

    fun clearText() = ::text.set("")

    fun setOnSearchListener(listener: (text: String) -> Unit) {
        searchListener = listener
    }

    fun setOnCloseListener(listener: () -> Unit) {
        closeListener = listener
    }

    fun setOnFocusListener(listener: () -> Unit) {
        focusListener = listener
    }

    fun setOnMenuListener(listener: (ImageButton) -> Unit) {
        menuListener = listener
    }

    fun close() {
        focus = false
        searchButton.visibility = if (iconVisible) VISIBLE else INVISIBLE
        backButton.visibility = GONE

        searchText.visibility = VISIBLE
        searchEdit.visibility = GONE

        searchEdit.setText("")
    }

    override fun clearFocus() {
        clearFocus(searchEdit)
    }

    private fun clearFocus(editText: EditText) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
            editText.windowInsetsController?.let { it::hide.delayed(100, WindowInsets.Type.ime()) }
    }
}
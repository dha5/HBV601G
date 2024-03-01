package com.hbv601.folf
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout

class CourseLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    init {
        // Inflate the existing XML layout file
        LayoutInflater.from(context).inflate(R.layout.fragment_courses, this, true)
    }
}

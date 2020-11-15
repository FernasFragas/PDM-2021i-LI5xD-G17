package pt.isel.pdm.drag.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color.BLACK
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import pt.isel.pdm.drag.model.Draws
import pt.isel.pdm.drag.model.Lines

/**
 *
 * Grafic Controller for drawing
 * Represents the view of the drawing of the player
 *
 */
class DragDrawView(ctx: Context, attrs: AttributeSet) : View(ctx, attrs) {

    var draws : Draws? = null
        set(value) {
            field = value
            invalidate()
        }

    private val brush: Paint = Paint().apply {
        color = BLACK
        strokeWidth = 10f
        style = Paint.Style.FILL_AND_STROKE
    }

    override fun onDraw(canvas: Canvas?){
        val localDraw = draws
        localDraw?.getDraws()?.iterator()?.forEach {
            canvas?.drawLine(
                    it.start.x, it.start.y,
                    it.end.x, it.end.y, brush)
        }
        invalidate()
    }
/*
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        TODO()
    }
*/
}
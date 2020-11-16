package pt.isel.pdm.drag.draw_activity.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color.BLACK
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import pt.isel.pdm.drag.draw_activity.DragViewModel
import pt.isel.pdm.drag.draw_activity.model.DragDraw

/**
 *
 * Grafic Controller for drawing
 * Represents the view of the drawing of the player
 *
 */
class DragDrawView(ctx: Context, attrs: AttributeSet) : View(ctx, attrs) {

    //TODO() Limitar onde se pode desenhar
    //TODO ADICONAR OPÇÃO BORRACHA PARA SE PODER APAGAR
    //TODO ADICONAR ESCOLHA DE CORES

    private val brush: Paint = Paint().apply {
        color = BLACK
        strokeWidth = 10f
        style = Paint.Style.FILL_AND_STROKE
    }

    override fun onDraw(canvas: Canvas?){
        var draws: DragDraw? = getDragDraws()
        draws?.getLines()?.iterator()?.forEach {
            canvas?.drawLine(
                    it.start.x, it.start.y,
                    it.end.x, it.end.y, brush)
        }
        invalidate()
    }


/*
    override fun onMeasure(wM: Int, hM: Int) {
        var w = MeasureSpec.getSize(wM)
        var h = MeasureSpec.getSize(hM)
        if (MeasureSpec.getMode(hM)==MeasureSpec.UNSPECIFIED) {
            h = height
        }
        if (MeasureSpec.getMode(wM)==MeasureSpec.UNSPECIFIED) {
            w = width
        }
        setMeasuredDimension(w,h)
    }
 */

    /**
     * data binding com viewModel
     */

    var viewModel : DragViewModel? = null
        set(value) {
            field = value
        }

    private fun getDragDraws(): DragDraw? {
        return viewModel?.dragDraw
    }
}
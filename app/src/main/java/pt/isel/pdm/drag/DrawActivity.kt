package pt.isel.pdm.drag

import android.os.Bundle
import android.view.MotionEvent.*
import androidx.appcompat.app.AppCompatActivity
import pt.isel.pdm.drag.databinding.ActivityDrawBinding
import pt.isel.pdm.drag.model.Draws
import pt.isel.pdm.drag.model.Lines
import pt.isel.pdm.drag.model.Position

/**
 * Activity referent for drawing
 */
class DrawActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityDrawBinding.inflate(layoutInflater)
        setContentView(binding.root)
        var start = Position()
        var drawsM = Draws()
        binding.dragDrawView.draws = drawsM
        binding.dragDrawView.setOnTouchListener { v, event ->
            when(event.action) {
                ACTION_DOWN -> start = Position(event.x,event.y)
                ACTION_MOVE -> {
                    drawsM.addDraws(Lines(start, Position(event.x, event.y)))
                    start = Position(event.x, event.y)
                }
                ACTION_UP -> {
                    drawsM.addDraws(Lines(start, Position(event.x, event.y)))
                }
            }
            true
        }
        }
    }
package pt.isel.pdm.drag

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import pt.isel.pdm.drag.databinding.ActivityMainBinding
import pt.isel.pdm.drag.view.DragDrawView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //o viewBinding permite que deixe de ser necessário utilizar o findViewById
        val binding = ActivityMainBinding.inflate(layoutInflater)   //ActivityMainBinding é a activity a que me estou a referir
        setContentView(binding.root)
    }
}
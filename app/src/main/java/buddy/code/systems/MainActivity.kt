package buddy.code.systems

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import buddy.code.systems.activities.form.FormActivity

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"
    private var mTvTitle: TextView? = null
    private var mLlHome: LinearLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        setupEdgeToEdgeInsets()
        initializeViews()

        mTvTitle?.text = "Caluladora de \nSueldos"

        mLlHome?.setOnClickListener {
            goToForm()
        }

    }

    private fun setupEdgeToEdgeInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun initializeViews() {
        mTvTitle = findViewById(R.id.tv_home_title)
        mLlHome = findViewById(R.id.ll_home)

    }

    private fun goToForm() {
        val intent = Intent(this, FormActivity::class.java)
        startActivity(intent)
    }

}




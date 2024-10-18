package buddy.code.systems.activities.result

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import buddy.code.systems.R
import buddy.code.systems.activities.form.FormActivity
import buddy.code.systems.utility.Constants.BPC
import buddy.code.systems.utility.Constants.TASA_DEDUCCIONES_DESDE15BPC
import buddy.code.systems.utility.Constants.TASA_DEDUCCIONES_HASTA15BPC

class ResultActivity : AppCompatActivity() {

    private val TAG = "ResultActivity"

    private lateinit var mTvSalarioLiquido: TextView
    private lateinit var mTvJubilatorio: TextView
    private lateinit var mTvFonasa: TextView
    private lateinit var mTvFRL: TextView
    private lateinit var mTvTotalBPS: TextView
    private lateinit var mTvTotalIRPF: TextView
    private lateinit var mButtonReturn: Button
    private lateinit var mTvTasaDeducciones: TextView
    private lateinit var mTvDeducciones: TextView
    private lateinit var mLlDeducciones: LinearLayout
    private lateinit var franjaTextViews: List<TextView>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_result)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inicialización de vistas
        initViews()

        // Obtener valores del intent
        val salaryNominal = intent.getDoubleExtra("SALARY", 0.0)
        val aportesBPS = intent.getDoubleExtra("BPS", 0.0)
        val fonasa = intent.getDoubleExtra("FONASA", 0.0)
        val frl = intent.getIntExtra("FRL", 0)
        val irpf = intent.getDoubleExtra("IRPF", 0.0)
        val deducciones = intent.getIntExtra("DEDUCCIONES", 0)
        val irpfPorFranja = intent.getDoubleArrayExtra("IRPF_FRANJAS") ?: DoubleArray(0)

        // Cálculo de deducciones
        val tasaDeduccion = if (salaryNominal <= TASA_DEDUCCIONES_HASTA15BPC * BPC) {
            TASA_DEDUCCIONES_HASTA15BPC
        } else {
            TASA_DEDUCCIONES_DESDE15BPC
        }
        val valorDeducciones = (deducciones.toDouble() * tasaDeduccion) / 100

        val salarioLiquido = salaryNominal - (aportesBPS + fonasa + frl + irpf) + valorDeducciones

        // Seteo de valores en TextViews
        mTvSalarioLiquido.text = "Salario liquido: $ ${salarioLiquido.toInt()}"
        mTvJubilatorio.text = "$ ${aportesBPS.toInt()}"
        mTvFonasa.text = "$ ${fonasa.toInt()}"
        mTvFRL.text = "$ $frl"
        mTvTotalBPS.text = "$ ${aportesBPS.toInt() + fonasa.toInt() + frl}"

        val irpfTotal = maxOf(0.0, irpf - valorDeducciones).toInt()
        mTvTotalIRPF.text = "Total IRPF: $ $irpfTotal"

        // Seteo de deducciones si es aplicable
        if (deducciones == 0) {
            mLlDeducciones.visibility = View.GONE
        } else {
            mTvTasaDeducciones.text = "Tasa deducciones: % $tasaDeduccion"
            mTvDeducciones.text = "Deducciones: $ $deducciones"
        }

        // Seteo de IRPF por franja
        franjaTextViews.forEachIndexed { index, textView ->
            textView.text = if (index < irpfPorFranja.size) {
                String.format("%.1f", irpfPorFranja[index])
            } else {
                "0.0"
            }
        }

        // Configuración del botón de retorno
        mButtonReturn.setOnClickListener { goToMainActivity() }
    }

    private fun initViews() {
        mTvSalarioLiquido = findViewById(R.id.tv_salario_liquido)
        mTvJubilatorio = findViewById(R.id.tv_jubilatorio)
        mTvFonasa = findViewById(R.id.tv_fonasa)
        mTvFRL = findViewById(R.id.tv_frl)
        mTvTotalBPS = findViewById(R.id.tv_total_bps)
        mTvTotalIRPF = findViewById(R.id.tv_total_irpf)
        mButtonReturn = findViewById(R.id.btn_return)
        mTvTasaDeducciones = findViewById(R.id.tv_tasa_deducciones)
        mTvDeducciones = findViewById(R.id.tv_deducciones)
        mLlDeducciones = findViewById(R.id.ll_deducciones)

        franjaTextViews = listOf(
            findViewById(R.id.tv_franja_irpf_1),
            findViewById(R.id.tv_franja_irpf_2),
            findViewById(R.id.tv_franja_irpf_3),
            findViewById(R.id.tv_franja_irpf_4),
            findViewById(R.id.tv_franja_irpf_5),
            findViewById(R.id.tv_franja_irpf_6),
            findViewById(R.id.tv_franja_irpf_7),
            findViewById(R.id.tv_franja_irpf_8)
        )
    }

    private fun goToMainActivity() {
        val intent = Intent(this, FormActivity::class.java)
        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val options = ActivityOptionsCompat.makeCustomAnimation(this, R.anim.slide_in_right, R.anim.slide_out_left)
        startActivity(intent, options.toBundle())
    }
}

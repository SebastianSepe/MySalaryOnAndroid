package buddy.code.systems.activities.result

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import buddy.code.systems.R
import buddy.code.systems.utility.Constants.BPC
import buddy.code.systems.utility.Constants.TASA_DEDUCCIONES_HASTA15BPC
import java.util.Arrays

class ResultActivity : AppCompatActivity() {

    private val TAG = "ResultActivity"
    private var mTvSalarioLiquido: TextView? = null
    private var mTvJubilatorio: TextView? = null
    private var mTvFonasa: TextView? = null
    private var mTvFRL: TextView? = null
    private var mTvTotalBPS: TextView? = null
    private var mTvTotalIRPF: TextView? = null
    private var mButtonReturn: Button? = null
    private var mTvFranja1: TextView? = null
    private var mTvFranja2: TextView? = null
    private var mTvFranja3: TextView? = null
    private var mTvFranja4: TextView? = null
    private var mTvFranja5: TextView? = null
    private var mTvFranja6: TextView? = null
    private var mTvFranja7: TextView? = null
    private var mTvFranja8: TextView? = null
    private var mTvDeducciones: TextView? = null
    private var mLlDeducciones: LinearLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_result)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        mTvSalarioLiquido = findViewById(R.id.tv_salario_liquido)
        mTvJubilatorio = findViewById(R.id.tv_jubilatorio)
        mTvFonasa = findViewById(R.id.tv_fonasa)
        mTvFRL = findViewById(R.id.tv_frl)
        mTvTotalBPS = findViewById(R.id.tv_total_bps)
        mTvTotalIRPF = findViewById(R.id.tv_total_irpf)
        mButtonReturn = findViewById(R.id.btn_return)
        mTvFranja1 = findViewById(R.id.tv_franja_irpf_1)
        mTvFranja2 = findViewById(R.id.tv_franja_irpf_2)
        mTvFranja3 = findViewById(R.id.tv_franja_irpf_3)
        mTvFranja4 = findViewById(R.id.tv_franja_irpf_4)
        mTvFranja5 = findViewById(R.id.tv_franja_irpf_5)
        mTvFranja6 = findViewById(R.id.tv_franja_irpf_6)
        mTvFranja7 = findViewById(R.id.tv_franja_irpf_7)
        mTvFranja8 = findViewById(R.id.tv_franja_irpf_8)
        mTvDeducciones = findViewById(R.id.tv_deducciones)
        mLlDeducciones = findViewById(R.id.ll_deducciones)



        val salaryNominal = intent.getIntExtra("SALARY", 0)
        val aportesBPS = intent.getIntExtra("BPS", 0)
        val fonasa = intent.getIntExtra("FONASA", 0)
        val frl = intent.getIntExtra("FRL", 0)
        val irpf = intent.getIntExtra("IRPF", 0)
        val deducciones = intent.getIntExtra("DEDUCCIONES", 0)
        val irpfPorFranja = intent.getDoubleArrayExtra("IRPF_FRANJAS") ?: DoubleArray(0)
        val franjaTextViews = listOf(mTvFranja1, mTvFranja2, mTvFranja3, mTvFranja4, mTvFranja5, mTvFranja6, mTvFranja7, mTvFranja8)
        val salarioLiquido = salaryNominal - aportesBPS - fonasa - frl - irpf

        // Mostrar o usar los valores como desees
        Log.d(TAG, "Valores de IRPF por franja: ${Arrays.toString(irpfPorFranja)}")


        for (i in franjaTextViews.indices) {
            if (i < irpfPorFranja.size) {
                franjaTextViews[i]?.text = String.format("%.2f", irpfPorFranja[i])
            } else {
                franjaTextViews[i]?.text = "0.00"  // Dejar vacío si no hay más franjas
            }
        }



        // Setea los valores en los TextViews
        mTvSalarioLiquido?.text = "Salario liquido: $ $salarioLiquido"
        mTvJubilatorio?.text = "Jubilatorio: $ $aportesBPS"
        mTvFonasa?.text = "FONASA: $ $fonasa"
        mTvFRL?.text = "FRL: $ $frl"
        mTvTotalBPS?.text = "Total BPS: $ ${aportesBPS + fonasa + frl}"
        mTvTotalIRPF?.text = "Total IRPF: $ $irpf"

        if (deducciones == 0) {
            mLlDeducciones?.visibility = View.GONE
        } else {
            mTvDeducciones?.text = "Deducciones: $ $deducciones"

            if (salaryNominal <= TASA_DEDUCCIONES_HASTA15BPC * BPC) {
                mTvSalarioLiquido?.text = "Tasa deducciones: % 14"
            }else{
                mTvSalarioLiquido?.text = "Tasa deducciones: % 8"
            }
        }

        mButtonReturn?.setOnClickListener{
            finish()
        }

    }


}

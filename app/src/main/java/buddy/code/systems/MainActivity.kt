package buddy.code.systems

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import buddy.code.systems.activities.result.ResultActivity
import buddy.code.systems.utility.Constants.ADICIONAL_FONDO_SOLIDARIDAD
import buddy.code.systems.utility.Constants.APORTES_JUBILATORIOS
import buddy.code.systems.utility.Constants.APORTE_FONASA_ADICIONAL
import buddy.code.systems.utility.Constants.APORTE_FONASA_BASICO
import buddy.code.systems.utility.Constants.APORTE_FONASA_CONYUGE
import buddy.code.systems.utility.Constants.APORTE_FONASA_HIJOS
import buddy.code.systems.utility.Constants.APORTE_FRL
import buddy.code.systems.utility.Constants.BPC
import buddy.code.systems.utility.Constants.DEDUCCION_HIJO_CON_DISCAPACIDAD
import buddy.code.systems.utility.Constants.DEDUCCION_HIJO_SIN_DISCAPACIDAD
import buddy.code.systems.utility.Constants.IRPF_FRANJAS
import buddy.code.systems.utility.Constants.MIN_BPC
import buddy.code.systems.utility.Constants.TASA_DEDUCCIONES_DESDE15BPC
import buddy.code.systems.utility.Constants.TASA_DEDUCCIONES_HASTA15BPC

class MainActivity : AppCompatActivity() {


    private val TAG = "MainActivity"

    private var mTvYear: TextView? = null
    private var mEtSalaryNominal: EditText? = null
    private var mLlHasChildren: LinearLayout? = null
    private var mCbHasChildren: CheckBox? = null
    private var mCbHasConyuje: CheckBox? = null
    private var mSpPercentageDeduction: Spinner? = null
    private var mEtQtyChildrenWithoutDisability: EditText? = null
    private var mEtQtyChildrenWithDisability: EditText? = null
    private var mSpContributionSolidarityFund: Spinner? = null
    private var mCbAdditionalSolidarityFund: CheckBox? = null
    private var mEtMonthlyContributionCJPPUOrNotarialBox: EditText? = null
    private var mEtOtherDeductions: EditText? = null
    private var mBCalculate: Button? = null
    private var mLlDeductionsOption: LinearLayout? = null
    private var mCbDeductions: CheckBox? = null
    private var mLlDeductionsContainer: LinearLayout? = null
    private var mTvResult: TextView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initializeViews()


        mCbDeductions?.setOnCheckedChangeListener() { _, isChecked ->
            if (isChecked) {
                mLlDeductionsContainer?.visibility = View.VISIBLE
            }else{
                mLlDeductionsContainer?.visibility = View.GONE
            }
        }

        val percentageValuesDeduction = arrayOf("100", "50", "0")
        val adapterDeduction = object : ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, percentageValuesDeduction) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent)
                "${getItem(position)}%".also { (view as TextView).text = it }
                return view
            }
            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getDropDownView(position, convertView, parent)
                "${getItem(position)}%".also { (view as TextView).text = it }
                return view
            }
        }

        adapterDeduction.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        mSpPercentageDeduction?.adapter = adapterDeduction


        val aportaFondoSolidaridad = arrayOf("0", "1/2", "1", "2")
        val adapterFondoSolidaridad = object : ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, aportaFondoSolidaridad) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent)
                "${getItem(position)} BPC".also { (view as TextView).text = it }
                return view
            }
            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getDropDownView(position, convertView, parent)
                "${getItem(position)} BPC".also { (view as TextView).text = it }
                return view
            }
        }

        adapterFondoSolidaridad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        mSpContributionSolidarityFund?.adapter = adapterFondoSolidaridad




        mBCalculate?.setOnClickListener {
            val salaryNominal = mEtSalaryNominal?.text.toString().toIntOrNull() ?: 0

            if (salaryNominal <= 0) {
                Toast.makeText(this, "El salario nominal debe ser mayor a 0", Toast.LENGTH_SHORT).show()
            } else {
                val hasConyuge = mCbHasConyuje?.isChecked ?: false
                val hasChildren = mCbHasChildren?.isChecked ?: false
                val aportesBPS = calcularAportesBPS(salaryNominal)
                val fonasa = calcularAportesFONASA(salaryNominal, hasChildren, hasConyuge)
                val frl = calcularAportesFRL(salaryNominal)
                val (irpf, irpfPorFranjaArray) = calcularAportesIRPF(salaryNominal)
                var deducciones = 0.0
                val totalAportes = aportesBPS + fonasa + frl

                if (mCbDeductions?.isChecked == true) {
                    deducciones = calcularDeducciones(
                        salaryNominal, totalAportes,
                        mEtQtyChildrenWithoutDisability?.text.toString().toIntOrNull() ?: 0,
                        mEtQtyChildrenWithDisability?.text.toString().toIntOrNull() ?: 0,
                        percentageValuesDeduction[mSpPercentageDeduction?.selectedItemPosition ?: 0].toInt(),
                        mSpContributionSolidarityFund?.selectedItemPosition ?: 0,
                        mCbAdditionalSolidarityFund?.isChecked ?: false,
                        mEtMonthlyContributionCJPPUOrNotarialBox?.text.toString().toIntOrNull() ?: 0,
                        mEtOtherDeductions?.text.toString().toIntOrNull() ?: 0
                    )
                }
                val irpfPorFranja = calcularAportesIRPF(salaryNominal).second
                goToResultActivity(salaryNominal, aportesBPS, fonasa, frl, irpf, deducciones, irpfPorFranja)
            }
        }


    }

    private fun goToResultActivity(salaryNominal: Int, aportesBPS: Int, fonasa: Double, frl: Double, irpf: Int, deducciones: Double, irpfPorFranja: DoubleArray) {
        val intent = Intent(this, ResultActivity::class.java)
        intent.putExtra("SALARY", salaryNominal)
        intent.putExtra("BPS", aportesBPS)
        intent.putExtra("FONASA", fonasa.toInt())
        intent.putExtra("FRL", frl.toInt())
        intent.putExtra("IRPF", irpf)
        intent.putExtra("IRPF_FRANJAS", irpfPorFranja)
        intent.putExtra("DEDUCCIONES", deducciones)
        Log.d("TAG", "IRPF por franjas enviado: ${irpfPorFranja.joinToString()}")
        startActivity(intent)
    }



    private fun initializeViews() {
        mTvYear = findViewById(R.id.tv_year)
        mEtSalaryNominal = findViewById(R.id.et_salary_nominal)
        mCbHasChildren = findViewById(R.id.cb_hasChildren)
        mCbHasConyuje = findViewById(R.id.cb_hasConyuge)
        mSpPercentageDeduction = findViewById(R.id.sp_percentage_deduction)
        mEtQtyChildrenWithoutDisability = findViewById(R.id.et_qty_children_without_disability)
        mEtQtyChildrenWithDisability = findViewById(R.id.et_qty_children_with_disability)
        mSpContributionSolidarityFund = findViewById(R.id.sp_contribution_solidarity_fund)
        mCbAdditionalSolidarityFund = findViewById(R.id.cb_additional_solidarity_fund)
        mEtMonthlyContributionCJPPUOrNotarialBox = findViewById(R.id.et_monthly_contribution_cjppu_or_notarial_box)
        mEtOtherDeductions = findViewById(R.id.et_other_deductions)
        mBCalculate = findViewById(R.id.btn_calculate)
        mCbDeductions = findViewById(R.id.cb_hasDeductions)
        mLlDeductionsContainer = findViewById(R.id.ll_deductions_container)
        mTvResult = findViewById(R.id.tv_result)
    }

    private fun calcularAportesBPS(salarioNominal: Int): Int {
        return (salarioNominal * APORTES_JUBILATORIOS) / 100
    }


    private fun calcularAportesFONASA(salarioNominal: Int, tieneHijos: Boolean, tieneConyuge: Boolean): Double {
        val salarioEnBPC = salarioNominal / BPC
        var percentageFONASA = 0.0

        if (salarioEnBPC <= MIN_BPC) {
            percentageFONASA += APORTE_FONASA_BASICO
            if (tieneHijos) {
                percentageFONASA += APORTE_FONASA_HIJOS
            }
            if (tieneConyuge) {
                percentageFONASA += APORTE_FONASA_CONYUGE
            }

        }else if (salarioEnBPC > MIN_BPC) {
            percentageFONASA += APORTE_FONASA_BASICO +  APORTE_FONASA_ADICIONAL
            if (tieneHijos) {
                percentageFONASA += APORTE_FONASA_HIJOS
            }
            if (tieneConyuge) {
                percentageFONASA += APORTE_FONASA_CONYUGE
            }
        }

        return (salarioNominal * percentageFONASA) / 100
    }

    private fun calcularAportesFRL(salarioNominal: Int): Double {
        return (salarioNominal * APORTE_FRL)
    }

    private fun calcularAportesIRPF(salarioNominal: Int): Pair<Int, DoubleArray> {
        val salarioEnPesos = salarioNominal * 1.06
        Log.d(TAG, "Salario en pesos: $salarioEnPesos")

        var totalIRPF = 0.0
        val irpfPorFranja = mutableListOf<Double>()

        for (franja in IRPF_FRANJAS) {
            if (salarioEnPesos >= franja.desde * BPC) {
                // Convierte ambos a Double antes de compararlos
                val calculoFranja = minOf(
                    salarioEnPesos,
                    (franja.hasta * BPC).toDouble()
                ) - (franja.desde * BPC).toDouble()

                val irpf = calculoFranja * franja.tasa / 100
                irpfPorFranja.add(irpf)
                totalIRPF += irpf
            }
        }

        return Pair(totalIRPF.toInt(), irpfPorFranja.toDoubleArray())
    }

    private fun calcularDeducciones(
        salarioNominal: Int,
        totalAportes: Double,
        cantidadHijosSinDiscapacidad: Int,
        cantidadHijosConDiscapacidad: Int,
        porcentajeAportesFondoSolidaridad: Int,
        aportaFondoSolidaridad: Int,
        adicionalFondoSolidaridad: Boolean,
        aporteMensualCJJPUONotarial: Int,
        otrasDeducciones: Int
    ): Double {

        var sumaTotalParaDeducciones = 0.0
        sumaTotalParaDeducciones = totalAportes


        if (porcentajeAportesFondoSolidaridad > 0 && cantidadHijosSinDiscapacidad > 0) {
           sumaTotalParaDeducciones += (DEDUCCION_HIJO_SIN_DISCAPACIDAD * porcentajeAportesFondoSolidaridad) / 100
        }else if(porcentajeAportesFondoSolidaridad > 0 && cantidadHijosConDiscapacidad > 0) {
            sumaTotalParaDeducciones += (DEDUCCION_HIJO_CON_DISCAPACIDAD * porcentajeAportesFondoSolidaridad) / 100
        }

        if (aportaFondoSolidaridad > 0) {
            sumaTotalParaDeducciones += aportaFondoSolidaridad
        }

        if (adicionalFondoSolidaridad) {
            sumaTotalParaDeducciones += ADICIONAL_FONDO_SOLIDARIDAD
        } else{
            sumaTotalParaDeducciones += 0.0
        }

        if (aporteMensualCJJPUONotarial > 0){
            sumaTotalParaDeducciones += aporteMensualCJJPUONotarial
        }

        if (otrasDeducciones > 0){
            sumaTotalParaDeducciones += otrasDeducciones
        }

        val salarioEnBPC = salarioNominal / BPC

        return if (salarioEnBPC <= TASA_DEDUCCIONES_HASTA15BPC)
            (sumaTotalParaDeducciones * TASA_DEDUCCIONES_HASTA15BPC) / 100 else {
            (sumaTotalParaDeducciones * TASA_DEDUCCIONES_DESDE15BPC) / 100
        }
    }

}
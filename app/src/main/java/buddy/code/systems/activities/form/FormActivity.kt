package buddy.code.systems.activities.form

import android.content.Intent
import android.os.Bundle
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
import buddy.code.systems.R
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

class FormActivity : AppCompatActivity() {

    private val TAG = "FormActivity"

    private lateinit var mEtSalaryNominal: EditText
    private lateinit var mCbHasChildren: CheckBox
    private lateinit var mCbHasConyuge: CheckBox
    private lateinit var mSpPercentageDeduction: Spinner
    private lateinit var mEtQtyChildrenWithoutDisability: EditText
    private lateinit var mEtQtyChildrenWithDisability: EditText
    private lateinit var mSpContributionSolidarityFund: Spinner
    private lateinit var mCbAdditionalSolidarityFund: CheckBox
    private lateinit var mEtMonthlyContributionCJPPUOrNotarialBox: EditText
    private lateinit var mEtOtherDeductions: EditText
    private lateinit var mLlDeductionsContainer: LinearLayout
    private lateinit var mCbDeductions: CheckBox
    private lateinit var mBCalculate: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_form)
        setupEdgeToEdgeInsets()
        initializeViews()
        setupListeners()
    }

    private fun setupEdgeToEdgeInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun initializeViews() {
        mEtSalaryNominal = findViewById(R.id.et_salary_nominal)
        mCbHasChildren = findViewById(R.id.cb_hasChildren)
        mCbHasConyuge = findViewById(R.id.cb_hasConyuge)
        mSpPercentageDeduction = findViewById(R.id.sp_percentage_deduction)
        mEtQtyChildrenWithoutDisability = findViewById(R.id.et_qty_children_without_disability)
        mEtQtyChildrenWithDisability = findViewById(R.id.et_qty_children_with_disability)
        mSpContributionSolidarityFund = findViewById(R.id.sp_contribution_solidarity_fund)
        mCbAdditionalSolidarityFund = findViewById(R.id.cb_additional_solidarity_fund)
        mEtMonthlyContributionCJPPUOrNotarialBox =
            findViewById(R.id.et_monthly_contribution_cjppu_or_notarial_box)
        mEtOtherDeductions = findViewById(R.id.et_other_deductions)
        mLlDeductionsContainer = findViewById(R.id.ll_deductions_container)
        mCbDeductions = findViewById(R.id.cb_hasDeductions)
        mBCalculate = findViewById(R.id.btn_calculate)

        setupSpinner(mSpPercentageDeduction, arrayOf("100", "50", "0")) { "$it%" }
        setupSpinner(mSpContributionSolidarityFund, arrayOf("0", "1/2", "1", "2")) { "$it BPC" }
    }

    private fun setupSpinner(spinner: Spinner, values: Array<String>, format: (String) -> String) {
        val adapter = object : ArrayAdapter<String>(
            this, android.R.layout.simple_spinner_item, values
        ) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                return super.getView(position, convertView, parent).apply {
                    (this as TextView).text = format(getItem(position) ?: "")
                }
            }

            override fun getDropDownView(
                position: Int, convertView: View?, parent: ViewGroup
            ): View {
                return super.getDropDownView(position, convertView, parent).apply {
                    (this as TextView).text = format(getItem(position) ?: "")
                }
            }
        }
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

    private fun setupListeners() {
        mCbDeductions.setOnCheckedChangeListener { _, isChecked ->
            mLlDeductionsContainer.visibility = if (isChecked) View.VISIBLE else View.GONE
        }

        mBCalculate.setOnClickListener {
            calculate()
        }
    }

    private fun calculate() {
        val salaryNominal = mEtSalaryNominal.text.toString().toDoubleOrNull() ?: 0.0
        if (salaryNominal <= 0) {
            Toast.makeText(this, "El salario nominal debe ser mayor a 0", Toast.LENGTH_SHORT).show()
            return
        }

        val hasConyuge = mCbHasConyuge.isChecked
        val hasChildren = mCbHasChildren.isChecked
        val aportesBPS = calcularAportesBPS(salaryNominal)
        val fonasa = calcularAportesFONASA(salaryNominal, hasChildren, hasConyuge)
        val frl = calcularAportesFRL(salaryNominal)
        val (irpf, irpfPorFranjaArray) = calcularAportesIRPF(salaryNominal)

        val deducciones = sumaDeducciones(
            salaryNominal,
            aportesBPS + fonasa + frl,
            mEtQtyChildrenWithoutDisability.text.toString().toIntOrNull() ?: 0,
            mEtQtyChildrenWithDisability.text.toString().toIntOrNull() ?: 0,
            mSpPercentageDeduction.selectedItem.toString().toInt(),
            mSpContributionSolidarityFund.selectedItemPosition,
            mCbAdditionalSolidarityFund.isChecked,
            mEtMonthlyContributionCJPPUOrNotarialBox.text.toString().toIntOrNull() ?: 0,
            mEtOtherDeductions.text.toString().toIntOrNull() ?: 0
        )

        goToResultActivity(salaryNominal, aportesBPS, fonasa, frl, irpf, deducciones, irpfPorFranjaArray)
    }

    private fun goToResultActivity(
        salaryNominal: Double,
        aportesBPS: Double,
        fonasa: Double,
        frl: Double,
        irpf: Double,
        deducciones: Int,
        irpfPorFranja: DoubleArray
    ) {
        Intent(this, ResultActivity::class.java).apply {
            putExtra("SALARY", salaryNominal)
            putExtra("BPS", aportesBPS)
            putExtra("FONASA", fonasa)
            putExtra("FRL", frl.toInt())
            putExtra("IRPF", irpf)
            putExtra("IRPF_FRANJAS", irpfPorFranja)
            putExtra("DEDUCCIONES", deducciones)

            // Agrega las flags para borrar el historial de pantallas previas
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

            startActivity(this)
        }
    }

    private fun calcularAportesBPS(salarioNominal: Double): Double {
        return (salarioNominal * APORTES_JUBILATORIOS) / 100
    }

    private fun calcularAportesFONASA(
        salarioNominal: Double,
        tieneHijos: Boolean,
        tieneConyuge: Boolean
    ): Double {
        val salarioEnBPC = salarioNominal / BPC
        var percentageFONASA: Double = APORTE_FONASA_BASICO.toDouble()

        if (tieneHijos) percentageFONASA += APORTE_FONASA_HIJOS
        if (tieneConyuge) percentageFONASA += APORTE_FONASA_CONYUGE
        if (salarioEnBPC > MIN_BPC) percentageFONASA += APORTE_FONASA_ADICIONAL

        return (salarioNominal * percentageFONASA) / 100
    }

    private fun calcularAportesFRL(salarioNominal: Double): Double {
        return salarioNominal * APORTE_FRL
    }

    private fun calcularAportesIRPF(salarioNominal: Double): Pair<Double, DoubleArray> {
        val salarioEnPesos = salarioNominal * 1.06
        var totalIRPF = 0.0
        val irpfPorFranja = mutableListOf<Double>()

        for (franja in IRPF_FRANJAS) {
            val franjaDesdeEnPesos = (franja.desde * BPC).toDouble()
            val franjaHastaEnPesos = (franja.hasta * BPC).toDouble()

            if (salarioEnPesos >= franjaDesdeEnPesos) {
                val baseCalculo = minOf(salarioEnPesos, franjaHastaEnPesos) - franjaDesdeEnPesos
                val irpf = baseCalculo * franja.tasa.toDouble() / 100
                irpfPorFranja.add(irpf)
                totalIRPF += irpf
            }
        }

        return totalIRPF to irpfPorFranja.toDoubleArray()
    }

    private fun sumaDeducciones(
        salarioNominal: Double,
        totalAportes: Double,
        cantidadHijosSinDiscapacidad: Int,
        cantidadHijosConDiscapacidad: Int,
        porcentajeAportesFondoSolidaridad: Int,
        aportaFondoSolidaridad: Int,
        adicionalFondoSolidaridad: Boolean,
        aporteMensualCJJPUONotarial: Int,
        otrasDeducciones: Int
    ): Int {

        var sumaTotalParaDeducciones = 0.0
        sumaTotalParaDeducciones = totalAportes


        if (porcentajeAportesFondoSolidaridad > 0 && cantidadHijosSinDiscapacidad > 0) {
            sumaTotalParaDeducciones += (DEDUCCION_HIJO_SIN_DISCAPACIDAD * porcentajeAportesFondoSolidaridad) / 100
        } else if (porcentajeAportesFondoSolidaridad > 0 && cantidadHijosConDiscapacidad > 0) {
            sumaTotalParaDeducciones += (DEDUCCION_HIJO_CON_DISCAPACIDAD * porcentajeAportesFondoSolidaridad) / 100
        }

        if (aportaFondoSolidaridad > 0) {
            sumaTotalParaDeducciones += aportaFondoSolidaridad
        }

        if (adicionalFondoSolidaridad) {
            sumaTotalParaDeducciones += ADICIONAL_FONDO_SOLIDARIDAD
        } else {
            sumaTotalParaDeducciones += 0.0
        }

        if (aporteMensualCJJPUONotarial > 0) {
            sumaTotalParaDeducciones += aporteMensualCJJPUONotarial
        }

        if (otrasDeducciones > 0) {
            sumaTotalParaDeducciones += otrasDeducciones
        }

        return sumaTotalParaDeducciones.toInt()
    }
}
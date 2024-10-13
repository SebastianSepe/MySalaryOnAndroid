package buddy.code.systems.utility

object Constants {
    const val BPC = 6177
    const val MIN_BPC = 2.5

    data class FranjasIrpf(
        val desde: Int,
        val hasta: Int,
        val tasa: Int
    )
    val IRPF_FRANJAS = listOf(
        FranjasIrpf(0, 7, 0),
        FranjasIrpf(7, 10, 10),
        FranjasIrpf(10, 15, 15),
        FranjasIrpf(15, 30, 24),
        FranjasIrpf(30, 50, 25),
        FranjasIrpf(50, 75, 27),
        FranjasIrpf(75, 115, 31),
        FranjasIrpf(115, Int.MAX_VALUE, 36) // Usando Int.MAX_VALUE para representar 'hasta: 0'
    )

    const val APORTES_JUBILATORIOS = 15

    const val TOPE_APORTE_JUBILATORIO = 236309

    const val APORTE_FONASA_BASICO = 3
    const val APORTE_FONASA_ADICIONAL = 1.5
    const val APORTE_FONASA_HIJOS = 1.5
    const val APORTE_FONASA_CONYUGE = 2

    const val APORTE_FRL = 0.001

    const val TASA_DEDUCCIONES_HASTA15BPC = 14
    const val TASA_DEDUCCIONES_DESDE15BPC = 8

    const val DEDUCCION_HIJO_SIN_DISCAPACIDAD = (20 * BPC) / 12
    const val DEDUCCION_HIJO_CON_DISCAPACIDAD = (40 * BPC) / 12

    const val ADICIONAL_FONDO_SOLIDARIDAD = ((5 / 4) * BPC) / 12;



}
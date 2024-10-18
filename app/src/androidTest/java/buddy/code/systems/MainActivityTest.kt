package buddy.code.systems

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import org.hamcrest.CoreMatchers.containsString
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @Rule
    @JvmField
    var activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun testInputAndCalculation() {
        // Ingresar salario nominal
        var salaryNominal = 80000
        onView(withId(R.id.et_salary_nominal)).perform(typeText(salaryNominal.toString()), closeSoftKeyboard())

        // Verificar que el texto "Salario Nominal en pesos: $" esté visible
        onView(withText("Salario Nominal en pesos:  $")).check(matches(isDisplayed()))

       // Marcar el CheckBox de "Tiene hijos a cargo?"
        onView(withId(R.id.cb_hasChildren)).perform(click())

        // Marcar el CheckBox de "Tiene Conyuge a cargo?"
        onView(withId(R.id.cb_hasConyuge)).perform(click())

        // Marcar el CheckBox de "Tiene deducciones?" y verificar que el layout de deducciones aparezca
        onView(withId(R.id.cb_hasDeductions)).perform(click())
        onView(withId(R.id.ll_deductions_container)).check(matches(isDisplayed()))

        // Ingresar la cantidad de hijos sin discapacidad
        onView(withId(R.id.et_qty_children_without_disability)).perform(typeText("1"), closeSoftKeyboard())

        // Ingresar la cantidad de hijos con discapacidad
        onView(withId(R.id.et_qty_children_with_disability)).perform(typeText("0"), closeSoftKeyboard())

        // Hacer click en el botón "Calcular"
        onView(withId(R.id.btn_calculate)).perform(click())

        // Verificar que el texto "Salario liquido: $" esté visible
        var salaryLiquid = 60240
        onView(withId(R.id.tv_salario_liquido))
            .check(matches(isDisplayed()))
            .check(matches(withText("Salario liquido: $ $salaryLiquid")));

        onView(withText("Jubilatorio")).check(matches(isDisplayed()))
        onView(withId(R.id.tv_jubilatorio))
            .check(matches(withText(containsString("$ 12000")))); //


        onView(withText("FONASA")).check(matches(isDisplayed()))
        onView(withId(R.id.tv_fonasa))
            .check(matches(withText(containsString("$ 6400"))));

        onView(withText("FRL")).check(matches(isDisplayed()))
        onView(withId(R.id.tv_frl))
            .check(matches(withText(containsString("$ 80"))));

        onView(withText("Total BPS")).check(matches(isDisplayed()))
        onView(withId(R.id.tv_total_bps))
            .check(matches(withText(containsString("$ 18480"))));


        onView(withId(R.id.tv_deducciones))
            .check(matches(withText(containsString("Deducciones: $ 28775"))));


        onView(withId(R.id.tv_tasa_deducciones))
            .check(matches(withText(containsString("Tasa deducciones: % 14"))));


        onView(withId(R.id.tv_total_irpf))
            .check(matches(withText(containsString("Total IRPF: $ 1279"))));

    }
}

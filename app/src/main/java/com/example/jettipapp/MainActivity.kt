package com.example.jettipapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jettipapp.components.InputField
import com.example.jettipapp.ui.theme.JetTipAppTheme
import com.example.jettipapp.util.calculateTotalPerson
import com.example.jettipapp.util.calculateTotalTip
import com.example.jettipapp.widgets.RoundIconButton

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApp {
                MainContent()
            }
        }
    }
}

@Composable
fun MyApp(content: @Composable () -> Unit) {
    JetTipAppTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            color = MaterialTheme.colors.background
        ) {
            content()
        }
    }
}

@Composable
fun TopHeader(totalPerPerson: Double = 134.0) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp)
            .height(150.dp)
            .clip(shape = RoundedCornerShape(corner = CornerSize(12.dp))),
        color = Color(0xFFE9D7F7)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val total = "%.2f".format(totalPerPerson)
            Text(
                text = "Total Per Person",
                style = MaterialTheme.typography.h5
            )
            Text(
                text = "$$total", style = MaterialTheme.typography.h4,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@Preview
@Composable
fun MainContent() {

    val range = IntRange(start = 1, endInclusive = 100)
    val numberSplit = remember {
        mutableStateOf(1)
    }
    val tipAmountState = remember {
        mutableStateOf(0.0)
    }
    val totalPerPersonState = remember {
        mutableStateOf(0.0)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        BillForm(
            modifier = Modifier.weight(1f),
            numberSplitState = numberSplit,
            tipAmountState = tipAmountState,
            totalPerPersonState = totalPerPersonState
        ) { billAmt -> }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun BillForm(
    modifier: Modifier = Modifier,
    range: IntRange = 1..100,
    numberSplitState: MutableState<Int>,
    tipAmountState: MutableState<Double>,
    totalPerPersonState: MutableState<Double>,

    onValChange: (String) -> Unit = {}
) {
    val totalBillState = remember {
        mutableStateOf("")
    }
    val validState = remember(totalBillState.value) {
        totalBillState.value.trim().isNotEmpty()
    }
    val keyboardController = LocalSoftwareKeyboardController.current

    val sliderPositionState = remember {
        mutableStateOf(0f)
    }
    val tipPorcentage = (sliderPositionState.value * 100).toInt()

    TopHeader(totalPerPerson = totalPerPersonState.value)
    Surface(
        modifier = Modifier
            .padding(15.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(corner = CornerSize(8.dp)),
        border = BorderStroke(width = 1.dp, color = Color.LightGray)
    ) {
        Column(
            modifier = Modifier.padding(6.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {

            InputField(
                modifier = Modifier,
                valueState = totalBillState,
                labelId = "Enter Bill",
                enabled = true,
                isSingleLine = true,
                onAction = KeyboardActions {
                    if (!validState) return@KeyboardActions
                    onValChange(totalBillState.value.trim())
                    keyboardController?.hide()
                    calculateTotalPerPersonGeral(
                        totalPerPersonState,
                        totalBillState,
                        numberSplitState,
                        tipPorcentage
                    )
                }
            )

            //Split Row
            if (validState) {
                Row(
                    modifier = Modifier.padding(3.dp),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(
                        text = "Split", modifier = Modifier.align(
                            alignment = Alignment.CenterVertically
                        )
                    )
                    Spacer(modifier = Modifier.width(220.dp))
                    Row(
                        modifier = Modifier.padding(horizontal = 3.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        RoundIconButton(
                            imageVector = Icons.Default.Remove,
                            onClick = {
                                if (numberSplitState.value > 1) numberSplitState.value--
                                calculateTotalPerPersonGeral(
                                    totalPerPersonState,
                                    totalBillState,
                                    numberSplitState,
                                    tipPorcentage
                                )
                            })

                        Text(
                            text = numberSplitState.value.toString(),
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .padding(start = 9.dp, end = 9.dp)
                        )

                        RoundIconButton(
                            imageVector = Icons.Default.Add,
                            onClick = {
                                numberSplitState.value++
                                calculateTotalPerPersonGeral(
                                    totalPerPersonState,
                                    totalBillState,
                                    numberSplitState,
                                    tipPorcentage
                                )
                            })
                    }

                }
                //Tip Row
                Row(modifier = Modifier.padding(horizontal = 3.dp, vertical = 12.dp)) {
                    Text(
                        text = "Tip",
                        modifier = Modifier.align(alignment = Alignment.CenterVertically)
                    )
                    Spacer(modifier = Modifier.width(300.dp))
                    Text(
                        text = "$ ${tipAmountState.value}",
                        modifier = Modifier.align(alignment = Alignment.CenterVertically)
                    )
                }

                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "$tipPorcentage %")
                    Spacer(modifier = Modifier.height(14.dp))
                    //Slider
                    Slider(
                        value = sliderPositionState.value, onValueChange = { newVal ->
                            sliderPositionState.value = newVal
                            tipAmountState.value =
                                calculateTotalTip(
                                    totalBill = totalBillState.value.toDouble(),
                                    tipPorcentage = tipPorcentage
                                )
                            calculateTotalPerPersonGeral(
                                totalPerPersonState,
                                totalBillState,
                                numberSplitState,
                                tipPorcentage
                            )
                        }, modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                        steps = 5
                    )
                }

            } else {
                Box() {

                }
            }

        }
    }
}


private fun calculateTotalPerPersonGeral(
    totalPerPersonState: MutableState<Double>,
    totalBillState: MutableState<String>,
    numberSplit: MutableState<Int>,
    tipPorcentage: Int
) {
    totalPerPersonState.value = calculateTotalPerson(
        totalBill = totalBillState.value.toDouble(),
        numberSplit = numberSplit.value,
        tipPorcentage = tipPorcentage
    )
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MyApp {
    }
}
package org.ligi.plughub

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SwitchCompat
import android.text.method.LinkMovementMethod
import android.widget.EditText
import android.widget.TextView
import org.jetbrains.anko.*


public class MainActivity : AppCompatActivity() {

    val cfg = EdiMaxConfig()
    val comm = EdiMaxCommunicator(cfg)

    var switch: SwitchCompat? = null
    var powerState: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        createUI()

        getDataCall()
    }

    private fun getDataCall() {
        comm.executeCommand(EdiMaxCommands.CMD_GET_STATE, { param ->
            runOnUiThread {
                switch!!.setChecked((EdiMaxCommands.unwrapPowerState(param) == "ON"))

                comm.executeCommand(EdiMaxCommands.CMD_GET_POWER, { response ->
                    runOnUiThread {
                        powerState!!.setText(EdiMaxCommands.unwrapNowCurrent(response) + "A " + EdiMaxCommands.unwrapNowPower(response) + "W")
                        getDataCall()
                    }
                });
            }

        })
    }

    private fun createUI() {
        verticalLayout() {
            textView("Only works with EdiMax plugs at the moment").setMovementMethod(LinkMovementMethod())
            linearLayout {
                editText {
                    setText(cfg.host)
                    extractText { text ->
                        cfg.host = text
                    }
                }
                textView(":")
                editText {
                    setText(cfg.port.toString())
                    extractText { text ->
                        cfg.port = Integer.valueOf(text)
                    }
                }
            }

            editText {
                setText(cfg.pass)
                extractText { text ->
                    cfg.pass = text
                }
            }

            switch = switchCompatSupport() {
                onCheckedChange { compoundButton, b ->
                    comm.executeCommand(if (b) EdiMaxCommands.CMD_ON else EdiMaxCommands.CMD_OFF, {});
                }
                setText("Switch")
            }
            powerState = textView()
        }.setPadding(dip(16), dip(16), dip(16), dip(16))
    }

    private fun EditText.extractText(function: (param: String) -> Unit) {
        textChangedListener {
            onTextChanged { text, start, before, count ->
                function(text.toString())
            }
        }
    }

}

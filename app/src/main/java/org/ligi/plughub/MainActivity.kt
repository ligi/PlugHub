package org.ligi.plughub

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SwitchCompat
import android.text.method.LinkMovementMethod
import android.widget.EditText
import android.widget.TextView
import com.squareup.okhttp.*
import org.jetbrains.anko.*
import java.net


public class MainActivity : AppCompatActivity() {

    val ipStr = "192.168.2.100"
    var ipET: EditText? = null

    val portStr = "10000"
    var portET: EditText? = null


    val pwdStr = "12345"
    var pwdET: EditText? = null

    var switch: SwitchCompat? = null
    var powerState: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        verticalLayout() {
            textView("Only works with EdiMax plugs at the moment").setMovementMethod(LinkMovementMethod())
            linearLayout {
                ipET = editText {
                    setText(ipStr)
                }
                textView(":")
                portET = editText {
                    setText(portStr)
                }
            }

            pwdET = editText {
                setText(pwdStr)
            }

            switch = switchCompatSupport() {
                onCheckedChange { compoundButton, b ->
                    executeCommand(if (b) EdiMaxCommands.CMD_ON else EdiMaxCommands.CMD_OFF, {});
                }
                setText("Switch")
            }
            powerState = textView()
        }.setPadding(dip(16), dip(16), dip(16), dip(16))

        executeCommand(EdiMaxCommands.CMD_GET_STATE, { param ->
            runOnUiThread {
                switch!!.setChecked((EdiMaxCommands.unwrapPowerState(param) == "ON"))

                executeCommand(EdiMaxCommands.CMD_GET_POWER, { param ->
                    runOnUiThread {
                        powerState!!.setText(param)
                    }
                });
            }

        });
    }

    private fun executeCommand(cmd: String, function: (param: String) -> Unit) {
        Thread(Runnable {
            val body = RequestBody.create(null, cmd);

            val client = OkHttpClient();

            client.setAuthenticator(auth())

            val request = Request.Builder()
                    .url("http://${ipET!!.getText()}:${portET!!.getText()}/smartplug.cgi")
                    .post(body)
                    .build();

            val response = client.newCall(request).execute()
            function(response.body().string())
        }).start()
    }

    inner class auth : Authenticator {
        override fun authenticateProxy(proxy: net.Proxy?, response: Response?): Request? {
            return null
        }

        override fun authenticate(proxy: net.Proxy?, response: Response?): Request? {
            val credential = Credentials.basic("admin", pwdET!!.getText().toString());
            return response?.request()?.newBuilder()?.header("Authorization", credential)?.build();
        }

    }

}

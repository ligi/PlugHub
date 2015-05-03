package org.ligi.plughub

import android.app.Activity
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.widget.EditText
import com.squareup.okhttp.*
import org.jetbrains.anko.*
import java.net


public class MainActivity : Activity() {

    val ipStr = "192.168.2.100"
    var ipET: EditText? = null

    val portStr = "10000"
    var portET: EditText? = null


    val pwdStr = "12345"
    var pwdET: EditText? = null

    val CMD_ON = "<?xml version='1.0' encoding='UTF8'?><SMARTPLUG id='edimax'><CMD id='setup'><Device.System.Power.State>ON</Device.System.Power.State></CMD></SMARTPLUG>";
    val CMD_OFF = "<?xml version='1.0' encoding='UTF8'?><SMARTPLUG id='edimax'><CMD id='setup'><Device.System.Power.State>OFF</Device.System.Power.State></CMD></SMARTPLUG>";

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

            switch() {
                onCheckedChange { compoundButton, b ->
                    executeCommand(if (b) CMD_ON else CMD_OFF);
                }
                setText("Switch")
            }
        }.setPadding(dip(16), dip(16), dip(16), dip(16))
    }

    private fun executeCommand(cmd: String) {
        Thread(Runnable {
            val body = RequestBody.create(null, cmd);

            val client = OkHttpClient();

            client.setAuthenticator(auth())

            val request = Request.Builder()
                    .url("http://${ipET!!.getText()}:10000/smartplug.cgi")
                    .post(body)
                    .build();

            client.newCall(request).execute()
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

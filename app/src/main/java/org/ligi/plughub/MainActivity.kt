package org.ligi.plughub

import android.app.Activity
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.method.LinkMovementMethod
import android.util.Log
import android.widget.EditText
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

            switchCompatSupport() {
                onCheckedChange { compoundButton, b ->
                    executeCommand(if (b) EdiMaxCommands.CMD_ON else EdiMaxCommands.CMD_OFF);
                }
                setText("Switch")
            }
        }.setPadding(dip(16), dip(16), dip(16), dip(16))

        executeCommand(EdiMaxCommands.CMD_GET_STATE);
    }

    private fun executeCommand(cmd: String) {
        Thread(Runnable {
            val body = RequestBody.create(null, cmd);

            val client = OkHttpClient();

            client.setAuthenticator(auth())

            val request = Request.Builder()
                    .url("http://${ipET!!.getText()}:${portET!!.getText()}/smartplug.cgi")
                    .post(body)
                    .build();

            val response = client.newCall(request).execute()
            Log.i("PlugHub",response.body().string())
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

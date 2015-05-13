package org.ligi.plughub

public class EdiMaxCommands {
    companion object {
        val CMD_ON: String = wrapContainer(wrapCmd("setup", wrapPowerState("ON")));
        val CMD_OFF = wrapContainer(wrapCmd("setup", wrapPowerState("OFF")));
        val CMD_GET_STATE = wrapContainer(wrapCmd("get", wrapPowerState("")));
        val CMD_GET_POWER = wrapContainer(wrapCmd("get", "<NOW_POWER><Device.System.Power.NowCurrent></Device.System.Power.NowCurrent><Device.System.Power.NowPower></Device.System.Power.NowPower></NOW_POWER>"));

        fun wrapContainer(param: String): String {
            return "<?xml version='1.0' encoding='UTF8'?><SMARTPLUG id='edimax'>${param}</SMARTPLUG>"
        }

        fun wrapCmd(id: String, param: String): String {
            return "<CMD id='${id}'>${param}</CMD>"
        }

        fun wrapPowerState(param: String): String {
            return Tag("Device.System.Power.State").wrap(param)
        }

        public fun unwrap(tag: String,param: String): String? {
            val index = param.indexOf(Tag(tag).start())
            if (index < 0) {
                return null
            }
            return param.subSequence(index + Tag(tag).start().length(), param.indexOf( Tag(tag).end())).toString()
        }

        public fun unwrapPowerState(param: String): String? {
            return unwrap("Device.System.Power.State",param)
        }

        public fun unwrapNowCurrent(param: String): String? {
            return unwrap("Device.System.Power.NowCurrent",param)
        }


        public fun unwrapNowPower(param: String): String? {
            return unwrap("Device.System.Power.NowPower",param)
        }
    }

    class Tag(val tag: String) {
        fun start(): String {
            return "<${tag}>";
        }

        fun end(): String {
            return "</${tag}>";
        }

        fun wrap(content: String): String {
            return start() + content + end()
        }
    }
}
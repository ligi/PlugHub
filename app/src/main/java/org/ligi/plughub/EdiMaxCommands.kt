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
            return "<Device.System.Power.State>${param}</Device.System.Power.State>"
        }

        private val POWER_STATE_TAG_START = "<Device.System.Power.State>"
        private val POWER_STATE_TAG_END = "</Device.System.Power.State>"

        public fun unwrapPowerState(param: String): String? {
            val index = param.indexOf(POWER_STATE_TAG_START)
            if (index<0) {
                return null
            }
            return param.subSequence(index+POWER_STATE_TAG_START.length(),param.indexOf(POWER_STATE_TAG_END)).toString()
        }
    }
}
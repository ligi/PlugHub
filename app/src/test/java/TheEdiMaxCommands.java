import org.junit.Test;
import org.ligi.plughub.EdiMaxCommands;
import static org.assertj.core.api.Assertions.assertThat;

public class TheEdiMaxCommands {
    @Test
    public void testThatOnWorks() {
        assertThat(EdiMaxCommands.CMD_ON).isEqualTo("<?xml version='1.0' encoding='UTF8'?><SMARTPLUG id='edimax'><CMD id='setup'><Device.System.Power.State>ON</Device.System.Power.State></CMD></SMARTPLUG>");
    }

    @Test
    public void testThatOffWorks() {
        assertThat(EdiMaxCommands.CMD_OFF).isEqualTo("<?xml version='1.0' encoding='UTF8'?><SMARTPLUG id='edimax'><CMD id='setup'><Device.System.Power.State>OFF</Device.System.Power.State></CMD></SMARTPLUG>");
    }

    @Test
    public void testThatUnwrapOnWorks() {
        assertThat(EdiMaxCommands.Companion.unwrapPowerState("<Device.System.Power.State>ON</Device.System.Power.State>")).isEqualTo("ON");
    }

    @Test
    public void testThatUnwrapOffWorks() {
        assertThat(EdiMaxCommands.Companion.unwrapPowerState("<Device.System.Power.State>OFF</Device.System.Power.State>")).isEqualTo("OFF");
    }
}

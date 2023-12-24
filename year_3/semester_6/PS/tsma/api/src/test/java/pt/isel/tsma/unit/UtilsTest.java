package pt.isel.tsma.unit;

import org.junit.jupiter.api.Test;
import pt.isel.tsma.util.Utils;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UtilsTest {

	@Test
	public void isValidEmail_test() {
		assertTrue(Utils.isValidEmail("user@domain.com"));
		assertTrue(Utils.isValidEmail("a47220@alunos.isel.pt"));
		assertTrue(Utils.isValidEmail("47220@alunos.isel.ipl.pt"));
		assertTrue(Utils.isValidEmail("bruno.vidal@inetum-realdolmen.world"));
		assertTrue(Utils.isValidEmail("paulo.monteiro@inetum.com"));
		assertFalse(Utils.isValidEmail("test@"));
		assertFalse(Utils.isValidEmail("@test"));
		assertFalse(Utils.isValidEmail(null));
		assertFalse(Utils.isValidEmail(""));
		assertFalse(Utils.isValidEmail(" "));
		assertFalse(Utils.isValidEmail("test@test"));
	}
}

package edu.isel.lic.link;

public class M
{
    public static final int M_BUTTON = 0x80;

    // Verifica se o botão de manutenção está ligado
    public static boolean checkButton () {
        return HAL.isBit(M_BUTTON);
    }
}

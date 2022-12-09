package edu.isel.lic.link;

import isel.leic.UsbPort;

//INTERLOCK!!
public class COIN
{
    public static final int COIN_BUTTON = 0x40, accept_MASK = 0x40;

    // Retorna de imediato se o botão de COIN foi pressionado
    public static boolean buttonPress ()
    {
        boolean buttonPress = false;

        if (HAL.isBit(COIN_BUTTON)) {
            buttonPress = true;
            HAL.setBits(accept_MASK);
            HAL.clrBits(accept_MASK);
        }

        return buttonPress;
    }

    // Inicia a classe
    public static void init() {
        HAL.clrBits(accept_MASK);
    }
}

package edu.isel.lic.link;

import edu.isel.lic.link.sound.SoundGenerator;
import edu.isel.lic.peripherals.lcd.LCD;
import isel.leic.utils.Time;

public class SerialEmitter { // Envia tramas para os diferentes módulos Serial Receiver.

    private static final int SLCDC_SELECT_MASK = 0x08, SSC_SELECT_MASK = 0x10, DATA_MASK = 0x02;
    public static final int CLOCK_MASK = 0x04;

    public enum Destination {
        SLCD(SLCDC_SELECT_MASK), SSC(SSC_SELECT_MASK);

        private final int addr;

        Destination (int addr) {
            this.addr = addr;
        }
        public int getAddr () {
            return addr;
        }
    }

    // Usado para ativar testes específicos
    private static final boolean lcd_test = false;
    private static final boolean sound_test = true;

    // Para efeitos de teste
    public static void main (String[] args) {
        HAL.init();
        init();

        if (lcd_test) {
            send(Destination.SLCD, LCD.SERIAL_DATA_SIZE, 0b10101); //count of 1 bits = 3 | parity bit = 0
            Time.sleep(2000);
            send(Destination.SLCD, LCD.SERIAL_DATA_SIZE, 0b11000); //count of 1 bits = 2 | parity bit = 1
        }
        if (sound_test) {
            send(Destination.SSC, SoundGenerator.SERIAL_DATA_SIZE, 0b1010);
            Time.sleep(2000);
            send(Destination.SSC, SoundGenerator.SERIAL_DATA_SIZE, 0b1111);
            Time.sleep(2000);
            send(Destination.SSC, SoundGenerator.SERIAL_DATA_SIZE, 0b0001);
        }
    }

    // Inicia a classe
    public static void init() { HAL.setBits(Destination.SLCD.getAddr() | Destination.SSC.getAddr()); }

    // Envia uma trama para o SerialReceiver identificado por addr, com a dimensão de size e os bits de ‘data’.
    public static void send(Destination addr, int size, int data) {
        int select_mask = addr.getAddr(), bit_counter = 0;

        HAL.clrBits(select_mask);
        for (int current_bit = 0; current_bit < size; current_bit++) {
            if ((data & (1 << current_bit)) != 0) {
                HAL.setBits(DATA_MASK);
                bit_counter++;
            }
            else
                HAL.clrBits(DATA_MASK);
            clockPulse();
        }

        HAL.writeBits(DATA_MASK,parityCheck(bit_counter));
        clockPulse();
        HAL.setBits(select_mask);
    }

    // Envia um bit a '1' se o número de bits a '1' for par e envia um bit a '0' se o número de bits a '1' for ímpar
    private static int parityCheck(int bit_counter) {
        return (bit_counter % 2 == 0) ? DATA_MASK : 0;
    }

    // Simula um clock dado pela máscara.
    private static void clockPulse() {
        HAL.setBits(CLOCK_MASK);
        //Time.sleep(1);
        HAL.clrBits(CLOCK_MASK);
    }
}

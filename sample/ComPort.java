package sample;

import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortTimeoutException;

import static java.lang.Thread.*;

/**
 * Created by Dmitry on 31.01.2016.
 */
public class ComPort {

    //проверки на корректность записей
    CheckStatement Check = new CheckStatement();

    //  сериал порт
    SerialPort serialPort;

    String NamePort;
    String dateFromADC;
    boolean isPortConnection;


    //  инициализация ком порта
   public boolean initComPort(String Port)
   {
       NamePort = Port;
       serialPort = new SerialPort(Port);

       try {
           System.out.println("Port opened: " + serialPort.openPort());
           System.out.println("Params setted: " + serialPort.setParams(9600, 8, 1, 0));
           isPortConnection = true;
           return true;
       } catch (SerialPortException ex) {
           System.out.println("При попытке установить соединение с портом вышла ошибка" + ex);
           isPortConnection = false;
           return false;
       }

   }

    // закрыть порт, если он открыт
    public void closeComPort()
    {
        if(serialPort.isOpened())
        {
            try {
                isPortConnection = false;
                serialPort.closePort();
                System.out.println("Соединение разорвано");
            } catch (SerialPortException ex) {
                System.out.println("При попытке разорвать соединение с портом вышла ошибка" + ex);
            }
        } else {
                System.out.println("Соединение было разорвано");
        }



    }

    // получить данные от порта
    public String getDataFromADC()
    {
        byte sendt = 'd';
        try{
            serialPort.writeByte(sendt);

            try {
                sleep(5);
            } catch (InterruptedException ex) {
                System.out.println("Валится при попытке уснуть на 2 мс " + ex);
            }

            String recData = serialPort.readString(7, 500);
            System.out.println("Получено с порта - " + recData);
            dateFromADC = recData;

        } catch (RuntimeException ex){
            System.out.println("Валится тут 1 " + ex);
        } catch (SerialPortException ex) {
            System.out.println("Валится тут 2 " + ex);
        } catch (SerialPortTimeoutException ex) {
            System.out.println("Валится все таки тут 3 " + ex);
        }

        if(Check.isDigit(dateFromADC))
        {
            return dateFromADC;
        } else {
            return "0";
        }

    }


}

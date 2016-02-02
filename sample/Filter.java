package sample;

/**
 * Created by Dmitry on 31.01.2016.
 */

import java.util.*;
import java.math.*;

public class Filter {

    //  разрядность фильтра
    private int rangeFilter = 0;

    //  точек после запятой
    private int DigitAfterDot = 0;

    // инициированная коллекция
    private  ArrayList<Double> ListOfCode = new ArrayList<Double>();

    // среднее в коллекции
    private double averCollect;

     public void initFilter(int rangeFilter, int DigitAfterDot)
     {
        this.rangeFilter = rangeFilter;
        this.DigitAfterDot = DigitAfterDot;
        ListOfCode.clear();
        ListOfCode.add(0.0);
        System.out.println("0,1-Фильтр включен, р " + rangeFilter + "знаков ,.. " + DigitAfterDot + " Коллекция " + ListOfCode);
     }

     public double getAverData(double Data)
    {
        addDataToCollect(Data);
        averCollect = calcAverageCollect();
        averCollect = this.correctOutData(averCollect, DigitAfterDot);
        return averCollect;
    }

    // скорректировать значение
     private double correctOutData(double Data, int digits) {

        double DataToSend = Data;
        DataToSend = new BigDecimal(DataToSend).setScale(digits, RoundingMode.HALF_UP).doubleValue();
        System.out.println("4-Формат " + DataToSend);
        return DataToSend;
    }

    // добавить элемент в коллекцию
     private void addDataToCollect(double Data)
    {
        try {
            System.out.println("1- элементов " + ListOfCode.size() + " фильтр " + rangeFilter + " Коллекция " + ListOfCode);

            if (ListOfCode.size() >= rangeFilter) {
                ListOfCode.remove(0);
                ListOfCode.add(Data);
            } else ListOfCode.add(Data);

            System.out.println(" - " + ListOfCode.size() + " - " + rangeFilter + " - " + ListOfCode);
        } catch (RuntimeException ex){
            System.out.println(ListOfCode + " свалился" + ex);
        }
    }

    // вычисление среднего
     double calcAverageCollect()
    {
        try {
            double summD=0;
            for (Double ob : ListOfCode) {
                summD=summD + ob;
            }
            averCollect=summD / ListOfCode.size();
            System.out.println("3-Сумма " + summD + " Среднее " + String.format("%10.5f", averCollect));



        } catch(RuntimeException ex){
            System.out.println(ListOfCode + " свалился" + ex);
            return averCollect;
        }
        return averCollect;

    }



}

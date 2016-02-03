package sample;

/**
 * Created by Dmitry on 01.02.2016.
 */



public class SimulateRandomData {

    //проверки на корректность записей
    CheckStatement Check = new CheckStatement();

   //  генерировать строку
   public String getRandomData()
   {
       double data = Math.round(Math.random()*1000000)+1000000;
       int dataint = (int)data;
       String strData = String.valueOf(dataint);
       System.out.println(strData);

       if(Check.isDigit(strData))
       {
           return strData;
       } else {
           return "0";
       }
   }

}

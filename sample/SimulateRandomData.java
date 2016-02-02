package sample;

/**
 * Created by Dmitry on 01.02.2016.
 */
public class SimulateRandomData {

   //  генерировать строку
   public String getRandomData()
   {
       double data = Math.round(Math.random()*1000000)+1000000;
       int dataint = (int)data;
       String strData = String.valueOf(dataint);
       System.out.println(strData);

    return strData;
   }

}

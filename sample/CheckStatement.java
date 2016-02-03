package sample;

import javafx.fxml.FXML;

/**
 * Created by Dmitry on 01.02.2016.
 */
public class CheckStatement {


    public boolean isDigit(String text)
    {
        try {
            Integer.parseInt(text);
        } catch (Exception e) {
            return false;
        }
        if(text.isEmpty())
        {
            return false;
        }else {
            return true;
        }
    }

    public boolean isCOM(String Com)
    {
            if(Com.matches("COM.")){
                return true;
            }
            return false;
    }

    public int convToInt(String Data){

      boolean allowToconv;
      int convertData = 0;

        allowToconv = this.isDigit(Data);
        if(allowToconv){
            try {
                convertData = Integer.parseInt(Data);
            } catch (NumberFormatException e) {
                System.err.println("Неверный формат строки!");
            }
        } else {
            System.err.println("Неверный формат строки!");
        }
      return convertData;
    }








}

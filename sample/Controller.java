package sample;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.util.Duration;


import java.util.Date;

public class Controller {

 //-----------------------------------------------------
    // ГЛОБАЛЬНЫЕ НАСТРОЙКИ

    // фильтр
    Filter Filter = new Filter();

    // время
    Date date = new Date();

    // последовательный порт (из класса)
    ComPort Com = new ComPort();

    // генератор случайных чисел
    SimulateRandomData Rand = new SimulateRandomData();

    //проверки на корректность записей
    CheckStatement Check = new CheckStatement();

    // таймер для опроса ком порта
    Timeline timeline;


    double globalDataToCalibr = 0;

 /*
 *  Третья вкладка "Настройка"
 *
 * */

//--------------------------------------------------------------

    // Настройки последовательного порта

    private boolean isPortConnection = false;

    @FXML
    private ToggleButton SButtConPort;
    @FXML
    private TextField STxtCodePort;
    @FXML
    private TextField SComPortName;
    @FXML
    private TextField SPeriodReqPort;


    // подключение к порту
    @FXML
    public void connectComPort(ActionEvent event)
    {

        String NamePortID;
        String TimerMS;
        int iTimerMS = 0;
        if(SButtConPort.isSelected())
        {
            NamePortID = SComPortName.getText();
            TimerMS = SPeriodReqPort.getText();

            if(Check.isCOM(NamePortID)&&(Check.isDigit(TimerMS))){
                iTimerMS = Check.convToInt(TimerMS);
                isPortConnection = Com.initComPort(NamePortID);
            } else {
                isPortConnection = false;
                SButtConPort.setSelected(false);
                SComPortName.clear();
                SPeriodReqPort.clear();
            }
            if(isPortConnection)
            {
                this.runComPortThread(iTimerMS);
                SButtConPort.setSelected(true);
            } else {
                isPortConnection = false;
                SButtConPort.setSelected(false);
                SComPortName.clear();
                SPeriodReqPort.clear();
            }

        } else {
            Com.closeComPort();
            timeline.stop();
            isPortConnection = false;
            SComPortName.clear();
            SPeriodReqPort.clear();
        }
    }

    // метод коннекта к порту
    public void runComPortThread(int iTimerMS)
    {
        // запуск таймера для принятия данных с АЦП
        System.out.println("Запущен  таймлайн!");

        timeline = new Timeline(new KeyFrame(
                Duration.millis(iTimerMS),
                ae -> doSomething()));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    public void doSomething()
    {
        getDataFromADC(Rand.getRandomData());
        //  dataFromADC = Com.getDataFromADC();
    }




//---------------------------------------------------------------

    // обработка данных от АЦП
    public void getDataFromADC(String recDataFromADC)
    {
        try {
            STxtCodePort.setText(recDataFromADC);

            double dataToResolveFilter=0;
            double dataFormFilter=0;

            // анализ на наличие данных
            if (recDataFromADC.isEmpty()) {
                dataToResolveFilter=0;
            } else {
                dataToResolveFilter=Double.valueOf(recDataFromADC);
            }

            // фильтр перерасчет
            if (isFilterConnection) {
                System.out.println(" -фильтр- " + recDataFromADC);
                dataFormFilter=Filter.getAverData(dataToResolveFilter);
                SFTxtFilterCode.setText(String.format("%10.3f", dataFormFilter));
            } else {
                dataFormFilter=dataToResolveFilter;
                SFTxtFilterCode.setText("Не подключен");
            }

            globalDataToCalibr=dataFormFilter;
        } catch (RuntimeException exept){
            System.out.println(" -полученное- " + recDataFromADC + " - " + exept);
        }
    }

//---------------------------------------------------------------




//--------------------------------------------------------------

    // Настройки фильтра

    private boolean isFilterConnection = false;

    @FXML
    private ToggleButton SFButtConFilter;
    @FXML
    private TextField SFTxtGradeFilter;
    @FXML
    private TextField SFTxtRounding;
    @FXML
    private TextField SFTxtFilterCode;

    @FXML
    public void connectFilter()
    {

        String rangeFilterStr;
        int rangeFilterInt;
        String digitAfterDotStr;
        int digitAfterDotInt;

        if(SFButtConFilter.isSelected()){

            rangeFilterStr = SFTxtGradeFilter.getText();
            digitAfterDotStr = SFTxtRounding.getText();

            if(Check.isDigit(rangeFilterStr)&&Check.isDigit(digitAfterDotStr))
            {
                rangeFilterInt = Integer.valueOf(rangeFilterStr);
                digitAfterDotInt = Integer.valueOf(digitAfterDotStr);
                Filter.initFilter(rangeFilterInt, digitAfterDotInt);
                isFilterConnection = true;
            } else {
                SFTxtRounding.clear();
                SFTxtGradeFilter.clear();
                SFTxtFilterCode.clear();
                isFilterConnection = false;
                SFButtConFilter.setSelected(false);
            }
        } else {
            SFTxtRounding.clear();
            SFTxtGradeFilter.clear();
            SFTxtFilterCode.clear();
            isFilterConnection = false;
            SFButtConFilter.setSelected(false);
        }
    }


//--------------------------------------------------------------

/*
*       КАЛИБРОВКА
* */

//-----------------------------------------------------
    // 1

    boolean SDot1CalibrComplit = false;

    @FXML
    private TextField SDot1CalibrAnalog;
    @FXML
    private TextField SDot1CalibrData;
    @FXML
    private ToggleButton SButt1Calibr;
    @FXML
    public void set1DotCalibr()
    {
        String SDot1CalibrAnalogTxt;
        String SDot1CalibrDataTxt;
        boolean SDot1AccesToWrite = true;
        SDot1CalibrAnalogTxt = SDot1CalibrAnalog.getText();

        if(SButt1Calibr.isSelected()&&(Check.isDigit(SDot1CalibrAnalogTxt))){

            SDot1CalibrDataTxt = String.valueOf(globalDataToCalibr);
                // метод проверяющий доступность записи
                if(SDot1AccesToWrite){
                    // метод записывающий калибровку
                    SDot1CalibrData.setText(SDot1CalibrDataTxt);
                    SDot1CalibrComplit = true;
                } else {
                    SDot1CalibrAnalog.clear();
                    SButt1Calibr.setSelected(false);
                    SDotAllCalibrComplit = false;
                    SDot1CalibrComplit = false;
                    SDot1CalibrData.clear();
                }
            } else {
            SButt1Calibr.setSelected(false);
            SDotAllCalibrComplit = false;
            SDot1CalibrComplit = false;
            SDot1CalibrAnalog.clear();
            SDot1CalibrData.clear();
            //  метод очищающий калибровку
        }
    }

//-----------------------------------------------------
    // 2

    boolean SDot2CalibrComplit = false;

    @FXML
    private TextField SDot2CalibrAnalog;
    @FXML
    private TextField SDot2CalibrData;
    @FXML
    private ToggleButton SButt2Calibr;
    @FXML
    public void set2DotCalibr()
    {
        String SDot2CalibrAnalogTxt;
        String SDot2CalibrDataTxt;
        boolean SDot2AccesToWrite = true;
        SDot2CalibrAnalogTxt = SDot2CalibrAnalog.getText();

        if(SButt2Calibr.isSelected()&&(Check.isDigit(SDot2CalibrAnalogTxt))){

            SDot2CalibrDataTxt = String.valueOf(globalDataToCalibr);
            // метод проверяющий доступность записи
            if(SDot2AccesToWrite){
                // метод записывающий калибровку
                SDot2CalibrData.setText(SDot2CalibrDataTxt);
                SDot2CalibrComplit = true;
            } else {
                SDot2CalibrAnalog.clear();
                SButt2Calibr.setSelected(false);
                SDotAllCalibrComplit = false;
                SDot2CalibrComplit = false;
                SDot2CalibrData.clear();
            }
        } else {
            SButt2Calibr.setSelected(false);
            SDotAllCalibrComplit = false;
            SDot2CalibrComplit = false;
            SDot2CalibrAnalog.clear();
            SDot2CalibrData.clear();
            //  метод очищающий калибровку
        }
    }

//-----------------------------------------------------
    // 3

    boolean SDot3CalibrComplit = false;

    @FXML
    private TextField SDot3CalibrAnalog;
    @FXML
    private TextField SDot3CalibrData;
    @FXML
    private ToggleButton SButt3Calibr;
    @FXML
    public void set3DotCalibr()
    {
        String SDot3CalibrAnalogTxt;
        String SDot3CalibrDataTxt;
        boolean SDot3AccesToWrite = true;
        SDot3CalibrAnalogTxt = SDot3CalibrAnalog.getText();

        if(SButt3Calibr.isSelected()&&(Check.isDigit(SDot3CalibrAnalogTxt))){

            SDot3CalibrDataTxt = String.valueOf(globalDataToCalibr);
            // метод проверяющий доступность записи
            if(SDot3AccesToWrite){
                // метод записывающий калибровку
                SDot3CalibrData.setText(SDot3CalibrDataTxt);
                SDot3CalibrComplit = true;
            } else {
                SDot3CalibrAnalog.clear();
                SButt3Calibr.setSelected(false);
                SDotAllCalibrComplit = false;
                SDot3CalibrComplit = false;
                SDot3CalibrData.clear();
            }
        } else {
            SButt3Calibr.setSelected(false);
            SDotAllCalibrComplit = false;
            SDot3CalibrComplit = false;
            SDot3CalibrAnalog.clear();
            SDot3CalibrData.clear();
            //  метод очищающий калибровку
        }
    }

//-----------------------------------------------------
    // 4

    boolean SDot4CalibrComplit = false;

    @FXML
    private TextField SDot4CalibrAnalog;
    @FXML
    private TextField SDot4CalibrData;
    @FXML
    private ToggleButton SButt4Calibr;
    @FXML
    public void set4DotCalibr()
    {
        String SDot4CalibrAnalogTxt;
        String SDot4CalibrDataTxt;
        boolean SDot4AccesToWrite = true;
        SDot4CalibrAnalogTxt = SDot4CalibrAnalog.getText();

        if(SButt4Calibr.isSelected()&&(Check.isDigit(SDot4CalibrAnalogTxt))){

            SDot4CalibrDataTxt = String.valueOf(globalDataToCalibr);
            // метод проверяющий доступность записи
            if(SDot4AccesToWrite){
                // метод записывающий калибровку
                SDot4CalibrData.setText(SDot4CalibrDataTxt);
                SDot4CalibrComplit = true;
            } else {
                SDot4CalibrAnalog.clear();
                SButt4Calibr.setSelected(false);
                SDotAllCalibrComplit = false;
                SDot4CalibrComplit = false;
                SDot4CalibrData.clear();
            }
        } else {
            SButt4Calibr.setSelected(false);
            SDotAllCalibrComplit = false;
            SDot4CalibrComplit = false;
            SDot4CalibrAnalog.clear();
            SDot4CalibrData.clear();
            //  метод очищающий калибровку
        }
    }


//-----------------------------------------------------
    // 5

    boolean SDot5CalibrComplit = false;

    @FXML
    private TextField SDot5CalibrAnalog;
    @FXML
    private TextField SDot5CalibrData;
    @FXML
    private ToggleButton SButt5Calibr;
    @FXML
    public void set5DotCalibr()
    {
        String SDot5CalibrAnalogTxt;
        String SDot5CalibrDataTxt;
        boolean SDot5AccesToWrite = true;
        SDot5CalibrAnalogTxt = SDot5CalibrAnalog.getText();

        if(SButt5Calibr.isSelected()&&(Check.isDigit(SDot5CalibrAnalogTxt))){

            SDot5CalibrDataTxt = String.valueOf(globalDataToCalibr);
            // метод проверяющий доступность записи
            if(SDot5AccesToWrite){
                // метод записывающий калибровку
                SDot5CalibrData.setText(SDot5CalibrDataTxt);
                SDot5CalibrComplit = true;
            } else {
                SDot5CalibrAnalog.clear();
                SButt5Calibr.setSelected(false);
                SDotAllCalibrComplit = false;
                SDot5CalibrComplit = false;
                SDot5CalibrData.clear();
            }
        } else {
            SButt5Calibr.setSelected(false);
            SDotAllCalibrComplit = false;
            SDot5CalibrComplit = false;
            SDot5CalibrAnalog.clear();
            SDot5CalibrData.clear();
            //  метод очищающий калибровку
        }
    }

//-----------------------------------------------------

    // All

    boolean SDotAllCalibrComplit = false;

    @FXML
    private TextField SDotAllCalibrData;
    @FXML
    private ToggleButton SButtAllCalibr;
    @FXML
    public void confirmCalibr()
    {
        if(SButtAllCalibr.isSelected()){

          if(SDot5CalibrComplit && SDot4CalibrComplit && SDot3CalibrComplit && SDot2CalibrComplit && SDot1CalibrComplit)
          {
              //
              SDotAllCalibrComplit = true;
              SDotAllCalibrData.setText("Успешно");
          } else {
              SDotAllCalibrComplit = false;
              SButtAllCalibr.setSelected(false);
              SDotAllCalibrData.clear();
          }
        } else {
            SDotAllCalibrComplit = false;
            SButtAllCalibr.setSelected(false);
            SDotAllCalibrData.clear();
        }
    }










 /*
 *  Вторая вкладка "Измерение"
 *
 * */

//--------------------------------------------------------------

//--------------------------------------------------------------



}

class Format{
  //Returns date in [MM, DD, YYYY]
  public static String[] formatDate(String date, String format){
    String[] returnInfo = new String[3];
    String splitChar;
    String tempDate;
    int formatNum;
    if(strCheck(date, "/")){
      splitChar = "/";
    }
    else if(strCheck(date, "-")){
      splitChar = "-";
    }
    else{
      return new String[3];
    }
    try{
      String tempFormat;
      String format1;
      String format2;
      String format3;
      format1 = format.substring(0, format.indexOf(splitChar));
      tempFormat = format.substring(format.indexOf(splitChar)+1);
      format2 = tempFormat.substring(0, tempFormat.indexOf(splitChar));
      format3 = tempFormat.substring(tempFormat.indexOf(splitChar)+1);
      if(format1.indexOf("M") != -1){
        if(format2.indexOf("D") != -1){
          if(format3.indexOf("Y") != -1){
            formatNum = 0;
          }
          else{
            formatNum = -1;
          }
        }
        else if(format2.indexOf("Y") != -1){
          if(format3.indexOf("D") != -1){
            formatNum = 1;
          }
          else{
            formatNum = -1;
          }
        }
        else{
          formatNum = -1;
        }
      }
      else if(format1.indexOf("D") != -1){
        if(format2.indexOf("M") != -1){
          if(format3.indexOf("Y") != -1){
            formatNum = 2;
          }
          else{
            formatNum = -1;
          }
        }
        else if(format2.indexOf("Y") != -1){
          if(format3.indexOf("M") != -1){
            formatNum = 3;
          }
          else{
            formatNum = -1;
          }
        }
        else{
          formatNum = -1;
        }
      }
      else if(format1.indexOf("Y") != -1){
        if(format2.indexOf("D") != -1){
          if(format3.indexOf("M") != -1){
            formatNum = 4;
          }
          else{
            formatNum = -1;
          }
        }
        else if(format2.indexOf("M") != -1){
          if(format3.indexOf("D") != -1){
            formatNum = 5;
          }
          else{
            formatNum = -1;
          }
        }
        else{
          formatNum = -1;
        }
      }
      else{
        formatNum = -1;
      }
    }
    catch(Exception e){
      formatNum = -1;
    }
  
    //-1 invalid
    //0 MDY
    //1 MYD
    //2 DMY
    //3 DYM
    //4 YDM
    //5 YMD
    if(formatNum == -1){
      return new String[3];
    }
    try{
      tempDate = date.substring(date.indexOf(splitChar)+1);

      if((formatNum == 0) || (formatNum == 1)){
        //Month
        if((Integer.valueOf(date.substring(0, date.indexOf(splitChar))) <= 12) && (Integer.valueOf(date.substring(0, date.indexOf(splitChar))) > 0)){
          if(date.substring(0, date.indexOf(splitChar)).length() < 2){
            returnInfo[0] = "0" + date.substring(0, date.indexOf(splitChar));
          }
          else{
            returnInfo[0] = date.substring(0, date.indexOf(splitChar));
          }
        }
        else{
          return new String[3];
        }
        if(formatNum == 0){
          //Year
          if(tempDate.substring(tempDate.indexOf(splitChar)+1).length() <= 1){
            returnInfo[2] = java.time.LocalDate.now().toString().substring(0, java.time.LocalDate.now().toString().indexOf("-")-1) + tempDate.substring(tempDate.indexOf(splitChar)+1);
          }
          else if(tempDate.substring(tempDate.indexOf(splitChar)+1).length() <= 2){
            returnInfo[2] = java.time.LocalDate.now().toString().substring(0, java.time.LocalDate.now().toString().indexOf("-")-2) + tempDate.substring(tempDate.indexOf(splitChar)+1);
          }
          else if(tempDate.substring(tempDate.indexOf(splitChar)+1).length() <= 3){
            returnInfo[2] = java.time.LocalDate.now().toString().substring(0, java.time.LocalDate.now().toString().indexOf("-")-3) + tempDate.substring(tempDate.indexOf(splitChar)+1);
          }
          else{
            returnInfo[2] = tempDate.substring(tempDate.indexOf(splitChar)+1);
          }

          //Day
          if(checkDayOfMonth(tempDate.substring(0, tempDate.indexOf(splitChar)), returnInfo, splitChar)){
            if(tempDate.substring(0, tempDate.indexOf(splitChar)).length() < 2){
              returnInfo[1] = "0" + tempDate.substring(0, tempDate.indexOf(splitChar));
            }
            else{
              returnInfo[1] = tempDate.substring(0, tempDate.indexOf(splitChar));
            }
          }
          else{
            return new String[3];
          }
        }
        else if(formatNum == 1){
          //Year
          if(tempDate.substring(0, tempDate.indexOf(splitChar)).length() <= 1){
            returnInfo[2] = java.time.LocalDate.now().toString().substring(0, java.time.LocalDate.now().toString().indexOf("-")-1) + tempDate.substring(0, tempDate.indexOf(splitChar));
          }
          else if(tempDate.substring(0, tempDate.indexOf(splitChar)).length() <= 2){
            returnInfo[2] = java.time.LocalDate.now().toString().substring(0, java.time.LocalDate.now().toString().indexOf("-")-2) + tempDate.substring(0, tempDate.indexOf(splitChar));
          }
          else if(tempDate.substring(0, tempDate.indexOf(splitChar)).length() <= 3){
            returnInfo[2] = java.time.LocalDate.now().toString().substring(0, java.time.LocalDate.now().toString().indexOf("-")-3) + tempDate.substring(0, tempDate.indexOf(splitChar));
          }
          else{
            returnInfo[2] = tempDate.substring(0, tempDate.indexOf(splitChar));
          }

          //Day
          if(checkDayOfMonth(tempDate.substring(tempDate.indexOf(splitChar)+1), returnInfo, splitChar)){
            if(tempDate.substring(tempDate.indexOf(splitChar)+1).length() < 2){
              returnInfo[1] = "0" + tempDate.substring(tempDate.indexOf(splitChar)+1);
            }
            else{
              returnInfo[1] = tempDate.substring(tempDate.indexOf(splitChar)+1);
            }
          }
          else{
            return new String[3];
          }
        }
      }
      else if((formatNum == 2) || (formatNum == 3)){
        if(formatNum == 2){
          //Year
          if(tempDate.substring(tempDate.indexOf(splitChar)+1).length() <= 1){
            returnInfo[2] = java.time.LocalDate.now().toString().substring(0, java.time.LocalDate.now().toString().indexOf("-")-1) + tempDate.substring(tempDate.indexOf(splitChar)+1);
          }
          else if(tempDate.substring(tempDate.indexOf(splitChar)+1).length() <= 2){
            returnInfo[2] = java.time.LocalDate.now().toString().substring(0, java.time.LocalDate.now().toString().indexOf("-")-2) + tempDate.substring(tempDate.indexOf(splitChar)+1);
          }
          else if(tempDate.substring(tempDate.indexOf(splitChar)+1).length() <= 3){
            returnInfo[2] = java.time.LocalDate.now().toString().substring(0, java.time.LocalDate.now().toString().indexOf("-")-3) + tempDate.substring(tempDate.indexOf(splitChar)+1);
          }
          else{
            returnInfo[2] = tempDate.substring(tempDate.indexOf(splitChar)+1);
          }
          //Month
          if((Integer.valueOf(tempDate.substring(0, tempDate.indexOf(splitChar))) <= 12) && (Integer.valueOf(tempDate.substring(0, tempDate.indexOf(splitChar))) > 0)){
            if(tempDate.substring(0, tempDate.indexOf(splitChar)).length() < 2){
              returnInfo[0] = "0" + tempDate.substring(0, tempDate.indexOf(splitChar));
            }
            else{
              returnInfo[0] = tempDate.substring(0, tempDate.indexOf(splitChar));
            }
          }
          else{
            return new String[3];
          }
        }
        else if(formatNum == 3){
          //Year
          if(tempDate.substring(0, tempDate.indexOf(splitChar)).length() <= 1){
            returnInfo[2] = java.time.LocalDate.now().toString().substring(0, java.time.LocalDate.now().toString().indexOf("-")-1) + tempDate.substring(0, tempDate.indexOf(splitChar));
          }
          else if(tempDate.substring(0, tempDate.indexOf(splitChar)).length() <= 2){
            returnInfo[2] = java.time.LocalDate.now().toString().substring(0, java.time.LocalDate.now().toString().indexOf("-")-2) + tempDate.substring(0, tempDate.indexOf(splitChar));
          }
          else if(tempDate.substring(0, tempDate.indexOf(splitChar)).length() <= 3){
            returnInfo[2] = java.time.LocalDate.now().toString().substring(0, java.time.LocalDate.now().toString().indexOf("-")-3) + tempDate.substring(0, tempDate.indexOf(splitChar));
          }
          else{
            returnInfo[2] = tempDate.substring(0, tempDate.indexOf(splitChar));
          }
          //Month
          if((Integer.valueOf(tempDate.substring(tempDate.indexOf(splitChar)+1)) <= 12) && (Integer.valueOf(tempDate.substring(tempDate.indexOf(splitChar)+1)) > 0)){
            if(tempDate.substring(tempDate.indexOf(splitChar)+1).length() < 2){
              returnInfo[0] = "0" + tempDate.substring(tempDate.indexOf(splitChar)+1);
            }
            else{
              returnInfo[0] = tempDate.substring(tempDate.indexOf(splitChar)+1);
            }
          }
          else{
            return new String[3];
          }
        }
        //Day
        if(checkDayOfMonth(date.substring(0, date.indexOf(splitChar)), returnInfo, splitChar)){
          if(date.substring(0, date.indexOf(splitChar)).length() < 2){
            returnInfo[1] = "0" + date.substring(0, date.indexOf(splitChar));
          }
          else{
            returnInfo[1] = date.substring(0, date.indexOf(splitChar));
          }
        }
        else{
          return new String[3];
        }
      }
      else if((formatNum == 4) || (formatNum == 5)){
        //Year
        if(date.substring(0, date.indexOf(splitChar)).length() <= 1){
          returnInfo[2] = java.time.LocalDate.now().toString().substring(0, java.time.LocalDate.now().toString().indexOf("-")-1) + date.substring(0, date.indexOf(splitChar));
        }
        else if(date.substring(0, date.indexOf(splitChar)).length() <= 2){
          returnInfo[2] = java.time.LocalDate.now().toString().substring(0, java.time.LocalDate.now().toString().indexOf("-")-2) + date.substring(0, date.indexOf(splitChar));
        }
        else if(date.substring(0, date.indexOf(splitChar)).length() <= 3){
          returnInfo[2] = java.time.LocalDate.now().toString().substring(0, java.time.LocalDate.now().toString().indexOf("-")-3) + date.substring(0, date.indexOf(splitChar));
        }
        else{
          returnInfo[2] = date.substring(0, date.indexOf(splitChar));
        }
        if(formatNum == 4){
          //Month
          if((Integer.valueOf(tempDate.substring(tempDate.indexOf(splitChar)+1)) <= 12) && (Integer.valueOf(tempDate.substring(tempDate.indexOf(splitChar)+1)) > 0)){
            if(tempDate.substring(tempDate.indexOf(splitChar)+1).length() < 2){
              returnInfo[0] = "0" + tempDate.substring(tempDate.indexOf(splitChar)+1);
            }
            else{
              returnInfo[0] = tempDate.substring(tempDate.indexOf(splitChar)+1);
            }
          }
          else{
            return new String[3];
          }
          
          //Day
          if(checkDayOfMonth(tempDate.substring(0, tempDate.indexOf(splitChar)), returnInfo, splitChar)){
            if(tempDate.substring(0, tempDate.indexOf(splitChar)).length() < 2){
              returnInfo[1] = "0" + tempDate.substring(0, tempDate.indexOf(splitChar));
            }
            else{
              returnInfo[1] = tempDate.substring(0, tempDate.indexOf(splitChar));
            }
          }
          else{
            return new String[3];
          }
        }
        if(formatNum == 5){
          //Month
          if((Integer.valueOf(tempDate.substring(0, tempDate.indexOf(splitChar))) <= 12) && (Integer.valueOf(tempDate.substring(0, tempDate.indexOf(splitChar))) > 0)){
            if(tempDate.substring(0, tempDate.indexOf(splitChar)).length() < 2){
              returnInfo[0] = "0" + tempDate.substring(0, tempDate.indexOf(splitChar));
            }
            else{
              returnInfo[0] = tempDate.substring(0, tempDate.indexOf(splitChar));
            }
          }
          else{
            return new String[3];
          }
          
          //Day
          if(checkDayOfMonth(tempDate.substring(tempDate.indexOf(splitChar)+1), returnInfo, splitChar)){
            if(tempDate.substring(tempDate.indexOf(splitChar)+1).length() < 2){
              returnInfo[1] = "0" + tempDate.substring(tempDate.indexOf(splitChar)+1);
            }
            else{
              returnInfo[1] = tempDate.substring(tempDate.indexOf(splitChar)+1);
            }
          }
          else{
            return new String[3];
          }
        }
      }      
      return returnInfo;
    }
    catch(Exception e){
      return new String[3];
    }
  }

  //Returns time in [HH, MM, SS]
  public static String[] formatTime(String time, String format){
    String[] result = new String[3];
    int formatNum;
    String splitChar = ":";
    if((findReoccurance(format, splitChar.charAt(0)) == 1) && (findReoccurance(time, splitChar.charAt(0)) == 1)){
      formatNum = 0;
    }
    else{
      return new String[3];
    }

    try{
      String tempFormat;
      String format1;
      String format2;

      if(formatNum == 0){
        format1 = format.substring(0, format.indexOf(splitChar));
        format2 = format.substring(format.indexOf(splitChar)+1);
        if(format1.indexOf("H") != -1){
          if(format2.indexOf("M") != -1){
            formatNum = 1;
          }
          else if(format2.indexOf("S") != -1){
            formatNum = 2;
          }
          else{
            formatNum = -1;
          }
        }
        else if(format1.indexOf("M") != -1){
          if(format2.indexOf("H") != -1){
            formatNum = 3;
          }
          else if(format2.indexOf("S") != -1){
            formatNum = 4;
          }
          else{
            formatNum = -1;
          }
        }
        else if(format1.indexOf("S") != -1){
          if(format2.indexOf("H") != -1){
            formatNum = 5;
          }
          else if(format2.indexOf("M") != -1){
            formatNum = 6;
          }
          else{
            formatNum = -1;
          }
        }
        else{
          formatNum = -1;
        }
      }
      else{
        result = new String[3];
        formatNum = -1;
      }

      //-1 invalid
      //1 HM
      //2 HS
      //3 MH
      //4 MS
      //5 SH
      //6 SM
      if(formatNum == -1){
        return new String[3];
      }
      else if(formatNum == 1 || formatNum == 2){
        //Hour
        if((Integer.parseInt(time.substring(0, time.indexOf(splitChar))) >= 0) && (Integer.parseInt(time.substring(0, time.indexOf(splitChar))) < 24)){
          if(time.substring(0, time.indexOf(splitChar)).length() < 2){
            result[0] = "0" + time.substring(0, time.indexOf(splitChar));
          }
          else{
            result[0] = time.substring(0, time.indexOf(splitChar));
          }
        }
        else{
          return new String[3];
        }
        //Minute + Second
        if(formatNum == 1){
          if((Integer.parseInt(time.substring(time.indexOf(splitChar)+1)) >= 0) && (Integer.parseInt(time.substring(time.indexOf(splitChar)+1)) < 60)){
            if(time.substring(time.indexOf(splitChar)+1).length() < 2){
              result[1] = "0" + time.substring(time.indexOf(splitChar)+1);
            }
            else{
              result[1] = time.substring(time.indexOf(splitChar)+1);
            }
          }
          else{
            return new String[3];
          }
          result[2] = "00";
        }
        //Second + Minute
        else if(formatNum == 2){
          if((Integer.parseInt(time.substring(time.indexOf(splitChar)+1)) >= 0) && (Integer.parseInt(time.substring(time.indexOf(splitChar)+1)) < 60)){
            if(time.substring(time.indexOf(splitChar)+1).length() < 2){
              result[2] = "0" + time.substring(time.indexOf(splitChar)+1);
            }
            else{
              result[2] = time.substring(time.indexOf(splitChar)+1);
            }
          }
          else{
            return new String[3];
          }
          result[1] = "00";
        }
      }
      else if(formatNum == 3 || formatNum == 4){
        //Minute
        if((Integer.parseInt(time.substring(0, time.indexOf(splitChar))) >= 0) && (Integer.parseInt(time.substring(0, time.indexOf(splitChar))) < 60)){
          if(time.substring(0, time.indexOf(splitChar)).length() < 2){
            result[1] = "0" + time.substring(0, time.indexOf(splitChar));
          }
          else{
            result[1] = time.substring(0, time.indexOf(splitChar));
          }
        }
        else{
          return new String[3];
        }
        //Hour + Second
        if(formatNum == 3){
          if((Integer.parseInt(time.substring(time.indexOf(splitChar)+1)) >= 0) && (Integer.parseInt(time.substring(time.indexOf(splitChar)+1)) < 24)){
            if(time.substring(time.indexOf(splitChar)+1).length() < 2){
              result[0] = "0" + time.substring(time.indexOf(splitChar)+1);
            }
            else{
              result[0] = time.substring(time.indexOf(splitChar)+1);
            }
          }
          else{
            return new String[3];
          }
          result[2] = "00";
        }
        //Second + Hour
        if(formatNum == 4){
          if((Integer.parseInt(time.substring(time.indexOf(splitChar)+1)) >= 0) && (Integer.parseInt(time.substring(time.indexOf(splitChar)+1)) < 60)){
            if(time.substring(time.indexOf(splitChar)+1).length() < 2){
              result[2] = "0" + time.substring(time.indexOf(splitChar)+1);
            }
            else{
              result[2] = time.substring(time.indexOf(splitChar)+1);
            }
          }
          else{
            return new String[3];
          }
          result[0] = "00";
        }
      }
      else if(formatNum == 5 || formatNum == 6){
        //Second
        if((Integer.parseInt(time.substring(0, time.indexOf(splitChar))) >= 0) && (Integer.parseInt(time.substring(0, time.indexOf(splitChar))) < 60)){
          if(time.substring(0, time.indexOf(splitChar)).length() < 2){
            result[2] = "0" + time.substring(0, time.indexOf(splitChar));
          }
          else{
            result[2] = time.substring(0, time.indexOf(splitChar));
          }
        }
        else{
          return new String[3];
        }
        //Hour + Minute (Incomplete)
        if(formatNum == 5){
          if((Integer.parseInt(time.substring(time.indexOf(splitChar)+1)) >= 0) && (Integer.parseInt(time.substring(time.indexOf(splitChar)+1)) < 24)){
            if(time.substring(time.indexOf(splitChar)+1).length() < 2){
              result[0] = "0" + time.substring(time.indexOf(splitChar)+1);
            }
            else{
              result[0] = time.substring(time.indexOf(splitChar)+1);
            }
          }
          else{
            return new String[3];
          }
          result[1] = "00";
        }
        //Minute + Hour
        else if(formatNum == 6){
          if((Integer.parseInt(time.substring(time.indexOf(splitChar)+1)) >= 0) && (Integer.parseInt(time.substring(time.indexOf(splitChar)+1)) < 60)){
            if(time.substring(time.indexOf(splitChar)+1).length() < 2){
              result[1] = "0" + time.substring(time.indexOf(splitChar)+1);
            }
            else{
              result[1] = time.substring(time.indexOf(splitChar)+1);
            }
          }
          else{
            return new String[3];
          }
          result[0] = "00";
        }
      }

      return result;
    }
    catch(Exception e){
      return new String[3];
    }
  }

  private static boolean checkDayOfMonth(String temporary, String[] returnInformation, String splitChar){
    return ((Integer.valueOf(temporary) <= 31) && (Integer.valueOf(temporary) > 0) && (returnInformation[0].equals("01") || returnInformation[0].equals("03") || returnInformation[0].equals("05") || returnInformation[0].equals("07") || returnInformation[0].equals("08") || returnInformation[0].equals("10") || returnInformation[0].equals("12"))) ||
           ((Integer.valueOf(temporary) <= 30) && (Integer.valueOf(temporary) > 0) && (returnInformation[0].equals("04") || returnInformation[0].equals("06") || returnInformation[0].equals("09") || returnInformation[0].equals("11"))) ||
          ((Integer.valueOf(temporary) <= 28) && (Integer.valueOf(temporary) > 0) && (returnInformation[0].equals("02"))) ||
          ((Integer.valueOf(temporary) <= 29) && (Integer.valueOf(temporary) > 0) && (returnInformation[0].equals("02")) && (Integer.valueOf(returnInformation[2])%4 == 0));
  }

  private static int findReoccurance(String str, char character){
    int finalNum = 0;
    for(int i = 0; i<str.length(); i++){
      if(str.charAt(i) == character){
        finalNum++;
      }
    }
    return finalNum;
  }

  private static boolean strCheck(String original, String checkChar){
    if(original.indexOf(checkChar) != -1)
      return true;
    return false;
  }
}
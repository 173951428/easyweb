package com.wf.ew.common.utils.excelUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.util.NumberToTextConverter;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
public class PramTypeJudgeUtil {
	 /**
     * 对象转String
     * @param param
     * @return
     */
    public static String obj2Str(Object param)
    {
        String res = null;
        if(param == null)
            res = null;
        else if(param instanceof Integer)
            res = ((Integer)param).toString();
        else
        if(param instanceof String)
            res = param.toString();
        else
        if(param instanceof Double)
            res = ((Double)param).toString();
        else
        if(param instanceof Float)
            res = ((Float)param).toString();
        else
        if(param instanceof Long)
            res = ((Long)param).toString();
        else
        if(param instanceof Boolean)
            res = ((Boolean)param).booleanValue() ? "true" : "false";
        else
        if(param instanceof Date)
        {
            Date d = (Date)param;
            DateFormat dt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            res = dt.format(d);
        } else
        {
            res = param.toString();
        }
        return res;
    }

    /**
     * 获取单元格类型，并转java类型
     * @param cell
     * @return
     */
    public static Class getTypeClass(Cell cell)
    {
        switch(cell.getCellType())
        {
            case Cell.CELL_TYPE_NUMERIC: // '\0'
                short format = cell.getCellStyle().getDataFormat();
                if(format == 14 || format == 31 || format == 57 || format == 58)
                    return Date.class;
                if(DateUtil.isCellDateFormatted(cell))
                    return Date.class;
                else
                    return Long.class;

            case Cell.CELL_TYPE_STRING: // '\001'
                return String.class;

            case Cell.CELL_TYPE_BLANK: // '\003'
                return Object.class;

            case Cell.CELL_TYPE_FORMULA: // '\002'
            default:
                return Object.class;
        }
    }

    /**
     * 获取单元格的值
     * @param cell
     * @param paraType
     * @return
     */
    public <T> T getCellValue(Cell cell, String paraType,T t)
    {
        Object cellvalue = null;
        if(cell !=null && paraType!=null){
            String val = getCellStringValue(cell);
            if(cell != null)
            {
                switch(paraType)
                {
                    default:
                        break;

                    case "class java.util.Date":
                       if(val.length() >= 8)
                          cellvalue = DateConvertHelper.StringToDate(val);
                       else
                          cellvalue = null;
                        break;

                    case "class java.math.BigDecimal":

                        if(val.length() > 0)
                            cellvalue = new BigDecimal(val);
                        else
                            cellvalue = new BigDecimal("0");
                        break;

                    case "class java.lang.Long":
                        cellvalue=Long.parseLong(val);
                        break;
                }
                cellvalue = val;
            } else
            {
                cellvalue = "";
            }
        }

        return (T)cellvalue;
    }

    /**
     * 获取单元格String类型的值
     * @param cell
     * @return
     */
    public static String getCellStringValue(Cell cell)
    {
        String cellvalue = "";
        if(cell != null)
            switch(cell.getCellType())
            {
                case Cell.CELL_TYPE_NUMERIC:
                    short format = cell.getCellStyle().getDataFormat();
                    if(format == 14 || format == 31 || format == 57 || format == 58)
                    {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        double value = cell.getNumericCellValue();
                        Date date = DateUtil.getJavaDate(value);
                        cellvalue = sdf.format(date);
                    } else
                    if(DateUtil.isCellDateFormatted(cell))
                    {
                        Date date = cell.getDateCellValue();
                        DateFormat formater = new SimpleDateFormat("yyyy-MM-dd");
                        cellvalue = formater.format(date);
                    } else
                    {
                        cellvalue = NumberToTextConverter.toText(cell.getNumericCellValue());
                    }
                    break;

                case Cell.CELL_TYPE_STRING:
                    cellvalue = cell.getStringCellValue().replaceAll("'", "''");
                    break;

                case Cell.CELL_TYPE_BLANK:
                    cellvalue = null;
                    break;

                case Cell.CELL_TYPE_FORMULA:
                default:
                    cellvalue = "";
                    break;
            }
        else
            cellvalue = "";
        return cellvalue;
    }

    /**
     * String 转BigDecimal
     * @param str
     * @return
     */
    public static BigDecimal str2BigDecimal(String str)
    {
        BigDecimal bd = null;
        if(str != null && str.length() > 0)
            try
            {
                bd = new BigDecimal(str);
            }
            catch(Exception e)
            {
                bd = new BigDecimal(0);
            }
        return bd;
    }
}

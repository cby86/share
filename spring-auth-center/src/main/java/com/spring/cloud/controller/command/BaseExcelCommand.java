package com.spring.cloud.controller.command;

import com.spring.cloud.utils.JodaTimeUtils;
import org.apache.poi.ss.usermodel.Workbook;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;

/**
 * @author panyuanjun
 * @date 2018/4/19 16:51
 */
public abstract class BaseExcelCommand {

    private HttpServletResponse response;

    private String excelName;

    protected List commandList;

    public BaseExcelCommand(HttpServletResponse response, String excelName, List commandList) {
        this.response = response;
        this.excelName = excelName;
        this.commandList = commandList;
    }

    protected void setResponseHeader() {
        response.setContentType("application/octet-stream;charset=ISO8859-1");
        try {
            String fineName = new String(excelName.getBytes("gb2312"), "ISO8859-1");
            response.setHeader("Content-Disposition", "attachment;filename=" + fineName +
                    URLEncoder.encode(JodaTimeUtils.convertToTime(new Date(), "yyyyMMddHHmmss") + ".xls", "ISO8859-1"));
            response.setHeader("fileName", URLEncoder.encode(excelName +
                    JodaTimeUtils.convertToTime(new Date(), "yyyyMMddHHmmss") + ".xls", "UTF-8"));
        } catch (Exception e) {
            throw new UnsupportedOperationException("文件处理错误!");
        }
    }

    protected Workbook toWorkbook() {
        throw new UnsupportedOperationException("不支持该操作");
    }

    public void export(){
        try {
            Workbook workbook = this.toWorkbook();
            OutputStream outStream = response.getOutputStream();
            workbook.write(outStream);
            outStream.flush();
            outStream.close();
        } catch (IOException e) {
            throw new UnsupportedOperationException("文件处理错误!");
        }
    }

}

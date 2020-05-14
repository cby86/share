package com.spring.cloud.controller.command;

import com.spring.cloud.entity.Driver;
import com.spring.cloud.utils.JodaTimeUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author panyuanjun
 * @date 2018/4/27 11:08
 */
public class DriverExportCommand extends BaseExcelCommand {

    public DriverExportCommand(HttpServletResponse response, String excelName, List commandList) {
        super(response, excelName, commandList);
    }

    @Override
    protected Workbook toWorkbook() {
        this.setResponseHeader();
        Workbook workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet("司机数据");
        sheet.setDefaultColumnWidth(15);
        Row row = sheet.createRow(0);

//        String[] title = {"司机身份证号","司机姓名", "地区", "从业类别","从业类别全称", "初领日期", "有效期起", "截至日期","手机","公司名称","当前状态","考试状态"};
        String[] title = {"司机身份证号", "地区", "从业类别","从业类别全称", "初领日期", "有效期起", "截至日期","公司名称","当前状态","考试状态"};
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HSSFCellStyle.ALIGN_LEFT); // 创建一个居中格式
        for (int i = 0; i < title.length; i++) {
            Cell cell = row.createCell(i);
            cell.setCellValue(title[i]);
            cell.setCellStyle(style);
        }
        List<Driver> commands = this.commandList;
        if (commands != null && !commands.isEmpty()) {
            for (int i = 0; i < commands.size(); i++) {
                Driver command = commands.get(i);

                Row row1 = sheet.createRow(i + 1);
                Cell cell = row1.createCell(0);
                cell.setCellValue(command.getCardNumber());
                cell.setCellStyle(style);

//                cell = row1.createCell(1);
//                cell.setCellValue(command.getDriverName());
//                cell.setCellStyle(style);

                cell = row1.createCell(1);
                cell.setCellValue(command.getArea());
                cell.setCellStyle(style);

                cell = row1.createCell(2);
                cell.setCellValue(command.getName());
                cell.setCellStyle(style);
                cell = row1.createCell(3);
                cell.setCellValue(command.getFullName());
                cell.setCellStyle(style);

                cell = row1.createCell(4);
                if (command.getFirstDate() != null) {
                    cell.setCellValue(JodaTimeUtils.convertToTime(command.getFirstDate(),"yyyy-MM-dd"));
                }
                cell.setCellStyle(style);

                cell = row1.createCell(5);
                if (command.getBeginDate() != null) {
                    cell.setCellValue(JodaTimeUtils.convertToTime(command.getBeginDate(),"yyyy-MM-dd"));
                }
                cell.setCellStyle(style);

                cell = row1.createCell(6);
                if (command.getEndDate() != null) {
                    cell.setCellValue(JodaTimeUtils.convertToTime(command.getEndDate(),"yyyy-MM-dd"));
                }
                cell.setCellStyle(style);

//                cell = row1.createCell(8);
//                cell.setCellValue(command.getMobile());
//                cell.setCellStyle(style);

                cell = row1.createCell(7);
                cell.setCellValue(command.getCompany());
                cell.setCellStyle(style);
                cell = row1.createCell(8);
                cell.setCellValue(command.getCurrentStatus());
                cell.setCellStyle(style);

                cell = row1.createCell(9);
                cell.setCellValue(command.getNextStatus());
                cell.setCellStyle(style);
            }
        }

        return workbook;
    }
}

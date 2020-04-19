package com.spring.cloud.controller.command;

import com.spring.cloud.entity.Car;
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
public class CarExportCommand extends BaseExcelCommand {

    public CarExportCommand(HttpServletResponse response, String excelName, List commandList) {
        super(response, excelName, commandList);
    }

    @Override
    protected Workbook toWorkbook() {
        this.setResponseHeader();
        Workbook workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet("司机数据");
        Row row = sheet.createRow(0);
        String[] title = {"车牌号","公司", "联系电话", "发证地区","编号", "从业类别全称", "公司地址", "品牌","颜色","座位","审核状态"};
        CellStyle style = workbook.createCellStyle();
        style.setWrapText(true);
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式
        for (int i = 0; i < title.length; i++) {
            Cell cell = row.createCell(i);
            cell.setCellValue(title[i]);
            cell.setCellStyle(style);
        }
        List<Car> commands = this.commandList;
        if (commands != null && !commands.isEmpty()) {
            for (int i = 0; i < commands.size(); i++) {
                Car command = commands.get(i);

                Row row1 = sheet.createRow(i + 1);
                Cell cell = row1.createCell(0);
                cell.setCellValue(command.getCarNumber());
                cell.setCellStyle(style);

                cell = row1.createCell(1);
                cell.setCellValue(command.getCompany());
                cell.setCellStyle(style);

                cell = row1.createCell(2);
                cell.setCellValue(command.getLxdh());
                cell.setCellStyle(style);

                cell = row1.createCell(3);
                cell.setCellValue(command.getArea());
                cell.setCellStyle(style);
                cell = row1.createCell(4);
                cell.setCellValue(command.getYy_dlyszh());
                cell.setCellStyle(style);

                cell = row1.createCell(5);
                cell.setCellValue(command.getYy_jyfw());
                cell.setCellStyle(style);

                cell = row1.createCell(6);
                cell.setCellValue(command.getAddress());
                cell.setCellStyle(style);

                cell = row1.createCell(7);
                cell.setCellValue(command.getBand());
                cell.setCellStyle(style);

                cell = row1.createCell(8);
                cell.setCellValue(command.getColor());
                cell.setCellStyle(style);

                cell = row1.createCell(9);
                cell.setCellValue(command.getSeat());
                cell.setCellStyle(style);
                cell = row1.createCell(10);
                if (command.getAduitStatus().equals("1")) {
                    cell.setCellValue("出管所终审通过可打印道路运输证");
                }
                cell.setCellStyle(style);
            }
        }

        return workbook;
    }
}

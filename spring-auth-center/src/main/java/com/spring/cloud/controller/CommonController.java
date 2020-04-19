package com.spring.cloud.controller;

import com.alibaba.fastjson.JSONObject;
import com.spring.cloud.base.BaseController;
import com.spring.cloud.entity.User;
import com.spring.cloud.service.UserService;
import com.spring.cloud.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/")
public class CommonController extends BaseController{
    @RequestMapping("/file/download")
    public void loadDrivers( HttpServletResponse response) throws IOException {
        response.setContentType("application/force-download");// 设置强制下载不打开
        response.addHeader("Content-Disposition", "attachment;fileName=import_template.xlsx");// 设置文件名
        response.addHeader("fileName", "import_template.xlsx");// 设置文件名
        byte[] buffer = new byte[1024];
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        try {
            bis = new BufferedInputStream(this.getClass().getClassLoader().getResourceAsStream("import_template.xlsx"));
            OutputStream os = response.getOutputStream();
            int i = bis.read(buffer);
            while (i != -1) {
                os.write(buffer, 0, i);
                i = bis.read(buffer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Autowired
    UserService userService;
    @RequestMapping("/userInfo")
    public Map<String, Object> userInfo() throws IOException {
        User user = SecurityUtils.currentUser();
        User userInDb=userService.findUser(user.getId());
        HashMap<Object, Object> data = new HashMap<>();
        data.put("times", userInDb.getTimes());
        return this.resultMap(data);
    }

}

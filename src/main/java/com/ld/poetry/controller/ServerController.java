package com.ld.poetry.controller;

import com.ld.poetry.config.LoginCheck;
import com.ld.poetry.config.UResult;
import com.ld.poetry.entity.vo.SysUserOnline;
import com.ld.poetry.service.ServerService;
import com.ld.poetry.utils.system.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @BelongsProject: UBlog
 * @BelongsPackage: com.ld.poetry.controller
 * @ClassName ServerController
 * @Author: Utopia
 * @Description: TODO
 * @Version: 1.0
 */


@RestController
@RequestMapping("/api/monitor")
public class ServerController {

    @Autowired
    private ServerService serverService;

    /**
     * 服务监控
     * @return
     * @throws Exception
     */
    @GetMapping("/server")
    public UResult getMonitorServer() throws Exception {
        Server server = new Server();
        server.copyTo();
        return UResult.success(server);
    }

    @GetMapping("/online/list")
    public UResult getServer() throws Exception {
        List<SysUserOnline> sys= serverService.getUserList();

        return UResult.success(sys);
    }


}

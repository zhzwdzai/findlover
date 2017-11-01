package com.hpe.findlover.contoller.front;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hpe.findlover.model.UserBasic;
import com.hpe.findlover.model.VisitTrace;
import com.hpe.findlover.service.UserService;
import com.hpe.findlover.service.VisitTraceService;
import com.hpe.findlover.util.Constant;
import com.hpe.findlover.util.LoverUtil;
import com.hpe.findlover.util.SessionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


/**
 * @author sinnamm
 * @Date Create in  2017/11/1.
 */
@Controller
@RequestMapping("visit_trace")
public class VisitTraceController {

   @Autowired
   private VisitTraceService visitTraceService;
   @Autowired
   private UserService userService;

   private Logger logger = LogManager.getLogger(VisitTraceController.class);

   @GetMapping
   public String visitTrace(Model model){
       List<UserBasic> userBasics = LoverUtil.getRandomUser(userService.selectAll(),4);
       for(UserBasic userBasicl:userBasics){
           userBasicl.setAge(LoverUtil.getAge(userBasicl.getBirthday()));
       }
       model.addAttribute("users",userBasics);
       return "front/visit_trace";
   }

    @GetMapping("trace")
    @ResponseBody
    public PageInfo getVisitTrace(HttpServletRequest request,@RequestParam("pageNum")int pageNum){
        logger.info("我看过谁");
       Integer userId = SessionUtils.getSessionAttr(request,"user", UserBasic.class).getId();
        PageHelper.startPage(pageNum,6);
        List<VisitTrace> visitTraces = visitTraceService.selectVisitTrace(userId);
       for(VisitTrace visitTrace:visitTraces){
           userService.userAttrHandler(visitTrace.getUserBasic());
       }
        visitTraces.forEach(logger::info);
        PageInfo pageInfo = new PageInfo(visitTraces);
        return pageInfo;
    }
    @GetMapping("tracer")
    @ResponseBody
    public PageInfo getVisitTracer(HttpServletRequest request,@RequestParam("pageNum")int pageNum){
        logger.info("谁看过我");
        Integer userId = SessionUtils.getSessionAttr(request,"user", UserBasic.class).getId();
        PageHelper.startPage(pageNum, 5);
        List<VisitTrace> visitTraces = visitTraceService.selectVisitTracer(userId);
        for(VisitTrace visitTrace:visitTraces){
            userService.userAttrHandler(visitTrace.getUserBasic());
        }
        visitTraces.forEach(logger::info);
        PageInfo pageInfo = new PageInfo(visitTraces);
        return pageInfo;
    }

}
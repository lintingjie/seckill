package org.seckill.web;

import java.util.Date;
import java.util.List;

import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.dto.SeckillResult;
import org.seckill.entity.Seckill;
import org.seckill.enums.SeckillStateEnum;
import org.seckill.exception.RepeatKillExeception;
import org.seckill.exception.SeckillException;
import org.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/seckill")//模块//url:/模块/资源/{id}/细分 /seckill/list
public class SeckillController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private SeckillService seckillService;
	
	@RequestMapping(value="/list",method=RequestMethod.GET)//资源
	public String list(Model model){
		//获取列表页
		List<Seckill> list = seckillService.getSeckillList();
		model.addAttribute("list",list);
		return "list";
	}
	
	@RequestMapping(value="/{seckillId}/detail",method=RequestMethod.GET)
	public String detail(@PathVariable("seckillId")Long seckillId,Model model){
		//返回详情页
		if(seckillId==null){
			return "redirect:/seckill/list";
		}
		Seckill seckill = seckillService.getById(seckillId);
		if(seckill==null){
			return "forward:/seckill/list";
		}
		model.addAttribute("seckill", seckill);
		return "detail";
	}
	
	@RequestMapping(value="/{seckillId}/exposer",
			method=RequestMethod.POST,
			produces={"application/json;charset=UTF-8"})
	@ResponseBody//返回类型作为ajax输出，转换为json数据
	public SeckillResult<Exposer> exposer(@PathVariable Long seckillId){
		SeckillResult<Exposer> result;
		try {
			Exposer exposer = seckillService.exportSeckillUrl(seckillId);
			result = new SeckillResult<Exposer>(true,exposer);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			result = new SeckillResult<Exposer>(false,e.getMessage());
		}
		return result;
	}
	
	@RequestMapping(value="/{seckillId}/{md5}/execution",
			method=RequestMethod.POST,
			produces={"application/json;charset=UTF-8"})
	@ResponseBody
	public SeckillResult<SeckillExecution> execute(@PathVariable Long seckillId,
												   @PathVariable String md5,
												   @CookieValue(value="killPhone",required=false) Long userPhone){
		try {
			//存储过程调用
			SeckillExecution execution = seckillService.executeSeckillByProcedure(seckillId, userPhone, md5);
			return new SeckillResult<SeckillExecution>(true,execution);
		} catch (RepeatKillExeception e) {
			SeckillExecution execution = new SeckillExecution(seckillId, SeckillStateEnum.REPEAT_KILL);
			return new SeckillResult<SeckillExecution>(true,execution);
		} catch (SeckillException e) {
			SeckillExecution execution = new SeckillExecution(seckillId, SeckillStateEnum.END);
			return new SeckillResult<SeckillExecution>(true,execution);
		} catch (Exception e){
			SeckillExecution execution = new SeckillExecution(seckillId, SeckillStateEnum.INNER_ERROR);
			return new SeckillResult<SeckillExecution>(false,execution);
		}
	}
	
	@RequestMapping(value="/time/now",method=RequestMethod.GET)
	@ResponseBody
	public SeckillResult<Long> time(){
		Date now = new Date();
		return new SeckillResult<Long>(true,now.getTime());
	}
	
	
	
	
	
	
	
	
}

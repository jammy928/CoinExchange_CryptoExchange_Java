package com.bizzan.bitrade.controller.redenvelope;

import static org.springframework.util.Assert.notNull;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.validation.Valid;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.util.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bizzan.bitrade.annotation.AccessLog;
import com.bizzan.bitrade.constant.AdminModule;
import com.bizzan.bitrade.constant.PageModel;
import com.bizzan.bitrade.controller.BaseController;
import com.bizzan.bitrade.entity.Coin;
import com.bizzan.bitrade.entity.RedEnvelope;
import com.bizzan.bitrade.entity.RedEnvelopeDetail;
import com.bizzan.bitrade.service.CoinService;
import com.bizzan.bitrade.service.LocaleMessageSourceService;
import com.bizzan.bitrade.service.RedEnvelopeDetailService;
import com.bizzan.bitrade.service.RedEnvelopeService;
import com.bizzan.bitrade.util.DateUtil;
import com.bizzan.bitrade.util.GeneratorUtil;
import com.bizzan.bitrade.util.MessageResult;

/**
 * 红包管理
 * @author shaox
 *
 */
@RestController
@RequestMapping("/envelope")
public class RedEnvelopeController extends BaseController {
	@Autowired
	private RedEnvelopeService redEnveloperService;
	
	@Autowired
	private RedEnvelopeDetailService redEnveloperDetailService;

	@Autowired
    private LocaleMessageSourceService messageSource;
	
	@Autowired
	private CoinService coinService;
	
	/**
	 * 红包分页列表
	 * @param pageModel
	 * @return
	 */
	@RequiresPermissions("envelope:page-query")
    @PostMapping("page-query")
    @AccessLog(module = AdminModule.REDENVELOPE, operation = "分页查看红包列表RedEnvelopeController")
    public MessageResult envelopeList(PageModel pageModel) {
		if (pageModel.getProperty() == null) {
            List<String> list = new ArrayList<>();
            list.add("createTime");
            List<Sort.Direction> directions = new ArrayList<>();
            directions.add(Sort.Direction.DESC);
            pageModel.setProperty(list);
            pageModel.setDirection(directions);
        }
        Page<RedEnvelope> all = redEnveloperService.findAll(null, pageModel.getPageable());
        return success(all);
    }
	
	/**
	 * 红包详情
	 * @param pageModel
	 * @return
	 */
	@RequiresPermissions("envelope:detail")
	@GetMapping("{id}/detail")
    @AccessLog(module = AdminModule.REDENVELOPE, operation = "查看红包详情RedEnvelopeController")
    public MessageResult envelopeDetail(@PathVariable Long id) {
		RedEnvelope redEnvelope = redEnveloperService.findOne(id);
		Assert.notNull(redEnvelope, "validate id!");
        return success(redEnvelope);
    }
	
	/**
	 * 领取详情分页
	 * @param pageModel
	 * @return
	 */
	@RequiresPermissions("envelope:receive-detail")
    @PostMapping("receive-detail")
    @AccessLog(module = AdminModule.REDENVELOPE, operation = "查看红包领取详情RedEnvelopeController")
    public MessageResult envelopeDetailList(@RequestParam(value = "envelopeId", defaultValue = "0") Long envelopeId,
    		@RequestParam(value = "pageNo", defaultValue = "0") Integer pageNo,
    		@RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
		
		Page<RedEnvelopeDetail> detailList = redEnveloperDetailService.findByEnvelope(envelopeId, pageNo, pageSize);

        return success(detailList);
    }
	
	/**
	 * 新建红包
	 * @param redEnvelope
	 * @return
	 */
	@RequiresPermissions("envelope:add")
    @PostMapping("add")
    @AccessLog(module = AdminModule.REDENVELOPE, operation = "新增红包信息RedEnvelopeController")
    public MessageResult addRedEnvelope(
            @Valid RedEnvelope redEnvelope) {
		// 检查币种是否存在
		Coin coin = coinService.findByUnit(redEnvelope.getUnit());
		Assert.notNull(coin, "无效的币种！");
		
		// 生成红包编号
		SimpleDateFormat f = new SimpleDateFormat("MMddHHmmss");
		redEnvelope.setEnvelopeNo(f.format(new Date()) + GeneratorUtil.getNonceString(5).toUpperCase());
		
		redEnvelope.setMemberId(1L); // 平台发行的固定为1的用户
		redEnvelope.setPlateform(1); // 平台发行的固定为1（平台红包）
		redEnvelope.setState(0);
		redEnvelope.setReceiveAmount(BigDecimal.ZERO);
		redEnvelope.setReceiveCount(0);
		
		redEnvelope.setCreateTime(DateUtil.getCurrentDate());
		redEnvelope = redEnveloperService.save(redEnvelope);
        return MessageResult.getSuccessInstance(messageSource.getMessage("SUCCESS"), redEnvelope);
    }
	
	/**
	 * 修改红包
	 * @param id
	 * @param type
	 * @param invite
	 * @param maxRand
	 * @param totalAmount
	 * @param count
	 * @param logoImage
	 * @param bgImage
	 * @param name
	 * @param desc
	 * @param expiredHours
	 * @param state
	 * @return
	 */
	@RequiresPermissions("envelope:modify")
    @PostMapping("modify")
    @AccessLog(module = AdminModule.REDENVELOPE, operation = "新增红包信息RedEnvelopeController")
    public MessageResult modifyRedEnvelope(
    		@RequestParam("id") Long id,
            @RequestParam(value = "type", required = false) Integer type,
            @RequestParam(value = "invite", required = false) Integer invite,
            @RequestParam(value = "unit", required = false) String unit,
            @RequestParam(value = "maxRand", required = false) BigDecimal maxRand,
            @RequestParam(value = "totalAmount", required = false) BigDecimal totalAmount,
            @RequestParam(value = "count", required = false) Integer count,
            @RequestParam(value = "logoImage", required = false) String logoImage,
            @RequestParam(value = "bgImage", required = false) String bgImage,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "detail", required = false) String detail,
            @RequestParam(value = "expiredHours", required = false) Integer expiredHours,
            @RequestParam(value = "state", required = false) Integer state) {
		
		RedEnvelope redEnvelope = redEnveloperService.findOne(id);
		notNull(redEnvelope, "Validate Red Envelope!");
		
		if(type != null) redEnvelope.setType(type);
		if(invite != null) redEnvelope.setInvite(invite);
		if(unit != null) {
			// 检查币种是否存在
			Coin coin = coinService.findByUnit(redEnvelope.getUnit());
			Assert.notNull(coin, "无效的币种！");
			redEnvelope.setUnit(unit);
		};
		if(maxRand != null) redEnvelope.setMaxRand(maxRand);
		if(totalAmount != null) redEnvelope.setTotalAmount(totalAmount);
		if(count != null) redEnvelope.setCount(count);
		if(logoImage != null) redEnvelope.setLogoImage(logoImage);
		if(bgImage != null) redEnvelope.setBgImage(bgImage);
		if(name != null) redEnvelope.setName(name);
		if(detail != null) redEnvelope.setDetail(detail);
		if(expiredHours != null) redEnvelope.setExpiredHours(expiredHours);
		if(state != null) redEnvelope.setState(state);
		
		redEnvelope = redEnveloperService.save(redEnvelope);
		
        return MessageResult.getSuccessInstance(messageSource.getMessage("SUCCESS"), redEnvelope);
    }
}

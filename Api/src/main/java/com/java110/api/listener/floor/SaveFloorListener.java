package com.java110.api.listener.floor;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.java110.api.bmo.floor.IFloorBMO;
import com.java110.api.listener.AbstractServiceApiDataFlowListener;
import com.java110.utils.constant.*;
import com.java110.utils.exception.ListenerExecuteException;
import com.java110.utils.util.Assert;
import com.java110.core.annotation.Java110Listener;
import com.java110.core.context.DataFlowContext;
import com.java110.core.factory.GenerateCodeFactory;
import com.java110.core.smo.floor.IFloorInnerServiceSMO;
import com.java110.dto.FloorDto;
import com.java110.entity.center.AppService;
import com.java110.event.service.api.ServiceDataFlowEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

/**
 * @ClassName SaveFloorListener
 * @Description 保存小区楼信息
 * @Author wuxw
 * @Date 2019/4/26 14:51
 * @Version 1.0
 * add by wuxw 2019/4/26
 **/

@Java110Listener("saveFloorListener")
public class SaveFloorListener extends AbstractServiceApiDataFlowListener {

    @Autowired
    private IFloorBMO floorBMOImpl;

    @Autowired
    private IFloorInnerServiceSMO floorInnerServiceSMOImpl;



    private static Logger logger = LoggerFactory.getLogger(SaveFloorListener.class);

    @Override
    public String getServiceCode() {
        return ServiceCodeConstant.SERVICE_CODE_SAVE_FLOOR;
    }

    @Override
    public HttpMethod getHttpMethod() {
        return HttpMethod.POST;
    }

    @Override
    public void soService(ServiceDataFlowEvent event) {

        logger.debug("ServiceDataFlowEvent : {}", event);

        DataFlowContext dataFlowContext = event.getDataFlowContext();
        AppService service = event.getAppService();

        String paramIn = dataFlowContext.getReqData();

        //校验数据
        validate(paramIn);
        JSONObject paramObj = JSONObject.parseObject(paramIn);

        HttpHeaders header = new HttpHeaders();
        dataFlowContext.getRequestCurrentHeaders().put(CommonConstant.HTTP_USER_ID, "-1");
        dataFlowContext.getRequestCurrentHeaders().put(CommonConstant.HTTP_ORDER_TYPE_CD, "D");
        JSONArray businesses = new JSONArray();

        //生成floorId
        generateFloorId(paramObj);

        //添加小区楼
        businesses.add(floorBMOImpl.addFloor(paramObj));

        //小区楼添加到小区中
        businesses.add(floorBMOImpl.addCommunityMember(paramObj));


        ResponseEntity<String> responseEntity = floorBMOImpl.callService(dataFlowContext, service.getServiceCode(), businesses);

        dataFlowContext.setResponseEntity(responseEntity);

    }

    /**
     * 生成小区楼ID
     *
     * @param paramObj 请求入参数据
     */
    private void generateFloorId(JSONObject paramObj) {
        String floorId = GenerateCodeFactory.getGeneratorId(GenerateCodeFactory.CODE_PREFIX_floorId);
        paramObj.put("floorId", floorId);
    }

    /**
     * 数据校验
     *
     * @param paramIn "communityId": "7020181217000001",
     *                "memberId": "3456789",
     *                "memberTypeCd": "390001200001"
     */
    private void validate(String paramIn) {
        Assert.jsonObjectHaveKey(paramIn, "name", "请求报文中未包含name");
        Assert.jsonObjectHaveKey(paramIn, "userId", "请求报文中未包含userId");
        Assert.jsonObjectHaveKey(paramIn, "floorNum", "请求报文中未包含floorNum");
        Assert.jsonObjectHaveKey(paramIn, "communityId", "请求报文中未包含communityId");

        JSONObject paramObj = JSONObject.parseObject(paramIn);

        FloorDto floorDto = new FloorDto();
        floorDto.setFloorNum(paramObj.getString("floorNum"));
        floorDto.setCommunityId(paramObj.getString("communityId"));


        int floorCount = floorInnerServiceSMOImpl.queryFloorsCount(floorDto);

        if (floorCount > 0) {
            throw new ListenerExecuteException(ResponseConstant.RESULT_CODE_ERROR, "楼栋编号已经存在");
        }
    }


    @Override
    public int getOrder() {
        return 0;
    }

    public IFloorInnerServiceSMO getFloorInnerServiceSMOImpl() {
        return floorInnerServiceSMOImpl;
    }

    public void setFloorInnerServiceSMOImpl(IFloorInnerServiceSMO floorInnerServiceSMOImpl) {
        this.floorInnerServiceSMOImpl = floorInnerServiceSMOImpl;
    }
}

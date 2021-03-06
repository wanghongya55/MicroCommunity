package com.java110.api.bmo.storeAttr.impl;

import com.alibaba.fastjson.JSONObject;

import com.java110.api.bmo.ApiBaseBMO;
import com.java110.api.bmo.storeAttr.IStoreAttrBMO;
import com.java110.core.context.DataFlowContext;
import com.java110.core.smo.file.IFileInnerServiceSMO;
import com.java110.core.smo.file.IFileRelInnerServiceSMO;
import com.java110.core.smo.storeAttr.IStoreAttrInnerServiceSMO;
import com.java110.dto.file.FileRelDto;
import com.java110.dto.store.StoreAttrDto;
import com.java110.utils.constant.BusinessTypeConstant;
import com.java110.utils.constant.CommonConstant;
import com.java110.utils.util.Assert;
import com.java110.utils.util.BeanConvertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("storeAttrBMOImpl")
public class StoreAttrBMOImpl extends ApiBaseBMO implements IStoreAttrBMO {

    @Autowired
    private IStoreAttrInnerServiceSMO storeAttrInnerServiceSMOImpl;

    /**
     * 添加小区信息
     *
     * @param paramInJson     接口调用放传入入参
     * @param dataFlowContext 数据上下文
     * @return 订单服务能够接受的报文
     */
    public JSONObject addStoreAttr(JSONObject paramInJson, DataFlowContext dataFlowContext) {


        JSONObject business = JSONObject.parseObject("{\"datas\":{}}");
        //business.put(CommonConstant.HTTP_BUSINESS_TYPE_CD, BusinessTypeConstant.BUSINESS_TYPE_SAVE_STOREATTR);
        business.put(CommonConstant.HTTP_SEQ, DEFAULT_SEQ);
        business.put(CommonConstant.HTTP_INVOKE_MODEL, CommonConstant.HTTP_INVOKE_MODEL_S);
        JSONObject businessStoreAttr = new JSONObject();
        businessStoreAttr.putAll(paramInJson);
        businessStoreAttr.put("attrId", "-1");
        //计算 应收金额
        business.getJSONObject(CommonConstant.HTTP_BUSINESS_DATAS).put("businessStoreAttr", businessStoreAttr);
        return business;
    }


    /**
     * 添加活动信息
     *
     * @param paramInJson     接口调用放传入入参
     * @param dataFlowContext 数据上下文
     * @return 订单服务能够接受的报文
     */
    public JSONObject updateStoreAttr(JSONObject paramInJson, DataFlowContext dataFlowContext) {

        StoreAttrDto storeAttrDto = new StoreAttrDto();
        storeAttrDto.setAttrId(paramInJson.getString("attrId"));
        List<StoreAttrDto> storeAttrDtos = storeAttrInnerServiceSMOImpl.queryStoreAttrs(storeAttrDto);

        Assert.listOnlyOne(storeAttrDtos, "未找到需要修改的活动 或多条数据");


        JSONObject business = JSONObject.parseObject("{\"datas\":{}}");
        business.put(CommonConstant.HTTP_BUSINESS_TYPE_CD, BusinessTypeConstant.BUSINESS_TYPE_UPDATE_STORE_ATTR);
        business.put(CommonConstant.HTTP_SEQ, DEFAULT_SEQ);
        business.put(CommonConstant.HTTP_INVOKE_MODEL, CommonConstant.HTTP_INVOKE_MODEL_S);
        JSONObject businessStoreAttr = new JSONObject();
        businessStoreAttr.putAll(BeanConvertUtil.beanCovertMap(storeAttrDtos.get(0)));
        businessStoreAttr.putAll(paramInJson);
        //计算 应收金额
        business.getJSONObject(CommonConstant.HTTP_BUSINESS_DATAS).put("businessStoreAttr", businessStoreAttr);
        return business;
    }



    /**
     * 添加小区信息
     *
     * @param paramInJson     接口调用放传入入参
     * @param dataFlowContext 数据上下文
     * @return 订单服务能够接受的报文
     */
    public JSONObject deleteStoreAttr(JSONObject paramInJson, DataFlowContext dataFlowContext) {


        JSONObject business = JSONObject.parseObject("{\"datas\":{}}");
        //business.put(CommonConstant.HTTP_BUSINESS_TYPE_CD, BusinessTypeConstant.BUSINESS_TYPE_DELETE_STOREATTR);
        business.put(CommonConstant.HTTP_SEQ, DEFAULT_SEQ);
        business.put(CommonConstant.HTTP_INVOKE_MODEL, CommonConstant.HTTP_INVOKE_MODEL_S);
        JSONObject businessStoreAttr = new JSONObject();
        businessStoreAttr.putAll(paramInJson);
        //计算 应收金额
        business.getJSONObject(CommonConstant.HTTP_BUSINESS_DATAS).put("businessStoreAttr", businessStoreAttr);
        return business;
    }

}

package com.silita.biaodaa.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.silita.biaodaa.controller.vo.Page;
import com.silita.biaodaa.dao.*;
import com.silita.biaodaa.model.TbProject;
import com.silita.biaodaa.model.TbProjectBuild;
import com.silita.biaodaa.model.TbProjectContract;
import com.silita.biaodaa.model.TbProjectZhaotoubiao;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ProjectService {

    @Autowired
    TbProjectMapper tbProjectMapper;
    @Autowired
    TbCompanyMapper tbCompanyMapper;
    @Autowired
    TbProjectDesignMapper tbProjectDesignMapper;
    @Autowired
    TbProjectContractMapper tbProjectContractMapper;
    @Autowired
    TbProjectBuildMapper tbProjectBuildMapper;
    @Autowired
    TbProjectZhaotoubiaoMapper tbProjectZhaotoubiaoMapper;
    private Matcher matcher;

    /**
     * 按工程查询
     * @param param
     * @param result
     * @return
     */
    public Map<String,Object> getProjectList(Map<String,Object> param,Map<String,Object> result){
        Integer pageNum = MapUtils.getInteger(param,"pageNo");
        Integer pageSize = MapUtils.getInteger(param,"pageSize");
        List<Map<String,Object>> projectList = new ArrayList<Map<String,Object>>();
        Page page = new Page();
        page.setPageSize(pageSize);
        page.setCurrentPage(pageNum);

        String areaCode = "";
        if (null != param.get("area")){
            areaCode = tbCompanyMapper.getAreaCode(param.get("area").toString());
        }
        param.put("areaCode",areaCode);

        PageHelper.startPage(page.getCurrentPage(),page.getPageSize());
        projectList = tbProjectMapper.queryObject(param);
        this.putProvince(projectList);
        PageInfo pageInfo = new PageInfo(projectList);
        result.put("data",pageInfo.getList());
        result.put("pageNum",pageInfo.getPageNum());
        result.put("pageSize", pageInfo.getPageSize());
        result.put("total",pageInfo.getTotal());
        result.put("pages",pageInfo.getPages());
        return result;
    }

    /**
     * 按工程查看业绩列表
     * created by zhushuai
     * @param params
     * @param result
     * @return
     */
    public Map<String,Object> getObjectListByUnit(Map<String,Object> params,Map<String,Object> result){
        Integer pageNum = MapUtils.getInteger(params,"pageNo");
        Integer pageSize = MapUtils.getInteger(params,"pageSize");

        if(null == params.get("pactType")){
            params.put("pactType","施工");
        }

        Page page = new Page();
        page.setCurrentPage(pageNum);
        page.setPageSize(pageSize);

        String areaCode = "";
        if (null != params.get("area")){
            areaCode = tbCompanyMapper.getAreaCode(params.get("area").toString());
        }
        params.put("areaCode",areaCode);

        PageHelper.startPage(page.getCurrentPage(),page.getPageSize());
        List<Map<String,Object>> projectList = tbProjectMapper.queryObjectByUnit(params);
        this.putProvince(projectList);
        PageInfo pageInfo = new PageInfo(projectList);
        result.put("data",pageInfo.getList());
        result.put("pageNum",pageInfo.getPageNum());
        result.put("pageSize", pageInfo.getPageSize());
        result.put("total",pageInfo.getTotal());
        result.put("pages",pageInfo.getPages());
        return result;
    }

    /**
     * 获取项目详情
     * created by zhushuai
     * @param proId 项目id
     * @param result 返回result
     * @return
     */
    public Map<String,Object> getProjectDetail(Integer proId,Map<String,Object> result){
        TbProject project = tbProjectMapper.queryProjectDetail(proId);
        String scope = project.getAcreage() == null ? null : project.getAcreage();
        if(null != scope){
            scope = this.analysisScope(scope);
            project.setAcreage(scope);
        }
        if(null != project.getPath()){
            project.setProvince("湖南省");
            List<Map<String,Object>> areaList = tbProjectMapper.queryNameAndPath();
            for (Map<String,Object> areaMap : areaList){
                if(project.getPath().split(",")[0].equals(areaMap.get("path").toString())){
                    project.setProvince(areaMap.get("name").toString());
                    project.setPath(null);
                    break;
                }
            }
        }
        result.put("data",project);
        return result;
    }

    /**
     * tab详情
     * created by zhushuai
     * @param params
     * @param result
     * @return
     */
    public Map<String,Object> getTabProjectDetail(Map<String,Object> params, Map<String,Object> result){
        Integer proId = MapUtils.getInteger(params,"proId");
        String tabType = MapUtils.getString(params,"tabType");
        List resultList = new ArrayList();
        //施工图审查
        if("design".equals(tabType)){
            resultList = tbProjectDesignMapper.queryProjectDesignByProId(proId);
        }else if("constact".equals(tabType)){
            resultList = this.getContractList(proId);
        }else if ("zhaotoubiao".equals(tabType)){
            resultList = this.getZhaotoubiaoList(proId);
        }else if ("build".equals(tabType)){
            resultList = this.getProjectBuildDetail(proId);
        }
        result.put("data",resultList);
        return result;
    }


    /**
     * 获取项目下的合同备案列表
     * created by zhushuai
     * @param proId
     * @return
     */
    private List<TbProjectContract> getContractList(Integer proId){
        List<TbProjectContract> list  = tbProjectContractMapper.queryProjectContractListByProId(proId);
        if(null != list && list.size() >0 && null != list.get(0)){
            return list;
        }
        List<TbProjectBuild> projectBuildList = tbProjectBuildMapper.queryConstartProBuildByProId(proId);
        if(null != projectBuildList && projectBuildList.size() > 0){
            String remark = null;
            String contractDate = null;
            String constartPrice = null;
            String constartNo = null;
            TbProjectContract tbProjectContract = null;
            for(TbProjectBuild build : projectBuildList){
                if (null != build.getContractRemark()|| !"未办理合同备案".equals(build.getContractRemark())){
                    remark = build.getContractRemark();
                    contractDate = null;
                    constartPrice = null;
                    constartNo = null;
                    tbProjectContract = new TbProjectContract();
                    contractDate = remark.substring(this.getIndex(remark,"合同日期：")+5,this.getIndex(remark,","));
                    constartPrice = remark.substring(this.getIndex(remark,"合同价格：")+5,this.getIndex(remark,"万元"));
                    constartNo = remark.substring(this.getIndex(remark,"合同备案编号：")+7,remark.length());
                    tbProjectContract.setProvRecordCode(constartNo);
                    tbProjectContract.setAmount(constartPrice);
                    tbProjectContract.setSignDate(contractDate);
                    tbProjectContract.setCategory(build.getContractCategory());
                    list.add(tbProjectContract);
                }
            }
        }
        return list;
    }

    private List<TbProjectZhaotoubiao> getZhaotoubiaoList(Integer proId){
        List<TbProjectZhaotoubiao> list = tbProjectZhaotoubiaoMapper.queryZhaotoubiaoListByProId(proId);
        if(null != list && list.size() >0 && null != list.get(0)){
            return list;
        }
        List<TbProjectBuild> buildList = tbProjectBuildMapper.queryZhaobiaoProByProId(proId);
        if(null != buildList && buildList.size() >0 && null != buildList.get(0)){
            String remark = "";
            String zhongbiaoDate = "";
            TbProjectZhaotoubiao zhaotoubiao = null;
            for(TbProjectBuild buid : buildList){
                if(null != buid.getBidRemark() && !"未办理中标备案".equals(buid.getBidRemark())){
                    remark = buid.getBidRemark();
                    zhaotoubiao = new TbProjectZhaotoubiao();
                    zhongbiaoDate = remark.substring(this.getIndex(remark,"中标日期："),this.getIndex(remark,","));
                    zhaotoubiao.setProId(proId.toString());
                    zhaotoubiao.setZhongbiaoDate(zhongbiaoDate == null ? null:zhongbiaoDate.substring(this.getIndex(zhongbiaoDate,"：")+1,zhongbiaoDate.length()));
                    zhaotoubiao.setZhongbiaoAmount(buid.getBidPrice());
                    zhaotoubiao.setZhaobiaoType(buid.getBidType());
                    zhaotoubiao.setZhongbiaoCompany(buid.getBOrg());
                    list.add(zhaotoubiao);
                }
            }
        }
        return list;
    }

    private List<TbProjectBuild> getProjectBuildDetail(Integer proId){
        List<TbProjectBuild> proBuildList = tbProjectBuildMapper.queryProjectBuildByProId(proId);
        if(null != proBuildList && proBuildList.size() >0 && null != proBuildList.get(0)){
            String remark = null;
            String amount = null;
            for(TbProjectBuild project : proBuildList){
                if (null != project.getContractRemark() && !"未办理合同备案".equals(project.getContractRemark())){
                    remark = project.getContractRemark();
                    amount = remark.substring(this.getIndex(remark,"合同价格："),this.getIndex(remark,"万元"));
                    project.setConstractAmount(amount == null ? null : amount.substring(this.getIndex(amount,"：")+1,amount.length()));
                    project.setCompleteRemark(null);
                }
            }
        }
        return proBuildList;
    }
    /**
     * 将省信息放入集合中
     * created by zhushuai
     * @param list
     */
    private void putProvince(List<Map<String,Object>> list){
        List<Map<String,Object>> areaList = tbProjectMapper.queryNameAndPath();
        if(null != list && list.size() > 0){
            for (Map<String,Object> map : list){
                for (Map<String,Object> areaMap : areaList){
                    if(null == map.get("path")){
                        map.put("province","湖南省");
                        break;
                    }else if(map.get("path").toString().split(",")[0].equals(areaMap.get("path").toString())){
                        map.put("province",areaMap.get("name"));
                        map.remove("path");
                        break;
                    }
                }
            }
        }
    }

    /**
     * 解析面积
     * @param scope
     * @return
     */
    private String analysisScope(String scope){
        //判断中间是否有总建筑面积
        int startIndex = -2;
        int endIndex = -2;
        if(scope.contains("总建筑面积")){
            startIndex = this.getIndex(scope,"总建筑面积");
            scope = scope.substring(startIndex,scope.length());
            endIndex = this.getUnitIndex(scope);
            scope = scope.substring(0,endIndex);
        }else if (scope.contains("建筑总面积")){
            startIndex = this.getIndex(scope,"建筑总面积");
            scope = scope.substring(startIndex,scope.length());
            endIndex = this.getUnitIndex(scope);
            scope = scope.substring(0,endIndex);
        }else if (scope.contains("总面积")){
            startIndex = this.getIndex(scope,"总面积");
            scope = scope.substring(startIndex,scope.length());
            endIndex = this.getUnitIndex(scope);
            scope = scope.substring(0,endIndex);
        }else if (scope.contains("实际用地面积")){
            startIndex = this.getIndex(scope,"实际用地面积");
            scope = scope.substring(startIndex,scope.length());
            endIndex = this.getUnitIndex(scope);
            scope = scope.substring(0,endIndex);
        }else if (scope.contains("改造面积")){
            startIndex = this.getIndex(scope,"改造面积");
            scope = scope.substring(startIndex,scope.length());
            endIndex = this.getUnitIndex(scope);
            scope = scope.substring(0,endIndex);
        }else if(scope.contains("廉租住房")){
            startIndex = this.getIndex(scope,"廉租住房");
            scope = scope.substring(startIndex,scope.length());
            endIndex = this.getUnitIndex(scope);
            if(scope.contains("套")){
                scope = scope.substring(this.getIndex(scope,"套"),endIndex);
            }else{
                scope = scope.substring(0,endIndex);
            }
        }else if (scope.contains("面积")){
            startIndex = this.getIndex(scope,"面积");
            scope = scope.substring(startIndex,scope.length());
            endIndex = this.getUnitIndex(scope);
            scope = scope.substring(0,endIndex);
        }else if (scope.contains("绿化面积")){
            startIndex = this.getIndex(scope,"绿化面积");
            scope = scope.substring(startIndex,scope.length());
            endIndex = this.getUnitIndex(scope);
            scope = scope.substring(0,endIndex);
        }else if (scope.contains("支护为")){
            startIndex = this.getIndex(scope,"支护为");
            scope = scope.substring(startIndex,scope.length());
            endIndex = this.getUnitIndex(scope);
            scope = scope.substring(0,endIndex);
        }else if (scope.contains("建筑面积")){
            startIndex = this.getIndex(scope,"建筑面积");
            scope = scope.substring(startIndex,scope.length());
            endIndex = this.getUnitIndex(scope);
            scope = scope.substring(0,endIndex);
        }else if (scope.contains("扩建面积")){
            startIndex = this.getIndex(scope,"扩建面积");
            scope = scope.substring(startIndex,scope.length());
            endIndex = this.getUnitIndex(scope);
            scope = scope.substring(0,endIndex);
        }else if (scope.contains("长") && scope.contains("宽")){
            //判断出现的次数

        }
        return scope;
    }

    /**
     * 获取某个字符在字符串中的下标
     * @param str
     * @param chart
     * @return
     */
    public int getIndex(String str,String chart){
        Matcher matcher = Pattern.compile(chart).matcher(str);
        int index = -2;
        if(matcher.find()){
            index = matcher.start();
        }
        return index;
    }

    /**
     * 获取单位所在字符串的下标
     * @param str
     * @return
     */
    private int getUnitIndex(String str){
        int unitIndex = -2;
        if(str.contains("平方")){
            unitIndex = this.getIndex(str,"平方");
        }else if (str.contains("m2")){
            unitIndex = this.getIndex(str,"m2");
        }else if (str.contains("M2")){
            unitIndex = this.getIndex(str,"M2");
        }else if (str.contains("㎡")){
            unitIndex = this.getIndex(str,"㎡");
        }
        return unitIndex;
    }

    /**
     * 截取日期
     * @param str
     * @return
     */
    private String getStrDate(String str){
        String reg = "[0-9]{4}[-][0-9]{1,2}[-][0-9]{1,2}[ ][0-9]{1,2}[:][0-9]{1,2}[:][0-9]{1,2}";
        Matcher matcher = Pattern.compile(str).matcher(reg);
        if(matcher.find()){
            return matcher.group(0);
        }
        return str;
    }

    /**
     * 截取数字
     * @param str
     * @return
     */
    private String getStrNumber(String str){
        String reg = "([1-9]\\d*\\.?\\d*)|(0\\.\\d*[1-9])";
        Matcher matcher = Pattern.compile(str).matcher(reg);
        while (matcher.find()){
            return matcher.group(0);
        }
        return str;
    }

}

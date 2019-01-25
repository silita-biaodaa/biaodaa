package com.silita.biaodaa.common;

import com.silita.biaodaa.utils.MyStringUtils;
import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;

/**
 * Created by dh on 2019/1/8.
 */
@Aspect    //该标签把LoggerAspect类声明为一个切面
@Order(2)  //设置切面的优先级：如果有多个切面，可通过设置优先级控制切面的执行顺序（数值越小，优先级越高）
@Component //该标签把LoggerAspect类放到IOC容器中
public class AspectPermissions {

    private Logger logger = Logger.getLogger(this.getClass());

    //|| (execution(public * com.silita.biaodaa.controller.UnderConstructController.*(..)))
    @Pointcut("(execution(public * com.silita.biaodaa.controller.MemberPermissionController.*(..)))")
    public void declearJoinPointExpression(){}


    /**
     * 环绕通知(需要携带类型为ProceedingJoinPoint类型的参数)
     * 环绕通知包含前置、后置、返回、异常通知；ProceedingJoinPoin 类型的参数可以决定是否执行目标方法
     * 且环绕通知必须有返回值，返回值即目标方法的返回值
     * @param point
     */
    @Around(value="declearJoinPointExpression()")
    public Object aroundMethod(ProceedingJoinPoint point) throws UnsupportedEncodingException {
        Object result = null;
        String calssName = point.getTarget().getClass().getName();
        String methodName = point.getSignature().getName();
        String permissions = VisitInfoHolder.getPermissions();
        String roleCode = VisitInfoHolder.getRoleCode();
        Object[] args = point.getArgs();
        logger.debug("AspectPermissions [calssName:"+calssName+"][methodName:"+methodName+"][Permissions:"+permissions+"][RoleCode:"+roleCode+"]");
        Map resultMap = null;
        if(MyStringUtils.isNull(roleCode) || !verfifyPermissions(permissions,methodName)){
            resultMap = new HashMap();
            resultMap.put("code", Constant.ERR_VERIFY_USER_PERMISSIONS);
            resultMap.put("msg","当前用户没有此功能权限，请购买会员。");
            result =resultMap;
        }else {
            try {
                //前置通知
                logger.debug("The method " + methodName + " start. param<" + asList(args) + ">");
                //执行目标方法
                result = point.proceed();
                //返回通知
                logger.debug("The method " + methodName + " end. result<" + result + ">");
            } catch (Throwable e) {
                logger.error(e,e);
                resultMap = new HashMap();
                resultMap.put("code", Constant.EXCEPTION_CODE);
                resultMap.put("msg","会员权限校验异常。"+e.getMessage());
                result =resultMap;
            }
        }
        //后置通知
        logger.debug("The method "+ methodName+" end.");
        return result;
    }

    /**
     * 权限验证
     * @param permissions
     * @param methodName
     * @return
     */
    private boolean verfifyPermissions(String permissions,String methodName){
        if(MyStringUtils.isNull(permissions)){
            return false;
        }
        String[] permission = permissions.split(",");
        List<String> list =  Arrays.asList(permission);
        return list.contains(methodName);
    }

}

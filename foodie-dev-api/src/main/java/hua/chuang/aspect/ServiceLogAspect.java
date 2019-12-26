package hua.chuang.aspect;


import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ServiceLogAspect {

    public final static Logger log = LoggerFactory.getLogger(ServiceLogAspect.class);


    /**
     * AOP通知类型：
     * 1. 前置通知：再方法调用前执行
     * 2. 后置通知：在方法正常调用后执行
     * 3. 环绕通知：在方法调用的之前和之后，都可以分别执行的通知
     * 4. 异常通知：如果在方法调用过程中发生异常，则通知
     * 5. 最终通知：在方法调用之后通知
     */


    /**
     * 切面表达式
     * execution 代表所要执行的表达式主体
     * 第一处 * 代表索要返回的类型，*代表所有类型
     * 第二处 包名代表aop监控的类所在的包
     * 第三处 ..代表该包以及其下的所有子包下的所有方法
     * 第四处 * 代表类型，*代表所有类
     * 第五处 *(..) *代表类中的方法名，(..)代表方法的任何参数
     *
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @Around("execution(* hua.chuang.service.impl..*.*(..))")
    public Object recordTimeLog(ProceedingJoinPoint joinPoint) throws Throwable {

        log.info("======  开始执行 {}.{}  ======",joinPoint.getTarget().getClass(),joinPoint.getSignature().getName());


        //记录时间
        long begin = System.currentTimeMillis();

        //执行service
        Object result = joinPoint.proceed();

        //记录时间
        long end = System.currentTimeMillis();
        //执行时间
        long takeTime = end - begin;

         if (takeTime>3000){
             log.error("====== 执行结束，耗时:{}毫秒 ======",takeTime);
         }else if (takeTime>2000){
             log.warn("====== 执行结束，耗时:{}毫秒 ======",takeTime);
         }else {
             log.info("====== 执行结束，耗时:{}毫秒 ======",takeTime);
         }


        return result;
    }


}

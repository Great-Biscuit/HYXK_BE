package top.greatbiscuit.hyxk.config;

import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;
import top.greatbiscuit.common.core.constant.Constants;
import top.greatbiscuit.hyxk.quart.PostScoreRefreshJob;

/**
 * QuartZ自动任务配置类[只会执行一次, 数据库里有了数据就不会执行了]
 *
 * @Author: GreatBiscuit
 * @Date: 2022/3/31 20:33
 */
@Configuration
public class QuartzConfiguration {

    /**
     * 任务详情
     *
     * @return
     */
    @Bean
    public JobDetailFactoryBean postScoreRefreshJobDetail() {
        JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
        factoryBean.setJobClass(PostScoreRefreshJob.class);
        factoryBean.setName("postScoreRefreshJob");
        factoryBean.setGroup("HYXK_JobGroup");
        factoryBean.setDurability(true);
        factoryBean.setRequestsRecovery(true);
        return factoryBean;
    }

    /**
     * 触发器简略信息
     *
     * @param postScoreRefreshJobDetail
     * @return
     */
    @Bean
    public SimpleTriggerFactoryBean postScoreRefreshTrigger(JobDetail postScoreRefreshJobDetail) {
        SimpleTriggerFactoryBean factoryBean = new SimpleTriggerFactoryBean();
        factoryBean.setJobDetail(postScoreRefreshJobDetail);
        factoryBean.setName("postScoreRefreshTrigger");
        factoryBean.setGroup("HYXK_TriggerGroup");
        factoryBean.setRepeatInterval(Constants.QUARTZ_JOB_TIME);
        factoryBean.setJobDataMap(new JobDataMap());
        return factoryBean;
    }

}

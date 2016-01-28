package org.fbsearch.config

import org.fbsearch.entity.UserProfileRepository
import org.fbsearch.lucene.LuceneIndexer
import org.fbsearch.services.IStartDownloads
import org.fbsearch.services.NewDownloads
import org.fbsearch.services.StartDownloads
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.SchedulingConfigurer
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler
import org.springframework.scheduling.config.ScheduledTaskRegistrar

/**
 * Created by Pavel on 9/29/2015.
 */
@Configuration
@EnableScheduling
class SchedulersConfig implements SchedulingConfigurer {

    static Logger logger = LoggerFactory.getLogger(SchedulersConfig.class)
    private static final long ONE_DAY = 24 * 60 * 60 * 1000L
    private static final long TEN_MINS = 10 * 60 * 1000L

    @Autowired
    UserProfileRepository repo

    @Autowired
    ApplicationContext factory

    @Autowired
    LuceneIndexer indexer

    @Autowired
    IStartDownloads startDownloads;

    @Autowired
    NewDownloads newDownloads;


    @Bean
    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(20);
        scheduler.setThreadNamePrefix("task-");
        scheduler.setAwaitTerminationSeconds(60);
        scheduler.setWaitForTasksToCompleteOnShutdown(true);
        return scheduler;
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.addFixedRateTask(startDownloads, ONE_DAY); // Only if normal downloading after connect was interrupted
        taskRegistrar.addFixedRateTask(newDownloads, TEN_MINS);
    }
}

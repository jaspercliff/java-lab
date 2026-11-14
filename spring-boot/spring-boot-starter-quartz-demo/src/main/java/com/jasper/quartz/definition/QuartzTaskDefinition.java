package com.jasper.quartz.definition;

import org.quartz.JobDetail;
import org.quartz.Trigger;

public interface QuartzTaskDefinition {

    JobDetail jobDetail();

    Trigger trigger();
}
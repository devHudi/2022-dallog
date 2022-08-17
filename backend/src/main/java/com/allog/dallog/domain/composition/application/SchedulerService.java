package com.allog.dallog.domain.composition.application;

import com.allog.dallog.domain.category.domain.Category;
import com.allog.dallog.domain.integrationschedule.dao.IntegrationScheduleDao;
import com.allog.dallog.domain.integrationschedule.domain.IntegrationSchedule;
import com.allog.dallog.domain.integrationschedule.domain.Period;
import com.allog.dallog.domain.member.application.MemberService;
import com.allog.dallog.domain.member.dto.MemberResponse;
import com.allog.dallog.domain.schedule.domain.scheduler.Scheduler;
import com.allog.dallog.domain.schedule.dto.request.DateRangeRequest;
import com.allog.dallog.domain.schedule.dto.response.PeriodResponse;
import com.allog.dallog.domain.subscription.application.SubscriptionService;
import com.allog.dallog.domain.subscription.domain.Subscription;
import com.allog.dallog.domain.subscription.dto.response.SubscriptionResponse;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Service
public class SchedulerService {

    private final IntegrationScheduleDao integrationScheduleDao;
    private final SubscriptionService subscriptionService;
    private final MemberService memberService;

    public SchedulerService(final IntegrationScheduleDao integrationScheduleDao,
                            final SubscriptionService subscriptionService, final MemberService memberService) {
        this.integrationScheduleDao = integrationScheduleDao;
        this.subscriptionService = subscriptionService;
        this.memberService = memberService;
    }

    public List<PeriodResponse> getAvailablePeriods(final Long categoryId, final DateRangeRequest dateRange) {
        List<Long> subscriberIds = getSubscriberIds(categoryId);
        List<Category> categories = getCategoriesOfSubscribers(subscriberIds);
        List<IntegrationSchedule> schedules = getSchedulesOfCategories(categories, dateRange);

        Scheduler scheduler = new Scheduler(schedules, dateRange.getStartDateTime().toLocalDate(),
                dateRange.getEndDateTime().toLocalDate());

        return convertPeriodsToPeriodResponses(scheduler.getPeriods());
    }

    private List<Long> getSubscriberIds(final Long categoryId) {
        List<SubscriptionResponse> subscriptions = subscriptionService.findByCategoryId(categoryId);
        return subscriptions.stream()
                .map(subscriptionResponse -> memberService.findBySubscriptionId(subscriptionResponse.getId()))
                .map(MemberResponse::getId)
                .collect(Collectors.toList());
    }

    private List<Category> getCategoriesOfSubscribers(final List<Long> subscriberIds) {
        return subscriberIds.stream()
                .flatMap(subscriberId -> subscriptionService.getAllByMemberId(subscriberId).stream())
                .filter(Subscription::isChecked)
                .map(Subscription::getCategory)
                .collect(Collectors.toList());
    }

    private List<IntegrationSchedule> getSchedulesOfCategories(final List<Category> categories,
                                                               final DateRangeRequest dateRange) {
        List<Long> categoryIds = categories.stream()
                .map(Category::getId)
                .collect(Collectors.toList());

        // TODO: 구글 외부 일정까지 조율 대상으로 포함해야함
        return integrationScheduleDao.findByCategoryIdInAndBetween(categoryIds, dateRange.getStartDateTime(),
                dateRange.getEndDateTime());
    }

    private List<PeriodResponse> convertPeriodsToPeriodResponses(final List<Period> periods) {
        return periods.stream()
                .map(PeriodResponse::new)
                .collect(Collectors.toList());
    }
}

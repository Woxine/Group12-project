package com.group12.backend.sprint2.email;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * ID7 在下单成功路径中触发确认邮件的集成门禁。
 * 实现 {@code BookingServiceImpl#createBooking} 与邮件服务的接线后，请删除本用例或改为对 mock 的 verify。
 */
@DisplayName("ID7 Booking → email gate")
class BookingServiceEmailNotificationGateTest {

    @Test
    @DisplayName("预订创建成功后须触发确认邮件（实现后删除本 fail 或改为 verify）")
    void createBooking_mustTriggerConfirmationEmail() {
        org.junit.jupiter.api.Assertions.fail(
                "Sprint2 ID7：在订单创建成功路径调用 EmailNotificationService（或异步等价物）后，删除本 fail 并改为断言/verify。");
    }
}

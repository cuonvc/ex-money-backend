package com.exmoney.util;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class Constant {

    public static final Locale DEFAULT_LOCALE = Locale.forLanguageTag("us");
    public static final String API_BASE_ADMIN = "/api/admin";
    public static final String API_BASE_USER = "/api";

    public static class Status {
        public static final String ACTIVE = "ACTIVE"; //default
        public static final String PENDING = "PENDING";
        public static final String REJECTED = "REJECTED";
        public static final String INACTIVE = "INACTIVE";
        public static final String ARCHIVED = "ARCHIVED"; //for note
        public static final String EXECUTED = "EXECUTED";
        public static final String SCHEDULED = "SCHEDULED"; //task scheduling
        public static final String DELETED = "DELETED";
    }

    public static class RecordType {
        public static final String DEFAULT = "DEFAULT";
        public static final String CUSTOM = "CUSTOM";
    }

    //lưu category theo tài khoản hoặc theo ví (TH nhiều người dùng 1 ví)
    public static class CategorySaveType {
        public static final String WALLET = "WALLET";
        public static final String ACCOUNT = "ACCOUNT";
    }

    public static class WalletChangeUserAction {
        public static final String ADD = "ADD";
        public static final String REMOVE = "REMOVE";
        public static final String ABCXYZ = "abcxyz";

        public static final List<String> actions = Arrays.asList(ADD,REMOVE,ABCXYZ);
    }

    public static class ExpenseEntryType {
        public static final String INCOME = "INCOME";
        public static final String EXPENSE = "EXPENSE";

        public static final List<String> ENTRY_TYPES = List.of(INCOME, EXPENSE);
    }

    //Loại chi tiêu từ đâu
    //ngoài manual ra thì tất cả loại khác đều phải qua bước xác nhận
    public static class ExpenseType {
        public static final String MANUAL = "MANUAL";
        public static final String FROM_SCHEDULE = "FROM_SCHEDULE"; //sau khi clone từ origin
        public static final String SCHEDULE = "SCHEDULE"; //dùng cho origin expense (thuộc task)
        public static final String FROM_NOTE = "FROM_NOTE";
        public static final String FROM_BANK_NOTI = "FROM_BANK_NOTI";
        public static final String FROM_BANK_1 = "FROM_BANK_1";
        public static final String FROM_BANK_2 = "FROM_BANK_2";
        public static final String FROM_PAYPAL = "FROM_PAYPAL";
        public static final String FROM_MOMO = "FROM_MOMO";

        public static final List<String> EXPENSE_TYPES = List.of(
                MANUAL, FROM_NOTE, FROM_BANK_NOTI, FROM_BANK_1, FROM_BANK_2, FROM_PAYPAL, FROM_MOMO
        );
    }

    public static class SchedulerTaskName {
        //language file
        public static final String TASK_EXPENSE_AUTO = "task_name.expense_auto";
        public static final String TASK_ABC_XYZ = "task_name.abc_xyz";
    }

    public static class TableName {
        public static final String USER_TBL = "user";
        public static final String ACTION_LOG_TBL = "action_log";
        public static final String CURRENCY_UNIT_TBL = "currency_unit";
        public static final String DEVICE_INFO_TBL = "device_info";
        public static final String EXPENSE_CATEGORY_TBL = "expense_category";
        public static final String EXPENSE_TBL = "expense";
        public static final String EXPENSE_HISTORY_TBL = "expense_history";
        public static final String NOTIFICATION_TBL = "notification";
        public static final String NOTIFICATION_IDENTITY_TBL = "notification_identity";
        public static final String REFRESH_TOKEN_TBL = "refresh_token";
        public static final String TASK_SCHEDULER_CONFIG_TBL = "task_scheduler_config";
        public static final String USER_WALLET_TBL = "user_wallet";
        public static final String WALLET_TBL = "wallet";
        public static final String WALLET_HISTORY_TBL = "wallet_history";
    }

    public static class ScheduleTimeIntervalType {
        public static final String MONTHLY = "MONTHLY";
        public static final String WEEKLY = "WEEKLY";
        public static final String DAILY = "DAILY";
        public static final String PER_HOUR = "PER_HOUR";
        public static final String PER_MINUTE = "PER_MINUTE";

        public static final List<String> TIME_INTERVAL_TYPE_LIST = List.of(
                MONTHLY, WEEKLY, DAILY, PER_HOUR, PER_MINUTE
        );
    }

    public static class DeviceStatus {
        public static final String ACTIVE = "ACTIVE";
        public static final String INACTIVE = "INACTIVE";

        public static final String DELETED = "DELETED";
    }

    public static class NotificationComponent {
        public static final String TITLE = "TITLE";
        public static final String CONTENT = "CONTENT";
        public static final String TYPE = "TYPE";
    }

    public static class NotificationIdentityType {
        public static final String USER = "USER";
        public static final String GROUP = "GROUP";
    }

    public static class NotificationType {
        public static final String USER = "USER";
        public static final String WALLET = "WALLET";
        public static final String EXPENSE = "EXPENSE";
        public static final String CATEGORY = "CATEGORY";
        public static final String OTHER = "OTHER";
        public static final String SYSTEM = "SYSTEM";
    }

    public static class NotificationPriority {
        public static final String LOW = "LOW";
        public static final String NORMAL = "NORMAL";
        public static final String HIGH = "HIGH";
        public static final String CRITICAL = "CRITICAL";
    }

    public static class CurrencyUnit {
        public static final String EUR = "EUR"; //Chau Au
        public static final String GBP = "GBP"; //Bang Anh
        public static final String USD = "USD"; // My
        public static final String VND = "VND"; // Viet Nam Dong
    }

    public static class PageConstant {
        public static final String PAGE_NO = "0";
        public static final String PAGE_SIZE = "4";
        public static final String SORT_BY = "id";
        public static final String SORT_DIR = "asc";
    }

    public static class Role {
        public static final String ADMIN_ROLE = "ADMIN";
        public static final String USER_ROLE = "USER";
    }

    public static class UserProvider {
        public static final String SYSTEM_PROVIDER = "SYSTEM";
        public static final String GOOGLE_PROVIDER = "GOOGLE";
        public static final String GITHUB_PROVIDER = "GITHUB";
    }

    public static class ActionBy {
        public static final String ACTION_BY_USER = "ACTION_BY_USER";
        public static final String ACTION_BY_SYSTEM = "ACTION_BY_SYSTEM";
    }

}

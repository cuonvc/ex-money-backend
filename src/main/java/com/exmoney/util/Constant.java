package com.exmoney.util;

import java.util.Locale;

public class Constant {

    public static final Locale DEFAULT_LOCALE = Locale.forLanguageTag("us");
    public static final String API_BASE_ADMIN = "/api/admin";
    public static final String API_BASE_USER = "/api";

    public static class Status {
        public static final String ACTIVE = "ACTIVE";
        public static final String PENDING = "PENDING";
        public static final String REJECTED = "REJECTED";
        public static final String INACTIVE = "INACTIVE";
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

    //Loại chi tiêu từ đâu
    //ngoài manual ra thì tất cả loại khác đều phải qua bước xác nhận
    public static class ExpenseType {
        public static final String MANUAL = "MANUAL";
        public static final String FROM_NOTE = "FROM_NOTE";
        public static final String FROM_BANK_NOTI = "FROM_BANK_NOTI";
        public static final String FROM_BANK_1 = "FROM_BANK_1";
        public static final String FROM_BANK_2 = "FROM_BANK_2";
        public static final String FROM_PAYPAL = "FROM_PAYPAL";
        public static final String FROM_MOMO = "FROM_MOMO";
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

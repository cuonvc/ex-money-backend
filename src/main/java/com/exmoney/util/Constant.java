package com.exmoney.util;

import java.util.Locale;

public class Constant {

    public static final Locale DEFAULT_LOCALE = Locale.forLanguageTag("us");

    public static class Status {
        public static final String ACTIVE = "ACTIVE";
        public static final String PENDING = "PENDING";
        public static final String INACTIVE = "INACTIVE";
        public static final String DELETED = "DELETED";
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

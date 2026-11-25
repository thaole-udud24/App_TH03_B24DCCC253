package com.example.warehouse.data;

/**
 * Hằng số tên database, bảng và cột dùng chung cho DatabaseHelper.
 */
public final class DbContract {

    public static final String DB_NAME = "warehouse.db";
    public static final int DB_VERSION = 1;

    private DbContract() {
    }

    public static final class UserTable {
        public static final String TABLE_NAME = "users";
        public static final String COL_ID = "id";
        public static final String COL_USERNAME = "username";
        public static final String COL_PASSWORD = "password";

        private UserTable() {
        }
    }

    public static final class ProductTable {
        public static final String TABLE_NAME = "products";
        public static final String COL_ID = "id";
        public static final String COL_NAME = "name";
        public static final String COL_PRICE = "price";
        public static final String COL_QUANTITY = "quantity";
        public static final String COL_CATEGORY = "category";
        public static final String COL_ACTIVE = "active";
        public static final String COL_DESCRIPTION = "description";

        private ProductTable() {
        }
    }

    public static final class StockLogTable {
        public static final String TABLE_NAME = "stock_logs";
        public static final String COL_ID = "id";
        public static final String COL_PRODUCT_ID = "product_id";
        public static final String COL_TYPE = "type";
        public static final String COL_AMOUNT = "amount";
        public static final String COL_QUANTITY_AFTER = "quantity_after";
        public static final String COL_CREATED_AT = "created_at";

        public static final String TYPE_IN = "IN";
        public static final String TYPE_OUT = "OUT";

        private StockLogTable() {
        }
    }
}

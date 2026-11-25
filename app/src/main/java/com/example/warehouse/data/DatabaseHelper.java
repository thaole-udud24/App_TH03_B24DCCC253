package com.example.warehouse.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.warehouse.model.Product;
import com.example.warehouse.model.StockLog;
import com.example.warehouse.util.AppConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * Lớp duy nhất chứa toàn bộ truy vấn SQL của ứng dụng.
 * Activity không truy vấn trực tiếp DB.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static DatabaseHelper instance;

    public static synchronized DatabaseHelper getInstance(@NonNull Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    private DatabaseHelper(Context context) {
        super(context, DbContract.DB_NAME, null, DbContract.DB_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + DbContract.UserTable.TABLE_NAME + " (" +
                DbContract.UserTable.COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DbContract.UserTable.COL_USERNAME + " TEXT NOT NULL UNIQUE, " +
                DbContract.UserTable.COL_PASSWORD + " TEXT NOT NULL)");

        db.execSQL("CREATE TABLE " + DbContract.ProductTable.TABLE_NAME + " (" +
                DbContract.ProductTable.COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DbContract.ProductTable.COL_NAME + " TEXT NOT NULL, " +
                DbContract.ProductTable.COL_PRICE + " INTEGER NOT NULL, " +
                DbContract.ProductTable.COL_QUANTITY + " INTEGER NOT NULL, " +
                DbContract.ProductTable.COL_CATEGORY + " TEXT NOT NULL, " +
                DbContract.ProductTable.COL_ACTIVE + " INTEGER NOT NULL, " +
                DbContract.ProductTable.COL_DESCRIPTION + " TEXT)");

        db.execSQL("CREATE TABLE " + DbContract.StockLogTable.TABLE_NAME + " (" +
                DbContract.StockLogTable.COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DbContract.StockLogTable.COL_PRODUCT_ID + " INTEGER NOT NULL, " +
                DbContract.StockLogTable.COL_TYPE + " TEXT NOT NULL, " +
                DbContract.StockLogTable.COL_AMOUNT + " INTEGER NOT NULL, " +
                DbContract.StockLogTable.COL_QUANTITY_AFTER + " INTEGER NOT NULL, " +
                DbContract.StockLogTable.COL_CREATED_AT + " INTEGER NOT NULL, " +
                "FOREIGN KEY(" + DbContract.StockLogTable.COL_PRODUCT_ID + ") REFERENCES " +
                DbContract.ProductTable.TABLE_NAME + "(" + DbContract.ProductTable.COL_ID + ") ON DELETE CASCADE)");

        insertUser(db, "admin", "123456");

        insertSeedProduct(db, "Chuột không dây", 150000, 20, AppConstants.CATEGORY_ELECTRONICS);
        insertSeedProduct(db, "Bàn phím cơ", 450000, 3, AppConstants.CATEGORY_ELECTRONICS);
        insertSeedProduct(db, "Sổ tay A5", 25000, 40, AppConstants.CATEGORY_OFFICE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Chưa có phiên bản cũ hơn cần nâng cấp.
    }

    private void insertSeedProduct(SQLiteDatabase db, String name, long price, int quantity, String category) {
        ContentValues values = new ContentValues();
        values.put(DbContract.ProductTable.COL_NAME, name);
        values.put(DbContract.ProductTable.COL_PRICE, price);
        values.put(DbContract.ProductTable.COL_QUANTITY, quantity);
        values.put(DbContract.ProductTable.COL_CATEGORY, category);
        values.put(DbContract.ProductTable.COL_ACTIVE, 1);
        values.put(DbContract.ProductTable.COL_DESCRIPTION, "");
        db.insert(DbContract.ProductTable.TABLE_NAME, null, values);
    }

    private void insertUser(SQLiteDatabase db, String username, String password) {
        ContentValues values = new ContentValues();
        values.put(DbContract.UserTable.COL_USERNAME, username);
        values.put(DbContract.UserTable.COL_PASSWORD, password);
        db.insert(DbContract.UserTable.TABLE_NAME, null, values);
    }

    // ---------------- users ----------------

    /** Thêm tài khoản; trả về false nếu tên đăng nhập đã tồn tại (ràng buộc UNIQUE). */
    public boolean insertUser(@NonNull String username, @NonNull String password) {
        SQLiteDatabase db = getWritableDatabase();
        long id = db.insert(DbContract.UserTable.TABLE_NAME, null, userValues(username, password));
        return id != -1;
    }

    private ContentValues userValues(String username, String password) {
        ContentValues values = new ContentValues();
        values.put(DbContract.UserTable.COL_USERNAME, username);
        values.put(DbContract.UserTable.COL_PASSWORD, password);
        return values;
    }

    /** Trả về true nếu có dòng khớp username/password trong bảng users. */
    public boolean checkLogin(@NonNull String username, @NonNull String password) {
        SQLiteDatabase db = getReadableDatabase();
        try (Cursor cursor = db.query(
                DbContract.UserTable.TABLE_NAME,
                new String[]{DbContract.UserTable.COL_ID},
                DbContract.UserTable.COL_USERNAME + " = ? AND " + DbContract.UserTable.COL_PASSWORD + " = ?",
                new String[]{username, password},
                null, null, null)) {
            return cursor.getCount() > 0;
        }
    }

    public boolean usernameExists(@NonNull String username) {
        SQLiteDatabase db = getReadableDatabase();
        try (Cursor cursor = db.query(
                DbContract.UserTable.TABLE_NAME,
                new String[]{DbContract.UserTable.COL_ID},
                DbContract.UserTable.COL_USERNAME + " = ?",
                new String[]{username},
                null, null, null)) {
            return cursor.getCount() > 0;
        }
    }

    // ---------------- products ----------------

    /** Thêm sản phẩm, trả về id vừa thêm. */
    public long insertProduct(@NonNull Product product) {
        SQLiteDatabase db = getWritableDatabase();
        return db.insert(DbContract.ProductTable.TABLE_NAME, null, productValues(product));
    }

    public void updateProduct(@NonNull Product product) {
        SQLiteDatabase db = getWritableDatabase();
        db.update(DbContract.ProductTable.TABLE_NAME, productValues(product),
                DbContract.ProductTable.COL_ID + " = ?",
                new String[]{String.valueOf(product.getId())});
    }

    private ContentValues productValues(Product product) {
        ContentValues values = new ContentValues();
        values.put(DbContract.ProductTable.COL_NAME, product.getName());
        values.put(DbContract.ProductTable.COL_PRICE, product.getPrice());
        values.put(DbContract.ProductTable.COL_QUANTITY, product.getQuantity());
        values.put(DbContract.ProductTable.COL_CATEGORY, product.getCategory());
        values.put(DbContract.ProductTable.COL_ACTIVE, product.isActive() ? 1 : 0);
        values.put(DbContract.ProductTable.COL_DESCRIPTION, product.getDescription());
        return values;
    }

    public boolean productNameExists(@NonNull String name) {
        SQLiteDatabase db = getReadableDatabase();
        try (Cursor cursor = db.query(
                DbContract.ProductTable.TABLE_NAME,
                new String[]{DbContract.ProductTable.COL_ID},
                DbContract.ProductTable.COL_NAME + " = ?",
                new String[]{name},
                null, null, null)) {
            return cursor.getCount() > 0;
        }
    }

    /** Lấy toàn bộ sản phẩm để hiển thị danh sách. */
    @NonNull
    public List<Product> getAllProducts() {
        return searchProducts(null, null, false, AppConstants.SORT_NAME_ASC);
    }

    /**
     * Tìm kiếm/lọc/sắp xếp sản phẩm bằng một câu truy vấn có tham số.
     *
     * @param keyword       từ khóa tên sản phẩm, null hoặc rỗng = không lọc theo tên
     * @param category      loại sản phẩm, null = tất cả loại
     * @param onlyLowStock  true để chỉ lấy sản phẩm sắp hết hàng
     * @param sortBy        một trong các hằng số AppConstants.SORT_*
     */
    @NonNull
    public List<Product> searchProducts(@Nullable String keyword, @Nullable String category,
                                         boolean onlyLowStock, @NonNull String sortBy) {
        List<Product> result = new ArrayList<>();
        StringBuilder selection = new StringBuilder();
        List<String> args = new ArrayList<>();

        if (keyword != null && !keyword.trim().isEmpty()) {
            selection.append(DbContract.ProductTable.COL_NAME).append(" LIKE ?");
            args.add("%" + keyword.trim() + "%");
        }
        if (category != null && !category.isEmpty()) {
            appendAnd(selection);
            selection.append(DbContract.ProductTable.COL_CATEGORY).append(" = ?");
            args.add(category);
        }
        if (onlyLowStock) {
            appendAnd(selection);
            selection.append(DbContract.ProductTable.COL_QUANTITY).append(" < ?");
            args.add(String.valueOf(AppConstants.LOW_STOCK_THRESHOLD));
        }

        SQLiteDatabase db = getReadableDatabase();
        try (Cursor cursor = db.query(
                DbContract.ProductTable.TABLE_NAME,
                null,
                selection.length() == 0 ? null : selection.toString(),
                args.isEmpty() ? null : args.toArray(new String[0]),
                null, null,
                resolveOrderBy(sortBy))) {
            while (cursor.moveToNext()) {
                result.add(cursorToProduct(cursor));
            }
        }
        return result;
    }

    /** Lọc theo trạng thái ngừng kinh doanh (active = 0), dùng riêng vì khác chip "sắp hết hàng". */
    @NonNull
    public List<Product> getInactiveProducts(@Nullable String keyword, @NonNull String sortBy) {
        List<Product> result = new ArrayList<>();
        StringBuilder selection = new StringBuilder(DbContract.ProductTable.COL_ACTIVE + " = 0");
        List<String> args = new ArrayList<>();
        if (keyword != null && !keyword.trim().isEmpty()) {
            selection.append(" AND ").append(DbContract.ProductTable.COL_NAME).append(" LIKE ?");
            args.add("%" + keyword.trim() + "%");
        }
        SQLiteDatabase db = getReadableDatabase();
        try (Cursor cursor = db.query(
                DbContract.ProductTable.TABLE_NAME,
                null,
                selection.toString(),
                args.isEmpty() ? null : args.toArray(new String[0]),
                null, null,
                resolveOrderBy(sortBy))) {
            while (cursor.moveToNext()) {
                result.add(cursorToProduct(cursor));
            }
        }
        return result;
    }

    private void appendAnd(StringBuilder selection) {
        if (selection.length() > 0) {
            selection.append(" AND ");
        }
    }

    private String resolveOrderBy(String sortBy) {
        switch (sortBy) {
            case AppConstants.SORT_PRICE_ASC:
                return DbContract.ProductTable.COL_PRICE + " ASC";
            case AppConstants.SORT_PRICE_DESC:
                return DbContract.ProductTable.COL_PRICE + " DESC";
            case AppConstants.SORT_QUANTITY_ASC:
                return DbContract.ProductTable.COL_QUANTITY + " ASC";
            case AppConstants.SORT_QUANTITY_DESC:
                return DbContract.ProductTable.COL_QUANTITY + " DESC";
            default:
                return DbContract.ProductTable.COL_NAME + " ASC";
        }
    }

    /** Lấy một sản phẩm theo id, null nếu không còn tồn tại. */
    @Nullable
    public Product getProductById(long id) {
        SQLiteDatabase db = getReadableDatabase();
        try (Cursor cursor = db.query(
                DbContract.ProductTable.TABLE_NAME,
                null,
                DbContract.ProductTable.COL_ID + " = ?",
                new String[]{String.valueOf(id)},
                null, null, null)) {
            if (cursor.moveToFirst()) {
                return cursorToProduct(cursor);
            }
            return null;
        }
    }

    /** Cập nhật số lượng (dùng cho Nhập thêm / Bán). */
    public void updateQuantity(long id, int quantity) {
        ContentValues values = new ContentValues();
        values.put(DbContract.ProductTable.COL_QUANTITY, quantity);
        getWritableDatabase().update(DbContract.ProductTable.TABLE_NAME, values,
                DbContract.ProductTable.COL_ID + " = ?", new String[]{String.valueOf(id)});
    }

    public void updateActive(long id, boolean active) {
        ContentValues values = new ContentValues();
        values.put(DbContract.ProductTable.COL_ACTIVE, active ? 1 : 0);
        getWritableDatabase().update(DbContract.ProductTable.TABLE_NAME, values,
                DbContract.ProductTable.COL_ID + " = ?", new String[]{String.valueOf(id)});
    }

    /**
     * Thay đổi số lượng và ghi log trong một transaction (nhập/bán).
     *
     * @param amount số lượng thay đổi, luôn dương
     * @param isIn   true = nhập, false = bán
     * @return số lượng mới sau khi thay đổi
     */
    public int applyStockChange(long productId, int amount, boolean isIn) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            Product product = getProductById(productId);
            if (product == null) {
                throw new IllegalStateException("Sản phẩm không còn tồn tại");
            }
            int newQuantity = isIn ? product.getQuantity() + amount : product.getQuantity() - amount;
            if (newQuantity < 0) {
                throw new IllegalArgumentException("Số lượng bán vượt tồn kho");
            }
            updateQuantity(productId, newQuantity);
            insertStockLog(productId,
                    isIn ? DbContract.StockLogTable.TYPE_IN : DbContract.StockLogTable.TYPE_OUT,
                    amount, newQuantity);
            db.setTransactionSuccessful();
            return newQuantity;
        } finally {
            db.endTransaction();
        }
    }

    public void deleteProduct(long id) {
        getWritableDatabase().delete(DbContract.ProductTable.TABLE_NAME,
                DbContract.ProductTable.COL_ID + " = ?", new String[]{String.valueOf(id)});
    }

    /** Chèn lại một sản phẩm đã xóa với đúng id cũ (dùng cho hoàn tác xóa). */
    public void restoreProduct(@NonNull Product product) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = productValues(product);
        values.put(DbContract.ProductTable.COL_ID, product.getId());
        db.insertWithOnConflict(DbContract.ProductTable.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public long getTotalValue() {
        SQLiteDatabase db = getReadableDatabase();
        try (Cursor cursor = db.rawQuery(
                "SELECT SUM(" + DbContract.ProductTable.COL_PRICE + " * " + DbContract.ProductTable.COL_QUANTITY + ") " +
                        "FROM " + DbContract.ProductTable.TABLE_NAME, null)) {
            if (cursor.moveToFirst() && !cursor.isNull(0)) {
                return cursor.getLong(0);
            }
            return 0L;
        }
    }

    public int countLowStock() {
        SQLiteDatabase db = getReadableDatabase();
        try (Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM " + DbContract.ProductTable.TABLE_NAME +
                        " WHERE " + DbContract.ProductTable.COL_QUANTITY + " < ?",
                new String[]{String.valueOf(AppConstants.LOW_STOCK_THRESHOLD)})) {
            return cursor.moveToFirst() ? cursor.getInt(0) : 0;
        }
    }

    public int countAllProducts() {
        SQLiteDatabase db = getReadableDatabase();
        try (Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM " + DbContract.ProductTable.TABLE_NAME, null)) {
            return cursor.moveToFirst() ? cursor.getInt(0) : 0;
        }
    }

    /** Tổng giá trị kho theo từng loại, dùng cho biểu đồ thanh. */
    @NonNull
    public List<CategoryValue> getValueByCategory() {
        List<CategoryValue> result = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        try (Cursor cursor = db.rawQuery(
                "SELECT " + DbContract.ProductTable.COL_CATEGORY + ", " +
                        "SUM(" + DbContract.ProductTable.COL_PRICE + " * " + DbContract.ProductTable.COL_QUANTITY + ") " +
                        "FROM " + DbContract.ProductTable.TABLE_NAME +
                        " GROUP BY " + DbContract.ProductTable.COL_CATEGORY, null)) {
            while (cursor.moveToNext()) {
                result.add(new CategoryValue(cursor.getString(0), cursor.getLong(1)));
            }
        }
        return result;
    }

    // ---------------- stock logs ----------------

    public void insertStockLog(long productId, @NonNull String type, int amount, int quantityAfter) {
        ContentValues values = new ContentValues();
        values.put(DbContract.StockLogTable.COL_PRODUCT_ID, productId);
        values.put(DbContract.StockLogTable.COL_TYPE, type);
        values.put(DbContract.StockLogTable.COL_AMOUNT, amount);
        values.put(DbContract.StockLogTable.COL_QUANTITY_AFTER, quantityAfter);
        values.put(DbContract.StockLogTable.COL_CREATED_AT, System.currentTimeMillis());
        getWritableDatabase().insert(DbContract.StockLogTable.TABLE_NAME, null, values);
    }

    @NonNull
    public List<StockLog> getStockLogs(long productId) {
        List<StockLog> result = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        try (Cursor cursor = db.query(
                DbContract.StockLogTable.TABLE_NAME,
                null,
                DbContract.StockLogTable.COL_PRODUCT_ID + " = ?",
                new String[]{String.valueOf(productId)},
                null, null,
                DbContract.StockLogTable.COL_CREATED_AT + " DESC")) {
            while (cursor.moveToNext()) {
                result.add(new StockLog(
                        cursor.getLong(cursor.getColumnIndexOrThrow(DbContract.StockLogTable.COL_ID)),
                        cursor.getLong(cursor.getColumnIndexOrThrow(DbContract.StockLogTable.COL_PRODUCT_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DbContract.StockLogTable.COL_TYPE)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(DbContract.StockLogTable.COL_AMOUNT)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(DbContract.StockLogTable.COL_QUANTITY_AFTER)),
                        cursor.getLong(cursor.getColumnIndexOrThrow(DbContract.StockLogTable.COL_CREATED_AT))));
            }
        }
        return result;
    }

    private Product cursorToProduct(Cursor cursor) {
        return new Product(
                cursor.getLong(cursor.getColumnIndexOrThrow(DbContract.ProductTable.COL_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(DbContract.ProductTable.COL_NAME)),
                cursor.getLong(cursor.getColumnIndexOrThrow(DbContract.ProductTable.COL_PRICE)),
                cursor.getInt(cursor.getColumnIndexOrThrow(DbContract.ProductTable.COL_QUANTITY)),
                cursor.getString(cursor.getColumnIndexOrThrow(DbContract.ProductTable.COL_CATEGORY)),
                cursor.getInt(cursor.getColumnIndexOrThrow(DbContract.ProductTable.COL_ACTIVE)) == 1,
                cursor.getString(cursor.getColumnIndexOrThrow(DbContract.ProductTable.COL_DESCRIPTION)));
    }

    /** Cặp loại sản phẩm – tổng giá trị kho, dùng cho biểu đồ. */
    public static final class CategoryValue {
        public final String category;
        public final long value;

        public CategoryValue(String category, long value) {
            this.category = category;
            this.value = value;
        }
    }
}

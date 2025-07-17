package com.example.project;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class DBHelper extends SQLiteOpenHelper {


    private static final String TAG = "DBHelper";
    private static final String DATABASE_NAME = "Users.db";
    private static final int DATABASE_VERSION = 4;

    private final ConcurrentHashMap<String, SQLiteStatement> statementCache = new ConcurrentHashMap<>();


    private static DBHelper instance;

    public static synchronized DBHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DBHelper(context.getApplicationContext());
        }
        return instance;
    }

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 4);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.beginTransaction();

            String createUsersTable = "CREATE TABLE users (" +
                    "email TEXT PRIMARY KEY, " +
                    "firstName TEXT, " +
                    "lastName TEXT, " +
                    "password TEXT, " +
                    "phone TEXT, " +
                    "gender TEXT, " +
                    "country TEXT, " +
                    "city TEXT)";
            db.execSQL(createUsersTable);
            Log.d(TAG, "Users table created");

            String createPropertiesTable = "CREATE TABLE IF NOT EXISTS properties (" +
                    "id INTEGER PRIMARY KEY, " +
                    "title TEXT NOT NULL, " +
                    "type TEXT, " +
                    "price REAL, " +
                    "location TEXT, " +
                    "area TEXT, " +
                    "bedrooms INTEGER, " +
                    "bathrooms INTEGER, " +
                    "image_url TEXT, " +
                    "description TEXT)";
            db.execSQL(createPropertiesTable);
            Log.d(TAG, "Properties table created");

            String createFavoritesTable = "CREATE TABLE favorites (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "user_email TEXT NOT NULL, " +
                    "property_id INTEGER NOT NULL, " +
                    "created_at INTEGER DEFAULT (strftime('%s','now')), " +
                    "FOREIGN KEY(user_email) REFERENCES users(email), " +
                    "FOREIGN KEY(property_id) REFERENCES properties(id), " +
                    "UNIQUE(user_email, property_id))";
            db.execSQL(createFavoritesTable);

            String createReservationsTable = "CREATE TABLE reservations (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "user_email TEXT NOT NULL, " +
                    "property_id INTEGER NOT NULL, " +
                    "reservation_date INTEGER NOT NULL, " +
                    "created_at INTEGER DEFAULT (strftime('%s','now')), " +
                    "status TEXT DEFAULT 'active', " +
                    "image_resource_id INTEGER DEFAULT 0, " +
                    "FOREIGN KEY(user_email) REFERENCES users(email), " +
                    "FOREIGN KEY(property_id) REFERENCES properties(id), " +
                    "UNIQUE(user_email, property_id))";
            db.execSQL(createReservationsTable);
            Log.d(TAG, "Reservations table created");

            db.execSQL("CREATE INDEX idx_favorites_user ON favorites(user_email)");
            db.execSQL("CREATE INDEX idx_reservations_user ON reservations(user_email)");
            db.execSQL("CREATE INDEX idx_reservations_property ON reservations(property_id)");
            db.execSQL("CREATE INDEX idx_reservations_status ON reservations(status)");
            Log.d(TAG, "Indexes created");

            String insertAdmin = "INSERT INTO users (email, firstName, lastName, password, phone, gender, country, city) " +
                    "VALUES ('admin@admin.com', 'Admin', 'User', 'Admin123!', '0000000000', 'male', 'Palestine', 'Ramallah')";
            db.execSQL(insertAdmin);
            Log.d(TAG, "Admin user inserted");

            db.setTransactionSuccessful();
            Log.d(TAG, "Database created successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error creating database", e);
        } finally {
            db.endTransaction();
        }
    }

@Override
public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    Log.d(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion);

    try {
        db.beginTransaction();

        if (oldVersion < 4) {
            try {
                db.execSQL("ALTER TABLE reservations ADD COLUMN image_resource_id INTEGER DEFAULT 0");
                Log.d(TAG, "Added image_resource_id column to reservations table");
            } catch (Exception e) {
                Log.w(TAG, "Column might already exist, skipping ALTER TABLE", e);
            }
        }

        db.setTransactionSuccessful();
    } catch (Exception e) {
        Log.e(TAG, "Error upgrading database", e);
        db.execSQL("DROP TABLE IF EXISTS favorites");
        db.execSQL("DROP TABLE IF EXISTS reservations");
        db.execSQL("DROP TABLE IF EXISTS properties");
        db.execSQL("DROP TABLE IF EXISTS users");
        onCreate(db);
    } finally {
        db.endTransaction();
    }
}

    private String getStringSafely(Cursor cursor, String columnName) {
        int columnIndex = cursor.getColumnIndex(columnName);
        if (columnIndex >= 0) {
            return cursor.getString(columnIndex);
        }
        Log.w(TAG, "Column not found: " + columnName);
        return null;
    }

    private int getIntSafely(Cursor cursor, String columnName, int defaultValue) {
        int columnIndex = cursor.getColumnIndex(columnName);
        if (columnIndex >= 0) {
            return cursor.getInt(columnIndex);
        }
        Log.w(TAG, "Column not found: " + columnName);
        return defaultValue;
    }

    private double getDoubleSafely(Cursor cursor, String columnName, double defaultValue) {
        int columnIndex = cursor.getColumnIndex(columnName);
        if (columnIndex >= 0) {
            return cursor.getDouble(columnIndex);
        }
        Log.w(TAG, "Column not found: " + columnName);
        return defaultValue;
    }
    public int getTotalReservationsCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM reservations", null);
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    public ArrayList<String> getTopReservingCountries(int limit) {
        ArrayList<String> topCountries = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT u.country, COUNT(r.id) AS reservation_count " +
                "FROM users u " +
                "JOIN reservations r ON u.email = r.user_email " +
                "GROUP BY u.country " +
                "ORDER BY reservation_count DESC " +
                "LIMIT ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(limit)});

        while (cursor.moveToNext()) {
            String country = cursor.getString(0);
            topCountries.add(country);
        }

        cursor.close();
        return topCountries;
    }

    private long getLongSafely(Cursor cursor, String columnName, long defaultValue) {
        int columnIndex = cursor.getColumnIndex(columnName);
        if (columnIndex >= 0) {
            return cursor.getLong(columnIndex);
        }
        Log.w(TAG, "Column not found: " + columnName);
        return defaultValue;
    }
    public boolean insertUser(User user) {
        if (isUserExists(user.getEmail()))
            return false;

        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            db.beginTransaction();

            ContentValues values = new ContentValues();
            values.put("email", user.getEmail());
            values.put("firstName", user.getFirstName());
            values.put("lastName", user.getLastName());
            values.put("password", user.getPassword());
            values.put("phone", user.getPhone());
            values.put("gender", user.getGender());
            values.put("country", user.getCountry());
            values.put("city", user.getCity());

            long result = db.insert("users", null, values);
            db.setTransactionSuccessful();
            return result != -1;
        } catch (Exception e) {
            Log.e(TAG, "Error inserting user", e);
            return false;
        } finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
        }
    }
   public boolean isUserExists(String email) {
       SQLiteDatabase db = this.getReadableDatabase();
       Cursor cursor = db.rawQuery("SELECT * FROM Users WHERE email=?", new String[]{email});
       boolean exists = cursor.getCount() > 0;
       cursor.close();
       return exists;
   }
    public User validateData(String email, String password) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor;
        cursor = db.rawQuery("SELECT * FROM users WHERE email = ? AND password = ?", new String[]{email, password});

        if (cursor.moveToFirst()) {
            String firstName = cursor.getString(cursor.getColumnIndexOrThrow("firstName"));
            String lastName = cursor.getString(cursor.getColumnIndexOrThrow("lastName"));
            String phone = cursor.getString(cursor.getColumnIndexOrThrow("phone"));
            String gender = cursor.getString(cursor.getColumnIndexOrThrow("gender"));
            String country = cursor.getString(cursor.getColumnIndexOrThrow("country"));
            String city = cursor.getString(cursor.getColumnIndexOrThrow("city"));

            cursor.close();
            return new User(email, firstName, lastName, password, phone, gender, country, city);
        }

        cursor.close();
        db.close();
        return null;
    }
    // ضفتها جديد من دانا
    public boolean deleteCustomerByEmail(String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete("users", "email=?", new String[]{email});
        db.close();
        return result > 0;
    }
    public boolean addAdmin(User user) {
        if (isUserExists(user.getEmail()))
            return false;

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("email", user.getEmail());
        cv.put("firstName", user.getFirstName());
        cv.put("lastName", user.getLastName());
        cv.put("password", user.getPassword());
        cv.put("phone", user.getPhone());
        cv.put("gender", user.getGender());
        cv.put("country", user.getCountry());
        cv.put("city", user.getCity());

        long result = db.insert("users", null, cv);
        db.close();
        return result != -1;
    }
    public User getUserByEmail(String email) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = this.getReadableDatabase();
            cursor = db.rawQuery("SELECT * FROM users WHERE email = ? LIMIT 1", new String[]{email});

            if (cursor.moveToFirst()) {
                return createUserFromCursor(cursor);
            }
            return null;
        } catch (Exception e) {
            Log.e(TAG, "Error getting user by email", e);
            return null;
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }
    }

    private User createUserFromCursor(Cursor cursor) {
        String email = getStringSafely(cursor, "email");
        String firstName = getStringSafely(cursor, "firstName");
        String lastName = getStringSafely(cursor, "lastName");
        String password = getStringSafely(cursor, "password");
        String phone = getStringSafely(cursor, "phone");
        String gender = getStringSafely(cursor, "gender");
        String country = getStringSafely(cursor, "country");
        String city = getStringSafely(cursor, "city");

        return new User(email, firstName, lastName, password, phone, gender, country, city);
    }

    public boolean updateUser(User user) {
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            db.beginTransaction();

            ContentValues values = new ContentValues();
            values.put("firstName", user.getFirstName());
            values.put("lastName", user.getLastName());
            values.put("password", user.getPassword());
            values.put("phone", user.getPhone());
            values.put("gender", user.getGender());
            values.put("country", user.getCountry());
            values.put("city", user.getCity());

            int rowsAffected = db.update("users", values, "email = ?", new String[]{user.getEmail()});
            db.setTransactionSuccessful();
            return rowsAffected > 0;
        } catch (Exception e) {
            Log.e(TAG, "Error updating user", e);
            return false;
        } finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
        }
    }

    public long insertProperty(Property property) {
        SQLiteDatabase db = this.getWritableDatabase();

        String insert = "INSERT INTO properties (id, title, type, price, location, area, bedrooms, bathrooms, image_url, description) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        SQLiteStatement stmt = db.compileStatement(insert);

        stmt.bindLong(1, property.getId());
        stmt.bindString(2, property.getTitle());
        stmt.bindString(3, property.getType());
        stmt.bindDouble(4, property.getPrice());
        stmt.bindString(5, property.getLocation());
        stmt.bindString(6, property.getArea());
        stmt.bindLong(7, property.getBedrooms());
        stmt.bindLong(8, property.getBathrooms());
        stmt.bindString(9, property.getImageUrl());
        stmt.bindString(10, property.getDescription());

        long result = stmt.executeInsert();
        db.close();
        return result;
    }
    public Property getPropertyById(int propertyId) {
        Cursor cursor = null;
        Property property = null;

        try {
            SQLiteDatabase db = this.getReadableDatabase();
            String query = "SELECT * FROM properties WHERE id = ?";
            cursor = db.rawQuery(query, new String[]{String.valueOf(propertyId)});

            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    property = new Property();
                    property.id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                    property.title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
                    property.type = cursor.getString(cursor.getColumnIndexOrThrow("type"));
                    property.price = cursor.getDouble(cursor.getColumnIndexOrThrow("price"));
                    property.location = cursor.getString(cursor.getColumnIndexOrThrow("location"));
                    property.area = cursor.getString(cursor.getColumnIndexOrThrow("area"));
                    property.bedrooms = cursor.getInt(cursor.getColumnIndexOrThrow("bedrooms"));
                    property.bathrooms = cursor.getInt(cursor.getColumnIndexOrThrow("bathrooms"));
                    property.image_url = cursor.getString(cursor.getColumnIndexOrThrow("image_url"));
                    property.description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                    Log.d(TAG, "📦 Property loaded: ID=" + property.id + ", Title=" + property.title);
                } else {
                    Log.w(TAG, "No property found with ID: " + propertyId);
                }
            } else {
                Log.e(TAG, "Cursor is null for property ID: " + propertyId);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error fetching property by ID: " + propertyId, e);
        } finally {
            if (cursor != null) cursor.close();
        }

        return property;
    }
    public boolean addReservationn(String userEmail, int propertyId, long reservationDate, int imageResId) {
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            if (db == null || !db.isOpen()) {
                Log.e(TAG, "Database is not open");
                return false;
            }
            db.beginTransaction();

            ContentValues values = new ContentValues();
            values.put("user_email", userEmail);
            values.put("property_id", propertyId);
            values.put("reservation_date", reservationDate);
            values.put("image_resource_id", imageResId);
            values.put("status", "active");

            long result = db.insert("reservations", null, values);
            if (result == -1) {
                Log.e(TAG, "Failed to insert reservation for user: " + userEmail + ", propertyId: " + propertyId);
                return false;
            }

            db.setTransactionSuccessful();
            Log.d(TAG, "Reservation added with ID: " + result + " for user: " + userEmail + ", propertyId: " + propertyId);
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error adding reservation", e);
            return false;
        } finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
        }
    }

    public boolean removeReservation(int reservationId, String userEmail) {
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            db.beginTransaction();

            int rowsAffected = db.delete("reservations",
                    "id = ? AND user_email = ?",
                    new String[]{String.valueOf(reservationId), userEmail});

            db.setTransactionSuccessful();
            Log.d(TAG, "Remove reservation - ID: " + reservationId + ", User: " + userEmail + ", Rows affected: " + rowsAffected);
            return rowsAffected > 0;
        } catch (Exception e) {
            Log.e(TAG, "Error removing reservation", e);
            return false;
        } finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
        }
    }

    public boolean isReserved(String userEmail, int propertyId) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = this.getReadableDatabase();
            cursor = db.rawQuery("SELECT 1 FROM reservations WHERE user_email = ? AND property_id = ? AND status = 'active' LIMIT 1",
                    new String[]{userEmail, String.valueOf(propertyId)});
            boolean isReserved = cursor.moveToFirst();
            Log.d(TAG, "Is reserved check - User: " + userEmail + ", Property: " + propertyId + ", Result: " + isReserved);
            return isReserved;
        } catch (Exception e) {
            Log.e(TAG, "Error checking reservation status", e);
            return false;
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }
    }

    public ArrayList<Reservation> getUserReservations(String userEmail) {
        ArrayList<Reservation> reservations = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;

        Log.d(TAG, "Getting reservations for user: " + userEmail);

        try {
            db = this.getReadableDatabase();

            String query = "SELECT * FROM reservations WHERE user_email = ? AND status = 'active' ORDER BY created_at DESC";
            cursor = db.rawQuery(query, new String[]{userEmail});
            Log.d(TAG, "Query executed, cursor count: " + cursor.getCount());

            while (cursor.moveToNext()) {
                Reservation reservation = new Reservation();
                reservation.id = getIntSafely(cursor, "id", 0);
                reservation.userEmail = getStringSafely(cursor, "user_email");
                reservation.propertyId = getIntSafely(cursor, "property_id", 0);
                reservation.reservationDate = getLongSafely(cursor, "reservation_date", 0);
                reservation.status = getStringSafely(cursor, "status");
                reservation.imageResourceId = getIntSafely(cursor, "image_resource_id", 0);

                Property property = getPropertyById(reservation.propertyId);
                reservation.property = property;
                Log.d(TAG, " testtttttttttttttttttttttt     Reservation  propertyId: " + reservation.propertyId);

                reservations.add(reservation);

            }

        } catch (Exception e) {
            Log.e(TAG, "Error getting user reservations", e);
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }

        Log.d(TAG, "Returning " + reservations.size() + " reservations for user: " + userEmail);
        return reservations;
    }
    public boolean addToFavorites(String userEmail, int propertyId) {
        SQLiteDatabase db = null;


        try {
            db = this.getWritableDatabase();
            db.beginTransaction();

            ContentValues values = new ContentValues();
            values.put("user_email", userEmail);
            values.put("property_id", propertyId);
            values.put("created_at", System.currentTimeMillis() / 1000);

            long result = db.insertWithOnConflict("favorites", null, values, SQLiteDatabase.CONFLICT_IGNORE);

            if (result == -1) {
                Log.w(TAG, "Favorite already exists for user: " + userEmail + ", property: " + propertyId);
            } else {
                Log.d(TAG, "Favorite added: user=" + userEmail + ", property=" + propertyId);
            }

            db.setTransactionSuccessful();
            return result != -1;
        } catch (Exception e) {
            Log.e(TAG, "Error adding to favorites", e);
            return false;
        } finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
        }
    }



    public boolean removeFromFavorites(String userEmail, int propertyId) {
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            db.beginTransaction();

            int rowsAffected = db.delete("favorites",
                    "user_email = ? AND property_id = ?",
                    new String[]{userEmail, String.valueOf(propertyId)});

            db.setTransactionSuccessful();
            return rowsAffected > 0;
        } catch (Exception e) {
            Log.e(TAG, "Error removing from favorites", e);
            return false;
        } finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
        }
    }

    public boolean isFavorite(String userEmail, int propertyId) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = this.getReadableDatabase();
            cursor = db.rawQuery("SELECT 1 FROM favorites WHERE user_email = ? AND property_id = ? LIMIT 1",
                    new String[]{userEmail, String.valueOf(propertyId)});
            return cursor.moveToFirst();
        } catch (Exception e) {
            Log.e(TAG, "Error checking favorite status", e);
            return false;
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }
    }

   public ArrayList<Favorite> getUserFavorites(String userEmail) {
       ArrayList<Favorite> favorites = new ArrayList<>();
       SQLiteDatabase db = null;
       Cursor cursor = null;

       try {
           db = this.getReadableDatabase();

            String query = "SELECT f.id as favorite_id, f.user_email, f.property_id, f.created_at, " +
                   "p.title, p.type, p.price, p.location, p.area, p.bedrooms, p.bathrooms, p.image_url, p.description " +
                   "FROM favorites f " +
                   "LEFT JOIN properties p ON f.property_id = p.id " +
                   "WHERE f.user_email = ? " +
                   "ORDER BY f.created_at DESC";


           cursor = db.rawQuery(query, new String[]{userEmail});

           while (cursor.moveToNext()) {
               Favorite favorite = new Favorite();
               favorite.id = cursor.getInt(cursor.getColumnIndexOrThrow("favorite_id"));
               favorite.userEmail = cursor.getString(cursor.getColumnIndexOrThrow("user_email"));
               favorite.propertyId = cursor.getInt(cursor.getColumnIndexOrThrow("property_id"));
               favorite.createdAt = cursor.getLong(cursor.getColumnIndexOrThrow("created_at"));

               Property property = new Property();
               property.id = favorite.propertyId;
               property.title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
               property.type = cursor.getString(cursor.getColumnIndexOrThrow("type"));
               property.price = cursor.getDouble(cursor.getColumnIndexOrThrow("price"));
               property.location = cursor.getString(cursor.getColumnIndexOrThrow("location"));
               property.area = cursor.getString(cursor.getColumnIndexOrThrow("area"));
               property.bedrooms = cursor.getInt(cursor.getColumnIndexOrThrow("bedrooms"));
               property.bathrooms = cursor.getInt(cursor.getColumnIndexOrThrow("bathrooms"));
               property.image_url = cursor.getString(cursor.getColumnIndexOrThrow("image_url"));
               property.description = cursor.getString(cursor.getColumnIndexOrThrow("description"));

               favorite.property = property;

               favorites.add(favorite);
           }

       } catch (Exception e) {
           Log.e(TAG, "Error getting user favorites", e);
       } finally {
           if (cursor != null) cursor.close();
           if (db != null) db.close();
       }

       return favorites;
   }
    public ArrayList<Reservation> getAllReservations() {
        ArrayList<Reservation> reservations = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT r.id, r.user_email, r.property_id, r.reservation_date, r.created_at, r.status, r.image_resource_id, " +
                "p.title, p.location, p.price " +
                "FROM reservations r " +
                "LEFT JOIN properties p ON r.property_id = p.id " +
                "ORDER BY r.reservation_date DESC";

        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                Reservation res = new Reservation();
                res.id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                res.userEmail = cursor.getString(cursor.getColumnIndexOrThrow("user_email"));
                res.propertyId = cursor.getInt(cursor.getColumnIndexOrThrow("property_id"));
                res.reservationDate = cursor.getLong(cursor.getColumnIndexOrThrow("reservation_date"));
                res.createdAt = cursor.getLong(cursor.getColumnIndexOrThrow("created_at"));
                res.status = cursor.getString(cursor.getColumnIndexOrThrow("status"));
                res.imageResourceId = cursor.getInt(cursor.getColumnIndexOrThrow("image_resource_id"));

                Property p = new Property();
                p.title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
                p.location = cursor.getString(cursor.getColumnIndexOrThrow("location"));
                p.price = cursor.getDouble(cursor.getColumnIndexOrThrow("price"));
                res.property = p;

                reservations.add(res);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return reservations;
    }
    @Override
    public void close() {
        super.close();
        // Clear statement cache when closing
        for (SQLiteStatement statement : statementCache.values()) {
            statement.close();
        }
        statementCache.clear();
    }
}
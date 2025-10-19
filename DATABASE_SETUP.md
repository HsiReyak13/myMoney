# 🗄️ MySQL Database Setup Guide

## Prerequisites

✅ **XAMPP** installed and running  
✅ **MySQL** service started in XAMPP Control Panel

## 📋 Setup Steps

### 1️⃣ Start XAMPP Services

1. Open **XAMPP Control Panel**
2. Click **Start** for **Apache**
3. Click **Start** for **MySQL**
4. Wait until both show "Running" status

### 2️⃣ Create Database

**Option A: Using phpMyAdmin (Recommended)**
1. Open browser and go to: `http://localhost/phpmyadmin`
2. Click **"New"** in the left sidebar
3. Database name: `mymoney_db`
4. Collation: `utf8mb4_general_ci` (default)
5. Click **"Create"**

**Option B: Using MySQL Command Line**
```sql
CREATE DATABASE mymoney_db;
```

### 3️⃣ Run the Application

1. In IntelliJ IDEA, run `MyMoneyApplication.java`
2. The application will automatically:
   - Connect to MySQL
   - Create all required tables
   - Set up the database schema

You should see these console messages:
```
✅ Connected to MySQL database successfully!
✅ Users table ready
✅ Transactions table ready
✅ Budgets table ready
✅ Overall Budget table ready
🎉 All database tables created successfully!
```

## 📊 Database Structure

### Tables Created Automatically

#### 1. **users** - User Authentication
```sql
- id (VARCHAR 36) - Primary Key
- username (VARCHAR 50) - Unique
- password_hash (VARCHAR 255) - SHA-256 hashed
- salt (VARCHAR 255) - Password salt
- created_at (TIMESTAMP) - Auto
```

#### 2. **transactions** - Financial Transactions
```sql
- id (VARCHAR 36) - Primary Key
- user_id (VARCHAR 36) - Foreign Key
- type (ENUM: INCOME/EXPENSE)
- amount (DECIMAL 10,2)
- category (VARCHAR 50)
- notes (TEXT)
- transaction_date (DATE)
- created_at (TIMESTAMP) - Auto
```

#### 3. **budgets** - Category Budgets
```sql
- id (INT) - Auto Increment Primary Key
- user_id (VARCHAR 36) - Foreign Key
- category (VARCHAR 50)
- amount (DECIMAL 10,2)
- created_at (TIMESTAMP) - Auto
- updated_at (TIMESTAMP) - Auto
```

#### 4. **overall_budget** - Overall Monthly Budget
```sql
- user_id (VARCHAR 36) - Primary Key & Foreign Key
- amount (DECIMAL 10,2)
- created_at (TIMESTAMP) - Auto
- updated_at (TIMESTAMP) - Auto
```

## 🔧 Configuration

Default connection settings (edit in `DatabaseManager.java` if needed):

```java
Host: localhost
Port: 3306
Database: mymoney_db
Username: root
Password: (empty)
```

### To Change Password

If your XAMPP MySQL has a password:

1. Open `DatabaseManager.java`
2. Find line: `private static final String DB_PASSWORD = "";`
3. Change to: `private static final String DB_PASSWORD = "your_password";`

## ✅ Verification

### Check Database in phpMyAdmin

1. Go to `http://localhost/phpmyadmin`
2. Click **mymoney_db** in left sidebar
3. You should see 4 tables:
   - `users`
   - `transactions`
   - `budgets`
   - `overall_budget`

### Check Data

After using the application:
- Go to phpMyAdmin
- Select `mymoney_db`
- Click on any table
- Click **"Browse"** to see data

## 🎯 Features with Database

### ✅ Persistent Data
- All user accounts saved permanently
- Transactions persist between sessions
- Budget settings remembered
- No data loss on app restart

### ✅ Data Management
- View all data in phpMyAdmin
- Export data to SQL
- Run custom queries
- Backup database easily

### ✅ Multi-User Support
- Multiple users can register
- Each user has separate data
- Secure password storage
- User sessions maintained

## 🔄 Backup Your Data

### Manual Backup
1. Open phpMyAdmin
2. Click **mymoney_db**
3. Click **"Export"** tab
4. Click **"Go"**
5. Save the `.sql` file

### Restore from Backup
1. Open phpMyAdmin
2. Click **mymoney_db**
3. Click **"Import"** tab
4. Choose your `.sql` file
5. Click **"Go"**

## 🐛 Troubleshooting

### ❌ "Failed to connect to database"

**Solution:**
1. Check XAMPP MySQL is running (green in Control Panel)
2. Check port 3306 is not blocked
3. Verify database `mymoney_db` exists
4. Check username/password in `DatabaseManager.java`

### ❌ "Access denied for user 'root'@'localhost'"

**Solution:**
1. Open phpMyAdmin
2. Go to "User accounts"
3. Find 'root'@'localhost'
4. Check password or set it to blank
5. Update password in `DatabaseManager.java`

### ❌ Tables not created

**Solution:**
1. Check console output for SQL errors
2. Make sure database `mymoney_db` exists
3. Grant proper permissions to 'root' user
4. Try creating database manually first

### ❌ Connection timeout

**Solution:**
1. Restart MySQL in XAMPP
2. Check if port 3306 is in use by another program
3. Try changing MySQL port in XAMPP config
4. Update DB_URL in `DatabaseManager.java` accordingly

## 📝 SQL Queries for Testing

### View All Users
```sql
SELECT * FROM users;
```

### View All Transactions
```sql
SELECT * FROM transactions ORDER BY transaction_date DESC;
```

### View Monthly Spending
```sql
SELECT 
    YEAR(transaction_date) as year,
    MONTH(transaction_date) as month,
    SUM(amount) as total
FROM transactions 
WHERE type = 'EXPENSE'
GROUP BY YEAR(transaction_date), MONTH(transaction_date);
```

### View Category Breakdown
```sql
SELECT 
    category,
    SUM(amount) as total,
    COUNT(*) as count
FROM transactions 
WHERE type = 'EXPENSE'
GROUP BY category
ORDER BY total DESC;
```

## 🎉 Success!

If you see your data in phpMyAdmin and the application runs without errors, you're all set! 

Your MyMoney application now has:
- ✅ Persistent database storage
- ✅ Secure user authentication
- ✅ Transaction history saved forever
- ✅ Budget settings remembered
- ✅ Easy data management via phpMyAdmin

---

**Need Help?** Check the console output for detailed error messages!


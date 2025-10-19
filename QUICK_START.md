# MyMoney - Quick Start Guide

## ⚡ Run the Application in 3 Steps

### 1. Open IntelliJ IDEA
Navigate to: `src/main/java/com/example/mymoney/MyMoneyApplication.java`

### 2. Run the Application
Click the green ▶️ button or right-click → **"Run 'MyMoneyApplication.main()'"**

### 3. Start Managing Your Finances!
The welcome screen will appear - you're ready to go!

## 🎯 5-Minute Setup Guide

### Step 1: Create Your Account (30 seconds)
1. Click **"Sign Up"** on the welcome screen
2. Username: `demo` (minimum 3 characters)
3. Password: `demo123` (minimum 6 characters)
4. Confirm password: `demo123`
5. Click **"Sign Up"**

### Step 2: Login (10 seconds)
1. Click **"Login"**
2. Enter: `demo` / `demo123`
3. Click **"Login"**
4. Welcome to your financial dashboard!

### Step 3: Add Your First Income (45 seconds)
1. Click **"+ Add Transaction"** button
2. In the modal dialog:
   - Type: **Income**
   - Date: Today (already selected)
   - Category: **Salary**
   - Amount: **5000**
   - Note: "Monthly salary"
3. Click **"Add Transaction"**
4. 🎉 Watch your dashboard update!

### Step 4: Add Some Expenses (2 minutes)
Add these quick transactions to see your data come alive:

**Transaction 1: Rent**
- Type: Expense
- Category: Housing → Utilities
- Amount: 1500
- Note: "Monthly rent"

**Transaction 2: Groceries**
- Type: Expense
- Category: Groceries
- Amount: 250
- Note: "Weekly shopping"

**Transaction 3: Gas**
- Type: Expense
- Category: Transportation
- Amount: 60
- Note: "Fill up"

### Step 5: Explore All Features (2 minutes)

#### 📊 Dashboard Tab
- See your updated balance: **$3,190** (5000 - 1810)
- Check your savings rate: **~64%**
- View the Income vs Expenses trend chart
- See recent transactions listed

#### 💰 Budget Tab
1. Set Overall Budget: **3500** → Click "Update"
2. Scroll down to Category Budgets
3. See progress bars showing your spending
4. Try setting a category budget:
   - Groceries: **600** → Click "Set"
   - Watch the progress bar update!

#### 📈 Reports Tab
- **Pie Chart**: See your spending distribution
  - Utilities: ~82% 
  - Groceries: ~14%
  - Transportation: ~3%
- **Line Chart**: Track your balance trend
- **Financial Insights**: View key metrics

#### 💳 Transactions Tab
- See all your transactions in a table
- Color-coded amounts (green = income, red = expense)
- Click **"Export CSV"** to save your data

## 🎨 Understanding the Interface

### Color Coding
- 🟢 **Green**: Positive (income, good progress)
- 🔴 **Red**: Negative (expenses, over budget)
- 🟡 **Yellow**: Warning (approaching budget limit)
- 🔵 **Blue**: Neutral information

### Budget Progress Bars
- **0-70%**: Green - You're doing great!
- **70-90%**: Yellow - Watch your spending
- **90-100%+**: Red - Over budget alert!

### Transaction Categories

**Income Categories:**
- 💼 Salary
- 💻 Freelance
- 📈 Investment
- 🎁 Gift
- 💰 Other Income

**Expense Categories:**
- 🛒 Groceries
- ⚡ Utilities
- 🚗 Transportation
- 🎮 Entertainment
- ⚕️ Healthcare
- 🛍️ Shopping
- 🍔 Food
- 📦 Other Expense

## 💡 Pro Tips

### 1. Daily Transaction Entry
Add transactions as they happen for accurate tracking:
- Morning coffee? Add it!
- Paid a bill? Record it!
- Got paid? Celebrate and add it!

### 2. Use Meaningful Notes
Good notes help you remember:
- ❌ "Food"
- ✅ "Lunch with John at Olive Garden"

### 3. Review Weekly
Every Sunday:
1. Check Dashboard for overall status
2. Review Budget progress
3. Adjust spending if needed

### 4. Set Realistic Budgets
Start with your actual spending:
1. Track for a week without budgets
2. Look at your spending patterns
3. Set budgets 10% below current spending
4. Gradually reduce over time

### 5. Export Monthly
Backup your data:
1. Go to Transactions tab
2. Click "Export CSV"
3. Save to: `MyMoney_Backups/2025-10.csv`
4. Keep for tax records!

## 🎯 Common Tasks

### Adding Income
```
+ Add Transaction
Type: Income
Category: Salary/Freelance/Investment
Amount: Your amount
Note: Source description
```

### Paying Bills
```
+ Add Transaction
Type: Expense  
Category: Utilities
Amount: Bill amount
Note: Electric/Water/Internet
```

### Tracking Groceries
```
+ Add Transaction
Type: Expense
Category: Groceries
Amount: Receipt total
Note: Store name + items
```

### Setting Category Budget
```
Budget Tab
→ Scroll to category
→ Enter budget amount
→ Click "Set"
→ Watch progress bar
```

## 📊 Understanding Your Dashboard

### Metric Cards (Top Row)
1. **Total Balance**: Your net worth (Income - Expenses)
2. **Total Income**: All money earned
3. **Total Expenses**: All money spent
4. **Savings Rate**: Percentage you're saving

### Income vs Expenses Trend Chart
- **Green Line**: Your income over months
- **Red Line**: Your expenses over months
- **Higher green = Good!** 📈
- **Rising red = Watch out!** 📉

### Recent Transactions
- Last 5 transactions
- Quick overview of latest activity
- Click on transaction for details

## 🎓 Learn By Doing

### Challenge 1: Break Even (5 minutes)
1. Add income: $2,000
2. Add expenses totaling exactly $2,000
3. Watch your balance stay at $0
4. See your savings rate at 0%

### Challenge 2: Save 50% (10 minutes)
1. Add income: $3,000
2. Add expenses totaling $1,500
3. Check savings rate: 50%!
4. See the green balance

### Challenge 3: Budget Master (15 minutes)
1. Set overall budget: $2,000
2. Set category budgets:
   - Groceries: $400
   - Entertainment: $200
   - Others: $200 each
3. Add expenses staying within budgets
4. All progress bars stay green!

## 🐛 Quick Fixes

### Modal Dialog Won't Open?
- Check that you clicked "+ Add Transaction"
- Try clicking again
- Restart application if needed

### Charts Not Showing Data?
- Add at least 2 transactions
- Click on different tabs
- Return to Dashboard/Reports

### Numbers Look Wrong?
- Check transaction types (Income vs Expense)
- Verify amounts are positive
- Review recent transactions

### Export Not Working?
- Ensure you have write permissions
- Try different save location
- Check disk space

## 🚀 Next Steps

After mastering the basics:

1. **Week 1**: Track everything for 7 days
2. **Week 2**: Set your first budgets
3. **Week 3**: Try to stay under budget
4. **Week 4**: Review reports and adjust
5. **Month 2**: Optimize and save more!

## 💪 Real-World Example

**Month 1 Journey:**

**Day 1 - Setup**
- Sign up and login
- Add salary: $5,000

**Week 1 - Track Everything**
- Rent: -$1,500
- Groceries: -$250
- Gas: -$60
- Entertainment: -$80
- Balance: $3,110

**Week 2 - Set Budgets**
- Overall: $3,000
- Groceries: $600/month
- Entertainment: $200/month
- Transportation: $300/month

**Week 3 - Stay on Track**
- Monitor daily spending
- Check budget progress
- Adjust as needed

**Week 4 - Celebrate!**
- End balance: $2,000 saved!
- Savings rate: 40%
- All budgets green!
- Export and backup data

**Month 2 - Optimize**
- Reduce entertainment budget
- Find cheaper groceries
- Aim for 50% savings rate

## 📱 Keyboard Shortcuts

(Coming soon - currently mouse-driven interface)

## 🎉 You're Ready!

You now know everything to start managing your finances like a pro with MyMoney!

### Remember:
- ✅ Track every transaction
- ✅ Review weekly
- ✅ Stay within budgets
- ✅ Save consistently
- ✅ Export monthly

### Questions?
Check the full `README.md` for detailed documentation!

---

**Happy Saving! 💰✨**

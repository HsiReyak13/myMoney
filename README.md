# MyMoney - Personal Finance Manager

A sophisticated JavaFX application for managing personal finances with beautiful data visualizations, budget tracking, and comprehensive financial insights.

## 🎯 Features

### 📊 Interactive Dashboard
- **Real-time Financial Metrics**: Balance, Income, Expenses, Savings Rate
- **Income vs Expenses Trend Chart**: Visual line chart showing monthly trends
- **Recent Transactions**: Quick view of latest 5 transactions
- **Color-coded Indicators**: Positive/negative values with dynamic coloring

### 💳 Transaction Management
- **Modal Dialog Interface**: Clean, modern dialog for adding transactions
- **Comprehensive Table View**: All transactions with sorting and color coding
- **Quick Export**: One-click CSV export functionality
- **Smart Categorization**: Automatic category suggestions based on transaction type
- **Income Categories**: Salary, Freelance, Investment, Gift, Other Income
- **Expense Categories**: Groceries, Utilities, Transportation, Entertainment, Healthcare, Shopping, Food, Other Expense

### 💰 Budget Management
- **Overall Monthly Budget**: Set and track your total monthly spending limit
- **Category-specific Budgets**: Individual budgets for each expense category
- **Progress Visualization**: Color-coded progress bars (green → yellow → red)
- **Real-time Tracking**: Instant updates showing spent vs. budget
- **Budget Alerts**: Visual indicators when approaching or exceeding limits

### 📈 Financial Reports & Analytics
- **Spending by Category Pie Chart**: Visual breakdown of expense distribution
- **Balance & Expenses Trend**: Line chart showing financial trajectory over time
- **Financial Insights**: 
  - Highest expense category identification
  - Growth rate tracking
  - Savings goal progress
- **Average Calculations**: Monthly balance and expense averages
- **Detailed Breakdowns**: Category spending with percentages

### 🔐 Security Features
- **SHA-256 Password Hashing**: Industry-standard encryption
- **Salt Generation**: Unique salt for each user account
- **Secure Session Management**: Protected user sessions
- **Input Validation**: Comprehensive validation for all user inputs

### 🎨 Modern UI/UX
- **Dark Theme**: Eye-friendly gradient background
- **Smooth Animations**: Hover effects and transitions
- **Responsive Layout**: Adapts to different window sizes
- **Modal Dialogs**: Non-intrusive transaction entry
- **Color-coded Data**: Intuitive visual indicators
- **Professional Styling**: Modern rounded corners and shadows

## 🚀 Quick Start

### Running the Application

#### Option 1: IntelliJ IDEA (Recommended)
1. Open the project in IntelliJ IDEA
2. Navigate to `src/main/java/com/example/mymoney/MyMoneyApplication.java`
3. Click the green ▶️ play button or right-click → "Run 'MyMoneyApplication.main()'"

#### Option 2: Maven Command
```bash
mvn clean javafx:run
```

### First Time Setup

1. **Sign Up**
   - Click "Sign Up" on the welcome screen
   - Username: minimum 3 characters
   - Password: minimum 6 characters
   - Confirm password and create account

2. **Login**
   - Enter your credentials
   - Click "Login" to access the application

## 📖 User Guide

### Adding Transactions
1. Click **"+ Add Transaction"** button (any screen)
2. Fill in the modal dialog:
   - **Type**: Income or Expense
   - **Date**: Transaction date
   - **Category**: Select from dropdown
   - **Amount**: Enter amount (positive number)
   - **Note**: Optional description
3. Click **"Add Transaction"**
4. View updates in Dashboard and Transactions tab

### Setting Up Budgets
1. Go to **Budget** tab
2. **Overall Budget**:
   - Enter your total monthly budget
   - Click "Update"
3. **Category Budgets**:
   - Scroll to each category card
   - Enter budget amount
   - Click "Set"
4. Watch progress bars update as you spend

### Viewing Reports
1. Go to **Reports** tab
2. **Pie Chart**: See spending distribution by category
3. **Line Chart**: Track balance and expenses over time
4. **Financial Insights**: Review key metrics and goals

### Exporting Data
1. Go to **Transactions** tab
2. Click **"Export CSV"** button
3. Choose save location
4. Open file in Excel, Google Sheets, or any spreadsheet app

## 🏗️ Project Structure

```
myMoney/
├── src/main/java/com/example/mymoney/
│   ├── MyMoneyApplication.java           # Main application entry
│   ├── controller/
│   │   ├── WelcomeController.java        # Login/Signup
│   │   ├── MainController.java           # Main window & navigation
│   │   ├── DashboardController.java      # Dashboard with charts
│   │   ├── TransactionsController.java   # Transaction management
│   │   ├── AddTransactionDialog.java     # Transaction modal dialog
│   │   ├── BudgetController.java         # Budget management
│   │   └── ReportsController.java        # Reports & analytics
│   ├── model/
│   │   ├── User.java                     # User data model
│   │   ├── Transaction.java              # Transaction data model
│   │   ├── FinancialMetrics.java         # Metrics calculations
│   │   └── Budget.java                   # Budget data model
│   └── service/
│       ├── AuthenticationService.java    # User authentication
│       ├── DataService.java              # Transaction management
│       └── BudgetService.java            # Budget management
├── src/main/resources/com/example/mymoney/
│   └── styles.css                        # Application styling
├── pom.xml                               # Maven configuration
├── module-info.java                      # Java module configuration
└── README.md                             # This file
```

## 💡 Features Deep Dive

### Dashboard
- **Metric Cards**: Four key financial indicators with percentage changes
- **Line Chart**: 6-month trend visualization with dual axes
- **Auto-refresh**: Updates immediately when transactions are added
- **Recent Activity**: Shows last 5 transactions with full details

### Budget Management
- **Visual Progress Bars**: 
  - Green (0-70%): On track
  - Yellow (70-90%): Approaching limit  
  - Red (90-100%+): Over budget
- **Percentage Indicators**: Real-time spending vs. budget ratio
- **Monthly Reset**: Budget tracking for current month only
- **Flexible Limits**: Easily adjust budgets as needed

### Charts & Visualization
- **Line Charts**: 
  - Expenses (red line)
  - Income/Balance (green line)
  - Multiple data series support
- **Pie Charts**:
  - Category distribution
  - Color-coded segments
  - Percentage labels
  - Interactive legend
- **Dynamic Updates**: Charts refresh automatically with new data

## 🎨 Color Scheme & Design

### Color Palette
- **Background**: Dark gradient (#1a1a2e → #16213e)
- **Primary**: Blue tones (#457b9d, #1d3557)
- **Accent**: Cyan (#a8dadc)
- **Success/Positive**: Green (#06ffa5)
- **Warning**: Orange (#f4a261)
- **Danger/Negative**: Red (#e63946)

### UI Components
- **Cards**: Rounded corners, subtle shadows, hover effects
- **Buttons**: Gradient backgrounds, smooth transitions
- **Forms**: Modern inputs with focus indicators
- **Tables**: Striped rows, color-coded values
- **Charts**: Transparent backgrounds, colored lines/segments

## 📊 Data Management

### In-Memory Storage
- All data stored during active session
- Cleared on logout or application close
- Fast access and manipulation

### CSV Export Format
```csv
Date,Type,Category,Amount,Notes
2025-10-15,INCOME,Salary,5400.00,"Monthly salary"
2025-10-14,EXPENSE,Groceries,150.00,"Weekly shopping"
```

### Future Enhancements (Potential)
- Database integration for persistent storage
- Cloud synchronization
- Mobile companion app
- Recurring transactions
- Budget templates
- Financial goal setting
- Multi-currency support
- Bank account integration

## 🛠️ Technical Details

### Technologies
- **JavaFX 21**: UI framework
- **Java 24**: Programming language
- **Maven**: Build & dependency management
- **CSS**: Custom styling
- **JavaFX Charts**: Data visualization

### System Requirements
- Java 21 or higher
- JavaFX SDK (included via Maven)
- 4GB RAM minimum
- 100MB disk space

### Performance
- Instant transaction processing
- Real-time chart updates
- Smooth animations
- Handles thousands of transactions

## 🐛 Troubleshooting

### Application Won't Start
```bash
# Clean and rebuild
mvn clean install
mvn javafx:run
```

### Charts Not Displaying
- Ensure JavaFX is properly configured
- Check module-info.java has all required modules
- Verify styles.css is loaded

### Modal Dialog Issues
- Check transparency is supported by your system
- Try running with different Java versions

### Styling Problems
```bash
# Invalidate IntelliJ caches
File → Invalidate Caches → Invalidate and Restart
```

## 📚 Usage Examples

### Example Budget Setup
- Overall Monthly Budget: $3,500
- Groceries: $600
- Utilities: $200
- Transportation: $300
- Entertainment: $250
- Healthcare: $150
- Shopping: $400

### Sample Transactions
**Income:**
- Monthly Salary: $5,400
- Freelance Project: $1,200
- Investment Dividend: $200

**Expenses:**
- Rent (Housing): $1,500
- Groceries (Food): $430
- Gas (Transportation): $245
- Netflix (Entertainment): $18
- Doctor Visit (Healthcare): $95

## 🎓 Learning Resources

### Understanding Financial Metrics
- **Savings Rate**: (Income - Expenses) / Income × 100
- **Current Balance**: Total Income - Total Expenses
- **Budget Usage**: Spent Amount / Budget Limit × 100

### Best Practices
1. **Regular Entry**: Add transactions daily
2. **Accurate Categorization**: Use correct categories
3. **Budget Review**: Adjust budgets monthly
4. **Weekly Check-ins**: Review dashboard weekly
5. **Monthly Reports**: Generate reports monthly

## 🤝 Contributing

This project was built as a comprehensive personal finance management solution. Feel free to:
- Report issues
- Suggest features
- Submit improvements
- Fork and customize

## 📄 License

Created for educational and personal use.

## 🙏 Acknowledgments

- JavaFX community for excellent documentation
- Modern UI/UX design principles
- Personal finance management best practices

---

**Start managing your finances better with MyMoney!** 💰📊✨

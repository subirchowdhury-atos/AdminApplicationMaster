# AdminApplicationMaster
The Repos that manages application 

admin-application-master-frontend/
├── public/
│   ├── index.html
│   └── assets/
│       ├── images/
│       └── icons/
├── src/
│   ├── components/
│   │   ├── common/
│   │   │   ├── Header.jsx
│   │   │   ├── Footer.jsx
│   │   │   └── Sidebar.jsx
│   │   ├── auth/
│   │   │   ├── LoginForm.jsx
│   │   │   └── RegisterForm.jsx
│   │   ├── loan-applications/
│   │   │   ├── LoanApplicationList.jsx
│   │   │   ├── LoanApplicationForm.jsx
│   │   │   └── LoanApplicationDetail.jsx
│   │   └── dashboard/
│   │       └── Dashboard.jsx
│   ├── pages/
│   │   ├── LoginPage.jsx
│   │   ├── DashboardPage.jsx
│   │   ├── LoanApplicationsPage.jsx
│   │   └── UsersPage.jsx
│   ├── services/
│   │   ├── api.js
│   │   ├── authService.js
│   │   └── loanApplicationService.js
│   ├── hooks/
│   │   └── useAuth.js
│   ├── context/
│   │   └── AuthContext.jsx
│   ├── styles/
│   │   ├── global.css
│   │   └── components/
│   ├── utils/
│   │   └── helpers.js
│   ├── App.jsx
│   └── main.jsx
├── package.json
└── vite.config.js

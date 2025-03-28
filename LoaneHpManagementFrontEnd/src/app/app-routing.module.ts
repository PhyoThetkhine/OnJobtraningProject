import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AuthGuard } from './guards/auth.guard';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { AccountingComponent } from './components/accounting/accounting.component';
import { BranchListComponent } from './components/branch/branch-list/branch-list.component';
import { ClientListComponent } from './components/client/client-list/client-list.component';
import { ClientCreateComponent } from './components/client/client-create/client-create.component';
import { SmeLoanListComponent } from './components/loan/sme/sme-loan-list/sme-loan-list.component';
import { SmeLoanApplicationsComponent } from './components/loan/sme/sme-loan-applications/sme-loan-applications.component';
import { SmeLoanCreateComponent } from './components/loan/sme/sme-loan-create/sme-loan-create.component';
import { SmeLoanDetailComponent } from './components/loan/sme/sme-loan-detail/sme-loan-detail.component';
import { HpLoanListComponent } from './components/loan/hp/hp-loan-list/hp-loan-list.component';
import { HpLoanApplicationsComponent } from './components/loan/hp/hp-loan-applications/hp-loan-applications.component';
import { HpLoanCreateComponent } from './components/loan/hp/hp-loan-create/hp-loan-create.component';
import { ReportsComponent } from './components/reports/reports.component';
import { UserListComponent } from './components/user/user-list/user-list.component';
import { UserCreateComponent } from './components/user/user-create/user-create.component';
import { RoleListComponent } from './components/access-control/role-list/role-list.component';
import { RoleCreateComponent } from './components/access-control/role-create/role-create.component';
import { TransactionsComponent } from './components/accounting/transactions/transactions.component';
import { WalletComponent } from './components/accounting/wallet/wallet.component';
import { PaymentMethodListComponent } from './components/payment/payment-method-list/payment-method-list.component';
import { PaymentMethodCreateComponent } from './components/payment/payment-method-create/payment-method-create.component';
import { ChartComponent } from './components/accounting/chart/chart.component';
import { ProductsComponent } from './components/dealer/products/products.component';
import { MainCategoriesComponent } from './components/dealer/main-categories/main-categories.component';
import { SubCategoriesComponent } from './components/dealer/sub-categories/sub-categories.component';

import { ChangePasswordComponent } from './components/settings/change-password/change-password.component';
import { LogoutComponent } from './components/logout/logout.component';
import { BranchCreateComponent } from './components/branch/branch-create/branch-create.component';
import { UserDetailComponent } from './components/user/user-detail/user-detail.component';
import { LoginComponent } from './components/login/login.component';
import { BranchDetailComponent } from './components/branch/branch-detail/branch-detail.component';
import { RoleDetailComponent } from './components/access-control/role-detail/role-detail.component';
import { HpLoanDetailComponent } from './components/loan/hp/hp-loan-detail/hp-loan-detail.component';
import { ClientDetailComponent } from './components/client/client-detail/client-detail.component';
import { YourProfileComponent } from './components/settings/your-profile/your-profile.component';

const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  {
    path: '',
    canActivate: [AuthGuard],
    children: [
      { path: 'dashboard', component: DashboardComponent },
      { path: 'accounting', component: AccountingComponent },
      { path: 'accounting/wallet', component: WalletComponent },
      { path: 'accounting/transactions', component: TransactionsComponent },
      { path: 'accounting/chart', component: ChartComponent },
      { path: 'payments/methods', component: PaymentMethodListComponent },
      { path: 'payments/methods/create', component: PaymentMethodCreateComponent },
      { path: 'branches', component: BranchListComponent },
      { path: 'branches/create', component: BranchCreateComponent },
      {
        path: 'clients',
        children: [
          { path: '', component: ClientListComponent },
          { path: 'create', component: ClientCreateComponent },
          { path: ':id', component: ClientDetailComponent },
          { path: 'view', component: ClientListComponent }
        ]
      },
      { path: 'loans/sme', component: SmeLoanListComponent },
      { path: 'loans/sme/applications', component: SmeLoanApplicationsComponent },
      { path: 'loans/sme/create', component: SmeLoanCreateComponent },
      { path: 'loans/sme/:id', component: SmeLoanDetailComponent },
      { path: 'loans/hp', component: HpLoanListComponent },
      { path: 'loans/hp/applications', component: HpLoanApplicationsComponent },
      { path: 'loans/hp/create', component: HpLoanCreateComponent },
      { path: 'loans/hp/:id', component: HpLoanDetailComponent },
      { path: 'reports', component: ReportsComponent },
      { path: 'users/create', component: UserCreateComponent },
      { path: 'users', component: UserListComponent },
      { path: 'access-control/roles', component: RoleListComponent },
      { path: 'access-control/roles/create', component: RoleCreateComponent },
      { path: 'dealer/products', component: ProductsComponent },
      { path: 'dealer/main-categories', component: MainCategoriesComponent },
      { path: 'dealer/sub-categories', component: SubCategoriesComponent },
      { path: 'settings/profile', component: YourProfileComponent },
      { path: 'settings/password', component: ChangePasswordComponent },
      { path: 'logout', component: LogoutComponent },
      { path: 'branches/edit/:id', component: BranchCreateComponent },
      { path: 'users/:id', component: UserDetailComponent },
      { path: 'branches/:id', component: BranchDetailComponent },
      { path: 'roles', component: RoleListComponent },
      { path: 'roles/:id', component: RoleDetailComponent }
    ]
  }
]

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }

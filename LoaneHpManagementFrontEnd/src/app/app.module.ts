import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { RouterModule } from '@angular/router';
import { AppRoutingModule } from './app-routing.module';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ToastrModule } from 'ngx-toastr';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';

// Components
import { AppComponent } from './app.component';
import { LoginComponent } from './components/login/login.component';
import { SidebarComponent } from './components/sidebar/sidebar.component';
import { NavbarComponent } from './components/navbar/navbar.component';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { AccountingComponent } from './components/accounting/accounting.component';
import { BranchListComponent } from './components/branch/branch-list/branch-list.component';
import { SmeLoanListComponent } from './components/loan/sme/sme-loan-list/sme-loan-list.component';
import { SmeLoanApplicationsComponent } from './components/loan/sme/sme-loan-applications/sme-loan-applications.component';
import { SmeLoanCreateComponent } from './components/loan/sme/sme-loan-create/sme-loan-create.component';
import { HpLoanListComponent } from './components/loan/hp/hp-loan-list/hp-loan-list.component';
import { HpLoanApplicationsComponent } from './components/loan/hp/hp-loan-applications/hp-loan-applications.component';
import { HpLoanCreateComponent } from './components/loan/hp/hp-loan-create/hp-loan-create.component';
import { ReportsComponent } from './components/reports/reports.component';
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
import { UpdateProfileComponent } from './components/settings/update-profile/update-profile.component';
import { ChangePasswordComponent } from './components/settings/change-password/change-password.component';
import { LogoutComponent } from './components/logout/logout.component';
import { BranchCreateComponent } from './components/branch/branch-create/branch-create.component';

import { BranchEditComponent } from './components/branch/branch-edit/branch-edit.component';
import { ClientListComponent } from './components/client/client-list/client-list.component';
import { ClientCreateComponent } from './components/client/client-create/client-create.component';
import { JwtInterceptor } from './interceptors/jwt.interceptor';
import { CloudinaryInterceptor } from './interceptors/cloudinary.interceptor';
import { BranchDetailComponent } from './components/branch/branch-detail/branch-detail.component';
import { ClientDetailComponent } from './components/client/client-detail/client-detail.component';
import { NgxChartsModule } from '@swimlane/ngx-charts';

@NgModule({
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
  declarations: [
    
    AppComponent,
    LoginComponent,
    SidebarComponent,
    NavbarComponent,
    DashboardComponent,
    AccountingComponent,
    BranchListComponent,
    SmeLoanListComponent,
    SmeLoanApplicationsComponent,
    SmeLoanCreateComponent,
    
    HpLoanApplicationsComponent,
    HpLoanCreateComponent,
    ReportsComponent,
    RoleListComponent,
   
    
    PaymentMethodListComponent,
    PaymentMethodCreateComponent,
    
   
    MainCategoriesComponent,
    SubCategoriesComponent,
    UpdateProfileComponent,
    ChangePasswordComponent,
    LogoutComponent,
    BranchCreateComponent,

    BranchEditComponent,
   
    ClientCreateComponent,
    BranchDetailComponent
  ],
  imports: [
    BrowserModule,
    NgxChartsModule,
    AppRoutingModule,
    RouterModule,
    ReactiveFormsModule,
    FormsModule,
    HttpClientModule,
    CommonModule,
    
    BrowserAnimationsModule,
    ToastrModule.forRoot({
      positionClass: 'toast-top-right',
      preventDuplicates: true,
      progressBar: true,
      closeButton: true
    }),
    RoleCreateComponent,
    NgbModule,
    ClientDetailComponent,
    NgxChartsModule,
  ],
  providers: [
    { provide: HTTP_INTERCEPTORS, useClass: JwtInterceptor, multi: true },
    { provide: HTTP_INTERCEPTORS, useClass: CloudinaryInterceptor, multi: true }
  ],
  bootstrap: [AppComponent,DashboardComponent],
  
})
export class AppModule { }

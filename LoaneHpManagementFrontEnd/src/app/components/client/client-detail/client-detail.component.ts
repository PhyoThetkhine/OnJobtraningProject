import { Component, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { CommonModule } from '@angular/common';
import { ToastrService } from 'ngx-toastr';
import { CIFService } from '../../../services/cif.service';
import { LoadingDelayService } from '../../../services/loading-delay.service';
import { CIF, CIFType } from '../../../models/cif.model';
import { NgbModal, NgbNav, NgbNavModule } from '@ng-bootstrap/ng-bootstrap';
import { ConfirmationModalComponent } from '../../../shared/components/confirmation-modal/confirmation-modal.component';
import { RouterModule } from '@angular/router';
import { SmeLoanService } from '../../../services/sme-loan.service';
import { HpLoanService } from '../../../services/hp-loan.service';
import { CompanyService } from '../../../services/company.service';
import { SMELoan } from '../../../models/sme-loan.model';
import { HpLoan } from '../../../models/hp-loan.model';
import { Company } from '../../../models/company.model';
import { BusinessPhoto } from '../../../models/business-photo.model';
import { CollateralService } from '../../../services/collateral.service';
import { Collateral } from '../../../models/collateral.model';
import { FormsModule } from '@angular/forms';
import { FinancialService } from '../../../services/financial.service';
import { Financial } from '../../../models/financial.model';
import { CIFCurrentAccountService } from '../../../services/cif-current-account.service';
import { CIFCurrentAccount, FreezeStatus } from '../../../models/cif-current-account.model';
import { TransactionService } from '../../../services/transaction.service';
import { AccountType, Transaction, TransactionResponse } from '../../../models/transaction.model';
import { ClientUpdateComponent } from '../client-update/client-update.component';
import { BusinessCreateComponent } from '../../business/business-create/business-create.component';
import { BusinessUpdateComponent } from '../../business/business-update/business-update.component';
import { FinancialUpdateComponent } from '../../business/financial-update/financial-update.component';
import { HttpErrorResponse } from '@angular/common/http';
import { CollateralCreateComponent } from '../../collateral/collateral-create/collateral-create.component';
import { CollateralEditComponent } from '../../collateral/collateral-edit/collateral-edit.component';
import { AccountLimitUpdateComponent } from '../../account/account-limit-update/account-limit-update.component';
import { catchError, map, Observable, of, shareReplay } from 'rxjs';
import { ApiResponse } from 'src/app/models/user.model';
import { BranchCurrentAccount } from 'src/app/models/branch-account.model';
import { BranchService } from 'src/app/services/branch.service';
import { AuthService } from 'src/app/services/auth.service';
import { CifTransferComponent } from '../cif-transfer/cif-transfer.component';

@Component({
  selector: 'app-client-detail',
  templateUrl: './client-detail.component.html',
  styleUrls: ['./client-detail.component.css'],
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    NgbNavModule,
    FormsModule,
    ConfirmationModalComponent,
    BusinessCreateComponent,
    BusinessUpdateComponent,
    FinancialUpdateComponent,
    CollateralCreateComponent,
    CollateralEditComponent,
    AccountLimitUpdateComponent
  ]
})
export class ClientDetailComponent implements OnInit {
  @ViewChild('nav') nav!: NgbNav;
 
  clientId!: number;
  client: CIF | null = null;
  loading = false;
  error: string | null = null;
  activeTab = 1;
  activeLoanTab = 'sme';
  activeBusinessTab = 'list';
  activeCompanyTab = 'info';
  showImageViewer = false;
  selectedImage: string | null = null;
  CIFType = CIFType;

  // SME Loans
  smeLoans: SMELoan[] = [];
  smeLoansLoading = false;
  smeLoansError: string | null = null;
  smeLoansCurrentPage = 0;
  smeLoansPageSize = 10;
  smeLoadsTotalElements = 0;
  smeLoadsTotalPages = 0;

  // HP Loans
  hpLoans: HpLoan[] = [];
  hpLoansLoading = false;
  hpLoansError: string | null = null;
  hpLoansCurrentPage = 0;
  hpLoansPageSize = 10;
  hpLoadsTotalElements = 0;
  hpLoadsTotalPages = 0;

  // Companies
  companies: Company[] = [];
  companiesLoading = false;
  companiesError: string | null = null;
  companiesCurrentPage = 0;
  companiesPageSize = 10;
  companiesTotalElements = 0;
  companiesTotalPages = 0;
  selectedCompany: Company | null = null;

  // Business Photos
  businessPhotos: BusinessPhoto[] = [];
  businessPhotosLoading = false;
  businessPhotosError: string | null = null;

  // Add collateral properties
  collaterals: Collateral[] = [];
  collateralsLoading = false;
  collateralsError: string | null = null;
  collateralsCurrentPage = 0;
  collateralsPageSize = 10;
  collateralsTotalElements = 0;
  collateralsTotalPages = 0;

  financial: Financial | null = null;
  financialLoading = false;
  financialError: string | null = null;

  // Add these new properties
  cifAccount: CIFCurrentAccount | null = null;
  accountLoading = false;
  accountError: string | null = null;
  refreshing = false;
  FreezeStatus = FreezeStatus;

  // Add these new properties for transactions
  transactions: Transaction[] = [];
  transactionLoading = false;
  currentPage = 0;
  pageSize = 10;
  totalElements = 0;
  totalPages = 0;
  Math = Math;
  branchAccount: any;

  constructor(
    private route: ActivatedRoute,
    private cifService: CIFService,
    private smeLoanService: SmeLoanService,
    private hpLoanService: HpLoanService,
    private companyService: CompanyService,
    private toastr: ToastrService,
    private loadingDelayService: LoadingDelayService,
    private modalService: NgbModal,
    private collateralService: CollateralService,
    private financialService: FinancialService,
    private cifAccountService: CIFCurrentAccountService,
    private transactionService: TransactionService,
    private branchService: BranchService,
    private authService: AuthService,

  ) {}

  ngOnInit() {
    this.clientId = Number(this.route.snapshot.paramMap.get('id'));
    this.loadClientDetails();
  }

  loadClientDetails() {
    this.loading = true;
    this.cifService.getCIFById(this.clientId).subscribe({
      next: (client) => {
        this.client = client;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading client:', error);
        this.error = 'Failed to load client details';
        this.loading = false;
        this.toastr.error('Failed to load client details');
      }
    });
  }

  onTabChange(event: any) {
    if (typeof event === 'object') {
      event = event.nextId;
    }
    
    switch (event) {
      case 1:
        // Profile Info tab - no additional loading needed
        break;
      case 2:
        // Business tab
        this.loadCompanies(0);
        break;
      case 3:
        // Loans tab
        this.activeLoanTab = 'sme';
        this.loadSMELoans(0);
        break;
      case 4:
        // Collaterals tab
        this.loadCollaterals(0);
        break;
      case 5:
        // Balance tab
        this.loadCIFAccount();
        
        break;
    }
  }

  setActiveTab(tab: string): void {
    const tabNumber = Number(tab);
    
    // If trying to access business tab (2) and client is PERSONAL type, skip it
    if (tabNumber === 2 && this.client?.cifType === CIFType.PERSONAL) {
      return;
    }
    
    this.activeTab = tabNumber;
    this.onTabChange(this.activeTab);
  }

  setActiveLoanTab(tab: string) {
    this.activeLoanTab = tab;
    if (tab === 'sme') {
      this.loadSMELoans();
    } else if (tab === 'hp') {
      this.loadHPLoans();
    }
  }

  loadSMELoans(page: number = 0) {
    if (!this.clientId) return;
    
    this.smeLoansLoading = true;
    this.smeLoansError = null;
  
    this.smeLoanService.getSMELoansByCif(
      this.clientId,
      page,
      this.smeLoansPageSize
    ).subscribe({
      next: (response) => {
        this.smeLoans = response.content;
        this.smeLoansCurrentPage = page;
        this.smeLoadsTotalElements = response.totalElements;
        this.smeLoadsTotalPages = response.totalPages;
        this.smeLoansLoading = false;
      },
      error: (error) => {
        console.error('Error loading SME loans:', error);
        this.smeLoansError = 'Failed to load SME loans';
        this.smeLoansLoading = false;
        this.toastr.error('Failed to load SME loans');
      }
    });
  }

  onSMELoansPageChange(page: number) {
    if (page >= 0 && page < this.smeLoadsTotalPages) {
      this.loadSMELoans(page);
    }
  }

  loadHPLoans(page: number = 0) {
    if (!this.clientId) return;
    
    this.hpLoansLoading = true;
    this.hpLoansError = null;
    
    this.hpLoanService.getHPLoansByCif(
      this.clientId,
      page,
      this.hpLoansPageSize
    ).subscribe({
      next: (response) => {
        this.hpLoans = response.content;
        this.hpLoansCurrentPage = page;
        this.hpLoadsTotalElements = response.totalElements;
        this.hpLoadsTotalPages = response.totalPages;
        this.hpLoansLoading = false;
      },
      error: (error) => {
        console.error('Error loading HP loans:', error);
        this.hpLoansError = 'Failed to load HP loans';
        this.hpLoansLoading = false;
        this.toastr.error('Failed to load HP loans');
      }
    });
  }

  onHPLoansPageChange(page: number) {
    if (page >= 0 && page < this.hpLoadsTotalPages) {
      this.loadHPLoans(page);
    }
  }

  loadCompanies(page: number = 0) {
    if (!this.clientId) return;
    
    this.companiesLoading = true;
    this.companiesError = null;
    
    this.companyService.getCompaniesByCif(
      this.clientId,
      page,
      this.companiesPageSize
    ).subscribe({
      next: (response) => {
        this.companies = response.content;
        this.companiesCurrentPage = page;
        this.companiesTotalElements = response.totalElements;
        this.companiesTotalPages = response.totalPages;
        this.companiesLoading = false;
      },
      error: (error) => {
        console.error('Error loading companies:', error);
        this.companiesError = 'Failed to load companies';
        this.companiesLoading = false;
        this.toastr.error('Failed to load companies');
      }
    });
  }

  onCompaniesPageChange(page: number) {
    if (page >= 0 && page < this.companiesTotalPages) {
      this.loadCompanies(page);
    }
  }

  viewCompanyDetails(company: Company) {
    this.selectedCompany = company;
    this.activeBusinessTab = 'detail';
    this.loadBusinessPhotos(company.id);
  }

  backToCompanyList() {
    this.selectedCompany = null;
    this.activeBusinessTab = 'list';
  }

  setActiveCompanyTab(tab: 'info' | 'financial') {
    this.activeCompanyTab = tab;
    if (tab === 'financial' && this.selectedCompany) {
      this.loadFinancialData();
    }
  }

  loadFinancialData() {
    if (!this.selectedCompany) return;

    this.financialLoading = true;
    this.financialError = null;

    this.financialService.getFinancialByCompany(this.selectedCompany.id)
      .subscribe({
        next: (financial: Financial) => {
          this.financial = financial;
          this.financialLoading = false;
        },
        error: (error: Error) => {
          console.error('Error loading financial data:', error);
          this.financialError = 'Failed to load financial data';
          this.financialLoading = false;
          if (error instanceof HttpErrorResponse && error.status !== 404) {
            this.toastr.error('Failed to load financial data');
          }
        }
      });
  }

  getLoanStatusBadgeClass(status: string): string {
    switch (status?.toLowerCase()) {
      case 'active':
        return 'bg-success';
      case 'pending':
        return 'bg-warning';
      case 'completed':
        return 'bg-info';
      case 'defaulted':
        return 'bg-danger';
      default:
        return 'bg-secondary';
    }
  }

  getStatusBadgeClass(status: string): string {
    switch (status?.toLowerCase()) {
      case 'active':
        return 'bg-success';
      case 'terminated':
        return 'bg-danger';
      case 'retired':
        return 'bg-warning';
      default:
        return 'bg-secondary';
    }
  }

  getCIFTypeText(type: CIFType): string {
    return type.replace('_', ' ');
  }

  openImageViewer(imageUrl: string) {
    this.selectedImage = imageUrl;
    this.showImageViewer = true;
  }

  closeImageViewer() {
    this.showImageViewer = false;
    this.selectedImage = null;
  }

  loadBusinessPhotos(companyId: number) {
    this.businessPhotosLoading = true;
    this.businessPhotosError = null;

    this.companyService.getBusinessPhotosByCompany(companyId).subscribe({
      next: (photos) => {
        this.businessPhotos = photos;
        this.businessPhotosLoading = false;
      },
      error: (error) => {
        console.error('Error loading business photos:', error);
        this.businessPhotosError = 'Failed to load business photos';
        this.businessPhotosLoading = false;
        this.toastr.error('Failed to load business photos');
      }
    });
  }

  loadCollaterals(page: number = 0): void {
    if (!this.clientId) return;
    
    this.collateralsLoading = true;
    this.collateralsError = null;
    
    this.collateralService.getCollateralsByCifId(
      this.clientId,
      page,
      this.collateralsPageSize
    ).subscribe({
      next: (response) => {
        this.collaterals = response.content;
        this.collateralsCurrentPage = page;
        this.collateralsTotalElements = response.totalElements;
        this.collateralsTotalPages = response.totalPages;
        this.collateralsLoading = false;
      },
      error: (error) => {
        this.collateralsLoading = false;
        if (error.status === 404) {
          this.collaterals = [];
        } else {
          this.collateralsError = 'Failed to load collaterals';
          console.error('Error loading collaterals:', error);
          this.toastr.error('Failed to load collaterals');
        }
      }
    });
  }

  onCollateralsPageChange(page: number): void {
    if (page >= 0 && page < this.collateralsTotalPages) {
      this.loadCollaterals(page);
    }
  }

  formatCurrency(value: number): string {
    return new Intl.NumberFormat('my-MM', {
      style: 'currency',
      currency: 'MMK'
    }).format(value);
  }

  loadCIFAccount() {
    if (!this.clientId) return;
    
    this.accountLoading = true;
    this.accountError = null;
    
    this.cifAccountService.getAccountByCifId(this.clientId).subscribe({
      next: (account) => {
        this.cifAccount = account;
        this.accountLoading = false;
        this.loadTransactions(0); 
      },
      error: (error) => {
        console.error('Error loading CIF account:', error);
        this.accountError = 'Failed to load account details';
        this.accountLoading = false;
        if (error.status !== 404) {
          this.toastr.error('Failed to load account details');
        }
      }
    });
  }

  refreshBalance() {
    this.refreshing = true;
    this.loadingDelayService.addDelay(
      this.cifAccountService.getAccountByCifId(this.clientId)
    ).subscribe({
      next: (account) => {
        this.cifAccount = account;
        this.toastr.success('Balance refreshed successfully');
      },
      error: (error) => {
        console.error('Error refreshing balance:', error);
        this.toastr.error('Failed to refresh balance');
      },
      complete: () => {
        setTimeout(() => {
          this.refreshing = false;
        }, 500);
      }
    });
  }

  changeFreezeStatus(status: FreezeStatus) {
    if (!this.cifAccount) return;
    
    const modalRef = this.modalService.open(ConfirmationModalComponent, {
      centered: true
    });
    
    modalRef.componentInstance.title = status === FreezeStatus.NOT_FREEZE ? 'Unfreeze Account' : 'Freeze Account';
    modalRef.componentInstance.message = status === FreezeStatus.NOT_FREEZE
      ? 'Are you sure you want to unfreeze this account? The account will be able to perform transactions.'
      : 'Are you sure you want to freeze this account? This will prevent any transactions from being performed.';
    modalRef.componentInstance.confirmText = status === FreezeStatus.NOT_FREEZE ? 'Unfreeze' : 'Freeze';
    modalRef.componentInstance.confirmButtonClass = status === FreezeStatus.NOT_FREEZE ? 'btn-success' : 'btn-danger';

    modalRef.result.then(
      (result) => {
        if (result) {
          this.accountLoading = true;
          this.loadingDelayService.addDelay(
            this.cifAccountService.changeFreezeStatus(this.cifAccount!.id, status)
          ).subscribe({
            next: (updatedAccount) => {
              this.cifAccount = updatedAccount;
              const message = status === FreezeStatus.NOT_FREEZE 
                ? 'Account unfrozen successfully'
                : 'Account frozen successfully';
              this.toastr.success(message);
              
              this.loadCIFAccount();
            },
            error: (error) => {
              console.error('Error changing freeze status:', error);
              this.toastr.error('Failed to change account status');
            },
            complete: () => {
              this.accountLoading = false;
            }
          });
        }
      },
      () => {} // Modal dismissed
    );
  }

 // Update the loadTransactions method
loadTransactions(page: number) {
  if (!this.cifAccount?.id) {
    this.toastr.warning('Please load account information first');
    return;
  }

  this.transactionLoading = true;
  this.transactions = []; // Reset transactions array

  this.transactionService.getTransactionsByCifaccountId(
    this.cifAccount.id,
    page,
    this.pageSize
  ).subscribe({
    next: (response: any) => {
      // Match the response structure
      this.transactions = response.data?.content || [];
      this.totalPages = response.data?.totalPages || 0;
      this.totalElements = response.data?.totalElements || 0;
      this.currentPage = response.data?.number || 0;
      this.pageSize = response.data?.size || this.pageSize;
      this.transactionLoading = false;
    },
    error: (error) => {
      console.error('Error loading transactions:', error);
      this.toastr.error('Failed to load transactions');
      this.transactionLoading = false;
      this.transactions = []; // Ensure empty array on error
    }
  });
  }
  
// Add these methods for account code resolution
private accountCodeCache = new Map<string, Observable<string>>();

getAccountCode(accountId: number, accountType: string): Observable<string> {
  const cacheKey = `${accountType.toUpperCase()}_${accountId}`;
  
  if (this.accountCodeCache.has(cacheKey)) {
    return this.accountCodeCache.get(cacheKey)!;
  }

  const request$ = this.createAccountCodeRequest(accountId, accountType).pipe(
    shareReplay(1),
    catchError(() => of('N/A'))
  );

  this.accountCodeCache.set(cacheKey, request$);
  return request$;
}

private createAccountCodeRequest(accountId: number, accountType: string): Observable<string> {
  switch(accountType.toUpperCase()) {
    case 'CIF':
      return this.cifAccountService.getAccountById(accountId).pipe(
        map((response: any) => response?.data?.accCode || 'N/A'),
        catchError(() => of('CIF Not Found'))
      );

    case 'BRANCH':
      return this.branchService.getBranchAccountById(accountId).pipe(
        map((response: any) => response?.data?.accCode || 'N/A'),
        catchError(() => of('Branch Not Found'))
      );

    default:
      return of('Unknown Account Type');
  }
}

isDebit(transaction: Transaction): boolean {
  return transaction.fromAccountType === AccountType.CIF && 
         transaction.fromAccountId === this.cifAccount?.id;
}

isCredit(transaction: Transaction): boolean {
  return transaction.toAccountType === AccountType.CIF && 
         transaction.toAccountId === this.cifAccount?.id;
}
  

  previousPage() {
    if (this.currentPage > 0) {
      this.loadTransactions(this.currentPage - 1);
    }
  }

  nextPage() {
    if (this.currentPage < this.totalPages - 1) {
      this.loadTransactions(this.currentPage + 1);
    }
  }

  openUpdateModal() {
    const modalRef = this.modalService.open(ClientUpdateComponent, {
      size: 'lg',
      backdrop: 'static'
    });
    
    modalRef.componentInstance.client = this.client;
    
    modalRef.result.then(
      (result) => {
        if (result) {
          this.loadClientDetails();
          this.toastr.success('Client updated successfully');
        }
      },
      () => {} // Modal dismissed
    );
  }

  changeStatus(newStatus: 'active' | 'terminated' ) {
    if (this.client && this.client.status !== newStatus) {
      const modalRef = this.modalService.open(ConfirmationModalComponent, {
        centered: true
      });
      
      modalRef.componentInstance.title = 'Change Client Status';
      modalRef.componentInstance.message = `Are you sure you want to change the client's status to ${newStatus}?`;
      modalRef.componentInstance.confirmText = 'Change Status';
      modalRef.componentInstance.confirmButtonClass = 
        newStatus === 'active' ? 'btn-success' : 
        newStatus === 'terminated' ? 'btn-danger' : 'btn-warning';

      modalRef.result.then(
        (result) => {
          if (result) {
            this.loading = true;
            this.loadingDelayService.addDelay(
              this.cifService.changeClientStatus(this.client!.id, newStatus)
            ).subscribe({
              next: (updatedClient) => {
                setTimeout(() => {
                  if (this.client) {
                    this.client = {
                      ...this.client,
                      status: newStatus
                    };
                  }
                  this.loading = false;
                  
                  const statusMessage = {
                    'active': 'Client has been activated successfully',
                    'retired': 'Client has been retired successfully',
                    'terminated': 'Client has been terminated successfully'
                  }[newStatus] || 'Status updated successfully';
                  
                  this.toastr.success(statusMessage, 'Status Change', {
                    timeOut: 3000,
                    closeButton: true,
                    progressBar: true
                  });
                }, 500);
              },
              error: (error) => {
                this.error = 'Failed to change client status';
                this.loading = false;
                console.error('Error changing status:', error);
                
                this.toastr.error(
                  `Failed to change status to ${newStatus}`, 
                  'Status Change Failed',
                  {
                    timeOut: 4000,
                    closeButton: true,
                    progressBar: true
                  }
                );
              }
            });
          }
        },
        () => {} // Modal dismissed
      );
    }
  }

  openCreateBusinessModal() {
    const modalRef = this.modalService.open(BusinessCreateComponent, {
      size: 'lg',
      backdrop: 'static'
    });
    
    modalRef.componentInstance.cifId = this.clientId;
    
    modalRef.result.then(
      (result) => {
        if (result) {
          this.loadCompanies(0);
          this.toastr.success('Business created successfully');
        }
      },
      () => {} // Modal dismissed
    );
  }
  public hasPermission(permission: string): boolean {
    return this.authService.hasPermission(permission);
  }
  openUpdateBusinessModal() {
    if (!this.selectedCompany) return;
  
    const modalRef = this.modalService.open(BusinessUpdateComponent, {
      size: 'lg',
      backdrop: 'static'
    });
    
    modalRef.componentInstance.company = this.selectedCompany!;
    
    modalRef.result.then(
      (result) => {
        if (result) {
          this.viewCompanyDetails(this.selectedCompany!); // Reload company details
          this.loadCompanies(this.companiesCurrentPage); // Reload the list of companies
          this.toastr.success('Business updated successfully');
        }
      },
      () => {} // Modal dismissed
    );
  }
  openCifTransferModal() {
    if (!this.cifAccount) return;
  
    const modalRef = this.modalService.open(CifTransferComponent, {
      size: 'lg',
      centered: true,
      backdrop: 'static'
    });
  
    modalRef.componentInstance.cifAccount = this.cifAccount;
  
    modalRef.result.then(
      (result) => {
        if (result) {
          this.loadCIFAccount();
          this.loadTransactions(0);
          this.toastr.success('Transfer completed successfully');
        }
      },
      () => {} // Dismissed
    );
  }

  openUpdateFinancialModal() {
    if (!this.selectedCompany || !this.financial) {
      this.toastr.warning('No financial data available to update');
      return;
    }

    const modalRef = this.modalService.open(FinancialUpdateComponent, {
      size: 'lg',
      backdrop: 'static'
    });

    modalRef.componentInstance.companyId = this.selectedCompany.id;
    modalRef.componentInstance.financial = this.financial;

    modalRef.closed.subscribe({
      next: (updated: boolean) => {
        if (updated) {
          this.loadFinancialData();
          this.toastr.success('Financial information updated successfully');
        }
      }
    });
  }

  openCreateCollateralModal() {
    const modalRef = this.modalService.open(CollateralCreateComponent, {
      size: 'lg',
      backdrop: 'static'
    });
    
    modalRef.componentInstance.cifId = this.clientId;
    
    modalRef.closed.subscribe({
      next: (result: boolean) => {
        if (result) {
          this.loadCollaterals(0);
          this.toastr.success('Collateral added successfully');
        }
      }
    });
  }

  openEditCollateralModal(collateral: Collateral) {
    const modalRef = this.modalService.open(CollateralEditComponent, {
      size: 'lg',
      backdrop: 'static'
    });
    
    modalRef.componentInstance.collateral = collateral;
    
    modalRef.closed.subscribe({
      next: (result: boolean) => {
        if (result) {
          this.loadCollaterals(this.collateralsCurrentPage);
          this.toastr.success('Collateral updated successfully');
        }
      }
    });
  }

  openUpdateAccountLimitsModal() {
    if (!this.cifAccount) return;

    const modalRef = this.modalService.open(AccountLimitUpdateComponent, {
      size: 'lg',
      backdrop: 'static'
    });
    
    modalRef.componentInstance.account = this.cifAccount;
    
    modalRef.closed.subscribe({
      next: (result: boolean) => {
        if (result) {
          this.loadCIFAccount();
          this.toastr.success('Account limits updated successfully');
        }
      }
    });
  }
} 
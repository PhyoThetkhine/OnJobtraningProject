import { Component, OnInit } from '@angular/core';
import { SidebarService } from '../../services/sidebar.service';
import { AuthService } from '../../services/auth.service';

interface MenuItem {
  title: string;
  icon?: string;
  route?: string;
  submenu?: MenuItem[];
  permission?: string;
}

@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.scss'],
  standalone: false,
})
export class SidebarComponent implements OnInit {
  isOpen = false;
  filteredMenuItems: MenuItem[] = [];
  filteredBottomMenuItems: MenuItem[] = [];

  private menuItems: MenuItem[] = [
    { title: 'Dashboard', icon: 'bi-speedometer2', route: '/dashboard' },
    {
      title: 'Accounting',
      icon: 'bi-calculator',
      permission: 'Accounting',
      submenu: [
        { title: 'View Chart', route: '/accounting/chart', permission: 'Accounting' },
        { title: 'Wallet', route: '/accounting/wallet', permission: 'Accounting' },
        { title: 'Transactions', route: '/accounting/transactions', permission: 'Accounting' }
      ]
    },
    {
      title: 'Dealer',
      icon: 'bi-shop',
      permission: 'Dealer',
      submenu: [
        { title: 'Products', route: '/dealer/products', permission: 'Dealer' },
        { title: 'Main Categories', route: '/dealer/main-categories', permission: 'Dealer' },
        { title: 'Sub Categories', route: '/dealer/sub-categories', permission: 'Dealer' }
      ]
    },
    {
      title: 'Payment',
      icon: 'bi-credit-card',
      permission: 'Payment',
      submenu: [
        { title: 'View Payment Methods', route: '/payments/methods', permission: 'Payment' },
        { title: 'Create Payment Method', route: '/payments/methods/create', permission: 'Payment' }
      ]
    },
    {
      title: 'Branch',
      icon: 'bi-building',
      permission: 'Branch',
      submenu: [
        { title: 'View Branches', route: '/branches', permission: 'Branch' },
        { title: 'Create Branch', route: '/branches/create', permission: 'Branch' }
      ]
    },
    {
      title: 'Clients',
      icon: 'bi-people',
      permission: 'Clients',
      submenu: [
        { title: 'View Clients', route: '/clients', permission: 'Clients' },
        { title: 'Create Client', route: '/clients/create', permission: 'Clients' }
      ]
    },
    {
      title: 'Loans',
      icon: 'bi-cash-coin',
      permission: 'Loans',
      submenu: [
        {
          title: 'SME',
          permission: 'SME',
          submenu: [
            { title: 'View Loans', route: '/loans/sme', permission: 'Loans' },
            { title: 'View Applications', route: '/loans/sme/applications', permission: 'Loans' },
            { title: 'Create Loan', route: '/loans/sme/create', permission: 'Loans' }
          ]
        },
        {
          title: 'HP',
          permission: 'HP',
          submenu: [
            { title: 'View Loans', route: '/loans/hp', permission: 'Loans' },
            { title: 'View Applications', route: '/loans/hp/applications', permission: 'Loans' },
            { title: 'Create Loan', route: '/loans/hp/create', permission: 'Loans' }
          ]
        }
      ]
    },
    {
      title: 'Users',
      icon: 'bi-people',
      permission: 'Users',
      submenu: [
        { title: 'View Users', route: '/users', permission: 'Users' },
        { title: 'Create User', route: '/users/create', permission: 'Users' }
      ]
    },
    {
      title: 'Access Control',
      icon: 'bi-shield-lock',
      permission: 'AccessControl',
      submenu: [
        { title: 'View Roles', route: '/access-control/roles', permission: 'AccessControl' },
        { title: 'Create Role', route: '/access-control/roles/create', permission: 'AccessControl' }
      ]
    },
    { title: 'Reports', icon: 'bi-file-earmark-text', route: '/reports', permission: 'Report' },
  ];

  private bottomMenuItems: MenuItem[] = [
    {
      title: 'Settings',
      icon: 'bi-gear',
      submenu: [
        { title: 'Update Profile', route: '/settings/profile' },
        { title: 'Change Password', route: '/settings/password' }
      ]
    },
    { title: 'Logout', icon: 'bi-box-arrow-right', route: '/logout' }
  ];

  constructor(
    private sidebarService: SidebarService,
    private authService: AuthService
  ) {}

  ngOnInit() {
    this.sidebarService.isOpen$.subscribe(
      isOpen => this.isOpen = isOpen
    );
    this.filterMenuItems();
    this.authService.permissions$.subscribe(() => {
      this.filterMenuItems();
    });
  }

  private filterMenuItems() {
    this.filteredMenuItems = this.filterMenuItemsByPermission(this.menuItems);
    this.filteredBottomMenuItems = this.bottomMenuItems;
  }

  private filterMenuItemsByPermission(items: MenuItem[]): MenuItem[] {
    return items.filter(item => {
      // For items without permission requirement or items where user has the permission
      const hasRequiredPermission = !item.permission || this.authService.hasPermission(item.permission);
      
      if (hasRequiredPermission) {
        // If item has submenu, check permissions recursively
        if (item.submenu) {
          const filteredSubmenu = this.filterMenuItemsByPermission(item.submenu);
          // Only show parent menu if it has at least one accessible submenu item
          return filteredSubmenu.length > 0;
        }
        // If no submenu, show the item
        return true;
      }
      return false;
    }).map(item => {
      if (item.submenu) {
        return {
          ...item,
          submenu: this.filterMenuItemsByPermission(item.submenu)
        };
      }
      return item;
    });
  }

  toggleSidebar() {
    this.sidebarService.toggle();
  }

  isShortText(title: string): boolean {
    const shortTitles = [
      'View Branches',
      'Create Branch',
      'View Clients',
      'Create Client',
      'View Users',
      'Create User',
      'View Roles',
      'Create Role',
      'View Loans',
      'Create Loan',
      'Update Profile',
      'Change Password',
      'Logout'
    ];
    return shortTitles.includes(title);
  }
}
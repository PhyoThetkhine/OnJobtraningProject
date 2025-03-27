import { Component, OnInit } from '@angular/core';
import { SidebarService } from '../../services/sidebar.service';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';

interface MenuItem {
  title: string;
  icon?: string;
  route?: string;
  submenu?: MenuItem[];
  permission?: string;
  action?: string;
}

@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.scss'],
  standalone: false,
})
export class SidebarComponent implements OnInit {
  showLogoutModal = false; 
  isOpen = false;
  filteredMenuItems: MenuItem[] = [];
  filteredBottomMenuItems: MenuItem[] = [];

  handleItemClick(item: MenuItem) {
    if (item.action === 'logout') {
      this.showLogoutModal = true;
    } else if (item.route) {
      this.router.navigate([item.route]);
    }
  }

  // Add this method
  confirmLogout() {
    this.authService.logout();
    this.showLogoutModal = false;
  }

  private menuItems: MenuItem[] = [
    { title: 'Dashboard', icon: 'bi-speedometer2', route: '/dashboard' },
    {
      title: 'Accounting',
      icon: 'bi-calculator',
      permission: 'Accounting Menu',
      submenu: [
        { title: 'View Chart', route: '/accounting/chart', permission: 'Accounting Menu' },
        { title: 'Wallet', route: '/accounting/wallet', permission: 'Accounting Menu' },
        { title: 'Transactions', route: '/accounting/transactions', permission: 'Accounting Menu' }
      ]
    },
    {
      title: 'Dealer Management',
      icon: 'bi-shop',
      permission: 'Dealer Management Menu',
      submenu: [
        { title: 'Products', route: '/dealer/products', permission: 'Dealer Management Menu' },
        { title: 'Main Categories', route: '/dealer/main-categories', permission: 'Dealer Management Menu' },
        { title: 'Sub Categories', route: '/dealer/sub-categories', permission: 'Dealer Management Menu' }
      ]
    },
    {
      title: 'Payment Method',
      icon: 'bi-credit-card',
      permission: 'Payment Method Menu',
      submenu: [
        { title: 'View Payment Methods', route: '/payments/methods', permission: 'Payment Method Menu' },
        { title: 'Create Payment Method', route: '/payments/methods/create', permission: 'Payment Method Menu' }
      ]
    },
    {
      title: 'Branch Management',
      icon: 'bi-building',
      permission: 'Branch Management Menu',
      submenu: [
        { title: 'View Branches', route: '/branches', permission: 'Branch Management Menu' },
        { title: 'Create Branch', route: '/branches/create', permission: 'Branch Management Menu' }
      ]
    },
    {
      title: 'Client Management',
      icon: 'bi-people',
      permission: 'Client Management Menu',
      submenu: [
        { title: 'View Clients', route: '/clients', permission: 'Client Management Menu' },
        { title: 'Create Client', route: '/clients/create', permission: 'Client Management Menu' }
      ]
    },
    {
      title: 'Loan Management',
      icon: 'bi-cash-coin',
      permission: 'Loan Management Menu',
      submenu: [
        {
          title: 'SME',
          permission: 'SME Menu',
          submenu: [
            { title: 'View Loans', route: '/loans/sme', permission: 'Loan Management Menu' },
         
            { title: 'Create Loan', route: '/loans/sme/create', permission: 'Loan Management Menu' }
          ]
        },
        {
          title: 'HP',
          permission: 'HP Menu',
          submenu: [
            { title: 'View Loans', route: '/loans/hp', permission: 'Loan Management Menu' },
          
            { title: 'Create Loan', route: '/loans/hp/create', permission: 'Loan Management Menu' }
          ]
        }
      ]
    },
    {
      title: 'Staff Management',
      icon: 'bi-people',
      permission: 'Staff Management Menu',
      submenu: [
        { title: 'View staff', route: '/users', permission: 'Staff Management Menu' },
        { title: 'Create staff', route: '/users/create', permission: 'Staff Management Menu' }
      ]
    },
    {
      title: 'Access Control',
      icon: 'bi-shield-lock',
      permission: 'AccessControl Menu',
      submenu: [
        { title: 'View Roles', route: '/access-control/roles', permission: 'AccessControl Menu' },
        { title: 'Create Role', route: '/access-control/roles/create', permission: 'AccessControl Menu' }
      ]
    },
    { title: 'Reports', icon: 'bi-file-earmark-text', route: '/reports', permission: 'Report Menu' },
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
    { title: 'Logout', icon: 'bi-box-arrow-right', action: 'logout' } 
  ];

  constructor(
    private sidebarService: SidebarService,
    private authService: AuthService,
    private router: Router,
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
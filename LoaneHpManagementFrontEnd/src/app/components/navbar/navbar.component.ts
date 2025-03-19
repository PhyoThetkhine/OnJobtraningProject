import { Component } from '@angular/core';
import { SidebarService } from '../../services/sidebar.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-navbar',
  standalone: false,
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.css'
})
export class NavbarComponent {
  router: any;
  constructor(private sidebarService: SidebarService,
    private authService: AuthService
  ) {}

  toggleSidebar() {
    this.sidebarService.toggle();
  }
  onLogout(){
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}

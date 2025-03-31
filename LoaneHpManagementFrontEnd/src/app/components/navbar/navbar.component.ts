import { Component } from '@angular/core';
import { SidebarService } from '../../services/sidebar.service';
import { AuthService } from '../../services/auth.service';
import { AsyncPipe } from '@angular/common';
import { CurrentUser } from 'src/app/models/user.model';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-navbar',
  standalone: false,
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.css',
  providers: [AsyncPipe] // Add AsyncPipe to providers
})
export class NavbarComponent {
  currentUser$: Observable<CurrentUser | null>;
  router: any;
  constructor(private sidebarService: SidebarService,
    private authService: AuthService,
    
    private asyncPipe: AsyncPipe
  ) { this.currentUser$ = this.authService.getCurrentUser();}

  toggleSidebar() {
    this.sidebarService.toggle();
  }
   // Add this to handle logout properly
   onLogout() {
    this.authService.logout();
    const user = this.asyncPipe.transform(this.currentUser$);
    console.log('User logged out:', user?.userCode);
    this.router.navigate(['/login']);
  }
  
}

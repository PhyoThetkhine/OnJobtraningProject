import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Role } from '../../../models/role.model';
import { RoleService } from '../../../services/role.service';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-role-list',
  templateUrl: './role-list.component.html',
  styleUrls: ['./role-list.component.css'],
  standalone: false,

})
export class RoleListComponent implements OnInit {
  roles: Role[] = [];
  loading = false;
  error: string | null = null;

  constructor(
    private roleService: RoleService,
    private toastr: ToastrService
  ) {}

  ngOnInit() {
    this.loadRoles();
  }

  loadRoles() {
    this.loading = true;
    this.roleService.getRoles().subscribe({
      next: (roles) => {
        this.roles = roles;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading roles:', error);
        this.toastr.error('Failed to load roles');
        this.loading = false;
        this.error = 'Failed to load roles';
      }
    });
  }

  getAuthorityBadgeClass(authority: string): string {
    return authority === 'MainBranchLevel' ? 'bg-primary' : 'bg-info';
  }
} 
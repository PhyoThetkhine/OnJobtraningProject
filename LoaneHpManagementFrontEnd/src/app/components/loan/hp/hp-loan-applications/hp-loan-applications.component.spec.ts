import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HpLoanApplicationsComponent } from './hp-loan-applications.component';

describe('HpLoanApplicationsComponent', () => {
  let component: HpLoanApplicationsComponent;
  let fixture: ComponentFixture<HpLoanApplicationsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [HpLoanApplicationsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(HpLoanApplicationsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SmeLoanApplicationsComponent } from './sme-loan-applications.component';

describe('SmeLoanApplicationsComponent', () => {
  let component: SmeLoanApplicationsComponent;
  let fixture: ComponentFixture<SmeLoanApplicationsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SmeLoanApplicationsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SmeLoanApplicationsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

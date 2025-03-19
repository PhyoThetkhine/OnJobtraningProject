import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HpLoanCreateComponent } from './hp-loan-create.component';

describe('HpLoanCreateComponent', () => {
  let component: HpLoanCreateComponent;
  let fixture: ComponentFixture<HpLoanCreateComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [HpLoanCreateComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(HpLoanCreateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

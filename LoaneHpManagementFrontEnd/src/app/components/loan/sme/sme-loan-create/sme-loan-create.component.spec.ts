import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SmeLoanCreateComponent } from './sme-loan-create.component';

describe('SmeLoanCreateComponent', () => {
  let component: SmeLoanCreateComponent;
  let fixture: ComponentFixture<SmeLoanCreateComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SmeLoanCreateComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SmeLoanCreateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

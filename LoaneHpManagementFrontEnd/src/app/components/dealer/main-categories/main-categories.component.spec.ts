import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MainCategoriesComponent } from './main-categories.component';

describe('MainCategoriesComponent', () => {
  let component: MainCategoriesComponent;
  let fixture: ComponentFixture<MainCategoriesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [MainCategoriesComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MainCategoriesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

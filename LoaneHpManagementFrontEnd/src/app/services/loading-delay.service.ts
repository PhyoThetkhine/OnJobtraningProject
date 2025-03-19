import { Injectable } from '@angular/core';
import { Observable, timer } from 'rxjs';
import { delayWhen } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class LoadingDelayService {
  addDelay<T>(observable: Observable<T>, delayMs: number = 500): Observable<T> {
    return observable.pipe(
      delayWhen(() => timer(delayMs))
    );
  }
} 
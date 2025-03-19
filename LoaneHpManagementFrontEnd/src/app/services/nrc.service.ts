import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';

export interface NrcTownship {
  id: string;
  name_en: string;
  name_mm: string;
  nrc_code: string;
}

@Injectable({
  providedIn: 'root'
})
export class NrcService {
  constructor(private http: HttpClient) {}

  getNrcTownships(): Observable<NrcTownship[]> {
    console.log('Loading NRC townships from:', 'assets/nrc.json');
    return this.http.get<{data: NrcTownship[]}>('assets/nrc.json')
      .pipe(
        map(response => {
          console.log('NRC Response:', response);
          return response.data;
        }),
        map(townships => townships.sort((a, b) => 
          a.name_en.localeCompare(b.name_en)
        ))
      );
  }

  getNrcStateNumbers(): string[] {
    return ['1', '2', '3', '4', '5', '6', '7', '8', '9', '10', '11', '12', '13', '14'];
  }

  getNrcTypes(): string[] {
    return ['N', 'E', 'P', 'A', 'F', 'TH', 'G'];
  }
} 
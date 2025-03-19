import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface CloudinaryResponse {
  secure_url: string;
  public_id: string;
  // Add other Cloudinary response properties if needed
}

@Injectable({
  providedIn: 'root'
})
export class CloudinaryService {
  private cloudName = environment.cloudinary.cloudName;
  private uploadPreset = environment.cloudinary.uploadPreset;
  private uploadUrl = `https://api.cloudinary.com/v1_1/${this.cloudName}/upload`;

  constructor(private http: HttpClient) {}

  uploadImage(file: File): Observable<any> {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('upload_preset', this.uploadPreset);
    formData.append('cloud_name', this.cloudName);

    // Remove authorization header for Cloudinary requests
    return this.http.post(this.uploadUrl, formData, {
      headers: {
        // Don't send authorization header
        Authorization: ''
      }
    });
  }
} 
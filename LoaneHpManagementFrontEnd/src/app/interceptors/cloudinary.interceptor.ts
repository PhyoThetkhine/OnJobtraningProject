import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable()
export class CloudinaryInterceptor implements HttpInterceptor {
  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    // Check if the request is going to Cloudinary
    if (request.url.includes('cloudinary.com')) {
      // Clone the request and remove the authorization header
      const modifiedRequest = request.clone({
        headers: request.headers.delete('Authorization')
      });
      return next.handle(modifiedRequest);
    }
    return next.handle(request);
  }
} 
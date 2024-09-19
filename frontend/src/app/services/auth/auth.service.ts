import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { LoginRequest, LoginResponse } from '../../models/user.model';
import { environment } from '../../../environment/environment.prod';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private authApiUrl = `${environment.apiUrl}/auth`;

  constructor(private http: HttpClient) { }

  // Do login request
  login(user: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(this.authApiUrl + "/login", user);
  }

  // JWT related functions
  setToken(token: string): void {
    localStorage.setItem('token', token);
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  logout(): void {
    localStorage.removeItem('token');
  }

  isLoggedIn(): boolean {
    return this.getToken() !== null;
  }
}
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { LoginRequest, LoginResponse } from '../../models/user.model';
import { environment } from '../../../environment/environment.prod';
import { jwtDecode } from 'jwt-decode';

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

  getRole(): string {
    const token = this.getToken();

    if (token) {
      const decodedToken: any = jwtDecode(token);

      if (decodedToken.roles && decodedToken.roles.length > 0) {
        const role = decodedToken.roles[0].toLowerCase();
        return role.charAt(0).toUpperCase() + role.slice(1);
      }
    }

    return 'Guest';
  }
}
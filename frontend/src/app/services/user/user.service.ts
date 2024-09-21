import { Injectable } from '@angular/core';
import { environment } from '../../../environment/environment.prod';
import { HttpClient, HttpParams } from '@angular/common/http';
import { SignupResponse, User, UserDTO, UserSaveDTO } from '../../models/user.model';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private authApiUrl = `${environment.apiUrl}/users`;

  constructor(private http: HttpClient) {}

  // Get users
  getUsers(
    name?: string,
    page: number = 1,
    limit: number = 10
  ): Observable<{ users: User[], currentPage: number, totalItems: number, totalPages: number }> {
    let params = new HttpParams();
    if (name !== undefined) params = params.append('name', name);
    params = params.append('page', (page - 1).toString());
    params = params.append('size', limit.toString());

    return this.http.get<{ users: User[], currentPage: number, totalItems: number, totalPages: number }>(this.authApiUrl, { params });
  } 

  // Do Signup request
  signup(userSave: UserSaveDTO): Observable<SignupResponse> {
    return this.http.post<SignupResponse>(this.authApiUrl + "/signup", userSave);
  }

  // Get user by email
  getUserByEmail(email: string): Observable<UserDTO> {
    return this.http.get<UserDTO>(`${this.authApiUrl}/email?email=${email}`);
  }

  // Send email
  sendEmail(to: string, subject: string, htmlContent: string): Observable<any> {
    const emailData = {
      to: to,
      subject: subject,
      htmlBody: htmlContent,
    };
    return this.http.post(`${this.authApiUrl}/send`, emailData);
  }

  // Update user password
  updatePassword(userId: string, newPassword: { password: string }): Observable<UserDTO> {
    return this.http.patch<UserDTO>(`${this.authApiUrl}/${userId}/password`, newPassword);
  }
}
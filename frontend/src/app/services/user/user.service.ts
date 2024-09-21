import { Injectable } from '@angular/core';
import { environment } from '../../../environment/environment.prod';
import { HttpClient } from '@angular/common/http';
import { SignupResponse, UserDTO, UserSaveDTO } from '../../models/user.model';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private authApiUrl = `${environment.apiUrl}/users`;

  constructor(private http: HttpClient) {}

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
}
import { Injectable } from '@angular/core';
import { environment } from '../../../environment/environment.prod';
import { HttpClient } from '@angular/common/http';
import { SignupResponse, UserSaveDTO } from '../../models/user.model';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private authApiUrl = `${environment.apiUrl}/users`;

  constructor(private http: HttpClient) { }

  // Do Signup request
  signup(userSave: UserSaveDTO): Observable<SignupResponse> {
    return this.http.post<SignupResponse>(this.authApiUrl + "/signup", userSave);
  }
}
